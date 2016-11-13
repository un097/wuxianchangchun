package cn.ffcs.external.trafficbroadcast.bo;

import java.io.File;
import java.util.Map;

import android.content.Context;
import cn.ffcs.config.ExternalKey;
import cn.ffcs.external.trafficbroadcast.entity.Traffic_AllList_Entity;
import cn.ffcs.wisdom.base.CommonStandardNewTask;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.resp.UpLoadImageResp;
import cn.ffcs.wisdom.city.task.ImageUpLoadTask;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;

/**
 * 图片上传业务逻辑类
 * 
 * @author daizhq
 * 
 * @date 2014.12.16
 * */
public class Traffic_PicUpload_Bo {
	
	private Context context;

	public Traffic_PicUpload_Bo(Context context) {
		this.context = context;
	}

	/**
	 * 图片上传
	 * @param call
	 * @param filePath
	 */
	public void imageUpLoad(HttpCallBack<UpLoadImageResp> call, String filePath, String cityCode) {
		ImageUpLoadTask task = new ImageUpLoadTask(call);
		//图片上传地址
		String upLoadUrl = "ccgd-pic.153.cn:51181" +  File.separator 
				+ "uploadFile.action?businessType=photo&cityCode=" + cityCode;
		task.setUpUrl(upLoadUrl);
		task.setLocalFilePath(filePath);
		task.execute();
	}
}
