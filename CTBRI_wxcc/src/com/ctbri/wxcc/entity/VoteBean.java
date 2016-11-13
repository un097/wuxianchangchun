package com.ctbri.wxcc.entity;

import java.util.List;

public class VoteBean {

	private VoteContainer data;

	public VoteContainer getData() {
		return data;
	}

	public void setData(VoteContainer data) {
		this.data = data;
		System.out.println("调用  setData...........++-=-=-=");
	}

	public static class VoteContainer {
		private int is_end;
		private List<Vote> vote_list;
		public int getIs_end() {
			return is_end;
		}
		public void setIs_end(int is_end) {
			this.is_end = is_end;
		}
		public List<Vote> getVote_list() {
			return vote_list;
		}
		public void setVote_list(List<Vote> vote_list) {
			this.vote_list = vote_list;
		}
	}

	public static class Vote {
		private String title;
		private String img_rel="";
		private String vote_num;
		private String vote_res;
		private String investigation_id;
		public String getInvestigation_id() {
			return investigation_id;
		}
		public void setInvestigation_id(String investigation_id) {
			this.investigation_id = investigation_id;
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
		private int status;

	}
}
