package cn.ffcs.external.share.utils;

import im.yixin.sdk.api.SendMessageToYX;
import im.yixin.sdk.util.BitmapUtil;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import cn.ffcs.external.share.entity.CustomSocialEntity;
import cn.ffcs.external.share.entity.CustomSocialShareEntity;
import cn.ffcs.external.share.view.TencentSocialShare;
import cn.ffcs.external.share.view.UmengSocialShare;
import cn.ffcs.external.share.view.WeiBoSocialShare;
import cn.ffcs.external.share.view.YiXinSocialShare;

import com.example.external_socialshare.R;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeConfig;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.UMWXHandler;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;

/**
 * <p>
 * Title:点击应用跳转事件
 * </p>
 * <p>
 * Description:
 * 
 * </p>
 * <p>
 * @author: Leo
 * </p>
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * <p>
 * Company: FFCS Co., Ltd.
 * </p>
 * <p>
 * Create Time: 2013-11-18
 * </p>
 * <p>
 * Update Time:
 * </p>
 * <p>
 * Updater:
 * </p>
 * <p>
 * Update Comments:
 * </p>
 */
public class CustomSocialApp {
	private final static String dir = SdcardTool.getSdcardDir()
			+ "/changchuntv/photo/temp";
	private final static String fileName = "vedio_image.jpg";

	/**
	 * 启动应用，图片分享
	 * 
	 * @param activity
	 * @param entity
	 */
	public static void startImageApp(Activity activity,
			CustomSocialEntity entity) {
		if (entity != null) {
			// 不是随手拍
			if (entity.media != SHARE_MEDIA.GENERIC) {
				startImageSharePlatform(activity, entity.media,
						entity.shareEntity);
			} else if ("photo".equals(entity.socialNameEn)) {// 启动随手拍
				startPhotoShare(activity, entity.shareEntity);
			} else if ("yixin".equals(entity.socialNameEn)) {
				startYiXinImageShare(activity, entity.shareEntity,
						SendMessageToYX.Req.YXSceneSession);
			} else if ("yixincircle".equals(entity.socialNameEn)) {
				startYiXinImageShare(activity, entity.shareEntity,
						SendMessageToYX.Req.YXSceneTimeline);
			}

		}
	}

	/**
	 * 启动应用：文字分享
	 * 
	 * @param activity
	 * @param entity
	 */
	public static void startTextApp(Activity activity,
			CustomSocialEntity entity, String shareTitle, String shareContent,
			String shareUrl) {
		if (entity != null) {
			// 不是随手拍
			if (entity.media != SHARE_MEDIA.GENERIC) {
				startTextSharePlatform(activity, entity.media, shareTitle,
						shareContent, shareUrl);
			} else if ("yixin".equals(entity.socialNameEn)) {
				startYiXinTextShare(activity, shareTitle, shareContent,
						shareUrl, SendMessageToYX.Req.YXSceneSession);
			} else if ("yixincircle".equals(entity.socialNameEn)) {
				startYiXinTextShare(activity, shareTitle, shareContent,
						shareUrl, SendMessageToYX.Req.YXSceneTimeline);
			}
		}
	}

	static Bitmap bitmap;
	public static QQShareContent qqShareContent;
	public  static QZoneShareContent qzone;

	/**
	 * 
	 * 
	 * @param QQAppId
	 * @param QQAppKey
	 * @param contentUrl
	 */
	public static void shareQQ(final Activity activity, String QQAppId,
			String QQAppKey, final SHARE_MEDIA media,
			CustomSocialShareEntity entity) {
		UMSocialService controller = UMServiceFactory.getUMSocialService(
				entity.shareSource, RequestType.SOCIAL);
		controller.getConfig().supportQQPlatform(activity, QQAppId, QQAppKey);
		bitmap = BitmapUtil.byteArrayToBmp(entity.imageByte);
		UMImage mUMImgBitmap = new UMImage(activity, bitmap);
		if (media == SHARE_MEDIA.QQ) {
			qqShareContent = new QQShareContent();
			qqShareContent.setShareContent(entity.shareContent);
			qqShareContent.setTitle(entity.shareTitle);
			qqShareContent.setShareImage(mUMImgBitmap);
			controller.setShareMedia(qqShareContent);
		} else {
//			qzone = new QZoneShareContent();
//			qzone.setShareContent(entity.shareContent);
//			qzone.setTargetUrl("");
//			qzone.setTitle(entity.shareTitle);
//			qzone.setShareImage(mUMImgBitmap);
//			controller.setShareMedia(qzone);
		}
		controller.postShare(activity, media, new SnsPostListener() {
			@Override
			public void onStart() {
			}

			@Override
			public void onComplete(SHARE_MEDIA platform, int eCode,
					SocializeEntity entity) {
				if (eCode != StatusCode.ST_CODE_SUCCESSED) {
					Toast.makeText(activity, "分享失败.", Toast.LENGTH_SHORT)
							.show();
				}
				// BitmapUtil.destoryBitmap(m);
				// BitmapUtil.destoryBitmap(m2);
				// BitmapUtil.destoryBitmap(bitmap);
			}
		});
	}

