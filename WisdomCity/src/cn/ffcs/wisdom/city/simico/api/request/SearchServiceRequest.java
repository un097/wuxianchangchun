package cn.ffcs.wisdom.city.simico.api.request;

import java.util.Map;

import org.json.JSONObject;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

/**
 * 检索服务
 * @author Sim
 *
 */
public class SearchServiceRequest extends BaseRequest {
	
	private String keyword;
	public SearchServiceRequest(String keyword, Listener<JSONObject> listener,
			ErrorListener errorListener) {
		super("queryApp", listener, errorListener);
		this.keyword = keyword;
	}

	@Override
	public Map<String, String> getParams() {
		Map<String, String> params = super.getParams();
		params.put("key_word", keyword);
		return params;
	}
}
