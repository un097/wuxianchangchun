package com.ctbri.wxcc.travel;

import com.ctbri.wxcc.community.BaseFragment;

public class TravelLocateFragment extends BaseFragment {

//	private LatLng point;
//	private BitmapDescriptor location_bg;
//	private SupportMapFragment mapFragment;
//	private BaiduMap bMap;
//	private MapView mapView;
//	private Marker location_marker;
//	private boolean isInitialized;
//	public final static TravelLocateFragment newInstance(String lat,String lng){
//		double d_lat = Double.parseDouble(lat);
//		double d_lng = Double.parseDouble(lng);
//		return newInstance(d_lat, d_lng);
//	}
//	public final static TravelLocateFragment newInstance(double lat,double lng){
//		Bundle data = new Bundle();
//		data.putDouble(KEY_LATITUDE, lat);
//		data.putDouble(KEY_LONGITUDE, lng);
//		
//		TravelLocateFragment fragment = new TravelLocateFragment();
//		fragment.setArguments(data);
//		return fragment;
//	}
//	
//	/**
//	 * 存储经纬度的key
//	 */
//	public static final String KEY_LATITUDE = "latlng_point_lat";
//	public static final String KEY_LONGITUDE = "latlng_point_lng";
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		double latitude , longitude = latitude = 0;
//		
//		Bundle data = getArguments();
//		if(data!=null){
//			latitude = data.getDouble(KEY_LATITUDE);
//			longitude = data.getDouble(KEY_LONGITUDE);
//			point = new LatLng(latitude	, longitude);
//		}
//		
//	}
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		View v = inflater.inflate(R.layout.travel_locate_map, container, false);
//		((TextView)v.findViewById(R.id.action_bar_title)).setText(R.string.title_locate);
//		v.findViewById(R.id.action_bar_left_btn).setOnClickListener(new FinishClickListener());
//		//地图级别 3 ~ 19
//		//{"20m","50m","100m","200m","500m","1km","2km","5km","10km","20km","25km","50km","100km","200km","500km","1000km","2000km"}
//		MapStatus.Builder builder = new MapStatus.Builder();
//		
//			builder.overlook(-20)
//			//设置级别
//			.zoom(15);
//			
//			//设置地址中心点
//			if(point!=null)
//				builder.target(point);
//		
//			BaiduMapOptions bo = new BaiduMapOptions().mapStatus(builder.build())
//				//显示指南针
//				.compassEnabled(true)
//				//隐藏绽放按钮
//				.zoomControlsEnabled(false);
//			mapFragment = SelfMapFragment.newInstance(bo);
//		FragmentManager manager = getChildFragmentManager();
//		manager.beginTransaction().add(R.id.frame_map , mapFragment, "map_fragment").commit();
//		return v;
//	}
//	@Override
//	public void onActivityCreated(Bundle savedInstanceState) {
//		super.onActivityCreated(savedInstanceState);
//		bMap = mapFragment.getBaiduMap();
//		initPositionOverlay();
//	}
//	@Override
//	public void onResume() {
//		super.onResume();
//		bMap = mapFragment.getBaiduMap();
//		initPositionOverlay();
//	}
//	
//	private void initPositionOverlay(){
//		if(bMap==null && !isInitialized)return;
//		
//		TextView tvInfo = new TextView(activity.getApplicationContext());
//		tvInfo.setText("百年卤煮 门框胡同");
//		MarginLayoutParams mlp = new MarginLayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
//		mlp.bottomMargin = 47;
//		tvInfo.setPadding(0, 0, 0, 47);
//		tvInfo.setLayoutParams(mlp);
//		tvInfo.setBackgroundColor(getResources().getColor( R.color.black_translucent));
//		
//		isInitialized = true;
//		location_bg = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
//		MarkerOptions ooLocation = new MarkerOptions().position(point).icon(location_bg).zIndex(5).draggable(true).title("adsf");
//		location_marker = (Marker) bMap.addOverlay(ooLocation);
//		InfoWindow mInfoWindow = new InfoWindow(tvInfo, point, null);
//		bMap.showInfoWindow(mInfoWindow);
//	}
	

	@Override
	protected boolean isEnabledAnalytics() {
		return false;
	}
	@Override
	protected String getAnalyticsTitle() {
		// TODO Auto-generated method stub
		return null;
	}
}
