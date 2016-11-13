package cn.ffcs.wisdom.city.myapp;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.ColorDrawable;
import android.widget.Toast;
import cn.ffcs.ui.tools.TopUtil;
import cn.ffcs.widget.TopFixExpandableListView;
import cn.ffcs.wisdom.city.WisdomCityActivity;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.myapp.adapter.AddAppExpandListViewAdapter;
import cn.ffcs.wisdom.city.sqlite.model.MenuItem;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.SharedPreferencesUtil;
import cn.ffcs.wisdom.tools.StringUtil;

/**
 * <p>Title:添加应用          </p>
 * <p>Description: 
 * 1、获取所有的应用列表
 * 2、二级菜单
 * 3、三级菜单
 * </p>
 * <p>@author: Leo                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-3-6             </p>
 * <p>Update Time: 2013-3-19             </p>
 * <p>Updater:   Leo                      </p>
 * <p>Update Comments: 
 * 	批量添加改成逐个添加               
 * 1、登录时：点击添加，提交到服务器；再次点击，取消收藏
 * 2、未登录：点击添加，提交到本地数据库；再次点击，取消收藏
 *  </p>
 */
public class AddAppActivity extends WisdomCityActivity {
	private TopFixExpandableListView mAddAppListView;// 应用列表
	private AddAppExpandListViewAdapter mAddAppAdapter;// 应用列表适配器
	// 过滤，得到已经添加过的应用列表
	private List<MenuItem> appMenu = new ArrayList<MenuItem>();
	// 已经添加的应用列表
	private List<MenuItem> mMyAppList = new ArrayList<MenuItem>();
	private String mMobile;
	private boolean login = false;

	@Override
	protected void initComponents() {
		mAddAppListView = (TopFixExpandableListView) findViewById(R.id.addapp_list);
		mAddAppListView.setCacheColorHint(0);
		mAddAppListView.setGroupIndicator(new ColorDrawable(0));
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void initData() {
		TopUtil.updateTitle(mActivity, R.id.top_title, R.string.myapp_addapp);
		mMobile = SharedPreferencesUtil.getValue(mContext, Key.K_PHONE_NUMBER);
		mMyAppList = (List<MenuItem>) getIntent().getSerializableExtra("mMyAppList");
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
		mAddAppAdapter = new AddAppExpandListViewAdapter(mActivity, appMenu, mMyAppList);
		mAddAppListView.setAdapter(mAddAppAdapter);
		if (appMenu.size() > 0) {
			mAddAppListView.expandGroup(0);
		} else {
			CommonUtils.showToast(mActivity, R.string.myapp_load_fail, Toast.LENGTH_SHORT);
		}
	}

	@Override
	public void finish() {
		setResult(RESULT_OK);
		super.finish();
	}

	@Override
	protected int getMainContentViewId() {
		return R.layout.act_addapp;
	}
}
