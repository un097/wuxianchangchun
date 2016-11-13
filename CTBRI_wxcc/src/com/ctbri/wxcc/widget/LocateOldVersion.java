package com.ctbri.wxcc.widget;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseActivity;
import com.ctbri.wxcc.entity.CommonPoint;
import com.umeng.analytics.MobclickAgent;
import com.wookii.tools.comm.LogS;

public class LocateOldVersion extends BaseActivity {

	/**
	 * 2015-03-12 添加默认的中心坐标为长春市 人民广场
	 */
	private GeoPoint default_center = new GeoPoint(43892680, 125331294);
	
	public static final String KEY_POINTS = "points";
	public static final float DEFAULT_ZOOM_LEVAL = 13;
	private ArrayList<CommonPoint> points;
	/**
	 * 无法在   Application 中创建 MapManager 对象的实例，频繁创建 BMapManager 又比较熬时，所以把该对象，存入 弱引用中。
	 */
	private static  WeakReference<BMapManager> managerRef;
	private MapView mapView = null;
	private MapController mMapController = null;
	private PopupOverlay popOverlay;
	public BMapManager mapManager = null;
	private View popupView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initEngineManager(getApplicationContext());
		
		setContentView(R.layout.locate_old_version_layout);

		initWidget();
		initData();
		initPop();
		addOverlay();
	}
	public void initEngineManager(Context context) {
		if(managerRef!=null && managerRef.get()!=null)
			mapManager = managerRef.get();
		else{
			LogS.i(getClass().getName(), "managerRef  ==null"+(managerRef==null) + "  mapManager="+ managerRef);
            mapManager = new BMapManager(context);
            managerRef = new WeakReference<BMapManager>(mapManager);
            
            if (!mapManager.init(
            		null)) {
                Toast.makeText(getApplicationContext(), 
                        "BMapManager  初始化错误!", Toast.LENGTH_LONG).show();
            }
            mapManager.start();
        }
	}
	
	private void initWidget() {
		// 设置标题
		((TextView) findViewById(R.id.action_bar_title))
				.setText(R.string.title_locate);
		// 设置返回按钮
		findViewById(R.id.action_bar_left_btn).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						finish();
					}
				});
		mapView = (MapView) findViewById(R.id.mapView);
		/**
		 * 获取地图控制器
		 */
		mMapController = mapView.getController();
		mMapController.enableClick(true);
		mMapController.setZoom(DEFAULT_ZOOM_LEVAL);
	}

	private void initPop() {
		popOverlay = new PopupOverlay(mapView, new PopOverlayClicker());
	}

	private void addOverlay() {

		if (points != null) {

			Resources res = getResources();
			Drawable drawable = res.getDrawable(R.drawable.icon_gcoding);
			MyOverlay myLocOverlay = new MyOverlay(drawable, mapView);

			int count = points.size();
			
			for (int i = 0; i < count; i++) {
				CommonPoint p = points.get(i);
				GeoPoint cent = CommonPoint.parseGeoPoint(p);
				/**  2015-03-12 注释掉
				if(setCenter)
				{
					mMapController.setCenter(cent);
					setCenter = false;
				}
				**/
				OverlayItem business_location = new OverlayItem(cent, p.getTitle(), "");
				myLocOverlay.addItem(business_location);
			}
			
			/**
			 * 2015-03-12 修改为默认的中心
			 */
			mMapController.setCenter(default_center);
			
			mapView.getOverlays().add(myLocOverlay);
			mapView.refresh();
		}
	}

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
		points = (ArrayList<CommonPoint>) it.getSerializableExtra(KEY_POINTS);
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

	@Override
	protected void onDestroy() {
		mapView.destroy();
		destoryMapManager();
		super.onDestroy();
	}
	private void destoryMapManager(){
		if(mapManager!=null){
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

	private TextView getTipView(String title) {
		
		popupView = getLayoutInflater().inflate(R.layout.common_popup_layout, null);
		
		TextView tvInfo = (TextView)popupView.findViewById(R.id.tv_pop_title);
//		tvInfo.setText(title);
//		tvInfo.setTextColor(Color.WHITE);
//		tvInfo.setBackgroundColor(getResources().getColor(
//				R.color.black_translucent));

		return tvInfo;
	}

	class MyOverlay extends ItemizedOverlay<OverlayItem> {
		private TextView tipView;

		public MyOverlay(Drawable arg0, MapView arg1) {
			super(arg0, arg1);
			tipView = getTipView("");
		}

		@Override
		public boolean onTap(GeoPoint arg0, MapView arg1) {
			popOverlay.hidePop();
			return super.onTap(arg0, arg1);
		}

		@Override
		protected boolean onTap(int arg0) {
			OverlayItem item = getItem(arg0);
			GeoPoint point = item.getPoint();
			tipView.setText(item.getTitle());
			popOverlay.showPopup(popupView, point, 84);
			mMapController.animateTo(point);
			return true;
		}

	}
	
	
	
	class PopOverlayClicker implements PopupClickListener {

		@Override
		public void onClickedPopup(int arg0) {
			
		}

	}

}
