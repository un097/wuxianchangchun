package com.ctbri.wxcc.audio;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;
import cn.ffcs.wisdom.city.common.MenuType;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.home.bo.AdvertisingBo;
import cn.ffcs.wisdom.city.home.datamgr.AdvertisingMgr;
import cn.ffcs.wisdom.city.home.entity.AdvertisingEntity;
import cn.ffcs.wisdom.city.home.entity.AdvertisingEntity.Advertising;
import cn.ffcs.wisdom.city.sqlite.model.MenuItem;
import cn.ffcs.wisdom.city.sqlite.model.MenuItem.MenuTypeMedia;
import cn.ffcs.wisdom.city.utils.AppMgrUtils;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;

import com.ctbri.comm.util._Utils;
import com.ctbri.wxcc.Constants;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.audio.AudioChanelHead.IChannelChangeListener;
import com.ctbri.wxcc.community.BaseActivity;
import com.ctbri.wxcc.community.Constants_Community;
import com.ctbri.wxcc.entity.AudioRecomBean;
import com.ctbri.wxcc.entity.AudioRecomBean.AudioChannel;
import com.ctbri.wxcc.entity.AudioVodBean;
import com.ctbri.wxcc.entity.AudioVodBean.AudioVodGroup;
import com.ctbri.wxcc.widget.ImageLooperFragment;
import com.ctbri.wxcc.widget.ImageLooperFragment.Pic;
import com.ctbri.wxcc.widget.ImageLooperFragment_new;
import com.ctbri.wxcc.widget.PlayConformDialog;
import com.ctbri.wxcc.widget.PullToExpandContainer;
import com.ctbri.wxcc.widget.PullToExpandContainer.OnExpandListener;
import com.google.gson.Gson;

/**
 * 音频
 * 
 * @author Administrator
 *
 */
