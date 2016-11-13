package cn.ffcs.wisdom.city.simico.base;

import cn.ffcs.wisdom.city.common.Config;
import android.os.Environment;

public class Constants {

	public final static String BASE_DIR = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/changchunTV/";

	public static final String CACHE_DIR = BASE_DIR + ".cache/";

	public static final String INTENT_ACTION_PREFIX = "cn.ffcs.changchuntv.intent.action.";
	public static final String INTENT_ACTION_EXIT_APP = INTENT_ACTION_PREFIX + "exitapp";
	
	public static final String INTENT_ACTION_SERVICE_CHANGED = INTENT_ACTION_PREFIX +"service_changed";
	
	public static final long INTEVAL_SYNC_TIME = 1 * 1800 * 1000;//30分钟
	
	//设置比较时间，主要防治时间间隔过久导致新闻不够实时，访问时间间隔大于5分钟后，也不会读缓存数据，保证新闻实时性
	public static final long NEWS_CACHE_TIME = 5 * 60 * 1000;

	public static final String SHARE_ROOT_URL = Config.GET_SERVER_ROOT_URL()
			+ "icity-api-client-web/v7/infoConfCall/toInfoConfDetail?id=";
	
	public static final String INTENT_ACTION_DOWNLOAD_APP = INTENT_ACTION_PREFIX
			+ "downloadapp";
}
