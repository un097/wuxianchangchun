package cn.ffcs.wisdom.city.simico.kit.activity;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.ffcs.wisdom.city.R;
import cn.ffcs.wisdom.city.simico.base.AppDownloadBroadcast;
import cn.ffcs.wisdom.city.simico.base.AppDownloadBroadcast.UpdateUIView;
import cn.ffcs.wisdom.city.simico.base.AppDownloadService;
import cn.ffcs.wisdom.city.simico.base.Application;
import cn.ffcs.wisdom.city.simico.base.Constants;
import cn.ffcs.wisdom.city.simico.image.CommonImageLoader;
import cn.ffcs.wisdom.city.sqlite.model.MenuItem;

public class DownloadAlertActivity extends Activity implements OnClickListener, UpdateUIView {

	private TextView mTvTitle;
	private TextView mTvMessage;
	private TextView mTvSure;
	private TextView mTvCancle;
	private boolean isDownload = false;
	private MenuItem menuItem;
	private AppDownloadBroadcast broadcast;
	private ProgressBar mProgressBar;
	private TextView mProgressTv;
	private ImageView mIcon;
	private CommonImageLoader mImageLoader;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simico_download_alertdialog);
		setFinishOnTouchOutside(false);
		isDownload = getIntent().getBooleanExtra("isDownload", false);
		if (getIntent().hasExtra("MenuMessage")) {
			menuItem = (MenuItem) getIntent().getSerializableExtra("MenuMessage");
		}
		if (!isDownload && menuItem == null) {
			Application.showToastShort("获取下载信息异常。");
			finish();
		}
		if (getIntent().getBooleanExtra("isWait", false))
			Toast.makeText(
					this,
					String.format(getResources().getString(R.string.app_download_wait),
							menuItem.getMenuName()), Toast.LENGTH_SHORT).show();
		mTvTitle = (TextView) findViewById(R.id.app_title);
		mTvMessage = (TextView) findViewById(R.id.app_text);
		mTvSure = (TextView) findViewById(R.id.download_sure);
		mTvCancle = (TextView) findViewById(R.id.download_cancle);
		mProgressBar = (ProgressBar) findViewById(R.id.app_download_progressbar);
		mProgressTv = (TextView) findViewById(R.id.app_download_text);
		mIcon = (ImageView) findViewById(R.id.iv_icon);
		mTvSure.setOnClickListener(this);
		mTvCancle.setOnClickListener(this);
		mImageLoader = new CommonImageLoader(Application.context().getApplicationContext());
		mImageLoader.setDefaultFailImage(R.drawable.simico_default_service);
		updateView(true);
	}

	private void updateView(boolean updateService) {
		if (isDownload) {
			mTvTitle.setText("正在下载");
			mTvSure.setText("后台运行");
			mTvCancle.setText("取消下载");
			mTvMessage.setVisibility(View.GONE);
			findViewById(R.id.app_download_layout).setVisibility(View.VISIBLE);
//			Application.getPicasso().load(menuItem.getIcon()).into(mIcon);
			mImageLoader.loadUrl(mIcon, menuItem.getIcon());
			mIcon.setTag(menuItem.getIcon());
			long t = getIntent().getLongExtra("cur_total", 0);
			mProgressTv.setText(String.format("%.2f", t / 1024.0 / 1024.0) + "MB / "
					+ getAppSize(menuItem.getAppsize()));
			mProgressBar.setProgress(getIntent().getIntExtra("cur_progress", 0));

			IntentFilter filter = new IntentFilter(Constants.INTENT_ACTION_DOWNLOAD_APP);
			broadcast = new AppDownloadBroadcast(this);
			registerReceiver(broadcast, filter);

			if (updateService) {
				Intent intent = new Intent(DownloadAlertActivity.this, AppDownloadService.class);
				intent.putExtra("MenuMessage", menuItem);
				intent.putExtra("isbackrun", false);
				startService(intent);
			}
		} else {
			mTvSure.setText("下载");
			mTvCancle.setText("取消");
			mTvMessage.setVisibility(View.VISIBLE);
			findViewById(R.id.app_download_layout).setVisibility(View.GONE);
			mTvTitle.setText(menuItem.getMenuName());
			String message = "大小：" + getAppSize(menuItem.getAppsize()) + "\n" + "描述："
					+ menuItem.getMenudesc() + "\n是否下载应用？";
			mTvMessage.setText(message);
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.download_cancle) {
			if (!isDownload) {
				finish();
				return;
			}
			ActivityManager myManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningServiceInfo> runningService = myManager.getRunningServices(50);
			for (int i = 0; i < runningService.size(); i++) {
				if (runningService.get(i).service.getClassName().equals(
						"cn.ffcs.wisdom.city.simico.base.AppDownloadService")) {
//					stopService(new Intent(this, AppDownloadService.class));
					AppDownloadService.stopDownload();
					break;
				}
			}
			finish();
		} else if (id == R.id.download_sure) {
			Intent intent = new Intent(DownloadAlertActivity.this, AppDownloadService.class);
			intent.putExtra("MenuMessage", menuItem);
			intent.putExtra("isbackrun", isDownload);
			startService(intent);
			if (!isDownload) {
				isDownload = true;
				updateView(false);
			} else {
				finish();
			}
		}
	}

	@Override
	public void updateProgress(int v, long t) {
		// TODO Auto-generated method stub
		mProgressBar.setProgress(v);
		mProgressTv.setText(String.format("%.2f", t / 1024.0 / 1024.0) + "MB / "
				+ getAppSize(menuItem.getAppsize()));
	}

	@Override
	public void updateFinish() {
		// TODO Auto-generated method stub
		finish();
	}

	@Override
	public void updateFail() {
		// TODO Auto-generated method stub
		isDownload = false;
		updateView(false);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (isDownload) {
			Intent intent = new Intent(DownloadAlertActivity.this, AppDownloadService.class);
			intent.putExtra("MenuMessage", menuItem);
			intent.putExtra("isbackrun", isDownload);
			startService(intent);
		}
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (broadcast != null)
			unregisterReceiver(broadcast);
	}

	private String getAppSize(String s) {
		s = s.toUpperCase().trim();
		if (s.endsWith("B"))
			return s;
		try {
			return String.format("%.2f", Integer.valueOf(s) / 1024.0) + "MB";
		} catch (Exception ex) {
			return s;
		}
	}
}
