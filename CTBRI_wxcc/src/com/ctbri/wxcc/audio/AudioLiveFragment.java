package com.ctbri.wxcc.audio;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ctbri.wxcc.Constants;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseFragment;
import com.ctbri.wxcc.community.ObjectAdapter;
import com.ctbri.wxcc.entity.AudioRecomBean;
import com.ctbri.wxcc.entity.AudioRecomBean.AudioChannel;
import com.ctbri.wxcc.entity.CommonHolder;
import com.ctbri.wxcc.entity.MediaLiveDetail;
import com.ctbri.wxcc.entity.MediaLiveDetail.Program;
import com.ctbri.wxcc.widget.CouponPopupWindow;
import com.google.gson.Gson;

public class AudioLiveFragment extends BaseFragment {

	public static final String KEY_CHANNEL_ID = "channel_id";

	public static final String FRAGMENT_DAEMON_PLAYER = "daemon_player";
	
	//-----------------------------------------电台广播-----------------------------
	/**
	 * 接受到的停止命令
	 */
	public static final String ACTION_AUDIO_STOP = "com.ctbri.audio.stop";
	public static final String ACTION_AUDIO_PLAY_AUDIO = "com.ctbri.audio.play_audio";
	public static final String ACTION_AUDIO_REPLAY = "com.ctbri.audio.replay";
	/**
	 * 发送给外部的停止命令
	 */
	public static final String ACTION_AUDIO_STOP_CMD = "com.ctbri.audio.stop_cmd";
	public static final String ACTION_AUDIO_PLAYING_CMD = "com.ctbri.audio.playing";
	public static final String ACTION_AUDIO_INFO_CMD = "com.ctbri.audio.info";
	
	//发送播放&停止后台播放广播
	public static final String ACTION_AUDIO_START_STOP_BACKGROUND_PLAY = "com.ctbri.audio.start.stop.background.play";
	
	/**
	 * 状态栏发送广播通知同步按钮和viewpager
	 */
	public static final String ACTION_AUDIO_UPDATE_BUTTON_STATU = "com.ctbri.audio.update.button_statu";
	public static final String ACTION_AUDIO_UPDATE_VIEWPAGER = "com.ctbri.audio.update.viewpager";
	
	/**
	 * 发送上下频道广播
	 */
	public static final String ACTION_AUDIO_LEFT_RIGHT_BACKGROUND_PLAY = "com.ctbri.audio.left.right.background.play";
	
	//发送停止播放时更新按钮
	public static final String ACTION_AUDIO_STOP_UPDATE_BUTTON = "com.ctbri.audio.stop.update.button";
	
	//发送开始播放时更新按钮
	public static final String ACTION_AUDIO_REPLAY_UPDATE_BUTTON = "com.ctbri.audio.replay.update.button";
	//-----------------------------------------电台广播--------------------------------------------------------
	
	//-----------------------------------------音频广播--------------------------------------------------------
	//音频后台播放广播
	public static final String ACTION_AUDIO_VOD_BACKGROUND_PLAY = "com.ctbri.audio.vod.background.play";
	
	//音频播放时UI界面控制广播
	public static final String ACTION_AUDIO_VOD_PLAY_CONTROL = "com.ctbri.audio.vod.play.control";
	
	//控制上下一曲音频播放广播
	public static final String ACTION_AUDIO_VOD_NEXT_PLAY_CONTROL = "com.ctbri.audio.vod.next.play.control";
	
	//控制停止音频播放广播
	public static final String ACTION_AUDIO_VOD_PAUSEPLAY_CONTROL = "com.ctbri.audio.vod.pauseplay.control";
	
	//控制音频缓冲状态中广播
	public static final String ACTION_AUDIO_VOD_BUFFER_STATU_CONTROL = "com.ctbri.audio.vod.bufferstatu.control";
	
	//控制进度条切换,音频播放广播
	public static final String ACTION_AUDIO_VOD_SEEKTO_BACKGROUND_PLAY = "com.ctbri.audio.vod.seekto.background.play";
	
	//控制集数显示广播
	public static final String ACTION_AUDIO_VOD_COLLECTIONS_PLAY = "com.ctbri.audio.vod.collections.play";
	
	//控制音频播放位置广播
	public static final String ACTION_AUDIO_VOD_PLAYPOSITION_PLAY = "com.ctbri.audio.vod.playposition.play";
	
