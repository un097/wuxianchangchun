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
 * 获取所有栏目 
 * @author Tonlin
 *
 */
public class GetAllServiceRequest extends JsonObjectMultiRequest {

	public GetAllServiceRequest(Listener<JSONObject> listener, ErrorListener errorListener) {
		super(
				Method.GET,
				String.format(
						Config.GET_SERVER_ROOT_URL()
								+ "icity/service/menu/menuquery/query?cityCode=%s&baseLine=%s&menuVer=%s&switchSystemType=%s&osType=%s&clientverMapping=%s",
						Application.getCurrentCity(),//cityCode
						400,//baseLine
						MenuMgr.getInstance().getMenuVer(Application.context(),
								Application.getCurrentCity()),//menuVer
						1,//siwtchSystemType
						1,//osType
						AppHelper.getVersionCode(Application.context())//clientverMapping
				), listener, errorListener);
	}
}
