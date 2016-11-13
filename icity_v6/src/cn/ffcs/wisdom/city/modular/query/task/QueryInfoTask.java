package cn.ffcs.wisdom.city.modular.query.task;

import cn.ffcs.wisdom.base.BaseTask;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.modular.query.resp.QueryInfoResp;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.http.HttpRequest;
import cn.ffcs.wisdom.tools.JsonUtil;
import cn.ffcs.wisdom.tools.Log;

/**
 * <p>Title:  查询类数据获取</p>
 * <p>Description: </p>
 * <p>@author: caijj                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2012-10-19             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class QueryInfoTask extends BaseTask<Void, Void, QueryInfoResp> {

	private String cityCode;

	public QueryInfoTask(HttpCallBack<QueryInfoResp> iCall, String cityCode) {
		super(iCall);

		this.cityCode = cityCode;
	}

	@Override
	protected QueryInfoResp doInBackground(Void... params) {

		String url = Config.UrlConfig.URL_QUERY_INFO;
		HttpRequest request = new HttpRequest(HttpRequest.HTTP_METHOD_GET, QueryInfoResp.class);
		QueryInfoResp resp = new QueryInfoResp();
		request.addParameter("cityCode", cityCode);
		
		resp = (QueryInfoResp) request.execute(url);

		try {
			if (resp.isSuccess()) {
				String result = resp.getHttpResult();
				resp = JsonUtil.toObject(result, QueryInfoResp.class);
			}
		} catch (Exception e) {
			// e.printStackTrace();
			Log.e(e.getMessage(), e);
			resp.setStatus(BaseResp.ERROR);
		} finally {
			request.release();
		}
		return resp;
	}

}
