package com.ctbri.wxcc.media;

import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;

import android.view.View;
import android.widget.ImageView;

import com.ctbri.wxcc.Constants;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseActivity;
import com.ctbri.wxcc.community.BaseActivity.RequestCallback;
import com.ctbri.wxcc.entity.FavoriteStatusBean;
import com.ctbri.wxcc.entity.LikeBean;
import com.google.gson.Gson;

public class MediaUtils {
	/**
	 * 视频组
	 */
	public static final String FLAG_GROUP = "2";
	/**
	 * 单个视频
	 */
	public static final String FLAG_SINGLE_VIDEO = "1";
	/**
	 * 播放统计 为直播类别
	 */
	public static final String PLAY_TYPE_VIDEO_LIVE = "1";
	/**
	 * 播放统计 为点播类别
	 */
	public static final String PLAY_TYPE_VIDEO_VOD = "2";
	/**
	 * 播放统计 为短视频
	 */
	public static final String PLAY_TYPE_VIDEO_SHORT = "3";
	/**
	 * 播放统计 为音频点播
	 */
	public static final String PLAY_TYPE_AUDIO_VOD = "2";
	/**
	 * 播放统计 为音频直播
	 */
	public static final String PLAY_TYPE_AUDIO_LIVE = "1";

	/**
	 * 添加收藏，或者更新收藏
	 * 
	 * @param id
	 * @param activity
	 * @param callback
	 */
	public static void favorite(String id, String flag, final BaseActivity activity) {
		favorite(id, flag, activity, new RequestCallback() {

			@Override
			public void requestSucc(String json) {
				activity.toast("收藏成功！");
			}

			@Override
			public void requestFailed(int errorCode) {
				activity.toast("您已经收藏了该视频！");
			}
		});
	}

	/**
	 * 添加收藏，或者更新收藏
	 * 
	 * @param id
	 * @param activity
	 * @param callback
	 */
	public static void favoriteAudio(String id, String flag, final BaseActivity activity) {
		favoriteAudio(id, flag, activity, new RequestCallback() {

			@Override
			public void requestSucc(String json) {
				activity.toast("收藏成功！");
			}

			@Override
			public void requestFailed(int errorCode) {
				activity.toast("您已经收藏了该音频！");
			}
		});
	}

	/**
	 * 检测当前的 视频有没有被 收藏
	 * 
	 * @param id
	 * @param type
	 * @param activity
	 * @param callback
	 */
	public static void isFavorite(String id, String type, final BaseActivity activity, final RequestCallback callback) {
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("video_id", id));
		params.add(new BasicNameValuePair("type", type));

