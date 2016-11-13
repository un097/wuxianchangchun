package cn.ffcs.external.share.view;

import im.yixin.sdk.api.IYXAPI;
import im.yixin.sdk.api.SendMessageToYX;
import im.yixin.sdk.api.YXAPIFactory;
import im.yixin.sdk.api.YXMessage;
import im.yixin.sdk.api.YXWebPageMessageData;
import im.yixin.sdk.util.BitmapUtil;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import cn.ffcs.external.share.entity.CustomSocialShareEntity;

import com.example.external_socialshare.R;

/** 
* </p>
* <p>@author: Leo               </p>
* <p>Copyright: Copyright (c) 2013    </p>
* <p>Company: FFCS Co., Ltd.          </p>
* <p>Create Time: 2013-12-10             </p>
* <p>Update Time:                     </p>
* <p>Updater:                         </p>
* <p>Update Comments:                 </p>
*/
public class YiXinSocialShare {
	private static IYXAPI api;
	public static String mYixinAppId;

	/**
	 * 注册易信
	 * @param activity
	 */
	public static void yixinRegister(Activity activity) {
		String yixinAppId = activity.getString(R.string.umeng_social_yixin_app_id);
		mYixinAppId = yixinAppId;
		api = YXAPIFactory.createYXAPI(activity, yixinAppId);
		api.registerApp();
	}

	/**
	 * 分享纯文字
	 * @param activity
	 * @param shareContent
	 * @param shareUrl
	 * @param yixinShareType
	 */
	public static void yixinShareByText(Activity activity, String shareTitle, String shareContent,
			String shareUrl, int yixinShareType) {
		yixinRegister(activity);
		YXWebPageMessageData webpage = new YXWebPageMessageData();
		webpage.webPageUrl = shareUrl;
		YXMessage msg = new YXMessage(webpage);
		if (TextUtils.isEmpty(shareTitle)) {
			shareTitle = shareContent;
		}
		msg.title = shareTitle;
		msg.description = shareContent;
		Bitmap thumb = BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.ic_launcher);
		msg.thumbData = BitmapUtil.bmpToByteArray(thumb, true);
		SendMessageToYX.Req req = new SendMessageToYX.Req();
		req.transaction = buildTransaction("webpage");
		req.message = msg;
		req.scene = yixinShareType;
		api.sendRequest(req);
	}

	/**
	 * 易信分享入口
	 * @param activity
	 * @param imageByte
	 * @param shareContent
	 * @param shareUrl
	 * @param yixinShareType
	 */
	public static void yixinShareByImageByte(Activity activity, CustomSocialShareEntity entity,
			int yixinShareType) {
		yixinRegister(activity);
		// Bitmap bmp = null;
		// if (entity.imageByte != null) {
		// bmp = BitmapUtil.byteArrayToBmp(entity.imageByte);
		// } else {
		// bmp = entity.imageBitmap;
		// }
		// YXImageMessageData imgObj = new YXImageMessageData(bmp);
		// YXMessage msg = new YXMessage();
		// msg.messageData = imgObj;
		// if (bmp != null) {
		// Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 200, 200, true);
		// msg.thumbData = BitmapUtil.bmpToByteArray(thumbBmp, true); // 设置缩略图
		// String title = entity.shareTitle;
		// if (TextUtils.isEmpty(title)) {
		// title = entity.shareContent;
		// }
		// if (yixinShareType == SendMessageToYX.Req.YXSceneTimeline) {
		// msg.comment = entity.shareContent;
		// }
		// msg.title = title;
		// imgObj.imageUrl = entity.shareUrl;
		// msg.description = entity.shareContent;
		// SendMessageToYX.Req req = new SendMessageToYX.Req();
		// req.transaction = buildTransaction("img");
		// req.message = msg;
		// req.scene = yixinShareType;
		// api.sendRequest(req);
		// }

		YXWebPageMessageData webpage = new YXWebPageMessageData();
		String shareUrl = entity.shareUrl;
		String shareTitle = entity.shareTitle;
		String shareContent = entity.shareContent;
		YXMessage msg = new YXMessage(webpage);
		if (TextUtils.isEmpty(shareTitle)) {
			shareTitle = shareContent;
		}
		webpage.webPageUrl = shareUrl;
		msg.title = shareTitle;
		msg.description = shareContent;
		Bitmap bmp = null;
		if (entity.imageByte != null) {
			bmp = BitmapUtil.byteArrayToBmp(entity.imageByte);
		} else {
			bmp = entity.imageBitmap;
		}
		 Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 200, 200, true);
	    msg.thumbData = BitmapUtil.bmpToByteArray(thumbBmp, true); // 设置缩略图
		SendMessageToYX.Req req = new SendMessageToYX.Req();
		req.transaction = buildTransaction("webpage");
		req.message = msg;
		req.scene = yixinShareType;
		api.sendRequest(req);
	}

	/**
	 * 标识符，用于区分每次提交
	 * @param type
	 * @return
	 */
	private static String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type
				+ System.currentTimeMillis();
	}

}
