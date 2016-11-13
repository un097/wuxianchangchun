package cn.ffcs.changchuntv.activity.home.view;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ctbri.wxcc.community.CommunityActivity;
import com.ctbri.wxcc.shake.ShakeMainActivity;
import com.ctbri.wxcc.travel.TravelActivity;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ffcs.changchuntv.R;
import cn.ffcs.changchuntv.activity.explore.ExploreActivity1;
import cn.ffcs.changchuntv.activity.home.HomeActivity;
import cn.ffcs.changchuntv.activity.home.MainActivity;
import cn.ffcs.changchuntv.activity.home.adapter.BannerAdapter;
import cn.ffcs.changchuntv.activity.home.traffic.TrafficAdapter;
import cn.ffcs.changchuntv.activity.home.traffic.TrafficAdapter.TrafficDelegate;
import cn.ffcs.changchuntv.activity.home.traffic.TrafficEntry;
import cn.ffcs.changchuntv.activity.login.LoginActivity;
import cn.ffcs.changchuntv.activity.login.bo.ThirdAccountBo;
import cn.ffcs.changchuntv.entity.AdvertisingEntity;
import cn.ffcs.changchuntv.entity.AdvertisingEntity.Advertising;
import cn.ffcs.external.trafficbroadcast.activity.TrafficBroadcastActivity;
import cn.ffcs.external.trafficbroadcast.activity.TrafficListActivity;
import cn.ffcs.external.trafficbroadcast.bo.Traffic_IsCollectedList_Bo;
import cn.ffcs.external.trafficbroadcast.entity.Traffic_IsCollectedItem_Entity;
import cn.ffcs.external.trafficbroadcast.entity.Traffic_IsCollectedList_Entity;
import cn.ffcs.surfingscene.common.Key;
import cn.ffcs.surfingscene.road.RoadMainActivity;
import cn.ffcs.ui.tools.AlertBaseHelper;
import cn.ffcs.wisdom.city.changecity.location.LocationUtil;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.personcenter.entity.Account;
import cn.ffcs.wisdom.city.web.BrowserActivity;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.JsonUtil;
import cn.ffcs.wisdom.tools.StringUtil;

public class HomeHeaderView extends FrameLayout implements TrafficDelegate {

	private Context mContext;
	private ViewPager bannerViewPager;
	private LinearLayout bannerIndicatorLayout;
	private TextView banner_title;
	private ImageView[] bannerIndicatorViews;
	private BannerAdapter viewPagerAdapter;
	private List<Advertising> advs = new ArrayList<Advertising>();
	private int mDelayTime = 3000;// 3秒更新一次
	private int selectIndex = 0;// 选中项
	private GridView trafficGrid;
	private TrafficAdapter traffcAdapter;
	private ArrayList<TrafficEntry> list = new ArrayList<TrafficEntry>();
	private View newtraffic;
	/**
	 * 轮播路况
	 */
	private TextView newtraffic_info;
	private TextView newtraffic_time;
	private RelativeLayout bannerroot;
	public static final String BUS_URL = "http://wap.buscity.cn/f?flag=1";
	public HomeHeaderView(Context context) {
		super(context);
		mContext = context;
		init(context);
	}

	private void init(Context context) {
		// 布局！
		inflate(context, R.layout.home_header, this);
		LayoutInflater inflater = LayoutInflater.from(context);
		View bannerView = inflater.inflate(R.layout.banner, null);
		bannerroot = (RelativeLayout) findViewById(R.id.bannerroot);
		int width = AppHelper.getScreenWidth(mContext);
		ViewGroup.LayoutParams params = new LayoutParams(width, width / 2);
		bannerroot.addView(bannerView, params);
		bannerViewPager = (ViewPager) bannerView
				.findViewById(R.id.bannerviewpager);
		bannerIndicatorLayout = (LinearLayout) bannerView
				.findViewById(R.id.banner_indicator_group);
		banner_title = (TextView) bannerView.findViewById(R.id.banner_title);
		refreshAdvertising();

		registerMessageReceiver();
		ShakeMainActivity shakeMainActivity = new ShakeMainActivity();
		shakeMainActivity.isNewActivities(mContext);
		
		// 路况的GridView
		trafficGrid = (GridView) findViewById(R.id.traffic_grid);
		traffcAdapter = new TrafficAdapter(this);
		

		newtraffic = findViewById(R.id.newtraffic);
		newtraffic_info = (TextView) findViewById(R.id.newtraffic_info);
		newtraffic_time = (TextView) findViewById(R.id.newtraffic_time);
		getTraffic();
	}

