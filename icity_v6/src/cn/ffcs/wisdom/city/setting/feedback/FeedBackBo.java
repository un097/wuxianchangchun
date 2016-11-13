package cn.ffcs.wisdom.city.setting.feedback;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import cn.ffcs.wisdom.base.CommonTask;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.entity.FeedbackEntity;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.resp.UpLoadImageResp;
import cn.ffcs.wisdom.city.task.ImageUpLoadTask;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.ManifestUtil;
import cn.ffcs.wisdom.tools.StringUtil;

/**
 * 
 * <p>Title: 意见反馈处理类        </p>
 * <p>Description: 
 *1.发送意见反馈信息
 *2.请求自己反馈的信息
 * </p>
 * <p>@author: xzw               </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-3-4             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class FeedBackBo {
	private Context mContext;
	private HttpCallBack<BaseResp> mCall;

	public FeedBackBo(Context mContext, HttpCallBack<BaseResp> call) {
		this.mContext = mContext;
		this.mCall = call;
	}

	/**
	 * 图片上传
	 */
	public void uploadImage(HttpCallBack<UpLoadImageResp> iCall, String filePath, String cityCode) {
		ImageUpLoadTask task = new ImageUpLoadTask(iCall);
		String upLoadUrl = Config.GET_UP_LOAD_IMAGE_ROOT_URL()
				+ "uploadFile.action?businessType=photo&cityCode=" + cityCode;// 图片上传地址;
		task.setUpUrl(upLoadUrl);
		task.setLocalFilePath(filePath);
		task.execute();
	}

	/*
	 * 发送意见
	 */
	public void sendFeedBack(String content, String ImageUrl, String source) {
		Map<String, String> params = new HashMap<String, String>(1);
		String versionName = AppHelper.getVersionName(mContext);// 获取版本名称
		int versionCode = AppHelper.getVersionCode(mContext);// 获取版本号
		String Brand = AppHelper.getBrand();
		String model = AppHelper.getModel();
		String cityCode = MenuMgr.getInstance().getCityCode(mContext);
		model = Brand + model;
		String imsi = AppHelper.getMobileIMSI(mContext);// 获取imis号
		String mobile = AccountMgr.getInstance().getMobile(mContext);// 获取手机号码
		params.put("imsi", imsi);// 手机mis号
		params.put("adviceType", "其他");// 信息类型
		params.put("advice", content);// 反馈内容
		params.put("mobile", mobile);// 手机号
		params.put("os_type", "1"); // Android系统类型为1
		String clientType = mContext.getString(R.string.version_name_update);
		params.put("client_type", clientType);// 客户端类型
		params.put("client_version", String.valueOf(versionCode));// 版本号
		params.put("versionName", versionName);// 版本名称
		params.put("model", model);// 手机型号
		params.put("city_code", cityCode);
		if (!StringUtil.isEmpty(ImageUrl) && !StringUtil.isEmpty(source)) {
			params.put("filePath", ImageUrl);
			params.put("fileSource", source);
		}
		// 获取操作系统版本
		String osRelease = AppHelper.getOSRelease();
		params.put("systemVersion", osRelease);
		
		String clientChannelType = ManifestUtil.readMetaData(mContext, "UMENG_CHANNEL");
		params.put("clientChannelType", clientChannelType);
		params.put("client_channel_type", clientChannelType);
		String imei = AppHelper.getIMEI(mContext);
		params.put("imei", imei);

		String url = Config.UrlConfig.URL_SEND_ADVICE;
		CommonTask task = CommonTask.newInstance(mCall, mContext, BaseResp.class);
		task.setParams(params, url);
		task.execute();
	}

	/*
	 * 请求历史反馈信息
	 */
	public void feedBackReply() {
		Map<String, String> params = new HashMap<String, String>(1);
		String imsi = AppHelper.getMobileIMSI(mContext);// 获取imis号
		String mobile = AccountMgr.getInstance().getMobile(mContext);// 获取手机号
		String url = Config.UrlConfig.URL_FEEDBACK_REPLY;
		params.put("imsi", imsi);// 手机mis号
		params.put("os_type", "1"); // Android系统类型为1
		params.put("mobile", mobile);// 手机号
		CommonTask task = CommonTask.newInstance(mCall, mContext, FeedbackEntity.class);
		task.setParams(params, url);
		task.execute();
	}
}
