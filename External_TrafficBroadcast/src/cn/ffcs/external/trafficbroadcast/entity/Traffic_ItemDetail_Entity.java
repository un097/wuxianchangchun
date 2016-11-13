package cn.ffcs.external.trafficbroadcast.entity;

/**
 * 路况详情类
 * 
 * @author daizhq
 * 
 * @date 2014.12.05
 * */
public class Traffic_ItemDetail_Entity {

	// 主键ID
	private int id;
	// 路况标题
	private String title;
	// 路况详情
	private String detail;
	//新路况状态：1顺畅、2缓慢、3拥堵、4道路封闭、5事故、6警察执法
	// 路况状态：1顺畅、2缓慢、3道路封闭、4拥堵、5事故、6警察执法
	private int status;
	// 路况状态描述，与路况状态一一对应：顺畅、缓慢、道路封闭、拥堵、事故、警察执法
	private String status_desc;
	// 经度
	private String lng;
	// 纬度
	private String lat;
	// 路况更新时间，格式为yyyy-MM-dd HH:mm:ss，例如：2008-01-25 20:23:30
	private String update_time;
	// 点赞数
	private int praise_num;
	// 图片相对地址
	private String[] pic_uri;
	// 来源，0官方、1用户
	private String source;
	// 爆料用户号码，来源为用户时才有
	private String mobile;
	// 用户头像地址
	private String head_pic;

	public String getHead_pic() {
		return head_pic;
	}

	public void setHead_pic(String head_pic) {
		this.head_pic = head_pic;
	}

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

	public String getLng() {
		return lng;
	}

	public String getStatus_desc() {
		return status_desc;
	}

	public void setStatus_desc(String status_desc) {
		this.status_desc = status_desc;
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

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

	public int getPraise_num() {
		return praise_num;
	}

	public void setPraise_num(int praise_num) {
		this.praise_num = praise_num;
	}

	public String[] getPic_uri() {
		return pic_uri;
	}

	public void setPic_uri(String[] pic_uri) {
		this.pic_uri = pic_uri;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

}
