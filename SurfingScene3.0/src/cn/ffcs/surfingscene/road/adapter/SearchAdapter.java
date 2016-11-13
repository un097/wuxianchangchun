package cn.ffcs.surfingscene.road.adapter;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.ffcs.surfingscene.R;
import cn.ffcs.surfingscene.sqlite.SearchKey;

public class SearchAdapter extends BaseAdapter {
	private List<SearchKey> list;
	private LayoutInflater layoutInflater;

	public SearchAdapter(Context context) {
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setData(List<SearchKey> list) {
		if (list != null) {
			this.list = list;
		} else {
			this.list = Collections.emptyList();
		}
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder;
		SearchKey searchKey = list.get(position);
		if (convertView == null) {
			holder = new Holder();
			convertView = layoutInflater.inflate(R.layout.glo_search_list_item, parent, false);
			holder.searchWord = (TextView) convertView.findViewById(R.id.search_word);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.searchWord.setText(searchKey.keyWord);
		return convertView;
	}

	final static class Holder {
		TextView searchWord;
	}
}
