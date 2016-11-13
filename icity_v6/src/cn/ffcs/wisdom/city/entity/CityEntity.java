package cn.ffcs.wisdom.city.entity;

import java.io.Serializable;

import cn.ffcs.wisdom.city.sqlite.model.CityListInfo;

/**
 * <p>Title:  定位返回的城市实体类		   </p>
 * <p>Description:<br/>          
 * </p>
 * <p>@author: liaodl                  </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-1-28           </p>
 */
public class CityEntity implements Serializable {

	private static final long serialVersionUID = -4195246316483131827L;

	private String city_name;
	private String server_url;
	private String is_build;
	private String city_code;
	private String city_order;
	private String alter_msg;
	private String carType;
	private String zip;
	private String city_style;
	
	public String getCity_style() {
		return city_style;
	}

	public void setCity_style(String city_style) {
		this.city_style = city_style;
	}

	private String cityQuanPin;
	private String cityJianPin;

	public String getCity_name() {
		return city_name;
	}

	public void setCity_name(String city_name) {
		this.city_name = city_name;
	}

	public String getServer_url() {
		return server_url;
	}

	public void setServer_url(String server_url) {
		this.server_url = server_url;
	}

	public String getIs_build() {
		return is_build;
	}

	public void setIs_build(String is_build) {
		this.is_build = is_build;
	}

	public String getCity_code() {
		return city_code;
	}

	public void setCity_code(String city_code) {
		this.city_code = city_code;
	}

	public String getCity_order() {
		return city_order;
	}

	public void setCity_order(String city_order) {
		this.city_order = city_order;
	}

	public String getAlter_msg() {
		return alter_msg;
	}

	public void setAlter_msg(String alter_msg) {
		this.alter_msg = alter_msg;
	}
	
	/**
	 * 实体转换器 CityListInfo --> CityEntity
	 * @param info
	 * @return
	 */
	public static CityEntity converter(CityListInfo info) {
		CityEntity entity = new CityEntity();
		if (info != null) {
			entity.setCity_code(info.getCityCode());
			entity.setCity_name(info.getCityName());
			entity.setIs_build(info.getIsBuild());
			entity.setCity_order(info.getCityOrder());
			entity.setAlter_msg(info.getCityInfo());
			entity.setServer_url(info.getCityServerUrl());
		}
		return entity;
	}

	public String getCarType() {
		return carType;
	}

	public void setCarType(String carType) {
		this.carType = carType;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getCityQuanPin() {
		return cityQuanPin;
	}

	public void setCityQuanPin(String cityQuanPin) {
		this.cityQuanPin = cityQuanPin;
	}

	public String getCityJianPin() {
		return cityJianPin;
	}

	public void setCityJianPin(String cityJianPin) {
		this.cityJianPin = cityJianPin;
	}

}
