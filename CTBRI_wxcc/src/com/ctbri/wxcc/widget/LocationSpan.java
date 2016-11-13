package com.ctbri.wxcc.widget;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public class LocationSpan<T> extends ClickableSpan {

	private static final int BG_COLOR = Color.parseColor("#FFEEEEEE");
	private boolean pressed;
	
	public boolean isPressed() {
		return pressed;
	}

	public void setPressed(boolean pressed) {
		this.pressed = pressed;
	}

	private LocationClickListener<T> clickListener;
	
	public LocationClickListener<T> getClickListener() {
		return clickListener;
	}

	public void setClickListener(LocationClickListener<T> clickListener) {
		this.clickListener = clickListener;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public LocationSpan(T t) {
		this.data = t;
	}
	public LocationSpan(T t, LocationClickListener<T> listener) {
		this.data = t;
		this.clickListener = listener;
	}
	
	private T data;
	
	@Override
	public void onClick(View widget) {
		if(this.clickListener!=null)
			this.clickListener.onClick(widget, data);
	}
	@Override
	public void updateDrawState(TextPaint ds) {
//		ds.setColor(ds.linkColor);
//		System.out.println("更新 点击颜色");
//		ColorStateList colors = getResources().getColorStateList(R.color.travel_spot_color);
//		colors.getColorForState(tv_address.getDrawableState()	, 0);
//		super.updateDrawState(ds);
		//如果按下了当前组件
		if(pressed){
			ds.bgColor = BG_COLOR;
		}
		
	}
	public static interface LocationClickListener<T>{
		void onClick(View view, T data);
	} 
}
