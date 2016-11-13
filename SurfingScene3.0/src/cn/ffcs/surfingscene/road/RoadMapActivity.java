package cn.ffcs.surfingscene.road;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.ffcs.TrafficApplication;
import cn.ffcs.config.BaseConfig;
import cn.ffcs.surfingscene.R;
import cn.ffcs.surfingscene.activity.GlobaleyeBaseActivity;
import cn.ffcs.surfingscene.common.Config;
import cn.ffcs.surfingscene.common.Key;
import cn.ffcs.surfingscene.datamgr.RoadVideoListMgr;
import cn.ffcs.surfingscene.road.bo.RoadBo;
import cn.ffcs.surfingscene.tools.VideoPlayerTool;
import cn.ffcs.widget.LoadingDialog;
import cn.ffcs.wisdom.tools.CommonUtils;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MapView.LayoutParams;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.ffcs.surfingscene.entity.GlobalEyeEntity;
import com.ffcs.surfingscene.http.HttpCallBack;
import com.ffcs.surfingscene.response.BaseResponse;

/**
 * <p>
 * Title: 地图页面
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * 
 * @author: zhangws
 *          </p>
 *          <p>
 *          Copyright: Copyright (c) 2013
 *          </p>
 *          <p>
 *          Company: ffcs Co., Ltd.
 *          </p>
 *          <p>
 *          Create Time: 2013-7-22
 *          </p>
 *          <p>
 * @author: </p>
 *          <p>
 *          Update Time:
 *          </p>
 *          <p>
 *          Updater:
 *          </p>
 *          <p>
 *          Update Comments:
 *          </p>
 */
public class RoadMapActivity extends GlobaleyeBaseActivity {

	private MapView mapView;
	private BMapManager mapManager;
	private MapController mapController;
	private View mPopView = null;// 点击mark时弹出的气泡view
	private TextView mapVideoTitle;
	private View mLocationView = null;
	private TextView mLocationTitle;
	private List<GlobalEyeEntity> eyeEntity;// 请求数据list

	// 定位相关
	private LocationClient mLocClient;
	private LocationData locData = null;
	private LocationOverlay myLocationOverlay;// 定位图层
	private BDLocation location;
	private MyLocationListenner myListener = new MyLocationListenner();

	private String areaCode;// 区域码
	private String gloType = "1031";// 数据类型
	private Drawable marker; // 有摄像头的标记点图标
	// private GeoPoint touchPoint; // 手指点击点
	private GeoPoint loactionPoint;
	private ImageView showMyLocation;

	private boolean isRequest = false;// 是否手动触发请求定位
	private boolean isFirstLoc = true;// 是否首次定位
	private boolean locaionSuccess = false;// 定位是否成功

