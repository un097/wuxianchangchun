package cn.ffcs.changchuntv.activity.home.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.ffcs.changchuntv.R;
import cn.ffcs.changchuntv.activity.home.adapter.BannerAdapter;
import cn.ffcs.changchuntv.entity.AdvertisingEntity;
import cn.ffcs.changchuntv.entity.AdvertisingEntity.Advertising;
import cn.ffcs.wisdom.tools.AppHelper;

public class AdvertisingHeaderView extends FrameLayout {

	private Activity mActivity;

	private ViewPager bannerViewPager;
	private LinearLayout bannerIndicatorLayout;
	private TextView banner_title;
	private ImageView[] bannerIndicatorViews;
	private BannerAdapter viewPagerAdapter;
	private List<Advertising> advs = new ArrayList<Advertising>();
	private int mDelayTime = 3000;// 3秒更新一次
	private int selectIndex = 0;// 选中项

	public AdvertisingHeaderView(Activity activity) {
		super(activity);
	}

	public AdvertisingHeaderView(Activity activity, int mChnl_id) {
		super(activity);
		this.mActivity = activity;
		init(activity);
	}

	private void init(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View bannerView = inflater.inflate(R.layout.banner, null);
		bannerViewPager = (ViewPager) bannerView.findViewById(R.id.bannerviewpager);
		bannerIndicatorLayout = (LinearLayout) bannerView.findViewById(R.id.banner_indicator_group);
		banner_title = (TextView) bannerView.findViewById(R.id.banner_title);
		int width = AppHelper.getScreenWidth(context);
		LayoutParams params = new LayoutParams(width, width/2);
		addView(bannerView, params);
	}

//	public void refresh() {
//		refreshAdvertising();
//	}

	public void initBanner(AdvertisingEntity adEntity) {
		int size = 1;
		bannerIndicatorLayout.removeAllViews();
		if (adEntity != null && adEntity.banner_advs != null) {
			advs = adEntity.banner_advs;
			if (advs != null && advs.size() > 0) {
				size = advs.size();
			}
		}
		bannerIndicatorViews = new ImageView[size];
		for (int i = 0; i < size; i++) {
			ImageView bannerIndicator = new ImageView(getContext());
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.setMargins(0, 0, 10, 0);
			bannerIndicator.setLayoutParams(params);
			bannerIndicatorViews[i] = bannerIndicator;
			if (i == 0) {
				bannerIndicatorViews[i].setBackgroundResource(R.drawable.banner_indicator_focused);
			} else {
				bannerIndicatorViews[i]
						.setBackgroundResource(R.drawable.banner_indicator_unfocused);
			}
			bannerIndicatorLayout.addView(bannerIndicatorViews[i]);
		}
		viewPagerAdapter = new BannerAdapter(mActivity, advs);
		bannerViewPager.setAdapter(viewPagerAdapter);
		viewPagerAdapter.notifyDataSetChanged();
		bannerViewPager.setOnPageChangeListener(new BannerPageListener());
		banner_title.setText(advs.get(selectIndex).title);
		if (size > 1) {
			uiHandler.postDelayed(mRunnable, mDelayTime);
		}
	}

	private Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			uiHandler.sendEmptyMessage(1);
			selectIndex++;
		}
	};

	private Handler uiHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (selectIndex == advs.size()) {
				selectIndex = 0;
			}
			bannerViewPager.setCurrentItem(selectIndex);
			banner_title.setText(advs.get(selectIndex).title);
		}

	};

	class BannerPageListener implements OnPageChangeListener {

		@Override
		public void onPageSelected(int index) {
			int newPosition = index;
			if (newPosition >= advs.size()) {
				newPosition = index % advs.size();
			}
			selectIndex = newPosition;
			banner_title.setText(advs.get(selectIndex).title);
			uiHandler.removeCallbacks(mRunnable);
			uiHandler.postDelayed(mRunnable, mDelayTime);
			bannerIndicatorViews[newPosition]
					.setBackgroundResource(R.drawable.banner_indicator_focused);
			for (int i = 0; i < bannerIndicatorViews.length; i++) {
				if (newPosition != i) {
					bannerIndicatorViews[i]
							.setBackgroundResource(R.drawable.banner_indicator_unfocused);
				}
			}
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}
	}

}
