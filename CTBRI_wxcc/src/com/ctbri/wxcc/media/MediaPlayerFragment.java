package com.ctbri.wxcc.media;

import static com.ctbri.wxcc.audio.AudioLiveFragment.ACTION_AUDIO_ALLSTOP_PLAY;
import static com.ctbri.wxcc.audio.AudioLiveFragment.ACTION_AUDIO_VOD_BACKGROUND_PLAY;
import static com.ctbri.wxcc.audio.AudioLiveFragment.ACTION_AUDIO_VOD_BUFFER_STATU_CONTROL;
import static com.ctbri.wxcc.audio.AudioLiveFragment.ACTION_AUDIO_VOD_PAUSEPLAY_CONTROL;
import static com.ctbri.wxcc.audio.AudioLiveFragment.ACTION_AUDIO_VOD_PLAY_CONTROL;
import static com.ctbri.wxcc.audio.AudioLiveFragment.ACTION_AUDIO_VOD_SEEKTO_BACKGROUND_PLAY;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnBufferingUpdateListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnCompletionListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnErrorListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnInfoListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnPreparedListener;
import tv.danmaku.ijk.media.widget.VideoView;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.ctbri.comm.util.Strings;
import com.ctbri.comm.util._Utils;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseActivity;
import com.ctbri.wxcc.community.BaseFragment;
import com.ctbri.wxcc.community.Constants_Community;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.utils.LogUtil;

public class MediaPlayerFragment extends BaseFragment {
	public static final String TAG = "MediaPlayerActivity";
	private static final int UPDATE_TIME = 1001;
	private static final int FADE_HIDE = 1002;
	private static final int CHANGE_UI = 1003;

	private static final int mDefaultDelay = 5000;
	protected static final String TGA = "MediaPlayerFragment";

	private ImageView iv_video_icon;
	private VideoView mVideoView;
	private View mView, mTopBar, mBottomBar;
	private int mPlayerHeight;
	private String mTitle, mType, mVideo_url;
	/**
	 * 当前的屏幕方向
	 */
	private int mScreentOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

	private boolean mDragging;
	private boolean mIsShowing;
	private boolean mBarAlwaysVisable;
	private boolean mIsBackPlay;

	private ImageButton mBtnFullscreen, mBtnShare, mBtnFavorite, mBtnPause;
	private int mDuration;
	private String video_id;
	private View mProgressView;
	private TextView mTime;
	private SeekBar mSeekbar;
	private PlayerHandler mHandler = new PlayerHandler(this);

