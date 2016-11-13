package cn.ffcs.external.share.view;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import cn.ffcs.external.share.adapter.CustomShareImageAdapter;
import cn.ffcs.external.share.adapter.CustomShareTextAdapter;
import cn.ffcs.external.share.config.CustomSocialConfig;
import cn.ffcs.external.share.entity.CustomSocialEntity;
import cn.ffcs.external.share.entity.CustomSocialShareEntity;

import com.example.external_socialshare.R;

/**
 * <p>Title: 自定义分享         </p>
 * <p>Description: 
 * 
 * </p>
 * <p>@author: Leo               </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-11-16             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class CustomSocialShare {
	private static HashMap<String, Dialog> dialogs = new HashMap<String, Dialog>();

	/**
	 * 创建对话框
	 * @param activity
	 * @return
	 */
	public static Dialog createDialog(Activity activity) {
		Dialog dialog = new Dialog(activity, R.style.custom_socialshare_alert_dialog);
		dialog.setContentView(R.layout.custom_share_platform_dialog);
		return dialog;
	}

	/**
	 * 分享图片统一入口
	 * @param activity
	 * @param entity  分享参数
	 * @param showPhoto 是否显示随手拍
	 * @return
	 */
	public static Dialog shareImagePlatform(final Activity activity,
			CustomSocialShareEntity entity, boolean showPhoto) {
		dismissAlertDialog(activity);
		Dialog dialog = createDialog(activity);
		dialog.setCanceledOnTouchOutside(true);
		dialogs.put(activity.toString(), dialog);
		GridView shareGrid = (GridView) dialog.findViewById(R.id.custom_share_platform_gridview);
		List<CustomSocialEntity> list = CustomSocialConfig.fillImageSocialData(entity, showPhoto);
		CustomShareImageAdapter adapter = new CustomShareImageAdapter(activity, list, showPhoto);
		shareGrid.setAdapter(adapter);
		Window window = dialog.getWindow();
		window.getAttributes().width = WindowManager.LayoutParams.MATCH_PARENT;
		window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
		dialog.show();
		return dialog;
	}

	/**
	 * 分享文字，带有链接
	 * @param activity
	 * @param entity
	 * @return
	 */
	public static Dialog shareTextPlatform(final Activity activity, String shareTitle,
			String shareContent, String shareUrl) {
		dismissAlertDialog(activity);
		Dialog dialog = createDialog(activity);
		dialog.setCanceledOnTouchOutside(true);
		dialogs.put(activity.toString(), dialog);
		GridView shareGrid = (GridView) dialog.findViewById(R.id.custom_share_platform_gridview);
		List<CustomSocialEntity> list = CustomSocialConfig.fillTextSocialData(shareContent,
				shareUrl);
		CustomShareTextAdapter adapter = new CustomShareTextAdapter(activity, list, shareTitle,
				shareContent, shareUrl);
		shareGrid.setAdapter(adapter);
		Window window = dialog.getWindow();
		window.getAttributes().width = WindowManager.LayoutParams.MATCH_PARENT;
		window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
		dialog.show();
		return dialog;
	}

	/**
	 * 销毁对话框
	 * @param activity
	 */
	public static void dismissAlertDialog(Activity activity) {
		Dialog dialog = (Dialog) dialogs.get(activity.toString());
		if ((dialog != null) && (dialog.isShowing())) {
			dialog.dismiss();
			dialogs.remove(activity.toString());
		}
	}
}
