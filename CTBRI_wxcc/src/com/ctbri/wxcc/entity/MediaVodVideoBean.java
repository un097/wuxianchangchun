package com.ctbri.wxcc.entity;

import java.util.List;

public class MediaVodVideoBean extends GsonContainer<MediaVodVideoBean> {

	
	private List<VodGroup> vod_group;
	
	
	public List<VodGroup> getVod_group() {
		return vod_group;
	}

	public void setVod_group(List<VodGroup> vod_group) {
		this.vod_group = vod_group;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1708487568337963007L;

	public static class VodGroup{
		private String group_name;
        private String group_url ;
        private String group_id;
        
        private List<Vod> vod_list;

		public String getGroup_name() {
			return group_name;
		}

		public void setGroup_name(String group_name) {
			this.group_name = group_name;
		}

		public String getGroup_url() {
			return group_url;
		}

		public void setGroup_url(String group_url) {
			this.group_url = group_url;
		}

		public String getGroup_id() {
			return group_id;
		}

		public void setGroup_id(String group_id) {
			this.group_id = group_id;
		}

		public List<Vod> getVod_list() {
			return vod_list;
		}

		public void setVod_list(List<Vod> vod_list) {
			this.vod_list = vod_list;
		}

        
	}
	
	public static class Vod{
		private String vod_id;
		private String vod_name;
		private String vod_url;
		public String getVod_id() {
			return vod_id;
		}
		public void setVod_id(String vod_id) {
			this.vod_id = vod_id;
		}
		public String getVod_name() {
			return vod_name;
		}
		public void setVod_name(String vod_name) {
			this.vod_name = vod_name;
		}
		public String getVod_url() {
			return vod_url;
		}
		public void setVod_url(String vod_url) {
			this.vod_url = vod_url;
		}
		
		
	}
}
