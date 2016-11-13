package cn.ffcs.external.share.view;

import im.yixin.sdk.util.BitmapUtil;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import cn.ffcs.external.share.entity.CustomSocialShareEntity;

import com.example.external_socialshare.R;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.utils.Utility;

public class WeiBoSocialShare {
	private static WeiboAuth mWeiboAuth;
	public static SsoHandler mSsoHandler;
	private static IWeiboShareAPI api;
	public static String mWeiBoAppId;
	public static String mWeiBoRedirectUrl;
	
	/**
	 * 注册微博
	 * @param activity
	 */
	public static void weiBoRegister(Activity activity, CustomSocialShareEntity entity) {
		
		String weiboAppId = activity.getString(R.string.social_weibo_app_key);
		String weiboRedirectUrl = activity.getString(R.string.social_weibo_redirect_url);
		mWeiBoAppId = weiboAppId;
		mWeiBoRedirectUrl = weiboRedirectUrl;
		
		api = WeiboShareSDK.createWeiboAPI(activity, mWeiBoAppId);
		api.registerApp();
		if (api.checkEnvironment(true)) {
			mWeiboAuth = new WeiboAuth(activity, weiboAppId, weiboRedirectUrl, "all");
			mSsoHandler = new SsoHandler(activity, mWeiboAuth);
//			mWeiboAuth.anthorize(new OnAuthListener(activity, entity));
			mSsoHandler.authorize(new OnAuthListener(activity, entity));
		}
	}
	
	public static void weiBoRegister(Activity activity, String shareTitle, String shareContent, String shareUrl) {
		
		String weiboAppId = activity.getString(R.string.social_weibo_app_key);
		String weiboRedirectUrl = activity.getString(R.string.social_weibo_redirect_url);
		mWeiBoAppId = weiboAppId;
		mWeiBoRedirectUrl = weiboRedirectUrl;
		
		api = WeiboShareSDK.createWeiboAPI(activity, mWeiBoAppId);
		api.registerApp();
		if (api.checkEnvironment(true)) {
			mWeiboAuth = new WeiboAuth(activity, weiboAppId, weiboRedirectUrl, "all");
			mSsoHandler = new SsoHandler(activity, mWeiboAuth);
//			mWeiboAuth.anthorize(new OnAuthListener(activity, entity));
			mSsoHandler.authorize(new OnAuthListener1(activity, shareTitle, shareContent, shareUrl));
		}
	}
	
	public static void weiBoShareByBitmap(Activity activity, CustomSocialShareEntity entity) {
		weiBoRegister(activity, entity);
	}
	
	public static void weiBoShareByText(Activity activity, String shareTitle, String shareContent, String shareUrl) {
		weiBoRegister(activity, shareTitle, shareContent, shareUrl);
	}
	
	public static class OnAuthListener implements WeiboAuthListener {
		
		Context context;
		CustomSocialShareEntity entity;

		public OnAuthListener(Context context, CustomSocialShareEntity entity) {
			super();
			this.context = context;
			this.entity = entity;
		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
			Log.e("sb", "oncancel111");
		}

		@Override
		public void onComplete(Bundle arg0) {
			String shareUrl = entity.shareUrl;
			String shareTitle = entity.shareTitle;
			String shareContent = entity.shareContent;
			Log.e("sb", " entity.shareUrl                 " + entity.shareUrl);
			if (TextUtils.isEmpty(shareTitle)) {
				shareTitle = shareContent;
			}
			Bitmap bmp = null;
			if (entity.imageByte != null) {
				bmp = BitmapUtil.byteArrayToBmp(entity.imageByte);
			} else {
				bmp = entity.imageBitmap;
			}
			Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 200, 200, true);
			TextObject textObject = new TextObject();
			textObject.title = shareTitle;
			textObject.text = shareContent;
			WebpageObject webObject = new WebpageObject();
			webObject.identify = Utility.generateGUID();
			webObject.title = shareTitle;
			webObject.description = shareContent;
			webObject.actionUrl = shareUrl;
			webObject.setThumbImage(thumbBmp);
			ImageObject imageObject = new ImageObject();
			imageObject.setImageObject(bmp);
			WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
			weiboMessage.textObject = textObject;
			weiboMessage.imageObject = imageObject;
			weiboMessage.mediaObject = webObject;
			SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
			request.transaction = String.valueOf(System.currentTimeMillis());
			request.multiMessage = weiboMessage;
			api.sendRequest(request);
			
		}

		@Override
		public void onWeiboException(WeiboException e) {
			Toast.makeText(context, "授权失败",
					Toast.LENGTH_SHORT).show();
			Log.i("weibo", e.getLocalizedMessage());
			Log.e("sb", "    onWeiboException111" + e.getMessage());
			
		}
		
	}
	
public static class OnAuthListener1 implements WeiboAuthListener {
		
		Context context;
		String shareTitle;
		String shareContent;
		String shareUrl;

		public OnAuthListener1(Context context, String shareTitle,
				String shareContent, String shareUrl) {
			super();
			this.context = context;
			this.shareTitle = shareTitle;
			this.shareContent = shareContent;
			this.shareUrl = shareUrl;
		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
			Log.e("sb", "oncancel");
		}

		@Override
		public void onComplete(Bundle arg0) {
			if (TextUtils.isEmpty(shareTitle)) {
				shareTitle = shareContent;
			}
			TextObject textObject = new TextObject();
			textObject.title = shareTitle;
			textObject.text = shareContent;
			WebpageObject webObject = new WebpageObject();
			webObject.identify = Utility.generateGUID();
			webObject.title = shareTitle;
			webObject.description = shareContent;
			webObject.actionUrl = shareUrl;
			WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
			weiboMessage.textObject = textObject;
			weiboMessage.mediaObject = webObject;
			SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
			request.transaction = String.valueOf(System.currentTimeMillis());
			request.multiMessage = weiboMessage;
			api.sendRequest(request);
			
		}

		@Override
		public void onWeiboException(WeiboException e) {
			Toast.makeText(context, "授权失败",
					Toast.LENGTH_SHORT).show();
			Log.i("weibo", e.getLocalizedMessage());
			Log.e("sb", "    onWeiboException" + e.getMessage());
		}
		
	}

}
