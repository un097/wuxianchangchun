package cn.ffcs.wisdom.push;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.SharedPreferencesUtil;

/**
 * <h3>推送工具类</h3>
 * <p>1. 设置和获取推送声音   </p>
 * <p>2. 设置和获取推送震动    </p>
 * <p>3. 设置和获取推送频率   </p>
 */
public class PushUtil {

	/**
	 * 设置推送常驻
	 * @param context
	 * 		上下文
	 * @param enable
	 * 		是否常驻
	 */
	public static void setPushResident(Context context, boolean enable) {
		SharedPreferencesUtil.setBoolean(context, PushConstant.SETTINGS_PUSH_RESIDENT, enable);
	}

	/**
	 * 获取推送是否常驻
	 * @param context
	 * 		上下文
	 * @return
	 * 		返回是否常驻，boolean
	 */
	public static boolean getPushResident(Context context) {
		return SharedPreferencesUtil.getBoolean(context, PushConstant.SETTINGS_PUSH_RESIDENT,
				PushConstant.DEFAULT_PUSH_RESIDENT);
	}

	/**
	 * 设置推送是否开启
	 * @param context
	 * 		上下文
	 * @param enable
	 * 		是否开启
	 */
	public static void setPushEnabled(Context context, boolean enable) {
		SharedPreferencesUtil.setBoolean(context, PushConstant.SETTINGS_PUSH_ENABLED, enable);
	}

	/**
	 * 获取推送开关，默认开启
	 * @param context
	 * 		上下文
	 * @return
	 * 		是否开启推送，boolean
	 */
	public static boolean getPushEnabled(Context context) {
		return SharedPreferencesUtil.getBoolean(context, PushConstant.SETTINGS_PUSH_ENABLED,
				PushConstant.DEFAULT_PUSH_ENABLED);
	}

	/**
	 * 设置推送声音开关
	 * @param context
	 * 		上下文
	 * @param enable
	 * 		是否开启声音
	 */
	public static void setPushSoundEnabled(Context context, boolean enable) {
		SharedPreferencesUtil.setBoolean(context, PushConstant.SETTINGS_PUSH_SOUND_ENABLED, enable);
	}

	/**
	 * 获取声音设置，默认开启
	 * @param context
	 * 		上下文
	 * @return
	 * 		是否开启声音，boolean
	 */
	public static boolean getPushSoundEnabled(Context context) {
		return SharedPreferencesUtil.getBoolean(context, PushConstant.SETTINGS_PUSH_SOUND_ENABLED,
				PushConstant.DEFAULT_PUSH_SOUND);
	}

	/**
	 * 设置推送震动开关
	 * @param context
	 * 		上下文
	 * @param enable
	 * 		是否开启
	 */
	public static void setPushVibrateEnabled(Context context, boolean enable) {
		SharedPreferencesUtil.setBoolean(context, PushConstant.SETTINGS_PUSH_VIBRATE_ENABLED,
				enable);
	}

	/**
	 * 获取震动设置，默认关闭
	 * @param context
	 * 		上下文
	 * @return
	 * 		是否开启，boolean
	 */
	public static boolean getPushVibrateEnabled(Context context) {
		return SharedPreferencesUtil.getBoolean(context,
				PushConstant.SETTINGS_PUSH_VIBRATE_ENABLED, PushConstant.DEFAULT_PUSH_VIBRATE);
	}

	/**
	 * 设置推送频率
	 * @param context
	 * 		上下文
	 * @param frequency
	 * 		频率
	 */
	public static void setPushFrequency(Context context, long frequency) {
		SharedPreferencesUtil.setLong(context, PushConstant.SETTINGS_PUSH_FREQUENCY, frequency);
	}

	/**
	 * 获取推送频率，默认10分钟
	 * @param context
	 * 		上下文
	 * @return
	 * 		频率 long
	 */
	public static long getPushFrequency(Context context) {
		return SharedPreferencesUtil.getLong(context, PushConstant.SETTINGS_PUSH_FREQUENCY,
				PushConstant.DEFAULT_PUSH_FREQUENCY);
	}

	/**
	 * 设置免打扰模式
	 * @param context
	 * 		上下文
	 * @param disturb
	 * 		是否开启
	 */
	public static void setPushTimeinterval(Context context, boolean disturb) {
		SharedPreferencesUtil.setBoolean(context, PushConstant.SETTINGS_PUSH_TIMEINTERVAL, disturb);
	}

	/**
	 * 获取免打扰模式，默认为true
	 * @param context
	 * 		上下文
	 * @return
	 * 		是否开启，boolean
	 */
	public static boolean getPushTimeinterval(Context context) {
		return SharedPreferencesUtil.getBoolean(context, PushConstant.SETTINGS_PUSH_TIMEINTERVAL,
				true);
	}

	/**
	 * 判断日期是否在合理范围，以在声音和震动中使用
	 * @param context
	 * 		上下文
	 * @param firstDate
	 * 		起始时间
	 * @param lastDate
	 * 		截止时间
	 * @return
	 * 		是否合理，boolean
	 */
	public static boolean isValidTimeInterval(Context context, String firstDate, String lastDate) {
		boolean timeinterval = PushUtil.getPushTimeinterval(context);
		if (timeinterval) {
			// 免打扰模式
			try {
				SimpleDateFormat format = new SimpleDateFormat("HH:mm");

				Date nightDate = format.parse(lastDate);
				Date morningDate = format.parse(firstDate);

				Date now = new Date();
				String nowStr = format.format(now);
				Date nowDate = format.parse(nowStr);

				if (nowDate.after(morningDate) && nowDate.before(nightDate)) {
					return true;
				}
			} catch (ParseException e) {
				Log.e("日期转换异常", e);
			}
		} else {
			return true;
		}

		return false;
	}
}
