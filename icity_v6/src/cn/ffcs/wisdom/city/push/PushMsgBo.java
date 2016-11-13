package cn.ffcs.wisdom.city.push;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask.Status;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import cn.ffcs.wisdom.base.BaseTask;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.common.MenuType;
import cn.ffcs.wisdom.city.sqlite.model.NotificationInfo;
import cn.ffcs.wisdom.city.sqlite.service.NotificationInfoService;
import cn.ffcs.wisdom.city.traffic.violations.TrafficViolationsListActivity;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.city.web.BrowserActivity;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.notify.MsgEntity;
import cn.ffcs.wisdom.notify.MsgEntity.Content.Param;
import cn.ffcs.wisdom.notify.NotificationConstants;
import cn.ffcs.wisdom.notify.NotificationIQ;
import cn.ffcs.wisdom.notify.Notifier;
import cn.ffcs.wisdom.push.PushUtil;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.SharedPreferencesUtil;
import cn.ffcs.wisdom.tools.StringUtil;


public class PushMsgBo implements HttpCallBack<BaseResp> {

	private Activity activity;
	private Context context;
	private String applicationID = Config.PUSH_APPLICATION_ID;
	private String deviceToken;

	public static final int REGESITER_MAX = 3;// 重复发起注册最大次数
	private int count = 0;// 重复向消息中心发起注册获取token次数
	private int regCount = 0;// 重复向平台发起注册Token次数

	private GetTokenTask getTokenTask;
	private RegTokenTask regTokenTask;

	private NotificationInfoService service;
	private RefreshMsgReceiver refreshMsgReceiver;
	private static IrefreshMsg iCall;

	/**
	 * @param activity
	 *            用于消息回执
	 */
	public PushMsgBo(Activity activity) {
		this.activity = activity;
		this.context = activity.getApplicationContext();
		service = NotificationInfoService.getInstance(context);
	}

	/**
	 * @param context
	 *            用于MCIntentService或开机启动服务类里
	 */
	public PushMsgBo(Context context) {
		this.context = context;
		service = NotificationInfoService.getInstance(context);
	}

	/**
	 * 初始化数据
	 */
	public void initData() {
		if (enablePushService()) {
			// 启动消息中心推送 服务
			startService();
			// 注册APP，获取DeviceToken，在回调动作里做了向平台注册token和绑定用户动作。
			startRegister();
		} else {
			Log.i("--不启动推送服务.因为最外层AppConfig.xml配置为：isenable_push_service = fasle。 --");
		}
	}

	/**
	 * 最外层AppConfig.xml文件配置情况
	 */
	public boolean enablePushService() {
		return "true".equals(context.getString(R.string.isenable_push_service));
	}

	public void refreshData() {
		startRegister();// 注册APP，获取DeviceToken
	}

	Handler mHandler = new Handler();
	public Runnable runnable = new Runnable() {

		@Override
		public void run() {
			++count;
			if (count >= REGESITER_MAX) {
				mHandler.removeCallbacks(runnable);
				Log.i("--从消息中心获取DeviceToken达到3次，不再尝试重新获取--");
				Log.i("--从消息中心获取DeviceToken失败,不能向平台注册!--");
				return;
			}
			onRegister();
		}
	};

	/**
	 * 注册APP 建议做法：启动服务，发起注册，失败后等待一秒重新发起注册（至多三次）<br/>
	 * 注册APP，获取DeviceToken，在回调动作里做了向平台注册token和绑定用户动作。
	 */
	public void startRegister() {
		if (!startServiceFlag()) {// 服务启动成功了，看看有没有注册爱城市APP，返回token
			Log.i("--爱动漫消息中心服务启动失败，尝试再一次启动服务--");
			startService();
		}
		if (!startServiceFlag()) {
			Log.i("--爱动漫消息中心服务启动失败，无法后续注册APP动作--");
			return;
		}
		onRegister();
	}

