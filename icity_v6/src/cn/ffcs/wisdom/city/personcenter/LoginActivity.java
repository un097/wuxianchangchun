package cn.ffcs.wisdom.city.personcenter;

import java.util.List;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import cn.ffcs.ui.tools.TopUtil;
import cn.ffcs.widget.LoadingDialog;
import cn.ffcs.wisdom.city.WisdomCityActivity;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.personcenter.adapter.RememberListAdapter;
import cn.ffcs.wisdom.city.personcenter.bo.LoginBo;
import cn.ffcs.wisdom.city.personcenter.bo.QueryRelevanceBo;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.personcenter.entity.Account;
import cn.ffcs.wisdom.city.personcenter.utils.AccountUtil;
import cn.ffcs.wisdom.city.sqlite.model.RememberInfo;
import cn.ffcs.wisdom.city.sqlite.service.RememberInfoService;
import cn.ffcs.wisdom.city.utils.AppUtil;
import cn.ffcs.wisdom.city.utils.Callback;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.city.xg.XgManager;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.StringUtil;

/**
 * <p>Title: 登录页面       </p>
 * <p>Description: 
 * 登录，找回密码
 * </p>
 * <p>@author: Leo                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-2-26             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class LoginActivity extends WisdomCityActivity {
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
	private Object request;

	@Override
	protected void initComponents() {
		request = AppUtil.getValue(LoginActivity.class.getName());
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
			startActivity(i);
			finish();
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
			mobile = mMobileEdit.getText().toString().trim();
			password = mPasswordEdit.getText().toString();
			if (StringUtil.isEmpty(mobile)) {
				CommonUtils.showToast(mActivity, getString(R.string.login_mobile_empty), Toast.LENGTH_SHORT);
				return;
			}
			if (!CommonUtils.isMobileNoValid(mobile)) {
				CommonUtils.showToast(mActivity, getString(R.string.login_not_mobile_number), Toast.LENGTH_SHORT);
				return;
			}
			if (StringUtil.isEmpty(password)) {
				CommonUtils.showToast(mActivity, getString(R.string.login_password_empty), Toast.LENGTH_SHORT);
				return;
			}
			if (!CommonUtils.isNetConnectionAvailable(mContext)) {
				CommonUtils.showToast(mActivity, R.string.net_error, Toast.LENGTH_SHORT);
				return;
			}
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
		showProgress();
		mLoginBo.login(mobile, password, mContext);
	}

	@Override
	protected void onDestroy() {
		dismissProgress();
		if(null != request && request instanceof Callback && null != AccountMgr.getInstance().getAccount_1(mContext)){
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
				XgManager.xg_register(mContext);
				CommonUtils.showToast(mActivity, R.string.login_success, Toast.LENGTH_SHORT);
				new QueryRelevanceBo(mContext).startGetWzRelevance();
				if (rememberPassword.isChecked()) {
					saveInfo();// 保存个人信息到本地
				} else {
					clearInfo();
				}
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

	@Override
	protected void initData() {
		TopUtil.updateTitle(mActivity, R.id.top_title, R.string.home_login);
		readInfo();
		mMobileEdit.dismissDropDown();
	}

	@Override
	protected int getMainContentViewId() {
		return R.layout.act_login;
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

//	@Override
//	public void finish() {
//		isFocus = false;
//		// modify by linjiafu 2014-09-12 修改703点击登录页面返回后，个人中心状态修改
//		setResult(RESULT_OK);
//		super.finish();
//	}
}
