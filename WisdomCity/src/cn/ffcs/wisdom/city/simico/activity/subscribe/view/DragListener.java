package cn.ffcs.wisdom.city.simico.activity.subscribe.view;

import android.view.View;

public interface DragListener {

	/**
	 * 拖动时回调方法
	 * @param action
	 * @param moveView 移动的视图moveView的上方
	 * @param sourceView 被拖拽的原视图
	 * @return
	 */
	public abstract boolean onDrag(int action, View moveView, View sourceView);
}
