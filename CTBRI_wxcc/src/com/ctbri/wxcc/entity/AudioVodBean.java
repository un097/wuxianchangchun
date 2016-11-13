package com.ctbri.wxcc.entity;

import java.util.List;

public class AudioVodBean extends GsonContainer<AudioVodBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1384862644673739073L;
	private List<AudioVodGroup> vod_group;

	public List<AudioVodGroup> getVod_group() {
		return vod_group;
	}

	public void setVod_group(List<AudioVodGroup> vod_group) {
		this.vod_group = vod_group;
	}

	public static class AudioVodGroup {
		private String group_name;
		private String group_id;
		private List<AudioVod> vod_list;

		public String getGroup_name() {
			return group_name;
		}

		public void setGroup_name(String group_name) {
			this.group_name = group_name;
		}

		public String getGroup_id() {
			return group_id;
		}

		public void setGroup_id(String group_id) {
			this.group_id = group_id;
		}

		public List<AudioVod> getVod_list() {
			return vod_list;
		}

		public void setVod_list(List<AudioVod> vod_list) {
			this.vod_list = vod_list;
		}

	}

	public static class AudioVod {
		private String vod_id;
		private String vod_name;
		private String vod_url="";

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
