package com.ctbri.wxcc.entity;

import java.util.List;

public class VoteDetailBean {
	
	private VoteDetailContainer data;

	public VoteDetailContainer getData() {
		return data;
	}

	public void setData(VoteDetailContainer data) {
		this.data = data;
	}

	public static class ResItem {
		private String res_content;
		private String res_o;
		public String getRes_content() {
			return res_content;
		}
		public void setRes_content(String res_content) {
			this.res_content = res_content;
		}
		public String getRes_o() {
			return res_o;
		}
		public void setRes_o(String res_o) {
			this.res_o = res_o;
		}
	}

	public static class Item {
		private String item_id;
		private int isChecked;
		public int getIsChecked() {
			return isChecked;
		}
		public void setIsChecked(int isChecked) {
			this.isChecked = isChecked;
		}
		public String getItem_id() {
			return item_id;
		}
		public void setItem_id(String item_id) {
			this.item_id = item_id;
		}
		public String getItem_content() {
			return item_content;
		}
		public void setItem_content(String item_content) {
			this.item_content = item_content;
		}
		private String item_content;
	}

	public static class VoteDetailContainer {
		private String title;
		private String img_rel="";
		private String vote_num;
		private String vote_res;
		private int status;
		private String content;
		private String comment_num = "0";
		public String getComment_num() {
			return comment_num;
		}
		public void setComment_num(String comment_num) {
			this.comment_num = comment_num;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getImg_rel() {
			return img_rel;
		}
		public void setImg_rel(String img_rel) {
			this.img_rel = img_rel;
		}
		public String getVote_num() {
			return vote_num;
		}
		public void setVote_num(String vote_num) {
			this.vote_num = vote_num;
		}
		public String getVote_res() {
			return vote_res;
		}
		public void setVote_res(String vote_res) {
			this.vote_res = vote_res;
		}
		public int getStatus() {
			return status;
		}
		public void setStatus(int status) {
			this.status = status;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public int getHas_res() {
			return has_res;
		}
		public void setHas_res(int has_res) {
			this.has_res = has_res;
		}
		public int getType() {
			return type;
		}
		public void setType(int type) {
			this.type = type;
		}
		public List<ResItem> getRes_item() {
			return res_item;
		}
		public void setRes_item(List<ResItem> res_item) {
			this.res_item = res_item;
		}
		public List<Item> getItem() {
			return item;
		}
		public void setItem(List<Item> item) {
			this.item = item;
		}
		private int has_res;
		private int type;
		private List<ResItem> res_item;
		private List<Item> item;
	}
}
