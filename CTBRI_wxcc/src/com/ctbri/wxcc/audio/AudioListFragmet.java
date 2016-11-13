package com.ctbri.wxcc.audio;

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
import com.ctbri.wxcc.entity.AudioListBean;
import com.ctbri.wxcc.entity.AudioListBean.AudioListItem;
import com.ctbri.wxcc.entity.CommonHolder;
import com.ctbri.wxcc.media.MediaPlayerActivity;
import com.ctbri.wxcc.media.MediaUtils;
import com.ctbri.wxcc.travel.CommonList;
import com.ctbri.wxcc.widget.LoadMorePTRListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class AudioListFragmet extends CommonList<AudioListBean, AudioListItem> {

	public static AudioListFragmet newInstance(String group_id, String title) {
		Bundle args = new Bundle();
		args.putString("group_id", group_id);
		args.putString("title", title);
		AudioListFragmet fragment = new AudioListFragmet();
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
	private String group_id, title;

	@Override
	public void onAttach(Activity activity_) {
		super.onAttach(activity_);
		group_id = getArgs("group_id");
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
		outState.putString("title", title);
	}

	@Override
	protected String getListUrl() {
		return Constants.METHOD_AUDIO_VOD_LIST + "?group_id=" + group_id;
	}

	@Override
	protected Class<AudioListBean> getGsonClass() {
		return AudioListBean.class;
	}

	@Override
	protected List<AudioListItem> getEntitys(AudioListBean bean) {
		if (bean.getData() != null)
			return bean.getData().getAudios();
		return null;
	}

	@Override
	protected void fillData(AudioListBean bean, boolean isClean) {
		super.fillData(bean, isClean);

		if (bean != null && bean.getData() != null && !isHeaderInit) {
			AudioListBean data = bean.getData();
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
		MediaUtils.isAudioFavorite(group_id, MediaUtils.FLAG_GROUP, iv_right_btn, (BaseActivity) activity);
	}

	@Override
	protected boolean isEnd(AudioListBean bean) {
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
		View v = inflater.inflate(R.layout.audio_vod_list_header, lv_list.getRefreshableView(), false);
		iv_video_img = (ImageView) v.findViewById(R.id.iv_video_img);
		tv_video_desc = (TextView) v.findViewById(R.id.tv_video_desc);
		lv_list.getRefreshableView().setDivider(getResources().getDrawable(R.drawable.ic_listview_default_divider));
		lv_list.getRefreshableView().setDividerHeight(1);
		lv_list.addHeader(v, null, false);
		return true;
	}

	private void resetImageViewSize(ImageView iv) {
		LayoutParams lp = iv.getLayoutParams();
		lp.width = AudioVodWidget.img_width;
		lp.height = AudioVodWidget.img_height;
		iv.setLayoutParams(lp);
	}

	@Override
	protected View getListItemView(int position, View convertView, ViewGroup parent, AudioListItem data, ImageLoader imgloader, DisplayImageOptions dio) {

		CommonHolder hold = null;
		if (convertView == null) {
			hold = new CommonHolder();
			convertView = inflater.inflate(R.layout.audio_list_item, parent, false);
			convertView.setTag(hold);

			hold.iv = (ImageView) convertView.findViewById(R.id.iv_video_img);
			resetImageViewSize(hold.iv);

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
		imgloader.displayImage(data.getPic(), hold.iv, _Utils.DEFAULT_DIO);
		hold.tv.setText(data.getName());
		hold.tv1.setText(data.getTimes());
		hold.tv2.setText(data.getPlays());
		hold.tv3.setText(data.getLikes());
		hold.tv4.setText(data.getComments());

		return convertView;
	}

	@Override
	protected void onItemClick(AdapterView<?> parent, View view, int position, long id, AudioListItem item) {
		Intent toDetail = new Intent(activity, MediaPlayerActivity.class);
		toDetail.putExtra("type_id", MediaPlayerActivity.TYPE_AUDIO_VOD);
		toDetail.putExtra("vod_id", item.getId());
		toDetail.putExtra("sub_title", item.getName());
		startActivity(toDetail);
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
