package com.ctbri.wxcc.widget;

import com.ctbri.wxcc.R;

import android.content.Context;
import android.os.Bundle;

public class LoadingDialog extends MySelfDialog {

	protected LoadingDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public LoadingDialog(Context context, int theme) {
		super(context, theme);
		setCancelable(false);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.common_loading);
	}

	public static LoadingDialog newInstance(Context context){
		return new LoadingDialog(context, R.style.self_dialog_loading);
	}
}
