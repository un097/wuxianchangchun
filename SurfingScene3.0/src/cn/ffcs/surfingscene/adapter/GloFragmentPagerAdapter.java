package cn.ffcs.surfingscene.adapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

/**
 * <p>Title: Fragment适配器         </p>
 * <p>Description: 
 * </p>
 * <p>@author: Leo                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-6-24             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class GloFragmentPagerAdapter extends FragmentPagerAdapter {

	private ArrayList<Fragment> mFragmentsList;
	private SparseArray<Fragment> mPositionFragment = new SparseArray<Fragment>();

	public GloFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	public GloFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
		super(fm);
		this.mFragmentsList = fragments;
	}

	@Override
	public Fragment getItem(int position) {
		if (mFragmentsList != null) {
			return mFragmentsList.get(position);
		}
		return null;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		if (mPositionFragment.get(position) == null) {
			mPositionFragment.put(position, getItem(position));
			return super.instantiateItem(container, position);
		}
		return mPositionFragment.get(position);
	}

	@Override
	public int getCount() {
		if (mFragmentsList != null) {
			return mFragmentsList.size();
		}
		return 0;
	}

	@Override
	public int getItemPosition(Object object) {
		return super.getItemPosition(object);
	}

}
