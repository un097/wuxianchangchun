package com.ctbri.wxcc.widget;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class RepeatDrawable extends Drawable {
	private Bitmap mBmp;
	private Resources res;
	private int mPadding;
	private int mWidth,mHeight;
	private Rect mBounds = new Rect();
	private int mCount, mTop;
	/**
	 * 指定图片的间隔
	 * @param bmp
	 * @param padding   in pixel
	 */
	public RepeatDrawable(Bitmap bmp, int padding) {
		mBmp = bmp;
		mPadding = padding;
	}
	/**
	 * 指定一个比率，设置两个图片之间的间隔
	 * @param bmp
	 * @param ratio  in float  betwwen  0~1 
	 */
	public RepeatDrawable(Bitmap bmp,Resources res,  float ratio) {
		if(bmp!=null){
			mBmp = bmp;
			int density = res.getDisplayMetrics().densityDpi;
			mWidth = mBmp.getScaledWidth(density);
			mHeight = mBmp.getScaledHeight(density);
			
			mPadding = (int)Math.floor(mWidth * ratio);
		}
	}
	@Override
	protected void onBoundsChange(Rect bounds) {
		super.onBoundsChange(bounds);
		
		copyBounds(mBounds);
		int width = mBounds.right - mBounds.left;
		mCount = width / mWidth;
		mTop = mBounds.bottom - mBounds.top - mHeight;
	}
	@Override
	public void draw(Canvas canvas) {
		if(mBmp==null)return;
		for(int i=0; i < mCount;i++){
			canvas.drawBitmap(mBmp, i * (mWidth + mPadding), mTop,null);
		}
	}
	@Override
	public void setBounds(int left, int top, int right, int bottom) {
		super.setBounds(left, top, right, bottom);
	}

	@Override
	public void setAlpha(int alpha) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getOpacity() {
		 return PixelFormat.TRANSLUCENT;
	}

}
