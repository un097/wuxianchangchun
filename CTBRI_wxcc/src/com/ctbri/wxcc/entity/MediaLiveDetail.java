package com.ctbri.wxcc.entity;

import java.util.List;

public class MediaLiveDetail extends GsonContainer<MediaLiveDetail> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5995604954167401309L;
	private String channel_id;
	private String channel_name;
	private String channel_code;
	private String channel_type;
	private String live;
	private String pic;
	private String program_id;
	private String program_name;
	private String plays;
	private String coupon_id;
	private String coupon_name;
	private String coupon_pic;
	private String coupon_validity;
	private List<Program> program_list;
	
	
	
	
	public String getChannel_id() {
		return channel_id;
	}




	public void setChannel_id(String channel_id) {
		this.channel_id = channel_id;
	}




	public String getChannel_name() {
		return channel_name;
	}




	public void setChannel_name(String channel_name) {
		this.channel_name = channel_name;
	}




	public String getChannel_code() {
		return channel_code;
	}




	public void setChannel_code(String channel_code) {
		this.channel_code = channel_code;
	}




	public String getChannel_type() {
		return channel_type;
	}




	public void setChannel_type(String channel_type) {
		this.channel_type = channel_type;
	}




	public String getLive() {
		return live;
	}




	public void setLive(String live) {
		this.live = live;
	}




	public String getPic() {
		return pic;
	}




	public void setPic(String pic) {
		this.pic = pic;
	}




	public String getProgram_id() {
		return program_id;
	}




	public void setProgram_id(String program_id) {
		this.program_id = program_id;
	}




	public String getProgram_name() {
		return program_name;
	}




	public void setProgram_name(String program_name) {
		this.program_name = program_name;
	}




	public String getPlays() {
		return plays;
	}




	public void setPlays(String plays) {
		this.plays = plays;
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




	public List<Program> getProgram_list() {
		return program_list;
	}




	public void setProgram_list(List<Program> program_list) {
		this.program_list = program_list;
	}




	public static class Program {
		public static final int STATUS_BROADCAST = 1;
		public static final int STATUS_NO_BROADCAST = 0;
		private String name;
		private String time;
		private int status;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getTime() {
			return time;
		}
		public void setTime(String time) {
			this.time = time;
		}
		/**
		 *  "0,[0=即将播放,1=正在直播]"
		 */
		public int getStatus() {
			return status;
		}
		public void setStatus(int status) {
			this.status = status;
		} 
		
		
	}
}
