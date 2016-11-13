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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
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
public class PersonCenterActivityNew extends WisdomCityActivity implements OnClickListener {

	/**
	 * 用户信息
	 */
	private View userInfoLayout; // 用户信息，编辑
	private TextView nickName;// 昵称
	private TextView level;// 等级

	/**
	 * 签到
	 */
	private View signInLayout;
	private TextView signIn;// 签到
	private ImageView signInIcon; // 签到标志

	private Button exit;// 退出
	private Account mAccount;
	private AccountData data;
	private PersonCenterBo personCenterBo;// 个人中心Bo
	private ProgressBar progressbar;// 显示等级的进度条

	private ImageView imageHead;// 头像控件
	private int EDIT = 4001;// 编辑
	private int NOTICE = 4002;// 我的消息
	private int RELEVANCE = 4003; // 我的关联
	private String issign = "1";// 判断是否签到

	/**
	 * 我的消息
	 */
	private TextView mynotif;// 我的消息
	private View mNotifLayout; // 我的消息 点击控件
	private PushMsgBo pushBo;

	/**
	 * 积分规则
	 */
	private View mRuleLayout;
	private CityImageLoader bitmaploader;

	private String webUrl;
	
	/**
	 * 话费
	 */
	private TextView telep_charge;// 话费
	private LinearLayout personcenter_telephone_layout;// 话费布局

	// ================ add&modify by caijj 2013-10-9 ======
	// 修改布局
	//
	int viewWidth;

	int measureView() {
		int spaceWidth = CommonUtils.convertDipToPx(mContext, 36);
		int screenWidth = AppHelper.getScreenWidth(mContext);
		return (screenWidth - spaceWidth) / 3;
	}

