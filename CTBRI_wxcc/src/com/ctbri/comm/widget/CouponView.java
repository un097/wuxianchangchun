package com.ctbri.comm.widget;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;

import com.ctbri.comm.util._Utils;
import com.ctbri.comm.widget.CouponView.MainCoouponBean.Data.Item;
import com.ctbri.wxcc.Constants;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseFragment;
import com.ctbri.wxcc.community.BaseFragment.RequestCallback;
import com.ctbri.wxcc.coupon.CouponDetailActivity;
import com.ctbri.wxcc.coupon.CouponMainActivity;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.wookii.widget.ryg.utils.Utils;
//import cn.ffcs.external.share.view.CustomSocialShare;

public class CouponView extends BaseFragment{

	private Activity context;
	private ImageView first;
	private ImageView second;
	private ImageView thrid;
	private View mRecommendContainer;
	private List<Item> list;
	private List<ImageView> viewList = new ArrayList<ImageView>();

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		this.context = activity;
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.coupon_view, null);
		first = (ImageView)view.findViewById(R.id.main_coupon_first);
		second = (ImageView)view.findViewById(R.id.main_coupon_second);
		thrid = (ImageView)view.findViewById(R.id.main_coupon_third);
		viewList.add(first);
		viewList.add(second);
		viewList.add(thrid);
		mRecommendContainer = view.findViewById(R.id.ll_coupon_recommend);
		mRecommendContainer.getViewTreeObserver().addOnGlobalLayoutListener(mImageContainerLayout);
		view.findViewById(R.id.main_coupon_more).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent it = new Intent(context, CouponMainActivity.class);
				startActivity(it);
			}
		});
		requestData();
		return view;
	}
	private OnGlobalLayoutListener mImageContainerLayout = new OnGlobalLayoutListener() {
		@Override
		public void onGlobalLayout() {
			mRecommendContainer.getViewTreeObserver().removeGlobalOnLayoutListener(mImageContainerLayout);
			int both_width = getResources().getDimensionPixelSize(R.dimen.coupon_recommend_padding);
			int mWidth = mRecommendContainer.getWidth();
			int mHalfWidth = (mWidth) / 2;
			// 大图的宽度减去两个小图之间的间距后，宽度的 1/4 ，即是小图的高度
			int mSmallImgHeight = (mWidth - both_width) / 2 / 2;
			
			first.getLayoutParams().height = mHalfWidth;
			first.setLayoutParams(first.getLayoutParams());
			
			second.getLayoutParams().height = mSmallImgHeight;
			second.setLayoutParams(second.getLayoutParams());
			
			thrid.getLayoutParams().height = mSmallImgHeight;
			thrid.setLayoutParams(thrid.getLayoutParams());
		}
	};

	public void refresh(){
		requestData();
	}
	private void requestData() {

		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		/*
		 * pairs.add(new BasicNameValuePair("md5", MessageEditor
		 * .getHotLineMd5(context))); pairs.add(new
		 * BasicNameValuePair("user_id", "test"MessageEditor
		 * .getUserId(context)));
		 */

		request(Constants.METHOD_MAIN_COUPON, new RequestCallback() {

			@Override
			public void requestSucc(String json) {
				Gson gson = new Gson();
				MainCoouponBean data = gson.fromJson(json,
						MainCoouponBean.class);
				if (data != null && data.getData() != null) {
					List<Item> list = data.getData().getCoupon_list();
					if (list != null && list.size() != 0) {
						updateViewData(list);
					}
				}
			}

			@Override
			public void requestFailed(int errorCode) {
				
			}
		}, pairs);

	}
	
	protected void updateViewData(List<Item> list) {
		DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(false)
				.cacheOnDisc(true)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.build();
		ImageLoader imageLoader = ImageLoader.getInstance();
		ImageLoaderConfiguration createDefault = ImageLoaderConfiguration.createDefault(context);
		imageLoader.init(createDefault);
		this.list = list;
		int i = 0;
		for (final Item item : list) {
			ImageView imageView = null;
			try {
				if(i > 2) break;
				imageView = viewList.get(i);
			} catch (Exception e) {
				break;
			}
			imageLoader.displayImage(item.getCoupon_pic_url(), imageView, options);
			imageView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String coupon_id = item.getCoupon_id();
					Intent it = new Intent(context, CouponDetailActivity.class);
					it.putExtra("COUPON_ID", coupon_id);
					startActivity(it);
				}
			});
			i ++;
		}
	}

	class MainCoouponBean {
		private Data data;
		
		public Data getData() {
			return data;
		}

		public void setData(Data data) {
			this.data = data;
		}

		class Data {
			private List<Item> coupon_list;
			
			public List<Item> getCoupon_list() {
				return coupon_list;
			}

			public void setCoupon_list(List<Item> coupon_list) {
				this.coupon_list = coupon_list;
			}

			class Item{
				private String coupon_pic_url;
				private String coupon_id;
				public String getCoupon_pic_url() {
					return coupon_pic_url;
				}
				public void setCoupon_pic_url(String coupon_pic_url) {
					this.coupon_pic_url = coupon_pic_url;
				}
				public String getCoupon_id() {
					return coupon_id;
				}
				public void setCoupon_id(String coupon_id) {
					this.coupon_id = coupon_id;
				}
			}
		}
		
		
	}

	@Override
	protected boolean isEnabledAnalytics() {
		return false;
	}
	@Override
	protected String getAnalyticsTitle() {
		// TODO Auto-generated method stub
		return null;
	}
}
