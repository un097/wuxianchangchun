package cn.ffcs.external.trafficbroadcast.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.ffcs.TrafficApplication;
import cn.ffcs.config.BaseConfig;
import cn.ffcs.external.trafficbroadcast.bo.Traffic_ItemDetail_Bo;
import cn.ffcs.external.trafficbroadcast.bo.Traffic_SimpleList_Bo;
import cn.ffcs.external.trafficbroadcast.entity.Traffic_Detail_Entity;
import cn.ffcs.external.trafficbroadcast.entity.Traffic_ItemDetail_Entity;
import cn.ffcs.external.trafficbroadcast.entity.Traffic_SimpleItem_Entity;
import cn.ffcs.external.trafficbroadcast.entity.Traffic_SimpleList_Entity;
import cn.ffcs.external.trafficbroadcast.tool.MyOverlayItem;
import cn.ffcs.external.trafficbroadcast.tool.MyOverlayItem.ClickPopListener;
import cn.ffcs.external.trafficbroadcast.view.PopWindow;
import cn.ffcs.external.trafficbroadcast.view.ScaleView;
import cn.ffcs.external.trafficbroadcast.view.ZoomControlView;
import cn.ffcs.widget.LoadingDialog;
import cn.ffcs.wisdom.city.changecity.location.LocationUtil;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.personcenter.entity.Account;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.CommonUtils;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MapView.LayoutParams;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.external_trafficbroadcast.R;

/**
 * Title: 定点查看地图页面
 * 
 * @author daizhq
 * 
 * @date 2014.12.22
 */
