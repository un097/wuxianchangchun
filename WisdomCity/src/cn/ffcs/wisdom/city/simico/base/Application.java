package cn.ffcs.wisdom.city.simico.base;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.simico.api.model.ApiResponse;
import cn.ffcs.wisdom.city.simico.api.model.EventLog;
import cn.ffcs.wisdom.city.simico.api.request.BaseRequestListener;
import cn.ffcs.wisdom.city.simico.api.request.UploadEventLogRequest;
import cn.ffcs.wisdom.city.simico.content.EventLogHelper;
import cn.ffcs.wisdom.city.simico.content.ServiceHistoryHelper;
import cn.ffcs.wisdom.city.simico.kit.application.BaseApplication;
import cn.ffcs.wisdom.city.simico.kit.log.TLog;
import cn.ffcs.wisdom.city.simico.kit.util.DateUtil;
import cn.ffcs.wisdom.city.simico.kit.util.SharedPreferencesHandler;
import cn.ffcs.wisdom.city.sqlite.model.MenuItem;
import cn.ffcs.wisdom.tools.SharedPreferencesUtil;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.ctbri.wxcc.MessageEditor;
import dalvik.system.DexClassLoader;

public class Application extends BaseApplication {
	private static final String TAG = Application.class.getSimpleName();

	private static final String KEY_EVENT_UPLOAD_TIME = "KEY_EVENT_UPLOAD_TIME";
	private static final String KEY_CACHE_TIME = "KEY_CACHE_TIME";

	private static final long UPLOAD_INTEVAL = 60 * 1000;// 1分钟发一次

	private static final String TAG_UPLOAD_EVENT_LOG_TAG = "TAG_UPLOAD_EVENT_LOG_TAG";

	private static final String SELECTED_GENDER_KEY = "SELECTED_GENDER_KEY";

	private static final String KEY_LAST_NEWS_IDS = "KEY_LAST_NEWS_IDS";

	private static final String KEY_COLLECT_NEWS_IDS = "KEY_COLLECT_NEWS_IDS";

	private static final String KEY_SUBSCRIBE_SERVICE_TIME = "KEY_SUBSCRIBE_SERVICE_TIME";

	private static final String KEY_COLLECT_TIME = "KEY_COLLECT_TIME";

	private static final String KEY_SUBSCRIBE_SERVICE = "KEY_SUBSCRIBE_SERVICE";

	private static final String KEY_SUBSCRIBE_SERVICE_JSON = "KEY_SUBSCRIBE_SERVICE_JSON";

	private static final String KEY_SUBSCRIBE_SERVICE_COUNT = "KEY_SUBSCRIBE_SERVICE_COUNT";

	private static final String KEY_SUBSCRIBE_SERVICE_SYSTEM = "KEY_SUBSCRIBE_SERVICE_SYSTEM";

	private static final String KEY_REFRESH_ON_RESUME = "KEY_REFRESH_ON_RESUME";

	private static final String KEY_REFRESH_INDEX_SERVICE_ON_RESUME = "KEY_REFRESH_INDEX_SERVICE_ON_RESUME";

	private static final String KEY_SERVICE_CACHE_TIME = "KEY_SERVICE_CACHE_TIME";

	private static final String KEY_LAST_NEWS_TIME = "KEY_LAST_NEWS_TIME";

	private static final String KEY_APPLIST_CACHE_TIME = "KEY_APPLIST_CACHE_TIME";

	private static final String KEY_CHANNEL_CACHE_TIME = "KEY_CHANNEL_CACHE_TIME";

	private static final String KEY_REFRESH_CHANNEL_ON_RESUME = "KEY_REFRESH_CHANNEL_ON_RESUME";

	public static String filePath;
	/**
	 * Global request queue for Volley 1. 创建一个RequestQueue对象。 2.
	 * 创建一个StringRequest对象。 3. 将StringRequest对象添加到RequestQueue里面。
	 */
	private RequestQueue mRequestQueue;

	private EventLogHelper mEventHelper;

	private boolean mIsUploadingEventLog;// 是否正在上传行为日志

	private ServiceHistoryHelper mServiceHistoryHelper;

	// private static Picasso mPicasso;
	private static Context mContext;

	// private static HashMap<String, HashSet<Integer>> mCityNewIds = new
	// HashMap<String, HashSet<Integer>>();

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = getApplicationContext();
		// 初始化电台播放状态存储路径
		filePath = getFilesDir().getAbsolutePath() + "/audio_play_statu.txt";

