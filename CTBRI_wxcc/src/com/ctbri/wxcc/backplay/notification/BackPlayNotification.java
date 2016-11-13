package com.ctbri.wxcc.backplay.notification;

import static com.ctbri.wxcc.audio.AudioLiveFragment.ACTION_AUDIO_LEFT_RIGHT_BACKGROUND_PLAY;
import static com.ctbri.wxcc.audio.AudioLiveFragment.ACTION_AUDIO_LEFT_RIGHT_PLAY_VOD;
import static com.ctbri.wxcc.audio.AudioLiveFragment.ACTION_AUDIO_REPLAY_UPDATE_BUTTON;
import static com.ctbri.wxcc.audio.AudioLiveFragment.ACTION_AUDIO_START_STOP_BACKGROUND_PLAY;
import static com.ctbri.wxcc.audio.AudioLiveFragment.ACTION_AUDIO_STOP_REPLAY_PLAY_VOD;
import static com.ctbri.wxcc.audio.AudioLiveFragment.ACTION_AUDIO_STOP_UPDATE_BUTTON;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.RemoteViews;

import com.ctbri.wxcc.R;
import com.ctbri.wxcc.entity.AudioRecomBean;

/**
 * 后台状态通知栏
 * 
 * @author 张政/2015-7-7
 */
public class BackPlayNotification{
	private static final String STATUS_LEFT_CLICK_ACTION = "left";
	private static final String STATUS_RIGHT_CLICK_ACTION = "right";
	private static final String STATUS_PUASE_CLICK_ACTION = "replay";
	
	private static int NOTIFY_ID = 1000;
	private static int VOD_NOTIFY_ID = 1001;
	
	private NotificationManager mNotifManager,vod_mNotifmanager;
	private Notification mNotif,vod_mNotif;
	private RemoteViews remoteViews,vod_remoteViews;
	private static BackPlayNotification backPlayNotif = null;
	private boolean playing,vod_playing;
	private String mLive,vod_Live;
	private String mChannelId;
	private String mChannelName,vod_ChannelName;
	private int mViewPagerPosition;
	private AudioRecomBean mBean;
	private boolean isVodPlay;
	private SharedPreferences preference;
	private Editor editor;
	
	private BackPlayNotification() {
	}

	public static BackPlayNotification getInstance() {
		if (backPlayNotif == null) {
			synchronized (BackPlayNotification.class) {
				if (backPlayNotif == null) {
					backPlayNotif = new BackPlayNotification();
				}
			}
		}
		return backPlayNotif;
	}
	
	/**
	 * 显示音频播放后台状态通知栏
	 * 
	 * @param mContext
	 */
	@SuppressLint("NewApi")
	public void showNotif(Context mContext,String channelName,boolean playStatu,String live) {
		
		if(mNotifManager != null) {
			mNotifManager.cancel(NOTIFY_ID);
		}
		isVodPlay = true;
		vod_playing = playStatu;
		vod_Live = live;
		vod_ChannelName = channelName;
		
		if(vod_mNotifmanager == null) {
			vod_mNotifmanager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		}
		
		// 点击通知栏跳转到当前播放页
//		Intent intent = new Intent(mContext,MediaPlayerActivity.class);
//		PendingIntent pi = PendingIntent.getActivity(mContext, 0, intent, 0);
		
		// 创建RemoteViews对象
		if(vod_remoteViews == null) {
			vod_remoteViews = new RemoteViews(mContext.getPackageName(),R.layout.audio_back_paly_layout);
		}
		
		// 设置控件的监听事件
		
		vod_remoteViews.setOnClickPendingIntent(R.id.iv_left, PendingIntent
				.getBroadcast(mContext, 0,
						new Intent(STATUS_LEFT_CLICK_ACTION), 0));
		vod_remoteViews.setOnClickPendingIntent(R.id.iv_right, PendingIntent
				.getBroadcast(mContext, 0,
						new Intent(STATUS_RIGHT_CLICK_ACTION), 1));
		vod_remoteViews.setOnClickPendingIntent(R.id.iv_pause, PendingIntent
				.getBroadcast(mContext, 0,
						new Intent(STATUS_PUASE_CLICK_ACTION), 2));
		
		if(playStatu)
			vod_remoteViews.setImageViewResource(R.id.iv_pause, R.drawable.cyberplayer_stop_media_disable);
		else 
			vod_remoteViews.setImageViewResource(R.id.iv_pause, R.drawable.cyberplayer_play_media_disable);
		
		vod_remoteViews.setTextViewText(R.id.tv_title,channelName);
		
		if(vod_mNotif == null) {
			vod_mNotif = new Notification.Builder(mContext)
					.setSmallIcon(R.drawable.ic_launcher)
					.setTicker("切到后台播放音频")
					.setContent(vod_remoteViews) // 在notification中设置content view
//					 .setContentIntent(pi)
					.build();
		}

		vod_mNotif.flags |= Notification.FLAG_ONGOING_EVENT;//FLAG_AUTO_CANCEL
		vod_mNotifmanager.notify(VOD_NOTIFY_ID, vod_mNotif);
		
		registerReceiver(mContext);
	}

