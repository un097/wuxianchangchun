package com.ctbri.wxcc.widget;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.CheckBox;

import com.ctbri.wxcc.widget.SmoothMenu.OnSmoothMenuItemSelectListener;

public class SmoothMenuItem {

	private static final String TAG = "SmoothMenuItem";
	private CheckBox view;
	private int x;
	private int type;
	private View parent;
	
	public SmoothMenuItem(View parent, int resId, int type){
		this.parent = parent;
		view = (CheckBox)parent.findViewById(resId);
		view.setClickable(false);
		setAlpha(0.3f);
		this.type = type;
		
		
	}

	public void resetPosition() {
		if(type == OnSmoothMenuItemSelectListener.left) {
			this.x = 0;
		} else {
			this.x = parent.getWidth() - view.getWidth();
		}
		//Log.i(TAG, "resetPosition:" + x);
		view.layout(x, view.getTop(), x + view.getWidth(), view.getBottom());
		setScale(1);
		setSelected(false);
		setAlpha(0.3f);
	}

	void setAlpha(float alpha) {
		// TODO Auto-generated method stub
		view.setAlpha(alpha);
	}

	public int getItemWwidth() {
		return view.getWidth();
	}

	public void setSelected(boolean b) {
		view.setChecked(b);
	}

	public boolean isSelected() {
		return view.isChecked();
	}

	@SuppressLint("NewApi")
	public void setScale(float scaleXY) {
		// TODO Auto-generated method stub
		view.setScaleX(scaleXY);
		view.setScaleY(scaleXY);
	}

	public void sommth(int start) {
		int t = view.getTop();
		int b = view.getBottom();
		int r = view.getRight();
		view.layout(start, t, start + view.getWidth(), b);
	}

	public SmoothMenuItem setType(int type) {
		this.type = type;
		return this;
	}

	@SuppressLint("NewApi")
	public float getScale() {
		// TODO Auto-generated method stub
		return view.getScaleX();
	}

	public float getAlpha() {
		// TODO Auto-generated method stub
		return view.getAlpha();
	}

	public int getType() {
		// TODO Auto-generated method stub
		return type;
	}
	
}
