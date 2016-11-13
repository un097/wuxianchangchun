package cn.ffcs.wisdom.city.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import cn.ffcs.wisdom.city.WisdomCityActivity;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.tools.StringUtil;

public class LocalBroadcastManagerActivity extends WisdomCityActivity implements OnClickListener {

	private Button mSendBroadcast;
	private TextView mResult;
	private LocalBroadcastManager mLocalBroadcast;
	private LocalBroadcastReceiver mReceiver;

	@Override
	protected void initComponents() {
		mSendBroadcast = (Button) findViewById(R.id.sendBroadcast);
		mResult = (TextView) findViewById(R.id.result);

		mSendBroadcast.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		mLocalBroadcast = LocalBroadcastManager.getInstance(mContext);

		mLocalBroadcast.registerReceiver(mReceiver, new IntentFilter(
				"cn.ffcs.wisdom.city.test.LocalBroadcastManagerActivity.LocalBroadcastReceiver"));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		mLocalBroadcast.unregisterReceiver(mReceiver);
	}

	@Override
	protected int getMainContentViewId() {
		return R.layout.act_localbroadcast_test;
	}

	class LocalBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String result = intent.getStringExtra("LocalBroadcastReceiver");
			if (!StringUtil.isEmpty(result)) {
				mResult.setText(result);
			}
		}

	}

	@Override
	public void onClick(View v) {
		String result = "已经收到广播消息！";
		Intent i = new Intent("cn.ffcs.wisdom.city.test.LocalBroadcastManagerActivity.LocalBroadcastReceiver");
		i.putExtra("LocalBroadcastReceiver", result);
		mLocalBroadcast.sendBroadcastSync(i);

	}

}
