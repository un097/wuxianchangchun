package cn.ffcs.wisdom.city.entity;

import java.util.List;

import cn.ffcs.wisdom.city.sqlite.model.CityConfig;
import cn.ffcs.wisdom.city.sqlite.model.MenuItem;
import cn.ffcs.wisdom.city.sqlite.model.WidgetInfo;

/**
 * <p>Title: 菜单 </p>
 * <p>Description: </p>
 * <p>@author: caijj                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-3-4             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class MenuEntity {

	private MenuConfig data;

	public MenuConfig getData() {
		return data;
	}

	public void setData(MenuConfig data) {
		this.data = data;
	}

	public class MenuConfig {
		private String splash_bg; // 闪屏图片
		private String city_name; // 城市名称
		private String city_code; // 城市编码
		private String province_name; // 省份名称
		private String tyjx_code; // 天翼景象编码
		private List<MenuItem> menuListInfo;
		
		/**
		 * add by caijj 2013-10-16
		 */
		String tyjx_image; // 天翼景象广告图片
		String tyjx_duration; // 天翼景象广告图片播放时间

		public String getTyjx_image() {
			return tyjx_image;
		}

		public void setTyjx_image(String tyjx_image) {
			this.tyjx_image = tyjx_image;
		}

		public String getTyjx_duration() {
			return tyjx_duration;
		}

		public void setTyjx_duration(String tyjx_duration) {
			this.tyjx_duration = tyjx_duration;
		}

		private List<WidgetInfo> widgetListInfo; // widget控件配置

		private Common commonUse;// 常用

		private List<MenuItem> recommend;// 热门推荐

		private List<MenuItem> levelOneMenuListInfo;// 一级栏目

		public CityConfig toCityConfig() {
			CityConfig config = new CityConfig();
			config.setCityCode(city_code);
			config.setCityName(city_name);
			config.setProvinceName(province_name);
			config.setSplashBg(splash_bg);
			config.setTyjxCode(tyjx_code);
			config.setTyjx_image(tyjx_image);
			config.setTyjx_duration(tyjx_duration);
			return config;
		}

		public String getSplash_bg() {
			return splash_bg;
		}

		public void setSplash_bg(String splash_bg) {
			this.splash_bg = splash_bg;
		}

		public String getCity_code() {
			return city_code;
		}

		public void setCity_code(String city_code) {
			this.city_code = city_code;
		}

		public String getTyjx_code() {
			return tyjx_code;
		}

		public void setTyjx_code(String tyjx_code) {
			this.tyjx_code = tyjx_code;
		}

		public String getCity_name() {
			return city_name;
		}

		public void setCity_name(String city_name) {
			this.city_name = city_name;
		}

		public String getProvince_name() {
			return province_name;
		}

		public void setProvince_name(String province_name) {
			this.province_name = province_name;
		}

		public List<WidgetInfo> getWidgetListInfo() {
			return widgetListInfo;
		}

		public void setWidgetListInfo(List<WidgetInfo> widgetListInfo) {
			this.widgetListInfo = widgetListInfo;
		}

		public List<MenuItem> getMenuListInfo() {
			return menuListInfo;
		}

		public void setMenuListInfo(List<MenuItem> menuListInfo) {
			this.menuListInfo = menuListInfo;
		}

		public Common getCommonUse() {
			return commonUse;
		}

		public void setCommonUse(Common commonUse) {
			this.commonUse = commonUse;
		}

		public List<MenuItem> getRecommend() {
			return recommend;
		}

		public void setRecommend(List<MenuItem> recommend) {
			this.recommend = recommend;
		}

		public List<MenuItem> getLevelOneMenuListInfo() {
			return levelOneMenuListInfo;
		}

		public void setLevelOneMenuListInfo(List<MenuItem> levelOneMenuListInfo) {
			this.levelOneMenuListInfo = levelOneMenuListInfo;
		}
	}
}
