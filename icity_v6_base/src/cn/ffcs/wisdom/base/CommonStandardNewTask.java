package cn.ffcs.wisdom.base;

import android.content.Context;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.http.HttpRequest;
import cn.ffcs.wisdom.http.HttpRequestJson;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.CrytoUtils;
import cn.ffcs.wisdom.tools.JsonUtil;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.ManifestUtil;
import cn.ffcs.wisdom.tools.StringUtil;
import cn.ffcs.wisdom.tools.TimeUitls;

/**
 * <p>Title: 通用Task请求 ,继承BaseTask，完善请求    </p>
 * <p>Description: 
 *  调用该类注意事项:<br>
 *  1. 实例化：CommonStandardTask task = CommonStandardTask.newInstance(icall, context, class);<br>
 *  2. 设置参数：task.setParams(maps, url);<br>
 *  3. 解析完后的Json对象，从BaseResp中的getObj()获取;<br>
 * </p>
 * <p>@author: Eric.wsd zhangwsh liaodl   </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-11-20          </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:    
 * 1.加入Agent头部请求
 * 2.多个结果码匹配，包括response_code、result_code
 * </p>
 */
public class CommonStandardNewTask extends BaseTask<Void, Void, Void> {

	private String jsonBody;// 字符串参数
	private String userAgent;// user-Agent
	private String mUrl;
	private Class<?> clazz;
	private String method = HttpRequest.HTTP_METHOD_POST;

	private String mContentType;

	public void setContentType(String contentType) {
		this.mContentType = contentType;
	}

	public CommonStandardNewTask(HttpCallBack iCall, Context context) {
		super(iCall, context);
	}

	public static CommonStandardNewTask newInstance(HttpCallBack iCall, Context context,
			Class<?> toJsonObjectClass) {
		return new CommonStandardNewTask(iCall, context, toJsonObjectClass);
	}

	private CommonStandardNewTask(HttpCallBack iCall, Context context, Class<?> clazz) {
		super(iCall, context);
		this.clazz = clazz;
	}

	private CommonStandardNewTask(HttpCallBack iCall, Class<?> clazz) {
		super(iCall);
		this.clazz = clazz;
	}