	public static MediaPlayerFragment newInstance(String group_id) {
		MediaPlayerFragment fragment = new MediaPlayerFragment();
		Bundle args = new Bundle();
		args.putString("group_id", group_id);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		registerReceiver();
	}

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_AUDIO_VOD_PLAY_CONTROL);
		filter.addAction(ACTION_AUDIO_VOD_PAUSEPLAY_CONTROL);
		filter.addAction(ACTION_AUDIO_VOD_BUFFER_STATU_CONTROL);
		activity.registerReceiver(mControlReceiver, filter);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		activity.unregisterReceiver(mControlReceiver);
	}
	
	/**
	 * 播放控制
	 */
	private BroadcastReceiver mControlReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String act = intent.getAction();
			if(ACTION_AUDIO_VOD_PLAY_CONTROL.equals(act)) {
				int progress = (int) intent.getLongExtra("progress", -1L);
				int duration = intent.getIntExtra("duration", -1);
				int isPlay = intent.getIntExtra("isPlay", 0);
				Message msg = Message.obtain();
				Integer[] intArr = {progress,duration,isPlay};
				msg.obj = intArr;
				msg.what = CHANGE_UI;
				mHandler.sendMessage(msg);
			}else if(ACTION_AUDIO_VOD_PAUSEPLAY_CONTROL.equals(act)) {
				int isPlay = intent.getIntExtra("startPuase", 0);
				togglePauseButton(isPlay);
			}else if(ACTION_AUDIO_VOD_BUFFER_STATU_CONTROL.equals(act)) {
				boolean bufferStatu = intent.getBooleanExtra("bufferStatu", false);
				BufferButton(bufferStatu);
			}
		};
	};
	
	/**
	 * 
	 * @param title
	 *            音视频 标题
	 * @param video_url
	 *            音视频地址
	 * @param video_id
	 *            音视频id
	 * @param type
	 *            直播或点播的标示 {@link VodVideoListFragmet#TYPE_BROADCAST}
	 *            {@link VodVideoListFragmet#TYPE_VOD}
	 * @param picUrl
	 *            为点播时，显示的 image url
	 */
	public void update(String title, String video_url, String video_id, String type, String picUrl) {
		
		this.video_id = video_id;
		// tv_title.setText(title);
		if (video_url == null) {
			return;
		}

		mTitle = title;
		mType = type;
		mVideo_url = video_url;
		
		//如果为音频则置于后台播放
		if(MediaPlayerActivity.TYPE_AUDIO_VOD.equals(type)) {
			mIsBackPlay = true;
			Intent intent = new Intent(ACTION_AUDIO_VOD_BACKGROUND_PLAY);
			intent.putExtra("url", video_url);
			intent.putExtra("title",mTitle);
			activity.sendBroadcast(intent);
		}else {
			//发送暂停后台播放
			Intent intent = new Intent(ACTION_AUDIO_ALLSTOP_PLAY);
			activity.sendBroadcast(intent);
			
			mIsBackPlay = false;
			mVideoView.setVideoPath(video_url);
			mVideoView.start();
		}
		
		// 直播将禁用拖动
		if (VodVideoListFragmet.TYPE_BROADCAST.equals(type)) {
			if (mSeekbar != null) {
				mSeekbar.setEnabled(false);
			}
		} else {
			mBtnFavorite.setVisibility(View.VISIBLE);
			// 如果是视频点播
			if (VodVideoListFragmet.TYPE_VOD.equals(type))
				MediaUtils.isFavorite(video_id, MediaUtils.FLAG_SINGLE_VIDEO, mBtnFavorite, (BaseActivity) activity);
			// 如果是音频点播
			else {
				// 隐藏全屏控件
				mBtnFullscreen.setVisibility(View.GONE);
				// 显示音频点播默认的图片
				if (!TextUtils.isEmpty(picUrl)) {
					ImageLoader.getInstance().displayImage(picUrl, iv_video_icon);
					iv_video_icon.setVisibility(View.VISIBLE);
				}
				mBarAlwaysVisable = true;
				MediaUtils.isAudioFavorite(video_id, MediaUtils.FLAG_SINGLE_VIDEO, mBtnFavorite, (BaseActivity) activity);
			}
		}
	}

	/**
	 * 
	 */
	private void showBar() {
		showBar(mDefaultDelay);
	}

	private void showBar(int delay) {
		mTopBar.setVisibility(View.VISIBLE);
		mBottomBar.setVisibility(View.VISIBLE);

		mHandler.removeMessages(FADE_HIDE);
		mHandler.sendEmptyMessage(UPDATE_TIME);

		if (delay > 0 && !mBarAlwaysVisable) {
			mHandler.sendEmptyMessageDelayed(FADE_HIDE, delay);
		}
		mIsShowing = true;
		togglePauseButton();
	}

	private void hideBar() {
		mTopBar.setVisibility(View.GONE);
		mBottomBar.setVisibility(View.GONE);

		mHandler.removeMessages(FADE_HIDE);
		mHandler.removeMessages(UPDATE_TIME);

		mIsShowing = false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		mView = inflater.inflate(R.layout.activity_media_video_player, container, false);
		mVideoView = (VideoView) mView.findViewById(R.id.sv_video);

		mProgressView = mView.findViewById(R.id.progress_loading);
		mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_FIT);
		mVideoView.setMediaBufferingIndicator(mProgressView);
		mVideoView.setOnPreparedListener(mVideoListener);
		mVideoView.setOnBufferingUpdateListener(mVideoListener);
		mVideoView.setOnCompletionListener(mVideoListener);
		mVideoView.setOnErrorListener(mVideoListener);
		// mVideoView.setOnInfoListener(mVideoListener);
		mVideoView.setOnTouchListener(mVideoListener);
		mVideoView.setOnClickListener(mVideoListener);

		// tv_title = (TextView) findViewById(R.id.tv_title);
		mView.findViewById(R.id.action_bar_left_btn).setOnClickListener(mBackClickListener);

		mBtnFullscreen = (ImageButton) mView.findViewById(R.id.ibtn_fullscreen);
		mBtnFullscreen.setOnClickListener(mFullScreenListener);

		mBtnPause = (ImageButton) mView.findViewById(R.id.mediacontroller_play_pause);
		mBtnPause.setOnClickListener(mPauseListener);

		mTime = (TextView) mView.findViewById(R.id.mediacontroller_time_total);

		mSeekbar = (SeekBar) mView.findViewById(R.id.mediacontroller_seekbar);
		mSeekbar.setOnSeekBarChangeListener(mSeekChangeListener);
		mSeekbar.setMax(1000);

		mTopBar = mView.findViewById(R.id.ll_video_topbar);
		mBottomBar = mView.findViewById(R.id.mediacontroller);

		mBtnFavorite = (ImageButton) mView.findViewById(R.id.ibtn_favorite);
		mBtnShare = (ImageButton) mView.findViewById(R.id.ibtn_share);
		mBtnShare.setOnClickListener(mShareListener);

		iv_video_icon = (ImageView) mView.findViewById(R.id.iv_video_icon);

		return mView;
	}

	private static class PlayerHandler extends Handler {
		private MediaPlayerFragment mPlayer;

		public PlayerHandler(MediaPlayerFragment player) {
			this.mPlayer = player;
		}

		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			
			switch (what) {
			case UPDATE_TIME:
				mPlayer.updateTime();
				break;
			case FADE_HIDE:
				mPlayer.hideBar();
				break;
			case CHANGE_UI:
				//改变UI界面
				Integer[] arr = (Integer[]) msg.obj;
				int progress = arr[0];
				int duration = arr[1];
				int isPlay = arr[2];
				mPlayer.changedUI(progress,duration,isPlay);
				
				break;
			}
		}
	}
	
	private void changedUI(int progress,int duration,int isPlay) {
		if(mSeekbar != null && mTime != null) {
			mSeekbar.setProgress(progress);
			togglePauseButton(isPlay);
			mTime.setText(Strings.millisToString(duration));
		}
	}

	private void updateTime() {
		if (mVideoView == null || !mVideoView.isPlaying() || mDragging)
			return;
		int duration = mVideoView.getDuration();
		mDuration = duration;
		if (duration > 0) {
			int pos = mVideoView.getCurrentPosition();
			long progress = 1000L * pos / duration;
			mSeekbar.setProgress((int) progress);
			mHandler.sendEmptyMessageDelayed(UPDATE_TIME, 1000 - (pos % 1000));
			togglePauseButton();
		}
		mTime.setText(Strings.millisToString(duration));
	}

	private OnSeekBarChangeListener mSeekChangeListener = new OnSeekBarChangeListener() {
		public void onStopTrackingTouch(SeekBar seekBar) {
			if(mIsBackPlay) {
				Intent intent = new Intent(ACTION_AUDIO_VOD_SEEKTO_BACKGROUND_PLAY);
				intent.putExtra("progress",seekBar.getProgress());
				activity.sendBroadcast(intent);
				return;
			}
			
			if (mVideoView.canSeekForward() && mVideoView.canSeekBackward())
				mVideoView.seekTo((mDuration * seekBar.getProgress()) / 1000);
			mDragging = false;
			mHandler.sendEmptyMessageDelayed(UPDATE_TIME, 1000);
		}

		public void onStartTrackingTouch(SeekBar seekBar) {
			if(mIsBackPlay) {
				return;
			}
			mDragging = true;
			mHandler.removeMessages(UPDATE_TIME);
			mHandler.removeMessages(FADE_HIDE);
		}

		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			if (!fromUser)
				return;
		}
	};

	/**
	 * VideoView 播放相关的事件
	 */
	private VideoListener mVideoListener = new VideoListener();

	/**
	 * Videoview 相关事件
	 * 
	 * @author yanyadi
	 * 
	 */
	class VideoListener implements OnPreparedListener, OnCompletionListener, OnClickListener, OnInfoListener, OnBufferingUpdateListener, OnTouchListener, OnErrorListener {

		@Override
		public void onPrepared(IMediaPlayer mp) {
			togglePauseButton();
			updateTime();
			showBar();
			if (mProgressView != null)
				mProgressView.setVisibility(View.GONE);
			// if (mVideoView.canSeekBackward() && mVideoView.canSeekForward())
			// {
			// mSeekbar.setEnabled(true);
			// } else {
			// mSeekbar.setProgress(0);
			// mSeekbar.setEnabled(false);
			// }
		}

		@Override
		public boolean onInfo(IMediaPlayer mp, int what, int extra) {
			return false;
		}

		@Override
		public void onCompletion(IMediaPlayer mp) {
			//播放结束
			mHandler.removeMessages(UPDATE_TIME);
			
			showBar(0);
			
			//当视频播放完毕之后通知播放下一集 by 张政 2015/7/06
			MediaInfoFragment player = (MediaInfoFragment) getFragmentManager().findFragmentByTag(MediaLivePlayerFragment.KEY_INFO_FRAGMENT);
			if(player != null) {
				player.playNextVedio();
			}
		}
		
		@Override
		public void onBufferingUpdate(IMediaPlayer mp, int percent) {

		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int eventCode = event.getAction();
			if (eventCode == MotionEvent.ACTION_DOWN) {
				if (mIsShowing) {
					if (!mBarAlwaysVisable)
						hideBar();
				} else {
					showBar();
				}
			}
			switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
				v.performClick();
				break;
			}
			return true;
		}

		@Override
		public void onClick(View v) {
			if(!mVideoView.isPlaying()) {
				mVideoView.start();
				togglePauseButton(false);
			}
//			doPauseState();
			
			if(mIsBackPlay) {
				//发送暂停&播放音乐的广播
				sendStopStartAudioVod();
			}
		}

		@Override
		public boolean onError(IMediaPlayer mp, int what, int extra) {
			toast(R.string.msg_video_play_error);
			return true;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mHandler.removeMessages(UPDATE_TIME);
	}

	/**
	 * 分享按钮点击事件
	 */
	private OnClickListener mShareListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mTitle == null)
				return;
			String content;
			// 视频
			if (VodVideoListFragmet.TYPE_BROADCAST.equals(mType) || VodVideoListFragmet.TYPE_VOD.equals(mType)) {
				content = getString(R.string.share_content_video, mTitle);
			} else {
				content = getString(R.string.share_content_audio, mTitle);
			}
			_Utils.shareAndCheckLogin(activity, mTitle, Constants_Community.APK_DOWNLOAD_URL, content, _Utils.getDefaultAppIcon(activity));
		}
	};
	
	/**
	 * 返回事件
	 */
	private OnClickListener mBackClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(mScreentOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
				activity.finish();
			else 
				exitFullScreen();
		}
	};
	
	/**
	 * 播放器 全屏事件
	 * 
	 * @author yanyadi
	 * 
	 */
	private OnClickListener mFullScreenListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Fragment parent = getParentFragment();

			if (mScreentOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {//开启全屏
				mScreentOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
				activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				if (mVideoView != null)
					mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE);
				if (parent instanceof MediaLivePlayerFragment) {
					mPlayerHeight = ((MediaLivePlayerFragment) parent).fullscreen();
				}

			} else {//退出全屏
				exitFullScreen();
			}
		}
	};
	
	public void exitFullScreen(){
		Fragment parent = getParentFragment();
		mScreentOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		if (parent instanceof MediaLivePlayerFragment) {
			((MediaLivePlayerFragment) parent).cancelFullscreen();

		}
		if (mVideoView != null) {
			mVideoView.getLayoutParams().height = LayoutParams.MATCH_PARENT;
			mVideoView.setLayoutParams(mVideoView.getLayoutParams());
		}

		if (mVideoView != null) {
			mVideoView.postDelayed(new Runnable() {

				@Override
				public void run() {
					mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_FIT);
				}
			}, 500);

		}
	}
	
	/**
	 * 暂停事件函数
	 */
	private OnClickListener mPauseListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			doPauseState();
			showBar();
		}
	};

	private void doPauseState() {
		if(mIsBackPlay) {
			//发送暂停&播放音乐的广播
			sendStopStartAudioVod();
			return;
		}
		
		boolean isPlay = mVideoView.isPlaying();
		if (isPlay) {
			mVideoView.pause();
		} else {
			mVideoView.start();
		}
		togglePauseButton(!isPlay);
		// togglePauseButton();
	}

	/**
	 * 发送暂停&播放音乐的广播
	 */
	private void sendStopStartAudioVod() {
		Intent intent = new Intent(ACTION_AUDIO_VOD_BACKGROUND_PLAY);
		intent.putExtra("statu", true);
		intent.putExtra("url", mVideo_url);
		intent.putExtra("title",mTitle);
		activity.sendBroadcast(intent);
	}

	/**
	 * 播放或者暂停按钮的图标
	 */
	private void togglePauseButton() {
		if(mIsBackPlay) {
			return;
		}
		if (mVideoView == null || mBtnPause == null)
			return;
		if (mVideoView.isPlaying())
			mBtnPause.setImageResource(R.drawable.media_palyer_pause_button_selector);
		else
			mBtnPause.setImageResource(R.drawable.media_palyer_play_button_selector);
	}

	/**
	 * 播放或者暂停按钮的图标
	 */
	private void togglePauseButton(boolean play) {
		if(mIsBackPlay) {
			return;
		}
		if (mVideoView == null || mBtnPause == null)
			return;
		if (play)
			mBtnPause.setImageResource(R.drawable.media_palyer_pause_button_selector);
		else
			mBtnPause.setImageResource(R.drawable.media_palyer_play_button_selector);
	}

	/**
	 * 播放或者暂停按钮的图标
	 */
	private void togglePauseButton(int play) {
		if (mBtnPause == null)
			return;
		if (play == 1)
			mBtnPause.setImageResource(R.drawable.media_palyer_pause_button_selector);
		else
			mBtnPause.setImageResource(R.drawable.media_palyer_play_button_selector);
	}
	
	/**
	 * 缓冲加载图片
	 * @param isBuffer
	 */
	private void BufferButton(boolean isBuffer){
		if(isBuffer) {
			if(mProgressView != null)
				mProgressView.setVisibility(View.VISIBLE);
		}
		else {
			if(mProgressView != null)
				mProgressView.setVisibility(View.GONE);
		}
	}
	
	@Override
	protected String getAnalyticsTitle() {
		return null;
	}

	@Override
	protected boolean isEnabledAnalytics() {
		return false;
	}
}