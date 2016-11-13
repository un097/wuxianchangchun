package cn.ffcs.changchuntv.activity.home;

import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.ffcs.changchun_base.activity.BaseFragmentActivity;
import cn.ffcs.changchuntv.R;
import cn.ffcs.changchuntv.activity.home.view.HomeHeaderView;
import cn.ffcs.changchuntv.activity.home.weather.WeatherFragment;
import cn.ffcs.changchuntv.activity.news.NewsActivity;
import cn.ffcs.changchuntv.activity.news.NewsAdapter;
import cn.ffcs.changchuntv.util.FileIOUtils;
import cn.ffcs.wisdom.city.simico.activity.detail.NewsDetailActivity;
import cn.ffcs.wisdom.city.simico.activity.home.adapter.ListBaseSectionedAdapter;
import cn.ffcs.wisdom.city.simico.activity.home.cache.IndexCacheUtils;
import cn.ffcs.wisdom.city.simico.api.model.ApiResponse;
import cn.ffcs.wisdom.city.simico.api.model.News;
import cn.ffcs.wisdom.city.simico.api.model.NewsGroup;
import cn.ffcs.wisdom.city.simico.api.request.BaseRequestListener;
import cn.ffcs.wisdom.city.simico.api.request.GetIndexNewsHome;
import cn.ffcs.wisdom.city.simico.base.Application;
import cn.ffcs.wisdom.city.simico.kit.log.TLog;
import cn.ffcs.wisdom.city.simico.kit.util.TDevice;
import cn.ffcs.wisdom.city.simico.ui.grid.EmptyView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.ctbri.comm.widget.AdViewPager;
import com.ctbri.comm.widget.CouponView;
import com.ctbri.wxcc.audio.AudioChannelWidget;
import com.ctbri.wxcc.audio.AudioStatusBarFragment;
import com.ctbri.wxcc.backplay.server.BackgroundPlayService;
import com.ctbri.wxcc.coupon.CouponMainActivity;
import com.ctbri.wxcc.shake.SingMainActivity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnPullEventListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.State;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;

public class HomeActivity extends BaseFragmentActivity implements OnRefreshListener<ListView>, OnLastItemVisibleListener, cn.ffcs.changchuntv.activity.news.NewsAdapter.NewsDelegate {

	private PullToRefreshListView listView;
	private HomeHeaderView headerView;

	private static final String TAG_INDEX_NEWS = "TAG_INDEX_NEWS";
	private static final String PREFIX_NEWS = "PREFIX_NEWS_";

	private NewsAdapter mAdapter;
	private EmptyView mEmptyView;

	private TextView mTvUpdateCount;// 更新资讯条数提醒
	private ImageView mIvScrollTop;

	private int mPageNumber = 1;
	private int firstPageItem = 0;
	private int mChnl_id = 0;
	private boolean mIsLoadNewsFinished;

	private boolean mFromRefresh;
	private boolean mNeedRefresh = false;

	private boolean hasLoadedCache;
	private boolean hasLoadedWeb;
	private String mFirstVisitTime;// 第一次网络获取时间 去重作用
	private boolean mIsCurrent = false;

	private WeatherFragment weatherFragment;

	private AdViewPager adViewPager;
	private CouponView couponView;
	private AudioChannelWidget audioChannelWidget;
	private AudioStatusBarFragment audioStatusBarFragment;

	private boolean refresh = false;
	private static int status_height = 0;
	private static int titlebar_height = 0;

	View titlebar;
	View AudioStatusBar;

