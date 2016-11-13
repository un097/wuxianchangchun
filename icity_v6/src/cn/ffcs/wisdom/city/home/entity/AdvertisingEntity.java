package cn.ffcs.wisdom.city.home.entity;

import java.io.Serializable;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <p>Title: 广告实体                  </p>
 * <p>Description:                     </p>
 * <p>@author: zhangws                 </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-5-2           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class AdvertisingEntity implements Parcelable {
	private List<Advertising> advs;

	public List<Advertising> getAdvs() {
		return advs;
	}

	public void setAdvs(List<Advertising> advs) {
		this.advs = advs;
	}

	public class Advertising implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3290902149675625123L;
		private String adv_id;
		private String title;
		private String content;
		private String official_title;
		private String official_link;
		private String app_name;
		private String app_link;
		private String item_type;
		private String pkg;
		private String cls;
		private String imgUrl;
		private String v6_android_img_url;

		
		public String getAdv_id() {
			return adv_id;
		}

		public void setAdv_id(String adv_id) {
			this.adv_id = adv_id;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public String getOfficial_title() {
			return official_title;
		}

		public void setOfficial_title(String official_title) {
			this.official_title = official_title;
		}

		public String getOfficial_link() {
			return official_link;
		}

		public void setOfficial_link(String official_link) {
			this.official_link = official_link;
		}

		public String getApp_name() {
			return app_name;
		}

		public void setApp_name(String app_name) {
			this.app_name = app_name;
		}

		public String getApp_link() {
			return app_link;
		}

		public void setApp_link(String app_link) {
			this.app_link = app_link;
		}

		public String getItem_type() {
			return item_type;
		}

		public void setItem_type(String item_type) {
			this.item_type = item_type;
		}

		public String getPkg() {
			return pkg;
		}

		public void setPkg(String pkg) {
			this.pkg = pkg;
		}

		public String getCls() {
			return cls;
		}

		public void setCls(String cls) {
			this.cls = cls;
		}

		public String getImgUrl() {
			return imgUrl;
		}

		public void setImgUrl(String imgUrl) {
			this.imgUrl = imgUrl;
		}

		public String getV6_android_img_url() {
			return v6_android_img_url;
		}

		public void setV6_android_img_url(String v6_android_img_url) {
			this.v6_android_img_url = v6_android_img_url;
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeSerializable((Serializable) advs);
	}

	public static final Parcelable.Creator<AdvertisingEntity> CREATOR = new Creator<AdvertisingEntity>() {

		@Override
		public AdvertisingEntity[] newArray(int size) {
			return new AdvertisingEntity[size];
		}

		@SuppressWarnings("unchecked")
		@Override
		public AdvertisingEntity createFromParcel(Parcel source) {
			AdvertisingEntity advertisingEntity = new AdvertisingEntity();
			advertisingEntity.setAdvs((List<Advertising>) source.readSerializable());
			return advertisingEntity;
		}
	};
}