		activity.request(Constants.METHOD_VIDEO_ISFAVORITE, callback, params);
	}

	/**
	 * 检测当前的 视频有没有被 收藏
	 * 
	 * @param id
	 * @param type
	 * @param activity
	 * @param callback
	 */
	public static void isFavorite(String id, String type, ImageView mImgBtn, final BaseActivity activity) {
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("video_id", id));
		params.add(new BasicNameValuePair("type", type));

		activity.request(Constants.METHOD_VIDEO_ISFAVORITE, new CheckFavoriteCallback(mImgBtn, id, type, new VideoFavoritCallback()), params);
	}

	private static class VideoFavoritCallback implements FavoriteCallback {

		@Override
		public void favorited(View view, String id, String type) {

			ImageView imgBtn = (ImageView) view;
			imgBtn.setImageResource(R.drawable.media_favourite_cancel_button_selector);
			imgBtn.setOnClickListener(new CancelVideoFavoriteCallback(imgBtn, id, type));
		}

		@Override
		public void unFavorite(View view, String id, String type) {
			ImageView imgBtn = (ImageView) view;
			imgBtn.setImageResource(R.drawable.media_favourite_button_selector);
			imgBtn.setOnClickListener(new VideoFavoriteCallback(imgBtn, id, type));
		}

	}

	/**
	 * 检测当前的 视频有没有被 收藏
	 * 
	 * @param id
	 * @param type
	 * @param activity
	 * @param callback
	 */
	public static void isAudioFavorite(String id, String type, ImageView mImgBtn, final BaseActivity activity) {
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("audio_id", id));
		params.add(new BasicNameValuePair("type", type));

		activity.request(Constants.METHOD_AUDIO_ISFAVORITE, new CheckAudioFavoriteCallback(mImgBtn, id, type), params);
	}

	/**
	 * 添加收藏，或者更新收藏
	 * 
	 * @param id
	 * @param activity
	 * @param callback
	 */
	public static void favoriteAudio(String id, String flag, BaseActivity activity, RequestCallback callback) {

		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("ids", id));
		params.add(new BasicNameValuePair("type", "0"));
		params.add(new BasicNameValuePair("flag", flag));

		activity.request(Constants.METHOD_AUDIO_EDIT_FAVORITES, callback, params);
	}

	/**
	 * 添加收藏，或者更新收藏
	 * 
	 * @param id
	 * @param activity
	 * @param callback
	 */
	public static void favorite(String id, String flag, BaseActivity activity, RequestCallback callback) {

		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("ids", id));
		params.add(new BasicNameValuePair("type", "0"));
		params.add(new BasicNameValuePair("flag", flag));

		activity.request(Constants.METHOD_VIDEO_EDITION_COLLECT, callback, params);
	}

	// new CancelFavoriteCallback(mImgBtn, video_id, type)

	private static interface FavoriteCallback {
		void favorited(View view, String id, String type);

		void unFavorite(View view, String id, String type);
	}

	/**
	 * 检测视频是否已经收藏
	 * 
	 * @author yanyadi
	 * 
	 */
	private static class CheckFavoriteCallback implements RequestCallback {
		private ImageView mImgBtn;
		private String video_id;
		private String type;
		private FavoriteCallback mCallback;

		public CheckFavoriteCallback(ImageView mBtn, String id, String type, FavoriteCallback callback) {
			this.mImgBtn = mBtn;
			this.video_id = id;
			this.type = type;
			this.mCallback = callback;
		}

		@Override
		public void requestSucc(String json) {
			Gson gson = new Gson();
			FavoriteStatusBean status = gson.fromJson(json, FavoriteStatusBean.class);
			// 如果未收藏
			if (status.getData().getStatus() == 0) {
				if (mCallback != null) {
					mCallback.unFavorite(mImgBtn, video_id, type);
				}
			} else {
				if (mCallback != null) {
					mCallback.favorited(mImgBtn, video_id, type);
				}
			}
		}

		@Override
		public void requestFailed(int errorCode) {

		}
	};

	/**
	 * 检测视频是否已经收藏
	 * 
	 * @author yanyadi
	 * 
	 */
	private static class CheckAudioFavoriteCallback implements RequestCallback {
		private ImageView mImgBtn;
		private String video_id;
		private String type;

		public CheckAudioFavoriteCallback(ImageView mBtn, String id, String type) {
			this.mImgBtn = mBtn;
			this.video_id = id;
			this.type = type;
		}

		@Override
		public void requestSucc(String json) {
			Gson gson = new Gson();
			FavoriteStatusBean status = gson.fromJson(json, FavoriteStatusBean.class);
			// 如果未收藏
			if (status.getData().getStatus() == 0) {
				this.mImgBtn.setImageResource(R.drawable.media_favourite_button_selector);
				this.mImgBtn.setOnClickListener(new AudioFavoriteCallback(mImgBtn, video_id, type));
			} else {
				this.mImgBtn.setImageResource(R.drawable.media_favourite_cancel_button_selector);
				this.mImgBtn.setOnClickListener(new CancelAudioFavoriteCallback(mImgBtn, video_id, type));
			}
		}

		@Override
		public void requestFailed(int errorCode) {

		}
	};

	/**
	 * 取消收藏回调函数
	 * 
	 * @author yanyadi
	 * 
	 */
	private static class CancelVideoFavoriteCallback implements RequestCallback, android.view.View.OnClickListener {
		private ImageView mImgBtn;
		private String video_id;
		private String type;

		public CancelVideoFavoriteCallback(ImageView ivBtn, String id, String type) {
			this.mImgBtn = ivBtn;
			this.video_id = id;
			this.type = type;
		}

		@Override
		public void requestSucc(String json) {
			mImgBtn.setEnabled(true);
			this.mImgBtn.setImageResource(R.drawable.media_favourite_button_selector);
			this.mImgBtn.setOnClickListener(new VideoFavoriteCallback(mImgBtn, video_id, type));
		}

		@Override
		public void requestFailed(int errorCode) {
			mImgBtn.setEnabled(true);
		}

		@Override
		public void onClick(View v) {
			mImgBtn.setEnabled(false);
			MediaUtils.unFavorite(video_id, type, (BaseActivity) mImgBtn.getContext(), this);

		}
	}

	/**
	 * 取消音频收藏回调函数
	 * 
	 * @author yanyadi
	 * 
	 */
	private static class CancelAudioFavoriteCallback implements RequestCallback, android.view.View.OnClickListener {
		private ImageView mImgBtn;
		private String video_id;
		private String type;

		public CancelAudioFavoriteCallback(ImageView ivBtn, String id, String type) {
			this.mImgBtn = ivBtn;
			this.video_id = id;
			this.type = type;
		}

		@Override
		public void requestSucc(String json) {
			mImgBtn.setEnabled(true);
			this.mImgBtn.setImageResource(R.drawable.media_favourite_button_selector);
			this.mImgBtn.setOnClickListener(new AudioFavoriteCallback(mImgBtn, video_id, type));
		}

		@Override
		public void requestFailed(int errorCode) {
			mImgBtn.setEnabled(true);
		}

		@Override
		public void onClick(View v) {
			mImgBtn.setEnabled(false);
			MediaUtils.unAudioFavorite(video_id, type, (BaseActivity) mImgBtn.getContext(), this);

		}
	}

	/**
	 * 收藏回调函数 和 点击事件
	 * 
	 * @author yanyadi
	 * 
	 */
	private static class VideoFavoriteCallback implements RequestCallback, android.view.View.OnClickListener {
		private ImageView mImgBtn;
		private String video_id;
		private String type;

		public VideoFavoriteCallback(ImageView ivBtn, String id, String type) {
			this.mImgBtn = ivBtn;
			this.video_id = id;
			this.type = type;
		}

		@Override
		public void requestSucc(String json) {
			mImgBtn.setEnabled(true);
			this.mImgBtn.setImageResource(R.drawable.media_favourite_cancel_button_selector);
			this.mImgBtn.setOnClickListener(new CancelVideoFavoriteCallback(mImgBtn, video_id, type));
		}

		@Override
		public void requestFailed(int errorCode) {
			mImgBtn.setEnabled(true);
		}

		@Override
		public void onClick(View v) {
			v.setEnabled(false);
			MediaUtils.favorite(video_id, type, (BaseActivity) this.mImgBtn.getContext(), this);
		}
	}

	/**
	 * 收藏回调函数 和 点击事件
	 * 
	 * @author yanyadi
	 * 
	 */
	private static class AudioFavoriteCallback implements RequestCallback, android.view.View.OnClickListener {
		private ImageView mImgBtn;
		private String video_id;
		private String type;

		public AudioFavoriteCallback(ImageView ivBtn, String id, String type) {
			this.mImgBtn = ivBtn;
			this.video_id = id;
			this.type = type;
		}

		@Override
		public void requestSucc(String json) {
			mImgBtn.setEnabled(true);
			this.mImgBtn.setImageResource(R.drawable.media_favourite_cancel_button_selector);
			this.mImgBtn.setOnClickListener(new CancelAudioFavoriteCallback(mImgBtn, video_id, type));
		}

		@Override
		public void requestFailed(int errorCode) {
			mImgBtn.setEnabled(true);
		}

		@Override
		public void onClick(View v) {
			v.setEnabled(false);
			MediaUtils.favoriteAudio(video_id, type, (BaseActivity) this.mImgBtn.getContext(), this);
		}
	}

	/**
	 * 取消收藏
	 * 
	 * @param id
	 * @param activity
	 * @param callback
	 */
	public static void unFavorite(String id, String flag, BaseActivity activity, RequestCallback callback) {

		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("ids", id));
		params.add(new BasicNameValuePair("type", "1"));
		params.add(new BasicNameValuePair("flag", flag));

		activity.request(Constants.METHOD_VIDEO_EDITION_COLLECT, callback, params);
	}

	/**
	 * 取消收藏
	 * 
	 * @param id
	 * @param activity
	 * @param callback
	 */
	public static void unAudioFavorite(String id, String flag, BaseActivity activity, RequestCallback callback) {

		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("ids", id));
		params.add(new BasicNameValuePair("type", "1"));
		params.add(new BasicNameValuePair("flag", flag));

		activity.request(Constants.METHOD_AUDIO_EDIT_FAVORITES, callback, params);
	}

	/**
	 * 取消收藏
	 * 
	 * @param id
	 * @param activity
	 * @param callback
	 */
	public static void unAudioFavorite(String id, String flag, final BaseActivity activity) {

		unAudioFavorite(id, flag, activity, new RequestCallback() {

			@Override
			public void requestSucc(String json) {
				activity.toast("已取消收藏！");
			}

			@Override
			public void requestFailed(int errorCode) {
				// TODO Auto-generated method stub

			}
		});
	}

	/**
	 * 取消收藏
	 * 
	 * @param id
	 * @param activity
	 * @param callback
	 */
	public static void unFavorite(String id, String flag, final BaseActivity activity) {

		unFavorite(id, flag, activity, new RequestCallback() {

			@Override
			public void requestSucc(String json) {
				activity.toast("已取消收藏！");
			}

			@Override
			public void requestFailed(int errorCode) {
				// TODO Auto-generated method stub

			}
		});
	}

	/**
	 * 对喜欢的视频点赞
	 * 
	 * @param id
	 * @param activity
	 */
	public static void like(String media_id, String type, ILikeSuccess callback, final BaseActivity activity) {
		// 如果是 音频
		if (MediaPlayerActivity.TYPE_AUDIO_VOD.equals(type)) {
			likeAudio(media_id, activity, callback);
		}
		// 其他的默认为 视频
		else {
			likeVideo(media_id, activity, callback);
		}
	}

	/**
	 * 对喜欢的视频点赞
	 * 
	 * @param id
	 * @param activity
	 * @param callback
	 */
	public static void likeVideo(String video_id, final BaseActivity activity, ILikeSuccess callback) {
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("video_id", video_id));
		activity.request(Constants.METHOD_VIDEO_LIKE, new LikeRequestCallback(callback), params);
	}

	/**
	 * 对喜欢的音频点赞
	 * 
	 * @param id
	 * @param activity
	 * @param callback
	 */
	public static void likeAudio(String audio_id, final BaseActivity activity, ILikeSuccess callback) {
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("audio_id", audio_id));
		activity.request(Constants.METHOD_AUDIO_LIKE, new LikeRequestCallback(callback), params);
	}

	/**
	 * 点赞 请求回调接口
	 * 
	 * @author yanyadi
	 * 
	 */
	private static class LikeRequestCallback implements RequestCallback {
		private ILikeSuccess mLikeSucc;

		public LikeRequestCallback(ILikeSuccess mSucc) {
			this.mLikeSucc = mSucc;
		}

		@Override
		public void requestSucc(String json) {
			if (mLikeSucc != null) {
				Gson gson = new Gson();
				LikeBean bean = gson.fromJson(json, LikeBean.class);
				if (bean != null && bean.getData() != null) {
					mLikeSucc.onSuccess(bean.getData().getTotal());
				}
			}
		}

		@Override
		public void requestFailed(int errorCode) {

		}

	}

	/**
	 * 点赞成功之后回调接口
	 * 
	 * @author yanyadi
	 * 
	 */
	public static interface ILikeSuccess {
		void onSuccess(String total);
	}

	/**
	 * 更新音频播放次数
	 * 
	 * @param id
	 * @param activity
	 * @param callback
	 */
	public static void updateAudioPlayCount(String id, String type, BaseActivity activity) {

		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("id", id));
		params.add(new BasicNameValuePair("type", type));

		activity.request(Constants.METHOD_AUDIO_PLAY, new RequestCallback() {

			@Override
			public void requestSucc(String json) {
			}

			@Override
			public void requestFailed(int errorCode) {
			}
		}, params);
	}

	/**
	 * 更新视频播放次数
	 * 
	 * @param id
	 * @param activity
	 * @param callback
	 */
	public static void updateVideoPlayCount(String id, String type, BaseActivity activity) {

		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("id", id));
		params.add(new BasicNameValuePair("type", type));

		activity.request(Constants.METHOD_VIDEO_PLAY, new RequestCallback() {

			@Override
			public void requestSucc(String json) {
			}

			@Override
			public void requestFailed(int errorCode) {
			}
		}, params);
	}

}
