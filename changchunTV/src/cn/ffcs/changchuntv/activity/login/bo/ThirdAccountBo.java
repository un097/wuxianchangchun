package cn.ffcs.changchuntv.activity.login.bo;

import java.util.Map;

import android.content.Context;
import cn.ffcs.changchuntv.R;
import cn.ffcs.config.ExternalKey;
import cn.ffcs.wisdom.base.CommonStandardNewTask;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;

public class ThirdAccountBo {

	public void isFirstLogin(HttpCallBack<BaseResp> iCall, Context context,
			Map<String, String> map, String sign) {
		commonTask(iCall, context, map, Config.UrlConfig.URL_IS_FIRST_LOGIN, sign);

	}

	public void isRegisteredMobile(HttpCallBack<BaseResp> iCall, Context context,
			Map<String, String> map, String sign) {
		commonTask(iCall, context, map, Config.UrlConfig.URL_IS_REGISTERED_MOBILE, sign);
	}

	public void isRepeatName(HttpCallBack<BaseResp> iCall, Context context,
			Map<String, String> map, String sign) {
		commonTask(iCall, context, map, Config.UrlConfig.URL_IS_REPEAT_NAME, sign);
	}

	public void isBoundMobile(HttpCallBack<BaseResp> iCall, Context context,
			Map<String, String> map, String sign) {
		commonTask(iCall, context, map, Config.UrlConfig.URL_IS_BOUND_MOBILE, sign);
	}

	public void sendCaptcha(HttpCallBack<BaseResp> iCall, Context context, Map<String, String> map,
			String sign) {
		commonTask(iCall, context, map, Config.UrlConfig.URL_SEND_CAPTCHA, sign);
	}

	public void verifyCaptcha(HttpCallBack<BaseResp> iCall, Context context,
			Map<String, String> map, String sign) {
		commonTask(iCall, context, map, Config.UrlConfig.URL_VERIFY_CAPTCHA, sign);
	}

	public void saveBinding(HttpCallBack<BaseResp> iCall, Context context, Map<String, String> map,
			String sign) {
		commonTask(iCall, context, map, Config.UrlConfig.URL_SAVE_BINDING, sign);
	}

	public void updatePerInfo(HttpCallBack<BaseResp> iCall, Context context,
			Map<String, String> map, String sign) {
		commonTask(iCall, context, map, Config.UrlConfig.URL_UPDATE_PERINFO, sign);
	}
	
	public void inputInvitation(HttpCallBack<BaseResp> iCall, Context context,
			Map<String, String> map, String sign) {
		commonTask(iCall, context, map, Config.UrlConfig.URL_INPUT_INVITATION, sign);
	}

	public void getPerInfo(HttpCallBack<BaseResp> iCall, Context context, Map<String, String> map,
			String sign) {
		commonTask(iCall, context, map, Config.UrlConfig.URL_GET_PERINFO, sign);
	}

	public void getPoints(HttpCallBack<BaseResp> iCall, Context context, Map<String, String> map,
			String sign) {
		commonTask(iCall, context, map, Config.UrlConfig.URL_GET_POINTS, sign);
	}

	public void getAdvertising(HttpCallBack<BaseResp> iCall, Context context,
			Map<String, Object> map, String sign) {
		ObjectCommonTask(iCall, context, map,
				"http://ccgd.153.cn:50081/icity-api-client-other/icity/service/adv/getBannerAdv",
				sign);
	}

	private void commonTask(HttpCallBack<BaseResp> iCall, Context context, Map<String, String> map,
			String url, String sign) {
		CommonStandardNewTask task = CommonStandardNewTask.newInstance(iCall, context,
				BaseResp.class);
		String productId = ExternalKey.K_PRODUCT_ID;
		String clientType = ExternalKey.K_CLIENT_TYPE;
		context.getResources().getString(R.string.version_name_update);
		task.setParams(url, map, productId, clientType, map.get("mobile"), map.get("cityCode"),
				map.get("orgCode"), map.get("longitude"), map.get("latitude"), sign);
		task.execute();
	}

	private void ObjectCommonTask(HttpCallBack<BaseResp> iCall, Context context,
			Map<String, Object> map, String url, String sign) {
		CommonStandardNewTask task = CommonStandardNewTask.newInstance(iCall, context,
				BaseResp.class);
		String productId = ExternalKey.K_PRODUCT_ID;
		String clientType = ExternalKey.K_CLIENT_TYPE;
		context.getResources().getString(R.string.version_name_update);
		task.setObjectParams(url, map, productId, clientType, (String) map.get("mobile"),
				(String) map.get("cityCode"), (String) map.get("orgCode"),
				(String) map.get("longitude"), (String) map.get("latitude"), sign);
		task.execute();
	}

}
