package cn.ffcs.wisdom.city.simico.api.request;

import java.util.Map;

import org.json.JSONObject;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

public class GetCollectionNews extends BaseRequest {

	private int mPage;

	public GetCollectionNews(int pageNumber, Listener<JSONObject> listener,
			ErrorListener errorListener) {
		super("getinfocollectionslist", listener, errorListener);
		mPage = pageNumber;
	}

	/**
	 * product_id client_version client_channel_type os_type org_code base_line
	 * timestamp imsi imei mobile longitude latitude
	 */
	@Override
	public Map<String, String> getParams() {
		Map<String, String> params = super.getParams();
		params.put("page_no", mPage + "");
		params.put("page_size", "100");
		params.put("client_type", "icity_ver");
		return params;
	}
}
