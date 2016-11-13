package cn.ffcs.external.trafficbroadcast.entity;

/**
 * 全部路况列表中单条路况详情类
 * 
 * @author daizhq
 * 
 * @date 2014.12.05
 * */
public class Traffic_AllItem_Entity {

	//主键ID
	private int id;
	//路况标题
	private String title;
	//路况详情
	private String detail;
	//新路况状态：1顺畅、2缓慢、3拥堵、4道路封闭、5事故、6警察执法
	//路况状态：1顺畅、2缓慢、3道路封闭、4拥堵、5事故、6警察执法
	private int status;
	//路况状态描述，与路况状态一一对应：顺畅、缓慢、道路封闭、拥堵、事故、警察执法
	private String status_disc;
	//是否被收藏：1是、0否
	private int is_collected;
	//间隔时间，当前时间-更新时间，单位分钟，0表示1分钟内
	private int interval_time;
	//经度
	private String lng;
	//纬度
	private String lat;
	//来源，0官方、1用户
	private String source;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
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

	public int getIs_collected() {
		return is_collected;
	}

	public void setIs_collected(int is_collected) {
		this.is_collected = is_collected;
	}

	public int getInterval_time() {
		return interval_time;
	}

	public void setInterval_time(int interval_time) {
		this.interval_time = interval_time;
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

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
}
