package cn.ffcs.wisdom.city.simico.api.request;

import java.util.Map;

import org.json.JSONObject;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

public class GetChannelService extends BaseRequest {

	private int channelId;
	private int pageNo;

	public GetChannelService(int channelId, int pageNo,
			Listener<JSONObject> listener, ErrorListener errorListener) {
		super("getinfolist", listener, errorListener);
		this.channelId = channelId;
		this.pageNo = pageNo;
	}

	@Override
	public Map<String, String> getParams() {
		Map<String, String> params = super.getParams();
		params.put("chnl_id", channelId + "");
		params.put("page_no", pageNo + "");
		return params;
	}
}
