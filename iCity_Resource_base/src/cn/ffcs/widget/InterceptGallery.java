package cn.ffcs.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Gallery;

/**
 * <p>Title:     首页控件gallery       </p>
 * <p>Description:                     </p>
 * <p>@author: zhangws                 </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-2-6           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class InterceptGallery extends Gallery {

	private float xDistance, yDistance, xLast, yLast;
	private InterceptType intercept = InterceptType.INTERCEPT_DEFAULT;
	private View parent;
	private boolean touch = false;

	public InterceptGallery(Context context) {
		super(context);
	}

	public InterceptGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			int scrollX = getSelectedItemPosition();
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
			xDistance += Math.abs(curX - xLast);
			yDistance += Math.abs(curY - yLast);
			xLast = curX;
			yLast = curY;
			if (intercept == InterceptType.INTERCEPT_TRANSVERSE && xDistance < yDistance + 50) {
				return false;
			}
			if (intercept == InterceptType.INTERCEPT_VERTICAL && xDistance > yDistance) {
				return false;
			}
			if (intercept == InterceptType.INTERCEPT_NULL) {
				return false;
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
	public boolean onTouchEvent(MotionEvent arg0) {
		if (touch) {
			return true;
		}
		return super.onTouchEvent(arg0);
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		int keyCode;
		if (isScrollingLeft(e1, e2)) {
			keyCode = KeyEvent.KEYCODE_DPAD_LEFT;
		} else {
			keyCode = KeyEvent.KEYCODE_DPAD_RIGHT;
		}
		onKeyDown(keyCode, null);
		return true;
	}

	private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) {
		return e2.getX() > e1.getX();
	}
}
