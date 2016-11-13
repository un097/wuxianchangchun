package cn.ffcs.wisdom.city.modular.query.entity;

import java.io.Serializable;
import java.util.List;

/**
 * <p>Title:  查询类配置信息</p>
 * <p>Description: </p>
 * <p>@author: caijj                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2012-10-19             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class QueryInfo implements Serializable {

	private static final long serialVersionUID = 8226002247895125480L;

	private String queryTitle; // 应用名
	private Boolean isRemainAccount; // 是否记住账号
	private Boolean isRemainPwd; // 是否有记住密码选项 1表示有 ；空表示没有
	private List<ViewConfig> queryInfo; // 控件配置信息

	private String queryUrl; // 提交请求的URL
	private String appId; // 该查询应用的ID
	private String queryTipInfo; // 底部提示信息
	private String item_id; // 栏目ID
	private String cityCode; // 城市ID

	private String requestUrl; // 接口请求的实际URL
	private String queryBottomInfo; // 底部信息
	private String randomQueryUrl; // 验证码请求接口

	public String getQueryTitle() {
		return queryTitle;
	}

	public void setQueryTitle(String queryTitle) {
		this.queryTitle = queryTitle;
	}

	public Boolean getIsRemainAccount() {
		return isRemainAccount;
	}

	public void setIsRemainAccount(Boolean isRemainAccount) {
		this.isRemainAccount = isRemainAccount;
	}

	public List<ViewConfig> getQueryInfo() {
		return queryInfo;
	}

	public void setQueryInfo(List<ViewConfig> queryInfo) {
		this.queryInfo = queryInfo;
	}

	public String getQueryUrl() {
		return queryUrl;
	}

	public void setQueryUrl(String queryUrl) {
		this.queryUrl = queryUrl;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getQueryTipInfo() {
		return queryTipInfo;
	}

	public void setQueryTipInfo(String queryTipInfo) {
		this.queryTipInfo = queryTipInfo;
	}

	public String getItem_id() {
		return item_id;
	}

	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public Boolean getIsRemainPwd() {
		return isRemainPwd;
	}

	public void setIsRemainPwd(Boolean isRemainPwd) {
		this.isRemainPwd = isRemainPwd;
	}

	public String getRequestUrl() {
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	public String getQueryBottomInfo() {
		return queryBottomInfo;
	}

	public void setQueryBottomInfo(String queryBottomInfo) {
		this.queryBottomInfo = queryBottomInfo;
	}

	public String getRandomQueryUrl() {
		return randomQueryUrl;
	}

	public void setRandomQueryUrl(String randomQueryUrl) {
		this.randomQueryUrl = randomQueryUrl;
	}

}
