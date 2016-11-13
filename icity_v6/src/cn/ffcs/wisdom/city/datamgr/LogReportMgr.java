package cn.ffcs.wisdom.city.datamgr;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import cn.ffcs.wisdom.base.DataManager;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.entity.LogReportEntity;
import cn.ffcs.wisdom.city.home.entity.AdvertisingEntity.Advertising;
import cn.ffcs.wisdom.city.home.widget.entity.CommonWidgetEntity.CommonWidgetData;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.sqlite.model.LogItem;
import cn.ffcs.wisdom.city.sqlite.model.MenuItem;
import cn.ffcs.wisdom.city.sqlite.service.LogReportService;
import cn.ffcs.wisdom.city.utils.ConfigUtil;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.SharedPreferencesUtil;
import cn.ffcs.wisdom.tools.TimeUitls;

/**
 * <p>Title:日志报告管理类          </p>
 * <p>Description: 
 * </p>
 * <p>@author: Leo                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-9-2             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class LogReportMgr extends DataManager {
	private static final String PRODUCTID = "changchuntv";// 爱城市
	private static LogReportMgr mInstance = new LogReportMgr();

	static final Object sInstanceSync = new Object();

	private LogReportMgr() {
	}

	public static LogReportMgr getInstance() {
		synchronized (sInstanceSync) {
			if (mInstance == null)
				mInstance = new LogReportMgr();
		}

		return mInstance;
	}

	/**
	 * 首页广告点击
	 * @param context
	 * @param adv
	 */
	public void addBannerLog(Context context, Advertising adv) {
		LogItem log = new LogItem();
		log.setAreaId(Config.LOG_AREA_INDEX);
		log.setItemId(adv.getAdv_id());
		log.setItemType(Config.LOG_ITEM_BANNER);
		log.setDesc(adv.getTitle());
		LogReportMgr.getInstance().addLogItem(context, log);
	}

	/**
	 * 常用栏目点击
	 * @param context
	 * @param menu
	 */
	public void addCommonLog(Context context, MenuItem menu) {
		LogItem log = new LogItem();
		log.setAreaId(Config.LOG_AREA_COMMON);
		log.setItemId(menu.getMenuId());
		log.setItemType(Config.LOG_ITEM_MENU);
		log.setDesc(menu.getMenuName());
		LogReportMgr.getInstance().addLogItem(context, log);
	}

	/**
	 * 热门推荐点击
	 * @param context
	 * @param menu
	 */
	public void addRecommendLog(Context context, MenuItem menu) {
		LogItem log = new LogItem();
		log.setAreaId(Config.LOG_AREA_RECOMMEND);
		log.setItemId(menu.getMenuId());
		log.setItemType(Config.LOG_ITEM_MENU);
		log.setDesc(menu.getMenuName());
		LogReportMgr.getInstance().addLogItem(context, log);
	}

	/**
	 * widget图片点击
	 * @param context
	 * @param menu
	 */
	public void addWidgetLog(Context context, CommonWidgetData widget) {
		LogItem log = new LogItem();
		log.setAreaId(widget.getMenuId());
		log.setItemId(widget.getWidgetId());
		log.setItemType(Config.LOG_ITEM_WIDGET);
		log.setDesc(widget.getDesc());
		LogReportMgr.getInstance().addLogItem(context, log);
	}

	/**
	 * 批量添加日志
	 * @param context
	 * @param item
	 */
	public void addLogAll(Context context, List<LogItem> logs) {
		LogReportService logService = LogReportService.getInstance(context);
		logService.addLogAll(logs);
	}

	/**
	 * 添加单个日志
	 * @param context
	 * @param item
	 */
	public void addLogItem(Context context, LogItem item) {
		LogReportService logService = LogReportService.getInstance(context);
		item = addParam(context, item);
		logService.addLogItem(item);
	}

	/**
	 * 添加公共参数
	 * 其余参数为：areaId，itemType，itemId，subItemId，desc
	 * @param mContext
	 * @param item
	 * @return
	 */
	private LogItem addParam(Context mContext, LogItem item) {
		if (item != null) {
			String clientType = mContext.getString(R.string.version_name_update);
			String clientVersion = String.valueOf(AppHelper.getVersionCode(mContext));
			String clientChannelType = ConfigUtil.readChannelName(mContext,
					Config.UMENG_CHANNEL_KEY);
			String mobile = AccountMgr.getInstance().getMobile(mContext);
			String cityCode = MenuMgr.getInstance().getCityCode(mContext);
			String lat = SharedPreferencesUtil.getValue(mContext, Key.K_LOCATION_LAT);
			String lng = SharedPreferencesUtil.getValue(mContext, Key.K_LOCATION_LNG);
			String imsi = AppHelper.getSerialCode2(mContext);
			String imei = AppHelper.getIMEI(mContext);
			item.setOsType("1");
			item.setProductId(PRODUCTID);
			item.setClientType(clientType);
			item.setClientVersion(clientVersion);
			item.setClientChannelType(clientChannelType);
			item.setMobile(mobile);
			item.setCityCode(cityCode);
			item.setLongitude(lng);
			item.setLatitude(lat);
			item.setImei(imei);
			item.setImsi(imsi);
			item.setTimestamp(TimeUitls.getCurrentTime());
		}
		return item;
	}

	/**
	 * 查询所有的日志
	 * @param context
	 * @return
	 */
	public List<LogItem> queryLogs(Context context) {
		LogReportService logService = LogReportService.getInstance(context);
		return logService.queryAllLogs();
	}

	/**
	 * 删除日志
	 * @param context
	 * @param logs
	 */
	public void deleteLogs(Context context, List<LogItem> logs) {
		LogReportService logService = LogReportService.getInstance(context);
		logService.deleteLogs(logs);
	}

	/**
	 * 实体对象转换
	 * @param items
	 * @return
	 */
	public List<LogReportEntity> convertToLogEntity(List<LogItem> items) {
		List<LogReportEntity> logList = null;
		if (items != null && items.size() > 0) {
			logList = new ArrayList<LogReportEntity>();
			for (LogItem item : items) {
				LogReportEntity entity = new LogReportEntity();
				entity.setProduct_id(item.getProductId());
				entity.setClient_type(item.getClientType());
				entity.setClient_version(item.getClientVersion());
				entity.setClient_channel_type(item.getClientChannelType());
				entity.setOs_type(item.getOsType());
				entity.setImei(item.getImei());
				entity.setImsi(item.getImsi());
				entity.setMobile(item.getMobile());
				entity.setCity_code(item.getCityCode());
				entity.setLongitude(item.getLongitude());
				entity.setLatitude(item.getLatitude());
				entity.setArea_id(item.getAreaId());
				entity.setItem_type(item.getItemType());
				entity.setItem_id(item.getItemId());
				entity.setSub_item_id(item.getSubItemId());
				entity.setDesc(item.getDesc());
				entity.setTimestamp(item.getTimestamp());
				logList.add(entity);
			}
		}
		return logList;
	}
}
