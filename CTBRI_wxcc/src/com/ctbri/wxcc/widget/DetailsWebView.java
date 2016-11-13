package com.ctbri.wxcc.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
/**
 * 展示  web 内容的用的 webview ，将扩展 javascript 接口。
 * 可能要实现  web 中图片中的预览
 * @author yanyadi
 *
 */
public class DetailsWebView extends WebView {

	public DetailsWebView(Context context) {
		this(context,null);
	}
	public DetailsWebView(Context context, AttributeSet attrs) {
		super(context,attrs);
		init();
	}
	public DetailsWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	@SuppressLint("SetJavaScriptEnabled") 
	private void init(){
		//如果在开发编辑模式，则直接返回
		if(isInEditMode())return;
		
		setWebChromeClient(new WebChromeClientImpl());
		setWebViewClient(new WebViewClientImpl());
		
		WebSettings  settings = getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setSupportZoom(false);
		settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
	}
	
	class WebChromeClientImpl extends WebChromeClient{
	}
	
	class  WebViewClientImpl extends WebViewClient{
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			return super.shouldOverrideUrlLoading(view, url);
		}
	}
	

}
