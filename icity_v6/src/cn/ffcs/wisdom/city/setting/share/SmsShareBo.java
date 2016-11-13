package cn.ffcs.wisdom.city.setting.share;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ffcs.android.api.internal.util.StringUtils;

import android.content.Context;
import android.telephony.SmsManager;
import cn.ffcs.wisdom.base.CommonNewTask;
import cn.ffcs.wisdom.base.CommonTask;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.entity.ConfigParams;
import cn.ffcs.wisdom.city.personcenter.entity.Account;
import cn.ffcs.wisdom.city.personcenter.utils.AccountUtil;
import cn.ffcs.wisdom.city.reportmenu.ReportUtil;
import cn.ffcs.wisdom.city.setting.entity.ShareNoticeEntity;
import cn.ffcs.wisdom.city.utils.ConfigUtil;
import cn.ffcs.wisdom.city.utils.MenuUtil;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.ManifestUtil;

/**
 * <p>Title: 短信分享逻辑类        </p>
 * <p>Description: 
 *  1. 调用异步发送分享内容和其他相关参数
 *  2. 调用成功后，提示相关信息
 * </p>
 * <p>@author: Eric.wsd                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2012-6-3             </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class SmsShareBo implements HttpCallBack<BaseResp> {
	private HttpCallBack<BaseResp> mCall;
	private Context mContext;

	public SmsShareBo(Context mContext, HttpCallBack<BaseResp> call) {
		this.mContext = mContext;
		this.mCall = call;
	}

	public void send(String contacts, String content) {
		Map<String, String> params = new HashMap<String, String>(1);
		String imsi = AppHelper.getMobileIMSI(mContext);
		// modify by linjiafu 2014-8-31 修改因取不到imsi而导致短信失败
		if(StringUtils.isEmpty(imsi)) {
			imsi = "0000000000000000";
		}
		int clientVersion = AppHelper.getVersionCode(mContext);
		String SHARE_TYPE = "1"; // 客户端分享
		String timestamp = String.valueOf(System.currentTimeMillis());
		String sign = ReportUtil.signKey(timestamp);
		params.put("sign", sign);
		params.put("timestamp", timestamp);
		params.put("imsi", imsi);
		params.put("client_version", String.valueOf(clientVersion));
		params.put("shareType", SHARE_TYPE); // 客户端分享
		params.put("content", content); // 分享内容
		params.put("mobile", contacts); // 分享号码组
		params.put("province", MenuUtil.getProviceCode(mContext));
		params.put("os_type", "1");
		params.put("client_type", mContext.getResources().getString(R.string.version_name_update));
		
		String imei = AppHelper.getIMEI(mContext);
		params.put("imei", imei);
		params.put("client_channel_type", ManifestUtil.readMetaData(mContext, "UMENG_CHANNEL"));

		Account account = AccountUtil.readAccountInfo(mContext);
		if (AccountUtil.isAccountExist(account)) {
			params.put("invitor", account.getData().getMobile());
		} else {
			params.put("invitor", "");
		}
		String url = Config.UrlConfig.URL_SMS_BIND_ID;
		CommonTask task = CommonTask.newInstance(this, mContext, null);
		task.setParams(params, url);
		task.execute();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void call(BaseResp resp) {
		if (resp.isSuccess()) {
			SMSResp smsResp = new SMSResp();
			try {
				JSONObject jsonObject = new JSONObject(resp.getHttpResult());
				String desc = jsonObject.getString("desc");
				if (jsonObject.has("native_net")) {
					String nativeNet = jsonObject.getString("native_net");
					JSONArray jar = jsonObject.getJSONArray("list");

					List<String> phoneList = new ArrayList<String>();

					/*
					 * "list":[{"1839":11,"138xx":22}]
					 */
					int size = jar.length();
					for (int i = 0; i < size; i++) {
						JSONObject jo = jar.getJSONObject(i);
						Iterator<String> key = jo.keys();
						while (key.hasNext()) {
							String keyString = key.next();// 迭代电话号码
							String keyValue = jo.getString(keyString);// 获取对应的id值
							phoneList.add(keyString + ":" + keyValue);
						}
					}

					smsResp.setList(phoneList);
					smsResp.setNative_net(nativeNet);
					smsResp.setStatus(BaseResp.SUCCESS);
					smsResp.setDesc(desc);
				} else {
					smsResp.setStatus(BaseResp.ERROR);
					smsResp.setDesc(desc);
				}
			} catch (Exception e) {
				smsResp.setStatus(BaseResp.ERROR);
				smsResp.setDesc("分享好友失败。");
			}
			mCall.call(smsResp);
		} else {
			mCall.call(resp);
		}
	}

	@Override
	public void progress(Object... obj) {
	}

	@Override
	public void onNetWorkError() {
	}

	/**
	 * 发送分享短信
	 * @param context
	 * @param content
	 * @param sPhone
	 * @param sId
	 */
	public void sendSMS(Context context, String content, String sPhone, String sId) {
		final ConfigParams params = ConfigUtil.readConfigParams(context);
		SmsManager sms = SmsManager.getDefault();
		boolean isAssemblySMSUrl = mContext.getResources().getBoolean(R.bool.isAssemblySMSUrl);
		String text;
		if (isAssemblySMSUrl) {
			text = content + params.getDOWNLOAD_URL() + sId;
		} else {
			text = content;
		}
		sms.sendMultipartTextMessage(sPhone, null, sms.divideMessage(text), null, null);
	}

	/**
	 * 获取分享提示信息
	 * @param call
	 */
	public void getShareNotice(HttpCallBack<BaseResp> call) {
		CommonNewTask task = CommonNewTask.newInstance(call, mContext, ShareNoticeEntity.class);
		Map<String, String> map = new HashMap<String, String>();
		map.put("cityCode", MenuMgr.getInstance().getCityCode(mContext));
		map.put("verType", mContext.getString(R.string.version_name_update));
		map.put("osType", AppHelper.getOSTypeNew());
		map.put("clientVerNum", String.valueOf(AppHelper.getVersionCode(mContext)));
		task.setParams(map, Config.UrlConfig.GET_SHARE_NOTICE);
		task.execute();
	}
}
