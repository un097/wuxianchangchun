package cn.ffcs.wisdom.city.simico.api.request;

import java.util.Map;

import org.json.JSONObject;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

public class GetCityHotServiceRequest extends BaseRequest {
	private int mPageNumber;

	public GetCityHotServiceRequest(int pageNo,
			Listener<JSONObject> listener, ErrorListener errorListener) {
		super("getCityHotSearch", listener, errorListener);
		mPageNumber = pageNo;
	}

	@Override
	public Map<String, String> getParams() {
		Map<String, String> params = super.getParams();
		params.put("page_no", String.valueOf(mPageNumber));
		return params;
	}
}
