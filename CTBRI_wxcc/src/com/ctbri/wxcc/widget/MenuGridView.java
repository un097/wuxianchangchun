package com.ctbri.wxcc.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

import com.ctbri.wxcc.R;

public class MenuGridView extends GridView {

	private Paint mPaint;
	private boolean isDrawBorder;
	/** 是否适应可滚动的父视图 **/
	private boolean isAdjustScrollParent;

	public MenuGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (isInEditMode())
			return;
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		int borderColor = Color.RED;
		TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.menu_grid, 0, R.style.MenuGridDefaultStyle);
		if (arr != null) {
			borderColor = arr.getColor(R.styleable.menu_grid_grid_border, Color.RED);
			isDrawBorder = arr.getBoolean(R.styleable.menu_grid_is_draw_border, false);

			isAdjustScrollParent = arr.getBoolean(R.styleable.menu_grid_is_adjust_scroll_parent, true);
		}
		arr.recycle();
		mPaint.setColor(borderColor);
	}

	public boolean isDrawBorder() {
		return isDrawBorder;
	}

	public void setDrawBorder(boolean isDrawBorder) {
		if (this.isDrawBorder != isDrawBorder) {
			this.isDrawBorder = isDrawBorder;
			invalidate();
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (!isDrawBorder)
			return;
		int count = getChildCount();
		if (count > 0) {
			View first_child = getChildAt(0);
			int firstChildWidth = first_child.getWidth();
			// firstChildWidth = getColumnWidth();
			int columns = getWidth() / firstChildWidth;
			boolean hasBlank = count % columns > 0;

			int m_top, m_right, m_left, m_bottom = m_left = m_right = m_top = 0;
			// canvas.save();
			// 绘制顶部线

			for (int i = 0; i < count; i++) {

				View v = getChildAt(i);
				m_top = v.getTop();
				m_right = v.getRight();
				m_left = v.getLeft();
				m_bottom = v.getBottom();
				// 如果子控件的位置 在 第2个跟 count-1 个之间，则画一条右侧的竖线
				if ((i + 1) % columns > 0) {
					canvas.drawLine(m_right, m_top, m_right, m_bottom, mPaint);
				}
				// 如果子控件不在第一行，在顶部画一条横线
				if (i + 1 - columns > 0) {
					canvas.drawLine(m_left, m_top, m_right, m_top, mPaint);
				}
				// 如果有空白格，则在空白格的上方 绘制底部线
				if (hasBlank)
					// 如果当前格 的下方是空白格，
					if (i >= count - columns && i + 1 + count % columns <= count) {
						canvas.drawLine(m_left, m_bottom, m_right, m_bottom, mPaint);
					}
			}
			// 绘制底部线
			canvas.drawLine(getLeft(), m_bottom, getRight(), m_bottom, mPaint);
			// canvas.restore();

		}

	}

	public boolean isAdjustScrollParent() {
		return isAdjustScrollParent;
	}

	/**
	 * 当前父类为 可滚动的容器时，是否自适应高度
	 * 
	 * @param isAdjustScrollParent
	 */
	public void setAdjustScrollParent(boolean isAdjustScrollParent) {
		this.isAdjustScrollParent = isAdjustScrollParent;
		requestLayout();
	}

	private void drawCellLine(Canvas canvas, int row, int col) {
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// int width = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE,
		// MeasureSpec.AT_MOST);
		// int height = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
		// MeasureSpec.UNSPECIFIED);
		if (isAdjustScrollParent) {
			int height = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
			super.onMeasure(widthMeasureSpec, height);
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}

		// super.onMeasure( widthMeasureSpec, heightMeasureSpec);

	}

}
