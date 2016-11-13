package com.ctbri.wxcc.entity;


public class ShakeBean {

	private String ret;
	private String msg;
	private String desp;
	private Data data;
	
	public String getRet() {
		return ret;
	}
	public void setRet(String ret) {
		this.ret = ret;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getDesp() {
		return desp;
	}
	public void setDesp(String desp) {
		this.desp = desp;
	}
	public Data getData() {
		return data;
	}
	public void setData(Data data) {
		this.data = data;
	}
	
	public class Data{
		private String status;
		private String url;
		
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		
		/*private String  type;
		private String vote_status;
		private String lottery_status;
		private Activity activity;
		
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getVote_status() {
			return vote_status;
		}
		public void setVote_status(String vote_status) {
			this.vote_status = vote_status;
		}
		public String getLottery_status() {
			return lottery_status;
		}
		public void setLottery_status(String lottery_status) {
			this.lottery_status = lottery_status;
		}
		public Activity getActivity() {
			return activity;
		}
		public void setActivity(Activity activity) {
			this.activity = activity;
		}*/
	}
	
	/*public class Activity{
		private String id;
		private String name;
		private String pic;
		private String desp;
		private List<Option> option;
		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
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
		public String getDesp() {
			return desp;
		}
		public void setDesp(String desp) {
			this.desp = desp;
		}
		public List<Option> getOption() {
			return option;
		}
		public void setOption(List<Option> option) {
			this.option = option;
		}
	}
	
	public class Option{
		private String id;
		private String name;
		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}*/
}