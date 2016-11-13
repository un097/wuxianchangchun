package cn.ffcs.wisdom.city.simico.api.request;

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.ffcs.wisdom.city.R;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.simico.api.model.EventLog;
import cn.ffcs.wisdom.city.simico.base.Application;
import cn.ffcs.wisdom.city.simico.kit.util.TDevice;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.ManifestUtil;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

public class UploadEventLogRequest extends CityPostRequest {

	private ArrayList<EventLog> eventLogs;

	public UploadEventLogRequest(ArrayList<EventLog> events, Listener<JSONObject> listener,
			ErrorListener errorListener) {
		super(Config.GET_SERVER_ROOT_URL() + "icity-api-jms/service/icity/v7/user-behavior/click",
				listener, errorListener);
		eventLogs = events;
	}

	@Override
	public JSONArray getJSONArray() {
		if (eventLogs == null)
			return null;
		try {
			JSONArray array = new JSONArray();
			for (EventLog event : eventLogs) {
				JSONObject json = event.toJSONObject();
				// --------------------------- //
				json.put("product_id", Application.context().getString(R.string.version_name_update));
				json.put("client_type", Application.context().getString(R.string.version_name_update));
				json.put("client_version", TDevice.getVersionCode() + "");
				json.put("client_channel_type", ManifestUtil.readUMChannel(Application.context()));
				json.put("os_type", AppHelper.getOSTypeNew());
				json.put("imei", TDevice.getIMEI());
				json.put("imsi", TDevice.getIMSI());
				json.put("mobile", Application.getCurrentUser());
				json.put("city_code", MenuMgr.getInstance().getCityCode(Application.context()));
				// --------------------------- //

				array.put(json);
			}
			return array;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return super.getJSONArray();
	}

	@Override
	public Map<String, String> getParams() {
		return null;
	}
}
