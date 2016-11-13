package com.wookii.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wookii.wookiiwidget.R;

public class IconButton extends LinearLayout {

	private TextView text;
	private ImageView icon;

	public IconButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public IconButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		// TODO Auto-generated method stub
		TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attrs, R.styleable.IconButton);
		icon = new ImageView(context);
		//text = (TextView)inflate(context, R.layout.icon_button_textview, null);
		text = new TextView(context);
		text.setTextSize(TypedValue.COMPLEX_UNIT_PX,(int)obtainStyledAttributes.getDimension(R.styleable.IconButton_textSize, 0));
		text.setText(obtainStyledAttributes.getText(R.styleable.IconButton_text));
		text.setTextColor(obtainStyledAttributes.getColor(R.styleable.IconButton_textColor, -16777216));
		text.setPadding((int)obtainStyledAttributes.getDimensionPixelSize(R.styleable.IconButton_between, 0), 0, 0, 0);
		icon.setImageDrawable(obtainStyledAttributes.getDrawable(R.styleable.IconButton_button_icon));
		addView(icon);
		addView(text);
	}
	public float getRawSize(int unit, float value) { 
        Resources res = this.getResources(); 
        return TypedValue.applyDimension(unit, value, res.getDisplayMetrics()); 
    }
	public void setText(String str){
		text.setText(str);
	}
	
	public void setIcon(int res) {
		icon.setImageResource(res);
	}
}
