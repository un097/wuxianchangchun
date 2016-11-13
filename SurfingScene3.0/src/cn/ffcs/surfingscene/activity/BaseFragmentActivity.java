package cn.ffcs.surfingscene.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;
import cn.ffcs.surfingscene.R;

/**
 * <p>Title: 继承FragmentActivity         </p>
 * <p>Description: 
 * </p>
 * <p>@author: Leo                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-6-17             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public abstract class BaseFragmentActivity extends FragmentActivity {
	protected Context mContext;
	protected Activity mActivity;

	private Handler mHandler;

	private View mLoadingTip; // 进度栏
	protected View mReturn; // 返回
	private boolean mReturnEnable = true; // 默认显示

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); // remove title

		setContentView(getMainContentViewId()); // set view
		setReturnBtnEnable();
		mContext = getApplicationContext(); // get context
		mActivity = this;
		initComponents(); // init all components

		initData(); // init the whole activity's data
	}

	protected abstract void initComponents();

	protected abstract int getMainContentViewId();

	protected abstract void initData();

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
	 * 获取返回标题
	 * @return
	 */
	private String getReturnTitle() {
		String returnTitle = getIntent().getStringExtra("k_return_title");
		if (returnTitle != null)
			return returnTitle;
		else
			return "";
	}

	/**
	 * 设置返回键
	 */
	private void setReturnBtnEnable() {
		mReturn = findViewById(R.id.btn_return);
		if (mReturn != null) {
			if (!mReturnEnable) {
				mReturn.setVisibility(View.GONE);
			} else {
				mReturn.setVisibility(View.VISIBLE);
				mReturn.setOnClickListener(new OnReturnClickListener());
				if (mReturn instanceof TextView) {
					((TextView) mReturn).setText(getReturnTitle());
				}
			}
		}
	}

	private class OnReturnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			onBackPressed();
		}
	}

	public void showProgressBar() {
		if (mLoadingTip == null) {
			mLoadingTip = findViewById(R.id.loading_bar);
		}
		if (mLoadingTip != null) {
			mLoadingTip.setVisibility(View.VISIBLE);
		}
	}

	public void hideProgressBar() {
		if (mLoadingTip == null) {
			mLoadingTip = findViewById(R.id.loading_bar);
		}
		if (mLoadingTip != null) {
			mLoadingTip.setVisibility(View.GONE);
		}
	}

	protected void delayTask(Runnable task, long delayMillis) {
		if (mHandler == null) {
			mHandler = new Handler();
		}
		mHandler.postDelayed(task, delayMillis);
	}
}