	@Override
	protected void initComponents() {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 竖屏
		nickName = (TextView) this.findViewById(R.id.personcenter_username);
		level = (TextView) this.findViewById(R.id.personcenter_rank);
		progressbar = (ProgressBar) this.findViewById(R.id.personcenter_progressbar);
		signInLayout = findViewById(R.id.personcenter_signin_layout);
		signIn = (TextView) this.findViewById(R.id.personcenter_signin);
		signInIcon = (ImageView) findViewById(R.id.personcenter_signin_icon);
		imageHead = (ImageView) this.findViewById(R.id.personcenter_userphoto);
		mNotifLayout = findViewById(R.id.personcenter_information_layout);
		mNotifLayout.setOnClickListener(this);
		userInfoLayout = findViewById(R.id.personcenter_info_layout);
		
		telep_charge = (TextView) this.findViewById(R.id.telep_charge);
		personcenter_telephone_layout = (LinearLayout) this
				.findViewById(R.id.personcenter_telephone_layout);
		personcenter_telephone_layout.setOnClickListener(this);

		exit = (Button) this.findViewById(R.id.personcenter_btn_eixt);
		mynotif = (TextView) this.findViewById(R.id.personcenter_information);
		mRuleLayout = findViewById(R.id.personcenter_integral_layout);

		// add by caijj 根据屏幕大小，计算控件宽高，宽=高
		ViewGroup tel_sign_rel = (ViewGroup) findViewById(R.id.tel_sign_rel);
		int childCount = tel_sign_rel.getChildCount();
		viewWidth = measureView();
		for (int i = 0; i < childCount; i++) {
			View v = tel_sign_rel.getChildAt(i);
			LayoutParams params = (LayoutParams) v.getLayoutParams();
			params.width = viewWidth;
			params.height = viewWidth;
			v.setLayoutParams(params);
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
		signInLayout.setOnClickListener(this);
		exit.setOnClickListener(this);
		imageHead.setOnClickListener(this);
		userInfoLayout.setOnClickListener(this);
		mRuleLayout.setOnClickListener(this);

	}

	@Override
	protected void onResume() {
		super.onResume();
		setuserInformation();
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
			mynotif.setText(String.valueOf(myNoticeNum));
		} 
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
					// mRelevance.setText(getResources()
					// .getString(R.string.person_center_my_relevance) + "(" +
					// count + ")");
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

		// 更新签到状态
		updateSignInState(issign.equals(signType));

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
	 *  更新签到状态
	 *  @param sign true 已签到， false 未签到
	 */
	public void updateSignInState(boolean sign) {
		if (sign) { // 已签到
			signInLayout.setEnabled(false);
			signIn.setText(getString(R.string.person_center_sign_in_already));
			signInIcon.setImageResource(R.drawable.personcenter_user_checkin_new_notavailable);
		} else { // 未签到
			signInLayout.setEnabled(true);
			signIn.setText(getString(R.string.person_center_sign_in));
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
		if (id == R.id.personcenter_signin_layout) {// 签到按钮
			signInLayout.setEnabled(false);
			personCenterBo.signin(new Signincall(), mContext);
		} else if (id == R.id.personcenter_info_layout) {// 编辑按钮
			Intent i = new Intent(PersonCenterActivityNew.this, ChangeInformationMainActivity.class);
			i.putExtra(Key.K_RETURN_TITLE, getString(R.string.person_center_user_center));
			startActivityForResult(i, EDIT);
		} else if (id == R.id.personcenter_btn_eixt) {// 退出登录
			setResult(RESULT_OK);
			finish();
			AccountMgr.getInstance().loginOut(mContext);
		} else if (id == R.id.personcenter_userphoto) {// 更改用户头像
			SystemCallUtil.showSelect(PersonCenterActivityNew.this, Config.SDCARD_CITY_TMP);
		} else if (id == R.id.personcenter_information_layout) {// 我的消息
			Intent intent = new Intent(PersonCenterActivityNew.this, MyNotifierActivity.class);
			intent.putExtra(Key.K_RETURN_TITLE, getString(R.string.person_center_user_center));
			startActivityForResult(intent, NOTICE);
		} else if (id == R.id.personcenter_telephone_layout) {
			Intent intent = new Intent(mContext, BrowserActivity.class);
			intent.putExtra(Key.K_RETURN_TITLE, getString(R.string.person_center_user_center));
			intent.putExtra(Key.U_BROWSER_URL, webUrl);
			intent.putExtra(Key.U_BROWSER_TITLE, mActivity.getString(R.string.personcenter_fare_search));
			intent.putExtra(Key.U_BROWSER_QUERY, "1");
			startActivity(intent);
		} else if (id == R.id.personcenter_integral_layout) {
			String url = Config.UrlConfig.URL_CREDIT_RULE;
			String versNo = AppHelper.getVersionName(mContext);
			String osType = AppHelper.getOSType();
			String versionType = getString(R.string.version_name_update);
			String cityCode = MenuMgr.getInstance().getCityCode(mContext);
			String cityName = MenuMgr.getInstance().getCityName(mContext);
			url = url + "?versType=" + versionType + "&client_type=" + versionType + "&cityCode=" + cityCode
					+ "&cityName=" + cityName + "&osType=" + osType + "&versNo=" + versNo + "&supportSystem="
					+ mContext.getString(R.string.suport_system);
			Intent intent = new Intent(mActivity, BrowserActivity.class);
			intent.putExtra(Key.K_RETURN_TITLE, getString(R.string.person_center_user_center));
			intent.putExtra(Key.U_BROWSER_URL, url);
			intent.putExtra(Key.U_BROWSER_TITLE, mActivity.getString(R.string.person_center_credit_and_level));
			startActivity(intent);
		} 
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == EDIT && resultCode == RESULT_OK) {// 编辑返回
			updataInformation(mAccount.getData().getPassword());
		} else if (requestCode == SystemCallUtil.REQUESTCODE_PHOTOALBUM && resultCode == RESULT_OK) {// 调用相册返回
			Uri uri = data.getData();
			SystemCallUtil.ImageCut(mActivity, uri, 300, 300);
		} else if (requestCode == SystemCallUtil.REQUESTCODE_CAMERA && resultCode == RESULT_OK) {// 调用照相机返回
			SystemCallUtil.ImageCut(PersonCenterActivityNew.this, SystemCallUtil.IMAGE_URI, 200, 200);
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
			// getRelevance();
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
			boolean signSuccess = false;
			try {
				if (response.isSuccess()) {
					signSuccess = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				updateSignInState(signSuccess);
				if (signSuccess) {
					updataInformation(data.getPassword());
					CommonUtils.showToast(mActivity, R.string.person_center_sign_succeed, Toast.LENGTH_SHORT);
				} else {
					CommonUtils.showToast(mActivity, R.string.person_center_sign_fail, Toast.LENGTH_SHORT);
				}
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
					personCenterBo.updataUserInformation(new UpDataUserInformation(), mContext, map);
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
		return R.layout.act_person_center_main_new;
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
		LocalBroadcastManager.getInstance(mContext).registerReceiver(new RefreshMsgReceiver(), filter);
	}

	class RefreshMsgReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null) {
				boolean isRefresh = intent.getBooleanExtra(getString(R.string.refresh_msg_count_action),
						false);
				if (isRefresh) {
					getNewPersonMsg();
				}
			}
		}
	}

	@Override
	public void finish() {
		setResult(RESULT_OK);
		super.finish();
	}
}
