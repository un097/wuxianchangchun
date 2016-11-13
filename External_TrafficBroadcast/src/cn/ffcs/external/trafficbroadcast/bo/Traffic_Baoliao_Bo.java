package cn.ffcs.external.trafficbroadcast.bo;

import java.io.File;
import java.util.Map;

import android.content.Context;
import cn.ffcs.config.ExternalKey;
import cn.ffcs.external.trafficbroadcast.entity.Traffic_AllList_Entity;
import cn.ffcs.external.trafficbroadcast.entity.Traffic_Baoliao_Entity;
import cn.ffcs.wisdom.base.CommonStandardNewTask;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.resp.UpLoadImageResp;
import cn.ffcs.wisdom.city.task.ImageUpLoadTask;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;

/**
 * 爆料业务逻辑类
 * 
 * @author daizhq
 * 
 * @date 2014.12.11
 * */
public class Traffic_Baoliao_Bo {

	private Context context;

	public Traffic_Baoliao_Bo(Context context) {
		this.context = context;
	}

	public void startRequestTask(HttpCallBack<BaseResp> callBack,
			Context context, Map<String, String> map, String url) {
		CommonStandardNewTask task = CommonStandardNewTask.newInstance(
				callBack, context, Traffic_Baoliao_Entity.class);
		String productId = "changchunTV";
		String clientType = ExternalKey.K_CLIENT_TYPE;
		task.setParams(url, map, productId, clientType, map.get("mobile"),
				map.get("city_code"), map.get("org_code"),
				map.get("longitude"), map.get("latitude"), map.get("sign"));
		task.execute();
	}

	public void startObRequestTask(HttpCallBack<BaseResp> callBack,
			Context context, Map<String, Object> map, String url) {
		CommonStandardNewTask task = CommonStandardNewTask.newInstance(
				callBack, context, Traffic_Baoliao_Entity.class);
		String productId = "changchunTV";
		String clientType = ExternalKey.K_CLIENT_TYPE;
		task.setObjectParams(url, map, productId, clientType, map.get("mobile")
				.toString(), map.get("city_code").toString(),
				map.get("org_code").toString(),
				map.get("longitude").toString(),
				map.get("latitude").toString(), map.get("sign").toString());
		task.execute();
	}
	
	/**
	 * 图片上传
	 * @param call
	 * @param filePath
	 */
	public void imageUpLoad(HttpCallBack<UpLoadImageResp> call, String filePath) {
		ImageUpLoadTask task = new ImageUpLoadTask(call);
		//图片上传地址
		//避免和随手拍图片上传同目录的冲突
//		//爱城市随手拍图片上传地址
//		String upLoadUrl = Config.GET_UP_LOAD_IMAGE_ROOT_URL()
//				+ "uploadFile.action?businessType=photo";
		//长春路况播报图片上传地址
		String upLoadUrl = Config.GET_UP_LOAD_IMAGE_ROOT_URL()
				+ "uploadFile.action?businessType=lbs_road";
		System.out.println("图片请求地址====》》"+upLoadUrl);
		task.setUpUrl(upLoadUrl);
		task.setLocalFilePath(filePath);
		task.execute();
	}

}
