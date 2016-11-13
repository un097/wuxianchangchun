package cn.ffcs.wisdom.city.personcenter.bo;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import cn.ffcs.wisdom.base.CommonNewTask;
import cn.ffcs.wisdom.city.changecity.location.LocationUtil;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.personcenter.entity.Account;
import cn.ffcs.wisdom.city.reportmenu.ReportUtil;
import cn.ffcs.wisdom.city.utils.ConfigUtil;
import cn.ffcs.wisdom.city.utils.LoginPwdEncrypter;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.ManifestUtil;

/**
 * <p>Title: 登录业务逻辑处理         </p>
 * <p>Description: 
 * </p>
 * <p>@author: Leo                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-2-27             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class LoginBo {
	private Context mContext;
	private String cityCode;// 城市编号
	private String clientVerType;// 客户端版本类型
	private String clientVerNum;// 版本号
	private String clientChannelType;// 渠道类型
	private HttpCallBack<BaseResp> mCall;
	private String imsi;
	private String longitude;// 经度
	private String latitude;// 维度

	public LoginBo(HttpCallBack<BaseResp> iCall, Activity activity) {
		this.mCall = iCall;
		mContext = activity.getApplicationContext();
		clientVerType = activity.getString(R.string.version_name_update);
		clientVerNum = String.valueOf(AppHelper.getVersionCode(activity));
		clientChannelType = ConfigUtil.readChannelName(mContext, Config.UMENG_CHANNEL_KEY);
		longitude = LocationUtil.getLongitude(mContext);
		latitude = LocationUtil.getLatitude(mContext);
		cityCode = MenuMgr.getInstance().getCityCode(activity);
		imsi = AppHelper.getMobileIMSI(mContext);
	}

	/**
	 * 提交登录
	 * @param mobile
	 * @param password
	 */
	public void login(String mobile, String password, Context context) {
		CommonNewTask task = CommonNewTask.newInstance(mCall, context, Account.class);
		Map<String, String> params = new HashMap<String, String>();
		params.put("mobile", mobile);
		params.put("cityCode", cityCode);
		params.put("password", LoginPwdEncrypter.EncryPwd(password));
		params.put("clientVerType", clientVerType);
		params.put("clientVerNum", clientVerNum);
		params.put("clientChannelType", clientChannelType);
		params.put("client_version", clientVerNum);
		params.put("client_channel_type", clientChannelType);
		params.put("imsi", imsi);
		String imei = AppHelper.getIMEI(mContext);
		params.put("imei", imei);
		task.setParams(params, Config.UrlConfig.URL_LOGIN_CHECK_NEW);
		task.execute();
	}

	/**
	 * 自动登录
	 */
	public void autoLogin(Context context) {
		CommonNewTask task = CommonNewTask.newInstance(mCall, context, Account.class);
		Map<String, String> params = new HashMap<String, String>();
		String imei = AppHelper.getIMEI(mContext);
		Log.i("imsi: " + imsi);
		
		String timestamp = String.valueOf(System.currentTimeMillis());
//		String sign = ReportUtil.signKey(timestamp);
		String sign = ReportUtil.signKey(timestamp+"$"+imsi);
		params.put("sign", sign);
		params.put("timestamp", timestamp);
		params.put("client_type", clientVerType);
		params.put("clientVerNum", clientVerNum);
		params.put("clientChannelType", clientChannelType);
		params.put("lng", longitude);
		params.put("lat", latitude);
		params.put("cityCode", cityCode);
		params.put("imsi", LoginPwdEncrypter.EncryPwd(imsi));
		task.setParams(params, Config.UrlConfig.URL_AUTO_LOGIN_NEW);
		task.execute();
	}
}