	// 启动消息中心的后台服务
	public void startService() {
		// 接入爱动漫
		try {
			String pkg = AppHelper.getPackageName(context);
			Log.i("--消息中心服务要启动的包名：--" + pkg + ",应用id:-- " + applicationID);
			if (!StringUtil.isEmpty(pkg)) {
				Intent msgIntent = new Intent();
				msgIntent.setAction(Config.MSG_CENTER_ACTION);
				msgIntent.addCategory(pkg);
				context.startService(msgIntent);
				SharedPreferencesUtil.setBoolean(context,
						Key.K_START_PUSH_SERVER + AppHelper.getVersionCode(context), true);
				Log.i("--消息中心服务启动成功!--");
			}
		} catch (Exception e) {
			SharedPreferencesUtil.setBoolean(context,
					Key.K_START_PUSH_SERVER + AppHelper.getVersionCode(context), false);
			Log.e("--消息中心服务启动失败!--" + e);
		}
	}

	// 服务是否启动成功
	public boolean startServiceFlag() {
		return SharedPreferencesUtil.getBoolean(context,
				Key.K_START_PUSH_SERVER + AppHelper.getVersionCode(context), false);
	}

	// 注册APP
	public void onRegister() {
		try {
			if (!CommonUtils.isNetConnectionAvailable(context)) {
				Log.i("--网络连接不可用，向消息中心注册APP失败--");
				return;
			}
			if (isRunning(getTokenTask)) {// 正在运行
				Log.i("--已有获取token的线程在运行中，不重复启动线程--");
				return;
			}
			getTokenTask = new GetTokenTask(null, context);
			getTokenTask.setCallBack(new onGetToken());
			getTokenTask.execute();
		} catch (Exception e) {
			Log.e("--向消息中心注册APP失败--" + e);
		}
	}

	// 获取token回调
	public class onGetToken implements IGetToken {

		@Override
		public void getToken(String token) {
			if (!StringUtil.isEmpty(token) && !"null".equals(token)) {
				setDeviceToken(token);
				deviceToken = token;
				regToken(deviceToken);// 向平台注册由消息中心分配的DeviceToken
				// onBindUser();// 绑定用户
				// onBindUserInThread();// 绑定用户
				mHandler.removeCallbacks(runnable);
			} else if ("null".equals(token) || StringUtil.isEmpty(token)) {
				Log.i("--从消息中心获取DeviceToken失败!第" + (count + 1) + "次尝试重新获取--");
				mHandler.postDelayed(runnable, 1000);
			}
		}

	}

	// 是否首次向平台注册Token，false：第一次注册
	private boolean firstRegister() {
		return SharedPreferencesUtil.getBoolean(context,
				Key.K_FIRST_REGISTER + AppHelper.getVersionCode(context));
	}

	// 注册标记
	private void registered() {
		SharedPreferencesUtil.setBoolean(context,
				Key.K_FIRST_REGISTER + AppHelper.getVersionCode(context), true);
	}

	// 每次都向平台注册Token
	public void regToken(String deviceToken) {
		if ("null".equals(deviceToken) || StringUtil.isEmpty(deviceToken)) {
			SharedPreferencesUtil.setBoolean(context, Key.K_REG_MEG_TOKEN, false);
			Log.i("--从消息中心获取DeviceToken失败,不能向平台注册!--");
			return;
		}
		Log.i("--向平台注册的DeviceToken值为: -- " + deviceToken);
		Log.i("--向平台注册DeviceToken过程开始--");
		startRegTokenTask(deviceToken);
	}