public class AudioMainActivity extends BaseActivity implements
		IChannelChangeListener {

	private ViewGroup mContentGroup, mVodGroups;
	private PullToExpandContainer mExpandContainer;
	private LayoutInflater inflater;
	private static final String TAG_CHANNEL_DETAIL = "live_channel_details";
	private View mStatusBar;
	private ImageView iv_play_icon;
	private View mMainActionBar, mLiveActionBar;
	private ActionbarAnimation mActionbarAnim;

	private boolean mFirstOpen = false;
	private static final String TAG_AUDIO_PLAYER_STATUS_BAR = "audio_player_status_bar";
	private Interpolator accelerator = new AccelerateInterpolator();
	private Interpolator decelerator = new DecelerateInterpolator();
	private Handler mHandler = new MyHandler(this);
	private AudioChannel mCurrentChannel;
	Activity mActivity = AudioMainActivity.this;
	private AudioRecomBean mBean;
	/**
	 * 更新channel
	 */
	public static final int UPDATE_LIVE_CHANNEL = 1001;
	/**
	 * 更新延迟时间
	 */
	private static final int UPDATE_DELAY = 1500;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_audio_main);

		inflater = LayoutInflater.from(this);

		mExpandContainer = (PullToExpandContainer) findViewById(R.id.pullExpandView1);
		mContentGroup = (ViewGroup) inflater.inflate(
				R.layout.audio_main_content_layout, null, false);
		mVodGroups = (ViewGroup) mContentGroup
				.findViewById(R.id.ll_audio_vod_container);
		View header = inflater.inflate(R.layout.audio_head_layout, null);
		View expand = inflater.inflate(R.layout.audio_expand_layout, null);
		iv_play_icon = (ImageView) expand.findViewById(R.id.iv_play);
		mExpandContainer.setHeaderView(header, expand);
		mExpandContainer.setContentView(mContentGroup);

		int mScrollHeight = getResources().getDimensionPixelSize(
				R.dimen.audio_live_head_channels_height);
		mExpandContainer.setHeadScrollHeight(mScrollHeight);
		mExpandContainer.setExpandListener(mExpandListener);

		mStatusBar = inflater.inflate(R.layout.audio_status_bar_container,
				mExpandContainer, false);
		mStatusBar.setVisibility(View.GONE);
		mExpandContainer.setFixedBar(mStatusBar);

		mMainActionBar = findViewById(R.id.action_bar_main);
		mLiveActionBar = findViewById(R.id.action_bar_live);

		mActionbarAnim = new AnimatorAnimation();

		if (_Utils.isWifiTriggerOpen(this)) {
			new PlayConformDialog().show(getSupportFragmentManager(),
					"play_conform");
		}

		init();
	}

	static class MyHandler extends Handler {
		public MyHandler(AudioMainActivity t) {
			target = new WeakReference<AudioMainActivity>(t);
		}

		private WeakReference<AudioMainActivity> target;

		@Override
		public void handleMessage(Message msg) {
			AudioMainActivity act = target.get();
			if (act == null)
				return;
			switch (msg.what) {
			case UPDATE_LIVE_CHANNEL:
				act.updateChannel();
				break;
			}
		}
	}

	/**
	 * expand layout 拉开之后触发的事件
	 */
	private OnExpandListener mExpandListener = new OnExpandListener() {
		@Override
		public void onExpand() {
			AudioLiveFragment live = (AudioLiveFragment) getSupportFragmentManager()
					.findFragmentByTag(TAG_CHANNEL_DETAIL);
			if (live == null) {
				String mChannelId = null;
				if (mCurrentChannel != null)
					mCurrentChannel.getChannel_id();
				live = AudioLiveFragment.newInstance(mChannelId);
				getSupportFragmentManager()
						.beginTransaction()
						.replace(R.id.frame_live_content, live,
								TAG_CHANNEL_DETAIL).commit();
			} else {
				FragmentTransaction ft = getSupportFragmentManager()
						.beginTransaction();
				ft.show(live).setTransition(
						FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				ft.commitAllowingStateLoss();
			}
			live.start();
			iv_play_icon.setVisibility(View.GONE);
			mActionbarAnim.hide();
		}

		@Override
		public void onClose() {
			AudioLiveFragment live = (AudioLiveFragment) getSupportFragmentManager()
					.findFragmentByTag(TAG_CHANNEL_DETAIL);
			if (live != null) {

				getSupportFragmentManager().beginTransaction().hide(live)
						.commit();
			}
			mActionbarAnim.show();
		}

		public void onPreClosed() {
			iv_play_icon.setVisibility(View.VISIBLE);
		}
	};

	private boolean onBack() {
		if (mExpandContainer.isExpand()) {
			mExpandContainer.closeExpand();
			return true;
		}
		return false;
	}

	public void onBackPressed() {
		if (onBack()) {
			return;
		}
		super.onBackPressed();
	};

	private void init() {

		TextView tvTitle = (TextView) mMainActionBar
				.findViewById(R.id.action_bar_title);
		tvTitle.setText(R.string.title_audio);
		// actionbar 返回按钮 按下事件
		ImageView iv_main_back = (ImageView) mMainActionBar
				.findViewById(R.id.action_bar_left_btn);
		iv_main_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!onBack())
					finish();
			}
		});

		// 音频直播 actionbar 标题
		((TextView) mLiveActionBar.findViewById(R.id.action_bar_title))
				.setText(R.string.title_audio_broadcast);
		// 音频直播 返回主页面
		ImageView img_btn = (ImageView) mLiveActionBar
				.findViewById(R.id.action_bar_left_btn);
		img_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBack();
			}
		});
		// 音频直播 分享
		ImageView img_share = (ImageView) mLiveActionBar
				.findViewById(R.id.action_bar_right_btn);
		img_share.setImageResource(R.drawable.share_button_selector);
		img_share.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mCurrentChannel == null)
					return;
				String title = mCurrentChannel.getChannel_name();
				String content = getString(R.string.share_content_audio, title);
				_Utils.shareAndCheckLogin(AudioMainActivity.this, title,
						Constants_Community.APK_DOWNLOAD_URL, content,
						_Utils.getDefaultAppIcon(AudioMainActivity.this));
			}
		});
		// 音频搜索控件，点击事件
		findViewById(R.id.tv_search).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent toSearch = new Intent(AudioMainActivity.this,
						AudioSearchActivity.class);
				startActivity(toSearch);
			}
		});

		ImageView iv_right_btn = ((ImageView) mMainActionBar
				.findViewById(R.id.action_bar_right_btn));
		iv_right_btn
				.setImageResource(R.drawable.video_favorite_button_selector);
		iv_right_btn.setOnClickListener(mFavoriteBtnListener);

		initImageLooper();

		initAudioVod();

		initLiveFragment();
	}

	private void initLiveFragment() {
		AudioLiveFragment live = AudioLiveFragment.newInstance(null);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.frame_live_content, live, TAG_CHANNEL_DETAIL)
				.hide(live).commitAllowingStateLoss();
	}

	private void initImageLooper() {
//		ArrayList<Pic> pics = new ArrayList<Pic>();
		// pics.add(new Pic("天上再没有这样的可人儿",
		// "http://img31.mtime.cn/mg/2015/02/01/094117.53566720.jpg"));
		// pics.add(new Pic("大胖子是个什么",
		// "http://img31.mtime.cn/mg/2015/02/02/093822.40293236.jpg"));
		// pics.add(new Pic("何以笙箫默",
		// "http://img31.mtime.cn/mg/2015/01/31/092710.37057010.jpg"));
		// pics.add(new Pic("从新婚蜜月到导演片场",
		// "http://img31.mtime.cn/mg/2015/01/30/085343.82401041.jpg"));

//		ImageLooperFragment_new fragment = ImageLooperFragment_new
//				.newInstance((ArrayList<AdvertisingEntity.Advertising>) list);
//		getSupportFragmentManager().beginTransaction()
//				.replace(R.id.frame_audio_looper, fragment).commit();

//		String cityCode = MenuMgr.getInstance().getCityCode(mActivity);
		AdvertisingEntity entity = AdvertisingMgr.getInstance()
				.getAdvertisingEntity_withCode("audio");
		if (entity == null) {
			AdvertisingBo.getInstance().getAdvertising_new(mActivity,
					new AdvertisingCallBack(), "2");
		} else {
			showAdvertising(entity);
		}

	}

	private void showAdvertising(AdvertisingEntity adEntity) {
		List<Advertising> list = adEntity.getAdvs();
		android.util.Log.e("sb", list.size()
				+ "                            音频");
		ImageLooperFragment_new fragment = ImageLooperFragment_new
				.newInstance((ArrayList<AdvertisingEntity.Advertising>) list);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.frame_audio_looper, fragment).commit();
		fragment.setItemClickListener(new ImageLooperFragment_new.OnItemClickListener() {

			@Override
			public void onClick(View v, Object data) { // TODO
				Advertising advertising = (Advertising) data;
				if (!advertising.getItem_type().equals("imgAdv")) {
					MenuItem item = new MenuItem();
					item.setMenuType(MenuType.WAP);
					item.setMenuName(advertising.getTitle());
					item.setUrl(advertising.getOfficial_link());
					item.media = MenuTypeMedia.bannerWapShare; // 首页Banner点击跳转到wap，右上角显示分享按钮
					AppMgrUtils.launchAPP(mActivity, item, R.string.home_name);
				}
			}
		});
	}

	/**
	 * 广告回调
	 */

	class AdvertisingCallBack implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp response) {
			if (response.isSuccess()) {
				AdvertisingEntity adEntity = (AdvertisingEntity) response
						.getObj();
				AdvertisingMgr.getInstance().setAdvertisingEntity_withCode("audio",
						adEntity);
				showAdvertising(adEntity);
			} else {
			}
		}

		@Override
		public void progress(Object... obj) {
		}

		@Override
		public void onNetWorkError() {
		}
	}

	private OnClickListener mFavoriteBtnListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent toFavorite = new Intent(AudioMainActivity.this,
					AudioFavoriteActivity.class);
			startActivity(toFavorite);
		}
	};

	/**
	 * 获取音频点播数据
	 */
	private void initAudioVod() {
		request(Constants.METHOD_AUDIO_VOD_RECOM, new RequestCallback() {
			@Override
			public void requestSucc(String json) {
				Gson gson = new Gson();
				AudioVodBean vodBean = gson.fromJson(json, AudioVodBean.class);
				if (vodBean.getData() != null
						&& vodBean.getData().getVod_group() != null) {
					fillAudioVodData(vodBean.getData().getVod_group());
				}
			}

			@Override
			public void requestFailed(int errorCode) {

			}
		}, null);
	}

	/**
	 * 填充数据
	 * 
	 * @param groups
	 */
	private void fillAudioVodData(List<AudioVodGroup> groups) {
		int count = groups.size();
		for (int i = 0; i < count; i++) {
			AudioVodGroup group = groups.get(i);
			// 一个个的正方形数据（图片）
			AudioVodWidget widget = (AudioVodWidget) inflater.inflate(
					R.layout.audio_vod_circle_widget, mVodGroups, false);
			mVodGroups.addView(widget);
			widget.update(group, group.getVod_list(), 4);
		}
	}

	@Override
	public void onChange(AudioChannel channel, AudioRecomBean bean) {
		mCurrentChannel = channel;
		mBean = bean;
		mHandler.removeMessages(UPDATE_LIVE_CHANNEL);
		mHandler.sendEmptyMessageDelayed(UPDATE_LIVE_CHANNEL, UPDATE_DELAY);
	}

	private void updateChannel() {
		AudioLiveFragment live = (AudioLiveFragment) getSupportFragmentManager()
				.findFragmentByTag(TAG_CHANNEL_DETAIL);
		if (live != null)
			live.update(mCurrentChannel, mBean);
	}

	private class AnimatorAnimation implements ActionbarAnimation {
		private int mHeight;

		@Override
		public void hide() {
			mHeight = mMainActionBar.getHeight();
			ObjectAnimator obj = ObjectAnimator.ofInt(mMainActionBar, "top",
					mMainActionBar.getTop(), mHeight * -1);
			obj.setDuration(300);
			obj.setInterpolator(accelerator);
			obj.addListener(new AnimatorListenerAdapter() {
				public void onAnimationEnd(Animator animation) {
					mMainActionBar.setVisibility(View.GONE);
				}
			});
			obj.start();
			mLiveActionBar.setVisibility(View.VISIBLE);
		}

		@Override
		public void show() {
			ObjectAnimator obj = ObjectAnimator.ofInt(mMainActionBar, "top",
					mHeight * -1, 0);
			obj.setDuration(300);
			obj.setInterpolator(decelerator);
			obj.addListener(new AnimatorListenerAdapter() {
				public void onAnimationEnd(Animator animation) {
					mLiveActionBar.setVisibility(View.GONE);
				}
			});
			obj.start();
			mMainActionBar.setVisibility(View.VISIBLE);

		}

	}

	public static interface ActionbarAnimation {
		void hide();

		void show();
	}
}
