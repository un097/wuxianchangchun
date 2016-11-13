package com.ctbri.wxcc.media;

import android.os.Bundle;

import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseActivity;

public class MediaPlayListActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		setContentView(R.layout.activity_media_play_list);
	}
	
	protected void onLaunch() {
		//统计 “播放列表”点击量
		postClickEvent("E_C_pageName_mediaListClick");
	}
}
