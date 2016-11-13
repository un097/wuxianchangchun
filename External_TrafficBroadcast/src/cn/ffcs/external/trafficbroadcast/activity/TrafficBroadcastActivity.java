package cn.ffcs.external.trafficbroadcast.activity;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.ffcs.TrafficApplication;
import cn.ffcs.config.BaseConfig;
import cn.ffcs.external.trafficbroadcast.bo.Traffic_IsCollectedList_Bo;
import cn.ffcs.external.trafficbroadcast.bo.Traffic_ItemDetail_Bo;
import cn.ffcs.external.trafficbroadcast.bo.Traffic_SimpleList_Bo;
import cn.ffcs.external.trafficbroadcast.entity.Traffic_Detail_Entity;
import cn.ffcs.external.trafficbroadcast.entity.Traffic_IsCollectedItem_Entity;
import cn.ffcs.external.trafficbroadcast.entity.Traffic_IsCollectedList_Entity;
import cn.ffcs.external.trafficbroadcast.entity.Traffic_ItemDetail_Entity;
import cn.ffcs.external.trafficbroadcast.entity.Traffic_SimpleItem_Entity;
import cn.ffcs.external.trafficbroadcast.entity.Traffic_SimpleList_Entity;
import cn.ffcs.external.trafficbroadcast.tool.BDHelper;
import cn.ffcs.external.trafficbroadcast.tool.MyOverlayItem;
import cn.ffcs.external.trafficbroadcast.tool.MyOverlayItem.ClickPopListener;
import cn.ffcs.external.trafficbroadcast.tool.MyOverlayItem.ItemClickListener;
import cn.ffcs.external.trafficbroadcast.view.PopWindow;
import cn.ffcs.external.trafficbroadcast.view.ScaleView;
import cn.ffcs.external.trafficbroadcast.view.ZoomControlView;
import cn.ffcs.tts.utils.TtsSpeechApi;
import cn.ffcs.tts.utils.TtsSpeechApi.SpeechDelegate;
import cn.ffcs.widget.LoadingDialog;
import cn.ffcs.wisdom.city.changecity.location.LocationUtil;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.personcenter.entity.Account;
import cn.ffcs.wisdom.city.simico.kit.util.DateUtil;
import cn.ffcs.wisdom.city.utils.Callback;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.SharedPreferencesUtil;
import cn.ffcs.wisdom.tools.StringUtil;

import com.baidu.lbsapi.auth.LBSAuthManagerListener;
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
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviPara;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.navisdk.BNaviEngineManager.NaviEngineInitListener;
import com.baidu.navisdk.BNaviPoint;
import com.baidu.navisdk.BaiduNaviManager;
import com.baidu.navisdk.BaiduNaviManager.OnStartNavigationListener;
import com.baidu.navisdk.comapi.routeplan.RoutePlanParams.NE_RoutePlan_Mode;
import com.baidu.navisdk.util.verify.BNKeyVerifyListener;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.external_trafficbroadcast.R;
import com.umeng.analytics.MobclickAgent;

/**
 * Title: 路况播报地图页面（路况详情）
 * 
 * @author daizhq
 * 
 * @date 2014.11.11
 */
