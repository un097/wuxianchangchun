package com.ctbri.wxcc.entity;
/**
 * 点赞后 返回的实体，该类可以公用
 * @author yanyadi
 *
 */
public class PraiseBean {
	
	private Result data;

	
	public Result getData() {
		return data;
	}


	public void setData(Result data) {
		this.data = data;
	}


	public static class Result{
		private String totls;

		public String getTotls() {
			return totls;
		}

		public void setTotls(String totls) {
			this.totls = totls;
		}
	}
}
