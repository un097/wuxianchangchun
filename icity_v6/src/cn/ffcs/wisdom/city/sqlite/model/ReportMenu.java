package cn.ffcs.wisdom.city.sqlite.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "t_report_menu")
public class ReportMenu {

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField(columnName = "item_id")
	private String item_id;

	@DatabaseField(columnName = "action_type")
	private String action_type;

	@DatabaseField(columnName = "city_code")
	private String city_code;

	@DatabaseField(columnName = "create_time")
	private String create_time;

	@DatabaseField(columnName = "imsi")
	private String imsi;

	@DatabaseField(columnName = "mobile")
	private String mobile;

	@DatabaseField(columnName = "lat")
	private String lat;

	@DatabaseField(columnName = "lng")
	private String lng;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getItem_id() {
		return item_id;
	}

	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}

	public String getAction_type() {
		return action_type;
	}

	public void setAction_type(String action_type) {
		this.action_type = action_type;
	}

	public String getCity_code() {
		return city_code;
	}

	public void setCity_code(String city_code) {
		this.city_code = city_code;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getImsi() {
		return imsi;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	
}
