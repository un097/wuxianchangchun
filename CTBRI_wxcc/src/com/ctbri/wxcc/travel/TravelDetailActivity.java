package com.ctbri.wxcc.travel;

import android.content.Intent;
import android.os.Bundle;

import cn.ffcs.external.share.view.WeiBoSocialShare;

import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseActivity;
import com.ctbri.wxcc.community.DefaultWatchManager;
import com.ctbri.wxcc.community.WatcherManager;
import com.ctbri.wxcc.community.WatcherManagerFactory;

public class TravelDetailActivity extends BaseActivity implements WatcherManagerFactory{
	private WatcherManager defaultManager = new DefaultWatchManager();
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_travel_detail);

		getSupportFragmentManager()
				.beginTransaction()
				.replace(
						R.id.frame_container,
						TravelDetailContainerFragment
								.newInstance(getDetailId(), getTypeId() , getString("title"))).commit();
	}

	private String getDetailId() {

		return getString(TravelContentDetail.KEY_DETAIL_ID);
	}
	private int getTypeId() {
		int id = -1;
		if (getIntent() != null && getIntent().getExtras() != null) {
			id = getIntent().getIntExtra(TravelListFragment.KEY_TYPEID ,-1);
		}
		return id;
	}

	@Override
	public WatcherManager getManager() {
		return defaultManager;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(WeiBoSocialShare.mSsoHandler != null){
			WeiBoSocialShare.mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

}
