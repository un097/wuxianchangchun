package cn.ffcs.wisdom.city.web;

import java.net.URLDecoder;
import java.util.Locale;

import android.app.Activity;
import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import cn.ffcs.wisdom.tools.JsonUtil;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.web.BaseWebViewClient;

public abstract class IcityWebViewClient extends BaseWebViewClient {

	/**
	 * 能力开放辅助类
	 */
	private IcityWebViewHelper helper;

	/**
	 * 获取返回标题
	 * @return
	 */
	public abstract String getReturnTitle();

	public IcityWebViewClient(Activity activity, WebView webView) {
		super(activity, webView);
		helper = IcityWebViewHelper.getHelper();
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		if (!super.shouldOverrideUrlLoading(view, url)) {
			if (url.indexOf("api:") == 0 || url.indexOf("API:") == 0) {
				try {
					url = URLDecoder.decode(url, "UTF-8");
					url = url.substring(4);
					jo = JsonUtil.parseJSON(url);
					funcId = Integer.parseInt(jo.getString("func_id"));
					switch (funcId) {
					case 2:// 个人信息
						helper.getUserInformation(context, webView, false, 2);
						break;
					case 4:// 对话框
						helper.showAlert(webView, activity, url);
						break;
					case 5:// 功能调用
						helper.openClass(activity, url, getReturnTitle());
						break;
					case 8:// 分享
						helper.share(activity, url, getReturnTitle());
						break;
					case 9:// 获取手机联系人
						helper.getPhone(activity, getReturnTitle());
						break;
					case 12:// 获取加密的个人信息
						helper.getUserInformation(context, webView, true, 12);
						break;
					case 13:// 调用登陆
						helper.openLogin(activity, getReturnTitle());
						break;
					case 14:// 调用意见反馈
						helper.openFeedBack(activity, getReturnTitle());
						break;
					case 17:// 播放器
						helper.openVideoPlayer(activity, url);
						break;
					case 18:// 天翼景象单一摄像头播放
						helper.openSurfingSence(activity, url, getReturnTitle());
						break;
					case 20:// 获取当前客户端版本
						helper.getCilentVersion(context, webView);
						break;
					case 21:// 社会化分享
						helper.shareToSocial(activity, url);
						break;
					case 22:// 二维码扫描
						helper.scanCode(activity);
						break;
					case 23:// 调用外部支付控件
						helper.toExtraPay(activity, webView, url);
						break;
//					case 24:// 语音播报功能
//						helper.toSpeech(activity, url);
//						break;
					case 25:// 获取客户端当前城市
						helper.getCity(context, webView);
						break;
//					case 26:// 调用随手拍详情
//						helper.openPhoto(activity, url);
//						break;
					case 27:	//调用弹出框（确定/取消）
						helper.showConfirm(webView, activity, url);
						break;
					case 30000:// 获取全部联系人
						helper.getAllContact(activity, webView, 30000);
						break;
					case 30001:// 调用爱城市支付
						helper.openIcityPay(activity, url);
						break;
					case 28:
						helper.showToast(webView, activity, url);
						break;
					case 29:
						helper.showKeyboard(activity, webView);
						break;
					case 30:
						helper.hideKeyboard(activity, webView);
						break;
					default:// 无能力提示
						helper.loadDefault(webView, funcId);
						break;
					}
				} catch (Exception e) {
					Log.e("Exception" + e);
				}
			} else if (url.toLowerCase(Locale.getDefault()).contains("rtsp://")) {
				// 播放视频
				helper.openPlayer(activity, url);
			} else {
				return false;
			}
			return true;
		}
		return true;
	}

	@Override
	public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
		handler.proceed();
	}
}
