package com.ctbri.wxcc.shake;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctbri.comm.util._Utils;
import com.ctbri.wxcc.MessageEditor;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseActivity;
import com.ctbri.wxcc.community.Constants_Community;

public class SingMainActivity extends BaseActivity{
	
	private WebView webView;
	private ImageView left_btn,right_btn;
	private TextView shake_title;
	private String url = ""; //TODO http://219.141.189.20:8080/wxcc/index.html
	private String userId = ""; //TODO
	private String mTitle;
	Handler handler = new Handler();
	private ValueCallback<Uri> mUploadMessage;
	private final static int FILECHOOSER_RESULTCODE = 1;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_shake_main);
		AppManager.getAppManager().addActivity(this);
		
		userId = MessageEditor.getUserId(this);
		url = getIntent().getStringExtra("url");
		
		initView();
//		initSetting();
	}
	
	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled", "NewApi" })
	private void initView() {
		webView = (WebView) findViewById(R.id.wv_shake);
//		wv = new WebView(this);
		left_btn = (ImageView) findViewById(R.id.action_bar_left_btn);
		left_btn.setOnClickListener(new OnClickListener() {//返回事件
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		shake_title = (TextView) findViewById(R.id.action_bar_title);
		shake_title.setText("...");
		right_btn = (ImageView) findViewById(R.id.action_bar_right_btn);
		
		WebSettings st = webView.getSettings();
		st.setJavaScriptEnabled(true);
		st.setAllowFileAccess(true);
		st.setGeolocationEnabled(true);
		st.setJavaScriptCanOpenWindowsAutomatically(true);
		st.setDomStorageEnabled(true);
		st.setDefaultTextEncodingName("utf-8");
		st.setBuiltInZoomControls(false);
		
		webView.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onReceivedTitle(WebView view, final String title) {
				super.onReceivedTitle(view, title);

				if(!"".equals(title) && title != null) {
					mTitle = title;
					shake_title.setText(title);
					
					/*right_btn.setImageResource(R.drawable.titlebar_icon_share_unpressed);
					right_btn.setOnClickListener(new ShareListener());*/
				}
			}
		
			// For Android 3.0+
			public void openFileChooser(ValueCallback<Uri> uploadMsg) {

				mUploadMessage = uploadMsg;
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.addCategory(Intent.CATEGORY_OPENABLE);
				i.setType("image/*");
				SingMainActivity.this.startActivityForResult(
						Intent.createChooser(i, "File Chooser"),
						FILECHOOSER_RESULTCODE);

			}

			// For Android 3.0+
			public void openFileChooser(ValueCallback uploadMsg,
					String acceptType) {
				mUploadMessage = uploadMsg;
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.addCategory(Intent.CATEGORY_OPENABLE);
				i.setType("*/*");
				SingMainActivity.this.startActivityForResult(
						Intent.createChooser(i, "File Browser"),
						FILECHOOSER_RESULTCODE);
			}

			// For Android 4.1
			public void openFileChooser(ValueCallback<Uri> uploadMsg,
					String acceptType, String capture) {
				mUploadMessage = uploadMsg;
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.addCategory(Intent.CATEGORY_OPENABLE);
				i.setType("image/*");
				SingMainActivity.this.startActivityForResult(
						Intent.createChooser(i, "File Chooser"),
						SingMainActivity.FILECHOOSER_RESULTCODE);

			}
		});

		webView.setWebViewClient(new MyWebViewClient(this));

		st.setJavaScriptEnabled(true);
		webView.loadUrl(url+"?&uid="+userId);
//		wv.loadUrl("http://ccgd-youhuiquan.153.cn:30088/registration/index_bx.html");
		// wv.loadUrl("file:///android_asset/jsalert.html");

		webView.addJavascriptInterface(new Object() {

			@SuppressWarnings("unused")
			public void onclick(String str) {

				webView.loadUrl("javascript:text('" + str + "')");
			}

		}, "android");

//		this.addContentView(wv, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}

	/*private void initSetting() {
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
		
		JSKit js = new JSKit(this);
		// 把js绑定到全局的Shake上，Shake的作用域是全局的，初始化后可随处使用
		webView.addJavascriptInterface(js, "Shake");
		
		// 触摸焦点起作用
		webView.requestFocus();
		// 设置滚动条不显示
		webView.setVerticalScrollBarEnabled(false);
		// 响应长按事件，相应鼠标的长按事件
		webView.setHapticFeedbackEnabled(false);
		// 清楚缓存，让其不使用缓存
		webView.clearCache(true);
		webView.clearHistory();
		
		// 加载页面
		webView.loadUrl(url+"?&uid="+userId);
		
		//调用 HTML 中的javaScript 函数
		webView.loadUrl("javascript:login()");
//		webView.loadUrl("javascript:toPhoto()");
		// 设置辅助WebView处理Javascript的对话框，网站图标，网站title，加载进度等
		webView.setWebChromeClient(new WebChromeClient(){
			@Override
			public void onReceivedTitle(WebView view, final String title) {
				super.onReceivedTitle(view, title);

				if(!"".equals(title) && title != null) {
					mTitle = title;
					shake_title.setText(title);
					
					right_btn.setImageResource(R.drawable.titlebar_icon_share_unpressed);
					right_btn.setOnClickListener(new ShareListener());
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
	}*/
	
	class ShareListener implements OnClickListener{

		@Override
		public  void onClick(View v) {
			if(mTitle == null) return;
			//分享
			String content = getString(R.string.share_content_shake, mTitle);
			_Utils.shareAndCheckLogin(
					SingMainActivity.this, 
					mTitle, 
					Constants_Community.APK_DOWNLOAD_URL,
					content,
					_Utils.getDefaultAppIcon(SingMainActivity.this));
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == FILECHOOSER_RESULTCODE) {
			if (null == mUploadMessage)
				return;
			Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
			if (result != null) {
				Uri uri = Uri.fromFile(new File(GetPathFromUri4kitkat.getPath(this, result)));
				mUploadMessage.onReceiveValue(result);
			} else {
				mUploadMessage.onReceiveValue(null);
			}

			mUploadMessage = null;
		}
	}
}
