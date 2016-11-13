package cn.ffcs.wisdom.city.sqlite.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "t_report")
public class Report {

	@Override
	public String toString() {
		return "{imsi:" + imsi + " " + "model:" + model + " " + "mobileName:" + mobileName + " " + "imei:"
				+ imei + " " + "accessType:" + accessType + " " + "carieer:" + carieer + " "
				+ "manufacturer:" + manufacturer + " " + "macAddress:" + macAddress + "cityCode:" + cityCode
				+ "}";
	}
	
	public String toLogString() {
		return "{mac_address:" + macAddress + " " + "imsi:" + imsi + " " + "channel_num:"
				+ channelNum + " " + "menu_version:" + menuVersion + " " + "osType:" + osType + ""
				+ "model:" + model + " " + "mobileName:" + mobileName + " " + "systemName:"
				+ systemName + " " + "systemVersion:" + systemVersion + " " + "imei:" + imei + " "
				+ "accessType:" + accessType + " " + "carieer:" + carieer + " " + "manufacturer:"
				+ manufacturer + " " + "appversion:" + appversion + " " + "appversion_name:"
				+ appversionName + " " + "lng:" + lng + " " + "lat:" + lat + " " + "mobile:"
				+ mobile+ " " + "client_type:" + clientType + " " + "cityCode:" + cityCode + "}";

	}

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField(columnName = "time")
	private String time;

	@DatabaseField(columnName = "mac_address")
	private String macAddress;

	@DatabaseField(columnName = "imsi")
	private String imsi;

	@DatabaseField(columnName = "channel_num")
	private String channelNum;

	@DatabaseField(columnName = "menu_version")
	private String menuVersion;

	@DatabaseField(columnName = "os_type")
	private String osType;

	@DatabaseField(columnName = "mobile_name")
	private String mobileName;

	@DatabaseField(columnName = "system_name")
	private String systemName;

	@DatabaseField(columnName = "system_version")
	private String systemVersion;

	@DatabaseField(columnName = "model")
	private String model;

	@DatabaseField(columnName = "carieer")
	private String carieer;

	@DatabaseField(columnName = "manufacturer")
	private String manufacturer;

	@DatabaseField(columnName = "appversion")
	private String appversion;

	@DatabaseField(columnName = "appversion_name")
	private String appversionName;

	@DatabaseField(columnName = "lat")
	private String lat;

	@DatabaseField(columnName = "lng")
	private String lng;

	@DatabaseField(columnName = "access_type")
	private String accessType;

	@DatabaseField(columnName = "mobile")
	private String mobile;

	@DatabaseField(columnName = "imei")
	private String imei;

	@DatabaseField(columnName = "client_type")
	private String clientType;

	@DatabaseField(columnName = "city_code")
	private String cityCode;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getImsi() {
		return imsi;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

	public String getChannelNum() {
		return channelNum;
	}

	public void setChannelNum(String channelNum) {
		this.channelNum = channelNum;
	}

	public String getMenuVersion() {
		return menuVersion;
	}

	public void setMenuVersion(String menuVersion) {
		this.menuVersion = menuVersion;
	}

	public String getOsType() {
		return osType;
	}

	public void setOsType(String osType) {
		this.osType = osType;
	}

	public String getMobileName() {
		return mobileName;
	}

	public void setMobileName(String mobileName) {
		this.mobileName = mobileName;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public String getSystemVersion() {
		return systemVersion;
	}

	public void setSystemVersion(String systemVersion) {
		this.systemVersion = systemVersion;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getCarieer() {
		return carieer;
	}

	public void setCarieer(String carieer) {
		this.carieer = carieer;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getAppversion() {
		return appversion;
	}

	public void setAppversion(String appversion) {
		this.appversion = appversion;
	}

	public String getAppversionName() {
		return appversionName;
	}

	public void setAppversionName(String appversionName) {
		this.appversionName = appversionName;
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

	public String getAccessType() {
		return accessType;
	}

	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getClientType() {
		return clientType;
	}

	public void setClientType(String clientType) {
		this.clientType = clientType;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

}
