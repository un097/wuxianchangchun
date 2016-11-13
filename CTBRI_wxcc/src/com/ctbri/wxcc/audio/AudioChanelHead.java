package com.ctbri.wxcc.audio;

import static com.ctbri.wxcc.audio.AudioLiveFragment.ACTION_AUDIO_PLAYING_CMD;
import static com.ctbri.wxcc.audio.AudioLiveFragment.ACTION_AUDIO_REPLAY;
import static com.ctbri.wxcc.audio.AudioLiveFragment.ACTION_AUDIO_START_STOP_BACKGROUND_PLAY;
import static com.ctbri.wxcc.audio.AudioLiveFragment.ACTION_AUDIO_STOP;
import static com.ctbri.wxcc.audio.AudioLiveFragment.ACTION_AUDIO_STOP_CMD;
import static com.ctbri.wxcc.audio.AudioLiveFragment.ACTION_AUDIO_UPDATE_BUTTON_STATU;
import static com.ctbri.wxcc.audio.AudioLiveFragment.ACTION_AUDIO_UPDATE_VIEWPAGER;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ctbri.wxcc.Constants;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseFragment;
import com.ctbri.wxcc.entity.AudioRecomBean;
import com.ctbri.wxcc.entity.AudioRecomBean.AudioChannel;
import com.ctbri.wxcc.widget.RepeatDrawable;
import com.google.gson.Gson;

public class AudioChanelHead extends BaseFragment {

	private ViewPager mChannels;
	private ChannelAdapter mAdapter;
	private LayoutInflater mInflater;
	private int mTouchSlop;
	private int viewPagerPosition;
	private IChannelChangeListener mChannelChange;
	private AudioChannel mChannel;
	private View mBtn_previous, mBtn_next;
	private ImageButton mBtn_paly;
	private AudioRecomBean bean;
	private boolean playing;
	public static final int MAX_VALUE = 100000;
	private SharedPreferences preference;
	private Editor editor;
	
	@Override
	public void onAttach(Activity activity_) {
		super.onAttach(activity_);
		mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(ViewConfiguration.get(activity));
		if (activity_ instanceof IChannelChangeListener) {
			mChannelChange = (IChannelChangeListener) activity_;
		}
		
		if (preference == null) {
			preference = activity_.getSharedPreferences("audioPlayStatu", Context.MODE_PRIVATE);
			editor = preference.edit();
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		registerReceiver();
		
		playing = preference.getBoolean("playStatu", false);
		if(mChannels != null) {
			mChannels.setCurrentItem(preference.getInt("viewPagerPosition",-1));
			// 同步播放按钮
			updateButtonState();
			
			if(!playing) {
				//发送停止播放命令
				sendStopPlayCmd(); 
			}else {
				//发送播放命令
				if(mChannel != null)
					sendPlayingCmd(mChannel.getChannel_name());
			}
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
	}
	
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
		View view = inflater.inflate(R.layout.audio_channel_head, container, false);
		Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.pindao_kedu);
		View points = view.findViewById(R.id.iv_channel_points);
		RepeatDrawable drawable = new RepeatDrawable(bmp, getResources(), 0.25F);
		points.setBackgroundDrawable(drawable);
//		mBtn_next = view.findViewById(R.id.img_next);
//		mBtn_next.setOnClickListener(mPreAndNextListener);

//		mBtn_previous = view.findViewById(R.id.img_previous);
//		mBtn_previous.setOnClickListener(mPreAndNextListener);

		mBtn_paly = (ImageButton) view.findViewById(R.id.ibtn_play);
		mBtn_paly.setOnClickListener(mItemClick);

		// ScrollView
		mChannels = (ViewPager) view.findViewById(R.id.vp_channels);
		mChannels.setOnTouchListener(mTouchListener);
		mChannels.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				viewPagerPosition = position;
				fireChannelChange(mAdapter.mData.get(position % mAdapter.mData.size()));
				savePlayStatu(viewPagerPosition, playing);
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
		mInflater = inflater;

		return view;
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
		filter.addAction(AudioLiveFragment.ACTION_AUDIO_UPDATE_BUTTON_STATU);
		filter.addAction(ACTION_AUDIO_UPDATE_VIEWPAGER);
		activity.registerReceiver(mPlayingListener, filter);
	}
	
