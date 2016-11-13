package com.ctbri.wxcc.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ctbri.wxcc.R;
/**
 * 无线长春 加载更多 listview
 * @author yanyadi
 *
 */
public class LoadMoreListView extends ListView {

	private Activity ctxt;
	private ViewGroup footerView;
	private OnLoadMoreListViewListener loadMoreListener;

	public OnLoadMoreListViewListener getLoadMoreListener() {
		return loadMoreListener;
	}

	public void setLoadMoreListener(OnLoadMoreListViewListener loadMoreListener) {
		this.loadMoreListener = loadMoreListener;
	}

	public LoadMoreListView(Context context) {
		this(context, null);
	}

	public LoadMoreListView(Context context, AttributeSet attrs) {
		super(context, attrs);

		addFooter();
	}

	private void addFooter() {

		if (isInEditMode())
			return;

		ctxt = (Activity) getContext();
		footerView = (ViewGroup) ctxt.getLayoutInflater().inflate(
				R.layout.common_listview_loadmore, null);
		footerView.setOnClickListener(new LoadMoreButtonListenr());
		// 为 listview 添加footer 并设置， footer 不可选中。
		addFooterView(footerView, null, false);
	}
	/**
	 * 隐藏 加载更多 view
	 */
	public void hideLoadMore(){
		footerView.setVisibility(View.INVISIBLE);
	}
	/**
	 * 显示 加载更多 view
	 */
	public void showLoadMore(){
		footerView.setVisibility(View.VISIBLE);
	}
	@Override
	public boolean performClick() {
		// TODO Auto-generated method stub
		return super.performClick();
	}
//	@Override
//	public boolean onTouchEvent(MotionEvent ev) {
//		
//		int action = ev.getAction();
//		switch(action & MotionEvent.ACTION_MASK){
//		
//		case MotionEvent.ACTION_DOWN:
//			
//			LogS.i("LoadMoreView", getScrollY()+"");
//			
//			LogS.i("LoadMoreView", getFirstVisiblePosition()+"");
//			LogS.i("LoadMoreView", getChildAt(0).getTop()+"");
//			if(getFirstVisiblePosition()==0 && getChildAt(0).getTop()==0)
//				requestDisallowInterceptTouchEvent(false);
//			else
//				requestDisallowInterceptTouchEvent(true);
//			
//			break;
//		case MotionEvent.ACTION_MOVE:
//			if(getFirstVisiblePosition()==0 && getChildAt(0).getTop()==0)
//				requestDisallowInterceptTouchEvent(false);
//			else
//				requestDisallowInterceptTouchEvent(true);
//			break;
//		}
//		return super.onTouchEvent(ev);
//	}
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return super.onInterceptTouchEvent(ev);
		
		
	}
	
	class LoadMoreButtonListenr implements OnClickListener{

		@Override
		public void onClick(View v) {
			if(loadMoreListener!=null)
				loadMoreListener.onLoadMore(LoadMoreListView.this);
		}
		
	}
	public static interface OnLoadMoreListViewListener{
		void onLoadMore(LoadMoreListView listview);
	}

}
