package cn.ffcs.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * <p>Title:    可控制滑动scrollview    </p>
 * <p>Description:                     </p>
 * <p>@author: zhangws                 </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-1-6           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class InterceptScrollView extends ScrollView {

	private float xDistance, yDistance, xLast, yLast;
	private InterceptType intercept = InterceptType.INTERCEPT_VERTICAL;

	public InterceptScrollView(Context context) {
		super(context);
		init();
	}

	public InterceptScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public InterceptScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public void init() {
		setVerticalScrollBarEnabled(false);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
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
			if (intercept == InterceptType.INTERCEPT_TRANSVERSE && xDistance < yDistance) {
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
}
