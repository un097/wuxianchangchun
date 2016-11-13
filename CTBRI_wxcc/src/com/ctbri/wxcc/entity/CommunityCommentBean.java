package com.ctbri.wxcc.entity;

import java.util.List;

public class CommunityCommentBean {
		
		private CommunityCommentContaienr data;
		
        
        public CommunityCommentContaienr getData() {
			return data;
		}

		public void setData(CommunityCommentContaienr data) {
			this.data = data;
		}

		public static class CommunityCommentContaienr{
        	private int type;
        	private int is_end;
        	private int last_index;
        	public int getType() {
				return type;
			}
			public void setType(int type) {
				this.type = type;
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
			public String getCommunity_id() {
				return community_id;
			}
			public void setCommunity_id(String community_id) {
				this.community_id = community_id;
			}
			public List<CommunityComment> getCommList() {
				return commList;
			}
			public void setCommList(List<CommunityComment> commList) {
				this.commList = commList;
			}
			private String community_id;
        	private List<CommunityComment> commList;
        }
        
        public static class CommunityComment{
        	private String comment_id;
        	private String user_name;
        	private String create_time;
        	public String getCreate_time() {
				return create_time;
			}
			public void setCreate_time(String create_time) {
				this.create_time = create_time;
			}
			private String pic_url;
        	private String comment_zan_num = "0";
        	public String getComment_id() {
				return comment_id;
			}
			public void setComment_id(String comment_id) {
				this.comment_id = comment_id;
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
			public String getComment_zan_num() {
				return comment_zan_num;
			}
			public void setComment_zan_num(String comment_zan_num) {
				this.comment_zan_num = comment_zan_num;
			}
			public String getContent() {
				return content;
			}
			public void setContent(String content) {
				this.content = content;
			}
			public String getStatus() {
				return status;
			}
			public void setStatus(String status) {
				this.status = status;
			}
			private String content;
        	private String status;
        	
        }
}
