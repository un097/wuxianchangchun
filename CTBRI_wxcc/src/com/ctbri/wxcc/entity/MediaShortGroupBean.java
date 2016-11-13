package com.ctbri.wxcc.entity;

import java.util.List;

public class MediaShortGroupBean extends GsonContainer<MediaShortGroupBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3155998007867412620L;

	private List<ShortVideoGroup> short_group;
	
	
	public List<ShortVideoGroup> getShort_group() {
		return short_group;
	}
	public void setShort_group(List<ShortVideoGroup> short_group) {
		this.short_group = short_group;
	}
	public static class ShortVideoGroup{
		private String group_name;
		private String group_id;
		private List<ShortVideo> short_list;
		public String getGroup_name() {
			return group_name;
		}
		public void setGroup_name(String group_name) {
			this.group_name = group_name;
		}
		public String getGroup_id() {
			return group_id;
		}
		public void setGroup_id(String group_id) {
			this.group_id = group_id;
		}
		public List<ShortVideo> getShort_list() {
			return short_list;
		}
		public void setShort_list(List<ShortVideo> short_list) {
			this.short_list = short_list;
		}
		
		
	}
	public static class ShortVideo{
		private String short_id="";
        private String short_name;
        private String short_time;
        private String short_url;
        private String play_count;
        private String praise_count;
        private String play_url;
        
		public String getShort_time() {
			return short_time;
		}
		public void setShort_time(String short_time) {
			this.short_time = short_time;
		}
		public String getShort_id() {
			return short_id;
		}
		public void setShort_id(String short_id) {
			this.short_id = short_id;
		}
		public String getShort_name() {
			return short_name;
		}
		public void setShort_name(String short_name) {
			this.short_name = short_name;
		}
		public String getShort_url() {
			return short_url;
		}
		public void setShort_url(String short_url) {
			this.short_url = short_url;
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
		public String getPlay_url() {
			return play_url;
		}
		public void setPlay_url(String play_url) {
			this.play_url = play_url;
		}
        
        
	}
}
