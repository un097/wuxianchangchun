package cn.ffcs.wisdom.city.utils;

import android.content.Context;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.tools.SharedPreferencesUtil;
import cn.ffcs.wisdom.tools.StringUtil;
/**
 * <p>Title: 菜单工具类         </p>
 * <p>Description: 包括
 *  1. 获取和设置城市编码
 *  2. 获取省份编码
 *  3. 获取城市名称
 *  4. 获取城市菜单
 *  5. 获取菜单版本
 *  6. 创建八项菜单
 *  7. 判断是否含有此项栏目
 * </p>
 * <p>@author: Eric.wsd                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2012-6-3             </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class MenuUtil {

	/**
	 * 返回城市编码，只传入Context参数
	 * @param context
	 * @return
	 */
	public static String getCityCode(Context context) {
		return getCityCode(context, "");
	}

	/**
	 * 返回城市编码，需要传入Context和默认编码参数mDefault
	 * @param context
	 * @param mDefault
	 * @return
	 */
	public static String getCityCode(Context context, String mDefault) {
		if (context == null) {
			return mDefault;
		}
		String cityCode = SharedPreferencesUtil.getValue(context, Key.K_LOCAL_CITY_CODE);
		if (StringUtil.isEmpty(cityCode))
			cityCode = mDefault;
		return cityCode;
	}

	// save cityCode
	public static void setCityCode(Context context, String cityCode) {
		SharedPreferencesUtil.setValue(context, Key.K_LOCAL_CITY_CODE, cityCode);
	}

	// ======================================================
	// CityName
	// ======================================================
	public static String getCityName(Context context) {
		return SharedPreferencesUtil.getValue(context, Key.K_LOCAL_CITY_NAME);
	}

	public static String getCityName(Context context, String mDefault) {
		String cityName = getCityName(context);
		if (StringUtil.isEmpty(cityName))
			cityName = mDefault;
		return cityName;
	}

	// save cityName
	public static void setCityName(Context context, String cityName) {
		SharedPreferencesUtil.setValue(context, Key.K_LOCAL_CITY_NAME, cityName);
	}

	/**
	 * 返回省份编码
	 * @param context
	 * @param cityCode
	 * @return
	 */
	public static String getProvinceCode(Context context, String cityCode) {
		if (!StringUtil.isEmpty(cityCode) && cityCode.length() > 2) {
			return cityCode.substring(0, 2);
		}
		return "";
	}

	/**
	 * 返回省份编码
	 * @param context
	 * @return
	 */
	public static String getProviceCode(Context context) {
		String cityCode = getCityCode(context);
		return getProvinceCode(context, cityCode);
	}
}
