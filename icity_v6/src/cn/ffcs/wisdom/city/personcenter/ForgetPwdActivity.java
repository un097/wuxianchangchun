package cn.ffcs.wisdom.city.personcenter;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.ffcs.ui.tools.TopUtil;
import cn.ffcs.widget.LoadingDialog;
import cn.ffcs.wisdom.city.WisdomCityActivity;
import cn.ffcs.wisdom.city.personcenter.bo.ForgetPasswordBo;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.StringUtil;

/**
 * <p>Title:找回密码          </p>
 * <p>Description: 
 * </p>
 * <p>@author: Leo                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-2-26             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class ForgetPwdActivity extends WisdomCityActivity {
	private EditText mMobileEdit;
	private Button mSendPwd;
	private String mobile; // 手机号

	private ForgetPasswordBo mForgetPwdBo;

	private void showProgress() {
		LoadingDialog.getDialog(mActivity).setMessage(getString(R.string.forget_password_get))
				.show();
	}

	private void dismissProgress() {
		LoadingDialog.getDialog(mActivity).dismiss();
	}

	@Override
	protected void initComponents() {
		mMobileEdit = (EditText) findViewById(R.id.mobile);
		mSendPwd = (Button) findViewById(R.id.send_password);
		mSendPwd.setOnClickListener(new OnSendPwdListener());
	}

	class OnSendPwdListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Animation shake = AnimationUtils.loadAnimation(mContext, R.anim.shake);
			String mobile = mMobileEdit.getText().toString().trim();

			if (StringUtil.isEmpty(mobile)) {
				CommonUtils.showErrorByEditText(mMobileEdit,
						getString(R.string.forget_mobile_empty), shake);
				return;
			}
			if (!CommonUtils.isMobileNoValid(mobile)) {
				CommonUtils.showErrorByEditText(mMobileEdit,
						getString(R.string.login_not_mobile_number), shake);
				return;
			}
			if (!CommonUtils.isNetConnectionAvailable(mContext)) {
				CommonUtils.showToast(mActivity, R.string.net_error, Toast.LENGTH_SHORT);
				return;
			}
			SendPwd(mobile, "");
		}
	}

	/**
	 * 获取密码
	 * @param mobile
	 * @param password
	 */
	private void SendPwd(String mobile, String username) {
		if (mForgetPwdBo == null) {
			mForgetPwdBo = new ForgetPasswordBo(new ForgetPwdCallback(), ForgetPwdActivity.this);
		}
		showProgress();
		mForgetPwdBo.sendPwd(mobile, username);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dismissProgress();
	}

	class ForgetPwdCallback implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp response) {
			dismissProgress();
			if (response.isSuccess()) {
				CommonUtils.showToast(mActivity, R.string.forget_get_password_success,
						Toast.LENGTH_SHORT);
				finish();
			} else {
				String desc = response.getDesc();
				if (!StringUtil.isEmpty(desc)) {
					CommonUtils.showToast(mActivity, desc, Toast.LENGTH_SHORT);
				} else {
					CommonUtils.showToast(mActivity, R.string.forget_get_password_fail,
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

	@Override
	protected void initData() {
		TopUtil.updateTitle(mActivity, R.id.top_title, R.string.login_find_password);
		mobile = getIntent().getExtras().getString("mobile");
		if (!StringUtil.isEmpty(mobile) && CommonUtils.isMobileNoValid(mobile)) {
			mMobileEdit.setText(mobile);
			mMobileEdit.setSelection(mobile.length());
		}
	}

	@Override
	protected int getMainContentViewId() {
		return R.layout.act_forgetpassword;
	}
}
