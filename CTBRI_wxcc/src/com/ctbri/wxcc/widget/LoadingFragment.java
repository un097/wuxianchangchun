package com.ctbri.wxcc.widget;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

public class LoadingFragment extends DialogFragment {
	private OnCancelListener cancelCallback;
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		setCancelable(true);
		return LoadingDialog.newInstance(getActivity());
	}
	public static LoadingFragment newInstance(){
		return new LoadingFragment();
	} 
	
	public void show(FragmentManager manager, String tag, OnCancelListener callback) {
		super.show(manager, tag);
		this.cancelCallback = callback;
	}
	@Override
	public void dismissAllowingStateLoss() {
		super.dismissAllowingStateLoss();
	}
	
	@Override
	public void onCancel(DialogInterface dialog) {
		if(cancelCallback !=null){
			cancelCallback.onCancel(dialog);
		}
	}
}
