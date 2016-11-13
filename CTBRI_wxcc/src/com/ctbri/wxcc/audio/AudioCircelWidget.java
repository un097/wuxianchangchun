package com.ctbri.wxcc.audio;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ctbri.comm.util.ImageLoaderInstance;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.widget.LineLayout;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 
 * @author yanyadi
 * 
 */
public class AudioCircelWidget<T, E> extends LinearLayout {

	private boolean mHasTitle;
	protected Context context;

	private ViewGroup mLineContainer;
	public static final String KEY_ISSHOWHEAD = "isShowHead";
	// protected DisplayImageOptions mDio;
	protected ImageLoader mImageLoader;
	private T mBean;
	private List<E> mList;
	private int mColoumns;
	private LayoutInflater inflater;
	private View mHead;
	private TextView mTvTitle, mTvMore;
	private int[] mIds;
	private OnItemClickListener<E> mItemClickListener;
	/**
	 * 默认每行显示 4 列
	 */
	public static final int DEFAULT_COLOUMNS = 4;

	public AudioCircelWidget(Context context) {
		super(context);
		this.context = context;
		init();
	}

	public AudioCircelWidget(Context context, boolean showHead) {
		super(context);
		this.context = context;
		this.mHasTitle = showHead;
		init();
	}

	public AudioCircelWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	public OnItemClickListener<E> getOnItemClickListener() {
		return mItemClickListener;
	}

	public void setOnItemClickListener(OnItemClickListener<E> mItemClickListener) {
		this.mItemClickListener = mItemClickListener;
	}

	public boolean isHasTitle() {
		return mHasTitle;
	}

	public void setHasTitle(boolean mHasTitle) {
		this.mHasTitle = mHasTitle;
		detemineHead();
	}

	/**
	 * 获取 item layout 布局
	 * 
	 * @return
	 */
	public int getItemLayout() {
		return R.layout.audio_circle_layout_line_item;
	}

	/**
	 * 获取 绑定的 view ids
	 * 
	 * @return
	 */
	public void setItemViews(int[] ids) {
		mIds = ids;
	}

	private void detemineHead() {
		if (mHasTitle) {
			if (mHead == null) {
				mHead = inflater.inflate(R.layout.media_video_category_header, this, false);
				mTvTitle = (TextView) mHead.findViewById(R.id.tv_category_tittle);
				mTvMore = (TextView) mHead.findViewById(R.id.tv_category_more);
			}
			if (mHead.getParent() == null)
				this.addView(mHead, 0);
		} else {
			if (mHead != null)
				this.removeView(mHead);
		}
	}

	/**
	 * 初始化 常用的辅助对象
	 */
	private void init() {
		inflater = LayoutInflater.from(context);
		setOrientation(VERTICAL);
		// int radius =
		// getResources().getDimensionPixelSize(R.dimen.audio_circle_image_radius);

		mImageLoader = ImageLoaderInstance.getInstance(context);
		// mDio = new
		// DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.icon_default_image_place_holder).showImageOnFail(R.drawable.icon_default_image_place_holder)
		// .showImageOnLoading(R.drawable.icon_default_image_place_holder).cacheInMemory(false).cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565)
		// 设置是否圆角
		// .build();
		// .displayer(new SelfRounderDisplayer(radius, getResources())).build();

		detemineHead();

		mLineContainer = createLineContainer();
		this.addView(mLineContainer);
	}

	private ViewGroup createLineContainer() {
		LinearLayout group = new LinearLayout(this.context);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		group.setLayoutParams(params);
		group.setOrientation(VERTICAL);
		return group;
	}

	/**
	 * 更新列表数据
	 * 
	 * @param t
	 * @param list
	 * @param coloumns
	 */
	public void update(T t, List<E> list, int coloumns) {
		// fragment 可能还没有初始化。先保存传递过来的参数
		if (mBinder != null && t != null && list != null && list.size() > 0) {
			coloumns = coloumns < 1 ? DEFAULT_COLOUMNS : coloumns;
			initHead(t);
			bindData(list, coloumns);
		} else {
			this.mBean = t;
			this.mList = list;
			this.mColoumns = coloumns;
		}
	}

	private void initHead(T t) {
		if (mTvTitle != null && mTvMore != null) {
			mBinder.init(mTvTitle, mTvMore, t);
		}
	}

	private void bindData(List<E> list, int coloumns) {
		// 先清空全部的 view
		mLineContainer.removeAllViews();
		int row_padding = getResources().getDimensionPixelSize(R.dimen.audio_circle_row_padding);
		int len = list.size();
		int line_count = len / coloumns;
		int itemLayout = getItemLayout();
		line_count += len % coloumns > 0 ? 1 : 0;
		for (int line = 0; line < line_count; line++) {
			int start_position = line * coloumns, end_position = (line + 1) * coloumns;
			LineLayout mLineLayout = (LineLayout) inflater.inflate(R.layout.audio_circle_layout_line, mLineContainer, false);
			mLineLayout.setColoumns(coloumns);
			for (int i = start_position; i < end_position && i < len; i++) {
				View mLineItem = inflater.inflate(itemLayout, mLineLayout, false);
				bindViewData(mLineItem, list.get(i));
				mLineLayout.addView(mLineItem);
			}
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			// 设置行间距
			if (line > 0) {
				params.setMargins(0, row_padding, 0, 0);
			}
			mLineContainer.addView(mLineLayout, params);
		}
	}

	private void bindViewData(View mLineItem, E data) {
		if (mIds != null) {
			View views[] = new View[mIds.length];
			for (int i = 0; i < mIds.length; i++)
				views[i] = mLineItem.findViewById(mIds[i]);
			mBinder.bindData((ImageView) mLineItem.findViewById(R.id.iv_audio_icon), (TextView) mLineItem.findViewById(R.id.tv_audio_title), data, views);
		} else {
			mBinder.bindData((ImageView) mLineItem.findViewById(R.id.iv_audio_icon), (TextView) mLineItem.findViewById(R.id.tv_audio_title), data);
		}
		mLineItem.setTag(data);
		mLineItem.setOnClickListener(mItemClicker);
	}

	private OnClickListener mItemClicker = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mItemClickListener != null) {
				mItemClickListener.onItemClick(v, (E) v.getTag());
			}
		}
	};

	private ViewBinder<T, E> mBinder;

	public ViewBinder<T, E> getmBinder() {
		return mBinder;
	}

	public void setBinder(ViewBinder<T, E> mBinder) {
		this.mBinder = mBinder;
	}

	public static interface OnItemClickListener<E> {
		void onItemClick(View item, E data);
	}

	public static interface ViewBinder<T, E> {
		void bindData(ImageView imgview, TextView tvTitle, E e);

		void bindData(ImageView imgview, TextView tvTitle, E e, View... v);

		void init(TextView tvTitle, TextView more, T t);
	}

}
