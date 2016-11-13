package cn.ffcs.wisdom.city.web.open;

import android.content.Context;
import android.content.Intent;
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
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebStorage.QuotaUpdater;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;
import cn.ffcs.ui.tools.AlertBaseHelper;
import cn.ffcs.wisdom.city.WisdomCityActivity;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.city.web.BrowserActivity;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.Log;

public class OpenMPlayerActivity extends WisdomCityActivity {

	private GestureDetector gestureDetector; // 手势判断
	private WebView webView;

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
		webView = (WebView) findViewById(R.id.webview);
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
//			finish();
		}
	}

	@Override
	protected int getMainContentViewId() {
		return R.layout.act_open_mplayer;
	}

	@Override
	protected Class<?> getResouceClass() {
		return R.class;
	}

	@Override
	protected void initData() {
		initWebView();
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
			mVideoView.setVideoPath(mRtspUrl);
			mVideoView.requestFocus();
			mVideoView.start();
		} else {
			Toast.makeText(mContext, "获取视频地址失败", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * webview内核配置
	 */
	private class OnWebChromeClientListener extends WebChromeClient {
		@Override
		public void onExceededDatabaseQuota(String url, String databaseIdentifier,
				long currentQuota, long estimatedSize, long totalUsedQuota,
				QuotaUpdater quotaUpdater) {
			quotaUpdater.updateQuota(estimatedSize * 2);
		}

		@Override
		public void onGeolocationPermissionsShowPrompt(String origin, Callback callback) {
			callback.invoke(origin, true, false);
			super.onGeolocationPermissionsShowPrompt(origin, callback);
		}
	}

	@SuppressWarnings("deprecation")
	public void initWebView() {
		webView.setDownloadListener(new OnDownloadListener());
		webView.setWebViewClient(new MyWebViewClient());
		webView.setWebChromeClient(new OnWebChromeClientListener());
//		mBrowser.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setSupportZoom(true);
		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		webView.getSettings().setJavaScriptEnabled(true);
		if (CommonUtils.getSystemVersion() < 18) {
			webView.getSettings().setPluginsEnabled(true);// flash支持
		}
		webView.getSettings().setPluginState(PluginState.ON);// flash支持
		webView.getSettings().setDomStorageEnabled(true);
		webView.getSettings().setAllowFileAccess(true);
//		mBrowser.getSettings().setSupportMultipleWindows(true);
		webView.getSettings().setDatabaseEnabled(true);
		webView.getSettings().setGeolocationEnabled(true);
		String dir = mContext.getDir("database", Context.MODE_PRIVATE).getPath();
		Log.d("dir: " + dir);
		webView.getSettings().setDatabasePath(dir);
		webView.getSettings().setGeolocationDatabasePath(dir);
		webView.getSettings().setUserAgentString(
				webView.getSettings().getUserAgentString() + " ffcs/icity");
		Log.d("UA: " + webView.getSettings().getUserAgentString());
		String webUrl = getIntent().getStringExtra(Key.K_WEB_URL);
		if (webUrl != null) {
			webView.loadUrl(webUrl);
		}
	}

	/**
	 * 下载监听
	 */
	private class OnDownloadListener implements DownloadListener {

		@Override
		public void onDownloadStart(String url, String userAgent, String contentDisposition,
				String mimetype, long contentLength) {
			if (url.indexOf("?") > 0) {
				url = url + "&ffcs=city";
			} else {
				url = url + "?ffcs=icity";
			}
			Uri uri = Uri.parse(url);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		}
	}

	class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (!super.shouldOverrideUrlLoading(view, url)) {
				Intent i = new Intent(mActivity, BrowserActivity.class);
				i.putExtra(Key.K_OUT_URL, url);
				startActivity(i);
				finish();
			}
			return true;
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
			int type = OpenMPlayerActivity.this.getResources().getConfiguration().orientation;
			switch (type) {
			case Configuration.ORIENTATION_LANDSCAPE:
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				webView.setVisibility(View.VISIBLE);
				break;
			case Configuration.ORIENTATION_PORTRAIT:
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				webView.setVisibility(View.GONE);
				break;
			}
			return true;
		}
	}
}
