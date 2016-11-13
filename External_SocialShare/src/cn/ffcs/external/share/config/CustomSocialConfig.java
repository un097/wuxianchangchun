package cn.ffcs.external.share.config;

import java.util.ArrayList;
import java.util.List;

import cn.ffcs.external.share.entity.CustomSocialEntity;
import cn.ffcs.external.share.entity.CustomSocialShareEntity;

import com.example.external_socialshare.R;
import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * <p>Title:分享数据配置          </p>
 * <p>Description: 
 * 
 * </p>
 * <p>@author: Leo               </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-11-18             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class CustomSocialConfig {
	/**
	 * 数据配置
	 * @return
	 */
	public static List<CustomSocialEntity> fillImageSocialData(CustomSocialShareEntity entity,
			boolean showPhoto) {
		List<CustomSocialEntity> list = new ArrayList<CustomSocialEntity>();
		CustomSocialEntity sinaEntity = new CustomSocialEntity();
		CustomSocialEntity tencentEntity = new CustomSocialEntity();
		CustomSocialEntity doubanEntity = new CustomSocialEntity();
		CustomSocialEntity weixinEntity = new CustomSocialEntity();
		CustomSocialEntity weixinCircleEntity = new CustomSocialEntity();
		CustomSocialEntity photoEntity = new CustomSocialEntity();
		CustomSocialEntity yixinEntity = new CustomSocialEntity();
		CustomSocialEntity yixinCircleEntity = new CustomSocialEntity();
		CustomSocialEntity qqEntity = new CustomSocialEntity();
		CustomSocialEntity qZoneEntity = new CustomSocialEntity();

		sinaEntity.media = SHARE_MEDIA.SINA;
		sinaEntity.socialImageResId = R.drawable.umeng_socialize_sina_on;
		sinaEntity.socialNameCn = "新浪微博";
		sinaEntity.socialNameEn = "sina";
		sinaEntity.shareEntity = entity;

		tencentEntity.media = SHARE_MEDIA.TENCENT;
		tencentEntity.socialImageResId = R.drawable.umeng_socialize_tx_on;
		tencentEntity.socialNameCn = "腾讯微博";
		tencentEntity.socialNameEn = "tencent";
		tencentEntity.shareEntity = entity;

		doubanEntity.media = SHARE_MEDIA.DOUBAN;
		doubanEntity.socialImageResId = R.drawable.umeng_socialize_douban_on;
		doubanEntity.socialNameCn = "豆瓣网";
		doubanEntity.socialNameEn = "douban";
		doubanEntity.shareEntity = entity;

		weixinEntity.media = SHARE_MEDIA.WEIXIN;
		weixinEntity.socialImageResId = R.drawable.umeng_socialize_wechat;
		weixinEntity.socialNameCn = "微信";
		weixinEntity.socialNameEn = "weixin";
		weixinEntity.shareEntity = entity;

		weixinCircleEntity.media = SHARE_MEDIA.WEIXIN_CIRCLE;
		weixinCircleEntity.socialImageResId = R.drawable.umeng_socialize_wxcircle;
		weixinCircleEntity.socialNameCn = "微信朋友圈";
		weixinCircleEntity.socialNameEn = "weixincircle";
		weixinCircleEntity.shareEntity = entity;

		photoEntity.media = SHARE_MEDIA.GENERIC;
		photoEntity.socialImageResId = R.drawable.custom_photo_share;
		photoEntity.socialNameCn = "随手拍";
		photoEntity.socialNameEn = "photo";
		photoEntity.shareEntity = entity;

		yixinEntity.media = SHARE_MEDIA.GENERIC;
		yixinEntity.socialImageResId = R.drawable.custom_yixin;
		yixinEntity.socialNameCn = "易信";
		yixinEntity.socialNameEn = "yixin";
		yixinEntity.shareEntity = entity;

		yixinCircleEntity.media = SHARE_MEDIA.GENERIC;
		yixinCircleEntity.socialImageResId = R.drawable.custom_yixin_circle;
		yixinCircleEntity.socialNameCn = "易信朋友圈";
		yixinCircleEntity.socialNameEn = "yixincircle";
		yixinCircleEntity.shareEntity = entity;
		
		qqEntity.media = SHARE_MEDIA.QQ;
		qqEntity.socialImageResId = R.drawable.umeng_socialize_qq_on;
		qqEntity.socialNameCn = "QQ";
		qqEntity.socialNameEn = "QQ";
		qqEntity.shareEntity = entity;

		qZoneEntity.media = SHARE_MEDIA.QZONE;
		qZoneEntity.socialImageResId = R.drawable.umeng_socialize_qzone_on;
		qZoneEntity.socialNameCn = "QQ空间";
		qZoneEntity.socialNameEn = "QZONE";
		qZoneEntity.shareEntity = entity;
		
		list.add(qqEntity);
		list.add(qZoneEntity);
		list.add(weixinEntity);
		list.add(weixinCircleEntity);
		list.add(yixinEntity);
		list.add(yixinCircleEntity);
		list.add(sinaEntity);
		if (showPhoto) {
			list.add(photoEntity);
		} else {
			list.add(new CustomSocialEntity());
		}

//		list.add(sinaEntity);
//		list.add(tencentEntity);
//		list.add(doubanEntity);
//		list.add(weixinEntity);
//		list.add(weixinCircleEntity);
//		if (showPhoto) {
//			list.add(photoEntity);
//		}
//		list.add(yixinEntity);
//		list.add(yixinCircleEntity);
//		if (!showPhoto) {
//			list.add(new CustomSocialEntity());
//		}
		return list;
	}

	/**
	 * 数据配置
	 * @return
	 */
	public static List<CustomSocialEntity> fillTextSocialData(String shareContent, String shareUrl) {
		List<CustomSocialEntity> list = new ArrayList<CustomSocialEntity>();
		CustomSocialEntity sinaEntity = new CustomSocialEntity();
		CustomSocialEntity tencentEntity = new CustomSocialEntity();
		CustomSocialEntity doubanEntity = new CustomSocialEntity();
		CustomSocialEntity weixinEntity = new CustomSocialEntity();
		CustomSocialEntity weixinCircleEntity = new CustomSocialEntity();
		CustomSocialEntity photoEntity = new CustomSocialEntity();
		CustomSocialEntity yixinEntity = new CustomSocialEntity();
		CustomSocialEntity yixinCircleEntity = new CustomSocialEntity();
		CustomSocialEntity qqEntity = new CustomSocialEntity();
		CustomSocialEntity qZoneEntity = new CustomSocialEntity();

		CustomSocialShareEntity entity = new CustomSocialShareEntity();
		entity.shareContent = shareContent;
		entity.shareUrl = shareUrl;

		sinaEntity.media = SHARE_MEDIA.SINA;
		sinaEntity.socialImageResId = R.drawable.umeng_socialize_sina_on;
		sinaEntity.socialNameCn = "新浪微博";
		sinaEntity.socialNameEn = "sina";
		sinaEntity.shareEntity = entity;

		tencentEntity.media = SHARE_MEDIA.TENCENT;
		tencentEntity.socialImageResId = R.drawable.umeng_socialize_tx_on;
		tencentEntity.socialNameCn = "腾讯微博";
		tencentEntity.socialNameEn = "tencent";
		tencentEntity.shareEntity = entity;

		doubanEntity.media = SHARE_MEDIA.DOUBAN;
		doubanEntity.socialImageResId = R.drawable.umeng_socialize_douban_on;
		doubanEntity.socialNameCn = "豆瓣网";
		doubanEntity.socialNameEn = "douban";
		doubanEntity.shareEntity = entity;

		weixinEntity.media = SHARE_MEDIA.WEIXIN;
		weixinEntity.socialImageResId = R.drawable.umeng_socialize_wechat;
		weixinEntity.socialNameCn = "微信";
		weixinEntity.socialNameEn = "weixin";
		weixinEntity.shareEntity = entity;

		weixinCircleEntity.media = SHARE_MEDIA.WEIXIN_CIRCLE;
		weixinCircleEntity.socialImageResId = R.drawable.umeng_socialize_wxcircle;
		weixinCircleEntity.socialNameCn = "微信朋友圈";
		weixinCircleEntity.socialNameEn = "weixincircle";
		weixinCircleEntity.shareEntity = entity;

		photoEntity.media = SHARE_MEDIA.SMS;
		photoEntity.socialImageResId = R.drawable.umeng_socialize_sms;
		photoEntity.socialNameCn = "短信";
		photoEntity.socialNameEn = "sms";
		photoEntity.shareEntity = entity;

		yixinEntity.media = SHARE_MEDIA.GENERIC;
		yixinEntity.socialImageResId = R.drawable.custom_yixin;
		yixinEntity.socialNameCn = "易信";
		yixinEntity.socialNameEn = "yixin";
		yixinEntity.shareEntity = entity;

		yixinCircleEntity.media = SHARE_MEDIA.GENERIC;
		yixinCircleEntity.socialImageResId = R.drawable.custom_yixin_circle;
		yixinCircleEntity.socialNameCn = "易信朋友圈";
		yixinCircleEntity.socialNameEn = "yixincircle";
		yixinCircleEntity.shareEntity = entity;
		
		qqEntity.media = SHARE_MEDIA.QQ;
		qqEntity.socialImageResId = R.drawable.umeng_socialize_qq_on;
		qqEntity.socialNameCn = "QQ";
		qqEntity.socialNameEn = "QQ";
		qqEntity.shareEntity = entity;

		qZoneEntity.media = SHARE_MEDIA.QZONE;
		qZoneEntity.socialImageResId = R.drawable.umeng_socialize_qzone_on;
		qZoneEntity.socialNameCn = "QQ空间";
		qZoneEntity.socialNameEn = "QZONE";
		qZoneEntity.shareEntity = entity;
		
		list.add(qqEntity);
		list.add(qZoneEntity);
		list.add(weixinEntity);
		list.add(weixinCircleEntity);
		list.add(yixinEntity);
		list.add(yixinCircleEntity);
		list.add(sinaEntity);
		list.add(photoEntity);

//		list.add(sinaEntity);
//		list.add(tencentEntity);
//		list.add(doubanEntity);
//		list.add(weixinEntity);
//		list.add(weixinCircleEntity);
//		list.add(photoEntity);
//		list.add(yixinEntity);
//		list.add(yixinCircleEntity);
		return list;
	}
}
