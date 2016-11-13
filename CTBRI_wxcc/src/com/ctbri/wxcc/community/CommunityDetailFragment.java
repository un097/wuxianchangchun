package com.ctbri.wxcc.community;

import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctbri.comm.util.DialogUtils;
import com.ctbri.comm.util._Utils;
import com.ctbri.wxcc.Constants;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.entity.CommunityDetailBean;
import com.ctbri.wxcc.entity.CommunityDetailBean.CommunityDetail;
import com.ctbri.wxcc.widget.DetailsWebView;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CommunityDetailFragment extends BaseFragment {

	public static final String KEY_HIDDEN_COMMENT_LINE = "comment_line";
	
	String community_id;
	DetailsWebView wv_details;
	TextView tv_userName, tv_pubDate, tv_category,tv_commentline;
	ImageView iv_avator;
	private DisplayImageOptions dio;
	ImageLoader imgloader;
	private WatcherManager watcherManager;
	private CommunityDetail detail;
	
	@Override
	public void onAttach(Activity activity_) {
		super.onAttach(activity_);
		if(activity_ instanceof WatcherManagerFactory)
		{
			watcherManager = ((WatcherManagerFactory)activity_).getManager();
			watcherManager.addWatcher(new ShareWatcher());
			watcherManager.addWatcher(new PullToRefreshWatcher());
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent it = activity.getIntent();
		if (it != null)
			community_id = it
					.getStringExtra(CommunityDetailsActivity.COMMUNITY_ID);
		else
			community_id = savedInstanceState
					.getString(CommunityDetailsActivity.COMMUNITY_ID);
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

		View v = (View) inflater.inflate(R.layout.community_detail_fragment,
				container, false);

		wv_details = (DetailsWebView) v.findViewById(R.id.wv_community_details);
		
		tv_category = (TextView) v.findViewById(R.id.tv_category);
		tv_pubDate = (TextView) v.findViewById(R.id.tv_pubdate);
		tv_userName = (TextView) v.findViewById(R.id.tv_username);
		iv_avator = (ImageView) v.findViewById(R.id.iv_avator);
		tv_commentline =(TextView) v.findViewById(R.id.tv_comment_line);
		// 是否隐藏 评论 分隔线
		if(activity.getIntent().getBooleanExtra(KEY_HIDDEN_COMMENT_LINE, false)){
			v.findViewById(R.id.tv_comment_line).setVisibility(View.GONE);
		}
		return v;
	}

	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		imgloader = ImageLoader.getInstance();
		dio = new DisplayImageOptions.Builder()
		.cacheOnDisc(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
		loadData(community_id);
	}
	/**
	 * 依据 communit_id  加载 爆料详情
	 * @param community_id
	 */
	private void loadData(String community_id) {
		String withParamsUrl = Constants.METHOD_COMMUNITY_DETAILE
				+ "?community_id=" + community_id;
		
		DialogUtils.showLoading(getFragmentManager());
		
		request(withParamsUrl, new RequestCallback() {
			@Override
			public void requestSucc(String json) {
				Gson gson = new Gson();
				fillDetails(gson.fromJson(json, CommunityDetailBean.class));
				DialogUtils.hideLoading(getFragmentManager());
			}

			@Override
			public void requestFailed(int errorCode) {
				//此处应该监听  comment list 中的下拉刷新事件。
				DialogUtils.hideLoading(getFragmentManager());
			}
		});
	}
	private void setCommentCount(String count){
		View v_count = activity.findViewById(R.id.send_comments_count);
		if(v_count!=null)
			((TextView)v_count).setText(count);
	}
	/**
	 * 填充爆料详情
	 * @param detailBean
	 */
	private void fillDetails(CommunityDetailBean detailBean) {
		if (detailBean != null) {
			detail = detailBean.getData();
			String content_url = detail.getContent().trim();
			wv_details.loadUrl(content_url);
			imgloader.displayImage(detail.getPic_url().trim(), iv_avator,dio);
			
//			content_url = "http://219.141.189.60:8080/ctwifi-api/resources/upload/views/communitydetail.html";
//			
			tv_category.setText(detail.getCategory().toUpperCase(Locale.getDefault()));
			tv_pubDate.setText(detail.getDate_publish());
//			
			tv_userName.setText(detail.getUser_name());
			
			setCommentCount(detail.getComment_num());
			
			fillCommentDesc(detail);
		}
	}
	
	private void fillCommentDesc(CommunityDetail detail){
		if(watcherManager!=null){
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("title", detail.getTitle());
			data.put("source", detail.getDate_publish());
			watcherManager.trigger(Watcher.TYPE_UPDATE_CONMENT_DESC, data);
		}
	}
	class ShareWatcher implements Watcher{

		@Override
		public void trigger(Object obj) {
			if(detail!=null)
				// 爆料社区，分享的地址，更改为 无线长春  下载地址
			_Utils.shareAndCheckLogin(activity, detail.getTitle(), Constants_Community.APK_DOWNLOAD_URL, getString(R.string.share_content_community), _Utils.getDefaultAppIcon(activity));
		}

		@Override
		public int getType() {
			return TYPE_SHARE;
		}
	}
	/**
	 * 添加在下拉刷新时，更新当前详细数据。
	 * @author yanyadi
	 *
	 */
	class PullToRefreshWatcher implements Watcher {

		@Override
		public void trigger(Object obj) {
			loadData(community_id);
		}

		@Override
		public int getType() {
			return TYPE_PULLTOREFRESH;
		}

	}
	
	@Override
	protected String getAnalyticsTitle() {
		return "community_detail";
	}
	
}
