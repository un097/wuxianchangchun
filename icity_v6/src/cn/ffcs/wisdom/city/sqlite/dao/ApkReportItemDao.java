package cn.ffcs.wisdom.city.sqlite.dao;

import java.util.List;

import cn.ffcs.wisdom.city.sqlite.model.ApkReportItem;

public interface ApkReportItemDao {

	public int addApkLog(ApkReportItem item);

	public List<ApkReportItem> queryAllApkLogs();

	public void deleteReportLogs(List<ApkReportItem> list);
	
	public boolean isExist(ApkReportItem item);

}
