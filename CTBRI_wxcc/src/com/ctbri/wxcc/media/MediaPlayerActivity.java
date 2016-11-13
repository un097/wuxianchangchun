package com.ctbri.wxcc.media;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import cn.ffcs.external.share.view.WeiBoSocialShare;

import com.ctbri.comm.widget.SendCommentsFragment;
import com.ctbri.comm.widget.SendCommentsFragment.OnSendCommentsListener;
import com.ctbri.wxcc.Constants;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseActivity;
import com.ctbri.wxcc.community.CommentFragment;
import com.ctbri.wxcc.community.CommentFragment.CommentFragmentBuilder;
import com.ctbri.wxcc.community.CommentFragment.CommentFragmentSomeListener;
import com.ctbri.wxcc.community.DefaultWatchManager;
import com.ctbri.wxcc.community.Watcher;
import com.ctbri.wxcc.community.WatcherManager;
import com.ctbri.wxcc.community.WatcherManagerFactory;
import com.ctbri.wxcc.widget.LoadMorePTRListView;


/**
 * 视频播放时，用该 Activity 展示 <br />
 * 
 * Intent 中传递两个参数 <Br />
 * 1. 为直播时 type_id : 视频类别 , channel_id : 频道id <br />
 * 2. 为点播时 type_id : 视频类别 , vod_id : 点播视频id.
 * 
 * @author yanyadi
 * 
 */
public class MediaPlayerActivity extends BaseActivity implements CommentFragmentSomeListener, WatcherManagerFactory {

	private ViewPager vp_pager;
	private WatcherManager defaultManager = new DefaultWatchManager();
	/** 播放器索引 */
	private static final int POS_PLAYER = 0;
	/** 评论列表索引 */
	private static final int POS_COMMENT = 1;

	private boolean mIsPortrait = true;

	/**
	 * 播放类别为 音频点播
	 */
	public static final String TYPE_AUDIO_VOD = "audio_vod";
	private static final String TAG = "MediaPlayerActivity";

	private String type_id, vod_id, mCommentListUrl, mCommentPraiseUrl, mCommentReportUrl,sub_title;

	private PlayerAdapter mPlayerAdapter;
	private boolean is_quanping;
	private boolean isTYPE_BROADCAST;
	private MediaLivePlayerFragment fragment ;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		// 获取 从 Intent 中传递来的参数
		type_id = getString("type_id");
		sub_title = getString("sub_title");
		// 如果是音频直播
		if (VodVideoListFragmet.TYPE_BROADCAST.equals(type_id)) {
			isTYPE_BROADCAST = true;
			String channel_id = getString("channel_id");
			FragmentManager fm = getSupportFragmentManager();
			// 直播没有评论，直接加载。视频播放fragment
			fm.beginTransaction().replace(android.R.id.content, MediaLivePlayerFragment.newInstance(type_id, channel_id), "livePlayer").commit();
			return;
		}
		String send_comment_url = null;
		isTYPE_BROADCAST = false;
		// 是视频点播
		if (VodVideoListFragmet.TYPE_VOD.equals(type_id)) {
			vod_id = getString("vod_id");
			mCommentListUrl = Constants.METHOD_VIDEO_COMMENT_LIST + "?type=0&video_id=" + vod_id;
			send_comment_url = Constants.METHOD_VIDEO_SEND_COMMENT + "?video_id=" + vod_id;
			mCommentPraiseUrl = Constants.METHOD_VIDEO_PRAISE;
			mCommentReportUrl = Constants.METHOD_VIDEO_REPORT;
		}
		// 是音频点播
		else if (TYPE_AUDIO_VOD.equals(type_id)) {
			vod_id = getString("vod_id");
			mCommentListUrl = Constants.METHOD_AUDIO_COMMENT + "?type=0&audio_id=" + vod_id;
			send_comment_url = Constants.METHOD_AUDIO_SEND_COMMENTS + "?audio_id=" + vod_id;
			mCommentPraiseUrl = Constants.METHOD_AUDIO_PRAISE;
			mCommentReportUrl = Constants.METHOD_AUDIO_REPORT;
		}

