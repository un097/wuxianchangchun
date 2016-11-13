package cn.ffcs.external.trafficbroadcast.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import cn.ffcs.config.ExternalKey;
import cn.ffcs.external.trafficbroadcast.adapter.TrafficAttentionAdapter;
import cn.ffcs.external.trafficbroadcast.bo.Traffic_IsCollectedList_Bo;
import cn.ffcs.external.trafficbroadcast.entity.Traffic_IsCollectedItem_Entity;
import cn.ffcs.external.trafficbroadcast.entity.Traffic_IsCollectedList_Entity;
import cn.ffcs.external.trafficbroadcast.view.MyListView;
import cn.ffcs.external.trafficbroadcast.view.PopWindow;
import cn.ffcs.tts.utils.TtsSpeechApi;
import cn.ffcs.tts.utils.TtsSpeechApi.SpeechDelegate;
import cn.ffcs.widget.LoadingDialog;
import cn.ffcs.wisdom.city.changecity.location.LocationBo;
import cn.ffcs.wisdom.city.changecity.location.LocationUtil;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.personcenter.entity.Account;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.lbs.ILbsCallBack;
import cn.ffcs.wisdom.tools.SharedPreferencesUtil;

import com.baidu.location.BDLocation;
import com.example.external_trafficbroadcast.R;
import com.umeng.analytics.MobclickAgent;

/**
 * Title: 路况播报文字列表页面 权威播报
 * 
 * @author daizhq
 * 
 * @date 2014.11.28
 */
