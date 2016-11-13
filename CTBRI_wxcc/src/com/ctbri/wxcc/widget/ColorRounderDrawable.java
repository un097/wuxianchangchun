package com.ctbri.wxcc.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;

public class ColorRounderDrawable extends ColorDrawable {
	private Paint mPaint;
	private int mRadius;

	/**
	 * Creates a new black ColorDrawable.
	 */
	public ColorRounderDrawable() {
		this(0, 0);
	}

	/**
	 * Creates a new ColorDrawable with the specified color.
	 * 
	 * @param color
	 *            The color to draw.
	 */
	public ColorRounderDrawable(int color, int radius) {
		super(color);
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setColor(color);
		mPaint.setAntiAlias(true);
		mRadius = radius;
		mPaint.setStyle(Style.FILL_AND_STROKE);
		// mPaint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
	}

	public void setRadius(int radius) {
		this.mRadius = radius;
	}

	@Override
	public void draw(Canvas canvas) {

		// super.draw(canvas);
		canvas.drawRoundRect(new RectF(getBounds()), mRadius, mRadius, mPaint);
	}

}
