package cn.ffcs.wisdom.city.report;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.tools.AlarmManagerUtil;
import cn.ffcs.wisdom.tools.StringUtil;

public class ReportReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

		if (!Config.isSuccess()) {
			System.out.println("icity stop...");
			AlarmManagerUtil.stopAlarmRepeat(context, ReportReceiver.class);
			return;
		}

		String myAction = Config.REPORT_ACTION;
		if (!StringUtil.isEmpty(myAction)) {
			if (myAction.equals(action)) {
				ReportManager.getInstance(context).start();
			}
		}
	}
}
