package cn.ffcs.wisdom.city.myapp.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.datamgr.AppMgr;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.myapp.bo.DelAppBo;
import cn.ffcs.wisdom.city.myapp.bo.MyAppBo;
import cn.ffcs.wisdom.city.myapp.entity.AppEntity;
import cn.ffcs.wisdom.city.sqlite.model.AppItem;
import cn.ffcs.wisdom.city.sqlite.model.MenuItem;
import cn.ffcs.wisdom.city.sqlite.service.AppService;
import cn.ffcs.wisdom.city.utils.CityImageLoader;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.SharedPreferencesUtil;
import cn.ffcs.wisdom.tools.StringUtil;

import com.google.gson.Gson;

/**
 * <p>Title: 添加应用列表适配器         </p>
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
public class AddAppExpandListViewAdapter extends BaseExpandableListAdapter {
	private Context mContext;
	private Activity mActivity;
	private LayoutInflater mInflater;
	// 第一级菜单列表
	private List<MenuItem> mHomeMenu = new ArrayList<MenuItem>();
	// 判断菜单是否被选中
	private List<String> mSelectItemList = new ArrayList<String>();
	// 应用列表menuId集合，添加应用
	private List<AppEntity> appList = new ArrayList<AppEntity>();
	// 已经添加过的应用列表
	private List<MenuItem> mMyAppList = new ArrayList<MenuItem>();
	// 获取本地删除数据参数
	private Map<String, MenuItem> paramsLocal = new HashMap<String, MenuItem>();
	// 转换成本地数据集合
	private List<AppItem> mAppLocal = new ArrayList<AppItem>();
	private String mMobile;
	private String cityCode;
	private MyAppBo mMyAppBo;
	private DelAppBo mDelBo;
	private boolean login = false;
	private CityImageLoader loader;

	public AddAppExpandListViewAdapter(Activity activity, List<MenuItem> mList,
			List<MenuItem> mAppList) {
		this.mContext = activity.getApplicationContext();
		this.mActivity = activity;
		this.mMobile = SharedPreferencesUtil.getValue(mContext, Key.K_PHONE_NUMBER);
		if (!StringUtil.isEmpty(mMobile)) {
			login = true;
		}
		this.cityCode = MenuMgr.getInstance().getCityCode(mContext);
		if (mList == null) {
			this.mHomeMenu = Collections.emptyList();
		}
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mHomeMenu = mList;
		this.mMyAppList = mAppList;
		loader = new CityImageLoader(mContext);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		MenuItem mHomeMenuItem = mHomeMenu.get(groupPosition);
		if (mHomeMenuItem != null) {
			String menuId = mHomeMenuItem.getMenuId();
			// 得到上一级菜单下的所有菜单列表
			List<MenuItem> thirdMenu = MenuMgr.getInstance().getThirdMenuListUnique(mContext,
					menuId, mMyAppList, login);
			if (thirdMenu != null) {
				MenuItem thirdMenuItem = thirdMenu.get(childPosition);
				if (thirdMenuItem != null) {
					return thirdMenuItem;
				}
			}
		}
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild,
			View convertView, ViewGroup parent) {
		final ChildHolder childHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.widget_addapp_child_item, null);
			childHolder = new ChildHolder();
			childHolder.mChildIcon = (ImageView) convertView.findViewById(R.id.app_icon);
			childHolder.mChildText = (TextView) convertView.findViewById(R.id.app_name);
			childHolder.mCheckbox = (CheckBox) convertView.findViewById(R.id.app_check);
			convertView.setTag(childHolder);
		} else {
			childHolder = (ChildHolder) convertView.getTag();
		}
		final MenuItem menuItem = (MenuItem) getChild(groupPosition, childPosition);
		if (menuItem != null) {
			loader.loadUrl(childHolder.mChildIcon, menuItem.getV6Icon());
			childHolder.mChildText.setSelected(true);
			childHolder.mChildText.setText(menuItem.getMenuName());
		}
		if (isExistMenuItem(menuItem)) {
			childHolder.mCheckbox.setChecked(true);
			childHolder.mCheckbox.setBackgroundResource(R.drawable.list_checkbox_select);
		} else {
			childHolder.mCheckbox.setChecked(false);
			childHolder.mCheckbox.setBackgroundResource(R.drawable.list_checkbox_default);
		}

		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String menuId = menuItem.getMenuId();
				// 已登录，提交服务器，需判断当前网络
				if (!StringUtil.isEmpty(mMobile)) {
					if (childHolder.mCheckbox.isChecked()) {
						// 取消操作
						childHolder.mCheckbox.setChecked(false);
						mSelectItemList.remove(menuId);
						childHolder.mCheckbox
								.setBackgroundResource(R.drawable.list_checkbox_default);
						delApp(menuId);

						mAppLocal.clear();
						mAppLocal = AppMgr.getInstance().queryAppByMenuId(mContext, cityCode,
								menuId);
						AppMgr.getInstance().deleteApp(mContext, mAppLocal);
						paramsLocal.remove(menuId);
					} else {
						// 添加操作
						childHolder.mCheckbox.setChecked(true);
						mSelectItemList.add(menuId);
						childHolder.mCheckbox
								.setBackgroundResource(R.drawable.list_checkbox_select);
						addApp(menuId);

						paramsLocal.put(menuId, menuItem);
						AppItem item = AppMgr.getInstance().convertToAppItem(menuItem);
						mAppLocal.clear();
						mAppLocal.add(item);
						AppMgr.getInstance().saveApp(mContext, mAppLocal);
					}
				} else {// 未登录，提交到本地数据库
					if (childHolder.mCheckbox.isChecked()) {
						// 取消操作
						childHolder.mCheckbox.setChecked(false);
						childHolder.mCheckbox
								.setBackgroundResource(R.drawable.list_checkbox_default);
						mAppLocal.clear();
						mAppLocal = AppMgr.getInstance().queryAppByMenuId(mContext, cityCode,
								menuId);
						AppMgr.getInstance().deleteApp(mContext, mAppLocal);
						paramsLocal.remove(menuId);
					} else {
						// 添加操作
						childHolder.mCheckbox.setChecked(true);
						paramsLocal.put(menuId, menuItem);
						childHolder.mCheckbox
								.setBackgroundResource(R.drawable.list_checkbox_select);
						AppItem item = AppMgr.getInstance().convertToAppItem(menuItem);
						mAppLocal.clear();
						mAppLocal.add(item);
						AppMgr.getInstance().saveApp(mContext, mAppLocal);
					}
				}
			}
		});
		return convertView;
	}

	/**
	 * 判断当前菜单是否已经选中
	 * @param menuItem
	 * @return
	 */
	public boolean isExistMenuItem(MenuItem menuItem) {
		boolean isFav = AppService.getInstance(mContext).isExist(
				AppMgr.getInstance().convertToAppItem(menuItem));
		if (isFav)
			return true;
		return false;
	}

	/**
	 * 集合参数转换成json字符串,添加应用
	 * @param entity
	 * @return
	 */
	public String toJson(String menuId) {
		AppEntity app = new AppEntity();
		app.setMenuId(menuId);
		appList.clear();
		appList.add(app);
		String json = null;
		if (appList != null && appList.size() > 0) {
			Gson gson = new Gson();
			json = "{" + "\"menuIdList\":" + gson.toJson(appList) + "}";
		}
		return json;
	}

	/**
	 * 添加应用
	 * @param menuId
	 */
	public void addApp(String menuId) {
		String menuIds = toJson(menuId);
		if (!StringUtil.isEmpty(menuIds)) {
			if (!CommonUtils.isNetConnectionAvailable(mContext)) {
				CommonUtils.showToast(mActivity, mActivity.getString(R.string.net_error),
						Toast.LENGTH_SHORT);
				return;
			} else {
				if (mMyAppBo == null) {
					mMyAppBo = new MyAppBo(new AddAppCallback(), mContext);
				}
				mMyAppBo.addMyApp(mMobile, menuIds);
			}
		}
	}

	class AddAppCallback implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp response) {
			if (response.isSuccess()) {
				Log.d("添加应用成功...");
			} else {
				Log.d("添加应用失败...");
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
	 * 删除应用
	 * @param appId
	 */
	public void delApp(String menuId) {
		String menuIds = toJson(menuId);
		if (!StringUtil.isEmpty(menuIds)) {
			if (!CommonUtils.isNetConnectionAvailable(mContext)) {
				CommonUtils.showToast(mActivity, mActivity.getString(R.string.net_error),
						Toast.LENGTH_SHORT);
				return;
			} else {
				if (mDelBo == null) {
					mDelBo = new DelAppBo(new DelAppCallback(), mContext);
				}
				mDelBo.delMyApp(mMobile, menuIds);
			}
		}
	}

	class DelAppCallback implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp response) {
			if (response.isSuccess()) {
				Log.d("删除应用成功");
			} else {
				Log.d("删除应用失败");
			}
		}

		@Override
		public void progress(Object... obj) {
		}

		@Override
		public void onNetWorkError() {
		}

	}

	@Override
	public int getChildrenCount(int groupPosition) {
		MenuItem mHomeMenuItem = mHomeMenu.get(groupPosition);
		if (mHomeMenuItem != null) {
			String menuId = mHomeMenuItem.getMenuId();
			List<MenuItem> thirdMenu = MenuMgr.getInstance().getThirdMenuListUnique(mContext,
					menuId, mMyAppList, login);
			if (thirdMenu != null) {
				return thirdMenu.size();
			}
		}
		return 0;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return mHomeMenu.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return mHomeMenu.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
			ViewGroup parent) {
		ParentHolder parentHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.widget_addapp_partent_item, null);
			parentHolder = new ParentHolder();
			parentHolder.mParentIndicator = (ImageView) convertView
					.findViewById(R.id.app_parent_indicator);
			parentHolder.mParentText = (TextView) convertView.findViewById(R.id.app_parent_title);
			convertView.setTag(parentHolder);
		} else {
			parentHolder = (ParentHolder) convertView.getTag();
		}
		MenuItem menuItem = mHomeMenu.get(groupPosition);
		if (menuItem != null) {
			parentHolder.mParentText.setText(menuItem.getMenuName());
		}
		// 切换展开图片效果
		if (isExpanded) {
			parentHolder.mParentIndicator.setBackgroundResource(R.drawable.list_icon_arrow_down);
		} else {
			parentHolder.mParentIndicator.setBackgroundResource(R.drawable.list_icon_arrow_right);
		}
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	// 标题标签项
	final static class ParentHolder {
		ImageView mParentIndicator;
		TextView mParentText;
	}

	// 子菜单项
	final static class ChildHolder {
		ImageView mChildIcon;
		TextView mChildText;
		CheckBox mCheckbox;
	}
}
