package cn.ffcs.wisdom.city.personcenter;

import java.net.URLDecoder;
import java.util.HashMap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.ffcs.ui.tools.TopUtil;
import cn.ffcs.wisdom.city.WisdomCityActivity;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.personcenter.bo.PersonCenterBo;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.personcenter.entity.Account;
import cn.ffcs.wisdom.city.sqlite.service.RememberInfoService;
import cn.ffcs.wisdom.city.utils.LoginPwdEncrypter;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.CrytoUtils;
import cn.ffcs.wisdom.tools.ManifestUtil;
import cn.ffcs.wisdom.tools.StringUtil;

/**
 * <p>Title: WisdomCity</p>
 * <p>Description: 修改用户名</p>
 * <p>Author: yangchx</p>
 * <p>CreateTime: 2013-3-8 </p>
 * <p>CopyRight: v6 </p>
 */
public class ChangePasswordActivity extends WisdomCityActivity implements HttpCallBack<BaseResp> {

	private Button mSave; // 保存

	private EditText mOldPassword; // 旧密码
	private EditText mNewPassword; // 新密码
	private EditText mRetryPwd; // 确认密码
	private String newPwd;// 新密码
	private PersonCenterBo bo;
	private ProgressDialog progressDialog;// 加载条
	private Account account;
	private int changePwd = 1;

	@Override
	protected void initComponents() {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 竖屏
		mOldPassword = (EditText) findViewById(R.id.old_pwd);
		mNewPassword = (EditText) findViewById(R.id.new_pwd);
		mRetryPwd = (EditText) findViewById(R.id.retry_pwd);
		mSave = (Button) findViewById(R.id.user_pws_btn_save);
		mSave.setOnClickListener(new OnSave());
	}

	/**
	 * 发送变更请求
	 */
	private void sendRequest() {
		if (!checkPwd())
			return;

		if (!CommonUtils.isNetConnectionAvailable(mContext)) {
			CommonUtils.showToast(mActivity, R.string.net_error, Toast.LENGTH_SHORT);
			return;
		}

		if (bo == null) {
			bo = new PersonCenterBo();
		}

		String cityCode = MenuMgr.getInstance().getCityCode(mContext);
		newPwd = mNewPassword.getText().toString();
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("client_type", mContext.getString(R.string.version_name_update));
		map.put("cityCode", cityCode);
		map.put("mobile", account.getData().getMobile());
		map.put("imsi", account.getData().getImsi());
		map.put("username", account.getData().getUserName());
		map.put("new_password", LoginPwdEncrypter.EncryPwd(newPwd));
		map.put("old_password", account.getData().getPassword());
		
		String imei = AppHelper.getIMEI(mContext);
		map.put("imei", imei);
		map.put("client_version", AppHelper.getVersionCode(mContext) + "");
		map.put("client_channel_type", ManifestUtil.readMetaData(mContext, "UMENG_CHANNEL"));
		showProgressBar();
		bo.changeUserpws(this, mContext, map);
	}

	class OnSave implements OnClickListener {
		@Override
		public void onClick(View v) {
			sendRequest();
		}
	}

	/**
	 * 密码是否合法
	 * @return
	 */
	private boolean checkPwd() {
		Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);

		String oldPwdStr = mOldPassword.getText().toString();
		String newPwdStr = mNewPassword.getText().toString();
		String retryPwdStr = mRetryPwd.getText().toString();

		if (StringUtil.isEmpty(oldPwdStr)) {
			CommonUtils.showErrorByEditText(mOldPassword,
					getString(R.string.person_center_psw_empty), shake);
			return false;
		}
		
		String rawPassword = "";
		try {
			rawPassword = CrytoUtils.decodeBy3DES(CrytoUtils.DESKEY,URLDecoder.decode(account.getData().getPassword(),"UTF-8"));
		} catch (Exception e) {
//			Log.e("密码解析出现异常，异常信息：" + e);
		}
		
		if (!oldPwdStr.equals(rawPassword)) {
			CommonUtils.showErrorByEditText(mOldPassword,
					getString(R.string.person_center_psw_wrong), shake);
			return false;
		}

		if (StringUtil.isEmpty(newPwdStr)) {
			CommonUtils.showErrorByEditText(mNewPassword,
					getString(R.string.person_center_psw_empty), shake);
			return false;
		}

		if (!StringUtil.isPwdValid(newPwdStr)) {
			CommonUtils.showErrorByEditText(mNewPassword,
					getString(R.string.person_center_pwd_valid), shake);
			return false;
		}

		if (StringUtil.isEmpty(retryPwdStr)) {
			CommonUtils.showErrorByEditText(mRetryPwd,
					getString(R.string.person_center_retry_psw_empty), shake);
			return false;
		}

		if (!newPwdStr.equals(retryPwdStr)) {
			CommonUtils.showErrorByEditText(mRetryPwd,
					getString(R.string.person_center_psw_nosame), shake);
			return false;
		}

		return true;
	}

	protected int getMainContentViewId() {
		return R.layout.act_change_password;
	}

	@Override
	protected Class<?> getResouceClass() {
		return R.class;
	}

	@Override
	protected void initData() {
		account = AccountMgr.getInstance().getAccount(mContext);
		TopUtil.updateTitle(this, R.id.top_title, R.string.person_center_change_password);
	}

	@Override
	public void call(BaseResp response) {
		isnetwork();
		CommonUtils.hideKeyboard(mActivity);
		if (response.isSuccess()) {
			CommonUtils
					.showToast(mActivity, getString(R.string.change_susseed), Toast.LENGTH_SHORT);
			account.getData().setPassword(LoginPwdEncrypter.EncryPwd(newPwd));
			AccountMgr.getInstance().refresh(mActivity, account); // 保存到本地数据库
			saveInfo(account.getData().getMobile(), newPwd);
			hideProgressBar();
			mActivity.finish();
		} else {
			hideProgressBar();
			CommonUtils.showToast(mActivity, getString(R.string.change_fail), Toast.LENGTH_SHORT);
		}
	}
	
	/**
	 *  保存个人信息
	 */
	private void saveInfo(String mobile, String password) {
		RememberInfoService.getInstance(mContext).saveUserInfo(
				Base64.encodeToString(mobile.getBytes(), Base64.DEFAULT),
				Base64.encodeToString(password.getBytes(), Base64.DEFAULT));
	}

	@Override
	public void progress(Object... obj) {

	}

	@Override
	public void onNetWorkError() {

	}

	public void showProgressBar(String message) {
		if (null == progressDialog) {
			progressDialog = new ProgressDialog(this);
		}
		progressDialog.setMessage(message);
		progressDialog.show();
	}

	public void hideProgressBar() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	@Override
	public void finish() {
		if (newPwd != null) {
			Intent intent = new Intent(mActivity, PersonCenterActivity.class);
			String provinceCode = MenuMgr.getInstance().getProvinceCode(mContext);
			if ("3500".equals(provinceCode)) {
				intent = new Intent(mContext, PersonCenterActivityNew.class);
			}
			intent.putExtra("password", LoginPwdEncrypter.EncryPwd(newPwd));
			setResult(changePwd, intent);
		}

		super.finish();
	}

	public void isnetwork() {
		if (!CommonUtils.isNetConnectionAvailable(mContext)) {
			CommonUtils.showToast(mActivity, R.string.net_error, Toast.LENGTH_SHORT);
			hideProgressBar();
			return;
		}
	}
}
