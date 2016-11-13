package cn.ffcs.surfingscene.road.bo;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.content.Context;
import cn.ffcs.surfingscene.R;
import cn.ffcs.surfingscene.common.Config;
import cn.ffcs.surfingscene.road.upload.RoadImageUpLoadTask;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.tools.AppHelper;

import com.ffcs.surfingscene.function.CameraList;
import com.ffcs.surfingscene.function.CollectEye;
import com.ffcs.surfingscene.http.HttpCallBack;
import com.ffcs.surfingscene.response.BaseResponse;

public class RoadBo {

	private static RoadBo roadBo;

	public static RoadBo getInstance() {
		if (roadBo == null) {
			roadBo = new RoadBo();
		}
		return roadBo;
	}

	private RoadBo() {

	}

	/**
	 * 获取视频
	 * @param context
	 * @param areaCode
	 * @param gloType
	 * @param iCall
	 */
	public void getVideoList(Context context, String areaCode, String gloType,
			HttpCallBack<BaseResponse> iCall) {
		CameraList camera = new CameraList(context);
		camera.getCameraLsit(areaCode, gloType, iCall, "/geye/list");
	}

	/**
	 * 添加或删除收藏
	 * @param context
	 * @param userId
	 * @param gloType
	 * @param eyeId
	 * @param isDel
	 * @param iCall
	 */
	public void collect(Context context, String userId, String gloType, int eyeId, int isDel,
			HttpCallBack<BaseResponse> iCall) {
		CollectEye collect = new CollectEye(context);
		collect.addOrDelCollect(userId, gloType, "" + eyeId, "" + isDel, iCall, "/geye/collect");
	}

	/**
	 * 获取收藏列表
	 * @param context
	 * @param userId
	 * @param gloType
	 * @param iCall
	 */
	public void getMyCollectList(Context context, String userId, String gloType,
			HttpCallBack<BaseResponse> iCall) {
		CollectEye collect = new CollectEye(context);
		collect.getCollectList(userId, gloType, iCall, "/geye/getCollectList");
	}

	/**
	 * 上报分享
	 * @param context
	 * @param geyeId
	 * @param typeCode
	 * @param actionId
	 */
	public void shareReport(Context context, String geyeId, String typeCode, String actionId) {
		CameraList camera = new CameraList(context);
		camera.shareStatistics(geyeId, typeCode, actionId, new HttpCallBack<BaseResponse>() {

			@Override
			public void callBack(BaseResponse arg0, String arg1) {

			}
		});
	}

	/**
	 * 图片上传
	 * @param path
	 * @param iCall
	 * @throws UnsupportedEncodingException 
	 */
	public void upLoadFile(Context context, String cityCode, String des, String path,
			String mobile, cn.ffcs.wisdom.http.HttpCallBack<BaseResp> iCall)
			throws UnsupportedEncodingException {
		RoadImageUpLoadTask task = new RoadImageUpLoadTask(iCall);
		task.setLocalFilePath(path);
		String url = Config.GET_UP_LOAD_IMAGE_ROOT_URL() + Config.UPLOAD_IMAGE;
		des = URLEncoder.encode(des, "utf-8");
		url = getUrl(context, url, cityCode, des, mobile);
		task.setUpUrl(url);
		task.execute();
	}

	/**
	 * 组装url
	 * @param context
	 * @param url
	 * @param cityCode
	 * @param des
	 * @param mobile
	 * @return
	 */
	private String getUrl(Context context, String url, String cityCode, String des, String mobile) {
		return url + "?cityCode=" + cityCode + "&description=" + des + "&imsi="
				+ AppHelper.getMobileIMSI(context) + "&mobile=" + mobile + "&client_vernum="
				+ AppHelper.getVersionCode(context) + "&os_type=" + AppHelper.getOSTypeNew()
				+ "&client_type=" + context.getString(R.string.version_name_update);
	}
}
