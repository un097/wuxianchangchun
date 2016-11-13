package cn.ffcs.wisdom.city.personcenter.bo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import cn.ffcs.config.ExternalKey;
import cn.ffcs.wisdom.base.CommonNewTask;
import cn.ffcs.wisdom.base.CommonStandardTaskNew;
import cn.ffcs.wisdom.city.bo.AllMenuBoNoCache;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.entity.MenuEntity;
import cn.ffcs.wisdom.city.modular.query.QueryInfoActivity;
import cn.ffcs.wisdom.city.modular.query.QueryInfoDataMgr;
import cn.ffcs.wisdom.city.modular.query.entity.QueryInfo;
import cn.ffcs.wisdom.city.modular.query.resp.QueryInfoResp;
import cn.ffcs.wisdom.city.modular.query.task.QueryInfoTask;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.personcenter.entity.Account;
import cn.ffcs.wisdom.city.personcenter.entity.MyRelevanceEntity;
import cn.ffcs.wisdom.city.personcenter.entity.MyRelevanceEntity.MyRelevance.MyRelevanceGroup.MyRelevanceDetail;
import cn.ffcs.wisdom.city.personcenter.entity.PhoneBillResp;
import cn.ffcs.wisdom.city.personcenter.entity.RelevanceAddEntity;
import cn.ffcs.wisdom.city.resp.UpLoadImageResp;
import cn.ffcs.wisdom.city.sqlite.model.MenuItem;
import cn.ffcs.wisdom.city.task.ImageUpLoadTask;
import cn.ffcs.wisdom.city.traffic.violations.TrafficViolationsInfoActivity;
import cn.ffcs.wisdom.city.traffic.violations.TrafficViolationsListActivity;
import cn.ffcs.wisdom.city.utils.AppMgrUtils;
import cn.ffcs.wisdom.city.utils.ConfigUtil;
import cn.ffcs.wisdom.city.utils.MenuUtil;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.AppHelper;

