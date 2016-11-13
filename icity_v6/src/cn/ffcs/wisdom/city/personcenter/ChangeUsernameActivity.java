package cn.ffcs.wisdom.city.personcenter;

import java.util.HashMap;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.ffcs.ui.tools.TopUtil;
import cn.ffcs.wisdom.city.WisdomCityActivity;
import cn.ffcs.wisdom.city.personcenter.bo.PersonCenterBo;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.personcenter.entity.Account;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.ManifestUtil;
import cn.ffcs.wisdom.tools.StringUtil;

/**
 * <p>Title: WisdomCity</p>
 * <p>Description: 修改用户名</p>
 * <p>Author: yangchx</p>
 * <p>CreateTime: 2013-3-8 </p>
 * <p>CopyRight: v6 </p>
 */
public class ChangeUsernameActivity extends WisdomCityActivity implements HttpCallBack<BaseResp> {

	private Button mSaveBtn; // 保存
	private EditText mUserName;

	private Account account;
	private String username;
	private PersonCenterBo bo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initComponents() {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 竖屏
		mSaveBtn = (Button) findViewById(R.id.user_name_btn_save);
		mUserName = (EditText) findViewById(R.id.username);
		TopUtil.updateTitle(this, R.id.top_title, R.string.person_center_change_nickname);
		mSaveBtn.setOnClickListener(new OnSave());
	}

	@Override
	protected void onSkin() {
		super.onSkin();
	}

	class OnSave implements OnClickListener {
		@Override
		public void onClick(View v) {
			sendRequest();
		}
	}

	private void sendRequest() {
		Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
		username = mUserName.getText().toString().trim();
		if (StringUtil.isEmpty(username)) {
			CommonUtils.showErrorByEditText(mUserName,
					getString(R.string.person_center_change_name_hint), shake);
			return;
		}
		if (username.equals(account.getData().getUserName())) {
			CommonUtils.showErrorByEditText(mUserName,
					getString(R.string.person_center_change_name_input_diff_name), shake);
			return;
		}

		if (bo == null) {
			bo = new PersonCenterBo();
		}
		HashMap<String, String> map = new HashMap<String, String>();
		String mobile = AccountMgr.getInstance().getAccount(this).getData().getMobile();
		String imsi = AccountMgr.getInstance().getAccount(this).getData().getImsi();
		String old_username = AccountMgr.getInstance().getAccount(this).getData().getUserName();
		map.put("mobile", mobile);
		map.put("imsi", imsi);
		map.put("old_username", old_username);
		map.put("new_username", username);
		
		String imei = AppHelper.getIMEI(mContext);
		map.put("imei", imei);
		map.put("client_version", AppHelper.getVersionCode(mContext) + "");
		map.put("client_channel_type", ManifestUtil.readMetaData(mContext, "UMENG_CHANNEL"));
		bo.changeUsername(this, mContext, map);
	}

	@Override
	protected int getMainContentViewId() {
		return R.layout.act_change_username;
	}

	@Override
	protected Class<?> getResouceClass() {
		return R.class;
	}

	protected void setCustomTitleTop() {
		TopUtil.updateTitle(this, R.id.top_title, getString(R.string.person_center_change_nickname));

	}

	@Override
	protected void initData() {
		account = AccountMgr.getInstance().getAccount(this);
	}

	@Override
	public void call(BaseResp response) {
		CommonUtils.hideKeyboard(mActivity);
		isnetwork();
		if (response.isSuccess()) {
			account.getData().setUserName(username);
			AccountMgr.getInstance().refresh(mActivity, account); // 保存到本地数据库
			Toast.makeText(mActivity, getString(R.string.change_susseed), Toast.LENGTH_SHORT)
					.show();
			Intent intent = new Intent();
			intent.putExtra("username", username);
			setResult(RESULT_OK, intent);
			finish();
		} else {
			CommonUtils.showToast(mActivity, R.string.change_fail, Toast.LENGTH_SHORT);
		}
	}

	@Override
	public void progress(Object... obj) {

	}

	@Override
	public void onNetWorkError() {

	}

	public void isnetwork() {
		if (!CommonUtils.isNetConnectionAvailable(mContext)) {
			CommonUtils.showToast(mActivity, R.string.net_error, Toast.LENGTH_SHORT);
			hideProgressBar();
			return;
		}
	}
}
