package cn.ffcs.surfingscene.advert;

import cn.ffcs.surfingscene.tools.GloImageLoader;
import android.content.Context;

public class AdvertBo {

	public void loadAdvertImage(Context context, String url) {
		GloImageLoader loader = new GloImageLoader(context);
		loader.loadUrl(null, url);
	}
}
