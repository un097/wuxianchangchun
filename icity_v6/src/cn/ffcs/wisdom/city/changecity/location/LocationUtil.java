package cn.ffcs.wisdom.city.changecity.location;

import android.content.Context;
import cn.ffcs.config.ExternalKey;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.tools.SharedPreferencesUtil;

/**
 * <p>Title:  定位信息获取工具类                         </p>
 * <p>Description:                     </p>
 * <p>@author: liaodl                  </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-3-20           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class LocationUtil {

	/**
	 * 保存经度
	 */
	public static void saveLongitude(Context context, String longitude) {
		SharedPreferencesUtil.setValue(context, Key.K_LOCATION_LNG, longitude);
	}

	/**
	 * 保存纬度
	 */
	public static void saveLatitude(Context context, String latitude) {
		SharedPreferencesUtil.setValue(context, Key.K_LOCATION_LAT, latitude);
	}

	/**
	 * 保存定位到的城市名称
	 */
	public static void saveLocationCityName(Context context, String name) {
		SharedPreferencesUtil.setValue(context, Key.K_LOCATION_CITY, name);
	}

	/**
	 * 得到经度信息
	 */
	public static String getLongitude(Context context) {
		return SharedPreferencesUtil.getValue(context, Key.K_LOCATION_LNG);
	}

	/**
	 * 得到纬度信息
	 */
	public static String getLatitude(Context context) {
		return SharedPreferencesUtil.getValue(context, Key.K_LOCATION_LAT);
	}

	/**
	 * 得到定位城市名称
	 */
	public static String getLocationCityName(Context context) {
		return SharedPreferencesUtil.getValue(context, Key.K_LOCATION_CITY);
	}

	/**
	 * 保存定位到的城市编码
	 */
	public static void saveLocationCityCode(Context context, String cityCode) {
		SharedPreferencesUtil.setValue(context, ExternalKey.K_ICITY_LOCATION_CITY_CODE, cityCode);
	}

	/**
	 * 得到定位到的城市编码
	 */
	public static String getLocationCityCode(Context context) {
		return SharedPreferencesUtil.getValue(context, ExternalKey.K_ICITY_LOCATION_CITY_CODE);
	}

}
