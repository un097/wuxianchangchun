package cn.ffcs.wisdom.notify;

/**
 * <p>Title:         消息通知常量类                           </p>
 * <p>Description:                     </p>
 * <p>@author:    liaodl               </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-9-16           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:   liaodl                </p>
 * <p>Update Comments:  
 *            底层不用关心上层实体类如何
 * </p>
 */
public class NotificationConstants {

	public static final String SHARED_PREFERENCE_NAME = "notification_preferences";

	/**
	 * 通知图标
	 */
	public static final String NOTIFICATION_ICON = "NOTIFICATION_ICON";
	/**
	 * 是否开启通知
	 */
	public static final String SETTINGS_NOTIFICATION_ENABLED = "SETTINGS_NOTIFICATION_ENABLED";
	/**
	 * 是否开启声音
	 */
	public static final String SETTINGS_SOUND_ENABLED = "SETTINGS_SOUND_ENABLED";
	/**
	 * 是否开启震动
	 */
	public static final String SETTINGS_VIBRATE_ENABLED = "SETTINGS_VIBRATE_ENABLED";
	/**
	 * 是否开启Toast
	 */
	public static final String SETTINGS_TOAST_ENABLED = "SETTINGS_TOAST_ENABLED";

	// NOTIFICATION FIELDS
	public static final String NOTIFICATION_ID = "NOTIFICATION_ID";
	public static final String NOTIFICATION_TITLE = "NOTIFICATION_TITLE";
	public static final String NOTIFICATION_CONTENT = "NOTIFICATION_CONTENT";
	public static final String NOTIFICATION_URI = "NOTIFICATION_URI";
	public static final String NOTIFICATION_MESSAGE = "NOTIFICATION_MESSAGE";
	public static final String NOTIFICATION_URL = "NOTIFICATION_URL";
	public static final String NOTIFICATION_PKG = "NOTIFICATION_PKG"; //跳转notification 包名
	public static final String NOTIFICATION_CLASS = "NOTIFICATION_CLASS"; //跳转notification activity
	public static final String NOTIFICATION_FLAG = "NOTIFICATION_FLAG"; //标记是由通知栏点击跳转过来的

	// INTENT ACTIONS
	public static final String ACTION_SHOW_NOTIFICATION = "org.androidpn.client.SHOW_NOTIFICATION";
	public static final String ACTION_NOTIFICATION_CLICKED = "org.androidpn.client.NOTIFICATION_CLICKED";
	public static final String ACTION_NOTIFICATION_CLEARED = "org.androidpn.client.NOTIFICATION_CLEARED";

	//FIXME 具体类名待修改,底层不知上层类如何
//	public static final String FEED_BACK_RESULT_ACTIVITY = "cn.ffcs.wisdom.city.setting.feedback.FeedBackReplyActivity";
//	public static final String NOTIFICATION_ACTIVITY = "cn.ffcs.wisdom.city.push.NotificationActivity";
//	public static final String NOTIFICATION_DETAIL_ACTIVITY = "cn.ffcs.wisdom.city.push.NotificationDetailActivity";
//	public static final String MY_NOTICE_ACTIVITY = "cn.ffcs.wisdom.city.personcenter.MyNotifierActivity";
//	public static final String BROWSER_ACTIVITY = "cn.ffcs.wisdom.city.web.BrowserActivity";

}
