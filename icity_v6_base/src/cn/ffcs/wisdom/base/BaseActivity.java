package cn.ffcs.wisdom.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Window;
import cn.ffcs.android.usragent.UsrActionAgent;
import cn.ffcs.wisdom.skin.SkinManager;
import cn.ffcs.wisdom.tools.GlobalExceptionHanlder;

import com.umeng.analytics.MobclickAgent;

/**
 * <p> Activity 基础类 <strong>所有Activity必须继承该类。</strong></p>
 * 
 * <p> onCreate方法执行操作: <br/>
 *    <i>1.getMainContentViewId</i> 渲染界面<br/>
 *    <i>2.initComponents</i> 初始化控件<br/>
 *    <i>3.initData</i> 初始化数据，填充数据<br/>
 *    <i>4.setCustomTitleTop</i><br/>
 *    <i>5.registerExitReceiver</i> 注册退出广播<br/>
 * </p>
 * 
 * @author  caijj
 * @version 1.00, 2012-4-6
 */
public abstract class BaseActivity extends Activity {

	protected Context mContext;
	protected Activity mActivity;
	private BroadcastReceiver exitBroadcastReceiver;
	protected static Resources resource;

	protected static String pkgName;
	public static final String ACTION_EXIT_APP = "action_exit_app";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		resource = this.getResources();
		pkgName = this.getPackageName();
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); // remove title
		if (getMainContentViewId() != 0) {
			setContentView(getMainContentViewId()); // set view
		}
		mContext = getApplicationContext(); // get context
		mActivity = this;
		initComponents(); // init all components

		initData(); // init the whole activity's data

		registerExitReceiver();

		registerException();
	}

	/**
	 * 获取相应的组件的ID
	 * @param idName  key的名称，如layout的名称：loading_bar
	 * @param idType  id的类型，IdentifierType
	 * @return
	 */
	protected int getIdentifier(String idName, String idType) {
		return resource.getIdentifier(idName, idType, pkgName);
	}

	public static class IdentifierType {
		public static final String ID = "id";
		public static final String STRING = "string";
		public static final String COLOR = "color";
		public static final String TYPE = "type";
		public static final String DRAWABLE = "drawable";
		public static final String LAYOUT = "layout";
	}
	
	/**
	 * 注册全局异常处理类
	 */
	private void registerException() {
		GlobalExceptionHanlder.getInstance().register(mContext);
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
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unRegisterExitReceiver();
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(mContext);
		UsrActionAgent.onPause(mContext);
	}

	/**
	 * 注册退出应用广播,默认注册
	 * 发送动作为action_exit_app的广播，可销毁所有Activity <br/>
	 * 不想当前activity被销毁，可覆盖该方法
	 */
	protected void registerExitReceiver() {
		exitBroadcastReceiver = new ExitBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_EXIT_APP);
		LocalBroadcastManager.getInstance(mContext).registerReceiver(exitBroadcastReceiver, filter);
	}

	/**
	 * 注销广播
	 * 当前activity销毁，自动调用该方法
	 */
	protected void unRegisterExitReceiver() {
		if (exitBroadcastReceiver != null) {
			LocalBroadcastManager.getInstance(mContext).unregisterReceiver(exitBroadcastReceiver);
			exitBroadcastReceiver = null;
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
