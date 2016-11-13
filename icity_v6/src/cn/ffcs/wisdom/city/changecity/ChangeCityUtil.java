//package cn.ffcs.wisdom.city.changecity;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Locale;
//
//import android.content.Context;
//import cn.ffcs.wisdom.city.common.Key;
//import cn.ffcs.wisdom.city.entity.CityEntity;
//import cn.ffcs.wisdom.city.entity.ProvinceEntity;
//import cn.ffcs.wisdom.tools.Log;
//import cn.ffcs.wisdom.tools.SharedPreferencesUtil;
//import cn.ffcs.wisdom.tools.StringUtil;
//
//public class ChangeCityUtil {
//
//	/**
//	 * 防系统通讯录查询方式，自己设置获取SortKey
//	 *
//	 * @param displayName
//	 * @return
//	 */
//	public static String getSortKey(String displayName) {
//		List<String> nameList = new ArrayList<String>();
//		nameList.clear();
//		for (int i = 0; i < displayName.length(); i++) {
//			String singleStr = displayName.substring(i, i + 1);
//			nameList.add(singleStr);
//		}
//
//		StringBuilder sb = new StringBuilder();
//		String pinyin = StringUtil.toPinYin(displayName, true);
//		String[] pinyinArray = pinyin.split(",");
//		List<String> pinyinList = Arrays.asList(pinyinArray);
//
//		for (int i = 0; i < nameList.size(); i++) {
//			sb.append(pinyinList.get(i).toUpperCase(Locale.getDefault()));
//			sb.append(' ');
//			sb.append(nameList.get(i));
//			sb.append(' ');
//		}
//
//		return sb.toString();
//	}
//
//	/**
//	 * 由城市编码反查到省份
//	 *
//	 * @param provinces
//	 * @return
//	 */
//	public static ProvinceEntity getProvinceFromCity(String cityCode, List<ProvinceEntity> provinces) {
//		ProvinceEntity entity = new ProvinceEntity();
//		try {
//			if (provinces == null || provinces.size() <= 0) {
//				return entity;
//			}
//			for (ProvinceEntity province : provinces) {
//				for (CityEntity city : province.getCities()) {
//					if (cityCode.equals(city.getCity_code())) {
//						return province;
//					}
//				}
//			}
//		} catch (Exception e) {
//			Log.e("getProvinceFromCity()方法异常！" + e);
//		}
//		return entity;
//	}
//
//	/**
//	 * 设置首次从网络请求省市列表标志
//	 * @param context
//	 * @param flag
//	 */
//	public static void setCityRequestNetTag(Context context, boolean flag) {
//		SharedPreferencesUtil.setBoolean(context, Key.K_REQ_NET_CITY, flag);
//	}
//
//	/**
//	 * 是否从网络请求省市列表标志
//	 * @param context
//	 * @return
//	 */
//	public static boolean getCityRequestNetTag(Context context) {
//		return SharedPreferencesUtil.getBoolean(context, Key.K_REQ_NET_CITY);
//	}
//
//	/**
//	 * 获取城市列表时候加载成功
//	 * @return
//	 */
//	public static boolean getCityIsSend(Context context) {
//		return SharedPreferencesUtil.getBoolean(context, Key.K_GET_CITY_IS_SEND);
//	}
//
//	/**
//	 * 设置城市列表已经加载成功
//	 * @param context
//	 * @param success
//	 */
//	public static void setCityIsSend(Context context, boolean send) {
//		SharedPreferencesUtil.setBoolean(context, Key.K_GET_CITY_IS_SEND, send);
//	}
//}
