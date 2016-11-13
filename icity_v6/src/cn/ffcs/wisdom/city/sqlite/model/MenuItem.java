package cn.ffcs.wisdom.city.sqlite.model;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * <p>Title:  栏目菜单</p>
 * <p>Description: </p>
 * <p>@author: caijj                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-1-29             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
@DatabaseTable(tableName = "t_menu")
public class MenuItem implements Serializable {

	private static final long serialVersionUID = 7163928912959328542L;

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField(columnName = "city_code", index = true)
	private String cityCode; // 城市编号

	@DatabaseField(columnName = "menu_id")
	private String menuId; // 菜单id

	@DatabaseField(columnName = "menu_name")
	private String menuName; // 菜单名

	@DatabaseField(columnName = "icon")
	private String icon; // 图片地址

	@DatabaseField(columnName = "menu_type")
	private String menuType; // 菜单类型

	@DatabaseField(columnName = "menu_desc")
	private String menudesc; // 菜单描述

	@DatabaseField(columnName = "must_login")
	private boolean mustLogin; // 是否需要登录

	@DatabaseField(columnName = "package_name")
	private String package_; // 包名

	@DatabaseField(columnName = "clazz_name")
	private String main; // 类名

	@DatabaseField(columnName = "url")
	private String url; // wap页面地址

	@DatabaseField(columnName = "apk_url")
	private String appUrl; // apk下载地址

	@DatabaseField(columnName = "menu_parent_id", index = true)
	private String menuPid; // 父菜单id

	@DatabaseField(columnName = "is_app")
	private String isApp; // 是否查询类

	@DatabaseField(columnName = "base_line")
	private String baseLine; // 基线版本

	@DatabaseField(columnName = "app_size")
	private String appsize;

	@DatabaseField(columnName = "menu_order")
	private int menuOrder;

	@DatabaseField(columnName = "is_new")
	private String isNew;

	@DatabaseField(columnName = "is_red")
	private String isRed; //是否栏目有小红点。0否，1是  数值型

	@DatabaseField(columnName = "map")
	private String parMapString;// 更多参数

	@DatabaseField(columnName = "v6_icon")
	private String v6Icon;

	@DatabaseField(columnName = "menu_ver")
	private String menuVer;

	@DatabaseField(columnName = "recommend")
	private String recommend;

	@DatabaseField(columnName = "recommend_order")
	private int recommendOrder;

	@DatabaseField(columnName = "redRecordTime")
	private String redRecordTime;

	public MenuTypeMedia media;

	// ---- new ----/
	@DatabaseField(columnName = "ancestorId")
	private int ancestorId;//三级栏目对应的一级栏目id

	@DatabaseField(columnName = "commonOrder")
	private int commonOrder;//常用排序

	@DatabaseField(columnName = "frontMenuOrder")
	private int frontMenuOrder;//首页服务排序

	@DatabaseField(columnName = "leafMenuOrder")
	private int leafMenuOrder;// 3级菜单排序

	@DatabaseField(columnName = "sharecontent")
	private String shareContent;// 分享内容

	@DatabaseField(columnName = "shareType")
	private String shareType;// 分享类型

	@DatabaseField(columnName = "subsribe")
	private boolean subsribe;// 是否订阅
	
	@DatabaseField(columnName = "isSystemDefault")
	private int isSystemDefault = 0;

	public int getIsSystemDefault() {
		return isSystemDefault;
	}

	public void setIsSystemDefault(int isSystemDefault) {
		this.isSystemDefault = isSystemDefault;
	}

	public boolean isSubsribe() {
		return subsribe;
	}

	public void setSubsribe(boolean subsribe) {
		this.subsribe = subsribe;
	}

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

	public int getLeafmenuorder() {
		return leafMenuOrder;
	}

	public void setLeafmenuorder(int leafMenuOrder) {
		this.leafMenuOrder = leafMenuOrder;
	}

	public int getCommonOrder() {
		return commonOrder;
	}

	public void setCommonOrder(int commonOrder) {
		this.commonOrder = commonOrder;
	}

	public int getFrontMenuOrder() {
		return frontMenuOrder;
	}

	public void setFrontMenuOrder(int frontMenuOrder) {
		this.frontMenuOrder = frontMenuOrder;
	}

	public int getAncestorId() {
		return ancestorId;
	}

	public void setAncestorId(int ancestorId) {
		this.ancestorId = ancestorId;
	}

	//自定义菜单参数
	public enum MenuTypeMedia {
		bannerWapShare;//首页广告点击跳转
	};

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getMenuId() {
		return menuId;
	}

	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}

	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getMenuType() {
		return menuType;
	}

	public void setMenuType(String menuType) {
		this.menuType = menuType;
	}

	public String getMenudesc() {
		return menudesc;
	}

	public void setMenudesc(String menudesc) {
		this.menudesc = menudesc;
	}

	public boolean isMustLogin() {
		return mustLogin;
	}

	public void setMustLogin(boolean mustLogin) {
		this.mustLogin = mustLogin;
	}

	public String getPackage_() {
		return package_;
	}

	public void setPackage_(String package_) {
		this.package_ = package_;
	}

	public String getMain() {
		return main;
	}

	public void setMain(String main) {
		this.main = main;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getAppUrl() {
		return appUrl;
	}

	public void setAppUrl(String appUrl) {
		this.appUrl = appUrl;
	}

	public String getMenuPid() {
		return menuPid;
	}

	public void setMenuPid(String menuPid) {
		this.menuPid = menuPid;
	}

	public String getIsApp() {
		return isApp;
	}

	public void setIsApp(String isApp) {
		this.isApp = isApp;
	}

	public String getBaseLine() {
		return baseLine;
	}

	public void setBaseLine(String baseLine) {
		this.baseLine = baseLine;
	}

	public String getAppsize() {
		return appsize;
	}

	public void setAppsize(String appsize) {
		this.appsize = appsize;
	}

	public int getMenuOrder() {
		return menuOrder;
	}

	public void setMenuOrder(int menuOrder) {
		this.menuOrder = menuOrder;
	}

	public String getIsNew() {
		return isNew;
	}

	public void setIsNew(String isNew) {
		this.isNew = isNew;
	}

	public String getMap() {
		return parMapString;
	}

	public void setMap(String map) {
		this.parMapString = map;
	}

	public String getV6Icon() {
		return v6Icon;
	}

	public void setV6Icon(String v6Icon) {
		this.v6Icon = v6Icon;
	}

	public String getMenuVer() {
		return menuVer;
	}

	public void setMenuVer(String menuVer) {
		this.menuVer = menuVer;
	}

	public String getRecommend() {
		return recommend;
	}

	public void setRecommend(String recommend) {
		this.recommend = recommend;
	}

	public int getRecommendOrder() {
		return recommendOrder;
	}

	public void setRecommendOrder(int recommendOrder) {
		this.recommendOrder = recommendOrder;
	}

	public String getIsRed() {
		return isRed;
	}

	public void setIsRed(String isRed) {
		this.isRed = isRed;
	}

	public String getRedRecordTime() {
		return redRecordTime;
	}

	public void setRedRecordTime(String redRecordTime) {
		this.redRecordTime = redRecordTime;
	}

}
