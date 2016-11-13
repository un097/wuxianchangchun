package com.ctbri.wxcc.hotline;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ctbri.wwcc.greenrobot.HotLine;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.widget.NoScrollViewPager;
import com.umeng.analytics.MobclickAgent;

public class HotLineFragment extends Fragment {

	public static final int SEARCH_VIEW = 1;
	public static final int LIST_VIEW = 0;
	private Activity context;
	private NoScrollViewPager pager;
	private SearchViewChangeListener listener;

	@Override
	public void onAttach(Activity activity) {
		this.context = activity;
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View hotline = inflater.inflate(R.layout.fragment_hotline, null);
		pager = (NoScrollViewPager)hotline.findViewById(R.id.hotline_viewpager);
		pager.setAdapter(new MyAdapter(getChildFragmentManager()));
		pager.setPagingEnabled(false);
		return hotline;
	}
	

	class MyAdapter extends FragmentPagerAdapter {
		private HotLineListFragment list;
		private HotLineSearchFragment search;

		public MyAdapter(FragmentManager fm) {
			super(fm);
			list = new HotLineListFragment();
			list.setOnCollectionChangeListener(new CollectionChangeListener() {
				
				@Override
				public void onChange(int status, HotLine hotLine) {
					search.notifyListUpdate();
				}
			});
			search = new HotLineSearchFragment();
			search.setOnCollectionChangeListener(new CollectionChangeListener() {
				
				@Override
				public void onChange(int status, HotLine hotLine) {
					list.changeCollectionOutside(status, hotLine);
				}
			});
		}

		@Override
		public Fragment getItem(int position) {
			Fragment f = null;
			switch (position) {
			case 0:
				f = list;
				break;
			case 1:
				f = search;
				break;

			default:
				break;
			}
			return f;
		}

		@Override
		public CharSequence getPageTitle(int position) {

			return "";
		}

		@Override
		public int getCount() {
			return 2/* indicator.getChildCount() */;
		}
	}
	
	interface SearchViewChangeListener {
		abstract void OnChange(int index);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(context);
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(context);
	}

	public void goSearch() {
		pager.setCurrentItem(1, true);
		if(listener != null) {
			listener.OnChange(SEARCH_VIEW);
		}
	}

	public void setOnSearchViewChangeListener(
			SearchViewChangeListener listener) {
		// TODO Auto-generated method stub
		this.listener = listener;
	}

	public void goList() {
		pager.setCurrentItem(0, true);
		if(listener != null) {
			listener.OnChange(LIST_VIEW);
		}
	}
}
