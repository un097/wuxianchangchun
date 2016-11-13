package com.ctbri.comm.util;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import cn.ffcs.external.share.entity.CustomSocialShareEntity;
import cn.ffcs.external.share.view.CustomSocialShare;

import com.ctbri.wxcc.MessageEditor;
import com.ctbri.wxcc.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class _Utils {
	public static final int SHARE_MAX_TITLE_LENGTH = 28;
	public static final int SHARE_MAX_CONTENT_LENGTH = 35;

	/**
	 * 默认的 DisplayImageOptions 实例
	 */
	public static final DisplayImageOptions DEFAULT_DIO = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.icon_default_image_place_holder)
			.showImageOnFail(R.drawable.icon_default_image_place_holder).showImageOnLoading(R.drawable.icon_default_image_place_holder).bitmapConfig(Config.ARGB_8888)
			.cacheInMemory(false).cacheOnDisc(true).build();

	public static void share(Activity context, CustomSocialShareEntity entity) {
		// 处理 分享的标题长度 和 内容长度
		if (!TextUtils.isEmpty(entity.shareTitle) && entity.shareTitle.length() > SHARE_MAX_TITLE_LENGTH)
			entity.shareTitle = entity.shareTitle.substring(0, SHARE_MAX_TITLE_LENGTH);

		if (!TextUtils.isEmpty(entity.shareContent) && entity.shareContent.length() > SHARE_MAX_CONTENT_LENGTH)
			entity.shareContent = entity.shareContent.substring(0, SHARE_MAX_CONTENT_LENGTH) + "……";

		CustomSocialShare.shareImagePlatform(context, entity, false);
	}

	// public static void shareAndCheckLogin(Activity context, String title,
	// String url, String content, Bitmap bmp){
	// if(!MessageEditor.isLogin(context))
	// {
	// _Utils.login(context);
	// return;
	// }
	// CustomSocialShareEntity entity = new CustomSocialShareEntity();
	// entity.shareTitle = title;
	// entity.shareUrl = url;
	// entity.shareContent = content;
	// entity.imageBitmap = bmp;
	// entity.imageUrl = "";
	// share(context, entity);
	// }
	public static void shareAndCheckLogin(Activity context, String title, String url, String content, Bitmap bmp) {
		if (!MessageEditor.isLogin(context)) {
			_Utils.login(context);
			return;
		}
		CustomSocialShareEntity entity = new CustomSocialShareEntity();
		entity.shareTitle = title;
		entity.shareUrl = url;
		entity.shareContent = content;
		entity.imageBitmap = bmp;
		entity.imageUrl = "";
		share(context, entity);
	}

	/**
	 * 只分享文本
	 * 
	 * @param context
	 * @param title
	 * @param url
	 * @param content
	 */
	public static void share(Activity context, String title, String url, String content) {
		CustomSocialShare.shareTextPlatform(context, title, content, url);
	}

	public static void share(Activity context, String title, String url, String content, String bmpUrl) {
		if (!MessageEditor.isLogin(context)) {
			_Utils.login(context);
			return;
		}
		CustomSocialShareEntity entity = new CustomSocialShareEntity();
		entity.shareTitle = title;
		entity.shareUrl = url;
		entity.imageUrl = bmpUrl;
		entity.shareContent = content;
		ImageLoader.getInstance().loadImage(bmpUrl, DEFAULT_DIO, new ShareImageLoadingListener(context, entity));
	}

	/**
	 * 检查当前用户是否已经登录，未登录则显示登录窗口。
	 * 
	 * @param context
	 * @return booelean true: 已经登录 false:未登录
	 */
	public static boolean checkLoginAndLogin(Activity context) {
		if (!MessageEditor.isLogin(context)) {
			_Utils.login(context);
			return false;
		}
		return true;
	}

	/**
	 * 显示登录界面
	 * 
	 * @param act
	 * @return
	 */
	public static boolean login(Activity act) {
		Intent intent = new Intent();
		intent.setClassName(act, "cn.ffcs.changchuntv.activity.login.LoginActivity");
		if (queryIntent(act, intent).size() > 0) {
			act.startActivity(intent);
			return true;
		} else
			Toast.makeText(act, "没安装登录模块", Toast.LENGTH_SHORT).show();
		return false;
		// cn.ffcs.changchuntv.activity.login.LoginActivity
		// Intent i = new Intent();
		// i.putExtra("loginSuccess", true);
		// setResult(RESULT_OK, i);
	}

	/**
	 * 获取圆角 bitmap
	 * 
	 * @param bitmap
	 * @param roundPx
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	public static List<ResolveInfo> queryIntent(Activity act, Intent intent) {
		return act.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

	}

	/**
	 * 获取应用的默认 icon
	 * 
	 * @param activity
	 * @return
	 */
	public static Bitmap getDefaultAppIcon(Activity activity) {
		Drawable drawable = activity.getApplicationInfo().loadIcon(activity.getPackageManager());
		if (drawable == null)
			return null;
		if (drawable instanceof BitmapDrawable)
			return ((BitmapDrawable) drawable).getBitmap();
		else
			return drawable2Bitmap(drawable);
	}

	/**
	 * drawable 转换为 bitmap
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawable2Bitmap(Drawable drawable) {
		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	public static void ratoteImage() {

	}

	/**
	 * 用反射的方式，调用对象的某一方法
	 * 
	 * @param receiver
	 * @param method
	 * @param clazz
	 * @param params
	 * @param args
	 * @return
	 */
	public static <T> T invokeMethod(Object receiver, Class<?> methodFrom, String method, Class<T> clazz, Class<?>[] params, Object... args) {

		Method tmpMethod;
		try {
			tmpMethod = methodFrom.getMethod(method, params);
			tmpMethod.setAccessible(true);
			Object obj = tmpMethod.invoke(receiver, args);

			Log.i(_Utils.class.getName(), "invoke method" + method);
			if (clazz.isInstance(obj))
				return (T) obj;
			return null;
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getDistance(int distance) {
		int km = distance / 1000;
		int hm = distance % 1000;
		if (km == 0)
			return hm + "m";
		else {
			DecimalFormat df = new DecimalFormat("#.#");
			return df.format(((float) km) + (hm / 1000F)) + "km";
		}
	}

	public static String getTime(int time) {

		time /= 60;

		int hour = time / 60;

		int minute = time % 60;
		if (hour > 0)
			return hour + "小时" + minute + "分钟";

		return minute + "分钟";
	}

	/**
	 * 修正图片方向
	 * 
	 * @param path
	 */
	public static void resetPictureDegress(String path) {
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int degress = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

			switch (degress) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degress = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degress = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degress = 270;
				break;
			}

			if (degress == ExifInterface.ORIENTATION_UNDEFINED) {
				System.out.println("图片方向未定义");
				return;
			}

			System.out.println(" 图片方向 :" + degress);
			Bitmap bitmap = BitmapFactory.decodeFile(path);
			bitmap = resetBitmapDegress(bitmap, degress);
			FileOutputStream fos = new FileOutputStream(path);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			bitmap.compress(CompressFormat.JPEG, 50, bos);
			bos.flush();
			bos.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static Bitmap resetBitmapDegress(Bitmap bitmap, int angle) {

		// 旋转图片 动作
		Matrix matrix = new Matrix();
		;
		matrix.postRotate(angle);
		System.out.println("angle2=" + angle);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

	public static class ShareImageLoadingListener implements ImageLoadingListener {
		private CustomSocialShareEntity entity;
		private Activity context;

		public ShareImageLoadingListener(Activity ctx, CustomSocialShareEntity entity) {
			this.entity = entity;
			this.context = ctx;
		}

		@Override
		public void onLoadingStarted(String imageUri, View view) {

		}

		@Override
		public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
			Toast.makeText(context.getApplicationContext(), "分享失败！[无法获取图片资源]", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap bmp) {
			entity.imageBitmap = bmp;
			_Utils.share(context, entity);
		}

		@Override
		public void onLoadingCancelled(String imageUri, View view) {
		}
	}

	public static final Bitmap buildVideoFrame(Activity context, Uri mUri, long time) {
		MediaMetadataRetriever mediaRetriever = new MediaMetadataRetriever();
		mediaRetriever.setDataSource(context, mUri);
		Bitmap bmp = mediaRetriever.getFrameAtTime(time, MediaMetadataRetriever.OPTION_NEXT_SYNC);
		mediaRetriever.release();
		return bmp;
	}
	
	/**
	 * 获取，是否检测网络环境的开关开启
	 * 3G提示功能保存在SharedPreferences中，
		1.是否开启3G提示；（k_3g_switch，boolean），false表明3G提示开启；
		2.使用什么网络：（k_network_type，int），0表示3G
	 * @param context
	 * @return
	 */
	public static boolean isWifiTriggerOpen(Context context){
		SharedPreferences preferences = context.getSharedPreferences("preferent0x", Context.MODE_PRIVATE);
		if(0 == preferences.getInt("k_network_type", -1)) {
			if(!preferences.getBoolean("k_3g_switch", false)){
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
}
