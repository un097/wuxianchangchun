package cn.ffcs.wisdom.city;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import cn.ffcs.ui.tools.AlertBaseHelper;
import cn.ffcs.widget.OnViewChangeListener;
import cn.ffcs.widget.ScrollLayout;
import cn.ffcs.wisdom.city.bo.MenuBo;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.splashs.SplashBo;
import cn.ffcs.wisdom.city.sqlite.service.MenuService;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.SdCardTool;
import cn.ffcs.wisdom.tools.StringUtil;

public class NewbieGuidActivity1 extends WisdomCityActivity implements OnViewChangeListener {

	private ScrollLayout mScrollLayout;

	private int count;
	private int mCurrentId;
	
	private ImageView newbie_guid1_4;

	@Override
	protected void initComponents() {
		mScrollLayout = (ScrollLayout) findViewById(R.id.scroll_layout);

		mScrollLayout.setOnViewChangeListener(this);
		newbie_guid1_4 = (ImageView) findViewById(R.id.newbie_guid1_4);
		newbie_guid1_4.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				createShotcut();
				String cityCode = MenuMgr.getInstance().getCityCode(mContext);
				if (StringUtil.isEmpty(cityCode)) {
//					Intent intent = new Intent();
//					intent.setClass(mContext, ChangeCityActivity.class);
//					intent.putExtra(Key.K_CITY_CHANGE_FOR_NEWGUID, "true");
//					startActivity(intent);
				} else {
					showProgressDialog();
					String menuVer = MenuService.getInstance(mActivity).getMenuVer(cityCode);
					MenuBo menuBo = new MenuBo(mActivity);
					menuBo.setHttpCallBack(menuCall);
					menuBo.request(mContext, cityCode, menuVer);
					// 平台贵森要求新增
//					requestToken();
					// 如果有配地市特色闪屏，就下载，用于下次启动展示
					getSplashs(cityCode);
				}
				
			}
		});

		count = mScrollLayout.getChildCount();
		mCurrentId = 0;
	}
	
	private HttpCallBack<BaseResp> menuCall = new HttpCallBack<BaseResp>() {

		@Override
		public void call(BaseResp response) {
			dismissProgressDialog();
			MenuMgr.getInstance().loadCityConfig(mActivity, MenuMgr.getInstance().getCityCode(mContext));
			Intent intent = new Intent();
			intent.setClassName(mContext, "cn.ffcs.changchuntv.activity.home.MainActivity");
			startActivity(intent);
			finish();
			
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

			AlertBaseHelper.showMessage(mActivity, "网络异常", listener);
			
		}
		
	};
	
	private void getSplashs(String cityCode) {
		if (!SdCardTool.isMounted()) {
			Log.i("未检测到SD卡,下载地市特色闪屏轮播图片失败!");
			return;
		}
		if (!StringUtil.isEmpty(cityCode)) {// 判断此城市是否配有闪屏图片
			new SplashBo(mActivity).reqSplashUrlTask();
		}
	}
	
//	private void requestToken() {
//		PushMsgBo bo = new PushMsgBo(mContext);
//		String deviceToken = bo.getDeviceToken();
//		if (!StringUtil.isEmpty(deviceToken) && !"null".equals(deviceToken)) {
//			SharedPreferencesUtil.setBoolean(mContext, Key.K_REG_MEG_TOKEN, false);
//			bo.regToken(deviceToken);
//		}
//	}
	
	/**
	 * 创建快捷方式
	 */
	private void createShotcut() {
//		if(!CommonUtils.hasShortcut(mContext, R.string.app_create_shotcut)) {
		CommonUtils.removeShortcut(mContext, R.string.app_name, WelcomeActivity.class);
		CommonUtils.createShortcut(mContext, R.string.app_name, R.drawable.ic_launcher, WelcomeActivity.class);
//		}
	}

	@Override
	protected void initData() {

	}

	@Override
	protected int getMainContentViewId() {
		return R.layout.act_newbie_guid1;
	}

	@Override
	public void OnViewChange(int position) {
		if (position < 0 || position > count - 1 || mCurrentId == position) {
			return;
		}
		mCurrentId = position;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}

}
