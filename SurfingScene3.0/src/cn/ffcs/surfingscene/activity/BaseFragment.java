package cn.ffcs.surfingscene.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment extends Fragment {
	protected Context mContext;
	
	protected Handler mHandler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity().getApplication();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = setOnCreateView(inflater, container, savedInstanceState);// 初始化控件
		initData();// 初始化数据
		return view;
	}

	protected abstract View setOnCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState);

	protected abstract void initData();

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	protected void delayTask(Runnable task, long delayMillis) {
		if (mHandler == null) {
			mHandler = new Handler();
		}
		mHandler.postDelayed(task, delayMillis);
	}
}