	@Override
	protected int getLayoutId() {
		return R.layout.activity_home;
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		initActionBar();
		initBackgroundPlayService();
		audioChannelWidget = new AudioChannelWidget();
		headerView = new HomeHeaderView(this);
		listView = (PullToRefreshListView) findViewById(R.id.list);
		listView.getRefreshableView().addHeaderView(headerView);
//		listView.getRefreshableView().removeHeaderView(headerView);
		titlebar = findViewById(R.id.titlebar);
		AudioStatusBar = findViewById(R.id.audio_statusbar);
		AudioStatusBar.setVisibility(View.GONE);
		findViewById(R.id.more_news).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				MobclickAgent.onEvent(mContext, "E_C_home_moreNewsClick");
				Intent intent = new Intent(HomeActivity.this,
						NewsActivity.class);
				startActivity(intent);

			}
		});

		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		weatherFragment = new WeatherFragment();
		fragmentTransaction.add(R.id.fl_inner, weatherFragment);
		// adViewPager = new AdViewPager();
		// fragmentTransaction.add(R.id.home_banner, adViewPager);
		couponView = new CouponView();
		fragmentTransaction.add(R.id.home_advertising, couponView);
		// fragmentTransaction.add(R.id.weatherfragment, weatherFragment);
		
		audioStatusBarFragment = new AudioStatusBarFragment();
		fragmentTransaction.add(R.id.audio_statusbar, audioStatusBarFragment);
		fragmentTransaction.add(R.id.home_audio, audioChannelWidget);
		fragmentTransaction.commit();

		findViewById(R.id.more_advertising).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(HomeActivity.this,
								CouponMainActivity.class);
						startActivity(intent);

					}
				});

		// final Handler handler = new Handler(){
		//
		// @Override
		// public void handleMessage(Message msg) {
		// switch (msg.what) {
		// case 1:
		// findViewById(R.id.weatherfragment).setVisibility(View.VISIBLE);
		// sendEmptyMessageDelayed(2, 3000);
		// break;
		// case 2:
		// findViewById(R.id.weatherfragment).setVisibility(View.GONE);
		// break;
		// }
		// super.handleMessage(msg);
		// }
		//
		// };
		// listView.setOnPullEventListener(new OnPullEventListener<ListView>() {
		//
		// @Override
		// public void onPullEvent(final PullToRefreshBase<ListView>
		// refreshListView,
		// State state, Mode direction) {
		// View refreshView = HomeActivity.this.findViewById(R.id.fl_inner);
		// View weatherView = HomeActivity.this.findViewById(R.id.weatherview);
		// if (weatherView == null) {
		// return;
		// }
		// if (state == State.RESET && -refreshListView.getScrollY() <
		// refreshView.getHeight() && -refreshListView.getScrollY() >
		// weatherView.getHeight()) {
		// handler.sendEmptyMessage(1);
		// } else {
		// handler.sendEmptyMessage(2);
		// }
		//
		// }
		//
		// });

		mTvUpdateCount = (TextView) findViewById(R.id.tv_update_count);
		mIvScrollTop = (ImageView) findViewById(R.id.btn_scrolltop);
		mIvScrollTop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				scrollToTop();
			}
		});

		listView.setOnRefreshListener(this);
		listView.setOnLastItemVisibleListener(this);
		mEmptyView = new EmptyView(mActivity);
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int[] heights = new int[5];
		// 1 图
		heights[0] = (displaymetrics.widthPixels - (int) TDevice.dpToPixel(20)) * 3 / 5;
		// 3图
		heights[1] = (displaymetrics.widthPixels - (int) TDevice.dpToPixel(40)) / 4;
		// 2图
		heights[3] = ((displaymetrics.widthPixels - (int) TDevice.dpToPixel(30)) / 2) * 3 / 4;
		mAdapter = new NewsAdapter(this);
		mAdapter.setImgHeights(heights);
		mAdapter.setEmptyView(mEmptyView);
		mAdapter.setState(ListBaseSectionedAdapter.STATE_EMPTY_ITEM);
		getTitleHeightAndStatusHeight();
		listView.setAdapter(mAdapter);
		listView.setRefreshing();

		super.init(savedInstanceState);
	}
	int weight = 0;
	void getTitleHeightAndStatusHeight() {
		// TODO
		// Log.e("sb", "getNewsData       audioChannelWidget  "
		// + audioChannelWidget.getY_Location());
		status_height = getStatusHeight();
		titlebar_height = titlebar.getHeight();
		if (titlebar_height == 0) {
			titlebar_height = 100;
		}
		// titlebar_height = getTitleHeight(status_height);
//		Log.e("sb", "   status_height = " + status_height + "");
		// Log.e("sb", "   titlebar_height = " + titlebar_height
		// + "");
//		Log.e("sb", "   titlebar_height " + titlebar_height);

		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (AudioStatusBar.getVisibility() == View.VISIBLE)
				{
					weight = titlebar_height;
				}else {
					weight = 0;
				}
//				 Log.e("sb", ".........." + (status_height + titlebar_height - audioChannelWidget.getHeight()));
				if ((audioChannelWidget.getY_Location()) - weight >= (status_height
						+ titlebar_height - audioChannelWidget.getHeight())
						 ) {
					// Log.e("sb", "隐藏"); && audioChannelWidget.getY_Location()
					// audioStatusBarFragment
					AudioStatusBar.setVisibility(View.GONE);
				} else {
					// Log.e("sb", "显示");
					AudioStatusBar.setVisibility(View.VISIBLE);
				}
				if (firstVisibleItem == 0)
					firstPageItem = visibleItemCount;
				if (firstVisibleItem >= firstPageItem)
					mIvScrollTop.setVisibility(View.VISIBLE);
				else
					mIvScrollTop.setVisibility(View.GONE);
			}
		});
	}

	/**
	 * 得到标题栏高度
	 * 
	 * @return
	 */
	private int getTitleHeight(int statusBarHeight) {
		int contentTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT)
				.getTop();
		// statusBarHeight是上面所求的状态栏的高度
		int titleBarHeight = contentTop - statusBarHeight;
		return titleBarHeight;
	}

	/**
	 * 得到状态栏高度
	 * 
	 * @return
	 */
	public int getStatusHeight() {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, sbar = 38;// 默认为38，貌似大部分是这样的
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			sbar = getResources().getDimensionPixelSize(x);

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return sbar;
	}

	private void initActionBar() {
		TextView title = (TextView) findViewById(R.id.title);
		title.setText("无线长春");
		// TextView traffic_live = (TextView) findViewById(R.id.top_right);
		// Drawable drawable =
		// getResources().getDrawable(R.drawable.traffic_live);
		// drawable.setBounds(0, 0, drawable.getMinimumWidth(),
		// drawable.getMinimumHeight());
		// traffic_live.setCompoundDrawables(null, null, drawable, null);
	}

	@Override
	public void onLastItemVisible() {
		if (!listView.isRefreshing()
				&& mAdapter.getState() == ListBaseSectionedAdapter.STATE_LOAD_MORE) {
			mPageNumber++;
			mAdapter.setState(ListBaseSectionedAdapter.STATE_LOAD_MORE);
			getNewsData();
		}

	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		mPageNumber = 1;
		mFromRefresh = true;
		mFirstVisitTime = "";
		getNewsData();

		// weatherFragment.refreshWeather();
		if (refresh == false) {
			refresh = true;
			return;
		}
//		headerView = new HomeHeaderView(this);
//		listView.getRefreshableView().addHeaderView(headerView);
		headerView.refresh();
		weatherFragment.refreshWeather();
		couponView.refresh();
		//刷新广播
		audioChannelWidget.loadfresh();
	}

	private void getNewsData() {
		mIsLoadNewsFinished = false;
		long current = System.currentTimeMillis();
		String lastNewsTime = Application.getLastSyncNewsTime(mChnl_id);
		long t = -1;
		if (!TextUtils.isEmpty(lastNewsTime)) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				t = sdf.parse(lastNewsTime).getTime();
			} catch (Exception ex) {
			}
		}
		long s_time = 5 * 60 * 1000;
		if (mPageNumber == 1) {
			mFirstVisitTime = "";
			hasLoadedCache = false;
		}
		if (mPageNumber != 1 || current - t >= s_time || mNeedRefresh) {
			hasLoadedWeb = true;
			GetIndexNewsHome req = new GetIndexNewsHome(mPageNumber,
					mFirstVisitTime, baseRequest, baseError);
			Application.instance().addToRequestQueue(req, TAG_INDEX_NEWS);
		} else {
			try {
				hasLoadedCache = true;
				hasLoadedWeb = false;
				new LoadNewsCacheTask().execute();
			} catch (Exception e) {
				TLog.error(e.getMessage());
			} finally {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						mIsLoadNewsFinished = true;
						handleListState();
					}
				}, 500);
			}
		}
		mNeedRefresh = false;

	}

	BaseRequestListener baseRequest = new BaseRequestListener() {

		@Override
		public void onRequestSuccess(ApiResponse json) throws Exception {
			JSONArray array = (JSONArray) json.getData();
			if (array == null)
				array = new JSONArray();
			if (mPageNumber == 1) {
				mFirstVisitTime = json.getFirstVisitTime();
				Application.setLastSyncNewsTime(mChnl_id, mFirstVisitTime);
				new SaveCacheTask(PREFIX_NEWS + mChnl_id).execute(array);
				hasLoadedCache = false;
			}
			handleNewsJSONArray(array);
		}

		@Override
		public void onRequestFinish() {
			mIsLoadNewsFinished = true;
			handleListState();
		}

		@Override
		public void onRequestFaile(ApiResponse json, Exception e) {
			e.printStackTrace();
			if (mPageNumber != 1) {
				mAdapter.setState(ListBaseSectionedAdapter.STATE_NO_MORE);
				mEmptyView.setState(EmptyView.STATE_NONE);
				mAdapter.notifyDataSetChanged();
				mIsLoadNewsFinished = true;
			} else {
				if (!hasLoadedCache) {
					hasLoadedCache = true;
					new LoadNewsCacheTask().execute();
				} else {
					mEmptyView.setState(EmptyView.STATE_ERROR);
					mEmptyView.setTip("暂无数据，请稍后再试");
					mAdapter.setState(ListBaseSectionedAdapter.STATE_NO_DATA);
					mAdapter.notifyDataSetChanged();
					mIsLoadNewsFinished = true;
				}
			}
		}
	};

	ErrorListener baseError = new ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError arg0) {
			if (mPageNumber != 1) {
				mAdapter.setState(ListBaseSectionedAdapter.STATE_NO_MORE);
				mEmptyView.setState(EmptyView.STATE_NONE);
				mAdapter.notifyDataSetChanged();
				mIsLoadNewsFinished = true;
			} else {
				if (!hasLoadedCache) {
					hasLoadedCache = true;
					new LoadNewsCacheTask().execute();
				} else {
					Application.showToastShort(BaseRequestListener
							.getErrorMessage(null));
					mAdapter.setState(ListBaseSectionedAdapter.STATE_NO_DATA);
					mEmptyView.setState(EmptyView.STATE_ERROR);
					mAdapter.notifyDataSetChanged();
					mIsLoadNewsFinished = true;
				}
			}
		}
	};

	private void handleNewsJSONArray(JSONArray array) throws Exception {
		if (mPageNumber == 1) {
			mAdapter.clear();
			scrollToTop();
		}
		HashSet<Integer> mIds = new HashSet<Integer>();
		ArrayList<NewsGroup> news = News.makeAll(mAdapter.getData(), array,
				mIds);
		mAdapter.setData(news);
		mEmptyView.setState(EmptyView.STATE_NONE);
		if ((array == null || array.length() == 0)) {
			if (mPageNumber == 1) {
				mEmptyView.setState(EmptyView.STATE_NO_DATA);
				mAdapter.setState(ListBaseSectionedAdapter.STATE_NO_DATA);
			} else {
				mAdapter.setState(ListBaseSectionedAdapter.STATE_NO_MORE);
			}
		} else if (array.length() < 15) {
			if (mPageNumber == 1) {
				TLog.log(TAG, "少于一页数据");
				mAdapter.setState(ListBaseSectionedAdapter.STATE_LESS_ONE_PAGE);
			} else {
				TLog.log(TAG, "没有更多");
				mAdapter.setState(ListBaseSectionedAdapter.STATE_NO_MORE);
			}
		} else {
			TLog.log(TAG, "正在加载更多");
			mAdapter.setState(ListBaseSectionedAdapter.STATE_LOAD_MORE);
		}
		handleUpdateCountTip(mIds);
	}

	private void handleUpdateCountTip(HashSet<Integer> mIds) {
		if (mPageNumber == 1) { // 如果是刷新这提示更新条数
			final HashSet<Integer> lastIds = Application
					.getLastNewsIds(mChnl_id);
			Application.setLastNewsIds(mChnl_id, mIds);
			mIds.removeAll(lastIds);
			final int updateCount = mIds.size();
//			if (mFromRefresh) {// 不是第一次刷新，且是下拉刷新
//				mFromRefresh = false;
//				new Handler().postDelayed(new Runnable() {
//
//					@Override
//					public void run() {
//						if (updateCount <= 0)
//							mTvUpdateCount
//									.setText(R.string.tip_news_update_none);
//						else
//							mTvUpdateCount
//									.setText(getString(
//											R.string.tip_news_update_count,
//											updateCount));
//						mTvUpdateCount.setVisibility(View.VISIBLE);
//						mTvUpdateCount.clearAnimation();
//						AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
//						anim.setAnimationListener(new AnimationListener() {
//
//							@Override
//							public void onAnimationStart(Animation animation) {
//								// TODO Auto-generated method stub
//							}
//
//							@Override
//							public void onAnimationRepeat(Animation animation) {
//								// TODO Auto-generated method stub
//							}
//
//							@Override
//							public void onAnimationEnd(Animation animation) {
//								mTvUpdateCount.setVisibility(View.GONE);
//							}
//						});
//						anim.setStartOffset(1000);
//						anim.setDuration(3000);
//						mTvUpdateCount.startAnimation(anim);
//					}
//				}, 500);
//			}
		}
	}

	private void handleListState() {
		if (mIsLoadNewsFinished) {
			listView.onRefreshComplete();
			mAdapter.notifyDataSetChanged();
		}
	}

	class LoadNewsCacheTask extends AsyncTask<Void, Void, JSONArray> {

		@Override
		protected JSONArray doInBackground(Void... params) {
			String cacheContent = IndexCacheUtils.getCache(PREFIX_NEWS
					+ mChnl_id);
			if (!TextUtils.isEmpty(cacheContent)) {
				try {
					return new JSONArray(cacheContent);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONArray result) {
			if (result == null) {
				if (hasLoadedWeb) {
					mEmptyView.setState(EmptyView.STATE_ERROR);
					mEmptyView.setTip("暂无数据，请稍后再试");
					mAdapter.setState(ListBaseSectionedAdapter.STATE_NO_DATA);
					mAdapter.notifyDataSetChanged();
					mIsLoadNewsFinished = true;
				} else {
					GetIndexNewsHome req = new GetIndexNewsHome(mPageNumber,
							mFirstVisitTime, baseRequest, baseError);
					Application.instance().addToRequestQueue(req,
							TAG_INDEX_NEWS);
				}
			} else {
				try {
					mFirstVisitTime = Application.getLastSyncNewsTime(mChnl_id);
					handleNewsJSONArray(result);
				} catch (Exception e) {
					mEmptyView.setState(EmptyView.STATE_ERROR);
					mAdapter.setState(ListBaseSectionedAdapter.STATE_NO_DATA);
				} finally {
					mIsLoadNewsFinished = true;
					handleListState();
				}
			}
			super.onPostExecute(result);
		}
	}

	class SaveCacheTask extends AsyncTask<JSONArray, Void, Void> {

		private String prefix;

		SaveCacheTask(String prefix) {
			this.prefix = prefix;
		}

		@Override
		protected Void doInBackground(JSONArray... params) {
			IndexCacheUtils.saveCache(params[0], prefix);
			return null;
		}
	}

	@Override
	public void onNewsItemClick(News news) {
		HashMap<String, String> param = new HashMap<String, String>();
		param.put("A_newsDetai_home_newsTitle", news.getTemplate().getTitle());
		MobclickAgent.onEvent(mContext, "E_C_home_newsDetailClick", param);
		if (news.getWapUrl().contains("web_type=banner_adv_type")) {
			Intent intent = new Intent(mActivity, SingMainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("url", news.getWapUrl());
			mContext.startActivity(intent);
		}else {
			Intent intent = new Intent(mActivity, NewsDetailActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("news", news);
			intent.putExtra("showMore", true);
			intent.setExtrasClassLoader(News.class.getClassLoader());
			startActivity(intent);
		}
		// 记录日志
		Application.instance().addEventLog("index",
				String.valueOf(news.getId()), "content");
	}

	private void scrollToTop() {
		if (!listView.getRefreshableView().isStackFromBottom()) {
			listView.getRefreshableView().setStackFromBottom(true);
		}
		listView.getRefreshableView().setStackFromBottom(false);
	}

	@Override
	public void onBackPressed() {
		((MainActivity) getParent()).onBack();
	}

	@Override
	protected void onPause() {
		mTvUpdateCount.setVisibility(View.GONE);
		super.onPause();
	}
	
	public void initBackgroundPlayService() {
		//开启后台播放音频服务
		Intent intent = new Intent(this,BackgroundPlayService.class);
		startService(intent);

		//创建本地存储文件
		File file = FileIOUtils.createFile();
		if(file!= null && file.length() <= 0)
			FileIOUtils.writeFileSdcardFile("viewPagerPosition:-1/playStatu:false");
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		setIntent(intent);// 必须要调用这句
	}
}
