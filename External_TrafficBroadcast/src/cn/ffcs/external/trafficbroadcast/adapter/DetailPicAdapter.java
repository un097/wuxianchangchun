package cn.ffcs.external.trafficbroadcast.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import cn.ffcs.wisdom.city.utils.CityImageLoader;

import com.example.external_trafficbroadcast.R;


/**
 * 详情页面图片的适配器
 * 
 * @author daizhq
 * 
 * @date 2014.12.02
 * */
public class DetailPicAdapter extends BaseAdapter {
	// 定义Context
	private Context context;
	private String[] pic_url;
	private LayoutInflater MyInflater;
	
	private CityImageLoader loader;

	public DetailPicAdapter(Context c, String[] pic_url) {
		context = c;
		if (pic_url != null) {
			this.pic_url = pic_url;
		} else {
			this.pic_url = new String[]{};
		}
		MyInflater = LayoutInflater.from(context);
		loader = new CityImageLoader(context);
//		loader.setDefaultFailImage(R.drawable.banner_default);
	}

	// 获取图片的个数
	public int getCount() {
		return pic_url.length;
	}

	// 获取图片在库中的位置
	public Object getItem(int position) {
		return position;
	}

	// 获取图片ID
	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = MyInflater.inflate(R.layout.item_detail_pic, null,
					false);
			viewHolder = new ViewHolder();
			viewHolder.iv_pic = (ImageView) convertView
					.findViewById(R.id.iv_pic);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		String url = pic_url[position];
		loader.loadUrl(viewHolder.iv_pic, url);
		return convertView;
	}

	class ViewHolder {
		ImageView iv_pic;
	}

}
