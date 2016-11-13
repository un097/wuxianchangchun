package cn.ffcs.wisdom.city.home.datamgr;

import java.util.HashMap;
import java.util.Map;

import cn.ffcs.wisdom.city.home.entity.WeatherEntity;

/**
 * <p>Title:  天气缓存      </p>
 * <p>Description:                     </p>
 * <p>@author: zhangwsh                </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-10-31           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class WeatherMgr {

	private static WeatherMgr weatherMgr;
	private Map<String, WeatherEntity> weatherEntityMap = new HashMap<String, WeatherEntity>();

	private WeatherMgr() {
	}

	public static WeatherMgr getInstance() {
		if (weatherMgr == null) {
			weatherMgr = new WeatherMgr();
		}
		return weatherMgr;
	}

	public WeatherEntity getWeatherEntity(String cityCode) {
		if (weatherEntityMap == null) {
			return null;
		}
		return weatherEntityMap.get(cityCode);
	}

	public void setWeatherEntity(String cityCode, WeatherEntity weatherEntity) {
		if (weatherEntityMap == null) {
			weatherEntityMap = new HashMap<String, WeatherEntity>();
		}
		weatherEntityMap.put(cityCode, weatherEntity);
	}

	public void clearWeatherEntity() {
		if (weatherEntityMap != null) {
			weatherEntityMap.clear();
			weatherEntityMap = null;
		}
	}
}
