package com.ctbri.wxcc.entity;

import java.util.List;

public class LiveListBean extends GsonContainer<LiveListBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4098093494940220342L;

	private List<LiveListItem> lives;
	
	public List<LiveListItem> getLives() {
		return lives;
	}

	public void setLives(List<LiveListItem> lives) {
		this.lives = lives;
	}
	private int is_end;
	

	public int getIs_end() {
		return is_end;
	}

	public void setIs_end(int is_end) {
		this.is_end = is_end;
	}


	public static class LiveListItem {
		private String channel_id; // 10001,[直播频道ID],
		private String channel_name; // 中央综合频道,[直播频道名称],
		private String channel_code; // CCTV-1,[直播频道代码],
		private String channel_type; // 1,[频道类型(1=央视,2=卫视,3=自有,4=其他)],
		private String pic=""; // http://xxxx/xxx/xxx.jpg,[直播节目图片],
		private String program_name; // 剩女的代价,[节目名称],
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
		public String getPic() {
			return pic;
		}
		public void setPic(String pic) {
			this.pic = pic;
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
