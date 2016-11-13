package cn.ffcs.wisdom.city.setting.about;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import cn.ffcs.wisdom.base.CommonTask;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.utils.MenuUtil;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.SharedPreferencesUtil;
/**
 * 
 * <p>Title: 检查更新处理类         </p>
 * <p>Description: 
 * 检查更新
 * </p>
 * <p>@author: xzw                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-3-15             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class VersionCheckBo {
	private HttpCallBack<BaseResp> mCall;
	private Context mContext;

	public VersionCheckBo(Context mContext, HttpCallBack<BaseResp> call) {
		this.mContext = mContext;
		this.mCall = call;
	}

	public void check() {
		String latitude = SharedPreferencesUtil.getValue(mContext, Key.K_LOCATION_LAT);
		String longitude = SharedPreferencesUtil.getValue(mContext, Key.K_LOCATION_LNG);
		String imsi = AppHelper.getMobileIMSI(mContext);
		String mobile = AccountMgr.getInstance().getMobile(mContext);
		String versionCode = AppHelper.getVersionCode(mContext) + "";
		String clientType = mContext.getString(R.string.version_name_update);
		String cityCode = MenuUtil.getCityCode(mContext);

		Map<String, String> params = new HashMap<String, String>(1);

		params.put("mobile", mobile);
		params.put("imsi", imsi);
		params.put("client_version", versionCode);
		params.put("client_type", clientType);
		params.put("city_code", cityCode);
		params.put("lat", latitude);
		params.put("lng", longitude);
		String url = Config.UrlConfig.URL_UPDATE;

		CommonTask task = CommonTask.newInstance(mCall, mContext, VersionResp.class);
		task.setParams(params, url);
		task.execute();
	}
}
