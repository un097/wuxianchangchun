package cn.ffcs.wisdom.city.home.bo;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import cn.ffcs.wisdom.base.CommonTask;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.home.entity.WeatherEntity;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;

/**
 * <p>Title: 天气业务逻辑          </p>
 * <p>Description: 
 * </p>
 * <p>@author: Leo                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-3-6             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class WeatherBo {
	private Context mContext;
	private CommonTask task;
	private static WeatherBo bo;

	public static WeatherBo getInstance(Context context) {
		if (bo == null) {
			bo = new WeatherBo(context);
		}
		return bo;
	}

	private WeatherBo(Context mContext) {
		this.mContext = mContext;
	}

	/**
	 * 获取天气
	 */
	public void acquireWeather(HttpCallBack<BaseResp> call) {
		if (task != null) {
			task.cancel(true);
		}
		task = CommonTask.newInstance(call, mContext, WeatherEntity.class);
		String cityName = MenuMgr.getInstance().getCityName(mContext);
		Map<String, String> params = new HashMap<String, String>();
		params.put("cityName", cityName);
		task.setParams(params, Config.UrlConfig.URL_WEATHER_DATA);
//		task.setParams(params, "http://iep-cas.153.cn:8084/icity-infosource-ztq/service/icity/ztq/weather.jhtml");
		
		task.execute();
	}

	/**
	 * 取消查询天气
	 */
	public void cancelQueryWeather() {
		if (task != null) {
			task.cancel(true);
		}
	}
}
