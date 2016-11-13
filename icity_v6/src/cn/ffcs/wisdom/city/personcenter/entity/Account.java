package cn.ffcs.wisdom.city.personcenter.entity;

import java.io.Serializable;

/**
 * <p>Title:     用户账号                                                   </p>
 * <p>Description:                     </p>
 * <p>@author: Leo                     </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-2-27           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class Account implements Serializable {

	private static final long serialVersionUID = -6099638467136416056L;

	private AccountData data = null;

	public AccountData getData() {
		if (data == null) {
			return new AccountData();
		}
		return data;
	}

	public void setData(AccountData data) {
		this.data = data;
	}

	public class AccountData implements Serializable {

		private static final long serialVersionUID = 6525359314547390976L;
		private String mobile = null;// 手机号码
		private String password = null;// 密码
		private String userName = null;// 昵称
		private String imsi = null;// imsi号
		private String email = null;// 邮件地址
		private String communityCode = null;// 社区编号
		private String communityName = null;// 社区名称
		private int id;// 个人中心id
		private int userId;// 用户id
		private String createTime;
		private String updateTime;
		private int gradeScore;// 总积分
		private int consumeScore;// 消耗积分 (余额 = 总积分 –消耗积分)
		private String isSign;// 是否签到
		private int low;
		private int high;
		private int isUse;// 是否在使用
		private String levelName;// 等级名称
		private int levelOrder;// 等级顺序
		private String iconUrl;

		public String getMobile() {
			return mobile;
		}

		public void setMobile(String mobile) {
			this.mobile = mobile;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public String getImsi() {
			return imsi;
		}

		public void setImsi(String imsi) {
			this.imsi = imsi;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getCommunityCode() {
			return communityCode;
		}

		public void setCommunityCode(String communityCode) {
			this.communityCode = communityCode;
		}

		public String getCommunityName() {
			return communityName;
		}

		public void setCommunityName(String communityName) {
			this.communityName = communityName;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public int getUserId() {
			return userId;
		}

		public void setUserId(int userId) {
			this.userId = userId;
		}

		public String getCreateTime() {
			return createTime;
		}

		public void setCreateTime(String createTime) {
			this.createTime = createTime;
		}

		public String getUpdateTime() {
			return updateTime;
		}

		public void setUpdateTime(String updateTime) {
			this.updateTime = updateTime;
		}

		public int getGradeScore() {
			return gradeScore;
		}

		public void setGradeScore(int gradeScore) {
			this.gradeScore = gradeScore;
		}

		public int getConsumeScore() {
			return consumeScore;
		}

		public void setConsumeScore(int consumeScore) {
			this.consumeScore = consumeScore;
		}

		public String getIsSign() {
			return isSign;
		}

		public void setIsSign(String isSign) {
			this.isSign = isSign;
		}

		public int getLow() {
			return low;
		}

		public void setLow(int low) {
			this.low = low;
		}

		public int getHigh() {
			return high;
		}

		public void setHigh(int high) {
			this.high = high;
		}

		public int getIsUse() {
			return isUse;
		}

		public void setIsUse(int isUse) {
			this.isUse = isUse;
		}

		public String getLevelName() {
			return levelName;
		}

		public void setLevelName(String levelName) {
			this.levelName = levelName;
		}

		public int getLevelOrder() {
			return levelOrder;
		}

		public void setLevelOrder(int levelOrder) {
			this.levelOrder = levelOrder;
		}

		public String getIconUrl() {
			return iconUrl;
		}

		public void setIconUrl(String iconUrl) {
			this.iconUrl = iconUrl;
		}
	}

}
