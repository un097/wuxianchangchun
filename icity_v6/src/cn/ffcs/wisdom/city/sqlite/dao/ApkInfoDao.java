package cn.ffcs.wisdom.city.sqlite.dao;

import java.util.List;

import cn.ffcs.wisdom.city.sqlite.model.ApkInfo;

public interface ApkInfoDao {

	/**
	 * 保存此应用下载记录
	 */
	public int saveApk(ApkInfo info);
	/**
	 * 是否存在此下载记录
	 * @param url
	 * @return
	 */
	public boolean isExistByUrl(String url);
	
	public boolean isExistByName(String apkName);
	
	public ApkInfo queryByUrl(String apkUrl);
	
	public ApkInfo queryByName(String apkName);
	
	public List<ApkInfo> queryNoCompleteApk();
	
	public List<ApkInfo> queryInstallList(int flag);
	
	public List<ApkInfo> queryDownSuccessList();
	
	public void updateInstallStatu(int flag, String url);
	
	public void updateDownStatu(int flag, String url);
	
	/**
	 * 查询正在下载列表
	 * @param url
	 */
	public List<ApkInfo> queryDowningList();
	
	public void deleteByUrl(String url);
	
	public void updateApk(ApkInfo apkInfo);
	
}
