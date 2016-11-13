package com.wookii.tools.net;

import java.io.UnsupportedEncodingException;

public abstract class WookiiHttpContent {


	public byte[] getDataBytes() {
		// TODO Auto-generated method stub
		try {
			return onData().getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return onData();
	}


	public abstract String onData();
	
	public abstract String put(String key, String value);
}
