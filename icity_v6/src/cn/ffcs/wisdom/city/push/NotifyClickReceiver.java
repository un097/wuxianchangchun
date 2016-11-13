package cn.ffcs.wisdom.city.push;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import cn.ffcs.config.ExternalKey;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.sqlite.model.CityConfig;
import cn.ffcs.wisdom.notify.MsgEntity;
import cn.ffcs.wisdom.notify.MsgEntity.Content;
import cn.ffcs.wisdom.notify.MsgEntity.Content.Param;
import cn.ffcs.wisdom.notify.NotificationConstants;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.StringUtil;

/**
 * <p>Title:     通知栏点击接收器     </p>
 * <p>Description:                     </p>
 * <p>@author: zhangwsh                </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2014-3-17           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class NotifyClickReceiver extends BroadcastReceiver {

	private String defaultAreaCode = "350100";

	@Override
	public void onReceive(Context context, Intent receiverIntent) {
		MsgEntity entity = (MsgEntity) receiverIntent
				.getSerializableExtra(NotificationConstants.NOTIFICATION_MESSAGE);

		new PushMsgBo(context).pushFeedBack(entity, Config.REBACK_USER_CLICK_MSG_TYPE);// 消息打开回执

		Content content = entity.getContent();
		Param param = content.getParam();
		String wapUrl = param.getWapUrl();
		if (!StringUtil.isEmpty(wapUrl) && !wapUrl.startsWith("http://")) {
			wapUrl = "http://" + wapUrl;
		}
		String pkg = receiverIntent.getStringExtra(NotificationConstants.NOTIFICATION_PKG);
		String cls = receiverIntent.getStringExtra(NotificationConstants.NOTIFICATION_CLASS);
		int id = Integer.parseInt(content.getMsgId());
		String contentString = content.getMsgContent();

		Intent actvityIntent = new Intent();
		if (!StringUtil.isEmpty(pkg) && !StringUtil.isEmpty(cls)) {
			try {
				actvityIntent.setComponent(new ComponentName(pkg, cls));
			} catch (Exception e) {
				Log.e("--调用的类不存在--");
			}
		} else {
			return;
		}
		// 添加额外的天翼景象参数
		CityConfig config = MenuMgr.getInstance().getCityConfig(context);
		if (config != null) {
			String areaCode = config.getTyjxCode();
			if (StringUtil.isEmpty(areaCode)) {
				areaCode = defaultAreaCode;
			}
			actvityIntent.putExtra(ExternalKey.K_AREA_CODE, areaCode);
		} else {
			actvityIntent.putExtra(ExternalKey.K_AREA_CODE, defaultAreaCode);
		}

		// 添加后台配置额外参数
		try {
			String extraParam = param.getExtraParam();
			if (!StringUtil.isEmpty(extraParam)) {// 解析额外参数
				String[] extraData = extraParam.split("\\||,");
				int extraDataLengh = extraData.length;
				if (extraData.length % 2 == 0) {
					for (int i = 0; i < extraDataLengh; i++) {
						if (i < extraDataLengh - 1) {
							actvityIntent.putExtra(extraData[i], extraData[i + 1]);
						}
					}
				}
			}
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}

		actvityIntent.putExtra(NotificationConstants.NOTIFICATION_ID, id);
		actvityIntent.putExtra(NotificationConstants.NOTIFICATION_TITLE, param.getTitle());
		actvityIntent.putExtra(NotificationConstants.NOTIFICATION_CONTENT, contentString);
		actvityIntent.putExtra(NotificationConstants.NOTIFICATION_MESSAGE, entity);
		actvityIntent.putExtra(NotificationConstants.NOTIFICATION_URL, wapUrl);
		actvityIntent.putExtra(NotificationConstants.NOTIFICATION_FLAG, true);
		actvityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		actvityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(actvityIntent);
	}
}
