package cn.ffcs.wisdom.city.setting.about;

import java.io.Serializable;

import cn.ffcs.wisdom.http.BaseResp;
/**
 * 
 * <p>Title:检查更新业务处理类        </p>
 * <p>Description: 
 * 
 * </p>
 * <p>@author: xzw                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-3-15             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class VersionResp extends BaseResp implements Serializable {

	private static final long serialVersionUID = -18883951292325469L;

	private boolean needupdate = false; // false 无需升级
	private String download_url; // 下载路径"http://xxx.xxx.xx/a.apk"
	private String update_desc; // "更新描述信息" \n 换行
	private String version_name;
	private String update_type;
	private String file_size; // 整型,文件大小，已byte为单位

	public String getFile_size() {
		return file_size;
	}

	public void setFile_size(String fileSize) {
		file_size = fileSize;
	}

	public boolean getNeedupdate() {
		return needupdate;
	}

	public void setNeedupdate(boolean needupdate) {
		this.needupdate = needupdate;
	}

	public String getDownload_url() {
		return download_url;
	}

	public void setDownload_url(String downloadUrl) {
		download_url = downloadUrl;
	}

	public String getUpdate_desc() {
		return update_desc;
	}

	public void setUpdate_desc(String updateDesc) {
		update_desc = updateDesc;
	}

	public String getVersion_name() {
		return version_name;
	}

	public void setVersion_name(String versionName) {
		version_name = versionName;
	}

	public String getUpdate_type() {
		return update_type;
	}

	public void setUpdate_type(String updateType) {
		update_type = updateType;
	}
}
