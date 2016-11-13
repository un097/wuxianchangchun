package cn.ffcs.wisdom.city.simico.api.request;

import org.json.JSONObject;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

/**
 * 获取全部频道信息
 * @author Sim
 *
 */
public class GetChannelList extends BaseRequest {

	public GetChannelList(Listener<JSONObject> listener,
			ErrorListener errorListener) {
		super("getchannellist", listener, errorListener);
	}
}
