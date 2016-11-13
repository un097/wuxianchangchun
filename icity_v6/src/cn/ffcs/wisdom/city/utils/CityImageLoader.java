package cn.ffcs.wisdom.city.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.image.BaseImageLoader;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.StringUtil;

/**
 * <p>Title:   爱城市图片加载                      </p>
 * <p>Description:                     </p>
 * <p>@author: zhangws                 </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-5-22           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class CityImageLoader extends BaseImageLoader {

	private boolean isRealTime = false;
	private Bitmap defaultFailLoading;

	public CityImageLoader(Context context) {
		super(context);
		defaultFailLoading = bitMap.get(R.drawable.icon_default);
		if (defaultFailLoading == null) {
			defaultFailLoading = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.icon_default);
			bitMap.put(R.drawable.icon_default, defaultFailLoading);
		}
	}

	@Override
	public String getImageDir() {
		return Config.SDCARD_IMAAGECACHE;
	}

	@Override
	public Bitmap getLoadFailImage() {
		return defaultFailLoading;
	}

	@Override
	public String patternUrl(String url) {
		if (StringUtil.isEmpty(url)) {
			return "";
		} else {
			if (!StringUtil.isEmpty(url) && url.indexOf("http://") < 0) {
				if (isRealTime) {
					url = Config.GET_REAL_TIME_IMAGE_URL() + url;
				} else {
					url = Config.GET_IMAGE_ROOT_URL() + url;
				}
			}
			Log.i("cityPatternUrl url:" + url);
			return url;
		}
	}

	@Override
	public float getMemoryCacheSizePercent() {
		return 0.8f;
	}

	@Override
	public int getThreadCount() {
		return 20;
	}

	/**
	 * 更新图片时是否立即获取可显示的更新
	 * @param isRealTime, true is RealTime 
	 */
	public void setIsRealTimeShowImage(boolean isRealTime) {
		this.isRealTime = isRealTime;
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
