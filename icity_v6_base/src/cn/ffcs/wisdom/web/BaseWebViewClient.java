package cn.ffcs.wisdom.web;

import java.net.URLDecoder;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import cn.ffcs.wisdom.interfaces.IScreenShotCallBack;
import cn.ffcs.wisdom.interfaces.IShakeCallBack;
import cn.ffcs.wisdom.tools.JsonUtil;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.web.BaseWebViewHelper;
import cn.ffcs.wisdom.web.WebLbsCallBack;

/**<p>Title:    baseWebViewClient   </p>
 * <p>Description:    主要实现能力开放    </p>
 * <p>@author: zhangws                 </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2012-9-20           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public abstract class BaseWebViewClient extends WebViewClient {

	/**
	 * 能力开放json对象
	 */
	public JSONObject jo = null;
	/**
	 * webView控件
	 */
	public WebView webView;
	/**
	 * 图片上传地址
	 */
	public static String UP_LOAD_PATH;// 图片上传地址
	public Activity activity;
	public Context context;
	public static int funcId;
	/**
	 * 能力开放辅助类
	 */
	private BaseWebViewHelper helper;

	/**
	 * 保存图片路径
	 * @return
	 */
	public abstract String getFilePath();
	
	/**
	 * 获取截屏回调
	 * @return
	 */
	public abstract IScreenShotCallBack getIScreenShotCallBack();
	
	/**
	 * 获取摇动回调
	 * @return
	 */
	public abstract IShakeCallBack getIShakeCallBack();

	/**
	 * @param activity 能力开放activity
	 * @param webView  能力开放webview控件
	 */
	public BaseWebViewClient(Activity activity, WebView webView) {
		this.activity = activity;
		this.webView = webView;
		helper = new BaseWebViewHelper(getFilePath());
		context = activity.getApplicationContext();
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		if (url.indexOf("api:") == 0 || url.indexOf("API:") == 0) {
			try {
				url = URLDecoder.decode(url, "UTF-8");
				url = url.substring(4);
				jo = JsonUtil.parseJSON(url);
				funcId = Integer.parseInt(jo.getString("func_id"));
				switch (funcId) {
				case 1:// 拍照
					String type = jo.getString("phototype");
					UP_LOAD_PATH = jo.getString("httpurl");
					if (type.equals("3")) {
						helper.camera(activity);
					} else if (type.equals("2")) {
						helper.photoAlbum(activity);
					} else if (type.equals("1")) {
						helper.showSelect(activity);
					}
					break;
				case 3:// 经纬度
					helper.getLatitudeAndLongitude(context, new WebLbsCallBack(webView));
					break;
				case 6:// 短信
					helper.sendSMS(activity, url);
					break;
				case 7:// 电话
					helper.callPhone(activity, url);
					break;
				case 11:// 调用摇动
					helper.startShake(context,getIShakeCallBack());
					break;
				case 15: // 关闭浏览器
					activity.finish();
					break;
				case 16: // 截屏
					UP_LOAD_PATH = jo.getString("httpurl");
					helper.cutScreen(activity, getIScreenShotCallBack());
					break;
				case 19:// 获取手机相关信息
					helper.getMobileInfo(webView, context);
					break;
				default:
					return false;
				}
			} catch (Exception e) {
				Log.e("Exception" + e);
			}
		} else if (url.indexOf("tel:") >= 0 || url.indexOf("wtai://wp/mc;") >= 0) {
			// 打电话
			helper.callPhone(activity, url);
		} else if (url.indexOf("sms:") == 0) {
			// 发送短信
			helper.sendSMS(activity, url);
		} else {
			return false;
		}
		return true;
	}
}
