package cn.ffcs.wisdom.web;

import java.util.HashMap;
import java.util.Map;

import android.webkit.WebView;
import cn.ffcs.wisdom.lbs.ILbsCallBack;
import cn.ffcs.wisdom.tools.JsonUtil;

import com.baidu.location.BDLocation;

/**
 * <p>Title:  web定位回调   </p>
 * <p>Description:                     </p>
 * <p>@author: zhangws                 </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2012-10-26           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class WebLbsCallBack implements ILbsCallBack {
	private WebView webView;

	public WebLbsCallBack(WebView webview) {
		this.webView = webview;
	}

	@Override
	public void call(BDLocation location) {
		String loc = null;
		if (location != null) {
			int locType = location.getLocType();
			if (locType != BDLocation.TypeGpsLocation && locType != BDLocation.TypeCacheLocation
					&& locType != BDLocation.TypeNetWorkLocation) {// 不是GPS,不是缓存,不是网络定位，则表示定位失败
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(BaseWebViewHelper.FUNCID,"3");
				map.put(BaseWebViewHelper.ISSUCCED, false);
				loc = JsonUtil.toJson(map);
			} else {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(BaseWebViewHelper.FUNCID,"3");
				map.put(BaseWebViewHelper.ISSUCCED, true);
				map.put("lng", location.getLongitude());
				map.put("lat", location.getLatitude());
				map.put("address", location.getAddrStr());
				loc = JsonUtil.toJson(map);
			}
		} else {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(BaseWebViewHelper.FUNCID,"3");
			map.put(BaseWebViewHelper.ISSUCCED, false);
			loc = JsonUtil.toJson(map);
		}
		webView.loadUrl("javascript:icity_func('" + loc + "')");
	}
}
