package cn.ffcs.wisdom.city.modular.query;

import java.util.List;

import android.content.Context;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.modular.query.entity.QueryInfo;
import cn.ffcs.wisdom.city.modular.query.resp.QueryInfoResp;
import cn.ffcs.wisdom.city.modular.query.task.QueryInfoTask;
import cn.ffcs.wisdom.http.HttpCallBack;

/**
 * <p>Title: 查询类业务逻辑层 </p>
 * <p>Description: </p>
 * <p>@author: caijj                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2012-10-23             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class QueryInfoBo {

	private Context mActivity;
	private String cityCode;

	public QueryInfoBo(Context activity) {
		this.mActivity = activity;
	}

	public void request() {
		cityCode = MenuMgr.getInstance().getCityCode(mActivity);
		QueryInfoTask task = new QueryInfoTask(new RequestQueryInfoCallBack(), cityCode);
		task.execute();
	}

	class RequestQueryInfoCallBack implements HttpCallBack<QueryInfoResp> {

		@Override
		public void call(QueryInfoResp response) {
			if (response.isSuccess()) {
				List<QueryInfo> list = response.getHome();
				if (list != null && list.size() >= 0) {
					QueryInfoDataMgr.getInstance().refresh(cityCode, list);
				}
			}
		}

		@Override
		public void progress(Object... obj) {
		}

		@Override
		public void onNetWorkError() {
		}
	}
}
