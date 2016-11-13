package cn.ffcs.external.trafficbroadcast.bo;

import java.util.Map;
import android.content.Context;
import cn.ffcs.config.ExternalKey;
import cn.ffcs.external.trafficbroadcast.entity.Traffic_SensitiveWord_Entity;
import cn.ffcs.wisdom.base.CommonStandardNewTask;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;

/**
 * 敏感词业务逻辑类
 * 
 * @author daizhq
 * 
 * @date 2014.12.29
 * */
public class Traffic_SensitiveWord_Bo {
	
	private Context context;

	public Traffic_SensitiveWord_Bo(Context context) {
		this.context = context;
	}

	public void startRequestTask(HttpCallBack<BaseResp> callBack,
			Context context, Map<String, Object> map, String url) {
		CommonStandardNewTask task = CommonStandardNewTask.newInstance(
				callBack, context, Traffic_SensitiveWord_Entity.class);
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
