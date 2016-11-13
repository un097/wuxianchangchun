package cn.ffcs.wisdom.city.web.view;

import android.graphics.Bitmap;
import android.webkit.WebView;

public interface WebClientListener {
	boolean shouldOverrideUrlLoading(WebView view, String url);

	void onPageFinished(WebView view, String url);

	void onPageStarted(WebView view, String url, Bitmap favicon);

	void onReceivedError(WebView view, int errorCode, String description, String failingUrl);

	String getReturnTitle();

}
