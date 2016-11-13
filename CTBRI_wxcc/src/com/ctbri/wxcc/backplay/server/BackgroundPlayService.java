package com.ctbri.wxcc.backplay.server;

import static com.ctbri.wxcc.audio.AudioLiveFragment.ACTION_AUDIO_ALLSTOP_PLAY;
import static com.ctbri.wxcc.audio.AudioLiveFragment.ACTION_AUDIO_LEFT_RIGHT_BACKGROUND_PLAY;
import static com.ctbri.wxcc.audio.AudioLiveFragment.ACTION_AUDIO_LEFT_RIGHT_PLAY_VOD;
import static com.ctbri.wxcc.audio.AudioLiveFragment.ACTION_AUDIO_PLAYING_CMD;
import static com.ctbri.wxcc.audio.AudioLiveFragment.ACTION_AUDIO_START_STOP_BACKGROUND_PLAY;
import static com.ctbri.wxcc.audio.AudioLiveFragment.ACTION_AUDIO_STOP_CMD;
import static com.ctbri.wxcc.audio.AudioLiveFragment.ACTION_AUDIO_STOP_REPLAY_PLAY_VOD;
import static com.ctbri.wxcc.audio.AudioLiveFragment.ACTION_AUDIO_UPDATE_BUTTON_STATU;
import static com.ctbri.wxcc.audio.AudioLiveFragment.ACTION_AUDIO_UPDATE_VIEWPAGER;
import static com.ctbri.wxcc.audio.AudioLiveFragment.ACTION_AUDIO_VOD_BACKGROUND_PLAY;
import static com.ctbri.wxcc.audio.AudioLiveFragment.ACTION_AUDIO_VOD_BUFFER_STATU_CONTROL;
import static com.ctbri.wxcc.audio.AudioLiveFragment.ACTION_AUDIO_VOD_COLLECTIONS_PLAY;
import static com.ctbri.wxcc.audio.AudioLiveFragment.ACTION_AUDIO_VOD_NEXT_PLAY_CONTROL;
import static com.ctbri.wxcc.audio.AudioLiveFragment.ACTION_AUDIO_VOD_PAUSEPLAY_CONTROL;
import static com.ctbri.wxcc.audio.AudioLiveFragment.ACTION_AUDIO_VOD_PLAYPOSITION_PLAY;
import static com.ctbri.wxcc.audio.AudioLiveFragment.ACTION_AUDIO_VOD_PLAY_CONTROL;
import static com.ctbri.wxcc.audio.AudioLiveFragment.ACTION_AUDIO_VOD_SEEKTO_BACKGROUND_PLAY;

import java.io.IOException;
import java.util.List;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnBufferingUpdateListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnCompletionListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnErrorListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnInfoListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnPreparedListener;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.ctbri.wxcc.audio.DaemonPlayerFragment;
import com.ctbri.wxcc.audio.DaemonPlayerFragment.DaemonPlayerListener;
import com.ctbri.wxcc.backplay.notification.BackPlayNotification;
import com.ctbri.wxcc.entity.AudioRecomBean;
import com.ctbri.wxcc.entity.AudioRecomBean.AudioChannel;
import com.ctbri.wxcc.entity.AudioVodDetail;
import com.ctbri.wxcc.entity.VodDetailBean.VodDetailItem;
import com.sina.weibo.sdk.utils.LogUtil;

/**
 * 后台播放音频服务
 * @author 张政  2015/7/7
 *
 */
public class BackgroundPlayService extends Service {
	private DaemonPlayerFragment player; 
	private IjkMediaPlayer mMediaPlayer;
	private AudioRecomBean bean;
	private List<VodDetailItem> vods;
	private int viewPagerPosition;
	private int mDuration;
	private int vodPlayCurrentPosition; // 缓存当前播放进度
	private int mProgress;
	private int playPosition = 0;
	private int lastId;
	private String channelName,title;
	private boolean playStatu,vod_PlayStatu;
	private String channelId;
	private String live,url;
	private String lastUrl = ""; // 上一次播放的url
	private String lastChannelName = "";
	private Context vod_context;
	private SharedPreferences preference;
	private Editor editor;
	private boolean isCachePlay, isCompletion, isSeekTo, isPiece;
	private boolean aodPlayStatu,radioPlayStatu;
	
