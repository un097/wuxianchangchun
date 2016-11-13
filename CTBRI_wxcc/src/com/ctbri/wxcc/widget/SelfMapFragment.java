//package com.ctbri.wxcc.widget;
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.view.View;
//
//import com.baidu.mapapi.map.BaiduMap;
//import com.baidu.mapapi.map.SupportMapFragment;
//import com.wookii.tools.comm.LogS;
//
//public class SelfMapFragment extends SupportMapFragment {
//
//	private MapInitListener mInitListener;
//	
//	@Override
//	public void onAttach(Activity activity) {
//		super.onAttach(activity);
//		if(activity instanceof MapInitListener)
//			this.mInitListener = (MapInitListener)activity;
//		else if(getParentFragment()!=null && getParentFragment() instanceof MapInitListener)
//			this.mInitListener = (MapInitListener) getParentFragment();
//
//	}
//	@Override
//	public void onActivityCreated(Bundle arg0) {
//		super.onActivityCreated(arg0);
//		if(this.mInitListener!=null)
//			mInitListener.init(getBaiduMap());
//		
//	}
//	@Override
//	public void onViewCreated(View arg0, Bundle arg1) {
//		super.onViewCreated(arg0, arg1);
//		LogS.i("mapVIew", "baidumap "+getBaiduMap());
//	}
//	public static interface MapInitListener{
//		void init(BaiduMap map);
//	}
//}