	/**
	 * 显示电台播放后台状态通知栏
	 * 
	 * @param mContext
	 */
	@SuppressLint("NewApi")
	public void showNotif(Context mContext,String channelName,boolean playStatu,String live,String channelId,int viewPagerPosition,AudioRecomBean bean) {
		if(vod_mNotifmanager != null) {
			vod_mNotifmanager.cancel(VOD_NOTIFY_ID);
		}
		isVodPlay = false;
		playing = playStatu;
		mLive = live;
		mChannelId = channelId;
		mChannelName = channelName;
		mViewPagerPosition = viewPagerPosition;
		mBean = bean;
		
		if(mNotifManager == null) {
			mNotifManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		}
		
		// 创建RemoteViews对象
		if(remoteViews == null) {
			remoteViews = new RemoteViews(mContext.getPackageName(),R.layout.audio_back_paly_layout);
		}
		
		// 设置控件的监听事件
		
		remoteViews.setOnClickPendingIntent(R.id.iv_left, PendingIntent
				.getBroadcast(mContext, 0,
						new Intent(STATUS_LEFT_CLICK_ACTION), 0));
		remoteViews.setOnClickPendingIntent(R.id.iv_right, PendingIntent
				.getBroadcast(mContext, 0,
						new Intent(STATUS_RIGHT_CLICK_ACTION), 1));
		remoteViews.setOnClickPendingIntent(R.id.iv_pause, PendingIntent
				.getBroadcast(mContext, 0,
						new Intent(STATUS_PUASE_CLICK_ACTION), 2));
		
		if(playStatu)
			remoteViews.setImageViewResource(R.id.iv_pause, R.drawable.cyberplayer_stop_media_disable);
		else 
			remoteViews.setImageViewResource(R.id.iv_pause, R.drawable.cyberplayer_play_media_disable);
		
		remoteViews.setTextViewText(R.id.tv_title,channelName);
		
		if(mNotif == null) {
			mNotif = new Notification.Builder(mContext)
					.setSmallIcon(R.drawable.ic_launcher)
					.setTicker(playing?"切到后台播放音频":"暂停音频")
					.setContent(remoteViews) // 在notification中设置content view
//					.setContentIntent(pi)
					.build();
		}

		mNotif.flags |= Notification.FLAG_ONGOING_EVENT;//FLAG_AUTO_CANCEL
		mNotifManager.notify(NOTIFY_ID, mNotif);

		registerReceiver(mContext);
	}
	
