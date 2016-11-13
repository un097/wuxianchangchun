package com.ctbri.wxcc.community;

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
import com.ctbri.wxcc.community.CommentFragment.CommentFragmentBuilder;

public class CommunityDetailContainerFragment extends BaseFragment {

	private ViewPager vp_community;
	private String community_id;
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
			community_id = savedInstanceState
			.getString(CommunityDetailsActivity.COMMUNITY_ID);
		else
			community_id = activity.getIntent().getStringExtra(CommunityDetailsActivity.COMMUNITY_ID);

	}
	/**
	 * 当 fragment 意外关闭时，存储本次打开的 community_id 
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(CommunityDetailsActivity.COMMUNITY_ID, community_id);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = (View) inflater.inflate(R.layout.community_details_container,container, false);
		vp_community = (ViewPager)v.findViewById(R.id.vp_community);
		vp_community.setAdapter(new CommunityAdapter(getChildFragmentManager()));
		vp_community.setOnPageChangeListener(new PageChangeListener());
		tv_title = (TextView)v.findViewById(R.id.action_bar_title);
		tv_title.setText("爆料详情");
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
		String withParamsUrl = Constants.METHOD_COMMUNITY_SEND_COMMENT+"?community_id=" + community_id;
		SendCommentsFragment newInstance = SendCommentsFragment.newInstance(withParamsUrl, community_id, 0);
		newInstance.setOnSendCommentsListener(new OnSendCommentsListener() {
			
			@Override
			public void onSendComments(int status) {
				switch (status) {
				case 0:
					toast("评论发表成功!!");
					notifyCommentRefreshed();
					break;

				default:
					toast("评论发表失败!!");
					break;
				}
			}
		});
		newInstance.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				toast("切换评论页");
				vp_community.setCurrentItem(1);
			}
		});
		getChildFragmentManager().beginTransaction().replace(R.id.fragment_send_comments_layout, newInstance).commit();
	
	}
	/**
	 * 通知 子组件 需要刷新评论列表
	 */
	private void notifyCommentRefreshed(){
		if(watcher!=null)
		watcher.trigger(Watcher.TYPE_REFRESH,null);
	}
	class PageChangeListener implements OnPageChangeListener{

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
			
		}

		@Override
		public void onPageSelected(int position) {
			if(position==0)
				tv_title.setText("爆料详情");//R.string.title_community
			else
				tv_title.setText(R.string.title_all_comment);
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			
		}
		
	}
	class ActionBarBackListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			if(vp_community.getCurrentItem()==0)
				activity.finish();
			else
				vp_community.setCurrentItem(0);
			
			View focusView = v.getRootView().findFocus();
			if(focusView!=null)
			 focusView.clearFocus();
		}
		
	}
	
	class CommunityAdapter extends FragmentPagerAdapter{

		public CommunityAdapter(FragmentManager fm) {
			super(fm);
		}
		
		@Override
		public Fragment getItem(int position) {
			
			CommentFragmentBuilder builder = CommentFragmentBuilder.newInstance(0)						
					.setTitle(getString(R.string.title_community))
					.setPraiseUrl(Constants.METHOD_COMMUNITY_PRAISE)
					.setReportUrl(Constants.METHOD_COMMUNITY_REPORT);
			if(position == 0){
				String withParamsUrl = Constants.METHOD_COMMUNITY_COMMENT
						+ "?type=1&community_id=" + community_id;
				builder.setFmtId(CommentFragment.FRAGMENT_DETAIL)
				.setCommentsUrl(withParamsUrl)
				.setDetailFmtCls(CommunityDetailFragment.class.getName())
				.setHiddenDesc(true);
				
				return builder.build();
			}else if(position == 1)
			{
				String withParamsUrl = Constants.METHOD_COMMUNITY_COMMENT
						+ "?type=0&community_id=" + community_id;
				builder.setFmtId(CommentFragment.FRAGMENT_COMMENT)
				.setCommentsUrl(withParamsUrl)
				.setHiddenDesc(false);
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
	protected boolean isEnabledAnalytics() {
		return false;
	}
	@Override
	protected String getAnalyticsTitle() {
		return null;
	}
}
