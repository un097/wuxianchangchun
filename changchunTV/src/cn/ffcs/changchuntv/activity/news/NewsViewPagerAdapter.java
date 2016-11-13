package cn.ffcs.changchuntv.activity.news;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

public class NewsViewPagerAdapter extends FragmentPagerAdapter {

	private ArrayList<Fragment> mFragmentList;

	public NewsViewPagerAdapter(FragmentManager fm,
			ArrayList<Fragment> fragmentList) {
		super(fm);
		if(this.mFragmentList != null)
			this.mFragmentList.clear();
		this.mFragmentList = fragmentList;
	}

	public Fragment getFragment(int key) {
		return mFragmentList.get(key);
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return ((NewsFragment) mFragmentList.get(position)).getTitle();
	}

	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		return mFragmentList.get(arg0);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mFragmentList.size();
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		super.destroyItem(container, position, object);
	}

	// @Override
	// public Object instantiateItem(ViewGroup container, int position) {
	// NewsFragment f = (NewsFragment) super.instantiateItem(container,
	// position);
	// return f;
	// }

	// @Override
	// public int getItemPosition(Object object) {
	// return NewsViewPagerAdapter.POSITION_NONE;
	// }
}
