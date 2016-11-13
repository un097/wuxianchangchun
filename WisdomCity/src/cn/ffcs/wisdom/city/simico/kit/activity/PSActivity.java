package cn.ffcs.wisdom.city.simico.kit.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import cn.ffcs.wisdom.city.R;
import cn.ffcs.wisdom.city.simico.activity.ActivityHelper;
import cn.ffcs.wisdom.city.simico.base.Constants;
import cn.ffcs.wisdom.city.simico.kit.application.BaseApplication;
import cn.ffcs.wisdom.city.simico.kit.log.TLog;
import cn.ffcs.wisdom.city.simico.ui.notify.PinterestToast;
import cn.ffcs.wisdom.city.simico.ui.notify.WaitDialog;

//import com.actionbarsherlock.app.SherlockFragmentActivity;

public abstract class PSActivity extends FragmentActivity implements VisibilityControl,
		OnClickListener {
	private static final String TAG = PSActivity.class.getSimpleName();
	private boolean _isVisible;
	private WaitDialog _waitDialog;
	private BroadcastReceiver existReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			finish();
		}
	};

	public abstract int getLayoutId();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		onBeforeSetContent(savedInstanceState);
		if(getLayoutId()!=0) {
			setContentView(getLayoutId());
		}
		BaseApplication.saveDisplaySize(this);
		init();
		init(savedInstanceState);
		IntentFilter filter = new IntentFilter(Constants.INTENT_ACTION_EXIT_APP);
		registerReceiver(existReceiver, filter);
	}

	protected void onBeforeSetContent(Bundle savedInstanceState) {
	}

	protected void init(Bundle savedInstanceState) {
	}

	@Override
	protected void onPause() {
		_isVisible = false;
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		_isVisible = true;
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(existReceiver);
		super.onDestroy();
	}

	@Override
	public boolean isVisible() {
		return _isVisible;
	}

	protected void init() {
	}

	public void showToast(int msgResid, int icon, int gravity) {
		showToast(getString(msgResid), icon, gravity);
	}

	public void showToast(String message, int icon, int gravity) {
		PinterestToast toast = new PinterestToast(this);
		toast.setMessage(message);
		toast.setMessageIc(icon);
		toast.setLayoutGravity(gravity);
		toast.show();
	}

	public WaitDialog showWaitDialog() {
		return showWaitDialog(R.string.loading);
	}

	public WaitDialog showWaitDialog(int resid) {
		return showWaitDialog(getString(resid));
	}

	public WaitDialog showWaitDialog(String message) {
		if (_isVisible) {
			if (_waitDialog == null) {
				_waitDialog = ActivityHelper.getWaitDialog(this, message);
			} else
				_waitDialog.setMessage(message);
			_waitDialog.show();
			return _waitDialog;
		}
		return null;
	}

	public WaitDialog showCancelableWaitDialog(int resid,
			OnCancelListener listener) {
		if (_isVisible) {
			if (_waitDialog == null) {
				_waitDialog = ActivityHelper.getCancelableWaitDialog(this,
						getString(resid));
			} else
				_waitDialog.setMessage(getString(resid));
			_waitDialog.setOnCancelListener(listener);
			_waitDialog.show();
			return _waitDialog;
		}
		return null;
	}

	public void hideWaitDialog() {
		if (_isVisible && _waitDialog != null) {
			try {
				_waitDialog.dismiss();
				_waitDialog = null;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public void onClick(View v) {
	}

	protected void recycleBitmap(ImageView view) {
		if(view == null) return;
		BitmapDrawable bitmapDrawable = (BitmapDrawable) view.getDrawable();
		if (bitmapDrawable != null) {
			view.setImageBitmap(null);
			// 如果图片还未回收，先强制回收该图片
			if (bitmapDrawable.getBitmap()!=null && !bitmapDrawable.getBitmap().isRecycled()) {
				TLog.log("回收图片");
				bitmapDrawable.getBitmap().recycle();
			}
		}
	}
	
	protected void unbindDrawables(View view) {
	    if (view.getBackground() != null) {
	    	TLog.log(TAG, "unbindDrawable:" + view);
	        view.getBackground().setCallback(null);
	    }
	    if (view instanceof ViewGroup) {
	        for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
	            unbindDrawables(((ViewGroup) view).getChildAt(i));
	        }
	        ((ViewGroup) view).removeAllViews();
	    }
	}
}