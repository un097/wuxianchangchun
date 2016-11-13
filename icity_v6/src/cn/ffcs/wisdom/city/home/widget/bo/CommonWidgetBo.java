package cn.ffcs.wisdom.city.home.widget.bo;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import cn.ffcs.wisdom.base.CommonNewTask;
import cn.ffcs.wisdom.base.CommonTask;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.home.widget.entity.CommonWidgetEntity;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.AppHelper;

/**
 * <p>Title:向平台发起请求，得到控件类型          </p>
 * <p>Description: 
 * </p>
 * <p>@author: Leo                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-4-10             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class CommonWidgetBo {
	private String cityCode;// 城市编号
	private String osType;// 操作系统类型
	private String verType;// 版本类型

	private Context mContext;
	private HttpCallBack<BaseResp> mCall;

	public CommonWidgetBo(HttpCallBack<BaseResp> icall, Context context) {
		this.mCall = icall;
		this.mContext = context;
	}

	/**
	 * 公共业务逻辑请求
	 * @param menuId
	 * @param widgetType
	 * 控件类型(0文字新闻、1图片2交通违章3旅游视频)
	 */
	public void queryWidget(String menuId, String widgetType) {
		cityCode = MenuMgr.getInstance().getCityCode(mContext);
		osType = AppHelper.getOSType();
		verType = mContext.getResources().getString(R.string.version_name_update);
		Map<String, String> params = new HashMap<String, String>();
		params.put("cityCode", cityCode);
		params.put("osType", osType);
		params.put("verType", verType);
		params.put("widgetType", widgetType);
		params.put("menuId", menuId);
		CommonTask task = CommonTask.newInstance(mCall, mContext, CommonWidgetEntity.class);
		task.setParams(params, Config.UrlConfig.URL_WIDGET_QUERY);
		task.execute();
	}

	/**
	 * 上报widget点击
	 */
	public void reportWidget(String menuId,String widgetType) {
		String cityCode=MenuMgr.getInstance().getCityCode(mContext);
		String osType=AppHelper.getOSTypeNew();
		String verType=mContext.getResources().getString(R.string.version_name_update);
		String imsi=AppHelper.getMobileIMSI(mContext);
		String imei=AppHelper.getIMEI(mContext);
		String mobile=AccountMgr.getInstance().getMobile(mContext);
		String clientVerNum=String.valueOf(AppHelper.getVersionCode(mContext));
		Map<String, String> params = new HashMap<String, String>();
		params.put("cityCode", cityCode);
		params.put("osType", osType);
		params.put("imsi", imsi);
		params.put("imei", imei);
		params.put("mobile", mobile);
		params.put("clientVerNum", clientVerNum);
		params.put("menuId", menuId);
		params.put("widgetType", widgetType);
		params.put("verType", verType);
		CommonNewTask task=CommonNewTask.newInstance(null, mContext, BaseResp.class);
		task.setParams(params, Config.UrlConfig.URL_WIDGET_LOG);
		task.execute();
	}
}
