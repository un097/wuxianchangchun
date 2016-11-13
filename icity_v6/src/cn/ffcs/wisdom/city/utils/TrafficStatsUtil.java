package cn.ffcs.wisdom.city.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import cn.ffcs.wisdom.city.bo.TrafficStatsBo;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.SharedPreferencesUtil;

/**
 * <p>Title: 流量统计工具类    </p>
 * <p>Description: 
 * 	<p>1. 3G流量</p>
 *  <p>2. wifi流量</p>
 * </p>
 * <p>@author: Eric.wsd                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2012-7-4             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class TrafficStatsUtil {
	public static String NETWORK_FILE = "/proc/self/net/dev";// 网络流量查询文件

	public static String RMNET0 = "rmnet0";// gprs流量
	public static String PPP0 = "ppp0";// gprs流量
	public static String TIWLAN0 = "tiwlan0";// wifi流量
	public static String ETH0 = "eth0";// eth0流量

	private static final String GPRS_START_FLOW = "gprs_start_flow";
	private static final String WIFI_START_FLOW = "wifi_start_flow";

	/**
	 * 读取应用开关产生的流量
	 * @param network 网络类型
	 * @return 返回流量差
	 */
	private static double[] paraseFlowByType(String network) {
		String line;
		File file = new File(NETWORK_FILE);
		FileReader fr;
		double[] temp = null;
		try {
			fr = new FileReader(file);
			BufferedReader bufReader = new BufferedReader(fr);
			while ((line = bufReader.readLine()) != null) {
				if (line.indexOf(":") != -1) {
					String[] segs = line.split(":");
					if (segs[1] != null && segs[0].endsWith(network)) {
						String[] data = segs[1].trim().split(" ");// 数据默认以空格隔开
						double downStream = data[0] != null && !"".equals(data[0]) ? Double
								.valueOf(data[0]) : 0;
						double downPackage = data[1] != null && !"".equals(data[1]) ? Double
								.valueOf(data[1]) : 0;
						double upStream = data[8] != null && !"".equals(data[8]) ? Double
								.valueOf(data[8]) : 0;
						double upPackage = data[9] != null && !"".equals(data[9]) ? Double
								.valueOf(data[9]) : 0;

						// 返回信息
						temp = new double[4];
						temp[0] = downStream;// 下行流量
						temp[1] = downPackage;// 下行包
						temp[2] = upStream;// 上行流量
						temp[3] = upPackage;
					}
				}
			}
			if (bufReader != null) {
				bufReader.close();
			}
			if (fr != null) {
				fr.close();
			}
		} catch (FileNotFoundException e) {
			Log.e("流量查询文件不存在,无法统计当前流量");
		} catch (IOException e) {
			Log.e("文件读取发生异常，无法统计当前流量");
		}
		if (temp == null) {
			temp = new double[4];
			temp[0] = 0;// 下行流量
			temp[1] = 0;// 下行包
			temp[2] = 0;// 上行流量
			temp[3] = 0;
		}

		return temp;
	}

	/**
	 * 应用启动，开始统计流量
	 * @param context
	 */
	public static void startTrafficStats(Context context) {
		Log.d("开始启动流量统计...");

		// gprs流量统计
		double[] gprsStream_rmnet_start = paraseFlowByType(RMNET0);
		double[] gprsStream_ppp_start = paraseFlowByType(PPP0);
		// wifi流量统计
		double[] wifiStream_tiwlan_start = paraseFlowByType(TIWLAN0);
		double[] wifiStream_eth_start = paraseFlowByType(ETH0);

		// 保存
		SharedPreferencesUtil.setValue(
				context,
				GPRS_START_FLOW,
				String.valueOf(gprsStream_rmnet_start[0] + gprsStream_rmnet_start[2]
						+ gprsStream_ppp_start[0] + gprsStream_ppp_start[2]));
		SharedPreferencesUtil.setValue(
				context,
				WIFI_START_FLOW,
				String.valueOf(wifiStream_tiwlan_start[0] + wifiStream_tiwlan_start[2]
						+ wifiStream_eth_start[0] + wifiStream_eth_start[2]));
	}

	/**
	 * 应用退出，停止统计
	 * @param context
	 */
	public static void stopTrafficStats(Context context) {
		Log.d("停止流量统计...");
		try {
			// gprs流量统计
			double[] gprsStream_rmnet_end = paraseFlowByType(RMNET0);
			double[] gprsStream_ppp_end = paraseFlowByType(PPP0);
			// wifi流量统计
			double[] wifiStream_end = paraseFlowByType(TIWLAN0);
			double[] wifiStream_eth_end = paraseFlowByType(ETH0);

			double gprs_start_flow = Double.valueOf(SharedPreferencesUtil.getValue(context,
					GPRS_START_FLOW));
			double gprs_end_flow = gprsStream_rmnet_end[0] + gprsStream_rmnet_end[2]
					+ gprsStream_ppp_end[0] + gprsStream_ppp_end[2];

			double wifi_start_flow = Double.valueOf(SharedPreferencesUtil.getValue(context,
					WIFI_START_FLOW));
			double wifi_end_flow = wifiStream_end[0] + wifiStream_end[2] + wifiStream_eth_end[0]
					+ wifiStream_eth_end[2];

			double gprsFlow = gprs_end_flow - gprs_start_flow;
			double wifiFlow = wifi_end_flow - wifi_start_flow;

			Log.d("3G流量 ： " + gprsFlow + "；wifi流量： " + wifiFlow);

			// 上报流量统计
			sendTrafficStats(context, gprsFlow, wifiFlow);
		} catch (Exception e) {
			Log.d("上报流量出错");
		}

	}

	/**
	 * 上报流量
	 * @param context
	 * @param gprsFlow	3G流量
	 * @param wifiFlow	wifi流量
	 */
	private static void sendTrafficStats(Context context, double gprsFlow, double wifiFlow) {
		Log.d("上报流量统计信息...");

		TrafficStatsBo bo = new TrafficStatsBo(context);

		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
			// 3g流量统计
			bo.sendTrafficStatsBo("3g", String.valueOf(gprsFlow));
		}
		if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			// wifi流量统计
			bo.sendTrafficStatsBo("wifi", String.valueOf(wifiFlow));
		}
	}
}
