package cn.ffcs.changchuntv.activity.login;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ctbri.wxcc.MessageEditor;

import android.content.Intent;
import android.os.Bundle;
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
import cn.ffcs.changchun_base.activity.BaseActivity;
import cn.ffcs.changchuntv.request.RegisterPostRequest;
import cn.ffcs.widget.LoadingDialog;
import cn.ffcs.wisdom.city.R;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.personcenter.bo.GetVerifyCodeBo;
import cn.ffcs.wisdom.city.personcenter.bo.LoginBo;
import cn.ffcs.wisdom.city.personcenter.bo.RegisterBo;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.personcenter.entity.Account;
import cn.ffcs.wisdom.city.simico.api.model.ApiResponse;
import cn.ffcs.wisdom.city.simico.api.request.BaseRequestListener;
import cn.ffcs.wisdom.city.simico.base.Application;
import cn.ffcs.wisdom.city.simico.kit.util.DateUtil;
import cn.ffcs.wisdom.city.simico.kit.util.TDevice;
import cn.ffcs.wisdom.city.utils.CityImageLoader;
import cn.ffcs.wisdom.city.utils.LoginPwdEncrypter;
import cn.ffcs.wisdom.city.xg.XgManager;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.CrytoUtils;
import cn.ffcs.wisdom.tools.ManifestUtil;
import cn.ffcs.wisdom.tools.StringUtil;

public class RegisterActivity extends BaseActivity {

	private Animation shake;
	private EditText mUserName; // 用户名
	private EditText mPassword; // 密码
	private EditText mRePassword; // 确认密码
	private EditText mEmail; // 邮箱
	private EditText mMobile; // 手机号
	private EditText mVerityCode; // 验证码
	private Button mVerifyBtn; // 获取验证码
	private EditText register_invitation; // 邀请码
	private TextView mRegisterTip;// 验证码发送提示
	private Account account = new Account();
	private Account.AccountData data = account.new AccountData();
	// private ImageView mRegister;
	private TextView register;
	private int recycleLen = 60;// 倒计时
	private LoginBo mLoginBo;

	@Override
	public int getLayoutId() {
		return R.layout.act_register;
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		initActionBar();
		// mRegister = (ImageView) findViewById(R.id.top_right);
		// mRegister.setImageResource(R.drawable.user_register_btn);
		// mRegister.setVisibility(View.VISIBLE);
		// mRegister.setOnClickListener(new OnRegister());
		register = (TextView) findViewById(R.id.top_right_title);
		register.setText("提交");
		register.setVisibility(View.VISIBLE);
		register.setOnClickListener(new OnRegister());
		mMobile = (EditText) findViewById(R.id.register_mobile);
		mVerityCode = (EditText) findViewById(R.id.register_verifycode);
		mUserName = (EditText) findViewById(R.id.register_username);
		mUserName.setHint("用户名");
		mPassword = (EditText) findViewById(R.id.register_password);
		mRePassword = (EditText) findViewById(R.id.register_password_again);
		mEmail = (EditText) findViewById(R.id.register_email);
		mVerifyBtn = (Button) findViewById(R.id.register_verifyBtn);
		mVerifyBtn.setOnClickListener(new OnVerifyCode());
		mRegisterTip = (TextView) findViewById(R.id.register_tip);
		register_invitation = (EditText) findViewById(R.id.register_invitation);
		super.init(savedInstanceState);
	}

