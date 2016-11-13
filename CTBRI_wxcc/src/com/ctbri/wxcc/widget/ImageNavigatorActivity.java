package com.ctbri.wxcc.widget;

import android.os.Bundle;

import com.ctbri.wxcc.community.BaseActivity;

public class ImageNavigatorActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		getSupportFragmentManager().beginTransaction().replace(android.R.id.content, ImageNavigator.newInstance(getPics()), "images_navigator").commit();
	}
	
	private String[] getPics(){
		return getIntent()==null?null:getIntent().getStringArrayExtra (ImageNavigator.KEY_PICS);
	}
}
