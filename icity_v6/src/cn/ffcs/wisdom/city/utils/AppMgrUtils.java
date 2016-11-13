package cn.ffcs.wisdom.city.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import cn.ffcs.config.ExternalKey;
import cn.ffcs.ui.tools.AlertBaseHelper;
import cn.ffcs.wisdom.city.changecity.location.LocationUtil;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.common.MenuType;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.download.ApkListActivity;
import cn.ffcs.wisdom.city.download.ApkMgrConstants;
import cn.ffcs.wisdom.city.download.ApkRunException;
import cn.ffcs.wisdom.city.download.ByteUtil;
import cn.ffcs.wisdom.city.download.DownMgrBo;
import cn.ffcs.wisdom.city.modular.query.QueryInfoActivity;
import cn.ffcs.wisdom.city.modular.query.QueryInfoDataMgr;
import cn.ffcs.wisdom.city.modular.query.entity.QueryInfo;
import cn.ffcs.wisdom.city.myapp.bo.TrackInfoBo;
import cn.ffcs.wisdom.city.personcenter.LoginActivity;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.personcenter.entity.Account;
import cn.ffcs.wisdom.city.reportmenu.ReportUtil;
//import cn.ffcs.wisdom.city.road.RoadConditionActivity;
import cn.ffcs.wisdom.city.setting.share.SMSShareActivity;
import cn.ffcs.wisdom.city.sqlite.model.ApkInfo;
import cn.ffcs.wisdom.city.sqlite.model.MenuItem;
import cn.ffcs.wisdom.city.sqlite.service.ApkInfoService;
import cn.ffcs.wisdom.city.traffic.violations.TrafficViolationsListActivity;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.city.web.BrowserActivity;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.Base64;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.CrytoUtils;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.MD5;
import cn.ffcs.wisdom.tools.SdCardTool;
import cn.ffcs.wisdom.tools.SharedPreferencesUtil;
import cn.ffcs.wisdom.tools.StringUtil;

import com.umeng.analytics.MobclickAgent;