public class TrafficListActivity extends Activity implements OnClickListener,
		SpeechDelegate {

	// 返回键
	private LinearLayout ll_back;
	// 右上角跳转到地图页面图标
	private Button iv_tomap;
	// 温馨提醒方块
	private LinearLayout ll_warming;
	// 语音播报开关
	private ImageView iv_speeker;
	// 更多路况按钮
	private ImageView iv_more;

	// 搜索输入框
	private EditText et_input;

	// 默认情况下，语音播报开关是关闭的 false:不报，true:播报
	private boolean is_broadcast = false;

	// 受关注路况列表
	private MyListView lv_attention;
	private TrafficAttentionAdapter adapter_attention;
	Traffic_IsCollectedList_Bo collectedBo = null;
	Traffic_IsCollectedList_Entity collectedEntity = null;
	List<Traffic_IsCollectedItem_Entity> collectedList = new ArrayList<Traffic_IsCollectedItem_Entity>();

	// 未受关注路况列表
	private MyListView lv_no_attention;
	private TrafficAttentionAdapter adapter_no_attention;
	Traffic_IsCollectedList_Bo nearBo = null;
	Traffic_IsCollectedList_Entity nearEntity = null;
	public static List<Traffic_IsCollectedItem_Entity> nearList = new ArrayList<Traffic_IsCollectedItem_Entity>();

	// 获取附近道路的时候如果取不到本地经纬值就得定位获取
	private LocationBo locationBo;
	String latitude = null;
	String longtitude = null;

	// 当前播报点
	private int currentPositio = 0;
	// 播报内容
	private String msg = "";

	// 语音播报路况列表
	List<Traffic_IsCollectedItem_Entity> mBroadcastList = null;

	// 语音播报框
	PopWindow popWindow;
	// 弹框是出现的遮罩层
	private static ImageView helpImg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		setContentView(R.layout.act_traffic_list);
		loadView();
		showProgressBar("正在加载路况列表...");

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		System.out.println("重启...");
		handler.removeCallbacks(mRefrash);
		// handler.postDelayed(mRefrash, 0000);
		loadData();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (Boolean.parseBoolean(SharedPreferencesUtil.getValue(
				TrafficListActivity.this, Key.K_IS_LOGIN))) {
			handler.obtainMessage(7).sendToTarget();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			handler.obtainMessage(7).sendToTarget();
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 加载本地控件及监听
	 * */
	private void loadView() {
		ll_back = (LinearLayout) findViewById(R.id.ll_back);
		iv_tomap = (Button) findViewById(R.id.iv_tomap);
		ll_warming = (LinearLayout) findViewById(R.id.ll_warming);
		iv_speeker = (ImageView) findViewById(R.id.iv_speeker);
		iv_more = (ImageView) findViewById(R.id.iv_more);
		helpImg = (ImageView) findViewById(R.id.help_img);

		et_input = (EditText) findViewById(R.id.et_input);
		et_input.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView view, int arg1, KeyEvent arg2) {
				// TODO Auto-generated method stub
				String searchStr = et_input.getText().toString().trim();
				Intent intent = new Intent();
				intent.setClass(TrafficListActivity.this,
						AllTrafficationActivity.class);
				intent.putExtra("searchStr", searchStr);
				startActivity(intent);
				return false;
			}
		});

		lv_attention = (MyListView) findViewById(R.id.lv_attention);
		lv_no_attention = (MyListView) findViewById(R.id.lv_no_attention);

		ll_back.setOnClickListener(this);
		iv_tomap.setOnClickListener(this);
		iv_speeker.setOnClickListener(this);
		iv_more.setOnClickListener(this);

	}

	/**
	 * 加载页面数据以及控件监听
	 * */
	private void loadData() {
		is_broadcast = SharedPreferencesUtil.getBoolean(
				TrafficListActivity.this, "k_road_broadcast");
		if (!Boolean.parseBoolean(SharedPreferencesUtil.getValue(
				TrafficListActivity.this, Key.K_IS_LOGIN))) {
			changeBtnStatus(false);
		} else {
			changeBtnStatus(is_broadcast);
		}
		// 获取收藏列表
		handler.postDelayed(mRefrash, 00000);

		lv_attention.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(TrafficListActivity.this,
						TrafficDetailActivity.class);
				System.out.println("选中的id===>>"
						+ String.valueOf(collectedList.get(arg2).getId()));
				intent.putExtra("road_id",
						String.valueOf(collectedList.get(arg2).getId()));
				startActivity(intent);
			}
		});

		lv_no_attention.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				HashMap<String, String> param = new HashMap<String, String>();
				param.put("A_trafficDetail_trafficList_trafficTitle", nearList
						.get(arg2).getTitle());
				MobclickAgent.onEvent(getApplicationContext(),
						"E_C_trafficList_trafficDetailClick", param);
				Intent intent = new Intent(TrafficListActivity.this,
						TrafficDetailActivity.class);
				System.out.println("选中的id===>>"
						+ String.valueOf(nearList.get(arg2).getId()));
				intent.putExtra("road_id",
						String.valueOf(nearList.get(arg2).getId()));
				startActivity(intent);
			}
		});

	}

	/**
	 * 获取收藏列表
	 * */
	private void getCollectedList() {
		// TODO Auto-generated method stub
		Account account = AccountMgr.getInstance().getAccount(
				TrafficListActivity.this);
		String user_id = String.valueOf(account.getData().getUserId());
		// 测试使用用户账号
		// String user_id = "7623773";
		boolean isLogin = Boolean.parseBoolean(SharedPreferencesUtil.getValue(
				TrafficListActivity.this, Key.K_IS_LOGIN));
		if (!isLogin) {// 如果是未登录用户就不用请求收藏列表
			handler.obtainMessage(3).sendToTarget();
		} else {
			collectedBo = new Traffic_IsCollectedList_Bo(
					TrafficListActivity.this);

			Map<String, String> params = new HashMap<String, String>(1);

			String mobile = account.getData().getMobile();
			String lat = LocationUtil.getLatitude(TrafficListActivity.this);
			String lng = LocationUtil.getLongitude(TrafficListActivity.this);

			System.out.println("lat===>>" + lat + ", lng===>>" + longtitude);

			String sign = user_id;

			// String cityCode = MenuMgr.getInstance().getCityCode(mContext);

			if (lat == null || lat.equals("")) {
				lat = "unknown";
			}
			if (lng == null || lng.equals("")) {
				lng = "unknown";
			}
			if (mobile == null || mobile.equals("")) {
				mobile = "unknown";
			}

			params.put("city_code", "2201");
			params.put("org_code", "2201");
			params.put("mobile", mobile);
			params.put("longitude", lng);
			params.put("latitude", lat);
			params.put("sign", sign);
			params.put("user_id", user_id);

			collectedBo
					.startRequestTask(
							new getCollectedListCallBack(),
							TrafficListActivity.this,
							params,
							"http://ccgd.153.cn:50081/icity-api-client-other/icity/service/lbs/road/getCollectionList");

		}
	}

	/**
	 * 获取收藏列表回调
	 * */
	class getCollectedListCallBack implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp response) {
			// TODO Auto-generated method stub
			System.out.println("获取收藏列表===>>" + response.getHttpResult());
			if (response.isSuccess()) {
				collectedEntity = (Traffic_IsCollectedList_Entity) response
						.getObj();
				handler.obtainMessage(1).sendToTarget();
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

	/**
	 * 获取附近道路列表
	 * */
	private void getNearList() {
		nearBo = new Traffic_IsCollectedList_Bo(TrafficListActivity.this);
		latitude = LocationUtil.getLatitude(TrafficListActivity.this);
		longtitude = LocationUtil.getLongitude(TrafficListActivity.this);
		if (latitude == null || latitude.equals("") || longtitude == null
				|| longtitude.equals("")) {// 如果没有获取到经纬度，就需要先定位获取经纬度
			System.out.println("开始定位。。。");
			locationBo = new LocationBo(TrafficListActivity.this);
			locationBo.getLocation(TrafficListActivity.this,
					new GetLocationCallback());
		} else {// 如果正常获取到经纬度就获取附近道路列表
			request();
		}

	}

	/**
	 * 获取定位结果回调
	 * */
	class GetLocationCallback implements ILbsCallBack {

		@Override
		public void call(BDLocation location) {
			if (location != null) {
				int locType = location.getLocType();
				// 不是GPS,不是缓存,不是网络定位，则表示定位失败
				if (locType != BDLocation.TypeGpsLocation
						&& locType != BDLocation.TypeCacheLocation
						&& locType != BDLocation.TypeNetWorkLocation) {
					BaseResp resp = new BaseResp();
					resp.setStatus(BaseResp.ERROR);
					locationBo.call(resp);
					locationBo.stopLocation();
					return;
				}
				latitude = String.valueOf(location.getLatitude());
				longtitude = String.valueOf(location.getLongitude());
				String city = location.getCity();
				String district = location.getDistrict();
				locationBo.location(latitude, longtitude, city, district);
				saveLocationInfo(latitude, longtitude, city);

				System.out.println("here...");
				// 重新定位获取到经纬度之后请求附近道路列表
				request();
			}
			locationBo.stopLocation();
		}
	}

	// 保存定位信息
	private void saveLocationInfo(String lat, String lng, String city) {
		LocationUtil.saveLatitude(TrafficListActivity.this, lat);
		LocationUtil.saveLongitude(TrafficListActivity.this, lng);
		LocationUtil.saveLocationCityName(TrafficListActivity.this, city);
	}

	/**
	 * 请求附近道路列表
	 * */
	private void request() {

		nearBo = new Traffic_IsCollectedList_Bo(TrafficListActivity.this);

		Map<String, String> params = new HashMap<String, String>(1);

		Account account = AccountMgr.getInstance().getAccount(
				TrafficListActivity.this);
		String user_id = String.valueOf(account.getData().getUserId());
		// String user_id = "7623773";
		String mobile = account.getData().getMobile();
		String sign = longtitude + "$" + latitude;
		// String cityCode = MenuMgr.getInstance().getCityCode(mContext);
		if (mobile == null || mobile.equals("")) {
			mobile = "unknown";
		}

		params.put("user_id", user_id);
		params.put("distance", "3");
		params.put("show_num", "");
		params.put("city_code", "2201");
		params.put("org_code", "2201");
		params.put("mobile", mobile);
		params.put("longitude", longtitude);
		params.put("latitude", latitude);
		params.put("sign", sign);

		nearBo.startRequestTask(
				new getNearListCallBack(),
				TrafficListActivity.this,
				params,
				Config.GET_SERVER_ROOT_URL()
						+ "icity-api-client-other/icity/service/lbs/road/getNearRoadList");
	}

	/**
	 * 获取附近道路列表回调
	 * */
	class getNearListCallBack implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp response) {
			// TODO Auto-generated method stub
			System.out.println("获取附近路况列表返回===>>" + response.getHttpResult());
			if (response.isSuccess()) {
				nearEntity = (Traffic_IsCollectedList_Entity) response.getObj();
				handler.obtainMessage(2).sendToTarget();
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

	/**
	 * 显示更新UI
	 * 
	 * @author 戴志强
	 * @date 2014/12/09
	 */
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {

			// 获取已收藏列表返回
			case 1:
				collectedList.clear();
				collectedList = collectedEntity.getData();
				if (collectedList.size() > 0) {
					lv_attention.setVisibility(View.VISIBLE);
					ll_warming.setVisibility(View.GONE);
					adapter_attention = new TrafficAttentionAdapter(
							TrafficListActivity.this, handler, collectedList);
					lv_attention.setAdapter(adapter_attention);

				} else {
					lv_attention.setVisibility(View.GONE);
					ll_warming.setVisibility(View.VISIBLE);
				}

				getNearList();
				break;

			// 获取附近道路列表返回
			case 2:
				nearList.clear();
				nearList = nearEntity.getData();
				if (nearList.size() > 0) {
					lv_no_attention.setVisibility(View.VISIBLE);
					adapter_no_attention = new TrafficAttentionAdapter(
							TrafficListActivity.this, handler, nearList);
					lv_no_attention.setAdapter(adapter_no_attention);
				} else {
					lv_no_attention.setVisibility(View.GONE);
				}

				// 数据加载完成之后等待0.5秒再关闭进度条，避免页面刚加载完造成停滞
				handler.postDelayed(mRun, 500);
				handler.removeCallbacks(mRefrash);
				if (!Boolean.parseBoolean(SharedPreferencesUtil.getValue(
						TrafficListActivity.this, Key.K_IS_LOGIN))
						|| !SharedPreferencesUtil.getBoolean(
								TrafficListActivity.this, "k_road_broadcast")) {
					handler.postDelayed(mRefrash, 60000);
				} else {
					// 语音播报
					enableBroadcast();
				}
				break;

			// 如果是非登录用户就直接请求附近道路列表
			case 3:
				getNearList();
				break;

			// 非登录用户无法实现添加/取消收藏
			case 4:
				Intent intent = new Intent();
				intent.setClassName(TrafficListActivity.this,
						"cn.ffcs.changchuntv.activity.login.LoginActivity");
				startActivity(intent);
				break;

			// 收藏操作成功返回
			case 5:
				getCollectedList();
				break;
			// 销毁线程，重载下一条语音播报
			case 6:
				handler.removeCallbacks(mSpeechRun);
				handler.postDelayed(mSpeechRun, 3000);
				break;
			// 销毁线程，停止语音播报
			case 7:
				is_broadcast = SharedPreferencesUtil.getBoolean(
						TrafficListActivity.this, "k_road_broadcast");
				changeBtnStatus(is_broadcast);
				handler.removeCallbacks(mSpeechRun);
				TtsSpeechApi.getIntance().stopSpeaking();
				break;
			// 销毁线程，进行语音播报
			case 8:
				is_broadcast = SharedPreferencesUtil.getBoolean(
						TrafficListActivity.this, "k_road_broadcast");
				changeBtnStatus(is_broadcast);
				handler.removeCallbacks(mRefrash);
				handler.removeCallbacks(mSpeechRun);
				enableBroadcast();
				break;
			case 9:// 刷新列表
				handler.postDelayed(mRefrash, 6000);
			}
		}
	};

	@SuppressWarnings("static-access")
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.ll_back) {
			finish();
		} else if (id == R.id.iv_tomap) {
			finish();
		} else if (id == R.id.iv_speeker) {
			boolean isLogin = Boolean.parseBoolean(SharedPreferencesUtil
					.getValue(TrafficListActivity.this, Key.K_IS_LOGIN));
			if (!isLogin) {
				Intent intent = new Intent();
				intent.setClassName(TrafficListActivity.this,
						"cn.ffcs.changchuntv.activity.login.LoginActivity");
				startActivity(intent);
			} else {
				popWindow = new PopWindow(helpImg, TrafficListActivity.this,
						handler);
				popWindow.popAwindow(iv_speeker, 7);
				showHelp();
			}
		} else if (id == R.id.iv_more) {
			MobclickAgent.onEvent(getApplicationContext(),
					"E_C_trafficList_moreRoadClick");
			Intent intent_more = new Intent(this, AllTrafficationActivity.class);
			startActivity(intent_more);
		}
	}

	/**
	 * 改变语音播报按钮状态
	 * 
	 * @param status
	 */
	private void changeBtnStatus(boolean status) {
		if (status) {
			iv_speeker.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.speeker_open));
		} else {
			iv_speeker.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.speeker_off));
		}
	}

	/**
	 * 启动路况播报（根据语音播报、定制道路、附近道路进行选择）
	 * 
	 * @param mChoice
	 *            0：语音播报，1：定制道路，2：附近道路
	 */
	public void initBroadcastList(int mChoice) {
		mBroadcastList = new ArrayList<Traffic_IsCollectedItem_Entity>();
		if (0 == mChoice) {
			if (collectedList != null && collectedList.size() > 0) {
				mBroadcastList.addAll(collectedList);
			}

			if (nearList != null && nearList.size() > 0) {
				mBroadcastList.addAll(nearList);
			}

		} else if (1 == mChoice) {
			if (collectedList != null && collectedList.size() > 0) {
				mBroadcastList.addAll(collectedList);
			}
		} else if (2 == mChoice) {
			if (nearList != null && nearList.size() > 0) {
				mBroadcastList.addAll(nearList);
			}
		}
	}

	Boolean is_first = true;

	/**
	 * 进行语音播报
	 * 
	 * @param bb
	 *            全部
	 * @param br
	 *            定制道路
	 * @param bn
	 *            附近道路
	 */
	public void startToBroadcast(boolean bb, boolean br, boolean bn) {
		if (bb && br && bn) {
			initBroadcastList(0);// 定制道路+附近道路
			currentPositio = 0;
			if (is_first) {
				handler.post(mSpeechRun);
				is_first = false;
			} else {
				handler.postDelayed(mSpeechRun, 1);
			}
		}
		if (bb && br && !bn) {
			initBroadcastList(1);// 定制道路
			currentPositio = 0;
			if (is_first) {
				handler.post(mSpeechRun);
				is_first = false;
			} else {
				handler.postDelayed(mSpeechRun, 1);
			}
		}
		if (bb && !br && bn) {
			initBroadcastList(2);// 附近道路
			currentPositio = 0;
			if (is_first) {
				handler.post(mSpeechRun);
				is_first = false;
			} else {
				handler.postDelayed(mSpeechRun, 1);
			}
		}
		if (!bb) {
			if (is_first) {
				handler.post(mSpeechRun);
				is_first = false;
			} else {
				handler.postDelayed(mSpeechRun, 1);
			}
		}
	}

	/**
	 * 根据开关的状态判断是否可以进行语音播报
	 */
	private void enableBroadcast() {
		boolean bb = SharedPreferencesUtil.getBoolean(TrafficListActivity.this,
				"k_road_broadcast");
		boolean br = SharedPreferencesUtil.getBoolean(TrafficListActivity.this,
				"k_road_collected");
		boolean bn = SharedPreferencesUtil.getBoolean(TrafficListActivity.this,
				"k_road_near");
		// 语音播报
		boolean isLogin = Boolean.parseBoolean(SharedPreferencesUtil.getValue(
				TrafficListActivity.this, Key.K_IS_LOGIN));
		if (isLogin) {
			//如果路况详情播放完毕    这边不播放了
			if (ExternalKey.PLAY_TTS_ONCE) {
				startToBroadcast(bb, br, bn);
			}
		}
	}

	/**
	 * 语音播报线程
	 */
	private Runnable mSpeechRun = new Runnable() {
		@Override
		public void run() {

			if (currentPositio < mBroadcastList.size()) {
				msg = mBroadcastList.get(currentPositio).getTitle() + " "
						+ mBroadcastList.get(currentPositio).getDetail() + "";
				// msg = mBroadcastList.get(currentPositio).getDetail() + "";
				// System.out.println("msg:" + msg);
				// 开始语音合成
				TtsSpeechApi.getIntance().toSpeech(TrafficListActivity.this,
						msg, 0);
			}
		}
	};

	/**
	 * 读取进度条
	 * */
	public void showProgressBar(String message) {
		LoadingDialog.getDialog(TrafficListActivity.this).setMessage(message)
				.show();
	}

	/**
	 * 隐藏进度条
	 * */
	public void hideProgressBar() {
		LoadingDialog.getDialog(TrafficListActivity.this).cancel();
	}

	/**
	 * 隐藏进度条线程
	 * */
	private Runnable mRun = new Runnable() {
		@Override
		public void run() {
			hideProgressBar();
		}
	};

	/**
	 * 刷新收藏列表线程
	 */
	private Runnable mRefrash = new Runnable() {
		@Override
		public void run() {
			getCollectedList();
		}
	};

	@Override
	public void onSpeakBegin() {
		// hideProgressBar();
		// CommonUtils.showToast(TrafficListActivity.this, "开始路况播报",
		// Toast.LENGTH_SHORT);
	}

	@Override
	public void onSpeakPaused() {
		handler.removeCallbacks(mSpeechRun);
	}

	@Override
	public void onSpeakResumed() {

	}

	@Override
	public void onBufferProgress(int percent, int beginPos, int endPos,
			String info) {

	}

	@Override
	public void onSpeakProgress(int percent) {

	}

	@Override
	public void onCompleted() {
		// CommonUtils.showToast(TrafficListActivity.this, "播报结束",
		// Toast.LENGTH_SHORT);
		TtsSpeechApi.getIntance().stopSpeaking();
		if (currentPositio == mBroadcastList.size() - 1) {
			//停止循环
			handler.removeCallbacks(mRefrash);
			handler.removeCallbacks(mSpeechRun);
			mBroadcastList.clear();
			//循环播报 
//			handler.postDelayed(mRefrash, 120000);
		} else {
			handler.obtainMessage(6).sendToTarget();
			currentPositio++;
		}
	}

	/**
	 * 显示引导
	 */
	private static void showHelp() {
		helpImg.setVisibility(View.VISIBLE);
		helpImg.setBackgroundResource(R.drawable.cover_bg);
	}

}
