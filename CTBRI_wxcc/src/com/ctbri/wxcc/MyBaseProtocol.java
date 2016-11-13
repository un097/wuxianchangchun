package com.ctbri.wxcc;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.os.Handler;
import android.util.Base64;

import com.wookii.tools.comm.DateTool;
import com.wookii.tools.net.BaseProtocol;
import com.wookii.tools.net.HttpBase;
import com.wookii.tools.net.WookiiHttpContent;

public class MyBaseProtocol extends BaseProtocol {

	private static final String PHONE = "1";
	private static final String CLIENT_TYPE = PHONE;
	private static final String ANDROID = "1";
	private static final String CLIENT_OS = ANDROID;
	private static final String DEFAULT_URL_ENCODING = "UTF-8";
	private String[] keys = new String[]{"TimeStamp","Nonce","Api-Version","Client-Type","Client-OS","Sign-Method"};
	private String propertyStr;
	private HashMap<String, String> property;
	private WookiiHttpContent content;
	private int what;
	public MyBaseProtocol(Handler handler, HttpBase httpBase, String methodName) {

		super(handler, httpBase, methodName);
		Arrays.sort(keys);
		property = new HashMap<String,String>();
		property.put("TimeStamp",
				String.valueOf(DateTool.getLongMilli()));
		property.put("Nonce",
				String.valueOf(getRadom()));
		property.put("Api-Version",
				"1.0");
		property.put("Client-Type",
				CLIENT_TYPE);
		property.put("Client-OS",
				CLIENT_OS);
		property.put("Sign-Method",
				"HMAC-SHA1");
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (String key : keys) {
			sb.append(key + "=").append(property.get(key));
			if(i < property.size() -1){
				sb.append("&");
			}
			i ++;
			
		}
		propertyStr = sb.toString();
	}
	
	@Override
	public void startInvoke(WookiiHttpContent content, int what) {
		// TODO Auto-generated method stub
		if(content != null) {
			String[] split = content.toString().split("&");
			Arrays.sort(split);
			StringBuilder sb = new StringBuilder();
			int i = 0;
			for (String nameValue : split) {
				sb.append(nameValue);
				if(i < split.length -1){
					sb.append("&");
				}
				i ++;
				
			}
			String nameValueStr = sb.toString();
			HttpBase httpBase = getHttpBase();
			String en = urlEncode(propertyStr + "&" + nameValueStr);
			String secret = "w4drfrzyk541xda6i0o75y7eefhjuuaj" + "&";
			String sign = hmacSha1(secret, en);
			property.put("Sign",
					sign);
			httpBase.setRequestProperty(property);
		}
		this.content = content;
		this.what = what;
		super.startInvoke(content, what);
	}
	/**
	 * URL 编码, Encode默认为UTF-8.
	 */
	public String urlEncode(String part) {
		String en = "";
		try {
			en  = URLEncoder.encode(part, DEFAULT_URL_ENCODING);
		} catch (UnsupportedEncodingException e) {
		}
		return en;
	}
	public int getRadom() {
		int max = 10000000;
		int min = 99999999;
		Random random = new Random();
		return random.nextInt(max) % (max - min + 1) + min;
	}
	
	private String hmacSha1(String key, String datas) {
		String reString = "";

		try {
			byte[] data = key.getBytes("UTF-8");
			SecretKey secretKey = new SecretKeySpec(data, "HmacSHA1");
			Mac mac = Mac.getInstance("HmacSHA1");
			mac.init(secretKey);
			byte[] text = datas.getBytes("UTF-8");
			byte[] text1 = mac.doFinal(text);
			reString = Base64.encodeToString(text1, Base64.DEFAULT);

		} catch (Exception e) {
			// TODO: handle exception
		}
		return reString;
	}
}
