package cn.ffcs.wisdom.city.simico.kit.activity;

import android.os.Bundle;
import android.widget.ListView;
import cn.ffcs.wisdom.city.simico.ui.action.ScrollControl;

public class PListFragment extends PFragment implements
		ScrollControl {

	protected ListView _listview;

	public PListFragment() {
	}

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
	}

	@Override
	public void scrollToTop() {
		_listview.setSelection(0);
	}
}