	public void togglePlayState() {
		if (playing) {
			playing = false;
			// 通知后台暂停播放
			stopBackPlay();
		} else {
			playing = true;
			// 在后台开启通知栏并在后台播放
			startBackPlay();
		}
		updateButtonState();
	}
	
	private void startBackPlay() {
		sendStartBackPlay();
		sendAudioLiveStartCmd();
	}

	private void stopBackPlay() {
		sendStopBackPlay();
		sendStopCmd();
		sendAudioLiveStopCmd();
	}

	private void sendAudioLiveStopCmd() {
		Intent stopCmd = new Intent(ACTION_AUDIO_STOP);
		activity.sendBroadcast(stopCmd);
	}
	
	private void sendAudioLiveStartCmd() {
		Intent startCmd = new Intent(ACTION_AUDIO_REPLAY);
		activity.sendBroadcast(startCmd);
	}

	private void sendStopBackPlay() {
		sendStart_StopPlay("stop");
	}
	
	public void sendStartBackPlay() {
		sendStart_StopPlay("start");
	}
	
	private void sendStart_StopPlay(String channel){
		Intent backPlay = new Intent(ACTION_AUDIO_START_STOP_BACKGROUND_PLAY);
		backPlay.putExtra("channel", channel);
		sendIntent(backPlay);
	}
	
	private void sendIntent(Intent intent) {
		if (mChannel != null) {
			intent.putExtra("ChannelName", mChannel.getChannel_name());
			intent.putExtra("Live", mChannel.getLive());
			intent.putExtra("ChannelId", mChannel.getChannel_id());
			intent.putExtra("playStatu", playing);
			intent.putExtra("viewPagerPosition", viewPagerPosition);
			intent.putExtra("AudioRecomBean", bean);
			activity.sendBroadcast(intent);
		}

		savePlayStatu(viewPagerPosition, playing);
	}

	/**
	 * 发送同步停止按钮命令
	 */
	private void sendStopCmd() {
		playing = false;
		updateButtonState();
	}

