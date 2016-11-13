package cn.ffcs.wisdom.city.simico.activity.collection.adapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import cn.ffcs.wisdom.city.R;
import cn.ffcs.wisdom.city.simico.activity.home.view.TagView;
import cn.ffcs.wisdom.city.simico.api.model.News;
import cn.ffcs.wisdom.city.simico.api.model.NewsTemplate;
import cn.ffcs.wisdom.city.simico.base.Application;
import cn.ffcs.wisdom.city.simico.image.CommonImageLoader;
import cn.ffcs.wisdom.city.simico.kit.adapter.ListBaseAdapter;
import cn.ffcs.wisdom.city.simico.kit.log.TLog;
import cn.ffcs.wisdom.city.simico.kit.util.DateUtil;
import cn.ffcs.wisdom.city.simico.ui.grid.EmptyView;

public class CollectionAdapter extends ListBaseAdapter {

	private static final String TAG = CollectionAdapter.class.getSimpleName();
	private EmptyView mEmptyView;

	public interface NewsDelegate {
		public void onNewsItemClick(News news);

		public void onNewsDeleteClick(News news);
	}

	public WeakReference<NewsDelegate> mDelegate;

	private CommonImageLoader mImageLoader;
	private int[] mImgHeights;
	private boolean isDelete = false;

	public CollectionAdapter(NewsDelegate delegate) {
		mDelegate = new WeakReference<NewsDelegate>(delegate);
		mImageLoader = new CommonImageLoader(Application.context()
				.getApplicationContext());
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	public void setImgHeights(int[] heights) {
		mImgHeights = heights;
	}

	@Override
	public View getRealView(int position, View convertView, ViewGroup parent) {
		// 处理空数据
		if (state == STATE_NO_DATA) {
			TLog.log(TAG, "没有数据");
			return mEmptyView;
		}

		final News news = (News) getItem(position);
		ViewHolder vh = null;

		boolean needCreateView = true;
		if (convertView != null) {
			Object t = convertView.getTag();
			if (t instanceof ViewHolder) {
				vh = (ViewHolder) t;
				if (news.getTemplate().getDisplayType() == vh.type) {
					needCreateView = false;
				}
			}
		}
		if (needCreateView) {
			TLog.log(TAG, "需要重新创建ConvertView");
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			int layoutRes = 0;
			switch (news.getTemplate().getDisplayType()) {
			case 1:
				layoutRes = R.layout.simico_list_cell_home_type_d1;
				break;
			case 2:
				layoutRes = R.layout.simico_list_cell_home_type_d2;
				break;
			case 3:
				layoutRes = R.layout.simico_list_cell_home_type_d3;
				break;
			case 4:
				layoutRes = R.layout.simico_list_cell_home_type_d4;
				break;
			case 5:
				layoutRes = R.layout.simico_list_cell_home_type_d5;
				break;
			default:
				break;
			}
			if (layoutRes != 0) {
				convertView = inflater.inflate(layoutRes, null);
			} else {
				convertView = inflater.inflate(
						R.layout.simico_list_cell_home_type_d3, null);
			}

			vh = new ViewHolder(convertView, mImgHeights, news.getTemplate()
					.getDisplayType());

			convertView.setTag(vh);
		} else {
			TLog.log(TAG, "复用ConvertView");
		}

		vh.top.setVisibility(news.isTop() ? View.VISIBLE : View.GONE);
		vh.title.setText(news.getTemplate().getTitle());
		vh.time.setText(DateUtil.getFormatTime(news.getEfficeTime()));
		vh.source.setText(news.getSourceName());
		vh.tag.setTags(news.getTemplate().getTags(), " ");
		vh.delete.setVisibility(isDelete ? View.VISIBLE : View.GONE);
		vh.delete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mDelegate != null && mDelegate.get() != null) {
					mDelegate.get().onNewsDeleteClick(news);
				}
			}
		});
		// handle images
		NewsTemplate t = news.getTemplate();
		ArrayList<String> imgs = null;

		if (t != null && (imgs = t.getImgs()) != null) {
			String imgTag = null;
			if (vh.img1 != null && imgs.size() > 0) {
				if ((imgTag = (String) vh.img1.getTag()) != null) {
					if (!imgTag.equals(imgs.get(0)))
						mImageLoader.clearMemeryCacheByKey(imgTag);
				}
				mImageLoader.loadUrl(vh.img1, imgs.get(0));//
				vh.img1.setTag(imgs.get(0));
			}

			if (vh.img2 != null && imgs.size() > 1) {
				if ((imgTag = (String) vh.img2.getTag()) != null) {
					if (!imgTag.equals(imgs.get(1)))
						mImageLoader.clearMemeryCacheByKey(imgTag);
				}
				mImageLoader.loadUrl(vh.img2, imgs.get(1));
				vh.img2.setTag(imgs.get(1));
				// Application.getPicasso().load(imgs.get(1)).into(vh.img2);
			}

			if (vh.img3 != null && imgs.size() > 2) {
				if ((imgTag = (String) vh.img3.getTag()) != null) {
					if (!imgTag.equals(imgs.get(2)))
						mImageLoader.clearMemeryCacheByKey(imgTag);
				}
				mImageLoader.loadUrl(vh.img3, imgs.get(2));
				vh.img3.setTag(imgs.get(2));
				// Application.getPicasso().load(imgs.get(2)).into(vh.img3);
			}
		}

		convertView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!isDelete && mDelegate != null && mDelegate.get() != null) {
					mDelegate.get().onNewsItemClick(news);
				}
			}
		});

		return convertView;
	}

	public void setEmptyView(EmptyView emptyView) {
		mEmptyView = emptyView;
	}

	static class ViewHolder {
		int type;
		TagView tag;
		ImageView top;
		TextView title, source, time;
		ImageView img1, img2, img3, delete;

		public ViewHolder(View convertView, int[] heights, int type) {
			top = (ImageView) convertView.findViewById(R.id.iv_top_tip);
			title = (TextView) convertView.findViewById(R.id.tv_title);
			time = (TextView) convertView.findViewById(R.id.tv_time);
			source = (TextView) convertView.findViewById(R.id.tv_source);
			tag = (TagView) convertView.findViewById(R.id.tv_tag);
			img1 = (ImageView) convertView.findViewById(R.id.iv_img1);
			img2 = (ImageView) convertView.findViewById(R.id.iv_img2);
			img3 = (ImageView) convertView.findViewById(R.id.iv_img3);
			delete = (ImageView) convertView.findViewById(R.id.iv_delete);
			this.type = type;

			if (type == 1) {
				LayoutParams params = img1.getLayoutParams();
				params.height = heights[type - 1];
			}
			if (type == 2) { // 3 图
				LayoutParams params = img1.getLayoutParams();
				params.height = heights[type - 1];

				params = img2.getLayoutParams();
				params.height = heights[type - 1];

				params = img3.getLayoutParams();
				params.height = heights[type - 1];
			} else if (type == 4) {
				LayoutParams params = img1.getLayoutParams();
				params.height = heights[type - 1];

				params = img2.getLayoutParams();
				params.height = heights[type - 1];
			}
		}
	}
}
