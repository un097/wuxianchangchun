package com.ctbri.wxcc.media;

import android.os.Bundle;

import com.ctbri.wxcc.community.BaseActivity;

public class MyFavoriteActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		init();
	}

	private void init() {
		getSupportFragmentManager().beginTransaction()
				.replace(android.R.id.content,new MyFavoriteVideoFragment())
				.commit();
	}
}
