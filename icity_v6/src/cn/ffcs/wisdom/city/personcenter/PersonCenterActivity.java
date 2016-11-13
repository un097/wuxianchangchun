package cn.ffcs.wisdom.city.personcenter;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.ffcs.ui.tools.TopUtil;
import cn.ffcs.widget.LoadingDialog;
import cn.ffcs.wisdom.city.WisdomCityActivity;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.personcenter.bo.PersonCenterBo;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.personcenter.entity.Account;
import cn.ffcs.wisdom.city.personcenter.entity.Account.AccountData;
import cn.ffcs.wisdom.city.personcenter.entity.MyRelevanceEntity;
import cn.ffcs.wisdom.city.personcenter.entity.MyRelevanceEntity.MyRelevance;
import cn.ffcs.wisdom.city.personcenter.entity.PhoneBillResp;
import cn.ffcs.wisdom.city.push.PushMsgBo;
import cn.ffcs.wisdom.city.resp.UpLoadImageResp;
import cn.ffcs.wisdom.city.utils.CityImageLoader;
import cn.ffcs.wisdom.city.utils.ConfigUtil;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.city.web.BrowserActivity;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.BitmapUtil;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.SdCardTool;
import cn.ffcs.wisdom.tools.StringUtil;
import cn.ffcs.wisdom.tools.SystemCallUtil;

