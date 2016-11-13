package cn.ffcs.surfingscene.road;

import java.util.List;

import android.content.Intent;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;
import cn.ffcs.surfingscene.R;
import cn.ffcs.surfingscene.activity.GlobaleyeBaseActivity;
import cn.ffcs.surfingscene.common.Config;
import cn.ffcs.surfingscene.common.Key;
import cn.ffcs.surfingscene.datamgr.RoadVideoListMgr;
import cn.ffcs.surfingscene.road.OftenBlockedActivity.LocationCallBack;
import cn.ffcs.surfingscene.road.adapter.MapGroupListAdapter;
import cn.ffcs.surfingscene.tools.VideoPlayerTool;
import cn.ffcs.widget.LoadingBar;
import cn.ffcs.widget.TopFixExpandableListView;
import cn.ffcs.wisdom.lbs.ILbsCallBack;
import cn.ffcs.wisdom.lbs.LBSLocationClient;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.Log;

import com.baidu.location.BDLocation;
import com.ffcs.surfingscene.entity.ActionEntity;
import com.ffcs.surfingscene.entity.GlobalEyeEntity;
import com.ffcs.surfingscene.function.LandscapeList;
import com.ffcs.surfingscene.http.HttpCallBack;
import com.ffcs.surfingscene.response.BaseResponse;

/**
 * <p>
 * Title: 全部视频分组列表
 * </p>
 * <p>
 * Description: 取代旧版{@link MapListActivity}
 * </p>
 * <p>
 * @author: liaodl
 * </p>
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * <p>
 * Company: ffcs Co., Ltd.
 * </p>
 * <p>
 * Create Time: 2013-8-15
 * </p>
 * <p>
 * @author:
 * </p>
 * <p>
 * Update Time:
 * </p>
 * <p>
 * Updater:
 * </p>
 * <p>
 * Update Comments:
 * </p>
 */
public class MapGroupListActivity extends GlobaleyeBaseActivity {
	private MapGroupListAdapter adapter;
	private TopFixExpandableListView listView;
	private String collectType = "1024";
	private String phone;
	private List<ActionEntity> list;
	private String areaCode;
	private LoadingBar loadingBar;

	private LandscapeList listLoader; // 列表数据

	@Override
	protected void initComponents() {
		listView = (TopFixExpandableListView) findViewById(R.id.map_group_list);
		listView.setGroupIndicator(null);
		listView.setOnChildClickListener(new MapListItemClick());
		loadingBar = (LoadingBar) findViewById(R.id.loading_bar);
	}

	@Override
	protected void initData() {
		areaCode = getIntent().getStringExtra(Key.K_AREA_CODE);
		phone = getIntent().getStringExtra(Key.K_PHONE_NUMBER);
		//发起定位
		getLocation();
		list = RoadVideoListMgr.getInstance().getGloGroupList();
		if (list != null) {
			showData();
		} else {
			loadEyeList();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
	}

	/**
	 * 列表点击
	 */
	class MapListItemClick implements OnChildClickListener {

		@Override
		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {
			try {
				GlobalEyeEntity entity = (GlobalEyeEntity) parent
						.getExpandableListAdapter().getChild(groupPosition,
								childPosition);
				Intent intent = new Intent(mActivity, RoadPlayActivity.class);
				intent.putExtra(Key.K_RETURN_TITLE,
						getString(R.string.glo_video_list_name));
				VideoPlayerTool.startRoadVideo(mActivity, entity, intent, 2);
			} catch (Exception e) {
				Log.e(e.getMessage(), e);
			}
			return false;
		}
	}

	/**
	 * 显示数据
	 */
	private void showData() {
		adapter = new MapGroupListAdapter(mActivity, phone, collectType);
		if (list == null) {
			list = RoadVideoListMgr.getInstance().getGloGroupList();
		}
		adapter.setData(list);
		listView.setAdapter(adapter);
		listView.expandGroup(0);
		adapter.notifyDataSetChanged();
	}

	/**
	 * 获取列表数据 1026
	 */
	protected void loadEyeList() {
		if (listLoader == null) {
			listLoader = new LandscapeList(mContext);
		}
		loadingBar.setVisibility(View.VISIBLE);
		listLoader.getAllActionList(areaCode, Config.TYPE_GROUP_LIST_VIDEO,
				mListCall, Config.METHOD_CITY_EYELIST); // 列表
	}

	HttpCallBack<BaseResponse> mListCall = new HttpCallBack<BaseResponse>() {
		@Override
		public void callBack(BaseResponse resp, String arg1) {
			loadingBar.setVisibility(View.GONE);
			if (resp != null && "1".equals(resp.getReturnCode())) { // 成功
				List<ActionEntity> actions = resp.getActions();
				if (actions != null && actions.size() > 0) {
					RoadVideoListMgr.getInstance().setGloGroupList(actions);
					showData();
				}
			} else {
				CommonUtils.showToast(mActivity, R.string.glo_error,
						Toast.LENGTH_SHORT);
			}
		};
	};

	@Override
	protected int getMainContentViewId() {
		return R.layout.glo_act_map_group_list;
	}
	
	
	public static double lat;
	public static double lng;
	/**
	 * 发起定位
	 */
	private void getLocation() {
		LBSLocationClient mLocationClient = LBSLocationClient.getInstance(mContext);
		mLocationClient.registerLocationListener(new LocationCallBack());
		mLocationClient.startLocationService();
		mLocationClient.getLocaion();
	}
	
	class LocationCallBack implements ILbsCallBack {

		@Override
		public void call(BDLocation location) {
			if (location != null) {
				int locType = location.getLocType();
				if (locType == BDLocation.TypeGpsLocation
						|| locType == BDLocation.TypeCacheLocation
						|| locType == BDLocation.TypeNetWorkLocation) {
					lat = location.getLatitude();
					lng = location.getLongitude();
					if (adapter != null) {
						adapter.notifyDataSetChanged();
					}
				}
			}
		}
	}
	
}
