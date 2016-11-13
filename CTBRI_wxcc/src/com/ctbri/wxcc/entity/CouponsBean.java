package com.ctbri.wxcc.entity;

import java.io.Serializable;
import java.util.List;

public class CouponsBean extends GsonContainer<CouponsBean.CouponList> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6812302587775506222L;

	public static class CouponList{
		private int type;
		private int is_end;
		private List<Coupon> coupon_list;
		public int getType() {
			return type;
		}
		public void setType(int type) {
			this.type = type;
		}
		public int getIs_end() {
			return is_end;
		}
		public void setIs_end(int is_end) {
			this.is_end = is_end;
		}
		public List<Coupon> getCoupon_list() {
			return coupon_list;
		}
		public void setCoupon_list(List<Coupon> coupon_lst) {
			this.coupon_list = coupon_lst;
		}
		
		
	}
		
	public static class Coupon implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = -1716330321852751048L;
		private String coupon_id;
		private String pic_url="";
		private String title;
		private int category;
		private int status;
		private String status_desp;
		private String code;
		private String validity;
		public String getCoupon_id() {
			return coupon_id;
		}
		public void setCoupon_id(String coupon_id) {
			this.coupon_id = coupon_id;
		}
		public String getPic_url() {
			return pic_url;
		}
		public void setPic_url(String pic_url) {
			this.pic_url = pic_url;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public int getCategory() {
			return category;
		}
		public void setCategory(int category) {
			this.category = category;
		}
		public int getStatus() {
			return status;
		}
		public void setStatus(int status) {
			this.status = status;
		}
		public String getStatus_desp() {
			return status_desp;
		}
		public void setStatus_desp(String status_desp) {
			this.status_desp = status_desp;
		}
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public String getValidity() {
			return validity;
		}
		public void setValidity(String validity) {
			this.validity = validity;
		}
		
		
	}
}
