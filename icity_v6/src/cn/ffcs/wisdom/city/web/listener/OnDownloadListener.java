package cn.ffcs.wisdom.city.web.listener;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.DownloadListener;

/**
 * <p>Title:  web下载监听       </p>
 * <p>Description:                     </p>
 * <p>@author: zhangwsh                </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-10-12           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class OnDownloadListener implements DownloadListener {
	private Context context;

	public OnDownloadListener(Context context) {
		this.context = context;
	}

	@Override
	public void onDownloadStart(String url, String userAgent, String contentDisposition,
			String mimetype, long contentLength) {
		if (url.indexOf("?") > 0) {
			url = url + "&ffcs=city";
		} else {
			url = url + "?ffcs=icity";
		}
		Uri uri = Uri.parse(url);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
}
