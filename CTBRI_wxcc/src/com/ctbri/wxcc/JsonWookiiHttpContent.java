package com.ctbri.wxcc;

import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;

import com.wookii.tools.net.WookiiHttpContent;

public class JsonWookiiHttpContent extends WookiiHttpContent {

	private ArrayList<BasicNameValuePair> pairs;

	public JsonWookiiHttpContent(ArrayList<BasicNameValuePair> pairs) {
		// TODO Auto-generated constructor stub
		this.pairs = pairs;
	}

	@Override
	public String onData() {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (BasicNameValuePair pair : pairs) {
			sb.append(pair.getName() + "=").append(pair.getValue());
			if (i < pairs.size() - 1) {
				sb.append("&");
			}
			i++;
		}
		return sb.toString();
	}

	@Override
	public String put(String key, String value) {
		pairs.add(new BasicNameValuePair(key, value));
		return value;
	}

}
