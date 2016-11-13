package cn.ffcs.wisdom.city.simico.api.request;

import org.json.JSONObject;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

public class GetServiceRequest extends BaseRequest {


	public GetServiceRequest(Listener<JSONObject> listener,
			ErrorListener errorListener) {
		super("getmoreservice", listener, errorListener);
	}

}
