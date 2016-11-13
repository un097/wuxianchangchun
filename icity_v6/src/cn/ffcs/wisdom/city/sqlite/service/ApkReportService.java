package cn.ffcs.wisdom.city.sqlite.service;

import java.util.List;

import android.content.Context;
import cn.ffcs.wisdom.city.sqlite.dao.ApkReportItemDao;
import cn.ffcs.wisdom.city.sqlite.dao.impl.ApkReportItemDaoImpl;
import cn.ffcs.wisdom.city.sqlite.model.ApkReportItem;

/**
 * <p>Title:   apk下载成功上报服务类                        </p>
 * <p>Description:                     </p>
 * <p>@author: liaodl                  </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-11-6           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class ApkReportService {

	private static ApkReportService apkReportService;
	private static ApkReportItemDao apkDao;
	static final Object sInstanceSync = new Object();

	private ApkReportService(Context ctx) {
		if (apkDao == null) {
			apkDao = new ApkReportItemDaoImpl(ctx);
		}
	}

	public static ApkReportService getInstance(Context ctx) {
		synchronized (sInstanceSync) {
			if (apkReportService == null) {
				apkReportService = new ApkReportService(ctx);
			}
		}

		return apkReportService;
	}

	public void addApkLog(ApkReportItem item) {
		apkDao.addApkLog(item);
	}

	public List<ApkReportItem> queryAllApkLogs() {
		return apkDao.queryAllApkLogs();
	}

	/**
	 * 用于上传成功，清空统计，便于下一次重新计算
	 * @param list
	 */
	public void deleteReportLogs(List<ApkReportItem> list) {
		if (list == null || list.size() <= 0) {
			return;
		}
		apkDao.deleteReportLogs(list);
	}

}