package com.ctbri.wxcc.media;

import static com.ctbri.wxcc.audio.AudioLiveFragment.ACTION_AUDIO_VOD_COLLECTIONS_PLAY;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.ctbri.comm.widget.SendCommentsFragment;
import com.ctbri.wxcc.Constants;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseActivity;
import com.ctbri.wxcc.community.BaseFragment;
import com.ctbri.wxcc.entity.AudioVodDetail;
import com.ctbri.wxcc.entity.MediaLiveDetail;
import com.ctbri.wxcc.entity.VodDetailBean;
import com.ctbri.wxcc.entity.VodDetailBean.VodDetailItem;
import com.google.gson.Gson;
import com.sina.weibo.sdk.utils.LogUtil;

public class MediaLivePlayerFragment extends BaseFragment {
	/**
	 * 直播或者点播 节目详情 fragment
	 */
	public static final String KEY_INFO_FRAGMENT = "FRAGMENT_INFO_DETAIL";
	private View mPlayerContainer;
	private int mPlayerHeight;
	private String type_id, media_id;
	/**
	 * 播放器控件 fragment
	 */
	public static final String KEY_PLAYER_FRAGMENT = "FRAGMENT_PLAYER_DETAIL";

	public static final MediaLivePlayerFragment newInstance(String group_id, String video_id) {
		MediaLivePlayerFragment fragment = new MediaLivePlayerFragment();
		Bundle args = new Bundle();
		args.putString("type_id", group_id);
		args.putString("video_id", video_id);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onAttach(Activity activity_) {
		super.onAttach(activity_);
		type_id = getArgs("type_id");
		media_id = getArgs("video_id");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.media_player_live_layout, container, false);
		mPlayerContainer = v.findViewById(R.id.frame_video_container);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		initPlayer();
		if (VodVideoListFragmet.TYPE_VOD.equals(type_id)) {
			getVodData();
		}
		else if (VodVideoListFragmet.TYPE_BROADCAST.equals(type_id)) {
			getLiveData();
		}
		else if (MediaPlayerActivity.TYPE_AUDIO_VOD.equals(type_id)) {
			getAudioVodData();
		}
	}

	/**
	 * 初始化 播放器 和 直播或点播 详情
	 */
	private void initPlayer() {
		FragmentManager fm = getChildFragmentManager();
		fm.beginTransaction().replace(R.id.frame_video_container, MediaPlayerFragment.newInstance(type_id), KEY_PLAYER_FRAGMENT)
				.replace(R.id.frame_video_info, MediaInfoFragment.newInstance(type_id), KEY_INFO_FRAGMENT).commit();

	}

	/**
	 * 获取点播节目数据
	 */
	private void getVodData() {
		String url = Constants.METHOD_VIDEO_VOD_DETAIL + "?video_id=" + media_id;
		request(url, new RequestCallback() {
			@Override
			public void requestSucc(String json) {
				Gson gson = new Gson();
				VodDetailBean mDetail = gson.fromJson(json, VodDetailBean.class);
				update(mDetail);
			}

			@Override
			public void requestFailed(int errorCode) {

			}
		}, null);
	}

	/**
	 * 更新视频点播信息
	 * 
	 * @param bean
	 */
	private void update(VodDetailBean bean) {
		MediaInfoFragment info = getInfoFragment();
		if (info != null)
			info.updateInfo(bean);
		MediaPlayerFragment player = getPlayerFragment();
		if (player != null && bean.getData() != null) {
			VodDetailBean data = bean.getData();
			//by zhangzheng 自动播放第一集
			List<VodDetailItem> vods = bean.getData().getVideos();
			String videoId = data.getVideo_id();
			player.update(data.getVideo_name(), vods.get(0).getVod(), data.getVideo_id(), VodVideoListFragmet.TYPE_VOD, null);
			MediaUtils.updateAudioPlayCount(videoId, MediaUtils.PLAY_TYPE_VIDEO_VOD, (BaseActivity) activity);
			// 更新评论数
			Fragment fragment = getFragmentManager().findFragmentByTag("send_comment");
			if (fragment != null)
				((SendCommentsFragment) fragment).setCommentsCount(bean.getData().getComments());
		}
	}
	
	/**
	 * 获取直播节目数据
	 */
	private void getLiveData() {
		String url = Constants.METHOD_VIDEO_LIVE_DETAIL + "?channel_id=" + media_id;
		request(url, new RequestCallback() {
			@Override
			public void requestSucc(String json) {
				Gson gson = new Gson();
				MediaLiveDetail mDetail = gson.fromJson(json, MediaLiveDetail.class);
				update(mDetail);
			}

			@Override
			public void requestFailed(int errorCode) {
			}
		}, null);
	}

