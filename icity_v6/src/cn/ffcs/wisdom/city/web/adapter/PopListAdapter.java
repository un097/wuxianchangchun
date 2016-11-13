package cn.ffcs.wisdom.city.web.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.city.web.entity.PopEntity;

public class PopListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<PopEntity> popList = new ArrayList<PopEntity>();

	public PopListAdapter(Context mContext, List<PopEntity> popList) {
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.popList = popList;
	}

	@Override
	public int getCount() {
		return popList.size();
	}

	@Override
	public Object getItem(int position) {
		return popList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		PopEntity entity = popList.get(position);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.listview_item_web_popwin, null);
		}
		ImageView img = (ImageView) convertView.findViewById(R.id.pop_icon);
		img.setImageResource(entity.getIconResId());
		TextView tx = (TextView) convertView.findViewById(R.id.pop_text);
		tx.setText(entity.getTextResId());
		return convertView;
	}
}
