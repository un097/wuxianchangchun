package cn.ffcs.wisdom.city.simico.kit.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.text.TextUtils;

public class HttpUtils {
	public static final int DEF_CONNECT_TIMEOUT = 10000;
	public static final int DEF_SOKET_TIMEOUT = 10000;
	public static final int DEFAULT_PROXY_PORT = 80;
	public static final int HTTP_OK_CODE = 202;
	public static final int TYPE_WAP = 1;
	public static final int TYPE_NET = 2;
	public static final int TYPE_UNKNOWN = 3;
	public static final String DEFAULT_ENCODE = HTTP.UTF_8;
	private static final String HTTP_ACCEPT_ENCODING = "Accept-Encoding";
	
	public HttpUtils() {
	}
	
	private static HttpUriRequest getHttpGetRequest(String url,boolean flag,Context context) {
		HttpGet httpget = new HttpGet(url);
		httpget.setHeader(HTTP_ACCEPT_ENCODING, "gzip, deflate");
		httpget.getParams().setBooleanParameter("http.protocol.handle-redirects", flag);
		return httpget;
	}
	
	public static HttpClient getHttpClient(int connectTimeout,int soketTimeout,Context context){
		BasicHttpParams baseParams = new BasicHttpParams();
		HttpProtocolParams.setVersion(baseParams, HttpVersion.HTTP_1_1);//
		HttpProtocolParams.setContentCharset(baseParams, "utf-8");//
		HttpConnectionParams.setConnectionTimeout(baseParams, connectTimeout);
		HttpConnectionParams.setSoTimeout(baseParams, soketTimeout);
		//HttpUtils.fillProxy(context, baseParams);
		DefaultHttpClient client = new DefaultHttpClient(baseParams);
		client.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(3,true));
		return client;
	}
	
	public static HttpResponse doRawGet(String url)
			throws IllegalStateException, IOException {
		return doRawGet(url, false, DEF_CONNECT_TIMEOUT, DEF_SOKET_TIMEOUT,
				null);
	}
	
	private static HttpResponse doRawGet(String url, boolean flag,
			int connectTimeout, int soketTimeout, Context context)
			throws IllegalStateException, IOException {
		// get http uri request
		HttpUriRequest uriRequest = getHttpGetRequest(url,flag,context);
		// get http client
		HttpClient client = getHttpClient(connectTimeout, soketTimeout, context);
		// execute uri request
		HttpResponse response = client.execute(uriRequest);
		return response;
	}

	private static HttpEntity doGet(String url, boolean flag, int timeout,
			Context context) throws IllegalStateException, IOException {
		HttpEntity httpEntity = null;
		HttpResponse response = doRawGet(url, flag, timeout, timeout, context);
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			httpEntity = response.getEntity();
		}
		return httpEntity;
	}
	
	public static InputStream getStringStream(String str) {
		ByteArrayInputStream bais = null;
		if (str != null && !str.trim().equals("")) {
			bais = new ByteArrayInputStream(str.getBytes());
		}
		return bais;
	}
	
	public static String entityToString(HttpEntity entity) throws IOException {
		return entityToString(entity, DEFAULT_ENCODE);
	}

	private static boolean isGzipEntity(HttpEntity entity) {
		if (entity.getContentEncoding() != null
				&& entity.getContentEncoding().getValue().contains("gzip")
				|| entity.getContentType() != null
				&& "application/zip".equals(entity.getContentType()
						.getValue())) {
			return true;
		}
		return false;
	}
	
	public static String entityToString(HttpEntity entity, String encode)
			throws IOException {
		if (entity != null) {
			String content;
			if (isGzipEntity(entity)) {
				GZIPInputStream is = new GZIPInputStream(entity.getContent());
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				IOUtils.copy(is, bos);
				bos.close();
				content = new String(bos.toByteArray(), encode);
			} else {
				content = EntityUtils.toString(entity, encode);
			}
			return content;
		}
		return null;
	}
	
	/**
	 * 下载文件
	 * 
	 * @param url
	 * @param filePath
	 * @param context
	 * @return
	 */
	public static File toFile(String url, String filePath, Context context) {
		return toFile(url, filePath, context, null);
	}

	/**
	 * 下载文件
	 * 
	 * @param url
	 * @param filePath
	 * @param context
	 * @param listener
	 * @return
	 */
	public static File toFile(final String url, String filePath, Context context,
			final OnDownloadListener listener) {
		if (TextUtils.isEmpty(url)) {
			if (listener != null) {
				listener.onError(url);
			}
			return null;
		}
		if (TextUtils.isEmpty(filePath)) {
			if (listener != null) {
				listener.onError(url);
			}
			return null;
		}
		InputStream is = null;
		FileOutputStream fos = null;
		try {
			File file = new File(filePath);
			if (file.exists()) {
				file.delete();
			}
			File parent = file.getParentFile();
			if (parent != null && !parent.exists()) {
				parent.mkdirs();
			}
			//try {
				//if(!file.exists())
				//	file.createNewFile();
			//} catch (IOException e) {
			//	 e.printStackTrace();
			//}
			HttpEntity entity = doGet(url, true,
					DEF_CONNECT_TIMEOUT, context);
			if (entity != null) {
				is = entity.getContent();
				final long fileLength = entity.getContentLength();
				fos = new FileOutputStream(file);
				if (listener != null) {
					IOUtils.copy(is, fos, new IOUtils.CopyListener() {
						@Override
						public void onCopy(int readLen, long copyLen) {
							listener.onDownload(url,readLen, copyLen, fileLength);
						}
					});
				} else {
					IOUtils.copy(is, fos);
				}
				return file;
			} else {
				if (listener != null) {
					listener.onError(url);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (listener != null) {
				listener.onError(url);
			}
		} finally {
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(fos);
		}
		return null;
	}
}
