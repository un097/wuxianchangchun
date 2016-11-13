package cn.ffcs.wisdom.city.sqlite.service;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import cn.ffcs.wisdom.city.sqlite.DBHelper;
import cn.ffcs.wisdom.city.sqlite.DBManager;
import cn.ffcs.wisdom.city.sqlite.model.MenuItem;
import cn.ffcs.wisdom.city.sqlite.model.WidgetInfo;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.StringUtil;

import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RuntimeExceptionDao;

public class MenuService {

	private static MenuService menuService;
	private static RuntimeExceptionDao<MenuItem, Integer> menuDao;
	private static RuntimeExceptionDao<WidgetInfo, Integer> widgetDao;

	static final Object sInstanceSync = new Object();
	private DBHelper helper ;

	private MenuService(Context ctx) {
		if (menuDao == null || widgetDao == null) {
			helper = DBManager.getHelper(ctx);
			menuDao = helper.getRuntimeExceptionDao(MenuItem.class);
			widgetDao = helper.getRuntimeExceptionDao(WidgetInfo.class);
		}
	}

	public static MenuService getInstance(Context ctx) {
		synchronized (sInstanceSync) {
			if (menuService == null) {
				menuService = new MenuService(ctx);
			}
		}
		return menuService;
	}

	public void saveMenus(List<MenuItem> menus) {
		if (menus != null) {
			AndroidDatabaseConnection db = new AndroidDatabaseConnection(helper.getWritableDatabase(), true); 
            db.setAutoCommit(false); 
            
			for (MenuItem m : menus) {
				// MenuItem defMenu = load(m.getMenuId());
				// if (defMenu != null) {
				// m.setId(defMenu.getId());
				// menuDao.update(m);
				// } else {
				//
				// }
				menuDao.create(m);
			}
			
			try {
				db.commit(null);
			} catch (SQLException e) {
				Log.e(e + "");
			}
		}
	}

	public List<MenuItem> query(String cityCode) {
		if (StringUtil.isEmpty(cityCode)) {
			return Collections.emptyList();
		}
		String sql = "select * from t_menu where city_code = " + cityCode
				+ " order by menu_order asc";
		GenericRawResults<MenuItem> results = menuDao.queryRaw(sql, menuDao.getRawRowMapper());
		if (results != null) {
			try {
				return results.getResults();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return Collections.emptyList();
	}

	public MenuItem load(String menuId) {
		String sql = "select * from t_menu where menu_id = " + menuId;
		GenericRawResults<MenuItem> results = menuDao.queryRaw(sql, menuDao.getRawRowMapper());
		if (results != null) {
			try {
				List<MenuItem> list = results.getResults();
				if (list != null && list.size() == 1) {
					return list.get(0);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public List<MenuItem> loadHomeMenu(String cityCode) {
		String sql = "select * from t_menu where menu_parent_id = 0 and city_code = " + cityCode
				+ " order by menu_order asc";
		GenericRawResults<MenuItem> results = menuDao.queryRaw(sql, menuDao.getRawRowMapper());
		if (results != null) {
			try {
				return results.getResults();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return Collections.emptyList();
	}

	public List<MenuItem> loadMenuByMenuPid(String menuPid) {
		String sql = "select * from t_menu where menu_parent_id = " + menuPid
				+ " order by menu_order asc";
		GenericRawResults<MenuItem> results = menuDao.queryRaw(sql, menuDao.getRawRowMapper());
		if (results != null) {
			try {
				return results.getResults();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return Collections.emptyList();
	}

	public List<MenuItem> queryByCityCode(String cityCode) {
		String sql = "select * from t_menu where city_code=" + cityCode
				+ " order by menu_order asc";
		GenericRawResults<MenuItem> results = menuDao.queryRaw(sql, menuDao.getRawRowMapper());
		if (results != null) {
			try {
				return results.getResults();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return Collections.emptyList();
	}

	public void delete(String cityCode) throws SQLException {
		String sql = "select * from t_menu where city_code=" + cityCode;
		GenericRawResults<MenuItem> results = menuDao.queryRaw(sql, menuDao.getRawRowMapper());
		if (results != null) {
			List<MenuItem> menus = results.getResults();
			if (menus != null)
				menuDao.delete(menus);
		}
	}

	public String getMenuVer(String cityCode) {
		String menuVer = "0";
		if (!StringUtil.isEmpty(cityCode)) {
			String sql = "select * from t_menu where city_code = " + cityCode
					+ " and menu_parent_id != 0 and recommend != 1 limit 1 ";
			GenericRawResults<MenuItem> results = menuDao.queryRaw(sql, menuDao.getRawRowMapper());

			if (results != null) {
				try {
					List<MenuItem> menus = results.getResults();
					if (menus != null && menus.size() == 1) {
						menuVer = menus.get(0).getMenuVer();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return menuVer;
	}

	public List<MenuItem> queryHotRecommond(String cityCode) {
		String sql = "select * from t_menu where city_code = " + cityCode
				+ " and recommend = 1 order by recommend_order asc";
		GenericRawResults<MenuItem> results = menuDao.queryRaw(sql, menuDao.getRawRowMapper());
		if (results != null) {
			try {
				return results.getResults();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return Collections.emptyList();
	}

	public void saveWdigetInfo(List<WidgetInfo> widgetInfo, String cityCode) {
		if (widgetInfo != null) {
			for (WidgetInfo m : widgetInfo) {
				m.setCityCode(cityCode);
				widgetDao.create(m);
			}
		}
	}

	public List<WidgetInfo> queryWidgetInfo(String cityCode) {
		String sql = "select * from t_widget_info where city_code = " + cityCode;
		GenericRawResults<WidgetInfo> results = menuDao.queryRaw(sql, widgetDao.getRawRowMapper());
		if (results != null) {
			try {
				return results.getResults();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return Collections.emptyList();
	}

	public void deleteWidget(String cityCode) {
		String sql = "select * from t_widget_info where city_code=" + cityCode;
		GenericRawResults<WidgetInfo> results = widgetDao
				.queryRaw(sql, widgetDao.getRawRowMapper());

		if (results != null) {
			try {
				List<WidgetInfo> widgets = results.getResults();
				if (widgets != null)
					widgetDao.delete(widgets);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
