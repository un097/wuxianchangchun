package cn.ffcs.external.share.view;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.external_socialshare.R;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeConfig;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.UMWXHandler;
import com.umeng.socialize.media.UMImage;

/**
 * <p>Title: 友盟社会化分享         </p>
 * <p>Description: 
 * 
 * </p>
 * <p>@author: Leo               </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-12-10             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class UmengSocialShare {
	public static final SHARE_MEDIA[] share_media = new SHARE_MEDIA[] { SHARE_MEDIA.SINA,
			SHARE_MEDIA.TENCENT, SHARE_MEDIA.DOUBAN, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE };// 分享的平台
	private final static String SERVICE_NAME = "com.umeng.share";

	/**
	 * 分享纯文字
	 * @param activity
	 * @param shareContent
	 * @param shareUrl
	 */
	public static void umengShareByText(Activity activity, String shareContent, String shareUrl) {
		UMSocialService controller = UMServiceFactory.getUMSocialService(SERVICE_NAME,
				RequestType.SOCIAL);
		SocializeConfig config = controller.getConfig();
		config.setDefaultShareLocation(false);// 默认不自动定位

		config.setPlatforms(share_media);
		config.setPlatformOrder(share_media);
		String wechatAppId = activity.getString(R.string.umeng_social_wechat_app_id);
		// 添加微信
		UMWXHandler wxHandler = config.supportWXPlatform(activity, wechatAppId, shareUrl);
		wxHandler.setWXTitle(shareContent);

		// 支持微信朋友圈
		UMWXHandler circleHandler = config.supportWXCirclePlatform(activity, wechatAppId, shareUrl);
		circleHandler.setCircleTitle(shareContent);
		controller.setShareContent(shareContent);// 设置分享文字内容
		Bitmap imageBitmap = BitmapFactory.decodeResource(activity.getResources(),
				R.drawable.ic_launcher);
		// 友盟分享的图片
		UMImage image = new UMImage(activity, imageBitmap);
		controller.setShareMedia(image);// 设置分享图片内容
		controller.openShare(activity, false);
	}

	/**
	 * 分享图片bitmap
	 * @param activity
	 * @param shareContent  分享内容
	 * @param shareImage  分享图标:bitmap
	 * @param shareUrl  分享链接地址
	 */
	public static void umengShareByImageBitmap(Activity activity, String shareContent,
			Bitmap shareImage, String shareUrl) {
		UMSocialService controller = UMServiceFactory.getUMSocialService(SERVICE_NAME,
				RequestType.SOCIAL);
		SocializeConfig config = controller.getConfig();
		config.setDefaultShareLocation(false);// 默认不自动定位

		// 设置平台
		config.setPlatforms(share_media);
		config.setPlatformOrder(share_media);
		String wechatAppId = activity.getString(R.string.umeng_social_wechat_app_id);
		// 添加微信
		UMWXHandler wxHandler = config.supportWXPlatform(activity, wechatAppId, shareUrl);
		wxHandler.setWXTitle(shareContent);

		// 支持微信朋友圈
		UMWXHandler circleHandler = config.supportWXCirclePlatform(activity, wechatAppId, shareUrl);
		circleHandler.setCircleTitle(shareContent);

		controller.setShareContent(shareContent);// 设置分享文字内容
		UMImage image = new UMImage(activity, shareImage);
		controller.setShareMedia(image);// 设置分享图片内容
		controller.openShare(activity, false);
	}

	/**
	 * 分享图片地址及链接
	 * @param activity
	 * @param shareContent  分享内容
	 * @param shareImage  分享图标:bitmap
	 * @param shareUrl  分享链接地址
	 */
	public static void umengShareByImageUrl(Activity activity, String shareContent,
			String shareImageUrl, String shareUrl) {
		UMSocialService controller = UMServiceFactory.getUMSocialService(SERVICE_NAME,
				RequestType.SOCIAL);
		SocializeConfig config = controller.getConfig();
		config.setDefaultShareLocation(false);// 默认不自动定位

		// 设置平台
		config.setPlatforms(share_media);
		config.setPlatformOrder(share_media);
		String wechatAppId = activity.getString(R.string.umeng_social_wechat_app_id);
		// 添加微信
		UMWXHandler wxHandler = config.supportWXPlatform(activity, wechatAppId, shareUrl);
		wxHandler.setWXTitle(shareContent);
		// 支持微信朋友圈
		UMWXHandler circleHandler = config.supportWXCirclePlatform(activity, wechatAppId, shareUrl);
		circleHandler.setCircleTitle(shareContent);

		controller.setShareContent(shareContent);// 设置分享文字内容

		UMImage image = new UMImage(activity, shareImageUrl);
		controller.setShareMedia(image);// 设置分享图片内容
		controller.openShare(activity, false);
	}

	/**
	 * 分享本地图片
	 * @param activity
	 * @param shareContent  分享内容
	 * @param shareImage  分享图标:res
	 * @param shareUrl  分享链接地址
	 */
	public static void umengShareByImageResId(Activity activity, String shareContent, int resId,
			String shareUrl) {
		UMSocialService controller = UMServiceFactory.getUMSocialService(SERVICE_NAME,
				RequestType.SOCIAL);
		SocializeConfig config = controller.getConfig();
		config.setDefaultShareLocation(false);// 默认不自动定位

		// 设置平台
		config.setPlatforms(share_media);
		config.setPlatformOrder(share_media);
		String wechatAppId = activity.getString(R.string.umeng_social_wechat_app_id);
		// 添加微信
		UMWXHandler wxHandler = config.supportWXPlatform(activity, wechatAppId, shareUrl);
		wxHandler.setWXTitle(shareContent);

		// 支持微信朋友圈
		UMWXHandler circleHandler = config.supportWXCirclePlatform(activity, wechatAppId, shareUrl);
		circleHandler.setCircleTitle(shareContent);

		controller.setShareContent(shareContent);// 设置分享文字内容
		UMImage image = new UMImage(activity, resId);
		controller.setShareMedia(image);// 设置分享图片内容
		controller.openShare(activity, false);
	}

	/**
	 * 分享图片地址:byte
	 * @param activity
	 * @param shareContent  分享内容
	 * @param shareImage  分享图标:byte类型
	 * @param shareUrl  分享链接地址
	 */
	public static void umengShareByImageByte(Activity activity, String shareContent,
			byte[] shareImage, String shareUrl) {
		UMSocialService controller = UMServiceFactory.getUMSocialService(SERVICE_NAME,
				RequestType.SOCIAL);
		SocializeConfig config = controller.getConfig();
		config.setDefaultShareLocation(false);// 默认不自动定位
		config.setPlatforms(share_media);
		config.setPlatformOrder(share_media);
		String wechatAppId = activity.getString(R.string.umeng_social_wechat_app_id);
		// 添加微信
		UMWXHandler wxHandler = config.supportWXPlatform(activity, wechatAppId, shareUrl);
		wxHandler.setWXTitle(shareContent);

		// 支持微信朋友圈
		UMWXHandler circleHandler = config.supportWXCirclePlatform(activity, wechatAppId, shareUrl);
		circleHandler.setCircleTitle(shareContent);

		controller.setShareContent(shareContent);// 设置分享文字内容
		UMImage image = new UMImage(activity, shareImage);
		controller.setShareMedia(image);// 设置分享图片内容
		controller.openShare(activity, false);
	}
}
