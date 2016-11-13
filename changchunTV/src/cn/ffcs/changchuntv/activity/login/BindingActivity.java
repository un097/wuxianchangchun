package cn.ffcs.changchuntv.activity.login;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

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
import cn.ffcs.changchuntv.R;
import cn.ffcs.changchuntv.activity.login.bo.ThirdAccountBo;
import cn.ffcs.ui.tools.TopUtil;
import cn.ffcs.widget.LoadingDialog;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.personcenter.bo.LoginBo;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.personcenter.entity.Account;
import cn.ffcs.wisdom.city.utils.CityImageLoader;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.StringUtil;

public class BindingActivity extends BaseActivity {
	
	private Animation shake;
	private ImageView mBinding;
	private EditText mMobile; // 手机号
	private EditText mVerityCode; // 验证码
	private Button mVerifyBtn; // 获取验证码
	private TextView mBindingTip;// 验证码发送提示
	private int recycleLen = 60;// 倒计时
	
	private String type;
	private String relaKey;
	
	private ThirdAccountBo thirdAccountBo;
	
	private LoginBo mLoginBo;
	
	private String mobile;// 手机
	private String password;// 密码

	@Override
	public int getLayoutId() {
		return R.layout.activity_binding;
	}
	
	@Override
	protected void init(Bundle savedInstanceState) {
		initActionBar();
		mBinding = (ImageView) findViewById(R.id.top_right);
		mBinding.setOnClickListener(new OnBinding());
		mMobile = (EditText) findViewById(R.id.binding_mobile);
		mVerityCode = (EditText) findViewById(R.id.binding_verifycode);
		mVerifyBtn = (Button) findViewById(R.id.binding_verifyBtn);
		mVerifyBtn.setOnClickListener(new OnVerifyCode());
		mBindingTip = (TextView) findViewById(R.id.binding_tip);
		
		TopUtil.updateTitle(mActivity, R.id.top_title, "绑定");
		TopUtil.updateRight(mBinding, R.drawable.user_register_btn);
		
		thirdAccountBo = new ThirdAccountBo();
		
		type = getIntent().getStringExtra("type");
		relaKey = getIntent().getStringExtra("relaKey");
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

	class OnBinding implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (shake == null) {
				shake = AnimationUtils.loadAnimation(mContext, R.anim.shake);
			}
			String verifyCode = mVerityCode.getText().toString().trim();
			mobile = mMobile.getText().toString().trim();

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
			if (!CommonUtils.isNetConnectionAvailable(mContext)) {
				CommonUtils.showToast(mActivity, R.string.net_error, Toast.LENGTH_SHORT);
				return;
			}
//			showProgress("正在校验验证码");
			showProgress("正在绑定");
			Map<String, String> map = new HashMap<String, String>();
			map.put("mobile", mobile);
			String cityCode = MenuMgr.getInstance().getCityCode(mContext);
			map.put("cityCode", cityCode);
			map.put("orgCode", cityCode);
			map.put("longitude", "unknown");
			map.put("latitude", "unknown");
			map.put("captcha", verifyCode);
			String sign = mobile + "$" + verifyCode;
			thirdAccountBo.verifyCaptcha(new VerifyCaptchaCallback(), mContext, map, sign);
		}
	}
	
	class VerifyCaptchaCallback implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp response) {
			if (response.isSuccess()) {
				if (response.getData().equals("1")) { // 校验失败
					dismissProgress();
					CommonUtils.showToast(mActivity, "校验失败", Toast.LENGTH_SHORT);
				} else if (response.getData().equals("0")) {
//					showProgress("正在绑定");
					Map<String, String> map = new HashMap<String, String>();
					map.put("mobile", mobile);
					String cityCode = MenuMgr.getInstance().getCityCode(mContext);
					map.put("cityCode", cityCode);
					map.put("orgCode", cityCode);
					map.put("longitude", "unknown");
					map.put("latitude", "unknown");
					map.put("type", type);
					map.put("relaKey", relaKey);
					String sign = mobile + "$" + type;
					thirdAccountBo.saveBinding(new OnBindingCallback(), mContext, map, sign);
				}
			} else {
				dismissProgress();
				String returnCode = response.getStatus();
				if ("-1".equals(returnCode)) {
					CommonUtils.showToast(mActivity, "验证失败", Toast.LENGTH_SHORT);
				} else {
					if (!StringUtil.isEmpty(response.getDesc())) {
						CommonUtils.showToast(mActivity, response.getDesc(), Toast.LENGTH_SHORT);
					} else {
						CommonUtils
								.showToast(mActivity, "验证失败", Toast.LENGTH_SHORT);
					}
				}
			}
			
		}

		@Override
		public void progress(Object... obj) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onNetWorkError() {
			CommonUtils.showToast(mActivity, "验证失败", Toast.LENGTH_SHORT);
			
		}
		
	}
	
	class OnBindingCallback implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp resp) {
//			dismissProgress();
			if (resp.isSuccess()) {
//				CommonUtils.showToast(mActivity, "绑定成功",
//						Toast.LENGTH_SHORT);
				login(mobile, password);
			} else {
				dismissProgress();
				String returnCode = resp.getStatus();
				if ("-1".equals(returnCode)) {
					CommonUtils.showToast(mActivity, "绑定失败", Toast.LENGTH_SHORT);
				} else {
					if (!StringUtil.isEmpty(resp.getDesc())) {
						CommonUtils.showToast(mActivity, resp.getDesc(), Toast.LENGTH_SHORT);
					} else {
						CommonUtils
								.showToast(mActivity, "绑定失败", Toast.LENGTH_SHORT);
					}
				}
			}
		}

		@Override
		public void progress(Object... obj) {

		}

		@Override
		public void onNetWorkError() {
			CommonUtils.showToast(mActivity, "绑定失败", Toast.LENGTH_SHORT);
		}

	}
	
	/**
	 * 提交登录
	 */
	private void login(String mobile, String password) {
		if (mLoginBo == null) {
			mLoginBo = new LoginBo(new LoginCallback(), mActivity);
		}
//		showProgress("正在登陆");
		mLoginBo.login(mobile, password, mContext);
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
				String userId = account.getData().getUserId() + "";
				String phone = account.getData().getMobile();
				CityImageLoader bitmaploader = new CityImageLoader(mContext);
				bitmaploader.setDefaultFailImage(R.drawable.info_userphoto);
				bitmaploader.setIsRealTimeShowImage(false);
				String imgUser = bitmaploader.patternUrl(account.getData().getIconUrl());
				String userName = account.getData().getUserName();
				MessageEditor.initOrUpdateCTBRI(mContext, userId, userName, imgUser, phone);
				Intent i = new Intent();
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
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onNetWorkError() {
			CommonUtils.showToast(mActivity, R.string.login_error, Toast.LENGTH_SHORT);
			
		}

	}
	
	class OnVerifyCode implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (shake == null) {
				shake = AnimationUtils.loadAnimation(mContext, R.anim.shake);
			}
			mobile = mMobile.getText().toString().trim();
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
//			showProgress("验证手机号是否已注册");
			showProgress("正在请求验证码");
			Map<String, String> map = new HashMap<String, String>();
			map.put("mobile", mobile);
			String cityCode = MenuMgr.getInstance().getCityCode(mContext);
			map.put("cityCode", cityCode);
			map.put("orgCode", cityCode);
			map.put("longitude", "unknown");
			map.put("latitude", "unknown");
			String sign = mobile;
			thirdAccountBo.isRegisteredMobile(new IsRegisteredMobileCallback(), mContext, map, sign);
			mVerifyBtn.setTextColor(getResources().getColor(R.color.gray));
			mVerifyBtn.setEnabled(false);
		}

	}
	
	class IsRegisteredMobileCallback implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp response) {
//			dismissProgress();
			if (response.isSuccess()) {
				if (response.getData().equals("1")) { // 已注册
//					showProgress("验证手机号是否已绑定");
					mobile = mMobile.getText().toString().trim();
					Map<String, String> map = new HashMap<String, String>();
					map.put("mobile", mobile);
					String cityCode = MenuMgr.getInstance().getCityCode(mContext);
					map.put("cityCode", cityCode);
					map.put("orgCode", cityCode);
					map.put("longitude", "unknown");
					map.put("latitude", "unknown");
					String sign = mobile;
					thirdAccountBo.isBoundMobile(new IsBoundMobileCallback(), mContext, map, sign);
				} else if (response.getData().equals("0")) {
					dismissProgress();
					CommonUtils.showToast(mActivity, "该号码未注册，请先注册", Toast.LENGTH_SHORT);
					mVerifyBtn.setTextColor(getResources().getColor(R.color.white));
					mVerifyBtn.setEnabled(true);
				}
			} else {
				dismissProgress();
				if (!StringUtil.isEmpty(response.getDesc())) {
					CommonUtils.showToast(mActivity, response.getDesc(), Toast.LENGTH_SHORT);
				} else {
					CommonUtils.showToast(mActivity, R.string.register_get_verify_code_fail,
							Toast.LENGTH_SHORT);
				}
				mVerifyBtn.setTextColor(getResources().getColor(R.color.white));
				mVerifyBtn.setEnabled(true);
			}
			
		}

		@Override
		public void progress(Object... obj) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onNetWorkError() {
			CommonUtils.showToast(mActivity, R.string.register_get_verify_code_fail,
					Toast.LENGTH_SHORT);
			
		}
		
	}
	
	class IsBoundMobileCallback implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp response) {
//			dismissProgress();
			if (response.isSuccess()) {
				try {
					JSONObject object = new JSONObject(response.getData());
					if (object.getString("is_banding").equals("1")) { // 已绑定
						dismissProgress();
						CommonUtils.showToast(mActivity, "该手机号已绑定，绑定失败", Toast.LENGTH_SHORT);
					} else if (object.getString("is_banding").equals("0")) {
//						showProgress("获取验证码");
						mobile = mMobile.getText().toString().trim();
						Map<String, String> map = new HashMap<String, String>();
						map.put("mobile", mobile);
						String cityCode = MenuMgr.getInstance().getCityCode(mContext);
						map.put("cityCode", cityCode);
						map.put("orgCode", cityCode);
						map.put("longitude", "unknown");
						map.put("latitude", "unknown");
						String sign = mobile;
						mobile = object.getString("username");
						password = object.getString("password");
						thirdAccountBo.sendCaptcha(new SendCaptchaCallback(), mContext, map, sign);
					}
				} catch (JSONException e) {
					dismissProgress();
					mVerifyBtn.setTextColor(getResources().getColor(R.color.white));
					mVerifyBtn.setEnabled(true);
					e.printStackTrace();
				}
			} else {
				dismissProgress();
				if (!StringUtil.isEmpty(response.getDesc())) {
					CommonUtils.showToast(mActivity, response.getDesc(), Toast.LENGTH_SHORT);
				} else {
					CommonUtils.showToast(mActivity, R.string.register_get_verify_code_fail,
							Toast.LENGTH_SHORT);
				}
				mVerifyBtn.setTextColor(getResources().getColor(R.color.white));
				mVerifyBtn.setEnabled(true);
			}
			
		}

		@Override
		public void progress(Object... obj) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onNetWorkError() {
			CommonUtils.showToast(mActivity, R.string.register_get_verify_code_fail,
					Toast.LENGTH_SHORT);
			
		}
		
	}
	
	class SendCaptchaCallback implements HttpCallBack<BaseResp> {

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
				mVerifyBtn.setTextColor(getResources().getColor(R.color.white));
				mVerifyBtn.setEnabled(true);
			}
		}

		@Override
		public void progress(Object... obj) {

		}

		@Override
		public void onNetWorkError() {
			CommonUtils.showToast(mActivity, R.string.register_get_verify_code_fail,
					Toast.LENGTH_SHORT);
		}

	}
	
	Handler handler = new Handler();
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			if (recycleLen > 0) {
				recycleLen--;
				mBindingTip.setText("验证码已发送，" + recycleLen + "s后可重新获取");
				handler.postDelayed(this, 1000);
			} else {
				recycleLen = 60;
				handler.removeCallbacks(runnable);
				mBindingTip.setText(R.string.register_tip1);
				mVerifyBtn.setTextColor(getResources().getColor(R.color.white));
				mVerifyBtn.setEnabled(true);
			}
		}
	};

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

}
