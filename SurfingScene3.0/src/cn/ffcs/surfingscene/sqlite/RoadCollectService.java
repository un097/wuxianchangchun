package cn.ffcs.surfingscene.sqlite;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.StringUtil;

import com.ffcs.surfingscene.entity.GlobalEyeEntity;
import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.umeng.analytics.MobclickAgent;

public class RoadCollectService {
	private static RoadCollectService mInstance;
	private static RuntimeExceptionDao<RoadCollect, Integer> mDao;
	private Context mContext;

	static final Object sInstanceSync = new Object();
	private DBHelper helper;

	private RoadCollectService(Context context) {
		mContext = context;
		if (mDao == null) {
			helper = new DBHelper(context);
			mDao = helper.getRuntimeExceptionDao(RoadCollect.class);
		}
	}

	public static RoadCollectService getInstance(Context context) {
		if (mInstance == null)
			mInstance = new RoadCollectService(context);
		return mInstance;
	}

	/**
	 * 判断是否收藏
	 * @param userId
	 * @param geyeId
	 * @return
	 */
	public boolean isCollect(String userId, Integer geyeId) {
		if(StringUtil.isEmpty(userId)){
			userId="0";
		}
		String sql = "select * from t_road_collect where geye_id = " + geyeId + " and user_id = '"
				+ userId + "' limit 1";
		GenericRawResults<RoadCollect> results = mDao.queryRaw(sql, mDao.getRawRowMapper());
		if (results != null) {
			try {
				List<RoadCollect> list = results.getResults();
				if (list != null && list.size() == 1) {
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * 获取用户收藏
	 * @param userId
	 * @return
	 */
	public List<RoadCollect> getCollect(String userId) {
		if(StringUtil.isEmpty(userId)){
			userId="0";
		}
		List<RoadCollect> list = null;
		String sql = "select * from t_road_collect where user_id = '" + userId + "' order by id desc";
		GenericRawResults<RoadCollect> results = mDao.queryRaw(sql, mDao.getRawRowMapper());
		if (results != null) {
			try {
				list = results.getResults();
			} catch (Exception e) {
				Log.e(e.getMessage(), e);
			}
		}
		if (list != null) {
			return list;
		} else {
			return Collections.emptyList();
		}
	}

	/**
	 * 保存收藏列表
	 * @param userId
	 * @param list
	 */
	public void saveCollectList(String userId, List<GlobalEyeEntity> list) {
		if(StringUtil.isEmpty(userId)){
			userId="0";
		}
		if (list != null) {
			AndroidDatabaseConnection db = new AndroidDatabaseConnection(
					helper.getWritableDatabase(), true);
			db.setAutoCommit(false);

			deleteByUserId(userId);// 先删除

			for (int i = 0; i < list.size(); i++) {
				mDao.create(RoadCollect.converVideoEntity(userId, list.get(i)));
			}

			try {
				db.commit(null);
			} catch (SQLException e) {
				Log.e(e + "");
			}
		}
	}

	/**
	 * 删除当前用户收藏
	 * @param userId
	 */
	public void deleteByUserId(String userId) {
		if(StringUtil.isEmpty(userId)){
			userId="0";
		}
		String sql = "delete from t_road_collect where user_id='" + userId + "'";
		mDao.executeRawNoArgs(sql);
	}

	/**
	 * 取消收藏
	 * @param userId
	 * @param geyeId
	 */
	public void unCollect(String userId, Integer geyeId) {
		if(StringUtil.isEmpty(userId)){
			userId="0";
		}
		String sql = "delete from t_road_collect where user_id='" + userId + "' and geye_id="
				+ geyeId;
		mDao.executeRawNoArgs(sql);
	}

	/**
	 * 单个收藏
	 * @param userId
	 * @param entity
	 */
	public void saveCollect(String userId, GlobalEyeEntity entity) {
		if(StringUtil.isEmpty(userId)){
			userId="0";
		}
		MobclickAgent.onEvent(mContext, "E_C_trafficVideo_collectVideoClick");
		mDao.create(RoadCollect.converVideoEntity(userId, entity));
	}
}
