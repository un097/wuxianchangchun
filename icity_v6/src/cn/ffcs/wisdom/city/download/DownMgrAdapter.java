package cn.ffcs.wisdom.city.download;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class DownMgrAdapter extends PagerAdapter {
	
	private List<View> mListView = new ArrayList<View>();

	public DownMgrAdapter(List<View> listView) {
		this.mListView = listView;
	}

	@Override
	public int getCount() {
		return mListView.size();
	}
	
	@Override
	public Object instantiateItem(View view, int position) {
		View contentView = mListView.get(position);
		((ViewPager) view).addView(mListView.get(position), 0);
		return contentView;
	}

	@Override
	public void destroyItem(View view, int position, Object obj) {
		((ViewPager) view).removeView(mListView.get(position));
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == (arg1);
	}
	
	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;// 调用notifyDataSetChanged()才可以更新
	}

}
