package cn.ffcs.wisdom.city.home.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.ffcs.surfingscene.activity.GloBannerListActivity;
import cn.ffcs.widget.LoadingBar;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.datamgr.TourWidgetMgr;
import cn.ffcs.wisdom.city.home.widget.bo.CommonWidgetBo;
import cn.ffcs.wisdom.city.utils.CityImageLoader;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.Log;

import com.ffcs.surfingscene.entity.ActionEntity;
import com.ffcs.surfingscene.function.LandscapeList;
import com.ffcs.surfingscene.http.HttpCallBack;
import com.ffcs.surfingscene.response.BaseResponse;

/**
 * <p>Title: 旅游widget                 </p>
 * <p>Description:                     </p>
 * <p>@author: liaodl                  </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-4-12           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class TourWidget extends BaseHomeWidget implements OnClickListener {

	public static final int HANDLER_REQUEST_SUCCESS = 1;
	public static final int HANDLER_REQUEST_FAIL = 2;
	public static final int HANDLER_SCROLL_PIC = 3;
	public static final int MAX_NUM = 4;
	/**
	 * 推荐旅游景点
	 */
	private List<ActionEntity> mData = new ArrayList<ActionEntity>(MAX_NUM);

	private TextView tourTitle;// 旅游景点标题
	private List<String> tourTitleList = new ArrayList<String>(MAX_NUM);// 旅游景点标题

	private ImageView bigPic;
	private List<String> bigPicUrls = new ArrayList<String>(MAX_NUM);

	private ImageView playPic;// 播放按钮

	private ImageView firstPic;
	private ImageView secondPic;
	private ImageView thirdPic;
	private ImageView fourthPic;
	private List<String> smallPicUrls = new ArrayList<String>(MAX_NUM);

	private ImageView selectFirstPic;
	private ImageView selectSecondPic;
	private ImageView selectThirdPic;
	private ImageView selectFourthPic;

	private int choose = 0;//当前选中的景点次序

	protected RelativeLayout firstPicLayout;
	protected RelativeLayout secondPicLayout;
	protected RelativeLayout thirdPicLayout;
	protected RelativeLayout fourthPicLayout;

	protected LoadingBar loadingBar;// 自定义加载进度条
	private int screenWidth;// 屏幕宽
	private int widgetHeight;// 屏幕高

	private ScheduledExecutorService scheduledExecutorService;

	private boolean startFlag;// 线程任务启动标志，防止启动多条线程，抢占图片轮播顺序
	private String menuId;
	private String widgetType = WidgetType.TRAVEL_WIDGET.getValue();
	private CityImageLoader loader;

	private LandscapeList globaleyeLoader;

	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}

	public TourWidget(Context context) {
		super(context);
		initComponents();
		initData();
		setOnClick();
	}

	private void initComponents() {
		loader = new CityImageLoader(mContext);
		loader.setDefaultFailImage(R.drawable.widget_bg);
		screenWidth = AppHelper.getScreenWidth(mContext);
		widgetHeight = CommonUtils.convertDipToPx(mContext, 138);
		tourTitle = (TextView) findViewById(R.id.tour_title);
		bigPic = (ImageView) findViewById(R.id.tour_video_big_pic);
		playPic = (ImageView) findViewById(R.id.tour_play_pic);
		firstPic = (ImageView) findViewById(R.id.tour_first_pic);
		secondPic = (ImageView) findViewById(R.id.tour_second_pic);
		thirdPic = (ImageView) findViewById(R.id.tour_third_pic);
		fourthPic = (ImageView) findViewById(R.id.tour_fourth_pic);

		selectFirstPic = (ImageView) findViewById(R.id.tour_select_first_pic);
		selectSecondPic = (ImageView) findViewById(R.id.tour_select_second_pic);
		selectThirdPic = (ImageView) findViewById(R.id.tour_select_third_pic);
		selectFourthPic = (ImageView) findViewById(R.id.tour_select_fourth_pic);

		firstPicLayout = (RelativeLayout) findViewById(R.id.tour_select_first_pic_layout);
		secondPicLayout = (RelativeLayout) findViewById(R.id.tour_select_second_pic_layout);
		thirdPicLayout = (RelativeLayout) findViewById(R.id.tour_select_third_pic_layout);
		fourthPicLayout = (RelativeLayout) findViewById(R.id.tour_select_fourth_pic_layout);

		loadingBar = (LoadingBar) findViewById(R.id.tour_loading_bar);// 防止与首页采用include方法引入LoadingBar冲突问题
		loadingBar.setTextColor(mContext.getResources().getColor(R.color.white));

	}

	private void initData() {
		requestTourInfo();
	}

	private void setOnClick() {
		bigPic.setOnClickListener(this);
		playPic.setOnClickListener(this);
		firstPic.setOnClickListener(this);
		secondPic.setOnClickListener(this);
		thirdPic.setOnClickListener(this);
		fourthPic.setOnClickListener(this);
		loadingBar.setOnClickListener(this);
	}

	@Override
	public void refresh() {
		requestTourInfo();
	}

	/**
	 * 请求旅游景点推荐信息
	 */
	private void requestTourInfo() {
		beforeReuqest();
		String cityId = MenuMgr.getInstance().getTyjxCitycode(mContext);
		String typeCode = Config.GLOBAL_GEYE_TYPE;
		globaleyeLoader = new LandscapeList(getContext());
		globaleyeLoader.getAllActionList(cityId, typeCode, new RecommendTourCallBack(),
				cn.ffcs.surfingscene.common.Config.METHOD_BANNER_ACTION_LIST);
	}

	private void beforeReuqest() {
		loadingBar.setVisibility(View.VISIBLE);
		loadingBar.showLoadingPic();
		loadingBar.setMessage(mContext.getString(R.string.common_loading));
		playPic.setVisibility(View.GONE);
		tourTitle.setVisibility(View.GONE);
		firstPicLayout.setVisibility(View.GONE);
	}

	private void afterRequest() {
		loadingBar.setVisibility(View.GONE);
		playPic.setVisibility(View.VISIBLE);
		tourTitle.setVisibility(View.VISIBLE);
		firstPicLayout.setVisibility(View.VISIBLE);
	}

	@Override
	public int setContentView() {
		return R.layout.widget_tour;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (R.id.tour_first_pic == id) {
			choose = 0;
		} else if (R.id.tour_second_pic == id) {
			choose = 1;
		} else if (R.id.tour_third_pic == id) {
			choose = 2;
		} else if (R.id.tour_fourth_pic == id) {
			choose = 3;
		} else if (R.id.loading_bar == id) {
			requestTourInfo();
		} else if (R.id.tour_play_pic == id || R.id.tour_video_big_pic == id) {
			new CommonWidgetBo(null, mContext).reportWidget(menuId, widgetType);
			if (!playPic.isShown()) {
				requestTourInfo();
			}
			try {
				Intent intent = new Intent();
				// 天翼景象
				Bundle bundle = new Bundle();
				bundle.putString(Key.K_RETURN_TITLE, mContext.getString(R.string.home_name));
				
				//以下是直接启动活动详情页面
				bundle.putSerializable(cn.ffcs.surfingscene.common.Key.K_BANNER_LIST, mData.get(choose));

//				/**************调用天翼景象saveConfig()方法， 解决天翼景象数据列表获取不稳定问题************************/
//				String tyjxCode = MenuMgr.getInstance().getTyjxCitycode(mContext);
//				new MySharedPreferences(mContext).saveConfig(MySharedPreferences.Current_City_Code,
//						tyjxCode);
//				/**************调用天翼景象saveConfig()方法， 解决天翼景象数据列表获取不稳定问题************************/
//
				intent.setComponent(new ComponentName(mContext, GloBannerListActivity.class));
				intent.putExtras(bundle);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.startActivity(intent);
			} catch (Exception e) {
				Log.e("TourWidget 启动天翼景象错误!");
			}
		}

		setSeclectPic(choose);
	}

	/**
	 * 设置图片选择框
	 */
	private void setSeclectPic(int choice) {
		selectFirstPic.setVisibility(View.GONE);
		selectSecondPic.setVisibility(View.GONE);
		selectThirdPic.setVisibility(View.GONE);
		selectFourthPic.setVisibility(View.GONE);
		try {
			if (choice == 0) {
				selectFirstPic.setVisibility(View.VISIBLE);
			} else if (choice == 1) {
				selectSecondPic.setVisibility(View.VISIBLE);
			} else if (choice == 2) {
				selectThirdPic.setVisibility(View.VISIBLE);
			} else if (choice == 3) {
				selectFourthPic.setVisibility(View.VISIBLE);
			}
			tourTitle.setText(tourTitleList.get(choice));
			loader.loadUrl(bigPic, bigPicUrls.get(choice), screenWidth, widgetHeight);
		} catch (Exception e) {
			Log.e(e.getMessage());
		}
	}

	private class RecommendTourCallBack implements HttpCallBack<BaseResponse> {

		@Override
		public void callBack(BaseResponse response, String methodName) {
			try {
//				afterRequest();
				if ("1".equals(response.getReturnCode())) {
					TourWidgetMgr.getInstance().refreshData(response.getActions());
					handler.sendEmptyMessage(HANDLER_REQUEST_SUCCESS);
//					handleData();
				} else {
					handler.sendEmptyMessage(HANDLER_REQUEST_FAIL);
//					afterRequestFail();
				}
			} catch (Exception e) {
				handler.sendEmptyMessage(HANDLER_REQUEST_FAIL);
				Log.e(e.getMessage(), e);
			}
		}

	}

	public void afterRequestFail() {
		loadingBar.setVisibility(View.VISIBLE);
		loadingBar.setMessage(mContext.getString(R.string.home_widget_error));
		loadingBar.hideLoadingPic();
		tourTitle.setVisibility(View.GONE);
		playPic.setVisibility(View.GONE);
		firstPicLayout.setVisibility(View.GONE);
		secondPicLayout.setVisibility(View.GONE);
		thirdPicLayout.setVisibility(View.GONE);
		fourthPicLayout.setVisibility(View.GONE);
	}

	/**
	 * 加工数据
	 */
	public synchronized void handleData() {
		try {
			setVisible();
			loadPics();
			startTask();
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
	}

	/**
	 * 设置控件可见性
	 */
	private void setVisible() {
		mData = TourWidgetMgr.getInstance().getTourInfo();
		if (mData != null && mData.size() > 0 && mData.size() < MAX_NUM) {
			if (mData.size() == 1) {
				firstPicLayout.setVisibility(View.VISIBLE);
				secondPicLayout.setVisibility(View.INVISIBLE);
				thirdPicLayout.setVisibility(View.INVISIBLE);
				fourthPicLayout.setVisibility(View.INVISIBLE);
			} else if (mData.size() == 2) {
				firstPicLayout.setVisibility(View.VISIBLE);
				secondPicLayout.setVisibility(View.VISIBLE);
				thirdPicLayout.setVisibility(View.INVISIBLE);
				fourthPicLayout.setVisibility(View.INVISIBLE);
			} else if (mData.size() == 3) {
				firstPicLayout.setVisibility(View.VISIBLE);
				secondPicLayout.setVisibility(View.VISIBLE);
				thirdPicLayout.setVisibility(View.VISIBLE);
				fourthPicLayout.setVisibility(View.INVISIBLE);
			}
		} else if (mData.size() >= MAX_NUM) {
			firstPicLayout.setVisibility(View.VISIBLE);
			secondPicLayout.setVisibility(View.VISIBLE);
			thirdPicLayout.setVisibility(View.VISIBLE);
			fourthPicLayout.setVisibility(View.VISIBLE);
		} else {
			firstPicLayout.setVisibility(View.INVISIBLE);
			secondPicLayout.setVisibility(View.INVISIBLE);
			thirdPicLayout.setVisibility(View.INVISIBLE);
			fourthPicLayout.setVisibility(View.INVISIBLE);
		}
	}

	private void loadPics() throws Exception {
		mData = TourWidgetMgr.getInstance().getTourInfo();
		for (ActionEntity entity : mData) {
			bigPicUrls.add(entity.getImgUrl());
			smallPicUrls.add(entity.getThumUrl());
			tourTitleList.add(entity.getActionName());
		}
		if (firstPic.isShown()) {
			Log.i("--旅游推荐URL1：--" + smallPicUrls.get(0));
			tourTitle.setText(tourTitleList.get(0));
			loader.loadUrl(bigPic, bigPicUrls.get(0), screenWidth, widgetHeight);
			loader.loadUrl(firstPic, smallPicUrls.get(0), screenWidth, widgetHeight);
		}
		if (secondPic.isShown()) {
			Log.i("--旅游推荐URL2：--" + smallPicUrls.get(1));
			loader.loadUrl(secondPic, smallPicUrls.get(1), screenWidth, widgetHeight);
		}
		if (thirdPic.isShown()) {
			Log.i("--旅游推荐URL3：--" + smallPicUrls.get(2));
			loader.loadUrl(thirdPic, smallPicUrls.get(2), screenWidth, widgetHeight);
		}
		if (fourthPic.isShown()) {
			Log.i("--旅游推荐URL4：--" + smallPicUrls.get(3));
			loader.loadUrl(fourthPic, smallPicUrls.get(3), screenWidth, widgetHeight);
		}
	}

	/**
	 * 开启定时任务
	 */
	private void startTask() {
		if (!startFlag) {
			try {
				scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
				// 当Widget显示出来后，每3秒钟切换一次图片显示
				scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, 3,
						TimeUnit.SECONDS);
				startFlag = true;
			} catch (Exception e) {
				startFlag = false;
				Log.e(e.getMessage());
			}
		}
	}

	// 切换当前显示的图片
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			int id = msg.what;
			if (HANDLER_REQUEST_SUCCESS == id) {
				afterRequest();
				handleData();
			} else if (HANDLER_REQUEST_FAIL == id) {
				afterRequestFail();
			} else if (HANDLER_SCROLL_PIC == id) {
				setSeclectPic(choose);
			}
		};
	};

	/**
	 * 换行切换任务
	 */
	private class ScrollTask implements Runnable {

		public void run() {
			synchronized (this) {
				try {
					int size = mData.size();
					if (size > MAX_NUM) {
						size = MAX_NUM;
					} else if (size == 0) {
						size = 1;
					}
					choose = (choose + 1) % size;
					handler.obtainMessage(HANDLER_SCROLL_PIC).sendToTarget(); // 通过Handler切换图片
				} catch (Exception e) {
					Log.e("ScrollTask异常");
				}
			}
		}
	}
}
