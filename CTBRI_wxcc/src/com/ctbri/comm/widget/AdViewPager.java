package com.ctbri.comm.widget;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;

import com.ctbri.comm.widget.AdViewPager.AdBean.Data.AdItem;
import com.ctbri.wxcc.Constants;
import com.ctbri.wxcc.CurrentBean;
import com.ctbri.wxcc.JsonWookiiHttpContent;
import com.ctbri.wxcc.MyBaseProtocol;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseFragment;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.viewpagerindicator.CirclePageIndicator;
import com.wookii.tools.net.WookiiHttpPost;

public class AdViewPager extends BaseFragment {

	private Activity context;
	private ViewPager viewPager;
	private CirclePageIndicator indcator;
	private OnItemClickListener listener;
	private MyPagerAdapter myPagerAdapter;
	private TextView title;
	// 跳转类型，0为调用内部组件显示，1为调用fufu",2 外部链接
	public static final String TYPE_WEB = "2";
	public static final String TYPE_FF = "1";
	private static final String TYPE_IN = "0";

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
		View view = inflater.inflate(R.layout.ad_view, null);
		title = (TextView) view.findViewById(R.id.ad_view_title);
		viewPager = (ViewPager) view.findViewById(R.id.ad_view_pager);
		indcator = (CirclePageIndicator) view
				.findViewById(R.id.ad_view_indcator);
		indcator.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				if (myPagerAdapter != null) {
					title.setText(myPagerAdapter.getPageTitle(arg0));
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		requestData();
		return view;
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		this.listener = listener;
	}

	public interface OnItemClickListener {
		public abstract void onItemClick(String type, String str);
	}

	public void refresh(){
		requestData();
	}
	private void requestData() {
		// TODO Auto-generated method stub
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		/*
		 * pairs.add(new BasicNameValuePair("md5", MessageEditor
		 * .getHotLineMd5(context))); pairs.add(new
		 * BasicNameValuePair("user_id", "test"MessageEditor
		 * .getUserId(context)));
		 */

		request(Constants.METHOD_MAIN_AD_LIST, new RequestCallback() {

			@Override
			public void requestSucc(String json) {
				Gson gson = new Gson();
				try {
					AdBean data = gson.fromJson(json, AdBean.class);
					if (data != null && data.getData() != null) {
						List<AdItem> ad_list = data.getData().getAd_list();
						if (ad_list != null && ad_list.size() != 0) {

							updateViewData(ad_list);
						}
					}
				} catch (Exception e) {
					/*Toast.makeText(getActivity(), "数据格式异常:" + e.getMessage(),
							Toast.LENGTH_SHORT).show();*/
				}
			}

			@Override
			public void requestFailed(int errorCode) {
				
			}
		}, pairs);
	}

	protected void updateViewData(List<AdItem> ad_list) {
		myPagerAdapter = new MyPagerAdapter(ad_list);
		viewPager.setAdapter(myPagerAdapter);
		indcator.setViewPager(viewPager);
		title.setText(ad_list.get(0).getAd_title());
	}

	protected class MyPagerAdapter extends PagerAdapter {

		private List<AdItem> data;
		public DisplayImageOptions options;
		public ImageLoader imageLoader;

		public MyPagerAdapter(List<AdItem> data) {
			options = new DisplayImageOptions.Builder().cacheInMemory(false)
					.cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565)
					.build();
			imageLoader = ImageLoader.getInstance();
			ImageLoaderConfiguration createDefault = ImageLoaderConfiguration
					.createDefault(context);
			imageLoader.init(createDefault);
			this.data = data;
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return data.get(position).getAd_title();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((ImageView) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			ImageView image = new ImageView(context);
			image.setScaleType(ScaleType.CENTER_CROP);
			image.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					String jump_type = data.get(position).getJump_type();
					String jump_string = data.get(position).getJump_string();
					if (!jump_type.equals(TYPE_IN)) {
						// 拋出去
						if (listener == null) {
							Toast.makeText(context, "没有注册监听",
									Toast.LENGTH_SHORT).show();
						} else {
							listener.onItemClick(jump_type, jump_string);
						}
					} else {
						// TODO
					}
				}
			});

			ImageLoader imgloader = ImageLoader.getInstance();
			String ad_pic_url = data.get(position).getAd_pic_url();
			imgloader.displayImage(ad_pic_url, image, options);
			container.addView(image);
			return image;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}

	protected class AdBean {

		public class Data {
			private List<AdItem> ad_list;

			public List<AdItem> getAd_list() {
				return ad_list;
			}

			public void setAd_list(List<AdItem> ad_list) {
				this.ad_list = ad_list;
			}

			public class AdItem {
				private String ad_id;
				private String ad_pic_url;
				private String ad_title;
				private String jump_type;
				private String jump_string;

				public String getAd_id() {
					return ad_id;
				}

				public void setAd_id(String ad_id) {
					this.ad_id = ad_id;
				}

				public String getAd_pic_url() {
					return ad_pic_url;
				}

				public void setAd_pic_url(String ad_pic_url) {
					this.ad_pic_url = ad_pic_url;
				}

				public String getAd_title() {
					return ad_title;
				}

				public void setAd_title(String ad_title) {
					this.ad_title = ad_title;
				}

				public String getJump_type() {
					return jump_type;
				}

				public void setJump_type(String jump_type) {
					this.jump_type = jump_type;
				}

				public String getJump_string() {
					return jump_string;
				}

				public void setJump_string(String jump_string) {
					this.jump_string = jump_string;
				}
			}
		}

		private Data data;

		public Data getData() {
			return data;
		}

		public void setData(Data data) {
			this.data = data;
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
