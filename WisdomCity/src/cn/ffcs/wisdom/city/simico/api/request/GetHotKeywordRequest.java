package cn.ffcs.wisdom.city.simico.api.request;

import org.json.JSONObject;

import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.simico.base.Application;
import cn.ffcs.wisdom.city.simico.kit.util.TDevice;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectMultiRequest;

public class GetHotKeywordRequest extends JsonObjectMultiRequest {

	public GetHotKeywordRequest(Listener<JSONObject> listener, ErrorListener errorListener) {
		super(Method.GET, String.format(Config.GET_SERVER_ROOT_URL()
				+ "exter.shtml?baseLine=%s&serviceType=%s&city_code=%s&imsi=%s", 400,//baseline
				1028,//serviceType
				Application.getCurrentCity(),//cityCode
				TDevice.getIMSI()//imsi
				), listener, errorListener);
	}
}
