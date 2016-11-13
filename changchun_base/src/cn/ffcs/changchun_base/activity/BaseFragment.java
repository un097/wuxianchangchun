package cn.ffcs.changchun_base.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BaseFragment extends Fragment {
	
	protected Activity mActivity;
	protected Context mContext;
	protected LayoutInflater mInflater;
	private View view;

	protected static final String TAG = BaseFragment.class.getSimpleName();

	public BaseFragment() {
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mActivity = getActivity();
		this.mContext = getActivity().getApplicationContext();
		init(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(getLayoutId(), null);
			mInflater = inflater;
			initview(view);
		}else{
			((ViewGroup)view.getParent()).removeAllViews();
		}
		return view;
	}
	
	protected void init(Bundle savedInstanceState) {
	}

	public int getLayoutId() {
		return 0;
	}
	
	protected void initview(View view) {
	}

}
