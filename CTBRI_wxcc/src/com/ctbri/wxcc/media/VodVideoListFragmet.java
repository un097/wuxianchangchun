package com.ctbri.wxcc.media;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctbri.comm.util.ImageLoaderInstance;
import com.ctbri.comm.util._Utils;
import com.ctbri.wxcc.Constants;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseActivity;
import com.ctbri.wxcc.entity.CommonHolder;
import com.ctbri.wxcc.entity.MediaPlayListBean;
import com.ctbri.wxcc.entity.MediaPlayListBean.PlayItem;
import com.ctbri.wxcc.travel.CommonList;
import com.ctbri.wxcc.widget.LoadMorePTRListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

public class VodVideoListFragmet extends CommonList<MediaPlayListBean, PlayItem> {

	public static VodVideoListFragmet newInstance(String group_id, String type_id, String title) {
		Bundle args = new Bundle();
		args.putString("group_id", group_id);
		args.putString("type_id", type_id);
		args.putString("title", title);
		VodVideoListFragmet fragment = new VodVideoListFragmet();
		fragment.setArguments(args);
		return fragment;
	}

	private ImageView iv_video_img, iv_right_btn;
	private TextView tv_video_desc;
	private LayoutInflater inflater;
	// 标示 顶部头像是否已经初始化
	private boolean isHeaderInit = false;
	/**
	 * 视频类别 为 点播
	 */
	public static final String TYPE_VOD = "1";
	/**
	 * 视频类别 为 直播
	 */
	public static final String TYPE_BROADCAST = "0";
	private String group_id, type_id, title;

	@Override
	public void onAttach(Activity activity_) {
		super.onAttach(activity_);
		group_id = getArgs("group_id");
		type_id = getArgs("type_id");
		title = getArgs("title");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null)
			group_id = savedInstanceState.getString("group_id", group_id);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("group_id", group_id);
		outState.putString("type_id", type_id);
		outState.putString("title", title);
	}

	@Override
	protected String getListUrl() {
		return Constants.METHOD_VIDEO_PLAY_LIST + "?group_id=" + group_id;
	}

	@Override
	protected Class<MediaPlayListBean> getGsonClass() {
		return MediaPlayListBean.class;
	}

	@Override
	protected List<PlayItem> getEntitys(MediaPlayListBean bean) {
		if (bean.getData() != null)
			return bean.getData().getVideo_list();
		return null;
	}

	@Override
	protected void fillData(MediaPlayListBean bean, boolean isClean) {
		super.fillData(bean, isClean);

		if (bean != null && bean.getData() != null && !isHeaderInit) {
			MediaPlayListBean data = bean.getData();
			String desp = data.getGroup_desp();
			String pic = data.getGroup_pic();

			ImageLoaderInstance.getInstance(activity).displayImage(pic, iv_video_img, _Utils.DEFAULT_DIO);
			tv_video_desc.setText(desp);
			isHeaderInit = true;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);

		iv_right_btn = (ImageView) v.findViewById(R.id.action_bar_right_btn);
		iv_right_btn.setVisibility(View.VISIBLE);
		return v;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		MediaUtils.isFavorite(group_id, MediaUtils.FLAG_GROUP, iv_right_btn, (BaseActivity) activity);
	}

	@Override
	protected boolean isEnd(MediaPlayListBean bean) {
		if (bean.getData() != null)
			return bean.getData().getIs_end() == 0;
		return true;
	}

	@Override
	protected String getActionBarTitle() {
		return title;
	}

	@Override
	protected boolean initHeaderDetail(LoadMorePTRListView lv_list, LayoutInflater inflater) {
		this.inflater = inflater;
		View v = inflater.inflate(R.layout.media_play_list_header, lv_list.getRefreshableView(), false);
		iv_video_img = (ImageView) v.findViewById(R.id.iv_video_img);
		tv_video_desc = (TextView) v.findViewById(R.id.tv_video_desc);
		lv_list.getRefreshableView().setDivider(getResources().getDrawable(R.drawable.ic_listview_default_divider));
		lv_list.getRefreshableView().setDividerHeight(1);
		lv_list.addHeader(v, null, false);
		return true;
	}

	@Override
	protected View getListItemView(int position, View convertView, ViewGroup parent, PlayItem data, ImageLoader imgloader, DisplayImageOptions dio) {

		CommonHolder hold = null;
		if (convertView == null) {
			hold = new CommonHolder();
			convertView = inflater.inflate(R.layout.media_video_list_item, parent, false);
			convertView.setTag(hold);
			hold.iv = (ImageView) convertView.findViewById(R.id.iv_video_img);

			LayoutParams lp = hold.iv.getLayoutParams();
			lp.width = VideoVodWidget.img_width;
			lp.height = VideoVodWidget.img_height;
			hold.iv.setLayoutParams(lp);
			
			// 视频名称
			hold.tv = (TextView) convertView.findViewById(R.id.tv_title);
			// 视频长度
			hold.tv1 = (TextView) convertView.findViewById(R.id.tv_time);
			// 播放次数
			hold.tv2 = (TextView) convertView.findViewById(R.id.tv_play_count);
			// 点赞数量
			hold.tv3 = (TextView) convertView.findViewById(R.id.tv_zan_count);
			// 评论数量
			hold.tv4 = (TextView) convertView.findViewById(R.id.tv_comment_count);
		} else {
			hold = (CommonHolder) convertView.getTag();
		}
		imgloader.displayImage(data.getPic_url(), hold.iv, dio);
		hold.tv.setText(data.getVideo_name());
		hold.tv1.setText(data.getPlay_duration());
		hold.tv2.setText(data.getPlay_times());
		hold.tv3.setText(data.getPraise_times());
		hold.tv4.setText(data.getComment_times());

		return convertView;
	}

	@Override
	protected void onItemClick(AdapterView<?> parent, View view, int position, long id, PlayItem item) {
		Intent toPlayList = new Intent(activity, MediaPlayerActivity.class);
		toPlayList.putExtra("type_id", VodVideoListFragmet.TYPE_VOD);
		toPlayList.putExtra("vod_id", item.getVideo_id());
		toPlayList.putExtra("sub_title", item.getVideo_name());
		startActivity(toPlayList);
		
		// 统计视频点播子模块点击量
		MobclickAgent.onEvent(activity, "E_C_pageName_vodItemClick");
	}

	@Override
	protected String getAnalyticsTitle() {
		return null;
	}

	@Override
	protected boolean isEnabledAnalytics() {
		return false;
	}

}
