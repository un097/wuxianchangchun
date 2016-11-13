package com.ctbri.wxcc.entity;

import java.util.List;

import com.ctbri.wxcc.entity.ShortListBean.ShortListData;

public class ShortListBean extends GsonContainer<ShortListData> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3332274258757327750L;

	public static class ShortListData {
		private int is_end;
		private List<ShortVideo> video_list;

		public int getIs_end() {
			return is_end;
		}

		public void setIs_end(int is_end) {
			this.is_end = is_end;
		}

		public List<ShortVideo> getVideo_list() {
			return video_list;
		}

		public void setVideo_list(List<ShortVideo> video_list) {
			this.video_list = video_list;
		}

	}

	public static class ShortVideo {
		private String video_id;
		private String video_name;
		private String play_count;
		private String praise_count;
		private String comment_count;
		private String video_time;
		private String pic_url = "";
		private String play_url = "";
		public boolean isPlaying;
		public long playPosition;
		public int state;
		private String play_times;// ": "播放次数",
		private String praise_times;// ": "点赞次数",
		private String comment_times;// ": "评论次数",

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

		public String getVideo_time() {
			return video_time;
		}

		public void setVideo_time(String video_time) {
			this.video_time = video_time;
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

		public String getPlay_count() {
			return play_count;
		}

		public void setPlay_count(String play_count) {
			this.play_count = play_count;
		}

		public String getPraise_count() {
			return praise_count;
		}

		public void setPraise_count(String praise_count) {
			this.praise_count = praise_count;
		}

		public String getComment_count() {
			return comment_count;
		}

		public void setComment_count(String comment_count) {
			this.comment_count = comment_count;
		}

		public String getPic_url() {
			return pic_url;
		}

		public void setPic_url(String pic_url) {
			this.pic_url = pic_url;
		}

		public String getPlay_url() {
			return play_url;
		}

		public void setPlay_url(String play_url) {
			this.play_url = play_url;
		}

	}
}
