package cn.ffcs.wisdom.city.simico.api.request;

import java.util.Map;

import org.json.JSONObject;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

public class GetNewsDetailRequest extends BaseRequest {

	private int id;
	
	public GetNewsDetailRequest(int id, Listener<JSONObject> listener,
			ErrorListener errorListener) {
		super("getinfodetail", listener, errorListener);
		this.id = id;
	}

	@Override
	public Map<String, String> getParams() {
		Map<String,String> params = super.getParams();
		params.put("info_id", id+"");
		return params;
	}
}
