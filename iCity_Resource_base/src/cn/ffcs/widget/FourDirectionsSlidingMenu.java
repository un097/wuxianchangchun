package cn.ffcs.widget;

import cn.ffcs.widget.FourDirectionsSlidingView.Orientation;
import android.content.Context;

import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * <p>Title:  可以4个方向拉的控件     </p>
 * <p>Description:                     </p>
 * <p>@author: zhangwsh                </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-10-18           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class FourDirectionsSlidingMenu extends RelativeLayout {

	private FourDirectionsSlidingView mSlidingView;

	public FourDirectionsSlidingMenu(Context context) {
		super(context);
	}

	public FourDirectionsSlidingMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * 设置滑动方向
	 * @param orientation
	 */
	private void setSildingOrientation(Orientation orientation) {
		mSlidingView.setSlidingOrientation(orientation);
	}

	/**
	 * 设置被拉出的view和中间的view
	 * @param view
	 * @param orientation
	 */
	public void setViews(View coverView, View view, Orientation orientation) {
		LayoutParams lps = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		switch (orientation) {
		case UP_TO_DWON:
			lps.addRule(ALIGN_PARENT_TOP);
			break;
		case LEFT_TO_RIGHT:
			lps.addRule(ALIGN_PARENT_LEFT);
			break;
		case RIGHT_TO_LEFT:
			lps.addRule(ALIGN_PARENT_RIGHT);
			break;
		case DOWN_TO_UP:
			lps.addRule(ALIGN_PARENT_BOTTOM);
			break;
		default:
			break;
		}
		if (getChildCount() > 0) {
			removeAllViews();
		}
		addView(coverView, lps);
		mSlidingView = new FourDirectionsSlidingView(getContext());
		addView(mSlidingView);
		setSildingOrientation(orientation);
		mSlidingView.setCoverView(view);
	}

	/**
	 * 自动开关
	 */
	public void toggle() {
		if (mSlidingView != null) {
			mSlidingView.toggle();
		}
	}

	/**
	 * 回到原始位置
	 */
	public void close() {
		if (mSlidingView != null) {
			mSlidingView.close();
		}
	}
}
