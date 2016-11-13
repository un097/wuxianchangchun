package cn.ffcs.changchuntv.activity.news;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import cn.ffcs.changchuntv.R;
import cn.ffcs.wisdom.city.simico.activity.home.adapter.ListBaseSectionedAdapter;
import cn.ffcs.wisdom.city.simico.activity.home.view.TagView;
import cn.ffcs.wisdom.city.simico.api.model.News;
import cn.ffcs.wisdom.city.simico.api.model.NewsTemplate;
import cn.ffcs.wisdom.city.simico.base.Application;
import cn.ffcs.wisdom.city.simico.image.CommonImageLoader;
import cn.ffcs.wisdom.city.simico.kit.log.TLog;
import cn.ffcs.wisdom.city.simico.kit.util.DateUtil;
import cn.ffcs.wisdom.city.simico.ui.grid.EmptyView;

public class NewsAdapter extends ListBaseSectionedAdapter {

	private static final String TAG = NewsAdapter.class.getSimpleName();
	private EmptyView mEmptyView;

	public interface NewsDelegate {
		public void onNewsItemClick(News news);
	}

	public WeakReference<NewsDelegate> mDelegate;

	private CommonImageLoader mImageLoader;
	private int[] mImgHeights;

	public NewsAdapter(NewsDelegate delegate) {
		mDelegate = new WeakReference<NewsDelegate>(delegate);
		mImageLoader = new CommonImageLoader(Application.context().getApplicationContext());
	}

	public void setImgHeights(int[] heights){
		mImgHeights = heights;
	}
	
	@Override
	public int getItemViewTypeCount() {
		return 5;//super.getItemViewTypeCount();
	}
	
	@Override
	public int getCountForSection(int section) {
		if (state == STATE_NO_DATA) {
			return 1;
		}
		return super.getCountForSection(section);
	}

	@Override
	public int getSectionCount() {
		if (state == STATE_NO_DATA) {
			return 1;
		}
		return super.getSectionCount();
	}

	@Override
	public View getSectionHeaderView(int section, View convertView,
			ViewGroup parent) {
		if (section == getSectionCount() - 1) {
			if (state == STATE_LOAD_MORE || state == STATE_NO_MORE || state == STATE_EMPTY_ITEM) {
				return super.getSectionHeaderView(section, convertView, parent);
			}
		}
		View view = new View(parent.getContext());
		view.setVisibility(View.GONE);
		return view;
	}

	@Override
	public View getItemView(int section, int position, View convertView, ViewGroup parent) {
		// 处理空数据
		if (state == STATE_NO_DATA) {
			TLog.log(TAG, "没有数据");
			return mEmptyView;
		}

		final News news = (News) getItem(section, position);// _data.get(position);
		
//		if ("adv".equals(news.getType())) {
//			MobclickAgent.onEvent(Application.context(), "" + news.getId(),
//					news.getTemplate().getTitle());
//			TLog.log("youmeng", news.getTemplate().getTitle());
//		}
		
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
				layoutRes = R.layout.news_list_cell_type1;  //一张大图在底部
				break;
			case 2:
				layoutRes = R.layout.news_list_cell_type2;  //三张小图
				break;
			case 3:
				layoutRes = R.layout.news_list_cell_type3;  //没有图片  纯文字
				break;
			case 4:
				layoutRes = R.layout.news_list_cell_type4;  //两张图
				break;
			case 5:
				layoutRes = R.layout.news_list_cell_type5;  //一张小图在左侧
				break;
			default:
				break;
			}
			if (layoutRes != 0) {
				convertView = inflater.inflate(layoutRes, null);
			} else {
				convertView = inflater.inflate(R.layout.news_list_cell_type3, null);
			}

			vh = new ViewHolder(convertView, mImgHeights,
					news.getTemplate().getDisplayType());
			
