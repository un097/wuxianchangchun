package cn.ffcs.surfingscene.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import cn.ffcs.surfingscene.R;
import cn.ffcs.surfingscene.common.Config;
import cn.ffcs.wisdom.image.BaseImageLoader;

/**
 * <p>Title: 图片下载         </p>
 * <p>Description: 
 * </p>
 * <p>@author: Leo                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-6-19             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class GloImageLoader extends BaseImageLoader {
	private Bitmap defaultFailLoading;

	public GloImageLoader(Context context) {
		super(context);
		defaultFailLoading = bitMap.get(R.drawable.glo_city_banner_default);
		if (defaultFailLoading == null) {
			defaultFailLoading = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.glo_city_banner_default);
			bitMap.put(R.drawable.glo_city_banner_default, defaultFailLoading);
		}
	}

	@Override
	public String getImageDir() {
		return Config.getSdcardImageRoot(mContext);
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
		return url;
	}

	/**
	 * 设置默认失败图
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
	
}
