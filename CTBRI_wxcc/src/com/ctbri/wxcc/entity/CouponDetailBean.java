package com.ctbri.wxcc.entity;

public class CouponDetailBean extends GsonContainer<CouponDetailBean.CouponDetail> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5705457154658170767L;

	public static class CouponDetail extends CouponsBean.Coupon{
		/**
		 * 
		 */
		private static final long serialVersionUID = 9149812880084703044L;
		private String name;

		private String content;
		private String location;
		private String tel;
		private String marks;
		private String add;
		private int is_more;
		private int count;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public String getLocation() {
			return location;
		}
		public void setLocation(String location) {
			this.location = location;
		}
		public String getTel() {
			return tel;
		}
		public void setTel(String tel) {
			this.tel = tel;
		}
		public String getMarks() {
			return marks;
		}
		public void setMarks(String marks) {
			this.marks = marks;
		}
		public String getAdd() {
			return add;
		}
		public void setAdd(String add) {
			this.add = add;
		}
		public int getIs_more() {
			return is_more;
		}
		public void setIs_more(int is_more) {
			this.is_more = is_more;
		}
		public int getCount() {
			return count;
		}
		public void setCount(int count) {
			this.count = count;
		}
		
		
		
	}
}
