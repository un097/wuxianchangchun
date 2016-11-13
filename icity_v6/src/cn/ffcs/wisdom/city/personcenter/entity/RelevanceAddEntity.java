package cn.ffcs.wisdom.city.personcenter.entity;

import java.util.List;

/**
 * <p>Title:      可关联应用实体                </p>
 * <p>Description:                     </p>
 * <p>@author: zhangws                 </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-5-13           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class RelevanceAddEntity {
	private List<RelevanceAdd> data;

	public List<RelevanceAdd> getData() {
		return data;
	}

	public void setData(List<RelevanceAdd> data) {
		this.data = data;
	}

	public static class RelevanceAdd {
		
		private int itemId;
		private String appMenuName;
		private String appMenuIcon;
		private String appMenuV6Icon;

		public String getAppMenuV6Icon() {
			return appMenuV6Icon;
		}

		public void setAppMenuV6Icon(String appMenuV6Icon) {
			this.appMenuV6Icon = appMenuV6Icon;
		}

		public int getItemId() {
			return itemId;
		}

		public void setItemId(int itemId) {
			this.itemId = itemId;
		}

		public String getAppMenuName() {
			return appMenuName;
		}

		public void setAppMenuName(String appMenuName) {
			this.appMenuName = appMenuName;
		}

		public String getAppMenuIcon() {
			return appMenuIcon;
		}

		public void setAppMenuIcon(String appMenuIcon) {
			this.appMenuIcon = appMenuIcon;
		}
	}
}
