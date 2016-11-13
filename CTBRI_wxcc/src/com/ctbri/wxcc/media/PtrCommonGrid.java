package com.ctbri.wxcc.media;

import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
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
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnPullEventListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshBase.State;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public abstract class PtrCommonGrid<BEAN, ENTITY> extends BaseFragment {

	protected GridView grid_list;
	protected CommonGridAdapter common_adapter;
	private PageModel pageModel;
	private String requestUrl;
	protected LayoutInflater inflater;
	private PullToRefreshGridView ptrView;
	private View mView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.inflater = inflater;
		int resId = getLayoutResId();

		if (resId == -1)
			resId = R.layout.media_video_ptr_grid;

		mView = inflater.inflate(resId, container, false);
		ptrView = (PullToRefreshGridView) mView.findViewById(R.id.ptr_gridview);
		ptrView.setOnRefreshListener(mRefreshListener);

		grid_list = (GridView) mView.findViewById(R.id.gridview);

		initHeaderDetail(mView, grid_list, inflater);

		if (isInflateActionBar() && mView.findViewById(R.id.vs_actionbar) != null) {
			((ViewStub) mView.findViewById(R.id.vs_actionbar)).inflate();
			TextView tvTitle = (TextView) mView.findViewById(R.id.action_bar_title);
			if (tvTitle != null)
				tvTitle.setText(getActionBarTitle());

			View backBtn = mView.findViewById(R.id.action_bar_left_btn);
			if (backBtn != null)
				backBtn.setOnClickListener(new BackPressListener());
		}

		grid_list.setOnItemClickListener(new ItemClickListenerImpl());
		return mView;
	}

	private OnRefreshListener2<GridView> mRefreshListener = new OnRefreshListener2<GridView>() {

		@Override
		public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
			ptrView.setRefreshing();
			pageModel.start = 0;
			loadData(requestUrl, pageModel, true);
		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {

		}

	};

	protected int getLayoutResId() {
		return -1;
	}

	protected String getActionBarTitle() {
		return "";
	}

	protected void onBackPress() {
		activity.finish();
	}

	/**
	 * 返回加载数据的url
	 * 
	 **/
	protected abstract String getListUrl();

	protected abstract Class<BEAN> getGsonClass();

	protected abstract List<ENTITY> getEntitys(BEAN bean);

	protected abstract boolean isEnd(BEAN bean);

	protected abstract boolean initHeaderDetail(View parent, GridView lv_list, LayoutInflater inflater);

	protected abstract View getListItemView(int position, View convertView, ViewGroup parent, ENTITY data, ImageLoader imgloader, DisplayImageOptions dio);

	protected abstract void onItemClick(AdapterView<?> parent, View view, int position, long id, ENTITY entity);
	
	protected int getViewItemType(int postion, ENTITY data) {
		return -1;
	}
	protected int getViewItemTypeCount() {
		return 1;
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

		ptrView.getChildAt(0).addOnLayoutChangeListener(mLoadingViewLayoutListener);
		loadData(requestUrl, pageModel, true);
	}

	private OnLayoutChangeListener mLoadingViewLayoutListener = new OnLayoutChangeListener() {
		@Override
		public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
			int size = bottom - top;
			if (size > 0) {
				ptrView.setRefreshing();
				v.removeOnLayoutChangeListener(this);
			}
		}
	};


	private void loadData(String requestUrl, PageModel pm, final boolean isClearData) {
		String url = pageModel.appendToTail(requestUrl);
		request(url, new RequestCallback() {
			@Override
			public void requestSucc(String json) {
				Gson gson = new Gson();
				BEAN travelBean = gson.fromJson(json, getGsonClass());
				fillData(travelBean, isClearData);
				ptrView.onRefreshComplete();
			}

			@Override
			public void requestFailed(int errorCode) {
				ptrView.onRefreshComplete();

			}
		});
	}

	protected void fillData(BEAN travelBean, final boolean isClean) {

		if (travelBean != null) {
			List<ENTITY> list = getEntitys(travelBean);
			if (common_adapter == null) {
				common_adapter = new CommonGridAdapter(activity, list);
				grid_list.setAdapter(common_adapter);
			} else {
				if (isClean)
					common_adapter.clearData();

				common_adapter.addAll(list);
				common_adapter.notifyDataSetChanged();
			}
		}
	}

	protected class CommonGridAdapter extends ObjectAdapter<ENTITY> {
		private ImageLoader imgloader;

		public CommonGridAdapter(Activity activity, List<ENTITY> data_) {
			super(activity, data_);
			imgloader = ImageLoader.getInstance();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getListItemView(position, convertView, parent, getItem(position), imgloader, _Utils.DEFAULT_DIO);
		}
		@Override
		public int getItemViewType(int position) {
			ENTITY  data = getItem(position);
			int type = getViewItemType(position, 	data);
			if(type != -1)
				return type;
			
			return super.getItemViewType(position);
		}
		@Override
		public int getViewTypeCount() {
			return getViewItemTypeCount();
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

	class ItemClickListenerImpl implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Log.i("CommonList ", " pos=" + position);
			PtrCommonGrid.this.onItemClick(parent, view, position, id, common_adapter.getItem((int) id));
		}
	}

	class BackPressListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			onBackPress();
		}

	}
}
