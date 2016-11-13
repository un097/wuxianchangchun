package com.ctbri.wxcc.audio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.ctbri.wxcc.Constants;
import com.ctbri.wxcc.entity.MediaCategoryBean.MediaCategory;
import com.ctbri.wxcc.media.VideoNavigatorGridFragment;

public class AudioCategoryFragment extends VideoNavigatorGridFragment {
	public AudioCategoryFragment() {
		Bundle args = new Bundle();
		args.putBoolean(KEY_HAS_MORE, false);
		args.putString(KEY_URL, Constants.METHOD_AUDIO_CATEGORY);
		setArguments(args);
	}

	public AudioCategoryFragment(boolean third) {
		super();
	}

	public static AudioCategoryFragment newInstance(boolean show_more) {
		Bundle args = new Bundle();
		args.putBoolean(KEY_HAS_MORE, show_more);
		args.putString(KEY_URL, Constants.METHOD_AUDIO_CATEGORY);
		AudioCategoryFragment videoGrid = new AudioCategoryFragment(false);
		videoGrid.setArguments(args);
		return videoGrid;
	}

	@Override
	protected void onItemClick(AdapterView<?> parent, View view, int position, long id, MediaCategory entity) {
		if ("more".equals(entity.getCategory_id())) {
			Intent toAllCategory = new Intent(activity, AudioCategoryActivity.class);
			startActivity(toAllCategory);
		} else {
			Intent toPlayList = new Intent(activity, AudioListActivity.class);
			toPlayList.putExtra("group_id", entity.getCategory_id());
			toPlayList.putExtra("title", entity.getCategory_name());

			startActivity(toPlayList);
		}
	}
}
