package cn.ffcs.wisdom.city.sqlite.model;

import java.io.Serializable;

import android.content.Context;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.common.MenuType;
import cn.ffcs.wisdom.city.download.ApkMgrConstants;
import cn.ffcs.wisdom.city.utils.AppMgrUtils;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "t_apk_info")
public class ApkInfo implements Serializable {

	private static final long serialVersionUID = -1952152593521539651L;

	@DatabaseField(generatedId = true, columnName = "apk_id")
	private int id;

	@DatabaseField(columnName = "apk_item_id")
	private String itemId;//栏目id

	@DatabaseField(columnName = "apk_dir")
	private String dir;

	@DatabaseField(columnName = "apk_url")
	private String url;

	@DatabaseField(columnName = "apk_icon_url")
	private String iconUrl;

	@DatabaseField(columnName = "apk_name")
	private String apkName;

	@DatabaseField(columnName = "apk_size")
	private String apkSize;

	@DatabaseField(columnName = "apk_package_name")
	private String packageName;

	@DatabaseField(columnName = "apk_main")
	private String apkMain;

	@DatabaseField(columnName = "apk_desc")
	private String apkDesc;

	@DatabaseField(columnName = "apk_file_size")
	private int fileSize;

	@DatabaseField(columnName = "apk_complete_size")
	private int completeSize;

	@DatabaseField(columnName = "create_time")
	private String createTime;//应用下载时间

	/**
	 * 下载状态:<br/>
	 * 未下载(状态码:ApkMgrConstants.DOWNLOAD_PADDING) <br/>
	 * 已下载(状态码:ApkMgrConstants.DOWNLOAD_SUCCESS) <br/>
	 * 暂停(状态码:ApkMgrConstants.DOWNLOAD_PAUSE) <br/>
	 */
	@DatabaseField(columnName = "apk_download_statu")
	private int downloadStatu;

	/**
	 * 安装状态:<br/>
	 * 已安装(状态码:ApkMgrConstants.INSTALL_SUCCESS) <br/>
	 * 未安装(状态码:ApkMgrConstants.INSTALL_FAIL)
	 */
	@DatabaseField(columnName = "apk_install_statu")
	private int installStatu;

	public static ApkInfo toApkInfo(MenuItem item) {
		ApkInfo entity = new ApkInfo();
		if (item != null) {
			entity.setItemId(item.getMenuId());//add 2013-11-6
			entity.setUrl(item.getAppUrl());//不是getUrl()
			entity.setApkName(item.getMenuName());
			entity.setApkSize(item.getAppsize());
			entity.setApkMain(item.getMain());
			entity.setPackageName(item.getPackage_());
			entity.setApkDesc(item.getMenudesc());
			entity.setIconUrl(item.getV6Icon());
			entity.setDir(Config.SDCARD_APK);
			entity.setDownloadStatu(ApkMgrConstants.DOWNLOAD_PADDING);
			return entity;
		}
		return entity;
	}

	public MenuItem toMenuItem(Context context) {
		MenuItem item = new MenuItem();
		item.setMenuId(getItemId());
		item.setMenuName(getApkName());
		item.setAppUrl(getUrl());
		item.setAppsize(getApkSize());
		item.setIcon(getIconUrl());
		item.setMain(getApkMain());
		item.setPackage_(getPackageName());
		item.setV6Icon(getIconUrl());
		item.setMenuType(MenuType.EXTERNAL_APP);

		// 获取自定义参数
		String params = AppMgrUtils.getOtherParams(context);
		item.setMap(params);

		return item;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getApkName() {
		return apkName;
	}

	public void setApkName(String apkName) {
		this.apkName = apkName;
	}

	public String getApkSize() {
		return apkSize;
	}

	public void setApkSize(String apkSize) {
		this.apkSize = apkSize;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getApkMain() {
		return apkMain;
	}

	public void setApkMain(String apkMain) {
		this.apkMain = apkMain;
	}

	public String getApkDesc() {
		return apkDesc;
	}

	public void setApkDesc(String apkDesc) {
		this.apkDesc = apkDesc;
	}

	public int getDownloadStatu() {
		return downloadStatu;
	}

	public void setDownloadStatu(int downloadStatu) {
		this.downloadStatu = downloadStatu;
	}

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public int getCompleteSize() {
		return completeSize;
	}

	public void setCompleteSize(int completeSize) {
		this.completeSize = completeSize;
	}

	public int getInstallStatu() {
		return installStatu;
	}

	public void setInstallStatu(int installStatu) {
		this.installStatu = installStatu;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

}
