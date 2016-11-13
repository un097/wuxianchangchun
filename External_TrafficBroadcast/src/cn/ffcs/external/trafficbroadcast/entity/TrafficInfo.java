package cn.ffcs.external.trafficbroadcast.entity;

/**
 * 路况详情类
 * 
 * @author daizhq
 * 
 * @date 2014.12.01
 * 
 * */
public class TrafficInfo {

	private String location;
	private String distance;
	private String detail;
	private String time;
	
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getDistance() {
		return distance;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
}
