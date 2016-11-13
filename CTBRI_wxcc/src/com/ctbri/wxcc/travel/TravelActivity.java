package com.ctbri.wxcc.travel;

import android.os.Bundle;

import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseActivity;

public class TravelActivity extends BaseActivity {
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_travel);
	}
	@Override
	protected void onLaunch() {
		//统计旅游资讯点击量
		postClickEvent("E_C_pageName_travelClick");
	}
	@Override
	protected boolean checkLogin() {
		return false;
	}
}
