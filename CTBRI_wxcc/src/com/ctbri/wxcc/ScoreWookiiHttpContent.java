package com.ctbri.wxcc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;
import com.wookii.tools.net.WookiiHttpContent;

public class ScoreWookiiHttpContent extends WookiiHttpContent {

	private ArrayList<BasicNameValuePair> pairs;

	public ScoreWookiiHttpContent(ArrayList<BasicNameValuePair> pairs) {
		// TODO Auto-generated constructor stub
		this.pairs = pairs;
	}

	@Override
	public String onData() {
		// TODO Auto-generated method stub
		Map<String, Object> mapObj = new HashMap<String, Object>();
		for (BasicNameValuePair pair : pairs) {
			mapObj.put(pair.getName(), pair.getValue());
		}
		Gson gson = new Gson();
		String json = gson.toJson(mapObj);
		return json;
	}

	@Override
	public String put(String key, String value) {
		pairs.add(new BasicNameValuePair(key, value));
		return value;
	}

}
