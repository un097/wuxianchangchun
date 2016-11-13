package com.ctbri.wxcc.shake;

import java.io.IOException;

import com.ctbri.comm.util._Utils;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.webkit.JavascriptInterface;

public class JSKit {
	private MediaPlayer mMediaPlayer;
	private Activity mContext;
	public JSKit(Activity context){
		this.mContext = context;
	}
	
	@JavascriptInterface
	public void palyShakeSound(){
		AssetManager assetManager =  mContext.getAssets();
		mMediaPlayer = new MediaPlayer();
		try {
			AssetFileDescriptor fileDescriptor = assetManager.openFd("shakeMusic.mp3");
			mMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
			                              fileDescriptor.getStartOffset(),
			                              fileDescriptor.getLength());
			mMediaPlayer.prepare();
			mMediaPlayer.start();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@JavascriptInterface
	public void login(){
		//去登录
		_Utils.login(mContext);
		//finish当前活动页
//		AppManager.getAppManager().finishActivity(SingMainActivity.class);
		AppManager.getAppManager().finishActivity();
	}
}
