package com.ctbri.wxcc.entity;

import java.io.Serializable;
import java.util.List;

public class CommunityBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8345584557483233586L;
	
	private Communitys data;
	
	
	public Communitys getData() {
		return data;
	}


	public void setData(Communitys data) {
		this.data = data;
	}

	public static class Communitys{
		private String column_id;
		private int is_end;
		private int last_index;
		private List<Community> news;
		public List<Community> getMy_news() {
			return my_news;
		}
		public void setMy_news(List<Community> my_news) {
			this.my_news = my_news;
		}
		private List<Community> my_news;
		
		
		public String getColumn_id() {
			return column_id;
		}
		public void setColumn_id(String column_id) {
			this.column_id = column_id;
		}
		public int getIs_end() {
			return is_end;
		}
		public void setIs_end(int is_end) {
			this.is_end = is_end;
		}
		public int getLast_index() {
			return last_index;
		}
		public void setLast_index(int last_index) {
			this.last_index = last_index;
		}
		public List<Community> getNews() {
			return news;
		}
		public void setNews(List<Community> news) {
			this.news = news;
		}
		
	}
	
	

	
	
	public static class Community{
		
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
		public String getDate_publish() {
			return date_publish;
		}
		public void setDate_publish(String date_publish) {
			this.date_publish = date_publish;
		}
		public List<String> getPreviews() {
			return previews;
		}
		public void setPreviews(List<String> previews) {
			this.previews = previews;
		}
		private String community_id;
		private String user_name;
		private String pic_url="";
		private String comment_num;
		private String content;
		private String date_publish;
		private List<String> previews;
		private String audit_status;
		private String category;
		private String my_id;
		public String getMy_id() {
			return my_id;
		}
		public void setMy_id(String my_id) {
			this.my_id = my_id;
		}
		public String getCategory() {
			return category;
		}
		public void setCategory(String category) {
			this.category = category;
		}
		public String getAudit_status() {
			return audit_status;
		}
		public void setAudit_status(String audit_status) {
			this.audit_status = audit_status;
		}
		
	}
	

	
}