	/**
	 * 由于MapView在setContentView()中初始化,所以它需要在BMapManager初始化之后
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mContext = getApplicationContext();
		if (mapManager == null) {
			mapManager = new BMapManager(mContext);
			mapManager.init(new MyGeneralListener());
			mapManager.start();
		}
//		TrafficApplication application = (TrafficApplication) getApplication();
//		if (application.mBMapManager == null) {
//			application.initEngineManager(mContext);
//		}
//		mapManager = application.mBMapManager;
//		mapManager.start();
		LoadingDialog.getDialog(this)
				.setMessage(getString(R.string.common_loading)).show();
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initComponents() {
		mapView = (MapView) findViewById(R.id.map_View);
		showMyLocation = (ImageView) findViewById(R.id.show_my_location);
		mPopView = getLayoutInflater().inflate(R.layout.glo_map_popup_window,
				null);// 创建点击mark时的弹出泡泡
		mapVideoTitle = (TextView) mPopView.findViewById(R.id.map_surfing_name);
		mLocationView = getLayoutInflater().inflate(
				R.layout.glo_map_popup_window, null);// 定位的弹窗
		mLocationTitle = (TextView) mLocationView
				.findViewById(R.id.map_surfing_name);
	}

	@Override
	protected void initData() {
		registerMessageReceiver();
		areaCode = getIntent().getStringExtra(Key.K_AREA_CODE);
		showMyLocation.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				isRequest = true;
				mPopView.setVisibility(View.GONE);
				if (loactionPoint != null) {
					mapController.setZoom(16); // 设置地图默认的缩放级别
					mapController.animateTo(loactionPoint);// 定位到当前位置
					mLocationView.setVisibility(View.VISIBLE);
					mapView.refresh();// 刷新地图
				} else {
					CommonUtils.showToast(mActivity, R.string.glo_loaction_ing,
							Toast.LENGTH_SHORT);
					location();
				}
			}
		});

		mapView.addView(mPopView, new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, null,
				MapView.LayoutParams.TOP_LEFT));
		mPopView.setVisibility(View.GONE);

		mapView.addView(mLocationView, new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, null,
				MapView.LayoutParams.TOP_LEFT));
		mLocationView.setVisibility(View.GONE);

		marker = getResources().getDrawable(R.drawable.camera_mark2);// 创建标记maker
																		// //TODO
		marker.setBounds(0, 0, marker.getIntrinsicWidth(),
				marker.getIntrinsicHeight());// 为maker定义位置和边界

		initmap();// 初始化地图

		eyeEntity = RoadVideoListMgr.getInstance().getGloList();
		if (eyeEntity != null) {
			handler.sendEmptyMessage(0);
//			showOverItemT();
		} else {
			geteye();// 获取全球眼视频点
		}
	}

	/**
	 * 初始化地图
	 */
	private void initmap() {
		mapView.setTraffic(true);// 设置地图模式为交通地图
		mapView.setBuiltInZoomControls(true);// 设置启用内置的缩放控件
		location();// 定位
		GeoPoint point = new GeoPoint((int) (26.08 * 1E6), (int) (119.28 * 1E6));// 用经纬度初始化中心点
		mapController = mapView.getController();// 取得地图控制器对象，用于控制MapView
		mapController.setCenter(point);// 设置地图的中心
		mapController.enableClick(true);
		mapController.setZoom(16); // 设置地图默认的缩放级别
		mapView.regMapViewListener(mapManager, new MKMapViewListener() {

			@Override
			public void onMapMoveFinish() {
				mPopView.setVisibility(View.GONE);
				mLocationView.setVisibility(View.GONE);
			}

			@Override
			public void onMapLoadFinish() {

			}

			@Override
			public void onMapAnimationFinish() {

			}

			@Override
			public void onGetCurrentMap(Bitmap arg0) {

			}

			@Override
			public void onClickMapPoi(MapPoi arg0) {

			}
		});

	}

	/**
	 * 开始定位
	 */
	private void location() {
		// 定位初始化
		if (mLocClient == null) {
			mLocClient = new LocationClient(mContext);
		}
		locData = new LocationData();
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setAddrType("all");// 返回的定位结果包含地址信息
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(500);
		mLocClient.setLocOption(option);
		mLocClient.start();

		// 定位图层初始化
		myLocationOverlay = new LocationOverlay(mapView);
		// 定位标记
		Drawable locMarker = getResources().getDrawable(
				R.drawable.location_arrows);
		myLocationOverlay.setMarker(locMarker);
		// 设置定位数据
		myLocationOverlay.setData(locData);
		// 添加定位图层
		mapView.getOverlays().add(myLocationOverlay);
		myLocationOverlay.enableCompass();
		// 修改定位数据后刷新图层生效
		mapView.refresh();
	}

	/**
	 * 获取数据
	 */
	private void geteye() {
		RoadBo.getInstance().getVideoList(mContext, areaCode,
				Config.TYPE_GROUP_LIST_VIDEO, new GeteyeCallBack());
	}

