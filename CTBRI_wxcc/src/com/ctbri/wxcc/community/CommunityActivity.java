package com.ctbri.wxcc.community;

import android.os.Bundle;

import com.ctbri.wxcc.R;

public class CommunityActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_community);
	}
	
	protected void onLaunch() {
		//统计 爆料社区点击量
		postClickEvent("E_C_pageName_communityClick");
	}
}
