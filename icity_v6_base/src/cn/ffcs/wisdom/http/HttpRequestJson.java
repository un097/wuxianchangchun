package cn.ffcs.wisdom.http;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import cn.ffcs.wisdom.tools.Log;

public class HttpRequestJson {
	private static final String URL_SEPARATOR = "?";
	private static final String PARAMETER_SEPARATOR = "&";
	private static final String NAME_VALUE_SEPARATOR = "=";
	private static final int OLDREQUEST = 0;// 旧接口
	private static final int NEWREQUEST = 1;// 新接口
	private static final int V2EQUEST = 2;// v2接口
	private static final int V3EQUEST = 3;// v2接口

	public static final String HTTP_METHOD_GET = "get";
	public static final String HTTP_METHOD_POST = "post";

	private String charSet;
	private String method;
	private String jsonBody;// 字符串参数
	private String userAgent;// user-Agent
	private HttpClient httpClient;
	private HttpRequestBase request;
	private List<NameValuePair> params;

	private Class<? extends BaseResp> clazz;

	public HttpRequestJson(Class<? extends BaseResp> clazz) {
		this(HTTP_METHOD_POST, clazz);
	}

	public HttpRequestJson(Class<? extends BaseResp> clazz, Context context) {
		// this(HTTP_METHOD_POST, clazz, context);
	}

	public HttpRequestJson(String method, Class<? extends BaseResp> clazz) {
		httpClient = new DefaultHttpClient();
		// set timeout
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 15000);
		params = new ArrayList<NameValuePair>();
		charSet = HTTP.UTF_8;
		this.method = method;
		this.clazz = clazz;

