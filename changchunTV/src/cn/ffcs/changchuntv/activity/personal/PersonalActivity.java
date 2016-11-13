package cn.ffcs.changchuntv.activity.personal;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.ffcs.changchun_base.activity.BaseFragmentActivity;
import cn.ffcs.changchuntv.R;
import cn.ffcs.changchuntv.activity.home.MainActivity;
import cn.ffcs.changchuntv.activity.login.LoginActivity;
import cn.ffcs.changchuntv.activity.login.RegisterActivity;
import cn.ffcs.changchuntv.activity.login.bo.ThirdAccountBo;
import cn.ffcs.changchuntv.activity.personal.PersonalInfoActivity.GetPerInfoCallback;
import cn.ffcs.external.share.entity.CustomSocialShareEntity;
import cn.ffcs.external.share.view.CustomSocialShare;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.entity.ConfigParams;
import cn.ffcs.wisdom.city.personcenter.PersonInfoActivity;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.personcenter.entity.Account.AccountData;
import cn.ffcs.wisdom.city.push.NotificationActivity;
import cn.ffcs.wisdom.city.setting.SettingPageActivity;
import cn.ffcs.wisdom.city.simico.activity.collection.CollectionActivity;
import cn.ffcs.wisdom.city.simico.activity.detail.NewsDetailActivity;
import cn.ffcs.wisdom.city.simico.image.CommonImageLoader;
import cn.ffcs.wisdom.city.utils.CityImageLoader;
import cn.ffcs.wisdom.city.utils.ConfigUtil;
import cn.ffcs.wisdom.city.web.BrowserActivity;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.BitmapUtil;
import cn.ffcs.wisdom.tools.StringUtil;

import com.ctbri.wxcc.MessageEditor;
import com.ctbri.wxcc.audio.AudioFavoriteActivity;
import com.ctbri.wxcc.community.CommunityMyActivity;
import com.ctbri.wxcc.coupon.MyCouponActivity;
import com.ctbri.wxcc.media.MyFavoriteActivity;
import com.umeng.analytics.MobclickAgent;

