package cn.ffcs.wisdom.city.web.view;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebStorage.QuotaUpdater;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
//import cn.ffcs.pay.entity.PayProduct;
import cn.ffcs.ui.tools.AlertBaseHelper;
import cn.ffcs.widget.LoadingDialog;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.resp.UpLoadImageResp;
import cn.ffcs.wisdom.city.task.ImageUpLoadTask;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.city.web.IcityWebViewClient;
import cn.ffcs.wisdom.city.web.IcityWebViewHelper;
import cn.ffcs.wisdom.city.web.listener.OnDownloadListener;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.interfaces.IScreenShotCallBack;
import cn.ffcs.wisdom.interfaces.IShakeCallBack;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.JsonUtil;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.SystemCallUtil;
import cn.ffcs.wisdom.web.BaseWebViewClient;

public class IcityWebView extends WebView {

	private Activity mActivity;
	private WebSettings mWebSettings;
	private WebChromeListener mWebChromeListener;
	private WebClientListener mWebClientListener;
	private ValueCallback<Uri> upLoadMessage;
	private String fileName = "imageCutTmp.jpg";
	private String filePath = Config.SDCARD_WEB;
	private IScreenCallBack iScreenCallBack = new IScreenCallBack();// 截屏回调
	private IShakeCall isShakeCall = new IShakeCall();// 摇动回调
	/**
	 * 选择文件requestCode
	 */
	private final int REQUESTCODE_SELECT_FILE = 0x10001;

	public IcityWebView(Context context) {
		super(context);
	}

