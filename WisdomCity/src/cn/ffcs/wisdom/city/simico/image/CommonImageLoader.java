package cn.ffcs.wisdom.city.simico.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;
import cn.ffcs.wisdom.city.R;
import cn.ffcs.wisdom.city.common.Config;

/**
 * <p>Title: 图片下载管理         </p>
 * <p>Description: 
 * </p>
 * <p>@author: Leo                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-7-4             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class CommonImageLoader extends BaseImageLoader {
	private Bitmap defaultFailLoading;
	private final String imageCacheDir = "/changchuntv/seven/image/cache/";

	public CommonImageLoader(Context context) {
		super(context);
		defaultFailLoading = bitMap.get(R.drawable.simico_picture_error);
		if (defaultFailLoading == null) {
			defaultFailLoading = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.simico_picture_error);
			bitMap.put(R.drawable.simico_picture_error, defaultFailLoading);
		}
	}

	@Override
	public String getImageDir() {
		String dirRoot = getSdcardDir() + imageCacheDir;
		return dirRoot;
	}

	/**
	 * sdcard root directory
	 */
	public static String getSdcardDir() {
		return Environment.getExternalStorageDirectory().toString();
	}

	@Override
	public Bitmap getLoadFailImage() {
		return defaultFailLoading;
	}

	@Override
	public float getMemoryCacheSizePercent() {
		return 0.8f;
	}

	@Override
	public int getThreadCount() {
		return 20;
	}

	@Override
	public String patternUrl(String url) {
		if (TextUtils.isEmpty(url)) {
			return "";
		} else {
			if (!TextUtils.isEmpty(url) && url.indexOf("http://") < 0) {
				url = getImageRoot() + url;
			}
			return url;
		}
	}

	/**
	 * 获取图片服务器地址及端口号
	 */
	private String getImageRoot() {
		return Config.GET_IMAGE_ROOT_URL();
	}

	/**
	 * 设置默认失败图
	 * 
	 * @param defaultFalidImage
	 */
	public void setDefaultFailImage(int defaultFalidImage) {
		if (defaultFalidImage == 0) {
			defaultFailLoading = null;
		} else {
			defaultFailLoading = bitMap.get(defaultFalidImage);
			if (defaultFailLoading == null) {
				defaultFailLoading = BitmapFactory.decodeResource(mContext.getResources(),
						defaultFalidImage);
				bitMap.put(defaultFalidImage, defaultFailLoading);
			}
		}
	}

//	public void resetScaleType(ImageView v) {
//		BitmapDrawable db = (BitmapDrawable) v.getDrawable();
//		if (db != null) {
//			Bitmap bm = db.getBitmap();
//			if (bm != null && !bm.isRecycled()) {
//				if (bm == getLoadFailImage()) {
//					v.setScaleType(ScaleType.CENTER);
//					return;
//				}
//			}
//		}
//		v.setScaleType(ScaleType.CENTER_CROP);
//	}
}
