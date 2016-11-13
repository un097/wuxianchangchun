package cn.ffcs.widget;

import java.util.List;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * <p>Title: 解决与其他横向滚动父控件冲突的子控件  </p>
 * <p>Description:                     </p>
 * <p>@author: zhangws                 </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2012-12-30           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class SubViewPager extends ViewPager {

	private List<View> viewList;
	private boolean noParentScroll = false;

	public SubViewPager(Context context) {
		super(context);
	}

	public SubViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setParent(List<View> viewList) {
		this.viewList = viewList;
	}

	public void setNoScrollByParent(boolean noParentScroll) {
		this.noParentScroll = noParentScroll;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent e) {
		if (viewList != null) {
			for (int i = 0; i < viewList.size(); i++) {
				View view = viewList.get(i);
				if (view != null && view instanceof SlidingMenuGroupView) {
					int scrollX = getScrollX();
					SlidingMenuGroupView slidingMenu = (SlidingMenuGroupView) view;
					switch (e.getAction()) {
					case MotionEvent.ACTION_DOWN:
						if (!noParentScroll) {
							if (scrollX == 0) {
								slidingMenu
										.setInterceptScroll(InterceptType.INTERCEPT_LEFT_TRANSVERSE);
							} else {
								slidingMenu.setInterceptScroll(InterceptType.INTERCEPT_NULL);
							}
						} else {
							slidingMenu.setInterceptScroll(InterceptType.INTERCEPT_NULL);
						}
						break;
					}
				}
				
				if (view != null && view instanceof SlidingMenuView) {
					int scrollX = getScrollX();
					SlidingMenuView slidingMenu = (SlidingMenuView) view;
					switch (e.getAction()) {
					case MotionEvent.ACTION_DOWN:
						if (!noParentScroll) {
							if (scrollX == 0) {
								slidingMenu
										.setInterceptScroll(InterceptType.INTERCEPT_LEFT_TRANSVERSE);
							} else {
								slidingMenu.setInterceptScroll(InterceptType.INTERCEPT_NULL);
							}
						} else {
							slidingMenu.setInterceptScroll(InterceptType.INTERCEPT_NULL);
						}
						break;
					}
				}
				if (view != null && view instanceof InterceptViewPager) {
					InterceptViewPager scrollView = (InterceptViewPager) view;
					switch (e.getAction()) {
					case MotionEvent.ACTION_DOWN:
						if (!noParentScroll) {
							if (getCurrentItem() == (getAdapter().getCount() - 1)) {
								scrollView
										.setIntercept(InterceptType.INTERCEPT_RIGHT_TRANSVERSE);
							} else {
								scrollView.setIntercept(InterceptType.INTERCEPT_NULL);
							}
						} else {
							scrollView.setIntercept(InterceptType.INTERCEPT_NULL);
						}
						break;
					}
				}
			}
		}
		return super.onInterceptTouchEvent(e);
	}
}