			convertView.setTag(vh);
		} else {
			TLog.log(TAG, "复用ConvertView");
		}

		vh.top.setVisibility(news.isTop() ? View.VISIBLE : View.GONE);
		vh.title.setText(news.getTemplate().getTitle());
		vh.time.setText(DateUtil.getFormatTime(news.getEfficeTime()));
		vh.source.setText(news.getSourceName());vh.source.setVisibility(View.GONE);
		vh.tag.setTags(news.getTemplate().getTags(), " ");
		String subtitle = news.getSubtitle();
		if (!TextUtils.isEmpty(subtitle) && !subtitle.equals("null")) {
			vh.subTitle.setVisibility(View.VISIBLE);
			vh.subTitle.setText(subtitle);
		} else {
			vh.subTitle.setVisibility(View.GONE);
		}
		vh.totalRead.setText("浏览量：" + news.getClickCount());

		// handle images
		NewsTemplate t = news.getTemplate();
		ArrayList<String> imgs = null;

		if (t != null && (imgs = t.getImgs()) != null) {
			String imgTag = null;
			if (vh.img1 != null && imgs.size() > 0) {
				if ((imgTag = (String) vh.img1.getTag()) != null) {
					if(!imgTag.equals(imgs.get(0)))
					mImageLoader.clearMemeryCacheByKey(imgTag);
				}
				mImageLoader.loadUrl(vh.img1, imgs.get(0));//
				vh.img1.setTag(imgs.get(0));
				//Application.getPicasso().load(imgs.get(0)).into(vh.img1);
				//Picasso.with(parent.getContext()).load(imgs.get(0)).into(vh.img1);
				//ImageLoader.getInstance().displayImage(imgs.get(0), vh.img1,ImageUtils.getOptions());
			}

			if (vh.img2 != null && imgs.size() > 1) {
				if ((imgTag = (String) vh.img2.getTag()) != null) {
					if(!imgTag.equals(imgs.get(1)))
					mImageLoader.clearMemeryCacheByKey(imgTag);
				}
				mImageLoader.loadUrl(vh.img2, imgs.get(1));
				vh.img2.setTag(imgs.get(1));
				//Application.getPicasso().load(imgs.get(1)).into(vh.img2);
			}

			if (vh.img3 != null && imgs.size() > 2) {
				if ((imgTag = (String) vh.img3.getTag()) != null) {
					if(!imgTag.equals(imgs.get(2)))
					mImageLoader.clearMemeryCacheByKey(imgTag);
				}
				mImageLoader.loadUrl(vh.img3, imgs.get(2));
				vh.img3.setTag(imgs.get(2));
				//Application.getPicasso().load(imgs.get(2)).into(vh.img3);
			}
		}

		convertView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mDelegate != null && mDelegate.get() != null) {
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
		TextView subTitle, totalRead;
		ImageView img1, img2, img3;
		
		public ViewHolder(View convertView,int[] heights, int type) {
			top = (ImageView) convertView.findViewById(R.id.iv_top_tip);
			title = (TextView) convertView.findViewById(R.id.tv_title);
			time = (TextView) convertView.findViewById(R.id.tv_time);
			source = (TextView) convertView.findViewById(R.id.tv_source);
			subTitle = (TextView) convertView.findViewById(R.id.tv_subtitle);
			totalRead = (TextView) convertView.findViewById(R.id.tv_totalread);
			tag = (TagView) convertView.findViewById(R.id.tv_tag);
			img1 = (ImageView) convertView.findViewById(R.id.iv_img1);
			img2 = (ImageView) convertView.findViewById(R.id.iv_img2);
			img3 = (ImageView) convertView.findViewById(R.id.iv_img3);
			this.type = type;
			
			if(type == 1) {
				LayoutParams params = img1.getLayoutParams();
				params.height = heights[type-1];
			} if(type == 2) { //3 图
				LayoutParams params = img1.getLayoutParams();
				params.height = heights[type-1];
				
				params = img2.getLayoutParams();
				params.height = heights[type-1];
				
				params = img3.getLayoutParams();
				params.height = heights[type-1];
			} else if(type ==4) {
				LayoutParams params = img1.getLayoutParams();
				params.height = heights[type-1];
				
				params = img2.getLayoutParams();
				params.height = heights[type-1];
			}
		}
	}
}
