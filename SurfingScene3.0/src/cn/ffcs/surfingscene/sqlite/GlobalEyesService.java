package cn.ffcs.surfingscene.sqlite;

import java.util.Collections;
import java.util.List;

import android.content.Context;

import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RuntimeExceptionDao;

public class GlobalEyesService {

	private static GlobalEyesService mInstance;
	private static RuntimeExceptionDao<GlobalEye, Integer> mDao;

	static final Object sInstanceSync = new Object();
	private DBHelper helper;

	private GlobalEyesService(Context context) {
		if (mDao == null) {
			helper = new DBHelper(context);
			mDao = helper.getRuntimeExceptionDao(GlobalEye.class);
		}
	}

	public static GlobalEyesService getInstance(Context context) {
		if (mInstance == null)
			mInstance = new GlobalEyesService(context);
		return mInstance;
	}

	public boolean isFavorite(Integer geyeId) {
		String sql = "select * from t_globaleye where geye_id = " + geyeId + " and favorite = 0";
		GenericRawResults<GlobalEye> results = mDao.queryRaw(sql, mDao.getRawRowMapper());
		if (results != null) {
			try {
				List<GlobalEye> list = results.getResults();
				if (list != null && list.size() == 1) {
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public void doFavorite(GlobalEye eye, Integer favorite) {
		if (eye == null) {
			return;
		}

		GlobalEye data = load(eye.getGeyeId());
		if (data != null) { // 更新
			data.setFavorite(favorite);
			mDao.update(data);
		} else {
			eye.setFavorite(favorite);
			mDao.create(eye);
		}
	}

	public GlobalEye load(Integer geyeId) {
		String sql = "select * from t_globaleye where geye_id = " + geyeId;
		GenericRawResults<GlobalEye> results = mDao.queryRaw(sql, mDao.getRawRowMapper());
		if (results != null) {
			try {
				List<GlobalEye> list = results.getResults();
				if (list != null && list.size() == 1) {
					return list.get(0);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 根据天翼景象code查询收藏视频
	 * @param tyjxCode
	 * @return
	 */
	public List<GlobalEye> queryFavoriteTyjxCode(String tyjxCode) {
		String sql = "select * from t_globaleye where tyjx_code = " + tyjxCode
				+ " and favorite = 0";
		GenericRawResults<GlobalEye> results = mDao.queryRaw(sql, mDao.getRawRowMapper());
		if (results != null) {
			try {
				List<GlobalEye> list = results.getResults();
				if (list != null && list.size() > 0) {
					return list;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return Collections.emptyList();
	}

	public List<GlobalEye> queryFavorite() {
		String sql = "select * from t_globaleye where favorite = 0";
		GenericRawResults<GlobalEye> results = mDao.queryRaw(sql, mDao.getRawRowMapper());
		if (results != null) {
			try {
				List<GlobalEye> list = results.getResults();
				if (list != null && list.size() > 0) {
					return list;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return Collections.emptyList();
	}

}
