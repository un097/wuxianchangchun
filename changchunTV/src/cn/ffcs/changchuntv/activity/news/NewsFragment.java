package cn.ffcs.changchuntv.activity.news;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.ffcs.changchun_base.activity.BaseFragment;
import cn.ffcs.changchuntv.R;
import cn.ffcs.changchuntv.activity.home.view.AdvertisingHeaderView;
import cn.ffcs.changchuntv.activity.login.bo.ThirdAccountBo;
import cn.ffcs.changchuntv.activity.news.NewsAdapter.NewsDelegate;
import cn.ffcs.changchuntv.entity.AdvertisingEntity;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.simico.activity.detail.NewsDetailActivity;
import cn.ffcs.wisdom.city.simico.activity.home.adapter.ListBaseSectionedAdapter;
import cn.ffcs.wisdom.city.simico.activity.home.cache.IndexCacheUtils;
import cn.ffcs.wisdom.city.simico.api.model.ApiResponse;
import cn.ffcs.wisdom.city.simico.api.model.News;
import cn.ffcs.wisdom.city.simico.api.model.NewsGroup;
import cn.ffcs.wisdom.city.simico.api.request.BaseRequestListener;
import cn.ffcs.wisdom.city.simico.api.request.GetIndexNewsByChannel;
import cn.ffcs.wisdom.city.simico.api.request.GetIndexNewsHome;
import cn.ffcs.wisdom.city.simico.base.Application;
import cn.ffcs.wisdom.city.simico.kit.log.TLog;
import cn.ffcs.wisdom.city.simico.kit.util.TDevice;
import cn.ffcs.wisdom.city.simico.ui.grid.EmptyView;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.JsonUtil;
import cn.ffcs.wisdom.tools.StringUtil;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.ctbri.wxcc.shake.SingMainActivity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;

