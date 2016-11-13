package cn.ffcs.wisdom.city.personcenter.bo;

import java.util.Map;

import android.content.Context;
import cn.ffcs.wisdom.base.CommonTask;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.personcenter.entity.Account;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;

/**
 * <p>Title: 注册业务逻辑         </p>
 * <p>Description: 
 * </p>
 * <p>@author: Leo                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-2-27             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class RegisterBo {
	private HttpCallBack<BaseResp> mCall;
	private Context mContext;

	public RegisterBo(HttpCallBack<BaseResp> icall, Context context) {
		this.mCall = icall;
		this.mContext = context;
	}

	/**
	 * 提交注册
	 * @param account
	 */
	public void register(Map<String,String> params) {
		CommonTask task = CommonTask.newInstance(mCall, mContext, Account.class);
		task.setParams(params, Config.UrlConfig.URL_REGISTER_NEW);
		task.execute();
	}
}
