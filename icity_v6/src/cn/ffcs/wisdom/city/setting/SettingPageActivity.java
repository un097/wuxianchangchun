package cn.ffcs.wisdom.city.setting;

import java.io.File;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.test.TouchUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.ffcs.ui.tools.AlertBaseHelper;
import cn.ffcs.wisdom.city.WisdomCityActivity;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.download.ApkListActivity;
import cn.ffcs.wisdom.city.push.NotificationActivity;
import cn.ffcs.wisdom.city.push.PushMsgBo;
import cn.ffcs.wisdom.city.push.PushMsgBo.IrefreshMsg;
import cn.ffcs.wisdom.city.setting.about.FFAboutActivity;
import cn.ffcs.wisdom.city.setting.feedback.FeedBackActivity;
import cn.ffcs.wisdom.city.setting.share.SMSShareActivity;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.city.web.BrowserActivity;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.IntentUitls;
import cn.ffcs.wisdom.tools.SharedPreferencesUtil;

/**
 * 
 * <p>Title: 设置首页        </p>
 * <p>Description: 
 * 设置列表配置
 * </p>
 * <p>@author: xzw             </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-3-4             </p>
 * <p>Update Time: 2013-3-15           </p>
 * <p>Updater:Leo                         </p>
 * <p>Update Comments: 绑定新浪微博账号             </p>
 */
public class SettingPageActivity extends WisdomCityActivity implements OnClickListener {

	private TextView topTitle;// 头部标题

	private RelativeLayout shareLine;// 分享好友
	private RelativeLayout feedbackLine;// 意见反馈
	private RelativeLayout pushSettingLine;// 推送设置
	private RelativeLayout downloadMgr;// 下载管理
	private RelativeLayout aboutLine;// 关于
	private RelativeLayout notification;// 消息推送
	private RelativeLayout faq;// FAQ
	private RelativeLayout umengShare;// 新浪微博
	private RelativeLayout flow3g; //3G流量提醒
	private RelativeLayout clearCache;
	private TextView msgCount;

	private PushMsgBo pushBo;

	@Override
	protected void initComponents() {
		topTitle = (TextView) findViewById(R.id.top_title);
		msgCount = (TextView) findViewById(R.id.new_msg_count);
		shareLine = (RelativeLayout) findViewById(R.id.share_setting);
		shareLine.setOnClickListener(new OnShareListener());

		feedbackLine = (RelativeLayout) findViewById(R.id.feedback_setting);
		feedbackLine.setOnClickListener(this);

		pushSettingLine = (RelativeLayout) findViewById(R.id.push_setting);
		pushSettingLine.setOnClickListener(this);

		/**
		 * 下载管理
		 */
		downloadMgr = (RelativeLayout) findViewById(R.id.download_setting);
		downloadMgr.setOnClickListener(this);
		downloadMgr.setVisibility(View.GONE);

		aboutLine = (RelativeLayout) findViewById(R.id.about_setting);
		aboutLine.setOnClickListener(this);

		/**
		 * 消息通知
		 */
		notification = (RelativeLayout) findViewById(R.id.notification);
		notification.setOnClickListener(this);
		notification.setVisibility(View.GONE);

//		changeHomeBg = (RelativeLayout) findViewById(R.id.change_home_bg);
//		changeHomeBg.setOnClickListener(this);

		umengShare = (RelativeLayout) findViewById(R.id.sina_setting);
		umengShare.setOnClickListener(new OnWeiboClick());
		umengShare.setVisibility(View.GONE);

		faq = (RelativeLayout) findViewById(R.id.faq_setting);
		faq.setOnClickListener(this);
		faq.setVisibility(View.GONE);

		flow3g = (RelativeLayout) findViewById(R.id.flow3g_setting);
		flow3g.setOnClickListener(this);
		
		clearCache = (RelativeLayout) findViewById(R.id.cache_setting);
		clearCache.setOnClickListener(this);
	}

