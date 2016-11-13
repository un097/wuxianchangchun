//package cn.ffcs.changchuntv.util;
//
//import java.io.File;
//
//import cn.ffcs.wisdom.city.download.ApkRunException;
//import cn.ffcs.wisdom.city.simico.activity.Utils;
//import cn.ffcs.wisdom.city.simico.base.AppDownloadService;
//import cn.ffcs.wisdom.city.simico.base.Application;
//import cn.ffcs.wisdom.city.simico.kit.activity.DownloadAlertActivity;
//import cn.ffcs.wisdom.city.utils.AppMgrUtils;
//import android.app.Activity;
//import android.app.ActivityManager;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Environment;
//import android.text.TextUtils;
//
//public class DownloadApk {
//	private Context mContext;
//	private Activity mActivity;
//	public DownloadApk(Activity activity){
//		this.mActivity = activity;
//	}
//	
//	private boolean isInstallByread(String packageName) {
//		return new File("/data/data/" + packageName).exists();
//	}
//
//	public void launchApp(String packageName, MenuItem menu) {
//		if (isInstallByread(packageName)) {
//			// Intent intent =
//			// getPackageManager().getLaunchIntentForPackage(packageName);
//			// startActivity(intent);
//			// ServiceHelper.openService(mActivity, menu);
//			// AppMgrUtils.launchAPP(mActivity, menu, "");
//			try {
//				AppMgrUtils.getInstance().startApp(mActivity, menu, "", false);
//			} catch (ApkRunException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} else {
//			String mPkgName = menu.getPackage_() + ".apk";
//			if (Environment.getExternalStorageState().equals(
//					android.os.Environment.MEDIA_MOUNTED)) {
//				File destDir = new File(Utils.getAppFilePath());
//				if (destDir.exists()) {
//					File destFile = new File(destDir.getPath() + "/" + mPkgName);
//					if (destFile.exists() && destFile.isFile()
//							&& checkApkFile(destFile.getPath())) {
//						install(destFile);
//						return;
//					}
//				}
//			}
//			if (TextUtils.isEmpty(menu.getAppUrl())) {
//				Application.showToastShort("没有找到下载地址");
//				return;
//			}
//			Intent intent = new Intent(this, DownloadAlertActivity.class);
//			intent.putExtra("MenuMessage", menu);
//			intent.putExtra("isDownload", false);
//			ActivityManager myManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//			List<RunningServiceInfo> runningService = myManager
//					.getRunningServices(50);
//			for (int i = 0; i < runningService.size(); i++) {
//				if (runningService.get(i).service.getClassName().equals(
//						"cn.ffcs.wisdom.city.simico.base.AppDownloadService")) {
//					intent.putExtra("isDownload", true);
//					if (AppDownloadService.menuItem != null
//							&& !menu.getMenuName().equals(
//									AppDownloadService.menuItem.getMenuName())) {
//						intent.putExtra("MenuMessage",
//								AppDownloadService.menuItem);
//						intent.putExtra("isWait", true);
//					}
//					break;
//				}
//			}
//			startActivity(intent);
//		}
//	}
//
//	private boolean checkApkFile(String apkFilePath) {
//		boolean result = false;
//		try {
//			PackageManager pManager = getPackageManager();
//			PackageInfo pInfo = pManager.getPackageArchiveInfo(apkFilePath,
//					PackageManager.GET_ACTIVITIES);
//			if (pInfo == null) {
//				result = false;
//			} else {
//				result = true;
//			}
//		} catch (Exception e) {
//			result = false;
//			e.printStackTrace();
//		}
//		return result;
//	}
//
//	private void install(File apkFile) {
//		Uri uri = Uri.fromFile(apkFile);
//		Intent intent = new Intent(Intent.ACTION_VIEW);
//		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		intent.setDataAndType(uri, "application/vnd.android.package-archive");
//		startActivity(intent);
//	}
//}
