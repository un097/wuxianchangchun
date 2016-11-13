package cn.ffcs.wisdom.city.myapp.fragment;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import cn.ffcs.ui.tools.AlertBaseHelper;
import cn.ffcs.wisdom.base.BaseFragment;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.datamgr.AppMgr;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.myapp.adapter.TrackInfoAdapter;
import cn.ffcs.wisdom.city.myapp.bo.DelAppBo;
import cn.ffcs.wisdom.city.myapp.bo.MyAppBo;
import cn.ffcs.wisdom.city.myapp.bo.TrackInfoBo;
import cn.ffcs.wisdom.city.myapp.entity.AppEntity;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.sqlite.model.AppItem;
import cn.ffcs.wisdom.city.sqlite.model.TrackInfo;
import cn.ffcs.wisdom.city.sqlite.service.AppService;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.SharedPreferencesUtil;
import cn.ffcs.wisdom.tools.StringUtil;
/**
 * <p>Title: TrackInfoFragment    </p>
 * <p>Description:  最近访问的Fragment    </p>
 * <p>@author: Eric.Wsd                 </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-8-20           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class TrackInfoFragment extends BaseFragment {
	private static TrackInfoFragment mTrackInfoFragment;
	static Object syncObject = new Object();

	private GridView mGridView;
	private View mLoadingTip; // 进度栏
	private Button mClearAllRecord;
	private TextView mNoContentTv; // 没有任何内容时显示信息

	List<TrackInfo> trackInfoList;
	private TrackInfoAdapter mAdapter;
	String mMobile;
	private MyAppBo mMyAppBo;
	private DelAppBo mDelBo;

	private List<AppEntity> appList = new ArrayList<AppEntity>();
	
	public static TrackInfoFragment newInstance() {
		synchronized (syncObject) {
			if (mTrackInfoFragment == null) {
				mTrackInfoFragment = new TrackInfoFragment();
			}
		}
		return mTrackInfoFragment;
	}

	@Override
	public void initComponents(View view) {
		mGridView = (GridView) view
				.findViewById(R.id.fragment_recent_track_gridview);
		mClearAllRecord = (Button) view
				.findViewById(R.id.fragment_recent_track_clear);
		mNoContentTv = (TextView) view
				.findViewById(R.id.fragment_recent_track_nocentent);
		mClearAllRecord.setOnClickListener(new OnClearClickListener());
		mGridView.setOnItemClickListener(new OnGridViewItemClickLinstener());

	}

	@Override
	public void initData() {
		mMobile = SharedPreferencesUtil.getValue(mContext, Key.K_PHONE_NUMBER);
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		request();
	}

	/**
	 * 查找最近访问的历史记录
	 */
	private void request() {
		showProgressBar();
		String cityCode = MenuMgr.getInstance().getCityCode(mContext);
		trackInfoList = TrackInfoBo.newInstance(mContext).queryAll(cityCode);

		// if (mAdapter == null) {
		mAdapter = new TrackInfoAdapter(getActivity());
		mAdapter.setData(trackInfoList);
		mGridView.setAdapter(mAdapter);
		// } else {
		// mAdapter.setData(trackInfoList);
		// mAdapter.notifyDataSetInvalidated();
		// }

		if(trackInfoList == null || trackInfoList.size() == 0) {
			mNoContentTv.setVisibility(View.VISIBLE);
			mClearAllRecord.setVisibility(View.GONE);
			mNoContentTv.bringToFront();
		}else {
			mClearAllRecord.setVisibility(View.VISIBLE);
			mNoContentTv.setVisibility(View.GONE);
		}
		hideProgressBar();
	}

	private class OnClearClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			AlertBaseHelper.showConfirm(getActivity(), "确认", "确认删除所有的最近访问记录吗？",
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							showProgressBar();
							TrackInfoBo.newInstance(getActivity()).clearAll(); // 删除所有的最近访问记录

							request();

							CommonUtils.showToast(getActivity(),
									"删除所有的最近访问记录成功！", Toast.LENGTH_SHORT);
							
							AlertBaseHelper.dismissAlert(getActivity());
						}
					});
		}
	}

	private class OnGridViewItemClickLinstener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
			TrackInfo info = (TrackInfo) parent.getAdapter().getItem(pos);

			boolean isFav = AppService.getInstance(mContext).isExist(
					AppItem.converter(info));

			String menuId = info.getMenuId();
			// 已登录，提交服务器，需判断当前网络
			if (AccountMgr.getInstance().isLogin(mContext)) {
				if (isFav) {
					// 取消操作
					delApp(menuId);
					AppMgr.getInstance().deleteApp(mContext,
							AppItem.converter(info));
				} else {
					// 添加操作
					addApp(menuId);

					AppMgr.getInstance().saveApp(mContext,
							AppItem.converter(info));
				}
			} else {// 未登录，提交到本地数据库
				if (isFav) {
					// 取消操作
					AppMgr.getInstance().deleteApp(mContext,
							AppItem.converter(info));
				} else {
					// 添加操作
					AppMgr.getInstance().saveApp(mContext,
							AppItem.converter(info));
				}
			}

			if (mAdapter != null)
				mAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 添加应用
	 * 
	 * @param menuId
	 */
	public void addApp(String menuId) {
		String menuIds = toJson(menuId);
		if (!StringUtil.isEmpty(menuIds)) {
			if (!CommonUtils.isNetConnectionAvailable(mContext)) {
				CommonUtils.showToast(getActivity(),
						getActivity().getString(R.string.net_error),
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
				Log.d("添加收藏成功...");
			} else {
				Log.d("添加收藏失败...");
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
	 * 
	 * @param appId
	 */
	public void delApp(String menuId) {
		String menuIds = toJson(menuId);
		if (!StringUtil.isEmpty(menuIds)) {
			if (!CommonUtils.isNetConnectionAvailable(mContext)) {
				CommonUtils.showToast(getActivity(),
						getActivity().getString(R.string.net_error),
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
				Log.d("删除收藏成功");
			} else {
				Log.d("删除收藏失败");
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
	 * 集合参数转换成json字符串,添加应用
	 * 
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

	public void showProgressBar() {
		if (mLoadingTip != null) {
			mLoadingTip.setVisibility(View.VISIBLE);
		}

		if (mGridView != null) {
			mGridView.setVisibility(View.INVISIBLE);
		}
	}

	public void hideProgressBar() {
		if (mLoadingTip != null) {
			mLoadingTip.setVisibility(View.INVISIBLE);
		}

		if (mGridView != null) {
			mGridView.setVisibility(View.VISIBLE);
			mGridView.bringToFront();
		}
	}

	@Override
	public int getMainContentViewId() {
		return R.layout.fragment_recent_track;
	}

}
