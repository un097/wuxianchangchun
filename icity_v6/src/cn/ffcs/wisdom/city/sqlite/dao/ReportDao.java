package cn.ffcs.wisdom.city.sqlite.dao;

import android.content.Context;
import cn.ffcs.wisdom.city.sqlite.DBManager;
import cn.ffcs.wisdom.city.sqlite.model.Report;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;

public class ReportDao extends RuntimeExceptionDao<Report, Integer> {
	private static ReportDao dao;
	static Object syncObject = new Object();

	public static ReportDao newInstance(Context ctx) {
		synchronized (syncObject) {
			if (dao == null) {
				dao = DBManager.getHelper(ctx).getRuntimeExceptionDao(Report.class);
			}

			return dao;
		}
	}

	private ReportDao(Dao<Report, Integer> dao) {
		super(dao);
	}
}
