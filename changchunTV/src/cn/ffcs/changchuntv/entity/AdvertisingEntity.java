package cn.ffcs.changchuntv.entity;

import java.io.Serializable;
import java.util.List;

public class AdvertisingEntity {
	public List<Advertising> banner_advs;
	public String group;

	public class Advertising implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -1247214295124166336L;
		public int id;
		public int group;
		public String title;
		public String url;
		public int order;
		public String banner_adv_type;
		public String android_pic;
	}
}