	/**
	 * 启动友盟分享平台
	 * 
	 * @param activity
	 * @param media
	 * @param entity
	 */
	public static void startImageSharePlatform(final Activity activity,
			SHARE_MEDIA media, CustomSocialShareEntity entity) {
		if (entity == null) {
			return;
		}
		if (media == SHARE_MEDIA.SINA) {
			startWeiBoImageShare(activity, entity);
			return;
		}
		if (media == SHARE_MEDIA.QQ) {
			startQQImageShare(activity, entity);
//			shareQQ(activity,"100424468", "c7394704798a158208a74ab60104f0ba", media, entity);
			return;
		}
		if (media == SHARE_MEDIA.QZONE) {
			startQZoneImageShare(activity, entity);
			return;
		}
		String shareSource = entity.shareSource;
		if (TextUtils.isEmpty(shareSource)) {
			shareSource = "无线长春";
		}
		UMSocialService controller = UMServiceFactory.getUMSocialService(
				shareSource, RequestType.SOCIAL);
		SocializeConfig config = controller.getConfig();
		config.setDefaultShareLocation(false);// 默认不自动定位
		config.setPlatforms(UmengSocialShare.share_media);
		config.setPlatformOrder(UmengSocialShare.share_media);
		String wechatAppId = activity
				.getString(R.string.umeng_social_wechat_app_id);

		String shareTitle = entity.shareTitle;
		String shareContent = entity.shareContent;
		String shareUrl = entity.shareUrl;
		if (TextUtils.isEmpty(shareTitle)) {
			shareTitle = entity.shareContent;
		}
		// 添加微信
		UMWXHandler wxHandler = config.supportWXPlatform(activity, wechatAppId,
				entity.shareUrl);
		wxHandler.setWXTitle(shareTitle);
		// 支持微信朋友圈
		UMWXHandler circleHandler = config.supportWXCirclePlatform(activity,
				wechatAppId, shareUrl);
		circleHandler.setCircleTitle(shareTitle);
		UMImage image = null;
		if (media != SHARE_MEDIA.WEIXIN && media != SHARE_MEDIA.WEIXIN_CIRCLE) {
			shareContent = shareContent + " " + shareUrl + "。";
		}
		
		Bitmap bmp = null;
		if (entity.imageByte != null) {
			bmp = BitmapUtil.byteArrayToBmp(entity.imageByte);
		} else {
			bmp = entity.imageBitmap;
		}
		try {
			image = new UMImage(activity, bmp);
		} catch (OutOfMemoryError e) {
			System.gc();
			Bitmap imageBitmap = BitmapFactory.decodeResource(
					activity.getResources(), R.drawable.ic_launcher);
			image = new UMImage(activity, imageBitmap);
		}
		controller.setShareContent(shareContent);// 设置分享文字内容
		controller.setShareMedia(image);// 设置分享图片内容
		controller.postShare(activity, media, new SnsPostListener() {

			@Override
			public void onStart() {
			}

			@Override
			public void onComplete(SHARE_MEDIA arg0, int eCode,
					SocializeEntity arg2) {
				if (eCode != StatusCode.ST_CODE_SUCCESSED) {
					Toast.makeText(activity, "分享失败.", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
	}

	/**
	 * 启动友盟分享平台
	 * 
	 * @param activity
	 * @param media
	 * @param entity
	 */
	public static void startTextSharePlatform(final Activity activity,
			SHARE_MEDIA media, String shareTitle, String shareContent,
			String shareUrl) {
		if (media == SHARE_MEDIA.SINA) {
			startWeiBoTextShare(activity, shareTitle, shareContent, shareUrl);
			return;
		}
		if (media == SHARE_MEDIA.QQ) {
			startQQTextShare(activity, shareTitle, shareContent, shareUrl);
			return;
		}
		if (media == SHARE_MEDIA.QZONE) {
			startQZoneTextShare(activity, shareTitle, shareContent, shareUrl);
			return;
		}
		UMSocialService controller = UMServiceFactory.getUMSocialService(
				"com.umeng.text", RequestType.SOCIAL);
		SocializeConfig config = controller.getConfig();
		config.setDefaultShareLocation(false);// 默认不自动定位
		config.setPlatforms(UmengSocialShare.share_media);
		config.setPlatformOrder(UmengSocialShare.share_media);
		String wechatAppId = activity
				.getString(R.string.umeng_social_wechat_app_id);
		// 添加微信
		UMWXHandler wxHandler = config.supportWXPlatform(activity, wechatAppId,
				shareUrl);
		if (TextUtils.isEmpty(shareTitle)) {
			shareTitle = shareContent;
		}
		wxHandler.setWXTitle(shareTitle);
		// 支持微信朋友圈
		UMWXHandler circleHandler = config.supportWXCirclePlatform(activity,
				wechatAppId, shareUrl);
		circleHandler.setCircleTitle(shareTitle);
		if (media != SHARE_MEDIA.WEIXIN && media != SHARE_MEDIA.WEIXIN_CIRCLE) {
			shareContent = shareContent + " " + shareUrl + "。";
		}
		controller.setShareContent(shareContent);// 设置分享文字内容
		Bitmap imageBitmap = BitmapFactory.decodeResource(
				activity.getResources(), R.drawable.ic_launcher);
		// 友盟分享的图片
		UMImage image = new UMImage(activity, imageBitmap);
		controller.setShareMedia(image);// 设置分享图片内容

		controller.postShare(activity, media, new SnsPostListener() {

			@Override
			public void onStart() {
			}

			@Override
			public void onComplete(SHARE_MEDIA arg0, int eCode,
					SocializeEntity arg2) {
				if (eCode != StatusCode.ST_CODE_SUCCESSED) {
					Toast.makeText(activity, "分享失败.", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
	}

	/**
	 * 启动随手拍分享
	 * 
	 * @param activity
	 */
	public static void startPhotoShare(final Activity activity,
			CustomSocialShareEntity entity) {
		if (entity != null) {
			String phoneNum = entity.mobile;
			if (TextUtils.isEmpty(phoneNum)) {
				AlertBaseHelper.showConfirm(activity, "",
						activity.getString(R.string.photo_share_notice),
						new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								AlertBaseHelper.dismissAlert(activity);
								Intent loginIntent = new Intent();
								loginIntent.setClassName(
										activity.getApplicationContext(),
										"cn.ffcs.wisdom.city.personcenter.LoginActivity");
								activity.startActivity(loginIntent);
							}
						});
			} else {
				String imagePath = null;
				if (TextUtils.isEmpty(entity.imagePath)) {
					imagePath = saveImageToLocal(entity.imagePath,
							entity.imageByte, entity.imageBitmap);
				} else {
					imagePath = entity.imagePath;
				}
				Intent intent = new Intent();
				intent.setClassName(activity,
						"cn.ffcs.external.photo.activity.CustomUmengSharePhotoActivity");
				intent.putExtra("shareType", entity.shareType);
				intent.putExtra("shareSource", entity.shareSource);
				intent.putExtra("cityCode", entity.cityCode);
				intent.putExtra("imagePath", imagePath);
				intent.putExtra("mobile", entity.mobile);
				activity.startActivity(intent);
			}
		}
	}

	/**
	 * 保存图片到本地
	 * 
	 * @param path
	 * @param byteImage
	 * @param bitmap
	 * @return
	 */
	private static String saveImageToLocal(String path, byte[] byteImage,
			Bitmap bitmap) {
		String fullPath = "";
		Bitmap bitmapImage = null;
		try {
			if (TextUtils.isEmpty(path)) {
				if (byteImage != null) {
					bitmapImage = BitmapUtil.byteArrayToBmp(byteImage);
				} else {
					bitmapImage = bitmap;
				}
				SdcardTool.save(bitmapImage, dir, fileName);
				fullPath = dir + "/" + fileName;
			}
		} catch (Exception e) {
			Log.d("customsocialapp", "图片保存失败");
		}
		return fullPath;
	}

	/**
	 * 启动易信分享，带有图片
	 * 
	 * @param activity
	 * @param entity
	 */
	public static void startYiXinImageShare(Activity activity,
			CustomSocialShareEntity entity, int yixinShareType) {
		YiXinSocialShare
				.yixinShareByImageByte(activity, entity, yixinShareType);
	}

	/**
	 * 启动易信分享，带有图片
	 * 
	 * @param activity
	 * @param entity
	 */
	public static void startYiXinTextShare(Activity activity,
			String shareTitle, String shareContent, String shareUrl,
			int yixinShareType) {
		YiXinSocialShare.yixinShareByText(activity, shareTitle, shareContent,
				shareUrl, yixinShareType);
	}

	/**
	 * 启动微博分享，带有图片
	 * 
	 * @param activity
	 * @param entity
	 */
	public static void startWeiBoImageShare(Activity activity,
			CustomSocialShareEntity entity) {
		WeiBoSocialShare.weiBoShareByBitmap(activity, entity);
	}

	/**
	 * 启动QQ分享，带有图片
	 * 
	 * @param activity
	 * @param entity
	 */
	public static void startQQImageShare(Activity activity,
			CustomSocialShareEntity entity) {
		TencentSocialShare.qqShareByBitmap(activity, entity);
	}

	/**
	 * 启动QQ空间分享，带有图片
	 * 
	 * @param activity
	 * @param entity
	 */
	public static void startQZoneImageShare(Activity activity,
			CustomSocialShareEntity entity) {
		TencentSocialShare.qzoneShareByBitmap(activity, entity);
	}

	private static void startWeiBoTextShare(Activity activity,
			String shareTitle, String shareContent, String shareUrl) {
		WeiBoSocialShare.weiBoShareByText(activity, shareTitle, shareContent,
				shareUrl);

	}

	private static void startQQTextShare(Activity activity, String shareTitle,
			String shareContent, String shareUrl) {
		TencentSocialShare.qqShareByText(activity, shareTitle, shareContent,
				shareUrl);

	}

	private static void startQZoneTextShare(Activity activity,
			String shareTitle, String shareContent, String shareUrl) {
		TencentSocialShare.qzoneShareByText(activity, shareTitle, shareContent,
				shareUrl);

	}
}
