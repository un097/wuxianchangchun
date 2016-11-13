//package com.simico.creativelocker.kit.activity;
//
//import android.os.Bundle;
//import android.support.v4.view.ViewPager;
//
//import com.hive.android.locker.gem.R;
//import com.simico.creativelocker.kit.adapter.PSPagerAdapter;
//import com.simico.creativelocker.ui.action.ScrollControl;
//
//public class PSPagerActivity extends PSFragmentActivity {
//	protected ViewPager _pager;
//	protected PSPagerAdapter _pagerAdapter;
//
//	public PSPagerActivity() {
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
//	@Override
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
//	@Override
//	protected void init() {
//		super.init();
//		_pager = (ViewPager) findViewById(R.id.pager);
//		_pager.setAdapter(_pagerAdapter);
//	}
//
//	@Override
//	protected void onSaveInstanceState(Bundle bundle) {
//		try {
//			super.onSaveInstanceState(bundle);
//		} catch (IllegalStateException e) {
//			e.printStackTrace();
//		}
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