public class PersonalActivity extends BaseFragmentActivity implements
		OnClickListener {

	private ImageView headportrait;
	private TextView username;
	private TextView integral;
	private TextView generalize_code, code_beizhu;
	private Button login;
	private Button register;
	private View profile;
	private View notification;
	private View mycoupon;
	private View mybroke;
	private View myprogram;
	private View myvoice;
	private View mynews;

	private AccountData data;
	private CommonImageLoader loader;
	String points = "?";

	private int LOGIN = 0;// 登录
	private int REGISTER = 1;// 注册
	private int INFO = 2; // 个人资料
	private int EDIT = 3; // 编辑
	private int AVATAR = 4; // 我的关联
	private int INTEGRAL = 5;

	@Override
	protected int getLayoutId() {
		return R.layout.activity_personal;
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		initActionBar();
		headportrait = (ImageView) findViewById(R.id.headportrait);
		// headportrait.setOnClickListener(this);
		username = (TextView) findViewById(R.id.username);
		integral = (TextView) findViewById(R.id.integral);
		generalize_code = (TextView) findViewById(R.id.generalize_code);
		generalize_code.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		generalize_code.setOnClickListener(this);
		code_beizhu = (TextView) findViewById(R.id.code_beizhu);
		integral.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		integral.setOnClickListener(this);
		login = (Button) findViewById(R.id.login);
		login.setOnClickListener(this);
		register = (Button) findViewById(R.id.register);
		register.setOnClickListener(this);
		profile = findViewById(R.id.profile);
		profile.setOnClickListener(this);
		notification = findViewById(R.id.notification);
		notification.setOnClickListener(this);
		mycoupon = findViewById(R.id.mycoupon);
		mycoupon.setOnClickListener(this);
		mybroke = findViewById(R.id.mybroke);
		mybroke.setOnClickListener(this);
		myprogram = findViewById(R.id.myprogram);
		myprogram.setOnClickListener(this);
		myvoice = findViewById(R.id.myvoice);
		myvoice.setOnClickListener(this);
		mynews = findViewById(R.id.mynews);
		mynews.setOnClickListener(this);
		loader = new CommonImageLoader(mContext);
		loader.setDefaultFailImage(R.drawable.simico_default_avatar);
		initUserStatus();
		super.init(savedInstanceState);

	}

	private void initActionBar() {
		TextView title = (TextView) findViewById(R.id.title);
		title.setText("个人中心");
		TextView setting = (TextView) findViewById(R.id.top_right);
		Drawable drawable = getResources().getDrawable(
				R.drawable.setting_selector);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(),
				drawable.getMinimumHeight());
		setting.setCompoundDrawables(null, null, drawable, null);
		setting.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		initUserStatus();
		super.onResume();
	}

	public void initUserStatus() {
		boolean isLogin = AccountMgr.getInstance().isLogin(
				getApplicationContext());
		if (isLogin) {
			setUserLogin();
			Map<String, String> map = new HashMap<String, String>();
			String phone = AccountMgr.getInstance().getMobile(mContext);
			map.put("mobile", phone);
			String cityCode = MenuMgr.getInstance().getCityCode(mContext);
			map.put("cityCode", cityCode);
			map.put("orgCode", cityCode);
			map.put("longitude", "unknown");
			map.put("latitude", "unknown");
			// int userid = AccountMgr.getInstance().getUserId(mContext);
//			int userid = Integer.parseInt(MessageEditor.getUserId(mActivity));
			int userid = AccountMgr.getInstance().getUserId(mActivity);
			// Log.e("sb", "initUserStatus   " + userid);
			map.put("user_id", userid + "");
			String imsi = AppHelper.getMobileIMSI(mContext);
			if (StringUtil.isEmpty(imsi)) {
				imsi = "0000000000000000";
			}
			String imei = AppHelper.getIMEI(mContext);
			String sign = phone + "$" + imsi + "$" + imei + "$" + userid;
			ThirdAccountBo bo = new ThirdAccountBo();
			bo.getPoints(new GetPointsCallback(), mContext, map, sign);

			String sign1 = phone;
			bo.getPerInfo(new GetPerInfoCallback(), mContext, map, sign1);

			data = AccountMgr.getInstance().getAccount(mContext).getData();
			CityImageLoader bitmaploader = new CityImageLoader(mContext);
			bitmaploader.setDefaultFailImage(R.drawable.info_userphoto);
			bitmaploader.setIsRealTimeShowImage(false);
			String imgUser = bitmaploader.patternUrl(data.getIconUrl());
			String userName = data.getUserName();
			MessageEditor.initOrUpdateCTBRI(mContext, userid + "", userName,
					imgUser, phone);
		} else {
			setUserNotLogin();
			points = "?";
		}

	}

	private void setUserLogin() {
		login.setVisibility(View.GONE);
		register.setVisibility(View.GONE);
		headportrait.setVisibility(View.VISIBLE);
		username.setVisibility(View.VISIBLE);
		integral.setVisibility(View.VISIBLE);
		generalize_code.setVisibility(View.VISIBLE);
		code_beizhu.setVisibility(View.VISIBLE);
		setUserInfo();

	}

	private void setUserNotLogin() {
		login.setVisibility(View.VISIBLE);
		register.setVisibility(View.VISIBLE);
		headportrait.setVisibility(View.GONE);
		username.setVisibility(View.GONE);
		integral.setVisibility(View.GONE);
		generalize_code.setVisibility(View.GONE);
		code_beizhu.setVisibility(View.GONE);

	}

	private void setUserInfo() {
		data = AccountMgr.getInstance().getAccount(mContext).getData();
		String iconurl = data.getIconUrl();
		String mobile = data.getMobile();
		// 设置头像图片
		Bitmap bm = BitmapUtil.compressBitmapFromFile(Config.SDCARD_CITY_TMP
				+ File.separator + mobile + ".jpg", 50, 50);
		if (bm != null) {
			headportrait.setImageBitmap(bm);
		} else {
			if (!StringUtil.isEmpty(iconurl) && !"null".equals(iconurl)) {
				if (iconurl.indexOf("http://") < 0) {
					iconurl = Config.GET_IMAGE_ROOT_URL() + iconurl;
				}
			} else {
				headportrait.setImageResource(R.drawable.simico_default_avatar);
			}
			loader.loadUrl(headportrait, iconurl);
		}
		if (data.getUserName() != null) {
			username.setText(data.getUserName());
		} else {
			username.setText(data.getMobile());
		}
		integral.setText("积分：" + points + "分");

	}

	String spreadCode = "";

	class GetPerInfoCallback implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp response) {
			BaseResp resp = (BaseResp) response;
			if (resp.isSuccess()) {
				try {
					JSONObject object = new JSONObject(response.getData());
					spreadCode = object.getString("spread_code");
					if (!TextUtils.isEmpty(spreadCode)
							&& !spreadCode.equals("null")) {
						generalize_code.setText("我的邀请码：" + spreadCode);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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

	class GetPointsCallback implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp response) {
			if (response.isSuccess()) {
				try {
					JSONObject object = new JSONObject(response.getData());
					points = object.getInt("real_points") + "";
					integral.setText("积分：" + points + "分");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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

//	Bitmap bitmap = null;

	@Override
	public void onClick(View v) {
		boolean isLogin = AccountMgr.getInstance().isLogin(
				getApplicationContext());
		if (v.getId() == R.id.login) {
			Intent intent = new Intent(PersonalActivity.this,
					LoginActivity.class);
			startActivityForResult(intent, LOGIN);
		} else if (v.getId() == R.id.register) {
			Intent intent = new Intent(PersonalActivity.this,
					RegisterActivity.class);
			startActivityForResult(intent, REGISTER);
		} else if (v.getId() == R.id.headportrait) {

		} else if (v.getId() == R.id.integral) {
			if (isLogin) {
				MobclickAgent.onEvent(mContext, "E_C_profile_integrationClick");
				String phone = AccountMgr.getInstance().getMobile(mContext);
				ConfigParams params = ConfigUtil.readConfigParams(mContext);
				String url = params.getPOINTS_DETAIL_URL();
				url += phone;
				Intent intent = new Intent(PersonalActivity.this,
						BrowserActivity.class);
				intent.putExtra(Key.U_BROWSER_URL, url);
				intent.putExtra(Key.U_BROWSER_TITLE, "积分记录");
				startActivityForResult(intent, INTEGRAL);
			}
		} else if (v.getId() == R.id.profile) {
			if (isLogin) {
				Intent intent = new Intent(PersonalActivity.this,
						PersonalInfoActivity.class);
				startActivityForResult(intent, EDIT);
			} else {
				Intent intent = new Intent(PersonalActivity.this,
						LoginActivity.class);
				startActivityForResult(intent, INFO);
			}
		} else if (v.getId() == R.id.notification) {
			Intent intent = new Intent(mContext, NotificationActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.mycoupon) {
			Intent intent = new Intent(PersonalActivity.this,
					MyCouponActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.mybroke) {
			Intent intent = new Intent(PersonalActivity.this,
					CommunityMyActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.myvoice) {
			Intent intent = new Intent(PersonalActivity.this,
					AudioFavoriteActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.myprogram) {
			Intent intent = new Intent(PersonalActivity.this,
					MyFavoriteActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.mynews) {
			Intent intent = new Intent(mContext, CollectionActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.top_right) {
			Intent intent = new Intent(mContext, SettingPageActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.generalize_code) { // 点击分享
//			CustomSocialShareEntity entity = new CustomSocialShareEntity();
//			if (bitmap == null) {
//				bitmap = BitmapFactory.decodeResource(getResources(),
//						R.drawable.ic_launcher);
//			}
//			entity.shareUrl = "http://t.cn/zWk3y6d?&code=" + spreadCode;
//			// entity.shareUrl =
//			// "http://ccgd-youhuiquan.153.cn:30089/download/index.html?&code="
//			// + spreadCode;
//			entity.shareTitle = "快来下载无线长春吧";
//			entity.shareContent = getString(R.string.spread_string, spreadCode);
//			// entity.imageUrl = bitmap;
//			entity.imageBitmap = bitmap;
//			entity.spreadCode = spreadCode;
//			// CustomSocialShare.shareImagePlatform(mActivity, entity, false);

			Bundle bundle = new Bundle();
			Intent intent = new Intent(mContext, CodeSharelActivity.class);
			bundle.putString("spreadCode", spreadCode);
			intent.putExtras(bundle);
			startActivity(intent);

			// CustomSocialShare.shareTextPlatform(mActivity, "邀请码分享",
			// getString(R.string.spread_string, spreadCode),
			// "http://ccgd-d.153.cn:50000/?vt=changchuntv_ver");
			// }
		}
	}

	@Override
	public void onBackPressed() {
		((MainActivity) getParent()).onBack();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == INTEGRAL) {
			initUserStatus();
			return;
		}
		if (resultCode == RESULT_OK) {
			if (requestCode == LOGIN) {
				initUserStatus();
				boolean isFromRegister = data.getBooleanExtra("isFromRegister",
						false);
				if (isFromRegister) {
					Intent intent = new Intent(PersonalActivity.this,
							PersonInfoActivity.class);
					startActivityForResult(intent, AVATAR);
				}
				return;
			} else if (requestCode == REGISTER) {
				initUserStatus();
				Intent intent = new Intent(PersonalActivity.this,
						PersonInfoActivity.class);
				startActivityForResult(intent, AVATAR);
				return;
			} else if (requestCode == INFO) {
				initUserStatus();
				Intent intent = new Intent(PersonalActivity.this,
						PersonalInfoActivity.class);
				startActivityForResult(intent, EDIT);
				return;
			} else if (requestCode == EDIT) {
				initUserStatus();
				return;
			} else if (requestCode == AVATAR) {
				initUserStatus();
				return;
			}
		}
	}

}
