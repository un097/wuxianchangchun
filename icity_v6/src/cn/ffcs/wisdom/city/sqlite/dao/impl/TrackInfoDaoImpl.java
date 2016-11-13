package cn.ffcs.wisdom.city.sqlite.dao.impl;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import cn.ffcs.wisdom.city.sqlite.DBHelper;
import cn.ffcs.wisdom.city.sqlite.DBManager;
import cn.ffcs.wisdom.city.sqlite.dao.TrackInfoDao;
import cn.ffcs.wisdom.city.sqlite.model.TrackInfo;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.StringUtil;

import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;

/**
 * <p>Title: 最近访问，Dao实现类      </p>
 * <p>Description: 
 * Title: 最近访问，Dao实现类
 * </p>
 * <p>@author: Eric.wsd                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-8-13             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class TrackInfoDaoImpl implements TrackInfoDao {
	private static RuntimeExceptionDao<TrackInfo, Integer> trackInfoDao;
	private DBHelper helper;

	public TrackInfoDaoImpl(Context context) {
		if (trackInfoDao == null) {
			helper = (DBHelper) DBManager.getHelper(context);
			trackInfoDao = helper.getRuntimeExceptionDao(TrackInfo.class);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ffcs.wisdom.city.sqlite.dao.TrackInfoDao#save(cn.ffcs.wisdom.city.
	 * sqlite.model.TrackInfo)
	 */
	@Override
	public void save(TrackInfo trackInfo) {
		if (trackInfo == null || StringUtil.isEmpty(trackInfo.getMenuId())) {
			Log.i("TrackInfo is null or not category app");
			return;
		}

		trackInfoDao.create(trackInfo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ffcs.wisdom.city.sqlite.dao.TrackInfoDao#update(cn.ffcs.wisdom.city
	 * .sqlite.model.TrackInfo)
	 */
	@Override
	public void update(TrackInfo trackInfo) {
		if (trackInfo == null || StringUtil.isEmpty(trackInfo.getMenuId())) {
			Log.i("TrackInfo is null or not category app");
			return;
		}

		TrackInfo info = find(trackInfo);
		if (info != null) {
			delete(info);
			save(trackInfo);
		}
	}
	
	/**
	 * 删除重复的应用
	 * @param trackInfo
	 */
	public void delete(TrackInfo trackInfo) {
		if (trackInfo == null || StringUtil.isEmpty(trackInfo.getMenuId())) {
			Log.i("TrackInfo is null or not category app");
			return;
		}
		
		TrackInfo info = find(trackInfo);
		if (info != null) {
			trackInfoDao.delete(info);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ffcs.wisdom.city.sqlite.dao.TrackInfoDao#saveOrUpdate(cn.ffcs.wisdom
	 * .city.sqlite.model.TrackInfo)
	 */
	@Override
	public void saveOrUpdate(TrackInfo trackInfo) {
		if (trackInfo == null) {
			Log.i("TrackInfo is null");
			return;
		}
		try {
			AndroidDatabaseConnection db = new AndroidDatabaseConnection(
					helper.getWritableDatabase(), true);
			db.setAutoCommit(false);

			if (isExist(trackInfo)) {
				update(trackInfo);
			} else {
				save(trackInfo);
			}

			db.commit(null);
		} catch (SQLException e) {
			Log.e(e + "");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ffcs.wisdom.city.sqlite.dao.TrackInfoDao#findAllByCityCode(java.lang
	 * .String)
	 */
	@Override
	public List<TrackInfo> findAllByCityCode(String cityCode) {
		try {
			QueryBuilder<TrackInfo, Integer> qb = trackInfoDao.queryBuilder();
			qb.where().eq("city_code", cityCode);
			qb.orderBy("id", false);
			qb.limit(20L);
			qb.offset(0L);
			return qb.query();
		} catch (Exception e) {
			Log.e(e + "");
		}

		return Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ffcs.wisdom.city.sqlite.dao.TrackInfoDao#isExist(cn.ffcs.wisdom.city
	 * .sqlite.model.TrackInfo)
	 */
	@Override
	public boolean isExist(TrackInfo trackInfo) {
		if (trackInfo == null) {
			Log.i("TrackInfo is null");
			return false;
		}

		TrackInfo info = find(trackInfo);
		if (info != null)
			return true;

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ffcs.wisdom.city.sqlite.dao.TrackInfoDao#find(cn.ffcs.wisdom.city.
	 * sqlite.model.TrackInfo)
	 */
	@Override
	public TrackInfo find(TrackInfo trackInfo) {
		if (trackInfo == null) {
			Log.i("TrackInfo is null");
			return null;
		}
		try {
			QueryBuilder<TrackInfo, Integer> qb = trackInfoDao.queryBuilder();
			qb.where().eq("menu_id", trackInfo.getMenuId()).or().eq("menu_name", trackInfo.getMenuName());
			return qb.queryForFirst();
		} catch (Exception e) {
			Log.e(e + "");
		}

		return null;
	}

	@Override
	public void clearAll() {
		AndroidDatabaseConnection db = new AndroidDatabaseConnection(helper.getWritableDatabase(),
				true);
		db.setAutoCommit(false);

		String sql = "delete from t_track_info ";
		trackInfoDao.executeRawNoArgs(sql);

		try {
			db.commit(null);
		} catch (SQLException e) {
			Log.e(e + "");
		}
	}

}
