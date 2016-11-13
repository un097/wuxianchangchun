package com.ctbri.wxcc.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.ctbri.wxcc.R;
import com.wookii.tools.comm.LogS;

public class CycleView extends View {

	private Drawable bg;
	private Drawable line;
	private boolean isInit;
	private PaintFlagsDrawFilter filter;

	private float degress;
	private float degressValue = 360;

	long startTime;
	long duration;
	private boolean isStart;
	private boolean hasAnim = true;

	private int pivotX, pivotY;
	private int defWidth,defHeight;

	public CycleView(Context context) {
		this(context,null);
	}
	public CycleView(Context context, AttributeSet attrs) {
		super(context, attrs);

//		if (isInEditMode())
//			return;
		
		filter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
				| Paint.FILTER_BITMAP_FLAG);

		Resources res = getResources();
		bg = res.getDrawable(R.drawable.global_loading_cycle_bg);
		line = res.getDrawable(R.drawable.global_loading_cycle_line);
		defHeight = bg.getIntrinsicHeight();
		defWidth = bg.getIntrinsicWidth();
		init();
	}


	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (isInit && hasAnim) {
			canvas.save();
			canvas.setDrawFilter(filter);
			bg.draw(canvas);
			
			degress = ((AnimationUtils.currentAnimationTimeMillis() - startTime) % duration)
					/ (float) duration * degressValue;
			canvas.rotate(degress, pivotX, pivotY);
			line.draw(canvas);
			
			canvas.restore();
//			invalidate();
			postInvalidateDelayed(1000/25);
		}

	}


	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		super.onVisibilityChanged(changedView, visibility);

		// System.out.println("Visi============================bility");

		if (visibility == GONE || visibility == INVISIBLE) {
			stopAnim();
		} else {
			startAnim();
		}
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);
		
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		System.out.println("heightMode  AT_MOST = " + (heightMode==MeasureSpec.AT_MOST));
		System.out.println("heightMode  EXACTLY = " + (heightMode==MeasureSpec.EXACTLY));
		System.out.println("heightMode  AT_MOST = " + (heightMode==MeasureSpec.AT_MOST));
		System.out.println("spec  w="+ width + " height="+ height);
		
		int heightSize = -1, widthSize = -1;
		if(heightMode == MeasureSpec.EXACTLY)
			heightSize = height;
		else
			heightSize = defHeight;
		
		if(widthMode == MeasureSpec.EXACTLY)
			widthSize = width;
		else
			widthSize = defWidth;
		
//		switch(heightMode){
//	
//		case MeasureSpec.AT_MOST:
//			height = Math.min(defHeight, height);
//			break;
//		case MeasureSpec.EXACTLY:
//		case MeasureSpec.UNSPECIFIED:
//			height = defHeight;
//			break;
//		}
//		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//		switch(widthMode){
//		
//		case MeasureSpec.AT_MOST:
//			width = Math.min(defWidth, width);
//			break;
//		case MeasureSpec.EXACTLY:
//		case MeasureSpec.UNSPECIFIED:
//			width	 = defWidth;
//			break;
//		}
		
		LogS.i("cycleView", "w="+widthSize + " h="+heightSize+"  hmode="+ heightMode);
		setMeasuredDimension(widthSize, heightSize);
	}

	private void stopAnim() {
		isStart = false;
		hasAnim = false;
		
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		startAnim();
	}
	private void startAnim() {
		if(isStart)return;
		isStart = true;
		hasAnim = true;
		startTime = AnimationUtils.currentAnimationTimeMillis();
	}

	private void init() {
		isInit = true;
		duration = 1000;
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		stopAnim();
	}

	@Override
	protected void onSizeChanged(int width, int height, int oldw, int oldh) {
		super.onSizeChanged(width, height, oldw, oldh);

		pivotX = width / 2;
		pivotY = height / 2;

		// 缩放后的图像宽高，和控件的宽高， 取最小两者中最小的一个单位，为动画的的宽和高
		int bg_width = Math.min(width, bg.getIntrinsicWidth());
		int bg_height = bg_width = Math.min(height, bg_width);

		bg.setBounds(width / 2 - bg_width / 2, height / 2 - bg_height / 2,
					width / 2 + bg_width / 2, height / 2 + bg_height / 2);
		
		line.setBounds(width / 2 - bg_width / 2, height / 2, width / 2,
					height / 2 + bg_height /2);
	}

}
