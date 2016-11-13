package cn.ffcs.surfingscene.road;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import cn.ffcs.surfingscene.R;
import cn.ffcs.surfingscene.common.Key;
import cn.ffcs.surfingscene.datamgr.RoadVideoListMgr;
import cn.ffcs.ui.tools.TopUtil;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.StringUtil;

import com.baidu.mapapi.map.MapView.LayoutParams;

public class RoadMainActivity extends GlobaleyeBaseGroupActivity {
	private FrameLayout container;
	private Button tab_1;
	private Button tab_2;
	private Button tab_3;
	private Button tab_4;
	private String title;
	private String eyeType;
	private boolean isNoRoad = false;
	private LinearLayout tabLayout;
	private static ImageView collectImage;
	private static int[] tabLocation = new int[2];
	private LinearLayout topLayout;
	private static int topHeight;
	private static int tabWidth;
	private static int tabHeight;
	private boolean first = true;

	private List<View> viewList = new ArrayList<View>();
	private View fourthView, oneView, oldView;

	@Override
	protected void initComponents() {
		topLayout = (LinearLayout) findViewById(R.id.top);
		tabLayout = (LinearLayout) findViewById(R.id.tab_layout);
		container = (FrameLayout) findViewById(R.id.container);
		collectImage = (ImageView) findViewById(R.id.collect_anim);
		tab_1 = (Button) findViewById(R.id.tab_1);
		tab_2 = (Button) findViewById(R.id.tab_2);
		tab_3 = (Button) findViewById(R.id.tab_3);
		tab_4 = (Button) findViewById(R.id.tab_4);
		initTab(tab_1, tab_2, tab_3, tab_4);
	}

	/**
	 * 初始化tab
	 * 
	 * @param view
	 */
	private void initTab(View... view) {
		int width = AppHelper.getScreenWidth(mContext) / view.length;
		for (int i = 0; i < view.length; i++) {
			view[i].getLayoutParams().width = width;
			view[i].setOnClickListener(new OnTabClickListener());
		}
	}

