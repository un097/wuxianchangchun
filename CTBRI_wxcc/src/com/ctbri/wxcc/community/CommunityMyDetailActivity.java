package com.ctbri.wxcc.community;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.ctbri.wxcc.R;

public class CommunityMyDetailActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		setContentView(R.layout.community_my_community_detail);
		findViewById(R.id.action_bar_left_btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		((TextView)findViewById(R.id.action_bar_title)).setText(R.string.title_my_community);
	}
}
