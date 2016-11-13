package com.ctbri.wxcc.media;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Path.FillType;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctbri.wxcc.Constants;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.entity.MediaBroadcastVideoBean;
import com.ctbri.wxcc.entity.MediaCategoryBean;
import com.ctbri.wxcc.entity.MediaBroadcastVideoBean.Channel;
import com.ctbri.wxcc.entity.MediaCategoryBean.MediaCategory;
import com.ctbri.wxcc.widget.MenuGridView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ViewScaleType;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;

public class VideoNavigatorGridFragment extends CommonGrid<MediaCategoryBean, MediaCategory> {

	public static final String KEY_HAS_MORE = "has_more";
	public static final String KEY_URL = "url";
	private int mItemLayoutId = R.layout.media_video_category_item;

	public static VideoNavigatorGridFragment newInstance(boolean show_more, String url) {
		Bundle args = new Bundle();
		args.putBoolean(KEY_HAS_MORE, show_more);
		args.putString(KEY_URL, url);
		VideoNavigatorGridFragment videoGrid = new VideoNavigatorGridFragment();
		videoGrid.setArguments(args);
		return videoGrid;
	}

	// 默认显示全部
	private boolean show_more = true;
	private String mCategory_url;
	private static final int MAX_CATEGORY_ITEMS = 5;

	private LayoutInflater inflater;
	private int[] colors = new int[] { R.color.video_category_color0, R.color.video_category_color1, R.color.video_category_color2, R.color.video_category_color3,
			R.color.video_category_color4, R.color.video_category_color5 };

	@Override
	public void onAttach(Activity activity_) {
		super.onAttach(activity_);
		Resources res = getResources();
		for (int i = 0; i < colors.length; i++) {
			colors[i] = res.getColor(colors[i]);
		}
		show_more = getArgsBoolean(KEY_HAS_MORE, show_more);
		mCategory_url = getArgs(KEY_URL);
	}

	@Override
	protected String getListUrl() {
		return mCategory_url + "?is_all=" + (show_more ? "0" : "1");
	}

	@Override
	protected String getAnalyticsTitle() {
		return null;
	}

	@Override
	protected boolean isEnabledAnalytics() {
		return false;
	}

	@Override
	protected int getLayoutResId() {
		return R.layout.media_video_category_grid;
	}

	@Override
	protected List<MediaCategory> getEntitys(MediaCategoryBean bean) {
		if (bean != null && bean.getData() != null) {
			// 如果要显示 ... 打开更多，追加一个空的。
			if (!show_more) {
				MediaCategory more = new MediaCategory();
				more.setCategory_id("more");
				more.setCategory_name(getString(R.string.tip_more_video_category));
				bean.getData().getCategory_list().add(more);
			}
			return bean.getData().getCategory_list();
		}
		return null;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	protected boolean isEnd(MediaCategoryBean bean) {
		return false;
	}

	@Override
	protected boolean initHeaderDetail(View parent, MenuGridView lv_list, LayoutInflater inflater) {
		this.inflater = inflater;
		lv_list.setDrawBorder(true);
		if (show_more)
			lv_list.setAdjustScrollParent(false);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		lv_list.setLayoutParams(lp);
		return false;
	}

	@Override
	protected Class<MediaCategoryBean> getGsonClass() {
		return MediaCategoryBean.class;
	}

	protected void setItemLayout(int layout) {
		mItemLayoutId = layout;
	}

	@Override
	protected View getListItemView(int position, View convertView, ViewGroup parent, MediaCategory data, ImageLoader imgloader, DisplayImageOptions dio) {

		View v = inflater.inflate(mItemLayoutId, parent, false);

		TextView tv_title = (TextView) v.findViewById(R.id.tv_title);

		// imgloader.displayImage(data.getCategory_url(), new
		// CategoryAware(tv_title), dio);
		tv_title.setText(data.getCategory_name());
		tv_title.setTextColor(colors[position % colors.length]);

		return v;
	}

	@Override
	protected void onItemClick(AdapterView<?> parent, View view, int position, long id, MediaCategory entity) {
		if ("more".equals(entity.getCategory_id())) {
			Intent toAllCategory = new Intent(activity, VideoCategoryActivity.class);
			startActivity(toAllCategory);
		} else {
			Intent toPlayList = new Intent(activity, VideoListActivity.class);
			toPlayList.putExtra("type_id", VodVideoListFragmet.TYPE_VOD);
			toPlayList.putExtra("group_id", entity.getCategory_id());
			toPlayList.putExtra("title", entity.getCategory_name());

			startActivity(toPlayList);
		}
	}

	class CategoryAware implements ImageAware {
		private Reference<TextView> ref;

		public CategoryAware(TextView tv) {
			this.ref = new WeakReference<TextView>(tv);
		}

		@Override
		public int getWidth() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getHeight() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public ViewScaleType getScaleType() {
			return ViewScaleType.CROP;
		}

		@Override
		public TextView getWrappedView() {
			return ref.get();
		}

		@Override
		public boolean isCollected() {
			return ref.get() == null;
		}

		@Override
		public int getId() {
			TextView imageView = ref.get();
			return imageView == null ? super.hashCode() : imageView.hashCode();
		}

		@Override
		public boolean setImageDrawable(Drawable drawable) {
			TextView tv = ref.get();
			if (tv != null) {
				tv.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
				return true;
			}
			return false;
		}

		@Override
		public boolean setImageBitmap(Bitmap bitmap) {
			TextView tv = ref.get();
			if (tv != null) {
				tv.setCompoundDrawablesWithIntrinsicBounds(null, new BitmapDrawable(getResources(), bitmap), null, null);
				return true;
			}
			return false;
		}
	}
}
