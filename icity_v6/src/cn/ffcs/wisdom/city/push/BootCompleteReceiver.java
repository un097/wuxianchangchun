package cn.ffcs.wisdom.city.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * <p>Title: 开机广播接收器        </p>
 * <p>Description: 
 *  在开机时，系统会发出BOOT_COMPLETED的广播，应用接收该广播，并启动相关服务 
 * </p>
 * <p>@author: Eric.wsd                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2012-8-29             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class BootCompleteReceiver extends BroadcastReceiver {
	private static final String BOOT_COMPLETED_ACTION = "android.intent.action.BOOT_COMPLETED";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (BOOT_COMPLETED_ACTION.equals(intent.getAction())) {
//			Intent i = new Intent(context, PushService.class);
//			i.setAction(PushService.PUSH_SERVICE_ACTION);
//			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			context.startService(i);
//			new PushMsgBo(context).initData();
		}

	}

}
