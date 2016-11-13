package com.ctbri.wxcc.vote;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import cn.ffcs.external.share.view.WeiBoSocialShare;

import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseActivity;
import com.ctbri.wxcc.community.CommentFragment;
import com.ctbri.wxcc.community.CommentFragment.CommentFragmentSomeListener;
import com.ctbri.wxcc.community.DefaultWatchManager;
import com.ctbri.wxcc.community.WatcherManager;
import com.ctbri.wxcc.community.WatcherManagerFactory;
import com.ctbri.wxcc.widget.LoadMorePTRListView;

public class VoteDetailActivity extends BaseActivity implements CommentFragmentSomeListener, WatcherManagerFactory{

	private WatcherManager defaultManager = new DefaultWatchManager();
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_vote_detail);
	}

	@Override
	public void onBack(int id) {
		//如果是第二个
		if(id==CommentFragment.FRAGMENT_COMMENT){
		ViewPager vp_commnuity = (ViewPager)findViewById(R.id.vp_community);
		if(vp_commnuity != null)
			vp_commnuity.setCurrentItem(0, true);
		
		}else if(id==CommentFragment.FRAGMENT_DETAIL){
			finish();
		}
	}
	@Override
	public void onBackPressed() {
		ViewPager vp_commnuity = (ViewPager)findViewById(R.id.vp_community);
		if(vp_commnuity!=null){
			if(vp_commnuity.getCurrentItem()==0)
				this.finish();
			else
				vp_commnuity.setCurrentItem(0,true);
		}else
			super.onBackPressed();
	}

	
	@Override
	public boolean onClickLoadMore(int fragmentId, LoadMorePTRListView lv_more) {
		ViewPager vp_commnuity = (ViewPager)findViewById(R.id.vp_community);
		if(vp_commnuity!=null){
				vp_commnuity.setCurrentItem(1,true);
				return true;
		}
		return false;
	}

	@Override
	public WatcherManager getManager() {
		return defaultManager;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(WeiBoSocialShare.mSsoHandler != null){
			WeiBoSocialShare.mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}
}
