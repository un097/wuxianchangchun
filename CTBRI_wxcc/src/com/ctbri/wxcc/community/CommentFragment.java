package com.ctbri.wxcc.community;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ctbri.comm.util._Utils;
import com.ctbri.comm.widget.ComplaintDialog;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.entity.CommunityCommentBean;
import com.ctbri.wxcc.entity.CommunityCommentBean.CommunityComment;
import com.ctbri.wxcc.entity.PageModel;
import com.ctbri.wxcc.entity.PraiseBean;
import com.ctbri.wxcc.widget.LoadMorePTRListView;
import com.ctbri.wxcc.widget.LoadMorePTRListView.LoadMoreMode;
import com.ctbri.wxcc.widget.LoadMorePTRListView.OnLoadMoreListViewListener;
import com.ctbri.wxcc.widget.SelfRounderDisplayer;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.socom.Log;
import com.wookii.widget.ryg.utils.Utils;

@SuppressLint("InflateParams")
public class CommentFragment extends BaseFragment {
	/** 当前列表中获取数据 url **/
	public static final String KEY_LIST_URL = "list_url";
	/** 当前评论点选 url **/
	public static final String KEY_ZAN_URL = "zan_url";
	/** 当前的标题 **/
	public static final String KEY_TITLE = "comment_title";
	/** 当前的举报 url **/
	public static final String KEY_REPORT_URL = "report_url";
	/** 当前的 DetailFragment **/
	public static final String KEY_DETAILS_FRAGMENT = "detail_fragment";
	/** 当前的fragment 标示，以区分一个 activity 包含两个 CommentFragment 实例时 **/
	public static final String KEY_FRAGMENT_ID = "fragment_id";
	/** 是否隐藏 评论列表顶部的 描述信息 **/
	public static final String KEY_HIDDEN_COMEENT_DESC = "hidden_comment_desc";
	/** 是否隐藏 action bar **/
	public static final String KEY_HIDDEN_ACTION = "hidden_action_bar";
	/** 包含详细的 COMMENT **/
	public static final int FRAGMENT_DETAIL = 1000;
	/** 只显示评论的 COMMENT **/
	public static final int FRAGMENT_COMMENT = 1001;
	public static final String KEY_BUILD_CONFIG = "comment_build";

	private CommentFragmentBuilder build;
	private WatcherManager watcherManager;

	public static CommentFragment newInstance(CommentFragmentBuilder build) {
		CommentFragment cfragment = new CommentFragment();
		Bundle args = new Bundle();
		args.putSerializable(KEY_BUILD_CONFIG, build);
		cfragment.setArguments(args);
		return cfragment;

	}

	@Override
	public void onAttach(Activity activity_) {
		super.onAttach(activity_);
		if (!(activity_ instanceof CommentFragmentSomeListener))
			toast("Activity 必须实现 CommentFragmentSomeListener 接口");
		else
			this.backListener = (CommentFragmentSomeListener) activity_;
		// 如果当前的 activity 已经实现 CommentWatcher 接口
		if (activity_ instanceof WatcherManagerFactory) {
			watcherManager = ((WatcherManagerFactory) activity_).getManager();
			watcherManager.addWatcher(new RefreshWatcherImpl());
			watcherManager.addWatcher(new UpdateConmmentDescImpl());
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		build = (CommentFragmentBuilder) getSerialzeable(KEY_BUILD_CONFIG);
		pageModel = new PageModel(0, Constants_Community.PAGE_SIZE);
	}

	private LoadMorePTRListView lv_more;
	private ObjectAdapter<CommunityCommentBean.CommunityComment> commentsAdapter;
	private CommentFragmentSomeListener backListener;
	private PageModel pageModel;
	private TextView tvDescSubTitle, tvDescPubDate, tvDescTitle;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.common_comment_list, container, false);
		lv_more = (LoadMorePTRListView) v.findViewById(R.id.lv_community_comment);
		lv_more.setLoadMoreListener(new LoadMoreListenerImpl());
		lv_more.setOnRefreshListener(new RefreshListener());
		if (!build.hiddenActionBar) {
			((ViewStub) v.findViewById(R.id.vs_actionbar)).inflate();
			initActionBar(v);
		}

