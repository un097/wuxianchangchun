package cn.ffcs.widget;

import java.util.HashMap;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;
import cn.ffcs.config.R;

/**
 * <p>Title:   自定义加载框                     </p>
 * <p>Description:                     </p>
 * <p>@author: zhangws                 </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-6-3           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class LoadingDialog extends Dialog {

	private static LoadingDialog loadingDialog = null;

	private static HashMap<Activity, LoadingDialog> dialogMap = new HashMap<Activity, LoadingDialog>();

	public LoadingDialog(Context context) {
		super(context);
	}

	public LoadingDialog(Context context, int theme) {
		super(context, theme);
	}

	public static LoadingDialog getDialog(Activity actvitiy) {
		loadingDialog = dialogMap.get(actvitiy);
		if (loadingDialog == null) {
			loadingDialog = new LoadingDialog(actvitiy, R.style.loading_dialog);
			loadingDialog.setContentView(R.layout.loading_dialog);
			loadingDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
			dialogMap.put(actvitiy, loadingDialog);
		}
		return loadingDialog;
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if (loadingDialog == null) {
			return;
		}

		ImageView imageView = (ImageView) loadingDialog.findViewById(R.id.loading_img);
		AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
//		animationDrawable.start();
	}

	/**
	 * 设置内容
	 * @param strMessage
	 * @return
	 */
	public LoadingDialog setMessage(String strMessage) {
		TextView tvMsg = (TextView) loadingDialog.findViewById(R.id.loading_text);

		if (tvMsg != null) {
			tvMsg.setText(strMessage);
		}

		return loadingDialog;
	}

	@Override
	public void show() {
		try {
			super.show();
		} catch (Exception e) {
			loadingDialog = null;
		}
	}

	@Override
	public void dismiss() {
		try {
			super.dismiss();
			loadingDialog = null;
		} catch (Exception e) {
			loadingDialog = null;
		}
	}

	@Override
	public void cancel() {
		try {
			super.cancel();
			loadingDialog = null;
		} catch (Exception e) {
			loadingDialog = null;
		}
	}
}
