package cn.ffcs.wisdom.city.sqlite.service;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import cn.ffcs.wisdom.city.sqlite.DBHelper;
import cn.ffcs.wisdom.city.sqlite.DBManager;
import cn.ffcs.wisdom.city.sqlite.model.LogItem;

import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RuntimeExceptionDao;

/**
 * <p>Title: 日志操作服务类         </p>
 * <p>Description: 
 * </p>
 * <p>@author: Leo                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-9-2             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class LogReportService {
	public static LogReportService logReportService;

	private static RuntimeExceptionDao<LogItem, Integer> logDao;

	static final Object sInstanceSync = new Object();
	private DBHelper helper;

	private LogReportService(Context ctx) {
		if (logDao == null) {
			helper = DBManager.getHelper(ctx);
			logDao = helper.getRuntimeExceptionDao(LogItem.class);
		}
	}

	public static LogReportService getInstance(Context ctx) {
		synchronized (sInstanceSync) {
			if (logReportService == null) {
				logReportService = new LogReportService(ctx);
			}
		}
		return logReportService;
	}

	/**
	 * 批量保存日志
	 * @param menus
	 */
	public void addLogAll(List<LogItem> logs) {
		if (logs != null) {
			for (LogItem m : logs) {
				logDao.create(m);
			}
		}
	}

	/**
	 * 单个日志
	 * @param item
	 */
	public void addLogItem(LogItem item) {
		if (item == null) {
			return;
		}
		logDao.create(item);
	}

	/**
	 * 查询所有的日志列表
	 * @param menuId
	 * @return
	 */
	public List<LogItem> queryAllLogs() {
		String sql = "select * from t_log_info";
		GenericRawResults<LogItem> results = logDao.queryRaw(sql, logDao.getRawRowMapper());
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
	public void deleteLogs(List<LogItem> list) {
		try {
			if (list != null) {
				logDao.delete(list);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
