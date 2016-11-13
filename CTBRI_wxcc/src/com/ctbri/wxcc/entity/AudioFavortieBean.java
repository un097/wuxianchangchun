package com.ctbri.wxcc.entity;

import java.util.List;

public class AudioFavortieBean extends GsonContainer<AudioFavortieBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7022636048725080483L;

	private int is_end;

	private List<AudioFavoriteEntity> audios;

	public int getIs_end() {
		return is_end;
	}

	public void setIs_end(int is_end) {
		this.is_end = is_end;
	}

	public List<AudioFavoriteEntity> getAudios() {
		return audios;
	}

	public void setAudios(List<AudioFavoriteEntity> audios) {
		this.audios = audios;
	}

	public static class AudioFavoriteEntity {
		public static final int TYPE_GROUP = 2;
		public static final int TYPE_ITEM = 1;
		private String id;
		private int type;
		private String name;
		private String pic = "";
		private int status;
		public boolean checked;
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public int getType() {
			return type;
		}
		public void setType(int type) {
			this.type = type;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getPic() {
			return pic;
		}
		public void setPic(String pic) {
			this.pic = pic;
		}
		public int getStatus() {
			return status;
		}
		public void setStatus(int status) {
			this.status = status;
		}
		
		
	}

}
