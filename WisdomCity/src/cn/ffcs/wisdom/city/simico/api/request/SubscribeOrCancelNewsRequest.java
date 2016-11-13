package cn.ffcs.wisdom.city.simico.api.request;

import java.util.Map;

import org.json.JSONObject;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

public class SubscribeOrCancelNewsRequest extends BaseRequest {

	private int newsId;
	private boolean subscribe;

	public SubscribeOrCancelNewsRequest(int newsId, boolean subscribe,
			Listener<JSONObject> listener, ErrorListener errorListener) {
		super("infocollectionsoperate", listener, errorListener);
		this.newsId = newsId;
		this.subscribe = subscribe;
	}

	@Override
	public Map<String, String> getParams() {
		Map<String, String> params = super.getParams();
		params.put("info_id", "" + newsId);
		params.put("oper_type", subscribe ? "add" : "del");
		params.put("client_type", "icity_ver");
		return params;
	}
}
