package cn.ffcs.wisdom.city.entity;

import java.io.Serializable;

/**
 * <p>Title: 配置详细参数实体类       </p>
 * <p>Description: 
 * 配置详细参数实体类
 * </p>
 * <p>@author: Eric.wsd                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2012-7-25             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class ConfigParams implements Serializable {

	private static final long serialVersionUID = 9222582057472453883L;
	public static final String SWITCH_OFF = "0"; // 开启推送

	private String BASE_URL;
	private String softsharesms;
	private String DOWNLOAD_URL;
	private String menuShareContent;
	private String menuShareUrl;
	private String ANDROID_TIMER; // 推送定时器时间
	private String OLDMSGSEND_SWITCH; // 全局推送开关。1为打开推送，0为关闭推送
	private String POINTS_DETAIL_URL;

	public String getSoftsharesms() {
		return softsharesms;
	}

	public void setSoftsharesms(String softsharesms) {
		this.softsharesms = softsharesms;
	}

	public String getDOWNLOAD_URL() {
		return DOWNLOAD_URL;
	}

	public void setDOWNLOAD_URL(String dOWNLOAD_URL) {
		DOWNLOAD_URL = dOWNLOAD_URL;
	}

	public String getBASE_URL() {
		return BASE_URL;
	}

	public void setBASE_URL(String bASE_URL) {
		BASE_URL = bASE_URL;
	}

	public String getMenuShareContent() {
		return menuShareContent;
	}

	public void setMenuShareContent(String menuShareContent) {
		this.menuShareContent = menuShareContent;
	}

	public String getMenuShareUrl() {
		return menuShareUrl;
	}

	public void setMenuShareUrl(String menuShareUrl) {
		this.menuShareUrl = menuShareUrl;
	}

	public String getANDROID_TIMER() {
		return ANDROID_TIMER;
	}

	public void setANDROID_TIMER(String aNDROID_TIMER) {
		ANDROID_TIMER = aNDROID_TIMER;
	}

	public String getOLDMSGSEND_SWITCH() {
		return OLDMSGSEND_SWITCH;
	}

	public void setOLDMSGSEND_SWITCH(String oLDMSGSEND_SWITCH) {
		OLDMSGSEND_SWITCH = oLDMSGSEND_SWITCH;
	}

	public String getPOINTS_DETAIL_URL() {
		return POINTS_DETAIL_URL;
	}

	public void setPOINTS_DETAIL_URL(String pOINTS_DETAIL_URL) {
		POINTS_DETAIL_URL = pOINTS_DETAIL_URL;
	}
}
