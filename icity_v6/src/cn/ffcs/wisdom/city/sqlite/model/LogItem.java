package cn.ffcs.wisdom.city.sqlite.model;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "t_log_info")
public class LogItem implements Serializable{

	private static final long serialVersionUID = 1L;
	@DatabaseField(generatedId = true, columnName = "log_id")
	private int id;
	
	@DatabaseField(columnName = "product_id")
	private String productId;//产品标示 icity 爱城市

	
	@DatabaseField(columnName = "client_type")
	private String clientType;//客户端类型 icity_ver 爱城市 icity_chongqing_ver 智慧重庆

	
	@DatabaseField(columnName = "client_version")
	private String clientVersion;//客户端版本号
	
	@DatabaseField(columnName = "client_channel_type")
	private String clientChannelType;//客户端渠道类型
	
	@DatabaseField(columnName = "os_type")
	private String osType;//操作系统类型: 1 android  2 ios 3 wap  4web

	
	@DatabaseField(columnName = "imei")
	private String imei;//手机设备号
	
	@DatabaseField(columnName = "imsi")
	private String imsi;//手机imsi
	
	@DatabaseField(columnName = "mobile")
	private String mobile;
	
	@DatabaseField(columnName = "city_code")
	private String cityCode;
	
	@DatabaseField(columnName = "logitude")
	private String longitude;
	
	@DatabaseField(columnName = "latitude")
	private String latitude;
	
	@DatabaseField(columnName = "area_id")
	private String areaId;
	
	@DatabaseField(columnName = "item_type")
	private String itemType;
	
	@DatabaseField(columnName = "item_id")
	private String itemId;
	
	@DatabaseField(columnName = "sub_item_id")
	private String subItemId;
	
	@DatabaseField(columnName = "desc")
	private String desc;
	
	@DatabaseField(columnName = "add_time")
	private String timestamp;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getClientType() {
		return clientType;
	}

	public void setClientType(String clientType) {
		this.clientType = clientType;
	}

	public String getClientVersion() {
		return clientVersion;
	}

	public void setClientVersion(String clientVersion) {
		this.clientVersion = clientVersion;
	}

	public String getClientChannelType() {
		return clientChannelType;
	}

	public void setClientChannelType(String clientChannelType) {
		this.clientChannelType = clientChannelType;
	}

	public String getOsType() {
		return osType;
	}

	public void setOsType(String osType) {
		this.osType = osType;
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

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getAreaId() {
		return areaId;
	}

	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getSubItemId() {
		return subItemId;
	}

	public void setSubItemId(String subItemId) {
		this.subItemId = subItemId;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	
}
