package com.ctbri.wxcc.coupon;

import android.os.Bundle;

import com.ctbri.wxcc.community.BaseActivity;

public class CouponMainActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		getSupportFragmentManager()
				.beginTransaction()
				.replace(android.R.id.content, new CouponCategoryFragment(),
						"coupon_main").commit();
	}
	@Override
	protected void onLaunch() {
		//统计 优惠券模块的点击量
		postClickEvent("E_C_pageName_couponClick");
	}
}
