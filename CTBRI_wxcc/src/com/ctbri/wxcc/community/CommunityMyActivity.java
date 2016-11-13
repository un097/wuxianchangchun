package com.ctbri.wxcc.community;

import com.ctbri.wxcc.R;

import android.os.Bundle;

public class CommunityMyActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_my_community);
	}
	@Override
	protected boolean checkLogin() {
		return true;
	}

}
