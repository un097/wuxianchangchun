//package com.simico.creativelocker.kit.activity;
//
//import android.support.v4.view.ViewPager;
//import android.view.View;
//
//import com.hive.android.locker.gem.R;
//import com.simico.creativelocker.kit.adapter.PSPagerAdapter;
//import com.simico.creativelocker.ui.action.ScrollControl;
//
//public class PSPagerFragment extends PFragment{
//
//	protected ViewPager _pager;
//	protected PSPagerAdapter _pagerAdapter;
//
//	public PSPagerFragment() {
//	}
//
//	protected static String makeFragmentName(int from, int to) {
//		return "android:switcher:" + from + ":" + to;
//	}
//
//	public void activateFragment(int index) {
//		if (_pagerAdapter != null && _pager != null) {
//			int count = 0;
//			if (index < (count = _pagerAdapter.getCount())) {
//				for (int k = 0; k < count; k++) {
//					getFragment(k).setActive(k == index ? true : false);
//				}
//			}
//		}
//	}
//
//	public PFragment getActiveFragment() {
//		if (_pagerAdapter != null && _pager != null)
//			return getFragment(_pager.getCurrentItem());
//		return null;
//	}
//
//	public PFragment getFragment(int i) {
//		if (_pagerAdapter != null && _pager == null)
//			return(PFragment) _pagerAdapter.instantiateItem(_pager, i);
//		return null;
//	}
//
//	protected void initPager(View root) {
//		_pager = (ViewPager) root.findViewById(R.id.pager);
//		_pager.setAdapter(_pagerAdapter);
//	}
//
//	public void scrollActiveFragmentToTop() {
//		PFragment pfragment = getActiveFragment();
//		if (pfragment != null
//				&& ScrollControl.class.isAssignableFrom(pfragment.getClass())) {
//			((ScrollControl) pfragment).scrollToTop();
//		}
//	}
//}
