package cn.ffcs.wisdom.city.entity;

import java.io.Serializable;

/**
 * <p>Title:日志实体类          </p>
 * <p>Description: 
 * 通过组成一个json串，在系统启动的时候添加进来
 * </p>
 * <p>@author: Leo                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-9-3             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class LogReportEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String product_id;// 产品标示 icity 爱城市
	private String client_type;// 客户端类型 icity_ver 爱城市 icity_chongqing_ver 智慧重庆
	private String client_version;// 客户端版本号
	private String client_channel_type;// 客户端渠道类型
	private String os_type;// 操作系统类型 1 android 2ios 3wap 4web
	private String imei;// 手机设备号
	private String imsi;// 手机IMSI号
	private String mobile;// 手机号
	private String city_code;// 城市编号
	private String longitude;// 经度
	private String latitude;// 纬度
	private String area_id;// 地区区域ID
	private String item_type;// 项目类型1banner 2 menu 3widget
	private String item_id;// 项目ID,如广告ID,bannerID等
	private String sub_item_id;// 子项目ID,如广告的图片1等
	private String desc;// 描述信息
	private String timestamp;// 点击时间
	public String getProduct_id() {
		return product_id;
	}
	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}
	public String getClient_type() {
		return client_type;
	}
	public void setClient_type(String client_type) {
		this.client_type = client_type;
	}
	public String getClient_version() {
		return client_version;
	}
	public void setClient_version(String client_version) {
		this.client_version = client_version;
	}
	public String getClient_channel_type() {
		return client_channel_type;
	}
	public void setClient_channel_type(String client_channel_type) {
		this.client_channel_type = client_channel_type;
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
	public String getArea_id() {
		return area_id;
	}
	public void setArea_id(String area_id) {
		this.area_id = area_id;
	}
	public String getItem_type() {
		return item_type;
	}
	public void setItem_type(String item_type) {
		this.item_type = item_type;
	}
	public String getItem_id() {
		return item_id;
	}
	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}
	public String getSub_item_id() {
		return sub_item_id;
	}
	public void setSub_item_id(String sub_item_id) {
		this.sub_item_id = sub_item_id;
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
