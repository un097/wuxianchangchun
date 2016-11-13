package cn.ffcs.wisdom.city.web.view;

import android.view.View;
import android.webkit.WebView;
import android.webkit.WebChromeClient.CustomViewCallback;

public interface WebChromeListener {
	void onProgressChanged(WebView view, int newProgress);

	void onShowCustomView(View view, CustomViewCallback callback);

	void onHideCustomView();
}
