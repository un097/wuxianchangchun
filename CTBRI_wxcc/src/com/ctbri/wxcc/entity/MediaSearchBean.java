package com.ctbri.wxcc.entity;

import java.util.List;

public class MediaSearchBean extends GsonContainer<MediaSearchBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 993511711436329625L;

	private int is_end; // 0代表结束，1为未结束,

	private List<VideoSearchEntity> videos;
	private List<VideoSearchEntity> audios;

	public List<VideoSearchEntity> getAudios() {
		return audios;
	}

	public void setAudios(List<VideoSearchEntity> audios) {
		this.audios = audios;
	}

	public int getIs_end() {
		return is_end;
	}

	public void setIs_end(int is_end) {
		this.is_end = is_end;
	}

	public List<VideoSearchEntity> getVideos() {
		return videos;
	}

	public void setVideos(List<VideoSearchEntity> videos) {
		this.videos = videos;
	}

	public static class VideoSearchEntity {
		private String id; // 视频ID,
		private String name; // 视频名称,
		private String time; // 播放时长hh; //mm; //ss,
		private String plays; // 播放数,
		private String likes; // 点赞数,
		private String comments; // 评论数,
		private String pic; // 图片URL

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

		public String getTime() {
			return time;
		}

		public void setTime(String time) {
			this.time = time;
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
