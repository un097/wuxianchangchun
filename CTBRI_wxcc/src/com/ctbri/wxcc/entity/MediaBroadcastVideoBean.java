package com.ctbri.wxcc.entity;

import java.util.List;

import com.ctbri.wxcc.entity.MediaBroadcastVideoBean.Channel_Hold;

public class MediaBroadcastVideoBean extends GsonContainer<Channel_Hold> {

	private static final long serialVersionUID = -6158466503800782914L;
	
	public static class Channel_Hold{
		private List<Channel> channel_list;

		public List<Channel> getChannel_list() {
			return channel_list;
		}

		public void setChannel_list(List<Channel> channel_list) {
			this.channel_list = channel_list;
		}
		
		
	}
	
	public static class Channel{
		private String channel_id;
		private String video_name;
		private String video_url="";
		private String channel_name;
		public String getChannel_id() {
			return channel_id;
		}
		public void setChannel_id(String channel_id) {
			this.channel_id = channel_id;
		}
		public String getVideo_name() {
			return video_name;
		}
		public void setVideo_name(String video_name) {
			this.video_name = video_name;
		}
		public String getVideo_url() {
			return video_url;
		}
		public void setVideo_url(String video_url) {
			this.video_url = video_url;
		}
		public String getChannel_name() {
			return channel_name;
		}
		public void setChannel_name(String channel_name) {
			this.channel_name = channel_name;
		}
		
		
	}
}
