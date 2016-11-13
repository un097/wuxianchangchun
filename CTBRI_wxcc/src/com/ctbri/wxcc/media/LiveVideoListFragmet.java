package com.ctbri.wxcc.media;

import java.util.List;

import android.content.Intent;
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
import com.ctbri.wxcc.entity.CommonHolder;
import com.ctbri.wxcc.entity.LiveListBean;
import com.ctbri.wxcc.entity.LiveListBean.LiveListItem;
import com.ctbri.wxcc.travel.CommonList;
import com.ctbri.wxcc.widget.LoadMorePTRListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

public class LiveVideoListFragmet extends CommonList<LiveListBean, LiveListItem> {

	private LayoutInflater inflater;
	@Override
	protected String getListUrl() {
		return Constants.METHOD_VIDEO_LIVE_LIST;
	}

	@Override
	protected Class<LiveListBean> getGsonClass() {
		return LiveListBean.class;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);
		//wuchen at  2015-6-2
		/*ImageView iv_right_btn = (ImageView) v.findViewById(R.id.action_bar_right_btn);
		iv_right_btn.setImageResource(R.drawable.media_favourite_button_selector);
		iv_right_btn.setVisibility(View.VISIBLE);*/
		return v;
	}

	@Override
	protected List<LiveListItem> getEntitys(LiveListBean bean) {
		if (bean.getData() != null)
			return bean.getData().getLives();

		return null;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	protected boolean isEnd(LiveListBean bean) {
		if (bean.getData() != null)
			return bean.getData().getIs_end() == 0;
		return true;
	}

	@Override
	protected String getActionBarTitle() {
		return "视频直播";
	}

	@Override
	protected boolean initHeaderDetail(LoadMorePTRListView lv_list, LayoutInflater inflater) {
		this.inflater = inflater;
		lv_list.getRefreshableView().setDivider(getResources().getDrawable(R.drawable.ic_listview_default_divider));
		lv_list.getRefreshableView().setDividerHeight(1);
		return true;
	}

	@Override
	protected View getListItemView(int position, View convertView, ViewGroup parent, LiveListItem data, ImageLoader imgloader, DisplayImageOptions dio) {

		CommonHolder hold = null;
		if (convertView == null) {
			hold = new CommonHolder();
			convertView = inflater.inflate(R.layout.media_video_list_item, parent, false);
			convertView.setTag(hold);
			hold.iv = (ImageView) convertView.findViewById(R.id.iv_video_img);
			LayoutParams lp = hold.iv.getLayoutParams();
			lp.width = BroadcastGridFragment.img_width;
			lp.height = BroadcastGridFragment.img_height;
			hold.iv.setLayoutParams(lp);
			// 视频名称
			hold.tv = (TextView) convertView.findViewById(R.id.tv_title);
			// 视频长度
			hold.tv1 = (TextView) convertView.findViewById(R.id.tv_time);
			// 播放次数
			hold.tv2 = (TextView) convertView.findViewById(R.id.tv_play_count);
			// 点赞数量
			hold.tv3 = (TextView) convertView.findViewById(R.id.tv_zan_count);
			hold.tv3.setVisibility(View.GONE);
			// 评论数量
			hold.tv4 = (TextView) convertView.findViewById(R.id.tv_comment_count);
			hold.tv4.setVisibility(View.GONE);
		} else {
			hold = (CommonHolder) convertView.getTag();
		}
		imgloader.displayImage(data.getPic(), hold.iv, dio);
		hold.tv.setText(data.getChannel_name());
		hold.tv1.setText(data.getProgram_name());
		hold.tv2.setText(data.getPlays());

		return convertView;
	}

	@Override
	protected void onItemClick(AdapterView<?> parent, View view, int position, long id, LiveListItem entity) {
		Intent toLiveDetail = new Intent(activity, MediaPlayerActivity.class);
		toLiveDetail.putExtra("type_id", VodVideoListFragmet.TYPE_BROADCAST);
		toLiveDetail.putExtra("channel_id", entity.getChannel_id());
		startActivity(toLiveDetail);
		
		// 统计视频直播子模块点击量
		MobclickAgent.onEvent(activity, "E_C_pageName_vodBroadcastClick");
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
