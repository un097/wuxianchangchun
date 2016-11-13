package cn.ffcs.surfingscene.road;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import cn.ffcs.surfingscene.R;
import cn.ffcs.surfingscene.common.Key;
import cn.ffcs.wisdom.base.BaseGroupActivity;

public abstract class GlobaleyeBaseGroupActivity extends BaseGroupActivity {

	private View mLoadingTip; // 进度栏
	protected View mReturn; // 返回
	private boolean mReturnEnable = true; // 默认显示
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setReturnBtnEnable();
		
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
}
