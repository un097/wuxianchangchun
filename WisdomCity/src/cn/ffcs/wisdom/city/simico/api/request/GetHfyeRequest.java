package cn.ffcs.wisdom.city.simico.api.request;

import org.json.JSONObject;

import cn.ffcs.wisdom.city.simico.base.Application;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectMultiRequest;

/**
 * 获取所有栏目
 * 
 * @author Tonlin
 * 
 */
public class GetHfyeRequest extends JsonObjectMultiRequest {

	public GetHfyeRequest(Listener<JSONObject> listener, ErrorListener errorListener) {
		super(Method.GET, String.format(
				"http://ccgd.153.cn:50081/icity/service/pc/queryFlowAndCharge/query?accNbr=%s",
				Application.getCurrentUser()), listener, errorListener);
	}
}
