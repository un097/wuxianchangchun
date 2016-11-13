package cn.ffcs.wisdom.city.sqlite.service;

import java.util.List;

import android.content.Context;
import cn.ffcs.wisdom.city.download.ApkMgrConstants;
import cn.ffcs.wisdom.city.sqlite.dao.ApkInfoDao;
import cn.ffcs.wisdom.city.sqlite.dao.impl.ApkInfoDaoImpl;
import cn.ffcs.wisdom.city.sqlite.model.ApkInfo;

public class ApkInfoService {

	private static ApkInfoService apkInfoService;
	private static ApkInfoDao apkDao;
	static final Object sInstanceSync = new Object();

	private ApkInfoService(Context ctx) {
		if (apkDao == null) {
			apkDao = new ApkInfoDaoImpl(ctx);
		}
	}

	public static ApkInfoService getInstance(Context ctx) {
		synchronized (sInstanceSync) {
			if (apkInfoService == null) {
				apkInfoService = new ApkInfoService(ctx);
			}
		}

		return apkInfoService;
	}

	public void add(ApkInfo info) {
		apkDao.saveApk(info);
	}

	public boolean isExistByUrl(String url) {
		return apkDao.isExistByUrl(url);
	}
	
	public boolean isExistByName(String apkName) {
		return apkDao.isExistByName(apkName);
	}

	public ApkInfo queryByUrl(String apkUrl) {
		return apkDao.queryByUrl(apkUrl);
	}
	
	public ApkInfo queryByName(String apkName) {
		return apkDao.queryByName(apkName);
	}

	public List<ApkInfo> queryNoCompleteApk() {
		return apkDao.queryNoCompleteApk();
	}

	public List<ApkInfo> queryNoInstallList() {
		return apkDao.queryInstallList(ApkMgrConstants.INSTALL_FAIL);
	}

	public List<ApkInfo> queryInstallList() {
		return apkDao.queryInstallList(ApkMgrConstants.INSTALL_SUCCESS);
	}

	public List<ApkInfo> queryDownSuccessList() {
		return apkDao.queryDownSuccessList();
	}

	public void updateInstallStatu(int flag, String url) {
		apkDao.updateInstallStatu(flag, url);
	}

	public void updateDownStatu(int flag, String url) {
		apkDao.updateDownStatu(flag, url);
	}
	
	public List<ApkInfo> queryDowningList() {
		return apkDao.queryDowningList();
	}

	public void deleteByUrl(String url) {
		apkDao.deleteByUrl(url);
	}

	public void updateApk(ApkInfo apkInfo) {
		apkDao.updateApk(apkInfo);
	}

}