package cn.ffcs.wisdom.base;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.http.HttpRequest;
import cn.ffcs.wisdom.tools.JsonUtil;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.StringUtil;

import com.umeng.analytics.MobclickAgent;

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
public class CommonV3Task extends BaseTask<Void, Void, Void> {

	private Map<String, String> mParams = new HashMap<String, String>(1);
	private String mUrl;
	private Class<?> clazz;
	
	private String serviceType;

	/**
	 * 获取CommonTask对象，每次调用，新创建一个对象
	 * @param iCall	
	 * 		回调方法
	 * @param context
	 * 		上下文
	 * @param toJsonObjectClass
	 * 		结果实体类，需要JSON解析获取
	 * @return
	 */
	public static CommonV3Task newInstance(HttpCallBack iCall, Context context, Class<?> toJsonObjectClass) {
		return new CommonV3Task(iCall, context, toJsonObjectClass);
	}
	
	/**
	 * 获取CommonTask对象，每次调用，新创建一个对象
	 * @param iCall	
	 * 		回调方法
	 * @param toJsonObjectClass
	 * 		结果实体类，需要JSON解析获取
	 * @return
	 */
//	public static CommonNewTask newInstance(HttpCallBack iCall, Class<?> toJsonObjectClass) {
//		return new CommonNewTask(iCall, toJsonObjectClass);
//	}

	private CommonV3Task(HttpCallBack iCall, Context context, Class<?> clazz) {
		super(iCall, context);
		this.clazz = clazz;
	}
	
	private CommonV3Task(HttpCallBack iCall, Class<?> clazz) {
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
	public void setParams(Map<String, String> params, String url) {
		if (params != null) {
			this.mParams.clear();
			this.mParams.putAll(params);
		}
		this.mUrl = url;
	}

	@Override
	protected BaseResp doInBackground(Void... parameters) {
		umentEvent();
		
		HttpRequest request = new HttpRequest(BaseResp.class);
		BaseResp resp = new BaseResp();
		try {
			request.addParameter(mParams);
			resp = request.executeUrl(mUrl);
			
			//解析Json数据，转为对象
			if(resp.isSuccess()) {
				String result = resp.getData();
				if(!StringUtil.isEmpty(result) && clazz != null) {
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
	
	@Override
	protected void onPostExecute(BaseResp response) {
		super.onPostExecute(response);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("serviceType", serviceType);
		if(response.isSuccess()){
			MobclickAgent.onEvent(mContext, "30000", map);
		} else {
			Map<String, String> params = new HashMap<String, String>();
			params.put("desc", response.getDesc());
			params.put("state", response.getStatus());
			MobclickAgent.onEvent(mContext, "30001", map);
		}
	}
	
	@Override
	protected void onNetWorkError() {
		super.onNetWorkError();
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("serviceType", serviceType);
		MobclickAgent.onEvent(mContext, "30001", map);
	}
	
	protected void umentEvent() {
		String url = mUrl;
		if (!url.contains("serviceType") || !url.contains("?")) {
			return;
		}
		String str = url.substring(url.indexOf("?") + 1, url.length());
		String[] params = str.split("&");

		if (params.length == 0) return;

		for (String s : params) {
			if (s.contains("serviceType")) {
				String[] param = s.split("=");
				if (param.length >= 2) {
					serviceType = param[1];
				}
			}
		}
		
		if(StringUtil.isEmpty(serviceType)){
			serviceType = mUrl;
		}
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("serviceType", serviceType);
		MobclickAgent.onEvent(mContext, "3000", map);
	}
}
