package com.ctbri.wxcc.widget;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ctbri.comm.util._Utils;
import com.ctbri.wxcc.R;
import com.handmark.pulltorefresh.library.PullToRefreshLoadMoreListView;

public class LoadMorePTRListView extends PullToRefreshLoadMoreListView {
	private ViewGroup footerView;
	private OnLoadMoreListViewListener loadMoreListener;
	private ProgressBar progress;
	private TextView tv_info;
	private boolean isSetHiddenMore = false;
	private boolean isLayout = false;
	private boolean isShowHeader = false;
	private LoadMoreMode mMode;
	public LoadMoreMode getmMode() {
		return mMode;
	}

	public void setmMode(LoadMoreMode mMode) {
		if(this.mMode == mMode)return;
		this.mMode = mMode;
		
		if(mMode==LoadMoreMode.MODE_CLICK_LOAD){
			tv_info.setText(R.string.seeMore);
			progress.setVisibility(View.GONE);
		}else{
			tv_info.setText(R.string.loading);
			progress.setVisibility(View.VISIBLE);
		}
	}
	/**
	 * 显示 Header loading ,并触发 onPullToRefresh 事件。<br/>调用该方法，不需要显式的调用网络请求的函数，实现下拉刷新接口即可。
	 */
	public void showHeaderLoading(){
		if(isLayout)
			setRefreshing();
		else
			isShowHeader = true;
	}
	/**
	 * 加载更多的方式
	 * @author yanyadi
	 *
	 */
	public static enum LoadMoreMode{
		MODE_AUTOLOAD,MODE_CLICK_LOAD
	}
	
	
	public OnLoadMoreListViewListener getLoadMoreListener() {
		return loadMoreListener;
	}

	public void setLoadMoreListener(OnLoadMoreListViewListener loadMoreListener) {
		this.loadMoreListener = loadMoreListener;
	}

	public LoadMorePTRListView(Context context) {
		this(context, null);
	}

	public LoadMorePTRListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		//注册 header loading 监听，在获取到尺寸后，做后续操作。
		getHeaderLayout().getViewTreeObserver().addOnGlobalLayoutListener(new GlobalLayoutListener());
		addFooter();
		//设置默认的 loading icon
//		getLoadingLayoutProxy().setLoadingDrawable(getResources().getDrawable(R.drawable.icon_pulltorefresh));
		
	}
	
	class GlobalLayoutListener implements OnGlobalLayoutListener{

		@Override
		public void onGlobalLayout() {
			int size = getHeaderSize();
			if(0 != size){
				//移动 layout 监听
				getHeaderLayout().getViewTreeObserver().removeGlobalOnLayoutListener(this);
				isLayout = true;
				
				if(isShowHeader)
				{
					setRefreshing();
					isShowHeader = false;
				}
			}
			
		}
		
	}
	
	public void addHeader(View view, Object data, boolean isSelectable){
		getRefreshableView().addHeaderView(view, data, isSelectable);
	}
	private void addFooter() {

		if (isInEditMode())
			return;
		
		footerView =  (ViewGroup)LayoutInflater.from(getContext()).inflate(
				R.layout.common_listview_loadmore, null);
		footerView.setOnClickListener(new LoadMoreButtonListenr());
		progress = (ProgressBar)footerView.findViewById(R.id.progressLoading);
		tv_info = (TextView)footerView.findViewById(R.id.tv_footer_info);
		
		// 为 listview 添加footer 并设置， footer 不可选中。
		ListView lv = getRefreshableView();
		lv.addFooterView(footerView, null, false);
		//去除MEIZU上的黑线
		if( !TextUtils.isEmpty(Build.MANUFACTURER) && "meizu".equalsIgnoreCase(Build.MANUFACTURER))
			_Utils.invokeMethod(lv, lv.getClass(), "setTopShadowEnable", Void.TYPE, new Class[]{Boolean.TYPE}, false);
		//设置默认的loadMore 方式为自动加载
	
		mMode = LoadMoreMode.MODE_AUTOLOAD;
		
//		setOnScrollListener(new ScrollListener());
		setOnLastItemVisibleListener(new LastItemVisiable());
		
		hideLoadMore(false);
	}
	
	/**
	 * 隐藏 加载更多 view
	 */
	public void hideLoadMore(){
		progress.setVisibility(View.GONE);
		tv_info.setText(R.string.noMore);
		isSetHiddenMore = true;
	}
	/**
	 * 隐藏 加载更多 view
	 */
	public void hideLoadMore(boolean isFromUser){
		progress.setVisibility(View.GONE);
		tv_info.setText("");
		isSetHiddenMore = isFromUser;
	}
	
	
	
	/**
	 * 显示 加载更多 view
	 */
	public void showLoadMore(){
		if(mMode==LoadMoreMode.MODE_AUTOLOAD)
		{
			progress.setVisibility(View.VISIBLE);
			tv_info.setText(R.string.loading);
		}else{
			progress.setVisibility(View.GONE);
			tv_info.setText(R.string.seeMore);
		}
		isSetHiddenMore = false;
	}

	
	class ScrollListener implements OnScrollListener{

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
//			totalItemCount   根据理解
			//
//				isLastVisiable = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount - 1);
		}
		
	}
	class LastItemVisiable implements OnLastItemVisibleListener{

		@Override
		public void onLastItemVisible() {
			if(mMode==LoadMoreMode.MODE_AUTOLOAD && loadMoreListener!=null && !isSetHiddenMore)
				loadMoreListener.onAutoLoadMore(LoadMorePTRListView.this);
		}
		
	}
	
	public void showLoading(){
		tv_info.setText(R.string.loading);
		progress.setVisibility(View.VISIBLE);
	}
	public void hideFooter(){
		View footerFirstView = footerView.getChildAt(0);
		footerFirstView.getLayoutParams().height = 0;
		footerFirstView.setLayoutParams(footerFirstView.getLayoutParams());
	}
	
	
	class LoadMoreButtonListenr implements OnClickListener{

		@Override
		public void onClick(View v) {
			if(loadMoreListener!=null && mMode==LoadMoreMode.MODE_CLICK_LOAD)
				loadMoreListener.onLastItemClick(LoadMorePTRListView .this);
		}
		
	}
	public static interface OnLoadMoreListViewListener{
		void onLastItemClick(LoadMorePTRListView listview);
		
		void onAutoLoadMore(LoadMorePTRListView list);
	}
}
