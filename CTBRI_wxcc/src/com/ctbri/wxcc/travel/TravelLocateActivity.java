package com.ctbri.wxcc.travel;

import com.ctbri.wxcc.community.BaseActivity;

public class TravelLocateActivity extends BaseActivity {

//	public static final String LTAG = TravelLocateActivity.class.getName();
//	/**
//	 * 构造广播监听类，监听 SDK key 验证以及网络异常广播
//	 */
//	public class SDKReceiver extends BroadcastReceiver {
//		public void onReceive(Context context, Intent intent) {
//			String s = intent.getAction();
//			Log.d(LTAG, "action: " + s);
//			if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
//				toast("key 验证出错! 请在 AndroidManifest.xml 文件中检查 key 设置");
//			} else if (s
//					.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
//				toast("网络出错");
//			}
//		}
//	}
//	SDKReceiver mReceiver;
//	@Override
//	protected void onCreate(Bundle arg0) {
//		super.onCreate(arg0);
//
//		getSupportFragmentManager()
//				.beginTransaction()
//				.replace(android.R.id.content, getMapFragment(),
//						"map_navigator").commit();
//		
//		// 注册 SDK 广播监听者
//		IntentFilter iFilter = new IntentFilter();
//		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
//		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
//		mReceiver = new SDKReceiver();
//		registerReceiver(mReceiver, iFilter);
//	}
//
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//		// 取消监听 SDK 广播
//		unregisterReceiver(mReceiver);
//	}
//	private Fragment getMapFragment() {
//		TravelLocateFragment fragment = new TravelLocateFragment();
//		if (getIntent() != null && getIntent().getExtras() != null) {
//			Bundle bd = new Bundle(getIntent().getExtras());
//			fragment.setArguments(bd);
//		}
//		return fragment;
//	}
}