	public void registerMessageReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction("action.intent.activity.statu");
		mContext.registerReceiver(mReceiver, filter);
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			String act = intent.getAction();
			if("action.intent.activity.statu".equals(act)){
				boolean isNew = intent.getBooleanExtra("isNew",false);
				TrafficEntry entry1 = new TrafficEntry();
				entry1.setResId(R.drawable.traffic_icon);
				entry1.setName("路况详情");
				entry1.setClazz(TrafficBroadcastActivity.class);
				TrafficEntry entry2 = new TrafficEntry();
				entry2.setResId(R.drawable.video_icon);
				entry2.setName("路况视频");
				entry2.setClazz(RoadMainActivity.class);
				TrafficEntry entry3 = new TrafficEntry();
				entry3.setResId(R.drawable.inqure_icon);
				entry3.setName("违章查询");
				entry3.setClazz(BrowserActivity.class);// AddCarActivity
				TrafficEntry entry4 = new TrafficEntry();
				entry4.setResId(R.drawable.community_icon);
				entry4.setName("爆料社区");
				entry4.setClazz(CommunityActivity.class);// AddCarActivity
				/*TrafficEntry entry5 = new TrafficEntry();
				entry5.setResId(R.drawable.survey_icon);
				entry5.setName("民意调查");
				entry5.setClazz(VoteActivity.class);// AddCarActivity*/
				TrafficEntry entry5 = new TrafficEntry();
				entry5.setResId(R.drawable.green_bus_icon);
				entry5.setName("公交查询");
				entry5.setClazz(BrowserActivity.class);// AddCarActivity
				TrafficEntry entry6 = new TrafficEntry();
				entry6.setResId(R.drawable.travel_icon);
				entry6.setName("旅游资讯");
				entry6.setClazz(TravelActivity.class);// AddCarActivity
				TrafficEntry entry7 = new TrafficEntry();
				if (isNew) {
					entry7.setResId(R.drawable.activity_new_icon);
				} else {
					entry7.setResId(R.drawable.jiangli_icon);
				}
				entry7.setName("活动");
				entry7.setClazz(ShakeMainActivity.class);// AddCarActivity
				list.add(entry5);
				list.add(entry1);
				list.add(entry2);
				list.add(entry3);
				list.add(entry4);
				list.add(entry6);
				list.add(entry7);

				// 更多
				TrafficEntry entry8 = new TrafficEntry();
				entry8.setResId(R.drawable.more_icon);
				entry8.setName("更多");
				entry8.setClazz(ExploreActivity1.class);
				list.add(entry8);
				traffcAdapter.setData(list);
				trafficGrid.setAdapter(traffcAdapter);
				return;
			}
		}
	};
	
	@Override
	public void onTrafficClick(TrafficEntry entry) {
		if (entry.getClazz() == null) {
			Toast.makeText(mContext, "正在建设中", Toast.LENGTH_SHORT).show();
			return;
		}
		boolean isLogin = AccountMgr.getInstance().isLogin(mContext);
		Intent intent = new Intent();
		intent.setClass(mContext, entry.getClazz());
		if (entry.getName().equals("实时路况")) {
			MobclickAgent.onEvent(mContext, "E_C_home_homeTrafficClick");
		}
		if (entry.getName().equals("路况视频")) {
			MobclickAgent.onEvent(mContext, "E_C_home_homeTrafficVideoClick");
			intent.putExtra(Key.K_AREA_CODE, "220100");
			intent.putExtra(Key.K_AREA_NAME, "长春");
			// intent.putExtra(Key.K_GLO_TYPE, "1024");
			intent.putExtra(Key.K_TITLE_NAME, "路况视频");
			if (isLogin) {
				intent.putExtra(Key.K_PHONE_NUMBER, AccountMgr.getInstance()
						.getMobile(mContext));
			}

		}
		if (entry.getName().equals("违章查询")) {
//			if (!isLogin) {
//				AlertBaseHelper.showConfirm((Activity) mContext, "提示", "请先登录",
//						new LoginOnclick());
//				return;
//			}
			intent.putExtra(Key.U_BROWSER_URL, "http://news.153.cn:12160/icity.xinjiang/changchun/jiaotong/index.jhtml");
			intent.putExtra(Key.U_BROWSER_TITLE, "违章查询");
			intent.putExtra(Key.K_RETURN_TITLE, mContext.getString(R.string.home_name));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			MobclickAgent.onEvent(mContext, "E_C_home_homeViolationQueryClick");
		}
		if (entry.getName().equals("公交查询")) {
//			if (!isLogin) {
//				AlertBaseHelper.showConfirm((Activity) mContext, "提示", "请先登录",
//						new LoginOnclick());
//				return;
//			}
			intent.putExtra(Key.U_BROWSER_URL, BUS_URL);
			intent.putExtra(Key.U_BROWSER_TITLE, "公交查询");
			intent.putExtra(Key.K_RETURN_TITLE, mContext.getString(R.string.home_name));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			MobclickAgent.onEvent(mContext, "E_C_home_homeViolationQueryClick");
		}
		if (entry.getName().equals("百度导航")) {
			try {
				// intent = Intent
				// .getIntent("intent://map/marker?location=40.047669,116.313082&title=导航&content=从这里出发&src=福富软件公司|无线长春#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
				intent = Intent
						.getIntent("intent://map?src=福富软件公司|无线长春#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
				if (isInstallByread("com.baidu.BaiduMap")) {
					MobclickAgent.onEvent(mContext, "E_C_home_homeNavClick");
					mContext.startActivity(intent);
				} else {
					Toast.makeText(mContext, "没有安装百度地图客户端", Toast.LENGTH_SHORT)
							.show();
				}
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		if (entry.getName().equals("更多")) {
			((MainActivity) ((HomeActivity) mContext).getParent()).onTab(1);
			return;
		}
		mContext.startActivity(intent);

	}

	private boolean isInstallByread(String packageName) {
		return new File("/data/data/" + packageName).exists();

	}

	class LoginOnclick implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			Intent intent = new Intent(mContext, LoginActivity.class);
			mContext.startActivity(intent);
			AlertBaseHelper.dismissAlert((Activity) mContext);

		}

	}

	private void getTraffic() {
		Traffic_IsCollectedList_Bo trafficBo = new Traffic_IsCollectedList_Bo(
				mContext);
		String latitude = LocationUtil.getLatitude(mContext);
		String longitude = LocationUtil.getLongitude(mContext);
		if (latitude == null || latitude.equals("") || longitude == null
				|| longitude.equals("")) {
			return;
		}
		Map<String, String> params = new HashMap<String, String>();
		Account account = AccountMgr.getInstance().getAccount(mContext);
		String user_id = String.valueOf(account.getData().getUserId());
		String mobile = account.getData().getMobile();
		if (user_id == null || user_id.equals("")) {
			user_id = "unknown";
		}
		if (mobile == null || mobile.equals("")) {
			mobile = "unknown";
		}
		String cityCode = MenuMgr.getInstance().getCityCode(mContext);
		String sign = longitude + "$" + latitude;
		params.put("user_id", user_id);
		// params.put("distance", "");
		params.put("distance", "3");
		params.put("show_num", "");
		params.put("mobile", mobile);
		params.put("city_code", cityCode);
		params.put("org_code", cityCode);
		params.put("longitude", longitude);
		params.put("latitude", latitude);
		params.put("sign", sign);
		trafficBo
				.startRequestTask(
						new TrafficCallBack(),
						mContext,
						params,
						Config.GET_SERVER_ROOT_URL()
								+ "icity-api-client-other/icity/service/lbs/road/getNearRoadList");

	}

	class TrafficCallBack implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp response) {
			if (response.isSuccess()) {
				Traffic_IsCollectedList_Entity trafficEntity = (Traffic_IsCollectedList_Entity) response
						.getObj();
				List<Traffic_IsCollectedItem_Entity> trafficList = trafficEntity
						.getData();
				if (trafficList != null && trafficList.size() > 0) {
					Traffic_IsCollectedItem_Entity trafficItem = trafficList
							.get(0);
					String trafficItemContent = "";
					for (Traffic_IsCollectedItem_Entity traffic_IsCollectedItem_Entity : trafficList) {
						trafficItemContent = trafficItemContent
								+ traffic_IsCollectedItem_Entity.getTitle()
								+ "  "
								+ traffic_IsCollectedItem_Entity.getDetail()
								+ "        ";
						// Log.e("sb", ".." + trafficItemContent);
					}

					newtraffic_info.setText(trafficItemContent);
					if (trafficItem.getStatus() == 1) {
						newtraffic_info.setCompoundDrawables(
								mContext.getResources().getDrawable(
										R.drawable.iv_shunchang_bg), null,
								null, null);
					} else if (trafficItem.getStatus() == 2) {
						newtraffic_info.setCompoundDrawables(
								mContext.getResources().getDrawable(
										R.drawable.iv_huanman_bg), null, null,
								null);
					} else if (trafficItem.getStatus() == 3) {
						newtraffic_info.setCompoundDrawables(
								mContext.getResources().getDrawable(
										R.drawable.iv_yongdu_bg), null, null,
								null);
					} else if (trafficItem.getStatus() == 4) {
						newtraffic_info.setCompoundDrawables(
								mContext.getResources().getDrawable(
										R.drawable.iv_daolufengbi_bg), null,
								null, null);
					} else if (trafficItem.getStatus() == 5) {
						newtraffic_info.setCompoundDrawables(
								mContext.getResources().getDrawable(
										R.drawable.iv_shigu_bg), null, null,
								null);
					} else if (trafficItem.getStatus() == 6) {
						newtraffic_info.setCompoundDrawables(
								mContext.getResources().getDrawable(
										R.drawable.iv_jingchazhifa_bg), null,
								null, null);
					}
					// if (trafficItem.getInterval_time() == 0) {
					// newtraffic_time.setText("刚刚");
					// } else if (trafficItem.getInterval_time() < 60) {
					// newtraffic_time.setText(trafficItem.getInterval_time() +
					// "分钟前");
					// } else if (trafficItem.getInterval_time() < 1440) {
					// newtraffic_time.setText(trafficItem.getInterval_time() /
					// 60 + "小时前");
					// } else {
					// newtraffic_time.setText(trafficItem.getInterval_time() /
					// 1440 + "天前");
					// }
					if (trafficItem.getInterval_time() == 0) {
						newtraffic_time.setText("刚刚");
					} else if (trafficItem.getInterval_time() < 60) {
						newtraffic_time.setText(trafficItem.getInterval_time()
								+ "分钟前");
					} else if (trafficItem.getInterval_time() < 120) {
						newtraffic_time.setText("1小时前");
					} else {
						newtraffic_time.setText("2小时前");
					}
					newtraffic.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							MobclickAgent.onEvent(mContext,
									"E_C_home_homeTrafficListClick");
							Intent intent = new Intent();
							intent.setClass(mContext, TrafficListActivity.class);
							mContext.startActivity(intent);

						}
					});
				}
			}

		}

		@Override
		public void progress(Object... obj) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onNetWorkError() {
			// TODO Auto-generated method stub

		}

	}

	public void refresh() {
		getTraffic();
		selectIndex = 0;
		uiHandler.removeCallbacks(mRunnable);
		initBanner(null);
		refreshAdvertising();
	}

	public void refreshAdvertising() {
		Map<String, Object> map = new HashMap<String, Object>();
		String phone = AccountMgr.getInstance().getMobile(mContext);
		phone = StringUtil.isEmpty(phone) ? "unknown" : phone;
		map.put("mobile", phone);
		String cityCode = MenuMgr.getInstance().getCityCode(mContext);
		map.put("cityCode", cityCode);
		map.put("orgCode", cityCode);
		map.put("longitude", "unknown");
		map.put("latitude", "unknown");
		String imsi = AppHelper.getMobileIMSI(mContext);
		if (StringUtil.isEmpty(imsi)) {
			imsi = "0000000000000000";
		}
		String[] groups = { "-1" };
		String imei = AppHelper.getIMEI(mContext);
		map.put("imsi", imsi);
		map.put("imei", imei);
		map.put("groups", groups);
		String sign = "-1";
		ThirdAccountBo bo = new ThirdAccountBo();
		bo.getAdvertising(new AdvertisingCallBack(), mContext, map, sign);
	}

	class AdvertisingCallBack implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp response) {
			if (response.isSuccess()) {
				// AdvertisingEntity adEntity = new AdvertisingEntity();
				// List<Advertising> banner_advs = new
				// ArrayList<AdvertisingEntity.Advertising>();
				// for (int i = 0; i < 5; i++) {
				// Advertising entity = new AdvertisingEntity().new
				// Advertising();
				// entity.group = -1;
				// entity.android_pic =
				// "http://img5.imgtn.bdimg.com/it/u=1041153494,1223721837&fm=116&gp=0.jpg";
				// entity.id = i;
				// entity.order = i;
				// entity.title = "测试数据" + i;
				// entity.url = "www.baiduy.com";
				// banner_advs.add(entity);
				// }
				// adEntity.banner_advs = banner_advs;
				// initBanner(adEntity);
				AdvertisingEntity adEntity = new AdvertisingEntity();
				try {
					JSONArray jarray = new JSONArray(response.getData());
					adEntity = JsonUtil.toObject(jarray.get(0).toString(),
							AdvertisingEntity.class);
					if (adEntity.banner_advs.size() != 0) {
						initBanner(adEntity);
						bannerroot.setVisibility(View.VISIBLE);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void progress(Object... obj) {
		}

		@Override
		public void onNetWorkError() {
		}
	}

	/**
	 * 初始化广告栏
	 * 
	 * @param adEntity
	 */
	private void initBanner(AdvertisingEntity adEntity) {
		int size = -1;
		bannerIndicatorLayout.removeAllViews();
		if (adEntity != null && adEntity.banner_advs != null) {
			advs = adEntity.banner_advs;
			if (advs != null && advs.size() > 0) {
				size = advs.size();
			}
		}
		if (size < 0) {
			size = 0;
			advs = new ArrayList<Advertising>();
		}
		bannerIndicatorViews = new ImageView[size];
		for (int i = 0; i < size; i++) {
			ImageView bannerIndicator = new ImageView(getContext());
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.setMargins(0, 0, 10, 0);
			bannerIndicator.setLayoutParams(params);
			bannerIndicatorViews[i] = bannerIndicator;
			if (i == 0) {
				bannerIndicatorViews[i]
						.setBackgroundResource(R.drawable.banner_indicator_focused);
			} else {
				bannerIndicatorViews[i]
						.setBackgroundResource(R.drawable.banner_indicator_unfocused);
			}
			bannerIndicatorLayout.addView(bannerIndicatorViews[i]);
		}
		// 广告的BannerAdapter
		viewPagerAdapter = new BannerAdapter((Activity) getContext(), advs);
		bannerViewPager.setAdapter(viewPagerAdapter);
		viewPagerAdapter.notifyDataSetChanged();
		bannerViewPager.setOnPageChangeListener(new BannerPageListener());
		if (advs != null && advs.size() > 0) {
			banner_title.setText(advs.get(selectIndex).title);
		} else {
			banner_title.setText("");
		}
		if (size > 0) {
			uiHandler.postDelayed(mRunnable, mDelayTime);
		}
	}

	private Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			uiHandler.sendEmptyMessage(1);
			selectIndex++;
		}
	};

	private Handler uiHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (selectIndex == advs.size()) {
				selectIndex = 0;
			}
			bannerViewPager.setCurrentItem(selectIndex);
			banner_title.setText(advs.get(selectIndex).title);
		}

	};

	class BannerPageListener implements OnPageChangeListener {

		@Override
		public void onPageSelected(int index) {
			int newPosition = index;
			if (newPosition >= advs.size()) {
				newPosition = index % advs.size();
			}
			selectIndex = newPosition;
			banner_title.setText(advs.get(selectIndex).title);
			uiHandler.removeCallbacks(mRunnable);
			uiHandler.postDelayed(mRunnable, mDelayTime);
			bannerIndicatorViews[newPosition]
					.setBackgroundResource(R.drawable.banner_indicator_focused);
			for (int i = 0; i < bannerIndicatorViews.length; i++) {
				if (newPosition != i) {
					bannerIndicatorViews[i]
							.setBackgroundResource(R.drawable.banner_indicator_unfocused);
				}
			}

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}
	}

}
