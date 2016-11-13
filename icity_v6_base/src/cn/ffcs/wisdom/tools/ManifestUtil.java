package cn.ffcs.wisdom.tools;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class ManifestUtil {
	
	/**
	 * 根据字段获取metadata值
	 * @param context
	 * @param Key
	 * @return
	 */
	public static String readMetaData(Context context, String Key) {
		ApplicationInfo info;
		String value = "";
		try {
			info = context.getPackageManager().getApplicationInfo(context.getPackageName(),
					PackageManager.GET_META_DATA);
			value = info.metaData.getString(Key);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return value;
	}
	
	/**
	 * 获得友盟渠道
	 * @param context
	 * @return
	 */
	public static String readUMChannel(Context context) {
		ApplicationInfo info;
		String value = "";
		try {
			info = context.getPackageManager().getApplicationInfo(context.getPackageName(),
					PackageManager.GET_META_DATA);
			value = info.metaData.getString("UMENG_CHANNEL");
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return value;
	}
}
