package cn.ffcs.wisdom.city.simico.api.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.ffcs.wisdom.city.simico.activity.subscribe.view.Channel;
import cn.ffcs.wisdom.city.simico.api.request.BaseRequest;
import cn.ffcs.wisdom.city.simico.kit.log.TLog;
import cn.ffcs.wisdom.city.sqlite.model.MenuItem;

public class MenuHelper {
	protected static final String TAG = MenuHelper.class.getSimpleName();

	// "icon":
	// "upload/3507/menu/img/400/icon_0_1365406508057_32046204189533488295669823.png",
	// "menuName": "母婴用品",
	// "menuId": 785539,
	// "menuPid": 427659,
	// "cityCode": "3507",
	// "menuType": "wap",
	// "package_": null,
	// "main": null,
	// "url": "http://218.5.102.205:8080/zhqz/ysyymyyp.do",
	// "appUrl": null,
	// "menudesc": null,
	// "mustLogin": "false",
	// "baseLine": "400",
	// "isApp": "0",
	// "v6Icon":
	// "upload/3507/menu/img/400/icon_4_1366334413822_22682276-7941570250990718315.png",
	// "recommend": "0",
	// "menuVer": 3643,
	// "appsize": "K",
	// "menuOrder": 3,
	// "recommendOrder": null,
	// "isNew": null,
	// "isRed": 0,
	// "commonOrder": null,
	// "parMapString": ""
	// frontMenuOrder:12
	// commonOrder:null
	//
	public static MenuItem make(JSONObject json) throws JSONException {
		MenuItem service = new MenuItem();
		service.setIcon(BaseRequest.IMG_URL + json.optString("icon"));
		service.setMenuName(json.optString("menuName"));
		service.setMenuId(json.optString("menuId"));
		service.setMenuPid(json.optString("menuPid"));
		service.setCityCode(json.optString("cityCode"));
		service.setMenuType(json.optString("menuType"));
		service.setPackage_(json.optString("package_"));
		service.setMain(json.optString("main"));
		service.setUrl(json.optString("url"));
		service.setAppUrl(json.optString("appUrl"));
		service.setMenudesc(getSafeString(json, "menudesc"));
		service.setMustLogin(json.optBoolean("mustLogin"));
		service.setBaseLine(json.optString("baseLine"));
		service.setIsApp(json.optString("isApp"));
		service.setV6Icon(json.optString("v6Icon"));
		service.setRecommend(json.optString("recommend"));
		service.setMenuVer(json.optString("menuVer"));
		service.setAppsize(json.optString("appsize"));
		service.setMenuOrder(json.optInt("menuOrder"));
		service.setRecommendOrder(json.optInt("recommendOrder"));
		service.setIsNew(json.optString("isNew"));
		service.setIsRed(json.optString("isRed"));
		service.setMap(json.optString("parMapString"));
		service.setFrontMenuOrder(json.optInt("frontMenuOrder"));
		service.setCommonOrder(json.optInt("commonOrder"));
		service.setAncestorId(json.optInt("ancestorId", -1));
		service.setMenuPid(json.optString("menuPid"));
		service.setLeafmenuorder(json.optInt("leafMenuOrder"));
		if (json.has("shareContent")) {
			service.setShareContent(json.getString("shareContent"));
		}
		if (json.has("shareType")) {
			service.setShareType(json.getString("shareType"));
		}
		return service;
	}

	public static ArrayList<MenuItem> makeAll(JSONArray array)
			throws JSONException {
		ArrayList<MenuItem> list = new ArrayList<MenuItem>();
		final int size = array.length();
		for (int i = 0; i < size; i++) {
			list.add(make(array.getJSONObject(i)));
		}
		return list;
	}

	public static ArrayList<MenuItem> makeAllWithSubscribe(JSONArray array,
			Set<String> subscribes) throws JSONException {
		ArrayList<MenuItem> list = new ArrayList<MenuItem>();
		final int size = array.length();
		for (int i = 0; i < size; i++) {
			MenuItem item = make(array.getJSONObject(i));
			item.setSubsribe(subscribes.contains(item.getMenuId()));
			list.add(item);
		}
		return list;
	}

