package com.ctbri.wxcc.entity;

import java.util.List;

public class MediaCategoryBean extends GsonContainer<MediaCategoryBean> {

	private static final long serialVersionUID = -2256040615477238701L;
	
	private List<MediaCategory> category_list;
	
	
	
	public List<MediaCategory> getCategory_list() {
		return category_list;
	}



	public void setCategory_list(List<MediaCategory> category_list) {
		this.category_list = category_list;
	}



	public static class MediaCategory{
		private String category_id;
		private String category_url;
		private String category_name;
		public String getCategory_id() {
			return category_id;
		}
		public void setCategory_id(String category_id) {
			this.category_id = category_id;
		}
		public String getCategory_url() {
			return category_url;
		}
		public void setCategory_url(String category_url) {
			this.category_url = category_url;
		}
		public String getCategory_name() {
			return category_name;
		}
		public void setCategory_name(String category_name) {
			this.category_name = category_name;
		}
		
		
	}
}
