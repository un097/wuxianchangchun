package com.ctbri.wxcc.vote;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ctbri.comm.util.ImageLoaderInstance;
import com.ctbri.comm.util._Utils;
import com.ctbri.wxcc.Constants;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseFragment;
import com.ctbri.wxcc.community.Constants_Community;
import com.ctbri.wxcc.community.ObjectAdapter;
import com.ctbri.wxcc.entity.PageModel;
import com.ctbri.wxcc.entity.VoteBean;
import com.ctbri.wxcc.entity.VoteBean.Vote;
import com.ctbri.wxcc.widget.LoadMorePTRListView;
import com.ctbri.wxcc.widget.LoadMorePTRListView.OnLoadMoreListViewListener;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

public class VoteListFragment extends BaseFragment {
	private PageModel pm;
	private LoadMorePTRListView lv_vote;
	private VoteAdapter voteAdapter;
	private ImageLoader imgloader;
	private DisplayImageOptions dio;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pm = new PageModel(0, Constants_Community.PAGE_SIZE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		View container = inflater
				.inflate(R.layout.fragment_vote, parent, false);
		lv_vote = (LoadMorePTRListView) container.findViewById(R.id.lv_vote);
		lv_vote.setOnRefreshListener(new RefreshListener());
		lv_vote.setMode(Mode.PULL_FROM_START);

		((TextView) container.findViewById(R.id.action_bar_title))
				.setText(R.string.title_vote);
		lv_vote.setOnItemClickListener(new ItemClickListener());

		container.findViewById(R.id.btn_new_vote).setOnClickListener(
				new NewSuggestListener());
		container.findViewById(R.id.action_bar_left_btn).setOnClickListener(
				new FinishClickListener());

		return container;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		lv_vote.showHeaderLoading();
	}

	private void loadData(PageModel pm, final boolean isClearData) {

		String withParamsUrl = pm
				.appendToTail(Constants.METHOD_INVESTIGATION_LIST);
		request(withParamsUrl, new RequestCallback() {
			@Override
			public void requestSucc(String json) {
				fillList(json, isClearData);
			}

			@Override
			public void requestFailed(int errorCode) {
				if (isClearData)
					lv_vote.onRefreshComplete();
				else
					lv_vote.showLoadMore();
			}
		});
	}

	private void fillList(String json, boolean isClearData) {
		Gson gson = new Gson();
		VoteBean data = gson.fromJson(json, VoteBean.class);

		if (voteAdapter == null || isClearData) {
			voteAdapter = new VoteAdapter(activity, data.getData()
					.getVote_list());
			lv_vote.setAdapter(voteAdapter);
		} else {
			voteAdapter.addAll(data.getData().getVote_list());
			voteAdapter.notifyDataSetChanged();
		}
		// 如果返回的数据已经到最后一条, 隐藏加载更多按钮
		if (data.getData().getIs_end() == 0)
			lv_vote.hideLoadMore();
		// 如果下拉刷新触发的事件
		if (isClearData)
			lv_vote.onRefreshComplete();
	}

	class VoteAdapter extends ObjectAdapter<Vote> {
		private String[] mVoteStatus;
		public VoteAdapter(Activity activity, List<Vote> data_) {
			super(activity, data_);
			imgloader = ImageLoaderInstance.getInstance(activity);
			dio = new DisplayImageOptions.Builder()
					.showImageForEmptyUri(
							R.drawable.icon_default_image_place_holder)
					.showImageOnFail(R.drawable.icon_default_image_place_holder)
					.showImageOnLoading(
							R.drawable.icon_default_image_place_holder)
					.cacheOnDisc(true)
					// .considerExifParams(true)
					.bitmapConfig(Bitmap.Config.RGB_565)
					// 设置是否圆角
					// .displayer(new RoundedBitmapDisplayer(20))
					.build();
			mVoteStatus = activity.getResources().getStringArray(R.array.vote_status);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			VoteHolder holder = null;

			if (convertView == null) {
				holder = new VoteHolder();
				convertView = activity.getLayoutInflater().inflate(
						R.layout.common_vote_desc, parent, false);
				holder.iv_img = (ImageView) convertView
						.findViewById(R.id.iv_vote_bg);
				holder.tv_num = (TextView) convertView
						.findViewById(R.id.tv_vote_number);
				holder.tv_source = (TextView) convertView
						.findViewById(R.id.tv_source);
				holder.tv_status = (TextView) convertView
						.findViewById(R.id.tv_vote_state);
				holder.tv_title = (TextView) convertView
						.findViewById(R.id.tv_vote_title);

				convertView.setTag(holder);
			} else {
				holder = (VoteHolder) convertView.getTag();
			}

			Vote vote = getItem(position);
			holder.tv_num.setText(getString(R.string.vote_number,
					vote.getVote_num()));
			holder.tv_source.setText(getString(R.string.vote_source,
					vote.getVote_res()));
			if (1 == vote.getStatus())
				holder.tv_status
						.setBackgroundResource(R.color.vote_status_starting);
			else
				holder.tv_status
						.setBackgroundResource(R.color.vote_status_ended);
			if(mVoteStatus.length > vote.getStatus() && vote.getStatus() >-1 )
			holder.tv_status.setText(mVoteStatus[ vote.getStatus() ]);
			
			holder.tv_title.setText(vote.getTitle());
			holder.investigation_id = vote.getInvestigation_id();
			imgloader
					.displayImage(vote.getImg_rel().trim(), holder.iv_img, dio);
			return convertView;
		}
	}

	class VoteHolder {
		ImageView iv_img;
		TextView tv_status, tv_source, tv_num, tv_title;
		String investigation_id;
	}

	class RefreshListener implements OnRefreshListener<ListView> {

		@Override
		public void onRefresh(PullToRefreshBase<ListView> refreshView) {
			pm.start = 0;
			loadData(pm, true);
		}
	}

	class LoadMoreListener implements OnLoadMoreListViewListener {

		@Override
		public void onLastItemClick(LoadMorePTRListView listview) {
			pm.start += Constants_Community.PAGE_SIZE;
			loadData(pm, false);
		}

		@Override
		public void onAutoLoadMore(LoadMorePTRListView list) {
			pm.start += Constants_Community.PAGE_SIZE;
			loadData(pm, false);
		}
	}

	class ItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			VoteHolder holder = (VoteHolder) view.getTag();
			Intent it = new Intent(activity, VoteDetailActivity.class);
			it.putExtra(VoteDetailFragment.KEY_INVESTIGATION_ID,
					holder.investigation_id);
			startActivity(it);

			// 统计民意调查的点击量
			HashMap<String, String> param = new HashMap<String, String>();
			param.put("A_vote_pageName_voteName", holder.tv_title.getText()
					.toString());
			MobclickAgent
					.onEvent(activity, "E_C_pageName_voteItemClick", param);

		}

	}

	class NewSuggestListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (_Utils.checkLoginAndLogin(activity)) {
				Intent toSuggest = new Intent(activity,
						VoteSuggestActivity.class);
				startActivity(toSuggest);
			}
		}

	}

	@Override
	protected String getAnalyticsTitle() {
		return "vote_list";
	}

}