	// “icon":"upload/3507/menu/img/400/icon_0_1365406508057_32046204189533488295669823.png",
	// "menu_name": "母婴用品",
	// "menu_id": 785539,
	// "city_code": "3507",
	// "menu_type": "wap",
	// "android_package_name ": null,
	// "android_class_name": null,
	// " ios_clsname ": null,
	// " ios_pkgname ": null,
	// “app_catalog”:”1”,
	// "url": "http://218.5.102.205:8080/zhqz/ysyymyyp.do",
	// "android_app_url": null,
	// "ios_app_url": null,
	// "menu_desc": null,
	// "must_login": "false",
	// "base_line": "400",
	// "is_app": "0",
	// "v6_icon":
	// "upload/3507/menu/img/400/icon_4_1366334413822_22682276-7941570250990718315.png",
	// "menu_ver": 3643,
	// "app_size": "K",
	// "menu_order": 3,
	// "is_new": null,
	// "is_red": 0,
	// "param_map_string": "",
	// "red_record_time":"2014-03-27 18:38:42",
	// "share_content": null,
	// "share_type": "2"
	public static MenuItem makeV2(JSONObject json) throws JSONException {
		MenuItem menu = new MenuItem();
		String iconUrl = json.optString("icon");
		menu.setIcon(iconUrl.startsWith("http://") ? iconUrl
				: BaseRequest.IMG_URL + iconUrl);
		menu.setMenuName(json.optString("menu_name"));
		menu.setMenuId(json.optString("menu_id"));
		// menu.setMenuPid(json.optString("menu_pid"));
		menu.setCityCode(json.optString("city_code"));
		menu.setMenuType(json.optString("menu_type"));
		if (json.has("android_package_name")) {
			menu.setPackage_(json.optString("android_package_name"));
		} else if (json.has("package_")) {
			menu.setPackage_(json.optString("package_"));
		} else {
			TLog.log(TAG, "没有找到包名");
		}
		if (json.has("android_class_name")) {
			menu.setMain(json.optString("android_class_name"));
		} else if (json.has("main_")) {
			menu.setMain(json.optString("main_"));
		} else {
			TLog.log(TAG, "没有找到类名");
		}

		menu.setUrl(json.optString("url"));
		if (json.has("android_app_url")) {
			menu.setAppUrl(json.optString("android_app_url"));
		} else {
			menu.setAppUrl(json.optString("app_url"));
		}
		menu.setMenudesc(getSafeString(json, "menu_desc"));
		menu.setMustLogin(json.optBoolean("must_login"));
		menu.setBaseLine(json.optString("base_line"));
		menu.setIsApp(json.optString("is_app"));
		menu.setV6Icon(json.optString("v6_icon"));
		// menu.setRecommend(json.optString("recommend"));
		menu.setMenuVer(json.optString("menu_ver"));
		menu.setAppsize(json.optString("app_size"));
		menu.setMenuOrder(json.optInt("menu_order"));
		// menu.setRecommendOrder(json.optInt("recommendOrder"));
		menu.setIsNew(json.optString("is_new"));
		menu.setIsRed(json.optString("is_red"));
		menu.setMap(json.optString("param_map_string"));
		menu.setShareType(json.optString("share_type"));
		menu.setShareContent(json.optString("share_content"));
		menu.setRedRecordTime(json.optString("red_record_time"));
		// menu.setFrontMenuOrder(json.optInt("frontMenuOrder"));
		// menu.setCommonOrder(json.optInt("commonOrder"));
		// menu.setAncestorId(json.optInt("ancestorId", -1));
		// menu.setMenuPid(json.optString("menuPid"));
		// menu.setLeafMenuOrder(json.optInt("leafMenuOrder"));
		menu.setIsSystemDefault(json.optInt("is_system_default", 0));
		return menu;
	}

