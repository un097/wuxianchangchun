package cn.ffcs.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Scroller;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.CommonUtils;

/**
 * <p>Title:        左右式抽屉控件      </p>
 * <p>Description:                     </p>
 * <p>@author: zhangws                 </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-2-5           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class SlidingMenuGroupView extends ViewGroup {

	public static int SHOW_LEFT = 0;
	public static int SHOW_CENTER = 1;

	private View leftView;
	private View centerView;
	private View childView;
	private LinearLayout mContainer;
	private float mLastMotionX;
	private float mLastMotionY;
	private VelocityTracker mVelocityTracker;
	private int mTouchSlop;
	private boolean first = true;
	private boolean isOpen = false;
	private int showView = SHOW_LEFT;
	private boolean allow = true;
	private int SPEED = 700;
	private Scroller mScroller;

	/**
	 * 屏蔽左边横滑
	 */
	public static int INTERCEPT_LEFT_TRANSVERSE = 4;
	private InterceptType intercept = InterceptType.INTERCEPT_TRANSVERSE;

	public SlidingMenuGroupView(Context context) {
		super(context);
		init();
	}

	public SlidingMenuGroupView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SlidingMenuGroupView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	@SuppressWarnings("deprecation")
	private void init() {
		setHorizontalScrollBarEnabled(false);
		setFadingEdgeLength(0);
		mScroller = new Scroller(getContext());
		mContainer = new LinearLayout(getContext());
		mContainer.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mContainer.measure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int height = b - t;
		mContainer.layout(0, 0, leftView.getWidth() + centerView.getWidth(), height);
		if (showView == SHOW_CENTER) {
			if (first) {
				scrollTo(leftView.getWidth(), 0);
				if (childView != null) {
					((InterceptViewPager) childView).setNoTouch(false);
				}
				first = false;
			}
		}
	}

	/**
	 * 这是是显示左边还是中间
	 * @param showView
	 */
	public void setShowMenu(int showView) {
		this.showView = showView;
	}

	/**
	 * 这只左边布局
	 * @param view
	 */
	@SuppressWarnings("deprecation")
	public void setLeftView(View view) {
		leftView = view;
		LayoutParams behindParams = new LayoutParams(AppHelper.getScreenWidth(getContext())
				- CommonUtils.convertDipToPx(getContext(), 40), LayoutParams.FILL_PARENT);
		mContainer.addView(leftView, behindParams);
	}

	/**
	 * 设置中间布局
	 * @param view
	 */
	@SuppressWarnings("deprecation")
	public void setCenterView(View view) {
		centerView = view;
		LayoutParams behindParams = new LayoutParams(AppHelper.getScreenWidth(getContext()),
				LayoutParams.FILL_PARENT);
		mContainer.addView(centerView, behindParams);
	}

	/**
	 * 设置会与其冲突的子View
	 * @param view
	 */
	public void setChildView(View view) {
		this.childView = view;
	}

	/**
	 * 初始化控件
	 */
	public void initView() {
		addView(mContainer);
		if (childView != null) {
			((InterceptViewPager) childView).setNoTouch(true);
		}
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
	}

	/**
	 * 自动判断左右滑动
	 */
	public void showOrHideView() {
		if (getScrollX() >= leftView.getWidth() / 2) {
			scrollToStart();
		} else if (getScrollX() < leftView.getWidth() / 2) {
			scrollToCenter();
		}
	}

	/**
	 * 关闭左边
	 */
	public void scrollToCenter() {
		smoothScrollTo(leftView.getWidth());
	}

	/**
	 * 打开左边
	 */
	public void scrollToStart() {
		smoothScrollTo(-leftView.getWidth());
	}

	/**
	 * 设置手势滑动屏蔽
	 * @param intercept
	 */
	public void setInterceptScroll(InterceptType intercept) {
		this.intercept = intercept;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (!allow) {
			return false;
		}
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);

		final int action = ev.getAction();
		final float x = ev.getX();
		final float y = ev.getY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			mLastMotionX = x;
			mLastMotionY = y;

			if (getScrollX() == getLeftViewWidth() && mLastMotionX > getLeftViewWidth()) {
				return false;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (getScrollX() < 0) {
				break;
			}
			enableChildrenCache();
			final float deltaX = mLastMotionX - x;
			mLastMotionX = x;
			float oldScrollX = getScrollX();
			float scrollX = oldScrollX + deltaX;
			if (deltaX > 0 && oldScrollX > 0) { // left view
				final float leftBound = 0;
				final float rightBound = getLeftViewWidth();
				if (scrollX < leftBound) {
					scrollX = leftBound;
				} else if (scrollX > rightBound) {
					scrollX = rightBound;
				}
			}
			if (scrollX < 0) {
				scrollX = 0;
			}
			scrollTo((int) scrollX, getScrollY());
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			final VelocityTracker velocityTracker = mVelocityTracker;
			velocityTracker.computeCurrentVelocity(1000);
			float velocityX = velocityTracker.getXVelocity();
			int nowScrollX = getScrollX();
			int dx = 0;
			if (nowScrollX > 0) {
				if (velocityX < -SPEED) {
					dx = getLeftViewWidth() - nowScrollX;
				} else if (velocityX > SPEED) {
					dx = -nowScrollX;
				} else if (nowScrollX > getLeftViewWidth() / 2) {
					dx = getLeftViewWidth() - nowScrollX;
				} else if (nowScrollX <= getLeftViewWidth() / 2) {
					dx = -nowScrollX;
				}
			}
//			else {
//				if (velocityX > SPEED) {
//					dx = -getMenuViewWidth() - oldScrollX;
//				} else if (velocityX < -SPEED) {
//					dx = -oldScrollX;
//				} else if (oldScrollX < -getMenuViewWidth() / 2) {
//					dx = -getMenuViewWidth() - oldScrollX;
//				} else if (oldScrollX >= -getMenuViewWidth() / 2) {
//					dx = -oldScrollX;
//				}
//			}
			smoothScrollTo(dx);
			clearChildrenCache();
			if (mVelocityTracker != null) {
				mVelocityTracker.recycle();
				mVelocityTracker = null;
			}
			break;
		}
		return true;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		final float x = ev.getX();
		final float y = ev.getY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mLastMotionX = x;
			mLastMotionY = y;
			break;
		case MotionEvent.ACTION_MOVE:
			final float dx = x - mLastMotionX;
			final float xDiff = Math.abs(dx);
			final float yDiff = Math.abs(y - mLastMotionY);
			if (isOpen) {
				intercept = InterceptType.INTERCEPT_TRANSVERSE;
			}
			if (intercept == InterceptType.INTERCEPT_TRANSVERSE) {
				if (xDiff > mTouchSlop && xDiff > yDiff) {
					mLastMotionX = x;
					mLastMotionY = y;
					return true;
				}
			} else if (intercept == InterceptType.INTERCEPT_VERTICAL) {
				if (yDiff > mTouchSlop && xDiff < yDiff) {
					mLastMotionX = x;
					mLastMotionY = y;
					return true;
				}
			} else if (intercept == InterceptType.INTERCEPT_ALL) {
				if (yDiff > mTouchSlop || xDiff > mTouchSlop) {
					mLastMotionX = x;
					mLastMotionY = y;
					return true;
				}
			} else if (intercept == InterceptType.INTERCEPT_LEFT_TRANSVERSE) {
				if (xDiff > mTouchSlop && xDiff > yDiff && dx > 0) {
					mLastMotionX = x;
					mLastMotionY = y;
					return true;
				}
			} else if (intercept == InterceptType.INTERCEPT_NULL) {
				return false;
			}
			break;
		}
		return false;
	}
	
