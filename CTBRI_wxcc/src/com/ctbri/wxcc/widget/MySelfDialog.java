package com.ctbri.wxcc.widget;

import android.app.Dialog;
import android.content.Context;

public class MySelfDialog extends Dialog {

	protected MySelfDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}
	public MySelfDialog(Context context,int theme) {
		super(context, theme);
	}
	

}