public class NewsFragment extends BaseFragment implements
		OnRefreshListener<ListView>, OnLastItemVisibleListener, NewsDelegate {

	private static final String TAG = NewsFragment.class.getSimpleName();
	private static final String TAG_INDEX_NEWS = "TAG_INDEX_NEWS";
	private static final String PREFIX_NEWS = "PREFIX_NEWS_";

	private NewsAdapter mAdapter;
	private PullToRefreshListView mListView;
	// private ServiceHeaderView mServiceView;
	private EmptyView mEmptyView;
	private TextView mTvUpdateCount;// 更新资讯条数提醒
	private ImageView mIvScrollTop; // 置顶的按钮图片

	private int mPageNumber = 1;
	private int firstPageItem = 0;
	private int mChnl_id = 0;
	private boolean mIsLoadNewsFinished;

	private boolean mFromRefresh;
	private boolean mNeedRefresh = false;

	private boolean hasLoadedCache;
	private boolean hasLoadedWeb;
	// private HashSet<Integer> mIds = new HashSet<Integer>();// 存储本次返回资讯ID
	private String mFirstVisitTime;// 第一次网络获取时间 去重作用
	private NewsActivity mActivity;
	private boolean mIsCurrent = false;

	private String title;
	AdvertisingHeaderView headerView;

	public static Fragment newInstance(int chnl_id) {
		NewsFragment f = new NewsFragment();
		f.setChnl_id(chnl_id);
		return f;
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.init(savedInstanceState);
	}

	@Override
	public int getLayoutId() {
		return R.layout.fragment_news;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	protected void initview(View view) {
		// init header about service.
		// View header = LayoutInflater.from(getActivity()).inflate(
		// R.layout.simico_list_cell_home_service_header_v2, null);
		// ViewPager viewPager = (ViewPager) header
		// .findViewById(R.id.sevice_view_pager);
		// HomeServiceViewPagerAdapter adapter = new
		// HomeServiceViewPagerAdapter();
		// viewPager.setAdapter(adapter);
		// header.findViewById(R.id.ib_more_service).setOnClickListener(
		// new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// Intent intent = new Intent(getActivity(),
		// ServiceActivity.class);
		// startActivity(intent);
		// }
		// });

		mTvUpdateCount = (TextView) view.findViewById(R.id.tv_update_count);
		mIvScrollTop = (ImageView) view.findViewById(R.id.btn_scrolltop);
		mIvScrollTop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				scrollToTop();
			}
		});
		mListView = (PullToRefreshListView) view.findViewById(R.id.list);
		// mListView.getRefreshableView().addHeaderView(header);

		// mListView.setOnScrollListener(mScrollListener);
		// mListView.getRefreshableView().setFastScrollEnabled(true);
		mListView.setOnRefreshListener(this);
		mListView.setOnLastItemVisibleListener(this);

		mEmptyView = new EmptyView(getActivity());
		// mListView.setEmptyView(mEmptyView);
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay()
				.getMetrics(displaymetrics);
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

		// 添加测试数据
		// mAdapter.addData(make(0));
		// mServiceView.setService(makeService());

		mListView.setAdapter(mAdapter);
		mListView.setRefreshing();
		mListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				// Log.e("sb", view.getScrollY() +
				// "                            ----------");
				if (firstVisibleItem == 0)
					firstPageItem = visibleItemCount;
				if (firstVisibleItem >= firstPageItem)
					mIvScrollTop.setVisibility(View.VISIBLE);
				else
					mIvScrollTop.setVisibility(View.GONE);
			}
		});
		super.initview(view);
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		mIsCurrent = isVisibleToUser;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = (NewsActivity) activity;
	}

	@Override
	public void setMenuVisibility(boolean menuVisible) {
		// TODO Auto-generated method stub
		super.setMenuVisibility(menuVisible);
		if (this.getView() != null)
			this.getView()
					.setVisibility(menuVisible ? View.VISIBLE : View.GONE);
	}

	public void setChnl_id(int chnl_id) {
		mChnl_id = chnl_id;
	}

	/**
	 * 滑动至顶部
	 */
	private void scrollToTop() {
		if (!mListView.getRefreshableView().isStackFromBottom()) {
			mListView.getRefreshableView().setStackFromBottom(true);
		}
		mListView.getRefreshableView().setStackFromBottom(false);
	}

	@Override
	public void onLastItemVisible() {
		TLog.log(TAG, "onLastItemVisible");
		if (!mListView.isRefreshing()
				&& mAdapter.getState() == ListBaseSectionedAdapter.STATE_LOAD_MORE) {
			TLog.log(TAG, "开始加载更多首页资讯..");
			mPageNumber++;
			mAdapter.setState(ListBaseSectionedAdapter.STATE_LOAD_MORE);
			getNewsData();
			Application.instance().addEventLog("index", "content-up");
		}

	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		TLog.log(TAG, "onRefresh");
		mPageNumber = 1;
		mFromRefresh = true;
		mFirstVisitTime = "";
		// getServiceData();
		getNewsData();
		refreshAdvertising();
		Application.instance().addEventLog("index", "content-down");

	}

	public void refreshAdvertising() {
		Map<String, Object> map = new HashMap<String, Object>();
		String phone = AccountMgr.getInstance().getMobile(mActivity);
		phone = StringUtil.isEmpty(phone) ? "unknown" : phone;
		map.put("mobile", phone);
		String cityCode = MenuMgr.getInstance().getCityCode(mActivity);
		map.put("cityCode", cityCode);
		map.put("orgCode", cityCode);
		map.put("longitude", "unknown");
		map.put("latitude", "unknown");
		String imsi = AppHelper.getMobileIMSI(mActivity);
		if (StringUtil.isEmpty(imsi)) {
			imsi = "0000000000000000";
		}
		String[] groups = { String.valueOf(mChnl_id) };
		String imei = AppHelper.getIMEI(mActivity);
		map.put("imsi", imsi);
		map.put("imei", imei);
		map.put("groups", groups);
		String sign = String.valueOf(mChnl_id);
		ThirdAccountBo bo = new ThirdAccountBo();
		bo.getAdvertising(new AdvertisingCallBack(), mActivity, map, sign);
	}

	class AdvertisingCallBack implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp response) {
			if (response.isSuccess()) {
				// AdvertisingEntity adEntity = new AdvertisingEntity();
				// List<Advertising> banner_advs = new
				// ArrayList<AdvertisingEntity.Advertising>();
				// for (int i = 0; i < 5; i++) {
				// Advertising entity = new AdvertisingEntity().new
				// Advertising();
				// entity.group = -1;
				// entity.android_pic =
				// "http://img5.imgtn.bdimg.com/it/u=1041153494,1223721837&fm=116&gp=0.jpg";
				// entity.id = i;
				// entity.order = i;
				// entity.title = "测试数据" + i;
				// entity.url = "www.baiduy.com";
				// banner_advs.add(entity);
				// }
				// adEntity.banner_advs = banner_advs;
				// initBanner(adEntity);
				AdvertisingEntity adEntity = new AdvertisingEntity();
				try {
					JSONArray jarray = new JSONArray(response.getData());
					adEntity = JsonUtil.toObject(jarray.get(0).toString(),
							AdvertisingEntity.class);
					if (adEntity.banner_advs.size() != 0) {
						if (headerView == null) {
							headerView = new AdvertisingHeaderView(mActivity,
									mChnl_id);
							mListView.getRefreshableView().addHeaderView(
									headerView);
						}
						headerView.initBanner(adEntity);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void progress(Object... obj) {
		}

		@Override
		public void onNetWorkError() {
		}
	}

	private void getNewsData() {
		if (mIsCurrent)
			mActivity.updateRefresh(true);
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
			TLog.log(TAG, "从网络获取新闻数据");
			hasLoadedWeb = true;
			if (mChnl_id == 0) {
				GetIndexNewsHome req = new GetIndexNewsHome(mPageNumber,
						mFirstVisitTime, baseRequest, baseError);
				Application.instance().addToRequestQueue(req, TAG_INDEX_NEWS);
			} else {
				GetIndexNewsByChannel req = new GetIndexNewsByChannel(
						mPageNumber, mChnl_id, mFirstVisitTime, baseRequest,
						baseError);
				Application.instance().addToRequestQueue(req, TAG_INDEX_NEWS);
			}
		} else {
			TLog.log(TAG, "获取新闻本地缓存");
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
			// TODO Auto-generated method stub
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
			// TODO Auto-generated method stub
			mIsLoadNewsFinished = true;
			handleListState();
			mActivity.updateRefresh(false);
		}

		@Override
		public void onRequestFaile(ApiResponse json, Exception e) {
			// TODO Auto-generated method stub
			e.printStackTrace();
			if (mPageNumber != 1) {
				mAdapter.setState(ListBaseSectionedAdapter.STATE_NO_MORE);
				mEmptyView.setState(EmptyView.STATE_NONE);
				mAdapter.notifyDataSetChanged();
				mIsLoadNewsFinished = true;
				mActivity.updateRefresh(false);
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
					mActivity.updateRefresh(false);
				}
			}
		}
	};

	ErrorListener baseError = new ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError arg0) {
			// TODO Auto-generated method stub
			if (mPageNumber != 1) {
				mAdapter.setState(ListBaseSectionedAdapter.STATE_NO_MORE);
				mEmptyView.setState(EmptyView.STATE_NONE);
				mAdapter.notifyDataSetChanged();
				mIsLoadNewsFinished = true;
				mActivity.updateRefresh(false);
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
					mActivity.updateRefresh(false);
				}
			}
		}
	};

	@SuppressWarnings("unchecked")
	private void handleNewsJSONArray(JSONArray array) throws Exception {
		if (mPageNumber == 1) {
			mAdapter.clear();
			scrollToTop();
		}
		HashSet<Integer> mIds = new HashSet<Integer>();//
		ArrayList<NewsGroup> news = News.makeAll(mAdapter.getData(), array,
				mIds);
		// ArrayList<News> news = News.makeAllForNews(array);
		// Utils.saveCache(array.toString(), "index_content.txt");
		mAdapter.setData(news);
		// TLog.log(TAG, "总共获取新闻条数:" + array.length() + " 当前第" + mPageNumber +
		// "页"+" 已获取："+mAdapter.getData().size());
		mEmptyView.setState(EmptyView.STATE_NONE);
		if ((array == null || array.length() == 0)) {
			if (mPageNumber == 1) {
				TLog.log(TAG, "列表为空");
				mEmptyView.setState(EmptyView.STATE_NO_DATA);
				mAdapter.setState(ListBaseSectionedAdapter.STATE_NO_DATA);
			} else {
				TLog.log(TAG, "没有更多");
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
			if (mFromRefresh) {// 不是第一次刷新，且是下拉刷新
				mFromRefresh = false;
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						if (updateCount <= 0)
							mTvUpdateCount
									.setText(R.string.tip_news_update_none);
						else
							mTvUpdateCount.setText(mActivity
									.getString(R.string.tip_news_update_count,
											updateCount));
						mTvUpdateCount.setVisibility(View.VISIBLE);
						mTvUpdateCount.clearAnimation();
						AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
						anim.setAnimationListener(new AnimationListener() {

							@Override
							public void onAnimationStart(Animation animation) {
								// TODO Auto-generated method stub
							}

							@Override
							public void onAnimationRepeat(Animation animation) {
								// TODO Auto-generated method stub
							}

							@Override
							public void onAnimationEnd(Animation animation) {
								// TODO Auto-generated method stub
								mTvUpdateCount.setVisibility(View.GONE);
							}
						});
						anim.setStartOffset(1000);
						anim.setDuration(3000);
						mTvUpdateCount.startAnimation(anim);
					}
				}, 500);
			}
		}
	}

	private void handleListState() {
		if (mIsLoadNewsFinished) {
			mListView.onRefreshComplete();
			mAdapter.notifyDataSetChanged();
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
					mActivity.updateRefresh(false);
				} else {
					if (mChnl_id == 0) {
						GetIndexNewsHome req = new GetIndexNewsHome(
								mPageNumber, mFirstVisitTime, baseRequest,
								baseError);
						Application.instance().addToRequestQueue(req,
								TAG_INDEX_NEWS);
					} else {
						GetIndexNewsByChannel req = new GetIndexNewsByChannel(
								mPageNumber, mChnl_id, mFirstVisitTime,
								baseRequest, baseError);
						Application.instance().addToRequestQueue(req,
								TAG_INDEX_NEWS);
					}
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
					mActivity.updateRefresh(false);
				}
			}
			super.onPostExecute(result);
		}
	}

	/**
	 * 新闻点击进入新闻详情页面
	 */
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
			Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("news", news);
			intent.putExtra("showMore", true);
			intent.putExtra("titleString", news.getChannelName());
			intent.setExtrasClassLoader(News.class.getClassLoader());
			startActivity(intent);
		}
		// 记录日志
		Application.instance().addEventLog("index",
				String.valueOf(news.getId()), "content");

	}

	public void onSelectInViewPager() {
		mActivity.updateRefresh(!mIsLoadNewsFinished);
	}

	public void refresh() {
		if (mListView != null) {
			mNeedRefresh = true;
			// 刷新列表控件
			mListView.setRefreshing();
		} else {
			mActivity.updateRefresh(false);
		}
	}

	@Override
	public void onPause() {
		mTvUpdateCount.setVisibility(View.GONE);
		super.onPause();
	}

}
