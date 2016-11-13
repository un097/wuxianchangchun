package com.ctbri.wxcc.community;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ctbri.comm.util.ImageLoaderInstance;
import com.ctbri.wxcc.Constants;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.entity.CommunityBean;
import com.ctbri.wxcc.entity.PageModel;
import com.ctbri.wxcc.entity.CommunityBean.Community;
import com.ctbri.wxcc.widget.LoadMorePTRListView;
import com.ctbri.wxcc.widget.LoadMorePTRListView.OnLoadMoreListViewListener;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnPullEventListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.State;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.wookii.tools.comm.LogS;

public class CommunityFragmentList extends BaseFragment implements
		OnItemClickListener {
	public static final String KEY_NAME = "name";
	public static final String KEY_COLUMN_ID = "column_id";

	private LoadMorePTRListView communitysList;
	private CommunityAdapter communitysAdapter;
	private PageModel page;
	private String colId;
	private View mView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mView == null) {
			View v = inflater
					.inflate(R.layout.community_list, container, false);
			communitysList = (LoadMorePTRListView) v
					.findViewById(R.id.pullToRefreshListView1);
			communitysList.setMode(Mode.PULL_FROM_START);
			communitysList.setOnItemClickListener(this);
			communitysList.setLoadMoreListener(new LoadMoreListenerImpl());
			communitysList.setOnRefreshListener(new PullEventListenerImpl());
			mView = v;
			communitysList.showHeaderLoading();
		} else {
			if (mView.getParent() != null)
				((ViewGroup) mView.getParent()).removeView(mView);
		}
		return mView;
	}

	@Override
	public void onAttach(Activity activity_) {
		super.onAttach(activity_);
		colId = getArguments().getString(KEY_COLUMN_ID);
		page = new PageModel(0, Constants_Community.PAGE_SIZE);
	}

	private void getCommunityList(String colId, PageModel page,
			final boolean isClearData) {
		// ============================== 默认参数 column_id =1
		// =====================================================//
		String withParamsUrl = Constants.METHOD_COMMUNITY_LIST + "?column_id="
				+ colId;
		withParamsUrl = page.appendToTail(withParamsUrl);

		request(withParamsUrl, new RequestCallback() {
			@Override
			public void requestSucc(String json) {
				fillCommunitysList(json, isClearData);
			}

			@Override
			public void requestFailed(int errorCode) {
				if (isClearData)
					communitysList.onRefreshComplete();
				else
					communitysList.showLoadMore();
			}
		});
	}

	private void getCommunityList(String colId, PageModel page) {
		getCommunityList(colId, page, false);
	}

	/**
	 * 填充 爆笑社区 列表
	 * 
	 * @param json
	 */
	private void fillCommunitysList(String json, boolean isClearData) {
		Gson gson = new Gson();
		CommunityBean data = gson.fromJson(json, CommunityBean.class);
		if (communitysAdapter == null || isClearData) {
			if (communitysAdapter != null) {
				communitysAdapter.clearData();
				communitysAdapter.addAll(data.getData().getNews());
				communitysAdapter.notifyDataSetChanged();
			} else {
				communitysAdapter = new CommunityAdapter(activity, data
						.getData().getNews());
				communitysList.setAdapter(communitysAdapter);
			}
		} else {
			communitysAdapter.addAll(data.getData().getNews());
			communitysAdapter.notifyDataSetChanged();
		}
		// 如果返回的数据已经到最后一条, 隐藏加载更多按钮
		if (data.getData().getIs_end() == 0)
			communitysList.hideLoadMore();
		else
			communitysList.showLoadMore();
		// 如果下拉刷新触发的事件
		if (isClearData)
			communitysList.onRefreshComplete();
	}

	/**
	 * Communitys Adapter
	 * 
	 * @author yanyadi
	 * 
	 */
	class CommunityAdapter extends ObjectAdapter<Community> {
		private LayoutInflater inflater;
		private DisplayImageOptions dio;
		private ImageLoadingListener loadingListener;
		/** 预览图片的 真实宽度和高度 **/
		private int height, width = height = -1;
		ImageLoader imgloader;

		public CommunityAdapter(Activity activity, List<Community> data_) {
			super(activity, data_);
			inflater = getActivity().getLayoutInflater();
			loadingListener = new ImageLoadingListener();
			imgloader = ImageLoaderInstance.getInstance(activity);
			dio = new DisplayImageOptions.Builder()
					.showImageForEmptyUri(R.drawable.icon_default_image_place_holder)
					.showImageOnFail(R.drawable.icon_default_image_place_holder)
					.showImageOnLoading(R.drawable.icon_default_image_place_holder)
					.cacheInMemory(false)
					.cacheOnDisc(true)
					.bitmapConfig(Bitmap.Config.RGB_565).build();
		}

		private void initImageSize(ViewGroup parent) {
			Resources res = getResources();
			// 外层元素的 左右间距，加上 三个 imageView 的间距
			int padding = res
					.getDimensionPixelOffset(R.dimen.community_list_item_padding)
					* 2
					+ res.getDimensionPixelOffset(R.dimen.community_preview_image_padding)
					* 2;
			width = ((Double) Math.floor((parent.getWidth() - padding) / 3.0F))
					.intValue();
			height = Math.round(width / 4.0F * 3);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			CommunityHolder holder = null;
			if (convertView == null) {
				if (height == -1) {
					initImageSize(parent);
				}
				convertView = inflater.inflate(R.layout.community_list_item,
						parent, false);
				holder = new CommunityHolder();
				convertView.setTag(holder);
				holder.previewContainer = convertView
						.findViewById(R.id.ll_preview_container);
				holder.tvUserName = (TextView) convertView
						.findViewById(R.id.tv_username);
				holder.tvCommentCount = (TextView) convertView
						.findViewById(R.id.tv_comment_count);
				holder.tvPubDate = (TextView) convertView
						.findViewById(R.id.tv_pubdate);
				holder.tvContent = (TextView) convertView
						.findViewById(R.id.tv_content);

				holder.ivAvator = (ImageView) convertView
						.findViewById(R.id.iv_avator);
				holder.priviews[0] = (ImageView) convertView
						.findViewById(R.id.iv_community_first);
				holder.priviews[1] = (ImageView) convertView
						.findViewById(R.id.iv_community_second);
				holder.priviews[2] = (ImageView) convertView
						.findViewById(R.id.iv_community_third);
				for (int i = 0; i < holder.priviews.length; i++) {
					LayoutParams lp = holder.priviews[i].getLayoutParams();
					lp.width = width;
					lp.height = height;
				}
				LogS.i("width", " contaienr Width= " + width + " height "
						+ height);

			} else {
				holder = (CommunityHolder) convertView.getTag();
			}
			bindValue(holder, getItem(position));
			return convertView;
		}

		private void bindValue(CommunityHolder holder, Community data) {

			String head_url = data.getPic_url().trim();
			// head_url =
			// "http://apps2.bdimg.com/store/static/kvt/ab0be1c8c7e02b6c61123a0530882e4f.jpg";
			// 显示默认的头像
			imgloader.displayImage(head_url, holder.ivAvator, dio);
			holder.tvCommentCount.setText(data.getComment_num());
			holder.tvContent.setText(data.getContent());
			holder.tvPubDate.setText(data.getDate_publish());
			holder.tvUserName.setText(data.getUser_name());
			holder.communityId = data.getCommunity_id();
			if (data.getPreviews().size() > 0) {
				holder.previewContainer.setVisibility(View.VISIBLE);
				for (int i = 0; i < holder.priviews.length; i++) {
					ImageView tmpImg = holder.priviews[i];
					tmpImg.setVisibility(View.INVISIBLE);
					if (data.getPreviews().size() > i) {
						imgloader.displayImage(
								data.getPreviews().get(i).trim(), tmpImg, dio,
								loadingListener);
						tmpImg.setVisibility(View.VISIBLE);
					} else {
						tmpImg.setImageDrawable(null);
					}
				}
			} else {
				holder.previewContainer.setVisibility(View.GONE);
			}

		}

	}

	class ImageLoadingListener implements
			com.nostra13.universalimageloader.core.assist.ImageLoadingListener {

		@Override
		public void onLoadingStarted(String imageUri, View view) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onLoadingFailed(String imageUri, View view,
				FailReason failReason) {

		}

		private Animation createAnim() {
			return AnimationUtils
					.loadAnimation(activity, R.anim.image_alpha_in);
		}

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			Animation anim = view.getAnimation() == null ? createAnim() : view
					.getAnimation();
			view.startAnimation(anim);
		}

		@Override
		public void onLoadingCancelled(String imageUri, View view) {

		}

	}

	class CommunityHolder {
		TextView tvUserName, tvPubDate, tvCommentCount, tvContent;
		View previewContainer;
		ImageView ivAvator;
		ImageView priviews[] = new ImageView[3];
		String communityId;
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View item, int position,
			long id) {
		LogS.i("CommunityList onClic", item.getTag().toString());
		CommunityHolder holder = (CommunityHolder) item.getTag();

		Intent it = new Intent(getActivity(), CommunityDetailsActivity.class);
		// 把该item 的 id 转入 Community details 页面
		it.putExtra(CommunityDetailsActivity.COMMUNITY_ID, holder.communityId);
		// 把评论的数量传入 Community details 页面
		// it.putExtra(name, value)
		startActivity(it);
	}

	/**
	 * 加载更多 事件监听函数
	 * 
	 * @author yanyadi
	 * 
	 */
	class LoadMoreListenerImpl implements OnLoadMoreListViewListener {

		@Override
		public void onLastItemClick(LoadMorePTRListView listview) {
			page.start += Constants_Community.PAGE_SIZE;
			getCommunityList(colId, page);
		}

		@Override
		public void onAutoLoadMore(LoadMorePTRListView list) {
			page.start += Constants_Community.PAGE_SIZE;
			getCommunityList(colId, page);
		}
	}

	class PullEventListenerImpl implements OnPullEventListener<ListView>,
			OnRefreshListener<ListView> {

		@Override
		public void onPullEvent(PullToRefreshBase<ListView> refreshView,
				State state, Mode direction) {
		}

		@Override
		public void onRefresh(PullToRefreshBase<ListView> refreshView) {
			page.start = 0;
			getCommunityList(colId, page, true);
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
