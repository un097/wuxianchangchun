package cn.ffcs.wisdom.city.simico.api.request;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import cn.ffcs.wisdom.base.CommonStandardTask;
import cn.ffcs.wisdom.city.R;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.simico.api.model.ChannelEntityResp;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;

public class GetChannelListRequest {
	private Context mContext;

	public GetChannelListRequest(Context context) {
		this.mContext = context;
	}

	/**
	 * 获取渠道列表
	 * @param url
	 * @param map
	 * @param productId
	 * @param phone
	 * @param orgCode
	 */
	public void getChannelList(HttpCallBack<BaseResp> iCall) {
		CommonStandardTask task = CommonStandardTask.newInstance(iCall, mContext,
				ChannelEntityResp.class);
		String url = Config.GET_SERVER_ROOT_URL()
				+ "icity-api-client-v7/icity/service/v7/getchannellist";
		Map<String, String> map = new HashMap<String, String>();
		String productId = mContext.getString(R.string.version_name_update);
		String phone = AccountMgr.getInstance().getMobile(mContext);
		String orgCode = MenuMgr.getInstance().getCityCode(mContext);
		task.setParams(url, map, productId, phone, orgCode);
		task.execute();
	}
}
