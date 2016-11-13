package cn.ffcs.wisdom.city.utils;

import android.content.Context;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.download.BaseDownLoader;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.StringUtil;

/**
 * <p>Title:  下载器                                                               </p>
 * <p>Description:                     </p>
 * <p>@author: liaodl                  </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-5-23           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class CityDownloader extends BaseDownLoader {

	public static final int CATEGORY_SPLASHS = 0x01;
	public static final int CATEGORY_APK = 0x02;

	private int category;//类别，区分闪屏图、应用汇下载存放目录,默认都存放到SDCARD_IMAAGECACHE

	public CityDownloader(Context context) {
		super(context);
	}

	public void setCategory(int category) {
		this.category = category;
	}

	@Override
	public String getDownDir() {
		String dir = Config.SDCARD_IMAAGECACHE;
		if (CATEGORY_SPLASHS == category) {
			dir = Config.SDCARD_SPLASHS;
		} else if (CATEGORY_APK == category) {
			dir = Config.SDCARD_APK;
		}
		return dir;
	}

	@Override
	public String patternUrl(String url) {
		if (StringUtil.isEmpty(url)) {
			return "";
		} else {
			if (!StringUtil.isEmpty(url) && url.indexOf("http://") < 0) {
				url = Config.GET_IMAGE_ROOT_URL() + url;
			}
			Log.i("CityDownloader url:" + url);
			return url;
		}
	}

}
