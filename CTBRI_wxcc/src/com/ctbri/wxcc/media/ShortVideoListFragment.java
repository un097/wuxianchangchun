package com.ctbri.wxcc.media;

import java.util.List;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnErrorListener;
import tv.danmaku.ijk.media.widget.VideoView;
import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ctbri.comm.util._Utils;
import com.ctbri.wxcc.Constants;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseActivity;
import com.ctbri.wxcc.community.Constants_Community;
import com.ctbri.wxcc.entity.ShortListBean;
import com.ctbri.wxcc.entity.ShortListBean.ShortVideo;
import com.ctbri.wxcc.media.MediaUtils.ILikeSuccess;
import com.ctbri.wxcc.media.VideoShortFragment.ShortVideoHolder;
import com.ctbri.wxcc.travel.CommonList;
import com.ctbri.wxcc.widget.LoadMorePTRListView;
import com.ctbri.wxcc.widget.SmoothMenu;
import com.ctbri.wxcc.widget.SmoothMenu.OnSmoothMenuItemSelectListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ShortVideoListFragment extends CommonList<ShortListBean, ShortVideo> {

	private static final String TAG = "ShortVideoListFragment";

	public static final int STATE_PLAY = 1001;
	public static final int STATE_PAUSE = 1002;
	// 当前 播放的 VideoVieo
	private VideoHolder mCurrentHolder;
	// 当前 播放的 ShortVideo 实体
	private ShortVideo mCurrentVideo;
	/**
	 * 保存当前播放的位置
	 */
	private int mPlayPosition = ListView.INVALID_POSITION;
	private LayoutInflater inflater;

	private String group_id, title;

	@Override
	public void onAttach(Activity activity_) {
		super.onAttach(activity_);
		Bundle data = activity_.getIntent().getExtras();
		if (data != null) {
			group_id = data.getString("group_id");
			title = data.getString("title");
		}
	}

	@Override
	protected String getListUrl() {
		return Constants.METHOD_VIDEO_SHORT_LIST + "?group_id=" + group_id;
	}

	@Override
	protected Class<ShortListBean> getGsonClass() {
		return ShortListBean.class;
	}

	@Override
	protected String getActionBarTitle() {
		return title;
	}

	@Override
	protected List<ShortVideo> getEntitys(ShortListBean bean) {
		if (bean != null && bean.getData() != null) {
			return bean.getData().getVideo_list();
		}
		return null;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setDataObserver(mDataSetObserver);
	}

	private DataSetObserver mDataSetObserver = new DataSetObserver() {
		public void onChanged() {
			if (mCurrentHolder != null) {
				mCurrentHolder.pause(false);
				mCurrentHolder = null;
			}
		}
	};

	@Override
	protected boolean isEnd(ShortListBean bean) {
		if (bean != null && bean.getData() != null)
			return bean.getData().getIs_end() == 0;
		return true;
	}

	@Override
	protected boolean initHeaderDetail(LoadMorePTRListView lv_list, LayoutInflater inflater) {
		this.inflater = inflater;
		lv_list.setOnScrollListener(mListScrollListener);
		return false;
	}

	private OnScrollListener mListScrollListener = new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			// 当位置不可见时
			if (mPlayPosition < firstVisibleItem - 1 || mPlayPosition > view.getLastVisiblePosition() - 1) {
				if (mCurrentHolder != null)
					if (mCurrentVideo != null) {
						if (mCurrentVideo.state == STATE_PLAY) {
							mCurrentHolder.pause(false);
							mCurrentVideo.isPlaying = false;
							mCurrentVideo.state = STATE_PAUSE;
						}
					}
				mCurrentHolder = null;
			}
		}
	};

	@Override
	protected boolean isInflateActionBar() {
		return true;
	}

	class VideoHolder extends ShortVideoHolder {
		TextView tv_title, tv_time, tv_zanView, tv_play_count;
		PlayVideoListener playListener;
		PauseButtonListener pauseListener;
		SmoothMenu mSmoothMenu;
		ShortVideo mVideo;
		int mPosition;
		int mState;

		@Override
		protected void pause(boolean isShowBtn) {
			super.pause(isShowBtn);
			mState = STATE_PAUSE;
			mVideo.playPosition = playOffset;
			mVideo.isPlaying = isShowBtn;
			mVideo.state = STATE_PAUSE;
		}

		@Override
		protected void start() {
			playOffset = mVideo.playPosition;
			mPlayPosition = mPosition;
			mVideo.isPlaying = true;
			mState = STATE_PLAY;
			mCurrentVideo = mVideo;
			mCurrentVideo.state = mState;
			super.start();
		}
	}

	@Override
	protected View getListItemView(int position, View convertView, ViewGroup parent, ShortVideo video, ImageLoader imgloader, DisplayImageOptions dio) {
		VideoHolder holder = null;
		boolean canApply = convertView != null;
		if (canApply) {
			holder = (VideoHolder) convertView.getTag();
		} else {
			holder = new VideoHolder();
			convertView = inflater.inflate(R.layout.media_short_video_list_item_box, parent, false);
			// convertView.setOnClickListener(new ItemClickListener(holder));
			convertView.setTag(holder);
			SmoothMenu mSmoothMenu = new SmoothMenu(activity);
			View v = inflater.inflate(R.layout.media_video_short_list_item, mSmoothMenu, false);
			v.setOnClickListener(new ItemClickListener(holder));
			// 视频标题
			holder.tv_title = (TextView) v.findViewById(R.id.tv_title);
			// 默认图片
			holder.mViewImage = (ImageView) v.findViewById(R.id.iv_video_img);
			// video view
			holder.mVideoView = (VideoView) v.findViewById(R.id.sv_video);
			// holder.mVideoView.setOnClickListener(new
			// VideoViewClickListener());
			holder.mLoading = v.findViewById(R.id.progress_loading);
			holder.mVideoView.setMediaBufferingIndicator(holder.mLoading);
			holder.mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_FIT);
			holder.mVideoView.setOnErrorListener(new VideoErrorListener(holder));

			// 暂停按钮
			holder.mPlayButton = (ImageButton) v.findViewById(R.id.ibtn_paly);
			// 视频信息视图
			holder.mInfoView = v.findViewById(R.id.short_video_container);
			// 右上暂停按钮
			holder.pauseListener = new PauseButtonListener(holder);
			holder.mPlayButton.setOnClickListener(holder.pauseListener);
			// 时长
			holder.tv_time = (TextView) v.findViewById(R.id.tv_time);
			// 播放次数
			holder.tv_play_count = (TextView) v.findViewById(R.id.tv_play_count);
			// 点赞数量
			holder.tv_zanView = (TextView) v.findViewById(R.id.tv_zan_count);

			holder.playListener = new PlayVideoListener(holder);
			holder.mViewImage.setOnClickListener(holder.playListener);
			mSmoothMenu.setOnSmoothMenuItemSelected(new MenuItemSelectedListener(holder.tv_zanView));
			mSmoothMenu.setContentView(v);
			holder.mSmoothMenu = mSmoothMenu;
			((ViewGroup) convertView).addView(mSmoothMenu);
		}
		// holder.mVideoView.setVideoListener(null);

		// 如果当前位置的视频正在播放，要恢复播放的视频
		// if (mPlayPosition == position) {
		// mCurrentView = holder.mVideoView;
		//
		// holder.ibtn_paly.setVisibility(View.GONE);
		// holder.infoView.setVisibility(View.GONE);
		// holder.iv_video.setVisibility(View.GONE);
		// isPlaying = true;
		//
		// } else {
		// holder.ibtn_paly.setVisibility(View.VISIBLE);
		// holder.infoView.setVisibility(View.VISIBLE);
		// holder.iv_video.setVisibility(View.VISIBLE);
		//
		// }
		if (video.isPlaying) {
			holder.mPlayButton.setVisibility(View.VISIBLE);
			holder.mPlayButton.setImageResource(R.drawable.media_palyer_play_button_selector);
		} else {
			holder.mPlayButton.setVisibility(View.GONE);
		}
		holder.mState = STATE_PAUSE;
		holder.mPosition = position;
		holder.video_url = video.getPlay_url();
		holder.video_id = video.getVideo_id();

		holder.mInfoView.setVisibility(View.VISIBLE);
		holder.mViewImage.setVisibility(View.VISIBLE);

		holder.mSmoothMenu.setTag(video);
		holder.tv_title.setText(video.getVideo_name());
		holder.tv_zanView.setText(video.getPraise_times());
		holder.tv_play_count.setText(video.getPlay_times());
		holder.tv_time.setText(video.getVideo_time());
		ImageLoader.getInstance().displayImage(video.getPic_url().trim(), holder.mViewImage, dio);

		holder.mVideo = video;
		return convertView;
	}

	private class VideoErrorListener implements OnErrorListener {

		public VideoErrorListener(VideoHolder holder) {
			this.mHolder = holder;
		}

		private VideoHolder mHolder;

		@Override
		public boolean onError(IMediaPlayer mp, int what, int extra) {
			toast(R.string.msg_video_play_error);
			if (mHolder.mLoading != null)
				mHolder.mLoading.setVisibility(View.GONE);
			mHolder.mViewImage.setVisibility(View.VISIBLE);

			return true;
		}
	};

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
				String title = video.getVideo_name();
				String content = context.getString(R.string.share_content_video, title);
				_Utils.shareAndCheckLogin(context, title, Constants_Community.APK_DOWNLOAD_URL, content, _Utils.getDefaultAppIcon(context));
				break;
			case left:
				MediaUtils.like(video.getVideo_id(), VodVideoListFragmet.TYPE_VOD, new LikeCallbackImpl(tv_like_count, video), (BaseActivity) sm.getContext());
				break;
			}
		}
	};

	/**
	 * 点赞后的回调函数
	 * 
	 * @author yanyadi
	 * 
	 */
	static class LikeCallbackImpl implements ILikeSuccess {
		private ShortVideo video;
		private TextView tv_like_count;

		public LikeCallbackImpl(TextView tv_count, ShortVideo video) {
			this.video = video;
			this.tv_like_count = tv_count;
		}

		@Override
		public void onSuccess(String total) {
			tv_like_count.setText(total);
			video.setPraise_times(total);
		}

	}

	@Override
	protected void onItemClick(AdapterView<?> parent, View view, int position, long id, ShortVideo entity) {
	}

	@Override
	protected String getAnalyticsTitle() {
		return null;
	}

	@Override
	protected boolean isEnabledAnalytics() {
		return false;
	}

	class ItemClickListener implements OnClickListener {
		public ItemClickListener(VideoHolder holder) {
			this.mHolder = holder;
		}

		VideoHolder mHolder;

		@Override
		public void onClick(View v) {
			if (mHolder != null && mHolder.mVideoView.isPlaying()) {
				mHolder.pause(true);
				// if (mHolder == mCurrentHolder)
				// mCurrentHolder = null;
			}
		}

	}

	/**
	 * 暂停按钮点击事件
	 * 
	 * @author yanyadi
	 * 
	 */
	class PauseButtonListener implements OnClickListener {
		private VideoHolder mHolder;

		public PauseButtonListener(VideoHolder holder) {
			this.mHolder = holder;
		}

		@Override
		public void onClick(View v) {
			if (mHolder.mVideoView.isPlaying()) {
				mHolder.pause(true);
			} else {
				playNewVideo(mHolder);
				mHolder.start();
			}
		}

	}

	/**
	 * 重置其他其他视频的播放状态
	 * 
	 * @param mTarget
	 */
	private void resetOtherVideoNoPlaying(ShortVideo mTarget) {
		int count = common_adapter.getCount();
		for (int i = 0; i < count; i++) {
			ShortVideo mVideo = common_adapter.getItem(i);
			if (mVideo == mTarget)
				continue;
			mVideo.isPlaying = false;
			mVideo.playPosition = 0;
		}
	}

	/**
	 * 播放新的视频
	 * 
	 * @param newHolder
	 */
	private void playNewVideo(VideoHolder newHolder) {
		if (mCurrentHolder != null) {
			// 如果上次暂停的视频，不是当前要播放的视频。则清除上次视频的播放状态
			if (mCurrentHolder != newHolder)
				mCurrentHolder.pause(false);
		}
		resetOtherVideoNoPlaying(newHolder.mVideo);
		mCurrentHolder = newHolder;
	}

	/**
	 * 视频图片 点击事件
	 * 
	 * @author yanyadi
	 * 
	 */
	class PlayVideoListener implements OnClickListener {
		private VideoHolder mHolder;

		public PlayVideoListener(VideoHolder holder) {
			this.mHolder = holder;
		}

		@Override
		public void onClick(View v) {
			playNewVideo(mHolder);
			mHolder.start();
		}
	}

}
