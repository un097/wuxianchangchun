package cn.ffcs.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class FourDirectionsSlidingView extends LinearLayout {

	private Scroller mScroller;
	private View coverView;
	private int animationTime = 500;
	private Orientation orientation = Orientation.LEFT_TO_RIGHT;

	/**
	 * 方向类型
	 */
	public enum Orientation {
		UP_TO_DWON, // 上往下拉
		LEFT_TO_RIGHT, // 左往右拉
		RIGHT_TO_LEFT, // 右往左拉
		DOWN_TO_UP;// 下往上拉
	}

	public FourDirectionsSlidingView(Context context) {
		super(context);
		init();
	}

	public FourDirectionsSlidingView(Context context, AttributeSet attrs) {
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
		int coverViewWidth = coverView.getWidth();
		int coverViewHeight = coverView.getHeight();
		int dx = 0;
		int dy = 0;
		int scrollX = getScrollX();
		int scrollY = getScrollY();
		switch (orientation) {
		case UP_TO_DWON:
			if (scrollY == 0) {
				dx = 0;
				dy = -coverViewHeight;
			} else if (scrollY == -coverViewHeight) {
				dx = 0;
				dy = coverViewHeight;
			}
			break;
		case DOWN_TO_UP:
			if (scrollY == 0) {
				dx = 0;
				dy = coverViewHeight;
			} else if (scrollY == coverViewHeight) {
				dx = 0;
				dy = -coverViewHeight;
			}
			break;
		case LEFT_TO_RIGHT:
			if (scrollX == 0) {
				dx = -coverViewWidth;
				dy = 0;
			} else if (scrollX == -coverViewWidth) {
				dx = coverViewWidth;
				dy = 0;
			}
			break;
		case RIGHT_TO_LEFT:
			if (scrollX == 0) {
				dx = coverViewWidth;
				dy = 0;
			} else if (scrollX == coverViewWidth) {
				dx = -coverViewWidth;
				dy = 0;
			}
			break;
		default:
			break;
		}
		smoothScrollBy(dx, dy);
	}

	/**
	 * 关闭
	 */
	public void close() {
		if (coverView != null) {
			smoothScrollTo(0, 0);
		}
	}

	/**
	 * 设置被覆盖的控件
	 * @param view
	 */
	public void setCoverView(View view) {
		this.coverView = view;
	}

	/**
	 * 设置方向
	 * @param orientation
	 */
	public void setSlidingOrientation(Orientation orientation) {
		this.orientation = orientation;
	}

	/**
	 * 滑动到指定点
	 * @param x
	 * @param y
	 */
	public void smoothScrollTo(int x, int y) {
		int duration = animationTime;
		int oldScrollX = getScrollX();
		int oldScrollY = getScrollY();
		mScroller.startScroll(oldScrollX, oldScrollY, x - oldScrollX, y - oldScrollY, duration);
		invalidate();
	}

	/**
	 * 滑动一段距离
	 * @param dx
	 * @param dy
	 */
	public void smoothScrollBy(int dx, int dy) {
		int duration = animationTime;
		mScroller.startScroll(getScrollX(), getScrollY(), dx, dy, duration);
		invalidate();
	}

	/**
	 * 获取滑动动作时间
	 * @return
	 */
	public int getScrollTime() {
		return animationTime;
	}

	/**
	 * 获取拉动方向
	 * @return
	 */
	public Orientation getSlidingOrientation() {
		return orientation;
	}
}