	private void update(MediaLiveDetail bean) {
		MediaInfoFragment info = getInfoFragment();
		if (info != null)
			info.updateInfo(bean);
		MediaPlayerFragment player = getPlayerFragment();
		if (player != null && bean.getData() != null) {
			MediaLiveDetail data = bean.getData();
	
			String channelId = data.getChannel_id();
			player.update(data.getProgram_name(), data.getLive(), channelId, VodVideoListFragmet.TYPE_BROADCAST, null);
			MediaUtils.updateVideoPlayCount(channelId, MediaUtils.PLAY_TYPE_VIDEO_LIVE, (BaseActivity) activity);
		}
	}
	
	/**
	 * 获取音频点播数据
	 */
	private void getAudioVodData() {
		String url = Constants.METHOD_AUDIO_VOD_DETAIL + "?audio_id=" + media_id;
		request(url, new RequestCallback() {
			@Override
			public void requestSucc(String json) {
				Gson gson = new Gson();
				AudioVodDetail mDetail = gson.fromJson(json, AudioVodDetail.class);
				update(mDetail);
			}

			@Override
			public void requestFailed(int errorCode) {

			}
		}, null);
	}
	
	/**
	 * 更新音频点播信息
	 * 
	 * @param bean
	 */
	private void update(AudioVodDetail bean) {
		MediaInfoFragment info = getInfoFragment();
		if (info != null)
			info.updateInfo(bean);
		MediaPlayerFragment player = getPlayerFragment();
		if (player != null && bean.getData() != null) {
			AudioVodDetail data = bean.getData();
			String audio_id = data.getAudio_id();
			
			List<VodDetailItem> vods = data.getAudios();
			if(vods != null && vods.size()>0) {
				
				//将合集传递到服务当中
				Intent intent = new Intent(ACTION_AUDIO_VOD_COLLECTIONS_PLAY);
				intent.putExtra("AudioVodDetail", data);
				activity.sendBroadcast(intent);

				//为合集，如果没有缓存则自动播放第一曲
				player.update(data.getAudio_name(), vods.get(0).getVod(), audio_id, MediaPlayerActivity.TYPE_AUDIO_VOD, data.getPic());
			}else {
				//为单曲
				Intent intent = new Intent(ACTION_AUDIO_VOD_COLLECTIONS_PLAY);
				activity.sendBroadcast(intent);
				player.update(data.getAudio_name(), data.getVod(), audio_id, MediaPlayerActivity.TYPE_AUDIO_VOD, data.getPic());
			}
			
			// 更新播放次数
			MediaUtils.updateAudioPlayCount(audio_id, MediaUtils.PLAY_TYPE_AUDIO_VOD, (BaseActivity) activity);
			// 更新评论数
			Fragment fragment = getFragmentManager().findFragmentByTag("send_comment");
			if (fragment != null)
				((SendCommentsFragment) fragment).setCommentsCount(data.getComments());
		}
	}

	private MediaInfoFragment getInfoFragment() {
		MediaInfoFragment infoFragment = (MediaInfoFragment) getChildFragmentManager().findFragmentByTag(KEY_INFO_FRAGMENT);
		return infoFragment;
	}

	public MediaPlayerFragment getPlayerFragment() {
		MediaPlayerFragment player = (MediaPlayerFragment) getChildFragmentManager().findFragmentByTag(KEY_PLAYER_FRAGMENT);
		return player;
	}

	@Override
	protected boolean isEnabledAnalytics() {
		return false;
	}

	@Override
	protected String getAnalyticsTitle() {
		return null;
	}

	public int fullscreen() {
		FragmentManager fm = getChildFragmentManager();
		Fragment fragmentInfo = fm.findFragmentByTag(KEY_INFO_FRAGMENT);
		if (fragmentInfo != null)
			fm.beginTransaction().hide(fragmentInfo).commit();

		if (mPlayerContainer != null) {
			mPlayerHeight = mPlayerContainer.getHeight();
			mPlayerContainer.getLayoutParams().height = LayoutParams.MATCH_PARENT;
			mPlayerContainer.setLayoutParams(mPlayerContainer.getLayoutParams());
		}
		return mPlayerHeight;
	}

	public int cancelFullscreen() {
		FragmentManager fm = getChildFragmentManager();
		Fragment fragmentInfo = fm.findFragmentByTag(KEY_INFO_FRAGMENT);
		if (fragmentInfo != null && fragmentInfo.isHidden())
			fm.beginTransaction().show(fragmentInfo).commit();

		if (mPlayerContainer != null) {
			mPlayerContainer.getLayoutParams().height = mPlayerHeight;
			mPlayerContainer.setLayoutParams(mPlayerContainer.getLayoutParams());
		}
		return mPlayerHeight;
	}

}