		mEventHelper = new EventLogHelper(getApplicationContext());
		mEventHelper.initialize();

		mServiceHistoryHelper = new ServiceHistoryHelper(
				getApplicationContext());
		mServiceHistoryHelper.initialize();

		MessageEditor.initOrUpdateCTBRI(mContext, null, null, null, null);
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}


	public void addEventLog(String areaId, String itemType) {
		addEventLog(areaId, "", itemType, "");
	}

	public void addEventLog(String areaId, String itemId, String itemType) {
		addEventLog(areaId, itemId, itemType, "");
	}

	public void addEventLog(String areaId, String itemId, String itemType,
			String subItemId) {
		TLog.log(TAG, "记录行为日志:" + areaId + " " + itemId + " " + itemType + " "
				+ subItemId + " " + DateUtil.getNow("yyyy-MM-dd HH:mm:ss"));

		EventLog event = new EventLog();
		event.setAreaId(areaId);
		event.setItemId(itemId);
		event.setItemType(itemType);
		event.setSubItemId(subItemId);
		event.setTimestamp(String.valueOf(System.currentTimeMillis()));
		event.setUser(getCurrentUser());
		mEventHelper.beginTransaction();
		mEventHelper.addEventLog(event);
		mEventHelper.setTransactionSuccessful();
		mEventHelper.endTransaction();
		handleUploadEventLogOrNot();
	}

	public static String getCurrentUser() {
		return AccountMgr.getInstance().isLogin(mContext) ? AccountMgr
				.getInstance().getMobile(mContext) : "";
	}

	public static String getCurrentCity() {
		return MenuMgr.getInstance().getCityCode(mContext);
	}

	private void handleUploadEventLogOrNot() {
		long lastUploadTime = getLastUploadEventLogTime();
		if (System.currentTimeMillis() - lastUploadTime >= UPLOAD_INTEVAL
				&& !mIsUploadingEventLog) {
			long time = System.currentTimeMillis();
			ArrayList<EventLog> events = queryEventLogBefore(time);
			if (events != null && events.size() > 0) {
				TLog.log(TAG, "发现新日志准备上传");
				sendUploadEventLogRequest(time, events);
			} else {
				TLog.log(TAG, "没有发现可上传日志");
			}
		}
	}

	private void sendUploadEventLogRequest(final long time,
			ArrayList<EventLog> events) {
		TLog.log(TAG, "准备上传行为日志");
		mIsUploadingEventLog = true;
		UploadEventLogRequest req = new UploadEventLogRequest(events,
				new BaseRequestListener() {

					@Override
					public void onRequestSuccess(ApiResponse json)
							throws Exception {
						TLog.log(TAG, "行为日志上传成功了:" + json);
						setLastUploadEventLogTime(time);
						// 删除旧的行为日志
						deleteOldEventLog(time);
					}

					@Override
					public void onRequestFaile(ApiResponse json, Exception e) {
						TLog.log(TAG, "行为日志上传失败了");
					}

					@Override
					public void onRequestFinish() {
						mIsUploadingEventLog = false;
					}

				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						mIsUploadingEventLog = false;
						TLog.log(TAG, "行为日志上传失败了");
					}

				});
		addToRequestQueue(req, TAG_UPLOAD_EVENT_LOG_TAG);
	}

	public void deleteOldEventLog(long timestamp) {
		mEventHelper.beginTransaction();
		mEventHelper.deleteEventLogBeforeTime(getCurrentUser(), timestamp);
		mEventHelper.setTransactionSuccessful();
		mEventHelper.endTransaction();
	}

	public ArrayList<EventLog> queryEventLogBefore(long timestamp) {
		return mEventHelper.queryEventLog(getCurrentUser(), timestamp);
	}

	public static void setLastUploadEventLogTime(long timestamp) {
		Editor editor = getPreferences().edit();
		editor.putLong(KEY_EVENT_UPLOAD_TIME, timestamp);
		apply(editor);
	}

	public static long getLastUploadEventLogTime() {
		return getPreferences().getLong(KEY_EVENT_UPLOAD_TIME, 0);
	}

	public void updateHistory(MenuItem item) {
		MenuItem history = mServiceHistoryHelper.queryHistoryById(
				item.getMenuId(), getCurrentUser(), getCurrentCity());
		mServiceHistoryHelper.beginTransaction();
		if (history == null) {
			mServiceHistoryHelper.addNewHistory(item, getCurrentUser(),
					getCurrentCity());
		} else {
			mServiceHistoryHelper.updateHistory(item, getCurrentUser(),
					getCurrentCity());
		}
		mServiceHistoryHelper.setTransactionSuccessful();
		mServiceHistoryHelper.endTransaction();
	}

	public ArrayList<MenuItem> queryHistories() {
		return mServiceHistoryHelper.queryHistories(getCurrentUser(),
				getCurrentCity());
	}

	/**
	 * 更多服务缓存时间，根据当前用户和所在城市做区分
	 * 
	 * @return
	 */
	public static long getLastCacheTime() {
		return getPreferences().getLong(
				getCurrentUser() + getCurrentCity() + KEY_CACHE_TIME, 0);
	}

	/**
	 * 设置更多服务缓存时间
	 * 
	 * @param timestamp
	 */
	public static void setLastCacheTime(long timestamp) {
		Editor editor = getPreferences().edit();
		editor.putLong(getCurrentUser() + getCurrentCity() + KEY_CACHE_TIME,
				timestamp);
		apply(editor);
	}

	public static String getSelectedGender() {
		return SharedPreferencesUtil.getValue(mContext, SELECTED_GENDER_KEY);
	}

	public static void setSelectedGender(String gender) {
		SharedPreferencesUtil.setValue(mContext, SELECTED_GENDER_KEY, gender);
	}

	public static HashSet<Integer> getLastNewsIds(int channelId) {
		String idsStr = getPreferences().getString(
				getCurrentUser() + getCurrentCity() + KEY_LAST_NEWS_IDS + "/"
						+ channelId, "[]");
		idsStr = idsStr.substring(1, idsStr.length() - 1).replaceAll(" ", "");
		HashSet<Integer> mNewsIds = new HashSet<Integer>();
		if (!TextUtils.isEmpty(idsStr)) {
			String[] ids = idsStr.split(",");
			for (String id : ids) {
				mNewsIds.add(Integer.parseInt(id.replaceAll(" ", "")));
			}
		}
		return mNewsIds;
	}

	public static void setLastNewsIds(int channelId, HashSet<Integer> ids) {
		if (ids.size() <= 0)
			return;
		Editor editor = getPreferences().edit();
		editor.putString(getCurrentUser() + getCurrentCity()
				+ KEY_LAST_NEWS_IDS + "/" + channelId, ids.toString());
		apply(editor);
	}

	public static HashSet<Integer> getCollectNewsIds() {
		String idsStr = getPreferences().getString(
				getCurrentUser() + KEY_COLLECT_NEWS_IDS, "[]");
		idsStr = idsStr.substring(1, idsStr.length() - 1).replaceAll(" ", "");
		HashSet<Integer> mNewsIds = new HashSet<Integer>();
		if (!TextUtils.isEmpty(idsStr)) {
			String[] ids = idsStr.split(",");
			for (String id : ids) {
				mNewsIds.add(Integer.parseInt(id.replaceAll(" ", "")));
			}
		}
		return mNewsIds;
	}

	public static HashSet<Integer> getLogoutCollectNewsIds() {
		String idsStr = getPreferences().getString(KEY_COLLECT_NEWS_IDS, "[]");
		idsStr = idsStr.substring(1, idsStr.length() - 1).replaceAll(" ", "");
		HashSet<Integer> mNewsIds = new HashSet<Integer>();
		if (!TextUtils.isEmpty(idsStr)) {
			String[] ids = idsStr.split(",");
			for (String id : ids) {
				mNewsIds.add(Integer.parseInt(id.replaceAll(" ", "")));
			}
		}
		return mNewsIds;
	}

	public static void setCollectNewsIds(HashSet<Integer> ids) {
		if (ids == null)
			return;
		Editor editor = getPreferences().edit();
		editor.putString(getCurrentUser() + KEY_COLLECT_NEWS_IDS,
				ids.toString());
		apply(editor);
	}

	public static void setLogoutCollectNewsIds(HashSet<Integer> ids) {
		if (ids == null)
			return;
		Editor editor = getPreferences().edit();
		editor.putString(KEY_COLLECT_NEWS_IDS, ids.toString());
		apply(editor);
	}

	public static long getLastCollectTime() {
		return getPreferences().getLong(
				new StringBuilder(KEY_COLLECT_TIME).append(getCurrentUser())
						.toString(), -1);
	}

	public static void setLastCollectTime(long time) {
		Editor editor = getPreferences().edit();
		editor.putLong(
				new StringBuilder(KEY_COLLECT_TIME).append(getCurrentUser())
						.toString(), time);
		apply(editor);
	}

	/**
	 * 获取某用户在某城市下上次同步订阅服务信息时间
	 * 
	 * @return
	 */
	public static long getLastSyncSubscribeServiceTime() {
		return getPreferences().getLong(
				new StringBuilder(KEY_SUBSCRIBE_SERVICE_TIME)
						.append(getCurrentCity()).append(getCurrentUser())
						.toString(), -1);
	}

	/**
	 * 设置上次同步订阅服务列表时间
	 * 
	 * @param time
	 */
	public static void setLastSyncSubscribeServiceTime(long time) {
		Editor editor = getPreferences().edit();
		editor.putLong(
				new StringBuilder(KEY_SUBSCRIBE_SERVICE_TIME)
						.append(getCurrentCity()).append(getCurrentUser())
						.toString(), time);
		apply(editor);
	}

	/**
	 * 上次同步的订阅服务ID列表
	 * 
	 * @return
	 */
	public static Set<String> getLastSyncSubscribeServiceIds() {
		return SharedPreferencesHandler.getStringSet(
				getPreferences(),
				new StringBuilder(KEY_SUBSCRIBE_SERVICE)
						.append(getCurrentCity()).append(getCurrentUser())
						.toString(), new HashSet<String>());
	}

	/**
	 * 设置上次同步的订阅服务ID列表
	 * 
	 * @param serviceIds
	 */
	public static void setLastSyncSubscribeServiceIds(Set<String> serviceIds) {
		Editor editor = getPreferences().edit();
		SharedPreferencesHandler.putStringSet(
				editor,
				new StringBuilder(KEY_SUBSCRIBE_SERVICE)
						.append(getCurrentCity()).append(getCurrentUser())
						.toString(), serviceIds);
		apply(editor);
	}

	/**
	 * 上次同步的订阅服务systemID列表
	 * 
	 * @return
	 */
	public static Set<String> getLastSyncSubscribeSystemServiceIds() {
		return SharedPreferencesHandler.getStringSet(
				getPreferences(),
				new StringBuilder(KEY_SUBSCRIBE_SERVICE_SYSTEM)
						.append(getCurrentCity()).append(getCurrentUser())
						.toString(), new HashSet<String>());
	}

	/**
	 * 设置上次同步的订阅服务systemID列表
	 * 
	 * @param serviceIds
	 */
	public static void setLastSyncSubscribeSystemServiceIds(
			Set<String> serviceIds) {
		Editor editor = getPreferences().edit();
		SharedPreferencesHandler.putStringSet(
				editor,
				new StringBuilder(KEY_SUBSCRIBE_SERVICE_SYSTEM)
						.append(getCurrentCity()).append(getCurrentUser())
						.toString(), serviceIds);
		apply(editor);
	}

	/**
	 * 获取某用户在某城市下上次同步订阅频道信息时间
	 * 
	 * @return
	 */
	public static long getLastSyncSubscribeChannelTime() {
		return getPreferences().getLong(
				new StringBuilder(KEY_SUBSCRIBE_SERVICE_TIME)
						.append(getCurrentCity()).append(getCurrentUser())
						.toString(), -1);
	}

	/**
	 * 设置上次同步订阅频道列表时间
	 * 
	 * @param time
	 */
	public static void setLastSyncSubscribeChannelTime(long time) {
		Editor editor = getPreferences().edit();
		editor.putLong(
				new StringBuilder(KEY_SUBSCRIBE_SERVICE_TIME)
						.append(getCurrentCity()).append(getCurrentUser())
						.toString(), time);
		apply(editor);
	}

	public static long getAppListLastCacheTime() {
		return getPreferences().getLong(
				new StringBuilder(KEY_APPLIST_CACHE_TIME)
						.append(getCurrentCity()).append(getCurrentUser())
						.toString(), -1);
	}

	public static void setAppListLastCacheTime(long time) {
		Editor editor = getPreferences().edit();
		editor.putLong(
				new StringBuilder(KEY_APPLIST_CACHE_TIME)
						.append(getCurrentCity()).append(getCurrentUser())
						.toString(), time);
		apply(editor);
	}

	public static String getLastSyncSubscribeServiceJSON() {
		String key = new StringBuilder(KEY_SUBSCRIBE_SERVICE_JSON)
				.append(getCurrentCity()).append(getCurrentUser()).toString();
		TLog.log(TAG, "获取缓存:" + key);
		return getPreferences().getString(key, null);
	}

	public static void setLastSyncSubscribeServiceJSON(String json) {
		Editor editor = getPreferences().edit();
		editor.putString(
				new StringBuilder(KEY_SUBSCRIBE_SERVICE_JSON)
						.append(getCurrentCity()).append(getCurrentUser())
						.toString(), json);
		apply(editor);
	}

	/**
	 * 获取已订阅服务数量
	 * 
	 * @return
	 */
	public static int getSubscribeServiceCount() {
		return getPreferences().getInt(
				new StringBuilder(KEY_SUBSCRIBE_SERVICE_COUNT)
						.append(getCurrentCity()).append(getCurrentUser())
						.toString(), 0);
	}

	/**
	 * 设置已订阅服务数量
	 * 
	 * @param count
	 */
	public static void setSubscribeServiceCount(int count) {
		Editor editor = getPreferences().edit();
		editor.putInt(
				new StringBuilder(KEY_SUBSCRIBE_SERVICE_COUNT)
						.append(getCurrentCity()).append(getCurrentUser())
						.toString(), count);
		apply(editor);
	}

	public static boolean isNeedRefreshOnResume(int idx) {
		return getPreferences().getBoolean(
				new StringBuilder(KEY_REFRESH_ON_RESUME)
						.append(getCurrentCity()).append(getCurrentUser())
						.append(idx).toString(), false);
	}

	public static void setNeedRefreshOnResume(int idx, boolean refresh) {
		Editor editor = getPreferences().edit();
		editor.putBoolean(
				new StringBuilder(KEY_REFRESH_ON_RESUME)
						.append(getCurrentCity()).append(getCurrentUser())
						.append(idx).toString(), refresh);
		apply(editor);
	}

	public static boolean isNeedRefreshIndexServiceOnResume() {
		return getPreferences().getBoolean(
				new StringBuilder(KEY_REFRESH_INDEX_SERVICE_ON_RESUME)
						.append(getCurrentCity()).append(getCurrentUser())
						.toString(), false);
	}

	public static void setNeedRefreshIndexServiceOnResume(boolean refresh) {
		Editor editor = getPreferences().edit();
		editor.putBoolean(
				new StringBuilder(KEY_REFRESH_INDEX_SERVICE_ON_RESUME)
						.append(getCurrentCity()).append(getCurrentUser())
						.toString(), refresh);
		apply(editor);
	}

	public static long getServiceLastCacheTime(int idx) {
		String key = new StringBuilder(KEY_SERVICE_CACHE_TIME)
				.append(getCurrentCity()).append(getCurrentUser()).append(idx)
				.toString();
		long time = getPreferences().getLong(key, -1);
		TLog.log(TAG, "getServiceLastCacheTime key:" + idx + " time:" + time);
		return time;
	}

	public static void setServiceLastCacheTime(int idx, long time) {
		String key = new StringBuilder(KEY_SERVICE_CACHE_TIME)
				.append(getCurrentCity()).append(getCurrentUser()).append(idx)
				.toString();
		TLog.log(TAG, "setServiceLastCacheTime key:" + key + " time:" + time);
		Editor editor = getPreferences().edit();
		editor.putLong(key, time);
		apply(editor);
	}

	// ------------------ begin volley controller ---------------------//
	/**
	 * @return The Volley Request queue, the queue will be created if it is null
	 */
	public RequestQueue getRequestQueue() {
		// lazy initialize the request queue, the queue instance will be
		// created when it is accessed for the first time
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}

		return mRequestQueue;
	}

	/**
	 * Adds the specified request to the global queue, if tag is specified then
	 * it is used else Default TAG is used.
	 * 
	 * @param req
	 * @param tag
	 */
	public <T> void addToRequestQueue(Request<T> req, String tag) {
		// set the default tag if tag is empty
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);

		// VolleyLog.d("Adding request to queue: %s", req.getUrl());

		getRequestQueue().add(req);
	}

	/**
	 * Adds the specified request to the global queue using the Default TAG.
	 * 
	 * @param req
	 * @param tag
	 */
	public <T> void addToRequestQueue(Request<T> req) {
		// set the default tag if tag is empty
		req.setTag(TAG);

		getRequestQueue().add(req);
	}

	/**
	 * Cancels all pending requests by the specified TAG, it is important to
	 * specify a TAG so that the pending/ongoing requests can be cancelled.
	 * 
	 * @param tag
	 */
	public void cancelPendingRequests(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}

	public static synchronized Application instance() {
		return (Application) context();
	}

	// ---------------------end volley controller----------------------------//

	// public static Picasso getPicasso() {// Context context
	// Context context = instance().getApplicationContext();
	// if (mPicasso == null) {
	// mPicasso = new Picasso.Builder(context)
	// // .debugging(true)
	// .downloader(new UrlConnectionDownloader(context))
	// .memoryCache(new LruCache(calculateMemoryCacheSize(context))).build();
	// }
	// return mPicasso;
	// }

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private static int calculateMemoryCacheSize(Context context) {
		ActivityManager mgr = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		boolean flag = (4000000 & context.getApplicationInfo().flags) != 0;
		int i = mgr.getMemoryClass();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if (flag)
				i = mgr.getLargeMemoryClass();
		}
		return (4000000 * i) / 5;
	}

	/**
	 * 获取某用户在某城市下上次缓存新闻信息时间
	 * 
	 * @return
	 */
	public static String getLastSyncNewsTime(int chnl_id) {
		return getPreferences().getString(
				new StringBuilder(KEY_LAST_NEWS_TIME + "_" + chnl_id)
						.append(getCurrentCity()).append(getCurrentUser())
						.toString(), "");
	}

	/**
	 * 设置上次缓存新闻列表时间
	 * 
	 * @param time
	 */
	public static void setLastSyncNewsTime(int chnl_id, String time) {
		Editor editor = getPreferences().edit();
		editor.putString(new StringBuilder(KEY_LAST_NEWS_TIME + "_" + chnl_id)
				.append(getCurrentCity()).append(getCurrentUser()).toString(),
				time);
		apply(editor);
	}

	public static long getChannelLastCacheTime() {
		return getPreferences().getLong(
				new StringBuilder(KEY_CHANNEL_CACHE_TIME).append(
						getCurrentCity()).toString(), -1);
	}

	public static void setChannelLastCacheTime(long time) {
		Editor editor = getPreferences().edit();
		editor.putLong(
				new StringBuilder(KEY_CHANNEL_CACHE_TIME).append(
						getCurrentCity()).toString(), time);
		apply(editor);
	}

	public static boolean isNeedRefreshChannelOnResume() {
		return getPreferences().getBoolean(
				new StringBuilder(KEY_REFRESH_CHANNEL_ON_RESUME).append(
						getCurrentCity()).toString(), false);
	}

	public static void setNeedRefreshChannelOnResume(boolean refresh) {
		Editor editor = getPreferences().edit();
		editor.putBoolean(new StringBuilder(KEY_REFRESH_CHANNEL_ON_RESUME)
				.append(getCurrentCity()).toString(), refresh);
		apply(editor);
	}

	
	private void dexTool() {
        File dexDir = new File(getFilesDir(), "dlibs");
        dexDir.mkdir();
        File dexFile = new File(dexDir, "libs.apk");
        File dexOpt = new File(dexDir, "opt");
        dexOpt.mkdir();
        try {
            InputStream ins = getAssets().open("libs.apk");
            if (dexFile.length() != ins.available()) {
                FileOutputStream fos = new FileOutputStream(dexFile);
                byte[] buf = new byte[4096];
                int l;
                while ((l = ins.read(buf)) != -1) {
                    fos.write(buf, 0, l);
                }
                fos.close();
            }
            ins.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        ClassLoader cl = getClassLoader();
        ApplicationInfo ai = getApplicationInfo();
        String nativeLibraryDir = null;
        if (Build.VERSION.SDK_INT > 8) {
            nativeLibraryDir = ai.nativeLibraryDir;
        } else {
            nativeLibraryDir = "/data/data/" + ai.packageName + "/lib/";
        }
        DexClassLoader dcl = new DexClassLoader(dexFile.getAbsolutePath(),
                dexOpt.getAbsolutePath(), nativeLibraryDir, cl.getParent());

        try {
            Field f = ClassLoader.class.getDeclaredField("parent");
            f.setAccessible(true);
            f.set(cl, dcl);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
