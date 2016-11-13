package cn.ffcs.external.trafficbroadcast.entity;

/**
 * 附近路况简单信息列表中单条路况详情类 （即地图页面上的打点列表）
 * 
 * @author daizhq
 * 
 * @date 2014.12.05
 * */
public class Traffic_SimpleItem_Entity {

	// 主键ID
	private int id;
	//新路况状态：1顺畅、2缓慢、3拥堵、4道路封闭、5事故、6警察执法
	// 路况状态：1顺畅、2缓慢、3道路封闭、4拥堵、5事故、6警察执法
	private int status;
	// 路况状态描述，与路况状态一一对应：顺畅、缓慢、道路封闭、拥堵、事故、警察执法
	private String status_disc;
	// 经度
	private String lng;
	// 纬度
	private String lat;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getStatus_disc() {
		return status_disc;
	}

	public void setStatus_disc(String status_disc) {
		this.status_disc = status_disc;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

}