	/**
	 * 添加请求参数
	 * @param url
	 * @param map
	 * @param productId(可为unknown)
	 * @param phone(可为unknown)
	 * @param orgCode(可为unknown)
	 */
	public void setParams(String url, Map<String, String> map, String productId, String clientType, String phone, String cityCode,
			String orgCode, String longitude, String latitude, String sign) {
		this.mUrl = url;
		String timestamp = TimeUitls.getCurrentTime();
		try {
			String imsi = AppHelper.getMobileIMSI(mContext);
			if (StringUtil.isEmpty(imsi)) {
				imsi = "0000000000000000";
			}
			String imei = AppHelper.getIMEI(mContext);
			String md5 = "";
			if (TextUtils.isEmpty(sign)) {
				md5 = CrytoUtils.md5(CrytoUtils.MD5KEY, timestamp);
			} else {
				md5 = CrytoUtils.md5(sign, CrytoUtils.MD5KEY, timestamp);
			}
			String signString = CrytoUtils.encode(CrytoUtils.DESKEY, timestamp, md5);
			map.put("sign", signString);
			map.put("product_id", productId);
			map.put("client_type", clientType);
			map.put("client_version", AppHelper.getVersionCode(mContext) + "");
			map.put("client_channel_type", ManifestUtil.readMetaData(mContext, "UMENG_CHANNEL"));
			map.put("os_type", AppHelper.getOSTypeNew());
			map.put("timestamp", timestamp);
			map.put("imsi", imsi);
			map.put("imei", imei);
			map.put("mobile", phone);
			map.put("city_code", cityCode);
			map.put("org_code", orgCode);
			map.put("longitude", longitude);
			map.put("latitude", latitude);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.jsonBody = JsonUtil.toJson(map);
		this.userAgent = getUserAgent(mContext, productId);
//		android.util.Log.e("fmj","Utils.hashMapToJson((HashMap) map)="+ hashMapToJson((HashMap) map));
	}
	
	/**
	 * 添加请求参数(针对object传送)
	 * @param url
	 * @param map
	 * @param productId(可为unknown)
	 * @param phone(可为unknown)
	 * @param orgCode(可为unknown)
	 */
	public void setObjectParams(String url, Map<String, Object> map, String productId, String clientType, String phone, String cityCode,
			String orgCode, String longitude, String latitude, String sign) {
		this.mUrl = url;
		String timestamp = TimeUitls.getCurrentTime();
		try {
			String imsi = AppHelper.getMobileIMSI(mContext);
			if (StringUtil.isEmpty(imsi)) {
				imsi = "0000000000000000";
			}
			String imei = AppHelper.getIMEI(mContext);
			String md5 = CrytoUtils.md5(sign, CrytoUtils.MD5KEY, timestamp);
			String signString = CrytoUtils.encode(CrytoUtils.DESKEY, timestamp, md5);
			map.put("sign", signString);
			map.put("product_id", productId);
			map.put("client_type", clientType);
			map.put("client_version", AppHelper.getVersionCode(mContext) + "");
			map.put("client_channel_type", ManifestUtil.readMetaData(mContext, "UMENG_CHANNEL"));
			map.put("os_type", AppHelper.getOSTypeNew());
			map.put("timestamp", timestamp);
			map.put("imsi", imsi);
			map.put("imei", imei);
			map.put("mobile", phone);
			map.put("city_code", cityCode);
			map.put("org_code", orgCode);
			map.put("longitude", longitude);
			map.put("latitude", latitude);
//			map.put("token", "unknown");
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.jsonBody = JsonUtil.toJson(map);
		this.userAgent = getUserAgent(mContext, productId);
	}

	/**
	 * 设置请求的参数
	 * @param params
	 * 		请求的参数，放置于Map对象中
	 * @param url
	 * 		请求的地址
	 */
	public void setParams(String url, String jsonBody, String productId) {
		this.mUrl = url;
		this.jsonBody = jsonBody;
		this.userAgent = getUserAgent(mContext, productId);
	}

	/**
	 * 获取httpclient的user-agent;
	 * @param context
	 * @return
	 */
	public static String getUserAgent(Context context, String productId) {
		String versionCode = String.valueOf(AppHelper.getVersionCode(context));
		String osType = AppHelper.getOSTypeNew();
		String osCode = AppHelper.getOSRelease();
		String brand = AppHelper.getBrand();
		String width = String.valueOf(AppHelper.getScreenWidth(context));
		String height = String.valueOf(AppHelper.getScreenHeight(context));
		StringBuilder sb = new StringBuilder();
		sb.append("Client");
		sb.append("(");
		sb.append(productId);
		sb.append("/");
		sb.append(versionCode);
		sb.append(";");
		sb.append(osType);
		sb.append("/");
		sb.append(osCode);
		sb.append(";");
		sb.append(brand);
		sb.append(";");
		sb.append(width);
		sb.append("*");
		sb.append(height);
		sb.append(")");
		return sb.toString();
	}

	@Override
	protected BaseResp doInBackground(Void... arg0) {
		HttpRequestJson request = new HttpRequestJson(method, BaseResp.class);
		BaseResp resp = new BaseResp();
		try {
			request.initParameter(jsonBody, userAgent);

			if (!StringUtil.isEmpty(mContentType)) {
				request.addHeader("content-Type", mContentType);
			}
			resp = request.executeV3(mUrl);

			// 解析Json数据，转为对象
			if (resp.isSuccess()) {
				String result = resp.getHttpResult();
				if (!StringUtil.isEmpty(result) && clazz != null) {
					Object obj = JsonUtil.toObject(result, clazz);
					resp.setObj(obj);
				}
			}
		} catch (Exception e) {
			Log.e("请求数据异常，异常信息：" + e);
			if (resp == null)
				resp = new BaseResp();
			resp.setStatus(BaseResp.ERROR);
		} finally {
			request.release();
		}

		return resp;
	}

	/**把数据源HashMap转换成json
	 * @param map
	 */
	public static String hashMapToJson(HashMap map) {
		String string = "{";
		for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
			Map.Entry e = (Map.Entry) it.next();
			string += "'" + e.getKey() + "':";
			string += "'" + e.getValue() + "',";
		}
		string = string.substring(0, string.lastIndexOf(","));
		string += "}";
		return string;
	}

}
