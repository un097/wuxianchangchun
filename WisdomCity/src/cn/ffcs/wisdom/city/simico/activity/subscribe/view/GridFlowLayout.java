package cn.ffcs.wisdom.city.simico.activity.subscribe.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import cn.ffcs.wisdom.city.R;

public class GridFlowLayout extends ViewGroup {

	protected int gfColumnWidth = 0;
	protected int gfVSpacing = 0;
	protected int gfMinHSpacing = 0;

	public GridFlowLayout(Context context) {
		this(context, null, 0);
	}

	public GridFlowLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public GridFlowLayout(Context context, AttributeSet attrs, int i) {
		super(context, attrs, i);
		TypedArray ty = context.obtainStyledAttributes(attrs,
				R.styleable.GridFlowLayout, i, 0);
		gfColumnWidth = ty.getDimensionPixelOffset(0, 0);
		gfVSpacing = ty.getDimensionPixelOffset(1, 0);
		gfMinHSpacing = ty.getDimensionPixelOffset(2, 0);
		ty.recycle();
		if (gfColumnWidth < 0)
			gfColumnWidth = 0;
		if (gfVSpacing < 0)
			gfVSpacing = 0;
		if (gfMinHSpacing < 0)
			gfMinHSpacing = 0;
	}

	protected GridFlowLayoutParams a() {
		return new GridFlowLayoutParams(-2, -2);
	}

	public GridFlowLayoutParams a(AttributeSet attrs) {
		return new GridFlowLayoutParams(getContext(), attrs);
	}

	protected GridFlowLayoutParams a(ViewGroup.LayoutParams params) {
		return new GridFlowLayoutParams(params.width, params.height);
	}

	protected boolean checkLayoutParams(ViewGroup.LayoutParams params) {
		return params instanceof GridFlowLayoutParams;
	}

	protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
		return a();
	}

	public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
		return a(attrs);
	}

	protected android.view.ViewGroup.LayoutParams generateLayoutParams(
			android.view.ViewGroup.LayoutParams layoutparams) {
		return a(layoutparams);
	}

	@Override
	protected void onLayout(boolean flag, int i, int j, int k, int l) {
		int i1 = getChildCount();
		for (int j1 = 0; j1 < i1; j1++) {
			View view = getChildAt(j1);
			GridFlowLayoutParams g1 = (GridFlowLayoutParams) view
					.getLayoutParams();
			view.layout(g1.a, g1.b, g1.a + view.getMeasuredWidth(),
					g1.b + view.getMeasuredHeight());
		}

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		
		int paddingTop = getPaddingTop();
		int paddingLeft = getPaddingLeft();
		
		int measuredWidth;
		int measuredHeight;
		

		if (widthMode == 0)
			measuredWidth = 0;
		else
			measuredWidth = widthSize;

		int w = measuredWidth - getPaddingLeft() - getPaddingRight();

		int i3;
		int j3;
		int l2 = 1;
		if (gfColumnWidth <= 0) {
			i3 = 0;
			j3 = l2;
		} else {
			if (w > gfColumnWidth + 2 * gfMinHSpacing)
				l2 = (w - gfMinHSpacing) / (gfColumnWidth + gfMinHSpacing);
			if (l2 < 1)
				l2 = 1;
			if (w <= gfColumnWidth) {
				i3 = 0;
				j3 = l2;
			} else {
				i3 = (w - l2 * gfColumnWidth) / (l2 + 1);
				j3 = l2;
			}
		}
		
		int k3 = paddingLeft + i3;
		int l3 = 0;
		//datas.clear();
		int childWidth = MeasureSpec.makeMeasureSpec(gfColumnWidth, 0x80000000);
		int j4 = 0;
		int k4 = 0;
		int childCount = getChildCount();
		while (k4 < childCount) {
			View view = getChildAt(k4);
			GridFlowLayoutParams params = (GridFlowLayoutParams) view.getLayoutParams();
			int i5;
			int j5;
			int k5;
			int l5;
			int i6;
			if (w <= 0 || gfColumnWidth <= 0) {
				i5 = MeasureSpec.makeMeasureSpec(0, 0x40000000);
				view.measure(i5, MeasureSpec.makeMeasureSpec(0, 0x40000000));
				params.a = k3;
				params.b = paddingTop;
				j5 = j4;
				k5 = k3;
				l5 = paddingTop;
				i6 = l3;
			} else {
				int childHeight;
				if (params.height == -2)
					childHeight = MeasureSpec.makeMeasureSpec(0, 0);
				else if (params.height == -1)
					childHeight = MeasureSpec.makeMeasureSpec(0, 0x40000000);
				else
					childHeight = MeasureSpec.makeMeasureSpec(params.height, 0x40000000);
				
				view.measure(childWidth, childHeight);
				
				if (view.getMeasuredHeight() > l3)
					l3 = view.getMeasuredHeight();
				
				int k6;
				if (k4 > 0 && j4 == 0)
					k6 = paddingTop + gfVSpacing;
				else
					k6 = paddingTop;
				
				params.a = k3;
				params.b = k6;
				j5 = j4 + 1;
				if (j5 >= j3) {
					j5 = 0;
					k5 = paddingLeft + i3;
					l5 = k6 + l3;
					i6 = 0;
					i5 = childWidth;
				} else {
					l5 = k6;
					i5 = childWidth;
					k5 = k3 + (i3 + gfColumnWidth);
					i6 = l3;
				}
			}
			k4++;
			k3 = k5;
			childWidth = i5;
			l3 = i6;
			paddingTop = l5;
			j4 = j5;
		}

		if (l3 > 0)
			paddingTop += l3;
		if (heightMode == 0)
			measuredHeight = paddingTop + getPaddingBottom();
		else if (heightMode == 0x80000000)
			measuredHeight = Math.min(heightSize, paddingTop + getPaddingBottom());
		else
			measuredHeight = heightSize;
		setMeasuredDimension(measuredWidth, measuredHeight);
	}
}
