package cn.ffcs.changchuntv.activity.home.traffic;

import java.util.ArrayList;

import cn.ffcs.wisdom.city.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TrafficAdapter extends BaseAdapter {

	private ArrayList<TrafficEntry> data;

	private int mInitWidth;

	private TrafficDelegate trafficDelegate;

	public interface TrafficDelegate {
		public void onTrafficClick(TrafficEntry entry);
	}

	public TrafficAdapter(TrafficDelegate trafficDelegate) {
		super();
		this.trafficDelegate = trafficDelegate;
	}

	public void setData(ArrayList<TrafficEntry> data) {
		this.data = data;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return (data != null && data.size() > 0) ? data.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		if (convertView == null || convertView.getTag() == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.simico_list_cell_service, null);
			vh = new ViewHolder();
			vh.icon = (ImageView) convertView.findViewById(R.id.iv_icon);
			vh.cover = (ImageView) convertView.findViewById(R.id.iv_cloer);
			vh.name = (TextView) convertView.findViewById(R.id.tv_name);

			RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) vh.icon
					.getLayoutParams();
			if (p != null && mInitWidth == 0) {
				mInitWidth = p.width;
			}

			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) vh.icon
				.getLayoutParams();
		RelativeLayout.LayoutParams p2 = (RelativeLayout.LayoutParams) vh.cover
				.getLayoutParams();

		p.height = mInitWidth;
		p.width = mInitWidth;
		p2.height = mInitWidth;
		p2.width = mInitWidth;

		final TrafficEntry entry = data.get(position);

		vh.cover.setImageBitmap(null);
		vh.icon.setImageResource(entry.getResId());
		vh.name.setText(entry.getName());

		convertView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (trafficDelegate != null) {
					trafficDelegate.onTrafficClick(entry);
				}
			}
		});

		return convertView;
	}

	static class ViewHolder {
		ImageView icon, cover;
		TextView name;
	}

}
