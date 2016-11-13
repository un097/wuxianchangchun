package cn.ffcs.wisdom.city.myapp.bo;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import cn.ffcs.wisdom.base.CommonNewTask;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;

/**
 * <p>Title: 删除应用         </p>
 * <p>Description: 
 * </p>
 * <p>@author: Leo                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-3-12             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class DelAppBo {
	private Context mContext;
	private HttpCallBack<BaseResp> iCall;

	public DelAppBo(HttpCallBack<BaseResp> call, Context context) {
		this.iCall = call;
		this.mContext = context;
	}

	/**
	 * 删除应用
	 * @param appId，Json字符串
	 */
	public void delMyApp(String mobile, String menuId) {
		CommonNewTask task = CommonNewTask.newInstance(iCall, mContext, null);
		Map<String, String> params = new HashMap<String, String>();
		String cityCode = MenuMgr.getInstance().getCityCode(mContext);
		params.put("mobile", mobile);
		params.put("cityCode", cityCode);
		params.put("menuIdList", menuId);
		task.setParams(params, Config.UrlConfig.URL_MYAPP_DEL);
		task.execute();
	}
}
