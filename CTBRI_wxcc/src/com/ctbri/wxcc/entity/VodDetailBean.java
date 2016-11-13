package com.ctbri.wxcc.entity;

import java.io.Serializable;
import java.util.List;

public class VodDetailBean extends GsonContainer<VodDetailBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7344997162965841356L;
	private String video_id;
	private String video_name;
	private int video_type;
	private String  video_desp; // 《奔跑吧兄弟第二季》...,[视频介绍],
	private String   video_time; // 3600,[视频时长,单位秒],
	private String vod; // http; ////xxxx/xxx/xxx,[点播地址],
	private String pic; // http; ////xxxx/xxx/xxx.jpg,[点播节目图片],
	private String  plays; // 100,[播放数],
	private String likes; // 100,[点赞数],
	private int  comments; // 100,[评论数],
	private String coupon_id; // 01718a7e132d4a63a1885816c7ee2b70,[优惠券ID],
	private String  coupon_name; // 香辣鸡腿堡,[优惠券名称],
	private String  coupon_pic; // http; ////xxxx/xxx/xxx.jpg,[优惠券图片],
	private String coupon_validity; // 有效期2015-01-01至2015-05-01,[优惠券有效期],
	private String group_name;
	
	private List<VodDetailItem>  videos;
	
	
	

    public String getGroup_name() {
		return group_name;
	}



	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}



	public String getVideo_id() {
		return video_id;
	}



	public void setVideo_id(String video_id) {
		this.video_id = video_id;
	}



	public String getVideo_name() {
		return video_name;
	}



	public void setVideo_name(String video_name) {
		this.video_name = video_name;
	}



	public int getVideo_type() {
		return video_type;
	}



	public void setVideo_type(int video_type) {
		this.video_type = video_type;
	}



	public String getVideo_desp() {
		return video_desp;
	}



	public void setVideo_desp(String video_desp) {
		this.video_desp = video_desp;
	}



	public String getVideo_time() {
		return video_time;
	}



	public void setVideo_time(String video_time) {
		this.video_time = video_time;
	}



	public String getVod() {
		return vod;
	}



	public void setVod(String vod) {
		this.vod = vod;
	}



	public String getPic() {
		return pic;
	}



	public void setPic(String pic) {
		this.pic = pic;
	}



	public String getPlays() {
		return plays;
	}



	public void setPlays(String plays) {
		this.plays = plays;
	}



	public String getLikes() {
		return likes;
	}



	public void setLikes(String likes) {
		this.likes = likes;
	}



	public int getComments() {
		return comments;
	}



	public void setComments(int comments) {
		this.comments = comments;
	}



	public String getCoupon_id() {
		return coupon_id;
	}



	public void setCoupon_id(String coupon_id) {
		this.coupon_id = coupon_id;
	}



	public String getCoupon_name() {
		return coupon_name;
	}



	public void setCoupon_name(String coupon_name) {
		this.coupon_name = coupon_name;
	}



	public String getCoupon_pic() {
		return coupon_pic;
	}



	public void setCoupon_pic(String coupon_pic) {
		this.coupon_pic = coupon_pic;
	}



	public String getCoupon_validity() {
		return coupon_validity;
	}



	public void setCoupon_validity(String coupon_validity) {
		this.coupon_validity = coupon_validity;
	}



	public List<VodDetailItem> getVideos() {
		return videos;
	}



	public void setVideos(List<VodDetailItem> videos) {
		this.videos = videos;
	}



	public static class VodDetailItem implements Serializable{
		private String   seq; // 2,[第2集],
		private String  name; // 奔跑吧兄弟之北京站,[分集名称],
		private String  video_time; // 3600,[视频时长,单位秒],
		private String   vod; // http; ////xxxx/xxx/xxx,[点播地址]
		public String getSeq() {
			return seq;
		}
		public void setSeq(String seq) {
			this.seq = seq;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getVideo_time() {
			return video_time;
		}
		public void setVideo_time(String video_time) {
			this.video_time = video_time;
		}
		public String getVod() {
			return vod;
		}
		public void setVod(String vod) {
			this.vod = vod;
		}
		
		
	}
}
