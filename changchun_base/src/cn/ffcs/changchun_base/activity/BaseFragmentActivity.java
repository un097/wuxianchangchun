package cn.ffcs.changchun_base.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import cn.ffcs.android.usragent.UsrActionAgent;
import cn.ffcs.changchun_base.R;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.simico.activity.ActivityHelper;
import cn.ffcs.wisdom.city.simico.base.Constants;
import cn.ffcs.wisdom.city.simico.kit.activity.DialogControl;
import cn.ffcs.wisdom.city.simico.kit.application.BaseApplication;
import cn.ffcs.wisdom.city.simico.ui.notify.PinterestToast;
import cn.ffcs.wisdom.city.simico.ui.notify.WaitDialog;
import cn.ffcs.wisdom.city.utils.MenuUtil;
import cn.ffcs.wisdom.skin.SkinManager;
import cn.ffcs.wisdom.tools.GlobalExceptionHanlder;

//import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.umeng.analytics.MobclickAgent;

public class BaseFragmentActivity extends FragmentActivity implements
		DialogControl {

	protected static final String TAG = BaseFragmentActivity.class
			.getSimpleName();

	protected Context mContext;
	protected Activity mActivity;

	private BroadcastReceiver exitBroadcastReceiver;

	protected static String pkgName;

	// public static final String ACTION_EXIT_APP = "action_exit_app";
	public static final String ACTION_EXIT_APP = Constants.INTENT_ACTION_EXIT_APP;

	private WaitDialog _waitDialog;

	public BaseFragmentActivity() {
	}

	@Override
	protected void onCreate(Bundle bundle) {
		pkgName = this.getPackageName();
		super.onCreate(bundle);
		if (getLayoutId() != 0) {
			setContentView(getLayoutId());
		}
		mContext = getApplicationContext();
		mActivity = this;
		BaseApplication.saveDisplaySize(this);
		init(bundle);
		registerExitReceiver();
		registerException();

		if (!Config.isSuccess()) {
			Config.init(mContext);
		}
	}

	protected int getLayoutId() {
		return 0;
	}

	protected void init(Bundle savedInstanceState) {
	}

	@Override
	protected void onResume() {
		super.onResume();

		// if skin change, execute this method
		if (mSkinChange) {
			onSkin();
		}

		MobclickAgent.onResume(mContext);

		UsrActionAgent.onResume(mContext);

		if (!Config.isSuccess()) {
			Config.init(mContext);
		}

		boolean loadSuccess = MenuMgr.getInstance().getLoadSuccess();
		if (!loadSuccess) {
			String cityCode = MenuUtil.getCityCode(mContext);
			MenuMgr.getInstance().refreshMenu(mContext, cityCode);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unRegisterExitReceiver();
	}

	public void exitApp() {
		LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(ACTION_EXIT_APP));
	}

	protected void registerExitReceiver() {
		exitBroadcastReceiver = new ExitBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_EXIT_APP);
		LocalBroadcastManager.getInstance(mContext).registerReceiver(
				exitBroadcastReceiver, filter);
	}

	protected void unRegisterExitReceiver() {
		if (exitBroadcastReceiver != null) {
			LocalBroadcastManager.getInstance(mContext).unregisterReceiver(
					exitBroadcastReceiver);
			exitBroadcastReceiver = null;
		}
	}

	private class ExitBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			finish();
		}
	}

	/**
	 * 注册全局异常处理类
	 */
	private void registerException() {
		GlobalExceptionHanlder.getInstance().register(mContext);
	}

	// ================================
	// skin
	// ================================
	private DataSetObserver mSkinObserver;
	private boolean mSkinChange = Boolean.TRUE;

	/**
	 * 
	 * Use this method to change the application skin after initComponents().
	 */
	protected void onSkin() {
		mSkinChange = Boolean.FALSE;
	}

	protected void registerSkinObserver() {
		if (mSkinObserver != null) {
			unregisterSkinObserver(mSkinObserver);
			mSkinObserver = null;
		}

		mSkinObserver = new SkinChangeObserver();
		SkinManager.registerObserver(mSkinObserver);
	}

	protected void unregisterSkinObserver(DataSetObserver observer) {
		SkinManager.unregisterDataSetObserver(observer);
	}

	// 观察菜单数据是否变化
	class SkinChangeObserver extends DataSetObserver {
		@Override
		public void onChanged() {
			mSkinChange = Boolean.TRUE;
		}
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
		if (_waitDialog == null) {
			_waitDialog = ActivityHelper.getWaitDialog(this, message);
		}
		if (_waitDialog != null) {
			_waitDialog.setMessage(message);
			_waitDialog.show();
		}
		return _waitDialog;
	}

	@Override
	public void hideWaitDialog() {
		if (_waitDialog != null) {
			try {
				_waitDialog.dismiss();
				_waitDialog = null;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

}
