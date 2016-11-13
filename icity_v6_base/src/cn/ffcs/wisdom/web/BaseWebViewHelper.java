package cn.ffcs.wisdom.web;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.telephony.SmsManager;
import android.webkit.WebView;
import cn.ffcs.wisdom.interfaces.IScreenShotCallBack;
import cn.ffcs.wisdom.interfaces.IShakeCallBack;
import cn.ffcs.wisdom.lbs.ILbsCallBack;
import cn.ffcs.wisdom.lbs.LBSLocationClient;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.JsonUtil;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.SdCardTool;
import cn.ffcs.wisdom.tools.ShakeManager;
import cn.ffcs.wisdom.tools.StringUtil;
import cn.ffcs.wisdom.tools.SystemCallUtil;

/**
 * <p>Title:  webview辅助类    </p>
 * <p>Description:                     </p>
 * <p>@author: zhangws                 </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2012-9-7            </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class BaseWebViewHelper {

	public static String FUNCID = "func_id";
	public static String ISSUCCED = "issucced";
	private String filePath;

	public BaseWebViewHelper(String filePath) {
		this.filePath = filePath;
	}

	/**
	 * web调用照相机
	 * @param activity
	 */
	public void camera(Activity activity) {
		SystemCallUtil.camera(activity, filePath);
	}

	/**
	 * web调用相册
	 * @param activity
	 */
	public void photoAlbum(Activity activity) {
		SystemCallUtil.photoAlbum(activity);
	}

	/**
	 * web图片选择
	 * @param activity
	 */
	public void showSelect(Activity activity) {
		SystemCallUtil.showSelect(activity, filePath);
	}

	/**
	 * web获取经纬度
	 * @param context
	 * @return 返回经纬度的详细信息的json
	 */
	public void getLatitudeAndLongitude(Context context, ILbsCallBack iCall) {
		LBSLocationClient mLocationClient = LBSLocationClient.getInstance(context);
		mLocationClient.registerLocationListener(iCall);
		mLocationClient.startLocationService();
		mLocationClient.getLocaion();
	}

	/**
	 * web发送短信
	 * @param activity
	 * @param json
	 * @return 返回是否调用成功json
	 */
	public String sendSMS(Activity activity, String json) {
		String phoneNum = "";
		String content = "";
		String sendType = "";
		try {
			if (json.indexOf("sms:") == 0) {
				phoneNum = json.replaceAll("sms:", "");
			} else {
				JSONObject jo = JsonUtil.parseJSON(json);
				phoneNum = jo.getString("phone_num");
				content = jo.getString("content");
				if (jo.has("send_type")) {
					sendType = jo.getString("send_type");
					if ("1".equals(sendType)) {
						SmsManager sms = SmsManager.getDefault();
						sms.sendMultipartTextMessage(phoneNum, null, sms.divideMessage(content),
								null, null);
						Map<String, Object> map = new HashMap<String, Object>();
						map.put(FUNCID, 6);
						map.put(ISSUCCED, true);
						return JsonUtil.toJson(map);
					}
				}
			}
			SystemCallUtil.sendSMS(activity, phoneNum, content);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(FUNCID, 6);
			map.put(ISSUCCED, true);
			return JsonUtil.toJson(map);
		} catch (Exception e) {
			Log.e("Exception" + e);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(FUNCID, 6);
			map.put(ISSUCCED, false);
			return JsonUtil.toJson(map);
		}
	}

	/**
	 * web打电话
	 * @param activity
	 * @param json
	 */
	public void callPhone(Activity activity, String json) {
		try {
			if (json.indexOf("tel:") == 0 || json.indexOf("wtai://wp/mc;") == 0 || json.indexOf("mailto:") == 0 || json.indexOf("geo:") == 0) {
				// modify by linjiafu 2014-08-29
				// 把url中包含wtai://wp/mc;、tel:、mailto:、geo:剔除
				json = json.replace("wtai://wp/mc;", "");
				json = json.replace("tel:", "");
				json = json.replace("mailto:", "");
				json = json.replace("geo:", "");
				SystemCallUtil.callPhone(activity, json);
			} else {
				String phoneNum = "";
				phoneNum = JsonUtil.parseJSON(json).getString("tel");
				SystemCallUtil.callPhone(activity, phoneNum);
			}
		} catch (Exception e) {
			Log.e("Exception" + e);
		}
	}

	/**
	 * 开启摇动
	 * @param context
	 */
	public void startShake(Context context, IShakeCallBack shakeCallBack) {
		ShakeManager.getShakeManger(context).register();
		ShakeManager.getShakeManger(context).setShakeCallBack(shakeCallBack);
	}

	/**
	 * 截屏
	 * @param activity
	 */
	public void cutScreen(Activity activity, IScreenShotCallBack iCallBack) {
		String fileName = "webScreenShot.jpg";
		SdCardTool.save(CommonUtils.shot(activity), filePath, fileName);
		iCallBack.call(filePath + "/" + fileName);
	}

	/**
	 * 获取手机相关信息
	 */
	public void getMobileInfo(WebView webView, Context context) {
		try {
			String imsi = AppHelper.getMobileIMSI(context);
			HashMap<String, Object> map = new HashMap<String, Object>();
			if (!StringUtil.isEmpty(imsi)) {
				map.put(FUNCID, 19);
				map.put(ISSUCCED, true);
				map.put("imsi", imsi);
			} else {
				map.put(FUNCID, 19);
				map.put(ISSUCCED, false);
			}
			webView.loadUrl("javascript:icity_func('" + JsonUtil.toJson(map) + "')");
		} catch (Exception e) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put(FUNCID, 19);
			map.put(ISSUCCED, false);
			webView.loadUrl("javascript:icity_func('" + JsonUtil.toJson(map) + "')");
		}
	}
}
