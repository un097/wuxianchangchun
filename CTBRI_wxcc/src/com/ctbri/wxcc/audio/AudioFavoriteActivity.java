package com.ctbri.wxcc.audio;

import android.os.Bundle;

import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseActivity;

public class AudioFavoriteActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		init();
	}

	private void init() {
		getSupportFragmentManager().beginTransaction()
				.replace(android.R.id.content,new AudioFavoriteFragment())
				.commit();
	}
}