	@Override
	protected void initData() {
//		if ("true".equals(getString(R.string.isenable_push_service))) {
//			pushSettingLine.setVisibility(View.VISIBLE);
//		} else {
//			pushSettingLine.setVisibility(View.GONE);
//		}
		pushBo = new PushMsgBo(mActivity);
		pushBo.registerRefreshMsgBroadcast(new IrefreshMsg() {

			@Override
			public void onRefreshMsg() {
				refreshMsg();
			}
		});

//		createFile(imageDir);
		topTitle.setText(getResources().getString(R.string.setting));
	}

	@Override
	protected int getMainContentViewId() {
		return R.layout.act_setting_page;
	}

	class OnWeiboClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			AlertBaseHelper.showConfirm(mActivity, R.string.setting_bing_umeng_account,
					R.string.setting_confirm_msg, new ConfirmBindClick());
		}

	}

	class ConfirmBindClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			AlertBaseHelper.dismissAlert(mActivity);
			Intent intent = new Intent(mActivity, UmengShareAuthActivity.class);
			intent.putExtra(Key.K_RETURN_TITLE, getString(R.string.home_setting));
			startActivity(intent);
		}
	}

//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if (resultCode == RESULT_OK) {
//			switch (requestCode) {
//			case SystemCallUtil.REQUESTCODE_CAMERA:
//				cutImage(SystemCallUtil.IMAGE_URI, Uri.fromFile(new File(imageDir, homeBgFileName)));
//				break;
//			case SystemCallUtil.REQUESTCODE_PHOTOALBUM:
//				Uri uri = data.getData();
//				cutImage(uri, Uri.fromFile(new File(imageDir, homeBgFileName)));
//				break;
//			case SystemCallUtil.REQUESTCODE_IMAGECUT:
//				String path = imageDir + homeBgFileName;
//				SharedPreferencesUtil.setValue(mContext, Key.K_HOME_BG, path);
//				notifyAndToast();
//				break;
//			default:
//				break;
//			}
//		}
//	}
//
//	private void cutImage(Uri cutImage, Uri saveImage) {
//		SystemCallUtil.ImageCut(mActivity, cutImage, saveImage, AppHelper.getScreenWidth(mContext),
//				AppHelper.getScreenHeight(mContext));
//	}
//
//	private void notifyAndToast() {
//		notifyBgChange();
//		CommonUtils.showToast(mActivity, R.string.setting_change_succes, Toast.LENGTH_SHORT);
//	}
//
//	private void notifyBgChange() {
//		Intent i = new Intent();
//		i.setAction("homeBgChange");
//		LocalBroadcastManager.getInstance(mContext).sendBroadcast(i);
//	}

	class OnShareListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			// 判断是否已登录，登录过则跳转到好友分享页，没登陆，则跳转到登录页面
			boolean isLogin = Boolean.parseBoolean(SharedPreferencesUtil.getValue(
					SettingPageActivity.this, Key.K_IS_LOGIN));
			if (isLogin) {
				Intent smsIntent = new Intent(mContext, SMSShareActivity.class);
				smsIntent.putExtra(Key.K_RETURN_TITLE, getResources().getString(R.string.setting));
				startActivity(smsIntent);
				return;
			} else {
				AlertBaseHelper.showConfirm(mActivity, "提示", "请先登录后分享", new LoginOnclick());
			}
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		int id = v.getId();
		if (id == R.id.feedback_setting) {
			intent = new Intent(mContext, FeedBackActivity.class);
		} else if (id == R.id.push_setting) {
			intent = new Intent(mContext, PushSettingActivity.class);
		} else if (id == R.id.download_setting) {
			intent = new Intent(mContext, ApkListActivity.class);
		} else if (id == R.id.about_setting) {
			intent = new Intent(mContext, FFAboutActivity.class);
		} else if (id == R.id.notification) {
			intent = new Intent(mContext, NotificationActivity.class);
		} else if (id == R.id.flow3g_setting) {
			intent = new Intent(mContext, Flow3gSettingActivity.class);
		}
		else if (id == R.id.faq_setting) {
			String url = Config.UrlConfig.URL_CREDIT_FAQ;
			String versNo = AppHelper.getVersionName(mContext);
			String osType = AppHelper.getOSType();
			String versionType = getString(R.string.version_name_update);
			String cityCode = MenuMgr.getInstance().getCityCode(mContext);
			String cityName = MenuMgr.getInstance().getCityName(mContext);
			url = url + "?versType=" + versionType + "&cityCode=" + cityCode + "&cityName="
					+ cityName + "&osType=" + osType + "&versNo=" + versNo + "&supportSystem="
					+ mContext.getString(R.string.suport_system);
			intent = new Intent(mActivity, BrowserActivity.class);
			intent.putExtra(Key.U_BROWSER_URL, url);
			intent.putExtra(Key.U_BROWSER_TITLE, mActivity.getString(R.string.setting_faq));
		}
		else if (id == R.id.cache_setting) {
			showProgressDialog();
			new ClearCacheTask().execute();
		}
		if (intent != null) {
			intent.putExtra(Key.K_RETURN_TITLE, getResources().getString(R.string.setting));
			startActivity(intent);
		}
	}
	
	/**
	 * 清理缓存
	 * @author Administrator
	 *
	 */
	class ClearCacheTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			if (Environment.getExternalStorageState().equals(
	                Environment.MEDIA_MOUNTED)) {
				String cachepath = Environment.getExternalStorageDirectory() + "/changchuntv/";
				File file = new File(cachepath);
				if(file != null && file.exists()) {
					File[]files = file.listFiles();
					for(int i=0; i<files.length; i++) {
						files[i].delete();
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			dismissProgressDialog();
			CommonUtils.showToast(SettingPageActivity.this, "清理完成");
			super.onPostExecute(result);
		}
		
	}

//	public void showSelect(final Activity activity, final String filePath) {
//		final CharSequence[] picItems = { "默认", "本地图片", "拍照" };
//		AlertDialog dlg = new AlertDialog.Builder(activity).setTitle("选择图片类型")
//				.setItems(picItems, new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int picItem) {
//						if (picItem == 0) {
//							SharedPreferencesUtil.setValue(mContext, Key.K_HOME_BG, "");
//							notifyAndToast();
//						} else if (picItem == 1) {
//							SystemCallUtil.photoAlbum(activity);
//						} else if (picItem == 2) {
//							try {
//								SystemCallUtil.camera(activity, filePath);
//							} catch (Exception e) {
//								Log.e("Exception:" + e);
//							}
//						}
//					}
//				}).create();
//		dlg.show();
//	}

	class LoginOnclick implements OnClickListener {

		@Override
		public void onClick(View v) {
//			Intent intent = new Intent(mContext, LoginActivity.class);
//			Intent intent = new Intent();
//			intent.setClassName(mContext, "cn.ffcs.changchuntv.activity.login.LoginActivity");
//			startActivity(intent);
			IntentUitls.intentActivityOfString(mActivity, "cn.ffcs.changchuntv.activity.login.LoginActivity");
			AlertBaseHelper.dismissAlert(mActivity);
			
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		refreshMsg();
	}

	/**
	 * 刷新消息数
	 */
	private void refreshMsg() {
		if (pushBo == null) {
			pushBo = new PushMsgBo(mActivity);
		}
		int count = pushBo.getNewMsgCount();
		if (count > 0) {
			msgCount.setVisibility(View.VISIBLE);
			msgCount.setText(String.valueOf(count));
		} else {
			msgCount.setVisibility(View.GONE);
		}
	}

	/**
	 * add by linjiafu 2014-09-01 V7 703版本点击设置返回，个人中心页面状态判断设置
	 */
	@Override
	public void finish() {
		setResult(RESULT_OK);
		super.finish();
	}

//	/**
//	 * 文件夹不存在则创建
//	 */
//	private void createFile(String dir) {
//		try {
//			File file = new File(dir);
//			if (!file.exists()) {
//				file.mkdirs();
//			}
//		} catch (Exception e) {
//			Log.e(e.getMessage(), e);
//		}
//	}
}
