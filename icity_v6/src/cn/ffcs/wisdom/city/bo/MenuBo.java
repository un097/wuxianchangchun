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
import cn.ffcs.wisdom.city.entity.MenuEntity.MenuConfig;
import cn.ffcs.wisdom.city.modular.query.QueryInfoBo;
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

public class MenuBo extends BaseBo {

	private CommonNewTask menuTask; // 菜单任务

	private HttpCallBack<BaseResp> call;
	private String cityCode;
	private String menuVer;

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
				QueryInfoBo queryBo = new QueryInfoBo(mActivity);
				queryBo.request();
				if (call != null) {
//					new CommonlyBo().getCommonlyUse(mActivity.getApplicationContext(),
//							new CommUseCallBack());
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

	public MenuBo(Activity activity) {
		super(activity);
	}

	public void request(Context context, String cityCode, String menuVer) {
		this.cityCode = cityCode;
		this.menuVer = menuVer;
		if (CommonUtils.isNetConnectionAvailable(mActivity)) { // 网络正常，从网络获取菜单数据
			menuTask = CommonNewTask.newInstance(menuCall, mActivity, MenuEntity.class);
			Map<String, String> menuParams = new HashMap<String, String>();
			String url = Config.UrlConfig.URL_HOME_MENU;
			menuParams.put("cityCode", cityCode);
			menuParams.put("menuVer", menuVer);
			menuParams.put("switchSystemType", "1");
			menuParams.put("osType", AppHelper.getOSType());
			menuParams.put("clientverMapping",String.valueOf(AppHelper.getVersionCode(context)));
			menuTask.setParams(menuParams, url);
			menuTask.execute();
			Log.i("菜单请求开始：------" + TimeUitls.getCurrentTime());
		} else { // 无网络，加载本地菜单
			loadMenuOffLine(cityCode);
		}
	}

	private void loadMenuOffLine(final String cityCode) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				List<MenuItem> menuItemList = MenuService.getInstance(mActivity).queryByCityCode(
						cityCode);

				Message msg = handler.obtainMessage();

				// 本地无缓存数据,第一次安装
				if (menuItemList == null || menuItemList.size() == 0) {

					msg.what = REQUEST_NETWORK_ERROR;
					handler.sendMessage(msg);
					return;
				}

				MenuMgr.getInstance().refreshMenu(mActivity, menuItemList, cityCode);
				MenuMgr.getInstance().getWidgetInfoCurrent(mActivity, cityCode, true); // 刷新控件配置

				msg.what = REQUEST_SUCCESS_WITHOUT_NETWORK;
				handler.sendMessage(msg);
			}
		}).start();
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
			Log.i("菜单服务器返回：------" + TimeUitls.getCurrentTime());
			if (response.isSuccess() || "200".equals(response.getStatus())) {

				ConfigUtil.getConfigParamsAsync(mActivity);

				MenuMgr.getInstance().saveCityCode(mActivity, cityCode);
				new Thread(new Runnable() {

					@Override
					public void run() {
						final List<MenuItem> allHomeMenuItemList=new ArrayList<MenuItem>();
						CityConfig cityConfig = null;
						if (response.isSuccess()) {
							Log.i("开始处理一级菜单：------" + TimeUitls.getCurrentTime());
							final MenuEntity menuEntity = (MenuEntity) response.getObj();
							if (menuEntity != null) {
								MenuConfig config = menuEntity.getData();
								cityConfig = config.toCityConfig();
								cityCode = cityConfig.getCityCode();
								List<MenuItem> homeList = config.getLevelOneMenuListInfo();
								if (homeList != null) {
									allHomeMenuItemList.addAll(homeList);// 一级栏目
								}
								List<MenuItem> recommendList = config.getRecommend();
								if (recommendList != null) {
									allHomeMenuItemList.addAll(recommendList);// 热门推荐
								}
								CommonlyUseMgr.getInstance().refreshCommUse(mActivity,
										config.getCommonUse(), cityCode);

								MenuMgr.getInstance().saveConfig(mActivity, cityCode, cityConfig);
								MenuMgr.getInstance().saveTyjcConfig(mActivity, cityConfig);

								new Thread(new Runnable() {

									@Override
									public void run() {
										MenuItem menuItem = allHomeMenuItemList.get(0);
										String version = menuItem.getMenuVer();
										if (version != null && !version.equals(menuVer)) { // 版本变化，更新本地数据
											MenuMgr.getInstance().saveMenuItem(mActivity,
													allHomeMenuItemList, cityCode);
											
											MenuMgr.getInstance().deleteWidget(mActivity, cityCode);
										}
									}
								}).start();
							}
						} else if ("200".equals(response.getStatus())) {
							List<MenuItem> list = MenuService.getInstance(mActivity)
									.queryByCityCode(cityCode);
							allHomeMenuItemList.clear();
							allHomeMenuItemList.addAll(list);
							MenuMgr.getInstance().getWidgetInfoCurrent(mActivity, cityCode, true);
						}

						MenuMgr.getInstance().refreshMenu(mActivity, allHomeMenuItemList, cityCode);

						Message msg = handler.obtainMessage();
						msg.what = REQUEST_SUCCESS_GET_COMMON;
						handler.sendMessage(msg); // 处理菜单结束
						Log.i("一级菜单处理完毕结束时间：------" + TimeUitls.getCurrentTime());
					}
				}).start();
			} else { // 请求失败，从缓存加载
				loadMenuOffLine(cityCode);
			}
		}
	};
}
