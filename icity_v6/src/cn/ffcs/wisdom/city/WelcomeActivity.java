package cn.ffcs.wisdom.city;

import java.io.File;
import java.util.List;

import android.R.anim;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ViewFlipper;
import cn.ffcs.android.usragent.UsrActionAgent;
//import cn.ffcs.config.CityContentProviderTool;
import cn.ffcs.config.ExternalKey;
import cn.ffcs.ui.tools.AlertBaseHelper;
import cn.ffcs.wisdom.city.bo.MenuBo;
//import cn.ffcs.wisdom.city.changecity.ChangeCityBo;
//import cn.ffcs.wisdom.city.changecity.ChangeCityUtil;
import cn.ffcs.wisdom.city.changecity.ProvinceMgr;
import cn.ffcs.wisdom.city.changecity.location.LocationBo;
import cn.ffcs.wisdom.city.changecity.location.LocationInfoMgr;
import cn.ffcs.wisdom.city.changecity.location.LocationUtil;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.entity.CityEntity;
import cn.ffcs.wisdom.city.entity.ProvinceEntity;
import cn.ffcs.wisdom.city.entity.ProvinceListEntity;
import cn.ffcs.wisdom.city.report.ReportManager;
import cn.ffcs.wisdom.city.report.ReportReceiver;
import cn.ffcs.wisdom.city.reportmenu.ReportUtil;
import cn.ffcs.wisdom.city.setting.share.ContactAsyncQueryHandler;
import cn.ffcs.wisdom.city.splashs.SplashBo;
import cn.ffcs.wisdom.city.sqlite.service.CityConfigService;
import cn.ffcs.wisdom.city.sqlite.service.MenuService;
//import cn.ffcs.wisdom.city.sqlite.service.ProvinceInfoService;
import cn.ffcs.wisdom.city.utils.CityImageLoader;
import cn.ffcs.wisdom.city.utils.ConfigUtil;
import cn.ffcs.wisdom.city.utils.TrafficStatisticsFlowUtils;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.city.xg.XgManager;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.lbs.ILbsCallBack;
import cn.ffcs.wisdom.tools.AlarmManagerUtil;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.BitmapUtil;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.MD5;
import cn.ffcs.wisdom.tools.SdCardTool;
import cn.ffcs.wisdom.tools.SharedPreferencesUtil;
import cn.ffcs.wisdom.tools.StringUtil;

import com.baidu.location.BDLocation;
import com.ffcs.surfingscene.util.PublicUtils;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushManager;

/**
 * <p>Title: 欢迎界面,包含闪屏                                             </p>
 * <p>Description:                     </p>
 * <p>@author: caijj                   </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2012-12-29          </p>
 * <p>@author:                         </p> 
 * <p>Update Time: 2013-03-22          </p>
 * <p>Updater:  Leo                    </p>
 * <p>Update Comments:   添加旧版流量统计             </p>
 * <p>Update Time: 2013-05-20          </p> 
 * <p>Updater:  liaodl                 </p>
 * <p>Update Comments: 
 *    合并闪屏，  去掉旧版SplashsActivity   
 * </p>
 */
public class WelcomeActivity extends WisdomCityActivity {

	private boolean focus = true;
	private boolean installFirst; // // 是否第一次启动爱城市，false：第一次启动
	private String cityCode; // 城市编码
	private View mBackground;
	private MenuBo menuBo;
	private LocationBo locationBo;
//	private ChangeCityBo changeCitybo;
	private SplashBo splashBo;
	private boolean first = true;
	// 闪屏
	public static final int SPALSH_NEXT_IMG = 0x1; // 播放下一张图片
	public static final int HANDLER_CHANGE_SPLASH_PROGRESS_BAR = 0x2;
	public static final int HANDLER_CHANGE_TO_NEWBIE = 0x3;
	public int mDelayTime = 2000; // 一张闪屏播放时间
	private FrameLayout splashLayout;
	private ViewFlipper mFlipper;
	private Bitmap bitmap;
	// private ProgressBar splashBar;
	// private ImageButton goToHomePage;
	private int maxNum = 0;
	// private SplashObserver splashObserver;
	Boolean menuComplete = false; // 菜单请求成功标识， true:成功
	private XGPushClickedResult pushMsg;



