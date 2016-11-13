package com.ctbri.wxcc.audio;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctbri.wxcc.Constants;
import com.ctbri.wxcc.audio.CircleFragment.ViewBinder;
import com.ctbri.wxcc.entity.AudioRecomBean;
import com.ctbri.wxcc.entity.AudioRecomBean.AudioChannel;
import com.google.gson.Gson;

public class AudioRecomFragment extends CircleFragment<AudioRecomBean, AudioRecomBean.AudioChannel> implements ViewBinder<AudioRecomBean, AudioRecomBean.AudioChannel> {

	public static AudioRecomFragment newInstance() {
		Bundle args = new Bundle();
		args.putBoolean(KEY_ISSHOWHEAD, false);
		AudioRecomFragment fragment = new AudioRecomFragment();
		fragment.setArguments(args);
		return fragment;
	}
	 
	@Override
	public void onAttach(Activity activity_) {
		super.onAttach(activity_);
		setBinder(this); 
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getData();
	}

	private void getData() {
		request(Constants.METHOD_AUDIO_LIVE_RECOM, new RequestCallback() {

			@Override
			public void requestSucc(String json) {
				Gson gson = new Gson();
				AudioRecomBean bean = gson.fromJson(json, AudioRecomBean.class);
				update(bean, bean.getData().getChannels(), 0);
			}

			@Override
			public void requestFailed(int errorCode) {

			}
		});
	}

	@Override
	public void bindData(ImageView imgview, TextView tvTitle, AudioChannel e) {
		tvTitle.setText(e.getChannel_name());
		mImageLoader.displayImage(e.getPic().trim(), imgview, mDio);
	}

	@Override
	public void init(TextView tvTitle, TextView more, AudioRecomBean t) {

	}
}
