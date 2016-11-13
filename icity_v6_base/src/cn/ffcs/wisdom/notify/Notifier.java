package cn.ffcs.wisdom.notify;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import cn.ffcs.wisdom.push.PushUtil;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.StringUtil;

/**
 * <h3>消息通知器 </h3>
 * <p>仅在通知栏进行通知操作，是否通知或者开启声音、震动。均由调用方提供。</p>
 */
public class Notifier {

	private Context context;

	private NotificationManager notificationManager;

	public Notifier(Context context) {
		this.context = context;
		this.notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	/**
	 * 判断日期是否在合理范围，以在声音和震动中使用
	 * @return
	 * 		返回结果，boolean
	 */
	private boolean isSoundAndVibrate() {
		return PushUtil.isValidTimeInterval(context, "07:00", "23:00");
	}

	
	/**
	 * 自定义通知栏点击
	 * @param iq
	 * @param pendingIntent 点击跳转
	 */
	public void notify(NotificationIQ iq, PendingIntent pendingIntent) {
		int id = iq.getId();
		String title = iq.getTitle();
		String content = iq.getContent();

		int icon = iq.getIcon();
		boolean isSound = iq.isSound();
		boolean isVibrate = iq.isVibrate();
		boolean noticeEnable = iq.isEnable();

		Log.d("notificationId=" + id);
		Log.d("notificationTitle=" + title);
		Log.d("notificationMessage=" + content);

		if (noticeEnable) {
			NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
			builder.setTicker(content);
			builder.setSmallIcon(icon);
			builder.setAutoCancel(true);
			builder.setWhen(System.currentTimeMillis());

			int notifyDefaults = Notification.DEFAULT_LIGHTS;

			if (isSoundAndVibrate()) {
				if (isSound) {
					notifyDefaults |= Notification.DEFAULT_SOUND;
				}
				if (isVibrate) {
					notifyDefaults |= Notification.DEFAULT_VIBRATE;
				}
			}

			builder.setDefaults(notifyDefaults);
			Notification notification;
			notification = builder.build();
			notification.setLatestEventInfo(context, title, content, pendingIntent);
			notificationManager.notify(id, notification);
		} else {
			Log.i("--Notificaitons disabled.--");
		}
	}

	/**
	 * 进行状态栏消息通知（有添加额外参数）
	 * @param iq
	 * @param pkg
	 * @param className
	 * @param bundle
	 */
	public void notify(NotificationIQ iq, String pkg, String className, Bundle bundle) {
		Log.d("--start notify...--");

		int id = iq.getId();
		String title = iq.getTitle();
		String content = iq.getContent();
		MsgEntity message = iq.getMsgEntity();
		String wapUrl = iq.getWapUrl();

		if (!StringUtil.isEmpty(wapUrl) && !wapUrl.startsWith("http://")) {
			wapUrl = "http://" + wapUrl;
		}
		int icon = iq.getIcon();
		boolean isSound = iq.isSound();
		boolean isVibrate = iq.isVibrate();
		boolean noticeEnable = iq.isEnable();

		Log.d("notificationId=" + id);
		Log.d("notificationTitle=" + title);
		Log.d("notificationMessage=" + content);

		if (noticeEnable) {
			// Show the toast
			// if (NotificationUtil.isNotificationToastEnabled(context)) {
			// Toast.makeText(context, content, Toast.LENGTH_LONG).show();
			// }

			// Notification
			NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
			builder.setTicker(content);
			builder.setAutoCancel(true);
			builder.setSmallIcon(icon);
			builder.setWhen(System.currentTimeMillis());

			int notifyDefaults = Notification.DEFAULT_LIGHTS;

			if (isSoundAndVibrate()) {
				if (isSound) {
					notifyDefaults |= Notification.DEFAULT_SOUND;
				}
				if (isVibrate) {
					notifyDefaults |= Notification.DEFAULT_VIBRATE;
				}
			}

			builder.setDefaults(notifyDefaults);

			Intent intent = new Intent();
			intent.putExtra(NotificationConstants.NOTIFICATION_ID, id);
			intent.putExtra(NotificationConstants.NOTIFICATION_TITLE, title);
			intent.putExtra(NotificationConstants.NOTIFICATION_CONTENT, content);
			intent.putExtra(NotificationConstants.NOTIFICATION_MESSAGE, message);
			intent.putExtra(NotificationConstants.NOTIFICATION_URL, wapUrl);
//			if (!NotificationConstants.NOTIFICATION_ACTIVITY.equals(className)
//					|| !NotificationConstants.MY_NOTICE_ACTIVITY.equals(className)) {
			intent.putExtra(NotificationConstants.NOTIFICATION_FLAG, true);
//			}

//			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//			intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			// 跳转页面-旧版做法，新版把包名也传进来。使用setComponent。防止通知栏点击无响应
//			if (!StringUtil.isEmpty(className)) {
//				try {
//					Class<?> clazz = Class.forName(className);
//					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//					intent.setClass(context, clazz);
//				} catch (ClassNotFoundException e) {
//					Log.e("调用的类不存在");
//				}
//			} 

			if (!StringUtil.isEmpty(pkg) && !StringUtil.isEmpty(className)) {
				try {
					intent.setComponent(new ComponentName(pkg, className));
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				} catch (Exception e) {
					Log.e("--调用的类不存在--");
				}
			}

			intent.putExtras(bundle);

			try {
				String extraParam = message.getContent().getParam().getExtraParam();
				if (!StringUtil.isEmpty(extraParam)) {// 解析额外参数
					String[] extraData = extraParam.split("\\||,");
					int extraDataLengh = extraData.length;
					if (extraData.length % 2 == 0) {
						for (int i = 0; i < extraDataLengh; i++) {
							if (i < extraDataLengh - 1) {
								intent.putExtra(extraData[i], extraData[i + 1]);
							}
						}
					}
				}
			} catch (Exception e) {
				Log.e(e.getMessage(), e);
			}

			PendingIntent contentIntent = PendingIntent.getActivity(context, id, intent,
					PendingIntent.FLAG_UPDATE_CURRENT);

			Notification notification;

			notification = builder.build();
			notification.setLatestEventInfo(context, title, content, contentIntent);
			notificationManager.notify(id, notification);
		} else {
			Log.i("--Notificaitons disabled.--");
		}
	}

	/**
	 * 进行状态栏消息通知
	 * @param iq
	 * 		通知实体类
	 * @param className
	 * 		调整页面类名
	 */
	public void notify(NotificationIQ iq, String pkg, String className) {
		notify(iq, pkg, className, null);
	}
}
