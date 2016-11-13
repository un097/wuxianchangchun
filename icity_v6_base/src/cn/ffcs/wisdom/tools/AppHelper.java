package cn.ffcs.wisdom.tools;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Rect;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.view.WindowManager;

/**
 * <p>Title: 应用帮助类         </p>
 * <p>Description: 包括
 * <p> 1. 获取版本名称          </p>
 * <p> 2. 获取版本号               </p>
 * <p> 3. 获取手机IMSI号    </p>
 * <p> 4. 获取手机IMEI号    </p>
 * <p> 5. 获取系统图类型     </p>
 * <p> 6. 获取手机屏幕宽高</p>
 * <p> 7. 获取手机屏幕密度比例</p>
 * <p> 8. 获取手机文字密度比例</p>
 * <p> 9. 获取手机顶部状态栏高度</p>
 * <p> 10. 获取手机IP地址</p>
 * <p> 11. 获取手机Mac地址</p>
 * </p>
 * <p>@author: Eric.wsd                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2012-6-3            </p>
 * <p>@author: zhangws      </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */

public final class AppHelper {

	/**
	 * 获取应用版本名称
	 * @param context
	 * @return
	 */
	public static String getVersionName(Context context) {
		String versionName = "";
		try {
			versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 1).versionName;
		} catch (NameNotFoundException e) {
			Log.e(e.getMessage(), e);
			// e.printStackTrace();
		}
		return versionName;
	}

	/**
	 * 获取应用版本号
	 * @param context
	 * @return
	 */
	public static int getVersionCode(Context context) {
		int versionCode = 0;
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(),
					1);
			versionCode = info.versionCode;
		} catch (NameNotFoundException e) {
			Log.e(e.getMessage(), e);
			// e.printStackTrace();
		}
		return versionCode;
	}

	public static String getPackageName(Context context) {
		String packageName = null;
		try {
			packageName = context.getPackageManager().getPackageInfo(context.getPackageName(), 1).packageName;
		} catch (NameNotFoundException e) {
			Log.e(e.getMessage(), e);
			// e.printStackTrace();
		}
		return packageName;
	}

	/**
	 * 获取IMSI
	 * @param context
	 * @return
	 */
	public static String getMobileIMSI(Context context) {
		// 获取mtkImsi
		String mtkImsi = getMtkFirstImsi(context);
		if (!StringUtil.isEmpty(mtkImsi)) {
			return mtkImsi;
		}

		// 获取高通Imsi
		String gaoTongImsi = getGaoTongFirstImsi(context);
		if (!StringUtil.isEmpty(gaoTongImsi)) {
			return gaoTongImsi;
		}

		// 获取展讯Imsi
		// modify by linjiafu 2014-08-26 
		// 去除获取展讯imsi的判断，防止高版本的Android系统闪退
//		String zhangXunImsi = getZhangXunFirstImsi(context);
//		if (!StringUtil.isEmpty(zhangXunImsi)) {
//			return zhangXunImsi;
//		}

		// 单卡Imsi
		TelephonyManager telMgr = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telMgr.getSubscriberId();
	}
	
	public static String getGaoTongFirstImsi(Context context) {
		try {
			Class<?> cx = Class.forName("android.telephony.MSimTelephonyManager");
			Method ms = cx.getMethod("getSubscriberId", int.class);
			Object obj = context.getSystemService("phone_msim");
			String firstImsi = (String) ms.invoke(obj, 0);
			if (StringUtil.isEmpty(firstImsi)) {
				return (String) ms.invoke(obj, 1);
			}
			return firstImsi;
		} catch (Exception e) {
			return null;
		}
	}

	public static String getMtkFirstImsi(Context context) {
		try {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			Class<?> c = Class.forName("com.android.internal.telephony.Phone");

			Field fields1 = c.getField("GEMINI_SIM_1");
			fields1.setAccessible(true);
			int simId_1 = (Integer) fields1.get(null);
			Field fields2 = c.getField("GEMINI_SIM_2");
			fields2.setAccessible(true);
			int simId_2 = (Integer) fields2.get(null);

			Method m = TelephonyManager.class.getDeclaredMethod("getSubscriberIdGemini", int.class);
			String imsi_1 = (String) m.invoke(tm, simId_1);
			String imsi_2 = (String) m.invoke(tm, simId_2);

			if (StringUtil.isEmpty(imsi_1)) {
				return imsi_2;
			}
			return imsi_1;
		} catch (Exception e) {
			return null;
		}
	}

	public static String getZhangXunFirstImsi(Context context) {
		try {
			Class<?> c = Class.forName("com.android.internal.telephony.PhoneFactory");
			Method m = c.getMethod("getServiceName", String.class, int.class);
			String spreadTmService = (String) m.invoke(c, Context.TELEPHONY_SERVICE, 1);

			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String imsi_1 = tm.getSubscriberId();
			TelephonyManager tm1 = (TelephonyManager) context.getSystemService(spreadTmService);
			String imsi_2 = tm1.getSubscriberId();
			if (StringUtil.isEmpty(imsi_1)) {
				return imsi_2;
			}
			return imsi_1;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 获取IMEI
	 * @param context
	 * @return
	 */
	public static String getIMEI(Context context) {
		TelephonyManager telManage = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telManage.getDeviceId();
	}

	/**
	 * 获取手机运营商
	 * @param context
	 * @return
	 */
	public static String getSimOperatorName(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getSimOperatorName();
	}

	/**
	 * 获取手机号码
	 * @param context
	 * @return
	 */
	public static String getSimMobile(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getLine1Number();
	}

	/**
	 * 获取手机品牌
	 * @return
	 */
	public static String getBrand() {
		return android.os.Build.BRAND;
	}

	/**
	 * 获取手机型号
	 * @return
	 */
	public static String getModel() {
		return android.os.Build.MODEL;
	}

	/**
	 * 返回系统类型，android系统为1
	 * @return
	 */
	public static String getOSType() {
		return "1"; // android系统为1
	}

	/**
	 * 获取新的OSType
	 * @return
	 */
	public static String getOSTypeNew() {
		return "android";
	}

	/**
	 * 操作系统版本
	 * @return
	 */
	public static String getOSRelease() {
		return android.os.Build.VERSION.RELEASE;
	}

	/**
	 * 手机厂商
	 * @return
	 */
	public static String getManufacturer() {
		return android.os.Build.MANUFACTURER;
	}

	/**
	 * 获取手机屏幕宽度
	 * @param activity
	 * @return
	 */
	public static int getScreenWidth(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	/**
	 * 获取手机屏幕高度
	 * @param activity
	 * @return
	 */
	public static int getScreenHeight(Context context) {
		return context.getResources().getDisplayMetrics().heightPixels;
	}

	/**
	 * 获取屏幕密度比例
	 * @param context
	 * @return
	 */
	public static float getScreenDensity(Context context) {
		return context.getResources().getDisplayMetrics().density;
	}

	/**
	 * 获取文字密度比例
	 * @param context
	 * @return
	 */
	public static float getScaledDensity(Context context) {
		return context.getResources().getDisplayMetrics().scaledDensity;
	}

	public static PackageInfo getPackageInfo(Activity activity, String packageName) {
		PackageInfo packageInfo;
		try {
			packageInfo = activity.getPackageManager().getPackageInfo(packageName, 0);

		} catch (NameNotFoundException e) {
			packageInfo = null;
			// e.printStackTrace();
			Log.e(e.getMessage(), e);
		}
		return packageInfo;
	}

	/**
	 * 获取手机顶部状态栏高度
	 * @param activity
	 * @return
	 */
	public static int getStatusBarHeight(Activity activity) {
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		return statusBarHeight;
	}

	/**
	 * 获取IP地址
	 * @return
	 */
	public static String getLocalIpAddress(Context context) {
		// 获取wifi服务
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		// 判断wifi是否开启
		if (wifiManager.isWifiEnabled()) {
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			int ipAddress = wifiInfo.getIpAddress();
			String ip = intToIp(ipAddress);
			return ip;
		}
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
					.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
						.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (Exception e) {
			Log.e("GetIpAddress Exception" + e);
		}
		return null;
	}

	private static String intToIp(int i) {
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "."
				+ (i >> 24 & 0xFF);
	}

	/**
	 * 获取手机MAC地址，通过wifi
	 * @param context
	 * @return
	 */
	public static String getLocalMacAddress(Context context) {
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();
	}

	/**
	 * 获取手机MAC地址
	 * @param context
	 * @return
	 */
	public static String getMacAddress() {
		String macSerial = null;
		String str = "";
		try {
			Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
			InputStreamReader ir = new InputStreamReader(pp.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);

			for (; null != str;) {
				str = input.readLine();
				if (str != null) {
					macSerial = str.trim();// 去空格
					break;
				}
			}
		} catch (IOException ex) {
			// 赋予默认值
			Log.e("获取MAC地址异常：" + ex);
		}
		return macSerial;

	}

	public static String getWifiMacAddress(Context context) {
		String macAddress = null;
		WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
		if (null != info) {
			macAddress = info.getMacAddress();
		}
		return macAddress;
	}

	/**
	 * 获取唯一码，通过对mac地址进行加密串
	 * @return
	 */
	public static String getSerialCode() {
		String mac = getMacAddress();
		if (!StringUtil.isEmpty(mac)) {
			return MD5.getMD5Str(mac);
		}

		return null;
	}

	/**
	 * 获取唯一码
	 * 1、默认imsi
	 * 2、当imsi为空时，imei
	 * 3、当imei为空时，获取mac地址
	 */
	public static String getSerialCode2(Context context) {
		String serial = getMobileIMSI(context);
		if (StringUtil.isEmpty(serial)) {
			serial = getIMEI(context);
		}

		if (StringUtil.isEmpty(serial)) {
			String macAddress = AppHelper.getMacAddress();
			if (!StringUtil.isEmpty(macAddress)) {
				serial = macAddress.replaceAll(":", "");
			}
		}

		return serial;
	}

	/**
	 * 开启硬件加速
	 * @param activity
	 */
	public static void OpenHardwareAcceleration(Activity activity) {
		if (CommonUtils.getSystemVersion() >= 11) {
			activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
					WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
		}
	}
}
