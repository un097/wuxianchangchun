package cn.ffcs.wisdom.image;

import net.tsz.afinal.bitmap.core.BitmapDisplayConfig;
import net.tsz.afinal.bitmap.display.Displayer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;

/**
 * <p>Title:                           </p>
 * <p>Description:                     </p>
 * <p>@author: zhangws                 </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-6-5           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class BaseDisplayer implements Displayer {

	private Context context;

	BaseDisplayer(Context context) {
		this.context = context;
	}

	@Override
	public void loadCompletedisplay(View view, Bitmap bitmap, BitmapDisplayConfig config) {
		if (view instanceof ImageView) {
			((ImageView) view).setImageBitmap(bitmap);
		} else {
			view.setBackgroundDrawable(new BitmapDrawable(context.getResources(), bitmap));
		}
	}

	@Override
	public void loadFailDisplay(View view, Bitmap bitmap) {
		if (view instanceof ImageView) {
			((ImageView) view).setImageBitmap(bitmap);
		} else {
			view.setBackgroundDrawable(new BitmapDrawable(context.getResources(), bitmap));
		}
	}
}
