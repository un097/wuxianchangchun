package com.ctbri.wxcc.media;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctbri.comm.util._Utils;
import com.ctbri.wxcc.Constants;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.audio.AudioCircelWidget;
import com.ctbri.wxcc.community.BaseFragment;
import com.ctbri.wxcc.entity.MediaBroadcastVideoBean;
import com.ctbri.wxcc.entity.MediaBroadcastVideoBean.Channel;
import com.google.gson.Gson;

public class BroadcastGridFragment extends BaseFragment {

	public static int img_width, img_height;
	private BroadcastWidget mWidget;

	protected String getListUrl() {
		return Constants.METHOD_VIDEO_BROADCAST_RECOM;
	}

	@Override
	protected String getAnalyticsTitle() {
		return null;
	}

	@Override
	protected boolean isEnabledAnalytics() {
		return false;
	}

	protected Class<MediaBroadcastVideoBean> getGsonClass() {
		return MediaBroadcastVideoBean.class;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mWidget = new BroadcastWidget(activity, false);
		return mWidget;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getData();
	}

	private void getData() {
		request(getListUrl(), new RequestCallback() {

			@Override
			public void requestSucc(String json) {
				Gson gson = new Gson();
				MediaBroadcastVideoBean bean = gson.fromJson(json, MediaBroadcastVideoBean.class);
				if (bean != null && bean.getData() != null) {
					List<Channel> list = bean.getData().getChannel_list();
					list.add(0, null);
					mWidget.update(bean, list, 3);
				}
			}

			@Override
			public void requestFailed(int errorCode) {

			}
		});
	}

	class BroadcastWidget extends AudioCircelWidget<MediaBroadcastVideoBean, Channel> implements AudioCircelWidget.ViewBinder<MediaBroadcastVideoBean, Channel> {
		private int[] ids = { R.id.tv_subtitle };

		public BroadcastWidget(Context context, AttributeSet attrs) {
			super(context, attrs);
			setHasTitle(true);
			setBinder(this);
			setItemViews(ids);
			setOnItemClickListener(mOnItemClicker);
		}

		public BroadcastWidget(Context context, boolean hasHead) {
			super(context);
			setHasTitle(hasHead);
			setBinder(this);
			setItemViews(ids);
			setOnItemClickListener(mOnItemClicker);
		}

		private OnItemClickListener<Channel> mOnItemClicker = new OnItemClickListener<MediaBroadcastVideoBean.Channel>() {
			@Override
			public void onItemClick(View item, Channel entity) {

				if (entity == null) {
					Intent toPlayList = new Intent(activity, VideoListActivity.class);
					toPlayList.putExtra("type_id", VodVideoListFragmet.TYPE_BROADCAST);
					startActivity(toPlayList);
				} else {
					Intent toLiveDetail = new Intent(activity, MediaPlayerActivity.class);
					toLiveDetail.putExtra("type_id", VodVideoListFragmet.TYPE_BROADCAST);
					toLiveDetail.putExtra("channel_id", entity.getChannel_id());
					startActivity(toLiveDetail);
				}
			}
		};

		@Override
		public int getItemLayout() {
			return R.layout.media_video_broadcast_grid_item;
		}

		@Override
		public void bindData(ImageView iv_cover, TextView tv_title, Channel data) {

		}

		@Override
		public void init(TextView tvTitle, TextView more, MediaBroadcastVideoBean t) {

		}

		@Override
		public void bindData(ImageView iv_cover, TextView tv_title, Channel data, View... v) {
			if (data != null) {
				TextView tv_subtitle = (TextView) v[0];
				tv_title.setText(data.getVideo_name());

				mImageLoader.displayImage(data.getVideo_url().trim(), iv_cover, _Utils.DEFAULT_DIO);
				tv_subtitle.setText(data.getChannel_name());
			} else {
				// iv_cover.setScaleType(ScaleType.FIT_XY);
				iv_cover.setImageResource(R.drawable.ic_zhibo);
				iv_cover.getViewTreeObserver().addOnGlobalLayoutListener(new LayoutListener(iv_cover));
			}
		}

		class LayoutListener implements OnGlobalLayoutListener {
			View target;

			public LayoutListener(View v) {
				target = v;
			}

			@Override
			public void onGlobalLayout() {
				final int mWidth = target.getWidth();
				final int mHeight = target.getHeight();
				if (img_height != 0 && mHeight == img_height)
					target.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				if (mHeight > img_height) {
					img_height = mHeight;
					img_width = mWidth;
				}
			}

		}
	}

}
