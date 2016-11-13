package cn.ffcs.wisdom.city.personcenter.bo;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import cn.ffcs.wisdom.base.CommonTaskJson;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.personcenter.entity.TianyiBillResp;
import cn.ffcs.wisdom.city.utils.ConfigUtil;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.AppHelper;

/**
 * <p>Title: 天翼积分</p>
 * <p>Description:天翼积分 </p>
 * <p>Author: caijj</p>
 * <p>CreateTime: 2013-11-13 上午9:24:43 </p>
 * <p>CopyRight: 6.7.0 </p>
 */
public class TianyiBillsBo {

	/**
	 * 查询天翼积分
	 */
	public final void queryBill(Context context, HttpCallBack<BaseResp> call) {

		StringBuffer sb = new StringBuffer();
		String clientVersion = ConfigUtil.readVersionCode(context);
		String imsi = AppHelper.getMobileIMSI(context);
		String cityCode = MenuMgr.getInstance().getCityCode(context);
		String mobile = AccountMgr.getInstance().getMobile(context);
		String client_channel_type = ConfigUtil.readChannelName(context, Config.UMENG_CHANNEL_KEY);
		String os_type = "1";
		String base_line = Config.URL_BASELINE;
		String menu_version = ConfigUtil.readVersionName(context);
		String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		
		sb.append("{");
		sb.append("\"client_verion\":").append("\"" + clientVersion + "\",");
		sb.append("\"imsi\":").append("\"" + imsi + "\",");
		sb.append("\"city_code\":").append("\"" + cityCode + "\",");
		sb.append("\"mobile\":").append("\"" + mobile + "\",");
		sb.append("\"client_channel_type\":").append("\"" + client_channel_type + "\",");
		sb.append("\"os_type\":").append("\"" + os_type + "\",");
		sb.append("\"base_line\":").append("\"" + base_line + "\",");
		sb.append("\"menu_version\":").append("\"" + menu_version + "\",");
		sb.append("\"timestamp\":").append("\"" + timestamp + "\"");
		sb.append("}");
		
		CommonTaskJson task = CommonTaskJson.newInstance(call, context, TianyiBillResp.class);
		task.setParams(Config.UrlConfig.URL_TIANYI_BILL, sb.toString(), "icity_ver");
		task.execute();

	}
}
