package com.ctbri.wxcc.entity;

import java.util.List;

import com.ctbri.wxcc.entity.VodDetailBean.VodDetailItem;

public class AudioVodDetail extends GsonContainer<AudioVodDetail> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 891840302852845776L;

	private String audio_id; // 026a99de7bd141f98cd7f854ced32d20,[音频ID],
	private String audio_name; // 百家讲坛,[音频名称],
	private int audio_type; // 1,[视频类型(1=单集,2=合集)],
	private String audio_desp; // 《百家讲坛》...,[音频介绍],
	private int audio_time; // 3600,[音频时长,单位秒],
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

	public String getGroup_name() {
		return group_name;
	}

	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}


	private List<VodDetailItem> audios;
	
	
	
	
	public List<VodDetailItem> getAudios() {
		return audios;
	}

	public void setAudios(List<VodDetailItem> audios) {
		this.audios = audios;
	}


	
	public String getAudio_id() {
		return audio_id;
	}


	public void setAudio_id(String audio_id) {
		this.audio_id = audio_id;
	}


	public String getAudio_name() {
		return audio_name;
	}


	public void setAudio_name(String audio_name) {
		this.audio_name = audio_name;
		
	}


	public int getAudio_type() {
		return audio_type;
	}


	public void setAudio_type(int audio_type) {
		this.audio_type = audio_type;
	}


	public String getAudio_desp() {
		return audio_desp;
	}


	public void setAudio_desp(String audio_desp) {
		this.audio_desp = audio_desp;
	}


	public int getAudio_time() {
		return audio_time;
	}


	public void setAudio_time(int audio_time) {
		this.audio_time = audio_time;
	}
	
	




}
