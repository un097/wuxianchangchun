package cn.ffcs.wisdom.city.myapp.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.datamgr.AppMgr;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.myapp.bo.DelAppBo;
import cn.ffcs.wisdom.city.myapp.entity.AppEntity;
import cn.ffcs.wisdom.city.sqlite.model.AppItem;
import cn.ffcs.wisdom.city.sqlite.model.MenuItem;
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
 * <p>Title：我的应用适配器          </p>
 * <p>Description: 
 * </p>
 * <p>@author: Leo                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-3-7             </p>
 * <p>Update Time: 2013-3-19             </p>
 * <p>Updater:   Leo                      </p>
 * <p>Update Comments:                 </p>
 */
public class EditAppGridViewAdapter extends BaseAdapter {
	private Context mContext;
	private Activity mActivity;
	private final LayoutInflater mInflater;
	// 存放服务器请求得到的数据
	private List<MenuItem> mAppList = new ArrayList<MenuItem>();
	// 应用列表appId集合，删除应用
	private List<AppEntity> appList = new ArrayList<AppEntity>();
	private String mMobile;
	// 转换成本地数据集合
	private List<AppItem> mAppLocal = new ArrayList<AppItem>();
	private DelAppBo mDelBo;
	private String cityCode;
	private CityImageLoader loader;

	public EditAppGridViewAdapter(Activity activity, List<MenuItem> list) {
		this.mContext = activity.getApplicationContext();
		this.mActivity = activity;
		this.mAppList = list;
		this.mMobile = SharedPreferencesUtil.getValue(mContext, Key.K_PHONE_NUMBER);
		this.cityCode = MenuMgr.getInstance().getCityCode(mContext);
		mInflater = (LayoutInflater) activity.getLayoutInflater();
		loader=new CityImageLoader(mContext);
	}

	@Override
	public int getCount() {
		return mAppList.size();
	}

	@Override
	public Object getItem(int position) {
		return mAppList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.gridview_item_myapp_edit, parent, false);
		}
		ImageView mMyAppIcon = (ImageView) convertView.findViewById(R.id.gird_item_indicator);
		TextView mMyAppText = (TextView) convertView.findViewById(R.id.gird_item_content);
		mMyAppText.setSelected(true);
		final ImageView mMyAppDelBtn = (ImageView) convertView.findViewById(R.id.myapp_delete_btn);
		mMyAppDelBtn.setVisibility(View.VISIBLE);
		final MenuItem item = mAppList.get(position);
		mMyAppText.setText(item.getMenuName());
		loader.loadUrl(mMyAppIcon, item.getV6Icon());

		// 点击删除，批量删除
		mMyAppDelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String menuId = item.getMenuId();
				mAppList.remove(item);
				notifyDataSetChanged();
				if (!StringUtil.isEmpty(mMobile)) {
					delApp(menuId);
				} else {
					mAppLocal.clear();
					mAppLocal = AppMgr.getInstance().queryAppByMenuId(mContext, cityCode,
							menuId);
					AppMgr.getInstance().deleteApp(mContext, mAppLocal);
				}
			}
		});
		return convertView;
	}

	/**
	 * 转换成json字符串,删除应用
	 * @param entity
	 * @return
	 */
	public String toJsonDel(String menuId) {
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
	 * 删除应用
	 * @param appId
	 */
	public void delApp(String menuId) {
		String menuIds = toJsonDel(menuId);
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
}
