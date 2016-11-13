package cn.ffcs.wisdom.city.modular.query.widget;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;
import org.kobjects.base64.Base64;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
//import cn.ffcs.external.watercoal.common.Constants;
//import cn.ffcs.external.watercoal.common.K;
import cn.ffcs.ui.tools.AlertBaseHelper;
import cn.ffcs.wisdom.base.BaseTask;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.modular.query.QueryInfoActivity;
import cn.ffcs.wisdom.city.modular.query.ViewFactory;
import cn.ffcs.wisdom.city.modular.query.emuns.ViewType;
import cn.ffcs.wisdom.city.modular.query.entity.DataArray;
import cn.ffcs.wisdom.city.modular.query.entity.QueryInfo;
import cn.ffcs.wisdom.city.modular.query.entity.ViewConfig;
import cn.ffcs.wisdom.city.modular.query.views.AgreementCheck;
import cn.ffcs.wisdom.city.modular.query.views.CheckImageView;
import cn.ffcs.wisdom.city.modular.query.views.CheckImageView.OnNumberClickListener;
import cn.ffcs.wisdom.city.modular.query.views.ListButton;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.personcenter.task.GetQueryRelevanceTask;
import cn.ffcs.wisdom.city.resp.QueryRelevanceResp;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.city.web.BrowserActivity;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.http.HttpRequest;
import cn.ffcs.wisdom.tools.StringUtil;

public class QueryLayout extends LinearLayout implements OnClickListener {

	public static final String KEY_SP_ACCOUNT = "key_sp_account_";

	private boolean isRequest;

	private View mMainLayout;

	private LinearLayout mViewsLayer;

	private View mYes;
	private Button mClear;

	private ProgressBar mSearchProgress;

	private TextView mBottomTip;

	private ImageView mQueryTip;

	private QueryInfo mQueryInfo;

	private CheckImageView mCheckImage;
	//private RemainAccount remainAccount;

	AgreementCheck mAgreement;
	private String checkUrl;
	private boolean checkLoading;

	HttpRequest request;
	private String cookie;

	private QueryInfoActivity mActivity;

	public void setQueryInfoActivity(QueryInfoActivity activity) {
		this.mActivity = activity;
	}

	public QueryLayout(Context context) {
		this(context, null);
	}

	public QueryLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

		request = new HttpRequest(BaseResp.class);

		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMainLayout = mInflater.inflate(R.layout.widget_query_info, null);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		addView(mMainLayout, params);

