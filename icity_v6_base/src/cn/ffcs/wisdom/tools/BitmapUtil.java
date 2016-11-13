package cn.ffcs.wisdom.tools;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;

public class BitmapUtil {

	public static Bitmap zoom(Bitmap bitmap, int swidth) {
		int bmpWidth = bitmap.getWidth();
		int bmpHeight = bitmap.getHeight();
		float scale = swidth / (float) bmpWidth;
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmpHeight, matrix, true);
		return bitmap;
	}
	/**
	 * 改变Bitmap图片的大小
	 * @param bitmap
	 * @param newWidth  宽
	 * @param newHeight 高
	 * @return
	 */
	public static Bitmap changeBitmap(Bitmap bitmap, float newWidth, float newHeight) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float scaleWidth = newWidth / width;
		float scaleHeight = newHeight / height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
	}

	/**
	 * 用最省内存的方式读取本地sd卡图片（注：使用次方法的图片在内存不够的时候会被自动回收,如果图片正被activity使用，activity也会被回收）
	 * @param filePath
	 * @return
	 * @throws FileNotFoundException
	 */
	public static Bitmap readBitMapByLowMemory(String filePath) throws FileNotFoundException {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		FileInputStream is = new FileInputStream(filePath);
		return BitmapFactory.decodeStream(is, null, opt);
	}

	/** 
	 * Drawable 转 bitmap 
	 * @param drawable 
	 * @return 
	 */
	public static Bitmap drawable2Bitmap(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		} else if (drawable instanceof NinePatchDrawable) {
			Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable
					.getIntrinsicHeight(),
					drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
							: Bitmap.Config.RGB_565);
			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			drawable.draw(canvas);
			return bitmap;
		} else {
			return null;
		}
	}

	/**
	* 压缩图片
	* @param filePath
	* @return
	*/
	public static Bitmap compressBitmapFromResource(Resources res, int resId, int reqWidth,
			int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions  
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		// Calculate inSampleSize  
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set  
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}
	
	/**
	* 如图片太大，根据文件路径压缩读取
	* @param pathName
	* @return
	*/
	public static Bitmap compressBitmapFromFile(String pathName, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions  
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(pathName, options);

		// Calculate inSampleSize  
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set  
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(pathName, options);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth,
			int reqHeight) {
		// Raw height and width of image  
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 2;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}
		return inSampleSize;
	}

}
