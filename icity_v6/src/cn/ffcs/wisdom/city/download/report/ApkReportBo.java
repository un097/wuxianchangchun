package cn.ffcs.wisdom.city.download.report;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask.Status;
import cn.ffcs.wisdom.base.CommonTaskJson;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.sqlite.model.ApkReportItem;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.JsonUtil;
import cn.ffcs.wisdom.tools.Log;

public class ApkReportBo implements HttpCallBack<BaseResp> {

	private Context mContext;
	private CommonTaskJson task;
	static final Object sInstanceSync = new Object();
	public static ApkReportBo instance;

	private ApkReportBo(Context context) {
		this.mContext = context;
	}

	public static ApkReportBo getInstance(Context context) {
		synchronized (sInstanceSync) {
			if (instance == null) {
				instance = new ApkReportBo(context);
			}
		}
		return instance;
	}

	public void reportApks() {
		if (task != null && task.getStatus() == Status.RUNNING) {
			return;
		}
		List<ApkReportItem> list = ApkReportUtil.findAllApkLogs(mContext);
		if (list == null || list.size() <= 0) {
			return;
		}
		long timestamp = System.currentTimeMillis();
		String sign = ApkReportUtil.sign(timestamp);
		ApkReportEntity entity = new ApkReportEntity();
		entity.setTimestamp(timestamp);
		entity.setSign(sign);
		entity.setData(list);
		String json = JsonUtil.toJson(entity);
		String productId = mContext.getString(R.string.version_name_update);
		task = CommonTaskJson.newInstance(this, mContext, BaseResp.class);
		task.setParams(Config.UrlConfig.URL_APK_LOG, json, productId);
		task.execute();
	}

	@Override
	public void call(BaseResp response) {
		if (response.isSuccess()) {// 上传成功，清空统计，便于下一次重新计算
			ApkReportUtil.deleteApkLogs(mContext);
			Log.i("--应用汇下载日志上报成功--");
		} else {
			Log.e("--应用汇下载日志上报失败--");
		}
	}

	@Override
	public void progress(Object... obj) {
	}

	@Override
	public void onNetWorkError() {
	}

}
