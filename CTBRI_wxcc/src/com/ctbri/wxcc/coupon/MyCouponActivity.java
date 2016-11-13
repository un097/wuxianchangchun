package com.ctbri.wxcc.coupon;

import android.os.Bundle;
import android.support.v4.content.CursorLoader;

import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseActivity;

public class MyCouponActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		getSupportFragmentManager()
				.beginTransaction()
				.replace(
						android.R.id.content,
						CouponListFragment.newInstance("1", true,
								getString(R.string.title_my_coupon)),
						"my_coupon_list").commit();
	}
	@Override
	protected boolean checkLogin() {
		return true;
	}
}
