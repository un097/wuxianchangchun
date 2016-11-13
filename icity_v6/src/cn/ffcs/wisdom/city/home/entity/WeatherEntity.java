package cn.ffcs.wisdom.city.home.entity;

import java.io.Serializable;

/**
 * <p>Title:天气实体对象          </p>
 * <p>Description: 
 * </p>
 * <p>@author: Leo                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-5-21             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class WeatherEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	private WeatherData data;

	public WeatherData getData() {
		if (data == null) {
			data = new WeatherData();
		}
		return data;
	}

	public void setData(WeatherData data) {
		this.data = data;
	}

	public class WeatherData implements Serializable{
		private static final long serialVersionUID = 1L;
		
		private String higt;// 高温
		private String wd_icon;// 首页图片
		private String wd;// 天气情况
		private String url;// 知天气信源地址
		private String lowt;// 低温
		private int temperatureContrast;// 温度差
		private int hightOrLow;// 高了还是低了，1：低，2：高
		private String aqi;// 污染描述
		private int aqiLevel;// 污染等级
		
		public String getHigt() {
			return higt;
		}

		public void setHigt(String higt) {
			this.higt = higt;
		}

		public String getWd_icon() {
			return wd_icon;
		}

		public void setWd_icon(String wd_icon) {
			this.wd_icon = wd_icon;
		}

		public String getWd() {
			return wd;
		}

		public void setWd(String wd) {
			this.wd = wd;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getLowt() {
			return lowt;
		}

		public void setLowt(String lowt) {
			this.lowt = lowt;
		}

		public int getTemperatureContrast() {
			return temperatureContrast;
		}

		public void setTemperatureContrast(int temperatureContrast) {
			this.temperatureContrast = temperatureContrast;
		}

		public int getHightOrLow() {
			return hightOrLow;
		}

		public void setHightOrLow(int hightOrLow) {
			this.hightOrLow = hightOrLow;
		}

		public String getAqi() {
			return aqi;
		}

		public void setAqi(String aqi) {
			this.aqi = aqi;
		}

		public int getAqiLevel() {
			return aqiLevel;
		}

		public void setAqiLevel(int aqiLevel) {
			this.aqiLevel = aqiLevel;
		}
	}
}
