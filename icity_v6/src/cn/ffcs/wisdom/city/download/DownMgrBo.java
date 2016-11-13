package cn.ffcs.wisdom.city.download;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.sqlite.model.ApkInfo;
import cn.ffcs.wisdom.city.sqlite.model.MenuItem;
import cn.ffcs.wisdom.city.sqlite.service.ApkInfoService;
import cn.ffcs.wisdom.city.utils.AppMgrUtils;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.StringUtil;

/**
 * <p>Title:  应用汇下载管理逻辑层                         </p>
 * <p>Description:                     </p>
 * <p>@author: liaodl                  </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-3-10           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class DownMgrBo {

	private Activity activity;
	private ApkInfoService service;

	public DownMgrBo(Activity activity) {
		this.activity = activity;
		service = ApkInfoService.getInstance(activity);
	}

	@Deprecated
	public void set2channel(MenuItem entity, Intent intent) {
		if (entity == null) {
			return;
		}
		String url = entity.getAppUrl();
		// 如果有下载记录，说明已下载过
		try {
			ApkInfo info = service.queryByUrl(url);
			if (info != null && info.getDownloadStatu() == ApkMgrConstants.DOWNLOAD_PADDING) {//下载中
				//CommonUtils.showToast(activity, R.string.download_is_exist, Toast.LENGTH_SHORT);
				intent.putExtra(ApkMgrConstants.COMPLETE_DOWNLOAD_FLAG,
						ApkMgrConstants.COMPLETE_DOWNLOAD_NO);
			} else {
				PackageInfo packageInfo = AppHelper.getPackageInfo(activity, info.getPackageName());
				if (packageInfo != null) {// 已经安装过apk
					CommonUtils.showToast(activity, R.string.download_is_install,
							Toast.LENGTH_SHORT);
					service.updateInstallStatu(ApkMgrConstants.INSTALL_SUCCESS, info.getUrl());// 10:已安装
				} else if (info.getInstallStatu() == ApkMgrConstants.INSTALL_FAIL) {
					CommonUtils.showToast(activity, R.string.download_is_no_install,
							Toast.LENGTH_SHORT);
					service.updateInstallStatu(ApkMgrConstants.INSTALL_FAIL, info.getUrl());// 20:未安装
				}
				intent.putExtra(ApkMgrConstants.COMPLETE_DOWNLOAD_FLAG,
						ApkMgrConstants.COMPLETE_DOWNLOAD_YES);
			}
		} catch (Exception e) {
			Log.e("下载错误：" + e);
		}
	}

	/**
	 * 找出已下载的安装包 安装
	 * 
	 * @param entity
	 */
	public void installAppByFile(ApkInfo entity) {
		if (entity == null) {
			return;
		}
		File file = new File(entity.getDir() + entity.getApkName() + ApkMgrConstants.POSTFIX);
		if (file.exists()) {
			AppMgrUtils.installApp(activity, file, entity.getApkName());
			//如果安装成功，更新数据库里下载install_statu状态
			PackageInfo packageInfo = AppHelper.getPackageInfo(activity, entity.getPackageName());
			if (packageInfo != null) {//已经安装过apk
				ApkInfoService.getInstance(activity).updateInstallStatu(
						ApkMgrConstants.INSTALL_SUCCESS, entity.getUrl());
			} else {
				ApkInfoService.getInstance(activity).updateInstallStatu(
						ApkMgrConstants.INSTALL_FAIL, entity.getUrl());
			}
		} else {
			CommonUtils.showToast(activity, R.string.download_again, Toast.LENGTH_SHORT);
			service.deleteByUrl(entity.getUrl());// 从数据库删除此记录
		}
	}

	/**
	 * 打开应用,在下载管理--已安装界面中调用
	 */
	public void runApp(final ApkInfo entity) {
		try {
			runApp(entity.toMenuItem(activity));
		} catch (Exception e) {
			CommonUtils.showToast(activity, R.string.download_run_error, Toast.LENGTH_LONG);
		}
	}

	/**
	 * 打开应用,在二级菜单 应用汇界面中调用
	 */
	public void runApp(MenuItem item) {
		try {
			AppMgrUtils.launchAPP(activity, item, R.string.download_manager, false);
			// new ReportApkBo.sendRunReport(item);//上报运行记录
		} catch (ApkRunException ex) {
			CommonUtils.showToast(activity, R.string.download_run_error, Toast.LENGTH_LONG);
		} catch (Exception e) {
			CommonUtils.showToast(activity, R.string.download_install_again, Toast.LENGTH_SHORT);
			installAppByFile(item);
			sendRefreshBroadcast(ApkMgrConstants.RECEIVER_PACKAGE_REFRESH);
		}
	}

	public void installAppByFile(MenuItem item) {
		if (item == null) {
			return;
		}
		try {
			String url = item.getUrl();
			String dir = Config.SDCARD_APK;
			String name = item.getMenuName();
			File file = new File(dir + name + ApkMgrConstants.POSTFIX);
			if (file.exists()) {
				AppMgrUtils.installApp(activity, file, item.getMenuName());
				// 如果安装成功，更新数据库里下载install_statu状态
				PackageInfo packageInfo = AppHelper.getPackageInfo(activity, item.getPackage_());
				if (packageInfo != null) {// 已经安装过apk
					service.updateInstallStatu(ApkMgrConstants.INSTALL_SUCCESS, url);// 10:已安装
				} else {
					service.updateInstallStatu(ApkMgrConstants.INSTALL_FAIL, url);// 20:未安装
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteApk(ApkInfo entity) {
		if (entity == null) {
			return;
		}
		String name = entity.getApkName();
		String dir = entity.getDir();
		if (StringUtil.isEmpty(dir)) {
			dir = Config.SDCARD_APK;
		}
		File file = new File(dir + name + ApkMgrConstants.POSTFIX);
		if (file.exists()) {
			if (file.delete()) {
				CommonUtils.showToast(activity, R.string.download_delete_success,
						Toast.LENGTH_SHORT);
			} else {
				CommonUtils.showToast(activity, R.string.download_delete_fail, Toast.LENGTH_SHORT);
			}
		} else {
			File noCompletefile = new File(dir + name);
			if (noCompletefile.exists()) {
				if (noCompletefile.delete()) {
					CommonUtils.showToast(activity, R.string.download_delete_success,
							Toast.LENGTH_SHORT);
				} else {
					CommonUtils.showToast(activity, R.string.download_delete_fail,
							Toast.LENGTH_SHORT);
				}
			}
		}
	}

	/**
	 * 发送广播通知Activity更改apk列表 <br/>
	 * 通过control值 判断具体动作 <br/>
	 * RECEIVER_DELETE_APK:更新已下载栏目,且 "正在下载"界面有应用下载完成时,自动跳到"已下载"界面<br/>
	 * RECEIVER_UPDATE:删除任务时刷新"正在下载"界面 <br/>
	 * RECEIVER_PACKAGE_REFRESH:刷新"已下载"界面,相比1，没有自动跳转界面 <br/>
	 */
	public void sendRefreshBroadcast(int control) {
		sendRefreshDownloadBroadcast(control, null);
	}

	/**
	 * 发送广播通知Activity更改apk列表 <br/>
	 * 通过control值 判断具体动作 <br/>
	 * RECEIVER_DELETE_APK:更新已下载栏目,且 "正在下载"界面有应用下载完成时,自动跳到"已下载"界面<br/>
	 * RECEIVER_UPDATE:删除任务时刷新"正在下载"界面 <br/>
	 * RECEIVER_PACKAGE_REFRESH:刷新"已下载"界面,相比1，没有自动跳转界面 <br/>
	 */
	public void sendRefreshDownloadBroadcast(int control, ApkInfo entity) {
		Intent sendIntent = new Intent(Config.ACTION_REFRESH_APK_LIST);
		sendIntent.putExtra(ApkMgrConstants.INTENT_CONTROL_APK, control);
		if (entity != null) {
			sendIntent.putExtra(ApkMgrConstants.INTENT_APK_ENTITY, entity);
		}
		LocalBroadcastManager.getInstance(activity).sendBroadcast(sendIntent);
	}

	/**
	 * 删除临时文件
	 * @param apkInfo
	 */
	public void deleteTmpApk(ApkInfo apkInfo) {
		File noCompletefile = new File(apkInfo.getDir() + apkInfo.getApkName());
		if (noCompletefile.exists()) {
			noCompletefile.delete();
		}

		File apk = new File(apkInfo.getDir() + apkInfo.getApkName() + ApkMgrConstants.POSTFIX);
		if (apk.exists()) {
			apk.delete();
		}
	}

}
