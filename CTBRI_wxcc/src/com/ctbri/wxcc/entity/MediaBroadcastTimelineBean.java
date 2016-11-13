package com.ctbri.wxcc.entity;

import java.util.List;

public class MediaBroadcastTimelineBean extends GsonContainer<MediaBroadcastTimelineBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6428578570809934566L;
	private List<BroadcastVideoTimeline> timeline_list;
	
	
	public List<BroadcastVideoTimeline> getTimeline_list() {
		return timeline_list;
	}


	public void setTimeline_list(List<BroadcastVideoTimeline> timeline_list) {
		this.timeline_list = timeline_list;
	}


	public static class BroadcastVideoTimeline{
		private String time;
		private int state;
		private String video_name;
		public String getTime() {
			return time;
		}
		public void setTime(String time) {
			this.time = time;
		}
		public int getState() {
			return state;
		}
		public void setState(int state) {
			this.state = state;
		}
		public String getVideo_name() {
			return video_name;
		}
		public void setVideo_name(String video_name) {
			this.video_name = video_name;
		}
		
		
	}
	
}
