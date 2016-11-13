package com.ctbri.wxcc.entity;

import java.util.List;

public class MerchantBean extends GsonContainer<MerchantBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2510604081835579810L;
	
	public List<Merchant> merchants;
	
	public List<Merchant> getMerchants() {
		return merchants;
	}

	public void setMerchants(List<Merchant> merchants) {
		this.merchants = merchants;
	}

	public static class Merchant {
		private String name;// ": "商家名称",
		private float distance; // ": "距离米",
		private String add;// ": "商家地址",
		private String merchant_id;// ": "商家id",
		private String tel;// ": "商家电话",
		private String location;// ": "商家坐标，经纬度 String 提供， 经度，维度"
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public float getDistance() {
			return distance;
		}
		public void setDistance(float distance) {
			this.distance = distance;
		}
		public String getAdd() {
			return add;
		}
		public void setAdd(String add) {
			this.add = add;
		}
		public String getMerchant_id() {
			return merchant_id;
		}
		public void setMerchant_id(String merchant_id) {
			this.merchant_id = merchant_id;
		}
		public String getTel() {
			return tel;
		}
		public void setTel(String tel) {
			this.tel = tel;
		}
		public String getLocation() {
			return location;
		}
		public void setLocation(String location) {
			this.location = location;
		}

	}
}
