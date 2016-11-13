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


public class PieceGridView extends GridView {

	private Paint mPaint;
	private boolean isDrawBorder;

	public PieceGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if(isInEditMode())return;
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		int borderColor = Color.RED;
		TypedArray arr = context.obtainStyledAttributes(attrs,
				R.styleable.menu_grid, 0, R.style.MenuGridDefaultStyle);
		if (arr != null) {
			borderColor = arr.getColor(R.styleable.menu_grid_grid_border,
					Color.RED);
			isDrawBorder = arr.getBoolean(R.styleable.menu_grid_is_draw_border, false);
		}
		arr.recycle();
		mPaint.setColor(borderColor);
	}
	
	
	
	public boolean isDrawBorder() {
		return isDrawBorder;
	}



	public void setDrawBorder(boolean isDrawBorder) {
		if(this.isDrawBorder!=isDrawBorder){
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
		if(!isDrawBorder)return;
		int count = getChildCount();
		if (count > 0) {
			View v = getChildAt(0);
			int firstChildWidth = v.getWidth();
//			firstChildWidth = getColumnWidth();
			int columns = getWidth() / firstChildWidth;
			boolean hasBlank = count % columns > 0;
			
//			canvas.save();
			for(int i=0; i < count;i++){
				
				v = getChildAt(i);
				// 如果子控件的位置 在 第2个跟 count-1 个之间，则画一条右侧的竖线
				if( (i+1) % columns > 0){
					canvas.drawLine(v.getRight(), v.getTop(), v.getRight(), v.getBottom(), mPaint);
				}
				// 如果子控件不在第一行，在顶部画一条横线
				if( i+1 - columns > 0){
					canvas.drawLine(v.getLeft(), v.getTop(), v.getRight(), v.getTop(), mPaint);
				}
				// 如果有空白格，则在空白格的上方 绘制底部线
				if(hasBlank)
				if(i >= count-columns && i+1 + count % columns <= count){
					canvas.drawLine(v.getLeft(), v.getBottom(), v.getRight(), v.getBottom(), mPaint);
				}
			}
			
//			canvas.restore();
			
		}

	}
	
	private void drawCellLine(Canvas canvas,int row,int col){
		
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		 int width = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE,
//		 MeasureSpec.AT_MOST);
//		int height = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
//				MeasureSpec.UNSPECIFIED);
		int height = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, height);
//		if(getChildCount() > 0)
//		System.out.println("child height="+getChildAt(0).getMeasuredHeight());
//		super.onMeasure( widthMeasureSpec,  heightMeasureSpec);

		
	}

}
