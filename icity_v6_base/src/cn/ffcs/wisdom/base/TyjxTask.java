package cn.ffcs.wisdom.base;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import android.content.Context;
import cn.ffcs.wisdom.http.BaseTyjxResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.MD5;

/**
 * <p>Title: 针对天翼景象的Task请求 ,继承BaseTask，兼容天翼景象旧接口    </p>
 * <p>Description: 
 *  调用该类注意事项:<br>
 *  1. 实例化：CommonTask task = CommonTask.newInstance(icall, context, class);<br>
 *  2. 设置参数：task.setParams(maps, url);<br>
 *  3. 解析完后的Json对象，从BaseResp中的getObj()获取;
 *  4. 解析的对象为基于TyjxResponse的对象<br>
 * </p>
 * <p>@author: Eric.wsd                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-7-18             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class TyjxTask {

	private Map<String, String> mParams = new HashMap<String, String>(1);
	private String mUrl;
	private String methodName;
	private String appKey;
	private String appSecret;
	private static TyjxTask task;
	static Object syncObject = new Object();

	private static final String APP_KEY = "app_key";
	private static final String FORMAT = "format";
	private static final String METHOD = "method";
	private static final String TIMESTAMP = "timestamp";
	private static final String VERSION = "v";
	private static final String SIGN = "sign";
	private static final String SIGN_METHOD = "sign_method";
	private static final String PARTNER_ID = "partner_id";

	private static final String version_code = "2.0"; // 目前支持2.0版本api协议
	private String FORMAT_VAL = "json";
	private String SIGN_METHOD_VAL = "md5";
	/** SDK版本号 */
	public static final String SDK_VERSION = "top-sdk-java-20120202";

	private DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
	
	private Context mContext;

	/**
	 * 实例化对象，采用单例模式，仅配置一次请求的参数
	 * @param context
	 * @param appKey
	 * 		分配给开发者的appkey
	 * @param appSecret
	 * 		分配给开发者的app密钥
	 * @return
	 * 		TyjxTask，实例化后，需要setParams设置参数，才能进行exec请求操作
	 */
	public static TyjxTask newInstance(Context context, String appKey, String appSecret) {
		synchronized (syncObject) {
			if (task == null) {
				task = new TyjxTask(context, appKey, appSecret);
			}
		}

		return task;
	}

	private TyjxTask(Context context, String appKey, String appSecret) {
		this.mContext = context;
		this.appKey = appKey;
		this.appSecret = appSecret;
	}

	public void exec(HttpCallBack<BaseTyjxResp> icall, Class<?> toJsonObjectClass) {
		CommonTask task = CommonTask.newInstance(icall, mContext, toJsonObjectClass);
		task.setParams(mParams, mUrl);
		task.execute();
	}

	/**
	 * 设置请求的参数
	 * @param params
	 * 		请求的参数，放置于Map对象中
	 * @param url
	 * 		请求的地址
	 */
	public void setParams(Map<String, String> params, String serverUrl, String methodName) {
		if (params != null) {
			this.mParams.clear();
			this.mParams.putAll(params);
		}
		this.mUrl = serverUrl;
		this.methodName = methodName;

		// 初始化内部参数
		initIntervalParams();
	}

	/**
	 * 初始化内部参数
	 */
	private void initIntervalParams() {
		mParams.put(METHOD, methodName);
		mParams.put(VERSION, version_code);
		mParams.put(APP_KEY, appKey);

		Long timestamp = System.currentTimeMillis();

		df.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		mParams.put(TIMESTAMP, df.format(new Date(timestamp)));// 因为沙箱目前只支持时间字符串，所以暂时用Date格式

		mParams.put(FORMAT, FORMAT_VAL);
		mParams.put(SIGN_METHOD, SIGN_METHOD_VAL);
		mParams.put(PARTNER_ID, SDK_VERSION);
		
		// FIXME 签名仅使用MD5加密方式，后续根据需求增加
		mParams.put(SIGN, signAppRequest(appKey, String.valueOf(timestamp), appSecret));
	}

	/**
	 * 生成签名
	 * @param appKey
	 * 		应用key
	 * @param timestamp
	 * 		当前时间戳
	 * @param appSecret
	 * 		应用密钥
	 * @return
	 * @throws IOException
	 */
	private String signAppRequest(String appKey, String timestamp, String appSecret) {
		StringBuffer encryptContent = new StringBuffer("");
		encryptContent.append(appSecret).append(timestamp).append(appKey).append(appSecret);
		byte[] bytes = MD5.getMD5Str(encryptContent.toString()).getBytes();
		return MD5.byte2hex(bytes).toUpperCase(Locale.getDefault()); // 将最后的值转为大写
	}
}
