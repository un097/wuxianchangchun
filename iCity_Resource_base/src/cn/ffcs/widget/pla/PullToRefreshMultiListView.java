package cn.ffcs.widget.pla;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import cn.ffcs.config.R;
import cn.ffcs.widget.EmptyViewMethodAccessor;

public class PullToRefreshMultiListView extends PullToRefreshPLAAdapterViewBase<MultiColumnListView> {

	class InternalMultiColumnListView extends MultiColumnListView implements EmptyViewMethodAccessor {

		public InternalMultiColumnListView(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		public void setEmptyView(View emptyView) {
			PullToRefreshMultiListView.this.setEmptyView(emptyView);
		}

		@Override
		public void setEmptyViewInternal(View emptyView) {
			super.setEmptyView(emptyView);
		}

		@Override
		public ContextMenuInfo getContextMenuInfo() {
			return super.getContextMenuInfo();
		}

	}

	public PullToRefreshMultiListView(Context context) {
		super(context);
	}

	public PullToRefreshMultiListView(Context context, int mode) {
		super(context, mode);
	}

	public PullToRefreshMultiListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected final MultiColumnListView createRefreshableView(Context context, AttributeSet attrs) {
		MultiColumnListView ml = new InternalMultiColumnListView(context, attrs);
		// Use Generated ID (from res/values/ids.xml)
		ml.setId(R.id.mulit_list);
		return ml;
	}

	@Override
	public ContextMenuInfo getContextMenuInfo() {
		return ((InternalMultiColumnListView) getRefreshableView()).getContextMenuInfo();
	}
}
