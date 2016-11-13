package cn.ffcs.wisdom.city.web;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Toast;
import cn.ffcs.config.ExternalKey;
import cn.ffcs.external.share.view.CustomSocialShare;
//import cn.ffcs.external.tourism.activity.PlayActivity;
//import cn.ffcs.pay.external.IPayResultCallBack;
//import cn.ffcs.pay.external.IcityPayResult;
//import cn.ffcs.pay.external.IcityPayUtil;
import cn.ffcs.surfingscene.road.RoadPlayActivity;
import cn.ffcs.ui.tools.AlertBaseHelper;
import cn.ffcs.widget.LoadingDialog;
import cn.ffcs.wisdom.city.MPlayerActivity;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.entity.OldMenuItemEntity;
import cn.ffcs.wisdom.city.personcenter.LoginActivity;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.personcenter.entity.Account.AccountData;
import cn.ffcs.wisdom.city.setting.feedback.FeedBackActivity;
import cn.ffcs.wisdom.city.setting.share.ContactAsyncQueryHandler;
import cn.ffcs.wisdom.city.setting.share.OnQueryContactsListener;
import cn.ffcs.wisdom.city.setting.share.SMSShareActivity;
import cn.ffcs.wisdom.city.setting.share.SelectContactActivity;
import cn.ffcs.wisdom.city.sqlite.model.MenuItem;
import cn.ffcs.wisdom.city.utils.AppMgrUtils;
import cn.ffcs.wisdom.city.web.open.OpenMPlayerActivity;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.JsonUtil;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.RsaTool;
import cn.ffcs.wisdom.tools.SharedPreferencesUtil;
import cn.ffcs.wisdom.tools.StringUtil;

import com.ffcs.surfingscene.entity.GeyeType;
import com.ffcs.surfingscene.entity.GlobalEyeEntity;

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
public class IcityWebViewHelper {
	public static String FUNCID = "func_id";
	public static String ISSUCCED = "issucced";
	private String publicKeyFile = "icity_public_key.der"; // 公钥
	private static IcityWebViewHelper icityWebViewHelper;

	/**
	 * 联系人回调值
	 */
	public static final int REQUESTCODE_CONTACT = 3001;

	/**
	 * 登陆回调
	 */
	public static final int REQUESTCODE_LOGIN = 3002;

	/**
	 * 扫描回调
	 */
	public static final int REQUESTCODE_SCAN = 3003;

	/**
	 * 爱城市支付回调
	 */
	public static final int REQUESTCODE_ICITY_PAY = 3004;

	private IcityWebViewHelper() {
	}

	/**
	 * 单例防止重复创建
	 * @return
	 */
	public static IcityWebViewHelper getHelper() {
		if (icityWebViewHelper == null) {
			icityWebViewHelper = new IcityWebViewHelper();
		}
		return icityWebViewHelper;
	}