	// 向平台注册Token(忽略，不用对比判断，已改成每次启动就注册token)
	@Deprecated
	public void regTokenOld(String deviceToken) {
		if ("null".equals(deviceToken) || StringUtil.isEmpty(deviceToken)) {
			SharedPreferencesUtil.setBoolean(context, Key.K_REG_MEG_TOKEN, false);
			Log.i("--从消息中心获取DeviceToken失败,不能向平台注册!--");
			return;
		}

		if (!firstRegister()) {// 首次向平台注册Token
			SharedPreferencesUtil.setBoolean(context, Key.K_REG_MEG_TOKEN, false);
		} else {//
			String localToken = getDeviceToken();
			Log.i("--本地保存的DeviceToken-- " + localToken + ", 本次启动获取的DeviceToken-- " + deviceToken);
			if (!StringUtil.isEmpty(localToken) && localToken.equals(deviceToken)) {
				Log.i("--从消息中心获取DeviceToken和本地保存的值一样,无需再次向平台注册!--");
				SharedPreferencesUtil.setBoolean(context, Key.K_REG_MEG_TOKEN, true);
				return;
			} else {
				SharedPreferencesUtil.setBoolean(context, Key.K_REG_MEG_TOKEN, false);
			}
		}

		boolean flag = SharedPreferencesUtil.getBoolean(context, Key.K_REG_MEG_TOKEN);// 如果值为false,代表没注册过，首次需注册！
		if (!flag) {
			Log.i("--向平台注册的DeviceToken值为: -- " + deviceToken);
			Log.i("--向平台注册DeviceToken过程开始--");
			startRegTokenTask(deviceToken);
		} else {
			Log.i("--DeviceToken已经向平台注册过,不能重复注册!--");
		}
	}

	private void startRegTokenTask(String deviceToken) {
		if (isRunning(regTokenTask)) {// 正在运行
			Log.i("--已有向平台注册token的线程在运行中，不重复启动线程--");
			return;
		}
		regTokenTask = new RegTokenTask(this, context);
		regTokenTask.setParams(deviceToken);
		regTokenTask.execute();
	}

	// 绑定用户，同步
	@Deprecated
	public boolean onBindUser() {
		String imsi = AppHelper.getSerialCode2(context);
		if ("null".equals(deviceToken) || StringUtil.isEmpty(deviceToken)
				|| StringUtil.isEmpty(imsi)) {
			Log.i("--绑定用户:" + imsi + "--失败--");
			return false;
		}
//		MCRegistration registration = new MCRegistration();
//		String result = registration.bindUserAsync(context, applicationID, deviceToken, imsi);
//		if ("0".equals(result)) {
//			Log.i("--绑定用户:" + imsi + "--成功--");
//			return true;
//		}
//		Log.i("--绑定用户:" + imsi + "--失败--");
		return false;
	}

