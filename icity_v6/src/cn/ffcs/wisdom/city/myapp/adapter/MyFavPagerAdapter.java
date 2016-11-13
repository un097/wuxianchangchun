package cn.ffcs.wisdom.city.myapp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import cn.ffcs.wisdom.city.myapp.fragment.CategoryFragment;
import cn.ffcs.wisdom.city.myapp.fragment.TrackInfoFragment;

/**
 * <p>Title: 我的收藏页面Viewpager适配器        </p>
 * <p>Description: 
 *  我的收藏页面Viewpager适配器 
 *   * </p>
 * <p>@author: Eric.wsd                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-6-13             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class MyFavPagerAdapter extends FragmentPagerAdapter {
	private static final int PAGE_SIZE = 2; // 页数

	public MyFavPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int pos) {
		switch (pos) {
		case 0:
			return TrackInfoFragment.newInstance();
		case 1:
			return CategoryFragment.newInstance();
		default:
			break;
		}

		return null;
	}

	@Override
	public int getCount() {
		return PAGE_SIZE;
	}
}
