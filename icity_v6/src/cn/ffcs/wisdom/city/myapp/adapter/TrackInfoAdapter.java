package cn.ffcs.wisdom.city.myapp.adapter;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.ffcs.wisdom.city.sqlite.model.AppItem;
import cn.ffcs.wisdom.city.sqlite.model.TrackInfo;
import cn.ffcs.wisdom.city.sqlite.service.AppService;
import cn.ffcs.wisdom.city.utils.CityImageLoader;
import cn.ffcs.wisdom.city.v6.R;

public class TrackInfoAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mInflater;
	private List<TrackInfo> mData;
	private CityImageLoader loader;

	public TrackInfoAdapter(Context context) {
		this.mContext = context;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		loader = new CityImageLoader(mContext);
	}

	public void setData(List<TrackInfo> mData) {
		if (mData == null) {
			this.mData = Collections.emptyList();
		}

		this.mData = mData;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ItemHolder holder = new ItemHolder();
		if (convertView == null) {
			convertView = mInflater.inflate(
					R.layout.gridview_item_recent_record, null);
			holder.mItem = (RelativeLayout) convertView.findViewById(R.id.bg);
			holder.mItemIv = (ImageView) convertView
					.findViewById(R.id.gird_item_indicator);
			holder.mItemTv = (TextView) convertView
					.findViewById(R.id.gird_item_content);
			holder.mItemAdd = (ImageView) convertView
					.findViewById(R.id.fragment_trackinfo_gridview_item_imageview);

			convertView.setTag(holder);
		} else {
			holder = (ItemHolder) convertView.getTag();
		}

		TrackInfo trackInfo = mData.get(position);

		if (trackInfo != null) {
			loader.loadUrl(holder.mItemIv, trackInfo.getV6Icon());
			holder.mItemTv.setText(trackInfo.getMenuName());
		}

		boolean isFav = AppService.getInstance(mContext).isExist(
				AppItem.converter(trackInfo));
		if (isFav)
			holder.mItemAdd.setVisibility(View.VISIBLE);
		else
			holder.mItemAdd.setVisibility(View.GONE);

		return convertView;
	}

	static class ItemHolder {
		public RelativeLayout mItem;
		public ImageView mItemIv;
		public TextView mItemTv;
		public ImageView mItemAdd;
	}

}
