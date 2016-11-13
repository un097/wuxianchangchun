package cn.ffcs.changchuntv.activity.personal;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.ffcs.changchun_base.activity.BaseActivity;
import cn.ffcs.changchuntv.R;
import cn.ffcs.changchuntv.activity.login.bo.ThirdAccountBo;
import cn.ffcs.external.weibo.AccessTokenKeeper;
import cn.ffcs.external.weibo.LogoutAPI;
import cn.ffcs.ui.tools.AlertBaseHelper;
import cn.ffcs.widget.LoadingDialog;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.personcenter.ChangePasswordActivity;
import cn.ffcs.wisdom.city.personcenter.ChangeUsernameActivity;
import cn.ffcs.wisdom.city.personcenter.bo.PersonCenterBo;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.personcenter.entity.Account;
import cn.ffcs.wisdom.city.resp.UpLoadImageResp;
import cn.ffcs.wisdom.city.utils.CityImageLoader;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.BitmapUtil;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.ManifestUtil;
import cn.ffcs.wisdom.tools.SdCardTool;
import cn.ffcs.wisdom.tools.SystemCallUtil;

import com.ctbri.wxcc.MessageEditor;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.tencent.tauth.Tencent;

/**
 * 个人资料的页面 修改相关的个人设置
 * 
 * @author Administrator
 *
 */
public class PersonalInfoActivity extends BaseActivity {

	private LinearLayout changeNickName;
	private LinearLayout changePassword;
	private LinearLayout changeHead;
	private LinearLayout ll_spread_code;
	private TextView username;
	private ProgressDialog progressDialog;
	private PersonCenterBo personCenterBo;
	private String paId;
	private ImageView headphoto;
	private CityImageLoader bitmaploader;
	private String iconurl;
	private int ispws = 40012;
	private int isuername = 40011;
	private int paw = 0;
	private String filePath;
	private String myusername = null;

	private Button exit;// 退出
	private LinearLayout changeGender;
	private LinearLayout changeAge;
	private TextView gender;
	private TextView age;
	private TextView spread_tv;
	private String qq_appid;
	private String weibo_appKey;
	private Oauth2AccessToken mAccessToken;

	private boolean isFocus = false;

