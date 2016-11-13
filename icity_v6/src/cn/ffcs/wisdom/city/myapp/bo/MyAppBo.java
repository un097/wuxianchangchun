package cn.ffcs.wisdom.city.myapp.bo;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import cn.ffcs.wisdom.base.CommonNewTask;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.myapp.entity.MyAppListEntity;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;

/**
 * <p>Title:获取我的应用列表          </p>
 * <p>Description: 
 * </p>
 * <p>@author: Leo                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-3-7             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class MyAppBo {
	private HttpCallBack<BaseResp> iCall;
	private Context mContext;
	
	public MyAppBo(HttpCallBack<BaseResp> call,Context context) {
		this.iCall = call;
		this.mContext = context;
	}

	/**
	 * 根据手机号码，获取我的应用
	 * @param mobile
	 */
	public void acquireMyApp(String mobile) {
		CommonNewTask task = CommonNewTask.newInstance(iCall, mContext, MyAppListEntity.class);
		String citycode = MenuMgr.getInstance().getCityCode(mContext);
		Map<String, String> params = new HashMap<String, String>();
		params.put("mobile", mobile);
		params.put("cityCode", citycode);
		task.setParams(params, Config.UrlConfig.URL_MYAPP_QUERY);
		task.execute();
	}

	/**
	 * 添加应用
	 * @param mobile
	 * @param cityCode
	 * @param menuId，Json字符串
	 */
	public void addMyApp(String mobile, String menuId) {
		CommonNewTask task = CommonNewTask.newInstance(iCall, mContext, null);
		Map<String, String> params = new HashMap<String, String>();
		String cityCode = MenuMgr.getInstance().getCityCode(mContext);
		params.put("mobile", mobile);
		params.put("cityCode", cityCode);
		params.put("menuIdList", menuId);
		task.setParams(params, Config.UrlConfig.URL_MYAPP_ADD);
		task.execute();
	}
}
