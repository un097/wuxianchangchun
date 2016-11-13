package cn.ffcs.wisdom.city.myapp.fragment;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Toast;
import cn.ffcs.widget.TopFixExpandableListView;
import cn.ffcs.wisdom.base.BaseFragment;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.datamgr.AppMgr;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.myapp.adapter.AddAppExpandListViewAdapter;
import cn.ffcs.wisdom.city.sqlite.model.MenuItem;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.SharedPreferencesUtil;
import cn.ffcs.wisdom.tools.StringUtil;

/**
 * <p>Title:  CategoryFragment  </p>
 * <p>Description: 分类查找Fragment     </p>
 * <p>@author: Eric.Wsd                 </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-8-27           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class CategoryFragment extends BaseFragment {
	private static CategoryFragment mCategoryFragment;
	static Object syncObject = new Object();

	private TopFixExpandableListView mAddAppListView;// 应用列表
	private AddAppExpandListViewAdapter mAddAppAdapter;// 应用列表适配器
	// 过滤，得到已经添加过的应用列表
	private List<MenuItem> appMenu = new ArrayList<MenuItem>();
	// 已经添加的应用列表
	private List<MenuItem> mMyAppList = new ArrayList<MenuItem>();
	private String mMobile;
	private boolean login = false;

	public static CategoryFragment newInstance() {
		synchronized (syncObject) {
			if (mCategoryFragment == null) {
				mCategoryFragment = new CategoryFragment();
			}
		}
		return mCategoryFragment;
	}

	@Override
	public void initComponents(View view) {
		mAddAppListView = (TopFixExpandableListView) view.findViewById(R.id.addapp_list);
		mAddAppListView.setCacheColorHint(0);
		mAddAppListView.setGroupIndicator(new ColorDrawable(0));
	}

	@Override
	public void initData() {
		mMobile = SharedPreferencesUtil.getValue(mContext, Key.K_PHONE_NUMBER);
		String cityCode = MenuMgr.getInstance().getCityCode(mContext);
		mMyAppList = AppMgr.getInstance().convertToMenuList(
				AppMgr.getInstance().queryApp(mContext, cityCode));
		if (mMyAppList.size() >= 1) {
			mMyAppList.remove(mMyAppList.size() - 1);
		}
		if (!StringUtil.isEmpty(mMobile)) {
			login = true;
		}
		refresh();
	}

	/**
	 * 刷新数据
	 */
	private void refresh() {
		appMenu = MenuMgr.getInstance().getHomeMenuList(mContext, mMyAppList, login);
		hideProgressBar();
		mAddAppAdapter = new AddAppExpandListViewAdapter(getActivity(), appMenu, mMyAppList);
		mAddAppListView.setAdapter(mAddAppAdapter);
		if (appMenu.size() > 0) {
			mAddAppListView.expandGroup(0);
		} else {
			CommonUtils.showToast(getActivity(), R.string.myapp_load_fail, Toast.LENGTH_SHORT);
		}
	}

	@Override
	public int getMainContentViewId() {
		return R.layout.act_addapp;
	}

}
