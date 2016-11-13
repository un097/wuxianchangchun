package cn.ffcs.wisdom.city.simico.image;

import cn.ffcs.wisdom.city.simico.base.Application;
import cn.ffcs.wisdom.city.simico.kit.log.TLog;
import net.tsz.afinal.bitmap.core.BitmapDisplayConfig;
import net.tsz.afinal.bitmap.display.Displayer;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * @author: zhangws
 * </p>
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * <p>
 * Company: ffcs Co., Ltd.
 * </p>
 * <p>
 * Create Time: 2013-6-5
 * </p>
 * <p>
 * @author:
 * </p>
 * <p>
 * Update Time:
 * </p>
 * <p>
 * Updater:
 * </p>
 * <p>
 * Update Comments:
 * </p>
 */
public class BaseDisplayer implements Displayer {
	public static final int i = 0;

	@Override
	public void loadCompletedisplay(View view, Bitmap bitmap, BitmapDisplayConfig config) {
		if (view instanceof ImageView) {
			TLog.log("TEST", "loadCompletedisplay");
			((ImageView) view).setImageBitmap(bitmap);
			((ImageView) view).setScaleType(ScaleType.CENTER_CROP);
		} else {
			view.setBackgroundDrawable(new BitmapDrawable(Application.instance().getResources(),
					bitmap));
		}
	}

	@Override
	public void loadFailDisplay(View view, Bitmap bitmap) {
		if (view instanceof ImageView) {
			TLog.log("TEST", "loadFailDisplay");
			//((ImageView) view).setImageBitmap(bitmap);
			//((ImageView) view).setScaleType(ScaleType.CENTER);
		} else {
			view.setBackgroundDrawable(new BitmapDrawable(Application.instance().getResources(),
					bitmap));
		}
	}
}
