package cn.ffcs.wisdom.city.setting;

import java.util.Iterator;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import cn.ffcs.ui.tools.AlertBaseHelper;
import cn.ffcs.ui.tools.TopUtil;
import cn.ffcs.widget.LoadingDialog;
import cn.ffcs.wisdom.city.WisdomCityActivity;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.tools.CommonUtils;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SnsAccount;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.SocializeUser;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMInfoAgent;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.FetchUserListener;
import com.umeng.socialize.controller.listener.SocializeListeners.SocializeClientListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.exception.SocializeException;

/***
 * <p>Title: 友盟分享授权         </p>
 * <p>Description: 
 * </p>
 * <p>@author: Leo                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-7-12             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class UmengShareAuthActivity extends WisdomCityActivity implements OnClickListener {
	private ImageView sinaIcon;
	private ImageView tencentIcon;
	private ImageView qzoneIcon;
	private ImageView renrenIcon;
	private ImageView doubanIcon;

	private TextView sinaUsername;
	private TextView tencentUsername;
	private TextView qzoneUsername;
	private TextView renrenUsername;
	private TextView doubanUsername;

	private ToggleButton sinaToggle;
	private ToggleButton tencentToggle;
	private ToggleButton qzoneToggle;
	private ToggleButton renrenToggle;
	private ToggleButton doubanToggle;

	private UMSocialService service;
	private List<SnsAccount> authUserList;// 已经授权的账号
	
	private boolean isFoucus=false;

	@Override
	protected void initComponents() {
		sinaIcon = (ImageView) findViewById(R.id.sina_icon);
		tencentIcon = (ImageView) findViewById(R.id.tencent_icon);
		qzoneIcon = (ImageView) findViewById(R.id.qzone_icon);
		renrenIcon = (ImageView) findViewById(R.id.renren_icon);
		doubanIcon = (ImageView) findViewById(R.id.douban_icon);

		sinaUsername = (TextView) findViewById(R.id.sina_username);
		tencentUsername = (TextView) findViewById(R.id.tencent_username);
		qzoneUsername = (TextView) findViewById(R.id.qzone_username);
		renrenUsername = (TextView) findViewById(R.id.renren_username);
		doubanUsername = (TextView) findViewById(R.id.douban_username);

		sinaToggle = (ToggleButton) findViewById(R.id.sina_toggle);
		tencentToggle = (ToggleButton) findViewById(R.id.tencent_toggle);
		qzoneToggle = (ToggleButton) findViewById(R.id.qzone_toggle);
		renrenToggle = (ToggleButton) findViewById(R.id.renren_toggle);
		doubanToggle = (ToggleButton) findViewById(R.id.douban_toggle);

		sinaToggle.setOnClickListener(this);
		tencentToggle.setOnClickListener(this);
		qzoneToggle.setOnClickListener(this);
		renrenToggle.setOnClickListener(this);
		doubanToggle.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.sina_toggle) {
			if (UMInfoAgent.isOauthed(mContext, SHARE_MEDIA.SINA)) {
				deleteAuthConfirm(sinaIcon, R.drawable.photo_share_sina_unfocus_selector,
						sinaUsername, sinaToggle, SHARE_MEDIA.SINA);
			} else {
				startAuth(sinaIcon, R.drawable.photo_share_sina_focus_selector, sinaUsername,
						sinaToggle, SHARE_MEDIA.SINA);
			}
		} else if (id == R.id.tencent_toggle) {
			if (UMInfoAgent.isOauthed(mContext, SHARE_MEDIA.TENCENT)) {
				deleteAuthConfirm(tencentIcon, R.drawable.photo_share_tencent_unfocus_selector,
						tencentUsername, tencentToggle, SHARE_MEDIA.TENCENT);
			} else {
				startAuth(tencentIcon, R.drawable.photo_share_tencent_focus_selector,
						tencentUsername, tencentToggle, SHARE_MEDIA.TENCENT);
			}
		} else if (id == R.id.qzone_toggle) {
			if (UMInfoAgent.isOauthed(mContext, SHARE_MEDIA.QZONE)) {
				deleteAuthConfirm(qzoneIcon, R.drawable.photo_share_zone_unfocus_selector,
						qzoneUsername, qzoneToggle, SHARE_MEDIA.QZONE);
			} else {
				startAuth(qzoneIcon, R.drawable.photo_share_zone_focus_selector, qzoneUsername,
						qzoneToggle, SHARE_MEDIA.QZONE);
			}
		} else if (id == R.id.renren_toggle) {
			if (UMInfoAgent.isOauthed(mContext, SHARE_MEDIA.RENREN)) {
				deleteAuthConfirm(renrenIcon, R.drawable.photo_share_renren_unfocus_selector,
						renrenUsername, renrenToggle, SHARE_MEDIA.RENREN);
			} else {
				startAuth(renrenIcon, R.drawable.photo_share_renren_focus_selector, renrenUsername,
						renrenToggle, SHARE_MEDIA.RENREN);
			}
		} else if (id == R.id.douban_toggle) {
			if (UMInfoAgent.isOauthed(mContext, SHARE_MEDIA.DOUBAN)) {
				deleteAuthConfirm(doubanIcon, R.drawable.photo_share_douban_unfocus_selector,
						doubanUsername, doubanToggle, SHARE_MEDIA.DOUBAN);
			} else {
				startAuth(doubanIcon, R.drawable.photo_share_douban_focus_selector, doubanUsername,
						doubanToggle, SHARE_MEDIA.DOUBAN);
			}

		}

	}

	@Override
	protected void initData() {
		TopUtil.updateTitle(mActivity, R.id.top_title, R.string.setting_bing_umeng_account);
		service = UMServiceFactory.getUMSocialService(getString(R.string.app_name),
				RequestType.SOCIAL);
		initAuthState();
	}

	/**
	 * 初始化授权状态
	 */
	private void initAuthState() {
		if (CommonUtils.isNetConnectionAvailable(mContext)) {
			showProgressBar("加载中...");
			service.getUserInfo(mActivity, new FetchUserInfoListener());
		} else {
			CommonUtils.showToast(mActivity, R.string.net_error, Toast.LENGTH_SHORT);
		}
	}

	class FetchUserInfoListener implements FetchUserListener {

		@Override
		public void onComplete(int arg0, SocializeUser user) {
			hideProgressBar();
			authUserList = user.mAccounts;
			if (authUserList != null && authUserList.size() > 0) {
				Iterator<SnsAccount> it = authUserList.iterator();
				while (it.hasNext()) {
					SnsAccount account = (SnsAccount) it.next();
					if (account != null) {
						String plat = account.getPlatform();
						String username = account.getUserName();
						initPlatForm(plat, username);
					}
				}
			}
		}

		@Override
		public void onStart() {

		}

	}

	/**
	 * 开始授权
	 * @param icon
	 * @param iconBackground
	 * @param username
	 * @param toggle
	 * @param media
	 */
	private void startAuth(final ImageView icon, final int iconBackground, final TextView username,
			final ToggleButton toggle, SHARE_MEDIA media) {
		if (!CommonUtils.isNetConnectionAvailable(mContext)) {
			CommonUtils.showToast(mActivity, R.string.net_error, Toast.LENGTH_SHORT);
			return;
		} else {
			service.doOauthVerify(mActivity, media, new UMAuthListener() {

				@Override
				public void onStart(SHARE_MEDIA arg0) {

				}

				@Override
				public void onError(SocializeException arg0, SHARE_MEDIA arg1) {
					CommonUtils.showToast(mActivity, R.string.setting_bind_error,
							Toast.LENGTH_SHORT);
				}

				@Override
				public void onComplete(Bundle arg0, SHARE_MEDIA arg1) {
					initAuthState();
				}

				@Override
				public void onCancel(SHARE_MEDIA arg0) {
					CommonUtils.showToast(mActivity, R.string.setting_bind_cancle_title,
							Toast.LENGTH_SHORT);
				}
			});
		}
	}

	/**
	 * 删除授权
	 * @param icon
	 * @param iconBackground
	 * @param username
	 * @param toggle
	 * @param media
	 */
	private void deleteAuthConfirm(final ImageView icon, final int iconBackground,
			final TextView username, final ToggleButton toggle, final SHARE_MEDIA media) {
		AlertBaseHelper.showConfirm(mActivity, "提示",
				getString(R.string.umeng_share_auth_delete_msg), "确定", "取消", new OnClickListener() {

					@Override
					public void onClick(View v) {
						showProgressDialog("正在解除绑定...");
						AlertBaseHelper.dismissAlert(mActivity);
						deleteAuth(icon, iconBackground, username, toggle, media);
					}
				}, new OnCancleClick());
	}

	class OnCancleClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			AlertBaseHelper.dismissAlert(mActivity);
		}

	}

	/**
	 * 解除绑定
	 * @param icon
	 * @param iconBackground
	 * @param username
	 * @param toggle
	 * @param media
	 */
	private void deleteAuth(final ImageView icon, final int iconBackground,
			final TextView username, final ToggleButton toggle, SHARE_MEDIA media) {
		if (!CommonUtils.isNetConnectionAvailable(mContext)) {
			CommonUtils.showToast(mActivity, R.string.net_error, Toast.LENGTH_SHORT);
			return;
		} else {

			service.deleteOauth(mActivity, media, new SocializeClientListener() {

				@Override
				public void onStart() {

				}

				@Override
				public void onComplete(int arg0, SocializeEntity arg1) {
					hideProgressBar();
					icon.setBackgroundResource(iconBackground);
					username.setText(R.string.umeng_share_auth_setting_unauthtip);
					toggle.setChecked(false);
					toggle.setBackgroundResource(R.drawable.setting_newssetting_switch_off);
				}
			});
		}
	}

	/**
	 * 初始化平台
	 * @param platForm
	 * @param username
	 */
	private void initPlatForm(String platForm, String username) {
		username = "(已授权)" + username;
		if ("sina".equals(platForm)) {
			sinaIcon.setBackgroundResource(R.drawable.photo_share_sina_focus_selector);
			sinaUsername.setText(username);
			sinaToggle.setChecked(true);
			sinaToggle.setBackgroundResource(R.drawable.setting_newssetting_switch_on);
		} else if ("tencent".equals(platForm)) {
			tencentIcon.setBackgroundResource(R.drawable.photo_share_tencent_focus_selector);
			tencentUsername.setText(username);
			tencentToggle.setChecked(true);
			tencentToggle.setBackgroundResource(R.drawable.setting_newssetting_switch_on);
		} else if ("qzone".equals(platForm)) {
			qzoneIcon.setBackgroundResource(R.drawable.photo_share_zone_focus_selector);
			qzoneUsername.setText(username);
			qzoneToggle.setChecked(true);
			qzoneToggle.setBackgroundResource(R.drawable.setting_newssetting_switch_on);
		} else if ("renren".equals(platForm)) {
			renrenIcon.setBackgroundResource(R.drawable.photo_share_renren_focus_selector);
			renrenUsername.setText(username);
			renrenToggle.setChecked(true);
			renrenToggle.setBackgroundResource(R.drawable.setting_newssetting_switch_on);
		} else if ("douban".equals(platForm)) {
			doubanIcon.setBackgroundResource(R.drawable.photo_share_douban_focus_selector);
			doubanUsername.setText(username);
			doubanToggle.setChecked(true);
			doubanToggle.setBackgroundResource(R.drawable.setting_newssetting_switch_on);
		}

	}

	@Override
	protected int getMainContentViewId() {
		return R.layout.act_umeng_share_auth;
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		isFoucus = hasFocus;
		super.onWindowFocusChanged(hasFocus);
	}
	
	public void showProgressBar(String message) {
		if(isFoucus){
			LoadingDialog.getDialog(mActivity).setMessage(message).show();
		}
	}

	public void hideProgressBar() {
		if(isFoucus){
			LoadingDialog.getDialog(mActivity).dismiss();
		}
	}
	
	@Override
	public void finish() {
		isFoucus = false;
		super.finish();
	}
}
