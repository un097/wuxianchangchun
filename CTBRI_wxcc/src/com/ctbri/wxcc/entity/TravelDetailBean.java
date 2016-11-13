package com.ctbri.wxcc.entity;

import java.util.ArrayList;
import java.util.List;

import com.ctbri.wxcc.entity.TravelDetailBean.TravelDetail;

public class TravelDetailBean extends GsonContainer<TravelDetail> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7877050017835084191L;

	/**
	 * “type”：1景区景点，2演出，3餐饮，4特产
	    "pic_url": "图片",
	    "item_id": "id",
	    "title": "标题",
	    "add": "地址",
	    "business_hours": "营业时间",
	    "feature": "特色菜品",
	    "tel": "电话",
	    "tag": "星级",
	    "price": "价格",
	    "location": [
	      "经度，维度"
	    ],
	    "intro": "简介",
	    "traffic": "交通",
	    "others": {
	      "key": "value"
	}
	"coupon_id": "优惠券id",
	" coupon_pic_url": "优惠券图片",
	" coupon_title": "标题",
	  }
	 * **/
	public static class TravelDetail {
		private int type;
		private List<String> pic_url;
		private String item_id;
		private String title;
		private String add;
		private String business_hours;
		private String feature;
		private String tel;
		private String tag;
		private String price;
		private List<String> location;
		private String intro;
		private String traffic;
		private String coupon_id;
		private String category;
		private String validity;
		private List<String> others = new ArrayList<String>();
		public String getValidity() {
			return validity;
		}
		public List<String> getOthers() {
			return others;
		}
		public void setOthers(List<String> others) {
			this.others = others;
		}
		public void setValidity(String validity) {
			this.validity = validity;
		}
		public String getCategory() {
			return category;
		}
		public void setCategory(String category) {
			this.category = category;
		}
		private String coupon_pic_url="";
		private String coupon_title;
		public int getType() {
			return type;
		}
		public void setType(int type) {
			this.type = type;
		}
		public List<String> getPic_url() {
			return pic_url;
		}
		public void setPic_url(List<String> pic_url) {
			this.pic_url = pic_url;
		}
		public String getItem_id() {
			return item_id;
		}
		public void setItem_id(String item_id) {
			this.item_id = item_id;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getAdd() {
			return add;
		}
		public void setAdd(String add) {
			this.add = add;
		}
		public String getBusiness_hours() {
			return business_hours;
		}
		public void setBusiness_hours(String business_hours) {
			this.business_hours = business_hours;
		}
		public String getFeature() {
			return feature;
		}
		public void setFeature(String feature) {
			this.feature = feature;
		}
		public String getTel() {
			return tel;
		}
		public void setTel(String tel) {
			this.tel = tel;
		}
		public String getTag() {
			return tag;
		}
		public void setTag(String tag) {
			this.tag = tag;
		}
		public String getPrice() {
			return price;
		}
		public void setPrice(String price) {
			this.price = price;
		}
		public List<String> getLocation() {
			return location;
		}
		public void setLocation(List<String> location) {
			this.location = location;
		}
		public String getIntro() {
			return intro;
		}
		public void setIntro(String intro) {
			this.intro = intro;
		}
		public String getTraffic() {
			return traffic;
		}
		public void setTraffic(String traffic) {
			this.traffic = traffic;
		}
		public String getCoupon_id() {
			return coupon_id;
		}
		public void setCoupon_id(String coupon_id) {
			this.coupon_id = coupon_id;
		}
		public String getCoupon_pic_url() {
			return coupon_pic_url;
		}
		public void setCoupon_pic_url(String coupon_pic_url) {
			this.coupon_pic_url = coupon_pic_url;
		}
		public String getCoupon_title() {
			return coupon_title;
		}
		public void setCoupon_title(String coupon_title) {
			this.coupon_title = coupon_title;
		}
		
		
	}
}
