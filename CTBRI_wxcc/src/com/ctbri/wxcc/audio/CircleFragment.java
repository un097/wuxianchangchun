package com.ctbri.wxcc.audio;

import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ctbri.comm.util.ImageLoaderInstance;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseFragment;
import com.ctbri.wxcc.widget.LineLayout;
import com.ctbri.wxcc.widget.SelfRounderDisplayer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wookii.widget.ryg.utils.Utils;

public class CircleFragment<T, E> extends BaseFragment {
	// 默认显示类别 标题
	private boolean isInflateHeader = true;
	public static final String KEY_ISSHOWHEAD = "isShowHead";
	protected DisplayImageOptions mDio;
	protected ImageLoader mImageLoader;
	private T mBean;
	private List<E> mList;
	private int mColoumns;
	@Override
	public void onAttach(Activity activity_) {
		super.onAttach(activity_);
		
		isInflateHeader = getArgsBoolean( KEY_ISSHOWHEAD , true);
		mImageLoader = ImageLoaderInstance.getInstance(activity_);
		mDio = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.icon_default_image_place_holder).showImageOnFail(R.drawable.icon_default_image_place_holder)
				.showImageOnLoading(R.drawable.icon_default_image_place_holder).cacheInMemory(false).cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565)
				// 设置是否圆角
				.displayer(new SelfRounderDisplayer(Utils.dp2px(activity, 20), getResources())).build();
	}

	/**
	 * 默认每行显示 4 列
	 */
	public static final int DEFAULT_COLOUMNS = 4;

	private ViewGroup mLineContainer;

	@Override
	protected String getAnalyticsTitle() {
		return null;
	}

	@Override
	protected boolean isEnabledAnalytics() {
		return false;
	}

	private View mView;
	private TextView mTitle, mMore;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.audio_circle_layout, container, false);
		mLineContainer = (ViewGroup) mView.findViewById(R.id.ll_audio_item_container);
		if (isInflateHeader) {
			((ViewStub)mView.findViewById(R.id.vs_more)).inflate();
			mTitle = (TextView) mView.findViewById(R.id.tv_category_tittle);
			mMore = (TextView) mView.findViewById(R.id.tv_category_more);
		}
		return mView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(this.mBean !=null)
			update(mBean, mList, mColoumns);
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
		if (mBinder != null && t != null && list != null && list.size() > 0 && isAdded()) {
			coloumns = coloumns < 1 ? DEFAULT_COLOUMNS : coloumns;
			mBinder.init(mTitle, mMore, t);
			bindData(list, coloumns);
		}else if(!isAdded()){
			this.mBean = t;
			this.mList = list;
			this.mColoumns = coloumns;
		}
	}

	private void bindData(List<E> list, int coloumns) {
		int len = list.size();
		int line_count = len / coloumns;
		line_count += len % coloumns > 0 ? 1 : 0;
		LayoutInflater inflater = LayoutInflater.from(activity);
		for (int line = 0; line < line_count; line++) {
			int start_position = line * coloumns, end_position = (line + 1) * coloumns;
			LineLayout mLineLayout = (LineLayout) inflater.inflate(R.layout.audio_circle_layout_line, mLineContainer, false);
			for (int i = start_position; i < end_position && i < len; i++) {
				View mLineItem = inflater.inflate(R.layout.audio_circle_layout_line_item, mLineLayout, false);
				mBinder.bindData((ImageView) mLineItem.findViewById(R.id.iv_audio_icon), (TextView) mLineItem.findViewById(R.id.tv_audio_title), list.get(i));
				mLineLayout.addView(mLineItem);
			}
			mLineContainer.addView(mLineLayout);
		}
	}

	private ViewBinder<T, E> mBinder;

	public ViewBinder<T, E> getmBinder() {
		return mBinder;
	}

	public void setBinder(ViewBinder<T, E> mBinder) {
		this.mBinder = mBinder;
	}

	public static interface ViewBinder<T, E> {
		void bindData(ImageView imgview, TextView tvTitle, E e);

		void init(TextView tvTitle, TextView more, T t);
	}

}
