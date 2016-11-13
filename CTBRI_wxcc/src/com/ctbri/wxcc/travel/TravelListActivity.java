package com.ctbri.wxcc.travel;

import android.os.Bundle;

import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseActivity;

public class TravelListActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		setContentView(R.layout.activity_travel_common_list);
		
		int typeId = getIntent().getIntExtra(TravelListFragment.KEY_TYPEID, 0);
		TravelListFragment fragment = TravelListFragment.newInstance(typeId);
		
		getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment).commit();
		
	}
}
