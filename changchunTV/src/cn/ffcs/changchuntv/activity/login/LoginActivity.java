package cn.ffcs.changchuntv.activity.login;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import cn.ffcs.changchun_base.activity.BaseActivity;
import cn.ffcs.changchuntv.R;
import cn.ffcs.changchuntv.activity.login.bo.ThirdAccountBo;
import cn.ffcs.external.weibo.AccessTokenKeeper;
import cn.ffcs.external.weibo.LogoutAPI;
import cn.ffcs.ui.tools.TopUtil;
import cn.ffcs.widget.LoadingDialog;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.personcenter.ForgetPwdActivity;
import cn.ffcs.wisdom.city.personcenter.adapter.RememberListAdapter;
import cn.ffcs.wisdom.city.personcenter.bo.LoginBo;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.personcenter.entity.Account;
import cn.ffcs.wisdom.city.personcenter.entity.Account.AccountData;
import cn.ffcs.wisdom.city.personcenter.utils.AccountUtil;
import cn.ffcs.wisdom.city.sqlite.model.RememberInfo;
import cn.ffcs.wisdom.city.sqlite.service.RememberInfoService;
import cn.ffcs.wisdom.city.utils.AppUtil;
import cn.ffcs.wisdom.city.utils.Callback;
import cn.ffcs.wisdom.city.utils.CityImageLoader;
import cn.ffcs.wisdom.city.xg.XgManager;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.StringUtil;
import com.ctbri.wxcc.MessageEditor;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.tencent.connect.auth.QQAuth;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

public class LoginActivity extends BaseActivity implements OnClickListener {
	
	private TextView mForgetPwd;// 忘记密码
	private AutoCompleteTextView mMobileEdit;// 手机号码输入框
	private EditText mPasswordEdit;// 密码输入框
	private Button mLogin;// 登录
	private Button register;// 注册
	private LoginBo mLoginBo;
	private String mobile;// 手机
	private String password;// 密码
	private CheckBox rememberPassword;// 记住密码
	private RememberListAdapter adapter;
	public ImageView deleteContent;
	private List<RememberInfo> rememberList;
	private boolean isFocus = false;
	private Button qq;
	private Button weibo;
	private ThirdAccountBo mThirdAccountBo;
	private String qq_appid;
	private String weibo_appKey;
	private String weibo_redirectUrl;
	private QQAuth mQQAuth;
	private Tencent mTencent;
	private WeiboAuth mWeiboAuth;
	private SsoHandler mSsoHandler;
	private Object request;

