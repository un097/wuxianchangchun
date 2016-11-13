package cn.ffcs.wisdom.city.sqlite.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import cn.ffcs.wisdom.city.download.ApkMgrConstants;
import cn.ffcs.wisdom.city.sqlite.DBHelper;
import cn.ffcs.wisdom.city.sqlite.DBManager;
import cn.ffcs.wisdom.city.sqlite.dao.ApkInfoDao;
import cn.ffcs.wisdom.city.sqlite.model.ApkInfo;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.StringUtil;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

/**
 * <p>Title:  应用下载数据库管理操作实现类                      </p>
 * <p>Description:                     </p>
 * <p>@author: liaodl                  </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-5-27           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class ApkInfoDaoImpl implements ApkInfoDao {

	private static RuntimeExceptionDao<ApkInfo, Integer> apkDao;

	public ApkInfoDaoImpl(Context context) {
		if (apkDao == null) {
			DBHelper helper = (DBHelper) DBManager.getHelper(context);
			apkDao = helper.getRuntimeExceptionDao(ApkInfo.class);
		}
	}

	@Override
	public int saveApk(ApkInfo info) {
		if (info == null) {
			return 0;
		}
		return apkDao.create(info);
	}

	@Override
	public boolean isExistByUrl(String url) {
		if (StringUtil.isEmpty(url)) {
			return false;
		}
		List<ApkInfo> apkList = apkDao.queryForEq("apk_url", url);
		if (apkList != null && apkList.size() > 0) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean isExistByName(String apkName) {
		if (StringUtil.isEmpty(apkName)) {
			return false;
		}
		List<ApkInfo> apkList = apkDao.queryForEq("apk_name", apkName);
		if (apkList != null && apkList.size() > 0) {
			return true;
		}
		return false;
	}

	@Override
	public ApkInfo queryByUrl(String apkUrl) {
		ApkInfo info = new ApkInfo();
		try {
			if (StringUtil.isEmpty(apkUrl)) {
				return info;
			}
			QueryBuilder<ApkInfo, Integer> qb = apkDao.queryBuilder();
			qb.where().eq("apk_url", apkUrl);
			info = qb.queryForFirst();
		} catch (SQLException e) {
			Log.i("queryByUrl()--数据库无相关信息。");
			return info;
		}
		return info;
	}
	
	@Override
	public ApkInfo queryByName(String apkName) {
		ApkInfo info = new ApkInfo();
		try {
			if (StringUtil.isEmpty(apkName)) {
				return info;
			}
			QueryBuilder<ApkInfo, Integer> qb = apkDao.queryBuilder();
			qb.where().eq("apk_name", apkName);
			info = qb.queryForFirst();
		} catch (SQLException e) {
			Log.i("queryByName()--数据库无相关信息。");
			return info;
		}
		return info;
	}

	@Override
	public List<ApkInfo> queryNoCompleteApk() {
		List<ApkInfo> infos = new ArrayList<ApkInfo>();
		try {
			QueryBuilder<ApkInfo, Integer> qb = apkDao.queryBuilder();
			qb.where().ne("apk_download_statu", ApkMgrConstants.DOWNLOAD_SUCCESS + "");
			infos = qb.query();
		} catch (SQLException e) {
			Log.i("queryNoCompleteApk()--数据库无相关信息。");
			return infos;
		}
		return infos;
	}

	@Override
	public List<ApkInfo> queryInstallList(int flag) {
		List<ApkInfo> infos = new ArrayList<ApkInfo>();
		try {
			QueryBuilder<ApkInfo, Integer> qb = apkDao.queryBuilder();
			Where<ApkInfo, Integer> where = qb.where();
			where.eq("apk_download_statu", ApkMgrConstants.DOWNLOAD_SUCCESS + "");
			where.and();
			where.eq("apk_install_statu", flag);
			infos = qb.query();
		} catch (SQLException e) {
			Log.i("queryNoInstallList()--数据库无相关信息。");
			return infos;
		}
		return infos;
	}

	@Override
	public List<ApkInfo> queryDownSuccessList() {
		List<ApkInfo> apkList = new ArrayList<ApkInfo>();
		apkList = apkDao.queryForEq("apk_download_statu", ApkMgrConstants.DOWNLOAD_SUCCESS + "");
		if (apkList != null && apkList.size() > 0) {
			return apkList;
		}
		return apkList;
	}

	@Override
	public void updateInstallStatu(int flag, String url) {
		String sql = "update t_apk_info set apk_install_statu=? where apk_url=?";
		String[] args = new String[] { flag + "", url };
		apkDao.executeRaw(sql, args);
	}

	@Override
	public void updateDownStatu(int flag, String url) {
		String sql = "update t_apk_info set apk_download_statu=? where apk_url=?";
		String[] args = new String[] { flag + "", url };
		apkDao.executeRaw(sql, args);
	}

	@Override
	public List<ApkInfo> queryDowningList() {
		List<ApkInfo> apkList = new ArrayList<ApkInfo>();
		apkList = apkDao.queryForEq("apk_download_statu", ApkMgrConstants.DOWNLOAD_RUNNING + "");
		if (apkList != null && apkList.size() > 0) {
			return apkList;
		}
		return apkList;
	}
	
	@Override
	public void deleteByUrl(String url) {
		if(StringUtil.isEmpty(url)){
			return;
		}
		apkDao.executeRaw("delete from t_apk_info where apk_url=?",new String[] { url });	
	}

	@Override
	public void updateApk(ApkInfo apkInfo) {
		apkDao.update(apkInfo);
	}

}
