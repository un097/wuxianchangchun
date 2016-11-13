package cn.ffcs.wisdom.city.traffic.violations.bo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Application;
import android.content.Context;
import cn.ffcs.wisdom.base.CommonStandardTask;
import cn.ffcs.wisdom.base.CommonTask;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.sqlite.model.MenuItem;
import cn.ffcs.wisdom.city.traffic.violations.entity.AllPayInfoEntity;
import cn.ffcs.wisdom.city.traffic.violations.entity.TrafficViolationsEntity;
import cn.ffcs.wisdom.city.utils.ConfigUtil;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.JsonUtil;
import cn.ffcs.wisdom.tools.StringUtil;
import cn.ffcs.wisdom.tools.TimeUitls;

/**<p>Title:                           </p>
 * <p>Description:                     </p>
 * <p>@author: zhangwsh                </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-8-30           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class TrafficViolationsBo {
	private Context context;
	private static TrafficViolationsBo trafficViolationsBo;
	private static Map<String, CommonTask> queryTaskMap = new HashMap<String, CommonTask>();

	private TrafficViolationsBo(Context context) {
		this.context = context;
	}

	public static TrafficViolationsBo getInstance(Context context) {
		if (trafficViolationsBo == null) {
			trafficViolationsBo = new TrafficViolationsBo(context);
		}
		return trafficViolationsBo;
	}

	/**
	 * 查询违章
	 * @param callBack
	 * @param carNo
	 * @param carLastCodes
	 * @param carType
	 */
	public void queryTrafficViolations(HttpCallBack<BaseResp> callBack, String carNo,
			String carLastCodes, String carType) {
		CommonTask task = queryTaskMap.get(carNo);
		if (task != null) {
			task.cancel(true);
		}
		task = CommonTask.newInstance(callBack, context, TrafficViolationsEntity.class);
		Map<String, String> map = new HashMap<String, String>();
		String imsi = AppHelper.getMobileIMSI(context);
		if (StringUtil.isEmpty(imsi)) {
			imsi = "unknown";
		}
		String mobile = AccountMgr.getInstance().getMobile(context);
		String itemId = "";
		List<MenuItem> homeList = MenuMgr.getInstance().getFirstMenu(context);
		for (int i = 0; i < homeList.size(); i++) {
			String itemName = homeList.get(i).getMenuName();
			if (itemName.equals("交通")) {
				itemId = homeList.get(i).getMenuId();
			}
		}
		map.put("imsi", imsi);
		map.put("mobile", mobile);
		map.put("carNo", carNo);
		map.put("carLastCodes", carLastCodes);
		map.put("carType", carType);
		map.put("keyNamesOfRele", "violCarNo|violCarType|violCarLastCodes");
		map.put("item_id", itemId);
		map.put("os_type", "1");
		task.setParams(map, Config.UrlConfig.URL_WZ);
		queryTaskMap.put(carNo, task);
		task.execute();
	}

	/**
	 * 查询缴费总额详情
	 * @param callBack
	 * @param carNo
	 * @param carLastCodes
	 * @param carType
	 */
	public void queryCarFeesDetail(HttpCallBack<BaseResp> callBack, String carNo,
			String carLastCodes, String carType) {
		CommonTask task = queryTaskMap.get(carNo);
		if (task != null) {
			task.cancel(true);
		}
		task = CommonTask.newInstance(callBack, context, TrafficViolationsEntity.class);
		Map<String, String> map = new HashMap<String, String>();
		String imsi = AppHelper.getMobileIMSI(context);
		String mobile = AccountMgr.getInstance().getMobile(context);
		String productId = context.getString(R.string.version_name_update);
		String clientVersion = String.valueOf(AppHelper.getVersionCode(context));
		String clientChannelType = ConfigUtil.readChannelName(context, Config.UMENG_CHANNEL_KEY);
		String osType = AppHelper.getOSType();
		String time = TimeUitls.getCurrentTime();
		map.put("product_id", productId);
		map.put("client_version", clientVersion);
		map.put("client_channel_type", clientChannelType);
		map.put("os_type", osType);
		map.put("timestamp", time);
		map.put("imsi", imsi);
		map.put("mobile", mobile);
		map.put("carNo", carNo);
		map.put("carLastCodes", carLastCodes);
		//此处需要填写查询地址
		task.setParams(map, "");
		task.execute();
	}

	/**
	 * 删除违章
	 * @param callBack
	 * @param carNo
	 * @param carLastCodes
	 * @param carType
	 */
	public void deleteTrafficViolations(HttpCallBack<BaseResp> callBack, String carNo,
			String carLastCodes, String carType) {
		String imsi = AppHelper.getMobileIMSI(context);
		String mobile = AccountMgr.getInstance().getMobile(context);
		String itemId = "";
		List<MenuItem> homeList = MenuMgr.getInstance().getFirstMenu(context);
		for (int i = 0; i < homeList.size(); i++) {
			String itemName = homeList.get(i).getMenuName();
			if (itemName.equals("交通")) {
				itemId = homeList.get(i).getMenuId();
			}
		}
//		if (carNo.indexOf("闽") >= 0) {
//			carNo = carNo.replace("闽", "");
//		}
		if (carNo.indexOf("吉") >= 0) {
			carNo = carNo.replace("吉", "");
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("imsi", imsi);
		map.put("mobile", mobile);
		map.put("releKeyHashMapStr", "violCarNo," + carNo + "|violCarType," + carType
				+ "|violCarLastCodes," + carLastCodes);
		map.put("keyNamesOfRele", "");
		map.put("item_id", itemId);
		CommonTask task = CommonTask.newInstance(callBack, context, BaseResp.class);
		task.setParams(map, Config.UrlConfig.DELETE_WZ_RELEVANCE);
		task.execute();
	}

	/**
	 * 取消查询请求
	 * @param carNo
	 */
	public void cancelQueryTask(String carNo) {
		CommonTask task = queryTaskMap.get(carNo);
		if (task != null) {
			task.cancel(true);
		}
	}

	/**
	 * 获取缴费信息
	 * @param iCall
	 * @param carNo
	 * @param carLastCodes
	 */
	public void getViolationAllPay(HttpCallBack<BaseResp> iCall, String carNo, String carLastCodes) {
		CommonStandardTask task = CommonStandardTask.newInstance(iCall, context,
				AllPayInfoEntity.class);
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("car_no", carNo);
		paramsMap.put("car_last_code", carLastCodes);
		task.setParams(Config.UrlConfig.ALL_VIOLATIONS_PAY, JsonUtil.toJson(paramsMap),
				context.getString(R.string.version_name_update));
		task.execute();
	}
	
	
	public void get_one_menu(HttpCallBack<BaseResp> callBack) {
		String imsi = AppHelper.getMobileIMSI(context);
		Map<String, String> map = new HashMap<String, String>();
		map.put("menuVer", "0");
		map.put("cityCode", "2201");
		map.put("baseLine", "400");
		map.put("switchSystemType", "1");
		map.put("osType", "1");
		map.put("clientverMapping", AppHelper.getVersionCode(context) + "");
		CommonTask task = CommonTask.newInstance(callBack, context, BaseResp.class);
		task.setParams(map, Config.UrlConfig.URL_ONE_MENU);
		task.execute();
	}
}