/**
 * 
 * <p>Title:个人中心主页面        </p>
 * <p>Description: 
 * 
 * </p>
 * <p>@author: yangchx                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-3-16             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class PersonCenterActivity extends WisdomCityActivity implements OnClickListener {

	private TextView nickName;// 昵称
	private TextView level;// 等级
	private TextView signIn;// 签到
	private ImageView changeInformation;// 编辑
	private Button exit;// 退出
	private Account mAccount;
	private AccountData data;
	private PersonCenterBo personCenterBo;// 个人中心Bo
	private ProgressBar progressbar;// 显示等级的进度条
	private TextView telep_charge;// 话费
	private LinearLayout personcenter_telephone_layout;// 话费布局
	private ImageView imageHead;// 头像控件
	private int EDIT = 4001;// 编辑
	private int NOTICE = 4002;// 我的消息
	private int RELEVANCE = 4003; // 我的关联
	private String issign = "1";// 判断是否签到
	private TextView mynotif;// 我的消息
	private PushMsgBo pushBo;
	private TextView mRelevance;
	private TextView mRule;
	private CityImageLoader bitmaploader;

	private String webUrl;
	
	private RelativeLayout aixiu_switch_layout;

	@Override
	protected void initComponents() {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 竖屏
		nickName = (TextView) this.findViewById(R.id.personcenter_username);
		level = (TextView) this.findViewById(R.id.personcenter_rank);
		progressbar = (ProgressBar) this.findViewById(R.id.personcenter_progressbar);
		signIn = (TextView) this.findViewById(R.id.personcenter_signin);
		imageHead = (ImageView) this.findViewById(R.id.personcenter_userphoto);
		changeInformation = (ImageView) this.findViewById(R.id.personcenter_btn_edit);
		telep_charge = (TextView) this.findViewById(R.id.telep_charge);
		personcenter_telephone_layout = (LinearLayout) this
				.findViewById(R.id.personcenter_telephone_layout);
		/**
		 * add by xzw 话费查询
		 */
		personcenter_telephone_layout.setOnClickListener(this);

		exit = (Button) this.findViewById(R.id.personcenter_btn_eixt);
		mynotif = (TextView) this.findViewById(R.id.personcenter_information);
		mRelevance = (TextView) findViewById(R.id.personcenter_relevance);
		mRule = (TextView) findViewById(R.id.personcenter_integral);
		
		aixiu_switch_layout = (RelativeLayout) findViewById(R.id.aixiu_switch_layout);
		String provinceCode = MenuMgr.getInstance().getProvinceCode(mContext);
		if (provinceCode.equals("7")) {
			aixiu_switch_layout.setVisibility(View.GONE);
		}
	}

	@Override
	protected void initData() {
		registerRefreshMsgBroadcast();
		TopUtil.updateTitle(this, R.id.top_title, R.string.person_center_user_center); // 设置标题
		personCenterBo = new PersonCenterBo();
		bitmaploader = new CityImageLoader(mContext);
		bitmaploader.setIsRealTimeShowImage(true);
		bitmaploader.setDefaultFailImage(R.drawable.info_userphoto);
		signIn.setOnClickListener(this);
		changeInformation.setOnClickListener(this);
		exit.setOnClickListener(this);
		imageHead.setOnClickListener(this);
		mynotif.setOnClickListener(this);

		mRelevance.setOnClickListener(this);
		mRule.setOnClickListener(this);
		setWidth(signIn, mRelevance, mRule, mynotif);// 改变宽度
		
//		aixiu_switch_layout.setOnClickListener(this);
//		aixiu_switch.setEnabled(false);
	}

	private void setWidth(View... view) {
		int screenWidth = AppHelper.getScreenWidth(mContext);
		int oneWidth = (screenWidth - CommonUtils.convertDipToPx(mContext, 33)) / 2;
		for (int i = 0; i < view.length; i++) {
			view[i].getLayoutParams().width = oneWidth;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		setuserInformation();
		getRelevance();
		getTelephoneBill();
		getNewPersonMsg();
	}

	/**
	 * 获取个人中心新的消息数
	 */
	private void getNewPersonMsg() {
		if (pushBo == null) {
			pushBo = new PushMsgBo(mContext);
		}
		int myNoticeNum = pushBo.getNewPersonMsgCount();
		if (myNoticeNum > 0) {
			String format = getString(R.string.person_center_account_tip);
			String num = String.format(format, String.valueOf(myNoticeNum));
			mynotif.setText(getString(R.string.person_center_my_notice) + num);
		} else {
			mynotif.setText(getString(R.string.person_center_my_notice));
		}
	}

	/**
	 * 获取关联数据并更新
	 */
	private void getRelevance() {
		personCenterBo.getRelevance(String.valueOf(data.getId()), mContext,
				new getRelevanceCallBack());
	}

	/**
	 * 获取关联回调
	 */
	class getRelevanceCallBack implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp response) {
			if (response.isSuccess()) {
				List<MyRelevance> list = ((MyRelevanceEntity) response.getObj()).getData();
				int count = 0;
				if (list != null) {
					for (int i = 0; i < list.size(); i++) {
						count += list.get(i).getKeyGroupList().size();
					}
					mRelevance.setText(getResources()
							.getString(R.string.person_center_my_relevance) + "(" + count + ")");
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
	 * 设置用户信息
	 */
	public void setuserInformation() {
		mAccount = AccountMgr.getInstance().getAccount(mContext);
		data = mAccount.getData();
		String Score = String.valueOf(data.getGradeScore());
		String high = String.valueOf(data.getHigh());
		String signType = data.getIsSign();// 是否签到判断
		String format = getResources().getString(R.string.person_center_level);
		String mylevel = String.format(format, data.getLevelName(), Score, high);
		if (data.getUserName() != null) {
			nickName.setText(data.getUserName());
		} else {
			nickName.setText(data.getMobile());
		}
		level.setText(mylevel);
		int iScore = data.getGradeScore();
		int iHigh = data.getHigh();
		progressbar.setMax(iHigh);
		progressbar.setProgress(iScore);
		if (issign.equals(signType)) {
			signIn.setEnabled(false);
			signIn.setText(getString(R.string.person_center_sign_in_already));
			signIn.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.personcenter_user_checkin_notavailable, 0, 0, 0);
		} else {
			signIn.setEnabled(true);
			signIn.setText(getString(R.string.person_center_sign_in));
		}
		changeInformation.setEnabled(true);
		String iconurl = data.getIconUrl();
		// 设置头像图片
		String mobile = AccountMgr.getInstance().getAccount(mContext).getData().getMobile();
		Bitmap bm = BitmapUtil.compressBitmapFromFile(Config.SDCARD_CITY_TMP + File.separator + mobile + ".jpg", 78, 78);
		if(bm != null) {
			imageHead.setImageBitmap(bm);
		} else {
		bitmaploader.loadUrl(imageHead, iconurl);
		}
	}

	/**
	 * 更新用户信息
	 */
	public void updataInformation(String password) {
		Map<String, String> params = new HashMap<String, String>();
		String mobile = data.getMobile();
		String cityCode = MenuMgr.getInstance().getCityCode(mContext);
		String clientVerType = this.getString(R.string.version_name_update);
		String clientVerNum = String.valueOf(AppHelper.getVersionCode(mContext));
		String clientChannelType = ConfigUtil.readChannelName(mActivity, Config.UMENG_CHANNEL_KEY);
		params.put("mobile", mobile);
		params.put("cityCode", cityCode);
		params.put("password", password);
		params.put("clientVerType", clientVerType);
		params.put("clientVerNum", clientVerNum);
		params.put("clientChannelType", clientChannelType);
		personCenterBo.getUserAllInformation(new updataInfocall(), this, params);
		showProgressBar(getString(R.string.refresh_data));
	}

	/**
	 * 话费和流量获取
	 * 
	 */
	public void getTelephoneBill() {
		HashMap<String, String> map = new HashMap<String, String>();
		String imsi = data.getImsi();
		String phone = data.getMobile();
		if (!StringUtil.isEmpty(imsi)) {
			map.put("imsi", data.getImsi());
		} else {
			map.put("imsi", "imsi");
		}
		map.put("accNbr", phone);
		personCenterBo.getPhoneBill(new GetPhoneBillCallBack(), mContext, map);
	}

	/**
	 * 按钮监听
	 */
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.personcenter_signin) {// 签到按钮
			signIn.setEnabled(false);
			personCenterBo.signin(new Signincall(), mContext);
		} else if (id == R.id.personcenter_btn_edit) {// 编辑按钮
			Intent i = new Intent(PersonCenterActivity.this, ChangeInformationMainActivity.class);
			i.putExtra(Key.K_RETURN_TITLE, getString(R.string.person_center_user_center));
			startActivityForResult(i, EDIT);
		} else if (id == R.id.personcenter_btn_eixt) {// 退出登录
			finish();
			AccountMgr.getInstance().loginOut(mContext);
		} else if (id == R.id.personcenter_userphoto) {// 更改用户头像
			SystemCallUtil.showSelect(PersonCenterActivity.this, Config.SDCARD_CITY_TMP);
		} else if (id == R.id.personcenter_information) {// 我的消息
			Intent intent = new Intent(PersonCenterActivity.this, MyNotifierActivity.class);
			intent.putExtra(Key.K_RETURN_TITLE, getString(R.string.person_center_user_center));
			startActivityForResult(intent, NOTICE);
		} else if (id == R.id.personcenter_relevance) {
			Intent i = new Intent(mActivity, MyRelevanceActivity.class);
			i.putExtra(Key.K_RETURN_TITLE, getString(R.string.person_center_user_center));
			i.putExtra("paId", String.valueOf(data.getId()));
			startActivityForResult(i, RELEVANCE);
		} else if (id == R.id.personcenter_telephone_layout) {
			Intent intent = new Intent(mContext, BrowserActivity.class);
			intent.putExtra(Key.K_RETURN_TITLE, getString(R.string.person_center_user_center));
			intent.putExtra(Key.U_BROWSER_URL, webUrl);
			intent.putExtra(Key.U_BROWSER_TITLE,
					mActivity.getString(R.string.personcenter_fare_search));
			intent.putExtra(Key.U_BROWSER_QUERY, "1");
			startActivity(intent);
		} else if (id == R.id.personcenter_integral) {
			String url = Config.UrlConfig.URL_CREDIT_RULE;
			String versNo = AppHelper.getVersionName(mContext);
			String osType = AppHelper.getOSType();
			String versionType = getString(R.string.version_name_update);
			String cityCode = MenuMgr.getInstance().getCityCode(mContext);
			String cityName = MenuMgr.getInstance().getCityName(mContext);
			url = url + "?versType=" + versionType + "&client_type=" + versionType + "&cityCode="
					+ cityCode + "&cityName=" + cityName + "&osType=" + osType + "&versNo="
					+ versNo + "&supportSystem=" + mContext.getString(R.string.suport_system);
			Intent intent = new Intent(mActivity, BrowserActivity.class);
			intent.putExtra(Key.K_RETURN_TITLE, getString(R.string.person_center_user_center));
			intent.putExtra(Key.U_BROWSER_URL, url);
			intent.putExtra(Key.U_BROWSER_TITLE,
					mActivity.getString(R.string.person_center_credit_and_level));
			startActivity(intent);
		}
	}

	// /**
	// * 删除当前登录的账号保存信息
	// */
	// private void deleteRememberInfo() {
	// String phone = AccountMgr.getInstance().getMobile(mContext);
	// RememberInfoService.getInstance(mContext).deleteByPhone(
	// Base64.encodeToString(phone.getBytes(), Base64.DEFAULT));
	// }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == EDIT && resultCode == RESULT_OK) {// 编辑返回
			updataInformation(mAccount.getData().getPassword());
		} else if (requestCode == SystemCallUtil.REQUESTCODE_PHOTOALBUM && resultCode == RESULT_OK) {// 调用相册返回
			Uri uri = data.getData();
			SystemCallUtil.ImageCut(mActivity, uri, 300, 300);
		} else if (requestCode == SystemCallUtil.REQUESTCODE_CAMERA && resultCode == RESULT_OK) {// 调用照相机返回
			SystemCallUtil.ImageCut(PersonCenterActivity.this, SystemCallUtil.IMAGE_URI, 200, 200);
		} else if (requestCode == SystemCallUtil.REQUESTCODE_IMAGECUT && resultCode == RESULT_OK) {// 调用裁剪图片返回
			showProgressBar(getResources().getString(R.string.person_center_up_head));
			Bitmap bitmap = data.getParcelableExtra("data");
			String mobile = AccountMgr.getInstance().getAccount(mContext).getData().getMobile();
			SdCardTool.save(bitmap, Config.SDCARD_CITY_TMP, File.separator + mobile + ".jpg");
			String load = Config.SDCARD_CITY_TMP + File.separator + mobile + ".jpg";
			String cityCString = MenuMgr.getInstance().getCityCode(mContext);
			personCenterBo.imageUpLoad(new fileUpLoadCallBack(), load, cityCString);
		} else if (requestCode == 1 && data != null) {// 编辑-----修改密码返回专用
			String newpassword = data.getStringExtra("password");
			updataInformation(newpassword);
		} else if (requestCode == NOTICE && resultCode == RESULT_OK) {// 我的消息返回--更新我的消息数量
			getNewPersonMsg();
		} else if (requestCode == RELEVANCE && resultCode == RESULT_OK) {
			getRelevance();
		}
	}

	/**
	 * 更新用户信息回调
	 */
	class updataInfocall implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp response) {
			isnetwork();
			if (response.isSuccess()) {
				mAccount = (Account) response.getObj();
				data = mAccount.getData();
				AccountMgr.getInstance().refresh(mActivity, mAccount); // 刷新帐号
				setuserInformation();
				hideProgressBar();
			} else {
				hideProgressBar();
				CommonUtils.showToast(mActivity, R.string.refresh_data_fail, Toast.LENGTH_SHORT);
			}
		}

		@Override
		public void progress(Object... obj) {
		}

		@Override
		public void onNetWorkError() {
			hideProgressBar();
			CommonUtils.showToast(mActivity, R.string.refresh_data_fail, Toast.LENGTH_SHORT);
		}
	}

	/**
	 * 签到回调
	 */
	class Signincall implements HttpCallBack<BaseResp> {
		@Override
		public void call(BaseResp response) {
			isnetwork();
			try {
				if (response.isSuccess()) {
					signIn.setEnabled(false);
					signIn.setText(getString(R.string.person_center_sign_in_already));
					signIn.setCompoundDrawablesWithIntrinsicBounds(
							R.drawable.personcenter_user_checkin_notavailable, 0, 0, 0);
					CommonUtils.showToast(mActivity, R.string.person_center_sign_succeed,
							Toast.LENGTH_SHORT);
					updataInformation(data.getPassword());
				} else {
					signIn.setEnabled(true);
					signIn.setText(getString(R.string.person_center_sign_in));
					CommonUtils.showToast(mActivity, R.string.person_center_sign_fail,
							Toast.LENGTH_SHORT);
				}
			} catch (Exception e) {
				signIn.setEnabled(true);
				signIn.setText(getString(R.string.person_center_sign_in));
				CommonUtils.showToast(mActivity, R.string.person_center_sign_fail,
						Toast.LENGTH_SHORT);
			}
		}

		@Override
		public void progress(Object... obj) {
		}

		@Override
		public void onNetWorkError() {
			signIn.setEnabled(true);
			signIn.setText(getString(R.string.person_center_sign_in));
			CommonUtils.showToast(mActivity, R.string.person_center_sign_fail, Toast.LENGTH_SHORT);
		}
	}

	/**
	 * 文件图片文件回调
	 */
	class fileUpLoadCallBack implements HttpCallBack<UpLoadImageResp> {
		@Override
		public void call(UpLoadImageResp response) {
			isnetwork();
			try {
				if (response.isSuccess()) {
					String filePath = response.getList().get(0).getFilePath();
					Map<String, String> map = new HashMap<String, String>();
					map.put("paId", String.valueOf(data.getId()));
					map.put("iconUrl", filePath);
					personCenterBo
							.updataUserInformation(new UpDataUserInformation(), mContext, map);
					hideProgressBar();
				}
			} catch (Exception e) {
				hideProgressBar();
				CommonUtils.showToast(mActivity, R.string.update_head_fail, Toast.LENGTH_SHORT);
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
	 * 图片信息回调
	 */
	class UpDataUserInformation implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp response) {
			isnetwork();
			if (response.isSuccess()) {
				updataInformation(data.getPassword());// 更新用户信息
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
	 * 获取话费回调
	 */
	class GetPhoneBillCallBack implements HttpCallBack<BaseResp> {

		public void call(BaseResp response) {
			try {
				if (response.isSuccess()) {
					PhoneBillResp entity = (PhoneBillResp) response.getObj();
					if (entity != null) {
						double balance = entity.getData().getBalance();
						double arrears = entity.getData().getArrearage();
						webUrl = entity.getData().getUrl();
						String cha = String.valueOf(balance - arrears);
						String format = getResources().getString(R.string.person_center_balance);
						String charge = String.format(format, cha);
						telep_charge.setText(charge);
						telep_charge.setVisibility(View.VISIBLE);
						personcenter_telephone_layout.setVisibility(View.VISIBLE);
						return;
					}
				}
			} catch (Exception e) {
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
	protected int getMainContentViewId() {
		return R.layout.act_person_center_main;
	}

	public void showProgressBar(String message) {
		LoadingDialog.getDialog(mActivity).setMessage(message).show();
	}

	public void hideProgressBar() {
		LoadingDialog.getDialog(mActivity).cancel();
	}

	public void isnetwork() {
		if (!CommonUtils.isNetConnectionAvailable(mContext)) {
			CommonUtils.showToast(mActivity, R.string.net_error, Toast.LENGTH_SHORT);
			hideProgressBar();
			return;
		}
	}

	/**
	 * 注册刷新推送消息个数广播
	 */
	private void registerRefreshMsgBroadcast() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(getString(R.string.refresh_msg_count_action));
		LocalBroadcastManager.getInstance(mContext).registerReceiver(new RefreshMsgReceiver(),
				filter);
	}

	class RefreshMsgReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null) {
				boolean isRefresh = intent.getBooleanExtra(
						getString(R.string.refresh_msg_count_action), false);
				if (isRefresh) {
					getNewPersonMsg();
				}
			}
		}
	}

	@Override
	public void finish() {
		super.finish();
	}
}
