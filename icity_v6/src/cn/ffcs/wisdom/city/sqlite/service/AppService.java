package cn.ffcs.wisdom.city.sqlite.service;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import cn.ffcs.wisdom.city.sqlite.DBHelper;
import cn.ffcs.wisdom.city.sqlite.DBManager;
import cn.ffcs.wisdom.city.sqlite.model.AppItem;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.StringUtil;

import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;

/**
 * <p>
 * Title: 操作我的应用列表
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * 
 * @author: Leo
 *          </p>
 *          <p>
 *          Copyright: Copyright (c) 2012
 *          </p>
 *          <p>
 *          Company: FFCS Co., Ltd.
 *          </p>
 *          <p>
 *          Create Time: 2013-3-12
 *          </p>
 *          <p>
 *          Update Time:
 *          </p>
 *          <p>
 *          Updater:
 *          </p>
 *          <p>
 *          Update Comments:
 *          </p>
 */
public class AppService {
	public static AppService appService;

	private static RuntimeExceptionDao<AppItem, Integer> appDao;

	static final Object sInstanceSync = new Object();
	private DBHelper helper ;

	private AppService(Context ctx) {
		if (appDao == null) {
			helper = DBManager.getHelper(ctx);
			appDao = helper.getRuntimeExceptionDao(AppItem.class);
		}
	}

	public static AppService getInstance(Context ctx) {
		synchronized (sInstanceSync) {
			if (appService == null) {
				appService = new AppService(ctx);
			}
		}
		return appService;
	}

	/**
	 * 批量保存我的应用菜单
	 * 
	 * @param menus
	 */
	public void saveMenus(List<AppItem> menus) {
		if (menus != null) {
			for (AppItem m : menus) {
				appDao.create(m);
			}
		}
	}
	
	/**
	 * 保存我的应用
	 * @param item
	 */
	public void save(AppItem item) {
		if(item == null) {
			Log.e("AppItem is null");
			return;
		}
		
		appDao.create(item);
	}

	/**
	 * 通过城市编号获取我的应用列表
	 * 
	 * @param menuId
	 * @return
	 */
	public List<AppItem> queryByCityCode(String cityCode) {
		String sql = "select * from t_myapp where city_code=" + cityCode
				+ " order by menu_order";
		GenericRawResults<AppItem> results = appDao.queryRaw(sql,
				appDao.getRawRowMapper());
		if (results != null) {
			try {
				return results.getResults();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return Collections.emptyList();
	}

	/**
	 * 通过城市编号获取我的应用列表
	 * 
	 * @param menuId
	 * @return
	 */
	public List<AppItem> queryByCityCodeMenuId(String cityCode, String menuId) {
		String sql = "select * from t_myapp where city_code=" + cityCode
				+ " and menu_id=" + menuId + " order by menu_order";
		GenericRawResults<AppItem> results = appDao.queryRaw(sql,
				appDao.getRawRowMapper());
		if (results != null) {
			try {
				return results.getResults();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return Collections.emptyList();
	}

	/**
	 * 批量删除数据
	 * 
	 * @param list
	 */
	public void delete(List<AppItem> list) {
		try {
			if (list != null) {
				appDao.delete(list);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 删除单个数据
	 * @param item
	 */
	public void delete(AppItem item) {
		if(item == null) {
			Log.e("AppItem is null");
			return;
		}
		
		AppItem appItem = find(item.getMenuId());
		if(appItem != null) {
			appDao.delete(appItem);
		}
	}

	/**
	 * 清空本地数据
	 * 
	 * @param item
	 * @param cityCode
	 */
	public void delete(List<AppItem> item, String cityCode) {
		String sql = "select * from t_menu where city_code=" + cityCode;
		GenericRawResults<AppItem> results = appDao.queryRaw(sql,
				appDao.getRawRowMapper());

		if (results != null) {
			try {
				List<AppItem> menus = results.getResults();
				if (menus != null)
					appDao.delete(menus);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 查找相关menuId的应用
	 * @param menuId
	 * @return
	 */
	public AppItem find(String menuId) {
		if(!StringUtil.isEmpty(menuId)) {
			QueryBuilder<AppItem, Integer> qb = appDao.queryBuilder();
			try {
				qb.where().eq("menu_id", menuId);
				AppItem app = qb.queryForFirst();
				return app;
			} catch (SQLException e) {
				Log.e(e + "");
			}

			return null;
		}
		
		return null;
	}

	/**
	 * 判断是否存在该收藏信息
	 * @param item
	 * @return
	 */
	public boolean isExist(AppItem item) {
		if(item == null){
			Log.i("AppItem is null");
			return false;
		}
			
		QueryBuilder<AppItem, Integer> qb = appDao.queryBuilder();
		try {
			qb.where().eq("menu_id", item.getMenuId()).or().eq("menu_name", item.getMenuName());
			AppItem app = qb.queryForFirst();
			if (app != null)
				return true;
			else
				return false;
		} catch (SQLException e) {
			Log.e(e + "");
		}

		return false;
	}
	
	/**
	 * 判断是否存在该收藏信息
	 * @param itemId
	 * @return
	 */
	public boolean isExist(String itemId) {
		if(itemId == null){
			Log.i("itemId is null");
			return false;
		}
			
		QueryBuilder<AppItem, Integer> qb = appDao.queryBuilder();
		try {
			qb.where().eq("menu_id", itemId);
			AppItem app = qb.queryForFirst();
			if (app != null)
				return true;
			else
				return false;
		} catch (SQLException e) {
			Log.e(e + "");
		}

		return false;
	}
	
	/**
	 * 删除所有的本地记录
	 */
	public void clearAllCache() {
		AndroidDatabaseConnection db = new AndroidDatabaseConnection(helper.getWritableDatabase(),
				true);
		db.setAutoCommit(false);

		String sql = "delete from t_myapp ";
		appDao.executeRawNoArgs(sql);

		try {
			db.commit(null);
		} catch (SQLException e) {
			Log.e(e + "");
		}
	}

	/**
	 * 根据itemID删除收藏
	 * @param itemId
	 */
	public void deleteByItemId(String itemId) {
		String sql = "delete from t_myapp where menu_id = '" + itemId +"'";
		appDao.executeRawNoArgs(sql);
	}
}