/**
 * <p>Title:个人中心业务数据处理          </p>
 * <p>Description: 
 * </p>
 * <p>@author: Leo                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-2-28             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class PersonCenterBo {

	/**
	 * 头像上传
	 * @param call
	 * @param filePath
	 */
	public void imageUpLoad(HttpCallBack<UpLoadImageResp> call, String filePath, String cityCode) {
		ImageUpLoadTask task = new ImageUpLoadTask(call);
		String upLoadUrl = Config.GET_UP_LOAD_IMAGE_ROOT_URL()
				+ "uploadFile.action?businessType=photo&cityCode=" + cityCode;//图片上传地址;
		task.setUpUrl(upLoadUrl);
		task.setLocalFilePath(filePath);
		task.execute();
	}

	/**
	 * 更新用户信息
	 * 
	 */
	public void updataUserInformation(HttpCallBack<BaseResp> iCall, Context context,
			Map<String, String> map) {
		CommonNewTask task = CommonNewTask.newInstance(iCall, context, BaseResp.class);
		task.setParams(map, Config.UrlConfig.URL_UPDATA_USER_INFORMATION);
		task.execute();
	}

	/**
	 * 签到
	 * 
	 */
	public void signin(HttpCallBack<BaseResp> iCall, Context context) {
		String mobile = AccountMgr.getInstance().getAccount(context).getData().getMobile();
		String cityCode = MenuUtil.getCityCode(context);
		String clientVerType = context.getResources().getString(R.string.version_name_update);
		String clientVerNum = String.valueOf(AppHelper.getVersionCode(context));
		String clientChannelType = ConfigUtil.readChannelName(context, Config.UMENG_CHANNEL_KEY);
		int id = AccountMgr.getInstance().getAccount(context).getData().getId();
		Map<String, String> map = new HashMap<String, String>();
		map.put("mobile", mobile);
		map.put("clientChannelType", clientChannelType);
		map.put("clientVerNum", clientVerNum);
		map.put("clientVerType", clientVerType);
		map.put("city_code", cityCode);
		map.put("paId", String.valueOf(id));
		CommonNewTask task = CommonNewTask.newInstance(iCall, context, Class.class);
		task.setParams(map, Config.UrlConfig.SIGN_IN);
		task.execute();
	}

	/**
	 * 修改用户名
	 * 
	 */

	public void changeUsername(HttpCallBack<BaseResp> iCall, Context context, Map<String, String> map) {
		CommonNewTask task = CommonNewTask.newInstance(iCall, context, BaseResp.class);
		task.setParams(map, Config.UrlConfig.URL_USERNAME_CHANGE);
		task.execute();
	}

	/**
	 * 修改密码
	 * 
	 */

	public void changeUserpws(HttpCallBack<BaseResp> iCall, Context context, Map<String, String> map) {
		CommonStandardTaskNew task = CommonStandardTaskNew.newInstance(iCall, context, BaseResp.class);
		String productId = ExternalKey.K_CLIENT_TYPE;
		map.put("oper_type", "changepw");
		task.setParams(Config.UrlConfig.URL_PASSWORD_CHANGE_NEW, map, productId, map.get("mobile"), map.get("cityCode"));
		task.execute();
	}

	/**
	 * 获取话费和流量
	 * @param call
	 * @param context
	 */
	public void getPhoneBill(HttpCallBack<BaseResp> iCall, Context context, Map<String, String> map) {
		CommonNewTask task = CommonNewTask.newInstance(iCall, context, PhoneBillResp.class);
		task.setParams(map, Config.UrlConfig.URL_PHONE_BILL_QUERY);
		task.execute();
	}

	/**
	 * 获取所有用户信息
	 * 
	 */
	public void getUserAllInformation(HttpCallBack<BaseResp> iCall, Context context,
			Map<String, String> map) {

		CommonNewTask task = CommonNewTask.newInstance(iCall, context, Account.class);
		task.setParams(map, Config.UrlConfig.URL_LOGIN_CHECK_NEW);
		task.execute();
	}

	/**
	 * 删除用户关联
	 * @param context
	 * @param iCall
	 */
	public void deleteRelevance(Context context, HttpCallBack<BaseResp> iCall, String groupId) {
		CommonNewTask task = CommonNewTask.newInstance(iCall, context, BaseResp.class);
		Map<String, String> map = new HashMap<String, String>();
		map.put("keyGroupId", groupId);
		task.setParams(map, Config.UrlConfig.URL_DELETE_RELEVANCE);
		task.execute();
	}

	/**
	 * 获取用户关联
	 * @param context
	 * @param iCall
	 */
	public void getRelevance(String paId, Context context, HttpCallBack<BaseResp> iCall) {
		CommonNewTask task = CommonNewTask.newInstance(iCall, context, MyRelevanceEntity.class);
		Map<String, String> map = new HashMap<String, String>();
		map.put("paId", paId);
		task.setParams(map, Config.UrlConfig.URL_GET_RELEVANCE);
		task.execute();
	}

	/**
	 * 获取可添加用户关联
	 * @param iCall
	 * @param context
	 */
	public void getRelevanceAdd(HttpCallBack<BaseResp> iCall, Context context) {
		CommonNewTask task = CommonNewTask.newInstance(iCall, context, RelevanceAddEntity.class);
		String cityCode = MenuMgr.getInstance().getCityCode(context);
		Map<String, String> map = new HashMap<String, String>();
		map.put("cityCode", cityCode);
		task.setParams(map, Config.UrlConfig.URL_RELEVANCE_ADD_NEW);
		task.execute();
	}

	/**
	 * 跳转对应栏目
	 * @param context
	 * @param cityCode
	 * @param itemId
	 * @param call
	 */
	public void startGotoMenu(Activity activity, String cityCode, String itemId,
			HttpCallBack<BaseResp> call, List<MyRelevanceDetail> list, boolean isAdd, int keyGroupId) {
		Context context = activity.getApplicationContext();
		MenuItem item = MenuMgr.getInstance().getMenu(context, itemId);
		if (item != null) {
			if ("交通".equals(item.getMenuName())) {
				isJiaoTong(activity, list, isAdd);
			} else {
				QueryInfoTask task = new QueryInfoTask(new QueryCallBack(activity, item, call,
						itemId, cityCode, keyGroupId), cityCode);
				task.execute();
			}
		} else {
			AllMenuBoNoCache bo = new AllMenuBoNoCache(activity);
			bo.setHttpCallBack(new MenuCallBack(activity, itemId, call, cityCode, keyGroupId, list,
					isAdd));
			bo.request(context, cityCode, "0");
		}
	}

	/**
	 * 交通栏目跳转
	 * @param activity
	 * @param list
	 * @param isAdd
	 * @param context
	 */
	private void isJiaoTong(Activity activity, List<MyRelevanceDetail> list, boolean isAdd) {
		if (isAdd) {
			Intent i = new Intent(activity, TrafficViolationsListActivity.class);
			i.putExtra(Key.K_RETURN_TITLE, activity.getString(R.string.person_center_my_relevance));
			activity.startActivity(i);
		} else {
			Intent intent = new Intent(activity, TrafficViolationsInfoActivity.class);
			intent.putExtra(Key.K_RETURN_TITLE,
					activity.getString(R.string.person_center_my_relevance));
			if (list != null) {
				for (int j = 0; j < list.size(); j++) {
					if (list.get(j).getKeyName().equals("violCarNo")) {
						intent.putExtra(Key.K_CAR_NO, list.get(j).getKeyValue());
					}
					if (list.get(j).getKeyName().equals("violCarLastCodes")) {
						intent.putExtra(Key.K_CAR_LAST_CODES, list.get(j).getKeyValue());
					}
//					if (list.get(j).getKeyName().equals("violCarType")) {
//						intent.putExtra("carType", list.get(j).getKeyValue());
//					}
				}
			}
			intent.putExtra(Key.K_IS_GET_VALUE, true);
			activity.startActivity(intent);
		}
	}

	class MenuCallBack implements HttpCallBack<BaseResp> {
		private HttpCallBack<BaseResp> call;
		private String itemId;
		private Activity activity;
		private String cityCode;
		private int keyGroupId;
		private List<MyRelevanceDetail> list;
		private boolean isAdd;

		public MenuCallBack(Activity activity, String itemId, HttpCallBack<BaseResp> call,
				String cityCode, int keyGroupId, List<MyRelevanceDetail> list, boolean isAdd) {
			this.call = call;
			this.activity = activity;
			this.cityCode = cityCode;
			this.itemId = itemId;
			this.keyGroupId = keyGroupId;
			this.list = list;
			this.isAdd = isAdd;
		}

		@Override
		public void call(BaseResp response) {
			if (response.isSuccess()) {
				MenuEntity menuEntity = (MenuEntity) response.getObj();
				List<MenuItem> itemList = menuEntity.getData().getMenuListInfo();
				MenuItem item = null;
				for (int i = 0; i < itemList.size(); i++) {
					String menuId = itemList.get(i).getMenuId();
					String menuName = itemList.get(i).getMenuName();
					if (itemId.equals(menuId) && !"交通".equals(menuName)) {
						item = itemList.get(i);
						break;
					} else if (itemId.equals(menuId) &&"交通".equals(menuName)) {
						isJiaoTong(activity, list, isAdd);
						callSuccess(call);
						return;
					}
				}
				if (item != null) {
					QueryInfoTask task = new QueryInfoTask(new QueryCallBack(activity, item, call,
							itemId, cityCode, keyGroupId), cityCode);
					task.execute();
				} else {
					callError(call);
				}
			} else {
				callError(call);
			}
		}

		@Override
		public void progress(Object... obj) {
		}

		@Override
		public void onNetWorkError() {
		}
	}
	
	/**
	 * 回调失败
	 */
	private void callError(HttpCallBack<BaseResp> iCall) {
		BaseResp resp = new BaseResp();
		resp.setStatus(BaseResp.ERROR);
		iCall.call(new BaseResp());
	}

	/**
	 * 回调成功
	 */
	private void callSuccess(HttpCallBack<BaseResp> iCall) {
		BaseResp resp = new BaseResp();
		resp.setStatus(BaseResp.SUCCESS);
		iCall.call(new BaseResp());
	}

	class QueryCallBack implements HttpCallBack<QueryInfoResp> {
		private Activity activity;
		private MenuItem entity;
		private HttpCallBack<BaseResp> call;
		private String itemId;
		private String cityCode;
		private int keyGroupId;

		public QueryCallBack(Activity activity, MenuItem entity, HttpCallBack<BaseResp> call,
				String itemId, String cityCode, int keyGroupId) {
			this.activity = activity;
			this.entity = entity;
			this.call = call;
			this.itemId = itemId;
			this.cityCode = cityCode;
			this.keyGroupId = keyGroupId;
		}

		@Override
		public void call(QueryInfoResp resp) {
			if (resp.isSuccess()) {
				List<QueryInfo> info = resp.getHome();
				QueryInfoDataMgr.getInstance().refresh(cityCode, info);
				for (QueryInfo queryinfo : info) {
					if (itemId.equals(queryinfo.getItem_id())) {
						callSuccess(call);
						Intent intent = new Intent();
						intent.setClass(activity, QueryInfoActivity.class);
						intent.putExtra("queryinfo_itemid", itemId);
						intent.putExtra("queryinfo_citycode", cityCode);
						intent.putExtra("queryinfo_keyGroupId", keyGroupId);
						intent.putExtra(Key.K_RETURN_TITLE,
								activity.getString(R.string.person_center_my_relevance));
						activity.startActivity(intent);
						return;
					}
				}
				AppMgrUtils.launchAPP(activity, entity, R.string.person_center_my_relevance);
				return;
			}else {
				callError(call);
			}
		}

		@Override
		public void progress(Object... obj) {
		}

		@Override
		public void onNetWorkError() {
		}
	}
}
