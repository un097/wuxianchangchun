package com.wookii.tools.net;

import com.wookii.tools.comm.LogS;

import android.os.Handler;
import android.os.Message;

public class BaseProtocol{


	protected static final String TAG = "BaseProtocol";
	private HttpBase httpBase;
	private String methodName;
	private Handler handler;
	public BaseProtocol(Handler handler, HttpBase httpBase ,String methodName){
		
		this.httpBase = httpBase;
		this.methodName = methodName;
		this.handler = handler;
	}
	
	public void startInvoke(final WookiiHttpContent content, final int what) {

		new Thread(){
			public void run() {
				String requestUrl = methodName;
				if(content != null){
					try {
						WookiiHttpGet wookiiHttpGet = (WookiiHttpGet)httpBase;
						requestUrl += "?";
						requestUrl += content.toString();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						httpBase.setContent(content);
					}
					LogS.i(TAG, content.toString());
				}
				httpBase.excuteHttpConnection(requestUrl);
				Message msg = Message.obtain();
				if(httpBase.getException() != null){
					msg.what = what;
					msg.obj  = httpBase.getException().getMessage();
					httpBase.clearException();
				} else {
					String data = httpBase.getData();
					msg.what = what;
					msg.obj  = data;
				}
				handler.sendMessage(msg);
			};
		}.start();
	}

	public HttpBase getHttpBase() {
		return httpBase;
	}

	public void setHttpBase(HttpBase httpBase) {
		this.httpBase = httpBase;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}
	
	
}
