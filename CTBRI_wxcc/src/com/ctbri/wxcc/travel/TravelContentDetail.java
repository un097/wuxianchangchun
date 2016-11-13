package com.ctbri.wxcc.travel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
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
import com.ctbri.wxcc.entity.CommonPoint;
import com.ctbri.wxcc.entity.StrategyBean;
import com.ctbri.wxcc.entity.StrategyBean.Spot;
import com.ctbri.wxcc.entity.StrategyBean.Strategy;
import com.ctbri.wxcc.widget.ClickableLinkMovementMethod;
import com.ctbri.wxcc.widget.DetailsWebView;
import com.ctbri.wxcc.widget.LocateNavVersion;
import com.ctbri.wxcc.widget.LocateOldVersion;
import com.ctbri.wxcc.widget.LocationSpan;
import com.ctbri.wxcc.widget.LocationSpan.LocationClickListener;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

public class TravelContentDetail extends BaseFragment {
	public static final String KEY_DETAIL_ID = "detail_id";
	private DetailsWebView wv_detail;
	private ImageView iv_travel;
	private TextView tv_title;
	private TextView tv_address;
	private String strategyId;
	private ImageLoader imgloader;
	private TextView ll_locate;
	private ArrayList<Spot> spots;
	private Strategy data;
	private View mSpotsContainer;
	/**
	 * 连接地点名称的分隔符
	 */
	private static final String SPOT_TOKEN = " - ";
	private WatcherManager watcher;

	@Override
	public void onAttach(Activity activity_) {
		super.onAttach(activity_);
		if (activity_ instanceof WatcherManagerFactory) {
			watcher = ((WatcherManagerFactory) activity_).getManager();
			watcher.addWatcher(new ShareWatcher());
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		strategyId = args == null ? "" : args.getString(KEY_DETAIL_ID, "");
	}

	public static TravelContentDetail newInstance(String id) {
		TravelContentDetail detailFragment = new TravelContentDetail();
		Bundle args = new Bundle();
		args.putString(TravelContentDetail.KEY_DETAIL_ID, id);
		detailFragment.setArguments(args);
		return detailFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.travel_raiders_detail_layout, container, false);
		wv_detail = (DetailsWebView) v.findViewById(R.id.wv_travel_detail);
		iv_travel = (ImageView) v.findViewById(R.id.iv_detail_image);
		tv_title = (TextView) v.findViewById(R.id.tv_detail_image_title);

		tv_address = (TextView) v.findViewById(R.id.tv_travel_address);
		ll_locate = (TextView) v.findViewById(R.id.ll_locate);
		ll_locate.setEnabled(false);
		ll_locate.setOnClickListener(new LocateListener());
		
		mSpotsContainer = v.findViewById(R.id.ll_spot);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		loadData();
		super.onActivityCreated(savedInstanceState);
	}

	private void loadData() {
		DialogUtils.showLoading(getFragmentManager());
		String url = Constants.METHOD_TRAVEL_STRATEGY_DETAIL + "?strategy_id=" + strategyId;
		request(url, new RequestCallback() {
			@Override
			public void requestSucc(String json) {
				Gson gson = new Gson();
				StrategyBean bean = gson.fromJson(json, StrategyBean.class);
				fillData(bean);
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

	private void fillData(StrategyBean bean) {
		if (bean != null) {
			initImageLoader();

			data = bean.getData();
			spots = (ArrayList<StrategyBean.Spot>) data.getSpots();
			if (spots != null && spots.size() != 0) {

				ll_locate.setEnabled(true);
			} else {
//				ll_locate.setEnabled(true);
//				ll_locate.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_position_gray, 0, 0);
//				ll_locate.setOnClickListener(new EmptyLocationListener());
				//2015年5月6号 需求变更。 景点为空时，隐藏该视图
				mSpotsContainer.setVisibility(View.GONE);
				
			}
			// 填充本页的标题
			tv_title.setText(data.getTitle());
			fillAddress(tv_address, data);
			imgloader.displayImage(data.getPic_url(), iv_travel, _Utils.DEFAULT_DIO);
			wv_detail.loadUrl(data.getContent_url());
		}
	}

	/**
	 * 位置数据为空时，触发该事件
	 * 
	 * @author yanyadi
	 * 
	 */
	class EmptyLocationListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			toast(R.string.msg_empty_address);
		}

	}

	private void fillAddress(TextView tv_address, Strategy data) {
		if (spots != null && spots.size() != 0) {

			String addrs = TextUtils.join(SPOT_TOKEN, spots);
			SpannableString ss = fillAddrColor(addrs);
			tv_address.setMovementMethod(ClickableLinkMovementMethod.getInstance());
			tv_address.setText(ss);
		}
	}

	private SpannableString fillAddrColor(String addrs) {

		SpannableString ss = new SpannableString(addrs);

		Pattern token = Pattern.compile(SPOT_TOKEN);

		Matcher match = token.matcher(addrs);

		Resources res = getResources();
		int font_color = res.getColor(R.color.travel_name_front_color);
		int token_color = res.getColor(R.color.travel_name_separator_color);
		// 地点的开始位置
		int font_start = 0;
		SpotListener mSpotListener = new SpotListener();
		int spot_index = 0;
		if (match.find()) {
			do {

				// 设置地点的颜色
				ss.setSpan(new ForegroundColorSpan(font_color), font_start, match.start(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				// 设置景点点击事件
				Spot sp = spots.get(spot_index++);
				ss.setSpan(new LocationSpan<Spot>(sp, mSpotListener), font_start, match.start(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				// 设置分隔符颜色
				ss.setSpan(new ForegroundColorSpan(token_color), match.start(), match.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				// 下一个文字开始的位置，是符号结束的位置
				font_start = match.end();
			} while (match.find());
		}
		// 设置最后一个地点的颜色
		ss.setSpan(new ForegroundColorSpan(font_color), font_start, addrs.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		// 设置景点点击事件
		ss.setSpan(new LocationSpan<Spot>(spots.get(spot_index), mSpotListener), font_start, addrs.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		return ss;
	}

	class SpotListener implements LocationClickListener<Spot> {

		@Override
		public void onClick(View view, Spot data) {
			String title = data.getSpots_name();
			int type_id = data.getSpots_type();
			Intent it = new Intent(activity, TravelDetailActivity.class);
			it.putExtra(TravelContentDetail.KEY_DETAIL_ID, data.getSpots_id());
			it.putExtra("type_id", type_id);
			it.putExtra("title", title);
			startActivity(it);

			HashMap<String, String> param = new HashMap<String, String>();
			param.put("title", title);
			MobclickAgent.onEvent(activity, Constants_Community.ITEM_EVENT_IDS[type_id], param);
		}

	}

	class LocateListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent it = new Intent(activity, LocateNavVersion.class);

			ArrayList<CommonPoint> points = new ArrayList<CommonPoint>();
			for (Spot spot : spots) {
				CommonPoint cp = CommonPoint.parseCommonPoint(spot.getSpots_location(), ",", spot.getSpots_name());
				if (cp != null)
					points.add(cp);
			}

			it.putExtra(LocateOldVersion.KEY_POINTS, points);
			startActivity(it);
		}
	}

	@Override
	protected String getAnalyticsTitle() {
		return "travel_strategy_detail";
	}

	class ShareWatcher implements Watcher {

		@Override
		public void trigger(Object obj) {
			if (data != null)
				_Utils.shareAndCheckLogin(activity, data.getTitle(), data.getContent_url(), getString(R.string.share_content_travel), _Utils.getDefaultAppIcon(activity));
		}

		@Override
		public int getType() {
			return TYPE_SHARE;
		}
	}
}