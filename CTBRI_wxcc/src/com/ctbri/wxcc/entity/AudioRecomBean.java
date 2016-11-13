package com.ctbri.wxcc.entity;

import java.io.Serializable;
import java.util.List;

public class AudioRecomBean extends GsonContainer<AudioRecomBean>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1735074176469788877L;
	private List<AudioChannel> channels;

	
	public List<AudioChannel> getChannels() {
		return channels;
	}


	public void setChannels(List<AudioChannel> channels) {
		this.channels = channels;
	}


	public static class AudioChannel implements Serializable{
		private static final long serialVersionUID = 1L;
		private String channel_id; // 10001,[直播频道ID],
		private String channel_name; // 长春新闻广播,[直播频道名称],
		private String channel_code; // FM20147,[直播频道代码],
		private String channel_type; // 1,[频道类型],
		private String live; // http; ////xxxx/xxx/xxx,[直播地址],
		private String pic=""; // http; ////xxxx/xxx/xxx.jpg,[直播节目图片],
		private String program_id; // 026a99de7bd141f98cd7f854ced32d20,[节目ID],
		private String program_name; // 百家讲坛,[节目名称],
		private String plays; // 100,[频道播放次数]

		public String getChannel_id() {
			return channel_id;
		}

		public void setChannel_id(String channel_id) {
			this.channel_id = channel_id;
		}

		public String getChannel_name() {
			return channel_name;
		}

		public void setChannel_name(String channel_name) {
			this.channel_name = channel_name;
		}

		public String getChannel_code() {
			return channel_code;
		}

		public void setChannel_code(String channel_code) {
			this.channel_code = channel_code;
		}

		public String getChannel_type() {
			return channel_type;
		}

		public void setChannel_type(String channel_type) {
			this.channel_type = channel_type;
		}

		public String getLive() {
			return live;
		}

		public void setLive(String live) {
			this.live = live;
		}

		public String getPic() {
			return pic;
		}

		public void setPic(String pic) {
			this.pic = pic;
		}

		public String getProgram_id() {
			return program_id;
		}

		public void setProgram_id(String program_id) {
			this.program_id = program_id;
		}

		public String getProgram_name() {
			return program_name;
		}

		public void setProgram_name(String program_name) {
			this.program_name = program_name;
		}

		public String getPlays() {
			return plays;
		}

		public void setPlays(String plays) {
			this.plays = plays;
		}

	}
}
