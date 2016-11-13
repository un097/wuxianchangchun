package com.ctbri.wxcc.media;

import static com.ctbri.wxcc.audio.AudioLiveFragment.ACTION_AUDIO_ALLSTOP_PLAY;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnBufferingUpdateListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnCompletionListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnErrorListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnPreparedListener;
import tv.danmaku.ijk.media.widget.VideoView;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ctbri.comm.util._Utils;
import com.ctbri.wxcc.Constants;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseActivity;
import com.ctbri.wxcc.community.BaseFragment;
import com.ctbri.wxcc.community.Constants_Community;
import com.ctbri.wxcc.entity.MediaShortGroupBean;
import com.ctbri.wxcc.entity.MediaShortGroupBean.ShortVideo;
import com.ctbri.wxcc.entity.MediaShortGroupBean.ShortVideoGroup;
import com.ctbri.wxcc.widget.SmoothMenu;
import com.ctbri.wxcc.widget.SmoothMenu.OnSmoothMenuItemSelectListener;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

public class VideoShortFragment extends BaseFragment {

	private LayoutInflater inflater;
	private LinearLayout video_container;
	private static final int ANIM_DURATION = 400;
	private ShortVideoHolder mCurrentVideo;
	public static final long PLAY_BUTTON_DELAY_TIME = 2000;
	private List<ShortVideoHolder> holderLists = new ArrayList<ShortVideoHolder>();
	private final String TAG = "ShortVideoHolder";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.inflater = inflater;
		video_container = new LinearLayout(activity);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		video_container.setLayoutParams(lp);
		video_container.setOrientation(LinearLayout.VERTICAL);
		video_container.setDividerDrawable(getResources().getDrawable(R.drawable.ic_media_vod_divider));
		video_container.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);

		return video_container;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getData();
	}

	@Override
	public void onPause() {
		super.onPause();
		for (ShortVideoHolder holder : holderLists) {
			holder.mViewImage.setVisibility(View.VISIBLE);
			holder.pause(false);
		}
	}

	private void getData() {
		request(Constants.METHOD_VIDEO_SHORT, new RequestCallback() {

			@Override
			public void requestSucc(String json) {
				Gson gson = new Gson();
				MediaShortGroupBean bean = gson.fromJson(json, MediaShortGroupBean.class);
				fillData(bean);
			}

			@Override
			public void requestFailed(int errorCode) {

			}
		});
	}

	/**
	 * 模拟 VideoView onClick 事件
	 */
	class ParentViewClick implements OnClickListener {
		public ParentViewClick(ShortVideoHolder holder) {
			this.mHolder = holder;
		}

		private ShortVideoHolder mHolder;
		public long mToken;

		public void resetToken() {
			mToken = SystemClock.uptimeMillis();
		}

		@Override
		public void onClick(View v) {
			if (SystemClock.uptimeMillis() < mToken + PLAY_BUTTON_DELAY_TIME)
				return;
			if (mHolder != null && mHolder.mVideoView.isPlaying()) {
				mHolder.pause(true);
			}
		}
	};

	private void fillData(MediaShortGroupBean bean) {
		if (bean != null && bean.getData() != null) {
			List<ShortVideoGroup> groups = bean.getData().getShort_group();
			if (groups != null) {
				for (ShortVideoGroup group : groups) {
					// 创建 Container Group
					View mGroup = inflater.inflate(R.layout.media_video_short_list, video_container, false);

					ViewGroup mGroupList = (ViewGroup) mGroup.findViewById(R.id.ll_short_video_container);
					TextView tv_category_name, tv_more;
					tv_category_name = (TextView) mGroup.findViewById(R.id.tv_category_tittle);
					// 更多 按钮
					tv_more = (TextView) mGroup.findViewById(R.id.tv_category_more);
					tv_more.setOnClickListener(new OpenVideoCategoryListener(group.getGroup_id(), group.getGroup_name()));

					tv_category_name.setText(group.getGroup_name());

					List<ShortVideo> list = group.getShort_list();

					if (list != null)
						for (ShortVideo video : list) {
							SmoothMenu mSmoothMenu = new SmoothMenu(activity);
							ShortVideoHolder mVideoHolder = new ShortVideoHolder();
							holderLists.add(mVideoHolder);
							mSmoothMenu.setTag(video);
							View v = inflater.inflate(R.layout.media_video_short_item, mSmoothMenu, false);
							mVideoHolder.mRoot = v;
							mVideoHolder.mVideoViewParentClicker = new ParentViewClick(mVideoHolder);
							// 视频标题
							TextView tv_title = (TextView) v.findViewById(R.id.tv_title);
							tv_title.setText(video.getShort_name());

							// 默认图片
							mVideoHolder.mViewImage = (ImageView) v.findViewById(R.id.iv_video_img);
							ImageLoader.getInstance().displayImage(video.getShort_url().trim(), mVideoHolder.mViewImage, _Utils.DEFAULT_DIO);

							// video view
							mVideoHolder.mVideoView = (VideoView) v.findViewById(R.id.sv_video);

							// 视频信息视图
							mVideoHolder.mInfoView = v.findViewById(R.id.short_video_container);
							// mVideoHolder.mInfoView.setOnClickListener(mToVodDetail);
							mVideoHolder.mInfoView.setTag(video.getShort_id());
							// 暂停按钮
							mVideoHolder.mPlayButton = (ImageButton) v.findViewById(R.id.ibtn_paly);
							mVideoHolder.mPlayButton.setOnClickListener(new PauseButtonListener(mVideoHolder));

							mVideoHolder.mLoading = (ProgressBar) v.findViewById(R.id.progress_loading);
							mVideoHolder.video_id = video.getShort_id();
							mVideoHolder.video_url = video.getPlay_url();
							mVideoHolder.mViewImage.setOnClickListener(new PlayVideoListener(mVideoHolder));

							// 时长
							TextView tv_time = (TextView) v.findViewById(R.id.tv_time);
							tv_time.setText(video.getShort_time());
							// 播放次数
							TextView tv_play_count = (TextView) v.findViewById(R.id.tv_play_count);
							tv_play_count.setText(video.getPlay_count());

							// 点赞数量
							TextView tv_zanView = (TextView) v.findViewById(R.id.tv_zan_count);
							tv_zanView.setText(video.getPraise_count());

							mSmoothMenu.setOnSmoothMenuItemSelected(new MenuItemSelectedListener(tv_zanView));

							mSmoothMenu.setContentView(v);

							mGroupList.addView(mSmoothMenu);
							// 添加底部 空白
							// inflater.inflate(R.layout.media_line,
							// video_container);
						}
					video_container.addView(mGroup);
				}
			}

		}
	}

	// private OnClickListener mToVodDetail = new OnClickListener() {
	// @Override
	// public void onClick(View v) {
	//
	// String vod_id = v.getTag().toString();
	// Intent toDetail = new Intent(activity, MediaPlayerActivity.class);
	// toDetail.putExtra("type_id", VodVideoListFragmet.TYPE_VOD);
	// toDetail.putExtra("vod_id", vod_id);
	// startActivity(toDetail);
	// }
	// };

	/**
	 * 分享与点赞事件
	 */
	private static class MenuItemSelectedListener implements OnSmoothMenuItemSelectListener {
		private TextView tv_like_count;

		public MenuItemSelectedListener(TextView tv_count) {
			this.tv_like_count = tv_count;
		}

		@Override
		public void onSmoothMenuItemSelected(SmoothMenu sm, int type) {

			ShortVideo video = (ShortVideo) sm.getTag();
			if (video == null)
				return;
			switch (type) {
				case right:
					Activity context = (Activity) sm.getContext();
					String title = video.getShort_name();
					String content = context.getString(R.string.share_content_video, title);
					_Utils.shareAndCheckLogin(context, title, Constants_Community.APK_DOWNLOAD_URL, content, _Utils.getDefaultAppIcon(context));
					break;
				case left:
					MediaUtils.like(video.getShort_id(), VodVideoListFragmet.TYPE_VOD, new MediaUtils.ILikeSuccess() {
						@Override
						public void onSuccess(String total) {
							tv_like_count.setText(total);
						}
					}, (BaseActivity) sm.getContext());
					break;
			}
		}
	};

	static class StopThread extends Thread {
		private ShortVideoHolder mHolder;

		public StopThread(ShortVideoHolder holder) {
			mHolder = holder;
		}

		@Override
		public void run() {
			mHolder.mLock.lock();
			mHolder.mVideoView.stopPlayback();
			mHolder.mLock.unlock();
		}
	}

	static class ShortVideoHolder {

		String video_url;
		long playOffset = 0;
		String video_id;
		boolean isPlaying;
		ImageView mViewImage;
		VideoView mVideoView;
		ImageButton mPlayButton;
		View mInfoView;
		View mLoading;
		View mRoot;
		ReentrantLock mLock = new ReentrantLock();
		ParentViewClick mVideoViewParentClicker;

		protected void pause(boolean isShowBtn) {
			if (mVideoView.isPlaying() && isShowBtn)
				playOffset = mVideoView.getCurrentPosition();
			else
				playOffset = 0;
			new StopThread(this).start();
			// 把播放的 uri 置空, 解决在新 sruface 销毁后(新打开页面之后返回)，又重新创建时，自动播放的bug.
			mVideoView.setVideoURI(null);

			showInfoView(mInfoView);

			if(playOffset==0) {
				mViewImage.setVisibility(View.VISIBLE);
			}
//			 mViewImage.setImageBitmap(_Utils.buildVideoFrame(activity_,
//			 Uri.parse(video_url), playOffset));
//			 mVideoView.setVisibility(View.GONE);
			mLoading.setVisibility(View.GONE);
			mPlayButton.setImageResource(R.drawable.media_palyer_play_button_selector);
			mPlayButton.setVisibility(isShowBtn ? View.VISIBLE : View.GONE);
			if (mRoot != null) {
				mRoot.setOnClickListener(null);
			}
		}

		protected void start() {
			if (!mLock.tryLock())
				return;

			mVideoView.setVisibility(View.GONE);
			mVideoView.setVisibility(View.VISIBLE);

			mVideoView.setVideoPath(video_url);
			mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_FIT);
			mVideoView.setMediaBufferingIndicator(mLoading);

			// 如果短视频还未播放，更新播放数
			if (!isPlaying) {
				isPlaying = true;
				updatePlayCount(video_id, (BaseActivity) mVideoView.getContext());
			}
			// 如果播放进度不为 0 ，跳到指定位置播放
			else if (playOffset > 0) {
				mVideoView.seekTo(playOffset);
			}

			mVideoView.start();

			mPlayButton.setImageResource(R.drawable.media_palyer_pause_button_selector);
			mPlayButton.setVisibility(View.VISIBLE);
			mViewImage.setVisibility(View.GONE);
			if (mRoot != null) {
				mVideoViewParentClicker.resetToken();
				mRoot.setOnClickListener(mVideoViewParentClicker);
			}
			mLock.unlock();
			hideInfoView(mInfoView);

			//发送暂停后台播放
			Intent intent = new Intent(ACTION_AUDIO_ALLSTOP_PLAY);
			((BaseActivity) mVideoView.getContext()).sendBroadcast(intent);
		}
	}

	/**
	 * 暂停按钮点击事件
	 *
	 * @author yanyadi
	 *
	 */
	class PauseButtonListener implements OnClickListener {
		private ShortVideoHolder mHolder;

		public PauseButtonListener(ShortVideoHolder mHolder) {
			this.mHolder = mHolder;
		}

		@Override
		public void onClick(View v) {
			if (mHolder.mVideoView.isPlaying()) {
				mHolder.pause(true);
			} else {
				swapNewVideo(mHolder);
				mHolder.start();
			}
		}

	}

	/**
	 * 切换短视频播放
	 *
	 * @param newHolder
	 */
	private void swapNewVideo(ShortVideoHolder newHolder) {
		if (mCurrentVideo != null) {
			// 如果上次暂停的视频，是当前要播放的视频。
			if (mCurrentVideo != newHolder)
				mCurrentVideo.pause(false);
		}
		mCurrentVideo = newHolder;
	}

	/**
	 * 视频图片 点击事件
	 *
	 * @author yanyadi
	 *
	 */
	class PlayVideoListener implements OnClickListener, OnCompletionListener, OnBufferingUpdateListener, OnPreparedListener, OnErrorListener {
		private ShortVideoHolder mHolder;

		public PlayVideoListener(ShortVideoHolder holder) {
			mHolder = holder;
			mHolder.mVideoView.setOnCompletionListener(this);
			mHolder.mVideoView.setOnBufferingUpdateListener(this);
			mHolder.mVideoView.setOnPreparedListener(this);
			mHolder.mVideoView.setOnErrorListener(this);
		}

		@Override
		public void onClick(View v) {
			swapNewVideo(mHolder);
			mHolder.start();
			// 统计视频点播子模块点击量
			MobclickAgent.onEvent(activity, "E_C_pageName_vodItemClick");
		}

		private void updatePauseButton(boolean isPlay) {
			if (isPlay)
				mHolder.mPlayButton.setImageResource(R.drawable.media_palyer_pause_button_selector);
			else
				mHolder.mPlayButton.setImageResource(R.drawable.media_palyer_play_button_selector);
			mHolder.mPlayButton.setVisibility(View.VISIBLE);
		}

		@Override
		public void onCompletion(IMediaPlayer mp) {
			mHolder.mViewImage.setVisibility(View.VISIBLE);

			updatePauseButton(false);
			showInfoView(mHolder.mInfoView);
		}

		@Override
		public void onBufferingUpdate(IMediaPlayer mp, int percent) {
		}

		@Override
		public void onPrepared(IMediaPlayer mp) {
			updatePauseButton(true);
			mHolder.mLoading.setVisibility(View.GONE);
		}

		@Override
		public boolean onError(IMediaPlayer mp, int what, int extra) {
			toast(R.string.msg_video_play_error);
			if (mHolder.mLoading != null)
				mHolder.mLoading.setVisibility(View.GONE);
			mHolder.mViewImage.setVisibility(View.VISIBLE);
			mHolder.mPlayButton.setVisibility(View.GONE);
			showInfoView(mHolder.mInfoView);

			return true;
		}

	}

	/**
	 * 更新视频播放次数
	 *
	 * @param mVideoId
	 */
	private static void updatePlayCount(String mVideoId, BaseActivity activity) {
		// 更新视频播放次数
		MediaUtils.updateVideoPlayCount(mVideoId, MediaUtils.PLAY_TYPE_VIDEO_SHORT, activity);

	}

	/**
	 * 隐藏 视频信息 view
	 */
	private static void hideInfoView(View mInfoView) {
		ObjectAnimator mAlpahAnimator = ObjectAnimator.ofFloat(mInfoView, "alpha", 1f, 0f).setDuration(ANIM_DURATION);
		mAlpahAnimator.setInterpolator(new AccelerateInterpolator());
		mAlpahAnimator.addListener(new HiddenViewListener(mInfoView, View.GONE));
		mAlpahAnimator.start();
	}

	/**
	 * 显示 视频信息 view
	 */
	private static void showInfoView(View mInfoView) {
		mInfoView.setAlpha(0);
		mInfoView.setVisibility(View.VISIBLE);
		ObjectAnimator mAlpahAnimator = ObjectAnimator.ofFloat(mInfoView, "alpha", 0f, 1f).setDuration(ANIM_DURATION);
		mAlpahAnimator.addListener(new HiddenViewListener(mInfoView, View.VISIBLE));
		mAlpahAnimator.setInterpolator(new DecelerateInterpolator());
		mAlpahAnimator.start();
	}

	static class HiddenViewListener implements AnimatorListener {
		private View mView;
		private int mLastState;

		public HiddenViewListener(View target, int mState) {
			this.mLastState = mState;
			this.mView = target;
		}

		@Override
		public void onAnimationStart(Animator animation) {
		}

		@Override
		public void onAnimationEnd(Animator animation) {
			this.mView.setVisibility(mLastState);
		}

		@Override
		public void onAnimationCancel(Animator animation) {
		}

		@Override
		public void onAnimationRepeat(Animator animation) {
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

	class OpenVideoCategoryListener implements OnClickListener {
		private String mGroupId;
		private String mTitle;

		public OpenVideoCategoryListener(String id, String title) {
			this.mGroupId = id;
			this.mTitle = title;
		}

		@Override
		public void onClick(View v) {
			Intent toPlayList = new Intent(activity, ShortVideoActivity.class);
			toPlayList.putExtra("group_id", mGroupId);
			toPlayList.putExtra("title", mTitle);
			startActivity(toPlayList);
		}

	}
}
