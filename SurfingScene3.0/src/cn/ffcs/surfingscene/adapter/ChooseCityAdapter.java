package cn.ffcs.surfingscene.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.ffcs.surfingscene.R;
import cn.ffcs.surfingscene.sqlite.AreaList;

public class ChooseCityAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<AreaList> arealist = new ArrayList<AreaList>();

	public ChooseCityAdapter(Context mContext, List<AreaList> list) {
		if (list != null) {
			arealist = list;
		} else {
			arealist = Collections.emptyList();
		}
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return arealist.size();
	}

	@Override
	public Object getItem(int position) {
		return arealist.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.glo_road_choose_city_item, null);
		}

		TextView cityname = (TextView) convertView.findViewById(R.id.name);
		if (arealist != null && arealist.size() > 0) {
			cityname.setText(arealist.get(position).areaName);
		}
		return convertView;
	}
}
