package cn.ffcs.widget;

import cn.ffcs.config.R;
import cn.ffcs.wisdom.tools.CommonUtils;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * <p>Title:  自定义横向滚动指示条                  </p>
 * <p>Description:                     </p>
 * <p>@author: zhangwsh                </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-8-27           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class HorizontalIndexScrollBar extends RelativeLayout {

	private int viewCount;
	private View scrollIndexView;
	private int width;

	public HorizontalIndexScrollBar(Context context) {
		super(context);
	}

	public HorizontalIndexScrollBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public HorizontalIndexScrollBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * init the view
	 */
	private void init() {
		int viewHeight = CommonUtils.convertDipToPx(getContext(), 2);
		getLayoutParams().height = viewHeight;
		setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.white_30));
		scrollIndexView = new View(getContext());
		scrollIndexView.setLayoutParams(new LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
				viewHeight));
		scrollIndexView.setBackgroundDrawable(getContext().getResources().getDrawable(
				R.drawable.yellow_normal));
		addView(scrollIndexView);
		if (viewCount > 0) {
			int mo = (width % viewCount);
			getLayoutParams().width = width + mo;
			scrollIndexView.getLayoutParams().width = getLayoutParams().width / viewCount;
		}
	}

	public void initViewPager(ViewPager viewPager) {
		if (viewPager == null) {
			return;
		}
		PagerAdapter adapter = viewPager.getAdapter();
		if (adapter != null) {
			viewCount = adapter.getCount();
		}
		init();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		width=getWidth();
		super.onLayout(changed, l, t, r, b);
	}

	/**
	 * set scrollbar height
	 * @param height
	 */
	public void setScrollBarHeight(int height) {
		getLayoutParams().height = height;
	}

}
