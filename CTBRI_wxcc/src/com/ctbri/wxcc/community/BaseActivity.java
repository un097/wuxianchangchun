package com.ctbri.wxcc.community;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.ctbri.comm.util._Utils;
import com.ctbri.wxcc.CurrentBean;
import com.ctbri.wxcc.JsonWookiiHttpContent;
import com.ctbri.wxcc.MessageEditor;
import com.ctbri.wxcc.MyBaseProtocol;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;
import com.wookii.tools.comm.LogS;
import com.wookii.tools.net.WookiiHttpPost;

public class BaseActivity extends FragmentActivity {
	/**
	 * 标示是否是第一次启动
	 */
	private boolean isFirstLaunch = true;

	@Override
	protected void onCreate(Bundle arg0) {
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		
		if(checkLogin() && !MessageEditor.isLogin(this))
		{
			if(_Utils.login(this))
				this.finish();
		}
		super.onCreate(arg0);
	}
	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
	}
	/**
	 * 是否检测 当前用户已经登录。
	 * @return
	 */
	protected boolean checkLogin(){
		return false;
	}
	
	/**
	 * 顶部 topbar 返回上级的默认实现。只是调用当前 Activity.finish() 方法。
	 * 如果有其他需求，可以重写该方法。
	 * @param v
	 */
	public void goBack(View v){
		this.finish();
	}
	
	public void toast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
	
	protected String getString(String key) {
		Bundle data = getIntent() != null ? getIntent().getExtras() : null;
		if(data!=null){
			return data.getString(key);
		}
		return null;
	}
	
	protected Serializable getSerializeable(String key) {
		Bundle data = getIntent() != null ? getIntent().getExtras() : null;
		if(data!=null){
			return data.getSerializable(key);
		}
		return null;
	}
	/**
	 * 在此方法中，统计模块运行次数。
	 */
	protected void onLaunch(){
		
	}

	protected void onResume() {
		super.onResume();
		/**
		 * 调用友盟统计接口
		 */
		MobclickAgent.onResume(this);
		if(isFirstLaunch){
			isFirstLaunch = !isFirstLaunch;
			onLaunch();
		}
	}
	protected void onPause() {
		super.onPause();
		/**
		 * 调用友盟统计接口
		 */
		MobclickAgent.onPause(this);
	}
	
	public void postClickEvent(String event){
		MobclickAgent.onEvent(this , event);
	}
	
	/**
	 * 
	 * @param event  event_id
	 * @param key 参数 key
	 * @param val 参数 value
	 */
	public void postClickEvent(String event, String key, String val) {
		HashMap<String, String> param = new HashMap<String, String>();
		param.put(key, val);
		postClickEvent(event, param);
	}
	/**
	 * 
	 * @param event  event_id
	 * @param key 参数 key
	 * @param val 参数 value
	 */
	public void postClickEvent(String event, HashMap<String, String> param) {
		MobclickAgent.onEvent(this, event, param);
	}
	
	/**
	 * 
	 * @param withParamsUrl
	 *            请求的 url
	 * @param callback
	 *            请求成功之后的回调
	 */
	public void request(String withParamsUrl,
			final RequestCallback callback, ArrayList<BasicNameValuePair> pairs) {
		if (pairs == null)
			pairs = new ArrayList<BasicNameValuePair>();
		String user_id = MessageEditor.getUserId(BaseActivity.this);
		String md5 =  MessageEditor.getHotLineMd5(BaseActivity.this);
		if(TextUtils.isEmpty(user_id))
			user_id = "null";
		if(TextUtils.isEmpty(md5))
			md5 = "null";
		// 添加请求头 ，参数
		pairs.add(new BasicNameValuePair("md5",md5));
		pairs.add(new BasicNameValuePair("user_id", user_id));
		
		
		new MyBaseProtocol(new Handler() {
			@Override
			public void handleMessage(Message msg) {
				String json = (String) msg.obj;
				Gson gson = new Gson();
				CurrentBean currentMessage = null;
				try {
					currentMessage = gson.fromJson(json, CurrentBean.class);
				} catch (Exception e) {
					LogS.e(BaseActivity.this.toString(),"数据格式异常 json = " + json);
				}
				if (currentMessage != null) {
					if (currentMessage.getRet() == 0){
						if (! isFinishing())
							callback.requestSucc(json);
					} else {
						LogS.e(BaseActivity.this.toString(),"请求异常 ret = " + currentMessage.getRet());
						if(! isFinishing())
							callback.requestFailed(RequestCallback.CODE_SERVER_ERROR);
					}
				}else{
					if(! isFinishing())
						callback.requestFailed(RequestCallback.CODE_UNKNOW);
				}
			}
		}, new WookiiHttpPost(), withParamsUrl).startInvoke(
				new JsonWookiiHttpContent(pairs), 0);
	}

	/**
	 * 
	 * 使用 BaseFragment.request(String, RequestCallback) 进行请示时，的回调接口
	 * 
	 * @see Method
	 * @author yanyadi
	 * 
	 */
	public static interface RequestCallback {
		/**
		 * 网络异常
		 */
		int CODE_NETWORK_ERROR = 1;
		/**
		 * 未知异常
		 */
		int CODE_UNKNOW = 2;
		/**
		 * 服务器端异常，比如 ret 返回值为 0 时
		 */
		int CODE_SERVER_ERROR = 3;
		void requestSucc(String json);
		/**
		 * 网络请求时，发生异常时回调该函数。 
		 *  <br />
		 *  
		 * @param errorCode  {@link BaseFragment.RequestCallback#CODE_NETWORK_ERROR} <br />{@link BaseFragment.RequestCallback#CODE_SERVER_ERROR} <br />{@link BaseFragment.RequestCallback#CODE_UNKNOW}
		 */
		void requestFailed(int errorCode);
	}
	
	public void request(Context context, String withParamsUrl,
			final RequestCallback callback, ArrayList<BasicNameValuePair> pairs) {
		if (pairs == null)
			pairs = new ArrayList<BasicNameValuePair>();
		String user_id = MessageEditor.getUserId(context);
		String md5 =  MessageEditor.getHotLineMd5(context);
		if(TextUtils.isEmpty(user_id))
			user_id = "null";
		if(TextUtils.isEmpty(md5))
			md5 = "null";
		// 添加请求头 ，参数
		pairs.add(new BasicNameValuePair("md5",md5));
		pairs.add(new BasicNameValuePair("user_id", user_id));
		
		
		new MyBaseProtocol(new Handler() {
			@Override
			public void handleMessage(Message msg) {
				String json = (String) msg.obj;
				Gson gson = new Gson();
				CurrentBean currentMessage = null;
				try {
					currentMessage = gson.fromJson(json, CurrentBean.class);
				} catch (Exception e) {
//					LogS.e(BaseActivity.this.toString(),"数据格式异常 json = " + json);
				}
				if (currentMessage != null) {
					if (currentMessage.getRet() == 0){
						if (! isFinishing())
							callback.requestSucc(json);
					} else {
//						LogS.e(BaseActivity.this.toString(),"请求异常 ret = " + currentMessage.getRet());
						if(! isFinishing())
							callback.requestFailed(RequestCallback.CODE_SERVER_ERROR);
					}
				}else{
					if(! isFinishing())
						callback.requestFailed(RequestCallback.CODE_UNKNOW);
				}
			}
		}, new WookiiHttpPost(), withParamsUrl).startInvoke(
				new JsonWookiiHttpContent(pairs), 0);
	}
}
