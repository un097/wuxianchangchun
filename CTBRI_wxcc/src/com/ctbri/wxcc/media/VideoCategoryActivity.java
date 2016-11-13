package com.ctbri.wxcc.media;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.ctbri.wxcc.Constants;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseActivity;

public class VideoCategoryActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		setContentView(R.layout.activity_media_category);

		((TextView) findViewById(R.id.action_bar_title)).setText(R.string.title_all_video_category);
		findViewById(R.id.action_bar_left_btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		init();
	}

	private void init() {
		getSupportFragmentManager().beginTransaction().replace(R.id.frame_category, VideoNavigatorGridFragment.newInstance(true, Constants.METHOD_VIDEO_CATEGORY)).commit();
	}
}
