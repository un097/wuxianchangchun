package cn.ffcs.wisdom.city.simico.api.request;

import java.util.Iterator;
import java.util.Map;

import org.json.JSONObject;

import cn.ffcs.wisdom.city.simico.kit.log.TLog;
import cn.ffcs.wisdom.city.sqlite.model.MenuItem;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

public class SubscribeOrCancelServiceRequest extends BaseRequest {

	private MenuItem menu;
	private boolean subscribe;

	public SubscribeOrCancelServiceRequest(MenuItem item, boolean subscribe,
			Listener<JSONObject> listener, ErrorListener errorListener) {
		super("subsorcancelservice", listener, errorListener);
		this.menu = item;
		this.subscribe = subscribe;
	}

	@Override
	public Map<String, String> getParams() {
		Map<String, String> params = super.getParams();
		params.put("menu_id", menu.getMenuId());
		params.put("oper_type", subscribe ? "add" : "del");
		
		Iterator<String> ks = params.keySet().iterator();
		while(ks.hasNext()){
			String key = ks.next();
			TLog.log("请求参数:"+key+" value="+ params.get(key));
		}
		return params;
	}

}
