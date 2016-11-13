package cn.ffcs.wisdom.city.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import cn.ffcs.external.share.entity.CustomSocialShareEntity;
import cn.ffcs.external.share.view.CustomSocialShare;
import cn.ffcs.ui.tools.TopUtil;
import cn.ffcs.widget.UpToDownSlidingView;
import cn.ffcs.wisdom.city.WelcomeActivity;
import cn.ffcs.wisdom.city.WisdomCityActivity;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.datamgr.AppMgr;
import cn.ffcs.wisdom.city.entity.ConfigParams;
import cn.ffcs.wisdom.city.myapp.bo.MyAppBo;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.push.PushMsgBo;
import cn.ffcs.wisdom.city.setting.feedback.FeedBackActivity;
import cn.ffcs.wisdom.city.sqlite.model.MenuItem;
import cn.ffcs.wisdom.city.sqlite.model.MenuItem.MenuTypeMedia;
import cn.ffcs.wisdom.city.utils.ConfigUtil;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.city.web.adapter.PopListAdapter;
import cn.ffcs.wisdom.city.web.entity.PopEntity;
import cn.ffcs.wisdom.city.web.view.IcityWebView;
import cn.ffcs.wisdom.city.web.view.WebChromeListener;
import cn.ffcs.wisdom.city.web.view.WebClientListener;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.notify.NotificationConstants;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.JsonUtil;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.SdCardTool;
import cn.ffcs.wisdom.tools.ShakeManager;
import cn.ffcs.wisdom.tools.SharedPreferencesUtil;
import cn.ffcs.wisdom.tools.StringUtil;