	// "share_content": null,
	// "menu_id": 200333,
	// "icon": "upload/3501/menu/img/400/1324365111014.png",
	// "share_type": "2",
	// "menu_name": "本地新闻",
	// "red_record_time": "2014-03-27 18:38:42",
	// "param_map_string": null,
	// "android_package_name": null,
	// "url": null,
	// "v6_icon": "",
	// "is_new": null,
	// "menu_type": "navi_menu",
	// "menu_order": 2,
	// "app_size": null,
	// "menu_desc": null,
	// "is_red": 0,
	// "android_class_name": null,
	// "is_app": null,
	// "city_code": "3501",
	// "android_app_url": null,
	// "subs_order": 6,
	// "must_login": "false"
	public static JSONObject toJSON(MenuItem menu) {
		JSONObject json = new JSONObject();
		try {
			// json.put("share_content", menu.get)
			// json.put("share_type", menu.get)

			json.put("android_package_name", menu.getPackage_());
			json.put("android_class_name", menu.getMain());

			json.put("menu_id", menu.getMenuId());
			json.put("icon", menu.getIcon());
			json.put("menu_name", menu.getMenuName());
			json.put("red_record_time", menu.getRedRecordTime());
			// json.put("param_map_string", menu.get);
			// json.put("android_package_name", );
			json.put("url", menu.getUrl());
			json.put("v6_icon", menu.getV6Icon());
			json.put("is_new", menu.getIsNew());
			json.put("menu_type", menu.getMenuType());
			json.put("menu_order", menu.getMenuOrder());
			json.put("app_size", menu.getAppsize());
			json.put("menu_desc", menu.getMenudesc());
			json.put("is_red", menu.getIsRed());
			// json.put("android_class_name", menu.getc);
			json.put("is_app", menu.getIsApp());
			json.put("city_code", menu.getCityCode());
			json.put("android_app_url", menu.getAppUrl());
			json.put("is_system_default", menu.getIsSystemDefault());
			// json.put("subs_order", );//??
			// json.put("must_login", menu.get);
		} catch (JSONException e) {
			TLog.error(e.getMessage());
		}
		return json;
	}

	private static String getSafeString(JSONObject json, String key) {
		String val = json.optString(key);
		if ("null".equals(val))
			return null;
		return val;
	}

	public static ArrayList<MenuItem> makeAllV2(JSONArray array)
			throws JSONException {
		ArrayList<MenuItem> list = new ArrayList<MenuItem>();
		final int size = array.length();
		for (int i = 0; i < size; i++) {
			list.add(makeV2(array.getJSONObject(i)));
		}
		return list;
	}

	public static ArrayList<MenuItem> makeAllV2WithSubscribe(JSONArray array,
			Set<String> subscribes) throws JSONException {
		ArrayList<MenuItem> list = new ArrayList<MenuItem>();
		final int size = array.length();
		for (int i = 0; i < size; i++) {
			MenuItem item = makeV2(array.getJSONObject(i));
			item.setSubsribe(subscribes.contains(item.getMenuId()));
			list.add(item);
		}
		return list;
	}

	public static Set<String> makeAllIdsV2(JSONArray array)
			throws JSONException {
		Set<String> list = new HashSet<String>();
		final int size = array.length();
		for (int i = 0; i < size; i++) {
			list.add(array.getJSONObject(i).getString("menu_id"));
		}
		return list;
	}

	public static Set<String> makeAllSystemIdsV2(JSONArray array)
			throws JSONException {
		Set<String> list = new HashSet<String>();
		final int size = array.length();
		for (int i = 0; i < size; i++) {
			JSONObject obj = array.getJSONObject(i);
			if (obj.optInt("is_system_default", 0) == 1)
				list.add(obj.getString("menu_id"));
		}
		return list;
	}

