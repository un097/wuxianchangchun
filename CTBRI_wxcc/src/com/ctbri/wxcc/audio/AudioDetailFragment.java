package com.ctbri.wxcc.audio;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseFragment;

public class AudioDetailFragment extends BaseFragment {

	private ListView lv_audio_program;

	@Override
	protected String getAnalyticsTitle() {
		return null;
	}

	@Override
	protected boolean isEnabledAnalytics() {
		return false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.audio_live_layout, container, false);
		lv_audio_program = (ListView) v.findViewById(R.id.lv_video_info);
		return v;
	}

}