	private static final int UPDATE_TIME = 1001;
	private static final int FADE_HIDE = 1002;
	protected static final String TAG = "BackgroundPlayService";
	
	private Handler mPlayerHandler = new  Handler() {
		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case UPDATE_TIME:
				updateTime();
				break;
			case FADE_HIDE:
				//TODO
//				hideBar();
				break;

			}
		}
	};
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		lastId++;
		//创建播放器
		player = getPlayer();
	
		registerReceiver();
		if(lastId != startId) {
			lastId = startId;
			savePlayStatu(-1,false);
			
			//取消状态通知栏
			((NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();
		}
		
		if (preference == null) {
			preference = this.getSharedPreferences("audioPlayStatu", Context.MODE_PRIVATE);
			editor = preference.edit();
		}
		
		//获取来电
		TelephonyManager teleMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		teleMgr.listen(new TelephoneStateListener(),PhoneStateListener.LISTEN_CALL_STATE);
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	class TelephoneStateListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch(state) {
			case TelephonyManager.CALL_STATE_IDLE:   // 撂电话
				if(player != null && !getPlayState() && !radioPlayStatu) {
					playRadio(live,channelName,channelId);
					radioPlayStatu = true;
					return;
				}
				
				if(mMediaPlayer!=null && !mMediaPlayer.isPlaying() && !aodPlayStatu) {
					mMediaPlayer.start();
					mPlayerHandler.sendEmptyMessage(UPDATE_TIME);
					aodPlayStatu = true;
				}
				break;
			case TelephonyManager.CALL_STATE_RINGING:// 来电话
				call_Ringing_Idle_Stop_Play();
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:// 接电话
				break;
			default:
				break;
			}
		}
	}
	
	/**
	 * 呼出、呼入电话暂停播放
	 */
	private void call_Ringing_Idle_Stop_Play(){
		if(player != null && getPlayState()) {
			player.stop(); 
			radioPlayStatu = false;
			aodPlayStatu = true;
		}
		
		if(mMediaPlayer!=null && mMediaPlayer.isPlaying()) {
			mMediaPlayer.pause();
			mPlayerHandler.removeMessages(UPDATE_TIME);
			aodPlayStatu = false;
			radioPlayStatu = true;
		}
	}
	
	/**
	 * 获得音频播放器
	 * @return
	 */
	private IjkMediaPlayer getMediaPlayer() {
		if (mMediaPlayer == null) {
			mMediaPlayer = new IjkMediaPlayer();
			mMediaPlayer.setOnPreparedListener(mMediaPlayerListener);
			mMediaPlayer.setOnBufferingUpdateListener(mMediaPlayerListener);
			mMediaPlayer.setOnCompletionListener(mMediaPlayerListener);
			mMediaPlayer.setOnErrorListener(mMediaPlayerListener);
			mMediaPlayer.setOnInfoListener(mMediaPlayerListener);
		}
		return mMediaPlayer;
	}

	/**
	 * 获取电台播放器
	 * @return
	 */
	private DaemonPlayerFragment getPlayer() {
		if (player == null) {
			player = new DaemonPlayerFragment();
			player.setListener(mListener);
		}
		return player;
	}
	
	private void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_AUDIO_START_STOP_BACKGROUND_PLAY);
		filter.addAction(ACTION_AUDIO_LEFT_RIGHT_BACKGROUND_PLAY);
		filter.addAction(ACTION_AUDIO_VOD_BACKGROUND_PLAY);
		filter.addAction(ACTION_AUDIO_VOD_SEEKTO_BACKGROUND_PLAY);
		filter.addAction(ACTION_AUDIO_VOD_COLLECTIONS_PLAY);
		filter.addAction(ACTION_AUDIO_VOD_PLAYPOSITION_PLAY);
		filter.addAction(ACTION_AUDIO_LEFT_RIGHT_PLAY_VOD);
		filter.addAction(ACTION_AUDIO_STOP_REPLAY_PLAY_VOD);
		filter.addAction(ACTION_AUDIO_ALLSTOP_PLAY);
		filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		registerReceiver(mBackgroundPlayReceiver, filter);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mBackgroundPlayReceiver);
	}
	
	private BroadcastReceiver mBackgroundPlayReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			vod_context = context;
			String act = intent.getAction();
			
			if(Intent.ACTION_NEW_OUTGOING_CALL.equals(act)){
				call_Ringing_Idle_Stop_Play();
				return;
			}		
			channelName = intent.getStringExtra("ChannelName");
			playStatu  = intent.getBooleanExtra("playStatu",false);
			live = intent.getStringExtra("Live");
			channelId = intent.getStringExtra("ChannelId");
			bean = (AudioRecomBean) intent.getSerializableExtra("AudioRecomBean");
			
			if(ACTION_AUDIO_START_STOP_BACKGROUND_PLAY.equals(act)) { //开始播放电台
				viewPagerPosition = intent.getIntExtra("viewPagerPosition", -1);	
				String channel = intent.getStringExtra("channel");
				if("start".equals(channel)) {
					//后台开始播放
					if(!lastChannelName.equals(channelName) && playStatu) {
						//在后台开启通知栏 
						playRadio(live,channelName,channelId);
					}
				}else if("stop".equals(channel)){
					//后台暂停播放
					if(player != null && getPlayState())
						player.stop(); //暂停音频
				}
			}else if(ACTION_AUDIO_LEFT_RIGHT_BACKGROUND_PLAY.equals(act)) { //上下频道播放电台
				String channel = intent.getStringExtra("channel"); //获取频道
				viewPagerPosition = intent.getIntExtra("viewPagerPosition", -1);
				if("left".equals(channel)) {
					leftRightChannel(context);
				}else if("right".equals(channel)) {
					leftRightChannel(context);
				}
			}else if(ACTION_AUDIO_VOD_BACKGROUND_PLAY.equals(act)) { //后台播放音频
				vod_PlayStatu = intent.getBooleanExtra("statu",false);
				url = intent.getStringExtra("url");
				title = intent.getStringExtra("title");
				lastChannelName = url;
				if(vod_PlayStatu) {
					doPauseState(context,title,url);
				} else {
					aodPlayStatu = false;
//TODO					BackPlayNotification.getInstance().showNotif(context,title,true,url);
					isSeekTo = false;
					if(vods != null && vods.size() > 0 && mMediaPlayer!=null) {//合集播放
						sendCheckedChanged();
						if(!mMediaPlayer.isPlaying()) {
							if(vodPlayCurrentPosition != -1)
								isCachePlay = true;
							playListsAod();
						}else if(mMediaPlayer.isPlaying() && isPiece){
							isPiece = false;
							playListsAod();
						}
						sendBufferStatuButton(false);
						return;
					}
					
					if(!lastUrl.equals(url) ||  vodPlayCurrentPosition == -1) {
						if(url != null && !"".equals(url)) {
							vodPlayCurrentPosition = -1;
							isCachePlay = false;
							playAod();
						}
					}else {
						sendBufferStatuButton(false);
					}
					
					if(mMediaPlayer!=null && !mMediaPlayer.isPlaying() && vodPlayCurrentPosition != -1) {
						if(url != null && !"".equals(url)) {
							isCachePlay = true;
							playAod();
						}
					}
				}
			}else if(ACTION_AUDIO_VOD_SEEKTO_BACKGROUND_PLAY.equals(act)) { //进度条拖动处理
				isSeekTo = true;
				mProgress = intent.getIntExtra("progress",-1);
				if(mProgress != -1 && mMediaPlayer != null) {
					mMediaPlayer.seekTo((mDuration * mProgress) / 1000);
					mPlayerHandler.sendEmptyMessage(UPDATE_TIME);
				}
				
				/*if(isCompletion && lastUrl != null && !"".equals(lastUrl)) {
					Message msg = Message.obtain();
					msg.obj = lastUrl;
					msg.what = AUDIO_VOD;
					mHandler.sendMessage(msg);
				}*/
			}else if(ACTION_AUDIO_VOD_COLLECTIONS_PLAY.equals(act)) { //音频合集播放
				String vod = "";
				if(vods!=null) {
					vod = vods.get(0).getVod();
				}
				AudioVodDetail data = (AudioVodDetail) intent.getSerializableExtra("AudioVodDetail");
				if(data != null) {
					vods = data.getAudios();
					String url = vods.get(0).getVod();
					if(!vod.equals(url)){
						playEnd();
						playPosition = 0; 
					}
				}else {
					vods = null;
					playPosition = 0; 
				}
			}else if(ACTION_AUDIO_VOD_PLAYPOSITION_PLAY.equals(act)) { //音频播放位置
				vodPlayCurrentPosition = -1;
				playPosition = intent.getIntExtra("playPosition", 0);
				isPiece = intent.getBooleanExtra("piece", false);
			}else if(ACTION_AUDIO_LEFT_RIGHT_PLAY_VOD.equals(act)) { //上下频道播放音频
				String channel = intent.getStringExtra("channel"); //获取频道
				if("left".equals(channel)) {
					if(vods != null && vods.size() > 0) {
						playPosition --;
						if(!(playPosition < 0)) {
							title = vods.get(playPosition).getName();
							backPlay();
						}else {
							playPosition = 0;						
						}
					}
				}else if("right".equals(channel)) {
					if(vods != null && vods.size() > 0) {
						playPosition ++;
						if(!(playPosition >= vods.size())) {
							title = vods.get(playPosition).getName();
							backPlay();
						}else {
							playPosition = vods.size()-1;						
						}
					}
				}
			}else if(ACTION_AUDIO_STOP_REPLAY_PLAY_VOD.equals(act)) { //暂停or播放音频
				String vod_live = intent.getStringExtra("live");
				String vod_title = intent.getStringExtra("title");
				doPauseState(context, vod_title, vod_live);
			}else if(ACTION_AUDIO_ALLSTOP_PLAY.equals(act)){ //如果为视频播放，那么电台和音频播放都得暂停
				if(player != null && getPlayState()) {
					player.stop(); 
					lastChannelName = "";
					savePlayStatu(viewPagerPosition,false);
				}
				
				if(mMediaPlayer!=null && mMediaPlayer.isPlaying()) {
					aodPlayStatu = true;
					mMediaPlayer.pause();
				}
				
				((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();
			}
		}
	};
	
	/**
	 * 上下频道调节
	 * @param context
	 */
	private void leftRightChannel(Context context) {
		
		viewPagerPosition =preference.getInt("viewPagerPosition",-1);
		AudioChannel audioChannel = bean.getData().getChannels().get(viewPagerPosition % bean.getData().getChannels().size());
		channelName = audioChannel.getChannel_name();
		live = audioChannel.getLive();
		channelId = audioChannel.getChannel_id();
		playStatu = true;
		
		playRadio(live,channelName,channelId);
	}
	
	/*
	 * 播放电台
	 */
	private void playRadio(String radio_url,String radio_name,String radio_id){
		aodPlayStatu = true;
		if(mMediaPlayer!=null && mMediaPlayer.isPlaying()) //如果要播放电台，那么暂停播放音乐
			mMediaPlayer.pause();
		
		if(player != null)
			player.openUrl(radio_url, radio_name, radio_id); //播放电台音频
	}
	/**
	 * 播放合集音频
	 */
	private void playListsAod(){
		sendBufferStatuButton(true);
		playEnd();
		lastUrl = vods.get(playPosition).getVod();
		playStart(lastUrl);
	}
	
	/**
	 * 播放单曲音频
	 */
	private void playAod(){
		sendBufferStatuButton(true);
		playEnd();
		lastUrl = url;
		playStart(url);
	}
	
	private void playStart(String aod_url){
		if(player != null && getPlayState()) {
			player.stop(); //如果要播放音乐，那么暂停播放电台
			savePlayStatu(viewPagerPosition,false);
		}
		try {
			//播放音频
			mMediaPlayer = getMediaPlayer();
			mMediaPlayer.setDataSource(aod_url);
			mMediaPlayer.prepareAsync();
			BackPlayNotification.getInstance().showNotif(vod_context,title,true,url);
			sendCheckedChanged();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 电台播放相关的事件
	 */
	private DaemonPlayerListener mListener = new DaemonPlayerListener() {

		@Override
		public void onStop() {
			changeNotification(lastChannelName,false);
			lastChannelName = "";
			sendStopCmd();
			sendUpdateButtonState(false);
		}

		@Override
		public void onPlaying(String url, String title, String id) {
			changeNotification(channelName,true);
			lastChannelName = channelName;
			sendPlayingCmd(title);
			sendUpdateButtonState(true);
			sendUpdateViewPager();
		}

		@Override
		public void onError() {
			changeNotification(lastChannelName,false);
			lastChannelName = "";
			sendStopCmd();
			sendUpdateButtonState(false);
		}
	};
	
	private void changeNotification(String name, boolean playStatu){
		BackPlayNotification.getInstance().showNotif(vod_context,name,playStatu,live,channelId,viewPagerPosition,bean);
	}
	
	/**
	 * 音频播放相关的事件
	 */
	private MediaPlayerListener mMediaPlayerListener = new MediaPlayerListener();
	
	class MediaPlayerListener implements OnPreparedListener, OnCompletionListener, OnInfoListener, OnBufferingUpdateListener, OnErrorListener {

		@Override
		public void onPrepared(IMediaPlayer mp) {
			if(isCachePlay)
				mMediaPlayer.seekTo(vodPlayCurrentPosition);
			
			if(isSeekTo)
				mMediaPlayer.seekTo((mDuration * mProgress) / 1000);
			
			togglePauseButton(1);
			updateTime();
			
			sendBufferStatuButton(false);
			isCompletion = false;
//			showBar();
		}

		@Override
		public boolean onInfo(IMediaPlayer mp, int what, int extra) {
			if(what == IMediaPlayer.MEDIA_INFO_BUFFERING_START){
				sendBufferStatuButton(true);
			}else if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_END) {
				sendBufferStatuButton(false);
			}
			return true;
		}

		@Override
		public void onCompletion(IMediaPlayer mp) {
			playPosition++;
			//播放结束
			vodPlayCurrentPosition = -1;
			playEnd();
			isCompletion = true;
//			showBar(0);
			//同步按钮
			togglePauseButton(0);
			if(vods != null && vods.size()>0){
				if(playPosition >= vods.size() || playPosition < 0) {
					playPosition = 0;
					BackPlayNotification.getInstance().showNotif(vod_context,title,false,lastUrl);
					return;
				}
				backPlay();
			}else {
				BackPlayNotification.getInstance().showNotif(vod_context,title,false,lastUrl);
			}
		}
		
		@Override
		public void onBufferingUpdate(IMediaPlayer mp, int percent) {
		}

		@Override
		public boolean onError(IMediaPlayer mp, int what, int extra) {
//			Toast.makeText(getApplicationContext(), R.string.msg_video_play_error, Toast.LENGTH_SHORT).show();
			return true;
		}
	}
	
	//后台连续播放
	private void backPlay() {
		aodPlayStatu = false;
//TODO		BackPlayNotification.getInstance().showNotif(vod_context,title,true,url);
		
		String mUrl = vods.get(playPosition).getVod();
		if(mUrl != null && !"".equals(mUrl)) {
			vodPlayCurrentPosition = -1;
			isCachePlay = false;
			isSeekTo = false;
			playEnd();
			lastUrl = mUrl;
			playStart(mUrl);
		}
	}
	
	/**
	 * 播放结束
	 */
	private void playEnd() {
		mPlayerHandler.sendEmptyMessage(UPDATE_TIME);
		lastUrl = "";
		if(mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}
	
	private void updateTime() {
		if (mMediaPlayer == null || !mMediaPlayer.isPlaying())
			return;
		
		int duration = (int) mMediaPlayer.getDuration();
		mDuration = duration;
		if (duration > 0) {
			int pos = (int) mMediaPlayer.getCurrentPosition();
			vodPlayCurrentPosition = pos;
			long progress = 1000L * pos / duration;
			
			Intent intent = new Intent(ACTION_AUDIO_VOD_PLAY_CONTROL);
			intent.putExtra("progress", progress);
			intent.putExtra("duration", duration);
			intent.putExtra("isPlay", 1);//1 表示true 0 表示false
			sendBroadcast(intent);
			mPlayerHandler.sendEmptyMessageDelayed(UPDATE_TIME, 1000 - (pos % 1000));
		}
	}
	
	private void doPauseState(Context context,String title,String vod_videoUrl) {
		
		if(mMediaPlayer != null) {
			boolean playStatu;
			boolean isPlay = mMediaPlayer.isPlaying();
			if (isPlay) {
				aodPlayStatu = true;
				playStatu = false;
				mMediaPlayer.pause();
				mPlayerHandler.removeMessages(UPDATE_TIME);
				togglePauseButton(0);
			}else {
				playStatu = true;
				aodPlayStatu = false;
				mMediaPlayer.start();
				mPlayerHandler.sendEmptyMessage(UPDATE_TIME);
				togglePauseButton(1);
			}
			BackPlayNotification.getInstance().showNotif(context,title,playStatu,vod_videoUrl);
		}
	}
	
	/**
	 * 同步音频播放按钮
	 * @param isPlay
	 */
	private void togglePauseButton(int isPlay) {
		Intent intent = new Intent(ACTION_AUDIO_VOD_PAUSEPLAY_CONTROL);
		intent.putExtra("startPuase", isPlay);//1 表示true 0 表示false
		sendBroadcast(intent);
	}

	/**
	 * 同步按钮
	 * @param playStatu
	 */
	private void sendUpdateButtonState(boolean state) {
		Intent updateButtonStatu = new Intent(ACTION_AUDIO_UPDATE_BUTTON_STATU);
		updateButtonStatu.putExtra("change", state);
		sendBroadcast(updateButtonStatu);
		savePlayStatu(viewPagerPosition,state);
	}
	
	/**
	 * 同步ViewPager
	 */
	private void sendUpdateViewPager() {
		Intent updateViewPager = new Intent(ACTION_AUDIO_UPDATE_VIEWPAGER);
		sendBroadcast(updateViewPager);
	}
	
	/**
	 * 获取播放状态
	 * @return
	 */
	private boolean getPlayState() {
		if(player != null)
			return player.isPlaying();
		return false;
	}
	
	/**
	 * 发送停止广播
	 */
	private void sendStopCmd() {
		Intent stop = new Intent(ACTION_AUDIO_STOP_CMD);
		sendBroadcast(stop);
	}
	
	/**
	 * 发送播放广播
	 * 
	 * @param name
	 */
	private void sendPlayingCmd(String name) {
		Intent playing = new Intent(ACTION_AUDIO_PLAYING_CMD);
		playing.putExtra("title", name);
		sendBroadcast(playing);
	}
	
	/**
	 * 保存数据到本地
	 * @param viewPagerPosition
	 * @param playStatu
	 */
	private void savePlayStatu(int viewPagerPosition, boolean playStatu) {
		if (editor != null) {
			editor.putInt("viewPagerPosition", viewPagerPosition);
			editor.putBoolean("playStatu", playStatu);
			editor.commit();
		}
	}
	
	/**
	 * 发送出现缓冲进度命令
	 * @param isBufferStatu
	 */
	private void sendBufferStatuButton(boolean isBufferStatu) {
		Intent intent = new Intent(ACTION_AUDIO_VOD_BUFFER_STATU_CONTROL);
		intent.putExtra("bufferStatu", isBufferStatu);
		sendBroadcast(intent);
	}
	
	/**
	 * 发送播放下上一曲集数变化按钮样式广播
	 */
	protected void sendCheckedChanged() {
		Intent intent = new Intent(ACTION_AUDIO_VOD_NEXT_PLAY_CONTROL);
		intent.putExtra("playPosition", playPosition);
		sendBroadcast(intent);
	}
	
	/*
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
	}*/
	
}