	/**
	 * 列表请求回调
	 */
	class GeteyeCallBack implements HttpCallBack<BaseResponse> {

		@Override
		public void callBack(BaseResponse resp, String url) {
			if (resp.getReturnCode().equals("1")) {
				eyeEntity = resp.getGeyes();
				RoadVideoListMgr.getInstance().setGloList(eyeEntity);
//				showOverItemT();
				handler.sendEmptyMessage(0);
			} else {
				CommonUtils.showToast(mActivity,
						getString(R.string.glo_loading_failed),
						Toast.LENGTH_SHORT);
			}
		}
	}

	// 新开线程加载点
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			int id = msg.what;
			if (0 == id) {
				showOverItemT();
			}
		};
	};


	/**
	 * 种点
	 */
	private void showOverItemT() {
		OverItemT temp = new OverItemT(marker, eyeEntity, mapView);
		temp.setClickPopListener(new ClickPopListener() {

			@Override
			public void show(final GlobalEyeEntity globalEyeEntity) {
				if (globalEyeEntity == null) {
					return;
				}
				mapVideoTitle.setText(globalEyeEntity.getPuName());
				mapVideoTitle.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(mActivity,
								RoadPlayActivity.class);
						intent.putExtra(Key.K_RETURN_TITLE,
								getString(R.string.glo_road_title));
						VideoPlayerTool.startRoadVideo(mActivity,
								globalEyeEntity, intent);
					}
				});
			}
		});
		mapView.getOverlays().add(temp); // 添加ItemizedOverlay实例到mMapView
		mapView.refresh();
	}

	/**
	 * 种点
	 */
	class OverItemT extends ItemizedOverlay<OverlayItem> {
		private List<OverlayItem> mGeoList = new ArrayList<OverlayItem>();
		private List<GlobalEyeEntity> tempList = new ArrayList<GlobalEyeEntity>();
		private List<GlobalEyeEntity> playGlobalEyeEntities = new ArrayList<GlobalEyeEntity>();
		private int playSelectedGlobalEyeIndex;
		public OverlayItem currentItem;

		public OverItemT(Drawable marker, List<GlobalEyeEntity> geyeList,
				MapView mapView) {
			super(marker, mapView);
			int size = geyeList.size();
			if (geyeList != null && size > 0) {
				for (int i = 0; i < size; i++) {
					GlobalEyeEntity globalEyeEntity = geyeList.get(i);
					if ((globalEyeEntity.getLatitude() != null && !globalEyeEntity
							.getLatitude().equals("0"))
							&& (globalEyeEntity.getLongitude() != null && !globalEyeEntity
									.getLongitude().equals("0"))) {
						// 用给定的经纬度构造GeoPoint，单位是微度 (度 * 1E6)
						int longitude = (int) (Double.valueOf(globalEyeEntity
								.getLongitude()) * 1E6);

						int latitude = (int) (Double.valueOf(globalEyeEntity
								.getLatitude()) * 1E6);

						// new一个点
						GeoPoint geyePoint = new GeoPoint(latitude, longitude);

						mGeoList.add(new OverlayItem(geyePoint, globalEyeEntity
								.getPuName(), ""));

						
						// 添加到列表
						tempList.add(globalEyeEntity);

						if (isFirstLoc && !locaionSuccess) {
							mapController.animateTo(geyePoint);

						}
					}
				}
				// 最后再添加到地图上！！
				addItem(mGeoList);
			}
			mapView.refresh();
		}

		@Override
		// 处理当点击事件
		protected boolean onTap(int index) {
//			Log.e("sb", "vonTap");
			playGlobalEyeEntities.clear();
			currentItem = getItem(index);
			GeoPoint clickPoint = currentItem.getPoint();
			int clickLongituded = clickPoint.getLongitudeE6();
			int clickLatitude = clickPoint.getLatitudeE6();

			if (tempList != null && !tempList.isEmpty()) {
				for (GlobalEyeEntity globalEyeEntity : tempList) {
					if (Double.compare(
							Double.valueOf(globalEyeEntity.getLongitude()),
							0.0d) != 0) {
						int longitude = (int) (Double.valueOf(globalEyeEntity
								.getLongitude()) * 1E6);
						int latitude = (int) (Double.valueOf(globalEyeEntity
								.getLatitude()) * 1E6);
						GeoPoint point = new GeoPoint(latitude, longitude);
						double distance = DistanceUtil.getDistance(clickPoint,
								point);
						if (0 == clickLongituded - longitude
								&& 0 == clickLatitude - latitude) {
							playGlobalEyeEntities.add(globalEyeEntity);
						} else if (distance <= 40) {
							playGlobalEyeEntities.add(globalEyeEntity);
						}
					}
				}
			}

			if (playGlobalEyeEntities.size() > 1) {
				showSelectedGlobalEyeDialog();
				mPopView.setVisibility(View.GONE);
			} else if (playGlobalEyeEntities.size() == 1) {
				showPopupOverlay(currentItem.getPoint());
				clickPopListener.show(playGlobalEyeEntities.get(0));
			}
			return true;
		}

		@Override
		public boolean onTap(GeoPoint geoPoint, MapView mapView) {
			// touchPoint = geoPoint;
//			Log.e("sb", "onTap short");
			playGlobalEyeEntities.clear();
			// 消去弹出的气泡
			mLocationView.setVisibility(View.GONE);
			mPopView.setVisibility(View.GONE);
			return super.onTap(geoPoint, mapView);
		}

		private void showSelectedGlobalEyeDialog() {
//			Log.e("sb", "showSelectedGlobalEyeDialog");
			String[] strings = new String[playGlobalEyeEntities.size()];
			for (int i = 0; i < strings.length; i++) {
				strings[i] = playGlobalEyeEntities.get(i).getPuName();
			}
			new AlertDialog.Builder(mActivity).setTitle("选择摄象头")
					.setSingleChoiceItems(strings, 0, new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							playSelectedGlobalEyeIndex = which;
						}
					}).setPositiveButton("确定", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent(mActivity,
									RoadPlayActivity.class);
							intent.putExtra(Key.K_RETURN_TITLE,
									getString(R.string.glo_road_title));
							VideoPlayerTool.startRoadVideo(mActivity,
									playGlobalEyeEntities
											.get(playSelectedGlobalEyeIndex),
									intent);
							playSelectedGlobalEyeIndex = 0;
						}
					}).setNegativeButton("取消", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							playSelectedGlobalEyeIndex = 0;
						}
					}).show();
		}

		private ClickPopListener clickPopListener;

		public void setClickPopListener(ClickPopListener clickPopListener) {
			this.clickPopListener = clickPopListener;
		}
	}

	public interface ClickPopListener {
		void show(GlobalEyeEntity globalEyeEntity);
	}

	@Override
	protected void onDestroy() {
		if (mLocClient != null)// 退出时销毁定位
			mLocClient.stop();
		//为了防止顶部四个tab栏切换的时候会闪一下  则onDestroy的时候不让地图也onDestroy  注释此代码！mapView.destroy();
//		mapView.destroy();
		if (mapManager != null) {
//			mapManager.stop();
			 mapManager.destroy();
			 mapManager = null;
		}
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// mapView.setVisibility(View.INVISIBLE);
//		mapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mapView.setVisibility(View.VISIBLE);
		mapView.onResume();
		super.onResume();
	}
	
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if (resultCode == 1)
//		{
//			mPopView.setVisibility(View.VISIBLE);
//		}
//		super.onActivityResult(requestCode, resultCode, data);
//	}

	private MessageReceiver mMessageReceiver;
	public static final String FINISH_ACTION = "PLAY_FINISH_ACTION";
	
	public class MessageReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (FINISH_ACTION.equals(intent.getAction())) {
				mPopView.setVisibility(View.VISIBLE);
			}
		}
	}
	
	public void registerMessageReceiver() {
		mMessageReceiver = new MessageReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(FINISH_ACTION);
		registerReceiver(mMessageReceiver, filter);
	}
	
	
	
	@Override
	protected int getMainContentViewId() {
		return R.layout.glo_map_show;
	}

	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
	class MyGeneralListener implements MKGeneralListener {

		@Override
		public void onGetNetworkState(int iError) {
			if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
				Toast.makeText(mContext, "您的网络出错啦！", Toast.LENGTH_LONG).show();
			} else if (iError == MKEvent.ERROR_NETWORK_DATA) {
				Toast.makeText(mContext, "输入正确的检索条件！", Toast.LENGTH_LONG)
						.show();
			}
		}

		@Override
		public void onGetPermissionState(int iError) {
			if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
				// 授权Key错误：
				Toast.makeText(mContext, "请输入正确的授权Key！", Toast.LENGTH_LONG)
						.show();
			}
		}
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			LoadingDialog.getDialog(RoadMapActivity.this).dismiss();
			if (location != null) {
				RoadMapActivity.this.location = location;
				locData.latitude = location.getLatitude();
				locData.longitude = location.getLongitude();
				locData.direction = 2.0f;
				locData.accuracy = location.getRadius();
				// 此处可以设置 locData的方向信息, 如果定位 SDK 未返回方向信息，用户可以自己实现罗盘功能添加方向信息。
				locData.direction = location.getDerect();
				// 更新定位数据
				myLocationOverlay.setData(locData);
				// 更新图层数据执行刷新后生效
				mapView.refresh();
				if (isRequest || isFirstLoc) {
					int latitude = (int) (location.getLatitude() * 1e6);// 获取当前的经度坐标值
					int longitude = (int) (location.getLongitude() * 1e6);// 获取当前的纬度坐标值
					loactionPoint = new GeoPoint(latitude, longitude);// 定义当前位置坐标
					// touchPoint = loactionPoint;
					mapController.animateTo(loactionPoint);
					showPopupOverlay(location);
					isRequest = false;
				}
				// 首次定位完成
				isFirstLoc = false;
				locaionSuccess = true;
			} else {
				locaionSuccess = false;
				CommonUtils.showToast(mActivity,
						getString(R.string.glo_loaction_fail),
						Toast.LENGTH_SHORT);
			}

		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}
	}

	/**
	 * 显示弹出窗口图层PopupOverlay
	 * 
	 * @param point
	 */
	private void showPopupOverlay(GeoPoint point) {
		if (point == null) {
			return;
		}
		mapView.updateViewLayout(mPopView, new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, point,
				-8, -40, MapView.LayoutParams.BOTTOM_CENTER));
		mPopView.bringToFront();
		mPopView.setVisibility(View.VISIBLE);
		mapView.refresh();
	}

	/**
	 * 显示弹出窗口图层PopupOverlay
	 * 
	 * @param location
	 */
	private void showPopupOverlay(BDLocation location) {
		if (location == null) {
			return;
		}
		mLocationTitle.setText(location.getAddrStr());
		mapView.updateViewLayout(mLocationView, new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				new GeoPoint((int) (location.getLatitude() * 1e6),
						(int) (location.getLongitude() * 1e6)), -5, -20,
				MapView.LayoutParams.BOTTOM_CENTER));// touchPoint
		mLocationView.bringToFront();
		mLocationView.setVisibility(View.VISIBLE);
		mapView.refresh();
	}

	class LocationOverlay extends MyLocationOverlay {

		public LocationOverlay(MapView mapView) {
			super(mapView);
		}

		@Override
		protected boolean dispatchTap() {
			showPopupOverlay(location);
			return super.dispatchTap();
		}

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mapView.onRestoreInstanceState(savedInstanceState);
	}

}