	/**
	 * 注册广播
	 * @param mContext
	 */
	private void registerReceiver(Context mContext) {
		IntentFilter filter = new IntentFilter();
		filter.addAction(STATUS_LEFT_CLICK_ACTION);
		filter.addAction(STATUS_RIGHT_CLICK_ACTION);
		filter.addAction(STATUS_PUASE_CLICK_ACTION);
		mContext.registerReceiver(onClickReceiver, filter);
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
	 * 注销广播
	 * @param mContext
	 */
	public void unregisterReceiver(Context mContext) {
		mContext.unregisterReceiver(onClickReceiver);
	}

	BroadcastReceiver onClickReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (preference == null) {
				preference = context.getSharedPreferences("audioPlayStatu", Context.MODE_PRIVATE);
				editor = preference.edit();
			}
			
			if(intent.getAction().equals(STATUS_LEFT_CLICK_ACTION)){// 上频道
				if(isVodPlay){
					vod_playing = true;
					sendPlayVodCmd(context,ACTION_AUDIO_LEFT_RIGHT_PLAY_VOD,vod_playing,"left");
					return ;
				}
				
				playing = true;
				mViewPagerPosition = preference.getInt("viewPagerPosition",-1) -1;
				
				savePlayStatu(mViewPagerPosition, playing);
				sendBackgroundPlayCMD(context,ACTION_AUDIO_LEFT_RIGHT_BACKGROUND_PLAY,playing,"left");
			}else if(intent.getAction().equals(STATUS_RIGHT_CLICK_ACTION)){// 下频道
				if(isVodPlay){
					vod_playing = true;
					sendPlayVodCmd(context,ACTION_AUDIO_LEFT_RIGHT_PLAY_VOD,vod_playing,"right");
					return ;
				}
				
				playing = true;
				mViewPagerPosition = preference.getInt("viewPagerPosition",-1) +1;
				
				savePlayStatu(mViewPagerPosition, playing);
				sendBackgroundPlayCMD(context,ACTION_AUDIO_LEFT_RIGHT_BACKGROUND_PLAY,playing,"right");
			}else if(intent.getAction().equals(STATUS_PUASE_CLICK_ACTION)){// 播放和暂停
				if(isVodPlay){
					if(vod_playing) {
						vod_playing = false;
					}else {
						vod_playing = true;
					}
					sendPlayVodCmd(context,ACTION_AUDIO_STOP_REPLAY_PLAY_VOD,vod_playing,"stop_replay");
					return ;
				}
				
				if(playing) {
					playing = false;
					
					savePlayStatu(mViewPagerPosition, playing);
					sendBackgroundPlayCMD(context,ACTION_AUDIO_START_STOP_BACKGROUND_PLAY,playing,"stop");
					sendAudioLiveButtonCmd(context,ACTION_AUDIO_STOP_UPDATE_BUTTON);
				} else { 
					playing = true;
					
					savePlayStatu(mViewPagerPosition, playing);
					sendBackgroundPlayCMD(context,ACTION_AUDIO_START_STOP_BACKGROUND_PLAY,playing,"start");
					sendAudioLiveButtonCmd(context,ACTION_AUDIO_REPLAY_UPDATE_BUTTON);
				}
			}
		}
	};
	
	private void sendAudioLiveButtonCmd(Context mContext,String action) {
		Intent updateButtonCmd = new Intent(action);
		mContext.sendBroadcast(updateButtonCmd);
	}
	
	protected void sendPlayVodCmd(Context context, String action, boolean playStatu,String channel) {
		Intent intentAct = new Intent();
		intentAct.putExtra("live",vod_Live);
		intentAct.putExtra("title", vod_ChannelName);
		intentAct.putExtra("vod_playing", playStatu);
		intentAct.putExtra("channel", channel);
		intentAct.setAction(action);
		context.sendBroadcast(intentAct);
	}

	private void sendBackgroundPlayCMD(Context mContext,String action,boolean playing,String channel) {
		Intent intentAct = new Intent();
		intentAct.putExtra("Live",mLive);
		intentAct.putExtra("ChannelId", mChannelId);
		intentAct.putExtra("ChannelName", mChannelName);
		intentAct.putExtra("playStatu", playing);
		intentAct.putExtra("viewPagerPosition", mViewPagerPosition);
		intentAct.putExtra("AudioRecomBean", mBean);
		intentAct.putExtra("channel", channel);
		intentAct.setAction(action);
		mContext.sendBroadcast(intentAct);
	}
}