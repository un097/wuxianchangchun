package cn.ffcs.wisdom.tools;

import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

/**
 * <p>Title: 对话框辅助类-旧版                                             </p>
 * <p>Description:   
 * 	已过期，被AlertBaseHelper替代
 * </p>
 * <p>@author: liaodl                  </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-3-15           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
@Deprecated
public class AlertHelper {

	private static HashMap<String, Dialog> dialogs = new HashMap<String, Dialog>();

	public static AlertDialog showConfirm(Activity activity, String sMessage,
			DialogInterface.OnClickListener confirmClick,
			DialogInterface.OnClickListener cancelClick) {
		dismissAlert(activity);
		AlertDialog alertDlg = new AlertDialog.Builder(activity).create();
		dialogs.put(activity.toString(), alertDlg);
		alertDlg.setTitle("提示");
		alertDlg.setMessage(sMessage);
		alertDlg.setButton("确定", confirmClick);
		alertDlg.setButton2("取消", cancelClick);
		alertDlg.setCanceledOnTouchOutside(false);
		alertDlg.show();
		return alertDlg;
	}

	/**
	 * 自定义对话框
	 * @param context
	 * @param title 标题
	 * @param sMessage 内容
	 * @param confirmText 确定按钮文字
	 * @param cancelText 取消按钮文字
	 * @param confirmClick 确定点击响应
	 * @param cancelClick 取消点击响应
	 * @return AlertDialog
	 */
	public static AlertDialog showConfirm(Activity activity, String title, String sMessage,
			String confirmText, String cancelText, DialogInterface.OnClickListener confirmClick,
			DialogInterface.OnClickListener cancelClick) {
		dismissAlert(activity);
		AlertDialog alertDlg = new AlertDialog.Builder(activity).create();
		dialogs.put(activity.toString(), alertDlg);
		alertDlg.setTitle(title);
		alertDlg.setMessage(sMessage);
		alertDlg.setButton(confirmText, confirmClick);
		alertDlg.setButton2(cancelText, cancelClick);
		alertDlg.show();
		return alertDlg;
	}

	public static AlertDialog showMessage(Activity activity, String sMessage) {
		return showMessage(activity, sMessage, null);
	}

	public static AlertDialog showMessage(Activity activity, String sMessage,
			DialogInterface.OnClickListener listener) {
		dismissAlert(activity);
		AlertDialog alertDlg = new AlertDialog.Builder(activity).create();
		dialogs.put(activity.toString(), alertDlg);
		alertDlg.setTitle("提示");
		alertDlg.setMessage(sMessage);
		DialogInterface.OnClickListener click = null;
		if (listener != null) {
			click = listener;
		} else {
			click = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface iDlg, int which) {
					iDlg.cancel();
				}
			};
		}
		alertDlg.setButton("确定", click);

		alertDlg.show();
		return alertDlg;
	}

	public static AlertDialog showMessage(Activity activity, String title, String sMessage,
			DialogInterface.OnClickListener listener) {
		dismissAlert(activity);
		AlertDialog alertDlg = new AlertDialog.Builder(activity).create();
		dialogs.put(activity.toString(), alertDlg);
		alertDlg.setTitle(title);
		alertDlg.setMessage(sMessage);
		DialogInterface.OnClickListener click = null;
		if (listener != null) {
			click = listener;
		} else {
			click = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface iDlg, int which) {
					iDlg.cancel();
				}
			};
		}
		alertDlg.setButton("确定", click);

		alertDlg.show();
		return alertDlg;
	}

	public static void dismissAlert(Activity activity) {
		Dialog dialog = (Dialog) dialogs.get(activity.toString());
		if ((dialog != null) && (dialog.isShowing())) {
			dialog.dismiss();
			dialogs.remove(activity.toString());
		}
	}
}
