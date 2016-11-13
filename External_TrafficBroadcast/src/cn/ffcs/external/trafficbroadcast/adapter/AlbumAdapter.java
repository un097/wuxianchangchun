package cn.ffcs.external.trafficbroadcast.adapter;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Thumbnails;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import cn.ffcs.external.trafficbroadcast.entity.PhotoAibum;
import cn.ffcs.external.trafficbroadcast.entity.PhotoItem;
import cn.ffcs.external.trafficbroadcast.tool.Constants;
import cn.ffcs.external.trafficbroadcast.tool.Util;
import cn.ffcs.external.trafficbroadcast.view.PhotoGridItem;

import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class AlbumAdapter extends BaseAdapter {
	private Context context;
	private PhotoAibum aibum;
	private ArrayList<PhotoItem> gl_arr;
	private ImageLoadingListener animateFirstListener = new Util.AnimateFirstDisplayListener();

	public AlbumAdapter(Context context, PhotoAibum aibum,
			ArrayList<PhotoItem> gl_arr) {
		this.context = context;
		this.aibum = aibum;
		this.gl_arr = gl_arr;
	}

	@Override
	public int getCount() {
		if (gl_arr == null) {
			return aibum.getBitList().size();
		} else {
			return gl_arr.size();
		}

	}

	@Override
	public PhotoItem getItem(int position) {
		if (gl_arr == null) {
			return aibum.getBitList().get(position);
		} else {
			return gl_arr.get(position);
		}

	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		PhotoGridItem item;
		if (convertView == null) {
			item = new PhotoGridItem(context);
			item.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT
					));
		} else {
			item = (PhotoGridItem) convertView;
		}
		// 通过ID 加载缩略图
		if (gl_arr == null) {
			Constants.imageLoader.displayImage("file://"
					+ aibum.getBitList().get(position).getPath(),
					item.getImageView(), Constants.image_display_options,
					animateFirstListener);
			boolean flag = aibum.getBitList().get(position).isSelect();
			item.setChecked(flag);
		} else {
			Constants.imageLoader.displayImage("file://"
					+ gl_arr.get(position).getPath(), item.getImageView(),
					Constants.image_display_options, animateFirstListener);
		}
		return item;
	}
}
