package com.ctbri.wxcc.coupon;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import cn.ffcs.external.share.view.WeiBoSocialShare;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.LocationData;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.ctbri.wxcc.community.BaseActivity;

public class CouponDetailActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getSupportFragmentManager()
				.beginTransaction()
				.replace(android.R.id.content, getDetailFragment(),
						"coupon_detail").commit();
		
		
	}

	private Fragment getDetailFragment() {

		String coupon_id = getString(CouponDetailFragment.KEY_COUPON_ID);
		return CouponDetailFragment.newInstance(coupon_id);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (WeiBoSocialShare.mSsoHandler != null) {
			WeiBoSocialShare.mSsoHandler.authorizeCallBack(requestCode,
					resultCode, data);
		}
	}
	
	
}
