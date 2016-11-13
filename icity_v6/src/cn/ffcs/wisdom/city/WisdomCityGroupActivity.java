package cn.ffcs.wisdom.city;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import cn.ffcs.wisdom.base.BaseGroupActivity;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.utils.MenuUtil;
import cn.ffcs.wisdom.city.v6.R;

/**
 * <p>Title:  BaseGroupActivity二级封装类                 </p>
 * <p>Description: 
 * 	建议所有ActivityGroup必须继承该类。该类实现了头部的封装、进度条的显示，后期可视情况增加。
 * </p>
 * <p>@author: liaodl                  </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-3-1            </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public abstract class WisdomCityGroupActivity extends BaseGroupActivity {

	private View mLoadingTip; // 进度栏
	protected View mReturn; // 返回
	private boolean mReturnEnable = true; // 默认显示
	
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
		return null;
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
}
