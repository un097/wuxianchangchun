package cn.ffcs.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

/**
 * <p>Title:不滚动的ExpandableListView          </p>
 * <p>Description: 
 * </p>
 * <p>@author: Leo                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-6-18             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class ExpandNoScrollListView extends ExpandableListView {

	public ExpandNoScrollListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ExpandNoScrollListView(Context context) {
		super(context);
	}

	public ExpandNoScrollListView(Context context, AttributeSet attrs, int defStyle) {

		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

}