		mYes = findViewById(R.id.query_yes);
		mClear = (Button) findViewById(R.id.query_clear);
		mBottomTip = (TextView) findViewById(R.id.query_bottom_tip);
		mQueryTip = (ImageView) findViewById(R.id.query_tip);
		mQueryTip.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// AlertHelper.showMessage(getContext(),
				// mQueryInfo.getQueryTipInfo());
				AlertDialog alertDlg = new AlertDialog.Builder(getContext()).create();
				alertDlg.setTitle("提示");
				alertDlg.setMessage(mQueryInfo.getQueryTipInfo());
				DialogInterface.OnClickListener click = null;
				alertDlg.setButton(DialogInterface.BUTTON_POSITIVE, "确定", click);
				alertDlg.show();
			}
		});

		mSearchProgress = (ProgressBar) findViewById(R.id.query_progress);

		mClear.setOnClickListener(this);
		mYes.setOnClickListener(this);

		mViewsLayer = (LinearLayout) findViewById(R.id.query_views_layer);

	}

	public boolean isRequest() {
		return isRequest;
	}

	public void setIsRequest(boolean isRequest) {
		this.isRequest = isRequest;
	}

	public String getAppId() {
		if (mQueryInfo != null) {
			return mQueryInfo.getAppId();
		}
		return "";
	}

	public String getRequestUrl() {
		if (mQueryInfo != null) {
			return mQueryInfo.getRequestUrl();
		}
		return "";
	}

	public void init(QueryInfo queryInfo, int keyGroupId) {

		if (queryInfo == null)
			return;
		this.mQueryInfo = queryInfo;
		checkUrl = mQueryInfo.getRandomQueryUrl();

		drawViews(keyGroupId);

		drawBottomInfo();

		if (StringUtil.isEmpty(mQueryInfo.getQueryTipInfo()))
			mQueryTip.setVisibility(View.GONE);

	}

	public void drawViews(final int keyGroupId) {

		List<ViewConfig> list = mQueryInfo.getQueryInfo();

		if (list == null || list.size() == 0)
			return;

		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.topMargin = 10;

		for (int i = 0; i < list.size(); i++) {
			ViewConfig config = list.get(i);
			ViewType type = ViewType.getViewType(config.getInputType());
			if (type == null)
				continue;

			View v = ViewFactory.createView(getContext(), type);
			if (v == null)
				continue;

			if (v instanceof QueryViewBinder) {
				QueryViewBinder binder = (QueryViewBinder) v;
				if (!StringUtil.isEmpty(config.getQueryPlaceholder())) {
					binder.setMyHint(config.getQueryPlaceholder());
				} else {
					binder.setMyHint(config.getQueryInputTitle());
				}

				if (i == 0) {
					binder.setMyFocus(true);
				}

				binder.setFieldName(config.getFieldName());
				binder.setMustInput(config.getIsMustInput());
				binder.setViewTitle(config.getQueryInputTitle());
				binder.setDefaultValue(config.getQueryDefault());
				binder.setMyTextSize(16);
				binder.setMyTextColor(getContext().getResources().getColor(R.color.gray_b4b4b4));
				binder.setMyHintTextColor(getContext().getResources().getColor(R.color.gray_b4b4b4));
			}

			if (v instanceof TextView) {
				v.setBackgroundResource(R.drawable.input_background);
			}

			if (v instanceof ListButton) {
				v.setBackgroundResource(R.drawable.input_background);

				ListButton listButton = (ListButton) v;
				listButton.setGravity(Gravity.CENTER_VERTICAL);
				List<DataArray> datas = config.getQueryInputDataInfo();
				if (datas != null) {
					int size = datas.size();
					String[] items = new String[size];
					String[] itemValues = new String[size];
					for (int j = 0; j < datas.size(); j++) {
						items[j] = datas.get(j).getDescript();
						itemValues[j] = datas.get(j).getData();
					}
					listButton.setList(items, itemValues);

					listButton.setListTitle(config.getQueryInputTitle());
				}
			}

			if (v instanceof CheckImageView) {
				mCheckImage = (CheckImageView) v;
				mCheckImage.setBackground(R.drawable.input_background);
				mCheckImage.setOnNumberClickListener(new OnCheckImageClick());
				random();
			}

			v.setLayoutParams(params);
			mViewsLayer.addView(v);
		}

		//drawRemainAccount(mQueryInfo, params, keyGroupId);

		drawAgreement(mViewsLayer, params);

		GetQueryRelevanceTask relevance = new GetQueryRelevanceTask(getContext(),
				new HttpCallBack<QueryRelevanceResp>() {
					@Override
					public void call(QueryRelevanceResp response) {
						if (response != null && response.isSuccess()) {
							int count = mViewsLayer.getChildCount();
							for (int i = 0; i < count; i++) {
								View v = mViewsLayer.getChildAt(i);
								if (v instanceof QueryViewBinder) {
									QueryViewBinder binder = (QueryViewBinder) v;
									binder.setDefaultValue(response.getValue(binder.getFieldName(),
											keyGroupId));
								}
							}
						}
					}

					@Override
					public void progress(Object... obj) {
					}

					@Override
					public void onNetWorkError() {
					}
				});
		relevance.execute(mQueryInfo.getItem_id());
	}

	private void drawAgreement(ViewGroup parent, LayoutParams params) {
		mAgreement = new AgreementCheck(getContext());
		mAgreement.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int id = v.getId();
				if (id == R.id.agreement) {
//					Intent intent = new Intent();
//					intent.setClass(mActivity, AgreementActivity.class);
//					mActivity.startActivity(intent);
					Intent i = new Intent();
					i.setClass(mActivity, BrowserActivity.class);
//					i.putExtra(K.U_BROWSER_URL, Constants.URL_USER_NOTE);
//					i.putExtra(K.U_BROWSER_TITLE, "服务协议");
					mActivity.startActivity(i);
				}
			}
		});

		parent.addView(mAgreement, params);
	}

