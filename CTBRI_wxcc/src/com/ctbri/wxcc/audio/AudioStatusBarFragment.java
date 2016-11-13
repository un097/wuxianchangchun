package com.ctbri.wxcc.audio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseFragment;

public class AudioStatusBarFragment extends BaseFragment {
	private TextView mTv_program;
	private ImageButton mIbtn_pause;
	private boolean play = false;
	private String mId, mUrl, mTitle;
	private SharedPreferences preference;
	private Editor editor;
	View v;
	@Override
	protected String getAnalyticsTitle() {
		return null;
	}

	@Override
	protected boolean isEnabledAnalytics() {
		return false;
	}

	@Override
	public void onStart() {
		super.onStart();
		registerReceiver();
		
		play = preference.getBoolean("playStatu", false);
		// 同步播放按钮
		updateButtonState();
	}

	@Override
	public void onStop() {
		super.onStop();
		activity.unregisterReceiver(mPlayingListener);
	}

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(AudioLiveFragment.ACTION_AUDIO_PLAYING_CMD);
		filter.addAction(AudioLiveFragment.ACTION_AUDIO_STOP_CMD);
		filter.addAction(AudioLiveFragment.ACTION_AUDIO_INFO_CMD);
		activity.registerReceiver(mPlayingListener, filter);
	}

	/**
	 * 播放控件 Receiver ,用于控制播放器控件UI
	 */
	private BroadcastReceiver mPlayingListener = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// 如果 action 为正在播放中
			if (AudioLiveFragment.ACTION_AUDIO_PLAYING_CMD.equals(action)) {
				play = true;
				mTitle = intent.getStringExtra("title");
				updateProgramName();
			} else if (AudioLiveFragment.ACTION_AUDIO_STOP_CMD.equals(action)) {
				play = false;
			} else if (AudioLiveFragment.ACTION_AUDIO_INFO_CMD.equals(action)) {
				play = false;
				mTitle = intent.getStringExtra("title");
				mUrl = intent.getStringExtra("url");
				mId = intent.getStringExtra("id");
				updateProgramName();
			}
			updateButtonState();
		}
	};

	private void updateProgramName() {
		if (mTv_program != null) {
			mTv_program.setText(mTitle);
		}
	}
	
	/**
	 * 更新按钮状态
	 */
	private void updateButtonState() {
		if (mIbtn_pause == null)
			return;
		
//		mIbtn_pause.setImageResource(play ? R.drawable.audio_status_bar_pause_selector : R.drawable.audio_status_bar_play_selector);
		mIbtn_pause.setImageResource(play ? R.drawable.audio_icon_pause : R.drawable.audio_play);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.audio_playing_status_bar, container, false);
		v.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN)
					v.getParent().requestDisallowInterceptTouchEvent(true);

				return true;
			}
		});
		mTv_program = (TextView) v.findViewById(R.id.tv_program_name);
		mIbtn_pause = (ImageButton) v.findViewById(R.id.btn_pause);
		mIbtn_pause.setOnClickListener(new PauseListener());
		
		if (preference == null) {
			preference = activity.getSharedPreferences("audioPlayStatu", Context.MODE_PRIVATE);
			editor = preference.edit();
		}
		
		return v;
	}

	/**
	 * 得到这个控件的高度
	 * @return
	 */
	public int getAudioStatusHeight(){
		return v.getHeight();
	}
	
	private class PauseListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			if (play) {
				intent.setAction(AudioLiveFragment.ACTION_AUDIO_STOP);
			} else {
				intent.setAction(AudioLiveFragment.ACTION_AUDIO_REPLAY);
			}
			activity.sendBroadcast(intent);
			// mIbtn_pause.setImageResource(play ?
			// R.drawable.audio_status_bar_play_selector :
			// R.drawable.audio_status_bar_pause_selector);
		}
	}
}