	protected final Handler allHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SPALSH_NEXT_IMG:
				nextSplahs();
				break;
			case HANDLER_CHANGE_TO_NEWBIE:
				switchToNewBieActivity();
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void initComponents() {
		UsrActionAgent.onAppStart(mContext);// 189Data
        XgManager.xg_register(mContext);
		mBackground = findViewById(R.id.welcome_frame);
		// 设置背景图片
		BitmapDrawable drawable = new BitmapDrawable(mContext.getResources(),
				BitmapFactory.decodeResource(getResources(), R.drawable.background));
		drawable.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);

		mBackground.setBackgroundDrawable(drawable);

		splashLayout = (FrameLayout) findViewById(R.id.widget_splash);
		mFlipper = (ViewFlipper) findViewById(R.id.splash_viewflipper);
		mFlipper.setDisplayedChild(0);
		mFlipper.setAutoStart(false);
		mFlipper.stopFlipping();
	}

	@Override
	protected void initData() {
		// 初始化数据服务
		MenuService.getInstance(mContext);
		CityConfigService.getInstance(mContext);
		ReportManager.getInstance(mContext);
		initSurfingScene();// 初始化天翼景象
		installFirst = installFirst();

		String enableChangeCity = getString(R.string.isenable_city_change); // 是否开启切换城市
		String enableDefaultCity = getString(R.string.default_city); // 默认城市
		boolean enableChangeCityAndHaveDefaultCity = mContext.getResources().getBoolean(
				R.bool.isHaveDefaultCity);
		if (!installFirst) {
			if ("true".equals(enableChangeCity)) {
				if (enableChangeCityAndHaveDefaultCity) {
					MenuMgr.getInstance().saveCityCode(mContext, enableDefaultCity);
				}else {
					MenuMgr.getInstance().saveCityCode(mContext, "");
				}
			} else {
				MenuMgr.getInstance().saveCityCode(mContext, enableDefaultCity);
			}
		}

		cityCode = MenuMgr.getInstance().getCityCode(mContext);

		menuBo = new MenuBo(mActivity);
		menuBo.setHttpCallBack(menuCall);

		splashBo = new SplashBo(mActivity);
		mDelayTime = splashBo.getPlayTime();

		ConfigUtil.getConfigParamsAsync(mContext);
		// 初始化资源配置
		Config.init(mContext);
		// 发起定位
		startLocation();
		// 下载地市特色闪屏
		getSplashs();

		// 播放闪屏 add by caijj
		playSplashs();

		if (!installFirst || StringUtil.isEmpty(cityCode)) {// 第一次安装，本地无缓存citycode
			allHandler.sendMessageDelayed(allHandler.obtainMessage(HANDLER_CHANGE_TO_NEWBIE), 700);
		} else {
			// 请求菜单
			requestMenus(cityCode);
		}

		// 启动定位提示标志
		enableLocationTip();

		// add by caijj 2013-07-18
		new Thread(new Runnable() {

			@Override
			public void run() {
				sendChannelReport();
				sendMenuReport();
			}
		}).start();

	}

	/**
	 * 初始化天翼景象
	 */
	private void initSurfingScene() {
		PublicUtils.setUrlOrIsOfficial(this, Config.SURFINGSCENE_ISOFFICIAL);
		PublicUtils.savekey(mContext, getString(R.string.glo_account_key),
				getString(R.string.glo_password_key));// 初始化天翼景象
	}

	private void enableLocationTip() {// 启动定位提示标志
		SharedPreferencesUtil.setBoolean(mContext, Key.K_CITY_CHANGE_NO_TIPS, true);
	}

	// 上报渠道
	private void sendChannelReport() {
		// modify by caijj 2013-7-17
		AlarmManagerUtil.startAlarmRepeat(mContext, ReportReceiver.class, 1000 * 60 * 3L,
				Config.REPORT_ACTION);
		ReportManager.getInstance(mContext).requestReprot();
		ReportManager.getInstance(mContext).start();
	}

	private void sendMenuReport() {
		ReportUtil.pullReport(mContext);
	}

	private void getSplashs() {
		if (!installFirst || StringUtil.isEmpty(cityCode)) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					splashBo.delFiles(splashBo.getSplashRootPath());
				}
			}).start();
			return;
		}

		if (!SdCardTool.isMounted()) {
			Log.i("未检测到SD卡,下载地市特色闪屏轮播图片失败!");
			return;
		}

		if (!StringUtil.isEmpty(cityCode)) {// 判断此城市是否配有闪屏图片
			splashBo.reqSplashUrlTask();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		installed();
	}

	@Override
	protected void onStart() {
		super.onStart();
		// 判断是否来自信鸽的打开方式
		pushMsg = XGPushManager.onActivityStarted(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		XGPushManager.onActivityStoped(this);
	}


	/**
	 * 播放闪屏图片
	 */
	private void playSplashs() {
		boolean flag = splashBo.isCompleteDown();
		if (flag) {
			splashLayout.setVisibility(View.VISIBLE);
			maxNum = splashBo.getSplashsNum();
			setDataToFlipper();
			allHandler.sendMessageDelayed(allHandler.obtainMessage(SPALSH_NEXT_IMG), mDelayTime + 500);
		}
	}

	/**
	 * 初始化Flipper
	 */
	private void setDataToFlipper() {
		try {
			int width = AppHelper.getScreenWidth(mContext) / 2;
			int height = AppHelper.getScreenHeight(mContext) / 2;
			String splashRoot = Config.SDCARD_SPLASHS;

			String[] urls = getSplashUrls();
			maxNum = urls.length;
			final int length = maxNum;
			if (urls == null || length <= 0) {
				return;
			}
			for (int i = 0; i < length; i++) {
				String urlKey = MD5.getMD5Str(urls[i]);
				String path = splashRoot + File.separator + urlKey;
				bitmap = BitmapUtil.compressBitmapFromFile(path, width, height);
				ImageView img = new ImageView(mContext);
				img.setImageBitmap(bitmap);
				img.setScaleType(ScaleType.FIT_XY);
				mFlipper.addView(img);
			}
			mFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.splash_appear_in));
			mFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.nothing));
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
			// switchToHomPageActivity();
		}
	}

	private String[] getSplashUrls() {
		String urls = splashBo.getSplashUrls();
		if (!StringUtil.isEmpty(urls)) {
			return urls.split(",");
		}
		return null;
	}

	// 播放闪屏图片
	private void nextSplahs() {
		if (mFlipper.getDisplayedChild() + 1 < maxNum) {
			mFlipper.showNext();
			allHandler.sendMessageDelayed(allHandler.obtainMessage(SPALSH_NEXT_IMG), mDelayTime);
		} else {
			allHandler.removeMessages(SPALSH_NEXT_IMG);
			isToSwitchHomePageActivity();
		}
	}

	private void switchToNewBieActivity() {
		Intent intent = new Intent();
		intent.setClass(mContext, NewbieGuidActivity1.class);
		intent.putExtra(Key.K_NEWGUID_FOR_NEWUSER, true);
		startActivity(intent);
		finish();
	}

	/**
	 * 是否跳转到home主页 条件1：menuComplete为true，菜单加载完成 条件2：splashComplete为true，闪屏播放完成
	 * 充要条件
	 */
	private void isToSwitchHomePageActivity() {
		if (menuComplete && (mFlipper.getDisplayedChild() + 1) >= maxNum) {
			switchToHomPageActivity();
		}
	}
	/**
	 * 跳转主页面
	 */
	private void switchToHomPageActivity() {
		removeAllMessage();
		Intent intent = new Intent();
		intent.setClassName(mContext, "cn.ffcs.changchuntv.activity.home.MainActivity");
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		//通过消息推送拉起的应用
		intent.putExtra("pushMsg", pushMsg);
		startActivity(intent);
		finish();
	}

	/**
	 * 请求菜单 1、本地无数据，需要重新请求
	 */
	private void requestMenus(String cityCode) {
		String menuVer = MenuMgr.getInstance().getMenuVer(mContext, cityCode);
		menuBo.request(mContext, cityCode, menuVer);
	}

	private HttpCallBack<BaseResp> menuCall = new HttpCallBack<BaseResp>() {

		@Override
		public void call(BaseResp response) {
			menuComplete = true;
			if (!CommonUtils.isNetConnectionAvailable(mActivity)) {
				splashLayout.setVisibility(View.VISIBLE);
				allHandler.sendMessageDelayed(allHandler.obtainMessage(SPALSH_NEXT_IMG), 700);
				return;
			}
			isToSwitchHomePageActivity();
		}

		@Override
		public void progress(Object... obj) {
		}

		@Override
		public void onNetWorkError() {
			OnClickListener listener = new OnClickListener() {

				@Override
				public void onClick(View v) {
					exitApp();
					finish();
				}
			};

			if (focus) {
				AlertBaseHelper.showMessage(mActivity, "网络异常", listener);
			}
		}
	};

	// 是否第一次启动爱城市，false：第一次启动
	private boolean installFirst() {
		return SharedPreferencesUtil.getBoolean(mContext,
				Key.K_FIRST_INSTALL + AppHelper.getVersionCode(mContext));
	}

	// 获取联系人
	private void getContacts() {
		ContactAsyncQueryHandler.getInstance(getContentResolver()).startGetContacts();
	}

	private void installed() {
		SharedPreferencesUtil.setBoolean(mContext,
				Key.K_FIRST_INSTALL + AppHelper.getVersionCode(mContext), true);
	}

	@Override
	protected int getMainContentViewId() {
		return R.layout.act_welcome;
	}

	@Override
	public void onBackPressed() {
		focus = false;
		super.onBackPressed();
	}

	/**
	 * 开始定位
	 */
	public void startLocation() {
		locationBo = new LocationBo(mContext, new GetLocationCityCodeCallback());
		locationBo.getLocation(mContext, new GetLocationCallback());
	}

	class GetLocationCallback implements ILbsCallBack {

		@Override
		public void call(BDLocation location) {
			if (location != null) {
				int locType = location.getLocType();
				if (locType != BDLocation.TypeGpsLocation
						&& locType != BDLocation.TypeCacheLocation
						&& locType != BDLocation.TypeNetWorkLocation) {// 不是GPS,不是缓存,不是网络定位，则表示定位失败
					BaseResp resp = new BaseResp();
					resp.setStatus(BaseResp.ERROR);
					locationBo.call(resp);
					locationBo.stopLocation();
					return;
				}
				String lat = String.valueOf(location.getLatitude());
				String lng = String.valueOf(location.getLongitude());
				String city = location.getCity();
				String district = location.getDistrict();
				locationBo.location(lat, lng, city, district);
				saveLocationInfo(lat, lng, city);
			}
			locationBo.stopLocation();
		}
	}

	class GetLocationCityCodeCallback implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp resp) {
			CityEntity entity = new CityEntity();
			if (resp.isSuccess()) {
				entity = (CityEntity) resp.getObj();
				if (entity == null) {
					locationBo.setLocationInfo(null);
					locationBo.setLocationStatus(false);
					Log.i("定位得到城市编号失败!");
					return;
				}
				LocationUtil.saveLocationCityCode(mContext, entity.getCity_code());
				locationBo.setLocationInfo(entity);
				locationBo.setLocationStatus(true);
				notifyDataSetChanged(entity);
			} else {
				locationBo.setLocationInfo(null);
				locationBo.setLocationStatus(false);
				notifyDataSetChanged(null);
				Log.i("定位得到城市编号失败!");
			}

		}

		@Override
		public void progress(Object... obj) {
		}

		@Override
		public void onNetWorkError() {
			locationBo.setLocationStatus(false);
			Log.i("定位得到城市编号失败!");
		}

	}

	// 保存定位信息
	private void saveLocationInfo(String lat, String lng, String city) {
		LocationUtil.saveLatitude(mContext, lat);
		LocationUtil.saveLongitude(mContext, lng);
		LocationUtil.saveLocationCityName(mContext, city);
	}

	protected void notifyDataSetChanged(CityEntity entity) {
		LocationInfoMgr.getInstance().setCityEntity(entity);
		LocationInfoMgr.getInstance().notifyDataSetChanged();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if (first) {
			first = false;
			getContacts();// 联系人
			// TrafficStatsUtil.startTrafficStats(mContext);// 流量统计
			TrafficStatisticsFlowUtils.startTrafficStats(mContext);// 基于2.2sdk流量统计
			new CityImageLoader(mContext);
		}
		super.onWindowFocusChanged(hasFocus);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		removeAllMessage();
		destoryBitmap();
		focus = false;
	}

	/**
	 * 删除全部消息监听
	 */
	private void removeAllMessage() {
		allHandler.removeMessages(SPALSH_NEXT_IMG);
		allHandler.removeMessages(HANDLER_CHANGE_TO_NEWBIE);
		// SplashMgr.getInstance().unregisterDataSetObserver(splashObserver);
	}

	public void destoryBitmap() {
		if (null != bitmap && !bitmap.isRecycled()) {
			bitmap.recycle();
			System.gc();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		focus = false;
	}
}
