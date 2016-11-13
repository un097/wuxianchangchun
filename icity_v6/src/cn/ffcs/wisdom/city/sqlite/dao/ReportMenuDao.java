package cn.ffcs.wisdom.city.sqlite.dao;

import android.content.Context;
import cn.ffcs.wisdom.city.sqlite.DBManager;
import cn.ffcs.wisdom.city.sqlite.model.ReportMenu;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;

public class ReportMenuDao extends RuntimeExceptionDao<ReportMenu, Integer> {
	private static ReportMenuDao dao;
	static Object syncObject = new Object();

	public static ReportMenuDao newInstance(Context ctx) {
		synchronized (syncObject) {
			if (dao == null) {
				dao = DBManager.getHelper(ctx).getRuntimeExceptionDao(ReportMenu.class);
			}

			return dao;
		}
	}

	private ReportMenuDao(Dao<ReportMenu, Integer> dao) {
		super(dao);
	}
}
