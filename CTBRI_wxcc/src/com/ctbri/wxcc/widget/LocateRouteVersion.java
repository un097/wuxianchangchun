package com.ctbri.wxcc.widget;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.map.RouteOverlay;
import com.baidu.mapapi.map.TransitOverlay;
import com.baidu.mapapi.search.MKLine;
import com.baidu.mapapi.search.MKRoute;
import com.baidu.mapapi.search.MKRoutePlan;
import com.baidu.mapapi.search.MKStep;
import com.baidu.mapapi.search.MKTransitRoutePlan;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.ctbri.comm.util._Utils;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseActivity;
import com.ctbri.wxcc.community.ObjectAdapter;
import com.ctbri.wxcc.widget.RoutePlanFragment.RouteType;
import com.umeng.analytics.MobclickAgent;

public class LocateRouteVersion extends BaseActivity implements OnItemClickListener{
	/**
	 * 无法在 Application 中创建 MapManager 对象的实例，频繁创建 BMapManager 又比较熬时，所以把该对象，存入
	 * 弱引用中。
	 */
//	public static WeakReference<BMapManager> managerRef;
	private MapView mapView = null;
	private MapController mMapController = null;
	private MyPopupOverlay popOverlay;
	private TextView tv_route_name, tv_distance, tv_time;
	private List<StepHold> all_sh;
	private List<MKStep> all_steps;
	private LayoutInflater inflater;
	private Resources res;
	private MyOverlay myOverlay ;
	
	private GeoPoint start, end;
	private static final int ZOOM_DELAY = 2000;
	private MyRouteOverlay routeOverlay;
	
	public BMapManager mapManager = null;
	
	private TransitOverlay transiteOverlay ;


	private MKRoute route;
	private MKTransitRoutePlan transite;

	private ListView lv_steps;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initEngineManager(getApplicationContext());

		setContentView(R.layout.locate_route_plan_layout);

