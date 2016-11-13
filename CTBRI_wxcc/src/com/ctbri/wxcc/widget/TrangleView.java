package com.ctbri.wxcc.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.ctbri.wxcc.R;
/**
 * 优惠券 顶部 三角形
 * @author yanyadi
 *
 */
public class TrangleView extends View {

	public TrangleView(Context context) {
		this(context, null);
	}

	public TrangleView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TrangleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		Resources res = getResources();
		TypedArray ta = context.obtainStyledAttributes(attrs,
				R.styleable.trangle_style, defStyle, 0);
		bgColor = res.getColor(ta.getColor(R.styleable.trangle_style_fillBg,
				R.color.coupon_tag_color));

		ta.recycle();

		paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
		paint.setStyle(Style.FILL);
		paint.setColor(bgColor);
	}
	/**
	 * 更新背影颜色
	 * @param color
	 */
	public void setTrangleColor(int color){
		paint.setColor(getResources().getColor(color));
		postInvalidate();
	}
	
	@Override
	protected void onSizeChanged(int width, int height, int oldw, int oldh) {
		super.onSizeChanged(width, height, oldw, oldh);

		if (trangle == null)
			trangle = new Path();

		trangle.reset();
		trangle.moveTo(0, 0);
		trangle.lineTo(0, height);
		trangle.lineTo(width, 0);
		trangle.close();
	}

	private int bgColor;
	private Paint paint;
	private Path trangle;
	

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		canvas.drawPath(trangle, paint);
		// PathShape ps = new PathShape(path, stdWidth, stdHeight);

	}
}