		// 加载带有评论控件的 layout
		setContentView(R.layout.common_comment_container);
		initWithCommentLayout();
		initCommentsWidget(send_comment_url);

		System.out.println("onCreateView  ================================================================");

	}

	private void fullscreen(boolean full) {
		FragmentManager fm = getSupportFragmentManager();
		if (full) {
			FragmentTransaction ft = fm.beginTransaction();
			// 隐藏评论
			Fragment fragmentComment = fm.findFragmentByTag("send_comment");
			if (fragmentComment != null) {
				ft.hide(fragmentComment);
			}
			ft.commit();
		} else {
			FragmentTransaction ft = fm.beginTransaction();
			// 隐藏评论
			Fragment fragmentComment = fm.findFragmentByTag("send_comment");
			if (fragmentComment != null) {
				ft.show(fragmentComment);
			}
			ft.commit();
		}
	}

	private void initCommentsWidget(String commentUrl) {
		SendCommentsFragment newInstance = SendCommentsFragment.newInstance(commentUrl, vod_id, 0);
		newInstance.setOnSendCommentsListener(new OnSendCommentsListener() {
			@Override
			public void onSendComments(int status) {
				switch (status) {
				case 0:
					toast(getString(R.string.msg_comment_publisher_succ));
					notifyCommentRefreshed();
					break;

				default:
					toast("评论发表失败!!");
					break;
				}
			}
		});
		newInstance.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				vp_pager.setCurrentItem(1);
			}
		});
		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_send_comments_layout, newInstance, "send_comment").commit();
	}

	private void initWithCommentLayout() {
		vp_pager = (ViewPager) findViewById(R.id.vp_pager);
		mPlayerAdapter = new PlayerAdapter(getSupportFragmentManager());
		vp_pager.setAdapter(mPlayerAdapter);
	}

	/**
	 * 通知 子组件 需要刷新评论列表
	 */
	private void notifyCommentRefreshed() {
		defaultManager.trigger(Watcher.TYPE_REFRESH, null);
	}
	class PlayerAdapter extends FragmentPagerAdapter {

		public PlayerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			if (position == POS_PLAYER) {
				fragment =MediaLivePlayerFragment.newInstance(type_id, vod_id);
				return fragment;
			} else {
				CommentFragmentBuilder builder = CommentFragmentBuilder.newInstance(CommentFragment.FRAGMENT_COMMENT).setPraiseUrl(mCommentPraiseUrl)
						.setReportUrl(mCommentReportUrl).setTitle("全部评论").setCommentsUrl(mCommentListUrl).setHiddenDesc(false).setHiddenActionBar(false);
				builder.setSubTitle(sub_title);
				return builder.build();
			}
		}

		/**
		 * 直播页面 和 评论页面
		 */
		@Override
		public int getCount() {
			if (mIsPortrait)
				return 2;
			return 1;
		}
	}

	@Override
	public void onBack(int fragmentId) {
		vp_pager.setCurrentItem(0, true);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			is_quanping = true;
			fullscreen(true);
		} else {
			is_quanping = false;
			fullscreen(false);
		}
	}

	@Override
	public boolean onClickLoadMore(int fragmentId, LoadMorePTRListView lv_more) {
		return true;
	}

	@Override
	public void onBackPressed() {
		if (vp_pager == null || vp_pager.getCurrentItem() == POS_PLAYER)
			super.onBackPressed();
		else
			vp_pager.setCurrentItem(POS_PLAYER);
	}

	@Override
	public WatcherManager getManager() {
		return defaultManager;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (WeiBoSocialShare.mSsoHandler != null) {
			WeiBoSocialShare.mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(is_quanping) {
			MediaPlayerFragment player;
			if(isTYPE_BROADCAST) {
				MediaLivePlayerFragment live = (MediaLivePlayerFragment)getSupportFragmentManager().findFragmentByTag("livePlayer");
				player = live.getPlayerFragment();
			}else {
				player = fragment.getPlayerFragment();
			}
			player.exitFullScreen();
			return true;
        }
		return super.onKeyDown(keyCode, event);
    }
}
