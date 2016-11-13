//package cn.ffcs.config;
//
//import cn.ffcs.wisdom.tools.StringUtil;
//import android.content.ContentResolver;
//import android.content.Context;
//import android.database.Cursor;
//import android.net.Uri;
//
//public class CityContentProviderTool {
//
//	private static String v7city = "";
//
//	/**
//	 * 获取v7城市
//	 * @param context
//	 * @return 以逗号隔开的v7城市编码
//	 */
//	public static String getV7City(Context context) {
//		if (StringUtil.isEmpty(v7city)) {
//			String authority = context.getResources().getString(
//					R.string.content_provider_authorities);
//			ContentResolver contentResolver = context.getContentResolver();
//			Uri uri = Uri.parse("content://" + authority + "/cities");
//			Cursor cursor = contentResolver.query(uri, new String[] { "city_code" },
//					"city_style=?", new String[] { "7" }, null);
//
//			if (cursor != null) {
//				if (cursor.moveToFirst()) {
//					v7city = cursor.getString(cursor.getColumnIndex("city_code"));
//					while (cursor.moveToNext()) {
//						v7city += "," + cursor.getString(cursor.getColumnIndex("city_code"));
//					}
//				}
//			}
//		}
//		return v7city;
//	}
//
//	public static void clearV7CityCache() {
//		v7city = "";
//	}
//}
