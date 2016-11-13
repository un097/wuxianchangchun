package com.ctbri.wxcc.travel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ctbri.comm.util._Utils;
import com.ctbri.wxcc.Constants;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseFragment;
import com.ctbri.wxcc.entity.TravelGallery;
import com.ctbri.wxcc.entity.TravelGallery.Pic;
import com.ctbri.wxcc.widget.ImageNavigator;
import com.ctbri.wxcc.widget.ImageNavigatorActivity;
import com.ctbri.wxcc.widget.InfinityAdapter;
import com.ctbri.wxcc.widget.InfinityCirclePageIndicator;
import com.ctbri.wxcc.widget.InfinityViewPager;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ImageLooperFragment extends BaseFragment {

	private InfinityViewPager vp_images;
	private String img_url;
	private ImagesAdapter img_adapter;
	private ImageLoader imgloader;
	private TravelGallery tg;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.travel_image_looper_fragment,
				container, false);
		vp_images = (InfinityViewPager) v.findViewById(R.id.vp_images);
		img_adapter = new ImagesAdapter();
		vp_images.setAdapter(img_adapter);
		InfinityCirclePageIndicator pageIndicator = (InfinityCirclePageIndicator) v
				.findViewById(R.id.circle_pageindicator);
		pageIndicator.setViewPager(vp_images);

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		img_url = Constants.METHOD_TRAVEL_MAIN_GALLERY;
		loadPicList();
	}

	private void loadPicList() {
		request(img_url, new RequestCallback() {
			@Override
			public void requestSucc(String json) {
				Gson gson = new Gson();
				tg = gson.fromJson(json, TravelGallery.class);
				fillData(tg);
			}

			@Override
			public void requestFailed(int errorCode) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void fillData(TravelGallery tg) {
		if (tg != null) {
			img_adapter.addData(tg.getData().getPic_list());
			img_adapter.notifyDataSetChanged();
			vp_images.setAutoLoop(5000);
		}
	}

	class ImagesAdapter extends InfinityAdapter {
		private List<View> view_pool = new LinkedList<View>();

		public ImagesAdapter(List<Pic> list) {
			this.pics = list;
			imgloader = ImageLoader.getInstance();
		}

		public ImagesAdapter() {
			this(new ArrayList<TravelGallery.Pic>());
		}

		public void addData(List<Pic> list) {
			this.pics.addAll(list);
		}

		private List<Pic> pics;

		@Override
		public int getRealCount() {
			return pics.size();
		}

		private View getViewByPool() {
			if (view_pool.size() != 0) {
				// LogS.i("ImageLooperFragment","获取view " + view_pool.size());
				return view_pool.remove(0);
			}
			return null;
		}

		@Override
		public Object getView(ViewGroup container, int position) {
			View view = getViewByPool();
			Pic pic = pics.get(position);
			ImageView iv = null;
			if (view == null) {
				view = activity.getLayoutInflater().inflate(
						R.layout.travel_image_looper_item, container, false);
				iv = (ImageView)view.findViewById(R.id.iv_travel);
				// 需要变更，不再跳转到图片浏览界面  by yyd 205-04-2
//				iv.setOnClickListener(new ToImageNavigator());
				view.setTag(iv);
			} else {
				iv = (ImageView) view.getTag();
			}
			// int imgId = position%2==0 ? R.drawable.travel1 :
			// R.drawable.travel2;
			// imgloader.displayImage("drawable://"+ imgId, iv,dio);
			imgloader.displayImage(pic.getPic_url().trim(), iv, _Utils.DEFAULT_DIO);
			// iv.setImageResource(imgId);
			container.addView(view);
			// view.setTag(pic);
			return view;
		}
		
		class ToImageNavigator implements OnClickListener{

			@Override
			public void onClick(View v) {
				String[] pics = null;
				if(tg!=null && tg.getData()!=null){
					 List<Pic> list = tg.getData().getPic_list();
					 pics = new String[list.size()];
					 for(int i=0;i < pics.length;i++){
						 pics[i] = list.get(i).getPic_url();
					 }
				}
				if (pics == null) {
					toast("没有图片");
					return;
				}
				Intent toNavigator = new Intent(activity,
						ImageNavigatorActivity.class);
				toNavigator.putExtra(ImageNavigator.KEY_PICS, pics);
				startActivity(toNavigator);
			}
			
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			View imgContainer = (View) object;
			container.removeView(imgContainer);

			ImageView iv = (ImageView) imgContainer.getTag();
			iv.setImageDrawable(null);

			view_pool.add(imgContainer);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
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
