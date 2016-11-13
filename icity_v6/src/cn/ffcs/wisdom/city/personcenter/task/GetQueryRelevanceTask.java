package cn.ffcs.wisdom.city.personcenter.task;

import java.util.List;

import android.content.Context;
import cn.ffcs.wisdom.base.BaseTask;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.entity.QueryRelevanceEntity;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.resp.QueryRelevanceResp;
import cn.ffcs.wisdom.city.sqlite.model.MenuItem;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.http.HttpRequest;
import cn.ffcs.wisdom.tools.JsonUtil;
import cn.ffcs.wisdom.tools.Log;

public class GetQueryRelevanceTask extends BaseTask<String, Void, QueryRelevanceResp> {

	public GetQueryRelevanceTask(Context context, HttpCallBack<QueryRelevanceResp> iCall) {
		super(iCall, context);
	}

	@Override
	protected QueryRelevanceResp doInBackground(String... params) {
		HttpRequest request = new HttpRequest(QueryRelevanceResp.class);
		QueryRelevanceResp resp = new QueryRelevanceResp();
		String mobile = AccountMgr.getInstance().getMobile(mContext);
		String itemId = params[0];
		if (itemId.equals("交通")) {
			List<MenuItem> list = MenuMgr.getInstance().getFirstMenu(mContext);
			for (int i = 0; i < list.size(); i++) {
				MenuItem item = list.get(i);
				if (itemId.equals(item.getMenuName())) {
					itemId = item.getMenuId();
				}
			}
		}
		request.addParameter("item_id", itemId);
		request.addParameter("mobile", mobile);
		String url = Config.UrlConfig.GET_QUERY_RELEVANCE;
		resp = (QueryRelevanceResp) request.executeUrl(url);
		try {
			if (resp.isSuccess()) {
				String result = resp.getHttpResult();
				QueryRelevanceEntity entity = JsonUtil.toObject(result, QueryRelevanceEntity.class);
				resp.setList(entity.getData());
			}
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
			resp.setStatus(BaseResp.ERROR);
		} finally {
			request.release();
		}
		return resp;
	}
}
