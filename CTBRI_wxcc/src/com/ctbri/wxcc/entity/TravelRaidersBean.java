package com.ctbri.wxcc.entity;

import java.util.List;

public class TravelRaidersBean extends GsonContainer<TravelRaidersBean.RaidersContainer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 200361864343724902L;
	
	public static class RaidersContainer{
		private List<Raiders> travel_list;
		private int is_end;
		private int type;
		public List<Raiders> getTravel_list() {
			return travel_list;
		}
		public void setTravel_list(List<Raiders> travel_list) {
			this.travel_list = travel_list;
		}
		public int getIs_end() {
			return is_end;
		}
		public void setIs_end(int is_end) {
			this.is_end = is_end;
		}
		public int getType() {
			return type;
		}
		public void setType(int type) {
			this.type = type;
		}
		
	}
	public static class Raiders{
		private int is_recommend;
		private String pic_url="";
		private String subtitle;
		private String tag;
		private String title;
		private String travel_id;
		public int getIs_recommend() {
			return is_recommend;
		}
		public void setIs_recommend(int is_recommend) {
			this.is_recommend = is_recommend;
		}
		public String getPic_url() {
			return pic_url;
		}
		public void setPic_url(String pic_url) {
			this.pic_url = pic_url;
		}
		public String getSubtitle() {
			return subtitle;
		}
		public void setSubtitle(String subtitle) {
			this.subtitle = subtitle;
		}
		public String getTag() {
			return tag;
		}
		public void setTag(String tag) {
			this.tag = tag;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getTravel_id() {
			return travel_id;
		}
		public void setTravel_id(String travel_id) {
			this.travel_id = travel_id;
		}
		
	}
}
