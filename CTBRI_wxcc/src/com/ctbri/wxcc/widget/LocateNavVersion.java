package com.ctbri.wxcc.widget;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.map.RouteOverlay;
import com.baidu.mapapi.map.TransitOverlay;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviPara;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPlanNode;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKRoute;
import com.baidu.mapapi.search.MKRoutePlan;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRoutePlan;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.ctbri.comm.util._Utils;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseActivity;
import com.ctbri.wxcc.community.DefaultWatchManager;
import com.ctbri.wxcc.community.WatcherManager;
import com.ctbri.wxcc.community.WatcherManagerFactory;
import com.ctbri.wxcc.entity.CommonPoint;
import com.ctbri.wxcc.widget.RoutePlanFragment.RouteType;
import com.umeng.analytics.MobclickAgent;
import com.wookii.tools.comm.LogS;

public class LocateNavVersion extends BaseActivity implements
		WatcherManagerFactory {

	// public static final String oldKey = "6rstkDGQseGTVE0lhLFInaMF";

	// public static String baidu_map_new_key_icity =
	// "zQCyqeYVyz3NQnqyE0DCLOnl";

	// 正式 key (2015-01-09 由福福团队提供)
	 public static String baidu_map_new_key_icity ="XIG5ilLShMFnByQBlnnBUlpu";

	// 测试key
//	public static String baidu_map_new_key_icity = "DOmGcI76t0upm5Bj42deXazH";
	/**
	 * 进行步行 路线搜索的最大距离
	 */
	private static final int WALK_ROUTE_MAX_DISTANCE = 1000;
	public static final String KEY_POINTS = "points";
	public static final float DEFAULT_ZOOM_LEVAL = 18;
	private ArrayList<CommonPoint> points;
	/**
	 * 无法在 Application 中创建 MapManager 对象的实例，频繁创建 BMapManager 又比较熬时，所以把该对象，存入
	 * 弱引用中。
	 */
	public static WeakReference<BMapManager> managerRef;
	private MapView mapView = null;
	private MapController mMapController = null;
	private MyPopupOverlay popOverlay;
	public BMapManager mapManager = null;
	// 定位功能，获取当前用户位置
	private LocationClient mLocClient;
	private LocationData locData;
	private MyLocOverlay myLocationOverlay;
	private MyLocationListenerImpl myLocListener;
	private TextView tv_walk_tips, tv_title, tv_right_btn;
	private boolean isFirstLoc = true;
	private boolean manaulRequest = false;
	// 要去往的目标点
	private TargetOverlay targetOverlay;
	// 是否是第一次 请求路线规划，如果是并且 路程在 1 千米以下，则标示出路线。
	private boolean isFirstRequestRoutePlan = false;
	// 第一次请求规划的路线 类型
	private RouteType firstRequestRouteType;
	// 路线搜索
	private MKSearch mSearch;

	// 要民航的目标位置
	private String targetLocation;

	private MKRoute route;

	private RouteOverlay routeOverlay;

	private MyOverlay myOverlay;

	private int defalut_view_duration;

	private boolean is_show_route_paln;

	public static Object route_plan;
	public static RouteType route_type;

	private GeoPoint myLocation;
	private String myCity, myAddr;
	private WatcherManager manager = new DefaultWatchManager();
	private View planContainer, mapContainer;
	private TextView tv_plan1, tv_plan2, tv_plan3;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initEngineManager(getApplicationContext());

		setContentView(R.layout.locate_nav_version_layout);

		initWidget();
		initData();
		initPop();

		startMyLocation();

		initRouteSearch();
		addOverlay();
	}

	/**
	 * 在第一次获取到位置数据后，进行路线搜索。<br />
	 * 大于 {@value #WALK_ROUTE_MAX_DISTANCE}m ，搜索本市 公交路线。<br>
	 * 否则 搜索步行路线。
	 */
	private void startRouteSearch() {
		GeoPoint endPoint = getAvaiblePoint();
		if (endPoint == null)
			return;
		double distance = DistanceUtil.getDistance(myLocation, endPoint);

		isFirstRequestRoutePlan = true;
		if (distance > WALK_ROUTE_MAX_DISTANCE) {
			firstRequestRouteType = RouteType.Transite;
			routeSearch(RouteType.Transite, false);
		} else {
			firstRequestRouteType = RouteType.Walk;
			routeSearch(RouteType.Walk, false);
		}
		applyPlanBtn();
		zoomToBothPoint(myLocation, endPoint);
	}

	/**
	 * 绽放至两点之间
	 * 
	 * @param p1
	 * @param p2
	 */
	private void zoomToBothPoint(GeoPoint p1, GeoPoint p2) {
		if (mapView == null || p1 == null || p2 == null)
			return;

		TargetOverlay mOverlay = new TargetOverlay(null, mapView);

		mOverlay.addItem(new OverlayItem(p1, "", ""));
		mOverlay.addItem(new OverlayItem(p2, "", ""));

		
		mapView.getController().zoomToSpan(mOverlay.getLatSpanE6(),
				mOverlay.getLonSpanE6());

		GeoPoint center = getBothPointCenter(p1, p2);
		// 移动地图到两点之间
		mapView.getController().animateTo(center);
	}
	
	/**
	 * 获取两点之间 中心的位置
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static GeoPoint getBothPointCenter(GeoPoint p1, GeoPoint p2){
		int lat, lng = lat = 0;
		int tmpSize = p1.getLatitudeE6() - p2.getLatitudeE6();
		lat = p1.getLatitudeE6() - (tmpSize / 2);
		tmpSize = p1.getLongitudeE6() - p2.getLongitudeE6();
		lng = p1.getLongitudeE6() - (tmpSize / 2);

		GeoPoint center = new GeoPoint(lat, lng);
		return center;
	}

	/**
	 * 修正 路线导航 按钮顺序
	 */
	private void applyPlanBtn() {
		RouteType[] types = new RouteType[] { RouteType.Walk,
				RouteType.Transite, RouteType.Driver };
		if (firstRequestRouteType == RouteType.Transite) {
			types[0] = RouteType.Transite;
			types[1] = RouteType.Walk;
		}
		TextView tvs[] = new TextView[] { tv_plan1, tv_plan2, tv_plan3 };
		Resources res = getResources();
		for (int i = 0; i < tvs.length; i++) {
			tvs[i].setText(types[i].getDesc());
			tvs[i].setCompoundDrawablesWithIntrinsicBounds(null,
					res.getDrawable(types[i].getIcon()), null, null);
			tvs[i].setTag(types[i]);
		}
	}

	/**
	 * 
	 * @param type
	 *            路线规划的类型 {@link RoutePlanFragment.RouteType}
	 * @param isReverse
	 *            是否 调换 起始坐标
	 */
	public void routeSearch(RouteType type, boolean isReverse) {
		GeoPoint point = getAvaiblePoint();
		// 如果我的位置 和 店铺位置不可用，直接返回
		if (null == myLocation || point == null) {
			toast("定位数据不可用！");
			return;
		}
		MKPlanNode sNode = new MKPlanNode();
		sNode.pt = myLocation;
		sNode.name = myAddr == null ? "我的位置" : myAddr;

		MKPlanNode eNode = new MKPlanNode();

		eNode.pt = point;
		// 测试设置为天安门
		// eNode.pt = new GeoPoint(39915168, 116403875);
		// eNode.pt = point;
		eNode.name = targetLocation;

		if (isReverse)
			routeSearch(type, eNode, sNode, myCity, myCity);
		else
			routeSearch(type, sNode, eNode, myCity, myCity);

	}

	private void routeSearch(RouteType type, MKPlanNode sNode,
			MKPlanNode eNode, String sCity, String eCity) {
		switch (type) {
		case Driver:
			mSearch.drivingSearch(sCity, sNode, eCity, eNode);
			break;
		case Walk:
			mSearch.walkingSearch(sCity, sNode, eCity, eNode);
			break;
		case Transite:
			mSearch.transitSearch(sCity, sNode, eNode);
			break;
		}
	}

	// 从传入的 CommonPoint 中 查找第一个 可用的 GeoPoint 坐标， 找不到返回 null
	private GeoPoint getAvaiblePoint() {
		for (CommonPoint p : points) {
			GeoPoint point = CommonPoint.parseGeoPoint(p);
			if (point != null)
				return point;
		}
		return null;
	}

	@Override
	public void onBackPressed() {
		backPressed();
	}

	/**
	 * 初始化路线搜索
	 */
	private void initRouteSearch() {
		// 初始化搜索模块，注册事件监听
		mSearch = new MKSearch();
		mSearch.init(mapManager, new RouteSearchListenerImpl());
	}

	class RouteSearchListenerImpl implements MKSearchListener {

		public void onGetDrivingRouteResult(MKDrivingRouteResult res, int error) {
			System.out.println("driving search  result");
			if (validateMKSerachResult(error, res, RouteType.Driver)) {
				int planCount = res.getNumPlan();
				List<MKRoutePlan> plans = new ArrayList<MKRoutePlan>();
				for (int i = 0; i < planCount; i++)
					plans.add(res.getPlan(i));
				manager.trigger(RouteType.Driver.ordinal(), plans);
			}

		}

		public void onGetTransitRouteResult(MKTransitRouteResult res, int error) {
			System.out.println("onGetTransitRouteResult search  result");
			if (validateMKSerachResult(error, res, RouteType.Transite)) {
				int planCount = res.getNumPlan();
				List<MKTransitRoutePlan> plans = new ArrayList<MKTransitRoutePlan>();
				for (int i = 0; i < planCount; i++)
					plans.add(res.getPlan(i));
				manager.trigger(RouteType.Transite.ordinal(), plans);

				if (isFirstRequestRoutePlan
						&& firstRequestRouteType == RouteType.Transite) {
					isFirstRequestRoutePlan = false;
					// 默认显示出第一个 计划的长度和时间
					MKTransitRoutePlan plan = res.getPlan(0);
					tv_walk_tips.setText(_Utils.getTime(plan.getTime()) + " "
							+ _Utils.getDistance(plan.getDistance()));
				}
			}
		}

		public void onGetWalkingRouteResult(MKWalkingRouteResult res, int error) {
			if (validateMKSerachResult(error, res, RouteType.Walk)) {
				route = res.getPlan(0).getRoute(0);
				// 触发回调数据
				int planCount = res.getNumPlan();
				List<MKRoutePlan> plans = new ArrayList<MKRoutePlan>();
				for (int i = 0; i < planCount; i++)
					plans.add(res.getPlan(i));
				manager.trigger(RouteType.Walk.ordinal(), plans);

				if (isFirstRequestRoutePlan
						&& firstRequestRouteType == RouteType.Walk) {

					isFirstRequestRoutePlan = false;

					routeOverlay = new MyRouteOverlay(LocateNavVersion.this,
							mapView);
					// 此处仅展示一个方案作为示例
					routeOverlay.setData(route);

					// 清除其他图层
					// mapView.getOverlays().clear();
					// 添加路线图层
					mapView.getOverlays().add(routeOverlay);
					// 执行刷新使生效
					mapView.refresh();
					// 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
					mapView.getController().zoomToSpan(
							routeOverlay.getLatSpanE6(),
							routeOverlay.getLonSpanE6());
					// 移动地图到起点
					mapView.getController().animateTo(routeOverlay.getCenter());
					// 将路线数据保存给全局变量
					tv_walk_tips.setText(_Utils.getTime(route.getTime()) + " "
							+ _Utils.getDistance(route.getDistance()));
				}

			}
		}

		public void onGetAddrResult(MKAddrInfo res, int error) {
		}

		public void onGetPoiResult(MKPoiResult res, int arg1, int arg2) {
		}

		public void onGetBusDetailResult(MKBusLineResult result, int iError) {
		}

		@Override
		public void onGetSuggestionResult(MKSuggestionResult res, int arg1) {
		}

		@Override
		public void onGetPoiDetailSearchResult(int type, int iError) {
		}

		@Override
		public void onGetShareUrlResult(MKShareUrlResult result, int type,
				int error) {
		}

	}

	private boolean validateMKSerachResult(int error, Object res, RouteType type) {
		// 起点或终点有歧义，需要选择具体的城市列表或地址列表
		if (error == MKEvent.ERROR_ROUTE_ADDR) {
			// 遍历所有地址
			// ArrayList<MKPoiInfo> stPois =
			// res.getAddrResult().mStartPoiList;
			// ArrayList<MKPoiInfo> enPois =
			// res.getAddrResult().mEndPoiList;
			// ArrayList<MKCityListInfo> stCities =
			// res.getAddrResult().mStartCityList;
			// ArrayList<MKCityListInfo> enCities =
			// res.getAddrResult().mEndCityList;
			manager.trigger(type.ordinal(), error);
			return false;
		} else if (error != 0 || res == null) {
			manager.trigger(type.ordinal(), error);
			toast("抱歉，未找到结果");
			return false;
		}
		return true;
	}

	/**
	 * 初始化百度地图引擎
	 * 
	 * @param context
	 */
	public void initEngineManager(Context context) {
		if (managerRef != null && managerRef.get() != null)
			mapManager = managerRef.get();
		else {
			LogS.i(getClass().getName(), "managerRef  ==null"
					+ (managerRef == null) + "  mapManager=" + managerRef);
			mapManager = new BMapManager(context);
			managerRef = new WeakReference<BMapManager>(mapManager);

			if (!mapManager.init(new MKGeneralImpl(
					context))) {
				Toast.makeText(getApplicationContext(), "BMapManager  初始化错误!",
						Toast.LENGTH_LONG).show();
			}
			mapManager.start();
		}
	}

	/**
	 * 开启定位功能
	 */
	private void startMyLocation() {
		// 定位初始化
		mLocClient = new LocationClient(this);
		myLocListener = new MyLocationListenerImpl();
		locData = new LocationData();
		mLocClient.registerLocationListener(myLocListener);

		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setAddrType("all");
		option.setScanSpan(15000);
		option.setProdName("com.ctri.wxcc");
		mLocClient.setLocOption(option);
		mLocClient.start();
		myLocationOverlay = new MyLocOverlay(mapView);
		// 设置定位数据
		myLocationOverlay.setData(locData);

		myLocationOverlay.setMarker(getResources().getDrawable(
				R.drawable.icon_yourplace));
		// 启用方向
		myLocationOverlay.enableCompass();
		// 添加定位图层
		mapView.getOverlays().add(myLocationOverlay);
		// 修改定位数据后刷新图层生效
		mapView.refresh();
	}

	/**
	 * 初始化控件，绑定控件事件
	 */
	private void initWidget() {
		planContainer = findViewById(R.id.frame_route_paln);
		mapContainer = findViewById(R.id.frame_map);
		// 设置标题
		tv_title = (TextView) findViewById(R.id.action_bar_title);
		tv_title.setText("线路导航");
		// 设置返回按钮
		findViewById(R.id.action_bar_left_btn).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						backPressed();
					}
				});
		tv_walk_tips = (TextView) findViewById(R.id.tv_walk_distance);

		PlanBtnClicker clicker = new PlanBtnClicker();

		tv_plan2 = (TextView) findViewById(R.id.btn_transit);
		tv_plan2.setOnClickListener(clicker);

		tv_plan1 = (TextView) findViewById(R.id.btn_walk);
		tv_plan1.setOnClickListener(clicker);

		tv_plan3 = (TextView) findViewById(R.id.btn_driver);
		tv_plan3.setOnClickListener(clicker);

		findViewById(R.id.img_my_location).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// mLocClient.requestLocation();
						// manaulRequest = true;
						if (myLocation != null) {
							mMapController.animateTo(myLocation);
							mapView.refresh();
						}
					}
				});

		tv_right_btn = (TextView) findViewById(R.id.action_bar_right_btn);
		tv_right_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				NaviPara para = new NaviPara();
				para.startPoint = myLocation;
				para.startName = "从这里开始";
				para.endPoint = new GeoPoint(39915168, 116403875);
				para.endName = "到这里结束";
				try {
					BaiduMapNavigation.openBaiduMapNavi(para,
							LocateNavVersion.this);
				} catch (BaiduMapAppNotSupportNaviException e) {
					toast("未安装百度地图，或者你的百度地图版本过低。请下载最新版本。");
				}
			}
		});
		defalut_view_duration = getResources().getInteger(
				R.integer.slide_animation_duration);
		mapView = (MapView) findViewById(R.id.mapView);
		/**
		 * 获取地图控制器
		 */
		mMapController = mapView.getController();
		mMapController.enableClick(true);
		mMapController.setZoom(DEFAULT_ZOOM_LEVAL);
	}

	/**
	 * 路线规划的点击事件
	 * 
	 * @author yanyadi
	 * 
	 */
	class PlanBtnClicker implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (v.getTag() != null)
				planIn((RouteType) v.getTag());
		}
	}

	private void planOut() {
		is_show_route_paln = false;
		tv_title.setText("线路导航");
		tv_right_btn.setVisibility(View.VISIBLE);

		FragmentManager fm = getSupportFragmentManager();
		Fragment planFragment = fm.findFragmentByTag("plan");
		if (planFragment != null && planFragment.isVisible()) {
			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			ft.setCustomAnimations(R.anim.base_nothing_alpha_right_out,
					R.anim.base_slide_right_out);
			ft.hide(planFragment);
			ft.commit();

			mapContainer.setVisibility(View.VISIBLE);
		} else {

		}

		// 显示地图
		tv_title.postDelayed(new MapViewShowCallback(), defalut_view_duration);
	}

	private void backPressed() {
		if (is_show_route_paln)
			planOut();
		else
			finish();
	}

	class MyRouteOverlay extends RouteOverlay {

		public MyRouteOverlay(Activity arg0, MapView arg1) {
			super(arg0, arg1);
			Resources res = getResources();
			setEnMarker(res.getDrawable(R.drawable.icon_routeend));
			setStMarker(res.getDrawable(R.drawable.icon_routestart));
		}

		@Override
		protected boolean onTap(int arg0) {
			OverlayItem item = getItem(arg0);
			if (route.getStart().equals(item.getPoint()))
				showPop(route.getStep(0).getContent(), item.getPoint(), 84);
			else if (route.getEnd().equals(item.getPoint()))
				showPop(route.getStep(route.getNumSteps() - 1).getContent(),
						item.getPoint(), 84);
			else
				showPop(item.getTitle(), item.getPoint(), 84);
			//
			//
			// myOverlay.removeAll();
			// myOverlay.addItem(item);
			// mMapController.animateTo(item.getPoint());
			// mapView.refresh();
			// MKStep mStep = route.getStep(arg0);
			// showPop(mStep.getContent(), mStep.getPoint(), 84);
			return true;
		}

		public void fire(GeoPoint point) {
			int c = getAllItem().size();
			for (int i = 0; i < c; i++)
				if (getItem(i).getPoint().equals(point)) {
					onTap(i);
					break;
				}
		}

		@Override
		public boolean onTap(GeoPoint arg0, MapView arg1) {
			popOverlay.hidePop();
			return super.onTap(arg0, arg1);
		}
	}

	private void planIn(RouteType type) {
		// 设置标题
		tv_title.setText(R.string.title_route_plan);
		tv_right_btn.setVisibility(View.GONE);
		is_show_route_paln = true;
		planContainer.setVisibility(View.VISIBLE);

		RoutePlanFragment planFragment = (RoutePlanFragment) getSupportFragmentManager()
				.findFragmentByTag("plan");

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.setCustomAnimations(R.anim.base_slide_right_in,
				R.anim.base_nothing_alpha_right_out);
		if (planFragment == null) {
			ft.add(R.id.frame_route_paln,
					RoutePlanFragment.newInstance(type, targetLocation), "plan");
			ft.commit();
		} else if (planFragment.isHidden()) {
			ft.show(planFragment);
			ft.commit();
			planFragment.updatePlan(type);
		} else {
		}
		// 隐藏地图
		tv_title.postDelayed(new MapViewHideCallback(), defalut_view_duration);
	}

	private void initPop() {
		popOverlay = new MyPopupOverlay(mapView, new PopOverlayClicker());
	}

	/**
	 * 添加目标 锚点，并设置地图中心位置
	 */
	private void addOverlay() {

		if (points != null) {
			Resources res = getResources();
			Drawable drawable = res.getDrawable(R.drawable.icon_gcoding);
			myOverlay = new MyOverlay(drawable, mapView);

			targetOverlay = new TargetOverlay(drawable, mapView);

			int count = points.size();
			boolean setCenter = true;
			for (int i = 0; i < count; i++) {
				CommonPoint p = points.get(i);
				GeoPoint cent = CommonPoint.parseGeoPoint(p);
				// 以第一个点，设置为地图中心。
				if (setCenter) {
					mMapController.setCenter(cent);
					setCenter = false;
				}
				OverlayItem business_location = new OverlayItem(cent,
						p.getTitle(), p.getTitle());

				targetOverlay.addItem(business_location);
			}
			mapView.getOverlays().add(myOverlay);
			mapView.getOverlays().add(targetOverlay);
			mapView.refresh();
		}
	}

	// public void updateRoutePlan(RouteType type, Object route) {
	// switch (type) {
	// case Driver:
	// case Walk: {
	// RouteOverlay routeOverlay = new MyRouteOverlay(LocateNavVersion.this,
	// mapView);
	// // 此处仅展示一个方案作为示例
	// routeOverlay.setData(((MKRoutePlan) (route)).getRoute(0));
	// // // 清除其他图层
	// // mapView.getOverlays().clear();
	// // 添加路线图层
	// mapView.getOverlays().add(routeOverlay);
	// // 执行刷新使生效
	// mapView.refresh();
	//
	// // 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
	// mapView.getController().zoomToSpan(routeOverlay.getLatSpanE6(),
	// routeOverlay.getLonSpanE6());
	// // 移动地图到起点
	// mapView.getController().animateTo(routeOverlay.getCenter());
	// }
	// break;
	// case Transite: {
	// TransitOverlay routeOverlay = new MyTransiteOverlay(
	// LocateNavVersion.this, mapView);
	// // 此处仅展示一个方案作为示例
	// routeOverlay.setData((MKTransitRoutePlan) (route));
	// // 清除其他图层
	// mapView.getOverlays().clear();
	// // 添加路线图层
	// mapView.getOverlays().add(routeOverlay);
	// // 执行刷新使生效
	// mapView.refresh();
	//
	// // 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
	// mapView.getController().zoomToSpan(routeOverlay.getLatSpanE6(),
	// routeOverlay.getLonSpanE6());
	// // 移动地图到起点
	// mapView.getController().animateTo(routeOverlay.getCenter());
	// }
	// break;
	// }
	// planOut();
	// }

	/**
	 * 初始化数据 <br />
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private void initData() {
		Intent it = getIntent();
		if (it == null)
			return;
		points = (ArrayList<CommonPoint>) getSerializeable(KEY_POINTS);
		targetLocation = getString("targetLocation");
		if (targetLocation == null) {
			if (points != null && points.size() > 0)
				targetLocation = points.get(0).getTitle();
		}
	}

	class MyTransiteOverlay extends TransitOverlay {

		public MyTransiteOverlay(Activity arg0, MapView arg1) {
			super(arg0, arg1);
			Resources res = getResources();
			setEnMarker(res.getDrawable(R.drawable.icon_routeend));
			setStMarker(res.getDrawable(R.drawable.icon_routestart));
		}

	}

	class MyPopupOverlay extends PopupOverlay {
		private ViewGroup popupView;
		private TextView tvInfo;

		public MyPopupOverlay(MapView arg0, PopupClickListener arg1) {
			super(arg0, arg1);
		}

		private void init() {
			popupView = (ViewGroup) getLayoutInflater().inflate(
					R.layout.common_popup_layout, mapView, false);
			tvInfo = (TextView) popupView.findViewById(R.id.tv_pop_title);
		}

		public void showPop(String text, GeoPoint point, int offset) {
			if (popupView != null && popupView.getParent() != null) {
				System.out.println("popupview != null \n 移除");
				((ViewGroup) popupView.getParent()).removeView(popupView);
			}

			init();

			tvInfo.setText(text);

			// popupView.measure(MeasureSpec.makeMeasureSpec(0,
			// MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0,
			// MeasureSpec.UNSPECIFIED));
			// popupView.layout(0, 0, popupView.getMeasuredWidth(),
			// popupView.getMeasuredHeight());
			// popupView.buildDrawingCache(true);
			// Bitmap bitmap = popupView.getDrawingCache();
			//
			// // int sizeMpec = MeasureSpec.makeMeasureSpec(0,
			// MeasureSpec.AT_MOST);
			// // popupView.measure(sizeMpec, sizeMpec);
			// // System.out.println(popupView.getParent());
			// mapView.requestLayout();
			showPopup(popupView, point, offset);
			// showPopup(popupView, point, offset);
		}

	}

	/**
	 * 显示 pop
	 * 
	 * @param text
	 * @param point
	 * @param offset
	 */
	private void showPop(String text, GeoPoint point, int offset) {

		// 显示pop
		popOverlay.showPop(text, point, offset);

		myOverlay.removeAll();
		OverlayItem oi = new OverlayItem(point, text, text);
		myOverlay.addItem(oi);
		mMapController.animateTo(point);
		mapView.refresh();
	}

	class MapViewShowCallback implements Runnable {
		@Override
		public void run() {
			mapView.setVisibility(View.VISIBLE);
			planContainer.setVisibility(View.GONE);
		}
	}

	class MapViewHideCallback implements Runnable {

		@Override
		public void run() {
			mapContainer.setVisibility(View.GONE);
		}

	}

	@Override
	protected void onPause() {
		mapView.onPause();
		if (mLocClient != null && mLocClient.isStarted()) {
			mLocClient.unRegisterLocationListener(myLocListener);
			mLocClient.stop();
		}
		MobclickAgent.onPageEnd("business_location_activity");
		super.onPause();
	}

	@Override
	protected void onResume() {
		mapView.onResume();
		if (mLocClient != null && !mLocClient.isStarted()) {
			mLocClient.registerLocationListener(myLocListener);
			mLocClient.start();
		}
		super.onResume();
		MobclickAgent.onPageStart("business_location_activity");
	}

	@Override
	protected void onDestroy() {
		if (mapView != null)
			mapView.destroy();
		
		destoryMapManager();
		super.onDestroy();
	}

	private void destoryMapManager() {
		if (mSearch != null) {
			mSearch.destory();
			mSearch = null;
		}

		if (mLocClient != null) {
			mLocClient.stop();
			mLocClient = null;
		}
		if (mapManager != null) {
			mapManager.stop();
			mapManager.destroy();
			mapManager = null;
			managerRef.clear();

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

	/**
	 * 目标点遮盖物
	 * 
	 * @author yanyadi
	 * 
	 */
	class TargetOverlay extends ItemizedOverlay<OverlayItem> {
		public TargetOverlay(Drawable arg0, MapView arg1) {
			super(arg0, arg1);
		}

		@Override
		public boolean onTap(GeoPoint arg0, MapView arg1) {
			popOverlay.hidePop();
			mapView.refresh();
			return super.onTap(arg0, arg1);
		}

		@Override
		protected boolean onTap(int arg0) {
			OverlayItem item = getItem(arg0);
			GeoPoint point = item.getPoint();
			showPop(item.getTitle(), point, 84);
			return true;
		}
	}

	class MyOverlay extends ItemizedOverlay<OverlayItem> {
		public MyOverlay(Drawable arg0, MapView arg1) {
			super(arg0, arg1);
		}

		@Override
		public boolean onTap(GeoPoint arg0, MapView arg1) {
			popOverlay.hidePop();
			this.removeAll();
			mapView.refresh();
			return super.onTap(arg0, arg1);
		}

		@Override
		protected boolean onTap(int arg0) {
			// OverlayItem item = getItem(arg0);
			// GeoPoint point = item.getPoint();
			// tipView.setText(item.getTitle());
			// popOverlay.showPopup(tipView, point, 84);
			// mMapController.animateTo(point);
			return false;
		}
	}

	/**
	 * 定位数据返回时，回调函数
	 * 
	 * @author yanyadi
	 * 
	 */
	class MyLocationListenerImpl implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;

			locData.latitude = location.getLatitude();
			locData.longitude = location.getLongitude();
			// 如果不显示定位精度圈，将accuracy赋值为0即可
			locData.accuracy = 200;// location.getRadius();
			// 此处可以设置 locData的方向信息, 如果定位 SDK 未返回方向信息，用户可以自己实现罗盘功能添加方向信息。
			locData.direction = location.getDerect();
			// 更新定位数据
			myLocationOverlay.setData(locData);
			// 更新图层数据执行刷新后生效
			mapView.refresh();
			// 保存当前位置
			myLocation = new GeoPoint((int) (locData.latitude * 1e6),
					(int) (locData.longitude * 1e6));
			// 保存当前城市
			myCity = location.getCity();
			myAddr = location.getAddrStr();
			// 是手动触发请求或首次定位时，移动到定位点
			if (isFirstLoc) {
				// 移动地图到定位点
				// mMapController.animateTo(myLocation);
				// myLocationOverlay.setLocationMode(LocationMode.FOLLOWING);

				// 搜索步行路线
				startRouteSearch();
			}
			if (!isFirstLoc && manaulRequest) {
				manaulRequest = false;
				mMapController.animateTo(myLocation);
				mapView.refresh();
				System.out.println("定位到 当前用户位置");
			}
			// 首次定位完成
			isFirstLoc = false;
		}

//		@Override
//		public void onReceivePoi(BDLocation arg0) {
//
//		}

	}

	class MyLocOverlay extends MyLocationOverlay {

		public MyLocOverlay(MapView arg0) {
			super(arg0);
		}

	}

	class PopOverlayClicker implements PopupClickListener {

		@Override
		public void onClickedPopup(int arg0) {

		}

	}

	@Override
	public WatcherManager getManager() {
		return manager;
	}

}
