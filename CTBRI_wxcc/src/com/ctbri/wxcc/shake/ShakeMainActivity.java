package com.ctbri.wxcc.shake;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ctbri.comm.util._Utils;
import com.ctbri.wxcc.Constants;
import com.ctbri.wxcc.MessageEditor;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseActivity;
import com.ctbri.wxcc.community.Constants_Community;
import com.ctbri.wxcc.entity.ShakeBean;
import com.google.gson.Gson;

public class ShakeMainActivity extends BaseActivity {
	private WebView webView;
	private ImageView left_btn,right_btn;
	private TextView shake_title;
	private JSKit js;
	private String shake_Url, userId, mTitle;
	private boolean isNew;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_shake_main);
		userId = MessageEditor.getUserId(this);
		if("".equals(userId) || userId == null) {
			_Utils.login(this);
			finish();
			return;
		}
		initView();
		initData();
    }  
	
	/**
	 * 初始化View
	 */
	private void initView() {
		webView = (WebView) findViewById(R.id.wv_shake);
		left_btn = (ImageView) findViewById(R.id.action_bar_left_btn);
		left_btn.setOnClickListener(new OnClickListener() {//返回事件
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		shake_title = (TextView) findViewById(R.id.action_bar_title);
		shake_title.setText("loading...");
		right_btn = (ImageView) findViewById(R.id.action_bar_right_btn); 
	}

	/**
	 * 获取接口数据
	 */
	private void initData() {
		request(Constants.METHOD_SHAKE, new RequestCallback() {
			@Override
			public void requestSucc(String json) {
				Gson gson = new Gson();
				ShakeBean shakeBean = gson.fromJson(json, ShakeBean.class);
				if (shakeBean.getData() != null && shakeBean.getMsg().equals("ok")) {
//					if(shakeBean.getData().getStatus().equals("1")) {
						shake_Url = shakeBean.getData().getUrl();
						initSetting();
//					}
				}
			}
			
			@Override
			public void requestFailed(int errorCode) {
				
			}
		}, null);
	}
	
	/**
	 * 判断是否有新活动
	 * @return
	 */
	public void isNewActivities (final Context context){
		
		request(context, Constants.METHOD_SHAKE, new RequestCallback() {
			@Override
			public void requestSucc(String json) {
				Gson gson = new Gson();
				ShakeBean shakeBean = gson.fromJson(json, ShakeBean.class);
				if (shakeBean.getData() != null) {
					isNew = shakeBean.getData().getStatus().equals("1");
					
					Intent intent = new Intent("action.intent.activity.statu");
					intent.putExtra("isNew", isNew);
					context.sendBroadcast(intent);
				}
			}
			
			@Override
			public void requestFailed(int errorCode) {
				
			}
		}, null);
	}
	
	/**
	 * WebView设置
	 */
	@SuppressLint("JavascriptInterface")
	private void initSetting() {	

		// 设置是否可以使用localStorag		
		WebSettings webSettings = webView.getSettings();
		// 设置JS支持
		webSettings.setJavaScriptEnabled(true);
		// 是否允许访问文件数据
		webSettings.setAllowFileAccess(true);
		// 设置滚动条隐藏
		webSettings.setGeolocationEnabled(true);
		// 支持JS打开新的窗口
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setDomStorageEnabled(true);
		// 设置编码格式
		webSettings.setDefaultTextEncodingName("utf-8");
		webSettings.setBuiltInZoomControls(false);
		
		js = new JSKit(this);
		//把js绑定到全局的Shake上，Shake的作用域是全局的，初始化后可随处使用
		webView.addJavascriptInterface(js, "Shake");
		
		// 触摸焦点起作用
		webView.requestFocus();
		// 设置滚动条不显示
		webView.setVerticalScrollBarEnabled(false);
		// 响应长按事件，相应鼠标的长按事件
		webView.setHapticFeedbackEnabled(false);
		//清楚缓存，让其不使用缓存
		webView.clearCache(true);
		webView.clearHistory();
		
		if(shake_Url != null && !"".equals(shake_Url)) {
			webView.loadUrl(shake_Url+"?&uid="+userId);
		}else {
			if(shake_title != null)
				shake_title.setText("当前无活动");
			Toast.makeText(this, "亲，当前无活动", Toast.LENGTH_SHORT).show();
		}
//		webView.loadUrl("http://219.141.189.22/demo.html");
		//调用 HTML 中的javaScript 函数
		webView.loadUrl("javascript:palyShakeSound()");
		
		// 设置辅助WebView处理Javascript的对话框，网站图标，网站title，加载进度等
		webView.setWebChromeClient(new WebChromeClient(){
			@Override
			public void onReceivedTitle(WebView view, final String title) {
				super.onReceivedTitle(view, title);

				if(!"".equals(title) && title != null) {
					mTitle = title;
					shake_title.setText(title);
					
//					if("摇一摇".equals(title)) {
						right_btn.setImageResource(R.drawable.titlebar_icon_share_unpressed);
						right_btn.setOnClickListener(new ShareListener());
//					}
				}
			}
		});

		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return super.shouldOverrideUrlLoading(view, url);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
			}
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				
				//设置当前webview是否需要加载图片
				if(!webView.getSettings().getLoadsImagesAutomatically()) {
			        webView.getSettings().setLoadsImagesAutomatically(true);
			    }
			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
				//服务端无法加载页面
				finish();
				JSONObject jsonObject = new JSONObject();			
				try {
					jsonObject.put("msg", "无法加载页面");	
					jsonObject.put("result", "1");
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
				handler.proceed();  // 接受所有网站的证书
			}
		});
	}

	class ShareListener implements OnClickListener{

		@Override
		public  void onClick(View v) {
			if(mTitle == null) return;
			//分享
			String content = getString(R.string.share_content_shake, mTitle);
			_Utils.shareAndCheckLogin(
					ShakeMainActivity.this, 
					mTitle, 
					Constants_Community.APK_DOWNLOAD_URL,
					content,
					_Utils.getDefaultAppIcon(ShakeMainActivity.this));
//			_Utils.share(this,title,shake_Url, getString(R.string.share_content_shake), "");
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			finish();
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	
}