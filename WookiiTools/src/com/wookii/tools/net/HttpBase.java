package com.wookii.tools.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;

import com.wookii.tools.comm.LogS;
import com.wookii.tools.comm.NetWorkResponseListener;

public abstract class HttpBase implements NetWorkResponseListener{

	private static final String TAG = "HttpBase";
	public static final String HTTP_REQUEST_URL = null;
	private final String encode = "UTF-8";
	private final int connectionTimeout = 1000 *20;
	private final int SocketBufferSize = 10240;
	private WookiiHttpContent content;
	private String data;
	private HttpURLConnection conn;
	private HashMap<String, String> property;
	private IOException exception;
	abstract HttpURLConnection confighttpURLConnection(HttpURLConnection httpURLConnection);
	public void setRequestProperty(HashMap<String,String> property){
		this.property = property;
	}
	public void excuteHttpConnection(String urlStr) {
		LogS.i(TAG, urlStr);
		URL url = null;
		try {
			url = new URL(urlStr);
		} catch (MalformedURLException e) {
			setException(e);
			e.printStackTrace();
			return;
		}
		URLConnection rulConnection = null;
		try {
			rulConnection = url.openConnection();
		} catch (IOException e) {
			setException(e);
			e.printStackTrace();
			return;
		}  
		conn = (HttpURLConnection) rulConnection;  
		conn.setConnectTimeout(connectionTimeout);
		conn.setRequestProperty(
				"Accept",
				"image/gif, image/jpeg, image/pjpeg, "
						+ "application/x-shockwave-flash, application/xaml+xml, "
						+ "application/vnd.ms-xpsdocument, application/x-ms-xbap, "
						+ "application/x-ms-application, application/vnd.ms-excel, "
						+ "application/vnd.ms-powerpoint, application/msword, application/x-java-serialized-object,"
						+ "application/json, */*");
		conn.setRequestProperty("Accept-Language", "zh-CN");
		conn.setRequestProperty("Referer", urlStr);
		conn.setRequestProperty("Charset", encode);
		conn.setRequestProperty(
				"User-Agent",
				"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; "
						+ "Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; "
						+ ".NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
		conn.setRequestProperty("Connection", "Keep-Alive");
		if(property != null) {
			Iterator<String> iterator = property.keySet().iterator();
			while(iterator.hasNext()){
				String key = iterator.next();
				conn.setRequestProperty(key, property.get(key));
			}
		}
		conn = confighttpURLConnection(conn);
		InputStream inputStream;
		String str = "";
		try {
			inputStream = conn.getInputStream();
			byte[] b = new byte[128];
			StringBuilder sb = new StringBuilder();
			int len = -1;
			while ((len = inputStream.read(b)) != -1) {
				sb.append(new String(b, 0, len, "iso8859-1"));
			}
			str = new String(sb.toString().trim().getBytes("iso8859-1"),
					"utf-8");
		} catch (IOException e) {
			setException(e);
			e.printStackTrace();
			return;
		}
		LogS.i(TAG, str + "");
		this.data = onStream(str);
		
	}

	public void setException(IOException e) {
		this.exception = e;
	}
	public IOException getException(){
		return exception;
	}
	public void clearException(){
		this.exception = null;
	}
	public WookiiHttpContent getContent(){
		return this.content;
	}
	public void setContent(WookiiHttpContent content) {
		// TODO Auto-generated method stub
		this.content = content;
	}

	public String getData() {
		// TODO Auto-generated method stub
		return this.data;
	}
}