	@Override
	public int getLayoutId() {
		return R.layout.activity_login;
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		request = AppUtil.getValue(LoginActivity.class.getName());
		initActionBar();
		mForgetPwd = (TextView) findViewById(R.id.forget_password);
		mForgetPwd.setOnClickListener(new OnForgetPwdListener());
		mMobileEdit = (AutoCompleteTextView) findViewById(R.id.mobile);
		mMobileEdit.setDropDownBackgroundResource(android.R.color.transparent);
		mMobileEdit.setDropDownVerticalOffset(1);
		mMobileEdit.addTextChangedListener(new MyWatcher());
		mPasswordEdit = (EditText) findViewById(R.id.password);
		mLogin = (Button) findViewById(R.id.login);
		mLogin.setOnClickListener(new OnLoginClickListener());
		register = (Button) findViewById(R.id.register);
		register.setOnClickListener(new OnRegisterClick());
		rememberPassword = (CheckBox) findViewById(R.id.remember_password);
		deleteContent = (ImageView) findViewById(R.id.delete_content);
		deleteContent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setUserNameAndPassword("", "");
			}
		});
		initEditMobile();
		initEditPasswd();
		
		TopUtil.updateTitle(mActivity, R.id.top_title, R.string.home_login);
		readInfo();
		mMobileEdit.dismissDropDown();
		
		qq = (Button) findViewById(R.id.qq);
		weibo = (Button) findViewById(R.id.weibo);
		qq.setOnClickListener(this);
		weibo.setOnClickListener(this);
		qq_appid = getResources().getString(R.string.social_qq_app_id);
		weibo_appKey = getResources().getString(R.string.social_weibo_app_key);
		weibo_redirectUrl = getResources().getString(R.string.social_weibo_redirect_url);
		mQQAuth = QQAuth.createInstance(qq_appid, getApplicationContext());
		mTencent = Tencent.createInstance(qq_appid, getApplicationContext());
		mWeiboAuth = new WeiboAuth(getApplicationContext(), weibo_appKey, weibo_redirectUrl, "all");
		mSsoHandler = new SsoHandler(LoginActivity.this, mWeiboAuth);
		mThirdAccountBo = new ThirdAccountBo();
		super.init(savedInstanceState);
	}
	
	private void initActionBar() {
		View mReturn = findViewById(R.id.btn_return);
		mReturn.setOnClickListener(this);
		
	}

	/**
	 * 初始化密码输入框
	 */
	private void initEditPasswd() {
		mPasswordEdit.setImeOptions(EditorInfo.IME_ACTION_SEND);
		mPasswordEdit.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEND) {
					mLogin.performClick();
				}
				return true;
			}
		});
	}

	/**
	 * 初始化手机输入框
	 */
	private void initEditMobile() {
		mMobileEdit.setOnItemClickListener(new FilerListItemClick());
		mMobileEdit.setImeOptions(EditorInfo.IME_ACTION_NEXT);
		mMobileEdit.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_NEXT) {
					mPasswordEdit.requestFocus();
				}
				return true;
			}
		});
	}
	
	class MyWatcher implements TextWatcher {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			if (s.toString().length() > 0) {
				deleteContent.setVisibility(View.VISIBLE);
				mPasswordEdit.setText("");
			} else {
				deleteContent.setVisibility(View.GONE);
			}
		}
	}
	
	/**
	 * 选择项点击
	 */
	class FilerListItemClick implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			RememberInfo info = (RememberInfo) parent.getAdapter().getItem(position);
			setUserNameAndPassword(info.userName, info.password);
		}
	}

	/**
	 * 传入密文账密，设置对应值
	 * @param phone
	 * @param passWord
	 */
	private void setUserNameAndPassword(String phone, String passWord) {
		if (!StringUtil.isEmpty(phone) && !StringUtil.isEmpty(passWord)) {
			String userName = new String(Base64.decode(phone, Base64.DEFAULT));
			mMobileEdit.setText(userName);
			mMobileEdit.setSelection(userName.length());
			String password = new String(Base64.decode(passWord, Base64.DEFAULT));
			mPasswordEdit.setText(password);
			mPasswordEdit.setSelection(password.length());
		} else {
			mMobileEdit.setText(phone);
			mPasswordEdit.setText(passWord);
		}
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		isFocus = hasFocus;
		super.onWindowFocusChanged(hasFocus);
	}

	// 注册点击
	class OnRegisterClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent i = new Intent(mActivity, RegisterActivity.class);
			i.putExtra(Key.K_RETURN_TITLE, getString(R.string.home_login));
			i.putExtra(Key.K_IS_FROM_LOGIN, true);
			startActivityForResult(i, 1);
		}
	}
	
	// 忘记密码点击
	class OnForgetPwdListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			String mobile = mMobileEdit.getText().toString().trim();
			Intent intent = new Intent(mActivity, ForgetPwdActivity.class);
			intent.putExtra(Key.K_RETURN_TITLE, getString(R.string.home_login));
			intent.putExtra("mobile", mobile);
			startActivity(intent);
		}
	}

	// 登陆点击
	class OnLoginClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			CommonUtils.hideKeyboard(LoginActivity.this);
			// Animation shake = AnimationUtils.loadAnimation(mContext,
			// R.anim.shake);
			mobile = mMobileEdit.getText().toString().trim();
			password = mPasswordEdit.getText().toString();
			if (StringUtil.isEmpty(mobile)) {
				// CommonUtils.showErrorByEditText(mMobileEdit,
				// getString(R.string.login_mobile_empty), shake);
				CommonUtils.showToast(mActivity,
						getString(R.string.login_mobile_empty),
						Toast.LENGTH_SHORT);
				return;
			}
			if (!CommonUtils.isMobileNoValid(mobile)) {
				// CommonUtils.showErrorByEditText(mMobileEdit,
				// getString(R.string.login_not_mobile_number), shake);
				CommonUtils.showToast(mActivity,
						getString(R.string.login_not_mobile_number),
						Toast.LENGTH_SHORT);
				return;
			}
			if (StringUtil.isEmpty(password)) {
				// CommonUtils.showErrorByEditText(mPasswordEdit,
				// getString(R.string.login_password_empty), shake);
				CommonUtils.showToast(mActivity,
						getString(R.string.login_password_empty),
						Toast.LENGTH_SHORT);
				return;
			}
			if (!CommonUtils.isNetConnectionAvailable(mContext)) {
				CommonUtils.showToast(mActivity, R.string.net_error,
						Toast.LENGTH_SHORT);
				return;
			}
			showProgress();
			login(mobile, password);
		}
	}

	/**
	 * 提交登录
	 */
	private void login(String mobile, String password) {
		if (mLoginBo == null) {
			mLoginBo = new LoginBo(new LoginCallback(), mActivity);
		}
		mLoginBo.login(mobile, password, mContext);
	}
	
	@Override
	protected void onDestroy() {
		dismissProgress();
		if(null != request && request instanceof Callback && null != AccountUtil.readAccountInfo(mContext)){
			try{
				((Callback) request).onData(AccountUtil.readAccountInfo(mContext));
			}catch (Exception e){
				Log.e("LoginCallback", e.getMessage(), e);
			}
		}
		request = null;
		super.onDestroy();
	}

	// 登陆回调
	class LoginCallback implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp response) {
			dismissProgress();
			BaseResp resp = (BaseResp) response;
			if (resp.isSuccess()) {
				Account account = (Account) resp.getObj();
				AccountMgr.getInstance().refresh(mActivity, account); // 刷新帐号
				CommonUtils.showToast(mActivity, R.string.login_success, Toast.LENGTH_SHORT);
				XgManager.xg_register(mContext);
				if (rememberPassword.isChecked()) {
					saveInfo();// 保存个人信息到本地
				} else {
					clearInfo();
				}
				String userId = account.getData().getUserId() + "";
				String phone = account.getData().getMobile();
				CityImageLoader bitmaploader = new CityImageLoader(mContext);
				bitmaploader.setDefaultFailImage(R.drawable.info_userphoto);
				bitmaploader.setIsRealTimeShowImage(false);
				String imgUser = bitmaploader.patternUrl(account.getData().getIconUrl());
				String userName = account.getData().getUserName();
				MessageEditor.initOrUpdateCTBRI(mContext, userId, userName, imgUser, phone);
				Intent i = new Intent();
				i.putExtra("loginSuccess", true);
				setResult(RESULT_OK, i);
				finish();
			} else {
				String msg = resp.getDetailDesc();
				if (!StringUtil.isEmpty(msg)) {
					CommonUtils.showToast(mActivity, msg, Toast.LENGTH_SHORT);
				} else {
					CommonUtils.showToast(mActivity, R.string.login_error, Toast.LENGTH_SHORT);
				}
			}
		}

		@Override
		public void progress(Object... obj) {
		}

		@Override
		public void onNetWorkError() {
			CommonUtils.showToast(mActivity, R.string.login_error, Toast.LENGTH_SHORT);
		}
	}

	/**
	 *  保存个人信息
	 */
	private void saveInfo() {
		RememberInfoService.getInstance(mContext).saveUserInfo(
				Base64.encodeToString(mobile.getBytes(), Base64.DEFAULT),
				Base64.encodeToString(password.getBytes(), Base64.DEFAULT));
	}

	/**
	 * 获取全部保存的账号的密码,并设置默认值
	 */
	private void readInfo() {
		rememberList = RememberInfoService.getInstance(mContext).getAllRemember();
		adapter = new RememberListAdapter(mActivity, rememberList);
		mMobileEdit.setAdapter(adapter);
		if (rememberList != null && rememberList.size() > 0) {
			setUserNameAndPassword(rememberList.get(0).userName, rememberList.get(0).password);
		}
	}

	/**
	 * 清除保存的密码
	 */
	private void clearInfo() {
		RememberInfoService.getInstance(mContext).deleteByPhone(
				Base64.encodeToString(mobile.getBytes(), Base64.DEFAULT));
	}
	
	private void showProgress() {
		if (isFocus) {
			LoadingDialog.getDialog(mActivity).setMessage(getString(R.string.login_process)).show();
		}else {
			if (mActivity != null) {
				LoadingDialog.getDialog(mActivity).setMessage(getString(R.string.login_process)).show();
			}
		}
	}

	private void dismissProgress() {
		if (isFocus) {
			LoadingDialog.getDialog(mActivity).cancel();
		} else {
			if (mActivity != null) {
				LoadingDialog.getDialog(mActivity).cancel();
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.qq) {
			qqLogin();
		} else if (v.getId() == R.id.weibo) {
			weiboLogin();
		} else if (v.getId() == R.id.btn_return) {
			finish();
		}
		
	}
	
	private void qqLogin() {
		IUiListener listener = new IUiListener() {
			
			@Override
			public void onError(UiError error) {
				Toast.makeText(getApplicationContext(), "登录失败",
						Toast.LENGTH_SHORT).show();
				
			}
			
			@Override
			public void onComplete(Object obj) {
				// TODO Auto-generated method stub
				JSONObject response = (JSONObject) obj;
				try {
					showProgress();
					String openid = response.getString("openid");
					Map<String, String> map = new HashMap<String, String>();
					map.put("mobile", "unknown");
					String cityCode = MenuMgr.getInstance().getCityCode(mContext);
					map.put("cityCode", cityCode);
					map.put("orgCode", cityCode);
					map.put("longitude", "unknown");
					map.put("latitude", "unknown");
					String type = "1";
					map.put("type", type);
					map.put("relaKey", openid);
					String sign = type;
					mThirdAccountBo.isFirstLogin(new IsFirstLoginCallback(type, openid), mContext, map, sign);
					mTencent.logout(LoginActivity.this);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			@Override
			public void onCancel() {
				// TODO Auto-generated method stub
				
			}
		};
//		mTencent.isSupportSSOLogin(mActivity);
//		mTencent.login(this, "all", listener);
		mTencent.login(this, "get_user_info,get_simple_userinfo,get_user_profile", listener);
		
	}

	private void weiboLogin() {
		WeiboAuthListener listener = new WeiboAuthListener() {
			
			@Override
			public void onWeiboException(WeiboException e) {
				Toast.makeText(getApplicationContext(), "登录失败",
						Toast.LENGTH_SHORT).show();
				
			}
			
			@Override
			public void onComplete(Bundle values) {
				Oauth2AccessToken mAccessToken = Oauth2AccessToken. parseAccessToken(values);
				if (mAccessToken.isSessionValid()) {
					AccessTokenKeeper.writeAccessToken(LoginActivity.this, mAccessToken);
					showProgress();
					String uid = mAccessToken.getUid();
					Map<String, String> map = new HashMap<String, String>();
					map.put("mobile", "unknown");
					String cityCode = MenuMgr.getInstance().getCityCode(mContext);
					map.put("cityCode", cityCode);
					map.put("orgCode", cityCode);
					map.put("longitude", "unknown");
					map.put("latitude", "unknown");
					String type = "2";
					map.put("type", type);
					map.put("relaKey", uid);
					String sign = type;
					mThirdAccountBo.isFirstLogin(new IsFirstLoginCallback(type, uid), mContext, map, sign);
					new LogoutAPI(LoginActivity.this, weibo_appKey, mAccessToken).logout(new LogOutRequestListener());
				}
				
			}
			
			@Override
			public void onCancel() {
				// TODO Auto-generated method stub
				
			}
		};
		IWeiboShareAPI api = WeiboShareSDK.createWeiboAPI(mActivity, weibo_appKey);
		api.registerApp();
		if (api.checkEnvironment(true)) { // mWeiboShareAPI.isWeiboAppInstalled();
//			mWeiboAuth.anthorize(listener);
			mSsoHandler.authorize(listener);
		}
		
	}
	
	/**
     * 注销按钮的监听器，接收注销处理结果。（API请求结果的监听器）
     */
    private class LogOutRequestListener implements RequestListener {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                try {
                    JSONObject obj = new JSONObject(response);
                    String value = obj.getString("result");
                    
                    if ("true".equalsIgnoreCase(value)) {
                        AccessTokenKeeper.clear(LoginActivity.this);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } 

        @Override
        public void onWeiboException(WeiboException e) {
        	
        }
    }
	
	// 第三方登陆回调
	class IsFirstLoginCallback implements HttpCallBack<BaseResp> {
		
		String type;
		String relaKey;

		public IsFirstLoginCallback(String type, String relaKey) {
			super();
			this.type = type;
			this.relaKey = relaKey;
		}

		@Override
		public void call(BaseResp response) {
			if (response.isSuccess()) {
				try {
					JSONObject object = new JSONObject(response.getData());
					if (object.getString("is_first_login").equals("1")) { // 首次登陆
						dismissProgress();
						Intent i = new Intent(mActivity, BindingActivity.class);
						i.putExtra("type", type);
						i.putExtra("relaKey", relaKey);
						startActivityForResult(i, 2);
					} else if (object.getString("is_first_login").equals("0")) {
						mobile = object.getString("username");
						password = object.getString("password");
						login(mobile, password);
					}
				} catch (JSONException e) {
					dismissProgress();
					CommonUtils.showToast(mActivity, R.string.login_error, Toast.LENGTH_SHORT);
					e.printStackTrace();
				}
			} else {
				dismissProgress();
				String msg = response.getDetailDesc();
				if (!StringUtil.isEmpty(msg)) {
					CommonUtils.showToast(mActivity, msg, Toast.LENGTH_SHORT);
				} else {
					CommonUtils.showToast(mActivity, R.string.login_error, Toast.LENGTH_SHORT);
				}
			}
		}

		@Override
		public void progress(Object... obj) {
		}

		@Override
		public void onNetWorkError() {
			CommonUtils.showToast(mActivity, R.string.login_error, Toast.LENGTH_SHORT);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			AccountData accountData = AccountMgr.getInstance().getAccount(mContext).getData();
			String userId = accountData.getUserId() + "";
			String phone = accountData.getMobile();
			CityImageLoader bitmaploader = new CityImageLoader(mContext);
			bitmaploader.setDefaultFailImage(R.drawable.info_userphoto);
			bitmaploader.setIsRealTimeShowImage(false);
			String imgUser = bitmaploader.patternUrl(accountData.getIconUrl());
			String userName = accountData.getUserName();
			MessageEditor.initOrUpdateCTBRI(mContext, userId, userName, imgUser, phone);
			if (requestCode == 1) {//注册
				Intent i = new Intent();
				i.putExtra("loginSuccess", true);
				i.putExtra("isFromRegister", true);
				setResult(RESULT_OK, i);
				finish();
				return;
			} else if (requestCode == 2) {//绑定
				Intent i = new Intent();
				i.putExtra("loginSuccess", true);
				setResult(RESULT_OK, i);
				finish();
				return;
			}
		}
		if (mSsoHandler != null) {
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}


}
