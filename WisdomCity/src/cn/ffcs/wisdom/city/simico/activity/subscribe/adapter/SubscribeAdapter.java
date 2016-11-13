package cn.ffcs.wisdom.city.simico.activity.subscribe.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.ffcs.wisdom.city.R;
import cn.ffcs.wisdom.city.simico.kit.adapter.ListBaseAdapter;

public class SubscribeAdapter extends ListBaseAdapter {

	public SubscribeAdapter() {
	}

	@Override
	public int getCount() {
		return 10;// super.getCount();
	}

	@Override
	protected View getRealView(int position, View convertView, ViewGroup parent) {
		if (convertView == null || convertView.getTag() == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.simico_list_cell_subscribe, null);
		}
		return convertView;
	}
}
