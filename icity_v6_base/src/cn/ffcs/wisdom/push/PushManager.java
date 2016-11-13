package cn.ffcs.wisdom.push;

import android.content.Context;
import cn.ffcs.wisdom.tools.AlarmManagerUtil;

/**
 * <h3>推送管理器      </h3>
 * <p>启动和关闭推送。</p>
 * <p>启动推送前，可以:PushUtil.setPushFrequency()进行定时频率设置</p>
 */
public class PushManager {
	/**
	 * 启动推送
	 * @param context
	 * 		上下文
	 * @param recevier
	 * 		定时广播触发接收器
	 */
	public static void startPush(Context context, Class<?> recevier) {
		boolean isEnable = PushUtil.getPushEnabled(context);
		if (isEnable) {
			long frequency = PushUtil.getPushFrequency(context);
			AlarmManagerUtil.startAlarmRepeat(context, recevier, frequency, null);
		}
	}

	/**
	 * 关闭推送
	 * @param context
	 * 		上下文
	 * @param recevier
	 * 		定时广播触发接收器
	 */
	public static void stopPush(Context context, Class<?> recevier) {
		boolean resident = PushUtil.getPushResident(context);
		if (!resident) { // 是否常驻
			AlarmManagerUtil.stopAlarmRepeat(context, recevier);
		}
	}

}
