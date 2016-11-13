package cn.ffcs.external.trafficbroadcast.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.external_trafficbroadcast.R;

/**
 * 已选图片类表适配器
 * 
 * @author daizhq
 * 
 * @date 2014.12.02
 */
public class PhotoListAdapter extends BaseAdapter {
	// 定义Context
	private Context context;
	private LayoutInflater MyInflater;
	private List<String> selectList;
	private Handler handler;

	public PhotoListAdapter(Context context, List<String> selectList,
			Handler handler) {
		context = context;
		MyInflater = LayoutInflater.from(context);
		this.selectList = selectList;
		this.handler = handler;
	}

	// 获取图片的个数
	public int getCount() {
		return selectList.size();
	}

	// 获取图片在库中的位置
	public Object getItem(int position) {
		return position;
	}

	// 获取图片ID
	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = MyInflater.inflate(R.layout.item_photo, null, false);
			viewHolder = new ViewHolder();
			viewHolder.iv_photo = (ImageView) convertView
					.findViewById(R.id.iv_photo);
			viewHolder.iv_del = (ImageView) convertView
					.findViewById(R.id.iv_delete);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.iv_photo.setImageBitmap(getImageThumbnail(
				selectList.get(position), 240, 200));
		
		viewHolder.iv_photo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectList.remove(position);
				handler.obtainMessage(0).sendToTarget();
			}
		});
		
		return convertView;
	}

	class ViewHolder {
		ImageView iv_photo;
		ImageView iv_del;
	}

	/**
	 * 第一次获取的bitmap实际上为null，只是为了读取宽度和高度，第二次读取的bitmap是根据比例压缩过的图像，
	 * 第三次读取的bitmap是所要的缩略图。
	 */
	private Bitmap getImageThumbnail(String imagePath, int width, int height) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		int h = options.outHeight;
		int w = options.outWidth;
		int beWidth = w / width;
		int beHeight = h / height;
		int be = 1;
		if (beWidth < beHeight) {
			be = beWidth;
		} else {
			be = beHeight;
		}
		options.inJustDecodeBounds = false;
		options.inSampleSize = be;
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);

		return bitmap;
	}

}