public class TrafficLocationActivity extends Activity implements
		OnClickListener {

	// 爆料按钮
	private ImageView iv_camera;
	// 定位按钮
	private LinearLayout showMyLocation;
	// 语音开关
	private LinearLayout ll_speeker;
	// 分享按钮
	private LinearLayout ll_nav;
	// 遮罩层
	private static ImageView helpImg;
	// 顶部
	private RelativeLayout rl_title;
	// 跳转到列表页面图标
	private Button iv_tolist;

	PopWindow popWindow;

	private MapView mapView;
	private BMapManager mapManager;
	private MapController mapController;
	// 点击mark时弹出的气泡view
	private View mPopView = null;
	private View mLocationView = null;
	private TextView mLocationTitle;

	// 定位相关
	private LocationClient mLocClient;
	private LocationData locData = null;
	// 定位图层
	private LocationOverlay myLocationOverlay;
	private BDLocation location;
	private MyLocationListenner myListener = new MyLocationListenner();

	// 自定义地图放大缩小控件
	public ZoomControlView mZoomControlView;
	// 自定义标尺控件
	public ScaleView mScaleView;

	private GeoPoint loactionPoint;

	// 定位获取到的当前点位置名称
	private String currentLocationName = null;

	// 是否手动触发请求定位
	private boolean isRequest = false;
	// 是否首次定位
	private boolean isFirstLoc = true;
	private Context mContext;

	// 定位获取到的经纬度
	private String longitude = "";
	private String latitude = "";

	// 从列表页面传递过来的列表项的经纬度
	private String latStr = "";
	private String lngStr = "";

	MyOverlayItem myItems = null;

	private Traffic_ItemDetail_Bo detailBo = null;

	/**
	 * 由于MapView在setContentView()中初始化,所以它需要在BMapManager初始化之后
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mContext = getApplicationContext();
		TrafficApplication application = (TrafficApplication) getApplication();
		if (application.mBMapManager == null) {
			application.initEngineManager(mContext);
		}
		mapManager = application.mBMapManager;
		mapManager.start();
		setContentView(R.layout.act_traffic_map);
		super.onCreate(savedInstanceState);

		loadView();
		loadData();
	}

	protected void loadView() {

		iv_camera = (ImageView) findViewById(R.id.iv_camera);
		rl_title = (RelativeLayout) findViewById(R.id.ll_title);
		iv_tolist = (Button) findViewById(R.id.tv_tolist);

		mapView = (MapView) findViewById(R.id.map_View);

		mZoomControlView = (ZoomControlView) findViewById(R.id.zoomControlView);
		mZoomControlView.setMapView(mapView);
		mScaleView = (ScaleView) findViewById(R.id.scaleView);
		mScaleView.setMapView(mapView);

		showMyLocation = (LinearLayout) findViewById(R.id.ll_location);
		ll_speeker = (LinearLayout) findViewById(R.id.ll_speeker);
		ll_nav = (LinearLayout) findViewById(R.id.ll_nav);

		helpImg = (ImageView) findViewById(R.id.help_img);
		// 创建点击mark时的弹出泡泡
		mPopView = getLayoutInflater().inflate(R.layout.glo_map_popup_window,
				null);
		// 定位的弹窗
		mLocationView = getLayoutInflater().inflate(
				R.layout.glo_map_popup_window, null);
		mLocationTitle = (TextView) mLocationView
				.findViewById(R.id.map_surfing_name);

		iv_camera.setOnClickListener(this);
		ll_speeker.setOnClickListener(this);
		iv_tolist.setOnClickListener(this);
		ll_nav.setOnClickListener(this);
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
					CommonUtils.showToast(TrafficLocationActivity.this,
							R.string.glo_loaction_ing, Toast.LENGTH_SHORT);
					location();
				}
			}
		});

		if (currentLocationName == null || currentLocationName.equals("")) {
			popWindow = new PopWindow(this, helpImg, "");
		} else {

			popWindow = new PopWindow(this, helpImg, currentLocationName);
		}
	}

	protected void loadData() {

		// 接收从列表页面传递过来的经纬度
		latStr = getIntent().getStringExtra("lat");
		lngStr = getIntent().getStringExtra("lng");

		mapView.addView(mPopView, new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, null,
				MapView.LayoutParams.TOP_LEFT));
		mPopView.setVisibility(View.GONE);

		mapView.addView(mLocationView, new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, null,
				MapView.LayoutParams.TOP_LEFT));
		mLocationView.setVisibility(View.GONE);

		// 初始化地图
		initmap();
	}

	/**
	 * 初始化地图
	 */
	private void initmap() {
		// 设置地图模式为交通地图
		mapView.setTraffic(true);
		// 设置启用内置的缩放控件
		mapView.setBuiltInZoomControls(false);
		// 定位
		location();
		GeoPoint point = new GeoPoint((int) (Double.valueOf(latStr) * 1E6),
				(int) (Double.valueOf(lngStr) * 1E6));
		// 取得地图控制器对象，用于控制MapView
		mapController = mapView.getController();
		// 设置地图的中心
		mapController.setCenter(point);
		mapController.enableClick(true);
		// 设置地图默认的缩放级别
		mapController.setZoom(16);
		refreshScaleAndZoomControl();
		mapView.regMapViewListener(mapManager, new MKMapViewListener() {

			@Override
			public void onMapMoveFinish() {
				mPopView.setVisibility(View.GONE);
				mLocationView.setVisibility(View.GONE);
			}

			@Override
			public void onMapLoadFinish() {
				refreshScaleAndZoomControl();
			}

			@Override
			public void onMapAnimationFinish() {
				refreshScaleAndZoomControl();
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
//		mLocClient = new LocationClient(getApplicationContext());
//		locData = new LocationData();
//		mLocClient.registerLocationListener(myListener);
//		LocationClientOption option = new LocationClientOption();
//		option.setOpenGps(true);// 打开gps
//		option.setAddrType("all");// 返回的定位结果包含地址信息
//		option.setCoorType("bd09ll"); // 设置坐标类型
//		option.setScanSpan(500);
//		mLocClient.setLocOption(option);
//		mLocClient.start();

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
	 * 更新缩放按钮的状态
	 */
	private void refreshScaleAndZoomControl() {
		mZoomControlView.refreshZoomButtonStatus(Math.round(mapView
				.getZoomLevel()));
		mScaleView.refreshScaleView(Math.round(mapView.getZoomLevel()));
	}

	/**
	 * 获取地图页面上的图钉点
	 * */
	private void getSimpleList() {
		// TODO Auto-generated method stub

		Traffic_SimpleList_Bo simpleListBo = new Traffic_SimpleList_Bo(
				TrafficLocationActivity.this);
		Map<String, String> params = new HashMap<String, String>(1);

		Account account = AccountMgr.getInstance().getAccount(mContext);
		String mobile = account.getData().getMobile();
		if (mobile == null || mobile.equals("")) {
			mobile = "unknown";
		}

		// String cityCode = MenuMgr.getInstance().getCityCode(mContext);

		String sign = longitude + "$" + latitude;

		params.put("city_code", "2201");
		params.put("org_code", "2201");
		params.put("mobile", mobile);
		// params.put("distance", "3");
		params.put("show_num", "50");
		params.put("longitude", longitude);
		params.put("latitude", latitude);
		params.put("sign", sign);

		simpleListBo
				.startRequestTask(
						new getSimpleListCallBack(),
						TrafficLocationActivity.this,
						params,
						"http://ccgd.153.cn:50081/icity-api-client-other/icity/service/lbs/road/getNearSimpleRoadList");
	}

	/**
	 * 获取打点列表回调
	 * */
	class getSimpleListCallBack implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp response) {
			// TODO Auto-generated method stub

			if (response.isSuccess()) {
				System.out.println("打点类表返回===>>" + response.getHttpResult());
				Traffic_SimpleList_Entity result = (Traffic_SimpleList_Entity) response
						.getObj();

				if (result != null) {
					showCameraOverItem(result.getData());
				}
			}
		}

		/**
		 * 种点
		 * */
		public void showCameraOverItem(List<Traffic_SimpleItem_Entity> allList) {

			Drawable marker = getResources()
					.getDrawable(R.drawable.camera_mark);

			myItems = new MyOverlayItem(marker, mapView,
					TrafficLocationActivity.this, popWindow, rl_title, helpImg,
					allList);
			myItems.setCameras();

			// 打点点击
			myItems.setClickPopListener(new ClickPopListener() {

				@Override
				public void showDetailPop(final Traffic_SimpleItem_Entity entity) {

					showProgressBar("正在加载详情...");

					Map<String, Object> params = new HashMap<String, Object>(1);

					Account account = AccountMgr.getInstance().getAccount(
							TrafficLocationActivity.this);
					String user_id = String.valueOf(account.getData()
							.getUserId());
					String mobile = account.getData().getMobile();
					String lat = latitude;
					String lng = longitude;
					String sign = String.valueOf(entity.getId());

					// String cityCode =
					// MenuMgr.getInstance().getCityCode(mContext);

					if (lat == null || lat.equals("")) {
						lat = "unknown";
					}
					if (lng == null || lng.equals("")) {
						lng = "unknown";
					}
					if (mobile == null || mobile.equals("")) {
						mobile = "unknown";
					}

					params.put("city_code", "2201");
					params.put("org_code", "2201");
					params.put("mobile", mobile);
					params.put("longitude", lng);
					params.put("latitude", lat);
					params.put("sign", sign);
					params.put("road_id", entity.getId());

					detailBo = new Traffic_ItemDetail_Bo(
							TrafficLocationActivity.this);
					detailBo.startRequestTask(
							new getDetailCallBack(),
							TrafficLocationActivity.this,
							params,
							"http://ccgd.153.cn:50081/icity-api-client-other/icity/service/lbs/road/getRoadDetail");
				}

				@Override
				public void removePopOnClickMap() {
				}
			});
			mapView.getOverlays().add(myItems);
			mapView.refresh();
		}

		@Override
		public void progress(Object... obj) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onNetWorkError() {
			// TODO Auto-generated method stub
			CommonUtils.showToast(TrafficLocationActivity.this, "获取该爆料失败...",
					Toast.LENGTH_SHORT);
		}
	}

	/**
	 * 打点详情回调
	 * */
	class getDetailCallBack implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp response) {
			// TODO Auto-generated method stub
			System.out.println("打点详情返回====>>" + response.getHttpResult());
			if (response.isSuccess()) {
				Traffic_Detail_Entity detailEntity = (Traffic_Detail_Entity) response
						.getObj();
				Traffic_ItemDetail_Entity entity = detailEntity.getData();
				PopWindow popWindow5 = new PopWindow(
						TrafficLocationActivity.this, helpImg, entity);
				showHelp();
				popWindow5.popAwindow(rl_title, 5);
				hideProgressBar();
			}
		}

		@Override
		public void progress(Object... obj) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onNetWorkError() {
			// TODO Auto-generated method stub
		}
	}

	@Override
	protected void onDestroy() {
		if (mLocClient != null)// 退出时销毁定位
			mLocClient.stop();
		mapView.destroy();
		if (mapManager != null) {
			mapManager.stop();
//			mapManager.destroy();
//			mapManager = null;
		}
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// mapView.setVisibility(View.INVISIBLE);
		 mapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mapView.onResume();
		mapView.setVisibility(View.VISIBLE);
		super.onResume();
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
				Toast.makeText(mContext, "您的应用没有获得正确的百度授权Key！",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location != null) {
				TrafficLocationActivity.this.location = location;
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
			} else {
				CommonUtils.showToast(TrafficLocationActivity.this, "定位失败！",
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
	 * @param location
	 */
	private void showPopupOverlay(BDLocation location) {
		if (location == null) {
			return;
		}
		mLocationTitle.setText(location.getAddrStr());
		currentLocationName = location.getAddrStr();

		// 爆料的时候需要当前位置名称，初始化弹出框的时候将地点名称传递过去
		if (currentLocationName == null || currentLocationName.equals("")) {
			popWindow = new PopWindow(this, helpImg, "");
		} else {

			popWindow = new PopWindow(this, helpImg, currentLocationName);
		}
		mapView.updateViewLayout(mLocationView, new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				new GeoPoint((int) (location.getLatitude() * 1e6),
						(int) (location.getLongitude() * 1e6)), -5, -20,
				MapView.LayoutParams.BOTTOM_CENTER));// touchPoint
		mLocationView.bringToFront();
		mLocationView.setVisibility(View.VISIBLE);
		mapView.refresh();

		longitude = String.valueOf(location.getLongitude());
		latitude = String.valueOf(location.getLatitude());

		saveLocationInfo(latitude, longitude,
				String.valueOf(location.getCity()));

		// 获取当前种点列表
		getSimpleList();

	}

	// 保存定位信息
	private void saveLocationInfo(String lat, String lng, String city) {
		LocationUtil.saveLatitude(TrafficLocationActivity.this, lat);
		LocationUtil.saveLongitude(TrafficLocationActivity.this, lng);
		LocationUtil.saveLocationCityName(TrafficLocationActivity.this, city);
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

	@Override
	public void onClick(View vv) {
		int id = vv.getId();
		if (id == R.id.iv_camera) {
			showHelp();
			popWindow.popAwindow(iv_camera, 1);
		} else if (id == R.id.ll_speeker) {
			showHelp();
			popWindow.popAwindow(ll_speeker, 2);
		} else if (id == R.id.ll_nav) {
			showHelp();
			popWindow.popAwindow(rl_title, 5);
		} else if (id == R.id.tv_tolist) {
			Intent intent = new Intent(TrafficLocationActivity.this,
					TrafficListActivity.class);
			startActivity(intent);
		} else {
		}
	}

	/**
	 * 显示引导
	 */
	private static void showHelp() {
		helpImg.setVisibility(View.VISIBLE);
		helpImg.setBackgroundResource(R.drawable.cover_bg);
	}

	/**
	 * 读取进度条
	 * */
	public void showProgressBar(String message) {
		LoadingDialog.getDialog(TrafficLocationActivity.this)
				.setMessage(message).show();
	}

	/**
	 * 隐藏进度条
	 * */
	public void hideProgressBar() {
		LoadingDialog.getDialog(TrafficLocationActivity.this).cancel();
	}

}
