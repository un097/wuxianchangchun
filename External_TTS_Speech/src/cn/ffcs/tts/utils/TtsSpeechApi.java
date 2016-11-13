package cn.ffcs.tts.utils;

import java.lang.ref.WeakReference;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

/**
 * <p>Title:   语音播报功能                                               </p>
 * <p>Description: 
 *    在线语音合成，实现文本转化为语音
 * </p>
 * <p>@author: Eric.lbc                </p>
 * <p>Copyright: Copyright (c) 2014    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2014-12-25          </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
@SuppressLint("ShowToast")
public class TtsSpeechApi {
	private static String TAG = "TtsSpeechApi"; 
	private static TtsSpeechApi mTtsSpeechApi = null;

	public static final String APPID = "54b489fc";
	private Activity mActivity;

	// 语音合成对象
	private SpeechSynthesizer mTts;
	// 语音播报内容
	private String msg;

	private Toast mToast;
	
	private TtsSpeechApi(){
		
	}
	
	public static TtsSpeechApi getIntance() {
		if (null == mTtsSpeechApi) {
			mTtsSpeechApi = new TtsSpeechApi();
		}
		return mTtsSpeechApi;
	}
	
	public interface SpeechDelegate {
		/**
		 * 开始播放
		 */
		public void onSpeakBegin();
		
		/**
		 * 暂停播放
		 */
		public void onSpeakPaused();
		
		/**
		 * 继续播放
		 */
		public void onSpeakResumed();
		
		/**
		 * 缓冲进度
		 * @param percent
		 * @param beginPos
		 * @param endPos
		 * @param info
		 */
		public void onBufferProgress(int percent, int beginPos, int endPos,String info);
		
		/**
		 * 播放进度
		 * @param percent
		 */
		public void onSpeakProgress(int percent);
		
		/**
		 * 播放结束
		 */
		public void onCompleted();
	}

	public WeakReference<SpeechDelegate> mDelegate;
	
	public void toSpeech(Activity activity, String content, int playFlag) {
		mActivity = activity;
		mDelegate = new WeakReference<SpeechDelegate>((SpeechDelegate) activity);
		SpeechUtility.createUtility(mActivity, "appid=" + APPID);
		msg = content;
		mToast = Toast.makeText(mActivity, "", Toast.LENGTH_SHORT);
		if (0 == playFlag && TextUtils.isEmpty(content)) {
			showTip(mActivity, "播放内容为空");
			return;
		}
		// 初始化合成对象
		if (0 == playFlag || mTts == null) {
			mTts = SpeechSynthesizer.createSynthesizer(mActivity, mTtsInitListener);
			mTts.startSpeaking(msg, mTtsListener);
		}
		if (1 == playFlag) {
			pauseSpeaking();
		} else if (2 == playFlag) {
			resumeSpeaking();
		}
	}
	
	/**
	 * 暂停播放
	 */
	public void pauseSpeaking() {
		if (mTts != null) {
			mTts.pauseSpeaking();
		}
	}

	/**
	 * 继续播放
	 */
	public void resumeSpeaking() {
		if (mTts != null) {
			mTts.resumeSpeaking();
		}
	}

	/**
	 * 退出时释放连接
	 */
	public void stopSpeaking() {
		if (mTts != null) {
			mTts.stopSpeaking();
			// 退出时释放连接
			mTts.destroy();
		}
	}
	
	/**
	 * 初期化监听。
	 */
	private InitListener mTtsInitListener = new InitListener() {
		@Override
		public void onInit(int code) {
			Log.d(TAG, "InitListener init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
        		showTip(mActivity, "初始化失败,错误码："+code);
        	} else {
        		// 设置参数
    			setParam();
			}		
		}
	};
	
	/**
	 * 合成回调监听。
	 */
	private SynthesizerListener mTtsListener = new SynthesizerListener() {
		@Override
		public void onSpeakBegin() {
//			showTip(mActivity, "开始播放");
			mDelegate.get().onSpeakBegin();
		}

		@Override
		public void onSpeakPaused() {
//			showTip(mActivity, "暂停播放");
			mDelegate.get().onSpeakPaused();
		}

		@Override
		public void onSpeakResumed() {
//			showTip(mActivity, "继续播放");
			mDelegate.get().onSpeakResumed();
		}

		@Override
		public void onBufferProgress(int percent, int beginPos, int endPos,
				String info) {
			mDelegate.get().onBufferProgress(percent, beginPos, endPos, info);
		}

		@Override
		public void onSpeakProgress(int percent, int beginPos, int endPos) {
			mDelegate.get().onSpeakProgress(percent);
		}

		@Override
		public void onCompleted(SpeechError error) {
			if(error == null) {
//				showTip(mActivity, "播放完成");
				mDelegate.get().onCompleted();
			} else if(error != null) {
				showTip(mActivity, error.getPlainDescription(true));
			}
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
			
		}
	};
	
	/**
	 * 参数设置
	 * @param param
	 * @return 
	 */
	private void setParam(){
		// 清空参数
		mTts.setParameter(SpeechConstant.PARAMS, null);
		
		//设置引擎类型
		mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
		
		//设置发音人
		mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
		
		//设置语速
		mTts.setParameter(SpeechConstant.SPEED, "50");

		//设置音调
		mTts.setParameter(SpeechConstant.PITCH, "50");

		//设置音量
		mTts.setParameter(SpeechConstant.VOLUME, "50");
		
		//设置播放器音频流类型
		mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
	}
	
	private void showTip(Activity activity, final String str) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mToast.setText(str);
				mToast.show();
			}
		});
	}
	
}
