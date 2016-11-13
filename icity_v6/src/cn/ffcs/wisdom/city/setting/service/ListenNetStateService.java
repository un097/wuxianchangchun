package cn.ffcs.wisdom.city.setting.service;

import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.tools.SharedPreferencesUtil;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.widget.Toast;

public class ListenNetStateService extends Service {
	private ConnectivityManager connectivityManager;
	private NetworkInfo info;
	private boolean isFirst = true;

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				info = connectivityManager.getActiveNetworkInfo();
				if (info != null && info.isAvailable()) {
					SharedPreferencesUtil.setInteger(context, Key.K_NETWORK_TYPE, info.getType());
					if (info.getType() == 0 && isFirst) {
						Toast.makeText(context, "友情提醒：您现在使用的是3G流量", Toast.LENGTH_SHORT).show();
						isFirst = false;
					} else if (info.getType() != 0) {
						isFirst = true;
					}
				}
//				if (info != null && info.isAvailable()) {
//					String name = info.getTypeName();
//					Log.d("mark", "当前网络名称：" + name);
//				} else {
//					Log.d("mark", "没有可用网络");
//				}
			}
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(mReceiver, mFilter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}
}