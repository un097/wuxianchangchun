package cn.ffcs.wisdom.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * <p>Title: Fragment基类  </p>
 * <p>Description: 
 * Fragment基类 
 * </p>
 * <p>@author: Eric.wsd                </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Create Time: 2013-5-27             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public abstract class BaseFragment extends Fragment {
	protected Activity mActivity;
	protected Context mContext;
	protected LayoutInflater mInflater;
	private View view;

	public abstract void initComponents(View view);

	public abstract void initData();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mActivity = getActivity();
		this.mContext = getActivity().getApplicationContext();
//		registerException();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(getMainContentViewId(), null);
			mInflater = inflater;
			initComponents(view);
			initData();
		}else{
			((ViewGroup)view.getParent()).removeAllViews();
		}
		return view;
	}

	public abstract int getMainContentViewId();

	/**
	 * 显示进度条
	 */
	public void showProgressBar() {

	}

	/**
	 * 隐藏进度条
	 */
	public void hideProgressBar() {

	}
}
