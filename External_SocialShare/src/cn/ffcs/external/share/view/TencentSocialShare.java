package cn.ffcs.external.share.view;

import com.example.external_socialshare.R;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import cn.ffcs.external.share.entity.CustomSocialShareEntity;

public class TencentSocialShare {
	private static Tencent mTencent;
	public static String mQQAppId;
	
	public static void tencentRegister(Activity activity) {
		String qqAppId = activity.getString(R.string.social_qq_app_id);
		mQQAppId = qqAppId;
		
		mTencent = Tencent.createInstance(qqAppId, activity);
	}
	
	public static void qqShareByBitmap(Activity activity, CustomSocialShareEntity entity) {
		tencentRegister(activity);
		if (!mTencent.isSupportSSOLogin(activity)) {
			return;
		}
		String shareUrl = entity.shareUrl;
		String shareTitle = entity.shareTitle;
		String shareContent = entity.shareContent;
		if (TextUtils.isEmpty(shareTitle)) {
			shareTitle = shareContent;
		}
		String shareimageUrl = entity.imageUrl;
		Bundle params = new Bundle();
		params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
		params.putString(QQShare.SHARE_TO_QQ_TITLE, shareTitle);
		params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareUrl);
		params.putString(QQShare.SHARE_TO_QQ_SUMMARY, shareContent);
//		params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, shareimageUrl);
		if (shareimageUrl == null || shareimageUrl.equals("")) {
			params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, "http://ccgd.153.cn:50000/icity-wap/image/ic_launcher.png");
		}else {
			params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, shareimageUrl); 
		} 
//		params.putSerializable(QQShare.SHARE_TO_QQ_IMAGE_URL,entity.imageBitmap);
//		(QQShare.SHARE_TO_QQ_IMAGE_URL,"http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif");(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, "img");
//		params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,"http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif");
//		params.putSerializable("QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL", entity.imageByte);
		params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "无线长春");
		mTencent.shareToQQ(activity, params, listener);
	}
	
	public static void qqShareByText(Activity activity, String shareTitle, String shareContent, String shareUrl) {
		tencentRegister(activity);
		if (!mTencent.isSupportSSOLogin(activity)) {
			return;
		}
		if (TextUtils.isEmpty(shareTitle)) {
			shareTitle = shareContent;
		}
		Bundle params = new Bundle();
		params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
		params.putString(QQShare.SHARE_TO_QQ_TITLE, shareTitle);
		params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareUrl);
		params.putString(QQShare.SHARE_TO_QQ_SUMMARY, shareContent);
		params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "无线长春");
		mTencent.shareToQQ(activity, params, listener);
	}
	
	public static void qzoneShareByBitmap(Activity activity, CustomSocialShareEntity entity) {
		tencentRegister(activity);
		if (!mTencent.isSupportSSOLogin(activity)) {
			return;
		}
		String shareUrl = entity.shareUrl;
		String shareTitle = entity.shareTitle;
		String shareContent = entity.shareContent;
		if (TextUtils.isEmpty(shareTitle)) {
			shareTitle = shareContent;
		}
		String shareimageUrl = entity.imageUrl;
		Bundle params = new Bundle();
//		params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
//		params.putString(QzoneShare.SHARE_TO_QQ_TITLE, shareTitle);
//		params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, shareUrl);
//		params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, shareContent);
//		// 支持传多个imageUrl
////		ArrayList<String> imageUrls = new ArrayList<String>();
////		imageUrls.add("img");
////		params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
//		params.putString(QzoneShare.SHARE_TO_QQ_IMAGE_URL, shareimageUrl);
//		params.putString(QzoneShare.SHARE_TO_QQ_APP_NAME, "无线长春");
//		mTencent.shareToQzone(activity, params, listener);
		
		params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
		params.putString(QQShare.SHARE_TO_QQ_TITLE, shareTitle);
		params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareUrl);
		params.putString(QQShare.SHARE_TO_QQ_SUMMARY, shareContent);

//		params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, "img");
		if (shareimageUrl == null || shareimageUrl.equals("")) {
			params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, "http://ccgd.153.cn:50000/icity-wap/image/ic_launcher.png");
		}else {
			params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, shareimageUrl);
		}
		
		params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "无线长春");
		int mExtarFlag = 0x00;
		params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
		mTencent.shareToQQ(activity, params, listener);
	}
	
	public static void qzoneShareByText(Activity activity, String shareTitle, String shareContent, String shareUrl) {
		tencentRegister(activity);
		if (!mTencent.isSupportSSOLogin(activity)) {
			return;
		}
		if (TextUtils.isEmpty(shareTitle)) {
			shareTitle = shareContent;
		}
		Bundle params = new Bundle();
		params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
		params.putString(QQShare.SHARE_TO_QQ_TITLE, shareTitle);
		params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareUrl);
		params.putString(QQShare.SHARE_TO_QQ_SUMMARY, shareContent);
		params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "无线长春");
		int mExtarFlag = 0x00;
		params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
		mTencent.shareToQQ(activity, params, listener);
	}
	
	static IUiListener listener = new IUiListener() {

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onComplete(Object arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onError(UiError arg0) {
			// TODO Auto-generated method stub
			
		}
		
	};

}
