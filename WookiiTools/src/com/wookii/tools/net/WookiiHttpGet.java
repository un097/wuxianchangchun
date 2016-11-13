package com.wookii.tools.net;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class WookiiHttpGet extends HttpBase {

	@Override
	public String onStream(String str) {
		// TODO Auto-generated method stub
		return str;
	}

	@Override
	HttpURLConnection confighttpURLConnection(
			HttpURLConnection httpURLConnection) {
		// TODO Auto-generated method stub
		try {
			httpURLConnection.connect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return httpURLConnection;
	}

}
