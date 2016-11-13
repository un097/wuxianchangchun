package com.ctbri.wxcc.widget;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.ThumbnailUtils;

import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

@SuppressLint("NewApi")
public class SelfRounderDisplayer implements BitmapDisplayer {

	protected final int cornerRadius;
	protected final int margin;
	protected Resources res;

	public SelfRounderDisplayer(int cornerRadiusPixels, Resources res) {
		this(cornerRadiusPixels, 0, res);
	}

	public SelfRounderDisplayer(int cornerRadiusPixels, int marginPixels, Resources res1) {
		this.res = res1;
		this.cornerRadius = cornerRadiusPixels;
		this.margin = marginPixels;
	}

	@Override
	public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
		if (!(imageAware instanceof ImageViewAware)) {
			throw new IllegalArgumentException("ImageAware should wrap ImageView. ImageViewAware is expected.");
		}
		if (android.os.Build.VERSION.SDK_INT > 17 && false)
			imageAware.setImageDrawable(new RoundedDrawable_orign(bitmap, cornerRadius, margin));
		else
			imageAware.setImageDrawable(new RoundedDrawable(bitmap, cornerRadius, margin));
	}

	protected static class RoundedDrawable extends Drawable {

		protected final float cornerRadius;
		protected final int margin;

		protected final RectF mRect = new RectF();
		protected final Rect rect = new Rect();
		protected BitmapShader bitmapShader;
		protected final Paint paint;
		protected Bitmap mmap;

		RoundedDrawable(Bitmap bitmap, int cornerRadius, int margin) {
			this.cornerRadius = cornerRadius;
			this.margin = margin;

			// bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP,
			// Shader.TileMode.CLAMP);
			mmap = bitmap;
			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setShader(bitmapShader);
		}

		@Override
		protected void onBoundsChange(Rect bounds) {
			super.onBoundsChange(bounds);
			mmap = ThumbnailUtils.extractThumbnail(mmap, bounds.right - bounds.left, bounds.bottom - bounds.top);
			bitmapShader = new BitmapShader(mmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
			paint.setShader(bitmapShader);
			rect.set(bounds);
			mRect.set(margin, margin, bounds.width() - margin, bounds.height() - margin);
		}

		@Override
		public void draw(Canvas canvas) {
			// ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
			// 设置显示的图片
			// shapeDrawable.getPaint().setShader(bitmapShader);
			// 设置显示的长和高
			// shapeDrawable.setBounds(rect);
			// shapeDrawable.setBounds(0, 0, bitPicWidth, bitPicHeight);
			// canvas.save();
			// canvas.scale(0.1f, 0.1f);
			// 绘制图像
			// shapeDrawable.draw(canvas);
			// super.draw(canvas);
			// paint.setShader(null);
			// paint.setColor(Color.TRANSPARENT);
			// int width = canvas.getWidth();
			// int height = canvas.getHeight();
			// System.out.println("width  "+ width + "   height="+ height);
			canvas.drawRoundRect(mRect, cornerRadius, cornerRadius, paint);
			// canvas.restore();
		}

		@Override
		public int getOpacity() {
			return PixelFormat.TRANSLUCENT;
		}

		@Override
		public void setAlpha(int alpha) {
			paint.setAlpha(alpha);
		}

		@Override
		public void setColorFilter(ColorFilter cf) {
			paint.setColorFilter(cf);
		}
	}

	protected static class RoundedDrawable_orign extends Drawable {

		protected final float cornerRadius;
		protected final int margin;

		protected final RectF mRect = new RectF();
		protected final BitmapShader bitmapShader;
		protected final Paint paint;

		RoundedDrawable_orign(Bitmap bitmap, int cornerRadius, int margin) {
			this.cornerRadius = cornerRadius;
			this.margin = margin;

			bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setShader(bitmapShader);
		}

		@Override
		protected void onBoundsChange(Rect bounds) {
			super.onBoundsChange(bounds);
			mRect.set(margin, margin, bounds.width() - margin, bounds.height() - margin);
		}

		@Override
		public void draw(Canvas canvas) {
			canvas.drawRoundRect(mRect, cornerRadius, cornerRadius, paint);
		}

		@Override
		public int getOpacity() {
			return PixelFormat.TRANSLUCENT;
		}

		@Override
		public void setAlpha(int alpha) {
			paint.setAlpha(alpha);
		}

		@Override
		public void setColorFilter(ColorFilter cf) {
			paint.setColorFilter(cf);
		}
	}

}
