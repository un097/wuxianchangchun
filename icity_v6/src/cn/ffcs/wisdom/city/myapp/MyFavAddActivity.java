package cn.ffcs.wisdom.city.myapp;

import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import cn.ffcs.ui.tools.TopUtil;
import cn.ffcs.wisdom.base.BaseFragmentActicity;
import cn.ffcs.wisdom.city.myapp.adapter.MyFavPagerAdapter;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.tools.AppHelper;
/**
 * <p>Title: 我的收藏添加页面    </p>
 * <p>Description: 
 * 包括：
 * 1. 最近访问
 * 2. 分类查找
 * 两种类型的tabUI
 * </p>
 * <p>@author: Eric.wsd                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-8-13             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class MyFavAddActivity extends BaseFragmentActicity {
	private TextView mTopRecent;
	private TextView mTopCategory;
	private ViewPager mPager;
	private MyFavPagerAdapter mAdapter;
	protected View mReturn; // 返回
	
	
	
	
	@Override
	protected void initComponents() {
		mTopRecent = (TextView) findViewById(R.id.myfav_recent_app);
		mTopCategory = (TextView) findViewById(R.id.myfav_category_app);
		mPager = (ViewPager) findViewById(R.id.myfav_viewpager);
		mReturn = findViewById(R.id.btn_return);
	}

	@Override
	protected void initData() {
		mPager.setOnPageChangeListener(new OnPagerChangeListener());
		mTopRecent.setOnClickListener(new OnRecentClickLinstener());
		mTopCategory.setOnClickListener(new OnCategoryClickLinstener());
		mReturn.setOnClickListener(new OnReturnClickListener());

		initViewPager();

		mPager.setCurrentItem(0);
		mTopRecent.setSelected(true);
		mTopCategory.setSelected(false);
		
		int width = AppHelper.getScreenWidth(mContext);
		LayoutParams params = new LayoutParams(width/2, LayoutParams.MATCH_PARENT);
		mTopRecent.setLayoutParams(params);
		mTopCategory.setLayoutParams(params);	
		
		TopUtil.updateTitle(this, R.id.top_title, R.string.myapp_title);
	}
	
	private void initViewPager() {

		if (mAdapter == null) {
			mAdapter = new MyFavPagerAdapter(getSupportFragmentManager());
			mPager.setAdapter(mAdapter);
		} else {
			mAdapter.notifyDataSetChanged();
		}

	}
	
	class OnReturnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			onBackPressed();
		}
	}

	class OnPagerChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int arg0) {
			switch (arg0) {
			case 0: // 最近访问
				mTopRecent.setSelected(true);
				mTopCategory.setSelected(false);
				break;
			case 1: // 分类查找
				mTopRecent.setSelected(false);
				mTopCategory.setSelected(true);
				break;
			default:
				break;
			}
		}
	}

	class OnRecentClickLinstener implements OnClickListener {

		@Override
		public void onClick(View v) {
			mPager.setCurrentItem(0);
		}
	}

	class OnCategoryClickLinstener implements OnClickListener {

		@Override
		public void onClick(View v) {
			mPager.setCurrentItem(1);
		}
	}
	
	@Override
	public void finish() {
		setResult(RESULT_OK);
		super.finish();
	}


	@Override
	protected int getMainContentViewId() {
		return R.layout.act_myfav_homepage;
	}

	@Override
	protected Class<?> getResouceClass() {
		return R.class;
	}

}