	// 绑定用户
	public void onBindUserInThread() {
		final String imsi = AppHelper.getSerialCode2(context);
		if ("null".equals(deviceToken) || StringUtil.isEmpty(deviceToken)
				|| StringUtil.isEmpty(imsi)) {
			Log.i("--deviceToken非法，绑定用户:" + imsi + "--失败--");
			return;
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
//				MCRegistration registration = new MCRegistration();
//				String result = registration.bindUser(context, applicationID, deviceToken, imsi);
//				if ("0".equals(result)) {
//					Log.i("--绑定用户:" + imsi + "--成功--");
//				} else {
//					Log.i("--绑定用户:" + imsi + "--失败--");
//				}
			}
		}).start();
	}

	/**
	 * 消息回执并清除通知栏上的相对应消息,在各个被打开的Activity调用<br/>
	 * <h1>以后新增的推送类型，都要在被打开的activity里调用此方法。</h1><br/>
	 */
	public void pushFeedbackAndClearMsg(String backType) {
		pushFeedback(backType, true);
	}

	/**
	 * 消息回执,在各个被打开的Activity调用<br/>
	 * <h1>在被打开的activity（系统通知消息、个人我的消息）里调用此方法，needClear需传false</h1><br/>
	 */
	public void pushFeedback(String backType, boolean needClear) {
		if (activity == null) {
			return;
		}
		try {
			boolean flag = activity.getIntent().getBooleanExtra(
					NotificationConstants.NOTIFICATION_FLAG, false);
			int msgId = activity.getIntent().getIntExtra(NotificationConstants.NOTIFICATION_ID, -1);
			String idmMsgId = getIdmMsgIdByOurId(msgId);
			if (flag && needClear) {//是从消息推送过来，并且需要清除通知栏通知信息和图标
				clearMsg(msgId + "");
			}
			if (flag && !StringUtil.isEmpty(idmMsgId) && !"null".equals(idmMsgId)) {
				Log.i("--pushFeedback()方法里消息中心里的消息回执id:--" + idmMsgId);
				String backInfo = idmMsgId + "|" + backType;
				onReceipt(backInfo);
			}
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
	}

	/**
	 * 回执反馈
	 * 
	 * @param notice
	 */
	public void pushFeedBack(MsgEntity notice, String backType) {
		try {
			List<NotificationInfo> infos = service.findNewSystemNotice(context);
			for (NotificationInfo info : infos) {
				String targetMsgId = notice.getContent().getMsgId();
				if (targetMsgId.equals(info.getMsgId())) {
					String idmMsgId = getIdmMsgIdByOurId(targetMsgId);
					String backInfo = idmMsgId + "|" + backType;
					onReceipt(backInfo);
				}
			}
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
	}

	// 消息回执
	private void onReceipt(final String msgId) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				ArrayList<String> msgIds = new ArrayList<String>();
				msgIds.add(msgId);
//				MCRegistration registration = new MCRegistration();
//				String result = registration.receipt(context, applicationID, msgIds);
//				if ("0".equals(result)) {
//					Log.i(msgId + "--消息回执成功--");
//				} else {
//					Log.i(msgId + "--消息回执失败--");
//				}
			}
		}).start();
	}

	// 消息回执
	protected void onReceipt(final List<String> msgIds) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				ArrayList<String> container = new ArrayList<String>();
				container.addAll(msgIds);
				if (container.size() <= 0) {
					Log.i("--消息回执编号为空,消息回执失败--");
				} else {
//					MCRegistration registration = new MCRegistration();
//					String result = registration.receipt(context, applicationID, container);
//					if ("0".equals(result)) {
//						Log.i("--消息回执成功--");
//					} else {
//						Log.i("--消息回执失败--");
//					}
				}
			}
		}).start();
	}

	// 注销APP
