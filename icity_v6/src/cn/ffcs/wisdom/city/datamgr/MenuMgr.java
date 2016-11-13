package cn.ffcs.wisdom.city.datamgr;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import cn.ffcs.config.BaseConfig;
import cn.ffcs.surfingscene.advert.AdvertMgr;
import cn.ffcs.wisdom.base.DataManager;
import cn.ffcs.wisdom.city.changecity.location.LocationUtil;
import cn.ffcs.wisdom.city.sqlite.model.CityConfig;
import cn.ffcs.wisdom.city.sqlite.model.MenuItem;
import cn.ffcs.wisdom.city.sqlite.model.WidgetInfo;
import cn.ffcs.wisdom.city.sqlite.service.CityConfigService;
import cn.ffcs.wisdom.city.sqlite.service.MenuService;
import cn.ffcs.wisdom.city.utils.MenuUtil;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.StringUtil;

/**
 * <p>Title:  菜单管理</p>
 * <p>Description: </p>
 * <p>@author: caijj                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2012-9-6             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class MenuMgr extends DataManager {

	private static MenuMgr mInstance = new MenuMgr();

	private List<MenuItem> homeMenu = new ArrayList<MenuItem>();
	private Map<String, List<MenuItem>> secondMenu = new HashMap<String, List<MenuItem>>(); // 二级菜单
	private Map<String, List<MenuItem>> thirdMenu = new HashMap<String, List<MenuItem>>(); // 三级菜单
	private List<MenuItem> hotRecommend = new ArrayList<MenuItem>();

	private List<WidgetInfo> widgetInfos = new ArrayList<WidgetInfo>(); // 控件配置信息

	private CityConfig config;

	static final Object sInstanceSync = new Object();

	private boolean loadSuccess = false; // 数据加载成功后，变成true

	private MenuMgr() {
	}

	public static MenuMgr getInstance() {
		synchronized (sInstanceSync) {
			if (mInstance == null)
				mInstance = new MenuMgr();
		}
		return mInstance;
	}

	public boolean getLoadSuccess() {
		return loadSuccess;
	}

	public void saveMenuItem(Context context, List<MenuItem> menus, String cityCode) {
		MenuService menuService = MenuService.getInstance(context);
		boolean deleteSuccess = false; // 增加判断是否删除正常
		try {// 删除所有本地数据
			menuService.delete(cityCode);
			deleteSuccess = true;
		} catch (SQLException e) {
			deleteSuccess = false;
			Log.e(e.getMessage(), e);
			// e.printStackTrace();
		}

		if (deleteSuccess) {
			menuService.saveMenus(menus);
		}
	}

	public void saveWidgetInfo(Context context, List<WidgetInfo> windgetInfos, String cityCode) {
		MenuService menuService = MenuService.getInstance(context);
		menuService.deleteWidget(cityCode); // 删除所有本地数据

		menuService.saveWdigetInfo(windgetInfos, cityCode);
	}

	public void deleteWidget(Context context, String cityCode) {
		MenuService menuService = MenuService.getInstance(context);
		menuService.deleteWidget(cityCode); // 删除所有本地数据
	}

	public void refreshMenu(Context context, String cityCode) {
		List<MenuItem> menus = MenuService.getInstance(context).query(cityCode);
		refreshMenu(context, menus, cityCode);
	}

	public void refreshWidgetInfo(Context context, List<WidgetInfo> list) {
		if (list == null || list.size() == 0) {
			return;
		}

		widgetInfos.clear();
		widgetInfos.addAll(list);
	}

	public void clearWidgetInfo() {
		widgetInfos.clear();
	}

	public void refreshMenu(Context context, List<MenuItem> menus, String cityCode) {
		if (menus == null || menus.size() <= 0) {
			return;
		}
		synchronized (homeMenu) {

			loadCityConfig(context, cityCode);

			clearData();

			for (MenuItem home : menus) {

				// 1、初始化一级菜单
				String menuPid = home.getMenuPid();
				if ("0".equals(menuPid)) {
					homeMenu.add(home);
				}

				// 对菜单排序
				Collections.sort(homeMenu, new Comparator<MenuItem>() {
					@Override
					public int compare(MenuItem menu1, MenuItem menu2) {
						// 根据字段"LEVEL"排序
						if (menu1.getMenuOrder() > menu2.getMenuOrder()) {
							return 1;
						}
						return -1;
					}
				});

				// 2、初始化热门推荐
				if ("1".equals(home.getRecommend())) {
					hotRecommend.add(home);
				}

				// 对菜单排序
				Collections.sort(hotRecommend, new Comparator<MenuItem>() {
					@Override
					public int compare(MenuItem menu1, MenuItem menu2) {
						// 根据字段"LEVEL"排序
						if (menu1.getRecommendOrder() > menu2.getRecommendOrder()) {
							return 1;
						}
						return -1;
					}
				});
			}

			// 初始化二级菜单
			if (homeMenu != null && homeMenu.size() > 0) {
				for (MenuItem home : homeMenu) {
					String menuId = home.getMenuId();
					List<MenuItem> secondList = new ArrayList<MenuItem>();
					for (MenuItem m : menus) {
						if (menuId.equals(m.getMenuPid())) {
							secondList.add(m);
						}
					}
					// 对菜单排序
					Collections.sort(secondList, new Comparator<MenuItem>() {
						@Override
						public int compare(MenuItem menu1, MenuItem menu2) {
							// 根据字段"LEVEL"排序
							if (menu1.getMenuOrder() > menu2.getMenuOrder()) {
								return 1;
							}
							return -1;
						}
					});
					secondMenu.put(menuId, secondList);
				}
			}

			// 初始化三级菜单
			if (secondMenu != null && secondMenu.size() > 0) {
				Collection<List<MenuItem>> c = secondMenu.values();
				Iterator<List<MenuItem>> it = c.iterator();
				List<MenuItem> secondAll = new ArrayList<MenuItem>();
				for (; it.hasNext();) {
					List<MenuItem> secondList = it.next();
					secondAll.addAll(secondList);
				}

				for (MenuItem menu : secondAll) {
					String menuId = menu.getMenuId();
					List<MenuItem> thirdList = new ArrayList<MenuItem>();
					for (MenuItem m : menus) {
						if (menuId.equals(m.getMenuPid())) {
							thirdList.add(m);
						}
					}
					// 对菜单排序
					Collections.sort(thirdList, new Comparator<MenuItem>() {
						@Override
						public int compare(MenuItem menu1, MenuItem menu2) {
							// 根据字段"LEVEL"排序
							if (menu1.getMenuOrder() > menu2.getMenuOrder()) {
								return 1;
							}
							return -1;
						}
					});
					thirdMenu.put(menuId, thirdList);
				}
			}

			loadSuccess = true;
			notifyDataSetChanged();
		}
	}

	private void clearData() {
		homeMenu.clear();
		secondMenu.clear();
		thirdMenu.clear();
		hotRecommend.clear();
	}

	public List<MenuItem> loadMenuByMenuPid(Context context, String menuPid) {
		return MenuService.getInstance(context).loadMenuByMenuPid(menuPid);
	}

	public List<MenuItem> loadHomeMenu(Context context, String cityCode) {
		return MenuService.getInstance(context).loadHomeMenu(cityCode);
	}

	public void saveConfig(Context context, String cityCode, CityConfig config) {
		if (config != null) {
			CityConfigService.getInstance(context).saveConfig(cityCode, config);
		}
	}

	public void saveTyjcConfig(Context context, CityConfig config) {
		if (config != null) {
			// ========添加天翼景象广告配置==========
			AdvertMgr.getInstance().saveAdvertImg(context, config.getTyjxCode(),
					BaseConfig.GET_IMAGE_ROOT_URL() + config.getTyjx_image());
			AdvertMgr.getInstance().saveAdvertDuration(context, config.getTyjxCode(),
					config.getTyjx_duration());
		}
	}

	public void refreshConfig(Context context, String cityCode, CityConfig config) {
		this.config = config;
	}

	public void loadCityConfig(Context context, String cityCode) {
		CityConfig config = CityConfigService.getInstance(context).load(cityCode);
		refreshConfig(context, cityCode, config);
	}

	public List<MenuItem> loadHotRecommend(Context context, String cityCode) {
		return MenuService.getInstance(context).queryHotRecommond(cityCode);
	}

	// 当前城市名
	public String getCityName(Context context) {
		// 内存读取
		if (config == null) {
			config = getCityConfig(context);
		}
		if (config != null) {
			return config.getCityName();
		}
		return "";
	}

	// 当前城市编码
	public String getCityCode(Context context) {
		// 内存读取
		if (config == null) {
			config = getCityConfig(context);
		}
		if (config != null) {
			return config.getCityCode();
		}
		// 本地读取
		return MenuUtil.getCityCode(context);
	}

	public String getProvinceCode(Context context) {
		final String cityCode = getCityCode(context);
		String provinceCode = "";
		if (!StringUtil.isEmpty(cityCode)) {
			int length = cityCode.length();
			if (length > 2)
				provinceCode = cityCode.substring(0, 2) + "00";
		}
		return provinceCode;
	}

	/**
	 * 获取天翼景象城市编码
	 * @param context
	 * @return
	 */
	public String getTyjxCitycode(Context context) {
		// 内存读取
		if (config == null) {
			config = getCityConfig(context);
		}
		if (config != null) {
			return config.getTyjxCode();
		}
		return "";
	}

	/**
	 * 获取定位到的天翼景象城市编码 - used for new_road_project,add by liaodl
	 * @param context
	 * @return
	 */
	public String getLocationTyjxCitycode(Context context) {
		CityConfig config = getLocationCityConfig(context);
		if (config != null) {
			return config.getTyjxCode();
		}
		return "";
	}

	/**
	 * 获取天翼景象广告图片url
	 */
	public String getTyjxAdvertImage(Context context) {
		// 内存读取
		if (config == null) {
			config = getCityConfig(context);
		}
		if (config != null) {
			return config.getTyjx_image();
		}
		// 本地读取
		return "";
	}

	/**
	 * 获取天翼景象广告播放时间
	 */
	public String getTyjxAdvertDuration(Context context) {
		// 内存读取
		if (config == null) {
			config = getCityConfig(context);
		}
		if (config != null) {
			return config.getTyjx_duration();
		}
		// 本地读取
		return "";
	}

	public String getMenuVer(Context context, String cityCode) {
		return MenuService.getInstance(context).getMenuVer(cityCode);
	}

	public CityConfig getCityConfig(Context context) {
		if (config == null) {
			String cityCode = MenuUtil.getCityCode(context);
			config = CityConfigService.getInstance(context).load(cityCode);
		}
		return config;
	}

	/**
	 * 获取定位到的城市的CityConfig - add by liaodl
	 * @param context
	 * @return
	 */
	public CityConfig getLocationCityConfig(Context context) {
		String cityCode = LocationUtil.getLocationCityCode(context);
		return CityConfigService.getInstance(context).load(cityCode);
	}

	public void saveCityName(Context context, String cityName) {
		MenuUtil.setCityName(context, cityName);
	}

	public void saveCityCode(Context context, String cityCode) {
		MenuUtil.setCityCode(context, cityCode);
	}

	// 获取一级栏目个数
	public int getFirstMenuSize(Context context) {
		if (!loadSuccess) {
			String cityCode = MenuUtil.getCityCode(context);
			List<MenuItem> list = MenuService.getInstance(context).loadHomeMenu(cityCode);
			if (list != null)
				return list.size();
		}
		return homeMenu.size();
	}

	// 获取热门推荐
	public List<MenuItem> getHotRecommend(Context context) {
		if (hotRecommend == null) {
			hotRecommend = new ArrayList<MenuItem>();
		}
		if (hotRecommend.size() == 0) {
			String cityCode = getCityCode(context);
			hotRecommend.addAll(loadHotRecommend(context, cityCode));
		}
		return hotRecommend;
	}

	// 获取一级菜单
	public List<MenuItem> getFirstMenu(Context context) {
		if (!loadSuccess) {
			String cityCode = MenuUtil.getCityCode(context);
			return MenuService.getInstance(context).loadHomeMenu(cityCode);
		}
		return homeMenu;
	}

	// 获取二级菜单
	public List<MenuItem> getSecondMenu(Context context, String parentMenuId) {
		if (!loadSuccess) {
			return MenuService.getInstance(context).loadMenuByMenuPid(parentMenuId);
		}
		return secondMenu.get(parentMenuId);
	}

	// 获取三级菜单
	public List<MenuItem> getThirdMenu(Context context, String parentMenuId) {
		if (!loadSuccess) {
			return MenuService.getInstance(context).loadMenuByMenuPid(parentMenuId);
		}
		return thirdMenu.get(parentMenuId);
	}

	public List<WidgetInfo> getWidgetInfoCurrent(Context context, String cityCode, boolean clear) {
		if (clear) {
			widgetInfos.clear();
			widgetInfos = null;
			widgetInfos = new ArrayList<WidgetInfo>();
		}

		if (StringUtil.isEmpty(cityCode)) {
			cityCode = getCityCode(context);
		}

		return getWidgetInfoList(context, cityCode);
	}

	public List<WidgetInfo> getWidgetInfoList(Context context, String cityCode) {
		if (widgetInfos.isEmpty()) {
			List<WidgetInfo> list = loadWidgetInfo(context, cityCode);
			if (list != null && !list.isEmpty()) {
				widgetInfos.addAll(list);
			}
		}
		return Collections.unmodifiableList(widgetInfos);
	}

	public WidgetInfo getWidget(Context context, String menuId) {
		if (StringUtil.isEmpty(menuId)) {
			return null;
		}
		String cityCode = getCityCode(context);
		List<WidgetInfo> list = getWidgetInfoCurrent(context, cityCode, false);
		for (WidgetInfo info : list) {
			String widgetMenuId = info.getMenuId();
			if (menuId.equals(widgetMenuId)) {
				return info;
			}
		}
		return null;
	}

	public List<WidgetInfo> loadWidgetInfo(Context context, String cityCode) {
		return MenuService.getInstance(context).queryWidgetInfo(cityCode);
	}

	// 获取单独一个菜单
	public MenuItem getMenu(Context context, String menuId) {
		MenuItem menu = MenuService.getInstance(context).load(menuId);
		if (menu == null) {
			menu = getMenuCache(menuId);
		}
		return menu;
	}

	private MenuItem getMenuCache(String menuId) {

		if (StringUtil.isEmpty(menuId)) {
			return null;
		}

		MenuItem menu = null;
		if (thirdMenu != null && thirdMenu.size() > 0) {
			Collection<List<MenuItem>> collection = thirdMenu.values();
			Iterator<List<MenuItem>> it = collection.iterator();
			for (; it.hasNext();) {
				List<MenuItem> list = it.next();
				if (list != null && list.size() > 0) {
					for (MenuItem item : list) {
						if (menuId.equals(item.getMenuId())) {
							menu = item;
							break;
						}
					}
				}

				if (menu != null) {
					break;
				}
			}
		}
		return menu;
	}

	/**
	 * 得到已经过滤后的一级菜单
	 * 如果一级菜单下的列表为空，则一级菜单也不显示
	 * @param context
	 * @param mMyAppList
	 * @return
	 */
	public List<MenuItem> getHomeMenuList(Context context, List<MenuItem> mMyAppList,
			boolean needLogin) {
		// 存放一级菜单结果集
		List<MenuItem> resultList = new ArrayList<MenuItem>();
		// 得到一级菜单的数据
		List<MenuItem> homeMenuList = getFirstMenu(context);
		for (MenuItem first : homeMenuList) {
			String menuId = first.getMenuId();
			List<MenuItem> thirdUnique = getThirdMenuListUnique(context, menuId, mMyAppList,
					needLogin);
			// 第三级目录不为空，显示第一级菜单，否则第一级菜单不显示
			if (thirdUnique != null && thirdUnique.size() > 0) {
				MenuItem firstItem = getMenu(context, menuId);
				if (firstItem != null) {
					resultList.add(firstItem);
				}
			}
		}
		return resultList;
	}

	/**
	 * 获取已经去除重复的三级菜单
	 * 添加过的数据不再显示
	 * @param context
	 * @param menuId
	 * @param mMyAppList
	 * @return
	 */
	public List<MenuItem> getThirdMenuListUnique(Context context, String menuId,
			List<MenuItem> mMyAppList, boolean needLogin) {
		// 获取所有 三级子菜单
		List<MenuItem> thirdAll = getThirdMenuList(context, menuId, needLogin);
		Map<String, MenuItem> map = new HashMap<String, MenuItem>();
		if (thirdAll != null && thirdAll.size() > 0) {
			for (MenuItem app : thirdAll) {
				String thirdMenuId = app.getMenuId();
				map.put(thirdMenuId, app);
			}
		}
		if (mMyAppList != null) {
			// 移除已经添加过的应用
			for (int i = 0; i < mMyAppList.size(); i++) {
				MenuItem item = mMyAppList.get(i);
				if (item != null) {
					String myAppItem = item.getMenuId();
					if (map.containsKey(myAppItem)) {
						map.remove(myAppItem);
					}
				}

			}
		}

		// 存放过滤后的结果
		List<MenuItem> list = new ArrayList<MenuItem>();
		Iterator<Entry<String, MenuItem>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			MenuItem appItem = (MenuItem) it.next().getValue();
			if (appItem != null) {
				list.add(appItem);
			}
		}
		return list;
	}

	/**
	 * 得到三级菜单的列表
	 * 1、根据是否已经登陆，过滤需要登录的应用
	 * @param context
	 * @param menuId
	 * @param mMyAppList
	 * @param isLogin 
	 * @return
	 */
	public List<MenuItem> getThirdMenuList(Context context, String menuId, boolean isLogin) {
		// 存放第三级菜单的数据
		List<MenuItem> thirdMenuAll = new ArrayList<MenuItem>();
		// 根据第一级菜单得到二级菜单项
		List<MenuItem> secondMenu = getSecondMenu(context, menuId);
		if (secondMenu != null && secondMenu.size() > 0) {
			for (MenuItem item : secondMenu) {
				// 遍历二级菜单项的menuId
				String secondMenuId = item.getMenuId();
				// 得到所有的三级菜单
				List<MenuItem> thirdMenu = getThirdMenu(context, secondMenuId);
				if (thirdMenu != null && thirdMenu.size() > 0) {
					for (MenuItem it : thirdMenu) {
						if (isLogin) {// 已经登陆，获取所有的应用列表
							thirdMenuAll.add(it);
						} else {// 未登录，过滤掉需要登录的应用
							boolean mustLogin = it.isMustLogin();
							if (!mustLogin) {
								thirdMenuAll.add(it);
							}
						}
					}

				}
			}
		}
		return thirdMenuAll;
	}
}