	/**
	 * web获取个人信息
	 * @return 返回个人信息json
	 */
	public void getUserInformation(Context mContext, WebView webView, boolean isRsa, int funcId) {
		String returnJson;
		if (!Boolean.parseBoolean(SharedPreferencesUtil.getValue(mContext, Key.K_IS_LOGIN))) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(FUNCID, funcId);
			map.put(ISSUCCED, false);
			returnJson = JsonUtil.toJson(map);
		} else {
			AccountData data = AccountMgr.getInstance().getAccount(mContext).getData();
			if (data != null) {
				String userName = data.getUserName();
				String phoneNum = data.getMobile();
				String userHead = data.getIconUrl();
				if (!StringUtil.isEmpty(userHead) && (!(userHead.indexOf("http://") == 0))) {
					userHead = Config.GET_IMAGE_ROOT_URL() + data.getIconUrl();
				}
				long userId = data.getId();
				String cityCode = MenuMgr.getInstance().getCityCode(mContext);
				String cityName = MenuMgr.getInstance().getCityName(mContext);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(FUNCID, funcId);
				map.put(ISSUCCED, true);
				map.put("username", userName);
				map.put("phone", phoneNum);
				map.put("city", cityCode);
				map.put("city_name", cityName);
				map.put("user_head", userHead == null ? "" : userHead);
				map.put("user_id", userId);
				//map.put(ExternalKey.K_CLIENT_TYPE, AppHelper.getVersionCode(context));// 版本号请到下面获取
				if (isRsa) {
					String encode = rsaEncrypt(mContext, map, publicKeyFile);
					map.clear();
					map.put(FUNCID, funcId);
					map.put(ISSUCCED, true);
					map.put("encode", encode);
					returnJson = JsonUtil.toJson(map);
				} else {
					returnJson = JsonUtil.toJson(map);
				}
			} else {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(FUNCID, funcId);
				map.put(ISSUCCED, false);
				returnJson = JsonUtil.toJson(map);
			}
		}
		webView.loadUrl("javascript:icity_func('" + returnJson + "')");
	}

	/**
	 * rsa加密
	 * @param map
	 */
	private String rsaEncrypt(Context mContext, Map<String, Object> map, String publicKeyFile) {
		try {
			InputStream in = mContext.getAssets().open(publicKeyFile);
			RSAPublicKey publicKey = RsaTool.getRsaPublicKey(in);
			String rsa = RsaTool.encryptByPublicKey(JsonUtil.toJson(map), publicKey);
			// String rsa =
			// "58af0d1dd90c3067050beb58f73544842fc8ddc36f97883521b03426a2d122d953b168111e78a74e0920036ed35193f13ceffe9e868576e71b6fff448435cba54ab69c51618601a36aab9115871c84f156512026da812f4fb11d0f5855c161cff18786959b333b93beb81a893ada1d2dfa7d61a9f3c8b7ce5846ab425ffe3626";
			// RSAPrivateKey pri =
			// RsaTool.getRsaPrivateKey(context.getAssets().open(
			// "icity_private_key.pem"));
			// String priRsa = RsaTool.decryptByPrivateKey(rsa, pri);
			return rsa;
		} catch (CertificateException e) {
			Log.e(e.toString(), e);
		} catch (FileNotFoundException e) {
			Log.e(e.toString(), e);
		} catch (Exception e) {
			Log.e(e.toString(), e);
		}
		return "";
	}

	/**
	 * web分享应用给其他好友
	 * @param url
	 * @param activity
	 * @return 返回是否调用成功json
	 */
	public String share(Activity mActivity, String url, String returnTitle) {
		try {
			JSONObject jo = JsonUtil.parseJSON(url);
			String title = jo.getString("title");
			String content = jo.getString("content");
			Intent i = new Intent();
			i.setClass(mActivity, SMSShareActivity.class);
			i.putExtra(Key.K_SHATE_TITLE, title);
			i.putExtra(Key.K_SHARE_CONTENT, content);
			i.putExtra(Key.K_RETURN_TITLE, returnTitle);
			mActivity.startActivity(i);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("func_id", "8");
			map.put("issucced", true);
			return JsonUtil.toJson(map);
		} catch (Exception e) {
			Log.e("Exception" + e);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("func_id", "8");
			map.put("issucced", false);
			return JsonUtil.toJson(map);
		}
	}

	/**
	 * web功能调用
	 * @param context
	 * @param json 
	 */
	public void openClass(Activity mActivity, String json, String returnTitle) {
		JSONObject jo = JsonUtil.parseJSON(json);
		JSONObject clientParam;
		try {
			clientParam = jo.getJSONObject("client_param");
			String clientString = clientParam.toString();
			OldMenuItemEntity webMenuItemEntity = JsonUtil.toObject(clientString,
					OldMenuItemEntity.class);
			MenuItem menuItem = OldMenuItemEntity.converOldMenuItemToNewMenuItem(webMenuItemEntity);
			AppMgrUtils.launchAPP(mActivity, menuItem, returnTitle);
		} catch (JSONException e) {
			Log.e("JSONException" + e.getMessage());
		} catch (Exception e) {
			Log.e("Exception" + e.getMessage());
		}
	}

	/**
	 * 播放视频
	 * @param context
	 * @param url 视频地址
	 */
	public void openPlayer(Activity mActivity, String url) {
		Bundle params = new Bundle();
		params.putString(Key.U_BROWSER_URL, url);
		Intent i = new Intent(mActivity, MPlayerActivity.class);
		i.putExtras(params);
		mActivity.startActivity(i);
	}

	/**
	 * 打开登陆
	 * @param activity
	 */
	public void openLogin(Activity mActivity, String returnTitle) {
//		Intent intent = new Intent(mActivity, LoginActivity.class);
		Intent intent = new Intent();
		intent.setClassName(mActivity, "cn.ffcs.changchuntv.activity.login.LoginActivity");
		intent.putExtra(Key.K_RETURN_TITLE, returnTitle);
		mActivity.startActivityForResult(intent, REQUESTCODE_LOGIN);
	}

	/**
	 * 打开意见反馈
	 * @param activity
	 */
	public void openFeedBack(Activity mActivity, String returnTitle) {
		Intent intent = new Intent(mActivity, FeedBackActivity.class);
		intent.putExtra(Key.K_RETURN_TITLE, returnTitle);
		mActivity.startActivity(intent);
	}

	/**
	 * web获取联系人
	 * @param activity
	 */
	public void getPhone(Activity mActivity, String returnTitle) {
		Intent i = new Intent(mActivity, SelectContactActivity.class);
		i.putExtra(Key.K_RETURN_TITLE, returnTitle);
		mActivity.startActivityForResult(i, REQUESTCODE_CONTACT);
	}

	/**
	 * web弹出对话框
	 * @param context
	 * @param json
	 */
	public void showAlert(final WebView webview, Activity mActivity, String json) {
		JSONObject jo = JsonUtil.parseJSON(json);
		try {
			String title = jo.getString("title");
			String message = jo.getString("info");
			AlertBaseHelper.showMessage(mActivity, title, message, new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put(FUNCID, 4);
					webview.loadUrl("javascript:icity_func('" + JsonUtil.toJson(map) + "')");
					
				}
			});
