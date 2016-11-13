package cn.ffcs.wisdom.city.simico.api.request;

import org.json.JSONObject;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

/**
 * 获取我订阅的服务
 * @author Sim
 *
 */
public class GetSubscribeServiceRequest extends BaseRequest {

	public GetSubscribeServiceRequest(Listener<JSONObject> listener,
			ErrorListener errorListener) {
		super("getsubsservice", listener, errorListener);
	}
}
