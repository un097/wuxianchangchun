package cn.ffcs.changchuntv.activity.explore;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.ctbri.wxcc.audio.AudioMainActivity;
import com.ctbri.wxcc.community.CommunityActivity;
import com.ctbri.wxcc.coupon.CouponMainActivity;
import com.ctbri.wxcc.hotline.HotLineActivity;
import com.ctbri.wxcc.media.MediaMainActivity;
import com.ctbri.wxcc.travel.TravelActivity;
import com.ctbri.wxcc.vote.VoteActivity;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

import cn.ffcs.changchun_base.activity.BaseFragmentActivity;
import cn.ffcs.changchuntv.R;
import cn.ffcs.changchuntv.activity.home.MainActivity;
import cn.ffcs.changchuntv.activity.home.view.HomeHeaderView;
import cn.ffcs.changchuntv.activity.login.LoginActivity;
import cn.ffcs.changchuntv.activity.news.HighWay_NewsActivity;
import cn.ffcs.changchuntv.activity.news.NewsActivity;
import cn.ffcs.external.trafficbroadcast.activity.TrafficBroadcastActivity;
import cn.ffcs.surfingscene.road.RoadMainActivity;
import cn.ffcs.ui.tools.AlertBaseHelper;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.common.MenuType;
import cn.ffcs.wisdom.city.download.ApkRunException;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.simico.activity.Utils;
import cn.ffcs.wisdom.city.simico.base.AppDownloadService;
import cn.ffcs.wisdom.city.simico.base.Application;
import cn.ffcs.wisdom.city.simico.kit.activity.DownloadAlertActivity;
import cn.ffcs.wisdom.city.sqlite.model.MenuItem;
import cn.ffcs.wisdom.city.traffic.violations.TrafficViolationsListActivity;
import cn.ffcs.wisdom.city.utils.AppMgrUtils;
import cn.ffcs.wisdom.city.web.BrowserActivity;

/**
 * “发现”页面
 * @author Administrator
 *
 */
public class ExploreActivity1 extends BaseFragmentActivity implements OnClickListener {
	
	View traffic_detail;
	View traffic_violation;
	View traffic_highway;
	View traffic_nav;
	View traffic_video;
	
	View hot_news;
	View changchun_news;
	View world_news;
	View lifestyle;
	View interesting;
	View read;
	
	View radio;
	View video;
	
	View coupon;
	View community;
	View vote;
	View hotline;
	View travel;
	View booking;
	View gov;
	
	View explore_fm;
	View bus_line;

	@Override
	protected int getLayoutId() {
		return R.layout.activity_explore1;
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		initActionBar();
		traffic_detail = findViewById(R.id.traffic_detail);
		traffic_detail.setOnClickListener(this);
		traffic_violation = findViewById(R.id.traffic_violation);
		traffic_violation.setOnClickListener(this);
		traffic_highway = findViewById(R.id.traffic_highway);
		traffic_highway.setOnClickListener(this);
		traffic_nav = findViewById(R.id.traffic_nav);
		traffic_nav.setOnClickListener(this);
		traffic_video = findViewById(R.id.traffic_video);
		traffic_video.setOnClickListener(this);
		bus_line=findViewById(R.id.bus_line);
		bus_line.setOnClickListener(this);
		
		hot_news = findViewById(R.id.hot_news);
		hot_news.setOnClickListener(this);
		changchun_news = findViewById(R.id.changchun_news);
		changchun_news.setOnClickListener(this);
		world_news = findViewById(R.id.world_news);
		world_news.setOnClickListener(this);
		lifestyle = findViewById(R.id.lifestyle);
		lifestyle.setOnClickListener(this);
		interesting = findViewById(R.id.interesting);
		interesting.setOnClickListener(this);
		read = findViewById(R.id.read);
		read.setOnClickListener(this);
		
		radio = findViewById(R.id.radio);
		radio.setOnClickListener(this);
		video = findViewById(R.id.video);
		video.setOnClickListener(this);
		
		coupon = findViewById(R.id.coupon);
		coupon.setOnClickListener(this);
		community = findViewById(R.id.community);
		community.setOnClickListener(this);
		vote = findViewById(R.id.vote);
		vote.setOnClickListener(this);
		hotline = findViewById(R.id.hotline);
		hotline.setOnClickListener(this);
		travel = findViewById(R.id.travel);
		travel.setOnClickListener(this);
		booking = findViewById(R.id.booking);
		booking.setOnClickListener(this);
		gov = findViewById(R.id.gov);
		gov.setOnClickListener(this);
		explore_fm = findViewById(R.id.explore_fm);
		explore_fm.setOnClickListener(this);
		super.init(savedInstanceState);
	}

