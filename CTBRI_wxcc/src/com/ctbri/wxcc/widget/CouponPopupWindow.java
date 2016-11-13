package com.ctbri.wxcc.widget;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ctbri.comm.util._Utils;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.coupon.CouponDetailActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CouponPopupWindow extends PopupWindow {

	private Activity context;
	private TextView tv_title , tv_coupon_validate;
	private ImageView iv_coupon;
	private String mCouponId;
	
	public CouponPopupWindow(Activity context) {
		super(context);
		this.context = context;
		init();
	}
	
	private void init(){
		
		View v = context.getLayoutInflater().inflate(R.layout.widget_coupon_popup_window, null);
		setContentView(v);
		
		setWindowLayoutMode(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		setFocusable(true);
		setOutsideTouchable(true);
		setBackgroundDrawable(new BitmapDrawable());
		
		tv_title = (TextView) v.findViewById(R.id.tv_coupon_image_title);
		tv_coupon_validate = (TextView) v.findViewById(R.id.tv_valid_or_code);
		iv_coupon = (ImageView) v.findViewById(R.id.iv_coupon_image);
		iv_coupon.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent it = new Intent(context, CouponDetailActivity.class);
				it.putExtra("COUPON_ID", mCouponId);
				context.startActivity(it);
			}
		});
		
		v.findViewById(R.id.btn_close).setOnClickListener(new CloseListener());
	}
	public void setArgs(String title, String date, String img_url, String id){
		tv_title.setText(title);
		tv_coupon_validate.setText(date);
		mCouponId = id;
		ImageLoader.getInstance().displayImage(img_url, iv_coupon, _Utils.DEFAULT_DIO);
	}
	class CloseListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			dismiss();
		}
	}
	
}