	@Override
	public int getLayoutId() {
		return R.layout.activity_personalinfo;
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		initActionBar();
		changeNickName = (LinearLayout) findViewById(R.id.change_nickname);
		changePassword = (LinearLayout) findViewById(R.id.change_password);
		changeHead = (LinearLayout) findViewById(R.id.change_user_head);
		ll_spread_code = (LinearLayout) findViewById(R.id.ll_spread_code);
		headphoto = (ImageView) this
				.findViewById(R.id.person_center_head_photo);
		username = (TextView) this.findViewById(R.id.change_username);
		paId = String.valueOf(AccountMgr.getInstance().getAccount(mContext)
				.getData().getId());
		iconurl = AccountMgr.getInstance().getAccount(mContext).getData()
				.getIconUrl();
		myusername = AccountMgr.getInstance().getAccount(mContext).getData()
				.getUserName();
		String mobile = AccountMgr.getInstance().getAccount(mContext).getData()
				.getMobile();
		// 设置头像图片
		bitmaploader = new CityImageLoader(mContext);
		bitmaploader.setIsRealTimeShowImage(true);
		bitmaploader.setDefaultFailImage(R.drawable.info_userphoto);
		// 设置头像图片
		Bitmap bm = BitmapUtil.compressBitmapFromFile(Config.SDCARD_CITY_TMP
				+ File.separator + mobile + ".jpg", 78, 78);
		if (bm != null) {
			headphoto.setImageBitmap(bm);
		} else {
			bitmaploader.loadUrl(headphoto, iconurl);
		}
		// 设置用户名
		if (myusername == null) {
			username.setText(mobile);
		} else {
			username.setText(myusername);
		}

		personCenterBo = new PersonCenterBo();
		changeNickName.setOnClickListener(new OnLayoutClickLinear());
		changePassword.setOnClickListener(new OnLayoutClickLinear());
		changeHead.setOnClickListener(new OnLayoutClickLinear());
		qq_appid = getResources().getString(R.string.social_qq_app_id);
		weibo_appKey = getResources().getString(R.string.social_weibo_app_key);
		mAccessToken = AccessTokenKeeper
				.readAccessToken(PersonalInfoActivity.this);

		exit = (Button) this.findViewById(R.id.personcenter_btn_eixt);
		exit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				AccountMgr.getInstance().loginOut(mContext);
				MessageEditor.unregisterCTBRI(mContext);
				if (mAccessToken != null && mAccessToken.isSessionValid()) {
					new LogoutAPI(PersonalInfoActivity.this, weibo_appKey,
							mAccessToken).logout(new LogOutRequestListener());
				}
				Tencent mTencent = Tencent.createInstance(qq_appid,
						getApplicationContext());
				if (mTencent.isSessionValid()) {
					mTencent.logout(PersonalInfoActivity.this);
				}
				setResult(RESULT_OK);
				finish();

			}
		});
		changeGender = (LinearLayout) findViewById(R.id.change_gender);
		changeAge = (LinearLayout) findViewById(R.id.change_age);
		gender = (TextView) findViewById(R.id.gender);
		age = (TextView) findViewById(R.id.age);
		spread_tv = (TextView) findViewById(R.id.spread_tv);
		changeGender.setOnClickListener(new OnLayoutClickLinear());
		changeAge.setOnClickListener(new OnLayoutClickLinear());
		ThirdAccountBo mThirdAccountBo = new ThirdAccountBo();
		Map<String, String> map = new HashMap<String, String>();
		map.put("mobile", mobile);
		String cityCode = MenuMgr.getInstance().getCityCode(mContext);
		map.put("cityCode", cityCode);
		map.put("orgCode", cityCode);
		map.put("longitude", "unknown");
		map.put("latitude", "unknown");
		String sign = mobile;
		mThirdAccountBo.getPerInfo(new GetPerInfoCallback(), mContext, map,
				sign);
		super.init(savedInstanceState);
	}

	private void initActionBar() {
		TextView title = (TextView) findViewById(R.id.top_title);
		title.setText(R.string.person_center_change_information);
		View mReturn = findViewById(R.id.btn_return);
		mReturn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();

			}
		});

	}

	class OnLayoutClickLinear implements OnClickListener {

		@Override
		public void onClick(View v) {
			int id = v.getId();

			if (id == R.id.change_nickname) {
				Intent intent = new Intent(PersonalInfoActivity.this,
						ChangeUsernameActivity.class);
				intent.putExtra(Key.K_RETURN_TITLE,
						getString(R.string.person_center_change_information));
				startActivityForResult(intent, isuername);
			} else if (id == R.id.change_password) {
				Intent intent = new Intent(PersonalInfoActivity.this,
						ChangePasswordActivity.class);
				intent.putExtra(Key.K_RETURN_TITLE,
						getString(R.string.person_center_change_information));
				startActivityForResult(intent, ispws);
			} else if (id == R.id.change_user_head) {
				SystemCallUtil.showSelect(PersonalInfoActivity.this,
						Config.SDCARD_CITY_TMP);
			} else if (id == R.id.change_gender) {
				final CharSequence[] picItems = { "男", "女" };
				AlertDialog dlg = new AlertDialog.Builder(
						PersonalInfoActivity.this)
						.setTitle("性别")
						.setItems(picItems,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0,
											int arg1) {
										String genderString = "";
										if (arg1 == 0) {
											genderString = "男";
										} else if (arg1 == 1) {
											genderString = "女";
										}
										gender.setText(genderString);
										showProgress();
										ThirdAccountBo mThirdAccountBo = new ThirdAccountBo();
										Map<String, String> map = new HashMap<String, String>();
										String mobile = AccountMgr
												.getInstance()
												.getAccount(mContext).getData()
												.getMobile();
										map.put("mobile", mobile);
										String cityCode = MenuMgr.getInstance()
												.getCityCode(mContext);
										map.put("cityCode", cityCode);
										map.put("orgCode", cityCode);
										map.put("longitude", "unknown");
										map.put("latitude", "unknown");
										map.put("gender", genderString);
										String sign = "";
										mThirdAccountBo.updatePerInfo(
												new UpdatePerInfoCallback(),
												mContext, map, sign);

									}

								}).create();
				dlg.show();
				// Intent intent = new Intent(PersonalInfoActivity.this,
				// ChangePersonalInfoActivity.class);
				// intent.putExtra(Key.K_RETURN_TITLE,
				// getString(R.string.person_center_change_information));
				// intent.putExtra("gender", gender.getText());
				// startActivityForResult(intent, 1);
			} else if (id == R.id.change_age) {
				Intent intent = new Intent(PersonalInfoActivity.this,
						ChangePersonalInfoActivity.class);
				intent.putExtra(Key.K_RETURN_TITLE,
						getString(R.string.person_center_change_information));
				intent.putExtra("age", age.getText());
				startActivityForResult(intent, 2);
			} else if (id == R.id.ll_spread_code) {  //TODO
				Intent intent = new Intent(PersonalInfoActivity.this,
						ChangeinvitationActivity.class);
				intent.putExtra(Key.K_RETURN_TITLE,
						getString(R.string.person_center_change_information));
				startActivityForResult(intent, 3);
				
			}
		}
	}
	
	

	class UpdatePerInfoCallback implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp response) {
			dismissProgress();
			if (response.isSuccess()) {
				CommonUtils.showToast(mActivity, R.string.change_susseed,
						Toast.LENGTH_SHORT);
			} else {
				CommonUtils.showToast(mActivity, R.string.change_fail,
						Toast.LENGTH_SHORT);
			}

		}

		@Override
		public void progress(Object... obj) {

		}

		@Override
		public void onNetWorkError() {

		}

	}

	class GetPerInfoCallback implements HttpCallBack<BaseResp> {
		@Override
		public void call(BaseResp response) {
			BaseResp resp = (BaseResp) response;
			if (resp.isSuccess()) {
				try {
					JSONObject object = new JSONObject(response.getData());
					String genderString = object.getString("gender");
					String ageString = object.getString("age");
					String spread_codeString = object.getString("spread_code");
					String invitation_code = object.getString("invitation_code");
					if (!TextUtils.isEmpty(genderString)
							&& !genderString.equals("null")) {
						gender.setText(genderString);
					}
					if (!TextUtils.isEmpty(ageString)
							&& !ageString.equals("null")) {
						age.setText(ageString);
					}
					
					if (!TextUtils.isEmpty(invitation_code)
							&& !invitation_code.equals("null")) {
							spread_tv.setText(invitation_code);
					}else {
						spread_tv.setText("请完善邀请码");
						ll_spread_code.setOnClickListener(new OnLayoutClickLinear());
					}
				} catch (JSONException e) {
					e.printStackTrace();
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
						AccessTokenKeeper.clear(PersonalInfoActivity.this);
						mAccessToken = null;
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

	@Override
	public void finish() {
		if (paw != 0) {
			setResult(paw);
		} else {
			setResult(RESULT_OK);
		}
		super.finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SystemCallUtil.REQUESTCODE_PHOTOALBUM
				&& resultCode == RESULT_OK) {
			Uri uri = data.getData();
			SystemCallUtil.ImageCut(this, uri, 200, 200);
		} else if (requestCode == SystemCallUtil.REQUESTCODE_CAMERA
				&& resultCode == RESULT_OK) {
			SystemCallUtil.ImageCut(PersonalInfoActivity.this,
					SystemCallUtil.IMAGE_URI, 200, 200);
		} else if (requestCode == SystemCallUtil.REQUESTCODE_IMAGECUT
				&& resultCode == RESULT_OK) {
			showProgressBar(getString(R.string.person_center_up_head));
			Bitmap bitmap = data.getParcelableExtra("data");
			String mobile = AccountMgr.getInstance().getAccount(mContext)
					.getData().getMobile();
			SdCardTool.save(bitmap, Config.SDCARD_CITY_TMP, File.separator
					+ mobile + ".jpg");
			String load = Config.SDCARD_CITY_TMP + File.separator + mobile
					+ ".jpg";
			personCenterBo.imageUpLoad(new fileUpLoadCallBack(), load, MenuMgr
					.getInstance().getCityCode(mContext));
		} else if (requestCode == ispws) {
			paw = resultCode;
		} else if (requestCode == isuername && resultCode == RESULT_OK) {
			myusername = data.getStringExtra("username");
			myusername = AccountMgr.getInstance().getAccount(mContext)
					.getData().getUserName();
			if (myusername != null) {
				username.setText(myusername);
			}
		} else if (requestCode == 1 && resultCode == RESULT_OK) {
			gender.setText(data.getStringExtra("gender"));
		} else if (requestCode == 2 && resultCode == RESULT_OK) {
			age.setText(data.getStringExtra("age"));
		}else if (requestCode == 3 && resultCode == RESULT_OK) {
			spread_tv.setText(data.getStringExtra("invitation_code"));
		}
	}

	/**
	 * 文件上传回调
	 */
	class fileUpLoadCallBack implements HttpCallBack<UpLoadImageResp> {

		@Override
		public void call(UpLoadImageResp response) {
			try {
				// hideProgressBar();
				// isnetwork();
				if (response.isSuccess()) {
					filePath = response.getList().get(0).getFilePath();
					Map<String, String> map = new HashMap<String, String>();
					map.put("paId", paId);
					map.put("iconUrl", filePath);

					String imsi = AppHelper.getMobileIMSI(mContext);
					String imei = AppHelper.getIMEI(mContext);
					map.put("imsi", imsi);
					map.put("imei", imei);
					map.put("client_version",
							AppHelper.getVersionCode(mContext) + "");
					map.put("client_channel_type", ManifestUtil.readMetaData(
							mContext, "UMENG_CHANNEL"));
					Account account = AccountMgr.getInstance().getAccount(
							mContext);
					if (account != null) {
						if (account.getData() != null) {
							account.getData().setIconUrl(filePath);
						}
					}
					AccountMgr.getInstance().refresh(mContext, account);
					personCenterBo.updataUserInformation(
							new UpDataUserInformation(), mContext, map);
					// showProgressBar(getString(R.string.person_center_up_head));
				} else {
					hideProgressBar();
				}
			} catch (Exception e) {
				hideProgressBar();
				CommonUtils.showToast(mActivity, R.string.change_fail,
						Toast.LENGTH_SHORT);
			}
		}

		@Override
		public void progress(Object... obj) {

		}

		@Override
		public void onNetWorkError() {

		}
	}

	/**
	 * 修改用户头像回调
	 */
	class UpDataUserInformation implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp response) {
			hideProgressBar();
			// isnetwork();
			if (response.isSuccess()) {
				// 设置头像图片
				String mobile = AccountMgr.getInstance().getAccount(mContext)
						.getData().getMobile();
				Bitmap bm = BitmapUtil.compressBitmapFromFile(
						Config.SDCARD_CITY_TMP + File.separator + mobile
								+ ".jpg", 35, 35);
				if (bm != null) {
					headphoto.setImageBitmap(bm);
				} else {
					bitmaploader.loadUrl(headphoto, filePath);
				}

				CommonUtils.showToast(mActivity, R.string.change_susseed,
						Toast.LENGTH_SHORT);
			} else {
				CommonUtils.showToast(mActivity, R.string.change_fail,
						Toast.LENGTH_SHORT);
			}
		}

		@Override
		public void progress(Object... obj) {

		}

		@Override
		public void onNetWorkError() {

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

	public void isnetwork() {
		if (!CommonUtils.isNetConnectionAvailable(mContext)) {
			CommonUtils.showToast(mActivity, R.string.net_error,
					Toast.LENGTH_SHORT);
			hideProgressBar();
			return;
		}
	}

}
