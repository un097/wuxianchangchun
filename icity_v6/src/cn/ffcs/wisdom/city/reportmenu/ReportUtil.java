package cn.ffcs.wisdom.city.reportmenu;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
//import cn.ffcs.pay.utils.DesEncrypter;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.sqlite.model.MenuItem;
import cn.ffcs.wisdom.city.sqlite.model.ReportMenu;
import cn.ffcs.wisdom.city.sqlite.service.ReportMenuService;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.DesEncrypter;
import cn.ffcs.wisdom.tools.JsonUtil;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.MD5;
import cn.ffcs.wisdom.tools.SharedPreferencesUtil;
import cn.ffcs.wisdom.tools.StringUtil;

/**
 * <p>Title: 上报记录工具类     </p>
 * <p>Description: 
 *  包括：
 * <p> 1. 收藏记录上报。</p>
 * <p>2. 访问记录上报。</p>
 * </p>
 * <p>@author: Eric.wsd                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2012-7-4             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class ReportUtil {
	public static final String FAVORITE = "0"; // 收藏
	public static final String ACCESS = "1"; // 访问

	private static final String DES_KEY = "b5eefe0437d945b98e82f46fbff8d3552c2ff6f7f8acd8de";
	private static final String SL_KEY = "75BD2E98AC17564B2DB7C74B064F5084C6557FDDF3E4C286";

	/**
	 * 上报收藏记录
	 * @param context
	 * @param itemId
	 */
	public static void sendFavoriteReport(Context context, MenuItem menu) {
		sendReport(context, FAVORITE, menu);
	}

	/**
	 * 上报访问记录
	 * @param context
	 * @param itemId
	 */
	public static void sendAccessReport(Context context, MenuItem menu) {
		sendReport(context, ACCESS, menu);
	}

	/**
	 * 发送报告
	 * @param activity	相应调用的activity
	 * @param actionType
	 * @param itemId
	 */
	private static void sendReport(Context context, String actionType, MenuItem menu) {
		// ReportBo bo = new ReportBo(context);
		// bo.sendReport(actionType, itemId);
		try {
			if (menu == null) {
				return;
			}
			String menuId = menu.getMenuId();
			if (StringUtil.isEmpty(menuId)) {
				Log.e("itemId is null...menuName:" + menu.getMenuName());
				return;
			}
			createReport(context, actionType, menuId);

			long count = ReportMenuService.getInstance(context).getCount();
			if (count > 10) {
				ReportUtil.pullReport(context);
			}
		} catch (Exception e) {

		}
	}

	private static void createReport(Context context, String actionType, String menuId) {
		String mobile = AccountMgr.getInstance().getMobile(context);
		String imsi = AppHelper.getSerialCode2(context);
		String cityCode = MenuMgr.getInstance().getCityCode(context);
		String lat = SharedPreferencesUtil.getValue(context, Key.K_LOCATION_LAT);
		String lng = SharedPreferencesUtil.getValue(context, Key.K_LOCATION_LNG);

		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sDateFormat.format(new Date());

		ReportMenu reportMenu = new ReportMenu();
		reportMenu.setAction_type(actionType);
		reportMenu.setCity_code(cityCode);
		reportMenu.setImsi(imsi);
		reportMenu.setItem_id(menuId);
		reportMenu.setLat(lat);
		reportMenu.setLng(lng);
		reportMenu.setMobile(mobile);
		reportMenu.setCreate_time(time);

		ReportMenuService.getInstance(context).save(reportMenu);
	}

	public static void pullReport(Context context) {
		try {
			List<ReportMenu> reports = ReportMenuService.getInstance(context).query();

			String json = "";
			String timestamp = System.currentTimeMillis() + "";
			String sign = sign(timestamp);
			if (reports != null && reports.size() > 0) {

				json = "{" + "\"sign\":\"" + sign + "\"," + "\"timestamp\":" + timestamp + ","
						+ "\"data\":" + JsonUtil.toJson(reports) + "}";
				ReportBo bo = new ReportBo(context);
				bo.sendReport(json);
			}
		} catch (Exception e) {
		}
	}

	public static String sign(String timestamp) {
		String key = SL_KEY;
		String sign = MD5.getMD5Str(key + timestamp);
		String desKey = DES_KEY;
		try {
			sign = DesEncrypter.encodeBy3DES(desKey, timestamp + "$" + sign);
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sign;
	}

	/**
	 * 加密，用于自动登录及分享短信
	 * @param timestamp
	 * @return
	 */
	public static String signKey(String timestamp) {
		String key = SL_KEY;
		String sign = MD5.getMD5Str(key + "$" + timestamp);
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