	/**
	 * 发送停止广播
	 */
	private void sendStopPlayCmd() {
		Intent stop = new Intent(ACTION_AUDIO_STOP_CMD);
		activity.sendBroadcast(stop);
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
	
	private OnClickListener mItemClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			togglePlayState();
		}
	};

	private Runnable mFireChannelChange = new Runnable() {
		@Override
		public void run() {
			mChannelChange.onChange(mChannel,bean);

			//发送后台播放广播
			if (playing)
				sendStartBackPlay();
		}
	};

	/**
	 * 上一项
	 */
	public boolean previous() {
		if (mChannels != null) {
			int currentItem = mChannels.getCurrentItem();
			if (currentItem == 0)
				return false;
			mChannels.setCurrentItem(currentItem - 1);
			return true;
		}
		return false;
	}

	/**
	 * 下一项
	 */
	public boolean next() {
		if (mChannels != null) {
			int currentItem = mChannels.getCurrentItem();
			if (currentItem == mAdapter.getCount() - 1)
				return false;
			mChannels.setCurrentItem(currentItem + 1);
			return true;
		}
		return false;
	}

	private void fireChannelChange(AudioChannel channel) {
		mChannel = channel;
		if (mChannelChange != null)
			mChannels.post(mFireChannelChange);
	}

	private OnTouchListener mTouchListener = new OnTouchListener() {
		private float mLastMotionX;
		private float mLastMotionY;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int action = event.getAction();
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				mLastMotionX = event.getX();
				mLastMotionY = event.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				final float x = event.getX();
				final float xDiff = Math.abs(x - mLastMotionX);
				final float y = event.getY();
				final float yDiff = Math.abs(y - mLastMotionY);
				if (xDiff > mTouchSlop && xDiff > yDiff) {
					v.getParent().requestDisallowInterceptTouchEvent(true);
				}
				break;
			case MotionEvent.ACTION_UP:
				v.performClick();
				break;
			case MotionEvent.ACTION_CANCEL:
				break;
			}

			return false;
		}
	};

	private void init() {
		request(Constants.METHOD_AUDIO_LIVE_RECOM, new RequestCallback() {

			@Override
			public void requestSucc(String json) {
				Gson gson = new Gson();
				bean = gson.fromJson(json, AudioRecomBean.class);
				update(bean, bean.getData().getChannels());
			}

			@Override
			public void requestFailed(int errorCode) {

			}
		});
	}

	private void update(AudioRecomBean bean, List<AudioRecomBean.AudioChannel> channels) {
		mAdapter = new ChannelAdapter(channels);
		mChannels.setAdapter(mAdapter);
		
		if (channels != null && channels.size() > 0) {
			fireChannelChange(channels.get(0));
		}
		
		// 计算位置用于左右无限滑动
		int item = MAX_VALUE / 2 - MAX_VALUE / 2 % mAdapter.mData.size();
		// 记录ViewPager滑动的位置
		viewPagerPosition = item; 
		int position = preference.getInt("viewPagerPosition", -1);
		if (playing) {
			mChannels.setCurrentItem(position);
			// 更新播放按钮
			updateButtonState();
			sendPlayingCmd(mChannel.getChannel_name());
		} else {
			if(position != -1) {
				mChannels.setCurrentItem(position);
			}else {
				mChannels.setCurrentItem(viewPagerPosition);
			}
		}
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
				playing = true;
			} else if (AudioLiveFragment.ACTION_AUDIO_STOP_CMD.equals(action)) {
				playing = false;
			} else if(ACTION_AUDIO_UPDATE_BUTTON_STATU.equals(action)){
//				playing = preference.getBoolean("playStatu", false);
				playing = intent.getBooleanExtra("change",false);
			}else if(ACTION_AUDIO_UPDATE_VIEWPAGER.equals(action)) {
				if(mChannels != null) {
					mChannels.setCurrentItem(preference.getInt("viewPagerPosition", -1));
				}
			}
			updateButtonState();
		}
	};

	private void updateButtonState() {
		
		if(mBtn_paly != null){
			if (playing) {
				mBtn_paly.setImageResource(R.drawable.audio_status_bar_pause_selector);
			} else {
				mBtn_paly.setImageResource(R.drawable.audio_status_bar_play_selector);
			}
		}
	}

	class ChannelAdapter extends PagerAdapter {
		private List<AudioChannel> mData;

		public ChannelAdapter(List<AudioRecomBean.AudioChannel> channels) {
			this.mData = channels;
		}

		@Override
		public int getCount() {
			return MAX_VALUE;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View view = mInflater.inflate(R.layout.audio_channel_head_item, container, false);
			view.setOnClickListener(mItemClick);
			TextView tv_code = (TextView) view.findViewById(R.id.tv_channel_code);
			TextView tv_program = (TextView) view.findViewById(R.id.tv_program_name);
			// TextView tv_nickname = (TextView)
			// view.findViewById(R.id.tv_channel_nickname);
			TextView tv_name = (TextView) view.findViewById(R.id.tv_channel_name);

			// AudioChannel data = mData.get(position);
			AudioChannel data = mData.get(position % mAdapter.mData.size());
			tv_code.setText(data.getChannel_code());
			tv_name.setText(data.getChannel_name());

			// tv_nickname.setText(data.getProgram_id());
			tv_program.setText(data.getProgram_name());

			container.addView(view);
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
	}

	public static interface IChannelChangeListener {
		void onChange(AudioChannel channel,AudioRecomBean bean);
	}
}
