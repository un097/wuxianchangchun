package com.ctbri.wxcc.vote;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctbri.comm.widget.SendCommentsFragment;
import com.ctbri.comm.widget.SendCommentsFragment.OnSendCommentsListener;
import com.ctbri.wxcc.Constants;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseFragment;
import com.ctbri.wxcc.community.CommentFragment;
import com.ctbri.wxcc.community.CommentFragment.CommentFragmentBuilder;
import com.ctbri.wxcc.community.CommunityDetailsActivity;
import com.ctbri.wxcc.community.Watcher;
import com.ctbri.wxcc.community.WatcherManager;
import com.ctbri.wxcc.community.WatcherManagerFactory;

public class VoteDetailContainerFragment extends BaseFragment {

	private ViewPager vp_fragments;
	private String investigation_id;
	private WatcherManager watcher;
	private TextView tv_title;
	private ImageView right_btn;
	@Override
	public void onAttach(Activity activity_) {
		super.onAttach(activity_);
		if(activity_ instanceof WatcherManagerFactory){
			watcher = ((WatcherManagerFactory) activity_).getManager();
		}
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(savedInstanceState!=null)
			investigation_id = savedInstanceState
			.getString(VoteDetailFragment.KEY_INVESTIGATION_ID);
		else
			investigation_id = activity.getIntent().getStringExtra(VoteDetailFragment.KEY_INVESTIGATION_ID);

	}
	/**
	 * 当 fragment 意外关闭时，存储本次打开的 community_id 
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(CommunityDetailsActivity.COMMUNITY_ID, investigation_id);
	}
	/**
	 * 通知 子组件 需要刷新评论列表
	 */
	private void notifyCommentRefreshed(){
		if(watcher!=null){
			watcher.trigger(Watcher.TYPE_REFRESH, null);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = (View) inflater.inflate(R.layout.vote_detail_container,container, false);
		vp_fragments = (ViewPager)v.findViewById(R.id.vp_community);
		vp_fragments.setAdapter(new CommunityAdapter(getChildFragmentManager()));
		vp_fragments.setOnPageChangeListener(new PageChangeListener());
		tv_title = (TextView)v.findViewById(R.id.action_bar_title);
		tv_title.setText(R.string.title_vote);
		v.findViewById(R.id.action_bar_left_btn).setOnClickListener(new ActionBarBackListener());
		
		right_btn = (ImageView)v.findViewById(R.id.action_bar_right_btn);
		right_btn.setImageResource(R.drawable.share_button_selector);
		right_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(watcher!=null)
					watcher.trigger(Watcher.TYPE_SHARE , null);
			}
		});
		return v;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initCommentsWidget();
	}
	
	private void initCommentsWidget(){
		String withParamsUrl = Constants.METHOD_INVESTIGATION_SEND_COMMENT + "?investigation_id=" + investigation_id;
		SendCommentsFragment newInstance = SendCommentsFragment.newInstance(withParamsUrl, investigation_id, 0);
		newInstance.setOnSendCommentsListener(new OnSendCommentsListener() {
			
			@Override
			public void onSendComments(int status) {
				switch (status) {
				case 0:
					toast("评论发布成功!!!");
					notifyCommentRefreshed();
					break;

				default:
					break;
				}
			}
		});
		newInstance.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				toast("切换评论页");
				vp_fragments.setCurrentItem(1);
			}
		});
		getChildFragmentManager().beginTransaction().replace(R.id.fragment_send_comments_layout, newInstance).commit();
	
	}
	class ActionBarBackListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			if(vp_fragments.getCurrentItem()==0)
				activity.finish();
			else
				vp_fragments.setCurrentItem(0);
			

			View focusView = v.getRootView().findFocus();
			if(focusView!=null)
			 focusView.clearFocus();
		}
		
	}
	class PageChangeListener implements OnPageChangeListener{

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
			
		}

		@Override
		public void onPageSelected(int position) {
			if(position==0)
				tv_title.setText(R.string.title_vote);
			else
				tv_title.setText(R.string.title_all_comment);
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			
		}
		
	}
	
	class CommunityAdapter extends FragmentPagerAdapter{

		public CommunityAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			CommentFragmentBuilder builder = CommentFragmentBuilder.newInstance(0)
				.setPraiseUrl(Constants.METHOD_INVESTIGATION_PRAISE)	
				.setReportUrl(Constants.METHOD_INVESTIGATION_REPORT)
				.setTitle(getString(R.string.title_vote));
			
			if(position == 0){
				String withParamsUrl = Constants.METHOD_INVESTIGATION_COMMENT 
						+ "?type=1&investigation_id=" + investigation_id;
				
				builder.setCommentsUrl(withParamsUrl)
				.setDetailFmtCls(VoteDetailFragment.class.getName())
				.setFmtId(CommentFragment.FRAGMENT_DETAIL);
				return builder.build();
				
			}else if(position == 1)
			{
				String withParamsUrl = Constants.METHOD_INVESTIGATION_COMMENT 
						+ "?type=0&investigation_id=" + investigation_id;
				
				
				builder.setCommentsUrl(withParamsUrl)
				.setHiddenDesc(false)
				.setFmtId(CommentFragment.FRAGMENT_COMMENT);
				
				return builder.build();
			}
			throw new IllegalArgumentException(this + " [ 参数异常！]");
		}
		@Override
		public int getCount() {
			return 2;
		}
		
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
