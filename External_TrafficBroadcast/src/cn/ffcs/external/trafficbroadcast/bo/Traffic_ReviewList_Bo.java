package cn.ffcs.external.trafficbroadcast.bo;

import java.util.HashMap;
import java.util.Map;

import cn.ffcs.config.ExternalKey;
import cn.ffcs.external.trafficbroadcast.entity.Traffic_AttentionOper_Entity;
import cn.ffcs.external.trafficbroadcast.entity.Traffic_ReviewList_Entity;
import cn.ffcs.wisdom.base.CommonStandardNewTask;
import cn.ffcs.wisdom.base.CommonTask;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import android.content.Context;

/**
 * 评论列表业务逻辑类
 * 
 * @author daizhq
 * 
 * @date 2014.12.22
 * */
public class Traffic_ReviewList_Bo {
	
	private Context context;

	public Traffic_ReviewList_Bo(Context context) {
		this.context = context;
	}

	public void startRequestTask(HttpCallBack<BaseResp> callBack,
			Context context, Map<String, Object> map, String url) {
		CommonStandardNewTask task = CommonStandardNewTask.newInstance(
				callBack, context, Traffic_ReviewList_Entity.class);
		String productId = "changchunTV";
		String clientType = ExternalKey.K_CLIENT_TYPE;
		task.setObjectParams(url, map, productId, clientType, map.get("mobile")
				.toString(), map.get("city_code").toString(),
				map.get("org_code").toString(),
				map.get("longitude").toString(),
				map.get("latitude").toString(), map.get("sign").toString());
		task.execute();
	}

}
