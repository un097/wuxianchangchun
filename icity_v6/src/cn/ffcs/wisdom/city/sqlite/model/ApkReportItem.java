package cn.ffcs.wisdom.city.sqlite.model;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "t_apk_report_info")
public class ApkReportItem implements Serializable {

	private static final long serialVersionUID = 1L;
	@DatabaseField(generatedId = true, columnName = "apk_report_id")
	private int id;

	@DatabaseField(columnName = "client_type")
	private String client_type;//客户端类型 icity_ver 爱城市 icity_chongqing_ver 智慧重庆

	@DatabaseField(columnName = "channel_type")
	private String channel_type;//客户端渠道类型

	@DatabaseField(columnName = "os_type")
	private String os_type;//操作系统类型: 1 android  2 ios 3 wap  4web

	@DatabaseField(columnName = "imei")
	private String imei;//手机设备号

	@DatabaseField(columnName = "imsi")
	private String imsi;//手机imsi

	@DatabaseField(columnName = "mobile")
	private String mobile;

	@DatabaseField(columnName = "city_code")
	private String city_code;

	@DatabaseField(columnName = "item_id")
	private String item_id;

	@DatabaseField(columnName = "create_time")
	private String create_time;//应用开始下载时间

	@DatabaseField(columnName = "lat")
	private String lat;//维度

	@DatabaseField(columnName = "lng")
	private String lng;//经度

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getClient_type() {
		return client_type;
	}

	public void setClient_type(String client_type) {
		this.client_type = client_type;
	}

	public String getChannel_type() {
		return channel_type;
	}

	public void setChannel_type(String channel_type) {
		this.channel_type = channel_type;
	}

	public String getOs_type() {
		return os_type;
	}

	public void setOs_type(String os_type) {
		this.os_type = os_type;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
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

	public String getCity_code() {
		return city_code;
	}

	public void setCity_code(String city_code) {
		this.city_code = city_code;
	}

	public String getItem_id() {
		return item_id;
	}

	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
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
