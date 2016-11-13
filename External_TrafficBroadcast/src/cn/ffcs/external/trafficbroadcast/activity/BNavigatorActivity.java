package cn.ffcs.external.trafficbroadcast.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import cn.ffcs.tts.utils.TtsSpeechApi;
import cn.ffcs.tts.utils.TtsSpeechApi.SpeechDelegate;

import com.baidu.navisdk.BaiduNaviManager;
import com.baidu.navisdk.comapi.mapcontrol.BNMapController;
import com.baidu.navisdk.comapi.routeplan.BNRoutePlaner;
import com.baidu.navisdk.comapi.tts.BNTTSPlayer;
import com.baidu.navisdk.comapi.tts.BNavigatorTTSPlayer;
import com.baidu.navisdk.comapi.tts.IBNTTSPlayerListener;
import com.baidu.navisdk.model.datastruct.LocData;
import com.baidu.navisdk.model.datastruct.SensorData;
import com.baidu.navisdk.ui.routeguide.BNavigator;
import com.baidu.navisdk.ui.routeguide.IBNavigatorListener;
import com.baidu.navisdk.ui.widget.RoutePlanObserver;
import com.baidu.navisdk.ui.widget.RoutePlanObserver.IJumpToDownloadListener;
import com.baidu.nplatform.comapi.map.MapGLSurfaceView;

public class BNavigatorActivity extends Activity implements SpeechDelegate {
	private String mBNavMsg = "";
	private int count = 0;
	private boolean isNavStarted = false;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0) {// 进行语音导航
				handler.removeCallbacks(mSpeechRun);
				handler.postDelayed(mSpeechRun, 0);
			}
			if (1 == msg.what) {// 停止播报
//				System.out.println("-----导航结束------");
				count = 0;
				isNavStarted = false;
				handler.removeCallbacks(mSpeechRun);
				TtsSpeechApi.getIntance().stopSpeaking();
				finish();
			}
		}
	};

	private Runnable mSpeechRun = new Runnable() {
		@Override
		public void run() {
			// 开始语音合成
			TtsSpeechApi.getIntance().toSpeech(BNavigatorActivity.this,
					mBNavMsg, 0);
		}
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 创建NmapView
		if (Build.VERSION.SDK_INT < 14) {
			BaiduNaviManager.getInstance().destroyNMapView();
		}
		MapGLSurfaceView nMapView = BaiduNaviManager.getInstance()
				.createNMapView(this);

		// 创建导航视图
		View navigatorView = BNavigator.getInstance().init(
				BNavigatorActivity.this, getIntent().getExtras(), nMapView);

		// 填充视图
		setContentView(navigatorView);
		BNavigator.getInstance().setListener(mBNavigatorListener);
		BNavigator.getInstance().startNav();

		// 初始化TTS. 开发者也可以使用独立TTS模块，不用使用导航SDK提供的TTS
		BNTTSPlayer.initPlayer();
		// 设置TTS播放回调
		BNavigatorTTSPlayer.setTTSPlayerListener(new IBNTTSPlayerListener() {

			@Override
			public int playTTSText(String arg0, int arg1) {
				count++;
//				System.out.println("百度导航提示信息tts>>>" + count + arg0);
				if (arg0.contains("导航开始")) {
					count = 1;
//					System.out.println("count>" + count);
					mBNavMsg = arg0;
					isNavStarted = true;
				} else if (count <= 2) {
					mBNavMsg = mBNavMsg + " " + arg0;
					if (2 == count) {
						// 进行语音导航
						handler.obtainMessage(0).sendToTarget();
					}
				} else if (isNavStarted) {
					mBNavMsg = arg0;
					// 进行语音导航
					handler.obtainMessage(0).sendToTarget();
				} else {
					mBNavMsg = "";
				}
				// TtsSpeechApi.getIntance().toSpeech(BNavigatorActivity.this,
				// arg0, 0);
				// //开发者可以使用其他TTS的API
				// return BNTTSPlayer.playTTSText(arg0, arg1);
				return 0;
			}

			@Override
			public void phoneHangUp() {
				// 手机挂断
			}

			@Override
			public void phoneCalling() {
				// 通话中
			}

			@Override
			public int getTTSState() {
				// 开发者可以使用其他TTS的API,
				return BNTTSPlayer.getTTSState();
			}
		});

		BNRoutePlaner.getInstance().setObserver(
				new RoutePlanObserver(this, new IJumpToDownloadListener() {

					@Override
					public void onJumpToDownloadOfflineData() {
						// TODO Auto-generated method stub

					}
				}));

	}

	private IBNavigatorListener mBNavigatorListener = new IBNavigatorListener() {

		@Override
		public void onYawingRequestSuccess() {
			// TODO 偏航请求成功

		}

		@Override
		public void onYawingRequestStart() {
			// TODO 开始偏航请求

		}

		@Override
		public void onPageJump(int jumpTiming, Object arg) {
			// TODO 页面跳转回调
			if (IBNavigatorListener.PAGE_JUMP_WHEN_GUIDE_END == jumpTiming) {
				finish();
			} else if (IBNavigatorListener.PAGE_JUMP_WHEN_ROUTE_PLAN_FAIL == jumpTiming) {
				finish();
			}
		}

		@Override
		public void notifyGPSStatusData(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void notifyLoacteData(LocData arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void notifyNmeaData(String arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void notifySensorData(SensorData arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void notifyStartNav() {
			// TODO Auto-generated method stub
			BaiduNaviManager.getInstance().dismissWaitProgressDialog();
		}

		@Override
		public void notifyViewModeChanged(int arg0) {
			// TODO Auto-generated method stub

		}

	};

	@Override
	public void onResume() {
		BNavigator.getInstance().resume();
		super.onResume();
		BNMapController.getInstance().onResume();
	};

	@Override
	public void onPause() {
		BNavigator.getInstance().pause();
		super.onPause();
		BNMapController.getInstance().onPause();
		// //退出导航页面停止语音播报
		// BNTTSPlayer.stopTTS();
		// 停止语音导航
		handler.obtainMessage(1).sendToTarget();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		BNavigator.getInstance().onConfigurationChanged(newConfig);
		super.onConfigurationChanged(newConfig);
	}

	public void onBackPressed() {
		BNavigator.getInstance().onBackPressed();
	}

	@Override
	public void onDestroy() {
		BNavigator.destory();
		BNRoutePlaner.getInstance().setObserver(null);
		super.onDestroy();
	}

	@Override
	public void onSpeakBegin() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSpeakPaused() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSpeakResumed() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBufferProgress(int percent, int beginPos, int endPos,
			String info) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSpeakProgress(int percent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCompleted() {
		TtsSpeechApi.getIntance().stopSpeaking();
	}
}
