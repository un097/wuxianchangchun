package com.ctbri.wxcc.media;

import android.graphics.PixelFormat;
import android.os.Bundle;

import com.ctbri.wxcc.community.BaseActivity;

public class ShortVideoActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		super.onCreate(arg0);
		getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new ShortVideoListFragment()).commit();
	}
}