	public IcityWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public IcityWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public IcityWebView(Context context, AttributeSet attrs, int defStyle, boolean privateBrowsing) {
		super(context, attrs, defStyle, privateBrowsing);
	}

	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void init(Activity activity) {
		mActivity = activity;
		mWebSettings = getSettings();
		if (CommonUtils.getSystemVersion() >= 11) {
			mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
					WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
		}
		setDownloadListener(new OnDownloadListener(getContext()));
		super.setWebViewClient(new MyWebClient(mActivity, this));
		super.setWebChromeClient(new MyWebChromeClient());
		// mBrowser.getSettings().setBuiltInZoomControls(true);
		this.requestFocus();
		this.requestFocusFromTouch();
		mWebSettings.setSupportZoom(true);// 可以缩放
		mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);// javascript可以自动打开窗体
		mWebSettings.setJavaScriptEnabled(true);// 允许javascript
		if (CommonUtils.getSystemVersion() < 18) {
			mWebSettings.setPluginsEnabled(true);// flash支持
		}
		mWebSettings.setPluginState(PluginState.ON);// flash支持
		mWebSettings.setRenderPriority(RenderPriority.HIGH);// 提高渲染优先级，以加速webview
		mWebSettings.setDomStorageEnabled(true);
		mWebSettings.setAllowFileAccess(true);
		// mBrowser.getSettings().setSupportMultipleWindows(true);
		mWebSettings.setLoadWithOverviewMode(true);
		mWebSettings.setDatabaseEnabled(true);
		mWebSettings.setGeolocationEnabled(true);
		String dir = getContext().getDir("database", Context.MODE_PRIVATE).getPath();
		Log.d("dir: " + dir);
		mWebSettings.setDatabasePath(dir);
		mWebSettings.setGeolocationDatabasePath(dir);
		mWebSettings.setUserAgentString(mWebSettings.getUserAgentString() + " ffcs/icity");
		Log.d("UA: " + getSettings().getUserAgentString());
	}

	/**
	 * 设置webClient监听
	 * @param listener
	 */
	public void setWebClientListener(WebClientListener listener) {
		this.mWebClientListener = listener;
	}

	/**
	 * 设置webChrome监听
	 * @param listener
	 */
	public void setWebChromeListener(WebChromeListener listener) {
		this.mWebChromeListener = listener;
	}

	@Override
	@Deprecated
	/**
	 * don't use this method,use setWebClientListener instead;
	 */
	public void setWebViewClient(WebViewClient client) {
		throw new DeprecatedException("icity webview don't use this method");
	}

	@Override
	@Deprecated
	/**
	 * don't use this method,use setWebChromeListener instead;
	 */
	public void setWebChromeClient(WebChromeClient client) {
		throw new DeprecatedException("icity webview don't use this method");
	}

	class DeprecatedException extends RuntimeException {
		/**
		 * 
		 */
		private static final long serialVersionUID = -1462437482551737462L;

		DeprecatedException(String message) {
			super(message);
		}
	}

	/**
	 * WebViewClient 设置，继承底层
	 */
	class MyWebClient extends IcityWebViewClient {

		public MyWebClient(Activity activity, WebView webView) {
			super(activity, webView);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (!super.shouldOverrideUrlLoading(view, url)) {
				if (mWebClientListener != null) {
					return mWebClientListener.shouldOverrideUrlLoading(view, url);
				}
				return false;
			}
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			if (mWebClientListener != null) {
				mWebClientListener.onPageFinished(view, url);
			}
			super.onPageFinished(view, url);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			if (mWebClientListener != null) {
				mWebClientListener.onPageStarted(view, url, favicon);
			}
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description,
				String failingUrl) {
			if (mWebClientListener != null) {
				mWebClientListener.onReceivedError(view, errorCode, description, failingUrl);
			}
			super.onReceivedError(view, errorCode, description, failingUrl);
		}

		@Override
		public void onFormResubmission(WebView view, Message dontResend, Message resend) {
			resend.sendToTarget();
		}
		
		@Override
		public String getFilePath() {
			return filePath;
		}

		@Override
		public String getReturnTitle() {
			if (mWebClientListener != null) {
				return mWebClientListener.getReturnTitle();
			}
			return "";
		}

		@Override
		public IScreenShotCallBack getIScreenShotCallBack() {
			return iScreenCallBack;
		}

		@Override
		public IShakeCallBack getIShakeCallBack() {
			return isShakeCall;
		}
	}

	// 摇动回调
	class IShakeCall implements IShakeCallBack {

		@Override
		public void call(int count) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(IcityWebViewHelper.FUNCID, 11);
			map.put(IcityWebViewHelper.ISSUCCED, true);
			map.put("shake_num", count);
			String shakeSuc = JsonUtil.toJson(map);
			loadUrl("javascript:icity_func('" + shakeSuc + "')");
		}
	}

	/**
	 * 截屏回调接口
	 */
	class IScreenCallBack implements IScreenShotCallBack {

		@Override
		public void call(String filePath) {
			SystemCallUtil.ImageCut(mActivity, Uri.fromFile(new File(filePath)),
					Uri.fromFile(new File(IcityWebView.this.filePath, fileName)));
		}
	}

	/**
	 * webview内核配置
	 */
	private class MyWebChromeClient extends WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			if (mWebChromeListener != null) {
				mWebChromeListener.onProgressChanged(view, newProgress);
			}
			super.onProgressChanged(view, newProgress);
		}

		@Override
		public void onShowCustomView(View view, CustomViewCallback callback) {
			if (mWebChromeListener != null) {
				mWebChromeListener.onShowCustomView(view, callback);
			}
			super.onShowCustomView(view, callback);
		}

		@Override
		public void onShowCustomView(View view, int requestedOrientation,
				CustomViewCallback callback) {
			onShowCustomView(view, callback);
		}

		@Override
		public void onHideCustomView() {
			if (mWebChromeListener != null) {
				mWebChromeListener.onHideCustomView();
			}
			super.onHideCustomView();
		}

		@Override
		public void onExceededDatabaseQuota(String url, String databaseIdentifier,
				long currentQuota, long estimatedSize, long totalUsedQuota,
				QuotaUpdater quotaUpdater) {
			quotaUpdater.updateQuota(estimatedSize * 2);
		}

		@Override
		public void onGeolocationPermissionsShowPrompt(String origin, Callback callback) {
			callback.invoke(origin, true, false);
			super.onGeolocationPermissionsShowPrompt(origin, callback);
		}

		// don't delete below code,it's used to uploadfile from html
		// For Android 3.0+
		public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
			upLoadMessage = uploadMsg;
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.addCategory(Intent.CATEGORY_OPENABLE);
			intent.setType("image/*");
			mActivity.startActivityForResult(Intent.createChooser(intent, "完成操作需要使用"),
					REQUESTCODE_SELECT_FILE);
		}

		// For Android < 3.0
		@SuppressWarnings("unused")
		public void openFileChooser(ValueCallback<Uri> uploadMsg) {
			openFileChooser(uploadMsg, "");
		}

		// For Android > 4.1.1
		@SuppressWarnings("unused")
		public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
			openFileChooser(uploadMsg, acceptType);
		}
	}

	// 上传提示
	private void showUpLoadPressBar(final String path, int funcId) {
		showProgressBar(mActivity.getString(R.string.web_image_uping));
		ImageUpLoadTask task = new ImageUpLoadTask(new PicCallBack(funcId));
		task.setLocalFilePath(path);
		task.setUpUrl(IcityWebViewClient.UP_LOAD_PATH);
		task.execute();
	}

	/**
	 * 图片上传回调
	 */
	class PicCallBack implements HttpCallBack<UpLoadImageResp> {

		private int funcId;
		private String image;

		PicCallBack(int funcId) {
			this.funcId = funcId;
		}

		@Override
		public void call(UpLoadImageResp response) {
			hideProgressBar();
			String urlFile = "";
			if (response.isSuccess()) {
				try {
					String filePath = response.getList().get(0).getFilePath();
					if (filePath.toLowerCase(Locale.getDefault()).indexOf("http://") == 0) {
						urlFile = filePath;
					} else {
						urlFile = Config.GET_IMAGE_ROOT_URL() + filePath;
					}
				} catch (Exception e) {
					Log.e(e.getMessage(), e);
					Map<String, Object> map = new HashMap<String, Object>();
					map.put(IcityWebViewHelper.FUNCID, funcId);
					map.put(IcityWebViewHelper.ISSUCCED, false);
					map.put("fileurl", urlFile);
					image = JsonUtil.toJson(map);
					loadUrl("javascript:icity_func('" + image + "')");
					return;
				}
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(IcityWebViewHelper.FUNCID, funcId);
				map.put(IcityWebViewHelper.ISSUCCED, true);
				map.put("fileurl", urlFile);
				image = JsonUtil.toJson(map);
				loadUrl("javascript:icity_func('" + image + "')");
				Toast.makeText(getContext(),
						mActivity.getString(R.string.web_image_upload_success), Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(getContext(), mActivity.getString(R.string.web_image_upload_fail),
						Toast.LENGTH_SHORT).show();
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(IcityWebViewHelper.FUNCID, funcId);
				map.put(IcityWebViewHelper.ISSUCCED, false);
				image = JsonUtil.toJson(map);
				loadUrl("javascript:icity_func('" + image + "')");
			}
		}

		@Override
		public void progress(Object... obj) {
		}

		@Override
		public void onNetWorkError() {
		}
	}

	@SuppressWarnings("unchecked")
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			if (requestCode == SystemCallUtil.REQUESTCODE_CAMERA
					&& resultCode == Activity.RESULT_OK) {
				File f = new File(filePath);
				if (!f.exists()) {
					f.mkdirs();
				}
				SystemCallUtil.ImageCut(mActivity, SystemCallUtil.IMAGE_URI,
						Uri.fromFile(new File(filePath, fileName)));
			} else if (requestCode == SystemCallUtil.REQUESTCODE_PHOTOALBUM
					&& resultCode == Activity.RESULT_OK) {
				Uri uri = data.getData();
				File f = new File(filePath);
				if (!f.exists()) {
					f.mkdirs();
				}
				SystemCallUtil.ImageCut(mActivity, uri, Uri.fromFile(new File(filePath, fileName)));
			} else if (requestCode == SystemCallUtil.REQUESTCODE_IMAGECUT
					&& resultCode == Activity.RESULT_OK) {
				AlertBaseHelper.showConfirm(mActivity, "",
						mActivity.getString(R.string.sure_up_load), new View.OnClickListener() {

							@Override
							public void onClick(View view) {
								AlertBaseHelper.dismissAlert(mActivity);
								showUpLoadPressBar(filePath + fileName, BaseWebViewClient.funcId);
							}
						});
			} else if (requestCode == IcityWebViewHelper.REQUESTCODE_CONTACT
					&& resultCode == Activity.RESULT_OK && data != null) {
				StringBuilder sb = new StringBuilder();// 存储用户号码
				List<Map<String, Object>> list = (List<Map<String, Object>>) data.getExtras()
						.getSerializable("phoneList");
				if (list == null) {
					Log.e("list为空");
					return;
				}
				for (Iterator<Map<String, Object>> it = list.iterator(); it.hasNext();) {
					Map<String, Object> map = it.next();
					if ((Boolean) map.get("checked")) {
						sb.append(map.get("name").toString() + "-" + map.get("phone").toString());
						sb.append(";");
					}
				}
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(IcityWebViewHelper.FUNCID, 9);
				map.put(IcityWebViewHelper.ISSUCCED, true);
				map.put("phone_num", sb.toString());
				String mobileString = JsonUtil.toJson(map);
				loadUrl("javascript:icity_func('" + mobileString + "')");
			} else if (requestCode == SystemCallUtil.REQUESTCODE_CAMERA
					|| requestCode == SystemCallUtil.REQUESTCODE_PHOTOALBUM) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(IcityWebViewHelper.FUNCID, 1);
				map.put(IcityWebViewHelper.ISSUCCED, false);
				String image = JsonUtil.toJson(map);
				loadUrl("javascript:icity_func('" + image + "')");
			} else if (requestCode == IcityWebViewHelper.REQUESTCODE_LOGIN) {
				String login;
				if (data == null) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put(IcityWebViewHelper.FUNCID, 13);
					map.put(IcityWebViewHelper.ISSUCCED, false);
					login = JsonUtil.toJson(map);
				} else {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put(IcityWebViewHelper.FUNCID, 13);
					map.put(IcityWebViewHelper.ISSUCCED,
							data.getBooleanExtra("loginSuccess", false));
					login = JsonUtil.toJson(map);
				}
				loadUrl("javascript:icity_func('" + login + "')");
			} else if (requestCode == IcityWebViewHelper.REQUESTCODE_SCAN) {
				if (resultCode == Activity.RESULT_OK && data != null) {
					final String result = data.getStringExtra("scan_result");
					AlertBaseHelper.showConfirm(mActivity, "", result, "确定", "重新扫描",
							new OnClickListener() {

								@Override
								public void onClick(View v) {
									Map<String, Object> map = new HashMap<String, Object>();
									map.put(IcityWebViewHelper.FUNCID, 22);
									map.put(IcityWebViewHelper.ISSUCCED, true);
									map.put("decode_content", result);
									String resultJson = JsonUtil.toJson(map);
									loadUrl("javascript:icity_func('" + resultJson + "')");
								}
							}, new OnClickListener() {

								@Override
								public void onClick(View v) {
									IcityWebViewHelper.getHelper().scanCode(mActivity);
								}
							});
				} else {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put(IcityWebViewHelper.FUNCID, 22);
					map.put(IcityWebViewHelper.ISSUCCED, false);
					String resultJson = JsonUtil.toJson(map);
					loadUrl("javascript:icity_func('" + resultJson + "')");
				}
			} else if (requestCode == REQUESTCODE_SELECT_FILE) {
				if (null == upLoadMessage)
					return;
				Uri result = data == null || resultCode != Activity.RESULT_OK ? null : data
						.getData();
				upLoadMessage.onReceiveValue(result);
				upLoadMessage = null;
			}
		} catch (Exception e) {
			Log.e("Exception" + e);
		}
	}

	public void onNewIntent(Intent intent) {
//		payCallBack(intent);// 支付回调
	}

	/**
	 * 内部支付回调
	 * @param intent
	 */
