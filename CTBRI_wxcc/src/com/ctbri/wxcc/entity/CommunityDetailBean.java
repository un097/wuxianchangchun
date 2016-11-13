package com.ctbri.wxcc.entity;

public class CommunityDetailBean {

	private CommunityDetail data;

	
	public CommunityDetail getData() {
		return data;
	}


	public void setData(CommunityDetail data) {
		this.data = data;
	}


	public static class CommunityDetail{
		private String community_id;
		private String user_name;
		private String pic_url="";
		public String getCommunity_id() {
			return community_id;
		}
		public void setCommunity_id(String community_id) {
			this.community_id = community_id;
		}
		public String getUser_name() {
			return user_name;
		}
		public void setUser_name(String user_name) {
			this.user_name = user_name;
		}
		public String getPic_url() {
			return pic_url;
		}
		public void setPic_url(String pic_url) {
			this.pic_url = pic_url;
		}
		public String getDate_publish() {
			return date_publish;
		}
		public void setDate_publish(String date_publish) {
			this.date_publish = date_publish;
		}
		public String getComment_num() {
			return comment_num;
		}
		public void setComment_num(String comment_num) {
			this.comment_num = comment_num;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public String getCategory() {
			return category;
		}
		public void setCategory(String category) {
			this.category = category;
		}
		private String date_publish;
		private String comment_num = "0";
		private String content="";
		private String category;
		private String title;
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
	}
}