	class OnTabClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			if (id == R.id.tab_1) {
				clickTab1();
			} else if (id == R.id.tab_2) {
				clickTab2();
			} else if (id == R.id.tab_3) {
				clickTab3();
			} else if (id == R.id.tab_4) {
				clickTab4();
			}
		}
	}

	/**
	 * 点击第一个按钮
	 */
	private void clickTab1() {
		tab_1.setBackgroundResource(R.drawable.glo_tab_select);
		tab_2.setBackgroundDrawable(null);
		tab_3.setBackgroundDrawable(null);
		tab_4.setBackgroundDrawable(null);
		// container.addView(getLocalActivityManager().startActivity(
		// "tab1",
		// new Intent(mContext, OftenBlockedActivity.class).addFlags(
		// Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtras(getIntent().getExtras()))
		// .getDecorView());
		 refreshView("tab1");
//		refreshView(1);
//		handler.sendEmptyMessage(1);
	}

	/**
	 * 点击按钮2
	 */
	private void clickTab2() {
		tab_1.setBackgroundDrawable(null);
		tab_2.setBackgroundResource(R.drawable.glo_tab_select);
		tab_3.setBackgroundDrawable(null);
		tab_4.setBackgroundDrawable(null);
		// container.addView(getLocalActivityManager().startActivity(
		// "tab2",
		// new Intent(mContext, RoadMapActivity.class)
		// .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
		// .putExtras(getIntent().getExtras())).getDecorView());
		 refreshView("tab2");
//		refreshView(2);
//		handler.sendEmptyMessage(2);
	}

	/**
	 * 点击按钮3
	 */
	private void clickTab3() {
		tab_1.setBackgroundDrawable(null);
		tab_2.setBackgroundDrawable(null);
		tab_3.setBackgroundResource(R.drawable.glo_tab_select);
		tab_4.setBackgroundDrawable(null);
		 refreshView("tab3");
//		refreshView(3);
//		handler.sendEmptyMessage(3);
	}

	/**
	 * 点击按钮4
	 */
	private void clickTab4() {
		tab_1.setBackgroundDrawable(null);
		tab_2.setBackgroundDrawable(null);
		tab_3.setBackgroundDrawable(null);
		tab_4.setBackgroundResource(R.drawable.glo_tab_select);
		// container.addView(getLocalActivityManager().startActivity(
		// "tab4",
		// new Intent(mContext, RoadCollectActivity.class).addFlags(
		// Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtras(getIntent().getExtras()))
		// .getDecorView());
		 refreshView("tab4");
//		refreshView(4);
//		handler.sendEmptyMessage(4);
	}

	private void removeViews() {
		if (oneView != null) {
			container.removeView(oneView);
		}
		if (fourthView != null) {
			container.removeView(fourthView);
		}
	}

	@Override
	protected void initData() {
		// registerBackBroadcast();
		eyeType = getIntent().getStringExtra(Key.K_GLO_TYPE);
		if (eyeType != null) {
			isNoRoad = true;
		}
		getIntent().putExtra(Key.K_IS_NO_ROAD, isNoRoad);
	}

	@Override
	protected int getMainContentViewId() {
		return R.layout.glo_act_road_main;
	}

	@Override
	protected void setCustomTitleTop() {
		title = getIntent().getStringExtra(Key.K_TITLE_NAME);
		if (StringUtil.isEmpty(title)) {
			title = getString(R.string.glo_road_video);
		}

		if (isNoRoad) {
			tabLayout.setVisibility(View.GONE);
		} else {
			ImageView topRight = (ImageView) findViewById(R.id.top_right);
			TopUtil.updateRight(topRight, R.drawable.glo_search);
			topRight.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent i = new Intent(mActivity, SearchResultActivity.class);
					i.putExtras(getIntent().getExtras());
					i.putExtra(Key.K_RETURN_TITLE, title);
					startActivity(i);
				}
			});
		}
		TopUtil.updateTitle(mActivity, R.id.top_title, title);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (first) {
			first = false;
			tab_4.getLocationOnScreen(tabLocation);
			topHeight = topLayout.getHeight();
			tabWidth = tab_4.getWidth();
			tabHeight = tab_4.getHeight();
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					clickTab1();
				}
			}, 50);
		}
	}

	public static void startCollectAnim(int startX, int startY) {
		AnimationSet set = new AnimationSet(true);
		set.setInterpolator(new AccelerateDecelerateInterpolator());
		set.setDuration(500);
		set.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				collectImage.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				collectImage.setVisibility(View.GONE);
			}
		});
		// 平移
		TranslateAnimation translateAnimation = new TranslateAnimation(
				TranslateAnimation.ABSOLUTE, startX,
				TranslateAnimation.ABSOLUTE, tabLocation[0] + tabWidth / 2,
				TranslateAnimation.ABSOLUTE, startY - topHeight,
				TranslateAnimation.ABSOLUTE, tabLocation[1] - topHeight
						- (tabHeight / 3));

		// 渐变
		AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0.3f);

		set.addAnimation(translateAnimation);
		set.addAnimation(alphaAnimation);
		collectImage.startAnimation(set);
	}

	private void refreshView(String id) {
		boolean flag = false; // 判断View是否存在
		View historyView = null;
		Activity historyActivity = getLocalActivityManager().getActivity(id);
		if (historyActivity != null) {
			historyView = historyActivity.getWindow().getDecorView();
		} else {
			historyView = createView(id);
		}
		for (int i = 0, size = viewList.size(); i < size; i++) {
			View v = viewList.get(i);
			if (null != v && !v.equals(historyView)) {
				v.setVisibility(View.GONE);
			} else if (null != v && v.equals(historyView)) {
				v.setVisibility(View.VISIBLE);
				flag = true;
			}
		}

		if (!flag && historyView != null) {
			// 加载Activity
			container.addView(historyView, new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			viewList.add(historyView);
		} else if (historyView != null) {
			// 全部和收藏tab 涉及到动态变化，不缓存view了
			removeViews();
			if ("tab1".equals(id)) {
				oneView = getLocalActivityManager().startActivity(
						"tab1",
						new Intent(mContext, MapGroupListActivity.class)
								.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
								.putExtras(getIntent().getExtras()))
						.getDecorView();
				container.addView(oneView);
			} else if ("tab4".equals(id)) {
				fourthView = getLocalActivityManager().startActivity(
						"tab4",
						new Intent(mContext, RoadCollectActivity.class)
								.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
								.putExtras(getIntent().getExtras()))
						.getDecorView();
				container.addView(fourthView);
			}
		}

	}

	// 新开线程加载点
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			int id = msg.what;
			if (1 == id) {
				refreshView(1);
			}
			if (2 == id) {
				refreshView(2);
			}
			if (3 == id) {
				refreshView(3);
			}
			if (4 == id) {
				refreshView(4);
			}
		};
	};

	private void refreshView(int id) {
		if (oldView != null)
			container.removeView(oldView);
		View historyView = createView("tab" + id);

		// 加载Activity
		container.addView(historyView, new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		oldView = historyView;
	}

	private View createView(String id) {
		View view = null;
		// 全部
		if ("tab1".equals(id)) {
			view = getLocalActivityManager().startActivity(
					"tab1",
					new Intent(mContext, MapGroupListActivity.class).setFlags(
							Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtras(
							getIntent().getExtras())).getDecorView();
			oneView = view;
		} else if ("tab2".equals(id)) {
			view = getLocalActivityManager().startActivity(
					"tab2",
					new Intent(mContext, RoadMapActivity.class).setFlags(
							Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtras(
							getIntent().getExtras())).getDecorView();
		} else if ("tab3".equals(id)) {
			// 常常堵
			view = getLocalActivityManager().startActivity(
					"tab3",
					new Intent(mContext, OftenBlockedActivity.class).setFlags(
							Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtras(
							getIntent().getExtras())).getDecorView();
		} else if ("tab4".equals(id)) {
			view = getLocalActivityManager().startActivity(
					"tab4",
					new Intent(mContext, RoadCollectActivity.class).setFlags(
							Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtras(
							getIntent().getExtras())).getDecorView();
			fourthView = view;
		}

		return view;
	}

	@Override
	public void finish() {
		super.finish();
		RoadVideoListMgr.getInstance().clearData();// 清除缓存
	}

	// private BroadcastReceiver mBroadcastReceiver;
	// private void registerBackBroadcast() {
	// mBroadcastReceiver = new BackBroadcastReceiver();
	// IntentFilter filter = new IntentFilter();
	// filter.addAction("road_back_action");
	// registerReceiver(mBroadcastReceiver, filter);
	// }
	//
	// private class BackBroadcastReceiver extends BroadcastReceiver {
	// @Override
	// public void onReceive(Context context, Intent intent) {
	// int resultCode = intent.getIntExtra("resultCode", 0);
	// if (resultCode == 0) {
	// clickTab1();
	// } else if (resultCode == 1) {
	// clickTab2();
	// } else if (resultCode == 2) {
	// clickTab3();
	// } else if (resultCode == 3) {
	// clickTab4();
	// }
	// }
	// }
}
