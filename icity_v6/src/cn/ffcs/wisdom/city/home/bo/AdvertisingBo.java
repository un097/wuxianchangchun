package cn.ffcs.wisdom.city.home.bo;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import cn.ffcs.wisdom.base.CommonTask;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.home.entity.AdvertisingEntity;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.StringUtil;

/**
 * <p>Title:  首页广告BO                </p>
 * <p>Description:                     </p>
 * <p>@author: zhangws                 </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-5-2            </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class AdvertisingBo {

	private CommonTask advertTask;
	public static AdvertisingBo bo;
	AdvertisingEntity adEntity;

	private AdvertisingBo() {
	}

	public static AdvertisingBo getInstance() {
		if (bo == null) {
			bo = new AdvertisingBo();
		}
		return bo;
	}

	/**
	 * 获取广告
	 * @param context
	 * @param call
	 */
	public void getAdvertising(Context context, HttpCallBack<BaseResp> call) {
		if (advertTask != null) {
			advertTask.cancel(true);
		}
		advertTask = CommonTask.newInstance(call, context, AdvertisingEntity.class);
		String imsi = AppHelper.getMobileIMSI(context);
		String cityCode = MenuMgr.getInstance().getCityCode(context);
		String province = "35";
		if (!StringUtil.isEmpty(cityCode) && cityCode.length() >= 2) {
			province = cityCode.substring(0, 2);
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("imsi", imsi);
		map.put("city_code", cityCode);
		map.put("os_type", AppHelper.getOSType());
		map.put("province", province);
		advertTask.setParams(map, Config.UrlConfig.URL_ADVERTISEMENT);
		advertTask.execute();
	}
	
	
	/**
	 * 获取听广播和看电视的广告
	 * @param context
	 * @param call
	 */
	public void getAdvertising_new(Context context, HttpCallBack<BaseResp> call,String adv_busiType) {
		if (advertTask != null) {
			advertTask.cancel(true);
		}
		advertTask = CommonTask.newInstance(call, context, AdvertisingEntity.class);
		String imsi = AppHelper.getMobileIMSI(context);
		String cityCode = MenuMgr.getInstance().getCityCode(context);
		String province = "35";
		if (!StringUtil.isEmpty(cityCode) && cityCode.length() >= 2) {
			province = cityCode.substring(0, 2);
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("imsi", imsi);
		map.put("city_code", cityCode);
		map.put("os_type", AppHelper.getOSType());
		map.put("province", province);
		map.put("adv_busiType", adv_busiType);
		advertTask.setParams(map, Config.UrlConfig.URL_ADVERTISEMENT);
		advertTask.execute();
	}

	/**
	 * 取消广告查询
	 */
	public void cancelAdvertTask() {
		if (advertTask != null) {
			advertTask.cancel(true);
		}
	}
	public AdvertisingEntity getAdEntity() {
		return adEntity;
	}

	public void setAdEntity(AdvertisingEntity adEntity) {
		this.adEntity = adEntity;
	}
}
