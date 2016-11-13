package cn.ffcs.wisdom.city.simico.api.request;

import org.json.JSONObject;

import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.simico.base.Application;
import cn.ffcs.wisdom.tools.AppHelper;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectMultiRequest;

/**
 * 取一级栏目接口数据
 * @author Tonlin
 *
 */
public class GetRecommendServiceRequest extends JsonObjectMultiRequest {

	public GetRecommendServiceRequest(Listener<JSONObject> listener, ErrorListener errorListener) {
		super(
				Method.GET,
				String.format(
						Config.GET_SERVER_ROOT_URL()
								+ "icity/service/menu/levelonemenu/query?cityCode=%s&baseLine=%s&menuVer=%s&switchSystemType=%s&osType=%s&clientverMapping=%s",
						Application.getCurrentCity(),//cityCode
						400, //baseline
						MenuMgr.getInstance().getMenuVer(Application.context(),
								Application.getCurrentCity()),//menuVer
						1,//switchSystemType
						1,//osType
						AppHelper.getVersionCode(Application.context())//clientverMapping
				), listener, errorListener);
	}

}
