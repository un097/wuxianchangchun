package com.ctbri.wxcc.audio;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnCompletionListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnErrorListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnPreparedListener;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import android.app.Activity;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.net.Uri;
import android.os.Bundle;

import com.ctbri.wxcc.community.BaseFragment;

public class DaemonPlayerFragment extends BaseFragment {

	public static final String KEY_CHANNEL_ID = "channel_id";
	// 要播放的 url
	private Uri mUri;

	// 播放时 暂时离开当界面
	private static final int STATE_LEAVE = 1001;
	private int mCurrentState;

	private String mTitle, mId;
	private IjkMediaPlayer mMediaPlayer;
	/**
	 * 异步处理播放事件
	 */
	private static final String TAG = AudioStatusBarFragment.class.getName();

	private OnAudioFocusChangeListener audioFocusListener;
	private DaemonPlayerListener mListener;

	@Override
	public void onAttach(Activity activity_) {
		super.onAttach(activity_);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public boolean isPlaying() {
		if (mMediaPlayer != null && mMediaPlayer.isPlaying())
			return true;
		return false;
	}

	public DaemonPlayerListener getListener() {
		return mListener;
	}

	public void setListener(DaemonPlayerListener mListener) {
		this.mListener = mListener;
	}

	@Override
	protected String getAnalyticsTitle() {
		return null;
	}

	@Override
	protected boolean isEnabledAnalytics() {
		return false;
	}

	public void stop() {
		if (isPlaying()) {
			fireStop();
		}
		release(true);

	}

	@Override
	public void onPause() {
		super.onPause();
		if (isPlaying()) {
			//屏蔽继续播放功能
			// mCurrentState = STATE_LEAVE;
			mCurrentState = -1;
			stop();
		} else
			mCurrentState = -1;
		
	}
	
	private void fireStop() {
		if (mListener != null)
			mListener.onStop();
	}

	private void firePlaying() {
		if (mListener != null)
			mListener.onPlaying(mUri.toString(), mTitle, mId);
	}

	private void fireError() {
		if (mListener != null)
			mListener.onError();
	}

	private void release(boolean cleartargetstate) {
		if (mMediaPlayer != null) {
			mMediaPlayer.reset();
			mMediaPlayer.release();
			mMediaPlayer = null;
//			activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			// mCurrentState = STATE_IDLE;
			// if (cleartargetstate)
			// mTargetState = STATE_IDLE;
		}
	}

	private void initWidget() {

		mMediaPlayer = new IjkMediaPlayer();

		mMediaPlayer.setOnPreparedListener(mIjkPreparedLis);

		mMediaPlayer.setOnCompletionListener(mIjkCompletionLis);

		mMediaPlayer.setOnErrorListener(mIjkErrorListener);
	}

	public void openUrl(String url, String title, String id) {
		if (url == null)
			return;
		mUri = Uri.parse(url);
		mTitle = title;
		mId = id;

		startPlay();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mCurrentState == STATE_LEAVE && mUri != null)
			startPlay();
	}

	private void startPlay() {
		if (mUri == null)
			return;

		release(true);
		try {
			initWidget();
			mMediaPlayer.setDataSource(mUri.toString());
			mMediaPlayer.prepareAsync();
//			activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private OnErrorListener mIjkErrorListener = new OnErrorListener() {
		@Override
		public boolean onError(IMediaPlayer mp, int what, int extra) {
			fireError();
			return false;
		}
	};
	/**
	 * 播放完成listener
	 */
	private OnCompletionListener mIjkCompletionLis = new OnCompletionListener() {

		@Override
		public void onCompletion(IMediaPlayer mp) {
			fireStop();
		}
	};

	private OnPreparedListener mIjkPreparedLis = new OnPreparedListener() {
		@Override
		public void onPrepared(IMediaPlayer mp) {
			mp.start();
			firePlaying();
		}
	};

	// @TargetApi(Build.VERSION_CODES.FROYO)
	// private void changeAudioFocus(boolean gain) {
	// if (!LibVlcUtil.isFroyoOrLater()) // NOP if not supported
	// return;
	//
	// if (audioFocusListener == null) {
	// audioFocusListener = new OnAudioFocusChangeListener() {
	// @Override
	// public void onAudioFocusChange(int focusChange) {
	// LibVLC libVLC = LibVLC.getExistingInstance();
	// switch (focusChange) {
	// case AudioManager.AUDIOFOCUS_LOSS:
	// if (libVLC.isPlaying())
	// libVLC.pause();
	// break;
	// case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
	// case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
	// /*
	// * Lower the volume to 36% to "duck" when an alert or
	// * something needs to be played.
	// */
	// libVLC.setVolume(36);
	// break;
	// case AudioManager.AUDIOFOCUS_GAIN:
	// case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
	// case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
	// libVLC.setVolume(100);
	// break;
	// }
	// }
	// };
	// }
	//
	// AudioManager am = (AudioManager)
	// activity.getSystemService(Context.AUDIO_SERVICE);
	// if (gain)
	// am.requestAudioFocus(audioFocusListener, AudioManager.STREAM_MUSIC,
	// AudioManager.AUDIOFOCUS_GAIN);
	// else
	// am.abandonAudioFocus(audioFocusListener);
	//
	// }
	//
	//

	public static interface DaemonPlayerListener {
		void onStop();

		void onPlaying(String url, String title, String id);

		void onError();
	}

}
