package cn.ffcs.wisdom.city.simico.api.request;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import cn.ffcs.config.BaseConfig;
import cn.ffcs.wisdom.city.simico.base.Application;
import cn.ffcs.wisdom.city.simico.kit.util.DateUtil;
import cn.ffcs.wisdom.city.simico.kit.util.TDevice;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.ffcs.icity.api.menuservice.utils.CrytoUtils;

/**
 * 获取应用汇下载列表
 * 
 */
public class GetAppofexchangeList extends CityPostRequest {

	public static final String BASE_RUL = BaseConfig.GET_SERVER_ROOT_URL()
			+ "icity-api-client-other/icity/service/appofexchange/query";

	public GetAppofexchangeList(Listener<JSONObject> listener,
			ErrorListener errorListener) {
		super(BASE_RUL, listener, errorListener);
	}

	@Override
	public Map<String, String> getParams() {
		String telephone = Application.getCurrentUser();
		String timestamp = DateUtil.getNow("yyyy-MM-dd HH:mm:ss");// "2013-11-05 16:08:05";//
		String md5Key = "75BD2E98AC17564B2DB7C74B064F5084C6557FDDF3E4C286";
		String desKey = "b5eefe0437d945b98e82f46fbff8d3552c2ff6f7f8acd8de";

		Map<String, String> params = new HashMap<String, String>();

		params.put("product_id", "icity");
		params.put("client_type", "icity_ver");
		params.put("client_version", TDevice.getVersionCode() + "");
		params.put("client_channel_type", "hiapk");
		params.put("os_type", "android");
		params.put("org_code", Application.getCurrentCity());// 3520
		params.put("base_line", "400");

		params.put("timestamp", timestamp);
		params.put("imsi", TDevice.getIMSI());
		params.put("imei", TDevice.getIMEI());
		params.put("mobile", telephone);

		try {
			String md5 = CrytoUtils.md5(telephone + "$" + TDevice.getIMSI()
					+ "$" + TDevice.getIMEI(), md5Key, timestamp);
			String sign = CrytoUtils.encode(desKey, timestamp, md5);
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

	@Override
	public String getBodyContentType() {
		return "application/json";
	}
}
