package cn.ffcs.ui.tools;

import java.util.HashMap;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import cn.ffcs.config.R;
import cn.ffcs.wisdom.tools.StringUtil;

/**
 * <p>Title:   自定义对话框                        </p>
 * <p>Description:        
 *  功能包括：
 *  <p>可修改标题、提示内容 </p>
 *  <p>可修改"确定"按钮上的文字 </p>
 *  <p>可修改"取消"按钮上的文字 </p>
 *  <p>可自定义"确定"按钮行为</p>
 *  <p>可自定义"取消"按钮行为</p>
 *  <p>注:自定义"确定"按钮行为，记得在自己方法里调用
 *     AlertBaseHelper.dismissAlert(activity);取消对话框显示
 *     否则，按返回键返回上一个Activity时，对话框还存在。用户体验不好。
 *  </p>
 * </p>
 * <p>@author: liaodl                  </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-3-14           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class AlertBaseHelper {

	private static HashMap<String, Dialog> dialogs = new HashMap<String, Dialog>();

	/**
	 * 自定义对话框,可改变标题和提示内容<br/>
	 * 可自定义确定按钮行为
	 * 
	 * @param activity
	 * @param title
	 *            标题
	 * @param sMessage
	 *            内容
	 * @param confirmClick
	 *            点确定动作行为
	 * @param cancelClick
	 *            取消动作行为
	 * @return
	 */
	public static Dialog showConfirm(final Activity activity, String title, String sMessage,
			final OnClickListener confirmClick) {
		dismissAlert(activity);

		Dialog dialog = createBasicDialog(activity);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(false);
		dialogs.put(activity.toString(), dialog);
		TextView dialogTitleName = (TextView) dialog.findViewById(R.id.dialog_title_name);
		TextView dialogContent = (TextView) dialog.findViewById(R.id.dialog_content);
		Button sureBtn = (Button) dialog.findViewById(R.id.dialog_btn_sure);
		Button cancleBtn = (Button) dialog.findViewById(R.id.dialog_btn_cancle);

		if (StringUtil.isEmpty(title)) {
			title = getString(activity, R.string.dialog_title_default);
		}
		if (StringUtil.isEmpty(sMessage)) {
			sMessage = "";
		}
		dialogTitleName.setText(title);
		dialogContent.setText(sMessage);
		sureBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismissAlert(activity);
				if (confirmClick != null) {
					confirmClick.onClick(v);
				}
			}
		});
		cancleBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismissAlert(activity);
			}
		});
		dialog.show();

		return dialog;
	}

	/**
	 * 自定义对话框,可改变标题和提示内容<br/>
	 * 可自定义确定按钮行为
	 */
	public static Dialog showConfirm(final Activity activity, int titleId, int msgId,
			OnClickListener confirmClick) {
		String title = getString(activity, titleId);
		String msg = getString(activity, msgId);
		return showConfirm(activity, title, msg, confirmClick);

	}

	/**
	 * 自定义对话框,可修改标题、提示内容、<br/>
	 * 可修改确定按钮文字、取消按钮文字<br/>
	 * 自定义确定按钮行为、取消按钮行为
	 * @param context
	 * @param title
	 *            标题
	 * @param sMessage
	 *            内容
	 * @param confirmText
	 *            确定按钮文字
	 * @param cancelText
	 *            取消按钮文字
	 * @param confirmClick
	 *            确定点击响应
	 * @param cancelClick
	 *            取消点击响应
	 * @return Dialog
	 */
	public static Dialog showConfirm(final Activity activity, String title, String sMessage,
			String confirmText, String cancelText, final OnClickListener confirmClick,
			final OnClickListener cancelClick) {
		dismissAlert(activity);
		Dialog dialog = createBasicDialog(activity);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(false);
		dialogs.put(activity.toString(), dialog);
		TextView dialogTitleName = (TextView) dialog.findViewById(R.id.dialog_title_name);
		TextView dialogContent = (TextView) dialog.findViewById(R.id.dialog_content);
		Button sureBtn = (Button) dialog.findViewById(R.id.dialog_btn_sure);
		Button cancleBtn = (Button) dialog.findViewById(R.id.dialog_btn_cancle);

		if (StringUtil.isEmpty(title)) {
			title = getString(activity, R.string.dialog_title_default);
		}
		if (StringUtil.isEmpty(sMessage)) {
			sMessage = "";
		}
		if (StringUtil.isEmpty(confirmText)) {
			confirmText = getString(activity, R.string.btn_sure);//确定
		}
		if (StringUtil.isEmpty(cancelText)) {
			cancelText = getString(activity, R.string.btn_cancle);//取消
		}
		dialogTitleName.setText(title);
		dialogContent.setText(sMessage);
		sureBtn.setVisibility(View.VISIBLE);
		cancleBtn.setVisibility(View.VISIBLE);
		sureBtn.setText(confirmText);
		cancleBtn.setText(cancelText);
		sureBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismissAlert(activity);
				if (confirmClick != null) {
					confirmClick.onClick(v);
				}
			}
		});

		cancleBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismissAlert(activity);
				if (cancelClick != null) {
					cancelClick.onClick(v);
				}
			}
		});

		dialog.show();

		return dialog;
	}

	/**
	 * 自定义对话框,可修改标题、提示内容、<br/>
	 * 可修改确定按钮文字、取消按钮文字<br/>
	 * 自定义确定按钮行为、取消按钮行为
	 */
	public static Dialog showConfirm(final Activity activity, int titleId, int msgId,
			int confirmTextId, int cancelTextId, OnClickListener confirmClick,
			OnClickListener cancelClick) {
		String title = getString(activity, titleId);
		String msg = getString(activity, msgId);
		String confirmText = getString(activity, confirmTextId);
		String cancelText = getString(activity, cancelTextId);
		return showConfirm(activity, title, msg, confirmText, cancelText, confirmClick, cancelClick);
	}

	/**
	 * 显示一个确定按钮对话框，对话框标题为"提示"<br/>
	 * 可以修改对话框内容
	 * @param activity
	 * @param sMessage
	 * @return
	 */
	public static Dialog showMessage(Activity activity, String sMessage) {
		return showMessage(activity, sMessage, null);
	}

	/**
	 * 显示一个确定按钮对话框，对话框标题为"提示"<br/>
	 * 可以修改对话框内容和按钮行为
	 * @param activity
	 * @param sMessage 话框内容
	 * @param listener
	 * @return
	 */
	public static Dialog showMessage(final Activity activity, String sMessage,
			final OnClickListener listener) {
		dismissAlert(activity);
		Dialog dialog = createBasicDialog(activity);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(false);
		dialogs.put(activity.toString(), dialog);
		TextView dialogTitleName = (TextView) dialog.findViewById(R.id.dialog_title_name);
		TextView dialogContent = (TextView) dialog.findViewById(R.id.dialog_content);
		Button sureBtn = (Button) dialog.findViewById(R.id.dialog_btn_sure);
		Button cancleBtn = (Button) dialog.findViewById(R.id.dialog_btn_cancle);

		String title = getString(activity, R.string.dialog_title_default);
		if (StringUtil.isEmpty(sMessage)) {
			sMessage = "";
		}
		String confirmText = getString(activity, R.string.btn_sure);//确定

		dialogTitleName.setText(title);
		dialogContent.setText(sMessage);
		sureBtn.setText(confirmText);
		cancleBtn.setVisibility(View.GONE);
		if (listener == null) {
			sureBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dismissAlert(activity);
				}
			});
		} else {
			sureBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dismissAlert(activity);
					listener.onClick(v);
				}
			});
		}
		dialog.show();
		return dialog;
	}

	/**
	 * 显示一个确定按钮对话框，对话框标题为"提示"<br/>
	 * 可以修改对话框内容和按钮行为
	 */
	public static Dialog showMessage(Activity activity, int msgId, OnClickListener listener) {
		String msg = getString(activity, msgId);
		return showMessage(activity, msg, listener);
	}

	/**
	 * 显示一个确定按钮对话框，可以修改对话框标题和内容<br/>
	 * 可以修改对话框内容和按钮行为
	 * @param activity
	 * @param title
	 * @param sMessage
	 * @param listener
	 * @return
	 */
	public static Dialog showMessage(final Activity activity, String title, String sMessage,
			final OnClickListener listener) {
		dismissAlert(activity);
		Dialog dialog = createBasicDialog(activity);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(false);
		dialogs.put(activity.toString(), dialog);
		TextView dialogTitleName = (TextView) dialog.findViewById(R.id.dialog_title_name);
		TextView dialogContent = (TextView) dialog.findViewById(R.id.dialog_content);
		Button sureBtn = (Button) dialog.findViewById(R.id.dialog_btn_sure);
		Button cancleBtn = (Button) dialog.findViewById(R.id.dialog_btn_cancle);

		if (StringUtil.isEmpty(title)) {
			title = getString(activity, R.string.dialog_title_default);
		}
		if (StringUtil.isEmpty(sMessage)) {
			sMessage = "";
		}
		dialogTitleName.setText(title);
		dialogContent.setText(sMessage);
		String confirmText = getString(activity, R.string.btn_sure);//确定
		sureBtn.setText(confirmText);
		cancleBtn.setVisibility(View.GONE);
		if (listener == null) {
			sureBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dismissAlert(activity);
				}
			});
		} else {
			sureBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dismissAlert(activity);
					listener.onClick(v);
				}
			});
		}
		dialog.show();
		return dialog;
	}

	/**
	 * 显示一个确定按钮对话框，可以修改对话框标题和内容<br/>
	 * 可以修改对话框内容和按钮行为
	 */
	public static Dialog showMessage(Activity activity, int titleId, int msgId,
			OnClickListener listener) {
		String title = getString(activity, titleId);
		String msg = getString(activity, msgId);
		return showMessage(activity, title, msg, listener);
	}

	/**
	 * 取消对话框
	 * @param activity
	 */
	public static void dismissAlert(Activity activity) {
		Dialog dialog = (Dialog) dialogs.get(activity.toString());
		if ((dialog != null) && (dialog.isShowing())) {
			dialog.dismiss();
			dialogs.remove(activity.toString());
		}
	}

	/**
	 * 创建一个基于风格为R.style.dialog_basic、内容为R.layout.dialog_basic的空对话框
	 * @param activity
	 * @return
	 */
	private static Dialog createBasicDialog(Activity activity) {
		Dialog dialog = new Dialog(activity, R.style.dialog_basic);
		dialog.setContentView(R.layout.dialog_basic);
		return dialog;
	}

	public static String getString(Activity activity, int resId) {
		return activity.getResources().getString(resId);
	}
}
