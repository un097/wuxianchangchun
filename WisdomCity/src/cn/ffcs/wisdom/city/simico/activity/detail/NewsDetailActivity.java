package cn.ffcs.wisdom.city.simico.activity.detail;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.anim;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebView;
import android.webkit.WebView.HitTestResult;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import cn.ffcs.config.BaseConfig;
import cn.ffcs.external.share.entity.CustomSocialShareEntity;
import cn.ffcs.external.share.view.CustomSocialShare;
import cn.ffcs.external.share.view.WeiBoSocialShare;
import cn.ffcs.wisdom.city.R;
import cn.ffcs.wisdom.city.simico.activity.detail.view.TitleBar;
import cn.ffcs.wisdom.city.simico.activity.detail.view.TitleBar.MenuDelegate;
import cn.ffcs.wisdom.city.simico.activity.home.cache.IndexCacheUtils;
import cn.ffcs.wisdom.city.simico.api.model.ApiResponse;
import cn.ffcs.wisdom.city.simico.api.model.News;
import cn.ffcs.wisdom.city.simico.api.request.BaseRequestListener;
import cn.ffcs.wisdom.city.simico.api.request.GetNewsDetailRequest;
import cn.ffcs.wisdom.city.simico.api.request.SubscribeOrCancelNewsRequest;
import cn.ffcs.wisdom.city.simico.base.Application;
import cn.ffcs.wisdom.city.simico.base.Constants;
import cn.ffcs.wisdom.city.simico.image.CommonImageLoader;
import cn.ffcs.wisdom.city.simico.kit.activity.PSActivity;
import cn.ffcs.wisdom.city.simico.kit.log.TLog;
import cn.ffcs.wisdom.city.simico.kit.util.DateUtil;
import cn.ffcs.wisdom.city.simico.kit.util.TDevice;
import cn.ffcs.wisdom.city.simico.ui.grid.EmptyView;
import cn.ffcs.wisdom.city.web.view.IcityWebView;
import cn.ffcs.wisdom.city.web.view.WebChromeListener;
import cn.ffcs.wisdom.city.web.view.WebClientListener;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.Log;





//import com.actionbarsherlock.app.ActionBar;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.baidu.android.bba.common.util.Util;
import com.ctbri.wxcc.MessageEditor;
import com.google.gson.JsonObject;
import com.umeng.analytics.MobclickAgent;

public class NewsDetailActivity extends PSActivity implements OnTouchListener {
	protected static final String TAG = NewsDetailActivity.class
			.getSimpleName();
	private static final String NEW_DETAIL_TAG = "NEW_DETAIL_TAG";
	private static final String PREFIX_COLLECT_NEWS = "PREFIX_COLLECT_NEWS_";
	private IcityWebView mWebView;
	private EmptyView mEmptyView;
	protected boolean isLoadError;
	private News mNews;
	private TitleBar titleBar;
	private PopupWindow popWindow;
	private String topUrl;
	private View mLlNewsDetailBar;
	private View mRlComment;
	private TextView mTvCommentCount;
	private ImageView mIvComment;
	private ImageView mIvCollection;
	private ImageView mIvShare;
	private HashSet<Integer> collectIds;
	private HashSet<Integer> collectIds_logout;
	private CommonImageLoader mImageLoader;
	private int LOGIN = 0x003;
	private FrameLayout webviewframe;
	private EditText edt_title;
	private News correntNews = null;
	private int type = 1;
	private String titleString = "";

	@Override
	public int getLayoutId() {
		return R.layout.simico_activity_news_detail;
	}

	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	@Override
	protected void init(Bundle savedInstanceState) {
		super.init(savedInstanceState);
		setConfigCallback((WindowManager) getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE));

		mImageLoader = new CommonImageLoader(getApplicationContext());
		collectIds = Application.getCollectNewsIds();
		collectIds_logout = Application.getLogoutCollectNewsIds();
		webviewframe = (FrameLayout) findViewById(R.id.webviewframe);
		mEmptyView = (EmptyView) findViewById(R.id.empty_view);
		mEmptyView.setWebView(true);
		mEmptyView.setOnClickListener(this);
		mEmptyView.setState(EmptyView.STATE_NONE);

