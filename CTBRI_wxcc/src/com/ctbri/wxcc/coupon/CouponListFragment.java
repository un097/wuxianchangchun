package com.ctbri.wxcc.coupon;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctbri.wxcc.Constants;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.entity.CouponsBean;
import com.ctbri.wxcc.entity.CouponsBean.Coupon;
import com.ctbri.wxcc.travel.CommonList;
import com.ctbri.wxcc.widget.LoadMorePTRListView;
import com.ctbri.wxcc.widget.TrangleView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CouponListFragment extends
		CommonList<CouponsBean, CouponsBean.Coupon> {
	/**
	 * 优惠券类别<br />
	 * 0：全部 。<br/>
	 * 1：我的
	 */
	public static final String KEY_TYPE = "coupon_type";
	public static final String KEY_HAS_TITLE = "coupon_has_title";
	public static final String KEY_TITLE = "coupon_title";

	private LayoutInflater inflater;

	public static final CouponListFragment newInstance(String type) {
		return newInstance(type, false, "") ;
	}
	public static final CouponListFragment newInstance(String type, boolean has_title, String title) {
		Bundle data = new Bundle();
		data.putString(KEY_TYPE, type);
		data.putBoolean(KEY_HAS_TITLE, has_title);
		data.putString(KEY_TITLE, title);
		CouponListFragment f = new CouponListFragment();
		f.setArguments(data);
		return f;
	}

	private String type, title;
	private boolean has_title;
	private String [] statusTexts;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		type = getArgs(KEY_TYPE, "");
		title = getArgs(KEY_TITLE, "");
		has_title = getArgsBoolean(KEY_HAS_TITLE, false);
		statusTexts = getResources().getStringArray(R.array.statusText);
	}

	@Override
	protected String getListUrl() {
		return Constants.METHOD_COUPON_CONTENT_LIST + "?type=" + type;
	}

	@Override
	protected Class<CouponsBean> getGsonClass() {
		return CouponsBean.class;
	}

	@Override
	protected List<Coupon> getEntitys(CouponsBean bean) {
		return bean.getData().getCoupon_list();
	}

	@Override
	protected boolean isEnd(CouponsBean bean) {
		return bean.getData().getIs_end() == 0;
	}

	@Override
	protected boolean initHeaderDetail(LoadMorePTRListView lv_list,
			LayoutInflater inflater) {
		this.inflater = inflater;
		return false;
	}

	@SuppressLint("ResourceAsColor")
	@Override
	protected View getListItemView(int position, View convertView,
			ViewGroup parent, Coupon data, ImageLoader imgloader,
			DisplayImageOptions dio) {
		CouponItemHolder holder = null;
		if (convertView == null) {
			holder = new CouponItemHolder();
			convertView = inflater.inflate(R.layout.coupon_main_list_item,
					parent, false);
			convertView.setTag(holder);
			holder.iv_coupon = (ImageView) convertView
					.findViewById(R.id.iv_coupon_image);
			holder.iv_tag = (ImageView) convertView
					.findViewById(R.id.iv_coupon_tag);
			holder.tv_status = (TextView) convertView
					.findViewById(R.id.tv_status);
			holder.tv_title = (TextView) convertView
					.findViewById(R.id.tv_title);
			holder.tv_valid = (TextView) convertView
					.findViewById(R.id.tv_valid_or_code);
			holder.trangle = (TrangleView) convertView
					.findViewById(R.id.trangle_view);
			
			holder.tv_status.setVisibility(type.equals("1") ? View.VISIBLE : View.GONE);
		} else
			holder = (CouponItemHolder) convertView.getTag();
		
		imgloader.displayImage(data.getPic_url().trim(), holder.iv_coupon,dio);
		
		holder.tv_title.setText(data.getTitle());
		if (type.equals("0"))
		{
			holder.tv_valid.setText(getString(R.string.tip_validity, data.getValidity()));
		}else{
			holder.tv_valid.setText(getString(R.string.tip_validate_code, data.getCode()));
			
			int status = data.getStatus();
			switch(status){
			case 4:
			case 3:
			case 2:
				holder.tv_status.setVisibility(View.VISIBLE);
				holder.tv_status.setText(statusTexts[data.getStatus()]);
				holder.tv_status.setBackgroundResource(R.drawable.coupon_validaty_expires_bg);
				break;
			case 5:
				holder.tv_status.setVisibility(View.VISIBLE);
				holder.tv_status.setBackgroundResource(R.drawable.coupon_validaty_bg);
				holder.tv_status.setText( data.getStatus_desp() );
				break;
			default:
				holder.tv_status.setVisibility(View.GONE);
			}
			
			
		}
		

		boolean isCoupon = data.getCategory() == 0;
		holder.trangle.setTrangleColor((isCoupon ? R.color.coupon_tag_color
				: R.color.party_tag_color));

		imgloader.displayImage(
				"drawable://"
						+ (isCoupon ? R.drawable.icon_coupon
								: R.drawable.icon_activity), holder.iv_tag);

		return convertView;
	}

	@Override
	protected void onItemClick(AdapterView<?> parent, View view, int position,
			long id, Coupon entity) {

		Intent it = new Intent(activity, CouponDetailActivity.class);
		it.putExtra(CouponDetailFragment.KEY_COUPON_ID, entity.getCoupon_id());
		startActivity(it);
		
		
		
		postClickEvent("E_C_pageName_couponItemClick", "A_coupon_pageName_couponName", entity.getTitle());
	}

	@Override
	protected boolean isInflateActionBar() {
		return has_title;
	}
	
	@Override
	protected String getActionBarTitle() {
		return title;
	}
	class CouponItemHolder {
		TextView tv_title, tv_status, tv_valid;
		TrangleView trangle;
		ImageView iv_coupon, iv_tag;
	}
	@Override
	protected boolean isEnabledAnalytics() {
		return false;
	}
	@Override
	protected String getAnalyticsTitle() {
		return "Coupon_list";
	}

}
