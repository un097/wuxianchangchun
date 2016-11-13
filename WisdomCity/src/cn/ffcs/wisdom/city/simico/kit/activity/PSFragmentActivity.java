package cn.ffcs.wisdom.city.simico.kit.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import cn.ffcs.wisdom.city.R;
import cn.ffcs.wisdom.city.simico.activity.ActivityHelper;
import cn.ffcs.wisdom.city.simico.base.Constants;
import cn.ffcs.wisdom.city.simico.ui.notify.PinterestToast;
import cn.ffcs.wisdom.city.simico.ui.notify.WaitDialog;

//import com.actionbarsherlock.app.SherlockFragmentActivity;

public class PSFragmentActivity extends FragmentActivity implements
		DialogControl, VisibilityControl, OnClickListener {
	private boolean _isVisible;
	private WaitDialog _waitDialog;
	private BroadcastReceiver existReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			finish();
		}
	};

	public PSFragmentActivity() {
	}

	public Fragment getActiveFragment() {
		return null;
	}

	@Override
	public boolean isVisible() {
		return _isVisible;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		beforeSetContentLayout();
		if (getLayoutId() != 0) {
			setContentView(getLayoutId());
		}
		init();
		IntentFilter filter = new IntentFilter(Constants.INTENT_ACTION_EXIT_APP);
		registerReceiver(existReceiver, filter);
	}

	protected void beforeSetContentLayout() {
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(existReceiver);
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		_isVisible = false;
		hideWaitDialog();
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

	protected int getLayoutId() {
		return 0;
	}

	protected void init() {
	}

	public void refresh() {
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

	@Override
	public WaitDialog showWaitDialog() {
		return showWaitDialog(R.string.loading);
	}

	@Override
	public WaitDialog showWaitDialog(int resid) {
		return showWaitDialog(getString(resid));
	}

	@Override
	public WaitDialog showWaitDialog(String message) {
		if (_isVisible) {
			if (_waitDialog == null) {
				_waitDialog = ActivityHelper.getWaitDialog(this, message);
			}
			if (_waitDialog != null) {
				_waitDialog.setMessage(message);
				_waitDialog.show();
			}
			return _waitDialog;
		}
		return null;
	}

	@Override
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
}
