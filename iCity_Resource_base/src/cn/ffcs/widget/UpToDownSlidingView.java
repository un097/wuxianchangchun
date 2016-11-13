package cn.ffcs.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * <p>Title:  上下拉控件     </p>
 * <p>Description:                     </p>
 * <p>@author: zhangwsh                </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-9-17           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class UpToDownSlidingView extends LinearLayout {

	private Scroller mScroller;
	private View upView;
	private int time = 500;

	public UpToDownSlidingView(Context context) {
		super(context);
		init();
	}

	public UpToDownSlidingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		mScroller = new Scroller(getContext());
	}

	@Override
	public void scrollTo(int x, int y) {
		super.scrollTo(x, y);
		postInvalidate();
	}

	@Override
	public void computeScroll() {
		if (!mScroller.isFinished()) {
			if (mScroller.computeScrollOffset()) {
				int oldX = getScrollX();
				int oldY = getScrollY();
				int x = mScroller.getCurrX();
				int y = mScroller.getCurrY();
				if (oldX != x || oldY != y) {
					scrollTo(x, y);
				}
				// Keep on drawing until the animation has finished.
				invalidate();
			}
		}
	}

	/**
	 * 自动开关
	 */
	public void toggle() {
		int menuHeight = upView.getHeight();
		int oldScrollY = getScrollY();
		if (oldScrollY == 0) {
			smoothScrollTo(-menuHeight);
		} else if (oldScrollY == -menuHeight) {
			smoothScrollTo(menuHeight);
		}
	}

	/**
	 * 关闭
	 */
	public void close() {
		if (upView != null) {
			int menuHeight = upView.getHeight();
			int oldScrollY = getScrollY();
			if (oldScrollY == -menuHeight) {
				smoothScrollTo(menuHeight);
			}
		}
	}

	public void setUpView(View view) {
		this.upView = view;
	}

	public void smoothScrollTo(int dy) {
		int duration = time;
		int oldScrollX = getScrollX();
		mScroller.startScroll(oldScrollX, getScrollY(), oldScrollX, dy, duration);
		invalidate();
	}

	public int getScrollTime() {
		return time;
	}
}
