package cn.ffcs.wisdom.city.bo;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import cn.ffcs.wisdom.base.CommonTask;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.Log;

/**
 * <p>Title: 流量统计逻辑类        </p>
 * <p>Description: 
 *  统计爱城市流量数
 * </p>
 * <p>@author: Eric.wsd                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2012-7-4             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class TrafficStatsBo implements HttpCallBack<BaseResp> {

	private Context mContext;

	public TrafficStatsBo(Context context) {
		this.mContext = context;
	}

	/**
	 * 流量统计
	 * @param trafficType
	 * @param trafficByte
	 */
	public void sendTrafficStatsBo(String trafficType, String trafficByte) {
		CommonTask task = CommonTask.newInstance(this, mContext, null);
		Map<String, String> params = new HashMap<String, String>();
		String imsi = AppHelper.getSerialCode2(mContext);
		params.put("imsi", imsi);
		params.put("traffic_type", trafficType);// 流量类型
		params.put("traffic_byte", trafficByte); // 流量字节
		task.setParams(params, Config.UrlConfig.URL_GLOBAL_FLOW);
		task.execute();
	}

	@Override
	public void call(BaseResp resp) {
		if (resp.isSuccess()) {
			Log.d("流量记录上报成功!");
		}
	}

	@Override
	public void progress(Object... obj) {

	}

	@Override
	public void onNetWorkError() {

	}

}