	private void initActionBar() {
		View mReturn = findViewById(R.id.btn_return);
		mReturn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();

			}
		});

	}

	class OnVerifyCode implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (shake == null) {
				shake = AnimationUtils.loadAnimation(mContext, R.anim.shake);
			}
			String mobile = mMobile.getText().toString().trim();
			if (StringUtil.isEmpty(mobile)) {
				CommonUtils.showErrorByEditText(mMobile,
						getString(R.string.register_mobile_empty), shake);
				return;
			}
			if (!CommonUtils.isMobileNoValid(mobile)) {
				CommonUtils.showErrorByEditText(mMobile,
						R.string.login_not_mobile_number, shake);
				return;
			}
			if (!CommonUtils.isNetConnectionAvailable(mContext)) {
				CommonUtils.showToast(mActivity, R.string.net_error,
						Toast.LENGTH_SHORT);
				return;
			}
			showProgress(getString(R.string.register_get_verify_running));
			GetVerifyCodeBo verifyBo = new GetVerifyCodeBo(
					new VerifyCallback(), mContext);
			verifyBo.VerifyCode(mobile);
			mVerifyBtn.setTextColor(getResources().getColor(R.color.gray));
			mVerifyBtn.setEnabled(false);
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
				CommonUtils.showToast(mActivity,
						R.string.register_get_verify_code_success,
						Toast.LENGTH_SHORT);
			} else {
				if (!StringUtil.isEmpty(response.getDesc())) {
					CommonUtils.showToast(mActivity, response.getDesc(),
							Toast.LENGTH_SHORT);
				} else {
					CommonUtils.showToast(mActivity,
							R.string.register_get_verify_code_fail,
							Toast.LENGTH_SHORT);
				}
				mVerifyBtn.setTextColor(getResources().getColor(R.color.white));
				mVerifyBtn.setEnabled(true);
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
			String invitation = register_invitation.getText().toString().trim();

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

//			Map<String, String> params = new HashMap<String, String>();
//			String imsi = AppHelper.getMobileIMSI(mContext);
//			String imei = AppHelper.getIMEI(mContext);
//			String cityCode = MenuMgr.getInstance().getCityCode(mContext);
//			params.put("mobile", mobile);
//			params.put("imsi", imsi);
//			params.put("imei", imei);
//			params.put("username", username);
//			params.put("password", LoginPwdEncrypter.EncryPwd(password));
//			params.put("sms", verifyCode);
//			params.put("email", email);
//			params.put("city_code", cityCode);
//			params.put("client_type", mActivity.getResources().getString(R.string.version_name_update));
//			params.put("client_version", AppHelper.getVersionCode(mContext) + "");
//			String clientChannelType = ManifestUtil.readMetaData(mContext, "UMENG_CHANNEL");
//			params.put("clientChannelType", clientChannelType);
//			params.put("client_channel_type", clientChannelType);
//			data.setUserName(username);
//			data.setMobile(mobile);
//			data.setEmail(email);
//			data.setImsi(imsi);
//			data.setPassword(LoginPwdEncrypter.EncryPwd(password));
//			account.setData(data);
//			if (!CommonUtils.isNetConnectionAvailable(mContext)) {
//				CommonUtils.showToast(mActivity, R.string.net_error, Toast.LENGTH_SHORT);
//				return;
//			}
//			showProgress(getString(R.string.register_running));
//			RegisterBo bo = new RegisterBo(new OnRegisterCallback(), mContext);
//			bo.register(params);
            Map<String, String> params = new HashMap<String, String>();
            String imsi = TDevice.getIMSI();
            String imei = TDevice.getIMEI();
            String cityCode = MenuMgr.getInstance().getCityCode(mContext);
            String timestamp = DateUtil.getNow("yyyy-MM-dd HH:mm:ss");
            String sign = null;
            try {
                String md5 = CrytoUtils.md5(
                        mobile + "$" + imsi + "$" + imei, CrytoUtils.MD5KEY,
                        timestamp);
                sign = CrytoUtils.encode(CrytoUtils.DESKEY, timestamp, md5);
            } catch (Exception e) {
                e.printStackTrace();
            }
            params.put("sign", sign);
            params.put("timestamp", timestamp);
            params.put("os_type", AppHelper.getOSTypeNew());
            params.put("org_code", cityCode);
            params.put("product_id", mContext.getString(cn.ffcs.wisdom.city.R.string.version_name_update));
            params.put("client_type", mContext.getString(cn.ffcs.wisdom.city.R.string.version_name_update));
            params.put("client_version", TDevice.getVersionCode() + "");
            params.put("client_channel_type", ManifestUtil.readMetaData(mContext, "UMENG_CHANNEL"));
            params.put("longitude", "unknown");
            params.put("latitude", "unknown");
            params.put("base_line", "400");
            params.put("mobile", mobile);
            params.put("imsi", imsi);
            params.put("imei", imei);
            params.put("user_name", username);
            params.put("password", LoginPwdEncrypter.EncryPwd(password));
            params.put("sms", verifyCode);
            params.put("email", email);
            params.put("city_code", cityCode);
            params.put("client_type", mActivity.getResources().getString(R.string.version_name_update));
            params.put("invitation_code", invitation);
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
            RegisterPostRequest req = new RegisterPostRequest(params,
                    new BaseRequestListener() {
                        @Override
                        public void onRequestSuccess(ApiResponse json) throws Exception {
                            showProgress("正在登录");
                            String mobile = mMobile.getText().toString().trim();
                            String password = mPassword.getText().toString().trim();
                            //注册成功，获取用户信息
                            mLoginBo = new LoginBo(new OnLoginCallback(), mActivity);
                            mLoginBo.login(mobile, password, mContext);
							XgManager.xg_register(mContext,mobile);
                        }

                        @Override
                        public void onRequestFaile(ApiResponse json, Exception e) {
                            CommonUtils
                                    .showToast(mActivity, "注册失败," + json.getMessage(), Toast.LENGTH_SHORT);
                            dismissProgress();
                        }

                        @Override
                        public void onRequestFinish() {

                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {
                    CommonUtils
                            .showToast(mActivity, R.string.register_fail, Toast.LENGTH_SHORT);
                    dismissProgress();
                }
            });
            req.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Application.instance().addToRequestQueue(req);
		}
	}

	class OnRegisterCallback implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp resp) {
			// dismissProgress();
			if (resp.isSuccess()) {
				// showProgress("正在登陆");
				String mobile = mMobile.getText().toString().trim();
				String password = mPassword.getText().toString().trim();
				// 注册成功，获取用户信息
				mLoginBo = new LoginBo(new OnLoginCallback(), mActivity);
				mLoginBo.login(mobile, password, mContext);
			} else {
				dismissProgress();
				String returnCode = resp.getStatus();
				if ("-1".equals(returnCode)) {
					CommonUtils.showToast(mActivity, R.string.register_fail,
							Toast.LENGTH_SHORT);
				} else {
					if (!StringUtil.isEmpty(resp.getDesc())) {
						CommonUtils.showToast(mActivity, resp.getDesc(),
								Toast.LENGTH_SHORT);
					} else {
						CommonUtils.showToast(mActivity,
								R.string.register_fail, Toast.LENGTH_SHORT);
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
				CommonUtils.showToast(mActivity, R.string.register_success,
						Toast.LENGTH_SHORT);
				String userId = account.getData().getUserId() + "";
				String phone = account.getData().getMobile();
				CityImageLoader bitmaploader = new CityImageLoader(mContext);
				bitmaploader.setDefaultFailImage(R.drawable.info_userphoto);
				bitmaploader.setIsRealTimeShowImage(false);
				String imgUser = bitmaploader.patternUrl(account.getData()
						.getIconUrl());
				String userName = account.getData().getUserName();
				MessageEditor.initOrUpdateCTBRI(mContext, userId, userName,
						imgUser, phone);
				Intent i = new Intent();
				setResult(RESULT_OK, i);
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
	protected void onDestroy() {
		dismissProgress();
		super.onDestroy();
	}

	private void showProgress(String msg) {
		LoadingDialog.getDialog(mActivity).setMessage(msg).show();
	}

	private void dismissProgress() {
		LoadingDialog.getDialog(mActivity).cancel();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

}
