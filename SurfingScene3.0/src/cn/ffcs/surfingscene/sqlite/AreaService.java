package cn.ffcs.surfingscene.sqlite;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import cn.ffcs.wisdom.tools.Log;

import com.ffcs.surfingscene.entity.AreaEntity;
import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RuntimeExceptionDao;

public class AreaService {
	private static AreaService mInstance;
	private static RuntimeExceptionDao<AreaList, Integer> mDao;

	static final Object sInstanceSync = new Object();
	private DBHelper helper;

	private AreaService(Context context) {
		if (mDao == null) {
			helper = new DBHelper(context);
			mDao = helper.getRuntimeExceptionDao(AreaList.class);
		}
	}

	public static AreaService getInstance(Context context) {
		if (mInstance == null)
			mInstance = new AreaService(context);
		return mInstance;
	}

	public void SaveCityList(List<AreaEntity> list) {
		if (list != null) {
			AndroidDatabaseConnection db = new AndroidDatabaseConnection(
					helper.getWritableDatabase(), true);
			db.setAutoCommit(false);
			int size = list.size();
			for (int i = 0; i < size; i++) {
				mDao.create(AreaList.converEntity(list.get(i)));
			}
			try {
				db.commit(null);
			} catch (SQLException e) {
				Log.e(e.getMessage(), e);
			}
		}
	}

	/**
	 * 获取省份
	 * @return
	 */
	public List<AreaList> getProList() {
		String sql = "select * from t_areaList where area_type='1' order by area_order asc";
		return getAreaList(sql);
	}

	/**
	 * 获取单一区域对象
	 * @param areaCode
	 * @return
	 */
	public AreaList getAreaListEntity(String areaCode) {
		String sql = "select * from t_areaList where area_code='" + areaCode + "' limit 1";
		List<AreaList> listEntity = getAreaList(sql);

		if (listEntity.size() > 0) {
			return listEntity.get(0);
		} else {
			return null;
		}
	}

	/**
	 * 获取城市和区域
	 * @param areaCode
	 * @return
	 */
	public List<AreaList> getCityList(String areaCode) {
		String sql = "select * from t_areaList where area_parent='" + areaCode
				+ "' order by area_order asc";
		return getAreaList(sql);
	}

	/**
	 * 获取实体列表
	 * @param sql
	 * @return
	 */
	private List<AreaList> getAreaList(String sql) {
		GenericRawResults<AreaList> results = mDao.queryRaw(sql, mDao.getRawRowMapper());
		if (results != null) {
			try {
				return results.getResults();
			} catch (SQLException e) {
				Log.e(e.getMessage(), e);
			}
		}
		return Collections.emptyList();
	}

	/**
	 * 获取父实体
	 * @param areaCode
	 * @return
	 */
	public AreaList getParentAreaEntity(String areaCode) {
		return getAreaListEntity(getAreaListEntity(areaCode).areaParent);
	}

	/**
	 * 删除全部数据
	 */
	public void deleteAll() {
		String sql = "delete from t_areaList";
		mDao.executeRawNoArgs(sql);
	}
}
