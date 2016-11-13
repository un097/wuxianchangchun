package cn.ffcs.wisdom.city.simico.activity.collection;

import java.util.ArrayList;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import cn.ffcs.wisdom.city.R;
//import cn.ffcs.wisdom.city.personcenter.LoginActivity;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.simico.activity.collection.adapter.CollectionAdapter;
import cn.ffcs.wisdom.city.simico.activity.collection.adapter.CollectionAdapter.NewsDelegate;
import cn.ffcs.wisdom.city.simico.activity.detail.NewsDetailActivity;
import cn.ffcs.wisdom.city.simico.activity.home.cache.IndexCacheUtils;
import cn.ffcs.wisdom.city.simico.api.model.ApiResponse;
import cn.ffcs.wisdom.city.simico.api.model.News;
import cn.ffcs.wisdom.city.simico.api.request.BaseRequestListener;
import cn.ffcs.wisdom.city.simico.api.request.GetCollectionNews;
import cn.ffcs.wisdom.city.simico.api.request.SubscribeOrCancelNewsRequest;
import cn.ffcs.wisdom.city.simico.base.Application;
import cn.ffcs.wisdom.city.simico.base.Constants;
import cn.ffcs.wisdom.city.simico.kit.activity.PSActivity;
import cn.ffcs.wisdom.city.simico.kit.adapter.ListBaseAdapter;
import cn.ffcs.wisdom.city.simico.kit.util.TDevice;
import cn.ffcs.wisdom.city.simico.ui.grid.EmptyView;

