package cn.ffcs.wisdom.city.sqlite.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "t_city_config")
public class CityConfig {

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField(columnName = "city_code")
	private String cityCode; // 城市编码

	@DatabaseField(columnName = "splash_bg")
	private String splashBg; // 闪屏图片

	@DatabaseField(columnName = "city_name")
	private String cityName; // 城市名称

	@DatabaseField(columnName = "province_name")
	private String provinceName; // 省份名称

	@DatabaseField(columnName = "tyjx_code")
	private String tyjxCode; // 天翼景象编码

	/**
	 * add by caijj 2013-10-16
	 */
	@DatabaseField(columnName = "tyjx_image")
	String tyjx_image; // 天翼景象广告图片

	@DatabaseField(columnName = "tyjx_duration")
	String tyjx_duration; // 天翼景象广告图片播放时间

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSplashBg() {
		return splashBg;
	}

	public void setSplashBg(String splashBg) {
		this.splashBg = splashBg;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getTyjxCode() {
		return tyjxCode;
	}

	public void setTyjxCode(String tyjxCode) {
		this.tyjxCode = tyjxCode;
	}

	public String getTyjx_image() {
		return tyjx_image;
	}

	public void setTyjx_image(String tyjx_image) {
		this.tyjx_image = tyjx_image;
	}

	public String getTyjx_duration() {
		return tyjx_duration;
	}

	public void setTyjx_duration(String tyjx_duration) {
		this.tyjx_duration = tyjx_duration;
	}

}