//	@Override
//	public void scrollTo(int x, int y) {
//		super.scrollTo(x, y);
//		postInvalidate();
//	}

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
//				// Keep on drawing until the animation has finished.
				invalidate();
			} else {
				clearChildrenCache();
			}
		} else {
			clearChildrenCache();
		}
	}

	private void clearChildrenCache() {
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View layout = (View) getChildAt(i);
			layout.setDrawingCacheEnabled(false);
		}
	}

	private void enableChildrenCache() {
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View layout = (View) getChildAt(i);
			layout.setDrawingCacheEnabled(true);
		}
	}

	/**
	 * 设置是否允许滑动
	 * @param allow
	 */
	public void setAllowScroll(boolean allow) {
		this.allow = allow;
	}

	/**
	 * 获取是否打开
	 */
	public boolean getIsOpen() {
		return isOpen;
	}

	/**
	 * 获取左边栏宽度
	 * @return
	 */
	private int getLeftViewWidth() {
		if (leftView == null) {
			return 0;
		}
		return leftView.getWidth();
	}
	
	/**
	 * 平滑滑动
	 * @param dx
	 */
	private void smoothScrollTo(int dx) {
		if (dx < 0) {
			isOpen = true;
			((InterceptViewPager) childView).setNoTouch(true);
		} else if (dx > 0) {
			isOpen = false;
			((InterceptViewPager) childView).setNoTouch(false);
		}
		int duration = 500;
		int oldScrollX = getScrollX();
		mScroller.startScroll(oldScrollX, getScrollY(), dx, getScrollY(), duration);
		invalidate();
	}
}