//			AlertBaseHelper.showConfirm(mActivity, title, message, new OnClickListener() {
//				
//				@Override
//				public void onClick(View arg0) {
//					Map<String, Object> map = new HashMap<String, Object>();
//					map.put(FUNCID, 4);
//					webview.loadUrl("javascript:icity_func('" + JsonUtil.toJson(map) + "')");
//					
//				}
//			});
		} catch (Exception e) {
			Log.e("Exception : " + e);
		}
	}
	
	/**
	 * web弹出Toast
	 * @param context
	 * @param json
	 */
	public void showToast(final WebView webview, Activity mActivity, String json) {
		JSONObject jo = JsonUtil.parseJSON(json);
		try {
			String message = jo.getString("info");
			CommonUtils.showToast(mActivity, message, Toast.LENGTH_SHORT);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(FUNCID, 28);
			webview.loadUrl("javascript:icity_func('" + JsonUtil.toJson(map) + "')");
		} catch (Exception e) {
			Log.e("Exception : " + e);
		}
	}
	
	/**
	 * web弹出对话框，确认和取消
	 * @param context
	 * @param json
	 */
	public void showConfirm(final WebView webview, Activity mActivity, String json) {
		JSONObject jo = JsonUtil.parseJSON(json);
		try {
			String title = jo.getString("title");
			String message = jo.getString("info");
			AlertBaseHelper.showConfirm(mActivity, title, message, "确定", "取消", new OnClickListener() {
				
				@Override
				public void onClick(View v) {	//确定
					Map<String, Object> map = new HashMap<String, Object>();
					map.put(FUNCID, 27);
					map.put(ISSUCCED, true);
					map.put("desc", "确定点击回调");
					webview.loadUrl("javascript:icity_func('" + JsonUtil.toJson(map) + "')");
					
				}
			}, new OnClickListener() {
				
				@Override
				public void onClick(View v) {	//取消
					Map<String, Object> map = new HashMap<String, Object>();
					map.put(FUNCID, 27);
					map.put(ISSUCCED, false);
					map.put("desc", "取消点击回调");
					webview.loadUrl("javascript:icity_func('" + JsonUtil.toJson(map) + "')");
				}
			});
		} catch (Exception e) {
			Log.e("Exception : " + e);
		}
	}

	/**
	 * 无能力提示
	 * @param webview
	 * @param funcId
	 */
	public void loadDefault(WebView webview, int funcId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(FUNCID, funcId);
		map.put(ISSUCCED, false);
		map.put("desc", "无此能力");
		webview.loadUrl("javascript:icity_func('" + JsonUtil.toJson(map) + "')");
	}

	/**
	 * 获取全部联系人
	 */
	public void getAllContact(Activity mActivity, WebView webView, int funcId) {
		ContactAsyncQueryHandler.getInstance(mActivity.getContentResolver()).getList(
				new GetContactListener(webView, funcId));
	}

	// 联系人接口
	class GetContactListener implements OnQueryContactsListener {

		private final int TO_WEBPAGE = 0x001;

		private WebView webView;
		private int funcId;

		private final Handler toWebHandler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case TO_WEBPAGE:
					webView.loadUrl("javascript:icity_func('" + msg.obj.toString() + "')");
					break;
				}
			};
		};

		GetContactListener(WebView webView, int funcId) {
			this.webView = webView;
			this.funcId = funcId;
		}

		@Override
		public void onQueryStart() {
		}

		@Override
		public void onQueryFinish(final List<Map<String, Object>> list) {
			Thread t = new Thread(new Runnable() {

				@Override
				public void run() {
					StringBuilder allContact = new StringBuilder();
					int size = list.size();
					if (list != null && size > 0) {
						for (int i = 0; i < size; i++) {
							Map<String, Object> map = list.get(i);
							allContact.append(map.get("name")).append("-").append(map.get("phone"));
							if (i < size - 1) {
								allContact.append(",");
							}
						}
					}
					Map<String, Object> map = new HashMap<String, Object>();
					if (!StringUtil.isEmpty(allContact.toString())) {
						map.put(FUNCID, funcId);
						map.put(ISSUCCED, true);
						map.put("phone_nums", allContact.toString());
					} else {
						map.put(FUNCID, funcId);
						map.put(ISSUCCED, false);
					}
					toWebHandler.sendMessage(toWebHandler.obtainMessage(TO_WEBPAGE,
							JsonUtil.toJson(map)));
				}
			});
			t.start();
		}
	}

	/**
	 * 打开视频播放器
	 * @param activity
	 * @param json
	 */
	public void openVideoPlayer(Activity mActivity, String json) {
		Intent i = null;
		JSONObject jo = JsonUtil.parseJSON(json);
		try {
			String url = jo.getString("video_url");
			if (jo.has("web_url")) {
				String webUrl = jo.getString("web_url");
				i = new Intent(mActivity, OpenMPlayerActivity.class);
				i.putExtra(Key.U_BROWSER_URL, url);
				i.putExtra(Key.K_WEB_URL, webUrl);
			} else {
				i = new Intent(mActivity, MPlayerActivity.class);
				i.putExtra(Key.U_BROWSER_URL, url);
			}
			if (i != null) {
				mActivity.startActivity(i);
			}
		} catch (JSONException e) {
			Log.e("json 解析异常");
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
	}

	/**
	 * 打开单一的天翼景象摄像头
	 * @param activity
	 * @param url
	 */
	public void openSurfingSence(Activity mActivity, String url, String returnTitle) {
		try {
			JSONObject jo = JsonUtil.parseJSON(url);
			String eyeId = jo.getString("eye_id");
			String eyeTitle = jo.getString("eye_title");

			int videoPlayType = 0;// 路况or景点， 0：路况       1：景点
			if (jo.has("play_type")) {
				videoPlayType = jo.getInt("play_type");
			}

			String eyeDes = "";
			if (jo.has("eye_des")) {
				eyeDes = jo.getString("eye_des");
			}

			String actionId = "0";
			if (jo.has("action_id")) {
				actionId = jo.getString("action_id");
			}

			String gloType = "";
			if (jo.has("glo_type")) {
				gloType = jo.getString("glo_type");
			}

			String rtsp = "";
			if (jo.has("rtsp_address")) {
				rtsp = jo.getString("rtsp_address");
			}

			int isHighFlag = 0;
			if (jo.has("high_flag")) {
				isHighFlag = jo.getInt("high_flag");
			}

			String viodeType = "";// 新旧播放器
			if (jo.has("video_type")) {
				viodeType = jo.getString("video_type");
			}

			if (videoPlayType == 1) {
				GlobalEyeEntity entity = new GlobalEyeEntity();
				entity.setPuName(eyeTitle);
				entity.setHighflag(isHighFlag);
				entity.setVlcplayerversion(viodeType);
				entity.setGeyeId(Integer.parseInt(eyeId));
				entity.setActionId(Long.parseLong(actionId));
				entity.setRtspAddr(rtsp);
				entity.setIntro(eyeDes);
				GeyeType type = new GeyeType();
				type.setTypeCode(gloType);
				entity.setGeyeType(type);
//				Intent i = new Intent(mActivity, PlayActivity.class);
//				i.putExtra(cn.ffcs.external.tourism.common.K.K_VIDEO_ENTITY, entity);
//				mActivity.startActivity(i);
			} else if (videoPlayType == 0) {
				Intent i = new Intent(mActivity, RoadPlayActivity.class);
				i.putExtra(ExternalKey.K_EYE_ID, Integer.parseInt(eyeId));
				i.putExtra(ExternalKey.K_EYE_TITLE, eyeTitle);
				i.putExtra(ExternalKey.K_RTSP_ADDRESS, rtsp);
				i.putExtra(ExternalKey.K_HIGH_FLAG, isHighFlag);
				i.putExtra(cn.ffcs.surfingscene.common.Key.K_EYE_NAME, eyeTitle);
				i.putExtra(cn.ffcs.surfingscene.common.Key.K_EYE_INTRO, eyeDes);
				i.putExtra(cn.ffcs.surfingscene.common.Key.K_ACTION_ID, actionId);
				i.putExtra(cn.ffcs.surfingscene.common.Key.K_GLO_TYPE, gloType);
				i.putExtra(cn.ffcs.surfingscene.common.Key.K_RETURN_TITLE, returnTitle);
				i.putExtra(cn.ffcs.surfingscene.common.Key.K_VIDEO_TYPE, viodeType);
				mActivity.startActivity(i);
			}
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
	}

	/**
	 * 获取客户端版本
	 * @param context
	 * @param webView
	 */
	public void getCilentVersion(Context mContext, WebView webView) {
		try {
			int versionCode = AppHelper.getVersionCode(mContext);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(FUNCID, 20);
			map.put(ISSUCCED, true);
			map.put("version_code", versionCode);
			map.put("os", "android");
			webView.loadUrl("javascript:icity_func('" + JsonUtil.toJson(map) + "')");
		} catch (Exception e) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(FUNCID, 20);
			map.put(ISSUCCED, false);
			webView.loadUrl("javascript:icity_func('" + JsonUtil.toJson(map) + "')");
		}
	}

	/**
	 * 分享到社会
	 * @param activity
	 * @param json
	 */
	public void shareToSocial(Activity mActivity, String json) {
		String shareContent = "";
		String shareUrl = "";
		String shareTitle = "";
		try {
			JSONObject jo = JsonUtil.parseJSON(json);
			if (jo.has("share_content")) {
				shareContent = jo.getString("share_content");
				shareTitle = shareContent;
			}

			if (jo.has("share_title")) {
				shareTitle = jo.getString("share_title");
			}

			if (jo.has("share_url")) {
				shareUrl = jo.getString("share_url");
			}

			CustomSocialShare.shareTextPlatform(mActivity, shareTitle, shareContent, shareUrl);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
	}

	/**
	 * 启动扫描二维码
	 * @param activity
	 */
	public void scanCode(Activity mActivity) {
		try {
			Intent i = new Intent();
			i.setClassName(mActivity, "com.google.zxing.client.android.CaptureActivity");
			mActivity.startActivityForResult(i, REQUESTCODE_SCAN);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
	}

	/**
	 * 打开爱城市支付
	 * @param activity
	 */
	public void openIcityPay(Activity mActivity, String json) {
		try {
			JSONObject jo = JsonUtil.parseJSON(json);
			String cityCode = "";
			if (jo.has("city_code")) {
				cityCode = jo.getString("city_code");
			}

			String phone = "";
			if (jo.has("phone")) {
				phone = jo.getString("phone");
			}

			String actionId = "";
			if (jo.has("action_id")) {
				actionId = jo.getString("action_id");
			}

			Intent i = new Intent();
			i.setClassName(mActivity, "cn.ffcs.pay.activity.TrafficPayActivity");
			i.putExtra(ExternalKey.K_CITYCODE, cityCode);
			i.putExtra(ExternalKey.K_PHONENUMBER, phone);
			i.putExtra(ExternalKey.K_FROM_WEB, true);
//			i.putExtra(cn.ffcs.pay.common.Key.K_ACTION_ID, actionId);
			mActivity.startActivityForResult(i, REQUESTCODE_ICITY_PAY);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
	}

	/**
	 * 打开外部支付
	 * @param activity
	 * @param webView
	 * @param json
	 */
	public void toExtraPay(Activity mActivity, WebView webView, String json) {
		LoadingDialog.getDialog(mActivity).setMessage("请稍后...").show();
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			JSONObject jo = new JSONObject(json);
			if (jo.has("login_phone")) {// 手机
				map.put("icity_login_phone", jo.getString("login_phone"));
			}

			if (jo.has("pay_type")) {// 支付类型
				map.put("pay_type", jo.getInt("pay_type"));
			}

			if (jo.has("order_count")) {// 购买数量
				map.put("icity_order_count", jo.getInt("order_count"));
			}

			if (jo.has("unit_price")) {// 单价
				map.put("price", jo.getDouble("unit_price"));
			}

			if (jo.has("fare")) {// 运费
				map.put("freight", jo.getDouble("fare"));
			}

			if (jo.has("discount")) {// 折扣
				map.put("discount", jo.getDouble("discount"));
			}

			if (jo.has("goods_name")) {// 商品名
				map.put("goods_name", jo.getString("goods_name"));
			}

			if (jo.has("goods_desc")) {// 商品描述
				map.put("goods_desc", jo.getString("goods_desc"));
			}

			if (jo.has("merchant_no")) {// 商户号
				map.put("merchant_no", jo.getString("merchant_no"));
			}

			if (jo.has("out_order_id")) { // 外部订单
				map.put("out_order_id", jo.getString("out_order_id"));
			}

			if (jo.has("notify_url")) {// 通知接口地址
				map.put("notify_url", jo.getString("notify_url"));
			}
		} catch (JSONException e) {
		}
//		IcityPayUtil.go2pay(mActivity, map, new PayCallBack(mActivity, webView));
	}

	/**
	 * 语音播报功能
	 * @param activity
	 */
	public void toSpeech(Activity mActivity, String json) {
//		String speechContent = "";
//		int playFlag = 0;//0:初始化  1:暂停播放   2:继续播放
//		try {
//			JSONObject jo = JsonUtil.parseJSON(json);
//			if (jo.has("speech_content")) {
//				speechContent = jo.getString("speech_content");
//			}
//			if (jo.has("play_flag")) {
//				playFlag = jo.getInt("play_flag");
//			}
//			TtsSpeechApi.toSpeech(mActivity, speechContent, playFlag);
//		} catch (Exception e) {
//			Log.e(e.getMessage(), e);
//		}
	}

	/**
	 * 获取客户端当前城市
	 * @param context
	 * @param webView
	 */
	public void getCity(Context mContext, WebView webView) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		String returnJson = "";
		String cityCode = MenuMgr.getInstance().getCityCode(mContext);
		String cityName = MenuMgr.getInstance().getCityName(mContext);
		if (!StringUtil.isEmpty(cityCode) && !StringUtil.isEmpty(cityName)) {
			map.clear();
			map.put(FUNCID, 25);
			map.put(ISSUCCED, true);
			map.put("city_code", cityCode);
			map.put("city_name", cityName);
			returnJson = JsonUtil.toJson(map);
		} else {
			map.clear();
			map.put(FUNCID, 25);
			map.put(ISSUCCED, false);
			returnJson = JsonUtil.toJson(map);
		}
		webView.loadUrl("javascript:icity_func('" + returnJson + "')");
	}
	
	/**
	 * 弹出软键盘
	 * @param context
	 * @param webView
	 */
	public void showKeyboard(Context mContext, WebView webView) {
		CommonUtils.showKeyboard(mContext, webView);
//		webView.requestFocus(View.FOCUS_UP);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(FUNCID, 29);
		webView.loadUrl("javascript:icity_func('" + JsonUtil.toJson(map) + "')");
	}
	
	/**
	 * 弹出软键盘
	 * @param context
	 * @param webView
	 */
	public void hideKeyboard(Activity a, WebView webView) {
		CommonUtils.hideKeyboard(a);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(FUNCID, 30);
		webView.loadUrl("javascript:icity_func('" + JsonUtil.toJson(map) + "')");
	}

	/* 调用外部支付回调 */
//	class PayCallBack implements IPayResultCallBack {
//		private WebView webView;
//		private Activity activity;
//
//		PayCallBack(Activity activity, WebView webView) {
//			this.webView = webView;
//			this.activity = activity;
//		}
//
//		@Override
//		public void onResult(IcityPayResult result) {
//			LoadingDialog.getDialog(activity).cancel();
//			HashMap<String, Object> map = new HashMap<String, Object>();
//			map.put(ISSUCCED, result.getResultCode() == 0 ? true : false);
//			map.put(FUNCID, 23);
//			map.put("resultCode", result.getResultCode());
//			map.put("orderId", result.getOrderId());
//			webView.loadUrl("javascript:icity_func('" + JsonUtil.toJson(map) + "')");
//		}
//	}

	/**
	 * 打开随手拍详情页面
	 * @param activity
	 * @param json
	 */
//	public void openPhoto(Activity mActivity, String json) {
//		try {
//			Intent i = new Intent(mActivity, PhotoDetailActivity.class);
//			JSONObject jo = new JSONObject(json);
//			if (jo.has("photo_entity")) {
//				JSONObject joEntity = jo.getJSONObject("photo_entity");
//				String entityString = joEntity.toString();
//				PhotoEntity entity = JsonUtil.toObject(entityString, PhotoEntity.class);
//				i.putExtra(cn.ffcs.external.photo.common.Key.K_SHOOT_ENTITY, entity);
//			}
//
//			if (jo.has("city_code")) {
//				i.putExtra(cn.ffcs.external.photo.common.Key.K_PLATFORM_CITY_CODE,
//						jo.getString("city_code"));
//			}
//
//			if (jo.has("phone")) {
//				i.putExtra(cn.ffcs.external.photo.common.Key.K_PHONE, jo.getString("phone"));
//			}
//
//			mActivity.startActivity(i);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//	}
}
