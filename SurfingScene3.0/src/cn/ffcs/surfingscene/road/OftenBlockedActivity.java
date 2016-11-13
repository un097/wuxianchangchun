package cn.ffcs.surfingscene.road;

import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import cn.ffcs.surfingscene.R;
import cn.ffcs.surfingscene.activity.GlobaleyeBaseActivity;
import cn.ffcs.surfingscene.common.Key;
import cn.ffcs.surfingscene.road.adapter.OftenBlockedAdapter;
import cn.ffcs.surfingscene.road.bo.RoadBo;
import cn.ffcs.surfingscene.sqlite.RoadCollectService;
import cn.ffcs.surfingscene.tools.ComparatorEye;
import cn.ffcs.surfingscene.tools.VideoPlayerTool;
import cn.ffcs.widget.LoadingBar;
import cn.ffcs.wisdom.lbs.ILbsCallBack;
import cn.ffcs.wisdom.lbs.LBSLocationClient;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.StringUtil;

import com.baidu.location.BDLocation;
import com.ffcs.surfingscene.entity.GlobalEyeEntity;
import com.ffcs.surfingscene.http.HttpCallBack;
import com.ffcs.surfingscene.response.BaseResponse;

/**
 * <p>Title:      常常堵             </p>
 * <p>Description:                     </p>
 * <p>@author: zhangws                 </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-7-16           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class OftenBlockedActivity extends GlobaleyeBaseActivity {

	private ListView videoList;
	public static double lat;
	public static double lng;
	private OftenBlockedAdapter adapter;
	private String areaCode;
	private String gloType;
	private String title;
	private String phone;
	private LoadingBar loadingBar;
	private boolean isNoRoad = false;
	private RoadBo roadBo;
	private LinearLayout noData;

	@Override
	protected void initComponents() {
		noData = (LinearLayout) findViewById(R.id.no_data);
		videoList = (ListView) findViewById(R.id.video_list);
		loadingBar = (LoadingBar) findViewById(R.id.loading_bar);
		videoList.setOnItemClickListener(new VideoItemClickListener());
	}

	@Override
	protected void initData() {
		areaCode = getIntent().getStringExtra(Key.K_AREA_CODE);
		gloType = getIntent().getStringExtra(Key.K_GLO_TYPE);
		title = getIntent().getStringExtra(Key.K_TITLE_NAME);
		phone = getIntent().getStringExtra(Key.K_PHONE_NUMBER);
		isNoRoad = getIntent().getBooleanExtra(Key.K_IS_NO_ROAD, false);
		if (gloType == null) {
			gloType = "1024";
		}
		roadBo = RoadBo.getInstance();
//		getLocation();// 定位
		if (!isNoRoad && !StringUtil.isEmpty(phone)) {
			getCollect();// 获取收藏
		} else {
			geteye();// 获取视频列表
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
	 * 获取收藏
	 */
	private void getCollect() {
		roadBo.getMyCollectList(mContext, phone, gloType + ",1025", new GetCollectListCallBack());
	}

	class GetCollectListCallBack implements HttpCallBack<BaseResponse> {

		@Override
		public void callBack(BaseResponse resp, String url) {
			List<GlobalEyeEntity> list = resp.getGeyes();
			RoadCollectService.getInstance(mContext).saveCollectList(phone, list);
			geteye();// 获取视频列表
//			if (adapter != null) {
//				adapter.notifyDataSetChanged();
//			}
		}
	}

	class GeteyeCallBack implements HttpCallBack<BaseResponse> {

		@Override
		public void callBack(BaseResponse resp, String url) {
			loadingBar.setVisibility(View.GONE);
			if (resp.getReturnCode().equals("1")) {
				List<GlobalEyeEntity> list = resp.getGeyes();
				if (list != null && list.size() > 0) {
					adapter = new OftenBlockedAdapter(mActivity, phone, gloType);
					ComparatorEye comparator = new ComparatorEye(mContext,phone);
					Collections.sort(list, comparator);// 收藏置顶
					adapter.setData(list);
					adapter.setNoCollect(isNoRoad);
					videoList.setAdapter(adapter);
				} else {
					noData.setVisibility(View.VISIBLE);
				}
			} else {
				CommonUtils.showToast(mActivity, R.string.glo_error, Toast.LENGTH_SHORT);
			}
		}
	}

	private void geteye() {
		roadBo.getVideoList(mContext, areaCode, gloType, new GeteyeCallBack());
	}

	/**
	 * 发起定位
	 */
	private void getLocation() {
		LBSLocationClient mLocationClient = LBSLocationClient.getInstance(mContext);
		mLocationClient.registerLocationListener(new LocationCallBack());
		mLocationClient.startLocationService();
		mLocationClient.getLocaion();
	}

	@Override
	protected int getMainContentViewId() {
		return R.layout.glo_act_often_blocked;
	}

	class VideoItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			GlobalEyeEntity entity = (GlobalEyeEntity) parent.getAdapter().getItem(position);
			Intent intent = new Intent(mActivity, RoadPlayActivity.class);
			intent.putExtra(Key.K_IS_COLLECT, !isNoRoad);
			if (title != null) {
				intent.putExtra(Key.K_RETURN_TITLE, title);
			} else {
				intent.putExtra(Key.K_RETURN_TITLE, getString(R.string.glo_road_title));
			}
			VideoPlayerTool.startRoadVideo(mActivity, entity, intent);
		}
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
