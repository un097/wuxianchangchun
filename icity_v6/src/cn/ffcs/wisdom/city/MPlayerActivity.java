package cn.ffcs.wisdom.city;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;
import cn.ffcs.ui.tools.AlertBaseHelper;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.tools.Log;

public class MPlayerActivity extends WisdomCityActivity {

	private GestureDetector gestureDetector; // 手势判断

	// component
	private VideoView mVideoView;
	private ImageView mImg;

	private MediaController mMediaController;

	// data var
	private String mRtspUrl;
	private int mPositionWhenPaused = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setBackgroundDrawableResource(R.color.black);
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (mVideoView != null) {
			mPositionWhenPaused = mVideoView.getCurrentPosition();
			mVideoView.stopPlayback();
			Log.d("Stop MPlayer Position on : " + mPositionWhenPaused);
			Log.d("Stop MPlayer Duration on : " + mVideoView.getDuration());
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (mPositionWhenPaused > 0) {
			mVideoView.seekTo(mPositionWhenPaused);
			mPositionWhenPaused = -1;
		}
	}

	@Override
	protected void initComponents() {
		mVideoView = (VideoView) findViewById(R.id.videoView);
		mImg = (ImageView) findViewById(R.id.imgView);
	}

	private class OnPreparedListener implements android.media.MediaPlayer.OnPreparedListener {
		@Override
		public void onPrepared(MediaPlayer mp) {
//			mp.setLooping(false);
			mp.setScreenOnWhilePlaying(true);
			mImg.setVisibility(View.GONE);
		}
	}

	private class OnErrorListener implements android.media.MediaPlayer.OnErrorListener {
		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			Log.e("MediaErrorWhat: " + what);
			Log.e("MediaErrorExtra: " + extra);
			AlertBaseHelper.showMessage(mActivity, getString(R.string.mplayer_error),
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							AlertBaseHelper.dismissAlert(mActivity);
							finish();
						}
					});
			return true;
		}
	}

	private class OnCompletionListener implements android.media.MediaPlayer.OnCompletionListener {
		@Override
		public void onCompletion(MediaPlayer mp) {
			Toast.makeText(mContext, R.string.mplayer_end, Toast.LENGTH_LONG).show();
			finish();
		}
	}

	@Override
	protected int getMainContentViewId() {
		return R.layout.act_mplayer;
	}

	@Override
	protected Class<?> getResouceClass() {
		return R.class;
	}

	@Override
	protected void initData() {
		// get data
		mRtspUrl = getIntent().getStringExtra(Key.U_BROWSER_URL);
		if (mRtspUrl.indexOf("?") > 0) {
			mRtspUrl = mRtspUrl + "&ffcs=city";
		} else {
			mRtspUrl = mRtspUrl + "?ffcs=icity";
		}
		if (mRtspUrl != null) {
			// load url
			Log.d("Rtsp Url : " + mRtspUrl);
			gestureDetector = new GestureDetector(mContext, new mGestureDetector());
			// init videoview
			mMediaController = new MediaController(this);
			mVideoView.setMediaController(mMediaController);
			mVideoView.setOnPreparedListener(new OnPreparedListener());
			mVideoView.setOnErrorListener(new OnErrorListener());
			mVideoView.setOnCompletionListener(new OnCompletionListener());
			mVideoView.setVideoURI(Uri.parse(mRtspUrl));
			mVideoView.requestFocus();
			mVideoView.start();
		} else {
			Toast.makeText(mContext, "获取视频地址失败", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (gestureDetector.onTouchEvent(event)) {
			return true;
		}
		return false;
	}

	public class mGestureDetector extends SimpleOnGestureListener {

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			int type = MPlayerActivity.this.getResources().getConfiguration().orientation;
			switch (type) {
			case Configuration.ORIENTATION_LANDSCAPE:
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				break;
			case Configuration.ORIENTATION_PORTRAIT:
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				break;
			}
			return true;
		}
	}
}