		mLlNewsDetailBar = findViewById(R.id.news_detail_bar);
		mRlComment = findViewById(R.id.rl_comment);
		mTvCommentCount = (TextView) findViewById(R.id.tv_comment_count);
		mIvComment = (ImageView) findViewById(R.id.iv_comment);
		mIvComment.setOnClickListener(this);
		mIvCollection = (ImageView) findViewById(R.id.iv_collection);
		mIvCollection.setOnClickListener(this);
		mIvShare = (ImageView) findViewById(R.id.iv_share);
		mIvShare.setOnClickListener(this);
		mWebView = new IcityWebView(this);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		webviewframe.addView(mWebView, 0, params);
		// mWebView = (IcityWebView) findViewById(R.id.webview);
		mWebView.init(NewsDetailActivity.this);
		mWebView.setVerticalScrollBarEnabled(true);
		mWebView.setScrollBarStyle(0);
		// mWebView.getSettings().setSupportZoom(false);
		// mWebView.getSettings().setBuiltInZoomControls(true);
		mWebView.getSettings().setJavaScriptEnabled(true);
		// mWebView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		// mWebView.requestFocus();
		// mWebView.requestFocusFromTouch();
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.setOnTouchListener(this);
		mWebView.setWebChromeListener(new WebChromeListener() {

			@Override
			public void onShowCustomView(View view, CustomViewCallback callback) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (type == 2) {
					view.requestFocus();
				}

			}

			@Override
			public void onHideCustomView() {
				// TODO Auto-generated method stub

			}
		});
		mWebView.setWebClientListener(new WebClientListener() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				HitTestResult hit = mWebView.getHitTestResult();
				if (hit == null) {
					return false;
				}
				int hitType = hit.getType();
				if (hitType == HitTestResult.UNKNOWN_TYPE) {
					android.util.Log.i("sb", "UNKNOWN_TYPE");
					if (!url.startsWith("icity-v7-wap:")
							&& !url.startsWith("icity-v7-news:")
							&& !url.startsWith("icity-v7-none:")) {
						return false;
					}
				}
				if (url.startsWith("method:")) {
					String u = url.substring("method:".length());
					if (!TextUtils.isEmpty(u)) {
						try {
							Intent intent = new Intent();
							intent.setAction(Intent.ACTION_VIEW);
							Uri content_url = Uri.parse(u);
							intent.setData(content_url);
							startActivity(intent);
						} catch (ActivityNotFoundException e) {
							Application.showToastShort("没有找到可用的浏览器");
						}
					}
					return true;
				} else if (url.startsWith("icity-v7-wap:")) {
					Intent intent = new Intent(NewsDetailActivity.this,
							NewsDetailActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.putExtra("url",
							url.substring("icity-v7-wap:".length()));
					intent.putExtra("showMore", true);
					intent.putExtra("type", 1);
					intent.setExtrasClassLoader(News.class.getClassLoader());
					startActivity(intent);
					return true;
				} else if (url.startsWith("icity-v7-news:")) {
					String newsUrl = url.substring("icity-v7-news:".length());
					if (!newsUrl.toLowerCase().startsWith("http://")
							&& !newsUrl.toLowerCase().startsWith("https://")
							&& !newsUrl.toLowerCase().startsWith("www.")) {
						newsUrl = BaseConfig.GET_SERVER_ROOT_URL()
								+ newsUrl.substring(1);
					}
					Intent intent = new Intent(NewsDetailActivity.this,
							NewsDetailActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.putExtra("url", newsUrl);
					intent.putExtra("showMore", true);
					intent.putExtra("type", 2);
					intent.setExtrasClassLoader(News.class.getClassLoader());
					startActivity(intent);
					return true;
				} else if (url.startsWith("icity-v7-none:")) {
					String newsUrl = url.substring("icity-v7-none:".length());
					if (!newsUrl.toLowerCase().startsWith("http://")
							&& !newsUrl.toLowerCase().startsWith("https://")
							&& !newsUrl.toLowerCase().startsWith("www.")) {
						newsUrl = BaseConfig.GET_SERVER_ROOT_URL()
								+ newsUrl.substring(1);
					}
					Intent intent = new Intent(NewsDetailActivity.this,
							NewsDetailActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.putExtra("url", newsUrl);
					intent.putExtra("showMore", true);
					intent.putExtra("type", 0);
					intent.setExtrasClassLoader(News.class.getClassLoader());
					startActivity(intent);
					return true;
				} else {
					if (url.toLowerCase().indexOf("http://") == 0
							|| url.toLowerCase().indexOf("https://") == 0
							|| url.toLowerCase().indexOf("www.") == 0) {
						Intent intent = new Intent(NewsDetailActivity.this,
								NewsDetailActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra("url", url);
						intent.putExtra("showMore", true);
						intent.putExtra("type", 1);
						intent.setExtrasClassLoader(News.class.getClassLoader());
						startActivity(intent);
						return true;
					} else if (url.startsWith("/")) {
						if (!TextUtils.isEmpty(topUrl)) {
							int index = topUrl.toLowerCase().indexOf("www.");
							String subString = topUrl.substring(index);
							index = subString.indexOf("/");
							String host = subString.substring(0, index);
							String u = host + url.substring(1);
							Intent intent = new Intent(NewsDetailActivity.this,
									NewsDetailActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.putExtra("url", u);
							intent.putExtra("showMore", true);
							intent.putExtra("type", 1);
							intent.setExtrasClassLoader(News.class
									.getClassLoader());
							startActivity(intent);
						}
						return true;
					} else {
						try {
							Uri uri = Uri.parse(url);
							Intent intent = new Intent(Intent.ACTION_VIEW, uri);
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);
						} catch (ActivityNotFoundException e) {
							CommonUtils.showToast(NewsDetailActivity.this,
									R.string.web_no_find, Toast.LENGTH_SHORT);
						}
						return true;
					}
				}
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				if (failingUrl.startsWith("dpshare://")) {
					return;
				}
				isLoadError = true;
				if (mEmptyView.getState() != EmptyView.STATE_ERROR) {
					mEmptyView.setState(EmptyView.STATE_ERROR);
					// mEmptyView.setTip(description);
				}
				if (mWebView.getVisibility() != View.INVISIBLE) {
					mWebView.setVisibility(View.INVISIBLE);
				}
				correntNews = null;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				isLoadError = false;
				if (mEmptyView.getState() != EmptyView.STATE_LOADING) {
					mEmptyView.setState(EmptyView.STATE_LOADING);
				}
				if (mWebView.getVisibility() != View.INVISIBLE) {
					mWebView.setVisibility(View.INVISIBLE);
				}
				correntNews = null;
				// if
				// (url.contains("icity-api-client-web/v7/infoConfCall/toInfoConfDetailWap"))
				// {
				// // mLlNewsDetailBar.setVisibility(View.VISIBLE);
				// mRlComment.setVisibility(View.VISIBLE);
				// mIvCollection.setVisibility(View.VISIBLE);
				// int index = url.toLowerCase().indexOf("id=");
				// String subString = url.substring(index + 3);
				// subString = subString.replaceAll("\\D", "_").replaceAll("_+",
				// "_");
				// int newsId = Integer.parseInt(subString.split("_+")[0]);
				// changeCollect(collectIds.contains(newsId)
				// || collectIds_logout.contains(newsId));
				// getDetail(newsId);
				// } else {
				// // mLlNewsDetailBar.setVisibility(View.INVISIBLE);
				// mRlComment.setVisibility(View.GONE);
				// mIvCollection.setVisibility(View.GONE);
				// }
				if (type == 2) {
					// mLlNewsDetailBar.setVisibility(View.VISIBLE);
					// mRlComment.setVisibility(View.VISIBLE);
					// mIvCollection.setVisibility(View.VISIBLE);
					int index = url.toLowerCase().indexOf("id=");
					String subString = url.substring(index + 3);
					subString = subString.replaceAll("\\D", "_").replaceAll(
							"_+", "_");
					try {
						int newsId = Integer.parseInt(subString.split("_+")[0]);
						changeCollect(collectIds.contains(newsId)
								|| collectIds_logout.contains(newsId));
						getDetail(newsId);
					} catch (Exception e) {
						android.util.Log.e("fmj", "topUrl===========363======" + topUrl);
						mWebView.loadUrl(topUrl);
						return;
					}
					return;
				} else if (type == 1) {
					// mLlNewsDetailBar.setVisibility(View.VISIBLE);
					// mRlComment.setVisibility(View.GONE);
					// mIvCollection.setVisibility(View.GONE);
				} else if (type == 0) {
					// mLlNewsDetailBar.setVisibility(View.INVISIBLE);
				}
				topUrl = url;
				// titleBar.setTitle(topUrl);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				if (!isLoadError) {
					addLinkClickListner();
					if (mEmptyView.getState() != EmptyView.STATE_NONE) {
						mEmptyView.setState(EmptyView.STATE_NONE);
					}
					if (mWebView.getVisibility() != View.VISIBLE) {
						mWebView.setVisibility(View.VISIBLE);
					}
				}
				if (type == 2) {
					mLlNewsDetailBar.setVisibility(View.VISIBLE);
					mRlComment.setVisibility(View.VISIBLE);
					mIvCollection.setVisibility(View.VISIBLE);
				} else if (type == 1) {
					mLlNewsDetailBar.setVisibility(View.VISIBLE);
					mRlComment.setVisibility(View.GONE);
					mIvCollection.setVisibility(View.GONE);
				} else if (type == 0) {
					mLlNewsDetailBar.setVisibility(View.GONE);
				}
				// CommonUtils.showKeyboard(NewsDetailActivity.this, mWebView);
				// if
				// (!url.contains("icity-api-client-web/v7/infoConfCall/toInfoConfDetailWap?id="))
				// {
				// mRlComment.setVisibility(View.GONE);
				// mIvCollection.setVisibility(View.GONE);
				// } else {
				// mRlComment.setVisibility(View.VISIBLE);
				// mIvCollection.setVisibility(View.VISIBLE);
				// }
			}

			@Override
			public String getReturnTitle() {
				return "";
			}
		});
		mWebView.addJavascriptInterface(new JavascriptInterface(this),
				"linklistner");
		// mWebView.addJavascriptInterface(this, "callFuncFromClient");
		type = getIntent().getIntExtra("type", 1);
		mNews = getIntent().getParcelableExtra("news");
		titleString = getIntent().getStringExtra("titleString");
		initActionBar();
		if (mNews != null) {
			changeCollect(collectIds.contains(mNews.getId())
					|| collectIds_logout.contains(mNews.getId()));
			loadData();
			return;
		}
		topUrl = getIntent().getStringExtra("url");
		if (!TextUtils.isEmpty(topUrl) && !topUrl.equals("null")) {
			if(topUrl.contains("?")){
				mWebView.loadUrl(topUrl + "&uid=" + MessageEditor.getUserId(this));	
			}else{
				mWebView.loadUrl(topUrl + "?&uid=" + MessageEditor.getUserId(this));
			}
			// titleBar.setTitleVisiable(View.VISIBLE);
			// titleBar.setTitle(topUrl);
		} else {
			mEmptyView.setState(EmptyView.STATE_NO_DATA);
			mWebView.setVisibility(View.INVISIBLE);
		}
	}

	private void addLinkClickListner() {
		mWebView.loadUrl("javascript:(function(){"
				+ "var objs = document.getElementsByTagName(\"a\");"
				+ "for (var i = 0; i < objs.length; i++) {"
				+ "    objs[i].ontouch=function() {"
				+ "        window.linklistner.openLink(this.href);" + "    }"
				+ "}" + "})()");// onclick\ontap\tap
	}

	public class JavascriptInterface {
		private Context context;

		public JavascriptInterface(Context context) {
			this.context = context;
		}

		public void openLink(String img) {
			// if (img.startsWith("method:")) {
			// String u = img.substring("method:".length());
			// if (!TextUtils.isEmpty(u)) {
			// try {
			// Intent intent = new Intent();
			// intent.setAction(Intent.ACTION_VIEW);
			// Uri content_url = Uri.parse(u);
			// intent.setData(content_url);
			// startActivity(intent);
			// } catch (ActivityNotFoundException e) {
			// Application.showToastShort("没有找到可用的浏览器");
			// }
			// }
			// return;
			// } else if (img.startsWith("icity-v7-wap:")) {
			// Intent intent = new Intent(NewsDetailActivity.this,
			// NewsDetailActivity.class);
			// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// intent.putExtra("url", img.substring("icity-v7-wap:".length()));
			// intent.putExtra("showMore", true);
			// intent.putExtra("type", 1);
			// intent.setExtrasClassLoader(News.class.getClassLoader());
			// startActivity(intent);
			// } else if (img.startsWith("icity-v7-news:")) {
			// String newsUrl = img.substring("icity-v7-news:".length());
			// if (!newsUrl.toLowerCase().startsWith("http://") &&
			// !newsUrl.toLowerCase().startsWith("https://") &&
			// !newsUrl.toLowerCase().startsWith("www.")) {
			// newsUrl = BaseConfig.GET_SERVER_ROOT_URL() +
			// newsUrl.substring(1);
			// }
			// Intent intent = new Intent(NewsDetailActivity.this,
			// NewsDetailActivity.class);
			// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// intent.putExtra("url", newsUrl);
			// intent.putExtra("showMore", true);
			// intent.putExtra("type", 2);
			// intent.setExtrasClassLoader(News.class.getClassLoader());
			// startActivity(intent);
			// return;
			// } else if (img.startsWith("icity-v7-none:")) {
			// String newsUrl = img.substring("icity-v7-none:".length());
			// if (!newsUrl.toLowerCase().startsWith("http://") &&
			// !newsUrl.toLowerCase().startsWith("https://") &&
			// !newsUrl.toLowerCase().startsWith("www.")) {
			// newsUrl = BaseConfig.GET_SERVER_ROOT_URL() +
			// newsUrl.substring(1);
			// }
			// Intent intent = new Intent(NewsDetailActivity.this,
			// NewsDetailActivity.class);
			// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// intent.putExtra("url", newsUrl);
			// intent.putExtra("showMore", true);
			// intent.putExtra("type", 0);
			// intent.setExtrasClassLoader(News.class.getClassLoader());
			// startActivity(intent);
			// return;
			// } else {
			// if (img.toLowerCase().indexOf("http://") == 0
			// || img.toLowerCase().indexOf("https://") == 0 ||
			// img.toLowerCase().indexOf("www.") == 0) {
			// Intent intent = new Intent(NewsDetailActivity.this,
			// NewsDetailActivity.class);
			// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// intent.putExtra("url", img);
			// intent.putExtra("showMore", true);
			// intent.putExtra("type", 1);
			// intent.setExtrasClassLoader(News.class.getClassLoader());
			// startActivity(intent);
			// return;
			// } else if (img.startsWith("/")) {
			// if (!TextUtils.isEmpty(topUrl)) {
			// int index = topUrl.toLowerCase().indexOf("www.");
			// String subString = topUrl.substring(index);
			// index = subString.indexOf("/");
			// String host = subString.substring(0, index);
			// String u = host + img.substring(1);
			// Intent intent = new Intent(NewsDetailActivity.this,
			// NewsDetailActivity.class);
			// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// intent.putExtra("url", u);
			// intent.putExtra("showMore", true);
			// intent.putExtra("type", 1);
			// intent.setExtrasClassLoader(News.class.getClassLoader());
			// startActivity(intent);
			// }
			// return;
			// } else {
			// try {
			// Uri uri = Uri.parse(img);
			// Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// startActivity(intent);
			// } catch (ActivityNotFoundException e) {
			// CommonUtils.showToast(NewsDetailActivity.this,
			// R.string.web_no_find, Toast.LENGTH_SHORT);
			// }
			// return;
			// }
			// }
		}
	}

	// public void mCallFuncFromClient() {
	// mWebView.loadUrl("javascript:callFuncFromClient(2)");
	// }

	private void changeCollect(boolean collect) {
		mIvCollection
				.setImageResource(collect ? R.drawable.ic_collection_select
						: R.drawable.simico_ic_collection);
	}

	private void loadData() {
		if (mNews.getChnlId() == 268) {
			titleBar.setTitle("政务发布");
		}
		mEmptyView.setState(EmptyView.STATE_LOADING);
		String type = mNews.getType();
		if ("news".equals(type) || "adv".equals(type)) {
			this.type = 2;
			// sendGetDetailRequest();
			// } else if ("wap".equals(type)) {
			// if (TextUtils.isEmpty(mNews.getSourceUrl())
			// || "null".equals(mNews.getSourceUrl())) {
			// mEmptyView.setState(EmptyView.STATE_NO_DATA);
			// mWebView.setVisibility(View.INVISIBLE);
			// } else {
			// topUrl = BaseConfig.GET_SERVER_ROOT_URL()
			// + "icity-api-client-web/v7/infoConfCall/toInfoConfDetailWap?id="
			// + mNews.getId();
			// // titleBar.setTitle(topUrl);
			//
			// // titleBar.setTitle(mNews.getSourceUrl());
			// // titleBar.setTitleVisiable(View.VISIBLE);
			// mWebView.loadUrl(topUrl);
			// // mLlNewsDetailBar.setVisibility(View.VISIBLE);
			// // mRlComment.setVisibility(View.VISIBLE);
			// // mIvCollection.setVisibility(View.VISIBLE);
			// }
			topUrl = BaseConfig.GET_SERVER_ROOT_URL()
					+ "icity-api-client-web/v7/infoConfCall/toInfoConfDetailWap?id="
					+ mNews.getId();
			// android.util.Log.e("sb", topUrl);
			// titleBar.setTitle(topUrl);

			// titleBar.setTitle(mNews.getSourceUrl());
			// titleBar.setTitleVisiable(View.VISIBLE);
			mWebView.loadUrl(topUrl);
			// mLlNewsDetailBar.setVisibility(View.VISIBLE);
			// mRlComment.setVisibility(View.VISIBLE);
			// mIvCollection.setVisibility(View.VISIBLE);
		} else if ("wap".equals(type)) {
			this.type = 1;
			if (TextUtils.isEmpty(mNews.getWapUrl())
					|| "null".equals(mNews.getWapUrl())) {
				mEmptyView.setState(EmptyView.STATE_NO_DATA);
				mWebView.setVisibility(View.INVISIBLE);
			} else {
				// titleBar.setTitle(mNews.getWapUrl());
				// titleBar.setTitleVisiable(View.VISIBLE);
				topUrl = mNews.getWapUrl();
				mWebView.loadUrl(mNews.getWapUrl());
				// mLlNewsDetailBar.setVisibility(View.VISIBLE);
				// mRlComment.setVisibility(View.GONE);
				// mIvCollection.setVisibility(View.GONE);
			}
		} else {
			Application.showToastShort("当前客户端版本无法显示该资讯，请先升级客户端。");
		}
	}

	public void setConfigCallback(WindowManager windowManager) {
		Field field;
		try {
			field = WebView.class.getDeclaredField("mWebViewCore");
			field = field.getType().getDeclaredField("mBrowserFrame");
			field = field.getType().getDeclaredField("sConfigCallback");
			field.setAccessible(true);
			Object configCallback = field.get(null);
			if (null == configCallback) {
				return;
			}
			field = field.getType().getDeclaredField("mWindowManager");
			field.setAccessible(true);
			field.set(configCallback, windowManager);
		} catch (Exception e) {
		}
	}

	private void initActionBar() {
		// ActionBar actionBar = getSupportActionBar();
		// actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		// titleBar = new TitleBar(this);
		titleBar = (TitleBar) findViewById(R.id.newsdetail_titlebar);
		titleBar.setDelegate(new MenuDelegate() {
			@Override
			public void onBackClick(View v) {
				if (mEmptyView.getState() == EmptyView.STATE_LOADING) {
					mWebView.stopLoading();
				}
				finish();
			}

			@Override
			public void onUrlClick(View v) {
				// showPop();
			}
		});
		View share = titleBar.findViewById(R.id.btn_share);
		share.setVisibility(View.GONE);
		share.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				handleShareNews();
			}
		});
		titleBar.setBackgroundColor(getResources().getColor(
				R.color.action_bar_bg_color));
		// actionBar.setCustomView(titleBar);
		Button back = (Button) findViewById(R.id.btn_back);
		back.setTextColor(getResources().getColor(R.color.text_color_title));
		back.setBackgroundDrawable(null);
		Drawable drawable = getResources().getDrawable(
				R.drawable.action_bar_back);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(),
				drawable.getMinimumHeight());
		back.setCompoundDrawables(drawable, null, null, null);
		back.setText("");
		TextView title = (TextView) findViewById(R.id.tv_title);
		title.setTextColor(getResources().getColor(R.color.text_color_title));
		if (titleString == null) {
			titleBar.setTitle("新闻资讯");
		} else {
			titleBar.setTitle(titleString); // TODO
		}

		// titleBar.setTitleVisiable(View.GONE);
	}

	private void showPop() {
		if (popWindow == null) {
			View view = View.inflate(this, R.layout.simico_titlebar_detail_edt,
					null);
			edt_title = (EditText) view.findViewById(R.id.edt_title);
			edt_title.setSelection(edt_title.getText().toString().length());
			popWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
					titleBar.getHeight());
			ColorDrawable dw = new ColorDrawable(-00000);
			popWindow.setBackgroundDrawable(dw);
			popWindow.setFocusable(true);
			popWindow.setOutsideTouchable(true);
		}
		edt_title.setText(((TextView) titleBar.findViewById(R.id.tv_title))
				.getText());
		Rect rect = new Rect();
		mWebView.getWindowVisibleDisplayFrame(rect);
		int statusBarHeight = rect.top;
		popWindow.showAtLocation(this.getWindow().getDecorView(), Gravity.TOP,
				0, statusBarHeight);
		InputMethodManager m = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}

	Handler handler = new Handler();
	CustomSocialShareEntity entity = new CustomSocialShareEntity();
	String url = "";
	String title = "";
	Bitmap bitmap = null;
	String imageUrl = "";
	String shareContent = "@无线长春 新闻资讯不间断更新，助您随时随地了解天下事 ";

	/**
	 * 分享资讯
	 */
	protected void handleShareNews() {
		if (mEmptyView.getState() != EmptyView.STATE_NONE) {
			return;
		}

		String android_pic = "";
		News news = null;
		if (mNews != null) {
			news = mNews;
		} else {
			news = correntNews;
		}

		if (news == null) {
			url = topUrl;
			title = mWebView.getTitle();
		} else {
			if (news.getChnlId() == 268) {
				shareContent = "及时、权威的长春政务发布尽在@无线长春 ";
			}
			if ("wap".equals(news.getType())) {
				// android.util.Log.e("sb", "wapwapwap");
				url = news.getWapUrl();
			} else if ("news".equals(news.getType())
					|| "adv".equals(news.getType())) {
				url = Constants.SHARE_ROOT_URL + news.getId()
						+ "&clientType=changchuntv_ver";
			}
			if (news.getTemplate() != null) {
				title = news.getTemplate().getTitle();
			} else {
				title = news.getSubtitle();
				android_pic = BaseConfig.GET_SERVER_ROOT_URL()
						+ news.getSourceUrl(); // 专题的图片
			}

			if (news.getTemplate() != null) {
				if (news.getTemplate().getDisplayType() != 3) {
					if (news.getTemplate().getImgs() != null) {
						ArrayList<String> imgs = news.getTemplate().getImgs();
						if (imgs != null && !imgs.isEmpty()) {
							if (imgs.get(0) != null
									&& !TextUtils.isEmpty(imgs.get(0))) {
								mImageLoader.loadUrl(new ImageView(this),
										imgs.get(0));
								bitmap = mImageLoader.getBitmapFormCache(imgs
										.get(0));
								imageUrl = imgs.get(0);
							}
						}
					} else {
						bitmap = BitmapFactory.decodeResource(getResources(),
								R.drawable.ic_launcher);
						imageUrl = "";
					}
				} else {
					// bitmap = BitmapFactory.decodeResource(getResources(),
					// R.drawable.ic_launcher);
					bitmap = BitmapFactory.decodeResource(getResources(),
							R.drawable.ic_launcher);
					imageUrl = "";
				}
			}else{
				bitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.ic_launcher);
				imageUrl = "";
			}
		}

		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (bitmap != null) {
					android.util.Log.e("sb", "imageUrl   " + imageUrl);
					entity.shareUrl = url;
					entity.shareTitle = title;
					entity.shareContent = shareContent;
					entity.imageBitmap = bitmap;
					entity.imageUrl = imageUrl;
					CustomSocialShare.shareImagePlatform(
							NewsDetailActivity.this, entity, false);
					// if (!bitmap.isRecycled()) {
					// bitmap.recycle();
					// }
				}
			}
		}, 800);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (mEmptyView.getState() == EmptyView.STATE_LOADING) {
				mWebView.stopLoading();
			}
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void recycle() {
		Application.instance().cancelPendingRequests(NEW_DETAIL_TAG);
		if (mWebView != null) {
			mWebView.stopLoading();
			mWebView.clearHistory();
			mWebView.clearView();
			mWebView.removeAllViews();
			mWebView.clearCache(true);
			mWebView.destroyDrawingCache();
			mWebView.destroy();
			mWebView = null;
			System.gc();
		}
	}

	@Override
	protected void onDestroy() {
		setConfigCallback(null);
		super.onDestroy();
//		recycle();
//		System.gc();
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		int id = v.getId();
		if (id == R.id.empty_view) {
			if (mEmptyView.getState() == EmptyView.STATE_ERROR) {
				mWebView.reload();
			}
		} else if (id == R.id.iv_comment) {
			// if (TextUtils.isEmpty(Application.getCurrentUser())) {
			// Application.showToast("您还未登录，请先登录才能评论！");
			// Intent loginIntent = new Intent(NewsDetailActivity.this,
			// LoginActivity.class);
			// startActivityForResult(loginIntent, LOGIN);
			// } else
			mWebView.loadUrl("javascript:callFuncFromClient('2')");
			mWebView.requestFocus();
			HashMap<String, String> param = new HashMap<String, String>();
			if (mNews != null) {
				param.put("A_commentNews_newsDetail_newsTitle", mNews
						.getTemplate().getTitle());
				MobclickAgent.onEvent(getApplicationContext(),
						"E_C_newsDetail_commentNewsClick", param);
			} else if (correntNews != null) {
				param.put("A_commentNews_newsDetail_newsTitle", correntNews
						.getTemplate().getTitle());
				MobclickAgent.onEvent(getApplicationContext(),
						"E_C_newsDetail_commentNewsClick", param);
			}
		} else if (id == R.id.iv_collection) {
			setResult(RESULT_OK);
			if (mNews != null) {
				newsAddOrRemove(mNews);
			} else if (correntNews != null) {
				newsAddOrRemove(correntNews);
			}
			HashMap<String, String> param = new HashMap<String, String>();
			if (mNews != null) {
				param.put("A_collectNews_newsDetail_newsTitle", mNews
						.getTemplate().getTitle());
				MobclickAgent.onEvent(getApplicationContext(),
						"E_C_newsDetail_collectNewsClick", param);
			} else if (correntNews != null) {
				param.put("A_collectNews_newsDetail_newsTitle", correntNews
						.getTemplate().getTitle());
				MobclickAgent.onEvent(getApplicationContext(),
						"E_C_newsDetail_collectNewsClick", param);
			}
		} else if (id == R.id.iv_share) {
			// HashMap<String, String> param = new HashMap<String, String>();
			// if (mNews != null) {
			// param.put("A_shareNews_newsDetail_newsTitle", mNews
			// .getTemplate().getTitle());
			// MobclickAgent.onEvent(getApplicationContext(),
			// "E_C_newsDetail_shareNewsClick", param);
			// } else if (correntNews != null) {
			// param.put("A_shareNews_newsDetail_newsTitle", correntNews
			// .getTemplate().getTitle());
			// MobclickAgent.onEvent(getApplicationContext(),
			// "E_C_newsDetail_shareNewsClick", param);
			// }
			handleShareNews();
		}
	}

	class SaveCacheTask extends AsyncTask<Void, Void, Void> {

		News mNews;

		public SaveCacheTask(News mNews) {
			super();
			this.mNews = mNews;
		}

		@Override
		protected Void doInBackground(Void... params) {
			collectIds = Application.getCollectNewsIds();
			collectIds_logout = Application.getLogoutCollectNewsIds();
			String cacheContent = IndexCacheUtils
					.getCacheNoUser(PREFIX_COLLECT_NEWS
							+ Application.getCurrentUser());
			JSONArray array = new JSONArray();
			if (!TextUtils.isEmpty(cacheContent)) {
				try {
					array = new JSONArray(cacheContent);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			if (collectIds.contains(mNews.getId())
					|| collectIds_logout.contains(mNews.getId())) {
				if (collectIds.contains(mNews.getId())) {
					JSONArray newArray = new JSONArray();
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.optJSONObject(i);
						int id = obj.optInt("id", -1);
						if (id == mNews.getId())
							continue;
						if (!collectIds.contains(id))
							continue;
						newArray.put(obj);
					}
					IndexCacheUtils.saveCacheNoUser(newArray,
							PREFIX_COLLECT_NEWS + Application.getCurrentUser());
					collectIds.remove(mNews.getId());
					Application.setCollectNewsIds(collectIds);
				}
				if (!TextUtils.isEmpty(Application.getCurrentUser())
						&& collectIds_logout.contains(mNews.getId())) {
					String cacheContent2 = IndexCacheUtils
							.getCacheNoUser(PREFIX_COLLECT_NEWS);
					JSONArray array2 = new JSONArray();
					if (!TextUtils.isEmpty(cacheContent2)) {
						try {
							array2 = new JSONArray(cacheContent2);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					JSONArray newArray = new JSONArray();
					for (int i = 0; i < array2.length(); i++) {
						JSONObject obj = array2.optJSONObject(i);
						int id = obj.optInt("id", -1);
						if (id == mNews.getId())
							continue;
						if (!collectIds_logout.contains(id))
							continue;
						newArray.put(obj);
					}
					IndexCacheUtils.saveCacheNoUser(newArray,
							PREFIX_COLLECT_NEWS);
					collectIds_logout.remove(mNews.getId());
					Application.setLogoutCollectNewsIds(collectIds_logout);
				}
			} else {
				try {
					mNews.setCollectTime(new Date().getTime());
					JSONArray newArray = new JSONArray();
					newArray.put(mNews.toJson());
					for (int i = 0; i < array.length(); i++)
						newArray.put(array.optJSONObject(i));
					IndexCacheUtils.saveCacheNoUser(newArray,
							PREFIX_COLLECT_NEWS + Application.getCurrentUser());
					collectIds.add(mNews.getId());
					Application.setCollectNewsIds(collectIds);
				} catch (JSONException ex) {
					ex.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			changeCollect(collectIds.contains(mNews.getId()));
			// Application.showToastShort(collectIds.contains(mNews.getId()) ?
			// "收藏成功"
			// : "取消收藏成功");
			Application.showToast(collectIds.contains(mNews.getId()) ? "收藏成功"
					: "取消收藏成功", 100, 0, 17);
			super.onPostExecute(result);
		}
	}

	private void newsAddOrRemove(final News news) {
		if (mEmptyView.getState() != EmptyView.STATE_NONE) {
			return;
		}
		if (TextUtils.isEmpty(Application.getCurrentUser())) {
			try {
				dealAdd(news);
			} catch (Exception ex) {
			}
			return;
		}
		if (news == null) {
			return;
		}
		int newsId = news.getId();
		showWaitDialog(R.string.progress_subming);
		SubscribeOrCancelNewsRequest req = new SubscribeOrCancelNewsRequest(
				newsId, !collectIds.contains(newsId),
				new BaseRequestListener() {

					@Override
					public void onRequestSuccess(ApiResponse json)
							throws Exception {
						dealAdd(news);
					}

					@Override
					public void onRequestFinish() {
						hideWaitDialog();
					}

					@Override
					public void onRequestFaile(ApiResponse json, Exception e) {
						String errMsg = getErrorMessage(json).trim();
						if (!errMsg.endsWith("您已经收藏过该资讯"))
							Application.showToastShort(getErrorMessage(json));
						else
							try {
								dealAdd(news);
							} catch (Exception ex) {
							}
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {
						Application.showToastShort(BaseRequestListener
								.getErrorMessage(null));
						hideWaitDialog();
					}
				});
		Application.instance().addToRequestQueue(req);
	}

	private void dealAdd(News news) {
		new SaveCacheTask(news).execute();
	}

	private void getDetail(int newsId) {
		Application.instance().cancelPendingRequests(NEW_DETAIL_TAG);
		GetNewsDetailRequest req = new GetNewsDetailRequest(newsId,
				new BaseRequestListener() {

					@Override
					public void onRequestSuccess(ApiResponse json)
							throws Exception {
						try {
							JSONArray list = (JSONArray) json.getData();
							if (list != null && list.length() > 0) {
								correntNews = News.make(list.getJSONObject(0));
								// titleBar.setTitle(correntNews.getSourceUrl());
								// titleBar.setTitleVisiable(View.VISIBLE);
							} else {

							}
						} catch (Exception ex) {
							Log.e(ex.getMessage(), ex);
						}
					}

					@Override
					public void onRequestFinish() {
					}

					@Override
					public void onRequestFaile(ApiResponse json, Exception e) {
						try {

						} catch (Exception ex) {
							Log.e(ex.getMessage(), ex);
						}
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						try {

						} catch (Exception ex) {
							Log.e(ex.getMessage(), ex);
						}
					}
				});
		Application.instance().addToRequestQueue(req, NEW_DETAIL_TAG);
	}

	private float DOWN_X;
	private float DOWN_Y;

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		switch (event.getAction()) {
		case 0:
			DOWN_X = event.getX();
			DOWN_Y = event.getY();
			break;
		case 1:
			break;
		case 2:
			float f = event.getX();
			float f1 = event.getY();
			if (Math.abs(DOWN_Y - f1) < 50F && f - DOWN_X > 100F) {
				// finish();
				// overridePendingTransition(R.anim.in_from_left,
				// R.anim.out_to_right);
			}
			break;
		default:
			break;
		}
		return false;
	}

	private void sendGetDetailRequest() {
		Application.instance().cancelPendingRequests(NEW_DETAIL_TAG);
		GetNewsDetailRequest req = new GetNewsDetailRequest(mNews.getId(),
				new BaseRequestListener() {

					@Override
					public void onRequestSuccess(ApiResponse json)
							throws Exception {
						try {
							JSONArray list = (JSONArray) json.getData();
							if (list != null && list.length() > 0) {
								TLog.log(TAG,
										"new detail:" + list.getJSONObject(0));
								showNews(News.make(list.getJSONObject(0)));
							} else {

							}
						} catch (Exception ex) {
							Log.e(ex.getMessage(), ex);
						}
					}

					@Override
					public void onRequestFinish() {
					}

					@Override
					public void onRequestFaile(ApiResponse json, Exception e) {
						try {

						} catch (Exception ex) {
							Log.e(ex.getMessage(), ex);
						}
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						try {

						} catch (Exception ex) {
							Log.e(ex.getMessage(), ex);
						}
					}
				});
		Application.instance().addToRequestQueue(req, NEW_DETAIL_TAG);
	}

	private void showNews(News news) {
		try {
			String content = news.getContent();
			Integer width = Integer
					.valueOf(TDevice.getNeighbourWidth(
							(float) (-20 + TDevice
									.getAppWidth(getApplicationContext()))
									/ TDevice
											.getDensity(getApplicationContext()),
							false));
			// Utils.saveCache(content, "test.txt");
			String s1 = content
					.replaceAll("width= \"[0-9,]+\" height=\"[0-9,]+\"", "")

					.replaceAll(
							".m3u8\"><[iI][mM][gG](?:[^>]+)[sS][rR][cC]=[\\\"']?http://([^>]+)(\\.[^>]+)\\s*[\\\"'] /",
							".m3u8\"><span class=\"imgholder\" data-type=\"video\"><img thumb=\"http://$1$2\" /></span")

					.replaceAll(
							"<[iI][mM][gG](?:[^>]+)[sS][rR][cC]=[\\\"']?http://([^>]+)(\\.[^>]+)\\s*[\\\"']",
							"<span class=\"imgholder\"><img data-src=\"http://$1$2\" alt=\"\" /></span")

					.replaceAll(
							"<[iI][mM][gG](?:[^>]+)[tT][hH][uU][mM][bB]=[\\\"']?http://([^>]+)(\\.[^>]+)\\s*[\\\"']",
							"<img data-src=\"http://$1$2\" alt=\"\" style=\"width:"
									+ width + "px;height=120px;\" ")

					.replaceAll(
							"<[iI][mM][gG](?:.*?)[sS][rR][cC]=[\\\"']?http://(.*?mobileme[^>]+)(\\.[^>]+)\\s*[\\\"']",
							"<img data-src=\"http://$1_" + width
									+ "$2\" alt=\"\" style=\"width:" + width
									+ "px;height=120px;\" ");
			// Utils.saveCache(s1, "test2.txt");
			// initPictureUrlList(s1);
			String template = TDevice.getAssertFileString("news_detail.html");
			if (template == null) {
				Application.showToastShort("读取本地文件出错");
				finish();
			} else {
				String chinnelName = news.getSourceName();
				String tmp;
				if (chinnelName == null || chinnelName.equals(""))
					tmp = template.replaceAll("\\{author\\}", "");
				else
					tmp = template.replaceAll("\\{author\\}", chinnelName);
				if (news.getTemplate().getTitle() != null) {
					if (tmp.contains("{news_title}")) {
						tmp = tmp.replace("{news_title}", news.getTemplate()
								.getTitle());
					}
				}
				if (news.getCreateTime() != 0)
					if (tmp.contains("{date_time}")) {
						tmp = tmp.replace("{date_time}", DateUtil.getDateStr(
								news.getEfficeTime(), "yyyy-MM-dd HH:mm"));
					}
				if (!TextUtils.isEmpty(news.getSourceUrl())) {
					if (tmp.contains("{source}")) {
						tmp = tmp.replace("{source}", news.getSourceUrl());
					}
					// titleBar.setTitle(news.getSourceUrl());
					// titleBar.setTitleVisiable(View.VISIBLE);
					topUrl = news.getSourceUrl();
				}

				if (s1 != null) {
					if (tmp.contains("{content}")) {
						tmp = tmp.replace("{content}", s1);
					}
				}

				String data = tmp.replaceAll("\\{whatsys\\}", "android");
				mWebView.loadDataWithBaseURL(null, data, "text/html", "utf-8",
						null);
			}
		} catch (OutOfMemoryError e) {
			System.gc();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (WeiBoSocialShare.mSsoHandler != null) {
			WeiBoSocialShare.mSsoHandler.authorizeCallBack(requestCode,
					resultCode, data);
		}
		if (requestCode == LOGIN && resultCode == RESULT_OK) {
			loadData();
		} else
			mWebView.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		mWebView.onNewIntent(intent);
		super.onNewIntent(intent);
	}

	@Override
	public void finish() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				System.gc();
				if (mNews != null) {
					System.exit(0);
				}

			}
		}).start();
		super.finish();
	}
}
