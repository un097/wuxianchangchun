package cn.ffcs.wisdom.city.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import cn.ffcs.wisdom.city.bo.TrafficStatsBo;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.SharedPreferencesUtil;

/**
 * <p>Title:  流量统计        </p>
 * <p>Description: 
 * 基于SDK 2.2 统计
 * </p>
 * <p>@author: Leo               </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-11-8             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class TrafficStatisticsFlowUtils {
	// 保存启动时的流量值
	private static final String RECORD_START_TOTAL_FLOW = "record_start_total_flow";

	// 保存退出时的流量值
	private static final String RECORD_STOP_TOTAL_FLOW = "record_stop_total_flow";

	/**
	 * 获取应用的uid
	 * @param context
	 * @return
	 */
	private static int getAppUid(Context context) {
		String packageName = AppHelper.getPackageName(context);
		PackageManager pm = context.getPackageManager();
		ApplicationInfo appInfo;
		int uid = 0;
		try {
			appInfo = pm.getApplicationInfo(packageName, PackageManager.GET_ACTIVITIES);
			uid = appInfo.uid;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return uid;
	}

	/**
	 * 记录当前的流量值
	 * @param context
	 */
	public static long recordCurrentTotalStatics(Context context) {
		int uid = getAppUid(context);
		long totalTraffic = 0l;
		if (uid != 0) {
			// 接收的流量
			long receiveTraffic = TrafficStats.getUidRxBytes(uid);
			// 发送的流量
			long transmitTraffic = TrafficStats.getUidTxBytes(uid);
			// 发送+接收的流量
			totalTraffic = receiveTraffic + transmitTraffic;
		}
		return totalTraffic;
	}

	/**
	 * 记录启动时的流量，并且保存在文件中
	 * @param context
	 */
	public static void startTrafficStats(Context context) {
		long startAppTotalFlow = recordCurrentTotalStatics(context);
		SharedPreferencesUtil.setLong(context, RECORD_START_TOTAL_FLOW, startAppTotalFlow);
	}

	/**
	 * 应用退出，停止统计
	 * @param context
	 */
	public static void stopTrafficStats(Context context) {
		Log.d("停止流量统计...");
		try {
			// 上一次退出的流量,如果大于零，表示保存了数据
			long lastQuitTraffic = SharedPreferencesUtil.getLong(context, RECORD_STOP_TOTAL_FLOW);
			// 应用退出的流量值
			long stopAppTraffic = recordCurrentTotalStatics(context);
			// 保存退出时的流量，以便于统计从退出这个时候到下一次启动前消耗的流量
			SharedPreferencesUtil.setLong(context, RECORD_STOP_TOTAL_FLOW, stopAppTraffic);
			// 得到启动时的流量
			long startAppTraffic = SharedPreferencesUtil.getLong(context, RECORD_START_TOTAL_FLOW,
					0);
			// 本次启动消耗的流量值：退出时的流量-启动时的流量+（本次启动的流量-上一次退出的流量）
			long totalCountTraffic = stopAppTraffic - startAppTraffic;
			if (lastQuitTraffic > 0 && (startAppTraffic - lastQuitTraffic > 0)) {
				totalCountTraffic = totalCountTraffic + startAppTraffic - lastQuitTraffic;
			}
			// 上报流量统计
			if (totalCountTraffic > 0) {
				sendTrafficStats(context, totalCountTraffic);
			}
		} catch (Exception e) {
			Log.d("上报流量出错");
		}

	}

	/**
	 * 上报流量
	 * @param context
	 * @param totalTrafficFlow
	 */
	private static void sendTrafficStats(Context context, long totalTrafficFlow) {
		Log.d("上报流量统计信息...");

		TrafficStatsBo bo = new TrafficStatsBo(context);
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo != null) {
			// 3g流量统计：本次启动消耗的流量，当前网络为3g
			if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
				bo.sendTrafficStatsBo("3g", String.valueOf(totalTrafficFlow));
			} else if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				// wifi流量统计：本次启动消耗的流量，当前网络为wifi
				bo.sendTrafficStatsBo("wifi", String.valueOf(totalTrafficFlow));
			}
		}
	}
}
