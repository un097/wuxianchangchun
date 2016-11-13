package com.ctbri.wxcc.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.ctbri.wxcc.R;

/**
 * 方形 imageview ，在layout之后。按指定的比例，重新计算高度，使自己变成方形
 * 
 * @author yanyadi
 * 
 */
public class RectImageView extends ImageView {

	private int size;
	private int mCalcHeight;
	/**
	 * 高度与宽度的比值
	 */
	private float mHeightRatio;
	/**
	 * 默认的图片高度比值
	 */
	private static final float DEFAULT_HEIGHT_RATIO = 1.0f;

	public RectImageView(Context context) {
		super(context);
	}

	public RectImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RectImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.square_imageview_style, defStyle, 0);
		// 获取高度比值
		mHeightRatio = a.getFloat(R.styleable.square_imageview_style_heightRatio, DEFAULT_HEIGHT_RATIO);

		a.recycle();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int mMeasureWidth = getMeasuredWidth();
		int mRealHeight = calculateHeight(mMeasureWidth);
		setMeasuredDimension(mMeasureWidth, mRealHeight);
	}

	private int calculateHeight(int w) {
		if (size == w)
			return mCalcHeight;
		size = w;
		mCalcHeight = (int) (size * mHeightRatio);
		return mCalcHeight;
	}

}
