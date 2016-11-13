package cn.ffcs.wisdom.city.simico.api.request;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.ffcs.wisdom.city.simico.kit.log.TLog;
import cn.ffcs.wisdom.city.sqlite.model.MenuItem;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

/**
 * 更新订阅服务排序
 * @author Sim
 *
 */
public class UpdateServiceOrderRequest extends BaseRequest {

	private JSONArray array;
	
	public UpdateServiceOrderRequest(List<MenuItem> menus,
			Listener<JSONObject> listener, ErrorListener errorListener) {
		super("updatesubsservice", listener, errorListener);
		array = new JSONArray();
		for(MenuItem item:menus) {
			JSONObject obj = new JSONObject();
			try {
				obj.put("menu_id", item.getMenuId());
				obj.put("order_no", item.getMenuOrder());
				array.put(obj);
			} catch (JSONException e) {
				TLog.error(e.getMessage());
			}
		}
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map getParamsV2() {
		Map params = super.getParamsV2();
		params.put("item_list", array);
		return params;
	}
}
