package com.ctbri.wxcc;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.os.Handler;
import android.util.Base64;

import com.wookii.tools.net.BaseProtocol;
import com.wookii.tools.net.HttpBase;
import com.wookii.tools.net.WookiiHttpContent;

public class ScoreProtocol extends BaseProtocol {

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
	public ScoreProtocol(Handler handler, HttpBase httpBase, String methodName) {

		super(handler, httpBase, methodName);
	}
	
	@Override
	public void startInvoke(WookiiHttpContent content, int what) {
		// TODO Auto-generated method stub
		if(content != null) {
			HttpBase httpBase = getHttpBase();
			property.put("User-Agent",
					"Client(icity-fuzhou/2.0;ios/6.0;iphone4;320*480)");
			property.put("Content-Type", "application/json;charset=UTF-8");
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
