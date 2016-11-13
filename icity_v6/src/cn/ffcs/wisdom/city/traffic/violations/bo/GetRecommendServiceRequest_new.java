package cn.ffcs.wisdom.city.traffic.violations.bo;

import org.json.JSONObject;

import android.content.Context;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.tools.AppHelper;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectMultiRequest;

/**
 * 取一级栏目接口数据
 * @author Tonlin
 *
 */
public class GetRecommendServiceRequest_new extends JsonObjectMultiRequest {

	public GetRecommendServiceRequest_new(Listener<JSONObject> listener, ErrorListener errorListener,Context context) {
		super(
				Method.GET,
				String.format(
						Config.GET_SERVER_ROOT_URL()
								+ "icity/service/menu/levelonemenu/query?cityCode=%s&baseLine=%s&menuVer=%s&switchSystemType=%s&osType=%s&clientverMapping=%s",
								2201,//cityCode
						400, //baseline
						0,//menuVer
						1,//switchSystemType
						1,//osType
						AppHelper.getVersionCode(context)//clientverMapping
				), listener, errorListener);
	}

}
