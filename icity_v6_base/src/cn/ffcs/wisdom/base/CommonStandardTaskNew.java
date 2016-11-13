package cn.ffcs.wisdom.base;

import java.util.Map;

import android.content.Context;
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
@SuppressWarnings("rawtypes")
public class CommonStandardTaskNew extends BaseTask<Void, Void, Void> {

	private String jsonBody;// 字符串参数
	private String userAgent;// user-Agent
	private String mUrl;
	private Class<?> clazz;
	private String method = HttpRequest.HTTP_METHOD_POST;

	private String mContentType;

	public void setContentType(String contentType) {
		this.mContentType = contentType;
	}

	public CommonStandardTaskNew(HttpCallBack iCall, Context context) {
		super(iCall, context);
	}

	public static CommonStandardTaskNew newInstance(HttpCallBack iCall, Context context,
			Class<?> toJsonObjectClass) {
		return new CommonStandardTaskNew(iCall, context, toJsonObjectClass);
	}

	private CommonStandardTaskNew(HttpCallBack iCall, Context context, Class<?> clazz) {
		super(iCall, context);
		this.clazz = clazz;
	}

	private CommonStandardTaskNew(HttpCallBack iCall, Class<?> clazz) {
		super(iCall);
		this.clazz = clazz;
	}

	/**
	 * 添加请求参数
	 * @param url
	 * @param map
	 * @param productId(可为null)
	 * @param phone(可为null)
	 * @param orgCode(可为mull)
	 */
	public void setParams(String url, Map<String, String> map, String productId, String phone,
			String orgCode) {
		this.mUrl = url;
		String timestamp = TimeUitls.getCurrentTime();
		try {
			String imsi = AppHelper.getMobileIMSI(mContext);
			if (StringUtil.isEmpty(imsi)) {
				imsi = "0000000000000000";
			}
			String imei = AppHelper.getIMEI(mContext);
			String md5 = CrytoUtils.md5(phone + "$" + imsi + "$" + imei, CrytoUtils.MD5KEY,
					timestamp);
			String sign = CrytoUtils.encode(CrytoUtils.DESKEY, timestamp, md5);
			map.put("sign", sign);
			map.put("os_type", AppHelper.getOSTypeNew());
			map.put("client_version", AppHelper.getVersionCode(mContext) + "");
			map.put("mobile", phone);
			map.put("imsi", imsi);
			map.put("imei", imei);
			map.put("product_id", productId);
			map.put("timestamp", timestamp);
			map.put("base_line", "400");
			map.put("org_code", orgCode);
			map.put("client_channel_type", ManifestUtil.readMetaData(mContext, "UMENG_CHANNEL"));
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

}
