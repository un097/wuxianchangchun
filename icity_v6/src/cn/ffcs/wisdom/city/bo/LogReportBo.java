package cn.ffcs.wisdom.city.bo;

import android.app.Activity;
import android.content.Context;
import cn.ffcs.wisdom.base.BaseBo;
import cn.ffcs.wisdom.base.CommonTaskJson;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;

/**
 * <p>Title: 日志业务逻辑         </p>
 * <p>Description: 
 * </p>
 * <p>@author: Leo                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-9-2             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class LogReportBo extends BaseBo {
	private Context mContext;

	public LogReportBo(Activity act, HttpCallBack<BaseResp> icall) {
		super(act, icall);
		mContext = act.getApplicationContext();
	}

	/**
	 * 日志记录
	 */
	public void reportLogs(String jsonBody) {
		String productId = mContext.getString(R.string.version_name_update);
		CommonTaskJson task = CommonTaskJson.newInstance(icall, mContext, null);
		task.setParams(Config.UrlConfig.URL_LOG_REPORT, jsonBody, productId);
		task.execute();
	}

	
}
