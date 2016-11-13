package cn.ffcs.wisdom.city.simico.base;

import java.io.File;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.RemoteViews;
import cn.ffcs.wisdom.city.R;
import cn.ffcs.wisdom.city.simico.activity.Utils;
import cn.ffcs.wisdom.city.simico.kit.activity.DownloadAlertActivity;
import cn.ffcs.wisdom.city.simico.kit.util.DownloadUtils;
import cn.ffcs.wisdom.city.sqlite.model.MenuItem;

public class AppDownloadService extends Service {

	public static final int UPDATE_PROGRESS = 0;
	public static final int DOWNLOAD_SUCCESS = 1;
	public static final int DOWNLOAD_FAIL = 2;
	public static final int mNotificationId = 100;

	private String mDownloadUrl = null;
	private String mAppName = null;
	private String mPkgName = null;
	private NotificationManager mNotificationManager = null;
	private Notification mNotification = null;

	private File destDir = null;
	private File destFile = null;
	private boolean isBackRun = false;
	private int cur_progress = 0;
	private long cur_total = 0;
	private AppDownloadThread downloadThread;
	public static MenuItem menuItem;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWNLOAD_SUCCESS:
				Application.showToastShort(String.format(getString(R.string.app_download_sucess),
						mAppName));
				install(destFile);
				break;
			case DOWNLOAD_FAIL:
				if (msg.obj.toString().equals(getResources().getString(R.string.app_download_stop)))
					Application.showToastShort(String.format(getString(R.string.app_download_stop),
							mAppName));
				else
					Application.showToastShort(String.format(getString(R.string.app_download_fail),
							mAppName));
				if (mNotification != null)
					mNotificationManager.cancel(mNotificationId);
				stopSelf();
				break;
			default:
				break;
			}
		}

	};

	private DownloadUtils.DownloadListener downloadListener = new DownloadUtils.DownloadListener() {
		@Override
		public void downloading(int progress, long total) {
			cur_progress = progress;
			cur_total = total;
			if (mNotification != null) {
				mNotification.contentView.setProgressBar(R.id.app_download_progressbar, 100,
						progress, false);
				mNotification.contentView.setTextViewText(R.id.app_download_progresstext, progress
						+ "%");
				mNotification.contentIntent = getPendingIntent();
				mNotificationManager.notify(mNotificationId, mNotification);
			} else {
				sendBroad(UPDATE_PROGRESS, progress, total);
			}
		}

		@Override
		public void downloaded() {
			if (mNotification != null) {
				mNotification.contentView.setViewVisibility(R.id.app_download_progressblock,
						View.GONE);
				mNotification.defaults = Notification.DEFAULT_SOUND;
				mNotification.contentView.setTextViewText(R.id.app_download_progresstext, "下载完成。");
				mNotificationManager.notify(mNotificationId, mNotification);
				mNotificationManager.cancel(mNotificationId);
				mNotification = null;
			} else {
				sendBroad(DOWNLOAD_SUCCESS, 100, 0);
			}
			if (destFile.exists() && destFile.isFile() && checkApkFile(destFile.getPath())) {
				Message msg = mHandler.obtainMessage();
				msg.what = DOWNLOAD_SUCCESS;
				mHandler.sendMessage(msg);
			}
			stopSelf();
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		isBackRun = intent.getBooleanExtra("isbackrun", false);
		menuItem = (MenuItem) intent.getSerializableExtra("MenuMessage");
		if (downloadThread == null) {
			mDownloadUrl = menuItem.getAppUrl();
			mAppName = menuItem.getMenuName();
			mPkgName = menuItem.getPackage_() + ".apk";
			if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
				destDir = new File(Utils.getAppFilePath());
				if (destDir.exists()) {
					File destFile = new File(destDir.getPath() + "/" + mPkgName);
					if (destFile.exists() && destFile.isFile() && checkApkFile(destFile.getPath())) {
						sendBroad(DOWNLOAD_SUCCESS, 100, 0);
						install(destFile);
						stopSelf();
						return super.onStartCommand(intent, flags, startId);
					}
				}
			} else {
				return super.onStartCommand(intent, flags, startId);
			}
		}
		if (isBackRun) {
			goBackRun();
		} else {
			if (mNotification != null) {
				mNotificationManager.cancel(mNotificationId);
				mNotification = null;
			}
			sendBroad(UPDATE_PROGRESS, cur_progress, cur_total);
		}
		if (downloadThread == null) {
			downloadThread = new AppDownloadThread();
			downloadThread.start();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	public static void stopDownload() {
		DownloadUtils.isStop = true;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		System.out.println("stop service");
		if (downloadThread != null && downloadThread.isAlive()) {
			DownloadUtils.isStop = true;
			System.out.println("stop thread");
			downloadThread = null;
		}
		menuItem = null;
		super.onDestroy();
	}

	private void sendBroad(int statu, int progress, long total) {
		Intent resultIntent = new Intent(Constants.INTENT_ACTION_DOWNLOAD_APP);
		resultIntent.putExtra("statu", statu);
		resultIntent.putExtra("V", progress);
		resultIntent.putExtra("T", total);
		sendBroadcast(resultIntent);
	}

	public void goBackRun() {
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mNotification = new Notification();

		mNotification.contentView = new RemoteViews(getApplication().getPackageName(),
				R.layout.simico_appdownload_notification);

		mNotification.icon = R.drawable.ic_launcher;
		mNotification.tickerText = "正在下载...";
		mNotification.contentIntent = getPendingIntent();
		mNotification.contentView.setProgressBar(R.id.app_download_progressbar, 100, cur_progress,
				false);
		mNotification.contentView.setTextViewText(R.id.app_download_progresstext, cur_progress
				+ "%");
		mNotificationManager.cancel(mNotificationId);
		mNotificationManager.notify(mNotificationId, mNotification);
	}

	private PendingIntent getPendingIntent() {
		Intent completingIntent = new Intent();
		completingIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		completingIntent.putExtra("MenuMessage", menuItem);
		completingIntent.putExtra("cur_progress", cur_progress);
		completingIntent.putExtra("cur_total", cur_total);
		completingIntent.putExtra("isDownload", true);
		completingIntent.setClass(getApplication().getApplicationContext(),
				DownloadAlertActivity.class);

		return PendingIntent.getActivity(AppDownloadService.this, R.string.app_name,
				completingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	}

	class AppDownloadThread extends Thread {

		@Override
		public void run() {
			if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
				if (destDir == null) {
					destDir = new File(Utils.getAppFilePath());
				}
				if (destDir.exists() || destDir.mkdirs()) {
					destFile = new File(destDir.getPath() + "/" + mPkgName);
					if (destFile.exists() && destFile.isFile() && checkApkFile(destFile.getPath())) {
						install(destFile);
						stopSelf();
					} else {
						try {
							DownloadUtils.download(mDownloadUrl, destFile, false, downloadListener);
						} catch (Exception e) {
							Message msg = mHandler.obtainMessage();
							msg.what = DOWNLOAD_FAIL;
							msg.obj = e.getMessage();
							mHandler.sendMessage(msg);
							e.printStackTrace();
							sendBroad(DOWNLOAD_FAIL, 0, 0);
							stopSelf();
						}
					}
				}
			}
		}
	}

	public boolean checkApkFile(String apkFilePath) {
		boolean result = false;
		try {
			PackageManager pManager = getPackageManager();
			PackageInfo pInfo = pManager.getPackageArchiveInfo(apkFilePath,
					PackageManager.GET_ACTIVITIES);
			if (pInfo == null) {
				result = false;
			} else {
				result = true;
			}
		} catch (Exception e) {
			result = false;
			e.printStackTrace();
		}
		return result;
	}

	public void install(File apkFile) {
		Uri uri = Uri.fromFile(apkFile);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(uri, "application/vnd.android.package-archive");
		startActivity(intent);
	}
}
