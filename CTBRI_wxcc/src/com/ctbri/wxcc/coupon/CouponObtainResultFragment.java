package com.ctbri.wxcc.coupon;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseFragment;
import com.ctbri.wxcc.entity.CouponDetailBean.CouponDetail;

public class CouponObtainResultFragment extends BaseFragment{
	public static final String KEY_BEAN = "coupon_bean"	;
	public static final CouponObtainResultFragment newInstance(CouponDetail detail){
		Bundle args = new Bundle();
		args.putSerializable(KEY_BEAN, detail);
		CouponObtainResultFragment cof = new CouponObtainResultFragment();
		
		cof.setArguments(args);
		return cof;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bean = (CouponDetail) getSerialzeable(KEY_BEAN);
	}
	
	private CouponDetail bean;
	private TextView tv_result, tv_valid, tv_code, tv_title;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View  v = inflater.inflate(R.layout.coupon_obtain_result_layout, container, false);
		tv_title = (TextView)v.findViewById(R.id.tv_title);
		tv_result = (TextView)v.findViewById(R.id.tv_result);
		tv_valid =(TextView) v.findViewById(R.id.tv_valid);
		tv_code =(TextView)v.findViewById(R.id.tv_valid_code);
		v.findViewById(R.id.btn_to_cupon_list).setOnClickListener(new ObtainListener());
		v.findViewById(R.id.btn_to_coupon_detail).setOnClickListener(new ObtainListener());
		
		((TextView)v.findViewById(R.id.action_bar_title)).setText(R.string.title_obtain_coupon);
		v.findViewById(R.id.action_bar_left_btn).setOnClickListener(new FinishClickListener());
		
//		new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				toCouponList();
//				FinishClickL
//			}
//		}
//		
		fillData();
		return v;
	}
	private void fillData(){
		if(bean==null)return;
		tv_title.setText(bean.getTitle());
		tv_result.setText(R.string.msg_coupon_obtain_succ	);
		tv_valid.setText(getString(R.string.tip_validate, bean.getValidity()));
		tv_code.setText(getString(R.string.tip_validate_code, bean.getCode()));
	}
	
	
	
	class ObtainListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			int v_id = v.getId();
			if(v_id==R.id.btn_to_cupon_list){
				toCouponList();
			}else if(v_id==R.id.btn_to_coupon_detail){
				Intent toDetail = new Intent(activity, CouponDetailActivity.class);
				toDetail.putExtra(CouponDetailFragment.KEY_COUPON_ID, bean.getCoupon_id());
				toDetail.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(toDetail);
			}
		}
		
	}
	/**
	 * 跳转到 优惠券 列表
	 */
	private void toCouponList(){
		Intent toList = new Intent(activity, CouponMainActivity.class);
		toList.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(toList);
	}



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
