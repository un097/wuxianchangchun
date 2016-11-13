package cn.ffcs.wisdom.city.simico.api.request;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import cn.ffcs.wisdom.base.CommonStandardTask;
import cn.ffcs.wisdom.city.R;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.simico.base.Application;
import cn.ffcs.wisdom.city.simico.kit.util.DateUtil;
import cn.ffcs.wisdom.city.simico.kit.util.TDevice;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.ManifestUtil;

import com.android.volley.AuthFailureError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.ffcs.icity.api.menuservice.utils.CrytoUtils;

public class BaseRequest extends CityPostRequest {

	public static final String serverRoot = Config.GET_SERVER_ROOT_URL();

	public static final String BASE_RUL = serverRoot + "icity-api-client-v7/icity/service/v7/%s";

	public static final String IMG_URL = Config.GET_IMAGE_ROOT_URL();

	//"http://prodics-pic.153.cn:8908/";
	//http://pic.153.cn:8081/ 正式环境

	public BaseRequest(String urlPart, Listener<JSONObject> listener, ErrorListener errorListener) {
		super(String.format(BASE_RUL, urlPart), listener, errorListener);
	}

	/**
	 * Client(产品标示;系统标示;设备标示;分辨率[;其他扩增信息]) 产品标示=主产品标示[-子产品标示]/版本号 系统标示=系统标示/版本号
	 * 设备标示=iphone4|iphone5|xiaomi|... 分辨率=宽*高
	 */
	public void setAgent() {
		try {
			String agent = CommonStandardTask.getUserAgent(Application.context(), Application.context().getString(R.string.version_name_update));
			getHeaders().put("User-Agent", agent);
		} catch (AuthFailureError e) {
			e.printStackTrace();
		}
	}

	@Override
	public Map<String, String> getParams() {
		Context context = Application.context();
		String telephone = Application.getCurrentUser();
		String timestamp = DateUtil.getNow("yyyy-MM-dd HH:mm:ss");//"2013-11-05 16:08:05";//

		Map<String, String> params = new HashMap<String, String>();
		params.put("product_id", context.getString(R.string.version_name_update));
		params.put("client_version", TDevice.getVersionCode() + "");
		params.put("client_channel_type", ManifestUtil.readMetaData(context, "UMENG_CHANNEL"));
		params.put("os_type", AppHelper.getOSTypeNew());
		params.put("org_code", MenuMgr.getInstance().getCityCode(Application.context()));// 3520
		params.put("base_line", "400");

		params.put("timestamp", timestamp);
		params.put("imsi", TDevice.getIMSI());
		params.put("imei", TDevice.getIMEI());
		params.put("mobile", telephone);

		try {
			String md5 = CrytoUtils.md5(
					telephone + "$" + TDevice.getIMSI() + "$" + TDevice.getIMEI(), CrytoUtils.MD5KEY,
					timestamp);
			String sign = CrytoUtils.encode(CrytoUtils.DESKEY, timestamp, md5);
			params.put("sign", sign);//
		} catch (Exception e) {
			e.printStackTrace();
		}
		return params;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map getParamsV2() {
		return this.getParams();
	}
}