//import com.actionbarsherlock.app.ActionBar;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class CollectionActivity extends PSActivity implements NewsDelegate {

	private CollectionAdapter mAdapter;
	private PullToRefreshListView mListView;
	private EmptyView mEmptyView;
	private static final String PREFIX_COLLECT_NEWS = "PREFIX_COLLECT_NEWS_";
	private boolean isEdit = false;
	private HashSet<Integer> collectIds;
	private HashSet<Integer> collectIds_logout;
	private LinearLayout bottom;
	private TextView tv_tip;
	private TextView edit;

	@Override
	public int getLayoutId() {
		return R.layout.simico_activity_collect;
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		super.init(savedInstanceState);
		initActionBar();

		collectIds = Application.getCollectNewsIds();
		collectIds_logout = Application.getLogoutCollectNewsIds();
		mListView = (PullToRefreshListView) findViewById(R.id.list);
		findViewById(R.id.iv_close).setOnClickListener(this);
		findViewById(R.id.iv_login).setOnClickListener(this);
		bottom = (LinearLayout) findViewById(R.id.ly_login);
		tv_tip = (TextView) findViewById(R.id.tv_tip);
		mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				refresh();
			}
		});
		mEmptyView = new EmptyView(this);
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int[] heights = new int[5];
		// 1 图
		heights[0] = (displaymetrics.widthPixels - (int) TDevice.dpToPixel(20)) * 3 / 5;
		// 3图
		heights[1] = (displaymetrics.widthPixels - (int) TDevice.dpToPixel(40)) / 4;
		// 2图
		heights[3] = ((displaymetrics.widthPixels - (int) TDevice.dpToPixel(30)) / 2) * 3 / 4;
		mAdapter = new CollectionAdapter(this);
		mAdapter.setImgHeights(heights);
		mAdapter.setEmptyView(mEmptyView);
		mAdapter.setState(ListBaseAdapter.STATE_EMPTY_ITEM);
		mListView.setAdapter(mAdapter);
		mListView.setRefreshing();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		bottom.setVisibility(AccountMgr.getInstance().isLogin(this) ? View.GONE
				: View.VISIBLE);
	}

	private void initActionBar() {
//		ActionBar actionBar = getSupportActionBar();
//		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//		View titleBar = getLayoutInflater().inflate(
//				R.layout.simico_titlebar_collection, null);
//		actionBar.setCustomView(titleBar);
		View titleBar = findViewById(R.id.titlebar_collect);
		titleBar.findViewById(R.id.btn_back).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();
					}
				});
		edit = (TextView) titleBar.findViewById(R.id.btn_edit);
		edit.setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						isEdit = !isEdit;
						((TextView) v).setText(isEdit ? "完成" : "编辑");
						mAdapter.setDelete(isEdit);
						mAdapter.notifyDataSetChanged();
					}
				});
	}

	@Override
	public void onNewsItemClick(News news) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, NewsDetailActivity.class);
		intent.putExtra("news", news);
		intent.putExtra("showMore", true);
		intent.setExtrasClassLoader(News.class.getClassLoader());
		startActivityForResult(intent, 1);
		// 记录日志
		Application.instance().addEventLog("index",
				String.valueOf(news.getId()), "content");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == RESULT_OK && requestCode == 1) {
			collectIds = Application.getCollectNewsIds();
			collectIds_logout = Application.getLogoutCollectNewsIds();
			mListView.setRefreshing();
		}
	}

	class LoadCacheTask extends AsyncTask<Void, Void, ArrayList<News>> {

		@Override
		protected ArrayList<News> doInBackground(Void... params) {
			String cacheContent = IndexCacheUtils
					.getCacheNoUser(PREFIX_COLLECT_NEWS
							+ Application.getCurrentUser());
			String cacheContent2 = IndexCacheUtils
					.getCacheNoUser(PREFIX_COLLECT_NEWS);
			JSONArray array = new JSONArray();
			JSONArray array2 = new JSONArray();
			ArrayList<News> news = new ArrayList<News>();
			if (!TextUtils.isEmpty(cacheContent)
					|| !TextUtils.isEmpty(cacheContent2)) {
				try {
					if (!TextUtils.isEmpty(cacheContent))
						array = new JSONArray(cacheContent);
					if (!TextUtils.isEmpty(cacheContent2))
						array2 = new JSONArray(cacheContent2);
					news = News.makeAllForCollectNews(array, array2,
							collectIds, collectIds_logout);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return news;
		}

		@Override
		protected void onPostExecute(ArrayList<News> result) {
			if (result != null && result.size() > 0) {
				try {
					handleNewsJSONArray(result);
				} catch (Exception e) {
					mEmptyView.setState(EmptyView.STATE_ERROR);
					mAdapter.setState(ListBaseAdapter.STATE_NO_DATA);
					mAdapter.notifyDataSetChanged();
				}
			} else {
				mEmptyView.setState(EmptyView.STATE_NO_DATA);
				mAdapter.setState(ListBaseAdapter.STATE_NO_DATA);
				mAdapter.notifyDataSetChanged();
				tv_tip.setVisibility(View.VISIBLE);
			}
			mListView.onRefreshComplete();
			super.onPostExecute(result);
		}
	}

	class SaveCacheTask extends AsyncTask<News, Void, Void> {

		@Override
		protected Void doInBackground(News... params) {
			String cacheContent = IndexCacheUtils
					.getCacheNoUser(PREFIX_COLLECT_NEWS
							+ Application.getCurrentUser());
			String cacheContent2 = IndexCacheUtils
					.getCacheNoUser(PREFIX_COLLECT_NEWS);
			JSONArray array = new JSONArray();
			JSONArray array2 = new JSONArray();
			if (!TextUtils.isEmpty(cacheContent)) {
				try {
					array = new JSONArray(cacheContent);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			if (!TextUtils.isEmpty(Application.getCurrentUser())
					&& !TextUtils.isEmpty(cacheContent2)) {
				try {
					array2 = new JSONArray(cacheContent2);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			if (collectIds.contains(params[0].getId())) {
				JSONArray newArray = new JSONArray();
				for (int i = 0; i < array.length(); i++) {
					JSONObject obj = array.optJSONObject(i);
					int id = obj.optInt("id", -1);
					if (id == params[0].getId())
						continue;
					if (!collectIds.contains(id))
						continue;
					newArray.put(obj);
				}
				IndexCacheUtils.saveCacheNoUser(newArray, PREFIX_COLLECT_NEWS
						+ Application.getCurrentUser());
				collectIds.remove(params[0].getId());
				Application.setCollectNewsIds(collectIds);
			}
			if (!TextUtils.isEmpty(Application.getCurrentUser())
					&& collectIds_logout.contains(params[0].getId())) {
				JSONArray newArray = new JSONArray();
				for (int i = 0; i < array2.length(); i++) {
					JSONObject obj = array2.optJSONObject(i);
					int id = obj.optInt("id", -1);
					if (id == params[0].getId())
						continue;
					if (!collectIds_logout.contains(id))
						continue;
					newArray.put(obj);
				}
				IndexCacheUtils.saveCacheNoUser(newArray, PREFIX_COLLECT_NEWS);
				collectIds_logout.remove(params[0].getId());
				Application.setLogoutCollectNewsIds(collectIds_logout);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			mListView.setRefreshing();
			super.onPostExecute(result);
		}
	}

	private void handleNewsJSONArray(ArrayList<News> news) throws Exception {
		mEmptyView.setState(EmptyView.STATE_NONE);
		mAdapter.setState(ListBaseAdapter.STATE_EMPTY_ITEM);
		mAdapter.setData(news);
	}

	@Override
	public void onNewsDeleteClick(News news) {
		// TODO Auto-generated method stub
		handleCancelService(news);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		int id = v.getId();
		if (id == R.id.iv_login) {
//			Intent loginIntent = new Intent(CollectionActivity.this,
//					LoginActivity.class);
			Intent loginIntent = new Intent();
			loginIntent.setClassName(CollectionActivity.this, "cn.ffcs.changchuntv.activity.login.LoginActivity");
			startActivityForResult(loginIntent, 1);
		} else if (id == R.id.iv_close) {
			bottom.setVisibility(View.GONE);
		}
	}

	private void handleCancelService(final News news) {
		if (TextUtils.isEmpty(Application.getCurrentUser())) {
			try {
				dealCancelService(news);
			} catch (Exception ex) {
			}
			return;
		}
		showWaitDialog(R.string.progress_subming);
		SubscribeOrCancelNewsRequest req = new SubscribeOrCancelNewsRequest(
				news.getId(), false, new BaseRequestListener() {

					@Override
					public void onRequestSuccess(ApiResponse json)
							throws Exception {
						// Application.showToastShort("已从首页移除");
						dealCancelService(news);
					}

					@Override
					public void onRequestFinish() {
						hideWaitDialog();
					}

					@Override
					public void onRequestFaile(ApiResponse json, Exception e) {
						Application.showToastShort(getErrorMessage(json));
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {
						Application.showToastShort(BaseRequestListener
								.getErrorMessage(null));
						hideWaitDialog();
					}
				});
		Application.instance().addToRequestQueue(req);
	}

	private void dealCancelService(News news) {
		new SaveCacheTask().execute(news);
	}

	private void refresh() {
		tv_tip.setVisibility(View.GONE);
		mEmptyView.setState(EmptyView.STATE_LOADING);
		long current = System.currentTimeMillis();
		if (!TextUtils.isEmpty(Application.getCurrentUser())
				&& current - Application.getLastCollectTime() >= Constants.INTEVAL_SYNC_TIME) {
			sendRequest();
		} else {
			new LoadCacheTask().execute();
		}
	}

	private void sendRequest() {
		GetCollectionNews req = new GetCollectionNews(1,
				new BaseRequestListener() {

					@Override
					public void onRequestSuccess(ApiResponse json)
							throws Exception {
						JSONArray array = (JSONArray) json.getData();
						handleResult(array);
					}

					@Override
					public void onRequestFinish() {
					}

					@Override
					public void onRequestFaile(ApiResponse json, Exception e) {
						if (!TextUtils.isEmpty(Application.getCurrentUser())) {
							mEmptyView.setState(EmptyView.STATE_ERROR);
							mEmptyView.setTip(getErrorMessage(json));
						} else {
							mEmptyView.setState(EmptyView.STATE_NONE);
						}
						mAdapter.setState(ListBaseAdapter.STATE_NO_DATA);
						mAdapter.notifyDataSetChanged();
						mListView.onRefreshComplete();
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {
						if (!TextUtils.isEmpty(Application.getCurrentUser())) {
							mEmptyView.setState(EmptyView.STATE_ERROR);
							mEmptyView.setTip(BaseRequestListener
									.getErrorMessage(null));
						} else {
							mEmptyView.setState(EmptyView.STATE_NONE);
						}
						mAdapter.setState(ListBaseAdapter.STATE_NO_DATA);
						mAdapter.notifyDataSetChanged();
						mListView.onRefreshComplete();
					}
				});
		Application.instance().addToRequestQueue(req);
	}

	private void handleResult(JSONArray array) {
		collectIds.clear();
		array = array == null ? new JSONArray() : array;
		for (int i = 0; i < array.length(); i++) {
			collectIds.add(array.optJSONObject(i).optInt("id"));
		}
		Application.setCollectNewsIds(collectIds);
		IndexCacheUtils.saveCacheNoUser(array, PREFIX_COLLECT_NEWS
				+ Application.getCurrentUser());
		Application.setLastCollectTime(System.currentTimeMillis());
		new LoadCacheTask().execute();
	}

	@Override
	public void onBackPressed() {
		if (isEdit) {
			isEdit = false;
			edit.setText("编辑");
			mAdapter.setDelete(isEdit);
			mAdapter.notifyDataSetChanged();
			return;
		}
		super.onBackPressed();
	}
}
