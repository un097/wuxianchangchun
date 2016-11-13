package com.ctbri.wxcc.hotline;

import java.util.List;

import com.ctbri.wwcc.greenrobot.HotLine;

public class HotLineBean {

	private Data data;
	
	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public class Data{
		private String user_id;
		private List<HotLine> tel_items;
		
		public String getUser_id() {
			return user_id;
		}

		public void setUser_id(String user_id) {
			this.user_id = user_id;
		}

		public List<HotLine> getTel_items() {
			return tel_items;
		}

		public void setTel_items(List<HotLine> tel_items) {
			this.tel_items = tel_items;
		}
	}
}
