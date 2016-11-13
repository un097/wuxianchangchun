package cn.ffcs.wisdom.city.push;

import android.content.Context;
import cn.ffcs.wisdom.base.BaseTask;
import cn.ffcs.wisdom.city.changecity.location.LocationUtil;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.utils.ConfigUtil;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.http.HttpRequest;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.Log;

/**
 * <p>Title: 向平台注册由消息平台分配的DeviceToken         </p>
 * <p>Description: 								 </p>
 * <p>@author: liaodl               			 </p>
 * <p>Copyright: Copyright (c) 2013    			 </p>
 * <p>Company: FFCS Co., Ltd.          			 </p>
 * <p>Create Time: 2013-1-8            			 </p>
 * <p>Update Time:                     			 </p>
 * <p>Updater:                        			 </p>
 * <p>Update Comments:                			 </p>
 */
public class RegTokenTask extends BaseTask<Void, Void, BaseResp> {
	private String tokenNum = "";

	// 测试地址
	// private String url =
	// "http://218.5.99.35:8083/icity/service/msgsend/msgInfoOper/registerToken";

	public RegTokenTask(HttpCallBack<BaseResp> iCall, Context context) {
		super(iCall, context);
	}

	public void setParams(String tokenNum) {
		this.tokenNum = tokenNum;
	}

	@Override
	protected BaseResp doInBackground(Void... params) {
		HttpRequest request = new HttpRequest(BaseResp.class);
		BaseResp resp = null;
		try {
			//必传
			String imsi = AppHelper.getSerialCode2(mContext);
			String imei = AppHelper.getIMEI(mContext);
			String osType = AppHelper.getOSType();
			String cityCode = MenuMgr.getInstance().getCityCode(mContext);
			String clientVerType = mContext.getResources().getString(R.string.version_name_update);//版本类型，区分爱城市或者重庆版本
			String clientVerNum = AppHelper.getVersionCode(mContext) + "";//客户端版本号
			String clientChannelType = ConfigUtil.readChannelName(mContext, Config.UMENG_CHANNEL_KEY);//客户端渠道类型
			String menuVerNum = MenuMgr.getInstance().getMenuVer(mContext, cityCode);//菜单版本号

			//可选
			
			/**
			 * //此方法有个问题，用户登了一个号码后，退出爱城市，上报的还是之前那个号码，并不是为空
			 */
//			String mobile = AccountMgr.getInstance().getMobileLocal(mContext);
			String mobile = AccountMgr.getInstance().getMobile(mContext);
			String lat = LocationUtil.getLatitude(mContext);
			String lng = LocationUtil.getLongitude(mContext);
			String operatorName = AppHelper.getSimOperatorName(mContext);//手机运营商

			request.addParameter("imsi", imsi);
			request.addParameter("imei", imei);
			request.addParameter("osType", osType);
			request.addParameter("cityCode", cityCode);
			request.addParameter("clientVerType", clientVerType);
			request.addParameter("clientVerNum", clientVerNum);
			request.addParameter("clientChannelType", clientChannelType);
			request.addParameter("menuVerNum", menuVerNum);
			request.addParameter("tokenNum", tokenNum);

			request.addParameter("mobile", mobile);
			request.addParameter("lat", lat);
			request.addParameter("lng", lng);
			request.addParameter("operatorName", operatorName);

			resp = request.executeUrl(Config.UrlConfig.URL_REGISTER_TOKEN);

			Log.i("--tokenNum=" + tokenNum + "; imsi=" + imsi + "; cityCode=" + cityCode
					+ "; clientVerType=" + clientVerType + "; clientVerNum=" + clientVerNum
					+ "; clientChannelType=" + clientChannelType + "; menuVerNum=" + menuVerNum
					+ "; mobile=" + mobile);
		} catch (Exception e) {
			Log.e("--注册DeviceToken请求异常,失败!--", e);
		} finally {
			request.release();
		}

		if (resp == null)
			return new BaseResp();
		return resp;
	}
}
