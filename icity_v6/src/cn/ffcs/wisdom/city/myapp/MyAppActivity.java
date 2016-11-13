package cn.ffcs.wisdom.city.myapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.ffcs.ui.tools.TopUtil;
import cn.ffcs.wisdom.city.WisdomCityActivity;
import cn.ffcs.wisdom.city.bo.AllMenuBo;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.datamgr.AppMgr;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.myapp.adapter.MyAppGridViewAdapter;
import cn.ffcs.wisdom.city.myapp.bo.MyAppBo;
import cn.ffcs.wisdom.city.myapp.entity.MyAppListEntity;
import cn.ffcs.wisdom.city.myapp.entity.MyAppListEntity.AppEntity;
import cn.ffcs.wisdom.city.sqlite.model.AppItem;
import cn.ffcs.wisdom.city.sqlite.model.MenuItem;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.SharedPreferencesUtil;
import cn.ffcs.wisdom.tools.StringUtil;

/**
 * <p>Title: 我的应用         </p>
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
public class MyAppActivity extends WisdomCityActivity {
	private final int REQUEST_ADD = 1;
	private final int REQUEST_EDIT = 2;
	private ImageView mMyAppEdit;
	private MyAppGridViewAdapter mMyAppGridViewAdapter;
	private GridView mMyAppGridView;
	private List<MenuItem> mMenuList = new ArrayList<MenuItem>();
	// 从服务器请求得到的数据
	private List<MenuItem> mMyAppList = new ArrayList<MenuItem>();
	// 从本地请求得到的数据
	private List<AppItem> mMyAppLocal = new ArrayList<AppItem>();
	private MyAppListEntity mAppListEntity;
	// 提交数据给后台处理，作为json字符串批量提交
	private List<AppEntity> mAppEntity = new ArrayList<AppEntity>();
	private String mMobile;
	private MyAppBo mMyAppBo;
	private String mCityCode = "";
	private AllMenuBo menuBo;
	private TextView mNoContent;

	private List<AppItem> mAppItemList = new ArrayList<AppItem>();

	@Override
	protected void initComponents() {
		mMyAppEdit = (ImageView) findViewById(R.id.top_right);
		mMyAppEdit.setOnClickListener(new OnEditMyApp());
		mMyAppGridView = (GridView) findViewById(R.id.myapp_list);
		mNoContent = (TextView) findViewById(R.id.myapp_nocentent);
	}

	class OnEditMyApp implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (mMyAppList.size() > 1) {
				Intent intent = new Intent(mActivity, EditMyAppActivity.class);
				intent.putExtra(Key.K_RETURN_TITLE, getString(R.string.myapp_title));
				intent.putExtra("mMyAppList", (Serializable) mMyAppList);
				startActivityForResult(intent, REQUEST_EDIT);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ADD && resultCode == RESULT_OK) {
			loadData();
		} else if (requestCode == REQUEST_EDIT && resultCode == RESULT_OK) {
			loadData();
		}
	}

	@Override
	protected void initData() {
		TopUtil.updateTitle(mActivity, R.id.top_title, R.string.myapp_title);
		mCityCode = MenuMgr.getInstance().getCityCode(mContext);
		loadData();
	}

	// 刷新数据
	private void loadData() {
		showProgressBar();
		mMyAppEdit.setVisibility(View.GONE);
		mMobile = SharedPreferencesUtil.getValue(mContext, Key.K_PHONE_NUMBER);
		getAllApp();
	}

	@Override
	protected int getMainContentViewId() {
		return R.layout.act_myapp_edit;
	}

	//FIXME 获取我的收藏的代码需要优化
	class AcquireMyAppCallbck implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp response) {
			hideProgressBar();
			mMyAppList.clear();
			if (response.isSuccess()) {
				mAppListEntity = (MyAppListEntity) response.getObj();
				if (mAppListEntity != null) {
					mAppEntity = mAppListEntity.getData();
					if (mAppEntity != null && mAppEntity.size() > 0) {
						AppMgr.getInstance().clearAllApp(mContext); //清除所有的应用本地缓存
						mAppItemList.clear();
						for (int i = 0; i < mAppEntity.size(); i++) {
							String menuId = mAppEntity.get(i).getMenuId();
							String cityCode = mAppEntity.get(i).getCityCode();
							if (!StringUtil.isEmpty(mCityCode) && mCityCode.equals(cityCode)) {
								if (!StringUtil.isEmpty(menuId)) {
									MenuItem menuItem = new MenuItem();
									menuItem = MenuMgr.getInstance().getMenu(mContext, menuId);
									if (menuItem != null) {
										mMyAppList.add(menuItem);
										AppItem item = AppMgr.getInstance().convertToAppItem(
												menuItem);
										mAppItemList.add(item);
									}
								}
							}
						}
					} else {
						mMyAppEdit.setVisibility(View.GONE);
					}
				} else {
					mMyAppEdit.setVisibility(View.GONE);
				}
			}

			AppMgr.getInstance().saveApp(mContext, mAppItemList); //保存数据到本地缓存
			mMyAppList.add(new MenuItem());
			refreshData();
		}

		@Override
		public void progress(Object... obj) {
		}

		@Override
		public void onNetWorkError() {
		}
	}

	/**
	 * 获取应用列表
	 */
	private void getAppList() {
		// 已经登录，从接口中获取数据
		if (!StringUtil.isEmpty(mMobile)) {
			mMyAppBo = new MyAppBo(new AcquireMyAppCallbck(), mContext);
			mMyAppBo.acquireMyApp(mMobile);
			return;
		} else {// 从本地加载数据
			hideProgressBar();
			mMyAppList.clear();
			mMyAppLocal = AppMgr.getInstance().queryApp(mContext, mCityCode);
			if (mMyAppLocal != null && mMyAppLocal.size() > 0) {
				mMyAppList = AppMgr.getInstance().convertToMenuList(mMyAppLocal);
			}
			mMyAppList.add(new MenuItem());
			refreshData();
		}
	}

	/**
	 * 刷新列表
	 */
	private void refreshData() {
		if (mMyAppList != null && mMyAppList.size() > 1) {
			mNoContent.setVisibility(View.GONE);
			mMyAppEdit.setVisibility(View.VISIBLE);
			TopUtil.updateRight(mActivity, R.id.top_right, R.drawable.btn_edit_icon_selector);
		} else {
			mNoContent.setVisibility(View.VISIBLE);
			mNoContent.bringToFront();
			mMyAppEdit.setVisibility(View.GONE);
		}
		mMyAppGridViewAdapter = new MyAppGridViewAdapter(mContext, mActivity, mMyAppList);
		mMyAppGridView.setAdapter(mMyAppGridViewAdapter);
	}

	/**
	 * 获取全部菜单
	 */
	private void getAllApp() {
		mMenuList = MenuMgr.getInstance().getHomeMenuList(mContext, null, false);
		if (mMenuList == null || mMenuList.size() == 0) {
			menuBo = new AllMenuBo(mActivity);
			menuBo.setHttpCallBack(new HttpCallBack<BaseResp>() {

				@Override
				public void progress(Object... obj) {
				}

				@Override
				public void onNetWorkError() {
				}

				@Override
				public void call(BaseResp response) {
					if (response.isSuccess()) {
						getAppList();
					} else {
						CommonUtils.showToast(mActivity, R.string.myapp_load_fail,
								Toast.LENGTH_SHORT);
					}
				}
			});
			String menuVer = MenuMgr.getInstance().getMenuVer(mContext, mCityCode);
			menuBo.request(mContext, mCityCode, menuVer);
		} else {
			getAppList();
		}
	}
}
