package cn.ffcs.wisdom.city.download.report;

import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
//import cn.ffcs.pay.utils.DesEncrypter;
import cn.ffcs.wisdom.city.changecity.location.LocationUtil;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.sqlite.model.ApkInfo;
import cn.ffcs.wisdom.city.sqlite.model.ApkReportItem;
import cn.ffcs.wisdom.city.sqlite.service.ApkReportService;
import cn.ffcs.wisdom.city.utils.ConfigUtil;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.DesEncrypter;
import cn.ffcs.wisdom.tools.MD5;
import cn.ffcs.wisdom.tools.TimeUitls;

/**
 * <p>Title:   Apk下载完成上报工具类                        </p>
 * <p>Description:                     </p>
 * <p>@author: liaodl                  </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-11-6           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class ApkReportUtil {

	public static final String DES_KEY = "b5eefe0437d945b98e82f46fbff8d3552c2ff6f7f8acd8de";
	public static final String SL_KEY = "75BD2E98AC17564B2DB7C74B064F5084C6557FDDF3E4C286";

//	private static ScheduledExecutorService scheduledExecutorService = Executors
//			.newSingleThreadScheduledExecutor();
	
	private static ExecutorService executorService = Executors.newSingleThreadExecutor(); 

	public static void addApkLog(final Context context, final ApkInfo info) {

		executorService.execute(new Runnable() {

			@Override
			public void run() {
				ApkReportItem item = createApkReportItem(context, info);
				ApkReportService service = ApkReportService.getInstance(context);
				service.addApkLog(item);
			}
		});
	}

	public static void deleteApkLogs(final Context context) {

		executorService.execute(new Runnable() {

			@Override
			public void run() {
				List<ApkReportItem> list = findAllApkLogs(context);
				ApkReportService service = ApkReportService.getInstance(context);
				service.deleteReportLogs(list);
			}
		});
	}

	private static ApkReportItem createApkReportItem(Context context, ApkInfo info) {
		ApkReportItem item = createApkReportItemCommon(context);
		item.setItem_id(info.getItemId());
		item.setCreate_time(TimeUitls.getCurrentTime());
		return item;
	}

	private static ApkReportItem createApkReportItemCommon(Context context) {
		ApkReportItem item = new ApkReportItem();
		String imsi = AppHelper.getSerialCode2(context);
		String imei = AppHelper.getIMEI(context);
		String osType = context.getString(R.string.os_type);
		String mobile = AccountMgr.getInstance().getMobile(context);
		String clientType = context.getString(R.string.version_name_update);
		String channelType = ConfigUtil.readChannelName(context,Config.UMENG_CHANNEL_KEY);//客户端渠道类型
		String cityCode = MenuMgr.getInstance().getCityCode(context);
		String lat = LocationUtil.getLatitude(context);
		String lng = LocationUtil.getLongitude(context);
		item.setImsi(imsi);
		item.setImei(imei);
		item.setOs_type(osType);
		item.setMobile(mobile);
		item.setClient_type(clientType);
		item.setChannel_type(channelType);
		item.setCity_code(cityCode);
		item.setLat(lat);
		item.setLng(lng);
		return item;
	}

	public static List<ApkReportItem> findAllApkLogs(Context context) {
		ApkReportService service = ApkReportService.getInstance(context);
		return service.queryAllApkLogs();
	}

	/**
	 * 签名
	 * Base64(3DES (timestamp +$+md5(key+$+timetamp)))
	 */
	public static String sign(long timestamp) {
		String key = SL_KEY;

		// MD5加密
		String sign = MD5.getMD5Str(key + "$" + timestamp);

		// 3DS+BASE64加密
		String desKey = DES_KEY;
		try {
			sign = DesEncrypter.encodeBy3DES(desKey, timestamp + "$" + sign);
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return sign;
	}
}
