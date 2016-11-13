package cn.ffcs.wisdom.city.bo;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import cn.ffcs.wisdom.base.BaseBo;
import cn.ffcs.wisdom.base.CommonNewTask;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.entity.MenuEntity;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.AppHelper;

public class AllMenuBoNoCache extends BaseBo {

	private CommonNewTask menuTask; // 菜单任务

	private HttpCallBack<BaseResp> call;

	private static final int REQUEST_NETWORK_ERROR = 0x1; // 网络异常
	private static final int REQUEST_SUCCESS_GET_COMMON = 0x2; // 系统异常
	private static final int REQUEST_SUCCESS_WITHOUT_NETWORK = 0x3; // 菜单加载成功，离线模式

	private Handler handler = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case REQUEST_SUCCESS_GET_COMMON:
				if (call != null) {
					call.call((BaseResp) msg.obj);
				}
				break;
			case REQUEST_NETWORK_ERROR:
				if (call != null) {
					call.onNetWorkError();
				}
				break;
			case REQUEST_SUCCESS_WITHOUT_NETWORK:
				if (call != null) {
					call.call(new BaseResp());
				}
			}

		}
	};

	public void setHttpCallBack(HttpCallBack<BaseResp> call) {
		this.call = call;
	}

	public AllMenuBoNoCache(Activity activity) {
		super(activity);
	}

	public void request(Context context, String cityCode, String menuVer) {
		menuTask = CommonNewTask.newInstance(menuCall, mActivity, MenuEntity.class);
		Map<String, String> menuParams = new HashMap<String, String>();
		String url = Config.UrlConfig.URL_MENU;
		menuParams.put("cityCode", cityCode);
		menuParams.put("menuVer", menuVer);
		menuParams.put("switchSystemType", "1");
		menuParams.put("osType", AppHelper.getOSType());
		menuParams.put("clientverMapping",String.valueOf(AppHelper.getVersionCode(context)));
		menuTask.setParams(menuParams, url);
		menuTask.execute();
	}

	private HttpCallBack<BaseResp> menuCall = new HttpCallBack<BaseResp>() {

		@Override
		public void progress(Object... obj) {
			if (call != null) {
				call.progress(obj);
			}
		}

		@Override
		public void onNetWorkError() {
			if (call != null) {
				call.onNetWorkError();
			}
		}

		@Override
		public void call(final BaseResp response) {
			Message msg = handler.obtainMessage();
			msg.what = REQUEST_SUCCESS_GET_COMMON;
			msg.obj = response;
			handler.sendMessage(msg);
		}
	};
}
