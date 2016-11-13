package cn.ffcs.wisdom.city.sqlite.service;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import cn.ffcs.wisdom.city.sqlite.DBHelper;
import cn.ffcs.wisdom.city.sqlite.DBManager;
import cn.ffcs.wisdom.city.sqlite.model.Report;

import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RuntimeExceptionDao;

public class ReportService {

	public static ReportService mService;

	private static RuntimeExceptionDao<Report, Integer> mDao;

	static final Object sInstanceSync = new Object();

	private ReportService(Context ctx) {
		if (mDao == null) {
			DBHelper helper = DBManager.getHelper(ctx);
			mDao = helper.getRuntimeExceptionDao(Report.class);
		}
	}

	public static ReportService getInstance(Context ctx) {
		synchronized (sInstanceSync) {
			if (mService == null) {
				mService = new ReportService(ctx);
			}
		}
		return mService;
	}

	public void save(Context context, Report rp) {
		mDao.create(rp);
	}

	public List<Report> query() {
		String sql = "select * from t_report";
		GenericRawResults<Report> results = mDao.queryRaw(sql, mDao.getRawRowMapper());
		if (results != null) {
			try {
				return results.getResults();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return Collections.emptyList();
	}

	public void delete(Report rp) {
		if (rp != null) {
			mDao.delete(rp);
		}
	}

}
