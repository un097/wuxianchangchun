package cn.ffcs.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * <p>Title:   解决viewpager与其他控件横竖向冲突问题       </p>
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
public class InterceptViewPager extends ViewPager {

	private float xDistance, yDistance, xLast, yLast;
	private InterceptType intercept = InterceptType.INTERCEPT_TRANSVERSE;
	private View parent;
	private boolean touch = false;

//	private ViewGroup indexView;
//	private int chlidWidth;
//	private Handler handler;

	public InterceptViewPager(Context context) {
		super(context);
	}

	public InterceptViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (touch) {
			return true;
		}
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			int scrollX = getCurrentItem();
			if (parent != null && parent instanceof SlidingMenuGroupView) {
				SlidingMenuGroupView slidingMenu = (SlidingMenuGroupView) parent;
				if (scrollX == 0) {
					slidingMenu.setInterceptScroll(InterceptType.INTERCEPT_LEFT_TRANSVERSE);
				} else {
					slidingMenu.setInterceptScroll(InterceptType.INTERCEPT_NULL);
				}
			}
			
			if (parent != null && parent instanceof SlidingMenuView) {
				SlidingMenuView slidingMenu = (SlidingMenuView) parent;
				if (scrollX == 0) {
					slidingMenu.setInterceptScroll(InterceptType.INTERCEPT_LEFT_TRANSVERSE);
				} else {
					slidingMenu.setInterceptScroll(InterceptType.INTERCEPT_NULL);
				}
			}
			xDistance = yDistance = 0f;
			xLast = ev.getX();
			yLast = ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			final float curX = ev.getX();
			final float curY = ev.getY();
			final float dx = curX - xLast;
			xDistance += Math.abs(curX - xLast);
			yDistance += Math.abs(curY - yLast);
			xLast = curX;
			yLast = curY;
			if (intercept == InterceptType.INTERCEPT_TRANSVERSE && xDistance < yDistance) {
				return false;
			}
			if (intercept == InterceptType.INTERCEPT_VERTICAL && xDistance > yDistance) {
				return false;
			}
			if (intercept == InterceptType.INTERCEPT_RIGHT_TRANSVERSE && xDistance > yDistance && dx > 0) {
				return false;
			}
			if (intercept == InterceptType.INTERCEPT_NULL) {
				return false;
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			setIntercept(InterceptType.INTERCEPT_TRANSVERSE);
			if (parent != null && parent instanceof SlidingMenuGroupView) {
				SlidingMenuGroupView slidingMenu = (SlidingMenuGroupView) parent;
				slidingMenu.setInterceptScroll(InterceptType.INTERCEPT_LEFT_TRANSVERSE);
			}
			if (parent != null && parent instanceof SlidingMenuView) {
				SlidingMenuView slidingMenu = (SlidingMenuView) parent;
				slidingMenu.setInterceptScroll(InterceptType.INTERCEPT_LEFT_TRANSVERSE);
			}
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}

	/**
	 * 设置触摸事件的传递方式
	 * @param intercept
	 */
	public void setIntercept(InterceptType intercept) {
		this.intercept = intercept;
	}

	public void setParent(View view) {
		this.parent = view;
	}

	public void setNoTouch(boolean touch) {
		this.touch = touch;
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		if (touch) {
			return true;
		}
		return super.onTouchEvent(e);
	}
	
// 下方为随手势滑动
//	@Override
//	protected void onScrollChanged(final int l, final int t, int oldl, int oldt) {
//		super.onScrollChanged(l, t, oldl, oldt);
//		if (handler == null) {
//			handler = new Handler();
//		}
//		handler.post(new Runnable() {
//
//			@Override
//			public void run() {
//				if (indexView != null) {
//					if (getChildCount() > 0 && ((ViewGroup) indexView).getChildCount() > 0) {
//						chlidWidth = getChildAt(0).getWidth();
//						if (chlidWidth != 0) {
//							int indexViewWidth = ((ViewGroup) indexView).getChildAt(0).getWidth();
//							indexView.scrollTo(-l / (chlidWidth / indexViewWidth), t);
//						}
//					}
//				}
//			}
//		});
//	}
//
//	public void setIndexView(ViewGroup view) {
//		this.indexView = view;
//	}
}