	public static ArrayList<Category> makeAll2(ArrayList<MenuItem> histories,
			JSONArray array) throws JSONException {
		ArrayList<Category> list = new ArrayList<Category>();

		ArrayList<MenuItem> temp = new ArrayList<MenuItem>();
		final int size = array.length();
		for (int i = 0; i < size; i++) {
			JSONObject json = array.getJSONObject(i);
			MenuItem menu = make(json);
			if (menu.getAncestorId() == -1) {
				Category ctg = new Category();
				ctg.setMenu(menu);
				list.add(ctg);
			} else {
				// TLog.log(TAG, "发现3级菜单所属1级菜单:" + menu.getAncestorId());
				// 找到父菜单并加入
				boolean found = false;
				for (Category ctg : list) {
					// TLog.log(TAG, "1级菜单:" + ctg.getMenu().getMenuId());
					if (ctg.getMenu().getMenuId() != null
							&& ctg.getMenu()
									.getMenuId()
									.equals(String.valueOf(menu.getAncestorId()))) {
						// TLog.log(TAG, "将3级菜单加入1级菜单");
						ctg.addSubMenu(menu);
						found = true;
						break;
					}
				}
				if (!found) {
					// TLog.log(TAG, "暂时没有找到父菜单");
					temp.add(menu);
				}
			}
		}
		if (temp.size() > 0) {
			for (MenuItem menu : temp) {
				for (Category ctg : list) {
					// TLog.log(TAG, "1级菜单:" + ctg.getMenu().getMenuId());
					if (ctg.getMenu().getMenuId() != null
							&& ctg.getMenu()
									.getMenuId()
									.equals(String.valueOf(menu.getAncestorId()))) {
						// TLog.log(TAG, "将3级菜单加入1级菜单");
						ctg.addSubMenu(menu);
						break;
					}
				}
			}
		}

		// 排序
		Collections.sort(list);
		for (Category ctg : list) {
			Collections.sort(ctg.getServices(), new Comparator<MenuItem>() {

				@Override
				public int compare(MenuItem lhs, MenuItem rhs) {
					return lhs.getLeafmenuorder() > rhs.getLeafmenuorder() ? 1
							: -1;
				}
			});
		}
		Collections.sort(list);
		for (Category ctg : list) {
			Collections.sort(ctg.getServices(), new Comparator<MenuItem>() {
				@Override
				public int compare(MenuItem lhs, MenuItem rhs) {
					return lhs.getLeafmenuorder() == rhs.getLeafmenuorder() ? 0
							: (lhs.getLeafmenuorder() > rhs.getLeafmenuorder() ? 1
									: -1);
				}
			});
		}
		if (histories != null && histories.size() > 0) {
			Category ctg = new Category();
			MenuItem m = new MenuItem();
			m.setMenuName("最近使用");
			ctg.setMenu(m);
			ctg.setShowMore(false);
			ctg.setServices(histories);
			list.add(0, ctg);
		}

		return list;
	}

	public static ArrayList<Category> makeAll3(ArrayList<MenuItem> histories,
			ArrayList<MenuItem> services) {
		ArrayList<Category> list = new ArrayList<Category>();

		if (histories != null && histories.size() > 0) {
			Category ctg = new Category();
			MenuItem m = new MenuItem();
			m.setMenuName("最近使用");
			ctg.setMenu(m);
			ctg.setShowMore(false);
			ctg.setServices(histories);
			list.add(ctg);
		}

		ArrayList<MenuItem> temp = new ArrayList<MenuItem>();
		final int size = services.size();
		for (int i = 0; i < size; i++) {
			MenuItem menu = services.get(i);
			if (menu.getAncestorId() == -1) {
				Category ctg = new Category();
				ctg.setMenu(menu);
				list.add(ctg);
			} else {
				// TLog.log(TAG, "发现3级菜单所属1级菜单:" + menu.getAncestorId());
				// 找到父菜单并加入
				boolean found = false;
				for (Category ctg : list) {
					// TLog.log(TAG, "1级菜单:" + ctg.getMenu().getMenuId());
					if (ctg.getMenu().getMenuId() != null
							&& ctg.getMenu()
									.getMenuId()
									.equals(String.valueOf(menu.getAncestorId()))) {
						// TLog.log(TAG, "将3级菜单加入1级菜单");
						ctg.addSubMenu(menu);
						found = true;
						break;
					}
				}
				if (!found) {
					// TLog.log(TAG, "暂时没有找到父菜单");
					temp.add(menu);
				}
			}
		}
		if (temp.size() > 0) {
			for (MenuItem menu : temp) {
				for (Category ctg : list) {
					// TLog.log(TAG, "1级菜单:" + ctg.getMenu().getMenuId());
					if (ctg.getMenu().getMenuId() != null
							&& ctg.getMenu()
									.getMenuId()
									.equals(String.valueOf(menu.getAncestorId()))) {
						// TLog.log(TAG, "将3级菜单加入1级菜单");
						ctg.addSubMenu(menu);
						break;
					}
				}
			}
		}
		return list;
	}

	public static class MenuCompartor implements Comparator<MenuItem> {
		@Override
		public int compare(MenuItem lhs, MenuItem rhs) {
			return lhs.getMenuOrder() == rhs.getMenuOrder() ? 0 : (lhs
					.getMenuOrder() > rhs.getMenuOrder() ? 1 : -1);
		}
	}

