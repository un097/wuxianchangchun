package cn.ffcs.wisdom.http;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import cn.ffcs.wisdom.tools.Log;

/**
 * HTTP请求,目前仅支持http请求,不支持https
 * 
 * @author  caijj
 * @version 1.00, 2012-4-6
 */
public class HttpRequest {

	private static final String URL_SEPARATOR = "?";
	private static final String PARAMETER_SEPARATOR = "&";
	private static final String NAME_VALUE_SEPARATOR = "=";
	private static final int OLDREQUEST = 0;// 旧接口
	private static final int NEWREQUEST = 1;// 新接口
	private static final int V2EQUEST = 2;// v2接口

	public static final String HTTP_METHOD_GET = "get";
	public static final String HTTP_METHOD_POST = "post";

	private String charSet;
	private String method;

	private HttpClient httpClient;
	private HttpRequestBase request;
	private List<NameValuePair> params;

	private Class<? extends BaseResp> clazz;

	public HttpRequest(Class<? extends BaseResp> clazz) {
		this(HTTP_METHOD_POST, clazz);
	}

	public HttpRequest(Class<? extends BaseResp> clazz, Context context) {
		// this(HTTP_METHOD_POST, clazz, context);
	}

	public HttpRequest(String method, Class<? extends BaseResp> clazz) {
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

	// // 设置3g网关代理，未使用
	// public HttpRequest(String method, Class<? extends BaseResp> clazz,
	// Context context) {
	// httpClient = new DefaultHttpClient();
	// // set timeout
	// httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
	// 5000);
	// if (CommonUtils.is3G(context)) {
	// HttpHost httpHost = new HttpHost("10.0.0.200", 80);
	// // 设置代理
	// httpClient.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY,
	// httpHost);
	// }
	// params = new ArrayList<NameValuePair>();
	// charSet = HTTP.UTF_8;
	// this.method = method;
	// this.clazz = clazz;
	// }

	public void setCharSet(String charSet) {
		this.charSet = charSet;
	}

	public void setHttpMethod(String method) {
		this.method = method;
	}

	/**
	 * add httprequest entiy
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
	 * 旧的接口协议
	 * @param url
	 * @return
	 */
	public BaseResp execute(String url) {
		return execute(url, OLDREQUEST);
	}

	/**
	 * 新的接口协议
	 * @param url
	 * @return
	 */
	public BaseResp executeUrl(String url) {
		return execute(url, NEWREQUEST);
	}

	/**
	 * V2版本新的接口协议
	 * @param url
	 * @return
	 */
	public BaseResp executeV2Url(String url) {
		return execute(url, V2EQUEST);
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
			request.addHeader("User-Agent", "ffcs/icity");

			if (request instanceof HttpGet) {
				url = prepareUrl(url);
			} else if (request instanceof HttpPost) {
				((HttpPost) request).setEntity(new UrlEncodedFormEntity(params, charSet));
			}

			request.setURI(URI.create(url));

			HttpResponse httpResp = httpClient.execute(request);
			int statusCode = httpResp.getStatusLine().getStatusCode();

			if (statusCode == 200) {
				sResult = EntityUtils.toString(httpResp.getEntity(), charSet, isZip);
				try {
					if (isNew == OLDREQUEST) {
						JSONObject jsonObject = new JSONObject(sResult); 
//						android.util.Log.e("sb", sResult);
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
							// if (status == 200) { // 当菜单未变化，返回200状态码时，
							// resp.setStatus("200");
							// } else {
							//
							// }
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
					}else if(isNew == V2EQUEST) {		//v2版本的请求格式
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
						if (jsonObject.has("timestamp")) {
							String timestamp = jsonObject.getString("timestamp");
							if (timestamp == null) {
								timestamp = "";
							}
							resp.setTimestamp(timestamp);
						}
						if (jsonObject.has("data")) {
							String data = jsonObject.getString("data");
							resp.setData(data);
						}
					}
				} catch (JSONException e) {
					resp.setStatus(BaseResp.ERROR);
				}
			} else {
				resp.setStatus(BaseResp.ERROR); // 访问不成功时，设置状态为ERROR
			}
		}  catch (ParseException e) {
			Log.e("ParseException error message:" + e.getMessage(), e);
			resp.setStatus(BaseResp.ERROR);
			// e.printStackTrace();
		} catch (IOException e) { // 网络问题
			Log.e("IOException error message:" + e.getMessage(), e);
			resp.setStatus(BaseResp.ERROR);
			// e.printStackTrace();
		}
		resp.setResult(sResult);
		return resp;
	}

	// public HttpResponse execute(HttpRequestBase request) throws
	// ClientProtocolException,
	// IOException {
	// this.request = request;
	// return httpClient.execute(request);
	// }

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

	/**
	 * 图片上传
	 * @param upUrl
	 * @param filePath
	 * @param fileName
	 * @return
	 */
	public String uploadFile(String upUrl, String filePath) {
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
		try {
			URL url = new URL(upUrl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setRequestMethod("POST");
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Charset", "UTF-8");
			con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			DataOutputStream ds = new DataOutputStream(con.getOutputStream());
			ds.writeBytes(twoHyphens + boundary + end);
			ds.writeBytes("Content-Disposition: form-data; " + "name=\"file\";filename=\""
					+ fileName + "\"" + end);
			ds.writeBytes(end);
			InputStream fStream = new FileInputStream(new File(filePath));
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];
			int length = -1;
			while ((length = fStream.read(buffer)) != -1) {
				ds.write(buffer, 0, length);
			}
			ds.writeBytes(end);
			ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
			fStream.close();
			ds.flush();
			InputStream is = con.getInputStream();
			int ch;
			StringBuffer b = new StringBuffer();
			while ((ch = is.read()) != -1) {
				b.append((char) ch);
			}
			ds.close();
			return b.toString();

		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
}
