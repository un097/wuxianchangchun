package com.wookii.tools.comm;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.WindowManager;

public class DeviceTool {

	private DeviceTool() {
	}

	private static DeviceTool instance = null;
	private static DisplayMetrics dm;

	public synchronized static DeviceTool getInstance() {

		if (instance == null) {
			instance = new DeviceTool();

		}
		return instance;
	}

	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	public static String getIpAddress(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);  
        if (!wifiManager.isWifiEnabled()) {  
        	wifiManager.setWifiEnabled(true);    
        }  
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();       
        int ipAddress = wifiInfo.getIpAddress();   
        String ip = intToIp(ipAddress);   
		return ip;
		
	}
	
	private static String intToIp(int i) {

		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
				+ "." + (i >> 24 & 0xFF);
	} 
	
	/**
	 * 获取自定义数据
	 * 
	 * @param context
	 * @return
	 */
	public static String getChannelCode(Context context, String key) {
		String code = getMetaData(context, key);
		if (code != null) {
			return code;
		}
		return "";
	}

	/**
	 * 通过Key获取MetaData
	 * 
	 * @param context
	 * @param key
	 * @return
	 */
	private static String getMetaData(Context context, String key) {
		try {
			ApplicationInfo ai = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);
			Object value = ai.metaData.get(key);
			if (value != null) {
				return value.toString();
			}
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 判断sd卡是否存在，并且可读
	 * 
	 * @return
	 */
	public static boolean isHasSDCard() {

		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取设备Id
	 * 
	 * @return deviceId
	 */
	public static String getDeviceId(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = telephonyManager.getDeviceId();
		if (imei != null) {
			return imei;
		}
		return "";
	}

	public static String getSubscriberId(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = telephonyManager.getSubscriberId();
		if (imsi != null) {
			return imsi;
		}
		return "";
	}

	/**
	 * 判断网络是否可用
	 * 
	 * @param context
	 * @return 如果可用，返回true
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (info == null) {
				return false;
			} else {
				if (info.isAvailable()) {
					return true;
				}

			}
		}
		return false;
	}

	/**
	 * 获取当前网络类型
	 * 
	 * @param context
	 * @return
	 */
	public static String getNetworkTypeName(Context context) {
		String typeName = "";
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm.getActiveNetworkInfo() != null) {
			typeName = cm.getActiveNetworkInfo().getTypeName();
		}
		return typeName;
	}

	/**
	 * 查询Apn名字
	 * 
	 * @param context
	 * @return
	 */
	public static String getApnName(Context context) {
		// 设置默认APN URI
		Uri preferApnUri = Uri.parse("content://telephony/carriers/preferapn");
		String apnName = "";
		try {
			// 查询默认APN
			Cursor c = context.getContentResolver().query(preferApnUri, null,
					null, null, null);
			if (c != null) {
				c.moveToFirst();
				String proxyStr = c.getString(c.getColumnIndex("proxy"));
				if (proxyStr.equals("10.0.0.172")) {
					apnName = "cmwap";
				} else if (proxyStr.equals("10.0.0.200")) {
					apnName = "ctwap";
				}
				c.close();
			}
		} catch (Exception e) {
			return "";
		}
		apnName = apnName.toLowerCase();
		return apnName;
	}

	/**
	 * 注： density 大于1的情况下，需要设置targetSdkVersion在4-9之间，例如 <uses-sdk
	 * android:minSdkVersion="3" android:targetSdkVersion="10" />
	 * 
	 * @param context
	 */
	public static Dispaly getDisplay(Context context) {
		dm = new DisplayMetrics();
		Dispaly dis = new Dispaly();
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
		dis.density = dm.density; // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
		dis.densityDPI = dm.densityDpi; // 屏幕密度（每寸像素：120/160/240/320）
		dis.xdpi = dm.xdpi;
		dis.ydpi = dm.ydpi;

		dis.screenWidthDip = dm.widthPixels; // 屏幕宽（dip，如：320dip）
		dis.screenHeightDip = dm.heightPixels; // 屏幕高（dip，如：533dip）
		return dis;
	}

	public static class Dispaly {

		public int screenHeightDip;
		public int screenWidthDip;
		public float ydpi;
		public float xdpi;
		public int densityDPI;
		public float density;

	}

	public static int getMeasuredHeight(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
		return child.getMeasuredHeight();
	}

	/**
	 * 判断设备的横竖屏
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isLand(Context context) {
		Configuration cf = context.getResources().getConfiguration();
		int ori = cf.orientation;
		boolean b = false;
		if (ori == Configuration.ORIENTATION_LANDSCAPE) {
			b = true;
		} else if (ori == Configuration.ORIENTATION_PORTRAIT) {
			b = false;
		}
		return b;
	}

	/**
	 * 获取设备唯一标示加强版
	 * 
	 * @param context
	 * @return
	 */
	public static String getUniqueId(Context context) {
		String PREFS_FILE = "device_id";
		String PREFS_DEVICE_ID = "device_id";
		final SharedPreferences prefs = context.getSharedPreferences(
				PREFS_FILE, 0);
		String uniqueId = prefs.getString(PREFS_DEVICE_ID, null);
		if (uniqueId == null) {
			try {
				uniqueId = DeviceTool.getDeviceId(context);
				if ("".equals(uniqueId)) {
					String mac = getMac();
					uniqueId = "".equals(mac) ? mac : Secure.getString(
							context.getContentResolver(), Secure.ANDROID_ID);
				}
			} catch (Exception e) {

			}
		}
		prefs.edit().putString(PREFS_DEVICE_ID, uniqueId).commit();
		return uniqueId;
	}

	/**
	 * 获取mac地址
	 * 
	 * @return
	 */
	public static String getMac() {
		String macSerial = null;
		String str = "";
		try {
			Process pp = Runtime.getRuntime().exec(
					"cat /sys/class/net/wlan0/address ");
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
			ex.printStackTrace();
		}
		return macSerial;
	}

	public static String getVersoin(Context context) {
		PackageManager manager = context.getPackageManager();
		String version = null;
		try {
			PackageInfo info = manager.getPackageInfo(context.getPackageName(),
					0);
			version = info.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "当前软件版本为(" + version + ")";
	}
}
