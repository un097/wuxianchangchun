package com.ctbri.wxcc.audio;

import android.os.Bundle;

import com.ctbri.wxcc.community.BaseActivity;

public class AudioListActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		String group_id = getString("group_id");
		String title = getString("title");

		getSupportFragmentManager().beginTransaction().replace(android.R.id.content, AudioListFragmet.newInstance(group_id, title)).commit();

	}
	
	@Override
	protected void onLaunch() {
		//统计 音频播放列表的点击量
		postClickEvent("E_C_pageName_aodListClick");
	}
}
