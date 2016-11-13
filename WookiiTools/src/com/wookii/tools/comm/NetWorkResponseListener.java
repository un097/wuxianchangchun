package com.wookii.tools.comm;



public interface NetWorkResponseListener {
	
	public static final int ERROR_CODE_SUCCESS = 0;
	
	public static final int ERROR_CODE_EXCEPTION = 1;
	
	public static final int ERROR_CODE_UN_UPDATE = 2;
	
	String onStream(String str);
}
