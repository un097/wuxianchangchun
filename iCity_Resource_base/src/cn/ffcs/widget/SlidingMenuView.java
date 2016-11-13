package cn.ffcs.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
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
public class SlidingMenuView extends HorizontalScrollView {

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
	private int SPEED = 500;
	private InterceptViewPager childViewPager;;
	private InterceptType intercept = InterceptType.INTERCEPT_NULL;

	public SlidingMenuView(Context context) {
		super(context);
		init();
	}

	public SlidingMenuView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SlidingMenuView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		setHorizontalScrollBarEnabled(false);
		setFadingEdgeLength(0);
		mContainer = new LinearLayout(getContext());
		mContainer.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (showView == SHOW_CENTER) {
			if (first) {
				scrollTo(leftView.getWidth(), 0);
				if (childViewPager != null) {
					childViewPager.setNoTouch(false);
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
	 * 设置左边布局
	 * @param view
	 */
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
		if (childView instanceof InterceptViewPager) {
			childViewPager = (InterceptViewPager) childView;
		}
	}

	/**
	 * 初始化控件
	 */
	public void initView() {
		addView(mContainer);
		if (childViewPager != null) {
			childViewPager.setNoTouch(true);
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
		smoothScrollTo(leftView.getWidth(), 0);
		isOpen = false;
		if (childViewPager != null) {
			childViewPager.setNoTouch(false);
		}
	}

	/**
	 * 打开左边
	 */
	public void scrollToStart() {
		smoothScrollTo(0, 0);
		isOpen = true;
		if (childViewPager != null) {
			childViewPager.setNoTouch(true);
		}
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
			mLastMotionX = x;
			mLastMotionY = y;
			break;
		case MotionEvent.ACTION_MOVE:
			float deltaX = mLastMotionX - x;
			mLastMotionX = x;
			mLastMotionY = y;
			if (getScrollX() <= 0 && deltaX <= 0) {
				break;
			} else if (getScrollX() >= leftView.getWidth() && deltaX >= 0) {
				break;
			}
			scrollBy((int) deltaX, getScrollY());
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			final VelocityTracker velocityTracker = mVelocityTracker;
			velocityTracker.computeCurrentVelocity(1000);
			float velocityX = velocityTracker.getXVelocity();
			int newScrollX = getScrollX();
			if (velocityX < -SPEED) {
				scrollToCenter();
			} else if (velocityX > SPEED) {
				scrollToStart();
			} else if (newScrollX < leftView.getWidth() / 2) {
				scrollToStart();
			} else if (newScrollX >= leftView.getWidth() / 2) {
				scrollToCenter();
			}

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
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			if (childViewPager != null) {
				childViewPager.setIntercept(InterceptType.INTERCEPT_TRANSVERSE);
			}
			break;
		}

		return false;
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

	@Override
	public void scrollTo(int x, int y) {// 防止与editText焦点混乱问题
		if (childViewPager != null) {
			if (childViewPager.getCurrentItem() == 0 || isOpen) {
				super.scrollTo(x, y);
			}
		} else {
			super.scrollTo(x, y);
		}
	}
}
