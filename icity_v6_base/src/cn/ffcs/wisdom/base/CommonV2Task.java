package cn.ffcs.wisdom.base;

import android.content.Context;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.http.HttpRequest;
import cn.ffcs.wisdom.http.HttpRequestJson;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.JsonUtil;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.StringUtil;

/**
 * <p>Title: 通用Task请求 ,继承BaseTask，完善请求    </p>
 * <p>Description: 
 *  调用该类注意事项:<br>
 *  1. 实例化：CommonTask task = CommonTask.newInstance(icall, context, class);<br>
 *  2. 设置参数：task.setParams(maps, url);<br>
 *  3. 解析完后的Json对象，从BaseResp中的getObj()获取;<br>
 * </p>
 * <p>@author: Eric.wsd                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-1-17             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
@SuppressWarnings("rawtypes")
public class CommonV2Task extends BaseTask<Void, Void, Void> {

	private String jsonBody;// 字符串参数
	private String userAgent;// user-Agent
	private String mUrl;
	private Class<?> clazz;
	private String method = HttpRequest.HTTP_METHOD_POST;

	public CommonV2Task(HttpCallBack iCall, Context context) {
		super(iCall, context);
	}

	public static CommonV2Task newInstance(HttpCallBack iCall, Context context,
			Class<?> toJsonObjectClass) {
		return new CommonV2Task(iCall, context, toJsonObjectClass);
	}

	private CommonV2Task(HttpCallBack iCall, Context context, Class<?> clazz) {
		super(iCall, context);
		this.clazz = clazz;
	}

	private CommonV2Task(HttpCallBack iCall, Class<?> clazz) {
		super(iCall);
		this.clazz = clazz;
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
	public static String getUserAgent(Context context,String productId) {
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
			resp = request.executeV2(mUrl);

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
