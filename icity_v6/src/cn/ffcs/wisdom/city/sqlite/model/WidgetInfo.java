package cn.ffcs.wisdom.city.sqlite.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "t_widget_info")
public class WidgetInfo {

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField(columnName = "widget_id")
	private String widgetId;
	
	@DatabaseField(columnName = "menu_id")
	private String menuId;

	@DatabaseField(columnName = "widget_type")
	private String widgetType;
	
	@DatabaseField(columnName = "city_code")
	private String cityCode;

	public int getId() {
		return id;
	}

	
	public String getWidgetId() {
		return widgetId;
	}


	public void setWidgetId(String widgetId) {
		this.widgetId = widgetId;
	}


	public void setId(int id) {
		this.id = id;
	}

	public String getMenuId() {
		return menuId;
	}

	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}

	public String getWidgetType() {
		return widgetType;
	}

	public void setWidgetType(String widgetType) {
		this.widgetType = widgetType;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}
	

}
