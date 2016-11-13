package cn.ffcs.wisdom.city.sqlite.model;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * <p>Title: 最近访问的记录表       </p>
 * <p>Description: 
 * 保存最近访问的记录到表中，仅取最近的20条记录进行显示
 * </p>
 * <p>@author: Eric.wsd                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-8-13             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
@DatabaseTable(tableName = "t_track_info")
public class TrackInfo implements Serializable {
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
	private String menuDesc; // 菜单描述

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

	@DatabaseField(columnName = "map")
	private String map;// 更多参数

	@DatabaseField(columnName = "v6_icon")
	private String v6Icon;

	@DatabaseField(columnName = "menu_ver")
	private String menuVer;
	
	@DatabaseField(columnName = "recommend")
	private String recommend;

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

	public String getMenuDesc() {
		return menuDesc;
	}

	public void setMenuDesc(String menuDesc) {
		this.menuDesc = menuDesc;
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
		return map;
	}

	public void setMap(String map) {
		this.map = map;
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
	
	/**
	 * 菜单对象转为最近访问记录对象
	 * @param menu
	 * @return
	 */
	public static TrackInfo converter(MenuItem menu) {
		if(menu == null)
			return null;
		
		TrackInfo info = new TrackInfo();
		info.setAppsize(menu.getAppsize());
		info.setAppUrl(menu.getAppUrl());
		info.setBaseLine(menu.getBaseLine());
		info.setCityCode(menu.getCityCode());
		info.setIcon(menu.getIcon());
		info.setIsApp(menu.getIsApp());
		info.setIsNew(menu.getIsNew());
		info.setMain(menu.getMain());
		info.setMap(menu.getMap());
		info.setMenuDesc(menu.getMenudesc());
		info.setMenuId(menu.getMenuId());
		info.setMenuName(menu.getMenuName());
		info.setMenuOrder(menu.getMenuOrder());
		info.setMenuPid(menu.getMenuPid());
		info.setMenuType(menu.getMenuType());
		info.setMenuVer(menu.getMenuVer());
		info.setMustLogin(menu.isMustLogin());
		info.setPackage_(menu.getPackage_());
		info.setRecommend(menu.getRecommend());
		info.setUrl(menu.getUrl());
		info.setV6Icon(menu.getV6Icon());
		
		return info;
	}

}
