package cn.ffcs.wisdom.city.report;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Handler;
import cn.ffcs.wisdom.base.CommonTask;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.sqlite.model.Report;
import cn.ffcs.wisdom.city.sqlite.service.ReportService;
import cn.ffcs.wisdom.city.utils.ConfigUtil;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.SharedPreferencesUtil;
import cn.ffcs.wisdom.tools.StringUtil;
import com.umeng.analytics.MobclickAgent;

public class ReportManager {

	private static ReportManager mInstance;
	private Context mContext;

	private LinkedList<Report> mData = new LinkedList<Report>();

	private boolean reporting; // 是否正在执行上报
	private boolean running; // 线程是否运行
	Report currentReport;

	private int failCount;

	Thread mThread;
	
	Handler mHandler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			sendReport(currentReport);
		};
	};

	private ReportManager(Context context) {
		this.mContext = context;
	}

	public static ReportManager getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new ReportManager(context);
		}
		return mInstance;
	}

	/**
	 * 初始化缓存数据
	 */
	private void initData() {
		List<Report> list = ReportService.getInstance(mContext).query();
		if (list != null) {
		}
		mData.addAll(list);
	}

	public void start() {
		synchronized (mInstance) {
			// 网络是否正常
			if (!CommonUtils.isNetConnectionAvailable(mContext)) {
				return;
			}

			if (mThread == null || !running) {
				mThread = new Thread(mr);
				initData();

				running = true;
				mThread.start();

			}
		}
	}

	Runnable mr = new Runnable() {

		@Override
		public void run() {

			try {
				while (running) {

					Thread.sleep(500);

					Log.e("running .mData size:" + mData.size());
					if (mData.size() == 0) {
						running = false;
						Log.e("running quit.");
						break;
					}

					if (reporting && currentReport != null) {
						continue;
					}

					currentReport = mData.peek();
					if (currentReport == null) {
						running = false;
						continue;
					}

					reporting = true;
					mHandler.sendEmptyMessage(0);

				}
			} catch (Exception e) {
				Log.e(e.getMessage(), e);
				reporting = false;
				running = false;
				mData.poll();
			}
		}
	};

	HttpCallBack<BaseResp> mCall = new HttpCallBack<BaseResp>() {

		@Override
		public void progress(Object... obj) {

		}

		@Override
		public void onNetWorkError() {
			System.out.println("reporting onNetWorkError...");
			reporting = false;
			failCount++;

			if (failCount >= 3) { // 失败，重试3次
				failCount = 0;
				currentReport = null;
				mData.poll();
			}
		}

		@SuppressWarnings("static-access")
		@Override
		public void call(BaseResp response) {
			System.out.println("reporting success..." + " desc:" + response.getDesc());
			Log.e("reporting call..." + " desc:" + response.getDesc());
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("state", response.getStatus());
			map.put("desc", response.getDesc());

			// 只要不是客户端网络原因，程序异常。就算提交成功
			if (!response.ERROR.equals(response.getStatus())
					&& !response.NETWORK_ERROR.equals(response.getStatus())) {
				// 发送成功
				// 删除队列数据
				// 删除数据库缓存
				mData.poll();
				deleteCache(currentReport);
				Log.e("reporting success...delete success.");
				currentReport = null;
				failCount = 0;
				MobclickAgent.onEvent(mContext, "103301", map);
			} else {
				failCount++;
				Log.e("reporting failt...delete success.");
				if (failCount >= 3) { // 失败，重试3次
					failCount = 0;
					currentReport = null;
					mData.poll();
				}

				if (!running) { // 线程结束，重启
					start();
				}

			}
			reporting = false;
			Log.e("reporting call end...reporting:" + reporting + " currentReport=null:"
					+ (currentReport == null) + "running:" + running);
		}
	};

	private void deleteCache(Report rp) {
		ReportService.getInstance(mContext).delete(rp);
	}

	private void saveCache(Report rp) {
		ReportService.getInstance(mContext).save(mContext, rp);
	}

	public void requestReprot() {
		Report rp = createReprot();
		saveCache(rp);
	}

	public Report createReprot() {
		Report rp = new Report();
		String imsi = AppHelper.getSerialCode2(mContext);
		String channelNum = ConfigUtil.readChannelName(mContext, Config.UMENG_CHANNEL_KEY);
		String osVersion = AppHelper.getOSRelease(); // 系统版本号
		String menuVersion = String.valueOf(AppHelper.getVersionCode(mContext));// 应用版本号
		String osName = mContext.getString(R.string.os_name);
		String osType = mContext.getString(R.string.os_type);
		String model = AppHelper.getModel();

		String carieer = AppHelper.getSimOperatorName(mContext); // 运营商
		String mobileName = AppHelper.getBrand(); // 手机品牌
		String manufacturer = AppHelper.getManufacturer(); // 厂商信息
		String appVersion = menuVersion; // 应用版本
		String appVersionName = AppHelper.getVersionName(mContext); // 应用名称
		String lat = SharedPreferencesUtil.getValue(mContext, Key.K_LOCATION_LAT);
		String lng = SharedPreferencesUtil.getValue(mContext, Key.K_LOCATION_LNG);
		String imei = AppHelper.getIMEI(mContext);
		String accessType = CommonUtils.getAccessType(mContext); // 网络制式
		String mobile = AccountMgr.getInstance().getMobile(mContext); // 手机号码
		String clientType = mContext.getString(R.string.version_name_update);
		String cityCode = MenuMgr.getInstance().getCityCode(mContext);
		String macAddress = AppHelper.getMacAddress();
		if (StringUtil.isEmpty(macAddress)) {
			macAddress = AppHelper.getWifiMacAddress(mContext);
		}
		if (!StringUtil.isEmpty(macAddress)) {
			macAddress = macAddress.replaceAll(":", "");
		}
		if (StringUtil.isEmpty(imsi) && !StringUtil.isEmpty(macAddress)) {
			imsi = macAddress;
		}
		if (StringUtil.isEmpty(imei) && !StringUtil.isEmpty(macAddress)) {
			imei = macAddress;
		}
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String time = sDateFormat.format(new Date());

		rp.setAccessType(accessType);
		rp.setAppversion(appVersion);
		rp.setAppversionName(appVersionName);
		rp.setCarieer(carieer);
		rp.setChannelNum(channelNum);
		rp.setCityCode(cityCode);
		rp.setClientType(clientType);
		rp.setImei(imei);
		rp.setImsi(imsi);
		rp.setLat(lat);
		rp.setLng(lng);
		rp.setManufacturer(manufacturer);
		rp.setMenuVersion(menuVersion);
		rp.setMobile(mobile);
		rp.setMobileName(mobileName);
		rp.setModel(model);
		rp.setOsType(osType);
		// rp.setScreenResolution(screenResolution);
		rp.setSystemName(osName);
		rp.setSystemVersion(osVersion);
		rp.setMacAddress(macAddress);
		rp.setTime(time);
		return rp;
	}

	public void sendReport(Report rp) {
		if (rp != null) {
			String screenResolution = AppHelper.getScreenWidth(mContext) + "x"
					+ AppHelper.getScreenHeight(mContext);
			Map<String, String> map = new HashMap<String, String>();
			map.put("imsi", rp.getImsi());
			map.put("channelNum", rp.getChannelNum());
			map.put("menu_version", rp.getMenuVersion());
			map.put("os_type", rp.getOsType());
			map.put("mobileName", rp.getMobileName());
			map.put("systemName", rp.getSystemName());
			map.put("systemVersion", rp.getSystemVersion());
			map.put("model", rp.getMobile());
			map.put("screenResolution", screenResolution);
			map.put("carieer", rp.getCarieer());
			map.put("manufacturer", rp.getManufacturer());
			map.put("appversion", rp.getAppversion());
			map.put("appversionName", rp.getAppversionName());
			map.put("lat", rp.getLat());
			map.put("lng", rp.getLng());
			map.put("accessType", rp.getAccessType());
			map.put("mobile", rp.getMobile());
			map.put("imei", rp.getImei());
			map.put("client_type", rp.getClientType());
			map.put("cityCode", rp.getCityCode());
			map.put("macAddress", rp.getMacAddress());

			Log.e("request to server...");
			// Looper.prepare();

			Log.e("request to server...");
			// Looper.prepare();
			CommonTask task = CommonTask.newInstance(mCall, mContext, BaseResp.class);
			String url = Config.UrlConfig.URL_CHANNEL_RECORD + "&time=" + rp.getTime();
			task.setParams(map, url);
			task.execute();
			// Looper.loop();
			MobclickAgent.onEvent(mContext, "103300", "发起渠道统计");

			// String filePath = Config.SDCARD_CITY_ROOT + "/report/report.txt";
			// String message = TimeUitls.getCurrentTime() +
			// " report to icity 1000..";
			// message += rp.toString();
			// ReportUtil.saveTxt(message, new File(filePath), true);
		}
	}

}
