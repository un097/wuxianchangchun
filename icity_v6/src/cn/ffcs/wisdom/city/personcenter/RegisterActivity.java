package cn.ffcs.wisdom.city.personcenter;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.ffcs.ui.tools.TopUtil;
import cn.ffcs.widget.LoadingDialog;
import cn.ffcs.wisdom.city.WisdomCityActivity;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.personcenter.bo.GetVerifyCodeBo;
import cn.ffcs.wisdom.city.personcenter.bo.LoginBo;
import cn.ffcs.wisdom.city.personcenter.bo.RegisterBo;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.personcenter.entity.Account;
import cn.ffcs.wisdom.city.utils.LoginPwdEncrypter;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.city.xg.XgManager;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.StringUtil;

/**
 * <p>Title: 注册         </p>
 * <p>Description: 
 * </p>
 * <p>@author: Leo                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-2-27             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class RegisterActivity extends WisdomCityActivity {
	private Animation shake;
	private EditText mUserName; // 用户名
	private EditText mPassword; // 密码
	private EditText mRePassword; // 确认密码
	private EditText mEmail; // 邮箱
	private EditText mMobile; // 手机号
	private EditText mVerityCode; // 验证码
	private Button mVerifyBtn; // 获取验证码
	private TextView mRegisterTip;// 验证码发送提示
	private Account account = new Account();
	private Account.AccountData data = account.new AccountData();
	private ImageView mRegister;
	private int recycleLen = 60;// 倒计时
	private LoginBo mLoginBo;
	private boolean isFromLogin = false;

	private void showProgress(String msg) {
		LoadingDialog.getDialog(mActivity).setMessage(msg).show();
	}

	private void dismissProgress() {
		LoadingDialog.getDialog(mActivity).cancel();
	}

	@Override
	protected void initComponents() {
		mRegister = (ImageView) findViewById(R.id.top_right);
		mRegister.setOnClickListener(new OnRegister());
		mMobile = (EditText) findViewById(R.id.register_mobile);
		mVerityCode = (EditText) findViewById(R.id.register_verifycode);
		mUserName = (EditText) findViewById(R.id.register_username);
		mPassword = (EditText) findViewById(R.id.register_password);
		mRePassword = (EditText) findViewById(R.id.register_password_again);
		mEmail = (EditText) findViewById(R.id.register_email);
		mVerifyBtn = (Button) findViewById(R.id.register_verifyBtn);
		mVerifyBtn.setOnClickListener(new OnVerifyCode());
		mRegisterTip = (TextView) findViewById(R.id.register_tip);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dismissProgress();
	}

	class OnVerifyCode implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (shake == null) {
				shake = AnimationUtils.loadAnimation(mContext, R.anim.shake);
			}
			String mobile = mMobile.getText().toString().trim();
			if (StringUtil.isEmpty(mobile)) {
				CommonUtils.showErrorByEditText(mMobile, getString(R.string.register_mobile_empty),
						shake);
				return;
			}
			if (!CommonUtils.isMobileNoValid(mobile)) {
				CommonUtils.showErrorByEditText(mMobile, R.string.login_not_mobile_number, shake);
				return;
			}
			if (!CommonUtils.isNetConnectionAvailable(mContext)) {
				CommonUtils.showToast(mActivity, R.string.net_error, Toast.LENGTH_SHORT);
				return;
			}
			showProgress(getString(R.string.register_get_verify_running));
			GetVerifyCodeBo verifyBo = new GetVerifyCodeBo(new VerifyCallback(), mContext);
			verifyBo.VerifyCode(mobile);
		}

	}

	class VerifyCallback implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp response) {
			dismissProgress();
			if (response.isSuccess()) {
				handler.postDelayed(runnable, 1000);
				mVerifyBtn.setTextColor(getResources().getColor(R.color.gray));
				mVerifyBtn.setEnabled(false);
				mVerifyBtn.setText(R.string.register_get_verifycode_again);
				CommonUtils.showToast(mActivity, R.string.register_get_verify_code_success,
						Toast.LENGTH_SHORT);
			} else {
				if (!StringUtil.isEmpty(response.getDesc())) {
					CommonUtils.showToast(mActivity, response.getDesc(), Toast.LENGTH_SHORT);
				} else {
					CommonUtils.showToast(mActivity, R.string.register_get_verify_code_fail,
							Toast.LENGTH_SHORT);
				}
			}
		}

		@Override
		public void progress(Object... obj) {

		}

		@Override
		public void onNetWorkError() {

		}

	}

	Handler handler = new Handler();
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			if (recycleLen > 0) {
				recycleLen--;
				mRegisterTip.setText("验证码已发送，" + recycleLen + "s后可重新获取");
				handler.postDelayed(this, 1000);
			} else {
				recycleLen = 60;
				handler.removeCallbacks(runnable);
				mRegisterTip.setText(R.string.register_tip1);
				mVerifyBtn.setTextColor(getResources().getColor(R.color.white));
				mVerifyBtn.setEnabled(true);
			}
		}
	};

	class OnRegister implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (shake == null) {
				shake = AnimationUtils.loadAnimation(mContext, R.anim.shake);
			}
			String username = mUserName.getText().toString().trim();
			String email = mEmail.getText().toString().trim();
			String password = mPassword.getText().toString();
			String repassword = mRePassword.getText().toString();
			String verifyCode = mVerityCode.getText().toString().trim();
			String mobile = mMobile.getText().toString().trim();

			if (StringUtil.isEmpty(mobile)) {
				CommonUtils.showErrorByEditText(mMobile, getString(R.string.register_mobile_empty),
						shake);
				return;
			}
			if (!CommonUtils.isMobileNoValid(mobile)) {
				CommonUtils.showErrorByEditText(mMobile,
						getString(R.string.login_not_mobile_number), shake);
				return;
			}
			if (StringUtil.isEmpty(verifyCode)) {
				CommonUtils.showErrorByEditText(mVerityCode,
						getString(R.string.register_verify_code_empty), shake);
				return;
			}

			if (StringUtil.isEmpty(username)) {
				CommonUtils.showErrorByEditText(mUserName,
						getString(R.string.register_nickname_empty), shake);
				return;
			}

			if (StringUtil.isEmpty(password)) {
				CommonUtils.showErrorByEditText(mPassword,
						getString(R.string.register_password_empty), shake);
				return;
			}

			if (!StringUtil.isPwdValid(password)) {
				CommonUtils.showErrorByEditText(mPassword,
						getString(R.string.register_password_valid), shake);
				return;
			}

			if (StringUtil.isEmpty(repassword)) {
				CommonUtils.showErrorByEditText(mRePassword,
						getString(R.string.register_password_empty), shake);
				return;
			}

			if (!password.equals(repassword)) {
				CommonUtils.showErrorByEditText(mRePassword,
						getString(R.string.register_password_not_equal), shake);
				return;
			}

			Map<String, String> params = new HashMap<String, String>();
			String imsi = AppHelper.getMobileIMSI(mContext);
			String imei = AppHelper.getIMEI(mContext);
			String cityCode = MenuMgr.getInstance().getCityCode(mContext);
			params.put("mobile", mobile);
			params.put("imsi", imsi);
			params.put("imei", imei);
			params.put("username", username);
			params.put("password", LoginPwdEncrypter.EncryPwd(password));
			params.put("sms", verifyCode);
			params.put("email", email);
			params.put("city_code", cityCode);
			params.put("client_type", mActivity.getResources().getString(R.string.version_name_update));
			data.setUserName(username);
			data.setMobile(mobile);
			data.setEmail(email);
			data.setImsi(imsi);
			data.setPassword(LoginPwdEncrypter.EncryPwd(password));
			account.setData(data);
			if (!CommonUtils.isNetConnectionAvailable(mContext)) {
				CommonUtils.showToast(mActivity, R.string.net_error, Toast.LENGTH_SHORT);
				return;
			}
			showProgress(getString(R.string.register_running));
			RegisterBo bo = new RegisterBo(new OnRegisterCallback(), mContext);
			bo.register(params);
		}
	}

	class OnRegisterCallback implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp resp) {
			dismissProgress();
			if (resp.isSuccess()) {
				String mobile = mMobile.getText().toString().trim();
				String password = mPassword.getText().toString().trim();
				//注册成功，获取用户信息
				mLoginBo = new LoginBo(new OnLoginCallback(), mActivity);
				mLoginBo.login(mobile, password, mContext);
				XgManager.xg_register(mContext, mobile);
			} else {
				String returnCode = resp.getStatus();
				if ("-1".equals(returnCode)) {
					CommonUtils.showToast(mActivity, R.string.register_fail, Toast.LENGTH_SHORT);
				} else {
					if (!StringUtil.isEmpty(resp.getDesc())) {
						CommonUtils.showToast(mActivity, resp.getDesc(), Toast.LENGTH_SHORT);
					} else {
						CommonUtils
								.showToast(mActivity, R.string.register_fail, Toast.LENGTH_SHORT);
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

	}

	class OnLoginCallback implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp response) {
			dismissProgress();
			BaseResp resp = (BaseResp) response;
			if (resp.isSuccess()) {
				Account account = (Account) resp.getObj();
				AccountMgr.getInstance().refresh(mActivity, account);
				CommonUtils.showToast(mActivity, R.string.register_success, Toast.LENGTH_SHORT);
				// 跳转到上传头像页面，完善资料
				Intent intentShowInfo = new Intent(mActivity, PersonInfoActivity.class);
				startActivity(intentShowInfo);
				finish();
			}
		}

		@Override
		public void progress(Object... obj) {

		}

		@Override
		public void onNetWorkError() {

		}

	}

	@Override
	protected void initData() {
		TopUtil.updateTitle(mActivity, R.id.top_title, R.string.home_register);
		TopUtil.updateRight(mRegister, R.drawable.user_register_btn);
		isFromLogin = getIntent().getBooleanExtra(Key.K_IS_FROM_LOGIN, false);
	}

	@Override
	protected int getMainContentViewId() {
		return R.layout.act_register;
	}

	@Override
	public void finish() {
		super.finish();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (isFromLogin) {
			Intent i = new Intent(mActivity, LoginActivity.class);
			i.putExtra(Key.K_RETURN_TITLE, getString(R.string.home_name));
			startActivity(i);
		}
	}
}
