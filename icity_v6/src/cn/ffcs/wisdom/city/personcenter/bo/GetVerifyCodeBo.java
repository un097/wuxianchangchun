package cn.ffcs.wisdom.city.personcenter.bo;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import cn.ffcs.wisdom.base.CommonTask;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.AppHelper;

/**
 * <p>Title:获取验证码          </p>
 * <p>Description: 
 * </p>
 * <p>@author: Leo                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-2-28             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class GetVerifyCodeBo {
	private HttpCallBack<BaseResp> mCall;
	private Context mContext;

	public GetVerifyCodeBo(HttpCallBack<BaseResp> iCall, Context context) {
		this.mCall = iCall;
		this.mContext = context;
	}

	/**
	 * 请求获取验证码
	 * @param mobile
	 */
	public void VerifyCode(String mobile) {
		Map<String, String> map = new HashMap<String, String>();
		String imsi = AppHelper.getMobileIMSI(mContext);
		map.put("mobile", mobile);
		map.put("imsi", imsi);
		map.put("client_type", mContext.getResources().getString(R.string.version_name_update));
		CommonTask task = CommonTask.newInstance(mCall, mContext, null);
		task.setParams(map, Config.UrlConfig.URL_GET_VERIFYCODE);
		task.execute();
	}
}
