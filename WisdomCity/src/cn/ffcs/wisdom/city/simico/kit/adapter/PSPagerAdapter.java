//package cn.ffcs.wisdom.city.simico.kit.adapter;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import android.support.v4.app.FixedFragmentStatePagerAdapter;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.view.ViewGroup;
//
//public class PSPagerAdapter extends FixedFragmentStatePagerAdapter {
//
//	private List<String> titles = new ArrayList<String>();
//
//	public PSPagerAdapter(FragmentManager fm, List<String> titles) {
//		this(fm);
//		this.titles = titles;
//	}
//
//	public PSPagerAdapter(FragmentManager fm) {
//		super(fm);
//	}
//
//	@Override
//	public CharSequence getPageTitle(int position) {
//		if (position < titles.size())
//			return titles.get(position);
//		return "";
//	}
//
//	public void addTitle(String title) {
//		titles.add(title);
//	}
//
//	public void updateTitle(int tabIndex, String title) {
//		if (tabIndex > titles.size() - 1)
//			return;
//		titles.remove(tabIndex);
//		titles.add(tabIndex, title);
//	}
//
//	@Override
//	public Fragment getItem(int arg0) {
//		return null;
//	}
//
//	@Override
//	public int getCount() {
//		return 0;
//	}
//	
//	@Override
//	public void destroyItem(ViewGroup container, int position, Object object) {
//		super.destroyItem(container, position, object);
//	}
//}
