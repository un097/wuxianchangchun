package cn.ffcs.wisdom.city.personcenter.bo;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import cn.ffcs.wisdom.base.CommonTask;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.AppHelper;

/**
 * <p>Title:忘记密码          </p>
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
public class ForgetPasswordBo {

	private Activity mActivity;
	private HttpCallBack<BaseResp> mCall;

	public ForgetPasswordBo(HttpCallBack<BaseResp> icall, Activity activity) {
		this.mActivity = activity;
		this.mCall = icall;
	}

	/**
	 * 发送请求，获取密码
	 * @param mobile
	 * @param userName
	 */
	public void sendPwd(String mobile, String userName) {
		String imsi = AppHelper.getMobileIMSI(mActivity);
		CommonTask task = CommonTask.newInstance(mCall, mActivity, null);
		Map<String, String> map = new HashMap<String, String>();
		map.put("mobile", mobile);
		map.put("userName", userName);
		map.put("imsi", imsi);
		map.put("client_type", mActivity.getResources().getString(R.string.version_name_update));
		task.setParams(map, Config.UrlConfig.URL_FORGET_PSW);
		task.execute();
	}
}
