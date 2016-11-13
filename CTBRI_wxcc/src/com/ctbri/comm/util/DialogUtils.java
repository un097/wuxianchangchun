package com.ctbri.comm.util;

import android.content.DialogInterface.OnCancelListener;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

import com.ctbri.wxcc.widget.LoadingFragment;

public class DialogUtils {

	public static final  String KEY_LOADING_DIALOG_TAG = "loading_dialog";
	public static LoadingFragment showLoading(FragmentManager manager){
		LoadingFragment loading = (LoadingFragment)manager.findFragmentByTag(KEY_LOADING_DIALOG_TAG);
		//如果已经显示了一个 loading ，则不再显示
		if(loading !=null){
			return loading;
		}
		loading = LoadingFragment.newInstance();
		loading.show(manager, KEY_LOADING_DIALOG_TAG);
		return loading;
	}
	public static LoadingFragment showLoading(FragmentManager manager, OnCancelListener callback){
		LoadingFragment loading = (LoadingFragment)manager.findFragmentByTag(KEY_LOADING_DIALOG_TAG);
		//如果已经显示了一个 loading ，则不再显示
		if(loading !=null){
			return loading;
		}
		loading = LoadingFragment.newInstance();
		loading.show(manager, KEY_LOADING_DIALOG_TAG, callback);
		return loading;
	}
	/**
	 * 隐藏该 fragment manager  中 tag 为 {@value #KEY_LOADING_DIALOG_TAG} 的 {@link DialogFragment}
	 * @param manager
	 */
	public static void hideLoading(FragmentManager manager){
		LoadingFragment fragment = (LoadingFragment)manager.findFragmentByTag(KEY_LOADING_DIALOG_TAG);
		if(fragment!=null)
			fragment.dismissAllowingStateLoss();
	}
}
