package cn.ffcs.surfingscene.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.ffcs.surfingscene.R;
import cn.ffcs.surfingscene.adapter.GloFragmentPagerAdapter;
import cn.ffcs.surfingscene.common.Key;
import cn.ffcs.surfingscene.datamgr.FavoriteDataMgr;
import cn.ffcs.surfingscene.datamgr.GloCityMgr;
import cn.ffcs.surfingscene.sqlite.GlobalEyesService;
import cn.ffcs.ui.tools.TopUtil;
import cn.ffcs.widget.InterceptType;
import cn.ffcs.widget.InterceptViewPager;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.SharedPreferencesUtil;
import cn.ffcs.wisdom.tools.StringUtil;

/**
 * <p>Title: 景点实况首页         </p>
 * <p>Description: 
 * </p>
 * <p>@author: Leo                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-6-17             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class GlobaleyeMainActivity extends BaseFragmentActivity {
	public InterceptViewPager mPager;
	private ArrayList<Fragment> mFragmentList;
	private ImageView mBottomLine;// 底部横线
	private TextView mTitleCompetitive;// 精品标题
	private ImageView mTitleCityBtn;// 切换城市按钮
	private LinearLayout mImageHelp;// 引导页
	private TextView mTitleCurrentCity;// 当前城市标题
	private String cityName;// 当前城市名称
	private String tyjxCode;// 当前城市编码
	private String title;// 最顶部标题
	private TextView mTitleCollect;// 收藏标题
	private GloFragmentPagerAdapter mGloFragmentAdapter;

	private int currIndex = 0;
	private int bottomLineWidth;
	private int offset = 0;
	private int positionOne;
	private int positionTwo;
	private Resources resources;
	private boolean flag = false;
	private Bundle paramBundle;// 参数传递
	private CompetitiveFragment competitiveFragment;
	private CurrentCityFragment currentFragment;
	private CollectFragment collectFragment;
	private Animation topAnimOut = null;// 头部离开动画
	private Animation topAnimIn = null; // 头部进入动画
	private TimeCount timeCount;

	@Override
	protected void initComponents() {
		mTitleCompetitive = (TextView) findViewById(R.id.glo_title_competitive);
		mTitleCurrentCity = (TextView) findViewById(R.id.glo_title_currentcity);
		mTitleCollect = (TextView) findViewById(R.id.glo_title_collect);
		topAnimOut = AnimationUtils.loadAnimation(mContext, R.anim.top_push_up_out);
		topAnimIn = AnimationUtils.loadAnimation(mContext, R.anim.top_push_up_in);
		mImageHelp = (LinearLayout) findViewById(R.id.glo_city_img_help);
		mImageHelp.setOnClickListener(new OnImageHelpClick());
		mTitleCityBtn = (ImageView) findViewById(R.id.glo_changecity_btn);
		mTitleCompetitive.setOnClickListener(new GloTitleOnClick(0));
		mTitleCurrentCity.setOnClickListener(new GloTitleOnClick(1));
		mTitleCollect.setOnClickListener(new GloTitleOnClick(2));

		resources = getResources();
		initWidth();

	}

	class OnImageHelpClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			hideHelp();
		}

	}

	/**
	 * 显示引导
	 */
	private void showHelp() {
		if (getIsShowHelp()) {
			mImageHelp.startAnimation(topAnimIn);
			mImageHelp.setVisibility(View.VISIBLE);
			timeCount = new TimeCount(5000, 1000);//倒计时五秒
			timeCount.start();
		}
	}

	class TimeCount extends CountDownTimer{

		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			hideHelp();
		}

		@Override
		public void onTick(long millisUntilFinished) {
			
		}
		
	}
	/**
	 * 获取是否显示引导
	 * @return
	 */
	private boolean getIsShowHelp() {
		return SharedPreferencesUtil.getBoolean(mContext, Key.K_CITY_HELP, true);
	}

	/**
	 * 隐藏引导页
	 */
	private void hideHelp() {
		if(getIsShowHelp()){//还未隐藏
			SharedPreferencesUtil.setBoolean(mContext, Key.K_CITY_HELP, false);
			mImageHelp.startAnimation(topAnimOut);
			mImageHelp.setVisibility(View.GONE);
		}
	}

	/**
	 * 初始化宽度
	 */
	private void initWidth() {
		mBottomLine = (ImageView) findViewById(R.id.glo_title_bottomline);
		int screenWidth = AppHelper.getScreenWidth(mContext);
		int minus = CommonUtils.convertDipToPx(mContext, 50);
		bottomLineWidth = (screenWidth - minus) / 3;
		offset = (int) ((screenWidth / 3.0 - bottomLineWidth) / 2);
		positionOne = (int) (screenWidth / 3.0);
		positionTwo = positionOne * 2;
	}

	/**
	 * 初始化ViewPager
	 */
	private void initViewPager() {
		mPager = (InterceptViewPager) findViewById(R.id.glo_viewpager);
		mPager.setIntercept(InterceptType.INTERCEPT_TRANSVERSE);
		mFragmentList = new ArrayList<Fragment>();
		competitiveFragment = CompetitiveFragment.newInstance(paramBundle);
		currentFragment = CurrentCityFragment.newInstance(paramBundle);
		collectFragment = CollectFragment.newInstance(paramBundle);

		mFragmentList.add(competitiveFragment);
		mFragmentList.add(currentFragment);
		mFragmentList.add(collectFragment);
		mGloFragmentAdapter = new GloFragmentPagerAdapter(getSupportFragmentManager(),
				mFragmentList);
		mPager.setAdapter(mGloFragmentAdapter);
		mPager.setCurrentItem(0);
		mPager.setOnPageChangeListener(new GloOnPageChangeListener());
	}

	@Override
	protected void initData() {
		cityName = getIntent().getStringExtra(Key.K_AREA_NAME);// 接收参数:城市名称
		tyjxCode = getIntent().getStringExtra(Key.K_AREA_CODE);// 接收参数:城市编号
		title = getIntent().getStringExtra(Key.K_SEARCH_CONTENT);// 接收参数:最顶部标题
		if (StringUtil.isEmpty(cityName)) {
			cityName = "福州";
		}
		if (StringUtil.isEmpty(tyjxCode)) {
			tyjxCode = "350100";
		}
		paramBundle = new Bundle();
		paramBundle.putString(Key.K_AREA_CODE, tyjxCode);
		paramBundle.putString(Key.K_AREA_NAME, cityName);
		if (StringUtil.isEmpty(title)) {
			title = getString(R.string.glo_app_title);
		}
		TopUtil.updateTitle(mActivity, R.id.top_title, title);
		// 初始化数据库 延迟执行
		delayTask(new Runnable() {
			@Override
			public void run() {
				GlobalEyesService.getInstance(mContext);
			}
		}, 1500);
		mTitleCurrentCity.setText(cityName);
		// 注册城市变化监听器
		registerDataSetObserver();
		initViewPager();
	}

	/**
	 * 注册观察者，当城市变化时，修改标题
	 */
	protected void registerDataSetObserver() {
		GloCityMgr.getInstance().registerDataSetObserver(new CityChangeObserver());
	}

	class CityChangeObserver extends DataSetObserver {
		@Override
		public void onChanged() {
			String cityName = GloCityMgr.getInstance().getCityName();
			if (!StringUtil.isEmpty(cityName)) {
				if (cityName.contains("市")) {
					cityName = cityName.replace("市", "");
				}
				mTitleCurrentCity.setText(cityName);
			}
		}
	}

	@Override
	protected int getMainContentViewId() {
		return R.layout.glo_act_main;
	}

	public class GloTitleOnClick implements OnClickListener {
		private int index = 0;

		public GloTitleOnClick(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mPager.setCurrentItem(index);
			if (index == 1 && flag == true) {
//				Intent intent = new Intent(mActivity, ChangeCityActivity.class);
//				intent.putExtra(Key.K_RETURN_TITLE, getString(R.string.glo_app_title));
//				startActivity(intent);
			}
			if (index == 1) {
				flag = true;
				mTitleCityBtn.setVisibility(View.VISIBLE);
			} else {
				flag = false;
				mTitleCityBtn.setVisibility(View.INVISIBLE);
			}
		}
	};

	public class GloOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int index) {
			Animation animation = null;
			switch (index) {
			case 0:
				if (currIndex == 1) {
					animation = new TranslateAnimation(positionOne, 0, 0, 0);
					mTitleCurrentCity.setTextColor(resources.getColor(R.color.dark_gray_787878));
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(positionTwo, 0, 0, 0);
					mTitleCollect.setTextColor(resources.getColor(R.color.dark_gray_787878));
				}
				competitiveFragment.refreshFavorite();
				mTitleCityBtn.setVisibility(View.GONE);
				mTitleCompetitive.setTextColor(resources.getColor(R.color.blue_0799ea));
				break;
			case 1:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, positionOne, 0, 0);
					mTitleCompetitive.setTextColor(resources.getColor(R.color.dark_gray_787878));
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(positionTwo, positionOne, 0, 0);
					mTitleCollect.setTextColor(resources.getColor(R.color.dark_gray_787878));
				}
				showHelp();// 显示引导页
				if(currentFragment!=null){
					currentFragment.refreshFavorite();
				}
				mTitleCityBtn.setVisibility(View.VISIBLE);
				mTitleCurrentCity.setTextColor(resources.getColor(R.color.blue_0799ea));
				break;
			case 2:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, positionTwo, 0, 0);
					mTitleCompetitive.setTextColor(resources.getColor(R.color.dark_gray_787878));
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(positionOne, positionTwo, 0, 0);
					mTitleCurrentCity.setTextColor(resources.getColor(R.color.dark_gray_787878));
				}
				collectFragment.initData();
				mTitleCityBtn.setVisibility(View.GONE);
				mTitleCollect.setTextColor(resources.getColor(R.color.blue_0799ea));
				break;
			}
			currIndex = index;
			animation.setFillAfter(true);
			animation.setDuration(300);
			mBottomLine.startAnimation(animation);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		hideHelp();
		return true;
	}

	/**
	 * 退出景点视频
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		FavoriteDataMgr.getInstance().clear(); // 清除收藏数据
	}

}
