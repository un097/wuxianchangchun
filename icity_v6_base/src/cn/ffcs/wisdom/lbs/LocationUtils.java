package cn.ffcs.wisdom.lbs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.SharedPreferencesUtil;
import cn.ffcs.wisdom.tools.StringUtil;

import com.baidu.location.BDLocation;

/**
 * <h3>位置信息工具类  </h3>
 * <p>1. GPS定位</p>
 * <p>2. Network定位</p>
 */
public class LocationUtils {

	/**
	 * 定位的Action值
	 */
	public static final String LOCATION_ACTION = "cn.ffcs.location.LocationManager";

	public static final String K_ADDR = "k_address";

	/**
	 * 广播定位信息
	 * @param location 
	 * 		定位信息
	 * @param context
	 * 		上下文
	 * @param action 
	 * 		广播Action
	 */
	public static void sendBroadcast(BDLocation location, Context context, String action) {
		// 当定位异常时，清除缓存数据
		if (location == null) {
			SharedPreferencesUtil.setValue(context, "k_location_lat", "");
			SharedPreferencesUtil.setValue(context, "k_location_lng", "");
			return;
		} else {
			int locType = location.getLocType();
			if (locType != BDLocation.TypeGpsLocation && locType != BDLocation.TypeCacheLocation
					&& locType != BDLocation.TypeNetWorkLocation) {
				SharedPreferencesUtil.setValue(context, "k_location_lat", "");
				SharedPreferencesUtil.setValue(context, "k_location_lng", "");
				return;
			}
		}

		if (StringUtil.isEmpty(action)) {
			Log.e("Location broadcast action is null");
			return;
		}

		Intent intent = new Intent();
		intent.setAction(action);
		Bundle b = new Bundle();
		String lat = String.valueOf(location.getLatitude());
		String lng = String.valueOf(location.getLongitude());
		String city = location.getCity();
		String district = location.getDistrict();

		b.putString("k_location_lat", lat);
		b.putString("k_location_lng", lng);
		b.putString("k_location_city", city);
		b.putString("k_location_district", district);

		intent.putExtras(b);
		context.sendBroadcast(intent);
	}

}
