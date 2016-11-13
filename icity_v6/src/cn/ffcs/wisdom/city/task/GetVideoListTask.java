package cn.ffcs.wisdom.city.task;

import android.content.Context;
import cn.ffcs.wisdom.base.BaseTask;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.http.HttpRequest;
import cn.ffcs.wisdom.tools.Log;

/**
 * 获取视频列表
 * @author Eric.wsd
 * @since 2012-4-10
 */
public class GetVideoListTask extends BaseTask<Void, Void, Void> {

	private String videoTypeId;

	public GetVideoListTask(HttpCallBack<BaseResp> iCall, Context context, String videoType) {
		super(iCall, context);
		this.videoTypeId = videoType;
	}

	@Override
	protected BaseResp doInBackground(Void... params) {
		HttpRequest request = new HttpRequest(BaseResp.class);
		String url = Config.UrlConfig.URL_VIDEO_LIST + "?videotypes=" + videoTypeId;
		BaseResp resp = null;
		try {
			resp = request.execute(url);
		} catch (Exception e) {
			Log.e("Exception:" + e);
			if (resp == null) {
				resp = new BaseResp();
				resp.setStatus(BaseResp.ERROR);
			}
		} finally {
			request.release();
		}
		return resp;
	}
}
