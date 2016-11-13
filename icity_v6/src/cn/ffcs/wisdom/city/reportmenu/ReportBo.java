package cn.ffcs.wisdom.city.reportmenu;


import android.content.Context;
import cn.ffcs.wisdom.base.CommonTaskJson;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.sqlite.service.ReportMenuService;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.Log;

/**
 * <p>Title: 上报信息业务处理类       </p>
 * <p>Description: 
 *   启动上报异步线程，进行信息上报，包括：收藏和访问记录
 * </p>
 * <p>@author: zhangwsh                </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-3-6             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class ReportBo implements HttpCallBack<BaseResp> {

	private Context mContext;

	public ReportBo(Context context) {
		mContext = context;
	}

	/**
	 * 上报栏目访问
	 * @param actionType
	 * @param itemId
	 */
	public void sendReport(String json) {
		String productId = mContext.getString(R.string.version_name_update);
		CommonTaskJson task = CommonTaskJson.newInstance(this, mContext, BaseResp.class);
		task.setParams(Config.UrlConfig.URL_UPLOAD_RECORD_BATCH, json, productId);
		task.execute();
	}

	@Override
	public void call(BaseResp response) {
		if (response.isSuccess()) {
			Log.d("上报信息成功");
			ReportMenuService.getInstance(mContext).deleteAll();
		}
	}

	@Override
	public void progress(Object... obj) {
	}

	@Override
	public void onNetWorkError() {
	}
}