//	private void drawRemainAccount(QueryInfo info, LayoutParams params,
//			final int keyGroupId) {
//
//		if (info == null)
//			return;
//
//		remainAccount = new RemainAccount(getContext());
//		remainAccount.setLayoutParams(params);
//		mViewsLayer.addView(remainAccount);
//		if (info.getIsRemainAccount()) {
//			remainAccount.showRemainAccount();
//			showLocalAccount();
//		}
//
//		if (info.getIsRemainPwd()) {
//			remainAccount.showRemainPwd();
//		}
//
//		if (info.getIsRemainAccount() || info.getIsRemainPwd()) {
//			
//		}
//	}

//	private void setLocalAccount() {
//		SharedPreferencesUtil.setValue(getContext(), KEY_SP_ACCOUNT
//				+ mQueryInfo.getAppId(), getAccountValue());
//	}

//	private String getLocalAccount() {
//		return SharedPreferencesUtil.getValue(getContext(), KEY_SP_ACCOUNT
//				+ mQueryInfo.getAppId());
//	}

//	private void showLocalAccount() {
//		String account = getLocalAccount();
//		setAccountValue(account);
//	}

	public String getAccountValue() {
		View v = mViewsLayer.getChildAt(0);
		if (v instanceof QueryViewBinder) {
			QueryViewBinder binder = (QueryViewBinder) v;
			return binder.getValue();
		}
		return null;
	}

	public void setAccountValue(String value) {
		View v = mViewsLayer.getChildAt(0);
		if (v instanceof QueryViewBinder) {
			QueryViewBinder binder = (QueryViewBinder) v;
			binder.setDefaultValue(value);
		}
	}

//	public boolean isRemainAccount() {
//		if (mQueryInfo != null && mQueryInfo.getIsRemainAccount()) {
//			return remainAccount.isAccountChecked();
//		}
//		return false;
//	}

	public void drawBottomInfo() {
		mBottomTip.setText(mQueryInfo.getQueryBottomInfo());
	}

	public void setSearchProgressVisibility(int visibility) {
		mSearchProgress.setVisibility(visibility);
	}

	public void queryEnd() {
		setIsRequest(false);
		setSearchProgressVisibility(View.GONE);
	}

	public void queryStart() {
		setIsRequest(true);
		setSearchProgressVisibility(View.VISIBLE);
	}

	public void query() {

		if (isRequest) {
			return;
		}

		if (!mAgreement.isChecked()) {
			AlertBaseHelper.showConfirm(mActivity, "温馨提示", "请阅读完相关协议", "马上去", "知道了",
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent i = new Intent();
							i.setClass(mActivity, BrowserActivity.class);
//							i.putExtra(K.U_BROWSER_URL, Constants.URL_USER_NOTE);
//							i.putExtra(K.U_BROWSER_TITLE, "服务协议");
							mActivity.startActivity(i);
						}
					}, new OnClickListener() {

						@Override
						public void onClick(View v) {
							mAgreement.setChecked(true);
						}
					});
			return;
		}

		queryStart();

		String url = mQueryInfo.getQueryUrl();
		Map<String, String> params = getFields();
		String appId = getAppId();
		params.put("appId", appId);
		params.put("item_id", mQueryInfo.getItem_id());
		params.put("mobile", AccountMgr.getInstance().getMobile(getContext()));
		params.put("isPrimaryChk", "1");
		params.put("isPrivateChk", "1");