		// 如果 要展示详细信息
		if (build.detailFmtCls != null) {
			initHeaderDetail();
			// 并设置加载更多为 手动加载
			lv_more.setmMode(LoadMoreMode.MODE_CLICK_LOAD);

		} else {
			initCommentDesc();
		}
		return v;
	}

	private void initActionBar(View v) {
		if (v != null) {
			v.findViewById(R.id.action_bar_left_btn).setOnClickListener(new BtnBackListener());
			// 设置标题
			if (build.title != null)
				((TextView) v.findViewById(R.id.action_bar_title)).setText(build.title);
		}
	}

	/**
	 * 初始化当前 评论的描述信息
	 */
	private void initCommentDesc() {
		LayoutInflater inflater = activity.getLayoutInflater();
		View detailHeader = inflater.inflate(R.layout.common_comment_desc, null);
		detailHeader.findViewById(R.id.rl_comment_desc_container).setVisibility(build.hiddenDesc ? View.GONE : View.VISIBLE);

		tvDescSubTitle = (TextView) detailHeader.findViewById(R.id.tv_source);
		tvDescPubDate = (TextView) detailHeader.findViewById(R.id.tv_pubdate);
		tvDescTitle = (TextView) detailHeader.findViewById(R.id.tv_title);
		if (build.source != null)
			tvDescSubTitle.setText(build.source);
		if (build.pubDate != null)
			tvDescPubDate.setText(build.pubDate);
		if (build.subTitle != null)
			tvDescTitle.setText(build.subTitle);
		
		((TextView) detailHeader.findViewById(R.id.tv_comment_line)).setText(R.string.all_comment);

		lv_more.addHeader(detailHeader, null, false);
	}

	public void updateSourceText(String s) {
		if (tvDescSubTitle != null && s != null)
			tvDescSubTitle.setText(s);
		else
			build.source = s;
	}

	public void updatePubDateText(String s) {
		if (tvDescPubDate != null && s != null)
			tvDescPubDate.setText(s);
		else
			build.pubDate = s;
	}

	public void updateDescTitle(String s) {
		if (tvDescTitle != null && s != null)
			tvDescTitle.setText(s);
		else
			build.subTitle = s;
	}

	// 给当前的 ListView 添加Header
	private void initHeaderDetail() {
		LayoutInflater inflater = activity.getLayoutInflater();
		View detailHeader = inflater.inflate(R.layout.vote_detail_header, null);
		lv_more.addHeader(detailHeader, null, false);

		// 先设置 Adapter ，否则 listview 的 header 不会真正 add 。
		commentsAdapter = new CommentAdapter(activity, null);
		lv_more.setAdapter(commentsAdapter);

		Fragment details = Fragment.instantiate(activity, build.detailFmtCls);
		getChildFragmentManager().beginTransaction().replace(R.id.vote_detail_header, details).commit();
	}

	/**
	 * 提交评论的按钮的点击事件
	 * 
	 * @author yanyadi
	 * 
	 */
	// class BtnSendListener implements OnClickListener {
	// public void onClick(View v) {
	//
	// ArrayList<BasicNameValuePair> pairs = new
	// ArrayList<BasicNameValuePair>();
	// final String content = txt_comment.getText().toString();
	//
	// pairs.add(new BasicNameValuePair("content", content));
	//
	// request(Constants.METHOD_COMMUNITY_SEND_COMMENT,
	// new RequestCallback() {
	// public void requestSucc(String json) {
	// toast("评论成功!");
	// // 评论成功后，默认添加至评论列表的顶部
	// CommunityComment item = new CommunityComment();
	// item.setComment_zan_num("0");
	// item.setContent(content);
	// item.setCreate_time("刚刚");
	//
	// // 当前用户的名称
	// item.setUser_name("adsf");
	// // 当前用户的头像
	// item.setPic_url("");
	// // 清空评论控件的文本
	// txt_comment.setText("");
	//
	// commentsAdapter.addOne(item, 0);
	// commentsAdapter.notifyDataSetChanged();
	//
	// }
	//
	// @Override
	// public void requestFailed(int errorCode) {
	//
	// }
	// }, pairs);
	// }
	//
	// }

	/**
	 * 返回按钮的点击事件
	 * 
	 * @author yanyadi
	 * 
	 */
	class BtnBackListener implements OnClickListener {
		@Override
		public void onClick(View v) {

			if (backListener != null)
				backListener.onBack(build.fmtId);
		}

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// 加载评论列表
		loadData(build.commentsUrl, pageModel, false);
	}

	/**
	 * 依据 communit_id 加载 爆料详情
	 * 
	 * @param community_id
	 */
	private void loadData(String url, PageModel pm, final boolean isClearData) {
		url = pm.appendToTail(url);
		request(url, new RequestCallback() {
			@Override
			public void requestSucc(String json) {
				fillList(json, isClearData);
			}

			@Override
			public void requestFailed(int errorCode) {
				if (isClearData)
					lv_more.onRefreshComplete();
				else
					lv_more.showLoadMore();
			}
		});
	}

	/**
	 * 填充 评论列表
	 * 
	 * @param json
	 */
	protected void fillList(String json) {
		fillList(json, false);
	}

	/**
	 * 填充 评论列表
	 * 
	 * @param json
	 */
	protected void fillList(String json, boolean isClearData) {
		Gson gson = new Gson();
		CommunityCommentBean commentBean = gson.fromJson(json, CommunityCommentBean.class);

		if (commentsAdapter == null) {
			commentsAdapter = new CommentAdapter(activity, commentBean.getData().getCommList());
			lv_more.setAdapter(commentsAdapter);
		} else {
			if (isClearData)
				commentsAdapter.clearData();
			commentsAdapter.addAll(commentBean.getData().getCommList());
			commentsAdapter.notifyDataSetChanged();
		}

		if (isClearData)
			lv_more.onRefreshComplete();

		// 如果包含详细，listview footer不可见
		if (build.fmtId == FRAGMENT_DETAIL) {
			lv_more.hideFooter();
			// 如果数据列表为空，隐藏 commentLine
			View commentLine = lv_more.findViewById(R.id.ll_comment_line);
			if (commentsAdapter.getCount() == 0 && commentLine != null)
				commentLine.setVisibility(View.INVISIBLE);
			else
				commentLine.setVisibility(View.VISIBLE);
		}
		// 如果返回的数据已经到最后一条, 隐藏加载更多按钮
		if (commentBean.getData().getIs_end() == 0) {
			lv_more.hideLoadMore();
		} else
			lv_more.showLoadMore();
	}

	class CommentAdapter extends ObjectAdapter<CommunityCommentBean.CommunityComment> {

		private DisplayImageOptions dio;
		ImageLoader imgloader;
		private ZanAndReportListener zanAndReportListener;

		public CommentAdapter(Activity activity, List<CommunityComment> data) {
			super(activity, data);
			zanAndReportListener = new ZanAndReportListener();
			imgloader = ImageLoader.getInstance();
			dio = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_user).showImageForEmptyUri(R.drawable.default_user)
					.showImageOnFail(R.drawable.default_user).cacheInMemory(false).cacheOnDisc(true)
					// .considerExifParams(true)
					.bitmapConfig(Bitmap.Config.RGB_565)
					// 设置是否圆角
					.displayer(new SelfRounderDisplayer(Utils.dp2px(activity, 4), getResources())).build();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			CommentHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.common_comment_item2, parent, false);
				holder = new CommentHolder();
				convertView.setTag(holder);

				holder.iv_avtor = (ImageView) convertView.findViewById(R.id.iv_avator);
				holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
				holder.tv_pubDate = (TextView) convertView.findViewById(R.id.tv_pubdate);
				holder.tv_report = (TextView) convertView.findViewById(R.id.tv_report);
				holder.tv_userName = (TextView) convertView.findViewById(R.id.tv_username);
				holder.tv_zan = (TextView) convertView.findViewById(R.id.tv_zan);
				holder.iv_zan = (ImageView) convertView.findViewById(R.id.iv_zan);

				holder.rl_zan_container = convertView.findViewById(R.id.rl_zan_cancontainer);
				holder.rl_zan_container.setOnClickListener(zanAndReportListener);

				holder.tv_report.setOnClickListener(zanAndReportListener);

			} else {
				holder = (CommentHolder) convertView.getTag();
			}
			CommunityComment comment = getItem(position);
			fillItem(holder, comment);
			return convertView;
		}

		private void fillItem(CommentHolder holder, CommunityComment comment) {
			// 显示头像
			String picUrl = comment.getPic_url();
			if(picUrl == null){
				holder.iv_avtor.setImageResource(R.drawable.default_user);
				holder.iv_avtor.setTag(null);
			}else if (!picUrl.equals(holder.iv_avtor.getTag())) {
				imgloader.displayImage(picUrl, holder.iv_avtor, dio);
				holder.iv_avtor.setTag(picUrl);
			}
			holder.tv_content.setText(comment.getContent());
			holder.tv_pubDate.setText(comment.getCreate_time());
			holder.tv_userName.setText(comment.getUser_name());
			holder.tv_report.setTag(comment);

			holder.tv_zan.setText(comment.getComment_zan_num());
			holder.mComment = comment;
			// holder.tv_zan.setSelected("0".equals(comment.getStatus()));

			// holder.iv_zan.setTag(holder);
			// holder.iv_zan.setSelected("0".equals(comment.getStatus()));

			holder.rl_zan_container.setTag(holder);
		}
	}

	class CommentHolder {
		ImageView iv_avtor, iv_zan;
		View rl_zan_container;
		CommunityComment mComment;
		TextView tv_userName, tv_pubDate, tv_content, tv_zan, tv_report;
	}

	/**
	 * 返回按钮按下回调
	 * 
	 * @author yanyadi
	 * 
	 */
	public static interface CommentFragmentSomeListener {
		/**
		 * 点击 actionbar 返回按钮时，触发的事件
		 * 
		 * @param fragmentId
		 */
		void onBack(int fragmentId);

		/**
		 * 点击加载更多时，触发的事件
		 * 
		 * @param
		 * @return boolean 返回 true CommentFragment 将不再做后续的处理，<br />
		 *         返回 false ，CommentFragment 将执行默认的方法。
		 */
		boolean onClickLoadMore(int fragmentId, LoadMorePTRListView lv_more);
	}

	/**
	 * Listvew 加载下一页的回调事件
	 * 
	 * @author yanyadi
	 * 
	 */
	class LoadMoreListenerImpl implements OnLoadMoreListViewListener {
		@Override
		public void onLastItemClick(LoadMorePTRListView listview) {
			if (backListener == null || !backListener.onClickLoadMore(build.fmtId, lv_more)) {
				pageModel.start += Constants_Community.PAGE_SIZE;
				loadData(build.commentsUrl, pageModel, false);
			}
		}

		@Override
		public void onAutoLoadMore(LoadMorePTRListView list) {
			pageModel.start += Constants_Community.PAGE_SIZE;
			loadData(build.commentsUrl, pageModel, false);
		}
	}

	/***
	 * 点赞 和 举报 的回调事件
	 * 
	 * @author yanyadi
	 * 
	 */
	class ZanAndReportListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			int v_id = v.getId();
			CommunityComment comment;
			if (v_id == R.id.tv_report) {
				comment = (CommunityComment) v.getTag();
				report(comment);
				toast("请举报");
			} else if (v_id == R.id.rl_zan_cancontainer) {
				// 判断当前用户是否登录，未登录则跳转至登录窗口 by 闫亚迪 2015-3-10
				if (!_Utils.checkLoginAndLogin(activity)) {
					return;
				}
				CommentHolder holder = (CommentHolder) v.getTag();
				comment = holder.mComment;
				// 开始点赞动画
				playZanCartoon(holder.iv_zan);

				// 提交点赞数据
				zan(comment);

				comment.setStatus("0");
				int zanNumber = Integer.parseInt(comment.getComment_zan_num()) + 1;
				holder.tv_zan.setText(zanNumber + "");
				// comment.setComment_zan_num(String.valueOf(zanNumber));
				// commentsAdapter.notifyDataSetChanged();
			}
		}
	}

	private void playZanCartoon(View v) {
		Animation anim = AnimationUtils.loadAnimation(activity, R.anim.anim_praise_zoom);
		// v.setAnimation(anim);
		// anim.startNow();
		v.startAnimation(anim);
	}

	private void report(final CommunityComment comment) {
		ComplaintDialog complaint = ComplaintDialog.newInstance(build.reportUrl, comment.getComment_id());
		complaint.show(getChildFragmentManager(), "report_dialog");
	}

	private void zan(final CommunityComment comment) {
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("comment_id", comment.getComment_id()));
		request(build.praiseUrl, new RequestCallback() {
			public void requestSucc(String json) {
				// 此外应该更新 commentsAdapter 中点赞评论的点赞数量
				Gson gson = new Gson();
				PraiseBean totals = gson.fromJson(json, PraiseBean.class);
				comment.setComment_zan_num(totals.getData().getTotls());
				comment.setStatus("0");
				commentsAdapter.notifyDataSetChanged();
			}

			@Override
			public void requestFailed(int errorCode) {

			}
		}, params);
	}

	class RefreshListener implements OnRefreshListener<ListView> {
		@Override
		public void onRefresh(PullToRefreshBase<ListView> refreshView) {
			pageModel.start = 0;
			loadData(build.commentsUrl, pageModel, true);

			// 触发下拉刷新事件 by 闫亚迪 2015-03-10
			if (watcherManager != null)
				watcherManager.trigger(RefreshWatcher.TYPE_PULLTOREFRESH, null);
		}
	}

	/**
	 * 执行刷新操作
	 * 
	 * @author yanyadi
	 * 
	 */
	class RefreshWatcherImpl implements RefreshWatcher {
		@Override
		public void trigger(Object obj) {
			doRefresh();
		}

		@Override
		public int getType() {
			return TYPE_REFRESH;
		}

		@Override
		public void doRefresh() {
			pageModel.start = 0;
			loadData(build.commentsUrl, pageModel, true);
		}
	}

	/**
	 * 更新评论描述信息
	 * 
	 * @author yanyadi
	 * 
	 */
	class UpdateConmmentDescImpl implements Watcher {

		@Override
		public void trigger(Object obj) {
			@SuppressWarnings("unchecked")
			HashMap<String, String> data = (HashMap<String, String>) obj;
			updateDescTitle(data.get("title"));
			updatePubDateText(data.get("pubdate"));
			updateSourceText(data.get("source"));
		}

		@Override
		public int getType() {
			return TYPE_UPDATE_CONMENT_DESC;
		}

	}

	@Override
	protected String getAnalyticsTitle() {
		return "common_comment";
	}

	public static class CommentFragmentBuilder implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2869441097254714086L;

		String commentsUrl;
		String reportUrl;
		String praiseUrl;
		String title;
		String detailFmtCls;
		String subTitle;
		String pubDate;
		String source;

		public String getSource() {
			return source;
		}

		public void setSource(String source) {
			this.source = source;
		}

		public String getSubTitle() {
			return subTitle;
		}

		public void setSubTitle(String subTitle) {
			this.subTitle = subTitle;
		}

		public String getPubDate() {
			return pubDate;
		}

		public void setPubDate(String pubDate) {
			this.pubDate = pubDate;
		}

		int fmtId;
		boolean hiddenDesc = true;
		boolean hiddenActionBar = true;

		public boolean isHiddenActionBar() {
			return hiddenActionBar;
		}

		public CommentFragmentBuilder setHiddenActionBar(boolean hiddenActionBar) {
			this.hiddenActionBar = hiddenActionBar;
			return this;
		}

		public String getCommentsUrl() {
			return commentsUrl;
		}

		public CommentFragmentBuilder setCommentsUrl(String commentsUrl) {
			this.commentsUrl = commentsUrl;
			return this;
		}

		public String getReportUrl() {
			return reportUrl;
		}

		public CommentFragmentBuilder setReportUrl(String reportUrl) {
			this.reportUrl = reportUrl;
			return this;
		}

		public String getPraiseUrl() {
			return praiseUrl;
		}

		public CommentFragmentBuilder setPraiseUrl(String praiseUrl) {
			this.praiseUrl = praiseUrl;
			return this;
		}

		public String getTitle() {
			return title;
		}

		public CommentFragmentBuilder setTitle(String title) {
			this.title = title;
			return this;
		}

		public String getDetailFmtCls() {
			return detailFmtCls;
		}

		public CommentFragmentBuilder setDetailFmtCls(String detailFmtCls) {
			this.detailFmtCls = detailFmtCls;
			return this;
		}

		public int getFmtId() {
			return fmtId;
		}

		public CommentFragmentBuilder setFmtId(int fmtId) {
			this.fmtId = fmtId;
			return this;
		}

		public boolean isHiddenDesc() {
			return hiddenDesc;
		}

		public CommentFragmentBuilder setHiddenDesc(boolean hiddenDesc) {
			this.hiddenDesc = hiddenDesc;
			return this;
		}

		private CommentFragmentBuilder(int id) {
			fmtId = id;
		}

		public static CommentFragmentBuilder newInstance(int fmtId) {
			return new CommentFragmentBuilder(fmtId);
		}

		public CommentFragment build() {
			return CommentFragment.newInstance(this);
		}
	}
}
