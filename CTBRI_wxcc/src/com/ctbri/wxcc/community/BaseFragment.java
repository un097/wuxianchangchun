package com.ctbri.wxcc.community;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.ctbri.wxcc.CurrentBean;
import com.ctbri.wxcc.JsonWookiiHttpContent;
import com.ctbri.wxcc.MessageEditor;
import com.ctbri.wxcc.MyBaseProtocol;
import com.ctbri.wxcc.R;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;
import com.wookii.tools.comm.LogS;
import com.wookii.tools.net.WookiiHttpPost;

public abstract class BaseFragment extends Fragment {

	protected Activity activity;

	@Override
	public void onAttach(Activity activity_) {
		super.onAttach(activity);
		this.activity = activity_;
	}

	/**
	 * 
	 * @param withParamsUrl
	 *            请求的 url
	 * @param callback
	 *            请求成功之后的回调
	 */
	protected void request(String withParamsUrl, final RequestCallback callback) {
		request(withParamsUrl, callback, null);
	}
	/**
	 * 返回该模块的名称
	 * @return
	 */
	protected abstract String getAnalyticsTitle();
	
	/**
	 * 在不需要统计用户行为的界面，可以重写该方法，不记录用户操作
	 * @return
	 */
	protected boolean isEnabledAnalytics(){
		return true;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if(isEnabledAnalytics())
		MobclickAgent.onPageEnd(getAnalyticsTitle());
	}
	@Override
	public void onResume() {
		super.onResume();
		if(isEnabledAnalytics())
		MobclickAgent.onPageStart(getAnalyticsTitle());
	}

	/**
	 * 
	 * @param withParamsUrl
	 *            请求的 url
	 * @param callback
	 *            请求成功之后的回调
	 */
	protected void request(String withParamsUrl,
			final RequestCallback callback, ArrayList<BasicNameValuePair> pairs) {
		if (pairs == null)
			pairs = new ArrayList<BasicNameValuePair>();
		
		String user_id = MessageEditor.getUserId(activity);
		String md5 =  MessageEditor.getHotLineMd5(activity);
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
					LogS.e(BaseFragment.this.toString(),"数据格式异常 json = " + json);
				}
				if (currentMessage != null) {
					if (currentMessage.getRet() == 0){
						if (!activity.isFinishing())
							callback.requestSucc(json);
					} else {
						LogS.e(BaseFragment.this.toString(),"请求异常 ret = " + currentMessage.getRet());
						
						if(!activity.isFinishing()) {
							callback.requestFailed(RequestCallback.CODE_SERVER_ERROR);
							Toast.makeText(activity, currentMessage.getMsg(), Toast.LENGTH_SHORT).show();
						}
					}
				}else{
					if(!activity.isFinishing())
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
	public static interface RequestCallback extends com.ctbri.wxcc.community.BaseActivity.RequestCallback{
		
	}

	public void toast(String msg) {
		Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
	}
	public void toast(int msg) {
		Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
	}

	public class FinishClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			activity.finish();
		}
	}
	public class ShareClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {

		}
		
	}
	
	protected String getArgs(String key) {
		return getArgs(key,null);
	}
	protected String getArgs(String key, String def) {
		Bundle args = getArguments();
		return args==null ? def : args.getString(key,def);
		
	}
	protected int getArgsInt(String key) {
		return getArgsInt(key,0);
	}
	protected int getArgsInt(String key, int def) {
		Bundle args = getArguments();
		return args==null ? def : args.getInt(key,def);
		
	}
	protected boolean getArgsBoolean(String key, boolean def) {
		Bundle args = getArguments();
		return args==null ? def : args.getBoolean(key,def);
		
	}
	
	protected Serializable getSerialzeable(String key) {
		Bundle args = getArguments();
		return args==null ? null : args.getSerializable(key);
	}
	
	protected void initActionBackListener(View v)
	{
		View btn = v.findViewById(R.id.action_bar_left_btn);
		if(btn!=null)
			btn.setOnClickListener(new FinishClickListener());
	}
	
	public void postClickEvent(String event){
		MobclickAgent.onEvent(activity , event);
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
		MobclickAgent.onEvent(activity, event, param);
	}
}