	/**
	 * 状态通知栏上下按键以及暂停播放按钮广播
	 */
	public static final String ACTION_AUDIO_LEFT_RIGHT_PLAY_VOD = "com.ctbri.audio.left.right_play_vod";
	public static final String ACTION_AUDIO_STOP_REPLAY_PLAY_VOD = "com.ctbri.audio.stop_replay_play_vod";
	
	/**
	 * 当播放视频时，音频以及电台都得暂停播放广播
	 */
	public static final String ACTION_AUDIO_ALLSTOP_PLAY = "com.ctbri.audio.allstop_play";
	//-----------------------------------------音频广播--------------------------------------------------------
	
	private ProgramAdapter mAdapter;
	private ImageButton ibtn_paly, ibtn_previous, ibtn_next;
	/**
	 * 处理主线程动作的handler
	 */
	private Handler mHandler = new Handler();

	/**
	 * 异步处理播放事件
	 */
	private static final String TAG = AudioStatusBarFragment.class.getName();
	public static final int MAX_THREAD = 1;
	private String mPlayUrl, mPlayId;
	private MediaLiveDetail mDetail;
	private String mChannelId, mCouponTitle, mCouponDate, mCouponImage, mCouponId;
	private ListView mInfoList;
	private Resources res;
	private ImageButton mBtn_show_coupon;
	private TextView mTv_listener_count;
	private View mView;
	private AudioRecomBean mBean;
	private SharedPreferences preference;
	private Editor editor;
	/**
	 * 自动播放音频的延迟时间
	 */
	private static final int AUTO_PLAY_BACK_DELAY = 1000 * 1;

	private OnAudioFocusChangeListener audioFocusListener;