		if (this.method.equals(HTTP_METHOD_GET)) {
			request = new HttpGet();
		} else {
			HttpPost post = new HttpPost();
			request = post;
		}
	}

	public void setCharSet(String charSet) {
		this.charSet = charSet;
	}

	public void setHttpMethod(String method) {
		this.method = method;
	}
	
	public void addHeader(String key, String value){
		request.addHeader("Content-Type", "application/json;charset=UTF-8");
	}

	/**
	 * add HttpRequestJson entiy
	 */
	public void addParameter(String key, String value) {
		params.add(new BasicNameValuePair(key, value));
	}

	public void addParameter(Map<String, String> maps) {
		if (maps != null && maps.size() > 0) {
			Set<String> keys = maps.keySet();
			Iterator<String> it = keys.iterator();
			while (it.hasNext()) {
				String key = it.next();
				params.add(new BasicNameValuePair(key, maps.get(key)));
			}
		}
	}

	/**
	 * 初始化参数
	 * @param jsonBody
	 * @param userAgent
	 */
	public void initParameter(String jsonBody, String userAgent) {
		this.jsonBody = jsonBody;
		this.userAgent = userAgent;
	}

	/**
	 * 旧的接口协议
	 * @param url
	 * @return
	 */
	public BaseResp execute(String url) {
		return execute(url, OLDREQUEST);
	}
	
	/**
	 * 旧的接口协议
	 * @param url
	 * @return
	 */
	public BaseResp executeNew(String url) {
		return execute(url, NEWREQUEST);
	}

	/**
	 * 旧的接口协议
	 * @param url
	 * @return
	 */
	public BaseResp executeV2(String url) {
		return execute(url, V2EQUEST);
	}
	
	public BaseResp executeV3(String url){
		return execute(url, V3EQUEST);
	}

	public BaseResp execute(String url, int isNew) {
		params.add(new BasicNameValuePair("product_version", "6"));// 临时写在这里，用于标示v6版本，后续看如何提取到上层。
		BaseResp resp = newInstance();

		if (resp == null) {
			resp = new BaseResp(BaseResp.ERROR);
			return resp;
		}

		String sResult = null;

		try {

			// URI uri = request.getURI();
			// String host = uri.getHost();
			boolean isZip = Boolean.FALSE;
			// if("www.153.cn".equals(host) || "218.5.99.35".equals(host)) {
			// request.addHeader("Accept-Encoding", "gzip,deflate");
			// request.addHeader("Cache-Control", "no-cache");
			// isZip = Boolean.TRUE;
			// }

			// 添加User-Agent头
			request.addHeader("User-Agent", userAgent);

			if (request instanceof HttpGet) {
				url = prepareUrl(url);
			} else if (request instanceof HttpPost) {
				HttpEntity entity = new StringEntity(jsonBody, charSet);
				((HttpPost) request).setEntity(entity);
			}

			request.setURI(URI.create(url));
			HttpResponse httpResp = httpClient.execute(request);
			int statusCode = httpResp.getStatusLine().getStatusCode();

			if (statusCode == 200) {
				sResult = EntityUtils.toString(httpResp.getEntity(), charSet, isZip);
				try {
					if (isNew == OLDREQUEST) {
						JSONObject jsonObject = new JSONObject(sResult);
						if (jsonObject.has("status")) {
							String status = jsonObject.getString("status");
							if ("200".equals(status)) { // 当菜单未变化，返回200状态码时，
								resp.setStatus(BaseResp.SUCCESS);
							} else {
								resp.setStatus(status);
							}
						}
						if (jsonObject.has("desc")) {
							String desc = jsonObject.getString("desc");
							resp.setDesc(desc);
						}
					} else if (isNew == NEWREQUEST) {
						JSONObject jsonObject = new JSONObject(sResult);
						if (jsonObject.has("responseCode")) {
							int status = jsonObject.getInt("responseCode");
							resp.setStatus(String.valueOf(status));
						}
						if (jsonObject.has("msgInfo")) {
							String desc = jsonObject.getString("msgInfo");
							if (desc == null) {
								desc = "";
							}
							resp.setDesc(desc);
						}
						if (jsonObject.has("msgDetailInfo")) {
							String detailDesc = jsonObject.getString("msgDetailInfo");
							if (detailDesc == null) {
								detailDesc = "";
							}
							resp.setDetailDesc(detailDesc);
						}
						if (jsonObject.has("data")) {
							String data = jsonObject.getString("data");
							resp.setData(data);
						}
					} else if (isNew == V2EQUEST) { //v2版本的请求格式
						JSONObject jsonObject = new JSONObject(sResult);
						if (jsonObject.has("response_code")) {
							int status = jsonObject.getInt("response_code");
							// if (status == 200) { // 当菜单未变化，返回200状态码时，
							// resp.setStatus("200");
							// } else {
							//
							// }
							resp.setStatus(String.valueOf(status));
						}
						if (jsonObject.has("response_info")) {
							String desc = jsonObject.getString("response_info");
							if (desc == null) {
								desc = "";
							}
							resp.setDesc(desc);
						}
						if (jsonObject.has("response_desc")) {
							String desc = jsonObject.getString("response_desc");
							if (desc == null) {
								desc = "";
							}
							resp.setDesc(desc);
						}
						if (jsonObject.has("timestamp")) {
							String timestamp = jsonObject.getString("timestamp");
							if (timestamp == null) {
								timestamp = "";
							}
							resp.setTimestamp(timestamp);
						}
						if (jsonObject.has("data")) {
							String data = jsonObject.getString("data");
							if (data == null) {
								data = "";
							}
							resp.setData(data);
						}
					}else if(isNew == V3EQUEST){
						JSONObject jsonObject = new JSONObject(sResult);
						if (jsonObject.has("result_code")) {
							int status = jsonObject.getInt("result_code");
							resp.setStatus(String.valueOf(status));
						}
						if (jsonObject.has("result_desc")) {
							String desc = jsonObject.getString("result_desc");
							if (desc == null) {
								desc = "";
							}
							resp.setDesc(desc);
						}
						if (jsonObject.has("response_code")) {
							int status = jsonObject.getInt("response_code");
							resp.setStatus(String.valueOf(status));
						}
						if (jsonObject.has("response_info")) {
							String desc = jsonObject.getString("response_info");
							if (desc == null) {
								desc = "";
							}
							resp.setDesc(desc);
						}
						if (jsonObject.has("response_desc")) {
							String desc = jsonObject.getString("response_desc");
							if (desc == null) {
								desc = "";
							}
							resp.setDesc(desc);
						}
						if (jsonObject.has("timestamp")) {
							String timestamp = jsonObject.getString("timestamp");
							if (timestamp == null) {
								timestamp = "";
							}
							resp.setTimestamp(timestamp);
						}
						if (jsonObject.has("data")) {
							String data = jsonObject.getString("data");
							if (data == null) {
								data = "";
							}
							resp.setData(data);
						}
					}
				} catch (JSONException e) {
					resp.setStatus(BaseResp.ERROR);
				}
			} else {
				resp.setStatus(BaseResp.ERROR); // 访问不成功时，设置状态为ERROR
			}
		} catch (ParseException e) {
			Log.e("ParseException error message:" + e.getMessage(), e);
			resp.setStatus(BaseResp.ERROR);
		} catch (IOException e) { // 网络问题
			Log.e("IOException error message:" + e.getMessage(), e);
			resp.setStatus(BaseResp.ERROR);
		}
		resp.setResult(sResult);
		return resp;
	}

	private BaseResp newInstance() {
		try {
			return (BaseResp) clazz.newInstance();
		} catch (IllegalAccessException e) {
			// e.printStackTrace();
			Log.e(e.getMessage(), e);
		} catch (InstantiationException e) {
			// e.printStackTrace();
			Log.e(e.getMessage(), e);
		}
		return null;
	}

	private String prepareUrl(String url) {
		if (params == null)
			params = new ArrayList<NameValuePair>();
		StringBuffer sb = new StringBuffer(url);
		for (int i = 0; i < params.size(); i++) {
			NameValuePair pair = params.get(i);
			url = sb.toString();
			if (url.contains(URL_SEPARATOR)) {
				if (url.endsWith(PARAMETER_SEPARATOR) || url.endsWith(URL_SEPARATOR)) {
					parameterJoin(sb, "", pair);
				} else {
					parameterJoin(sb, PARAMETER_SEPARATOR, pair);
				}

			} else {
				if (i == 0)
					parameterJoin(sb, URL_SEPARATOR, pair);
				else
					parameterJoin(sb, PARAMETER_SEPARATOR, pair);
			}
		}
		return sb.toString();
	}

	protected void parameterJoin(StringBuffer sb, String join, NameValuePair pair) {
		sb.append(join + pair.getName() + NAME_VALUE_SEPARATOR + pair.getValue());
	}

	public void release() {
		if (request != null)
			request.abort();
	}

}
