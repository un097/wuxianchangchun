package com.ctbri.wxcc.coupon;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseFragment;
import com.ctbri.wxcc.community.BaseFragment.FinishClickListener;
import com.viewpagerindicator.TabPageIndicator;

public class CouponCategoryFragment extends BaseFragment {

	private TabPageIndicator pageIndicator;
	private ViewPager vp_coupon;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.coupon_main_fragment, container,false);
		pageIndicator = (TabPageIndicator)v.findViewById(R.id.tab_page_indicator);
		
		vp_coupon = (ViewPager) v.findViewById(R.id.vp_coupon);
		vp_coupon.setAdapter(new CouponFragmentAdapter(getChildFragmentManager(), getResources().getStringArray(R.array.coupon_columns)));
		pageIndicator.setViewPager(vp_coupon);
		
		((TextView)v.findViewById(R.id.action_bar_title)).setText("优惠活动");
		v.findViewById(R.id.action_bar_left_btn).setOnClickListener(new FinishClickListener());
		return v;
	}
	
	class CouponFragmentAdapter extends FragmentPagerAdapter {
		private String[] types;
		public CouponFragmentAdapter(FragmentManager fm, String[] types) {
			super(fm);
			this.types = types;
		}

		@Override
		public Fragment getItem(int position) {
			return  CouponListFragment.newInstance(position+"");
		}
		@Override
		public CharSequence getPageTitle(int position) {
			return types[position];
		}

		@Override
		public int getCount() {
			return types==null?0:types.length;
		}
		
	}

	@Override
	protected boolean isEnabledAnalytics() {
		return true;
	}
	@Override
	protected String getAnalyticsTitle() {
		return "coupon_category_list";
	}
}
