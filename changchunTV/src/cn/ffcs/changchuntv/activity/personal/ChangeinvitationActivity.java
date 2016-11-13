package cn.ffcs.changchuntv.activity.personal;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.ffcs.changchun_base.activity.BaseActivity;
import cn.ffcs.changchuntv.R;
import cn.ffcs.changchuntv.activity.login.bo.ThirdAccountBo;
import cn.ffcs.widget.LoadingDialog;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.StringUtil;

public class ChangeinvitationActivity extends BaseActivity {

	private Button mSaveBtn; // 保存
	private EditText mInfo;

	private String info;
	private ThirdAccountBo mThirdAccountBo;

	private String invitation_code;
	private boolean isFocus = false;

	@Override
	public int getLayoutId() {
		return R.layout.activity_changepersonalinfo;
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		initActionBar();
		mInfo = (EditText) findViewById(R.id.info);
		mInfo.setHint("请输入邀请码");
//		mInfo.setInputType(InputType.TYPE_CLASS_NUMBER);
		mSaveBtn = (Button) findViewById(R.id.info_btn_save);
		mSaveBtn.setOnClickListener(new OnSave());
		super.init(savedInstanceState);
	}

	private void initActionBar() {
		TextView title = (TextView) findViewById(R.id.top_title);
		title.setText("填写邀请码");
		View mReturn = findViewById(R.id.btn_return);
		mReturn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

	}

	class OnSave implements OnClickListener {
		@Override
		public void onClick(View v) {
			sendRequest();
		}
	}

	private void sendRequest() {
		Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
		info = mInfo.getText().toString().trim();
		if (StringUtil.isEmpty(info)) {
				CommonUtils.showErrorByEditText(mInfo, "请输入邀请码", shake);
			return;
		}

		if (!CommonUtils.isNetConnectionAvailable(mContext)) {
			CommonUtils.showToast(mActivity, R.string.net_error,
					Toast.LENGTH_SHORT);
			return;
		}

		CommonUtils.hideKeyboard(mActivity);
		showProgress();
		if (mThirdAccountBo == null) {
			mThirdAccountBo = new ThirdAccountBo();
		}
		Map<String, String> map = new HashMap<String, String>();
		String mobile = AccountMgr.getInstance().getAccount(mContext).getData()
				.getMobile();
		map.put("mobile", mobile);
		String cityCode = MenuMgr.getInstance().getCityCode(mContext);
		map.put("cityCode", cityCode);
		map.put("orgCode", cityCode);
		map.put("longitude", "unknown");
		map.put("latitude", "unknown");
		map.put("invitation_code", info.toLowerCase());
		String sign = "";
		mThirdAccountBo.inputInvitation(new UpdatePerInfoCallback(), mContext,
				map, sign);
	}

	class UpdatePerInfoCallback implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp response) {
			dismissProgress();
			if (response.isSuccess()) {
				Intent intent = new Intent();
				intent.putExtra("invitation_code", info);
				setResult(RESULT_OK, intent);
				finish();
			} else {
				CommonUtils.showToast(mActivity, response.getDesc(),
						Toast.LENGTH_SHORT);
			}

		}

		@Override
		public void progress(Object... obj) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onNetWorkError() {
			// TODO Auto-generated method stub

		}

	}

	private void showProgress() {
		if (isFocus) {
			LoadingDialog.getDialog(mActivity).setMessage("正在修改").show();
		} else {
			if (mActivity != null) {
				LoadingDialog.getDialog(mActivity).setMessage("正在修改").show();
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
	public void onWindowFocusChanged(boolean hasFocus) {
		isFocus = hasFocus;
		super.onWindowFocusChanged(hasFocus);
	}

}
