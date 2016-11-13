package com.ctbri.wxcc.coupon;

import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ctbri.comm.util._Utils;
import com.ctbri.wxcc.Constants;
import com.ctbri.wxcc.MessageEditor;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseFragment;
import com.ctbri.wxcc.entity.CouponDetailBean;
import com.ctbri.wxcc.entity.CouponDetailBean.CouponDetail;
import com.google.gson.Gson;

public class CouponObtainFragment extends BaseFragment{
	public static final String KEY_BEAN = "coupon_bean"	;
	public static final CouponObtainFragment newInstance(CouponDetail detail){
		Bundle args = new Bundle();
		args.putSerializable(KEY_BEAN, detail);
		CouponObtainFragment cof = new CouponObtainFragment();
		cof.setArguments(args);
		return cof;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bean = (CouponDetail) getSerialzeable(KEY_BEAN);
		phone_number = MessageEditor.getTel(activity);
	}
	
	private CouponDetail bean;
	private TextView tv_title, tv_marks, tv_phone;
	private String phone_number;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View  v = inflater.inflate(R.layout.coupon_obtain_layout, container, false);
		tv_title = (TextView)v.findViewById(R.id.tv_title);
		tv_marks =(TextView) v.findViewById(R.id.tv_marks);
		tv_phone =(TextView)v.findViewById(R.id.tv_phone);
		((TextView)v.findViewById(R.id.action_bar_title)).setText(R.string.title_obtain_coupon);
		
		initActionBackListener(v);
		v.findViewById(R.id.btn_obtain).setOnClickListener(new ObtainListener());
		fillData();
		return v;
	}
	private void fillData(){
		if(bean==null)return;
		tv_title.setText(bean.getTitle());
		tv_marks.setText(getString(R.string.tip_marks, bean.getMarks()));
		tv_phone.setText(getString(R.string.tip_phone_number, phone_number));
	}
	
	private void notifyObtainResult(CouponDetailBean bean){
		if(bean!=null){
			CouponDetail newBean = bean.getData();
			newBean.setTitle(this.bean.getTitle());
			newBean.setCoupon_id(this.bean.getCoupon_id());
			Intent it = new Intent(activity, CouponObtainResultActivity.class);
			it.putExtra(KEY_BEAN, newBean);
			startActivity(it);
			activity.finish();
		}
	}
	
	private void obtainCoupon(){
		
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("coupon_id", bean.getCoupon_id()));
		params.add(new BasicNameValuePair("tel", phone_number));
		
		request(Constants.METHOD_COUPON_OBTAIN, new RequestCallback() {
			@Override
			public void requestSucc(String json) {
				Gson gson = new Gson();
				CouponDetailBean detail = gson.fromJson(json, CouponDetailBean.class);
				notifyObtainResult(detail);
			}

			@Override
			public void requestFailed(int errorCode) {
				// TODO Auto-generated method stub
				
			}
		}, params);
	}
	class ObtainListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			if(_Utils.checkLoginAndLogin(activity)){
			obtainCoupon();
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
