package com.ctbri.wxcc.entity;

import java.util.List;

import com.ctbri.wxcc.entity.TravelGallery.PicList;

public class TravelGallery extends GsonContainer<PicList> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 432767863432593481L;

	public static class PicList{
		private List<Pic> pic_list;

		public List<Pic> getPic_list() {
			return pic_list;
		}

		public void setPic_list(List<Pic> pic_list) {
			this.pic_list = pic_list;
		}
		
	}
	public static class Pic{
		private String pic_title;
		private String pic_url;
		public String getPic_title() {
			return pic_title;
		}
		public void setPic_title(String pic_title) {
			this.pic_title = pic_title;
		}
		public String getPic_url() {
			return pic_url;
		}
		public void setPic_url(String pic_url) {
			this.pic_url = pic_url;
		}
	}
}
