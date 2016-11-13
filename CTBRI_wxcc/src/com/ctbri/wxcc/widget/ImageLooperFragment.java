package com.ctbri.wxcc.widget;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctbri.comm.util._Utils;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseFragment;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ImageLooperFragment extends BaseFragment {

	private List<Pic> pic_list; 
	
	private InfinityViewPager vp_images;
	private ImagesAdapter img_adapter;
	private ImageLoader imgloader;
	/**
	 * 默认的滚动间隔
	 */
	private static final int DEFAULT_LOOP_INTERVAL = 5000;
	
	public static ImageLooperFragment newInstance(ArrayList<Pic> pics){
		ImageLooperFragment fragment = new ImageLooperFragment();
		Bundle data = new Bundle();
		data.putSerializable("pic_list", pics);
		fragment.setArguments(data);
		
		return fragment;
	}
	public static ImageLooperFragment newInstance(String[] urls){
		ArrayList<Pic> pics = Pic.urls2List(urls);
		return newInstance(pics);
	}
	@SuppressWarnings("unchecked")
	@Override
	public void onAttach(Activity activity_) {
		super.onAttach(activity_);
		pic_list = (ArrayList<Pic>) getSerialzeable("pic_list");
	}
	
	
	private OnItemClickListener mItemClickListener;
	
	
	public OnItemClickListener getItemClickListener() {
		return mItemClickListener;
	}
	public void setItemClickListener(OnItemClickListener mItemClickListener) {
		this.mItemClickListener = mItemClickListener;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.travel_image_looper_fragment,
				container, false);
		vp_images = (InfinityViewPager) v.findViewById(R.id.vp_images);
		img_adapter = new ImagesAdapter();
		vp_images.setAdapter(img_adapter);
		InfinityCirclePageIndicator pageIndicator = (InfinityCirclePageIndicator)v.findViewById(R.id.circle_pageindicator);
		pageIndicator.setViewPager(vp_images);
		return v;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		fillData();
	}

	private void fillData() {
		if (pic_list != null) {
			img_adapter.addData(pic_list);
			img_adapter.notifyDataSetChanged();
			vp_images.setAutoLoop(DEFAULT_LOOP_INTERVAL);
		}
	}

	class ImagesAdapter extends InfinityAdapter {
		private List<Holder> view_pool = new LinkedList<Holder>();
		private List<Pic> pics;
		private OnClickListener imageClick = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mItemClickListener != null)
					mItemClickListener.onClick(v, v.getTag());
			}
		};
		public ImagesAdapter(List<Pic> list) {
			if(list==null)
				this.pics = new ArrayList<Pic>();
			else
				this.pics = list;
			
			imgloader = ImageLoader.getInstance();
		}
		

		public ImagesAdapter() {
			this(new ArrayList<Pic>());
		}

		public void addData(List<Pic> list) {
			this.pics.addAll(list);
		}

		

		@Override
		public int getRealCount() {
			return pics.size();
		}

		private Holder getViewByPool() {
			if (view_pool.size() != 0) {
//				LogS.i("ImageLooperFragment","获取view " + view_pool.size());
				return view_pool.remove(0);
			}
			return null;
		}

		@Override
		public Object getView(ViewGroup container, int position) {
			Holder holder = getViewByPool();
			Pic pic = pics.get(position);
			if (holder == null) {
				View view = activity.getLayoutInflater().inflate(R.layout.travel_image_looper_item, container, false);
				
				holder = new Holder();
				holder.container = view;
				holder.iv = (ImageView) view.findViewById(R.id.iv_travel);
				holder.tv = (TextView) view.findViewById(R.id.tv_title);
				holder.iv.setOnClickListener(imageClick);
			}
			if(TextUtils.isEmpty( pic.title ))
				holder.tv.setVisibility(  View.GONE );
			else{
				holder.tv.setVisibility(  View.VISIBLE);
				holder.tv.setText(pic.title);
			}
			holder.iv.setTag(pic.data);
			imgloader.displayImage(pic.url.trim(), holder.iv, _Utils.DEFAULT_DIO);
			container.addView(holder.container);
			return holder;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			Holder holder = (Holder)object;
			container.removeView(holder.container);
			
			holder.iv.setImageDrawable(null);
			
			view_pool.add(holder);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == ((Holder)object).container;
		}
		class Holder{
			View container;
			ImageView iv;
			TextView tv;
		}
	}
	
	public static class Pic implements Serializable{
		
		public static ArrayList<Pic> urls2List(String[]urls){
			ArrayList<Pic> list = new ArrayList<ImageLooperFragment.Pic>();
			if(urls==null || urls.length==0)return list;
			for(String url: urls){
				list.add(new Pic(url));
			}
			return list;
		}
		
		private static final long serialVersionUID = -2250768150667686221L;
		
		public Pic() {
			super();
		}
		public Pic(String title, String url) {
			super();
			this.title = title;
			this.url = url;
		}
		public Pic(String url) {
			this.url = url;
		}
		String title;
		String url;
		Object data;
		
		public Object getData() {
			return data;
		}
		public void setData(Object data) {
			this.data = data;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
	}
	
	
	/**
	 * 点击其中的列表时，触发该事件
	 * @author yanyadi
	 *
	 */
	public static interface OnItemClickListener{
		void onClick(View v, Object data);
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