/**
 * <p>Title:    应用跳转                  </p>
 * <p>Description:                     </p>
 * <p>@author: zhangws                 </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-3-13           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public final class AppMgrUtils {

	private static AppMgrUtils menuItem;

	static final Object sInstanceSync = new Object();

	private AppMgrUtils() {
	}

	public static AppMgrUtils getInstance() {
		synchronized (sInstanceSync) {
			if (menuItem == null) {
				menuItem = new AppMgrUtils();
			}
		}

		return menuItem;
	}

	/**
	 * 启动应用，默认是栏目<br/><br/>
	 * 默认找不到应用时，从网上下载<br/>
	 * 
	 * @param ctx
	 * @param menu
	 */
	public static void launchAPP(Activity activity, MenuItem menu, String returnTitle) {
		if (!launchQuery(activity, menu, returnTitle)) { // 跳转至查询类
			try {
				launchAPP(activity, menu, true, returnTitle, true);
			} catch (ApkRunException e) {
				Log.e(e.getMessage(), e);
			}
		}
	}

	/**
	 * 启动应用，默认是栏目<br/><br/>
	 * @param activity
	 * @param menu
	 * @param returnTitle
	 * @param download 找不到应用时，是否从网上下载
	 * @throws ApkRunException 
	 */
	public static void launchAPP(Activity activity, MenuItem menu, String returnTitle,
			boolean download) throws ApkRunException {
		if (!launchQuery(activity, menu, returnTitle)) { // 跳转至查询类
			launchAPP(activity, menu, true, returnTitle, download);
		}
	}

	public static void launchAPP(Activity activity, MenuItem menu, int resId) {
		try {
			launchAPP(activity, menu, activity.getString(resId), true);
		} catch (ApkRunException e) {
			Log.e(e.getMessage(), e);
		}
	}

	public static void launchAPP(Activity activity, MenuItem menu, int resId, boolean download)
			throws ApkRunException {
		launchAPP(activity, menu, activity.getString(resId), download);
	}

	/**
	 * 判断是否配置查询类
	 * 
	 * @param ctx
	 * @param menu
	 * @return
	 */
	public static boolean launchQuery(Context ctx, MenuItem menu, String returnTitle) {
		// 保存到最近历史记录
		TrackInfoBo.newInstance(ctx).saveOrUpdate(menu);

		// 判断是否查询类
		String itemId = menu.getMenuId();
		String cityCode = MenuMgr.getInstance().getCityCode(ctx);
		QueryInfo queryInfo = QueryInfoDataMgr.getInstance().getQueryInfo(cityCode, itemId);
		if (queryInfo != null) {
			Intent intent = new Intent();
			intent.setClass(ctx, QueryInfoActivity.class);
			intent.putExtra("queryinfo_itemid", itemId);
			intent.putExtra("queryinfo_citycode", cityCode);
			intent.putExtra(Key.K_RETURN_TITLE, returnTitle);
			ctx.startActivity(intent);
			return true;
		}
		return false;
	}

	/**
	 * 启动应用，需要告知是否栏目应用 <br/><br/>
	 * 如果第三方应用不存在，是否下载<br/>
	 * @param ctx
	 * @param menu
	 * @param isCategoryMenu
	 *            是否栏目应用
	 * @throws ApkRunException 
	 */
	public static void launchAPP(Activity activity, MenuItem menu, boolean isCategoryMenu,
			String returnTitle, boolean download) throws ApkRunException {

		if (isCategoryMenu) {
			// 保存到最近历史记录
			TrackInfoBo.newInstance(activity).saveOrUpdate(menu);
		}

		// UMeng 统计分析
		String eventId = MenuUtil.getCityCode(activity);
		MobclickAgent.onEvent(activity, eventId, menu.getMenuName());

		// 启动对应栏目
		String type = menu.getMenuType();
		if (type.equals(MenuType.NATIVE_APP) || type.equals(MenuType.EXTERNAL_APP)) {
			AppMgrUtils.getInstance().startApp(activity, menu, returnTitle, download);
		} else if (type.equals(MenuType.WAP)) {
			AppMgrUtils.openUrl(activity, menu, returnTitle);
		} else if (type.equals(MenuType.NAVI_MENU)) {

		}
	}

	/**
	 * 浏览wap
	 * 
	 * @param activity
	 * @param entity
	 */
	public static void openUrl(final Activity activity, MenuItem entity, String returnTitle) {
		try {
			Context context = activity.getApplicationContext();
			// 保存到最近历史记录
			TrackInfoBo.newInstance(context).saveOrUpdate(entity);

			// 上报访问记录
			ReportUtil.sendAccessReport(context, entity);

			List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair(Key.U_BROWSER_CITYCODE, MenuMgr.getInstance()
					.getCityCode(context)));
			params.add(new BasicNameValuePair(Key.U_BROWSER_ITEMID, String.valueOf(entity
					.getMenuId())));
			params.add(new BasicNameValuePair(Key.U_BROWSER_IMSI, AccountMgr.getInstance().getImsi(
					context)));
			String url = urlConverter(entity.getUrl(), context, params); // 增加参数

			Bundle bundle = new Bundle();
			bundle.putString(Key.U_BROWSER_URL, url);
			bundle.putString(Key.U_BROWSER_TITLE, entity.getMenuName());
			bundle.putString(Key.K_RETURN_TITLE, returnTitle);
			bundle.putSerializable(Key.U_BROWSER_ITEM, entity);
			bundle.putString(Key.U_BROWSER_QUERY, entity.getIsApp());
			if (!isNeedLogin(entity, activity, returnTitle)) {
				Intent intent = new Intent();
				intent.setClass(activity, BrowserActivity.class);
				intent.putExtras(bundle);
				activity.startActivity(intent);
			}
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
	}

	/**
	 * 是否需要登陆
	 * 
	 * @param context
	 * @param entity
	 * @return
	 */
	private static boolean isNeedLogin(MenuItem entity, final Activity activity,
			final String returnTitle) {
		try {
			Context context = activity.getApplicationContext();
			if (entity.isMustLogin()) {
				// 需要登录用户
				// final Account account = AccountUtil.readAccountInfo(context);
				// modify by caijj 2013-3-18
				if (!AccountMgr.getInstance().isLogin(activity)) {
					String tipsFormat = context.getString(R.string.menu_item_click_login_notice);
					AlertBaseHelper.showConfirm(activity, "",
							String.format(tipsFormat, entity.getMenuName()),
							new View.OnClickListener() {

								@Override
								public void onClick(View arg0) {
									AlertBaseHelper.dismissAlert(activity);
									Intent loginIntent = new Intent(activity, LoginActivity.class);
									loginIntent.putExtra(Key.K_RETURN_TITLE, returnTitle);
									activity.startActivity(loginIntent);
								}
							});
					return true;
				}
			}
		} catch (Exception e) {
			Log.e(e.getMessage());
		}
		return false;
	}

	/**
	 * URL转换器，添加参数到URL中。
	 */
	private static String urlConverter(String url, Context context, List<BasicNameValuePair> params) {

		try {
			Account account = AccountMgr.getInstance().getAccount(context);
			if (url.indexOf('?') == -1) {
				if (!StringUtil.isEmpty(account.getData().getMobile())) {// 规避当&key=null导致有部分网页无法访问的问题
					url = url + "?accnbr=" + account.getData().getMobile();
				} else {
					url = url + "?";
				}
			} else {
				if (!StringUtil.isEmpty(account.getData().getMobile())) {// 规避当&key=null导致有部分网页无法访问的问题
					url = url + "&accnbr=" + account.getData().getMobile();
				}
			}

			for (Iterator<BasicNameValuePair> it = params.iterator(); it.hasNext();) {
				BasicNameValuePair param = it.next();
				if (!StringUtil.isEmpty(param.getValue())) { // 规避当&key=null导致有部分网页无法访问的问题
					url = url + "&" + param.getName() + "=" + param.getValue();
				}
			}
		} catch (Exception e) {
			Log.e(e.getMessage());
		}
		return url;
	}

	/**
	 * 启动第三方的应用<br/><br/>
	 * 如果第三方应用不存在，是否下载<br/>
	 * @param menuText
	 *            菜单名称
	 * @param item
	 *            应用对象
	 */
	public void startApp(final Activity activity, final MenuItem item, String returnTitle,
			boolean download) throws ApkRunException {
		Context mContext = activity.getApplicationContext();
		if (item == null) {
			return;
		}
		// 上报访问记录
		ReportUtil.sendAccessReport(activity, item);
		// 检验是否需要登陆
		if (isNeedLogin(item, activity, returnTitle)) {
			return;
		}

		// 组装参数
		Account account = AccountMgr.getInstance().getAccount(activity);
		String userName = "";
		String phoneNumber = "";
		String userId = "";
		if (account.getData() != null) {
			userName = account.getData().getUserName();
			// String passWord = account.getPassword();
			phoneNumber = account.getData().getMobile();
			userId = account.getData().getUserId() + "";
		}
		String cityCode = MenuMgr.getInstance().getCityCode(activity);
		String cityName = MenuMgr.getInstance().getCityName(activity);

		Bundle bundle = new Bundle();
		bundle.putString(ExternalKey.K_USERNAME, userName);
		bundle.putString(ExternalKey.K_USER_ID, userId); // add by caijj
															// 2013-10-16
															// 支撑水电煤外包
		// bundle.putString(ExternalKey.K_PASSWORD, passWord);// 密码也给出去？？有没搞错。。
		bundle.putString(ExternalKey.K_PHONENUMBER, phoneNumber);
		bundle.putString(ExternalKey.K_CITYCODE, cityCode);
		bundle.putString(ExternalKey.K_CITYNAME, cityName);
		bundle.putString(ExternalKey.K_MENUID, String.valueOf(item.getMenuId()));
		bundle.putString(ExternalKey.K_MENUNAME, item.getMenuName());
		bundle.putString(ExternalKey.K_ANDROID_URL, item.getAppUrl());
		bundle.putString(ExternalKey.K_URL, item.getUrl());
		bundle.putString(ExternalKey.K_CLIENT_TYPE, AppHelper.getVersionCode(mContext) + "");
		bundle.putString(Key.K_RETURN_TITLE, returnTitle);
		
		/***************加密校验参数  Start*******************/
		String type = item.getMenuType();
		if (type.equals(MenuType.EXTERNAL_APP) && !StringUtil.isEmpty(phoneNumber)) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd H:mm:ss");
			String timestamp = sdf.format(new Date());
			String PassportSSORequestValue = "src_id=" + item.getPackage_() + "&" + "app_id="
					+ item.getMain() + "&" + "mobile=" + phoneNumber + "&" + "timestamp="
					+ timestamp;

			if (!StringUtil.isEmpty(PassportSSORequestValue)) {
				try {
					bundle.putString("PassportSSORequestValue",
							CrytoUtils.encode("SysKey", PassportSSORequestValue));
				} catch (Exception e) {
					Log.e(e + "");
				}
			}
		}
		/***************加密校验参数  End *******************/

		// 路况 - 传递经纬度
		bundle.putString(ExternalKey.K_LOCATION_LAT, LocationUtil.getLatitude(mContext));
		bundle.putString(ExternalKey.K_LOCATION_LNG, LocationUtil.getLongitude(mContext));
		bundle.putString(ExternalKey.K_ICITY_LOCATION_CITY_CODE,
				LocationUtil.getLocationCityCode(mContext));
		bundle.putString(ExternalKey.K_ICITY_LOCATION_CITY_NAME,
				LocationUtil.getLocationCityName(mContext));
		String locationTyjxCode = MenuMgr.getInstance().getLocationTyjxCitycode(activity);
		bundle.putString(ExternalKey.K_LOCATION_AREA_CODE, locationTyjxCode);// 天翼景象城市区域编码

		// 翼支付参数
		bundle.putString(ExternalKey.K_PRODUCTNO, AccountMgr.getInstance().getMobile(activity));
		// TODO
		String proviceCode = MenuUtil.getProviceCode(activity);
		bundle.putString(ExternalKey.K_LOCATION,
				XmlParser.getProviceCodeByCode(activity, proviceCode));

		// 天翼景象
		String tyjxCode = MenuMgr.getInstance().getTyjxCitycode(activity);
		bundle.putString(ExternalKey.K_AREA_CODE, tyjxCode);// 城市区域编码
		bundle.putString(ExternalKey.K_CITY_NAME, cityName);// 城市名称
		bundle.putString(ExternalKey.K_MENUID, item.getMenuId());
		// TODO 参数作用？
		SharedPreferencesUtil.setValue(activity, ExternalKey.K_MENUID,
				String.valueOf(item.getMenuId()));

		Intent intent = new Intent();

		// 翼支付-业务类型 add by caijj 2013-3-16
		String payCategory = "";

		// 获取自定义参数
		String params = item.getMap();
		shareOtherParams(activity, params);
		Map<String, String> paramsMap = ConfigUtil.params2Map(params);
		Set<String> paramsMapKeySet = paramsMap.keySet();
		for (Iterator<String> it = paramsMapKeySet.iterator(); it.hasNext();) {
			String key = it.next();

			// 翼支付-业务类型 add by caijj 2013-3-16
			if (ExternalKey.K_PAY_CATEGORY.equals(key)) {
				payCategory = paramsMap.get(key);

			}
			bundle.putString(key, paramsMap.get(key));
		}

		// 翼支付-v2 add by caijj 2013-3-16
		String timestamp = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());
		bundle.putString(ExternalKey.K_PAY_CITY_CODE, cityCode + "00");
		bundle.putString(ExternalKey.K_PAY_MOBILE, phoneNumber);
		bundle.putString(ExternalKey.K_PAY_TIME_TAMP, timestamp);
		bundle.putString(ExternalKey.K_PAY_CATEGORY, payCategory);
		String signCode = "icity" + cityCode + "00" + phoneNumber + payCategory + timestamp;
		signCode = MD5.getMD5Str(signCode);
		signCode = Base64.encodeToString(signCode.getBytes(), 0);
		if (!StringUtil.isEmpty(signCode)) {
			bundle.putString(ExternalKey.K_PAY_SIGN_CODE, signCode);
		}

		// 如果为内部应用，代码获取本应用包名
		String itemType = item.getMenuType();
		String packageName = MenuType.NATIVE_APP.equals(itemType) ? AppHelper
				.getPackageName(activity) : item.getPackage_();
		String cls = item.getMain();

		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if ("cn.ffcs.wisdom.city.BreakRulesActivity".equals(cls)) {// 兼容旧版违章
			intent.setComponent(new ComponentName(mContext, TrafficViolationsListActivity.class));
		} else if ("cn.ffcs.wisdom.city.SMSShareActivity".equals(cls)) {// 兼容V5短信跳转
			intent.setComponent(new ComponentName(mContext, SMSShareActivity.class));
//		} else if ("cn.ffcs.external.road.activity.RoadConditionActivity".equals(cls)) {// 兼容旧版厦门路况
//			intent.setComponent(new ComponentName(mContext, RoadConditionActivity.class));
		} else {
			if (cls != null) {
				intent.setComponent(new ComponentName(packageName, cls));
			}
		}

		intent.putExtras(bundle);
		try {
			activity.startActivity(intent);
		} catch (Exception e) {
			Log.d(item.getMain() + ":" + e.toString());
			if (download) {// 如果第三方应用不存在，就下载
				getExternalApp(activity, item, returnTitle);
			} else {// 提示包名类名配置错误，启动不了应用
				throw new ApkRunException("包名、类名信息错误!");
			}
		}
	}

	/**
	 * 保存共享启动第三方应用需要的一些其他参数 --by liaodl
	 */
	public static void shareOtherParams(Context context, String params) {
		SharedPreferencesUtil.setValue(context, Key.K_OTHER_APP_KEY_PARAMS, params);

	}

	public static String getOtherParams(Context context) {
		return SharedPreferencesUtil.getValue(context, Key.K_OTHER_APP_KEY_PARAMS);
	}

	/**
	 * 获取第三方的APP，从网上下载，并安装应用
	 * 
	 * @param act
	 * @param pgBar
	 * @param apkUrl
	 * @param fileApk
	 */
	private void getExternalApp(final Activity activity, final MenuItem item,
			final String returnTitle) {
		if (!SdCardTool.isMounted()) {
			CommonUtils.showToast(activity, R.string.download_sd_nomount, Toast.LENGTH_SHORT);
			return;
		}

		try {
			if (item == null || StringUtil.isEmpty(item.getAppUrl())) {
				return;
			}

			Intent intent = new Intent(activity, ApkListActivity.class);
			intent.putExtra(Key.K_RETURN_TITLE, returnTitle);

			String appUrl = item.getAppUrl();
			if (StringUtil.isEmpty(appUrl)) {
				CommonUtils.showToast(activity, R.string.download_url_no_exits, Toast.LENGTH_SHORT);
				return;
			}
			ApkInfoService service = ApkInfoService.getInstance(activity);
			// 判断应用是否下载
			boolean flag = service.isExistByUrl(appUrl)
					|| service.isExistByName(item.getMenuName());
			if (!flag) {// 首次下载
				showDownDlgTip(activity, item, returnTitle, service, intent);
				// new ReportApkBo(context).sendDownloadReport(entity);// 上报记录
				return;
			} else {
				hasDownload(activity, intent, item, service);
			}
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
	}

	private void showDownDlgTip(final Activity activity, final MenuItem item,
			final String returnTitle, final ApkInfoService service, final Intent intent) {
		String appName = item.getMenuName();
		String appSize = "";
		String formatSize = "";
		if (!StringUtil.isEmpty(item.getAppsize())) {
			appSize = item.getAppsize().trim().replaceAll("K", "").replaceAll("k", "");
			formatSize = ByteUtil.bytes2KBorMB(Integer.valueOf(appSize) * 1024);
		}
		String appDesc = item.getMenudesc();
		String appSizeIndex = getString(activity, R.string.download_dialog_app_size);
		String descIndex = getString(activity, R.string.download_dialog_desc_index);
		String sure = getString(activity, R.string.download_dialog_desc);
		String msg = "";
		if (!StringUtil.isEmpty(appSize) && !StringUtil.isEmpty(appDesc)) {
			msg = "\n" + appSizeIndex + formatSize + "\n\n" + descIndex + appDesc + "\n\n" + sure
					+ "\n";
		} else if (StringUtil.isEmpty(appSize) && !StringUtil.isEmpty(appDesc)) {
			msg = "\n" + descIndex + "\n" + appDesc + "\n\n" + sure + "\n";
		} else if (!StringUtil.isEmpty(appSize) && StringUtil.isEmpty(appDesc)) {
			msg = "\n" + appSizeIndex + formatSize + "\n\n" + sure + "\n";
		} else {
			msg = sure;
		}

		OnClickListener confirmClick = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertBaseHelper.dismissAlert(activity);
				ApkInfo info = ApkInfo.toApkInfo(item);
				service.add(info);
				new DownMgrBo(activity).deleteTmpApk(info);
				activity.startActivity(intent);
			}
		};
		String sureBtnTxt = getString(activity, R.string.btn_download);
		AlertBaseHelper.showConfirm(activity, appName, msg, sureBtnTxt, "", confirmClick, null);
	}

	private String getString(Activity activity, int resId) {
		return activity.getResources().getString(resId);
	}

	/**
	 * 下载应用
	 * @param activity
	 * @param entity
	 */
	public void requestDownload(Activity activity, MenuItem item, String returnTitle) {

		if (!SdCardTool.isMounted()) {
			CommonUtils.showToast(activity, R.string.download_sd_nomount, Toast.LENGTH_SHORT);
			return;
		}
		if (item == null) {
			return;
		}

		Intent intent = new Intent(activity, ApkListActivity.class);
		intent.putExtra(Key.K_RETURN_TITLE, returnTitle);

		try {
			String appUrl = item.getAppUrl();
			ApkInfoService service = ApkInfoService.getInstance(activity);
			// 判断应用是否下载
			boolean flag = service.isExistByUrl(appUrl);
			if (flag) {
				hasDownload(activity, intent, item, service);
			} else {// 首次下载
				ApkInfo info = ApkInfo.toApkInfo(item);
				service.add(info);
				new DownMgrBo(activity).deleteTmpApk(info);
			}

			// new ReportApkBo(context).sendDownloadReport(entity);// 上报记录
			activity.startActivity(intent);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
	}

	private void hasDownload(Activity activity, Intent intent, MenuItem item, ApkInfoService service) {
		ApkInfo info = service.queryByUrl(item.getAppUrl());
		if (info == null) {
			info = service.queryByName(item.getMenuName());
		}
		int statu = info.getDownloadStatu();
		// intent.putExtra(ApkMgrConstants.INTENT_APK_ENTITY, info);

		if (ApkMgrConstants.DOWNLOAD_PADDING == statu) {
			intent.putExtra(ApkMgrConstants.COMPLETE_DOWNLOAD_FLAG,
					ApkMgrConstants.COMPLETE_DOWNLOAD_NO);
		} else if (ApkMgrConstants.DOWNLOAD_RUNNING == statu
				|| ApkMgrConstants.DOWNLOAD_PAUSE == statu) {
			CommonUtils.showToast(activity, R.string.download_is_exist, Toast.LENGTH_SHORT);
			intent.putExtra(ApkMgrConstants.COMPLETE_DOWNLOAD_FLAG,
					ApkMgrConstants.COMPLETE_DOWNLOAD_NO);
		} else {
			PackageInfo packageInfo = AppHelper.getPackageInfo(activity, info.getPackageName());
			if (packageInfo != null) {// 已经安装过apk
				CommonUtils.showToast(activity, R.string.download_is_install, Toast.LENGTH_SHORT);
				service.updateInstallStatu(ApkMgrConstants.INSTALL_SUCCESS, info.getUrl());
			} else {
				CommonUtils
						.showToast(activity, R.string.download_is_no_install, Toast.LENGTH_SHORT);
				service.updateInstallStatu(ApkMgrConstants.INSTALL_FAIL, info.getUrl());
			}
			intent.putExtra(ApkMgrConstants.COMPLETE_DOWNLOAD_FLAG,
					ApkMgrConstants.COMPLETE_DOWNLOAD_YES);
		}
		activity.startActivity(intent);
	}

	/**
	 *  安装应用
	 * @param activity
	 * @param fileApk apk文件名，包含完整路径
	 */
	public static void installApp(Activity activity, File fileApk) {
		installApp(activity, fileApk, "");
	}

	public static void installApp(Activity activity, File fileApk, String appName) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		intent.setDataAndType(Uri.fromFile(fileApk), "application/vnd.android.package-archive");
		activity.startActivity(intent);

		// ReportApkBo reportApkBo = new ReportApkBo(ctx);
		// reportApkBo.setAppName(appName);
		// reportApkBo.setAppType("external_app");
		// 设置版本号
		// reportApkBo.setVersion("430");
		// reportApkBo.sendInstalldReport();// 上报记录
	}

}