/**<p>Title:   web浏览器页面   </p>
 * <p>Description:                     </p>
 * <p>@author: zhangwsh                 </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2012-9-13           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
@SuppressLint("NewApi")
public class BrowserActivity extends WisdomCityActivity {

	private boolean DEBUG = false;// 开启调试，改为true之后必须做个标示
	// 控件
	private IcityWebView mBrowser; // 浏览器控件
	private ImageView mBackBtn; // 后退键
	private ImageView mFawordBtn;// 前进键
	private ImageView mFankuiBtn;// 反馈键
	private ImageView mRefreshBtn; // 停止键
	private ImageView mShareBtn;// 分享键
	private ProgressBar mProgressBar; // 进度条
	private LinearLayout topBar;// 顶部
	private LinearLayout bottomBar; // 底部控件
	private LinearLayout webLayout;
	private UpToDownSlidingView upToDownView;
	private RelativeLayout webGroup;
	private RelativeLayout webInterGroup;
	private ImageView smallIco;// 底部滑动后的缩小按钮
	private boolean startIn = true;// 是否是刚启动
	private TextView topTitle;// 头部标题控件
	private View customView;// web页面自定义播放器view
	private CustomViewCallback customViewCallback;
	private FrameLayout fullScreenView;// 全屏view

	private String mUrl;// 页面初始载入的url地址
	private String mTitle; // 页面头部标题

	private String cookies; // 查询类cookies add by 蔡杰杰

	// 动作
	private GestureDetector gestureDetector; // 手势判断
	private Animation topAnimOut = null; // 顶部离开动画
	private Animation topAnimIn = null;// 顶部进入动画
	private Animation bottomAnimOut = null;// 底部离开动画
	private Animation bottomAnimIn = null; // 底部进入动画
	private Animation refreshAnimation;// 刷新动作
	private boolean scorll = true; // 滚动中
	private long distanX;// 拖动距离X
	private long distanY;// 拖动距离Y
	private boolean moving = false;// 移动中
	private int statusBarHeight;// 顶部状态栏高度
	private int right;// 右边距离
	private int bottom;// 底部距离
	private String isQuery;// 是否是查询类过来的。
	private LayoutInflater mInflater;
	private ImageView topRight;
	private PopupWindow popWin;
	private View popLayout;// popwin的布局
	private ListView popListView;
	private boolean state;
	private String itemId;// 菜单id
	private MenuItem menuItem;// 整个菜单

	private PushMsgBo pushBo;

	private RelativeLayout tipLayout;
	private boolean collecting = false;// 正在收藏
	
	private boolean isFromPushPage = false;

	private WebChromeListener webChromeListener = new WebChromeListener() {

		@Override
		public void onShowCustomView(View view, CustomViewCallback callback) {
			if (customView != null) {
				callback.onCustomViewHidden();
				return;
			}
			customView = view;
			customViewCallback = callback;
			fullScreenView.addView(customView, new LayoutParams(
					WindowManager.LayoutParams.MATCH_PARENT,
					WindowManager.LayoutParams.MATCH_PARENT));
			webGroup.setVisibility(View.GONE);
			getWindow().setBackgroundDrawableResource(R.color.black);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			if (newProgress == 100) {
				mProgressBar.setVisibility(View.GONE);
			} else if (mProgressBar.getVisibility() != View.VISIBLE) {
				mProgressBar.setVisibility(View.VISIBLE);
			}
			mProgressBar.setProgress(newProgress);
		}

		@Override
		public void onHideCustomView() {
			hideCustomViewAndRmoveView();
		}
	};

	private WebClientListener webClientListener = new WebClientListener() {

		@Override
		public void onReceivedError(WebView view, int errorCode, String description,
				String failingUrl) {
			Log.e("web page error,code：" + errorCode);
			Log.e("web page error,url：" + failingUrl);
			Log.e("web page error,description：" + description);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			Log.i("web page start");
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			mRefreshBtn.clearAnimation();
			Log.i("web page finish");
			if (startIn && !"1".equals(isQuery)) {
				startIn = false;
				topBar.startAnimation(topAnimOut);
				topBar.setVisibility(View.GONE);
				bottomBar.startAnimation(bottomAnimOut);
				bottomBar.setVisibility(View.GONE);
			} else if ("1".equals(isQuery)) {
				startIn = false;
				scorll = false;
			}
			try {
				mFawordBtn.setEnabled(view.canGoForward());
				mBackBtn.setEnabled(view.canGoBack());
			} catch (Exception ex) {
				Log.e("browser exception: ", ex);
			}
		}

		@Override
		public String getReturnTitle() {
			return mTitle;
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			initProgressBar();
			if (url.indexOf("http://") == 0 || url.indexOf("https://") == 0) {
				view.loadUrl(url);
			} else {
				try {
					Uri uri = Uri.parse(url);
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				} catch (ActivityNotFoundException e) {
					CommonUtils.showToast(mActivity, R.string.web_no_find, Toast.LENGTH_SHORT);
				}
			}
			return true;
		}
	};

	private OnClickListener itemClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			upToDownView.close();
			int id = v.getId();
			// if (id == R.id.web_share) {
			// share();
			// } else
			if (id == R.id.web_collect) {
				if (!collecting) {
					collect();
				}
			} else if (id == R.id.web_error) {
				CommonUtils.showToast(mActivity, R.string.web_cuting, Toast.LENGTH_SHORT);
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						error();
					}
				}, upToDownView.getScrollTime());
			}
		}
	};

	@Override
	protected void initComponents() {
		getWindow().setBackgroundDrawableResource(R.color.white);// 改变背景为白色
		mInflater = getLayoutInflater();
		fullScreenView = (FrameLayout) findViewById(R.id.fullscreen_view);
		upToDownView = (UpToDownSlidingView) findViewById(R.id.up_to_down_view);
		webGroup = (RelativeLayout) findViewById(R.id.web_group);
		webLayout = (LinearLayout) findViewById(R.id.web_layout);
		webInterGroup = (RelativeLayout) findViewById(R.id.web_inter_group);
		topBar = (LinearLayout) mInflater.inflate(R.layout.top, null);
		//底部功能栏
		bottomBar = (LinearLayout) mInflater.inflate(R.layout.widget_web_tool_bar, null);
		mBrowser = (IcityWebView) findViewById(R.id.browser_view);
		mBrowser.init(mActivity);
		mBrowser.setWebClientListener(webClientListener);
		mBrowser.setWebChromeListener(webChromeListener);
		mBackBtn = (ImageView) bottomBar.findViewById(R.id.browser_btn_back);
		mBackBtn.setOnClickListener(new OnBottomClickListener());
		mBackBtn.setEnabled(false);
		mFawordBtn = (ImageView) bottomBar.findViewById(R.id.browser_btn_faword);
		mFawordBtn.setOnClickListener(new OnBottomClickListener());
		mFawordBtn.setEnabled(false);
		mFankuiBtn = (ImageView) bottomBar.findViewById(R.id.browser_btn_fankui);
		mFankuiBtn.setOnClickListener(new OnBottomClickListener());
		mRefreshBtn = (ImageView) bottomBar.findViewById(R.id.browser_btn_refresh);
		mRefreshBtn.setOnClickListener(new OnBottomClickListener());
		mShareBtn = (ImageView) bottomBar.findViewById(R.id.browser_btn_share);
		mShareBtn.setOnClickListener(new OnBottomClickListener());
		topTitle = (TextView) topBar.findViewById(R.id.top_title);
		topRight = (ImageView) topBar.findViewById(R.id.top_right);
		topRight.setOnClickListener(new RightClickListener());
		gestureDetector = new GestureDetector(mContext, new MyGestureDetector());
		smallIco = (ImageView) findViewById(R.id.small_ico);

		mBrowser.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent e) {
				if (upToDownView != null) {
					upToDownView.close();
				}
				state = false;
				if (gestureDetector.onTouchEvent(e)) {
					return true;
				}
				return false;
			}
		});
	}

	@Override
	protected void initData() {
		getParam();// 获取参数
		initTopAndBottom();// 设置头部和底部
		mProgressBar.setMax(100);
		configAnimation();// 配置上下条进出动画
		iconConfig();// 配置小图标
		initProgressBar();
		String test = "file:///android_asset/test.html";
		if (DEBUG) {
			mBrowser.loadUrl(test);
		} else {
			mBrowser.loadUrl(mUrl);
			Log.d("Url is : " + mUrl);
		}
		// 消息回执--liaodl
		pushBo = new PushMsgBo(mActivity);
		pushBo.pushFeedbackAndClearMsg(Config.REBACK_WAP_MSG_TYPE);
	}

	/**
	 *  
	 */
	// private void loadUrl(final String url) {
	// new Thread(new Runnable() {
	// @Override
	// public void run() {
	// Message msg = new Message();
	// msg.what = getRespStatus(url);
	// msg.obj = url;
	// handler.sendMessage(msg);
	// }
	// }).start();
	// }

	/**
	 * 右上角点击
	 */
	class RightClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if ("1".equals(isQuery)) {
				upToDownView.toggle();
			} else {
				showPopWin();
			}
		}
	}

	/**
	 * 显示popupwin
	 */
	private void showPopWin() {
		if (popWin == null) {
			popLayout = getLayoutInflater().inflate(R.layout.widget_web_popwin, null);
			popListView = (ListView) popLayout.findViewById(R.id.pop_list);
			popListView.setOnItemClickListener(new PopWinItemClick());
			// int[] icon = new int[] { R.drawable.web_pop_share,
			// R.drawable.web_pop_collect,
			// R.drawable.web_pop_error };
			// int[] text = new int[] { R.string.web_share,
			// R.string.web_collect, R.string.web_error };

//			int[] icon = new int[] { R.drawable.web_pop_collect, R.drawable.web_pop_error };
//			int[] text = new int[] { R.string.web_collect, R.string.web_error };
			int[] icon = new int[] { R.drawable.web_pop_error };
			int[] text = new int[] { R.string.web_error };
			List<PopEntity> popContent = new ArrayList<PopEntity>();

			for (int i = 0; i < icon.length; i++) {
				PopEntity entity = new PopEntity();
				entity.setIconResId(icon[i]);
				entity.setTextResId(text[i]);
				popContent.add(entity);
			}

			PopListAdapter listAdapter = new PopListAdapter(mContext, popContent);
			popListView.setAdapter(listAdapter);

			popWin = new PopupWindow(popLayout, CommonUtils.convertDipToPx(mContext, 220),
					android.view.WindowManager.LayoutParams.WRAP_CONTENT);
			ColorDrawable cd = new ColorDrawable(getResources().getColor(R.color.transparent));
			popWin.setBackgroundDrawable(cd);
			popWin.setAnimationStyle(R.style.popwinAnimation);
			popWin.update();
			popWin.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
			popWin.setTouchable(true); // 设置popupwindow可点击
			popWin.setOutsideTouchable(true); // 设置popupwindow外部可点击
			popWin.setFocusable(true); // 获取焦点
			popWin.setTouchInterceptor(new View.OnTouchListener() {
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
						popWin.dismiss();
						return true;
					}
					return false;
				}
			});
		}
		if (state == true && popWin.isShowing()) {
			state = false;
			popWin.dismiss();
		} else {
			popWin.showAsDropDown(topRight);
			state = true;
		}
	}

	/**
	 * 右上角item点击
	 */
	class PopWinItemClick implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			popWin.dismiss();
			switch (position) {
			// case 0:// 分享
			// share();
			// break;
			case 0:// 收藏
				if (!collecting) {
					collect();
				}
				break;
			case 1:// 截屏报错
				error();
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 截屏报错
	 */
	private void error() {
		state = false;
		if (!SdCardTool.isMounted()) {
			Toast.makeText(BrowserActivity.this, "请确认SD卡", Toast.LENGTH_SHORT).show();
			return;
		}
		String path = Config.SDCARD_CITY_TMP;
		String fileName = "fankui.jpg";
		Bitmap mBit = CommonUtils.shot(BrowserActivity.this);// 获取到截取的图片
		SdCardTool.save(mBit, path, fileName);// 调用保存sd卡的函数
		mBit.recycle();// 回收
		Intent intent = new Intent(BrowserActivity.this, FeedBackActivity.class);// 跳转到意见反馈界面
		path = path + fileName;// 获取到图片
		intent.putExtra("path", path);// 传递sd卡路劲
		intent.putExtra("title", mTitle);// 传递标题
		intent.putExtra(Key.K_RETURN_TITLE, mTitle);
		startActivity(intent);
	}

	/**
	 * 收藏
	 */
	private void collect() {
		collecting = true;
		boolean isLogin = AccountMgr.getInstance().isLogin(mContext);
		if (isLogin) {
			CommonUtils.showToast(mActivity, R.string.web_collecting, Toast.LENGTH_SHORT);
			String mobile = AccountMgr.getInstance().getMobile(mContext);
			Map<String, String> map = new HashMap<String, String>();
			map.put("menuId", itemId);
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			list.add(map);
			String json = "{" + "\"menuIdList\":" + JsonUtil.toJson(list) + "}";
			new MyAppBo(new CollectCallBack(), mContext).addMyApp(mobile, json);
		} else {
			int successMsg = R.string.web_collect_successed;
			if (AppMgr.getInstance().isExist(mContext, itemId)) {
				AppMgr.getInstance().deleteByItemId(mContext, itemId);
				successMsg = R.string.web_collect_is_exist;
			}
			AppMgr.getInstance().saveApp(mContext, AppMgr.getInstance().convertToAppItem(menuItem));
			CommonUtils.showToast(mActivity, successMsg, Toast.LENGTH_SHORT);
			collecting = false;
		}
	}

	/**
	 * 收藏回调
	 */
	class CollectCallBack implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp response) {
			collecting = false;
			if (response.isSuccess()) {
				CommonUtils
						.showToast(mActivity, R.string.web_collect_successed, Toast.LENGTH_SHORT);
			} else {
				CommonUtils.showToast(mActivity, response.getDesc(), Toast.LENGTH_SHORT);
			}
		}

		@Override
		public void progress(Object... obj) {
		}

		@Override
		public void onNetWorkError() {
		}
	}

	/**
	 * 分享
	 * 1、栏目有配置分享语，根据分享类型进行判断
	 * 分享类型：1、内容型（浏览器当前的地址）；2、应用型（读取菜单的url地址）
	 * 2、没有配置，默认读取1038接口的分享语跟分享地址
	 */
	private void share() {
		String menuShareContent = null;// 栏目分享语
		String menuShareType = null;// 栏目分享类型
		String shareContent = null;// 判断之后的分享语
		ConfigParams params = ConfigUtil.readConfigParams(mContext);
		String shareUrl = params.getMenuShareUrl();
		String itemName = mTitle;
		String shareContentParam = params.getMenuShareContent();
		// 2、判断栏目有没有配置
		if (menuItem != null) {
			menuShareContent = menuItem.getShareContent();
			menuShareType = menuItem.getShareType();
			if (!StringUtil.isEmpty(menuShareType)) {
				if ("1".equals(menuShareType)) {// 内容型
					if (!mBrowser.canGoBack()) {// 无法后退，表示初次进入
						shareUrl = menuItem.getUrl();
					} else {
						shareUrl = mBrowser.getUrl();
					}
				} else if ("2".equals(menuShareType)) {// 应用型
					shareUrl = menuItem.getUrl();
				}
			} else {
				shareUrl = menuItem.getUrl();
			}
		}
		//如果全部为空，则显示：www.153.cn
		if (StringUtil.isEmpty(shareUrl) || StringUtil.isEmpty(shareUrl = shareUrl.trim())) {
			shareUrl = mContext.getString(R.string.category_default_url);
		}
		//栏目没有配置
		if (StringUtil.isEmpty(menuShareContent) || "null".equals(menuShareContent)) {
			// 客户端默认的分享语言
			shareContent = mContext.getString(R.string.category_share_msg, itemName,
					shareContentParam);
		} else {
			shareContent = menuShareContent;
		}
		CustomSocialShare.shareTextPlatform(mActivity, itemName, shareContent, shareUrl);
	}

	/**
	* 分享
	*/
	private void bannerShare() {
		ConfigParams params = ConfigUtil.readConfigParams(mContext);
		String shareUrl = params.getMenuShareUrl();
		String itemName = mTitle;
		String shareContentParam = params.getMenuShareContent();

		if (StringUtil.isEmpty(shareUrl) || StringUtil.isEmpty(shareUrl = shareUrl.trim())) {
			shareUrl = mContext.getString(R.string.category_default_url);
		}

		String shareContent = mContext.getString(R.string.category_share_msg, itemName,
				shareContentParam, shareUrl);
		if (StringUtil.isEmpty(shareContent)) {
			shareContent = mContext.getString(R.string.category_default_msg, itemName);
		}
		CustomSocialShare.shareTextPlatform(mActivity, itemName, shareContent, shareUrl);
	}

	/**
	 * 初始化拉出框
	 */
	private void initTipLayout() {
		tipLayout = (RelativeLayout) findViewById(R.id.full_layout);
		// TableRow share = (TableRow) findViewById(R.id.web_share);
		// share.setLayoutParams(new
		// LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
		// CommonUtils.convertDipToPx(mContext, 50)));
		// share.setGravity(Gravity.CENTER);
		// share.setOnClickListener(itemClick);

		TableRow collect = (TableRow) findViewById(R.id.web_collect);
		collect.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, CommonUtils.convertDipToPx(mContext, 50)));
		collect.setGravity(Gravity.CENTER);
		collect.setOnClickListener(itemClick);

		TableRow error = (TableRow) findViewById(R.id.web_error);
		error.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				CommonUtils.convertDipToPx(mContext, 50)));
		error.setGravity(Gravity.CENTER);
		error.setOnClickListener(itemClick);

		upToDownView.setUpView(tipLayout);
	}

	/**
	 * 配置头部和底部
	 */
	private void initTopAndBottom() {
		if (!"1".equals(isQuery)) {
			// 顶部
			LayoutParams topBarParam = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
			webGroup.addView(topBar, topBarParam);
			// 顶部投影
			LinearLayout.LayoutParams topShadowBarParam = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			View topShadow = mInflater.inflate(R.layout.top_shadow, null);
			topBar.addView(topShadow, topShadowBarParam);

			// 底部
			LayoutParams toolBarParam = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
			toolBarParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			webGroup.addView(bottomBar, toolBarParam);

			// 底部投影
			LinearLayout.LayoutParams toolShadowBarParam = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			View toolshadow = mInflater.inflate(R.layout.tool_bar_shadow, null);
			bottomBar.addView(toolshadow, 0, toolShadowBarParam);

			// 顶部加载条
			View loadingBar = mInflater.inflate(R.layout.widget_web_loading_bar, null);
			mProgressBar = (ProgressBar) loadingBar.findViewById(R.id.top_loading);
			LayoutParams LoadingParam = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
			webGroup.addView(loadingBar, LoadingParam);
		} else {
			LinearLayout.LayoutParams topBarParam = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			LinearLayout.LayoutParams toolBarParam = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

			webLayout.addView(topBar, 0, topBarParam);
			initTipLayout();
			webLayout.addView(bottomBar, toolBarParam);

			// 顶部投影
			View topShadow = mInflater.inflate(R.layout.top_shadow, null);
			LayoutParams topBarShadowParam = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
			topBarShadowParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			webInterGroup.addView(topShadow, topBarShadowParam);

			// 加载条
			View loadingBar = mInflater.inflate(R.layout.widget_web_loading_bar, null);
			mProgressBar = (ProgressBar) loadingBar.findViewById(R.id.top_loading);
			LayoutParams LoadingParam = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
			webInterGroup.addView(loadingBar, LoadingParam);

			// 底部投影
			View toolShadow = mInflater.inflate(R.layout.tool_bar_shadow, null);
			LayoutParams toolBarShadowParam = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
			toolBarShadowParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			webInterGroup.addView(toolShadow, toolBarShadowParam);
		}
	}

	/**
	 * 获取属性
	 */
	private void getParam() {
		isFromPushPage = getIntent().getBooleanExtra(Key.U_BROWSER_IS_PUSH, false);
		
		mUrl = getIntent().getStringExtra(Key.U_BROWSER_URL);
		if (mUrl == null) {
			mUrl = getIntent().getStringExtra(NotificationConstants.NOTIFICATION_URL);
		}
		if (mUrl == null) {
			mUrl = "";
		}
		mTitle = getIntent().getStringExtra(Key.U_BROWSER_TITLE);
		if (mTitle == null) {
			mTitle = getIntent().getStringExtra(NotificationConstants.NOTIFICATION_TITLE);
		}
		if (mTitle == null) {
			mTitle = "";
		}
		boolean force = getIntent().getBooleanExtra(Key.U_FORCE_HIDE_T_B, false);
		if (force) {
			isQuery = "0";
		} else {
			isQuery = getIntent().getStringExtra(Key.U_BROWSER_QUERY);
			isQuery = "1";
		}
		TopUtil.updateTitle(topTitle, mTitle);

		Serializable serializable = getIntent().getSerializableExtra(Key.U_BROWSER_ITEM);
		if (serializable != null && serializable instanceof MenuItem) {
			menuItem = (MenuItem) serializable;
			itemId = menuItem.getMenuId();
			if (!StringUtil.isEmpty(itemId)) {
				TopUtil.updateRight(topRight, R.drawable.web_top_right);
				mShareBtn.setVisibility(View.VISIBLE);
			} else {
				if (menuItem.media == MenuTypeMedia.bannerWapShare) {
					mShareBtn.setVisibility(View.VISIBLE);
					mShareBtn.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							bannerShare();
						}
					});

				}
			}
		}
		boolean showShare = getIntent().getBooleanExtra("shareTraffic", false);
		if (showShare) {
			TopUtil.updateRight(topRight, R.drawable.share_button_selector);
			topRight.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					shareTraffic();
					
				}
			});
		}
		cookies = getIntent().getStringExtra(Key.U_BROWSER_COOKIES);
		// start add by 蔡杰杰 2012-12-12
		if (!StringUtil.isEmpty(cookies)) {
			CookieSyncManager.createInstance(mContext);
			CookieManager cookieManager = CookieManager.getInstance();
			cookieManager.setAcceptCookie(true);
			cookieManager.setCookie(mUrl, cookies);// cookies是在HttpClient中获得的cookie
			CookieSyncManager.getInstance().sync();
		}
		// end
	}
	
	private void shareTraffic() {
		CustomSocialShareEntity entity = new CustomSocialShareEntity();
		String url = mContext.getString(R.string.category_default_url);;
		String title = mTitle;
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		String shareContent = "@无线长春 高速路况分享给您。大家帮大家，方便你我他";
		entity.shareUrl = url;
		entity.shareTitle = title;
		entity.shareContent = shareContent;
		entity.imageBitmap = bitmap;
		CustomSocialShare.shareImagePlatform(mActivity, entity, false);
	}

	/**
	 * 配置小图标
	 */
	private void iconConfig() {
		// 恢复上次小图标位置
		smallIco.setOnTouchListener(new WebViewIconOnTouchListener());
		LayoutParams params = (LayoutParams) smallIco.getLayoutParams();
		String icoRight = SharedPreferencesUtil.getValue(mContext, Key.K_WEB_ICO_RIGHT);
		String icoBottom = SharedPreferencesUtil.getValue(mContext, Key.K_WEB_ICO_BOTTOM);
		if (!StringUtil.isEmpty(icoRight)) {
			right = Integer.parseInt(icoRight);
		} else {
			right = 10;
		}
		if (!StringUtil.isEmpty(icoBottom)) {
			bottom = Integer.parseInt(icoBottom);
		} else {
			bottom = 10;
		}
		params.rightMargin = right;
		params.bottomMargin = bottom;
		smallIco.setLayoutParams(params);
		smallIco.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (Math.abs(distanX) < smallIco.getWidth() / 2
						&& Math.abs(distanY) < smallIco.getHeight() / 2) {
					topBar.setVisibility(View.VISIBLE);
					topBar.startAnimation(topAnimIn);
					bottomBar.setVisibility(View.VISIBLE);
					bottomBar.startAnimation(bottomAnimIn);
					smallIco.setVisibility(View.GONE);
					scorll = false;
				}
			}
		});
	}

	/**
	 * 配置动作
	 */
	private void configAnimation() {
		// 进出动画
		refreshAnimation = AnimationUtils.loadAnimation(mContext, R.anim.keep_rotate);
		refreshAnimation.setDuration(1000);
		topAnimOut = AnimationUtils.loadAnimation(mContext, R.anim.top_push_up_out);
		topAnimIn = AnimationUtils.loadAnimation(mContext, R.anim.top_push_up_in);
		bottomAnimOut = AnimationUtils.loadAnimation(mContext, R.anim.bottom_push_up_out);
		bottomAnimIn = AnimationUtils.loadAnimation(mContext, R.anim.bottom_push_up_in);
		bottomAnimOut.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				if (popWin != null && popWin.isShowing()) {
					state = false;
					popWin.dismiss();
				}
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				smallIco.setVisibility(View.VISIBLE);
			}
		});
	}

	/**
	 * 手势判断
	 */
	class MyGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			if (!scorll && !"1".equals(isQuery)) {
				topBar.startAnimation(topAnimOut);
				topBar.setVisibility(View.GONE);
				bottomBar.startAnimation(bottomAnimOut);
				bottomBar.setVisibility(View.GONE);
				scorll = true;
				return true;
			}
			return false;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			if (!scorll && !"1".equals(isQuery)) {
				topBar.startAnimation(topAnimOut);
				topBar.setVisibility(View.GONE);
				bottomBar.startAnimation(bottomAnimOut);
				bottomBar.setVisibility(View.GONE);
				scorll = true;
				return true;
			}
			return false;
		}
	}

	/**
	 * 底部按钮点击
	 */
	private class OnBottomClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			if (id == R.id.browser_btn_back) {
				if (mBrowser.canGoBack()) {
					mBrowser.goBack();
				}
			} else if (id == R.id.browser_btn_faword) {
				if (mBrowser.canGoForward()) {
					mBrowser.goForward();
				}
			} else if (id == R.id.browser_btn_refresh) {
				refreshPage();
			} else if (id == R.id.browser_btn_share) {
				share();
			}
		}
	}

	/**
	 * 刷新页面
	 */
	private void refreshPage() {
		if (mBrowser != null) {
			// error = false;
			mBrowser.stopLoading();
			String url = mBrowser.getUrl();
			Log.d("refresh url: " + url);
			initProgressBar();
			if (!StringUtil.isEmpty(url)) {
				mBrowser.reload();
			}
		}
	}

	/**
	 * 初始化进度条
	 */
	private void initProgressBar() {
		mRefreshBtn.startAnimation(refreshAnimation);
		mProgressBar.setVisibility(View.VISIBLE);
		mProgressBar.setProgress(10);
	}

	/**
	 * 删除额外添加的View
	 */
	private void hideCustomViewAndRmoveView() {
		try {
			customViewCallback.onCustomViewHidden();
			fullScreenView.removeView(customView);
			customView = null;
			webGroup.setVisibility(View.VISIBLE);
			getWindow().setBackgroundDrawableResource(R.color.white);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
	}

	/**
	 * 回退按钮捕获
	 */
	public boolean onKeyDown(int keyCoder, KeyEvent event) {
		if (keyCoder == KeyEvent.KEYCODE_BACK) {
			if (customView != null) {
				hideCustomViewAndRmoveView();
				return true;
			}

			if (mBrowser.canGoBack()) {
				if (upToDownView != null) {
					upToDownView.close();
				}
				mBrowser.goBack();
			} else
				this.finish();
			return true;
		}
		return super.onKeyDown(keyCoder, event);
	}

	/**
	 * 小图标拖动监听
	 */
	class WebViewIconOnTouchListener implements OnTouchListener {
		int lastX, lastY; // 记录移动的最后的位置
		int screenWidth = AppHelper.getScreenWidth(mContext);
		int screenHeight = AppHelper.getScreenHeight(mContext);

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int action = event.getAction();
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				statusBarHeight = AppHelper.getStatusBarHeight(mActivity);// 获取顶部状态栏高度
				lastX = (int) event.getRawX();
				lastY = (int) event.getRawY();
				distanX = 0;
				distanY = 0;
				break;
			case MotionEvent.ACTION_MOVE:
				int dx = (int) event.getRawX() - lastX;
				int dy = (int) event.getRawY() - lastY;
				distanX += Math.abs(dx);
				distanY += Math.abs(dy);
				int touchSlop = ViewConfiguration.get(getApplicationContext()).getScaledTouchSlop();
				if (Math.abs(dx) < touchSlop && Math.abs(dy) < touchSlop) {
					if (!moving) {
						break;
					}
				}
				moving = true;
				right = screenWidth - v.getRight() - dx;
				bottom = screenHeight - v.getBottom() - statusBarHeight - dy;
				if (v.getLeft() <= 0) {
					if (dx < 0) {
						right = screenWidth - v.getWidth();
					}
				}

				if (v.getRight() > screenWidth) {
					right = 0;
				}

				if (v.getTop() <= 0) {
					if (dy < 0) {
						bottom = screenHeight - v.getHeight() - statusBarHeight;
					}
				}

				if (v.getBottom() + statusBarHeight > screenHeight) {
					bottom = 0;
				}

				LayoutParams params = (LayoutParams) v.getLayoutParams();
				params.rightMargin = right;
				params.bottomMargin = bottom;
				v.setLayoutParams(params);
				// 将当前的位置再次设置
				lastX = (int) event.getRawX();
				lastY = (int) event.getRawY();
				break;
			case MotionEvent.ACTION_UP:
				moving = false;
			default:
				break;
			}
			return false;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		mBrowser.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void finish() {
		ShakeManager.getShakeManger(mContext).unRegister();
		// 保存当前小图位置
		SharedPreferencesUtil.setValue(mContext, Key.K_WEB_ICO_RIGHT, String.valueOf(right));
		SharedPreferencesUtil.setValue(mContext, Key.K_WEB_ICO_BOTTOM, String.valueOf(bottom));
		super.finish();
		
		if (isFromPushPage) {
			Intent intent = new Intent(mActivity, WelcomeActivity.class);
			startActivity(intent);
		}
	}

	@Override
	protected int getMainContentViewId() {
		return R.layout.act_browser;
	}

	@Override
	protected void onResume() {
		if (CommonUtils.getSystemVersion() >= 11) {
			mBrowser.onResume();
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		if (CommonUtils.getSystemVersion() >= 11) {
			mBrowser.onPause();
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mBrowser.loadData("<a></a>", "text/html", "utf-8");
//		TtsSpeechApi.stopSpeaking();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		String url = intent.getStringExtra(Key.K_OUT_URL);
		if (url == null) {
			url = intent.getStringExtra(NotificationConstants.NOTIFICATION_URL);
		}
		mTitle = intent.getStringExtra(Key.U_BROWSER_TITLE);
		if (mTitle == null) {
			mTitle = intent.getStringExtra(NotificationConstants.NOTIFICATION_TITLE);
		}
		if (mTitle == null) {
			mTitle = "";
		}
		if (url != null) {
			TopUtil.updateTitle(topTitle, mTitle);
			mBrowser.loadUrl(url);
			int msgId = intent.getIntExtra(NotificationConstants.NOTIFICATION_ID, -1);
			if (pushBo == null) {
				pushBo = new PushMsgBo(mActivity);
			}
			pushBo.clearMsg(msgId + "");
			pushBo.pushFeedback(Config.REBACK_WAP_MSG_TYPE, false);
		}

		mBrowser.onNewIntent(intent);
		super.onNewIntent(intent);
	}
}
