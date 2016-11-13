package cn.ffcs.external.trafficbroadcast.adapter;

import java.util.List;

import com.example.external_trafficbroadcast.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import cn.ffcs.external.trafficbroadcast.entity.PhotoAibum;
import cn.ffcs.external.trafficbroadcast.tool.Constants;
import cn.ffcs.external.trafficbroadcast.tool.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Thumbnails;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;

/**
 * 相册目录列表适配器
 * 
 * @author daizhq
 * 
 * @date 2014.12.25
 */
public class PhotoAibumAdapter extends BaseAdapter {
	private List<PhotoAibum> aibumList;
	private Context context;
	private ViewHolder holder;

	private ImageLoadingListener animateFirstListener = new Util.AnimateFirstDisplayListener();

	public PhotoAibumAdapter(List<PhotoAibum> list, Context context) {
		this.aibumList = list;
		this.context = context;
	}

	@Override
	public int getCount() {
		return aibumList.size();
	}

	@Override
	public Object getItem(int position) {
		return aibumList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = (LinearLayout) LayoutInflater.from(context).inflate(
					R.layout.item_photo_list, null);
			holder = new ViewHolder();
			holder.iv = (ImageView) convertView
					.findViewById(R.id.photoalbum_item_image);
			holder.tv = (TextView) convertView
					.findViewById(R.id.photoalbum_item_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		PhotoAibum photoAibum = aibumList.get(position);
		/** 通过ID 获取缩略图 */
		// Bitmap bitmap =
		// MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(),
		// aibumList.get(position).getBitmap(), Thumbnails.MICRO_KIND, null);
		// holder.iv.setImageBitmap(bitmap);

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 1)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.memoryCache(new LruMemoryCache(2 * 1024 * 1024))
				.memoryCacheSize(2 * 1024 * 1024)
				.discCacheSize(50 * 1024 * 1024).discCacheFileCount(100)
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);

		Constants.imageLoader.displayImage("file://" + photoAibum.getPath(),
				holder.iv, Constants.image_display_options,
				animateFirstListener);
		holder.tv.setText(aibumList.get(position).getName() + " ( "
				+ aibumList.get(position).getCount() + " )");
		return convertView;
	}

	static class ViewHolder {
		ImageView iv;
		TextView tv;
	}

}
