package com.ctbri.wxcc.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.ctbri.wxcc.R;

public class LineLayout extends ViewGroup {
	/**
	 * 默认的每行行数
	 */
	private static final int DEFAULT_COLOUMNS = 4;
	private static final int DEFAULT_CELL_PADDING = 4;
	private int coloumns;
	private int cell_padding;

	private static final String TAG = LineLayout.class.getName();
	private static final boolean DEBUG = false;

	public LineLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.line_layout_style, 0, -1);
		if (ta != null) {
			coloumns = ta.getInt(R.styleable.line_layout_style_columns, DEFAULT_COLOUMNS);
			cell_padding = ta.getDimensionPixelSize(R.styleable.line_layout_style_cell_padding, DEFAULT_CELL_PADDING);
		} else {
			coloumns = DEFAULT_COLOUMNS;
			cell_padding = DEFAULT_CELL_PADDING;

		}

		ta.recycle();
	}

	
	public int getColoumns() {
		return coloumns;
	}


	public void setColoumns(int coloumns) {
		this.coloumns = coloumns;
	}


	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		final int width = r - l;
		final int real_width = width - getPaddingLeft() - getPaddingRight() - ((coloumns - 1) * cell_padding);
		final int child_width = real_width / coloumns;

		int child_count = getChildCount();
		int start_left = getPaddingLeft();
		int top = getPaddingTop();
		for (int i = 0; i < child_count; i++) {
			View child = getChildAt(i);
			int right = start_left + child_width;
			if(DEBUG)
			Log.d(TAG, "child layout  =" + start_left + " - " + top + " - " + right + " - " + (child.getMeasuredHeight() + top));
			child.layout(start_left, top, right, child.getMeasuredHeight() + top);
			start_left = right + cell_padding;
		}

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int width_size = MeasureSpec.getSize(widthMeasureSpec);
		final int width_mode = MeasureSpec.getMode(widthMeasureSpec);
		int child_width = 0;
		if (width_size > 0) {
			child_width = (width_size - ((coloumns - 1) * cell_padding)) / coloumns;
			child_width = child_width < 0 ? 0 : child_width;
		}

		int childWidthSpec = MeasureSpec.makeMeasureSpec(child_width, MeasureSpec.EXACTLY);
		int childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		
		measureChildren(childWidthSpec, childHeightSpec);

		final int child_count = getChildCount();
		int max_width = getPaddingLeft() + getPaddingRight();
		int max_height = getPaddingTop() + getPaddingBottom();

		if (width_mode == MeasureSpec.EXACTLY) {
			if(DEBUG)
			Log.d(TAG, "measure width = width_mode == MeasureSpec.EXACTLY");
			// 统计子 view 的宽度
			for (int i = 0; i < child_count; i++) {
				View child = getChildAt(i);
				max_width += child.getMeasuredWidth();
				if (child.getMeasuredHeight() > max_height)
					max_height = child.getMeasuredHeight() + getPaddingTop() + getPaddingBottom();
				if (i < child_count - 1)
					max_width += cell_padding;
			}
			if (child_width != 0)
				max_width = width_size;
		} else {
			if(DEBUG)
			Log.d(TAG, "measure width = width_mode == default");
			max_width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
			max_height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
		}
		if(DEBUG)
		Log.d(TAG, "measure width    size= " + max_width + " x " + max_height);
		setMeasuredDimension(max_width, max_height);
	}

}
