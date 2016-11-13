package cn.ffcs.wisdom.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import cn.ffcs.wisdom.skin.SkinManager;

public abstract class BaseFragmentActicity extends FragmentActivity {
	protected Context mContext;
	protected Activity mActivity;
	private BroadcastReceiver eixtBroadcastReceiver;

	public static final String ACTION_EXIT_APP = "action_exit_app";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); // remove title

		setContentView(getMainContentViewId()); // set view

		registerExitReceiver();
		
//		registerException();

		mContext = getApplicationContext(); // get context
		mActivity = this;

		initComponents(); // init all components

		initData(); // init the whole activity's data
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		// if skin change, execute this method
		if (mSkinChange) {
			onSkin();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unRegisterExitReceiver();
	}

	/**
	 * 注册退出应用广播,默认注册
	 * 发送动作为action_exit_app的广播，可销毁所有Activity <br/>
	 * 不想当前activity被销毁，可覆盖该方法
	 */
	protected void registerExitReceiver() {
		eixtBroadcastReceiver = new ExitBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_EXIT_APP);
		registerReceiver(eixtBroadcastReceiver, filter);
	}

	/**
	 * 注销广播
	 * 当前activity销毁，自动调用该方法
	 */
	protected void unRegisterExitReceiver() {
		if (eixtBroadcastReceiver != null) {
			unregisterReceiver(eixtBroadcastReceiver);
			eixtBroadcastReceiver = null;
		}
	}

	/**
	 * 退出应用程序
	 * 发送动作为action_exit_app的广播，销毁所有注册了广播的activity
	 */
	public void exitApp() {
		sendBroadcast(new Intent(ACTION_EXIT_APP));
	}

	// 接收退出广播
	private class ExitBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			finish();
		}
	}

	/**
	 * 初始化UI组件及数据
	 */
	protected abstract void initComponents();

	/**
	 * 初始化数据
	 */
	protected abstract void initData();

	/**
	 * 布局ID
	 */
	protected abstract int getMainContentViewId();

	protected abstract Class<?> getResouceClass();

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
}
