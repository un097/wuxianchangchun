package cn.ffcs.wisdom.city.simico.api.request;

import java.util.Map;

import org.json.JSONObject;

import android.text.TextUtils;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

public class GetIndexNews extends BaseRequest {

	private int mPage;
	private String mFirstVisitTime;
	private int mChannelId;

	public GetIndexNews(int channelId, int pageNumber,String firstVisitTime,
			Listener<JSONObject> listener, ErrorListener errorListener) {
		super("getinfolist", listener, errorListener);
		mPage = pageNumber;
		mFirstVisitTime = firstVisitTime;
		mChannelId = channelId;
	}

	/**
	 * product_id client_version client_channel_type os_type org_code base_line
	 * timestamp imsi imei mobile longitude latitude
	 */
	@Override
	public Map<String, String> getParams() {
		Map<String, String> params = super.getParams();
		params.put("page_no", mPage + "");
		params.put("version_type", "all");
		params.put("chnl_id", String.valueOf(mChannelId));
		if (!TextUtils.isEmpty(mFirstVisitTime)) {
			params.put("first_visit_time", mFirstVisitTime);
		}
		return params;
	}
}
