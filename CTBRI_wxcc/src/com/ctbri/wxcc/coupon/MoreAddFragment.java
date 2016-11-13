package com.ctbri.wxcc.coupon;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.baidu.mapapi.map.LocationData;
import com.ctbri.comm.util._Utils;
import com.ctbri.wxcc.Constants;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.entity.CommonPoint;
import com.ctbri.wxcc.entity.MerchantBean;
import com.ctbri.wxcc.entity.MerchantBean.Merchant;
import com.ctbri.wxcc.travel.CommonList;
import com.ctbri.wxcc.widget.LoadMorePTRListView;
import com.ctbri.wxcc.widget.LocateNavVersion;
import com.ctbri.wxcc.widget.LocateOldVersion;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MoreAddFragment extends CommonList<MerchantBean, Merchant> {
	private String coupon_id;
	private TextView tv_merchants_count;
	private String lat="", lng="";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		coupon_id = getArgs("coupon_id");
	}
	public static MoreAddFragment newInstance(String coupon_id){
		Bundle args = new Bundle();
		args.putString("coupon_id", coupon_id);
		MoreAddFragment fragment = new MoreAddFragment();
		fragment.setArguments(args);
		return fragment;
	} 
	
	
	@Override
	protected int getLayoutResId() {
		return R.layout.coupon_more_address_list;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		LocationData loc = ((MoreAddressActivity)activity).getMyLocation();
		if(loc!=null){
			lat =  Double.toString( loc.latitude);
			lng =  Double.toString( loc.longitude );
		}
		super.onActivityCreated(savedInstanceState);
	}
	@Override
	public void onAttach(Activity activity_) {
		super.onAttach(activity_);
	}
	private LayoutInflater inflater;
	@Override
	protected String getListUrl() {
		//GeoPoint(39915168, 116403875);
		return Constants.METHOD_COUPON_MERCHANT_LIST + "?coupon_id=" + coupon_id+ "&lat="+ lat + "&lnt=" + lng;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v =super.onCreateView(inflater, container, savedInstanceState);
		tv_merchants_count =(TextView) v.findViewById(R.id.tv_address_count);
		return v;
	}
	@Override
	protected Class<MerchantBean> getGsonClass() {
		return MerchantBean.class;
	}

	@Override
	protected List<Merchant> getEntitys(MerchantBean bean) {
		List<Merchant> list = bean.getData().getMerchants();
		tv_merchants_count.setText(getString(R.string.tip_merchants_used, list.size()));
		return list;
	}

	@Override
	protected boolean isEnd(MerchantBean bean) {
		return true;
	}

	@Override
	protected boolean initHeaderDetail(LoadMorePTRListView lv_list,
			LayoutInflater inflater) {
		this.inflater = inflater;
		return false;
	}
	@Override
	protected boolean isInflateActionBar() {
		return false;
	}

	@Override
	protected View getListItemView(int position, View convertView,
			ViewGroup parent, final Merchant data, ImageLoader imgloader,
			DisplayImageOptions dio) {
		Holder hold = null;
		if(convertView==null){
			convertView = inflater.inflate(R.layout.coupon_more_address_item, null);
			hold = new Holder();
			convertView.setTag(hold);
			hold.tv_addr = (TextView) convertView.findViewById(R.id.tv_address);
			hold.tv_distance = (TextView)  convertView.findViewById(R.id.tv_distance);
			hold.tv_name =  (TextView) convertView.findViewById(R.id.tv_name);
			hold.iv_call = (ImageButton) convertView.findViewById(R.id.iv_call);
			
		}else{
			hold = (Holder)convertView.getTag();
		}
		hold.tv_addr.setText(data.getAdd());
	
		hold.tv_distance.setText(_Utils.getDistance( (int)data.getDistance()));
		hold.tv_name.setText(data.getName());
		hold.iv_call.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent call = new Intent(Intent.ACTION_DIAL);
				call.setData(Uri.parse("tel://" + data.getTel()));
				startActivity(call);
			}
		});
		return convertView;
	}
	
	class Holder{
		TextView tv_name;
		TextView tv_addr;
		TextView tv_distance;
		ImageButton iv_call;
	}

	@Override
	protected void onItemClick(AdapterView<?> parent, View view, int position,
			long id, Merchant data) {

		Intent it = new Intent(activity, LocateNavVersion.class);

		ArrayList<CommonPoint> points = new ArrayList<CommonPoint>();
		CommonPoint point = CommonPoint.parseCommonPoint(data.getLocation(), ",", data.getAdd());
		if (point != null)
			points.add(point);
		it.putExtra(LocateOldVersion.KEY_POINTS, points);
		startActivity(it);
		// 统计 购物券店铺位置 点击量
		postClickEvent("E_C_pageName_couponLocationClick");
	}

	@Override
	protected String getAnalyticsTitle() {
		return "coupon_more_address";
	}
	@Override
	protected boolean isEnabledAnalytics() {
		return false;
	}


}
