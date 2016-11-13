package com.ctbri.wxcc.widget;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class MyImageView extends ImageView {

	public MyImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MyImageView(Context context, AttributeSet attrs, int style) {
		super(context, attrs, style);
		super.setScaleType(ScaleType.MATRIX);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	public void setScaleType(ScaleType scaleType) {
		// super.setScaleType(scaleType);
	}

	@Override
	protected boolean setFrame(int l, int t, int r, int b) {
		boolean changed = super.setFrame(l, t, r, b);
		//updateMyMatrix();
		return changed;
	}

	@Override
	public void setImageDrawable(Drawable drawable) {
		udpateDrawableSize(drawable);
		super.setImageDrawable(drawable);
	}
	
	private void updateMyMatrix() {
		udpateDrawableSize(getDrawable());
//		updateMyMatrix(getDrawable());
	}
	/**
	 * 重新设置  drawable 的size，让其imageview 适应 drawable 的size; 总是改变  drawable 的大小
	 * @param bd
	 */
	private void udpateDrawableSize(Drawable bd){
		if(bd==null)return;
		
		float scale;
		float dx = 0, dy = 0;
		int dwidth = bd.getIntrinsicWidth();
		int dheight = bd.getIntrinsicHeight();
		int vheight = getHeight();
		int vwidth = getWidth();
		
		scale = (float)vwidth / (float)dwidth;
		bd.setBounds(0, 0, (int)(dwidth * scale), (int)(dheight * scale));
		
		
	}
	/**
	 * 设置 drawable 超出 imageVew size 时，显示 drawable 的哪个部分。
	 * 
	 * @param bd
	 */
	private void updateMyMatrix(Drawable bd) {
		if(bd==null)return;
		
		float scale;
		float dx = 0, dy = 0;
		int dwidth = bd.getIntrinsicWidth();
		int dheight = bd.getIntrinsicHeight();
		int vheight = getHeight();
		int vwidth = getWidth();

		if (dwidth * vheight > vwidth * dheight) {
			scale = (float) vheight / (float) dheight;
			dx = (vwidth - dwidth * scale) * 0.5f;
		} else {
			scale = (float) vwidth / (float) dwidth;
			// 居中
			// dy = (vheight - dheight * scale) * 0.5f;

			// 显示底部
			//dy = vheight - (dheight * scale);

			// 显示顶部
			 dy=0;
		}
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		matrix.postTranslate(dx, dy);
		
		setImageMatrix(matrix);
	}
}