		initWidget();
		initRoutePaln();
		initPop();
	}

	@Override
	public void onBackPressed() {
		backPressed();
	}

	private void initRoutePaln() {
		if (LocateNavVersion.route_plan != null) {
			updateRoutePlan(LocateNavVersion.route_type,
					LocateNavVersion.route_plan);

			String routeName = "";
			int time = 0, distance = 0;

			if (this.route != null) {

				time = this.route.getTime();
				routeName = this.route.getTip().replaceAll("_", "—");;
				distance = this.route.getDistance();
				fillRoutes(this.route);
			} else {
				time = transite.getTime();
				routeName = transite.getContent().replaceAll("_", "—");
				distance = transite.getDistance();
				fillRoutes(transite);
			}

			tv_route_name.setText(routeName);
			tv_distance.setText(_Utils.getDistance(distance));
			tv_time.setText(_Utils.getTime(time));
		}
	}

	/**
	 * 初始化百度地图引擎
	 * 
	 * @param context
	 */
	public void initEngineManager(Context context) {
		if (LocateNavVersion.managerRef != null && LocateNavVersion.managerRef.get() != null)
			mapManager = LocateNavVersion.managerRef.get();
		else {
//			LogS.i(getClass().getName(), "managerRef  ==null"
//					+ (managerRef == null) + "  mapManager=" + managerRef);
			mapManager = new BMapManager(context);
			LocateNavVersion.managerRef = new WeakReference<BMapManager>(mapManager);

			if (!mapManager.init(
					null)) {
				Toast.makeText(getApplicationContext(), "BMapManager  初始化错误!",
						Toast.LENGTH_LONG).show();
			}
			mapManager.start();
		}
	}

	/**
	 * 初始化控件，绑定控件事件
	 */
	private void initWidget() {
		inflater = getLayoutInflater();
		res = getResources();
		// 设置标题
		TextView tv_title = (TextView) findViewById(R.id.action_bar_title);
		tv_title.setText(R.string.title_route_plan);
		// 设置返回按钮
		findViewById(R.id.action_bar_left_btn).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						backPressed();
					}
				});

		tv_route_name = (TextView) findViewById(R.id.tv_route_name);
		tv_distance = (TextView) findViewById(R.id.tv_distance);
		tv_time = (TextView) findViewById(R.id.tv_time);

		lv_steps = (ListView) findViewById(R.id.lv_steps);
		lv_steps.setOnItemClickListener(this);

		mapView = (MapView) findViewById(R.id.mapView);
		/**
		 * 获取地图控制器
		 */
		mMapController = mapView.getController();
		mMapController.enableClick(true);
		mMapController.setZoom(LocateNavVersion.DEFAULT_ZOOM_LEVAL);
	}

	private void backPressed() {
		finish();
	}

	private void initPop() {
		popOverlay = new MyPopupOverlay(mapView, new PopOverlayClicker());
		myOverlay = new MyOverlay( res.getDrawable(R.drawable.icon_gcoding), mapView);
		mapView.getOverlays().add(myOverlay);
	}
	public void updateRoutePlan(RouteType type, Object route) {
		switch (type) {
		case Driver:
		case Walk: {
			routeOverlay = new MyRouteOverlay(
					LocateRouteVersion.this, mapView);

			this.route = ((MKRoutePlan) (route)).getRoute(0);

			// 此处仅展示一个方案作为示例
			routeOverlay.setData(this.route);
			// 清除其他图层
//			mapView.getOverlays().clear();
			// 添加路线图层
			mapView.getOverlays().add(routeOverlay);
			// 执行刷新使生效
			mapView.refresh();
			
			start = this.route.getStart();
			end = this.route.getEnd();
			
			mapView.postDelayed(mZoomToSpan, ZOOM_DELAY);
		}
			break;
		case Transite: {
			transiteOverlay = new MyTransiteOverlay(
					LocateRouteVersion.this, mapView);
			transite = (MKTransitRoutePlan) (route);
			// 此处仅展示一个方案作为示例
			transiteOverlay.setData(transite);
			// 清除其他图层
//			mapView.getOverlays().clear();
			// 添加路线图层
			mapView.getOverlays().add(transiteOverlay);
			
			start = this.transite.getStart();
			end = this.transite.getEnd();
			mapView.postDelayed(mZoomToSpan, ZOOM_DELAY);
		}
			break;
		}
	}
	
	/**
	 * 绽放至两点之间
	 * 
	 * @param p1
	 * @param p2
	 */
	private void zoomToBothPoint(GeoPoint p1, GeoPoint p2) {
		if (mapView == null)
			return;

		RouteOverlay mOverlay = new RouteOverlay(this, mapView);

		mOverlay.addItem(new OverlayItem(p1, "", ""));
		mOverlay.addItem(new OverlayItem(p2, "", ""));

		System.out.println("span =" + mOverlay.getLatSpanE6() + "   x  "
				+ mOverlay.getLonSpanE6());
		mapView.getController().zoomToSpan(mOverlay.getLatSpanE6(),
				mOverlay.getLonSpanE6());

		GeoPoint center = LocateNavVersion.getBothPointCenter(p1, p2);
		// 移动地图到两点之间
		mapView.getController().animateTo(center);
	}

	private void fillRoutes(MKTransitRoutePlan plan) {
		all_sh = new ArrayList<StepHold>();
		int c = plan.getNumRoute();
		int tc = plan.getNumLines();
		int tc_ = 0;
		for (int i = 0; i < c; i++) {
			StepHold sh = new StepHold();
			MKRoute mRoute = plan.getRoute(i);
			sh.type = mRoute.getRouteType();
			sh.content = mRoute.getTip();
			sh.point = mRoute.getStart();
			all_sh.add(sh);
//			all_sh.addAll( getStepHold(mRoute) );
			
			if(tc_ < tc){
				StepHold  tmpSh = new StepHold();
				
				MKLine line = plan.getLine(tc_++);
//				tmpSh.type = line.getType() ;
				tmpSh.type = MKRoute.ROUTE_TYPE_BUS_LINE;
				tmpSh.content = line.getTip();
				tmpSh.point = line.getPoints().get(0);
				all_sh.add(tmpSh);
			}
			
		}
		lv_steps.setAdapter(new StepHoldAdapter (this, all_sh));
	}
	

	private void fillRoutes(MKRoute plan) {
		all_steps = new ArrayList<MKStep>();
		int c = plan.getNumSteps();
		for (int i = 0; i < c; i++) {
			all_steps.add(plan.getStep(i));
		}
		lv_steps.setAdapter(new StepsAdapter(this, all_steps, plan.getRouteType()));
	}

	class MyTransiteOverlay extends TransitOverlay {

		public MyTransiteOverlay(Activity arg0, MapView arg1) {
			super(arg0, arg1);
			Resources res = getResources();
			setEnMarker(res.getDrawable(R.drawable.icon_routeend));
			setStMarker(res.getDrawable(R.drawable.icon_routestart));
		}
		@Override
		protected boolean onTap(int arg0) {
			OverlayItem item = getItem(arg0);
			
			if(transite.getEnd().equals(item.getPoint()))
				showPop("到达终点", item.getPoint(), 84);
			else
				showPop(item.getTitle() , item.getPoint(), 84);
			
			
//			popOverlay.showPop(item.getTitle(), item.getPoint(), 84);
//			
//			myOverlay.removeAll();
//			myOverlay.addItem(item);
//			mapView.refresh();
			
			return true;
		}
		@Override
		public boolean onTap(GeoPoint arg0, MapView arg1) {
			popOverlay.hidePop();
			return super.onTap(arg0, arg1);
		}
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
			
			if(route.getStart().equals(item.getPoint()))
				showPop(route.getStep(0).getContent(), item.getPoint(), 84);
			else if(route.getEnd().equals(item.getPoint()))
				showPop(route.getStep(route.getNumSteps()-1).getContent(), item.getPoint(), 84);
			else
				showPop(item.getTitle() , item.getPoint(), 84);
//			
//			
//			myOverlay.removeAll();
//			myOverlay.addItem(item);
//			mMapController.animateTo(item.getPoint());
//			mapView.refresh();
//			MKStep mStep = route.getStep(arg0);
//			showPop(mStep.getContent(), mStep.getPoint(), 84);
			return true;
		}
		
		public void fire(GeoPoint point){
			int c = getAllItem().size();
			for(int i=0;i<c;i++)
				if(getItem(i).getPoint().equals(point))
				{
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

	@Override
	protected void onPause() {
		mapView.onPause();
		MobclickAgent.onPageEnd("business_location_activity");
		super.onPause();
	}

	@Override
	protected void onResume() {
		mapView.onResume();
		super.onResume();
		MobclickAgent.onPageStart("business_location_activity");
	}
	
	/**
	 * zoomtospan 只有在地图绘制完成后，才可以调用。<br >
	 * 百度地图，真特么是坨shit。
	 */
	private Runnable mZoomToSpan = new Runnable() {
		
		@Override
		public void run() {
			if(start !=null && end !=null)
				zoomToBothPoint(start, end);
		}
	};
	@Override
	protected void onDestroy() {
		if(popOverlay!=null){
			popOverlay.hidePop();
		}
		mapView.destroy();
		//该组件，不再 destroy mapManager。 因为，这个狗日的百度地图api
		//是他娘的 老版本。如果存在两个 mapview ，就不能随意释放 mapManager
//		destoryMapManager();
		super.onDestroy();
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
//			OverlayItem item = getItem(arg0);
//			GeoPoint point = item.getPoint();
//			tipView.setText(item.getTitle());
//			popOverlay.showPopup(tipView, point, 84);
//			mMapController.animateTo(point);
			return false;
		}
	}

	class PopOverlayClicker implements PopupClickListener {

		@Override
		public void onClickedPopup(int arg0) {
		}
	}
	
	class TransiteAdapter extends ObjectAdapter<MKRoute>{

		public TransiteAdapter(Activity activity, List<MKRoute> data_) {
			super(activity, data_);
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			MKRoute mRoute = getItem(position);
			String content = mRoute.getTip();
			return getItemView(content, mRoute.getRouteType(), convertView, parent);
		}
	}
	
	class StepHoldAdapter extends ObjectAdapter<StepHold>{

		public StepHoldAdapter(Activity activity, List<StepHold> data_) {
			super(activity, data_);
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			StepHold mRoute = getItem(position);
			String content = mRoute.content;
			return getItemView(content, mRoute.type, convertView, parent);
		}
	}
	
	private View getItemView(String content, int type, View conView, ViewGroup parent){
		TextView tmpView;
		if(conView != null){
			tmpView = (TextView)(conView.getTag());
		}else{
			conView = inflater.inflate(R.layout.common_route_step_list_item, parent, false);
			
			tmpView =(TextView)conView.findViewById(R.id.tv_step);
			conView.setTag(tmpView);
		}
		Drawable d = null;
		switch(type){
		case MKRoute.ROUTE_TYPE_BUS_LINE:
			d = res.getDrawable(R.drawable.route_icon_bus);
			break;
		case MKRoute.ROUTE_TYPE_DRIVING:
			d = res.getDrawable(R.drawable.route_icon_drive);
			break;
		case MKRoute.ROUTE_TYPE_WALKING:
			d = res.getDrawable(R.drawable.route_icon_walking);
			break;
		}
		System.out.println("type  "+ type);
		tmpView.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);
		tmpView.setText(content);
		return conView;
	}
	
	
	class StepsAdapter extends ObjectAdapter<MKStep>{
		private int type;
		public StepsAdapter(Activity activity, List<MKStep> data_, int type) {
			super(activity, data_);
			this.type = type;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			MKStep mStep = getItem(position);
			String content = mStep.getContent();
			return getItemView(content, type, convertView,parent);
		}
		
	}
	
	class MyPopupOverlay extends PopupOverlay{
		private ViewGroup popupView;
		private TextView tvInfo;
		public MyPopupOverlay(MapView arg0, PopupClickListener arg1) {
			super(arg0, arg1);
		}
		private void init(){
			popupView = (ViewGroup)getLayoutInflater().inflate(R.layout.common_popup_layout, mapView, false);
			tvInfo = (TextView)popupView.findViewById(R.id.tv_pop_title);
		}
		public void showPop(String text, GeoPoint point, int offset){
			if(popupView!=null && popupView.getParent()!=null){
					System.out.println("popupview != null \n 移除");
					((ViewGroup)popupView.getParent()).removeView(popupView);
			}
			
			init();
		
			tvInfo.setText(text);
			
//			popupView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
//			popupView.layout(0, 0, popupView.getMeasuredWidth(), popupView.getMeasuredHeight());
//			popupView.buildDrawingCache(true);
//	        Bitmap bitmap = popupView.getDrawingCache();
//			
////			int sizeMpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.AT_MOST);
////			popupView.measure(sizeMpec, sizeMpec);
////			System.out.println(popupView.getParent());
//	        mapView.requestLayout();
			showPopup(popupView,point, offset);
//			showPopup(popupView, point, offset);
		}
		
	}
	/**
	 * 显示 pop
	 * @param text
	 * @param point
	 * @param offset
	 */
	private void showPop(String text, GeoPoint point, int offset){
		
		//显示pop
		popOverlay.showPop(text , point, offset);
		
		
		myOverlay.removeAll();
		OverlayItem oi = new OverlayItem(point, text, text);
		myOverlay.addItem(oi);
		mMapController.animateTo(point);
		mapView.refresh();
	}
	
	class StepHold{
		String content;
		int type;
		GeoPoint point;
	}
	
	
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		GeoPoint point = null;
		if(transiteOverlay!=null){
			StepHold hold = (StepHold)lv_steps.getAdapter().getItem(position);
			point = hold.point;
			showPop(hold.content , point, 84);
		}else{
			MKStep mStep = (MKStep)lv_steps.getAdapter().getItem(position);;
			point = mStep.getPoint();
			showPop(mStep.getContent(), point, 84);
//			routeOverlay.fire(point);
		}
	}
}
