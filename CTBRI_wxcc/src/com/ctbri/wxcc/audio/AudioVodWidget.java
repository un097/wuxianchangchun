package com.ctbri.wxcc.audio;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctbri.comm.util._Utils;
import com.ctbri.wxcc.audio.AudioCircelWidget.ViewBinder;
import com.ctbri.wxcc.entity.AudioVodBean;
import com.ctbri.wxcc.entity.AudioVodBean.AudioVod;
import com.ctbri.wxcc.entity.AudioVodBean.AudioVodGroup;
import com.ctbri.wxcc.media.MediaPlayerActivity;
import com.umeng.analytics.MobclickAgent;

public class AudioVodWidget extends AudioCircelWidget<AudioVodBean.AudioVodGroup, AudioVodBean.AudioVod> implements ViewBinder<AudioVodBean.AudioVodGroup, AudioVodBean.AudioVod> {
	private String group_id, title;

	private boolean isInit = false;
	public static int img_width, img_height;

	public AudioVodWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		setHasTitle(true);
		setBinder(this);
	}

	public AudioVodWidget(Context context) {
		super(context, true);
		setBinder(this);
	}

	@Override
	public void bindData(ImageView imgview, TextView tvTitle, AudioVod e) {
		mImageLoader.displayImage(e.getVod_url().trim(), imgview, _Utils.DEFAULT_DIO);
		imgview.setOnClickListener(mItemClicker);
		imgview.setTag(e.getVod_id()+"/"+e.getVod_name());
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
	public void init(TextView tvTitle, TextView more, AudioVodGroup t) {
		tvTitle.setText(t.getGroup_name());
		group_id = t.getGroup_id();
		title = t.getGroup_name();
		more.setOnClickListener(mClickListener);
	}

	private OnClickListener mClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent toList = new Intent(context, AudioListActivity.class);
			toList.putExtra("title", title);
			toList.putExtra("group_id", group_id);
			context.startActivity(toList);
			
			// 统计音频点播更多模块点击量
			MobclickAgent.onEvent(context, "E_C_pageName_aodMoreClick");
		}
	};
	private OnClickListener mItemClicker = new OnClickListener() {
		@Override
		public void onClick(View v) {
			String[] tag = v.getTag().toString().split("/");
			Intent toDetail = new Intent(context, MediaPlayerActivity.class);
			toDetail.putExtra("type_id", MediaPlayerActivity.TYPE_AUDIO_VOD);
			toDetail.putExtra("vod_id", tag[0]);
			toDetail.putExtra("sub_title", tag[1]);
			context.startActivity(toDetail);
			
			// 统计音频点播子模块点击量
			MobclickAgent.onEvent(context, "E_C_pageName_aodItemClick");
		}
	};

	@Override
	public void bindData(ImageView imgview, TextView tvTitle, AudioVod e, View... v) {

	}

}
