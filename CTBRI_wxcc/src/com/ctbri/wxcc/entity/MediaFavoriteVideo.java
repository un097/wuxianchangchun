package com.ctbri.wxcc.entity;

import java.util.List;

public class MediaFavoriteVideo extends GsonContainer<MediaFavoriteVideo> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3384583942663564912L;
	private int is_end;
	private List<VideoCollection> collection_list;

	public int getIs_end() {
		return is_end;
	}

	public void setIs_end(int is_end) {
		this.is_end = is_end;
	}
	

	public List<VideoCollection> getCollection_list() {
		return collection_list;
	}

	public void setCollection_list(List<VideoCollection> collection_list) {
		this.collection_list = collection_list;
	}

	public static class VideoCollection {
		private String collection_id;
		private String collection_type;
		private String collection_name;
		private int status;
		public int getStatus() {
			return status;
		}
		public void setStatus(int status) {
			this.status = status;
		}
		public boolean checked;
		private String pic_url="";
		public String getCollection_id() {
			return collection_id;
		}
		public void setCollection_id(String collection_id) {
			this.collection_id = collection_id;
		}
		public String getCollection_type() {
			return collection_type;
		}
		public void setCollection_type(String collection_type) {
			this.collection_type = collection_type;
		}
		public String getCollection_name() {
			return collection_name;
		}
		public void setCollection_name(String collection_name) {
			this.collection_name = collection_name;
		}
		public String getPic_url() {
			return pic_url;
		}
		public void setPic_url(String pic_url) {
			this.pic_url = pic_url;
		}
		
		
	}

}