	private BroadcastReceiver mControlReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String act = intent.getAction();
			if (ACTION_AUDIO_STOP.equals(act)) {
				if (isPlaying())
					stop();
				updatePlayButton();
			} else if (ACTION_AUDIO_REPLAY.equals(act)) {
				playBackCurrentProgram();
			} else if (ACTION_AUDIO_PLAY_AUDIO.equals(act)) {
				String url = intent.getStringExtra("url");
				String name = intent.getStringExtra("name");
				String id = intent.getStringExtra("id");
				playBackFromAction(url, name, id);
			} else if(ACTION_AUDIO_STOP_UPDATE_BUTTON.equals(act)) {
				updateStopReplayButton(false);
			} else if(ACTION_AUDIO_REPLAY_UPDATE_BUTTON.equals(act)) {
				updateStopReplayButton(true);
			} else if(ACTION_AUDIO_UPDATE_BUTTON_STATU.equals(act)){
				updateStopReplayButton(intent.getBooleanExtra("change",false));
			}
		}
	};

	public static final AudioLiveFragment newInstance(String id) {
		AudioLiveFragment fragment = new AudioLiveFragment();
		Bundle args = new Bundle();
		args.putString(KEY_CHANNEL_ID, id);
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public void onAttach(Activity activity_) {
		super.onAttach(activity_);
		mChannelId = getArgs(KEY_CHANNEL_ID);
		
		if (preference == null) {
			preference = activity_.getSharedPreferences("audioPlayStatu", Context.MODE_PRIVATE);
			editor = preference.edit();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_AUDIO_STOP);
		filter.addAction(ACTION_AUDIO_REPLAY);
		filter.addAction(ACTION_AUDIO_STOP_UPDATE_BUTTON);
		filter.addAction(ACTION_AUDIO_REPLAY_UPDATE_BUTTON);
		filter.addAction(ACTION_AUDIO_UPDATE_BUTTON_STATU);
		activity.registerReceiver(mControlReceiver, filter);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		res = getResources();
		if (!TextUtils.isEmpty(mChannelId))
			getData();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		boolean playStatu = isPlaying();
		ibtn_paly.setKeepScreenOn(playStatu);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mHandler.removeCallbacksAndMessages(null);
		activity.unregisterReceiver(mControlReceiver);
	}
	
	@Override
	protected String getAnalyticsTitle() {
		return null;
	}

	protected boolean isEnabledAnalytics() {
		return false;
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.audio_live_layout, container, false);

		mInfoList = (ListView) mView.findViewById(R.id.lv_audio_info);
		ibtn_next = (ImageButton) mView.findViewById(R.id.ibtn_next);
		ibtn_paly = (ImageButton) mView.findViewById(R.id.ibtn_play);
		ibtn_previous = (ImageButton) mView.findViewById(R.id.ibtn_previous);
		mTv_listener_count = (TextView) mView.findViewById(R.id.tv_listener_count);

		mBtn_show_coupon = (ImageButton) mView.findViewById(R.id.btn_show_coupon);
		mBtn_show_coupon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CouponPopupWindow mCouponWindow = new CouponPopupWindow(activity);
				// mCouponWindow.showAtLocation(mView, Gravity.NO_GRAVITY, 0 ,0);
				int[] pos = new int[2];
				mView.getLocationOnScreen(pos);
				mCouponWindow.setArgs(mCouponTitle, mCouponDate, mCouponImage, mCouponId);
				mCouponWindow.setAnimationStyle(R.style.coupon_animation);
				mCouponWindow.showAtLocation(mView, Gravity.NO_GRAVITY, 0, pos[1]);
				// mCouponWindow.showAsDropDown(v);
			}
		});
		ibtn_next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AudioChanelHead channels = (AudioChanelHead) getFragmentManager().findFragmentByTag("fragment_channels");
				if (channels != null) {
					if (!channels.next()) {
						toast("已经是最后一条");
						v.setEnabled(false);
					} else {
						ibtn_previous.setEnabled(true);
					}
				}
			}
		});
		ibtn_previous.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AudioChanelHead channels = (AudioChanelHead) getFragmentManager().findFragmentByTag("fragment_channels");
				if (channels != null) {
					if (!channels.previous()) {
						toast("已经是第一条");
						v.setEnabled(false);
					} else {
						ibtn_next.setEnabled(true);
					}
				}
			}
		});
		return mView;
	}

	/**
	 * 重置上一条，下一条按钮状态
	 */
	private void resetNextOrPreBtnState() {
		if (ibtn_next == null)
			return;
		ibtn_next.setEnabled(true);
		ibtn_previous.setEnabled(true);
	}

	private OnClickListener mPlay = new OnClickListener() {
		@Override
		public void onClick(View v) {
			playBackCurrentProgram();
			updatePlayButton(true);
		}
	};

	private OnClickListener mStop = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (isPlaying()) {
				stop();
				sendStopCmd();
			}
			updatePlayButton(false);

		}
	};
	/**
	 * 自动延迟播放
	 */
	private Runnable mAutoPlayback = new Runnable() {
		@Override
		public void run() {
			playBackCurrentProgram();
		}
	};

	private void playBackFromAction(String url, String name, String id) {
		play(url, name,id,true,getPlayPosition(),mBean);
		sendPlayingCmd(mDetail.getChannel_name());
	}

	private void playBackCurrentProgram() {
		if (mDetail != null) {
			updatePlayButton(true);
			play(mDetail.getLive(), mDetail.getChannel_name(), mDetail.getChannel_id(),true,getPlayPosition(),mBean);
			sendPlayingCmd(mDetail.getChannel_name());
		}
	}

	private boolean isPlaying() {
		return preference.getBoolean("playStatu",false);
	}
	
	 /*@TargetApi(Build.VERSION_CODES.FROYO)
	 private void changeAudioFocus(boolean gain) {
		 if (!LibVlcUtil.isFroyoOrLater()) // NOP if not supported
			 return;
	
		 if (audioFocusListener == null) {
			 audioFocusListener = new OnAudioFocusChangeListener() {
				 @Override
				 public void onAudioFocusChange(int focusChange) {
					 LibVLC libVLC = LibVLC.getExistingInstance();
					 switch (focusChange) {
					 case AudioManager.AUDIOFOCUS_LOSS:
						 if (libVLC.isPlaying())
							 libVLC.pause();
						 break;
					 case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
					 case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
						 
						 * Lower the volume to 36% to "duck" when an alert or
						 * something needs to be played.
						 
						 libVLC.setVolume(36);
						 break;
					 case AudioManager.AUDIOFOCUS_GAIN:
					 case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
					 case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
						 libVLC.setVolume(100);
						 break;
					 }
				 }
			 };
		 }
	
		 AudioManager am = (AudioManager)activity.getSystemService(Context.AUDIO_SERVICE);
		 if (gain)
			 am.requestAudioFocus(audioFocusListener, AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
		 else
			 am.abandonAudioFocus(audioFocusListener);
 	}*/

	public void play(String url, String title, String mId, boolean playStatu, int viewPagerPosition,AudioRecomBean bean) {
		if (TextUtils.isEmpty(url))
			return;

		// 在后台开启通知栏并在后台播放
		sendStartBackPlay(url, title, mId, playStatu, viewPagerPosition, bean);
	}

	/**
	 * 发送后台播放广播
	 * @param url
	 * @param title
	 * @param mId
	 * @param playStatu
	 * @param viewPagerPosition
	 * @param bean
	 */
	private void sendStartBackPlay(String url, String title, String mId, boolean playStatu, int viewPagerPosition, AudioRecomBean bean) {
		Intent backPlay = new Intent(ACTION_AUDIO_START_STOP_BACKGROUND_PLAY);
		sendIntent(backPlay,url,title,mId,playStatu,viewPagerPosition,bean,"start");
		ibtn_paly.setKeepScreenOn(true);
	}
	
	private void stopBackPlay() {
		sendStopBackPlay();
		sendStopCmd();
	}
	
	private void sendStopBackPlay() {
		Intent stopBackPlay = new Intent(ACTION_AUDIO_START_STOP_BACKGROUND_PLAY);
		sendIntent(stopBackPlay,mDetail.getLive(), mDetail.getChannel_name(), mDetail.getChannel_id(),false,getPlayPosition(),mBean,"stop");
	}

	private void stop() {
		// 通知后台暂停播放
		stopBackPlay();
		
	}
	
	/**
	 * 开始播放当前音频
	 */
	public void start() {
		if (isPlaying())
			return;
		else
			playBackCurrentProgram();
	}

	/**
	 * 获取播放位置
	 * @return
	 */
	private int getPlayPosition() {
		return preference.getInt("viewPagerPosition",-1);
	}
	
	/**
	 * 发送 停止广播
	 */
	private void sendStopCmd() {
		Intent stop = new Intent(ACTION_AUDIO_STOP_CMD);
		activity.sendBroadcast(stop);
	}

	private void sendChannelInfo(String mid, String title, String url) {
		Intent intent = new Intent(ACTION_AUDIO_INFO_CMD);
		intent.putExtra("title", title);
		intent.putExtra("url", url);
		intent.putExtra("id", mid);
		activity.sendBroadcast(intent);
	}

	/**
	 * 发送播放广播
	 * 
	 * @param name
	 */
	private void sendPlayingCmd(String name) {
		Intent playing = new Intent(ACTION_AUDIO_PLAYING_CMD);
		playing.putExtra("title", name);
		activity.sendBroadcast(playing);
	}
	
	/**
	 * 同步暂停播放时的按钮
	 * @param statu
	 */
	private void updateStopReplayButton(boolean statu) {
		if (mDetail != null) {
			updatePlayButton(statu);
			if(statu)
				sendPlayingCmd(mDetail.getChannel_name());
			else {
				sendStopCmd();
			}
		}
	}

	/**
	 * 发送在后台开启通知栏并在后台播放广播
	 */
	public void sendIntent(Intent intent, String url, String title, String mId, boolean playStatu, int viewPagerPosition,AudioRecomBean bean,String channel) {
		intent.putExtra("ChannelName", title);
		intent.putExtra("Live", url);
		intent.putExtra("ChannelId", mId);
		intent.putExtra("playStatu", playStatu);
		intent.putExtra("viewPagerPosition", viewPagerPosition);
		intent.putExtra("AudioRecomBean",bean);
		intent.putExtra("channel", channel);
		activity.sendBroadcast(intent);
		
		savePlayStatu(viewPagerPosition, playStatu);
	}

	/**
	 * 保存最新播放状态
	 * @param viewPagerPosition 当前记录的ViewPager滑动页
	 * @param playStatu 播放状态
	 */
	private void savePlayStatu(int viewPagerPosition, boolean playStatu) {
		if (editor != null) {
			editor.putInt("viewPagerPosition", viewPagerPosition);
			editor.putBoolean("playStatu", playStatu);
			editor.commit();
		}
	}

	private void updatePlayButton() {
		updatePlayButton(isPlaying());
	}

	private void updatePlayButton(boolean isPlay) {
		if(ibtn_paly != null) {
			if (isPlay) {
				ibtn_paly.setImageResource(R.drawable.audio_vod_pause_selector);
				ibtn_paly.setOnClickListener(mStop);
			} else {
				ibtn_paly.setImageResource(R.drawable.audio_vod_play_selector);
				ibtn_paly.setOnClickListener(mPlay);
			}
		}
	}

	public void update(AudioChannel channel,AudioRecomBean bean) {
		this.mChannelId = channel.getChannel_id();
		mBean = bean;
		if (isPlaying()) {
			play(channel.getLive(), channel.getChannel_name(), this.mChannelId,true,getPlayPosition(),mBean);
		} else {
			mDetail = new MediaLiveDetail();
			mDetail.setChannel_id(channel.getChannel_id());
			mDetail.setChannel_name(channel.getChannel_name());
			mDetail.setLive(channel.getLive());
			sendChannelInfo(channel.getChannel_id(), channel.getChannel_name(), channel.getLive());
		}
		resetNextOrPreBtnState();
		updatePlayButton();
		getData();
	}

	private void getData() {
		String url = Constants.METHOD_AUDIO_LIVE_DETAIL + "?channel_id=" + mChannelId;
		request(url, new RequestCallback() {
			@Override
			public void requestSucc(String json) {
				Gson gson = new Gson();
				MediaLiveDetail bean = gson.fromJson(json, MediaLiveDetail.class);
				if (bean != null && bean.getData() != null) {
					mDetail = bean.getData();

					mCouponDate = mDetail.getCoupon_validity();
					mCouponId = mDetail.getCoupon_id();
					mCouponImage = mDetail.getCoupon_pic();
					mCouponTitle = mDetail.getCoupon_name();

					mBtn_show_coupon.setVisibility(TextUtils.isEmpty(mCouponId) ? View.INVISIBLE : View.VISIBLE);

					mTv_listener_count.setText(mDetail.getPlays());

					List<Program> list = mDetail.getProgram_list();
					mAdapter = new ProgramAdapter(activity, list);
					mInfoList.setAdapter(mAdapter);
					selectPlayingItem();
					updatePlayButton();
				}
			}

			@Override
			public void requestFailed(int errorCode) {

			}
		});
	}

	private void autoPlayback() {
		mHandler.removeCallbacks(mAutoPlayback);
		mHandler.postDelayed(mAutoPlayback, AUTO_PLAY_BACK_DELAY);
	}

	/**
	 * 根据当前播放的音频，和 Libvlc 的状态，判断是否在播放进行
	 */
	private void checkPlayButtonStatus() {
		if (mPlayId == null || !mPlayId.equals(mDetail.getChannel_id()) || !isPlaying()) {
			// 如果是第一次打开,立即播放当前的节目
			if (mPlayId == null) {
				playBackCurrentProgram();
				updatePlayButton(false);
			} else {
				autoPlayback();
				updatePlayButton(true);
			}
		} else {
			updatePlayButton(false);
		}
	}

	/**
	 * 滚动至直播节目单列表中正在播放的节目
	 */
	public void selectPlayingItem() {
		if (mAdapter != null) {
			int c = mAdapter.getCount();
			for (int i = 0; i < c; i++) {
				Program program = mAdapter.getItem(i);
				if (program.getStatus() == Program.STATUS_BROADCAST) {
					i = i > 1 ? i - 1 : i;
					mInfoList.setSelection(i);
					break;
				}
			}
		}
	}

	class ProgramAdapter extends ObjectAdapter<Program> {

		public ProgramAdapter(Activity activity, List<Program> data_) {
			super(activity, data_);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			CommonHolder holder = null;
			if (convertView == null) {
				holder = new CommonHolder();
				convertView = inflater.inflate(R.layout.media_broadcast_video_info_item, parent, false);
				convertView.setTag(holder);
				// 正在播放、已经播放、未播放 的状态圆环
				holder.iv = (ImageView) convertView.findViewById(R.id.iv_broadcast_circle);
				// 节目时间
				holder.tv1 = (TextView) convertView.findViewById(R.id.tv_time_line);
				// 节目名称
				holder.tv2 = (TextView) convertView.findViewById(R.id.tv_video_name);
			} else {
				holder = (CommonHolder) convertView.getTag();
			}
			Program timeline = getItem(position);
			// 如果当前的视频正在播放中
			if (timeline.getStatus() == Program.STATUS_BROADCAST) {
				holder.iv.setImageResource(R.drawable.ic_broadcast_state_circle_broadcast);
				holder.tv1.setTextColor(res.getColor(R.color.media_time_line_broadcast_color));
				holder.tv2.setTextColor(res.getColor(R.color.media_time_line_broadcast_color));
			} else {
				holder.iv.setImageResource(R.drawable.ic_broadcast_state_circle_normal);
				holder.tv1.setTextColor(res.getColor(R.color.media_time_line_normal_color));
				holder.tv2.setTextColor(res.getColor(R.color.media_time_line_normal_color));
			}

			holder.tv1.setText(timeline.getTime());
			holder.tv2.setText(timeline.getName());

			return convertView;
		}
	}
}