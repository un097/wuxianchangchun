package cn.ffcs.surfingscene.road.upload;

import cn.ffcs.wisdom.base.BaseTask;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.http.HttpRequest;
import cn.ffcs.wisdom.tools.JsonUtil;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.StringUtil;

/**
 * <p>Title:     图片上传线程                   </p>
 * <p>Description:   请调用setLocalFilePath设置把本地文件路径，
 * 	setUpUrl设置上传服务器路径         </p>
 * <p>@author: zhangws                 </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-2-25           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class RoadImageUpLoadTask extends BaseTask<Void, Void, Void> {

	private String filePath = "";
	private String url;

	public RoadImageUpLoadTask(HttpCallBack<BaseResp> iCall) {
		super(iCall);
	}

	public void setLocalFilePath(String path) {
		this.filePath = path;
	}

	public void setUpUrl(String url) {
		this.url = url;
	}

	@Override
	protected BaseResp doInBackground(Void... param) {
		BaseResp resp = new BaseResp();
		HttpRequest httpRequest = new HttpRequest(BaseResp.class);
		String result = httpRequest.uploadFile(url, filePath);
		try {
			if (!StringUtil.isEmpty(result)) {
				RoadUpLoadImageEntity entity = JsonUtil.toObject(result, RoadUpLoadImageEntity.class);
				resp.setResult(result);
				resp.setDesc(entity.desc);
				if ("0".equals(entity.status)) {
					resp.setObj(entity);
					resp.setStatus(BaseResp.SUCCESS);
				} else {
					resp.setStatus(BaseResp.ERROR);
				}
			} else {
				resp.setStatus(BaseResp.ERROR);
				resp.setDesc("上传文件失败！");
			}
		} catch (Exception e) {
			Log.e(e.getMessage());
			resp.setStatus(BaseResp.ERROR);
			resp.setDesc("上传文件失败！");
		}
		return resp;
	}
}
