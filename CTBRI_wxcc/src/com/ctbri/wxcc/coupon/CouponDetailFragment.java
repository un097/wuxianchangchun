package com.ctbri.wxcc.coupon;

import java.util.ArrayList;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctbri.comm.util.DialogUtils;
import com.ctbri.comm.util._Utils;
import com.ctbri.wxcc.Constants;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseFragment;
import com.ctbri.wxcc.community.Constants_Community;
import com.ctbri.wxcc.entity.CommonPoint;
import com.ctbri.wxcc.entity.CouponDetailBean;
import com.ctbri.wxcc.entity.CouponDetailBean.CouponDetail;
import com.ctbri.wxcc.widget.DetailsWebView;
import com.ctbri.wxcc.widget.LocateNavVersion;
import com.ctbri.wxcc.widget.LocateOldVersion;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CouponDetailFragment extends BaseFragment {
	public static final String KEY_COUPON_ID = "COUPON_ID";
	private CouponDetail data;
	private DisplayImageOptions dio;
	private TextView tv_more;
	private DetailsWebView mWebView;
	String coupon_id;
	// "状态0我要领取，1已领取，2已兑换，3抢光了，4已过期",
	private int[] obtainButtonText = { R.string.coupon_obtain,
			R.string.coupon_obtained, R.string.coupon_converted,
			R.string.coupon_empty, R.string.coupon_expired };

	public static final CouponDetailFragment newInstance(String coupon_id) {
		Bundle data = new Bundle();
		data.putString(KEY_COUPON_ID, coupon_id);
		CouponDetailFragment f = new CouponDetailFragment();
		f.setArguments(data);
		return f;
	}

	@Override
	public void onAttach(Activity activity_) {
		super.onAttach(activity_);
		dio = new DisplayImageOptions.Builder().cacheOnDisc(true).cacheInMemory(true).build();
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		coupon_id = getArgs(KEY_COUPON_ID);
	}

	private ImageView iv_detail_image, iv_locate, iv_phone, right_btn ;
	private TextView tv_title, tv_marks, tv_valid, tv_locate,
			tv_marks_tip;
	private View line_more;
	private Button btn_obtain;
	String lat, lng;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.coupon_detail_fragment, container,
				false);
		iv_detail_image = (ImageView) v.findViewById(R.id.iv_detail_image);

		iv_locate = (ImageView) v.findViewById(R.id.iv_locate);
		iv_locate.setOnClickListener(new LocateListener());
		iv_locate.setEnabled(false);
		
		tv_locate = (TextView) v.findViewById(R.id.tv_locate);
		tv_locate.setOnClickListener(new LocateListener());
		tv_locate.setEnabled(false);
		
		iv_phone = (ImageView) v.findViewById(R.id.iv_call);
		iv_phone.setOnClickListener(new CallPhoneListener());
		iv_phone.setEnabled(false);

		
		tv_marks = (TextView) v.findViewById(R.id.tv_marks);
		tv_title = (TextView) v.findViewById(R.id.tv_detail_image_title);
		tv_valid = (TextView) v.findViewById(R.id.tv_valid);
		tv_marks_tip = (TextView) v.findViewById(R.id.tv_marks_tip);
		tv_more = (TextView) v.findViewById(R.id.tv_more_address);
		
		tv_more.setOnClickListener(new JumpToMoreAddress());
		line_more = v.findViewById(R.id.line_more_address);
		
		btn_obtain = (Button) v.findViewById(R.id.btn_obtain);
		btn_obtain.setEnabled(false);
		btn_obtain.setOnClickListener(new ObtainListener());
		
		((TextView) v.findViewById(R.id.action_bar_title))
				.setText(R.string.title_coupon_detail);
		v.findViewById(R.id.action_bar_left_btn).setOnClickListener(
				new FinishClickListener());
		right_btn = (ImageView)v.findViewById(R.id.action_bar_right_btn);
		right_btn.setImageResource(R.drawable.share_button_selector);
		right_btn.setEnabled(false);
		right_btn.setOnClickListener(new ShareListener());
		
		mWebView = (DetailsWebView)v.findViewById(R.id.wv_coupon_detail);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		loadData();
	}
	class JumpToMoreAddress implements OnClickListener{

		@Override
		public void onClick(View v) {
			Intent it = new Intent(activity, MoreAddressActivity.class );
			it.putExtra("coupon_id", data.getCoupon_id());
			startActivity(it);
		}
		
	}
	private void loadData() {
		
		DialogUtils.showLoading(getFragmentManager(), new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				activity.finish();
			}
		});
		String url = Constants.METHOD_COUPON_CONTENT_DETAIL + "?coupon_id="
				+ coupon_id;
		request(url, new RequestCallback() {

			@Override
			public void requestSucc(String json) {
				Gson gson = new Gson();
				CouponDetailBean bean = gson.fromJson(json,
						CouponDetailBean.class);
				fillData(bean);
				DialogUtils.hideLoading(getFragmentManager());
			}
			@Override
			public void requestFailed(int errorCode) {
				DialogUtils.hideLoading(getFragmentManager());
			}
		});
	}

	private void fillData(CouponDetailBean bean) {
		if (bean != null) {
			data = bean.getData();
			// 状态码
			int status = data.getStatus();
			switch (status) {
			case 1:
			case 2:
			case 4:
				tv_marks.setText(data.getCode());
				tv_marks_tip.setText(R.string.tip_validate_code_tip);
				break;
			default:

				tv_marks.setTextColor(getResources()
						.getColor(R.color.text_gray));
				tv_marks.setText(data.getMarks());
			}

//			tv_coupon_detail.setText(data.getContent());
//			mWebView.getSettings().setDefaultTextEncodingName("utf-8");
//			mWebView.loadData(data.getContent(), "text/html; charset=utf-8", null);
			if(data.getContent()!=null)
			mWebView.loadUrl(data.getContent());
			tv_locate.setText(data.getAdd());

			tv_title.setText(data.getTitle());
			tv_valid.setText(data.getValidity());

			if (!TextUtils.isEmpty(data.getLocation())) {
				iv_locate.setEnabled(true);
				tv_locate.setEnabled(true);
			} else {
				iv_locate.setEnabled(false);
				tv_locate.setEnabled(false);
			}

			ImageLoader.getInstance().displayImage(data.getPic_url().trim(),
					iv_detail_image,dio);
			
			iv_phone.setEnabled(!TextUtils.isEmpty(data.getTel()));
			right_btn.setEnabled(true);
			
			btn_obtain.setEnabled(status == 0);
			// "状态0我要领取，1已领取，2已兑换，3抢光了，4已过期",
			if (status < obtainButtonText.length)
				btn_obtain.setText(obtainButtonText[status]);
			
			//判断是否存在 多个地址
			if(data.getIs_more()==0){
				tv_more.setText(getString(R.string.tip_more_address, data.getCount()));
			}else{
				tv_more.setVisibility(View.GONE);
				line_more.setVisibility(View.GONE);
			}
			

		}
	}

	class LocateListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if(data==null )return;
			Intent it = new Intent(activity, LocateNavVersion.class);
			ArrayList<CommonPoint> points = new ArrayList<CommonPoint>();
			CommonPoint point = CommonPoint.parseCommonPoint(
					data.getLocation(), ",", data.getAdd());
			if (point != null)
				points.add(point);
			it.putExtra(LocateOldVersion.KEY_POINTS, points);
			startActivity(it);
			// 统计 购物券店铺位置 点击量
			postClickEvent("E_C_pageName_couponLocationClick");
		}

	}

	class CallPhoneListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent call = new Intent(Intent.ACTION_DIAL);
			call.setData(Uri.parse("tel://" + data.getTel()));
			startActivity(call);
		}
	}

	class ObtainListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if(_Utils.checkLoginAndLogin(activity)){
			Intent obtainIntent = new Intent(activity,
					CouponObtainActivity.class);
			obtainIntent.putExtra(CouponObtainFragment.KEY_BEAN, data);
			startActivity(obtainIntent);
//			activity.finish();
			}
		}
	}
	
	class ShareListener implements OnClickListener{

		@Override
		public  void onClick(View v) {
			if(data==null )return;
//				_Utils.shareAndCheckLogin(activity,data.getTitle(), Constants_Community.APK_DOWNLOAD_URL, getString(R.string.share_content_coupon), _Utils.getDefaultAppIcon(activity));
				_Utils.share(activity,data.getTitle(), Constants_Community.APK_DOWNLOAD_URL, getString(R.string.share_content_coupon), data.getPic_url().trim());
			}
		
	}
	@Override
	protected String getAnalyticsTitle() {
		return "coupon_detail";
	}


}
