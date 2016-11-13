package cn.ffcs.wisdom.city.simico.api.request;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import cn.ffcs.wisdom.city.simico.base.Application;
import cn.ffcs.wisdom.city.simico.kit.util.DateUtil;
import cn.ffcs.wisdom.city.simico.kit.util.TDevice;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

public class GetApplyResourcesRequest extends CityPostRequest {

	public static final String BASE_RUL = "http://ccgd.153.cn:50081/icity-api-client-busi/service/icity/livelihood/weg/%s";

	public GetApplyResourcesRequest(Listener<JSONObject> listener,
			ErrorListener errorListener) {
		super(String.format(BASE_RUL, "getApplyResources"), listener,
				errorListener);
	}

	@Override
	public Map<String, String> getParams() {
		String timestamp = DateUtil.getNow("yyyy-MM-dd HH:mm:ss");// "2013-11-05 16:08:05";//

		Map<String, String> params = new HashMap<String, String>();

		params.put("product_id", "icity_ver");
		params.put("client_version", TDevice.getVersionCode() + "");
		params.put("client_channel_type", "hiapk");
		params.put("os_type", "android");
		params.put("org_code", Application.getCurrentCity());// 3520
		params.put("base_line", "400");

		params.put("timestamp", timestamp);
		return params;
	}
}
