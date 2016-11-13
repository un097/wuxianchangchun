package cn.ffcs.wisdom.city;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushManager;

import org.json.JSONException;
import org.json.JSONObject;

import cn.ffcs.widget.LoadingBar;
import cn.ffcs.widget.LoadingDialog;
import cn.ffcs.wisdom.base.BaseActivity;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.utils.MenuUtil;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.city.xg.XgManager;
import cn.ffcs.wisdom.city.xg.XgPushMessageReceiver;
import cn.ffcs.wisdom.notify.NotificationConstants;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.StringUtil;

/**
 * <p>Title: WisdomCityActivity        </p>
 * <p>Description:                     </p>
 * <p>@author: liaodl                  </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-6-19           </p>
 * <p>@author:                         </p>
 * <p>Update Time: 2013-6-19           </p>
 * <p>Updater:  liaodl                 </p>
 * <p>Update Comments:
 *  1.统一用showProgressBar()、hideProgressBar()显示和隐藏自定义进度条。
 * 	2.统一用 showProgressDialog()显示自定义 LoadingDialog。
 *    dismissProgressDialog()关闭进度对话框。
 * </p>
 */
public abstract class WisdomCityActivity extends BaseActivity {

//	private View mLoadingTip; // 进度栏
	protected LoadingBar mLoadingBar; // 进度栏
	protected View mReturn; // 返回
	private boolean mReturnEnable = true; // 默认显示

	protected LoadingDialog mProgress;
	private OnClickListener onReturnClickListener;// 返回监听

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setReturnBtnEnable();
		if (!Config.isSuccess()) {
			Config.init(mContext);
		}
	}

	@Override
	protected Class<?> getResouceClass() {
		return R.class;
	}

	/**
	 * 是否启用返回按钮
	 * @param enable
	 */
	public void setReturnBtnEnable(boolean enable) {
		if (!enable) {
			mReturnEnable = enable;
		}
	}

	/**
	 * 设置返回键
	 */
	private void setReturnBtnEnable() {
		mReturn = findViewById(R.id.btn_return);
		if (mReturn != null) {
			if (!mReturnEnable) {
				mReturn.setVisibility(View.INVISIBLE);
			} else {
				mReturn.setVisibility(View.VISIBLE);
				mReturn.setOnClickListener(new OnReturnClickListener());
				if (mReturn instanceof TextView) {
					String returnTitle = getReturnTitle();
					if (!StringUtil.isEmpty(returnTitle)) {
//						((TextView) mReturn).setText(returnTitle);
					}
				}
			}
		}
	}

	/**
	 * 获取返回标题
	 * @return
	 */
	private String getReturnTitle() {
		String returnTitle = getIntent().getStringExtra(Key.K_RETURN_TITLE);
		if (returnTitle != null)
			return returnTitle;
		else
			return "";
	}

	/**
	 * 设置返回点击监听，替代原来监听
	 * @param listener
	 */
	public void setOnReturnClickListener(OnClickListener listener) {
		onReturnClickListener = listener;
	}

	private class OnReturnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (onReturnClickListener != null) {
				onReturnClickListener.onClick(v);
			} else {
				onBackPressed();
			}
		}
	}

	public void showProgressBar() {
		if (mLoadingBar == null) {
			mLoadingBar = (LoadingBar) findViewById(R.id.loading_bar);
		}
		if (mLoadingBar != null) {
			mLoadingBar.setVisibility(View.VISIBLE);
		}
	}

	public void hideProgressBar() {
		if (mLoadingBar == null) {
			mLoadingBar = (LoadingBar) findViewById(R.id.loading_bar);
		}
		if (mLoadingBar != null) {
			mLoadingBar.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!Config.isSuccess()) {
			Config.init(mContext);
		}
		boolean loadSuccess = MenuMgr.getInstance().getLoadSuccess();
		if (!loadSuccess) {
			String cityCode = MenuUtil.getCityCode(mContext);
			MenuMgr.getInstance().refreshMenu(mContext, cityCode);
		}
	}

	public void showProgressDialog() {
		showProgressDialog("");//默认显示"数据加载，请稍候..."
	}

	public void showProgressDialog(String msg) {
		mProgress = LoadingDialog.getDialog(mActivity);
		if (StringUtil.isEmpty(msg)) {
			msg = getString(R.string.common_loading);
		}
		mProgress.setMessage(msg);
		mProgress.show();
	}

	public void dismissProgressDialog() {
		if (null != mProgress && mProgress.isShowing()) {
			mProgress.dismiss();
		}
	}

	@Override
	public void onBackPressed() {
		launchHomePage();
		super.onBackPressed();
	}

	private void launchHomePage() {
		boolean fromPushFlag = getIntent().getBooleanExtra(NotificationConstants.NOTIFICATION_FLAG, false);
		if (fromPushFlag && !isExitsHomePage()) {
			Intent intent = new Intent();
			intent.setClassName(mContext, "cn.ffcs.changchuntv.activity.home.MainActivity");
			startActivity(intent);
		}
	}

	//首页是否存在(启动过)
	public boolean isExitsHomePage() {
		try {
			ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(
					Context.ACTIVITY_SERVICE);
			List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(2);
			String activityName = "";
			for (RunningTaskInfo info : taskInfo) {
				ComponentName name = info.baseActivity;
				activityName = name.getClassName();
				if ("cn.ffcs.changchuntv.activity.home.MainActivity".equals(activityName)) {
					return true;
				}
			}
		} catch (SecurityException e) {
			Log.e(e.getMessage(), e);
		}
		return false;
	}

	@Override
	public void finish() {
		super.finish();
		dismissProgressDialog();
		launchHomePage();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dismissProgressDialog();
	}
}
