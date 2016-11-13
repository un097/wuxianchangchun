package cn.ffcs.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;

/**
 * <p>Title:                           </p>
 * <p>Description:                     </p>
 * <p>@author: zhangwsh                 </p>
 * <p>Copyright: Copyright (c) 2014    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2014-4-21           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class DragImageView extends ImageView {

	private boolean moving = false;// 移动中
	private long distanX;// 拖动距离X
	private long distanY;// 拖动距离Y
	private int right;// 右边距离
	private int bottom;// 底部距离
	private int lastX, lastY; // 记录移动的最后的位置

	public DragImageView(Context context) {
		super(context);
	}

	public DragImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		ViewParent parent= getParent();
		if (parent == null||!(parent instanceof ViewGroup)) {
			return super.onTouchEvent(event);
		}
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
//				statusBarHeight = AppHelper.getStatusBarHeight(mActivity);// 获取顶部状态栏高度
			lastX = (int) event.getRawX();
			lastY = (int) event.getRawY();
			distanX = 0;
			distanY = 0;
			break;
		case MotionEvent.ACTION_MOVE:
			int dx = (int) event.getRawX() - lastX;
			int dy = (int) event.getRawY() - lastY;
			distanX += Math.abs(dx);
			distanY += Math.abs(dy);
			int touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
			if (Math.abs(dx) < touchSlop && Math.abs(dy) < touchSlop) {
				if (!moving) {
					break;
				}
			}
			moving = true;
			right = ((ViewGroup)getParent()).getWidth() - getRight() - dx;
			bottom = ((ViewGroup)getParent()).getHeight() - getBottom() - dy;
			if (getLeft() <= 0) {
				if (dx < 0) {
					right = ((ViewGroup)getParent()).getWidth() - getWidth();
				}
			}

			if (getRight() > ((ViewGroup)getParent()).getWidth()) {
				right = 0;
			}

			if (getTop() <= 0) {
				if (dy < 0) {
					bottom = ((ViewGroup)getParent()).getHeight() - getHeight();
				}
			}

			if (getBottom() > ((ViewGroup)getParent()).getHeight()) {
				bottom = 0;
			}

			LayoutParams params = (LayoutParams) getLayoutParams();
			params.rightMargin = right;
			params.bottomMargin = bottom;
			setLayoutParams(params);
			// 将当前的位置再次设置
			lastX = (int) event.getRawX();
			lastY = (int) event.getRawY();
			break;
		case MotionEvent.ACTION_UP:
			moving = false;
		default:
			break;
		}
		return false;
	}

	/**
	 * 配置小图标
	 */
//	private void iconConfig() {
//		// 恢复上次小图标位置
//		smallIco.setOnTouchListener(new WebViewIconOnTouchListener());
//		LayoutParams params = (LayoutParams) smallIco.getLayoutParams();
//		String icoRight = SharedPreferencesUtil.getValue(mContext, Key.K_WEB_ICO_RIGHT);
//		String icoBottom = SharedPreferencesUtil.getValue(mContext, Key.K_WEB_ICO_BOTTOM);
//		if (!StringUtil.isEmpty(icoRight)) {
//			right = Integer.parseInt(icoRight);
//		} else {
//			right = 10;
//		}
//		if (!StringUtil.isEmpty(icoBottom)) {
//			bottom = Integer.parseInt(icoBottom);
//		} else {
//			bottom = 10;
//		}
//		params.rightMargin = right;
//		params.bottomMargin = bottom;
//		smallIco.setLayoutParams(params);
//		smallIco.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if (Math.abs(distanX) < smallIco.getWidth() / 2
//						&& Math.abs(distanY) < smallIco.getHeight() / 2) {
//					topBar.setVisibility(View.VISIBLE);
//					topBar.startAnimation(topAnimIn);
//					bottomBar.setVisibility(View.VISIBLE);
//					bottomBar.startAnimation(bottomAnimIn);
//					smallIco.setVisibility(View.GONE);
//					scorll = false;
//				}
//			}
//		});
//	}
}
