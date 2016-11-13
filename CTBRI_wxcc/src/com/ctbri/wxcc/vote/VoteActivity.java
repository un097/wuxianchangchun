package com.ctbri.wxcc.vote;

import android.os.Bundle;

import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseActivity;

public class VoteActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		setContentView(R.layout.activity_vote);
	}
	@Override
	protected void onLaunch() {
		//统计民意调查模块的点击量
		postClickEvent("E_C_pageName_voteClick");
	}
}
