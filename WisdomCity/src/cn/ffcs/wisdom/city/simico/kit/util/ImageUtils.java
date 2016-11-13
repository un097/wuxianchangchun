package cn.ffcs.wisdom.city.simico.kit.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageUtils {

	public static void recyleBitmap(Bitmap bm) {
		if (bm != null && !bm.isRecycled()) {
			bm.recycle();
		}
	}

	public static void recyleBitmap(View view) {
		if (view.getBackground() != null) {
			// TLog.log(TAG, "unbindDrawable:" + view);
			view.getBackground().setCallback(null);
			// view.setBackgroundDrawable(null);
			Drawable d = view.getBackground();
			if (d instanceof BitmapDrawable) {
				((BitmapDrawable) view.getBackground()).getBitmap().recycle();
			}
			view.setBackgroundResource(0);
		}
		if (view instanceof ImageView) {
			ImageView imgView = (ImageView) view;
			BitmapDrawable db = (BitmapDrawable) imgView.getDrawable();
			if (db != null) {
				Bitmap bm = db.getBitmap();
				if (bm != null && !bm.isRecycled()) {
					// TLog.log(TAG, "recycle bitmap :" + view);
					bm.recycle();
					bm = null;
				}
			}
			imgView.setImageBitmap(null);
		}
	}

	public static void unbindDrawables(View view) {
		if (view == null)
			return;
		if (view.getBackground() != null) {
			// TLog.log(TAG, "unbindDrawable:" + view);
			view.getBackground().setCallback(null);
		}
		if (view instanceof ViewGroup) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				unbindDrawables(((ViewGroup) view).getChildAt(i));
			}
			((ViewGroup) view).removeAllViews();
		}
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	public static Bitmap decodeSampledBitmapFromResource(Resources res,
			int resId, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	public static Bitmap scaleBitmap(Bitmap bitmap, float scale) {
		if (scale == 1.0f) {
			return bitmap;
		} else {
			return Bitmap.createScaledBitmap(bitmap,
					(int) (scale * bitmap.getWidth()),
					(int) (scale * bitmap.getHeight()), true);
		}
	}

	public static Bitmap scaleBitmap(String filePath, float scale) {
		Bitmap bitmap = BitmapFactory.decodeFile(filePath);
		if (bitmap != null)
			return scaleBitmap(bitmap, scale);
		return null;
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		// 根据原来图片大小画一个矩形
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		// 圆角弧度参数,数值越大圆角越大,甚至可以画圆形
		final float roundPx = 7;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		// 画出一个圆角的矩形
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		// 取两层绘制交集,显示上层
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		// 显示图片
		canvas.drawBitmap(bitmap, rect, rect, paint);
		// 返回Bitmap对象
		return output;
	}

	public static Bitmap getRoundedCornerBitmapBig(Bitmap bitmap) {
		Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(outBitmap);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPX = bitmap.getWidth() / 2;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPX, roundPX, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return outBitmap;
	}
}