//	public String onUnRegister() {
//		if (!CommonUtils.isNetConnectionAvailable(context)) {
//			return "null";
//		}
//		String result = "";
//		try {
//			MCRegistration registration = new MCRegistration();
//			result = registration.cancelRegister(context, applicationID, deviceToken);
//		} catch (Exception e) {
//			Log.e(e.getMessage(), e);
//			return "";
//		}
//		return result;
//	}

	/**
	 * 不用广播通知到底层再分发到通知栏，直接分发到通知栏
	 * 
	 * @param intent
	 */
	public void showMsgInNoticeBar(MsgEntity entity) {
		if (!enablePushService()) {
			Log.i("--不启动推送服务.因为最外层AppConfig.xml配置为：isenable_push_service = fasle。 --");
			return;
		}
		try {
			NotificationIQ iq = new NotificationIQ();
			int id = Integer.valueOf(entity.getContent().getMsgId());
			String title = entity.getTitle();
			String content = entity.getContent().getMsgContent();
			String wapUrl = entity.getContent().getParam().getWapUrl();
			if (!StringUtil.isEmpty(wapUrl) && !wapUrl.startsWith("http://")) {
				wapUrl = "http://" + wapUrl;
			}
			String pkg = entity.getContent().getParam().getAndroidPak();
			String cls = entity.getContent().getParam().getAndroidClass();// 包名+类名一串传过来
			String msgType = entity.getContent().getParam().getMsgType();
			// 预先判断用户可能行为，防止平台乱配置过来
			if (!StringUtil.isEmpty(wapUrl) && !StringUtil.isEmpty(pkg) && !StringUtil.isEmpty(cls)) {
				// 保存配置不变
			} else if (!StringUtil.isEmpty(wapUrl)) {
				pkg = AppHelper.getPackageName(context);
				cls = NotificationInfo.BROWSER_ACTIVITY;// 带有wap网址时，使用爱城市浏览器打开
			} else if (StringUtil.isEmpty(pkg) && StringUtil.isEmpty(cls)) {
				pkg = AppHelper.getPackageName(context);
				if (Config.PERSON_MSG_TYPE.equals(msgType)) {
					cls = NotificationInfo.MY_NOTICE_ACTIVITY;// 跳到个人中心我的消息列表
				} else {
//				    cls = NotificationInfo.NOTIFICATION_ACTIVITY;//跳到消息列表界面
					cls = NotificationInfo.NOTIFICATION_DETAIL_ACTIVITY;// 跳到消息详情界面
				}
			}

			int iconId = R.drawable.ic_launcher;
			// 状态控制
			boolean isSound = PushUtil.getPushSoundEnabled(context);
			boolean isVibrate = PushUtil.getPushVibrateEnabled(context);
			boolean pushEnabled = PushUtil.getPushEnabled(context);

			iq.setId(id);
			iq.setTitle(title);
			iq.setContent(content);
			iq.setWapUrl(wapUrl);
			iq.setIcon(iconId);
			iq.setSound(isSound);
			iq.setVibrate(isVibrate);
			iq.setEnable(pushEnabled);
			iq.setMsgEntity(entity);

			// 自定义消息通知view
//			RemoteViews remoteView = new RemoteViews(context.getPackageName(),
//					R.layout.widget_notify_item);
//			remoteView.setTextViewText(R.id.notify_title, title);
//			remoteView.setTextViewText(R.id.notify_content, content);
			Intent receiverIntent = new Intent(context, NotifyClickReceiver.class);
			receiverIntent.putExtra(NotificationConstants.NOTIFICATION_MESSAGE, entity);
			receiverIntent.putExtra(NotificationConstants.NOTIFICATION_PKG, pkg);
			receiverIntent.putExtra(NotificationConstants.NOTIFICATION_CLASS, cls);
			receiverIntent.setAction(context.getResources().getString(R.string.notify_click) + entity.getIdmMsgId());

			PendingIntent clickIntent = PendingIntent.getBroadcast(context, 0, receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//			remoteView.setOnClickPendingIntent(R.id.notify_layout, clickIntent);

			Notifier notifier = new Notifier(context);
			notifier.notify(iq,clickIntent);
			Log.i("--PushMsgBo 通知栏里的：消息推送包名--：" + pkg + ", 类名--：" + cls);

			// 新消息插入数据库
			NotificationInfoService service = NotificationInfoService.getInstance(context);
			service.save(context, entity);
			String action = Config.REFRESH_MSG_COUNT_ACTION;
			// 通知更新首页设置里消息的显示个数
			LocalBroadcastManager.getInstance(context).sendBroadcast(
					new Intent().putExtra(action, true).setAction(action));
			// 消息回执 -- 统计有多少客户端已经收到消息了
			final String idmMsgId = entity.getIdmMsgId();
			onReceipt(idmMsgId + "|" + Config.REBACK_MSG_TYPE);
		} catch (NumberFormatException e) {
			Log.w("--消息id转成整型异常，请检查平台给的消息id字符串是否合法--" + e.getMessage());
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
			Log.e("--显示消息中心的消息失败。--");
		}
	}

	/**
	 * 返回违章消息
	 * 
	 * @return
	 */
	public List<NotificationInfo> getWZMsg() {
		NotificationInfoService service = NotificationInfoService.getInstance(context);
		return service.findWZMsg(context);
	}

	/**
	 * 返回个人中心消息
	 * 
	 * @return
	 */
	public List<NotificationInfo> getPersonCenterMsg() {
		NotificationInfoService service = NotificationInfoService.getInstance(context);
		return service.findMyNotification(context);
	}

	@Override
	public void call(BaseResp response) {
		++regCount;
		Log.i("--第" + regCount + "次--注册DeviceToken平台返回的消息：--" + response.getHttpResult());
		if (response.isSuccess()) {
			registered();
			SharedPreferencesUtil.setBoolean(context, Key.K_REG_MEG_TOKEN, true);
			onBindUserInThread();// 绑定用户
			Log.i("--第" + regCount + "次--注册DeviceToken成功!--");
		} else {
			SharedPreferencesUtil.setBoolean(context, Key.K_REG_MEG_TOKEN, false);
			Log.i("--第" + regCount + "次--注册DeviceToken失败!--");
			if (regCount < 3) {
				onRegister();
				Log.i("--向平台注册DeviceToken失败,第" + regCount + "次尝试重新向平台注册!--");
			} else if (regCount == 3) {
				// regToken("过时或非法token");
				Log.i("--本次启动，已向平台注册DeviceToken3次-均失败，不再尝试重新注册，只能下次启动继续注册--");
			}
		}
		Log.i("--第" + regCount + "次--向平台注册DeviceToken过程结束--");
	}

	@Override
	public void progress(Object... obj) {
	}

	@Override
	public void onNetWorkError() {
		firstRegister();
		SharedPreferencesUtil.setBoolean(context, Key.K_REG_MEG_TOKEN, false);
		Log.i("--注册DeviceToken失败!--");
	}

	public String getDeviceToken() {
		String deviceToken = SharedPreferencesUtil.getValue(context, Key.K_DEVICE_TOKEN);
		return deviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		SharedPreferencesUtil.setValue(context, Key.K_DEVICE_TOKEN, deviceToken);
	}

	/**
	 * 初始化设置要跳转的Intent
	 * 
	 * @param activity
	 * @param info
	 * @param notice
	 */
	public Intent initIntent(Activity activity, MsgEntity notice) {
		Intent intent = new Intent();
		if (notice == null || notice.getContent() == null) {
			return intent;
		}
		Param param = notice.getContent().getParam();
		String category = "";
		if (param != null) {
			category = param.getAppType();// 注意不是：msgType
		}
		if (MenuType.WAP.equals(category)) {
			intent.putExtra(Key.U_BROWSER_TITLE, notice.getTitle());
			String url = param.getWapUrl();
			if (!url.startsWith("http://")) {
				url = "http://" + url;
			}
			Log.i("--PushMsgBo 消息推送要跳转的url:--" + url);
			intent.putExtra(Key.U_BROWSER_URL, url);
			intent.setClass(activity, BrowserActivity.class);
		} else if (MenuType.NATIVE_APP.equals(category)) {
			intent.putExtra(NotificationConstants.NOTIFICATION_URL, param.getWapUrl());
			setInnerClzz(param, intent);
		} else if (MenuType.EXTERNAL_APP.equals(category)) {
			intent.putExtra(NotificationConstants.NOTIFICATION_URL, param.getWapUrl());
			setExternalClzz(param, intent);
		} else {
			intent.setClass(activity, NotificationDetailActivity.class);
			intent.putExtra(NotificationConstants.NOTIFICATION_MESSAGE, notice);
		}
		intent.putExtra(Key.K_RETURN_TITLE,
				activity.getResources().getString(R.string.notification_title));

		return intent;
	}

	// 设置外部应用包名、类名
	private void setExternalClzz(Param param, Intent intent) {
		if (param == null) {
			return;
		}
		String pkg = param.getAndroidPak();// 获取本第三方应用包名，后台配置
		String cls = param.getAndroidClass();// 包名+类名一串传过来
		if (!StringUtil.isEmpty(pkg) && !StringUtil.isEmpty(cls)) {
			intent.setComponent(new ComponentName(pkg, cls));
		} else {
			Log.e("--调用的外部类错误或不存在--");
		}
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Log.i("--消息推送要跳转的外部类的类名--:" + cls);
	}

	// 设置内部应用包名、类名
	private void setInnerClzz(Param param, Intent intent) {
		if (param == null) {
			return;
		}
		String pkg = AppHelper.getPackageName(context);// 获取本应用包名
		String cls = param.getAndroidClass();// 包名+类名一串传过来
		if (!StringUtil.isEmpty(pkg) && !StringUtil.isEmpty(cls)) {
			if ("cn.ffcs.wisdom.city.breakrules.BreakRulesActivity".equals(cls)) {
				intent.setComponent(new ComponentName(context, TrafficViolationsListActivity.class));
			} else {
				intent.setComponent(new ComponentName(pkg, cls));
			}
		} else {
			Log.e("--调用的内部类错误或不存在--");
		}
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Log.i("--消息推送要跳转的内部类的类名:--" + cls);
	}

	/**
	 * 用于在主页设置显示消息个数
	 * 
	 * @return
	 */
	public int getNewMsgCount() {
		int count = 0;
		List<NotificationInfo> list = service.findNewSystemNotice(context);
		if (list != null && list.size() > 0) {
			return list.size();
		}
		return count;
	}

	/**
	 * 用于在主页个人头像旁边显示我的消息个数
	 * 
	 * @return
	 */
	public int getNewPersonMsgCount() {
		int count = 0;
		List<NotificationInfo> list = service.findNewMyNotification(context);
		if (list != null && list.size() > 0) {
			return list.size();
		}
		return count;
	}

	/**
	 * 通过平台id查找爱动漫消息分配给我们消息的id
	 * 
	 * @param msgId
	 * @return
	 */
	public String getIdmMsgIdByOurId(int msgId) {
		if (msgId < 0) {
			return "";
		}
		NotificationInfo info = service.findIdmMsgId(msgId + "");
		return info.getIdmMgId();
	}

	public String getIdmMsgIdByOurId(String msgId) {
		NotificationInfo info = service.findIdmMsgId(msgId);
		return info.getIdmMgId();
	}

	/**
	 * 更新 新消息数据为已读 并清除通知栏上的消息显示
	 * 
	 * @param msgId
	 */
	public void clearMsg(String msgId) {
		Log.i("--清除推送消息的id是--：" + msgId);
		if (StringUtil.isEmpty(msgId)) {
			return;
		}
		try {
			NotificationManager notificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.cancel(Integer.valueOf(msgId.trim()));
			service.updateNewById(msgId.trim());
		} catch (NumberFormatException e) {
			Log.w("--消息id转成整型异常，请检查平台给的消息id字符串是否合法--" + e.getMessage());
		}
	}

	public boolean isRunning(BaseTask<?, ?, ?> task) {
		return task != null && task.getStatus() == Status.RUNNING;
	}

	/**
	 * 注册刷新推送消息个数广播
	 */
	public void registerRefreshMsgBroadcast(IrefreshMsg iCall) {
		PushMsgBo.iCall = iCall;
		IntentFilter filter = new IntentFilter();
		if (!Config.isSuccess()) {
			Config.init(context);
		}
		if (refreshMsgReceiver == null) {
			refreshMsgReceiver = RefreshMsgReceiver.getInstance();
		}
		filter.addAction(Config.REFRESH_MSG_COUNT_ACTION);
		LocalBroadcastManager.getInstance(context).registerReceiver(refreshMsgReceiver, filter);
	}

	static class RefreshMsgReceiver extends BroadcastReceiver {

		private static RefreshMsgReceiver mInstance = new RefreshMsgReceiver();

		private RefreshMsgReceiver() {
		}

		public static RefreshMsgReceiver getInstance() {
			return mInstance;
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null) {
				boolean isRefresh = intent.getBooleanExtra(Config.REFRESH_MSG_COUNT_ACTION, false);
				if (isRefresh && iCall != null) {
					iCall.onRefreshMsg();
				}
			}
		}
	}

	// 注销更新新推送消息条数接收器
	public void unRegisterRefreshMsgReceiver() {
		if (refreshMsgReceiver != null) {
			LocalBroadcastManager.getInstance(context).unregisterReceiver(refreshMsgReceiver);
		}
	}

	public interface IrefreshMsg {
		public void onRefreshMsg();
	}

}
