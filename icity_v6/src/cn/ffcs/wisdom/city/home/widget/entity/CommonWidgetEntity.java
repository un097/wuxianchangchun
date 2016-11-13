package cn.ffcs.wisdom.city.home.widget.entity;

import java.io.Serializable;
import java.util.List;

/**
 * <p>Title: widget请求公共类         </p>
 * <p>Description: 
 * </p>
 * <p>@author: Leo                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-4-10             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class CommonWidgetEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	private List<CommonWidgetData> data;

	public List<CommonWidgetData> getData() {
		return data;
	}

	public void setData(List<CommonWidgetData> data) {
		this.data = data;
	}

	public class CommonWidgetData implements Serializable{
		private static final long serialVersionUID = 1L;
		private String menuId;// 菜单编号
		private String widgetId;//控件编号
		private String cityCode;// 城市编号
		private String imageUrl;// 图片地址
		private String desc;//描述信息
		private String createDate;//创建时间
		private String datasourceUrl;//信源接口地址
		private String wapUrl;//wap跳转地址
		
		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		public String getCreateDate() {
			return createDate;
		}

		public void setCreateDate(String createDate) {
			this.createDate = createDate;
		}

		public String getDatasourceUrl() {
			return datasourceUrl;
		}

		public void setDatasourceUrl(String datasourceUrl) {
			this.datasourceUrl = datasourceUrl;
		}

		public String getWapUrl() {
			return wapUrl;
		}

		public void setWapUrl(String wapUrl) {
			this.wapUrl = wapUrl;
		}

		public String getMenuId() {
			return menuId;
		}

		public void setMenuId(String menuId) {
			this.menuId = menuId;
		}

		public String getCityCode() {
			return cityCode;
		}

		public void setCityCode(String cityCode) {
			this.cityCode = cityCode;
		}

		public String getImageUrl() {
			return imageUrl;
		}

		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}

		public String getWidgetId() {
			return widgetId;
		}

		public void setWidgetId(String widgetId) {
			this.widgetId = widgetId;
		}

	}
}
