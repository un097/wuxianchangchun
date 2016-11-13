package com.ctbri.wxcc.widget;

import java.util.LinkedList;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher.OnViewTapListener;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.ctbri.comm.util._Utils;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseFragment;
import com.ctbri.wxcc.entity.CommonHolder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingProgressListener;
import com.viewpagerindicator.CirclePageIndicator;

public class ImageNavigator extends BaseFragment {

	public static final String KEY_PICS = "pics";
	
 	private	ViewPager vp_images;
 	private String [] pics;
	private ImageLoader imgloader;
	private int pic_count;
	private CirclePageIndicator circleIndicator;
	private int screenWidth;
	public static final ImageNavigator newInstance(String[] pics){
		Bundle data = new Bundle();
		data.putStringArray(KEY_PICS, pics);

		ImageNavigator in = new ImageNavigator();
		in.setArguments(data);
		return in;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle data = getArguments();
		if(data!=null){
			pics = data.getStringArray(KEY_PICS);
			pic_count = pics.length;
		}
		
		screenWidth = getResources().getDisplayMetrics().widthPixels;
		imgloader = ImageLoader.getInstance();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View v = inflater.inflate(R.layout.common_image_navigator_layout, container, false);
		vp_images = (ViewPager) v.findViewById(R.id.vp_image_navigator);
		vp_images.setAdapter(new ImageAdapter(pics));
		
		
		circleIndicator = (CirclePageIndicator) v.findViewById(R.id.circle_page_indicator);
		circleIndicator.setViewPager(vp_images);
//		vp_images.setOnPageChangeListener(new PageChangeListener());
		
//		tv_title = (TextView) v.findViewById(R.id.action_bar_title);
//		tv_title.setText("1/"+ pic_count);
		
//		actionbar = v.findViewById(R.id.action_bar);
//		
//		v.findViewById(R.id.action_bar_left_btn).setOnClickListener(new FinishClickListener());
		return v;
	}
	
	private OnViewTapListener mPhotoTabListener = new OnViewTapListener(){
		@Override
		public void onViewTap(View view, float x, float y) {
			activity.finish();
		}
	};
	
	class ImageAdapter extends PagerAdapter{
		
		LayoutInflater inflater;
		private String[] pics;
		private LinkedList<CommonHolder> viewCache;
		public ImageAdapter(String[] pics) {
			this.pics = pics;
			inflater = activity.getLayoutInflater();
			viewCache = new LinkedList<CommonHolder>();
		}
		
		@Override
		public int getCount() {
			
			return pics==null ? 0 : pics.length;
		}
		
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			CommonHolder holder = null;// getView();
			
			if(holder == null){
				holder = new CommonHolder();
				holder.v = inflater.inflate(R.layout.common_image_navigator_item, container, false);
//				holder.v.setOnClickListener(finishListener);
				
				PhotoView photoView = (PhotoView)holder.v.findViewById(R.id.iv_image);
				photoView.setOnViewTapListener(mPhotoTabListener);
				holder.iv = photoView;
//				holder.iv.setOnClickListener(new ImageViewClicker());
				holder.v1 = holder.v.findViewById(R.id.progress_loading);
			}
			imgloader.displayImage(pics[position].trim(), holder.iv, _Utils.DEFAULT_DIO , new ImageLoadingListener(holder) );
			container.addView(holder.v);
			return holder;
		}
		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view==((CommonHolder)object).v;
		}
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			CommonHolder holder = (CommonHolder)object;
			container.removeView(holder.v);
//			holder.iv.destroyDrawingCache();
//			holder.iv.setImageDrawable(null);
//			viewCache.add(holder);
		}
	}
	class LoadingProcessListener implements ImageLoadingProgressListener{

		@Override
		public void onProgressUpdate(String imageUri, View view, int current,
				int total) {
			
		}
		
	}
	
	class ImageViewClicker  implements OnClickListener{

		@Override
		public void onClick(View v) {
		}
		
	}
	class ImageLoadingListener implements com.nostra13.universalimageloader.core.assist.ImageLoadingListener{
		private CommonHolder holder;
		public ImageLoadingListener(CommonHolder holder) {
			this.holder = holder;
		}
		@Override
		public void onLoadingStarted(String imageUri, View view) {
			holder.v1.setVisibility(View.VISIBLE);
		}

		@Override
		public void onLoadingFailed(String imageUri, View view,
				FailReason failReason) {
			holder.v1.setVisibility(View.GONE);
		}

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			holder.v1.setVisibility(View.GONE);
			
		}
		
		private void resetImageSize(Bitmap loadedImage){
			BitmapDrawable bd = new BitmapDrawable(getResources(), loadedImage);
			int dwidth = bd.getIntrinsicWidth();
			int dheight = bd.getIntrinsicHeight();
			int vwidth = holder.iv.getWidth();
			vwidth = vwidth == 0 ? screenWidth : vwidth;
			float scale = (float)vwidth / (float)dwidth;
			LayoutParams  lp = holder.iv.getLayoutParams();
			lp.height = (int)( dheight * scale);
			holder.iv.setLayoutParams(lp);
		}
		

		@Override
		public void onLoadingCancelled(String imageUri, View view) {
			holder.v1.setVisibility(View.GONE);
		}
		
	} 
	@Override
	protected String getAnalyticsTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isEnabledAnalytics() {
		return false;
	}
	
//	private void showActionbar(){
//		AnimatorBean bean = new AnimatorBean(actionbar);
//		Animator anim = ObjectAnimator.ofInt(bean, "height", 0,mActionbarHeight);
//		anim.setDuration(300L);
//		anim.setInterpolator(new AccelerateDecelerateInterpolator());
//		anim.start();
//		actionbar_is_visiable = !actionbar_is_visiable;
//	}
//	private void hideActionbar(){
//		AnimatorBean bean = new AnimatorBean(actionbar);
//		Animator anim = ObjectAnimator.ofInt(bean, "height", mActionbarHeight,0);
//		anim.setDuration(300L);
//		anim.setInterpolator(new AccelerateDecelerateInterpolator());
//		anim.start();
//		actionbar_is_visiable = !actionbar_is_visiable;
//	}
//	
//	class ShowActionbarListener implements OnClickListener{
//
//		@Override
//		public void onClick(View v) {
//			if(actionbar_is_visiable){
//				hideActionbar();
//			}else{
//				showActionbar();
//			}
//		}
//		
//	}
}