	public static class MenuRecommendCompartor implements Comparator<MenuItem> {
		@Override
		public int compare(MenuItem lhs, MenuItem rhs) {
			return lhs.getRecommendOrder() == rhs.getRecommendOrder() ? 0
					: (lhs.getRecommendOrder() > rhs.getRecommendOrder() ? 1
							: -1);
		}
	}

	public static class ServiceTagCompartor implements Comparator<ServiceTag> {
		@Override
		public int compare(ServiceTag lhs, ServiceTag rhs) {
			return lhs.getOrder() == rhs.getOrder() ? 0 : (lhs.getOrder() > rhs
					.getOrder() ? 1 : -1);
		}
	}

	public static JSONArray toJSONArray(List<MenuItem> mDataToSave) {
		JSONArray array = new JSONArray();
		for (MenuItem menu : mDataToSave) {
			array.put(toJSON(menu));
		}
		return array;
	}

	public static MenuItem makeAppV2(JSONObject json) throws JSONException {
		MenuItem menu = new MenuItem();
		menu.setId(json.getInt("id"));
		menu.setMenuName(json.optString("app_name"));
		String iconUrl = json.optString("default_icon");
		menu.setIcon(iconUrl.startsWith("http://") ? iconUrl
				: BaseRequest.IMG_URL + iconUrl);
		menu.setPackage_(json.optString("android_pkgname"));
		menu.setMain(json.optString("android_clsname"));
		menu.setAppUrl(json.optString("android_app_url"));
		menu.setMenudesc(json.optString("description"));
		menu.setMustLogin(json.optBoolean("must_login"));
		menu.setAppsize(json.optString("app_size"));
		menu.setMenuType(json.optString("os_type"));
		menu.setShareType(json.optString("download_type"));
		menu.setShareContent(json.optString("wap_url"));
		return menu;
	}

	public static ArrayList<MenuItem> makeAppListV2(JSONArray array)
			throws JSONException {
		ArrayList<MenuItem> list = new ArrayList<MenuItem>();
		final int size = array.length();
		for (int i = 0; i < size; i++) {
			list.add(makeAppV2(array.optJSONObject(i)));
		}
		return list;
	}

	public static Channel makeChannelV2(JSONObject json) throws JSONException {
		int id = json.getInt("id");
		int chnl_order = json.optInt("chnl_order", -1);
		String name = json.getString("name");
		String description = json.optString("description", "");
		String logo = json.optString("logo", "");
		return new Channel(id, "推荐".endsWith(name) ? "__all__" : "news_local",
				name, description, json.toString(), chnl_order);
	}

	public static ArrayList<Channel> makeMyChannelV2(JSONArray array)
			throws JSONException {
		ArrayList<Channel> list = new ArrayList<Channel>();
		final int size = array.length();
		for (int i = 0; i < size; i++) {
			Channel item = makeChannelV2(array.getJSONObject(i));
			item.isMy = true;
			list.add(item);
		}
		return list;
	}

	public static ArrayList<Channel> makeAllChannelV2(JSONArray array,
			List<Channel> myChannel) throws JSONException {
		ArrayList<Channel> list = new ArrayList<Channel>();
		final int size = array.length();
		for (int i = 0; i < size; i++) {
			Channel item = makeChannelV2(array.getJSONObject(i));
			if (myChannel != null && myChannel.contains(item))
				continue;
			list.add(item);
		}
		return list;
	}

	public static cn.ffcs.wisdom.city.simico.activity.home.view.Category makeCategoryV2(
			JSONObject json) throws JSONException {
		int id = json.getInt("id");
		int chnl_order = json.optInt("chnl_order", -1);
		String name = json.getString("name");
		String description = json.optString("description", "");
		String logo = json.optString("logo", "");
		return new cn.ffcs.wisdom.city.simico.activity.home.view.Category(id,
				"" + id, name, description, json.toString(), chnl_order);
	}

	public static ArrayList<cn.ffcs.wisdom.city.simico.activity.home.view.Category> makeMyCategoryV2(
			JSONArray array) throws JSONException {
		ArrayList<cn.ffcs.wisdom.city.simico.activity.home.view.Category> list = new ArrayList<cn.ffcs.wisdom.city.simico.activity.home.view.Category>();
		final int size = array.length();
		for (int i = 0; i < size; i++) {
			cn.ffcs.wisdom.city.simico.activity.home.view.Category item = makeCategoryV2(array
					.getJSONObject(i));
			list.add(item);
		}
		return list;
	}

}
