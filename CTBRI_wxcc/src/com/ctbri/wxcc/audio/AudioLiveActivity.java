package com.ctbri.wxcc.audio;

import android.os.Bundle;

import com.ctbri.wxcc.community.BaseActivity;

public class AudioLiveActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		String channel_id = getString(AudioLiveFragment.KEY_CHANNEL_ID);
		getSupportFragmentManager().beginTransaction().replace(android.R.id.content, AudioLiveFragment.newInstance(channel_id)).commit();
	}
}
