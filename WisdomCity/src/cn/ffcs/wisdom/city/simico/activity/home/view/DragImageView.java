package cn.ffcs.wisdom.city.simico.activity.home.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

/**
 * <p>Title:     可拖动ImageView        </p>
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
	private int lastX, lastY; // 记录移动的最后的位置

	public DragImageView(Context context) {
		super(context);
		init();
	}

	public DragImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	void init() {
		setClickable(true);
		setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				ViewParent parent = getParent();
				if (parent == null || !(parent instanceof ViewGroup)) {
					System.out.println("no parent");
					return true;
				}
				int action = event.getAction();
				switch (action) {
				case MotionEvent.ACTION_DOWN:
					moving = false;
					lastX = (int) event.getRawX();
					lastY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					int dx = (int) event.getRawX() - lastX;
					int dy = (int) event.getRawY() - lastY;
					int touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
					if (Math.abs(dx) < touchSlop && Math.abs(dy) < touchSlop) {
						if (!moving) {
							break;
						}
					}
					moving = true;
					if (getLeft() <= 0) {
						if (dx < 0) {
							dx = 0;
						}
					}

					if (getRight() >= ((ViewGroup) getParent()).getWidth()) {
						if (dx > 0) {
							dx = 0;
						}
					}

					if (getTop() <= 0) {
						if (dy < 0) {
							dy = 0;
						}
					}

					if (getBottom() > ((ViewGroup) getParent()).getHeight()) {
						if (dy > 0) {
							dy = 0;
						}
					}
					setFrame(getLeft() + dx, getTop() + dy, getRight() + dx, getBottom() + dy);
					// 将当前的位置再次设置
					lastX = (int) event.getRawX();
					lastY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_UP:
					break;
				default:
					break;
				}
				return false;
			}
		});
	}

	@Override
	public boolean performClick() {
		if (!moving) {
			return super.performClick();
		}
		return false;
	}
}
