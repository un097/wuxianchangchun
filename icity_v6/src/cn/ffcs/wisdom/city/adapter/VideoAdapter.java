package cn.ffcs.wisdom.city.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.ffcs.wisdom.city.entity.VideoEntity.VideoItem;
import cn.ffcs.wisdom.city.utils.CityImageLoader;
import cn.ffcs.wisdom.city.v6.R;

public class VideoAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<VideoItem> videoItemList = new ArrayList<VideoItem>();
	private CityImageLoader loader;

	public VideoAdapter(Context mContext, List<VideoItem> videoItemList) {
		this.videoItemList = videoItemList;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		loader = new CityImageLoader(mContext);
	}

	@Override
	public int getCount() {
		return videoItemList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return videoItemList.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		VideoHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.listview_item_row_nofavorite, null);
			holder = new VideoHolder();
			holder.mImageView = (ImageView) convertView.findViewById(R.id.listview_image);
			holder.mDescTv = (TextView) convertView.findViewById(R.id.listview_desc);
			holder.mTitleTv = (TextView) convertView.findViewById(R.id.listview_title);
			convertView.setTag(holder);
		} else {
			holder = (VideoHolder) convertView.getTag();
		}

		VideoItem item = videoItemList.get(position);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

		holder.mDescTv.setText(df.format(item.getAddTime())); // 原简介，改时间
		holder.mTitleTv.setText(item.getVideoName()); // 标题

		// if (contain(item)) {
		// holder.mTitleTv.setTextColor(Color.GRAY);
		// } else {
		// holder.mTitleTv.setTextColor(Color.BLACK);
		// }

		String url = item.getThumUrl();
		// holder.mImageView.setImageResource(R.drawable.default_image);
		// ImageLoader.getInstance().loadBitmap(holder.mImageView, url);
		loader.loadUrl(holder.mImageView, url);
		return convertView;
	}

	class VideoHolder {
		private ImageView mImageView;
		private TextView mTitleTv;
		private TextView mDescTv;
	}
}
