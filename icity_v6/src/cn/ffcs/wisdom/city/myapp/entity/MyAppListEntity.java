package cn.ffcs.wisdom.city.myapp.entity;

import java.io.Serializable;
import java.util.List;

/**
 * <p>Title: 我的应用列表         </p>
 * <p>Description: 
 * </p>
 * <p>@author: Leo                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-3-7             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class MyAppListEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<AppEntity> data;

	public List<AppEntity> getData() {
		return data;
	}

	public void setData(List<AppEntity> data) {
		this.data = data;
	}

	public class AppEntity {
		private int id;
		private String mobile;// 手机号码
		private String menuId;// 菜单编号
		private String createTime;// 创建时间
		private String updateTime;// 更新时间
		private String isUse;// 是否使用
		private String cityCode;// 城市编号

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getMobile() {
			return mobile;
		}

		public void setMobile(String mobile) {
			this.mobile = mobile;
		}

		public String getMenuId() {
			return menuId;
		}

		public void setMenuId(String menuId) {
			this.menuId = menuId;
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

		public String getIsUse() {
			return isUse;
		}

		public void setIsUse(String isUse) {
			this.isUse = isUse;
		}

		public String getCityCode() {
			return cityCode;
		}

		public void setCityCode(String cityCode) {
			this.cityCode = cityCode;
		}
	}
}