	private void initActionBar() {
		TextView title = (TextView) findViewById(R.id.title);
		title.setText("发现");
		
	}

	@Override
	public void onClick(View v) {
		boolean isLogin = AccountMgr.getInstance().isLogin(
				mContext);
		if (v.getId() == R.id.traffic_detail) {
			/**
			 * 路况详情
			 */
			MobclickAgent.onEvent(mContext, "E_C_service_serviceTrafficClick");
			Intent intent = new Intent(ExploreActivity1.this, TrafficBroadcastActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.traffic_violation) {
			/**
			 * 违章查询
			 */
			if (!isLogin) {
				AlertBaseHelper.showConfirm(mActivity, "提示", "请先登录", new LoginOnclick());
				return;
			}
			MobclickAgent.onEvent(mContext, "E_C_service_serviceViolationQueryClick");
			Intent intent = new Intent(ExploreActivity1.this, TrafficViolationsListActivity.class);//AddCarActivity
			startActivity(intent);
		} else if (v.getId() == R.id.traffic_highway) {
			/**
			 * 高速路况
			 */
			MobclickAgent.onEvent(mContext, "E_C_service_highspeedTrafficClick");
//			Intent intent = new Intent(ExploreActivity1.this, BrowserActivity.class);
//			intent.putExtra(Key.U_BROWSER_URL, "http://59.63.163.116:11000/WebTestForProvideData/gslk/index.jhtml");
//			intent.putExtra(Key.U_BROWSER_TITLE, "高速路况");
//			intent.putExtra("shareTraffic", true);
//			startActivity(intent);
			Intent intent = new Intent(ExploreActivity1.this, HighWay_NewsActivity.class);
			intent.putExtra("chnlId", -2);
			startActivity(intent);
		}
		else if (v.getId()==R.id.bus_line){
			Intent intent=new Intent(mContext,BrowserActivity.class);
			intent.putExtra(cn.ffcs.surfingscene.common.Key.U_BROWSER_URL, HomeHeaderView.BUS_URL);
			intent.putExtra(cn.ffcs.surfingscene.common.Key.U_BROWSER_TITLE, "公交查询");
			intent.putExtra(cn.ffcs.surfingscene.common.Key.K_RETURN_TITLE, mContext.getString(R.string.home_name));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);

		}
		else if (v.getId() == R.id.traffic_video) {
			/**
			 * 路况视频
			 */
			MobclickAgent.onEvent(mContext, "E_C_service_serviceTrafficVideoClick");
			Intent intent = new Intent(ExploreActivity1.this, RoadMainActivity.class);
			intent.putExtra(cn.ffcs.surfingscene.common.Key.K_AREA_CODE, "220100");
			intent.putExtra(cn.ffcs.surfingscene.common.Key.K_AREA_NAME, "长春");
//			intent.putExtra(cn.ffcs.surfingscene.common.Key.K_GLO_TYPE, "1024");
			intent.putExtra(cn.ffcs.surfingscene.common.Key.K_TITLE_NAME, "路况视频");
			if (isLogin) {
				intent.putExtra(cn.ffcs.surfingscene.common.Key.K_PHONE_NUMBER, AccountMgr.getInstance().getMobile(mContext));
			}
			startActivity(intent);
		} else if (v.getId() == R.id.traffic_nav) {
			/**
			 * 百度导航
			 */
			Intent intent = new Intent(ExploreActivity1.this, TrafficBroadcastActivity.class);
//			startActivity(intent);
			try {
				MobclickAgent.onEvent(mContext, "E_C_service_serviceNavClick");
//				intent = Intent
//						.getIntent("intent://map/marker?location=40.047669,116.313082&title=导航&content=从这里出发&src=福富软件公司|无线长春#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
				intent = Intent
						.getIntent("intent://map?src=福富软件公司|无线长春#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");if(isInstallByread("com.baidu.BaiduMap")){
					startActivity(intent);
				} else {
					Toast.makeText(mContext, "没有安装百度地图客户端", Toast.LENGTH_SHORT).show();
				}
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (v.getId() == R.id.hot_news) {
			/**
			 * 热点新闻
			 */
			MobclickAgent.onEvent(mContext, "E_C_service_hotNewsClick");
			Intent intent = new Intent(ExploreActivity1.this, NewsActivity.class);
			intent.putExtra("chnlId", 0);
			startActivity(intent);
		} else if (v.getId() == R.id.changchun_news) {
			/**
			 * 本地咨询
			 */
			MobclickAgent.onEvent(mContext, "E_C_service_ccKnowsClick");
			Intent intent = new Intent(ExploreActivity1.this, NewsActivity.class);
			intent.putExtra("chnlId", 1);
			startActivity(intent);
		} else if (v.getId() == R.id.world_news) {
			/**
			 * 天下新闻
			 */
			MobclickAgent.onEvent(mContext, "E_C_service_worldNewsClick");
			Intent intent = new Intent(ExploreActivity1.this, NewsActivity.class);
			intent.putExtra("chnlId", 2);
			startActivity(intent);
		} else if (v.getId() == R.id.lifestyle) {
			/**
			 * 生活娱乐
			 */
			MobclickAgent.onEvent(mContext, "E_C_service_liveEnjoymentClick");
			Intent intent = new Intent(ExploreActivity1.this, NewsActivity.class);
			intent.putExtra("chnlId", 3);
			startActivity(intent);
		} else if (v.getId() == R.id.interesting) {
			/**
			 * 趣闻轶事
			 */
			MobclickAgent.onEvent(mContext, "E_C_service_anecdotesClick");
			Intent intent = new Intent(ExploreActivity1.this, NewsActivity.class);
			intent.putExtra("chnlId", 5);
			startActivity(intent);
		} else if (v.getId() == R.id.read) {
			/**
			 * 日度文章
			 */
			MobclickAgent.onEvent(mContext, "E_C_service_dailyReaderClick");
			Intent intent = new Intent(ExploreActivity1.this, NewsActivity.class);
			intent.putExtra("chnlId", 6);
			startActivity(intent);
		} else if (v.getId() == R.id.radio) {
			/**
			 * 听广播
			 */
			Intent intent = new Intent(ExploreActivity1.this, AudioMainActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.video) {
			/**
			 * 看电视
			 */
			Intent intent = new Intent(ExploreActivity1.this, MediaMainActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.coupon) {
			/**
			 * 优惠好礼
			 */
			Intent intent = new Intent(ExploreActivity1.this, CouponMainActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.community) {
			/**
			 * 爆料社区
			 */
			Intent intent = new Intent(ExploreActivity1.this, CommunityActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.vote) {
			/**
			 * 民意调查
			 */
			Intent intent = new Intent(ExploreActivity1.this, VoteActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.hotline) {
			/**
			 * 热线
			 */
			Intent intent = new Intent(ExploreActivity1.this, HotLineActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.travel) {
			/**
			 * 旅游资讯
			 */
			Intent intent = new Intent(ExploreActivity1.this, TravelActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.booking) {
			/**
			 * 商旅预订
			 */
			MobclickAgent.onEvent(mContext, "E_C_service_businessBookClick");
			Intent intent = new Intent(ExploreActivity1.this, BrowserActivity.class);
			intent.putExtra(Key.U_BROWSER_URL, "http://u.ctrip.com/union/CtripRedirect.aspx?TypeID=2&Allianceid=27054&sid=463555&OUID=&jumpUrl=http://www.ctrip.com");
			intent.putExtra(Key.U_BROWSER_TITLE, "商旅预订");
			startActivity(intent);
		} else if (v.getId() == R.id.gov) {
			/**
			 * 政务发布
			 */
			Intent intent = new Intent(ExploreActivity1.this, NewsActivity.class);
			intent.putExtra("chnlId", -1);
			startActivity(intent);
		}else if (v.getId() == R.id.explore_fm) {
			/**
			 * fm广播
			 */
//			Intent intent = new Intent(ExploreActivity1.this, NewsActivity.class);
//			intent.putExtra("chnlId", -1);
//			startActivity(intent);
			
			
			MenuItem menu = new MenuItem();
			//启动的主要类（包名.类名）
			menu.setMain("com.sict.chcradio.MainActivity");
			menu.setMenuName("豆包FM");
			//包名
			menu.setPackage_("com.sict.chcradio");
			menu.setAppsize("11264");
			menu.setMenudesc("暂无");
			menu.setMenuType(MenuType.EXTERNAL_APP);
			menu.setAppUrl("http://dbfm.yqting.com/bsys/apkdown/download.php?radioid=0431");
//			launchApp("com.sict.chcradio", menu);
			AppMgrUtils.launchAPP(mActivity, menu, "");
//			ServiceHelper.openService(getActivity(), menu);
		}
	}
	
	class LoginOnclick implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			Intent intent = new Intent(mActivity, LoginActivity.class);
			mActivity.startActivity(intent);
			AlertBaseHelper.dismissAlert(mActivity);
			
		}
		
	}
	
	private boolean isInstallByread(String packageName) {
		return new File("/data/data/" + packageName).exists();
	}

	public void launchApp(String packageName, MenuItem menu) {
		if (isInstallByread(packageName)) {
			// Intent intent =
			// getPackageManager().getLaunchIntentForPackage(packageName);
			// startActivity(intent);
			// ServiceHelper.openService(mActivity, menu);
			// AppMgrUtils.launchAPP(mActivity, menu, "");
			try {
				AppMgrUtils.getInstance().startApp(mActivity, menu, "", false);
			} catch (ApkRunException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			String mPkgName = menu.getPackage_() + ".apk";
//			Log.e("sb", "mPkgName   " + mPkgName);
			if (Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED)) {
				File destDir = new File(Utils.getAppFilePath());
//				Log.e("sb", "destDir   " + destDir.getAbsolutePath());
				if (destDir.exists()) {
					File destFile = new File(destDir.getPath() + "/" + mPkgName);
//					Log.e("sb", "destFile   " + destFile.getAbsolutePath());
					if (destFile.exists() && destFile.isFile()
							&& checkApkFile(destFile.getPath())) {
						install(destFile);
						return;
					}
				}
			}
			if (TextUtils.isEmpty(menu.getAppUrl())) {
				Application.showToastShort("没有找到下载地址");
				return;
			}
			Intent intent = new Intent(this, DownloadAlertActivity.class);
			intent.putExtra("MenuMessage", menu);
			intent.putExtra("isDownload", false);
			ActivityManager myManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningServiceInfo> runningService = myManager
					.getRunningServices(50);
			for (int i = 0; i < runningService.size(); i++) {
				if (runningService.get(i).service.getClassName().equals(
						"cn.ffcs.wisdom.city.simico.base.AppDownloadService")) {
					intent.putExtra("isDownload", true);
					if (AppDownloadService.menuItem != null
							&& !menu.getMenuName().equals(
									AppDownloadService.menuItem.getMenuName())) {
						intent.putExtra("MenuMessage",
								AppDownloadService.menuItem);
						intent.putExtra("isWait", true);
					}
					break;
				}
			}
			startActivity(intent);
		}
	}

	private boolean checkApkFile(String apkFilePath) {
		boolean result = false;
		try {
			PackageManager pManager = getPackageManager();
			PackageInfo pInfo = pManager.getPackageArchiveInfo(apkFilePath,
					PackageManager.GET_ACTIVITIES);
			if (pInfo == null) {
				result = false;
			} else {
				result = true;
			}
		} catch (Exception e) {
			result = false;
			e.printStackTrace();
		}
		return result;
	}

	private void install(File apkFile) {
		Uri uri = Uri.fromFile(apkFile);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(uri, "application/vnd.android.package-archive");
		startActivity(intent);
	}

	@Override
	public void onBackPressed() {
		((MainActivity) getParent()).onBack();
	}
	
	

}
