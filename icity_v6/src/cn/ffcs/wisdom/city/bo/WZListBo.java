package cn.ffcs.wisdom.city.bo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import cn.ffcs.wisdom.base.BaseBo;
import cn.ffcs.wisdom.base.CommonNewTask;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.datamgr.CommonlyUseMgr;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.entity.MenuEntity;
import cn.ffcs.wisdom.city.entity.WZListEntity;
import cn.ffcs.wisdom.city.entity.MenuEntity.MenuConfig;
import cn.ffcs.wisdom.city.modular.query.QueryInfoBo;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.sqlite.model.CityConfig;
import cn.ffcs.wisdom.city.sqlite.model.MenuItem;
import cn.ffcs.wisdom.city.sqlite.service.MenuService;
import cn.ffcs.wisdom.city.utils.ConfigUtil;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.TimeUitls;

public class WZListBo extends BaseBo {

	private CommonNewTask wzlistTask; // 菜单任务

	private HttpCallBack<BaseResp> call;
	private String menuid;
	Context context;

	private static final int REQUEST_NETWORK_ERROR = 0x1; // 网络异常
	private static final int REQUEST_SUCCESS_GET_COMMON = 0x2; // 系统异常
	private static final int REQUEST_SUCCESS_WITHOUT_NETWORK = 0x3; // 菜单加载成功，离线模式

	private Handler handler = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			// 请求菜单成功，重新刷新查询类数据
			int what = msg.what;
			switch (what) {
			case REQUEST_SUCCESS_GET_COMMON:
				// QueryInfoBo queryBo = new QueryInfoBo(mActivity);
				// queryBo.request();
				if (call != null) {
					// new
					// CommonlyBo().getCommonlyUse(mActivity.getApplicationContext(),
					// new CommUseCallBack());
					BaseResp resp = new BaseResp();
					resp.setStatus(BaseResp.SUCCESS);
					call.call(resp);
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

	public WZListBo(Activity activity) {
		super(activity);
	}

	public void request(Context context) {
		if (CommonUtils.isNetConnectionAvailable(mActivity)) { // 网络正常，从网络获取菜单数据
			wzlistTask = CommonNewTask.newInstance(BindCall, mActivity, 
					WZListEntity.class);//WZListEntity
			Map<String, String> Params = new HashMap<String, String>();
			String url = Config.UrlConfig.URL_WZ_SELECT_BIND;
			Params.put("item_id", menuid);
			Params.put("mobile", AccountMgr.getInstance()
					.getMobile(context));
			wzlistTask.setParams(Params, url);
			wzlistTask.execute();
		} else { // 无网络，加载本地菜单
		}
	}

	private HttpCallBack<BaseResp> BindCall = new HttpCallBack<BaseResp>() {

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
			if (response.isSuccess() || "200".equals(response.getStatus())) {
				// new Thread(new Runnable() {
				// @Override
				// public void run() {
				if (response.isSuccess()) {
//					android.util.Log.e("sb", response.getObj());
				}

				// Message msg = handler.obtainMessage();
				// msg.what = REQUEST_SUCCESS_GET_COMMON;
				// handler.sendMessage(msg); // 处理菜单结束

				// }
				// }).start();
				// } else { // 请求失败，从缓存加载
			}
		}
	};

}
