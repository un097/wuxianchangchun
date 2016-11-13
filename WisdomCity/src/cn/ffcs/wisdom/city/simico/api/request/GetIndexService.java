package cn.ffcs.wisdom.city.simico.api.request;

import java.util.Map;

import org.json.JSONObject;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

public class GetIndexService extends BaseRequest {

	public GetIndexService(Listener<JSONObject> listener,
			ErrorListener errorListener) {
		super("getindexservice", listener, errorListener);

	}

	/**
	 *  product_id
		client_version
		client_channel_type
		os_type
		org_code
		base_line
		timestamp
		imsi
		imei
		mobile
		longitude
		latitude
	 */
	@Override
	public Map<String, String> getParams() {
		Map<String, String> params = super.getParams();
//		params.put("product_id", "");
//		params.put("client_version", TDevice.getVersionName());
//		params.put("client_channel_type", "");
//		params.put("os_type", "android");
//		params.put("org_code", "");
//		params.put("timestamp", DateUtil.getNow("yyyy-MM-dd HH:mm:ss"));
//		params.put("imsi", "");
//		params.put("imei", "");
//		params.put("mobile", "");
//		params.put("base_line", "");
		return params;
	}
}