//		if (remainAccount.isAccountChecked()) { // 记住帐号
//			params.put("isPrimaryChk", "1");
//		} else {
//			params.put("isPrimaryChk", "0");
//		}
//
//		if (remainAccount.isPwdChecked()) { // 记住密码
//			params.put("isPrivateChk", "1");
//		} else {
//			params.put("isPrivateChk", "0");
//		}

		QueryResultTask task = new QueryResultTask(new HttpCallBack<BaseResp>() {

			@Override
			public void call(BaseResp response) {
				queryEnd();

				if (response.isSuccess()) {
					try {
						String result = response.getHttpResult();
						if (!StringUtil.isEmpty(result)) {
							JSONObject json = new JSONObject(result);

							String resultCode = json.getString("resultCode");

							if ("0".equals(resultCode)) {

//										if (isRemainAccount()) { // 查询成功，记住帐号
//											setLocalAccount();
//										}
								String url = json.getString("url");
								Intent intent = new Intent();
								url += "&item_id=" + mQueryInfo.getItem_id();
								url += "&mobile="
										+ AccountMgr.getInstance().getMobile(getContext());

								intent.setClass(getContext(), BrowserActivity.class);
								intent.putExtra(Key.U_BROWSER_URL, url);
								intent.putExtra(Key.U_BROWSER_COOKIES, cookie);
								intent.putExtra(Key.U_BROWSER_TITLE, mQueryInfo.getQueryTitle());
								intent.putExtra(Key.U_BROWSER_QUERY, "1");
								getContext().startActivity(intent);
							} else {
								String desc = json.getString("desc");
								if(StringUtil.isEmpty(desc) || "null".equals(desc)) {
									desc = "查询失败，请稍后再试！";
								}
								Toast.makeText(getContext(), desc, Toast.LENGTH_SHORT).show();
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				// random(); // 查询完，重新刷新验证码
			}

			@Override
			public void progress(Object... obj) {
			}

			@Override
			public void onNetWorkError() {
			}
		}, request, url);
		task.setParams(params);
		task.execute();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.query_yes) {
			if (!verify()) {
				return;
			}

			query();

		} else if (id == R.id.query_clear) {
			clearValue();
		}
	}

	public boolean verify() {

		// 验证是否为空
		int count = mViewsLayer.getChildCount();
		for (int i = 0; i < count; i++) {
			View v = mViewsLayer.getChildAt(i);
			if (v instanceof QueryViewBinder) {
				QueryViewBinder binder = (QueryViewBinder) v;
				if (binder.mustInput() && binder.isEmpty()) {
					binder.verifyFail();
					return false;
				}
			}
		}
		return true;
	}

	public void clearValue() {
		int count = mViewsLayer.getChildCount();
		for (int i = 0; i < count; i++) {
			View v = mViewsLayer.getChildAt(i);
			if (v instanceof QueryViewBinder) {
				QueryViewBinder binder = (QueryViewBinder) v;
				binder.clearValue();
			}
		}
	}

	private Map<String, String> getFields() {
		int count = mViewsLayer.getChildCount();
		Map<String, String> fields = new HashMap<String, String>();
		for (int i = 0; i < count; i++) {
			View v = mViewsLayer.getChildAt(i);
			if (v instanceof QueryViewBinder) {
				QueryViewBinder binder = (QueryViewBinder) v;
				fields.put(binder.getFieldName(), binder.getValue());
			}
		}
		return fields;
	}

	class QueryResultTask extends BaseTask<Void, Void, BaseResp> {

		private String url;
		private Map<String, String> params;

		public QueryResultTask(HttpCallBack<BaseResp> iCall, HttpRequest request, String url) {
			super(iCall);
			this.url = url;
		}

		public void setParams(Map<String, String> params) {
			this.params = params;
		}

		@Override
		protected BaseResp doInBackground(Void... arg0) {
			BaseResp resp = new BaseResp();
			resp.setResult(request(url, params));
			return resp;
		}

	}

	class CheckImageTask extends BaseTask<Void, Void, BaseResp> {

		public CheckImageTask(HttpCallBack<BaseResp> iCall) {
			super(iCall);
		}

		@Override
		protected BaseResp doInBackground(Void... params) {
			BaseResp resp = new BaseResp();
			cookieStore = new BasicCookieStore();
			resp.setResult(request(checkUrl, null));
			return resp;
		}
	}

	class CheckImageCallBack implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp response) {
			try {
				String result = response.getHttpResult();
				if (!StringUtil.isEmpty(result)) {
					JSONObject json = new JSONObject(result);
					String status = json.getString("resultCode");
					if ("0".equals(status)) {
						String imageStream = json.getString("imageStream");
						byte[] resulte = Base64.decode(imageStream.trim());
						ByteArrayInputStream in = new ByteArrayInputStream(resulte);
						Bitmap bitmap = BitmapFactory.decodeStream(in);
						mCheckImage.setImageBitmap(bitmap);

					}
				}
				mCheckImage.showNumber();
				checkLoading = false;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void progress(Object... obj) {
		}

		@Override
		public void onNetWorkError() {
		}
	}

	class OnCheckImageClick implements OnNumberClickListener {

		@Override
		public void onClick(View v) {
			random();
		}
	}

	private void random() {
		if (!checkLoading) {
			checkLoading = true;
			CheckImageTask task = new CheckImageTask(new CheckImageCallBack());
			task.execute();
		}
	}

	// private QueryLayoutListener mListener;
	//
	// public void setQueryLayoutListener(QueryLayoutListener listener) {
	// this.mListener = listener;
	// }

	private CookieStore cookieStore = new BasicCookieStore();
	HttpContext localContext = new BasicHttpContext();;
	HttpClient client;
	String sessionid = "";

	public String request(String url, Map<String, String> params) {

		client = getNewHttpClient(getContext());

		List<NameValuePair> list = new ArrayList<NameValuePair>();
		if (params != null && params.size() > 0) {

			Set<String> keys = params.keySet();
			Iterator<String> it = keys.iterator();
			while (it.hasNext()) {
				String key = it.next();
				list.add(new BasicNameValuePair(key, params.get(key)));
			}
			url = prepareUrl(url, list);
		}
		try {
			HttpGet httpget = new HttpGet();

			httpget.setURI(URI.create(url));

			HttpResponse response = client.execute(httpget, localContext);
			HttpEntity entity = response.getEntity();
			List<Cookie> cookies = cookieStore.getCookies();
			for (int i = 0; i < cookies.size(); i++) {
				String cookieName = cookies.get(i).getName();
				if ("JSESSIONID".equals(cookieName)) {
					cookie = "JSESSIONID=" + cookies.get(i).getValue();
				}
			}
			try {
				InputStream in = (InputStream) entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				StringBuilder str = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					str.append(line + "\n");
				}
				in.close();
				String message = str.toString();
				return message;
			} catch (IllegalStateException exc) {
				exc.printStackTrace();
			}

		} catch (Exception e) {
		} finally {
			// When HttpClient instance is no longer needed,
			// shut down the connection manager to ensure
			// immediate deallocation of all system resources
			// client.getConnectionManager().shutdown();
		}
		return "";
	}

	private static final String URL_SEPARATOR = "?";
	private static final String PARAMETER_SEPARATOR = "&";
	private static final String NAME_VALUE_SEPARATOR = "=";

	private String prepareUrl(String url, List<NameValuePair> params) {
		if (params == null)
			params = new ArrayList<NameValuePair>();
		StringBuffer sb = new StringBuffer(url);
		for (int i = 0; i < params.size(); i++) {
			NameValuePair pair = params.get(i);

			if (i == 0) {
				if (url.endsWith(URL_SEPARATOR)) {
					parameterJoin(sb, "", pair);
				} else {
					parameterJoin(sb, URL_SEPARATOR, pair);
				}
			} else {
				parameterJoin(sb, PARAMETER_SEPARATOR, pair);
			}
			// if (url.endsWith(URL_SEPARATOR)) {
			// parameterJoin(sb, "", pair);
			// } else {
			// if (i == 0)
			// parameterJoin(sb, URL_SEPARATOR, pair);
			//
			// }
		}
		return sb.toString();
	}

	protected void parameterJoin(StringBuffer sb, String join, NameValuePair pair) {
		sb.append(join + pair.getName() + NAME_VALUE_SEPARATOR + pair.getValue());
	}

	public HttpClient getNewHttpClient(Context context) {
		try {

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
			HttpConnectionParams.setStaleCheckingEnabled(params, false);

			HttpConnectionParams.setConnectionTimeout(params, 4 * 1000);
			// HttpConnectionParams.setSoTimeout(params, 5 * 1000);
			// HttpConnectionParams.setSocketBufferSize(params, 8192);

			// HttpClientParams.setRedirecting(params, false);

			SchemeRegistry registry = new SchemeRegistry();

			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			// registry.register(new Scheme("https",
			// SSLCertificateSocketFactory.getHttpSocketFactory(
			// 10 * 60 * 1000, sslSession), 444));
			// registry.register(new Scheme("https",
			// SSLSocketFactory.getSocketFactory(), 444));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

			return new DelegateHttpClient(ccm, params);

		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}

	private class DelegateHttpClient extends DefaultHttpClient {

		private DelegateHttpClient(ClientConnectionManager ccm, HttpParams params) {
			super(ccm, params);
		}

		@Override
		protected HttpContext createHttpContext() {

			HttpContext context = new BasicHttpContext();
			context.setAttribute(ClientContext.AUTHSCHEME_REGISTRY, getAuthSchemes());
			context.setAttribute(ClientContext.COOKIESPEC_REGISTRY, getCookieSpecs());
			context.setAttribute(ClientContext.CREDS_PROVIDER, getCredentialsProvider());

			context.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

			return context;
		}
	}

}
