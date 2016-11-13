package com.ctbri.wxcc.media;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctbri.comm.util._Utils;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.audio.AudioCircelWidget;
import com.ctbri.wxcc.entity.MediaVodVideoBean;
import com.ctbri.wxcc.entity.MediaVodVideoBean.Vod;
import com.ctbri.wxcc.entity.MediaVodVideoBean.VodGroup;
import com.sina.weibo.sdk.utils.LogUtil;
import com.umeng.analytics.MobclickAgent;

public class VideoVodWidget extends AudioCircelWidget<VodGroup, MediaVodVideoBean.Vod> implements AudioCircelWidget.ViewBinder<VodGroup, MediaVodVideoBean.Vod> {

	private String title;
	public static int img_width, img_height;
	private boolean isInit = false;

	public VideoVodWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		setHasTitle(true);
		setBinder(this);
		setOnItemClickListener(mItemClicker);
	}

	public VideoVodWidget(Context context, boolean hasHead) {
		super(context);
		setHasTitle(hasHead);
		setBinder(this);
		setOnItemClickListener(mItemClicker);
	}

	private OnItemClickListener<Vod> mItemClicker = new OnItemClickListener<MediaVodVideoBean.Vod>() {

		@Override
		public void onItemClick(View item, Vod data) {
			Intent toDetail = new Intent(context, MediaPlayerActivity.class);
			toDetail.putExtra("type_id", VodVideoListFragmet.TYPE_VOD);
			toDetail.putExtra("vod_id", data.getVod_id());
			toDetail.putExtra("sub_title", data.getVod_name());
			context.startActivity(toDetail);
		}
	};

	@Override
	public void bindData(ImageView imgview, TextView tvTitle, Vod e) {
		mImageLoader.displayImage(e.getVod_url().trim(), imgview, _Utils.DEFAULT_DIO);
		tvTitle.setText(e.getVod_name());
		if (!isInit) {
			isInit = true;
			imgview.getViewTreeObserver().addOnGlobalLayoutListener(new LayoutListener(imgview));
		}
	}

	class LayoutListener implements OnGlobalLayoutListener {
		View target;

		public LayoutListener(View v) {
			target = v;
		}

		@Override
		public void onGlobalLayout() {
			final int mWidth = target.getWidth();
			final int mHeight = target.getHeight();
			if (img_height != 0 && mHeight == img_height)
				target.getViewTreeObserver().removeGlobalOnLayoutListener(this);
			if (mHeight != img_height) {
				img_height = mHeight;
				img_width = mWidth;
			}
		}

	}

	@Override
	public int getItemLayout() {
		return R.layout.media_video_vod_widget_item;
	}

	@Override
	public void init(TextView tvTitle, TextView more, VodGroup t) {
		title = t.getGroup_name();
		tvTitle.setText(title);
		more.setTag(t);
		more.setOnClickListener(mMoreListener);
	}

	private OnClickListener mMoreListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// group_id
			Intent toListIntent = new Intent(context, VideoListActivity.class);
			VodGroup group = (VodGroup) v.getTag();
			toListIntent.putExtra("group_id", group.getGroup_id().toString());
			toListIntent.putExtra("type_id", VodVideoListFragmet.TYPE_VOD);
			toListIntent.putExtra("title", group.getGroup_name());
			context.startActivity(toListIntent);

			// 统计视频点播更多模块点击量
			MobclickAgent.onEvent(context, "E_C_pageName_vodMoreClick");
		}
	};

	@Override
	public void bindData(ImageView imgview, TextView tvTitle, Vod e, View... v) {
		// TODO Auto-generated method stub

	}

}
