package cn.ffcs.wisdom.city.sqlite.service;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import cn.ffcs.wisdom.city.sqlite.DBHelper;
import cn.ffcs.wisdom.city.sqlite.DBManager;
import cn.ffcs.wisdom.city.sqlite.model.ReportMenu;

import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RuntimeExceptionDao;

public class ReportMenuService {

	public static ReportMenuService mService;

	private static RuntimeExceptionDao<ReportMenu, Integer> mDao;

	static final Object sInstanceSync = new Object();

	private ReportMenuService(Context ctx) {
		if (mDao == null) {
			DBHelper helper = DBManager.getHelper(ctx);
			mDao = helper.getRuntimeExceptionDao(ReportMenu.class);
		}
	}

	public static ReportMenuService getInstance(Context ctx) {
		synchronized (sInstanceSync) {
			if (mService == null) {
				mService = new ReportMenuService(ctx);
			}
		}
		return mService;
	}

	public void save(ReportMenu rp) {
		mDao.create(rp);
	}

	public List<ReportMenu> query() {
		String sql = "select * from t_report_menu";
		GenericRawResults<ReportMenu> results = mDao.queryRaw(sql, mDao.getRawRowMapper());
		if (results != null) {
			try {
				return results.getResults();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return Collections.emptyList();
	}
	
	public long getCount() {
		return mDao.countOf();
	}

	public void delete(ReportMenu rp) {
		if (rp != null) {
			mDao.delete(rp);
		}
	}
	
	public void deleteAll() {
		String sql = "delete from t_report_menu";
		mDao.executeRaw(sql);
	}

}
