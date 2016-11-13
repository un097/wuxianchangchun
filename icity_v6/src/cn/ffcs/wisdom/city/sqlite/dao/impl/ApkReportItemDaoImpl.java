package cn.ffcs.wisdom.city.sqlite.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import cn.ffcs.wisdom.city.sqlite.DBHelper;
import cn.ffcs.wisdom.city.sqlite.DBManager;
import cn.ffcs.wisdom.city.sqlite.dao.ApkReportItemDao;
import cn.ffcs.wisdom.city.sqlite.model.ApkReportItem;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

/**
 * <p>Title:  应用下载成功上报操作实现类                      </p>
 * <p>Description:                     </p>
 * <p>@author: liaodl                  </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-11-06          </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class ApkReportItemDaoImpl implements ApkReportItemDao {

	private static RuntimeExceptionDao<ApkReportItem, Integer> apkDao;

	public ApkReportItemDaoImpl(Context context) {
		if (apkDao == null) {
			DBHelper helper = (DBHelper) DBManager.getHelper(context);
			apkDao = helper.getRuntimeExceptionDao(ApkReportItem.class);
		}
	}

	@Override
	public int addApkLog(ApkReportItem item) {
		if (isExist(item)) {
			return -1;
		}
		return apkDao.create(item);
	}

	@Override
	public List<ApkReportItem> queryAllApkLogs() {
		return apkDao.queryForAll();
	}

	@Override
	public void deleteReportLogs(List<ApkReportItem> list) {
		apkDao.delete(list);
	}

	@Override
	public boolean isExist(ApkReportItem item) {
		if (item == null) {
			return false;
		}
		List<ApkReportItem> apkList = new ArrayList<ApkReportItem>();
		try {
			QueryBuilder<ApkReportItem, Integer> qb = apkDao.queryBuilder();
			Where<ApkReportItem, Integer> where = qb.where();
			where.eq("create_time", item.getCreate_time());
			where.and();
			where.eq("item_id", item.getItem_id());
			apkList = qb.query();
		} catch (SQLException e) {
			return false;
		}
		if (apkList != null && apkList.size() > 0) {
			return true;
		}
		return false;
	}

}
