package cn.ffcs.wisdom.city.datamgr;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import cn.ffcs.wisdom.base.DataManager;
import cn.ffcs.wisdom.city.sqlite.model.AppItem;
import cn.ffcs.wisdom.city.sqlite.model.MenuItem;
import cn.ffcs.wisdom.city.sqlite.service.AppService;

/**
 * <p>Title:操作我的应用          </p>
 * <p>Description: 
 * </p>
 * <p>@author: Leo                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-3-12             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class AppMgr extends DataManager {
	private static AppMgr mInstance = new AppMgr();

	static final Object sInstanceSync = new Object();

	private AppMgr() {
	}

	public static AppMgr getInstance() {
		synchronized (sInstanceSync) {
			if (mInstance == null)
				mInstance = new AppMgr();
		}

		return mInstance;
	}

	/**
	 * 批量添加我的应用
	 * @param context
	 * @param item
	 */
	public void saveApp(Context context, List<AppItem> item) {
		AppService appService = AppService.getInstance(context);
		appService.saveMenus(item);
	}

	/**
	 * 添加但一个我的应用
	 * @param context
	 * @param item
	 */
	public void saveApp(Context context, AppItem item) {
		AppService.getInstance(context).save(item);
	}

	/**
	 * 查询应用列表
	 * @param context
	 * @param cityCode
	 * @return
	 */
	public List<AppItem> queryApp(Context context, String cityCode) {
		AppService appService = AppService.getInstance(context);
		List<AppItem> item = appService.queryByCityCode(cityCode);
		return item;
	}

	/**
	 * 根据menuId查询
	 * @param context
	 * @param cityCode
	 * @param menuId
	 * @return
	 */
	public List<AppItem> queryAppByMenuId(Context context, String cityCode, String menuId) {
		AppService appService = AppService.getInstance(context);
		List<AppItem> item = appService.queryByCityCodeMenuId(cityCode, menuId);
		return item;
	}

	/**
	 * 集合对象转化
	 * @param appList
	 * @return
	 */
	public List<MenuItem> convertToMenuList(List<AppItem> appList) {
		List<MenuItem> menuList = new ArrayList<MenuItem>();
		if (appList != null) {
			for (AppItem app : appList) {
				if (app != null) {
					MenuItem menu = convertToMenuItem(app);
					menuList.add(menu);
				}
			}
		}
		return menuList;
	}

	/**
	 * 集合对象转化
	 * @param appList
	 * @return
	 */
	public List<AppItem> convertToAppList(List<MenuItem> menuList) {
		List<AppItem> appList = new ArrayList<AppItem>();
		if (menuList != null) {
			for (MenuItem app : menuList) {
				if (app != null) {
					AppItem menu = convertToAppItem(app);
					appList.add(menu);
				}
			}
		}
		return appList;
	}

	/**
	 * 转换成Menu
	 * @param app
	 * @return
	 */
	public AppItem convertToAppItem(MenuItem app) {
		if (app != null) {
			AppItem menu = new AppItem();
			menu.setCityCode(app.getCityCode());
			menu.setMenuId(app.getMenuId());
			menu.setMenuName(app.getMenuName());
			menu.setIcon(app.getIcon());
			menu.setMenuType(app.getMenuType());
			menu.setMenuDesc(app.getMenudesc());
			menu.setMustLogin(app.isMustLogin());
			menu.setPackage_(app.getPackage_());
			menu.setMain(app.getMain());
			menu.setUrl(app.getUrl());
			menu.setAppUrl(app.getAppUrl());
			menu.setMenuPid(app.getMenuPid());
			menu.setIsApp(app.getIsApp());
			menu.setBaseLine(app.getBaseLine());
			menu.setAppsize(app.getAppsize());
			menu.setIsNew(app.getIsNew());
			menu.setMap(app.getMap());
			menu.setV6Icon(app.getV6Icon());
			menu.setMenuVer(app.getMenuVer());
			menu.setRecommend(app.getRecommend());
			return menu;
		}
		return null;
	}

	public MenuItem convertToMenuItem(AppItem app) {
		if (app != null) {
			MenuItem menu = new MenuItem();
			menu.setCityCode(app.getCityCode());
			menu.setMenuId(app.getMenuId());
			menu.setMenuName(app.getMenuName());
			menu.setIcon(app.getIcon());
			menu.setMenuType(app.getMenuType());
			menu.setMenudesc(app.getMenuDesc());
			menu.setMustLogin(app.isMustLogin());
			menu.setPackage_(app.getPackage_());
			menu.setMain(app.getMain());
			menu.setUrl(app.getUrl());
			menu.setAppUrl(app.getAppUrl());
			menu.setMenuPid(app.getMenuPid());
			menu.setIsApp(app.getIsApp());
			menu.setBaseLine(app.getBaseLine());
			menu.setAppsize(app.getAppsize());
			menu.setIsNew(app.getIsNew());
			menu.setMap(app.getMap());
			menu.setV6Icon(app.getV6Icon());
			menu.setMenuVer(app.getMenuVer());
			menu.setRecommend(app.getRecommend());
			return menu;
		}
		return null;
	}

	/**
	 * 批量删除应用
	 * @param context
	 * @param item
	 */
	public void deleteApp(Context context, List<AppItem> item) {
		AppService appService = AppService.getInstance(context);
		appService.delete(item);
	}

	/**
	 * 删除单个应用
	 * @param context
	 * @param item
	 */
	public void deleteApp(Context context, AppItem item) {
		AppService.getInstance(context).delete(item);
	}

	/**
	 * 根据栏目ID删除收藏
	 * @param context
	 * @param itemId
	 */
	public void deleteByItemId(Context context, String itemId) {
		AppService.getInstance(context).deleteByItemId(itemId);
	}

	/**
	 * 删除所有的我的应用记录
	 * @param context
	 */
	public void clearAllApp(Context context) {
		AppService.getInstance(context).clearAllCache();
	}

	/**
	 * 根据itemId判断是否存在
	 * @param context
	 * @param itemId
	 * @return
	 */
	public boolean isExist(Context context, String itemId) {
		return AppService.getInstance(context).isExist(itemId);
	}
}
