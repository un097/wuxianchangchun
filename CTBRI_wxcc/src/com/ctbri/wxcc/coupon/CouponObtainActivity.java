package com.ctbri.wxcc.coupon;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.ctbri.wxcc.community.BaseActivity;
import com.ctbri.wxcc.entity.CouponDetailBean.CouponDetail;

public class CouponObtainActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getSupportFragmentManager().beginTransaction().replace(android.R.id.content , getDetailFragment(), "coupon_obtain").commit();
	}
	private Fragment getDetailFragment(){
		
		CouponDetail bean = (CouponDetail)getSerializeable(CouponObtainFragment.KEY_BEAN);
		return CouponObtainFragment.newInstance(bean);
	}
}
