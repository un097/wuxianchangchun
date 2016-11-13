package com.ctbri.wxcc.coupon;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.LocationData;
import com.ctbri.comm.util.DialogUtils;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseActivity;

public class MoreAddressActivity extends BaseActivity {
	
	
	private LocationClient mLocClient;
	private LocationData locData;
	private MyLocationListenerImpl myLocListener;
	
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		setContentView(R.layout.coupon_more_address);
		
		TextView tvTitle = (TextView	) findViewById(R.id.action_bar_title);
		tvTitle.setText(R.string.title_merchant);
		findViewById(R.id.action_bar_left_btn).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		DialogUtils.showLoading(getSupportFragmentManager());
		startMyLocation();
	}
	
	public LocationData getMyLocation(){
		return locData;
	} 

	private void startMyLocation() {
		// 定位初始化
		mLocClient = new LocationClient(this);
		myLocListener = new MyLocationListenerImpl();
	
		mLocClient.registerLocationListener(myLocListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
//		option.setScanSpan(LocationClientOption.MIN_SCAN_SPAN );
		option.setAddrType("all");
		option.setProdName("com.ctri.wxcc");
		option.setTimeOut(30000);
		mLocClient.setLocOption(option);
		mLocClient.start();
		
		
		
	}
	
	private void init(){
		getSupportFragmentManager().beginTransaction().replace(R.id.content, MoreAddFragment.newInstance(getString("coupon_id"))).commit();
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

			locData = new LocationData();
			locData.latitude = location.getLatitude();
			locData.longitude = location.getLongitude();
			// 如果不显示定位精度圈，将accuracy赋值为0即可
			locData.accuracy = 200;// location.getRadius();
			// 此处可以设置 locData的方向信息, 如果定位 SDK 未返回方向信息，用户可以自己实现罗盘功能添加方向信息。
			locData.direction = location.getDerect();
			
			mLocClient.stop();
			System.out.println("定位成功" + this);
			
			
			DialogUtils.hideLoading(getSupportFragmentManager());
			init();
		}

//		@Override
//		public void onReceivePoi(BDLocation arg0) {
//
//		}

	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mLocClient!=null){
			if(mLocClient.isStarted())
				mLocClient.stop();
			mLocClient = null;
		}
	}
}
