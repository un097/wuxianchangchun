package com.ctbri.wxcc.entity;

import java.util.List;

public class MediaPlayListBean extends GsonContainer<MediaPlayListBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2344890312816434969L;

	private int is_end;
	private List<PlayItem> video_list;
	
	private String group_desp;
	private String group_id;
	private String group_name = "";
	private String group_pic="";
	

	
	public String getGroup_desp() {
		return group_desp;
	}


	public void setGroup_desp(String group_desp) {
		this.group_desp = group_desp;
	}


	public String getGroup_id() {
		return group_id;
	}


	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}


	public String getGroup_name() {
		return group_name;
	}


	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}


	public String getGroup_pic() {
		return group_pic;
	}


	public void setGroup_pic(String group_pic) {
		this.group_pic = group_pic;
	}


	public int getIs_end() {
		return is_end;
	}


	public void setIs_end(int is_end) {
		this.is_end = is_end;
	}


	public List<PlayItem> getVideo_list() {
		return video_list;
	}


	public void setVideo_list(List<PlayItem> video_list) {
		this.video_list = video_list;
	}


	public static class PlayItem {
		private String video_id;
		private String video_name;
		private String play_duration;
		private String play_times;
		private String praise_times;
		private String comment_times;
		private String pic_url;
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
		public String getPlay_duration() {
			return play_duration;
		}
		public void setPlay_duration(String play_duration) {
			this.play_duration = play_duration;
		}
		public String getPlay_times() {
			return play_times;
		}
		public void setPlay_times(String play_times) {
			this.play_times = play_times;
		}
		public String getPraise_times() {
			return praise_times;
		}
		public void setPraise_times(String praise_times) {
			this.praise_times = praise_times;
		}
		public String getComment_times() {
			return comment_times;
		}
		public void setComment_times(String comment_times) {
			this.comment_times = comment_times;
		}
		public String getPic_url() {
			return pic_url;
		}
		public void setPic_url(String pic_url) {
			this.pic_url = pic_url;
		}
		
		
	}

}
