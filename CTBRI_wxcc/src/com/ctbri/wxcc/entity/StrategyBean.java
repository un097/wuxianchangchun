package com.ctbri.wxcc.entity;

import java.util.List;
public class StrategyBean extends GsonContainer<StrategyBean.Strategy> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8627615284645441437L;
	
	

	public static class Strategy{
		private String pic_url;
		private String title;
		private String name;
		private String content_url;
		private List<Spot> spots;
		public List<Spot> getSpots() {
			return spots;
		}
		public void setSpots(List<Spot> spots) {
			this.spots = spots;
		}
		public String getPic_url() {
			return pic_url;
		}
		public void setPic_url(String pic_url) {
			this.pic_url = pic_url;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getContent_url() {
			return content_url;
		}
		public void setContent_url(String content_url) {
			this.content_url = content_url;
		}
	}
	
	public static class Spot{
		private String spots_id;
		private String spots_location;
		private String spots_name;
		private int spots_type;
		public String getSpots_id() {
			return spots_id;
		}
		public void setSpots_id(String spots_id) {
			this.spots_id = spots_id;
		}
		public String getSpots_location() {
			return spots_location;
		}
		public void setSpots_location(String spots_location) {
			this.spots_location = spots_location;
		}
		public String getSpots_name() {
			return spots_name;
		}
		public void setSpots_name(String spots_name) {
			this.spots_name = spots_name;
		}
		public int getSpots_type() {
			return spots_type;
		}
		public void setSpots_type(int spots_type) {
			this.spots_type = spots_type;
		}
		@Override
		public String toString() {
			return this.spots_name;
		}
	}
}
