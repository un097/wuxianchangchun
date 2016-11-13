package cn.ffcs.wisdom.city.simico.activity.home.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

public class ListenerScrollView extends HorizontalScrollView {

	private ScrollViewListener mListener = null;

	public ListenerScrollView(Context context) {
		super(context);
	}

	public ListenerScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ListenerScrollView(Context context, AttributeSet attrs, int i) {
		super(context, attrs, i);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (mListener != null)
			mListener.onScrollChanged(this, l, t, oldl, oldt);
	}

	public void setScrollViewListener(ScrollViewListener listener) {
		mListener = listener;
	}
}
