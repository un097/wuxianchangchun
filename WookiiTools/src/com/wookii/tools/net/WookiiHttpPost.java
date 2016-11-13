package com.wookii.tools.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.util.ArrayList;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wookii.tools.comm.NetWorkResponseListener;

public class WookiiHttpPost extends HttpBase {

	
	@Override
	HttpURLConnection confighttpURLConnection(
			HttpURLConnection httpURLConnection) {
		// TODO Auto-generated method stub
		httpURLConnection.setDoOutput(true);
		httpURLConnection.setDoInput(true);
		httpURLConnection.setUseCaches(false);
		try {
			httpURLConnection.setRequestMethod("POST");
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(getContent() != null) {
			OutputStream outputStream = null;
			try {
				outputStream = httpURLConnection.getOutputStream();
				outputStream.write(getContent().getDataBytes());
				outputStream.flush();
				outputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return httpURLConnection;
	}

	@Override
	public String onStream(String str) {
		// TODO Auto-generated method stub
		return str;
	}


}
