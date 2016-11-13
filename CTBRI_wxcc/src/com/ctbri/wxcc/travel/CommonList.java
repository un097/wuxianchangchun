package com.ctbri.wxcc.travel;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.ctbri.comm.util._Utils;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseFragment;
import com.ctbri.wxcc.community.Constants_Community;
import com.ctbri.wxcc.community.ObjectAdapter;
import com.ctbri.wxcc.entity.PageModel;
import com.ctbri.wxcc.widget.LoadMorePTRListView;
import com.ctbri.wxcc.widget.LoadMorePTRListView.OnLoadMoreListViewListener;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public abstract class CommonList<BEAN, ENTITY> extends BaseFragment {

	protected LoadMorePTRListView lv_list;
	protected CommonListAdapter common_adapter;
	private PageModel pageModel;
	private String requestUrl;
	private DataSetObserver mDataObserver;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		int resId = getLayoutResId();

		if (resId == -1)
			resId = R.layout.travel_main_list;

		View v = inflater.inflate(resId, container, false);
		lv_list = (LoadMorePTRListView) v.findViewById(R.id.lv_travel_raiders);

		initHeaderDetail(lv_list, activity.getLayoutInflater());
		if (isInflateActionBar()) {
			((ViewStub) v.findViewById(R.id.vs_actionbar)).inflate();

			TextView tvTitle = (TextView) v.findViewById(R.id.action_bar_title);
			if (tvTitle != null)
				tvTitle.setText(getActionBarTitle());

			View backBtn = v.findViewById(R.id.action_bar_left_btn);
			if (backBtn != null)
				backBtn.setOnClickListener(new BackPressListener());
		}
		lv_list.setLoadMoreListener(new LoadMoreListenerImpl());
		lv_list.setOnRefreshListener(new RefreshListenerImpl());
		lv_list.setOnItemClickListener(new ItemClickListenerImpl());
		return v;
	}

	protected int getLayoutResId() {
		return -1;
	}

	protected String getActionBarTitle() {
		return "";
	}

	protected void onBackPress() {
		activity.finish();
	}

	public DataSetObserver getmDataObserver() {
		return mDataObserver;
	}

	public void setDataObserver(DataSetObserver mDataObserver) {
		this.mDataObserver = mDataObserver;
	}

	/**
	 * 返回加载数据的url
	 * 
	 **/
	protected abstract String getListUrl();

	protected abstract Class<BEAN> getGsonClass();

	protected abstract List<ENTITY> getEntitys(BEAN bean);

	protected abstract boolean isEnd(BEAN bean);

	protected abstract boolean initHeaderDetail(LoadMorePTRListView lv_list, LayoutInflater inflater);

	protected abstract View getListItemView(int position, View convertView, ViewGroup parent, ENTITY data, ImageLoader imgloader, DisplayImageOptions dio);

	protected abstract void onItemClick(AdapterView<?> parent, View view, int position, long id, ENTITY entity);

	/**
	 * 是否在 onCreated 时，加载数据
	 * 
	 * @return
	 */
	protected boolean isAfterActivityCreatedDoLoad() {
		return true;
	}

	/**
	 * 是否回收该位置的 view
	 * 
	 * @param position
	 * @return
	 */
	protected boolean isScrapItem(int position) {

		return true;
	}

	/**
	 * 是否显示 actionbar
	 * 
	 * @return
	 */
	protected boolean isInflateActionBar() {
		return true;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		pageModel = new PageModel(0, Constants_Community.PAGE_SIZE);
		requestUrl = getListUrl();
		if (isAfterActivityCreatedDoLoad())
			lv_list.showHeaderLoading();
		// loadData(requestUrl, pageModel, true);
	}

	private void loadData(String requestUrl, PageModel pm, final boolean isClearData) {
		String url = pageModel.appendToTail(requestUrl);
		request(url, new RequestCallback() {
			@Override
			public void requestSucc(String json) {
				Gson gson = new Gson();
				BEAN travelBean = gson.fromJson(json, getGsonClass());

				onDataLoaded(travelBean);
				fillData(travelBean, isClearData);

			}

			@Override
			public void requestFailed(int errorCode) {
				if (isClearData)
					lv_list.onRefreshComplete();
				else
					lv_list.showLoadMore();
				onDataLoaded(null);
			}
		}, getParams());
	}

	protected void onDataLoaded(BEAN bean) {

	}

	protected ArrayList<BasicNameValuePair> getParams() {
		return null;
	}

	protected void fillData(BEAN bean, final boolean isClean) {

		if (bean != null) {
			List<ENTITY> list = getEntitys(bean);
			if (common_adapter == null) {
				common_adapter = new CommonListAdapter(activity, list);
				if (mDataObserver != null)
					common_adapter.registerDataSetObserver(mDataObserver);
				lv_list.setAdapter(common_adapter);
			} else {
				if (isClean)
					common_adapter.clearData();

				common_adapter.addAll(list);
				common_adapter.notifyDataSetChanged();
			}
			// 如果无更多数据
			if (isEnd(bean)) {
				lv_list.hideLoadMore();
			} else {
				lv_list.showLoadMore();
			}
		}
		if (isClean) {
			lv_list.onRefreshComplete();
		}
	}

	protected class CommonListAdapter extends ObjectAdapter<ENTITY> {
		private ImageLoader imgloader;

		public CommonListAdapter(Activity activity, List<ENTITY> data_) {
			super(activity, data_);
			imgloader = ImageLoader.getInstance();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getListItemView(position, convertView, parent, getItem(position), imgloader, _Utils.DEFAULT_DIO);
		}

		/**
		 * 标示是否为收该位置的 view
		 */
		@Override
		public int getItemViewType(int position) {
			if (isScrapItem(position))
				return super.getItemViewType(position);
			else
				return AdapterView.ITEM_VIEW_TYPE_IGNORE;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}
	}

	class RefreshListenerImpl implements OnRefreshListener<ListView> {
		@Override
		public void onRefresh(PullToRefreshBase<ListView> refreshView) {
			pageModel.start = 0;
			loadData(requestUrl, pageModel, true);
		}
	}

	class LoadMoreListenerImpl implements OnLoadMoreListViewListener {

		@Override
		public void onLastItemClick(LoadMorePTRListView listview) {

		}

		@Override
		public void onAutoLoadMore(LoadMorePTRListView list) {
			pageModel.start += Constants_Community.PAGE_SIZE;
			loadData(requestUrl, pageModel, false);
		}

	}

	/**
	 * 重新加载第一页的数据
	 */
	protected void reload() {
		pageModel.start = 0;
		loadData(requestUrl, pageModel, true);
	}

	class ItemClickListenerImpl implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			CommonList.this.onItemClick(parent, view, position, id, common_adapter.getItem((int) id));
		}
	}

	class BackPressListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			onBackPress();
		}

	}
}