public class TrafficBroadcastActivity extends Activity implements
		OnClickListener, SpeechDelegate {

	// 返回键
	private LinearLayout ll_back;

	// 爆料按钮
	private ImageView iv_camera;
	// 定位按钮
	private LinearLayout showMyLocation;
	// 语音开关
	private LinearLayout ll_speeker;
	// 导航按钮
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
//	private LocationClient mLocClient;
	private LocationData locData = null;
	// 定位图层
	private LocationOverlay myLocationOverlay;
	private BDLocation location;
//	private MyLocationListenner myListener = new MyLocationListenner();

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

	private MKSearch mSearch = null;

	// 定制道路
	private Traffic_IsCollectedList_Bo collectedBo = null;
	private Traffic_IsCollectedList_Entity collectedEntity = null;
	private List<Traffic_IsCollectedItem_Entity> collectedList = new ArrayList<Traffic_IsCollectedItem_Entity>();
	// 附近道路
	private Traffic_IsCollectedList_Bo nearBo = null;
	private Traffic_IsCollectedList_Entity nearEntity = null;
	private List<Traffic_IsCollectedItem_Entity> nearList = new ArrayList<Traffic_IsCollectedItem_Entity>();
	// 语音播报路况列表
	private List<Traffic_IsCollectedItem_Entity> mBroadcastList = null;
	// private static String latitude = null;
	// private static String longtitude = null;
	// 当前播报点
	public int currentPositio = 0;
	// 播报内容
	public String msg = "";

	// 是否在地图界面
	private boolean isAtBroadcast = false;

	private boolean mIsEngineInitSuccess = false;

	private Toast toast;

	Handler closepopHandler = new Handler();

	
	NaviEngineInitListener mNaviEngineInitListener1 = new NaviEngineInitListener() {

		public void engineInitSuccess() {

			Log.e("NaviEngineInitListener", "初始化成功");

		}

		public void engineInitStart() {

			Log.e("NaviEngineInitListener", "开始初始化");

		}

		public void engineInitFail() {

			Log.e("NaviEngineInitListener", "初始化失败");

		}

	};

	BNKeyVerifyListener mKeyVerifyListener1 = new BNKeyVerifyListener() {

		@Override
		public void onVerifySucc() {

			// TODO Auto-generated method stub

			Log.e("sb", "key校验成功");
//			Toast.makeText(TrafficBroadcastActivity.this, "key校验成功",
//
//			Toast.LENGTH_LONG).show();

		}

		@Override
		public void onVerifyFailed(int arg0, String arg1) {

			// TODO Auto-generated method stub
			Log.e("sb", "key校验失败");
			// Toast.makeText(Maintain_route_Activity.this, "key校验失败",

			// Toast.LENGTH_LONG).show();

		}
	};
	/**
	 * 由于MapView在setContentView()中初始化,所以它需要在BMapManager初始化之后
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mContext = getApplicationContext();
		// if (mapManager == null) {
		// mapManager = new BMapManager(getApplicationContext());
		// mapManager.init(BaseConfig.baidu_map_new_key_icity,
		// new MyGeneralListener());
		// mapManager.start();
		// }
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
		fiveSecondClosePopView();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		// 返回地图页面就立即后台刷新种点
		handler.removeCallbacks(mRefrashRun);
		handler.postDelayed(mRefrashRun, 00000);
	}

	protected void loadView() {

		ll_back = (LinearLayout) findViewById(R.id.ll_back);
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
		mPopView = getLayoutInflater().inflate(R.layout.glo_map_popup_window,null);
		// 定位的弹窗
		mLocationView = getLayoutInflater().inflate(R.layout.glo_map_popup_window, null);
		mLocationTitle = (TextView) mLocationView.findViewById(R.id.map_surfing_name);

		ll_back.setOnClickListener(this);
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
					CommonUtils.showToast(TrafficBroadcastActivity.this,R.string.glo_loaction_ing, Toast.LENGTH_SHORT);
					location();
				}
			}
		});

		if (currentLocationName == null || currentLocationName.equals("")) {
			popWindow = new PopWindow(TrafficBroadcastActivity.this, helpImg, "");
		} else {
			popWindow = new PopWindow(TrafficBroadcastActivity.this, helpImg, currentLocationName);
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

		mSearch = new MKSearch();
		mSearch.init(mapManager, new MKSearchListener() {
			@Override
			public void onGetAddrResult(MKAddrInfo info, int error) {
				if (error != 0 || info == null) {
					return;
				}
				if (info.type == MKAddrInfo.MK_GEOCODE) {
					// 地理编码：通过地址检索坐标点
					GeoPoint geoPt = info.geoPt;
				} else if (info.type == MKAddrInfo.MK_REVERSEGEOCODE) {
					String strAddr = info.strAddr;
				}
			}

			@Override
			public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
			}

			@Override
			public void onGetDrivingRouteResult(MKDrivingRouteResult arg0,
												int arg1) {
			}

			@Override
			public void onGetPoiDetailSearchResult(int arg0, int arg1) {
			}

			@Override
			public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {
			}

			@Override
			public void onGetShareUrlResult(MKShareUrlResult arg0, int arg1,
											int arg2) {
			}

			@Override
			public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
			}

			@Override
			public void onGetTransitRouteResult(MKTransitRouteResult arg0,
												int arg1) {
			}

			@Override
			public void onGetWalkingRouteResult(MKWalkingRouteResult arg0,
												int arg1) {
			}

		});

		// 初始化导航引擎
		BaiduNaviManager.getInstance().initEngine(this, getSdcardDir(),
				mNaviEngineInitListener, new LBSAuthManagerListener() {
					@Override
					public void onAuthResult(int status, String msg) {
						String str = null;
						if (0 == status) {
							Toast.makeText(TrafficBroadcastActivity.this,
									"请拖动地图,定位终点", 3000).show();
							Log.e("sb",
									"startBaiduNavi   "
											+ BaiduNaviManager.getInstance().checkEngineStatus(
											getApplicationContext()) + "");
						} else {
							str = "key校验失败, " + msg;
							Toast.makeText(TrafficBroadcastActivity.this, str,
									Toast.LENGTH_LONG).show();
						}
					}
				});
		Log.e("sb",
				"startBaiduNavi   "
						+ BaiduNaviManager.getInstance().checkEngineStatus(
						getApplicationContext()) + "");

		
	}

	/**
	 * 初始化地图
	 */
	private void initmap() {
		// 设置地图模式为交通地图
		mapView.setTraffic(true);
		// 设置启用内置的缩放控件
		mapView.setBuiltInZoomControls(false);
		GeoPoint point = null;
		// 如果不是从列表页面跳转过来的则表示进入自定位，如果有值则按照值去定位
		if ("".equals(latStr) || latStr == null) {
			// 定位
			location();
			// 用经纬度初始化中心点
			point = new GeoPoint((int) (26.08 * 1E6), (int) (119.28 * 1E6));
		} else {
			// 用传递过来的经纬度初始化中心点
			point = new GeoPoint((int) (Double.valueOf(latStr) * 1E6),
					(int) (Double.valueOf(lngStr) * 1E6));
		}
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
				showNavToast("点击开始导航");
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

			@SuppressWarnings("static-access")
			@Override
			public void onClickMapPoi(MapPoi mapPoiInfo) {
				/**
				 * 在此处理底图poi点击事件 显示底图poi名称并移动至该点 设置过：
				 * mMapController.enableClick(true); 时，此回调才能被触发
				 * 
				 */
				String title = "";
				if (mapPoiInfo != null) {
					title = mapPoiInfo.strText;
					mapController.animateTo(mapPoiInfo.geoPt);

					Drawable marker = getResources().getDrawable(
							R.drawable.icon_gcoding);

					final MyOverlayItem item = new MyOverlayItem(marker,
							mapView, TrafficBroadcastActivity.this,
							mapPoiInfo.geoPt, title);

					item.showLocation();

					mapView.getOverlays().add(item);
					mapView.refresh();

					item.setItemClickListener(new ItemClickListener() {

						@Override
						public void removeItemOnClickMap() {
							// TODO Auto-generated method stub
							item.removeAll();
							mapView.refresh();
						}
					});
					PopWindow pp = new PopWindow(helpImg,
							TrafficBroadcastActivity.this, title);
					showHelp();
					popWindow.popAwindow(ll_speeker, 6);
				}
			}
		});

	}

	/**
	 * 开始定位
	 */
	private void location() {
		locData = new LocationData();
		BDHelper.locate1(mContext, new Callback<BDLocation>() {
			@Override
			public boolean onData(BDLocation location) {
				if(location == null){
					return true;
				}
				if (location != null) {
				TrafficBroadcastActivity.this.location = location;
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
				//确保爆料进入获取到最新地址
				currentLocationName = location.getAddrStr();
				if(popWindow != null){
					popWindow.setCurrentLocationName(currentLocationName);
				}
				if (isRequest || isFirstLoc) {
					int latitude = (int) (location.getLatitude() * 1e6);// 获取当前的经度坐标值
					int longitude = (int) (location.getLongitude() * 1e6);// 获取当前的纬度坐标值
					loactionPoint = new GeoPoint(latitude, longitude);// 定义当前位置坐标
					mapController.animateTo(loactionPoint);
					showPopupOverlay(location);
					if(!StringUtil.isEmpty(location.getAddrStr())){
						isRequest = false;
					}
				}
				// 首次定位完成
				isFirstLoc = false;
				} else {
				CommonUtils.showToast(TrafficBroadcastActivity.this, "定位失败！",
						Toast.LENGTH_SHORT);
				}
				return false;
			}
		});

		// 定位图层初始化
		myLocationOverlay = new LocationOverlay(mapView);
		// 定位标记
		// Drawable locMarker = getResources().getDrawable(
		// R.drawable.location_arrows);
		// myLocationOverlay.setMarker(locMarker);
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

		// System.out.println("刷新。。。。。。");
		Traffic_SimpleList_Bo simpleListBo = new Traffic_SimpleList_Bo(
				TrafficBroadcastActivity.this);
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
		params.put("distance", "3");
		params.put("show_num", "50");
		params.put("longitude", longitude);
		params.put("latitude", latitude);
		params.put("sign", sign);

		simpleListBo
				.startRequestTask(
						new getSimpleListCallBack(),
						TrafficBroadcastActivity.this,
						params,
						BaseConfig.GET_SERVER_ROOT_URL()+"icity-api-client-other/icity/service/lbs/road/getNearSimpleRoadList");
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
					// handler.obtainMessage(2).sendToTarget();
					if (Boolean.parseBoolean(SharedPreferencesUtil.getValue(
							TrafficBroadcastActivity.this, Key.K_IS_LOGIN))) {
						// 获取定制道路和附近道路列表
						getCollectedList();
						getNearList();
						// 语音播报
						enableBroadcast();
					} else {
						handler.postDelayed(mRefrashRun, 60000);
					}
				}
			}
		}

		/**
		 * 种点（爆料的点！）
		 * */
		public void showCameraOverItem(List<Traffic_SimpleItem_Entity> allList) {

			Drawable marker = getResources()
					.getDrawable(R.drawable.camera_mark);

			myItems = new MyOverlayItem(marker, mapView,
					TrafficBroadcastActivity.this, popWindow, rl_title,
					helpImg, allList);
			myItems.setCameras();

			// 地图上小点的 打点点击
			myItems.setClickPopListener(new ClickPopListener() {

				@Override
				public void showDetailPop(final Traffic_SimpleItem_Entity entity) {

					showProgressBar("正在加载详情...");

					Map<String, Object> params = new HashMap<String, Object>(1);

					Account account = AccountMgr.getInstance().getAccount(
							TrafficBroadcastActivity.this);
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
							TrafficBroadcastActivity.this);
					detailBo.startRequestTask(
							new getDetailCallBack(),
							TrafficBroadcastActivity.this,
							params,
							BaseConfig.GET_SERVER_ROOT_URL()+"icity-api-client-other/icity/service/lbs/road/getRoadDetail");
				}

				@Override
				public void removePopOnClickMap() {
				}
			});
			if (mapView != null) {
				if (mapView.getOverlays() != null) {
					mapView.getOverlays().add(myItems);
					mapView.refresh();
					System.out.println("帅新了。。。");
				}
			}
		}

		@Override
		public void progress(Object... obj) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onNetWorkError() {
			// TODO Auto-generated method stub
			CommonUtils.showToast(TrafficBroadcastActivity.this, "获取该爆料失败...",
					Toast.LENGTH_SHORT);
		}
	}

	/**
	 * 打点详情回调
	 * */
	class getDetailCallBack implements HttpCallBack<BaseResp> {

		@SuppressWarnings("static-access")
		@Override
		public void call(BaseResp response) {
			// TODO Auto-generated method stub
			System.out.println("打点详情返回====>>" + response.getHttpResult());
			if (response.isSuccess()) {
				Traffic_Detail_Entity detailEntity = (Traffic_Detail_Entity) response
						.getObj();
				Traffic_ItemDetail_Entity entity = detailEntity.getData();
				// 打开顶部的自定义的白色对话框
				PopWindow popWindow5 = new PopWindow(
						TrafficBroadcastActivity.this, helpImg, entity);
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
//		if (mLocClient != null)// 退出时销毁定位
//			mLocClient.stop();
		BDHelper.stopLocation();
		mapView.destroy();
		if (mapManager != null) {
			mapManager.stop();
			// mapManager.destroy();
			// mapManager = null;
		}
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// mapView.setVisibility(View.INVISIBLE);
		mapView.onPause();
		super.onPause();
		isAtBroadcast = false;
		SharedPreferencesUtil.setBoolean(mContext, "isAtBroadcast",
				isAtBroadcast);
		handler.obtainMessage(2).sendToTarget();
		if (toast != null) {
			toast.cancel();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		handler.removeCallbacks(mRefrashRun);
	}

	@Override
	protected void onResume() {
		mapView.onResume();
		mapView.setVisibility(View.VISIBLE);
		super.onResume();
		isAtBroadcast = true;
		SharedPreferencesUtil.setBoolean(mContext, "isAtBroadcast",
				isAtBroadcast);
		if (Boolean.parseBoolean(SharedPreferencesUtil.getValue(
				TrafficBroadcastActivity.this, Key.K_IS_LOGIN))) {
			// 获取定制道路和附近道路列表
			getCollectedList();
			getNearList();
			// 语音播报
			enableBroadcast();
		}
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
//	public class MyLocationListenner implements BDLocationListener {
//
//		@Override
//		public void onReceiveLocation(BDLocation location) {
//			
//			String aaaString = "当前位置============111111=======纬度"
//										+ location.getLatitude() + "    经度======="
//										+ location.getLongitude() + "   地址====="
//										+ location.getAddrStr() + "error===="
//										+ location.getLocType() + "      时间====="
//										+ DateUtil.getNow(null) + "\n";
//								
//								Log.e("fmj",
//										"当前位置===================纬度"
//												+ location.getLatitude() + "    经度======="
//												+ location.getLongitude() + "   地址====="
//												+ location.getAddrStr() + "");
//								
//								appendData(null, aaaString.getBytes());
//			if (location != null) {
//				TrafficBroadcastActivity.this.location = location;
//				locData.latitude = location.getLatitude();
//				locData.longitude = location.getLongitude();
//				locData.direction = 2.0f;
//				locData.accuracy = location.getRadius();
//				// 此处可以设置 locData的方向信息, 如果定位 SDK 未返回方向信息，用户可以自己实现罗盘功能添加方向信息。
//				locData.direction = location.getDerect();
//				// 更新定位数据
//				myLocationOverlay.setData(locData);
//				// 更新图层数据执行刷新后生效
//				mapView.refresh();
//				if (isRequest || isFirstLoc) {
//					int latitude = (int) (location.getLatitude() * 1e6);// 获取当前的经度坐标值
//					int longitude = (int) (location.getLongitude() * 1e6);// 获取当前的纬度坐标值
//					
//					
//					String aaa = "当前位置============111111=======纬度"
//							+ location.getLatitude() + "    经度======="
//							+ location.getLongitude() + "   地址====="
//							+ location.getAddrStr() + "error===="
//							+ location.getLocType() + "      时间====="
//							+ DateUtil.getNow(null) + "\n";
//					
//					Log.e("fmj",
//							"当前位置===================纬度"
//									+ location.getLatitude() + "    经度======="
//									+ location.getLongitude() + "   地址====="
//									+ location.getAddrStr() + "");
//					
//					appendData(null, aaa.getBytes());
//					
//					
//					loactionPoint = new GeoPoint(latitude, longitude);// 定义当前位置坐标
//					// touchPoint = loactionPoint;
//					mapController.animateTo(loactionPoint);
//					showPopupOverlay(location);
//					isRequest = false;
//				}
//				// 首次定位完成
//				isFirstLoc = false;
//			} else {
//				CommonUtils.showToast(TrafficBroadcastActivity.this, "定位失败！",
//						Toast.LENGTH_SHORT);
//			}
//
//		}
//
//		public void onReceivePoi(BDLocation poiLocation) {
//			if (poiLocation == null) {
//				return;
//			}
//		}
//	}
	
	/**
	 * 往现有文件中追加数据
	 * @param file
	 * @param datas
	 * @return boolean
	 */
	public static boolean appendData(File file, byte[]... datas) {
		
		file = new File(Environment.getExternalStorageDirectory().getPath() + "/FMJ/");
		RandomAccessFile rfile = null;
		try {
			rfile = new RandomAccessFile(file, "rw");
			rfile.seek(file.length());
			for (byte[] data : datas) {
				rfile.write(data);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rfile != null) {
				try {
					rfile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
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
		if(!StringUtil.isEmpty(location.getAddrStr())){
			mLocationView.setVisibility(View.VISIBLE);
		}
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
		LocationUtil.saveLatitude(TrafficBroadcastActivity.this, lat);
		LocationUtil.saveLongitude(TrafficBroadcastActivity.this, lng);
		LocationUtil.saveLocationCityName(TrafficBroadcastActivity.this, city);
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

	@SuppressWarnings("static-access")
	@Override
	public void onClick(View vv) {
		int id = vv.getId();
		if (id == R.id.iv_camera) {
			boolean isLogin = Boolean.parseBoolean(SharedPreferencesUtil
					.getValue(TrafficBroadcastActivity.this, Key.K_IS_LOGIN));
			if (!isLogin) {
				Intent intent = new Intent();
				intent.setClassName(TrafficBroadcastActivity.this,
//						"cn.ffcs.wisdom.city.personcenter.LoginActivity");
						"cn.ffcs.changchuntv.activity.login.LoginActivity");
				startActivity(intent);
			} else {
				showHelp();
				popWindow.popAwindow(iv_camera, 1);
			}
		} else if (id == R.id.ll_speeker) {
			boolean isLogin = Boolean.parseBoolean(SharedPreferencesUtil
					.getValue(TrafficBroadcastActivity.this, Key.K_IS_LOGIN));
			if (!isLogin) {
				Intent intent = new Intent();
				intent.setClassName(TrafficBroadcastActivity.this,
//						"cn.ffcs.wisdom.city.personcenter.LoginActivity");
						"cn.ffcs.changchuntv.activity.login.LoginActivity");
						
				startActivity(intent);
			} else {
				popWindow = new PopWindow(this, helpImg, "", handler);
				showHelp();
				popWindow.popAwindow(ll_speeker, 2);
			}
		} else if (id == R.id.ll_nav) {
			MobclickAgent.onEvent(mContext,
					"E_C_trafficBroadcast_trafficNavClick");
			startBaiduNavi();
			// startNavi();
			// showHelp();
			// popWindow.popAwindow(rl_title, 5);
		} else if (id == R.id.tv_tolist) {
			Intent intent = new Intent(TrafficBroadcastActivity.this,
					TrafficListActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		} else if (id == R.id.ll_back) {
			finish();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		popWindow.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 开始导航
	 */
	public void startBaiduNavi() {
		if (loactionPoint == null) {
			Toast.makeText(mContext, "正在定位您所在位置", Toast.LENGTH_SHORT).show();
			return;
		}
		GeoPoint pt1 = loactionPoint;
		GeoPoint pt2 = mapView.getMapCenter();
		// 构建 导航参数
		double startLatitude = pt1.getLatitudeE6() / 1e6;
		double startLongitude = pt1.getLongitudeE6() / 1e6;
		String startName = "从这里开始";
		double endLatitude = pt2.getLatitudeE6() / 1e6;
		double endLongitude = pt2.getLongitudeE6() / 1e6;
		String endName = "到这里结束";

		// 这里给出一个起终点示例，实际应用中可以通过POI检索、外部POI来源等方式获取起终点坐标
		BNaviPoint startPoint = new BNaviPoint(startLongitude, startLatitude,
				startName, BNaviPoint.CoordinateType.BD09_MC);
		BNaviPoint endPoint = new BNaviPoint(endLongitude, endLatitude,
				endName, BNaviPoint.CoordinateType.BD09_MC);
		BaiduNaviManager.getInstance().launchNavigator(this, startPoint, // 起点（可指定坐标系）
				endPoint, // 终点（可指定坐标系）
				NE_RoutePlan_Mode.ROUTE_PLAN_MOD_MIN_TIME, // 算路方式
				true, // 真实导航
				BaiduNaviManager.STRATEGY_FORCE_ONLINE_PRIORITY, // 在离线策略
				new OnStartNavigationListener() { // 跳转监听

					@Override
					public void onJumpToNavigator(Bundle configParams) {
						Intent intent = new Intent(
								TrafficBroadcastActivity.this,
								BNavigatorActivity.class);  //BNavigatorActivity导航的视图类
						intent.putExtras(configParams);
						startActivity(intent);
					}

					@Override
					public void onJumpToDownloader() {
					}
				});
	}

	/**
	 * 开始导航（调用百度地图的导航）
	 */
	public void startNavi() {
		if (loactionPoint == null) {
			Toast.makeText(mContext, "正在定位您所在位置", Toast.LENGTH_SHORT).show();
			return;
		}
		GeoPoint pt1 = loactionPoint;
		GeoPoint pt2 = mapView.getMapCenter();
		// 构建 导航参数
		NaviPara para = new NaviPara();
		para.startPoint = pt1;
		para.startName = "从这里开始";
		para.endPoint = pt2;
		para.endName = "到这里结束";

		try {
			BaiduMapNavigation.openBaiduMapNavi(para, this);
		} catch (BaiduMapAppNotSupportNaviException e) {
			e.printStackTrace();
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("您尚未安装百度地图app或app版本过低，点击确认安装？");
			builder.setTitle("提示");
			builder.setPositiveButton("确认",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							BaiduMapNavigation
									.GetLatestBaiduMapApp(TrafficBroadcastActivity.this);
						}
					});

			builder.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			builder.create().show();
		}
	}

	private Runnable mRefrashRun = new Runnable() {
		@Override
		public void run() {
			getSimpleList();
		}
	};

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				handler.removeCallbacks(mRefrashRun);
				handler.postDelayed(mRefrashRun, 60000);
			}
			if (1 == msg.what) {// 播报下一条
				handler.removeCallbacks(mSpeechWinRun);
				handler.postDelayed(mSpeechWinRun, 3000);
			}
			if (2 == msg.what) {// 停止播报
				handler.removeCallbacks(mSpeechWinRun);
				TtsSpeechApi.getIntance().stopSpeaking();
			}
			if (3 == msg.what) {// 进行语音播报
				handler.removeCallbacks(mRefrashRun);
				handler.removeCallbacks(mSpeechWinRun);
				enableBroadcast();
			}
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			handler.obtainMessage(2).sendToTarget();
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
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
		LoadingDialog.getDialog(TrafficBroadcastActivity.this)
				.setMessage(message).show();
	}

	/**
	 * 隐藏进度条
	 * */
	public void hideProgressBar() {
		LoadingDialog.getDialog(TrafficBroadcastActivity.this).cancel();
	}

	/**
	 * 获取收藏列表
	 * */
	private void getCollectedList() {
		Account account = AccountMgr.getInstance().getAccount(
				TrafficBroadcastActivity.this);
		String user_id = String.valueOf(account.getData().getUserId());
		// // 测试使用用户账号
		// String user_id = "7623773";
		boolean isLogin = Boolean.parseBoolean(SharedPreferencesUtil.getValue(
				TrafficBroadcastActivity.this, Key.K_IS_LOGIN));
		if (!isLogin) {// 如果是未登录用户就不用请求收藏列表
			// handler.obtainMessage(3).sendToTarget();
			Intent intent = new Intent();
			intent.setClassName(TrafficBroadcastActivity.this,
//					"cn.ffcs.wisdom.city.personcenter.LoginActivity");
					"cn.ffcs.changchuntv.activity.login.LoginActivity");
			startActivity(intent);
		} else {
			collectedBo = new Traffic_IsCollectedList_Bo(
					TrafficBroadcastActivity.this);
			Map<String, String> params = new HashMap<String, String>(1);
			String mobile = account.getData().getMobile();
			String lat = LocationUtil
					.getLatitude(TrafficBroadcastActivity.this);
			String lng = LocationUtil
					.getLongitude(TrafficBroadcastActivity.this);
			String sign = user_id;
			// String cityCode = MenuMgr.getInstance().getCityCode(mContext);
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
			params.put("user_id", user_id);
			collectedBo
					.startRequestTask(
							new getCollectedListCallBack(),
							TrafficBroadcastActivity.this,
							params,
							BaseConfig.GET_SERVER_ROOT_URL()+"icity-api-client-other/icity/service/lbs/road/getCollectionList");
		}
	}

	/**
	 * 获取收藏列表回调
	 * */
	class getCollectedListCallBack implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp response) {
			collectedList.clear();
			if (response.isSuccess()) {
				collectedEntity = (Traffic_IsCollectedList_Entity) response
						.getObj();
				if (null != collectedEntity
						&& collectedEntity.getData().size() > 0) {
					collectedList.addAll(collectedEntity.getData());
				}
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
	 * 获取附近道路列表
	 * */
	private void getNearList() {
		nearBo = new Traffic_IsCollectedList_Bo(TrafficBroadcastActivity.this);
		String lat = LocationUtil.getLatitude(TrafficBroadcastActivity.this);
		String lng = LocationUtil.getLongitude(TrafficBroadcastActivity.this);
		nearBo = new Traffic_IsCollectedList_Bo(TrafficBroadcastActivity.this);
		Map<String, String> params = new HashMap<String, String>(1);
		Account account = AccountMgr.getInstance().getAccount(
				TrafficBroadcastActivity.this);
		String user_id = String.valueOf(account.getData().getUserId());
		// String user_id = "7623773";
		String mobile = account.getData().getMobile();
		String sign = lng + "$" + lat;
		// String cityCode = MenuMgr.getInstance().getCityCode(mContext);
		if (mobile == null || mobile.equals("")) {
			mobile = "unknown";
		}
		params.put("user_id", user_id);
		params.put("distance", "3");
		params.put("show_num", "10");
		params.put("city_code", "2201");
		params.put("org_code", "2201");
		params.put("mobile", mobile);
		params.put("longitude", lng);
		params.put("latitude", lat);
		params.put("sign", sign);
		nearBo.startRequestTask(
				new getNearListCallBack(),
				TrafficBroadcastActivity.this,
				params,
				BaseConfig.GET_SERVER_ROOT_URL()+"icity-api-client-other/icity/service/lbs/road/getNearRoadList");
	}

	/**
	 * 获取附近道路列表回调
	 * */
	class getNearListCallBack implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp response) {
			nearList.clear();
			if (response.isSuccess()) {
				nearEntity = (Traffic_IsCollectedList_Entity) response.getObj();
				if (null != nearEntity && nearEntity.getData().size() > 0) {
					nearList.addAll(nearEntity.getData());
					if (nearList.size() > 0) {
						for (int i = 0; i < nearList.size(); i++) {
							if (nearList.get(i).getIs_collected() == 1) {
								// 去掉附近道路中已收藏的项
								nearList.remove(i);
							}
						}
					}
				}
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
	 * 启动路况播报（根据语音播报、定制道路、附近道路进行选择）
	 * 
	 * @param mChoice
	 *            0：语音播报，1：定制道路，2：附近道路
	 */
	public void initBroadcastList(int mChoice) {
		mBroadcastList = new ArrayList<Traffic_IsCollectedItem_Entity>();
		if (0 == mChoice) {
			if (collectedList != null && collectedList.size() > 0) {
				mBroadcastList.addAll(collectedList);
			}

			if (nearList != null && nearList.size() > 0) {
				mBroadcastList.addAll(nearList);
			}

		} else if (1 == mChoice) {
			if (collectedList != null && collectedList.size() > 0) {
				mBroadcastList.addAll(collectedList);
			}
		} else if (2 == mChoice) {
			if (nearList != null && nearList.size() > 0) {
				mBroadcastList.addAll(nearList);
			}
		}
	}

	Boolean is_first = true;

	/**
	 * 进行语音播报
	 * 
	 * @param bb
	 *            全部
	 * @param br
	 *            定制道路
	 * @param bn
	 *            附近道路
	 */
	public void startToBroadcast(boolean bb, boolean br, boolean bn) {
		if (bb && br && bn) {
			initBroadcastList(0);// 定制道路+附近道路
			currentPositio = 0;
			if (is_first) {
				handler.post(mSpeechWinRun);
				is_first = false;
			} else {
				handler.postDelayed(mSpeechWinRun, 1);
			}
		}
		if (bb && br && !bn) {
			initBroadcastList(1);// 定制道路
			currentPositio = 0;
			if (is_first) {
				handler.post(mSpeechWinRun);
				is_first = false;
			} else {
				handler.postDelayed(mSpeechWinRun, 1);
			}
		}
		if (bb && !br && bn) {
			initBroadcastList(2);// 附近道路
			currentPositio = 0;
			if (is_first) {
				handler.post(mSpeechWinRun);
				is_first = false;
			} else {
				handler.postDelayed(mSpeechWinRun, 1);
			}
		}
	}

	/**
	 * 根据开关的状态判断是否可以进行语音播报
	 */
	private void enableBroadcast() {
		boolean bb = SharedPreferencesUtil.getBoolean(
				TrafficBroadcastActivity.this, "k_road_broadcast");
		boolean br = SharedPreferencesUtil.getBoolean(
				TrafficBroadcastActivity.this, "k_road_collected");
		boolean bn = SharedPreferencesUtil.getBoolean(
				TrafficBroadcastActivity.this, "k_road_near");
		// 语音播报
		boolean isLogin = Boolean.parseBoolean(SharedPreferencesUtil.getValue(
				TrafficBroadcastActivity.this, Key.K_IS_LOGIN));
		if (isLogin) {
			startToBroadcast(bb, br, bn);
		}
	}

	private Runnable mSpeechWinRun = new Runnable() {
		@Override
		public void run() {
			if (currentPositio < mBroadcastList.size()
					&& SharedPreferencesUtil.getBoolean(mContext,
							"isAtBroadcast")) {
				msg = mBroadcastList.get(currentPositio).getTitle()
						+ mBroadcastList.get(currentPositio).getDetail() + "";
				// msg = mBroadcastList.get(currentPositio).getDetail() + "";
				// 开始语音合成
				TtsSpeechApi.getIntance().toSpeech(
						TrafficBroadcastActivity.this, msg, 0);
			}
		}
	};

	@Override
	public void onSpeakBegin() {

	}

	@Override
	public void onSpeakPaused() {

	}

	@Override
	public void onSpeakResumed() {

	}

	@Override
	public void onBufferProgress(int percent, int beginPos, int endPos,
			String info) {

	}

	@Override
	public void onSpeakProgress(int percent) {

	}

	@Override
	public void onCompleted() {
		TtsSpeechApi.getIntance().stopSpeaking();
		if (currentPositio == mBroadcastList.size() - 1) {
			handler.removeCallbacks(mSpeechWinRun);
			mBroadcastList.clear();
			//循环播放
//			handler.postDelayed(mRefrashRun, 120000);
		} else {
			handler.obtainMessage(1).sendToTarget();
			currentPositio++;
		}
	}

	private String getSdcardDir() {
		if (Environment.getExternalStorageState().equalsIgnoreCase(
				Environment.MEDIA_MOUNTED)) {
			return Environment.getExternalStorageDirectory().toString();
		}
		return null;
	}

	private NaviEngineInitListener mNaviEngineInitListener = new NaviEngineInitListener() {
		public void engineInitSuccess() {
			mIsEngineInitSuccess = true;
//			Log.e("sb", "engineInitSuccess");
		}

		public void engineInitStart() {
//			Log.e("sb", "engineInitStart");
		}

		public void engineInitFail() {
//			Log.e("sb", "engineInitFail");
		}
	};

	public void showNavToast(String msg) {
		// Inflater意思是充气
		// LayoutInflater这个类用来实例化XML文件到其相应的视图对象的布局
		LayoutInflater inflater = getLayoutInflater();
		// 通过制定XML文件及布局ID来填充一个视图对象
		View layout = inflater.inflate(R.layout.widget_nav_toast,
				(ViewGroup) findViewById(R.id.llToast));
		TextView mShowMsg = (TextView) layout.findViewById(R.id.showMsg);
		mShowMsg.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		// 设置信息内容
		if (!StringUtil.isEmpty(msg)) {
			mShowMsg.setText(msg);
		} else {
			mShowMsg.setText("点击开始导航");
		}

		toast = new Toast(getApplicationContext());
		int[] location = new int[2];
		ll_nav.getLocationOnScreen(location);// 获取在整个屏幕内的绝对坐标
		System.out.println("view--->x坐标:" + location[0] + "view--->y坐标:"
				+ location[1]);
		toast.setGravity(Gravity.TOP | Gravity.LEFT,
				location[0] + ll_nav.getWidth(),
				location[1] - ll_nav.getHeight() / 2);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(layout);
		toast.show();
	}

	/**
	 * 五秒钟自动把定位的框框消失掉
	 */
	void fiveSecondClosePopView() {
		closepopHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mLocationView.setVisibility(View.GONE);
			}
		}, 5000);

	}
}