//	private void payCallBack(Intent intent) {
//		int payCode = intent.getIntExtra(cn.ffcs.pay.common.Key.K_PAY_RESULT, -1);
//		if (payCode != -1) {
//			if (payCode == 0) {
//				String loginPhone = intent.getStringExtra(cn.ffcs.pay.common.Key.K_LOGIN_PHONE);// 登陆手机
//				String payPhone = intent.getStringExtra(cn.ffcs.pay.common.Key.K_BUYER_PHONE);// 充值手机
//				String price = intent.getStringExtra(cn.ffcs.pay.common.Key.K_ALL_PRICE);// 总金额
//				Serializable serializable = intent
//						.getSerializableExtra(cn.ffcs.pay.common.Key.K_PRODUCT);
//				PayProduct product = (PayProduct) serializable;
//				Map<String, Object> map = new HashMap<String, Object>();
//				map.put(IcityWebViewHelper.FUNCID, 30001);
//				map.put(IcityWebViewHelper.ISSUCCED, true);
//				map.put("result_code", payCode);
//				map.put("all_price", price);
//				map.put("goods_name", product.goods_name);
//				map.put("order_id", product.orderId);
//				map.put("login_phone", loginPhone);
//				map.put("pay_phone", payPhone);
//				map.put("product", product);
//				loadUrl("javascript:icity_func('" + JsonUtil.toJson(map) + "')");
//			} else {
//				Map<String, Object> map = new HashMap<String, Object>();
//				map.put(IcityWebViewHelper.FUNCID, 30001);
//				map.put(IcityWebViewHelper.ISSUCCED, false);
//				map.put("result_code", payCode);
//				loadUrl("javascript:icity_func('" + JsonUtil.toJson(map) + "')");
//			}
//		}
//	}

	public void showProgressBar(String message) {
		LoadingDialog.getDialog(mActivity).setMessage(message).show();
	}

	public void hideProgressBar() {
		LoadingDialog.getDialog(mActivity).cancel();
	}
}
