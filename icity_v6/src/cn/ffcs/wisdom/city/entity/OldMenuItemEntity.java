package cn.ffcs.wisdom.city.entity;

import java.io.Serializable;

import cn.ffcs.wisdom.city.sqlite.model.MenuItem;

/**
 * 二级菜单项实体类
 */
public class OldMenuItemEntity implements Serializable {

	private static final long serialVersionUID = 7875509073543878107L;
	private String item_id;

	private String item_name; // 菜单项名称
	private String item_image; // 菜单项图片
	private String item_image_url; // 菜单项图片地址

	// 菜单项类型:
	// 分external_app、native_app、wap三种，external_app指外部第三方应用需下载，native_app指应用的其他页面
	private String item_type;
	private String must_login = "false"; // 是否需要登录界面
	private String pkg; // Android包名
	private String cls; // Android类名
	private String scheme_name; // iOS项目名称
	private String scheme_url; // iOS包名
	private String url_wap; // wap地址
	private String url_android; // android应用下载地址
	private String url_ios; // iOS应用下载地址
	private String item_desc; // 应用描述
	private String app_size; // 应用大小
	public String bIsFav = "false";
	private String favor_count;
	private String click_count;
	private String lastdate;
	private String map;
	private String isApp;// 是不是查询类
	private String v6_icon;// v6 图标
	private String shareContent;//分享内容
	private String shareType;//分享类型

	// 是否为最新或者热点 add by caijj 2012-9-11
	// 0：否， 1：最新 2：热点 3：最新热点
	private String isNew;

	
	public String getShareContent() {
		return shareContent;
	}

	public void setShareContent(String shareContent) {
		this.shareContent = shareContent;
	}

	public String getShareType() {
		return shareType;
	}

	public void setShareType(String shareType) {
		this.shareType = shareType;
	}

	public String getItem_name() {
		return item_name;
	}

	public void setItem_name(String item_name) {
		this.item_name = item_name;
	}

	public String getItem_image() {
		return item_image;
	}

	public void setItem_image(String item_image) {
		this.item_image = item_image;
	}

	public String getItem_image_url() {
		return item_image_url;
	}

	public void setItem_image_url(String item_image_url) {
		this.item_image_url = item_image_url;
	}

	public String getItem_type() {
		return item_type;
	}

	public void setItem_type(String item_type) {
		this.item_type = item_type;
	}

	public String getPkg() {
		return pkg;
	}

	public void setPkg(String pkg) {
		this.pkg = pkg;
	}

	public String getCls() {
		return cls;
	}

	public void setCls(String cls) {
		this.cls = cls;
	}

	public String getScheme_name() {
		return scheme_name;
	}

	public void setScheme_name(String scheme_name) {
		this.scheme_name = scheme_name;
	}

	public String getScheme_url() {
		return scheme_url;
	}

	public void setScheme_url(String scheme_url) {
		this.scheme_url = scheme_url;
	}

	public String getUrl_wap() {
		return url_wap;
	}

	public void setUrl_wap(String url_wap) {
		this.url_wap = url_wap;
	}

	public String getUrl_android() {
		return url_android;
	}

	public void setUrl_android(String url_android) {
		this.url_android = url_android;
	}

	public String getItem_id() {
		return item_id;
	}

	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}

	public String getApp_size() {
		return app_size;
	}

	public void setApp_size(String app_size) {
		this.app_size = app_size;
	}

	public String getItem_desc() {
		return item_desc;
	}

	public void setItem_desc(String item_desc) {
		this.item_desc = item_desc;
	}

	public String getUrl_ios() {
		return url_ios;
	}

	public void setUrl_ios(String url_ios) {
		this.url_ios = url_ios;
	}

	public String getbIsFav() {
		return bIsFav;
	}

	public String getV6Icon() {
		return v6_icon;
	}

	public void setV6Icon(String v6Icon) {
		this.v6_icon = v6Icon;
	}

	public void setbIsFav(String bIsFav) {
		this.bIsFav = bIsFav;
	}

	public String getMust_login() {
		return must_login;
	}

	public void setMust_login(String must_login) {
		this.must_login = must_login;
	}

	public String getFavor_count() {
		return favor_count;
	}

	public void setFavor_count(String favor_count) {
		this.favor_count = favor_count;
	}

	public String getClick_count() {
		return click_count;
	}

	public void setClick_count(String click_count) {
		this.click_count = click_count;
	}

	public String getLastdate() {
		return lastdate;
	}

	public void setLastdate(String lastdate) {
		this.lastdate = lastdate;
	}

	public String getMap() {
		return map;
	}

	public void setMap(String map) {
		this.map = map;
	}

	public String getIsNew() {
		return isNew;
	}

	public void setIsNew(String isNew) {
		this.isNew = isNew;
	}

	@Override
	public String toString() {
		return "{item_id:" + item_id + ", item_name:" + item_name + ", item_image:" + item_image
				+ ", item_image_url:" + item_image_url + ", item_type:" + item_type + ", must_login:"
				+ must_login + ", pkg:" + pkg + ", cls:" + cls + ", scheme_name:" + scheme_name
				+ ", scheme_url:" + scheme_url + ", url_wap:" + url_wap + ", url_android:" + url_android
				+ ", url_ios:" + url_ios + ", item_desc:" + item_desc + ", app_size:" + app_size
				+ ", bIsFav:" + bIsFav + ", favor_count:" + favor_count + ", click_count:" + click_count
				+ ", lastdate:" + lastdate + ", map:" + map + "}";
	}

	public String getIsApp() {
		return isApp;
	}

	public void setIsApp(String isApp) {
		this.isApp = isApp;
	}

	public static MenuItem converOldMenuItemToNewMenuItem(OldMenuItemEntity oldMenuItemEntity) {
		MenuItem menuItem = new MenuItem();
		if (oldMenuItemEntity == null) {
			return menuItem;
		}
		menuItem.setAppUrl(oldMenuItemEntity.getUrl_android());
		menuItem.setAppsize(oldMenuItemEntity.getApp_size());
		String v6Icon = oldMenuItemEntity.getV6Icon();
//		if(StringUtil.isEmpty(v6Icon)){
//			v6Icon = oldMenuItemEntity.getItem_image_url();
//		}
		menuItem.setV6Icon(v6Icon);
		menuItem.setMenuId(oldMenuItemEntity.getItem_id());
		menuItem.setMenudesc(oldMenuItemEntity.getItem_desc());
		menuItem.setIsApp(oldMenuItemEntity.getIsApp());
		menuItem.setMain(oldMenuItemEntity.getCls());
		menuItem.setPackage_(oldMenuItemEntity.getPkg());
		menuItem.setMap(oldMenuItemEntity.getMap());
		menuItem.setIsNew(oldMenuItemEntity.getIsNew());
		menuItem.setMustLogin(Boolean.parseBoolean(oldMenuItemEntity.getMust_login()));
		menuItem.setMenuType(oldMenuItemEntity.getItem_type());
		menuItem.setMenuName(oldMenuItemEntity.getItem_name());
		menuItem.setUrl(oldMenuItemEntity.getUrl_wap());
		menuItem.setIcon(oldMenuItemEntity.getItem_image());
		menuItem.setShareContent(oldMenuItemEntity.getShareContent());
		menuItem.setShareType(oldMenuItemEntity.getShareType());
		return menuItem;
	}
	
}
