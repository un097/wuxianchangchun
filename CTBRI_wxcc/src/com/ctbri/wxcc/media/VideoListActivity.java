package com.ctbri.wxcc.media;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;

import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseActivity;

public class VideoListActivity extends BaseActivity {

	private String group_id, type_id, title;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		setContentView(R.layout.activity_media_list);

		initFragment();

	}

	private void initFragment() {
		group_id = getString("group_id");
		type_id = getString("type_id");
		title = getString("title");

		Fragment listFragment = null;
		FragmentManager fm = getSupportFragmentManager();

		if (VodVideoListFragmet.TYPE_BROADCAST.equals(type_id)) {
			listFragment = new LiveVideoListFragmet();
		} else {
			listFragment = fm.findFragmentByTag("listFragment");
			if (listFragment == null)
				listFragment = VodVideoListFragmet.newInstance(group_id, type_id, title);
		}
		fm.beginTransaction().replace(R.id.frame_video_list, listFragment, "listFragment").commit();
	}
}
