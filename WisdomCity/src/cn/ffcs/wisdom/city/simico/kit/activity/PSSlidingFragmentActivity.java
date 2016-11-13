//package com.simico.creativelocker.kit.activity;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.DialogInterface.OnCancelListener;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.graphics.drawable.BitmapDrawable;
//import android.os.Bundle;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.ImageView;
//
//import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
//import com.nostra13.universalimageloader.core.ImageLoader;
//import com.hive.android.locker.gem.R;
//import com.simico.creativelocker.activity.ActivityHelper;
//import com.simico.creativelocker.base.Constants;
//import com.simico.creativelocker.kit.application.BaseApplication;
//import com.simico.creativelocker.kit.log.TLog;
//import com.simico.creativelocker.ui.notify.PinterestToast;
//import com.simico.creativelocker.ui.notify.WaitDialog;
//
//public abstract class PSSlidingFragmentActivity extends SlidingFragmentActivity implements VisibilityControl,
//		OnClickListener {
//	// protected ImageLoader imageLoader = ImageLoader.getInstance();
//	private boolean _isVisible;
//	private WaitDialog _waitDialog;
//	private BroadcastReceiver existReceiver = new BroadcastReceiver() {
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			TLog.log("收到退出广播..准备退出Activity:" + PSSlidingFragmentActivity.this);
//			finish();
//		}
//	};
//
//	public abstract int getLayoutId();
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		if(getLayoutId()!=0) {
//			setContentView(getLayoutId());
//		}
//		BaseApplication.saveDisplaySize(this);
//		init();
//		init(savedInstanceState);
//		IntentFilter filter = new IntentFilter(Constants.INTENT_ACTION_EXIT_APP);
//		registerReceiver(existReceiver, filter);
//	}
//
//	protected void init(Bundle savedInstanceState) {
//	}
//
//	@Override
//	protected void onPause() {
//		_isVisible = false;
//		super.onPause();
//	}
//
//	@Override
//	protected void onStop() {
//		super.onStop();
//	}
//
//	@Override
//	protected void onStart() {
//		super.onStart();
//	}
//
//	@Override
//	protected void onResume() {
//		_isVisible = true;
//		//if (System.currentTimeMillis() - Application.getLastClearImageCache() > 24 * 3600 * 1000l) {
//			//ImageLoader.getInstance().clearMemoryCache();
//			//ImageLoader.getInstance().clearDiscCache();
//			//Application.setLastClearImageCache(System.currentTimeMillis());
//		//}
//		super.onResume();
//	}
//
//	@Override
//	protected void onDestroy() {
//		unregisterReceiver(existReceiver);
//		existReceiver = null;
//		ImageLoader.getInstance().clearMemoryCache();
//		super.onDestroy();
//	}
//
//	@Override
//	public boolean isVisible() {
//		return _isVisible;
//	}
//
//	protected void init() {
//	}
//
//	public void showToast(int msgResid, int icon, int gravity) {
//		showToast(getString(msgResid), icon, gravity);
//	}
//
//	public void showToast(String message, int icon, int gravity) {
//		PinterestToast toast = new PinterestToast(this);
//		toast.setMessage(message);
//		toast.setMessageIc(icon);
//		toast.setLayoutGravity(gravity);
//		toast.show();
//	}
//
//	public WaitDialog showWaitDialog() {
//		return showWaitDialog(R.string.loading);
//	}
//
//	public WaitDialog showWaitDialog(int resid) {
//		return showWaitDialog(getString(resid));
//	}
//
//	public WaitDialog showWaitDialog(String message) {
//		if (_isVisible) {
//			if (_waitDialog == null) {
//				_waitDialog = ActivityHelper.getWaitDialog(this, message);
//			} else
//				_waitDialog.setMessage(message);
//			_waitDialog.show();
//			return _waitDialog;
//		}
//		return null;
//	}
//
//	public WaitDialog showCancelableWaitDialog(int resid,
//			OnCancelListener listener) {
//		if (_isVisible) {
//			if (_waitDialog == null) {
//				_waitDialog = ActivityHelper.getCancelableWaitDialog(this,
//						getString(resid));
//			} else
//				_waitDialog.setMessage(getString(resid));
//			_waitDialog.setOnCancelListener(listener);
//			_waitDialog.show();
//			return _waitDialog;
//		}
//		return null;
//	}
//
//	public void hideWaitDialog() {
//		if (_isVisible && _waitDialog != null) {
//			try {
//				_waitDialog.dismiss();
//				_waitDialog = null;
//			} catch (Exception ex) {
//				ex.printStackTrace();
//			}
//		}
//	}
//
//	@Override
//	public void onClick(View v) {
//	}
//
//	protected void recycleBitmap(ImageView view) {
//		if(view == null) return;
//		BitmapDrawable bitmapDrawable = (BitmapDrawable) view.getDrawable();
//		if (bitmapDrawable != null) {
//			view.setImageBitmap(null);
//			// 如果图片还未回收，先强制回收该图片
//			if (bitmapDrawable.getBitmap()!=null && !bitmapDrawable.getBitmap().isRecycled()) {
//				TLog.log("回收图片");
//				bitmapDrawable.getBitmap().recycle();
//			}
//		}
//	}
//}