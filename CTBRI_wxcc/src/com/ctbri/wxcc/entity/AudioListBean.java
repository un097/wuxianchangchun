package com.ctbri.wxcc.entity;

import java.util.List;

public class AudioListBean extends GsonContainer<AudioListBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7283735485133103646L;
	private int is_end; // 0代表结束，1为未结束,
	private String group_id; // 分类ID,
	private String group_name; // 分类名称,
	private String group_desp; // 分类简介,
	private String group_pic; // 分类图片地址,
	private List<AudioListItem> audios;
	

	public int getIs_end() {
		return is_end;
	}



	public void setIs_end(int is_end) {
		this.is_end = is_end;
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



	public String getGroup_desp() {
		return group_desp;
	}



	public void setGroup_desp(String group_desp) {
		this.group_desp = group_desp;
	}



	public String getGroup_pic() {
		return group_pic;
	}



	public void setGroup_pic(String group_pic) {
		this.group_pic = group_pic;
	}



	public List<AudioListItem> getAudios() {
		return audios;
	}



	public void setAudios(List<AudioListItem> audios) {
		this.audios = audios;
	}



	public static class AudioListItem {
		private String id; // 音频ID,
		private String name; // 音频名称,
		private String times; // 播放时长hh; //mm; //ss,
		private String plays; // 播放次数,
		private String likes; // 点赞次数,
		private String comments; // 评论次数,
		private String pic; // 图片url
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
		public String getTimes() {
			return times;
		}
		public void setTimes(String times) {
			this.times = times;
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
		public String getComments() {
			return comments;
		}
		public void setComments(String comments) {
			this.comments = comments;
		}
		public String getPic() {
			return pic;
		}
		public void setPic(String pic) {
			this.pic = pic;
		}
		
		
	}

}
