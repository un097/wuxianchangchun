package cn.ffcs.wisdom.push;

/**
 * <h3>推送常量   </h3>
 *  <p>1.推送开关  </p>
 *  <p>2.推送声音开关   </p>
 *  <p>3.推送震动开关   </p>
 *  <p>4.推送时间间隔   </p>
 */
public class PushConstant {

	// push settings constant
	/**
	 * 推送常驻开关
	 */
	public static final String SETTINGS_PUSH_RESIDENT = "settings_push_resident"; //
	/**
	 * 推送开关
	 */
	public static final String SETTINGS_PUSH_ENABLED = "setting_push_enable"; //
	/**
	 * 通知栏声音开关
	 */
	public static final String SETTINGS_PUSH_SOUND_ENABLED = "setting_push_sound_enabled"; //
	/**
	 * 通知栏震动开关
	 */
	public static final String SETTINGS_PUSH_VIBRATE_ENABLED = "settings_push_vibrate_enabled"; //
	/**
	 * 推送间隔时间
	 */
	public static final String SETTINGS_PUSH_FREQUENCY = "settings_push_frequency"; //
	/**
	 * 时间区间
	 */
	public static final String SETTINGS_PUSH_TIMEINTERVAL = "settings_push_timeinterval"; //

	/**
	 * 水费
	 */
	public static final String SETTINGS_PUSH_WATERCOST="setting_push_watercast";
	// default value
	/**
	 * 默认推送时间，10分钟
	 */
	public static final long DEFAULT_PUSH_FREQUENCY = 60 * 1000; //
	/**
	 * 默认推送声音，开启
	 */
	public static final boolean DEFAULT_PUSH_SOUND = true; //
	/**
	 * 默认推送震动，关闭
	 */
	public static final boolean DEFAULT_PUSH_VIBRATE = false; //
	/**
	 * 默认推送开关，启动
	 */
	public static final boolean DEFAULT_PUSH_ENABLED = true; //
	/**
	 * 默认常驻，关闭
	 */
	public static final boolean DEFAULT_PUSH_RESIDENT = false; //
	/**
	 * 默认免打扰模式，即默认时间段
	 */
	public static final boolean DEFAULT_PUSH_TIMEINTERVAL = true; //


}
