package cn.ffcs.external.trafficbroadcast.bo;

import java.util.Map;
import cn.ffcs.config.ExternalKey;
import cn.ffcs.external.trafficbroadcast.entity.Traffic_AllList_Entity;
import cn.ffcs.wisdom.base.CommonStandardNewTask;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import android.content.Context;

/**
 * 全部路况列表业务逻辑类
 * 
 * @author daizhq
 * 
 * @date 2014.12.05
 * */
public class Traffic_AllList_Bo {
	
	private Context context;

	public Traffic_AllList_Bo(Context context) {
		this.context = context;
	}

	public void startRequestTask(HttpCallBack<BaseResp> callBack, Context context,
			Map<String, String> map, String url) {
		CommonStandardNewTask task = CommonStandardNewTask.newInstance(callBack,
				context, Traffic_AllList_Entity.class);
		String productId = "changchunTV";
		String clientType = ExternalKey.K_CLIENT_TYPE;
		task.setParams(url, map, productId, clientType, map.get("mobile"),
				map.get("city_code"), map.get("org_code"), map.get("longitude"),
				map.get("latitude"), map.get("sign"));
		task.execute();
	}

}
