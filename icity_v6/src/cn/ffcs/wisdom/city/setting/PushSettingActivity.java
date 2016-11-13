package cn.ffcs.wisdom.city.setting;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.ffcs.wisdom.city.WisdomCityActivity;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.push.PushMsgBo;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.city.xg.XgManager;
import cn.ffcs.wisdom.push.PushUtil;
import cn.ffcs.wisdom.tools.CommonUtils;

/**
 * 
 * <p>Title: 推送设置        </p>
 * <p>Description: 
 * 设置一些消息，声音，震动，应用启动的开关
 * </p>
 * <p>@author: xzw                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-3-4             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class PushSettingActivity extends WisdomCityActivity implements OnClickListener {

	private RelativeLayout pushSettingLine;
	private RelativeLayout dndSettingLine;
	private RelativeLayout soundSettingLine;
	private RelativeLayout shakeSettingLine;
	private LinearLayout mSecondLinearLayout; // 推送布局

	private ImageView pushSettingImage;
	private ImageView dndSettingImage;
	private ImageView soundSettingImage;
	private ImageView shakeSettingImage;
	private TextView topTitle;


	@Override
	protected void initComponents() {
		topTitle = (TextView) findViewById(R.id.top_title);
		mSecondLinearLayout = (LinearLayout) findViewById(R.id.second_linearlayout);

		pushSettingLine = (RelativeLayout) findViewById(R.id.pullsent_setting_line);
		pushSettingLine.setOnClickListener(this);

		dndSettingLine = (RelativeLayout) findViewById(R.id.dnd_setting_line);
		dndSettingLine.setOnClickListener(this);

		soundSettingLine = (RelativeLayout) findViewById(R.id.sound_setting_line);
		soundSettingLine.setOnClickListener(this);

		shakeSettingLine = (RelativeLayout) findViewById(R.id.shake_setting_line);
		shakeSettingLine.setOnClickListener(this);

		pushSettingImage = (ImageView) findViewById(R.id.pullsent_image);
		dndSettingImage = (ImageView) findViewById(R.id.dnd_image);
		soundSettingImage = (ImageView) findViewById(R.id.sound_image);
		shakeSettingImage = (ImageView) findViewById(R.id.shake_image);

	}

	@Override
	protected void initData() {
		topTitle.setText(getResources().getString(R.string.setting_push));
		// 初始化免打扰模式
		initPushTimeinterval();

		// 初始化推送通知开关
		initPushSetting();
		// 初始化通知声音开关
		initNotifySoundSetting();
		// 初始化通知震动开关
		initNotifyVibrateSetting();
	}

	@Override
	protected int getMainContentViewId() {
		return R.layout.act_pushsetting_page;
	}

	/**
	 * 初始化推送开关设置
	 */
	private void initPushSetting() {
		boolean isResident = PushUtil.getPushEnabled(mContext);
		if (isResident) {
			pushSettingImage.setEnabled(true);
//			mSecondLinearLayout.setVisibility(View.VISIBLE);
		} else {
			pushSettingImage.setEnabled(false);
//			mSecondLinearLayout.setVisibility(View.GONE);
		}
	}

	private void initPushTimeinterval() {
		boolean timeinterval = PushUtil.getPushTimeinterval(mContext);
		if (timeinterval) {
			dndSettingImage.setEnabled(true);
		} else {
			dndSettingImage.setEnabled(false);
		}
	}

	/**
	 * 初始化声音设置
	 */
	private void initNotifySoundSetting() {
		boolean isSound = PushUtil.getPushSoundEnabled(mContext);
		if (isSound) {
			soundSettingImage.setEnabled(true);
		} else {
			soundSettingImage.setEnabled(false);
		}
	}

	/**
	 * 初始化震动设置
	 */
	private void initNotifyVibrateSetting() {
		boolean isVibrate = PushUtil.getPushVibrateEnabled(mContext);
		if (isVibrate) {
			shakeSettingImage.setEnabled(true);
		} else {
			shakeSettingImage.setEnabled(false);
		}
	}

	/**
	 * 改变推送开关设置
	 */
	private void changePushSetting() {
		boolean isResident = PushUtil.getPushEnabled(mContext);
		if (isResident) {
			pushSettingImage.setEnabled(false);
			PushUtil.setPushEnabled(mContext, false);
//			mSecondLinearLayout.setVisibility(View.GONE);
			XgManager.unregisterPush(mContext);
		} else {
			pushSettingImage.setEnabled(true);
			PushUtil.setPushEnabled(mContext, true);
//			mSecondLinearLayout.setVisibility(View.VISIBLE);
			XgManager.xg_register(mContext);
		}
	}

	/**
	 * 改变免打扰开关设置
	 */
	private void changePushTimeinterval() {
		boolean timeinterval = PushUtil.getPushTimeinterval(mContext);
		if (timeinterval) {
			dndSettingImage.setEnabled(false);
			PushUtil.setPushTimeinterval(mContext, false);
		} else {
			dndSettingImage.setEnabled(true);
			PushUtil.setPushTimeinterval(mContext, true);
			// 提示免打扰模式时间
			CommonUtils.showToast(mActivity, R.string.notification_settings_timeinterval,Toast.LENGTH_SHORT);
		}
	}

	/**
	 * 改变声音设置
	 */
	private void changeNotifySoundSetting() {
		boolean isSound = PushUtil.getPushSoundEnabled(mContext);
		if (isSound) {
			soundSettingImage.setEnabled(false);
			PushUtil.setPushSoundEnabled(mContext, false);
		} else {
			soundSettingImage.setEnabled(true);
			PushUtil.setPushSoundEnabled(mContext, true);
		}
	}

	/**
	 * 改变震动设置
	 */
	private void changeNotifyVibrateSetting() {
		boolean isVibrate = PushUtil.getPushVibrateEnabled(mContext);
		if (isVibrate) {
			shakeSettingImage.setEnabled(false);
			PushUtil.setPushVibrateEnabled(mContext, false);
		} else {
			shakeSettingImage.setEnabled(true);
			PushUtil.setPushVibrateEnabled(mContext, true);
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.pullsent_setting_line) {
			changePushSetting();
		} else if (id == R.id.dnd_setting_line) {
			changePushTimeinterval();
		} else if (id == R.id.sound_setting_line) {
			changeNotifySoundSetting();
		} else if (id == R.id.shake_setting_line) {
			changeNotifyVibrateSetting();
		}

	}

}
