package cn.ffcs.wisdom.lbs;

import java.io.Serializable;

/**
 * <h3>位置信息实体类  </h3>
 */
public class Address implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7594451023124819236L;

	private String country; // 国家
	private String country_code; // 国家编码
	private String region; // 省份
	private String city; // 市
	private String district;	//区
	private String street; // 街道
	private String street_number; // 门牌号

	/**
	 * 获取国家名称
	 * @return 返回国家名称
	 */
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * 获取国家编码
	 * @return 返回国家编码
	 */
	public String getCountry_code() {
		return country_code;
	}

	public void setCountry_code(String country_code) {
		this.country_code = country_code;
	}

	/**
	 * 获取省份名称
	 * @return 返回省份名称
	 */
	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	/**
	 * 获取地市名称
	 * @return 返回地市名称
	 */
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * 获取街道名称
	 * @return 返回街道名称
	 */
	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	/**
	 * 获取街道名称
	 * @return 返回地市名称
	 */
	public String getStreet_number() {
		return street_number;
	}

	public void setStreet_number(String street_number) {
		this.street_number = street_number;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

}
