package cn.ffcs.wisdom.city.sqlite.dao.impl;

import java.sql.SQLException;

import android.content.Context;
import cn.ffcs.wisdom.city.sqlite.DBHelper;
import cn.ffcs.wisdom.city.sqlite.DBManager;
import cn.ffcs.wisdom.city.sqlite.dao.RedPotInfoDao;
import cn.ffcs.wisdom.city.sqlite.model.RedPotInfo;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.StringUtil;

import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;

/**
 * <p>Title: 小红点，Dao实现类      </p>
 * <p>Description: 
 * Title: 小红点，Dao实现类
 * </p>
 * <p>@author: Eric.wsd                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2014-3-11             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class RedPotInfoDaoImpl implements RedPotInfoDao {
	private static RuntimeExceptionDao<RedPotInfo, Integer> RedPotInfoDao;
	private DBHelper helper;

	public RedPotInfoDaoImpl(Context context) {
		if (RedPotInfoDao == null) {
			helper = (DBHelper) DBManager.getHelper(context);
			RedPotInfoDao = helper.getRuntimeExceptionDao(RedPotInfo.class);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ffcs.wisdom.city.sqlite.dao.RedPotInfoDao#save(cn.ffcs.wisdom.city.
	 * sqlite.model.RedPotInfo)
	 */
	@Override
	public void save(RedPotInfo redPotInfo) {
		if (redPotInfo == null || StringUtil.isEmpty(redPotInfo.getMenuId())) {
			Log.i("RedPotInfo is null or not category app");
			return;
		}

		RedPotInfoDao.create(redPotInfo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ffcs.wisdom.city.sqlite.dao.RedPotInfoDao#update(cn.ffcs.wisdom.city
	 * .sqlite.model.RedPotInfo)
	 */
	@Override
	public void update(RedPotInfo redPotInfo) {
		if (redPotInfo == null || StringUtil.isEmpty(redPotInfo.getMenuId())) {
			Log.i("RedPotInfo is null or not category app");
			return;
		}

		RedPotInfo info = find(redPotInfo.getMenuId());
		if (info != null) {
			redPotInfo.setId(info.getId());
			RedPotInfoDao.update(redPotInfo);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ffcs.wisdom.city.sqlite.dao.RedPotInfoDao#saveOrUpdate(cn.ffcs.wisdom
	 * .city.sqlite.model.RedPotInfo)
	 */
	@Override
	public void saveOrUpdate(RedPotInfo RedPotInfo) {
		if (RedPotInfo == null) {
			Log.i("RedPotInfo is null");
			return;
		}
		try {
			AndroidDatabaseConnection db = new AndroidDatabaseConnection(
					helper.getWritableDatabase(), true);
			db.setAutoCommit(false);

			if (isExist(RedPotInfo)) {
				update(RedPotInfo);
			} else {
				save(RedPotInfo);
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
	 * cn.ffcs.wisdom.city.sqlite.dao.RedPotInfoDao#isExist(cn.ffcs.wisdom.city
	 * .sqlite.model.RedPotInfo)
	 */
	@Override
	public boolean isExist(RedPotInfo redPotInfo) {
		if (redPotInfo == null) {
			Log.i("RedPotInfo is null");
			return false;
		}

		RedPotInfo info = find(redPotInfo.getMenuId());
		if (info != null)
			return true;

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ffcs.wisdom.city.sqlite.dao.RedPotInfoDao#find(cn.ffcs.wisdom.city.
	 * sqlite.model.RedPotInfo)
	 */
	@Override
	public RedPotInfo find(String menuId) {
		if (menuId == null) {
			Log.i("RedPotInfo is null");
			return null;
		}
		try {
			QueryBuilder<RedPotInfo, Integer> qb = RedPotInfoDao.queryBuilder();
			qb.where().eq("menu_id", menuId);
			return qb.queryForFirst();
		} catch (Exception e) {
			Log.e(e + "");
		}

		return null;
	}
}
