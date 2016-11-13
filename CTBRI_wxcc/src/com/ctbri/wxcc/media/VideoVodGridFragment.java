package com.ctbri.wxcc.media;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ctbri.wxcc.Constants;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseFragment;
import com.ctbri.wxcc.entity.MediaVodVideoBean;
import com.ctbri.wxcc.entity.MediaVodVideoBean.VodGroup;
import com.google.gson.Gson;

public class VideoVodGridFragment extends BaseFragment{

	private LayoutInflater inflater;
	// 显示每个子类别的你容器
	private LinearLayout mContainer;
	protected String getListUrl() {
		return Constants.METHOD_VIDEO_VOD_RECOM;
	}

	protected int getLayoutResId() {
		return R.layout.media_video_category_layout;
	}

	@Override
	protected String getAnalyticsTitle() {
		return null;
	}

	@Override
	protected boolean isEnabledAnalytics() {
		return false;
	}

	

	protected Class<MediaVodVideoBean> getGsonClass() {
		return MediaVodVideoBean.class;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContainer = (LinearLayout) inflater.inflate(R.layout.media_vod_divider, container, false);
		this.inflater = inflater;
		return mContainer;
	}

	private void loadData() {
		request(getListUrl(), new RequestCallback() {
			@Override
			public void requestSucc(String json) {
				Gson gson = new Gson();
				MediaVodVideoBean travelBean = gson.fromJson(json,
						getGsonClass());
				fillData(travelBean, false);
			}

			@Override
			public void requestFailed(int errorCode) {
			}
		});
	}

	private void fillData(MediaVodVideoBean bean, boolean isClear) {
		List<VodGroup> groups = getEntitys(bean);
		
		if (groups != null) {
			for (VodGroup vGroup : groups) {
				VideoVodWidget mItem = (VideoVodWidget)inflater.inflate(R.layout.media_vod_widget_layout, mContainer,false);
				mItem.update(vGroup, vGroup.getVod_list(), 3);
//				TextView tvTitle = (TextView) mItem.findViewById(R.id.tv_category_tittle);
//				tvTitle.setText(vGroup.getGroup_name());
//				
//				TextView tvMore = (TextView) mItem.findViewById(R.id.tv_category_more);
//				tvMore.setTag(vGroup);
//				tvMore.setOnClickListener(mMoreListener);
//				
//				MenuGridView grid = (MenuGridView)mItem.findViewById(R.id.grid);
//				grid.setAdapter(new VodItemAdapter(getActivity(), vGroup.getVod_list()));
//				grid.setOnItemClickListener(mItemClick);
				mContainer.addView(mItem);
			}
		}

	}
	
	protected List<VodGroup> getEntitys(MediaVodVideoBean bean) {
		if (bean.getData() != null)
			return bean.getData().getVod_group();
		return null;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		loadData();
	}



}
