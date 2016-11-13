package com.ctbri.wxcc.audio;

import java.util.List;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.ctbri.wxcc.Constants;
import com.ctbri.wxcc.entity.MediaSearchBean;
import com.ctbri.wxcc.entity.MediaSearchBean.VideoSearchEntity;
import com.ctbri.wxcc.media.MediaPlayerActivity;
import com.ctbri.wxcc.media.MediaSearchFragment;

public class AudioSearchFragment extends MediaSearchFragment {

	@Override
	protected String getListUrl() {
		return Constants.METHOD_AUDIO_SEARCH_VOD;
	}

	@Override
	protected List<VideoSearchEntity> getEntitys(MediaSearchBean bean) {
		if (bean != null && bean.getData() != null)
			return bean.getData().getAudios();
		return null;
	}

	@Override
	protected void onItemClick(AdapterView<?> parent, View view, int position, long id, VideoSearchEntity entity) {
		Intent toDetail = new Intent(activity, MediaPlayerActivity.class);
		toDetail.putExtra("type_id", MediaPlayerActivity.TYPE_AUDIO_VOD);
		toDetail.putExtra("vod_id", entity.getId());
		startActivity(toDetail);
	}
}
