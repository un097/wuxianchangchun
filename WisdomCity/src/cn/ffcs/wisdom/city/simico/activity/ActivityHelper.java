package cn.ffcs.wisdom.city.simico.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import cn.ffcs.wisdom.city.R;
//import cn.ffcs.wisdom.city.simico.activity.search.SearchActivity;
//import cn.ffcs.wisdom.city.simico.activity.service.ServiceActivity;
import cn.ffcs.wisdom.city.simico.base.Constants;
import cn.ffcs.wisdom.city.simico.ui.notify.PinterestDialog;
import cn.ffcs.wisdom.city.simico.ui.notify.WaitDialog;

public class ActivityHelper {

	public ActivityHelper() {
	}

	public static PinterestDialog getPinterestDialog(Context context) {
		return new PinterestDialog(context, R.style.dialog_pinterest);
	}

	public static PinterestDialog getPinterestDialogCancelable(Context context) {
		PinterestDialog dialog = new PinterestDialog(context,
				R.style.dialog_pinterest);
		dialog.setCanceledOnTouchOutside(true);
		return dialog;
	}

	public static WaitDialog getWaitDialog(Activity activity, int message) {
		WaitDialog dialog = null;
		try {
			dialog = new WaitDialog(activity, R.style.dialog_waiting);
			dialog.setMessage(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dialog;
	}

	public static WaitDialog getWaitDialog(Activity activity, String message) {
		WaitDialog dialog = null;
		try {
			dialog = new WaitDialog(activity, R.style.dialog_waiting);
			dialog.setMessage(message);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return dialog;
	}

	public static WaitDialog getCancelableWaitDialog(Activity activity,
			String message) {
		WaitDialog dialog = null;
		try {
			dialog = new WaitDialog(activity, R.style.dialog_waiting);
			dialog.setMessage(message);
			dialog.setCancelable(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return dialog;
	}

//	public static Intent getServiceIntent(Context context) {
//		return new Intent(context, ServiceActivity.class);
//	}
//	
//	public static Intent getSearchIntent(Context context) {
//		return new Intent(context, SearchActivity.class);
//	}
	
	public static void sendIntentExitApp(Context context) {
		context.sendBroadcast(new Intent(Constants.INTENT_ACTION_EXIT_APP));
	}
}
