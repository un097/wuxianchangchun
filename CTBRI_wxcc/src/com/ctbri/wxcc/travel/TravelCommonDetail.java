package com.ctbri.wxcc.travel;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctbri.comm.util.DialogUtils;
import com.ctbri.comm.util._Utils;
import com.ctbri.wxcc.Constants;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseFragment;
import com.ctbri.wxcc.community.Constants_Community;
import com.ctbri.wxcc.community.Watcher;
import com.ctbri.wxcc.community.WatcherManager;
import com.ctbri.wxcc.community.WatcherManagerFactory;
import com.ctbri.wxcc.coupon.CouponDetailActivity;
import com.ctbri.wxcc.coupon.CouponDetailFragment;
import com.ctbri.wxcc.entity.CommonPoint;
import com.ctbri.wxcc.entity.TravelDetailBean;
import com.ctbri.wxcc.entity.TravelDetailBean.TravelDetail;
import com.ctbri.wxcc.widget.ImageLooperFragment;
import com.ctbri.wxcc.widget.ImageNavigator;
import com.ctbri.wxcc.widget.ImageNavigatorActivity;
import com.ctbri.wxcc.widget.LocateNavVersion;
import com.ctbri.wxcc.widget.LocateOldVersion;
import com.ctbri.wxcc.widget.TrangleView;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TravelCommonDetail extends BaseFragment {
	private ImageView iv_coupon, iv_coupon_tag;
	private TextView tv_title, tv_address, tv_introduction, tv_traffic,
			tv_time, tv_price, tv_coupon_title, tv_piece, tv_star, tv_validity,
			tv_feature, tv_phone, tv_others;
	private View ll_locate, rl_coupon , iv_locate;
	private String detailId;
	private int typeId;
	private ImageLoader imgloader;
	private TrangleView trangle;
	private TravelDetail data ;
	private int[][] showWidgets = new int[][] {
			{},
			// 景区
			{ R.id.rl_locaion_container, R.id.ll_introduction, R.id.ll_traffic,	R.id.ll_others },
			// 活动
			{ R.id.rl_locaion_container, R.id.ll_introduction, R.id.ll_others },
			// 食品
			{ R.id.rl_locaion_container, R.id.ll_introduction,R.id.ll_features, R.id.ll_others },
			// 特产
			{ R.id.ll_introduction } 
		};

	public static TravelCommonDetail newInstance(String id, int type_id) {
		TravelCommonDetail detailFragment = new TravelCommonDetail();
		Bundle args = new Bundle();
		args.putString(TravelContentDetail.KEY_DETAIL_ID, id);
		args.putInt(TravelListFragment.KEY_TYPEID, type_id);
		detailFragment.setArguments(args);
		return detailFragment;
	}
	
	private WatcherManager watcher;
	@Override
	public void onAttach(Activity activity_) {
		super.onAttach(activity_);
		if(activity_ instanceof WatcherManagerFactory){
			watcher = ((WatcherManagerFactory) activity_).getManager();
			watcher.addWatcher(new ShareWatcher());
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		if (args != null) {
			detailId = args.getString(TravelContentDetail.KEY_DETAIL_ID, "");
			typeId = args.getInt(TravelListFragment.KEY_TYPEID, -1);
		}
	}

	/**
	 * 初始化 图片滚动
	 */
	private void initImageLooper(String[] pics){
		ImageLooperFragment fragment = ImageLooperFragment.newInstance(pics);
//		fragment.setItemClickListener(imgItemClick);
		getChildFragmentManager().beginTransaction()
				.replace(R.id.frame_image_container, fragment).commit();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.travel_detail_layout, container,
				false);
//		iv_travel = (ImageView) v.findViewById(R.id.iv_detail_image);
//		iv_travel.setOnClickListener(new NavigatorListener());

		iv_coupon_tag = (ImageView) v.findViewById(R.id.iv_coupon_tag);

		iv_coupon = (ImageView) v.findViewById(R.id.iv_coupon_image);
		iv_coupon.setOnClickListener(new CouponImageListener());

		trangle = (TrangleView) v.findViewById(R.id.trangle_view);

		iv_locate = (ImageView) v.findViewById(R.id.iv_locate);
		ll_locate = v.findViewById(R.id.ll_locate);
		ll_locate.setEnabled(false);
		ll_locate.setOnClickListener(new LocateListener());
		// iv_locate.setOnClickListener(new LocateListener());

		tv_title = (TextView) v.findViewById(R.id.tv_detail_image_title);
		tv_address = (TextView) v.findViewById(R.id.tv_travel_address);
		tv_introduction = (TextView) v.findViewById(R.id.tv_introduction);
		tv_price = (TextView) v.findViewById(R.id.tv_price);
		tv_time = (TextView) v.findViewById(R.id.tv_time);
		tv_traffic = (TextView) v.findViewById(R.id.tv_traffic);
		tv_coupon_title = (TextView) v.findViewById(R.id.tv_coupon_image_title);
		tv_star = (TextView) v.findViewById(R.id.tv_star);
		if (typeId == TravelMainFragment.MENU_TRAVEL)
			tv_star.setVisibility(View.VISIBLE);
		tv_piece = (TextView) v.findViewById(R.id.tv_image_count);
		tv_validity = (TextView) v.findViewById(R.id.tv_valid_or_code);
		
		tv_phone = (TextView) v.findViewById(R.id.tv_phone);
		tv_phone.setOnClickListener(new PhoneClick());
		
		tv_feature = (TextView) v.findViewById(R.id.tv_feature);
		tv_others = (TextView) v.findViewById(R.id.tv_others);
		
		//获取 优惠券 layout
		rl_coupon = v.findViewById(R.id.rl_coupon);
		// 根据不同的类别，显示不同的界面
		showTypeWidgets(showWidgets[typeId], v);
		loadData();
		return v;
	}

	/**
	 * 显示隐藏的控件
	 * 
	 * @param ids
	 */
	private void showTypeWidgets(int ids[], View container) {
		for (int i = 0; i < ids.length; i++) {
			View item = container.findViewById(ids[i]);
			if (item != null)
				item.setVisibility(View.VISIBLE);
		}
	}

	private void loadData() {
		DialogUtils.showLoading(getFragmentManager());
		
		String url = Constants.METHOD_TRAVEL_CONTENT_DETAIL + "?content_id="
				+ detailId + "&type=" + typeId;
		request(url, new RequestCallback() {
			@Override
			public void requestSucc(String json) {
				Gson gson = new Gson();
				TravelDetailBean detailBean = gson.fromJson(json,
						TravelDetailBean.class);
				fillData(detailBean);
				
				DialogUtils.hideLoading(getFragmentManager());
			}

			@Override
			public void requestFailed(int errorCode) {
				DialogUtils.hideLoading(getFragmentManager());
			}
		});
	}

	private void initImageLoader() {
		if (imgloader != null)
			return;
		imgloader = ImageLoader.getInstance();
	}

	@SuppressLint("ResourceAsColor")
	private void fillData(TravelDetailBean bean) {
		if (bean != null) {
			initImageLoader();

			data = bean.getData();
			tv_title.setText(data.getTitle());
			tv_address.setText(data.getAdd());
			if (data.getPic_url().size() > 0) {

				String[] pics = data.getPic_url().toArray(new String[] {});
				initImageLooper(pics);
			}
			// 显示优惠券相关数据   如果，优惠券 id 不为空，就显示优惠券
			if (!TextUtils.isEmpty(data.getCoupon_id())) {
				iv_coupon.setTag(data.getCoupon_id());
				imgloader.displayImage(data.getCoupon_pic_url().trim(),
						iv_coupon, _Utils.DEFAULT_DIO);
				tv_coupon_title.setText(data.getCoupon_title());
				boolean isCoupon = data.getCategory().equals("0");
				trangle.setTrangleColor((isCoupon ? R.color.coupon_tag_color
						: R.color.party_tag_color));

				imgloader.displayImage("drawable://"
						+ (isCoupon ? R.drawable.icon_coupon
								: R.drawable.icon_activity), iv_coupon_tag);
				
				rl_coupon.setVisibility(View.VISIBLE);
			}
			//判断 位置如果不为空，则响应点击事件
			if (data.getLocation() != null && data.getLocation().size() > 0 && data.getLocation().get(0).length() > 2) {
				ll_locate.setEnabled(true);
				iv_locate.setEnabled(true);
			} else {
				ll_locate.setEnabled(true);
				iv_locate.setEnabled(false);
				ll_locate.setOnClickListener(new EmptyLocationListener());
			}
			//判断电话是否为空
			if(TextUtils.isEmpty(data.getTel()))
			{
				tv_phone.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.icon_phone_gray, 0);
				tv_phone.setOnClickListener(new EmptyPhoneListener());
				tv_phone.setEnabled(true);
			}else{
				tv_phone.setText(data.getTel());
				tv_phone.setEnabled(true);
			}

			tv_validity.setText(getString(R.string.tip_validity, data.getValidity()));

			tv_time.setText(data.getBusiness_hours());
			tv_introduction.setText(data.getIntro());
			tv_price.setText(data.getPrice());
			//如果交通数据 为空，隐藏交通控件
			if(TextUtils.isEmpty(data.getTraffic()))
				getView().findViewById(R.id.ll_traffic).setVisibility(View.GONE);
			else
				tv_traffic.setText(data.getTraffic());
			
			/* 添加判断条件， 内容为空时，同样隐藏 其他控件 */
			//如果其他数据为空， 隐藏其他控件
			List<String> otherStr = data.getOthers();
			if (otherStr != null && otherStr.size() != 0 && !TextUtils.isEmpty(otherStr.get(0)))
				tv_others.setText(otherStr.get(0));
			else
				getView().findViewById(R.id.ll_others).setVisibility(View.GONE);
			
			tv_feature.setText(data.getFeature());
			
			tv_piece.setText(getString(R.string.piece, data.getPic_url().size()));
			tv_star.setText(data.getTag());

		}
	}
	
	/**
	 * 位置数据为空时，触发该事件
	 * @author yanyadi
	 *
	 */
	class EmptyLocationListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			toast(R.string.msg_empty_address);
		}
		
	}
	/**
	 * 电话数据为空时，触发该事件
	 * @author yanyadi
	 *
	 */
	class EmptyPhoneListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			toast(R.string.msg_empty_telephone);
		}
		
	}
	/**
	 * 触发图片浏览事件
	 * 
	 * @author yanyadi
	 * 
	 */
	class NavigatorListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			String[] pics = (String[]) v.getTag();
			if (pics == null) {
				toast("没有图片");
				return;
			}
			Intent toNavigator = new Intent(activity, ImageNavigatorActivity.class);
			toNavigator.putExtra(ImageNavigator.KEY_PICS, pics);
			startActivity(toNavigator);
		}
	}

	class LocateListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent it = new Intent(activity, LocateNavVersion.class);
			ArrayList<CommonPoint> points = new ArrayList<CommonPoint>();
			for(String loc: data.getLocation()){
				CommonPoint cp =  CommonPoint.parseCommonPoint(loc ,",",data.getTitle());
				if(cp!=null)
				points.add(cp);
			}
			
			it.putExtra(LocateOldVersion.KEY_POINTS , points);
			startActivity(it);
		}
	}

	class CouponImageListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			String coupon_id = v.getTag() == null ? "" : v.getTag().toString();
			Intent toDetail = new Intent(activity, CouponDetailActivity.class);
			toDetail.putExtra(CouponDetailFragment.KEY_COUPON_ID, coupon_id);
			toDetail.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(toDetail);
		}

	}

	class PhoneClick implements OnClickListener {
		@Override
		public void onClick(View v) {
			String phone_num = (((TextView) v).getText()).toString();
			if(TextUtils.isEmpty(phone_num))
			{
				toast("没有电话信息");
				return;
			}
			Intent callPhone = new Intent(Intent.ACTION_DIAL);
			callPhone.setData(Uri.parse("tel://" + phone_num));
			startActivity(callPhone);
		}

	}

	@Override
	protected String getAnalyticsTitle() {
		return "travel_content_detail";
	}
	
	class ShareWatcher implements Watcher{

		@Override
		public void trigger(Object obj) {
			if(	data!=null)
			_Utils.shareAndCheckLogin(activity, data.getTitle(), Constants_Community.APK_DOWNLOAD_URL, data.getIntro(), _Utils.getDefaultAppIcon(activity));
		}

		@Override
		public int getType() {
			return TYPE_SHARE;
		}
	}
}
