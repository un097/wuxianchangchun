package com.ctbri.wxcc.audio;

import android.os.Bundle;

import com.ctbri.wxcc.community.BaseActivity;

public class AudioSearchActivity extends BaseActivity {

	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		getSupportFragmentManager().beginTransaction().replace(android.R.id.content , new AudioSearchFragment()).commit();
	}
}
