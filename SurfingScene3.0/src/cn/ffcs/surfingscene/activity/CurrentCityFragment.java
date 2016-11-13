package cn.ffcs.surfingscene.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.database.DataSetObserver;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.ffcs.surfingscene.R;
import cn.ffcs.surfingscene.adapter.BannerAdapter;
import cn.ffcs.surfingscene.adapter.GloCityExpandAdapter;
import cn.ffcs.surfingscene.common.Config;
import cn.ffcs.surfingscene.common.Key;
import cn.ffcs.surfingscene.datamgr.GloCityMgr;
import cn.ffcs.widget.ExpandNoScrollListView;
import cn.ffcs.widget.LoadingBar;
import cn.ffcs.widget.SubViewPager;
import cn.ffcs.wisdom.tools.CommonUtils;

import com.ffcs.surfingscene.entity.ActionEntity;
import com.ffcs.surfingscene.function.LandscapeList;
import com.ffcs.surfingscene.http.HttpCallBack;
import com.ffcs.surfingscene.response.BaseResponse;

/**
 * <p>Title:  当前城市的景点列表        </p>
 * <p>Description: 
 * </p>
 * <p>@author: Leo                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-6-18             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class CurrentCityFragment extends BaseFragment {
	private Bundle paramBundle;
	private View baseView; // 布局顶层
	private View basicLayer; // 内容层
	private SubViewPager bannerViewPager;// 滑屏滚动
	private ViewGroup bannerIndicatorGroup;// 标签指示器
	private ImageView[] bannerIndicatorViews;// 底部指示器
	private ImageView bannerIndicator = null;
	private List<ActionEntity> mBannnerActionEyeList = new ArrayList<ActionEntity>();// 广告
	private ExpandNoScrollListView mActionListView;
	private List<ActionEntity> mAllActionEyeList = new ArrayList<ActionEntity>();// 所有的数据
	private BannerAdapter mBannerAdapter;
	private GloCityExpandAdapter mCurrentExpandAdapter;
	private LoadingBar loadingBar;
	private View loadingError;
	private int selectIndex = 0;// 选中项
	private Runnable mRunnable;
	private int mDelayTime = 3000;// 3秒更新一次
	private TextView mBannerTitle;
	private LandscapeList mLandscapeList;
	private String tyjxCode;
	private Activity mActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		paramBundle = getArguments();
		mActivity = getActivity();
	}

	/**
	 * 实例化，并传递参数
	 * @param bundle
	 * @return
	 */
	public static CurrentCityFragment newInstance(Bundle bundle) {
		CurrentCityFragment fragMent = new CurrentCityFragment();
		fragMent.setArguments(bundle);
		return fragMent;
	}

	@Override
	protected View setOnCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		baseView = inflater.inflate(R.layout.glo_act_currentcity, container, false);
		initComponents(baseView);
		return baseView;
	}

	private void initComponents(View view) {
		loadingBar = (LoadingBar) view.findViewById(R.id.loading_bar);
		loadingBar.setVisibility(View.VISIBLE);
		bannerViewPager = (SubViewPager) view.findViewById(R.id.glo_city_bannerviewpager);
		List<View> viewList = new ArrayList<View>();
		viewList.add(((GlobaleyeMainActivity) getActivity()).mPager);
		bannerViewPager.setParent(viewList);
		bannerIndicatorGroup = (ViewGroup) view.findViewById(R.id.glo_banner_indicator_group);
		mBannerTitle = (TextView) view.findViewById(R.id.glo_banner_title);

		basicLayer = view.findViewById(R.id.basic_layer);
		basicLayer.setVisibility(View.GONE);
		loadingError = view.findViewById(R.id.loading_error);
		loadingError.setVisibility(View.GONE);
		loadingError.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				loadData(tyjxCode);
			}
		});

		mCurrentExpandAdapter = new GloCityExpandAdapter(getActivity());
		mActionListView = (ExpandNoScrollListView) view
				.findViewById(R.id.glo_city_expandablelistview);
		mActionListView.setGroupIndicator(new ColorDrawable(0));
	}

	/**
	 * 初始化数据
	 */
	@Override
	protected void initData() {
		mLandscapeList = new LandscapeList(mContext);
		tyjxCode = paramBundle.getString(Key.K_AREA_CODE);
		loadData(tyjxCode);
		registerDataSetObserver();
	}

	protected void showError() {
		hideLoading();
		hideBasicLayer();
		if (loadingError != null) {
			loadingError.setVisibility(View.VISIBLE);
		}
	}

	protected void hideError() {
		if (loadingError != null) {
			loadingError.setVisibility(View.GONE);
		}
	}

	protected void hideLoading() {
		if (loadingBar != null) {
			loadingBar.setVisibility(View.GONE);
		}
	}

	protected void showLoading() {
		hideError();
		hideBasicLayer();
		if (loadingBar != null) {
			loadingBar.setVisibility(View.VISIBLE);
		}
	}

	protected void showBasicLayer() {
		hideError();
		hideLoading();
		if (basicLayer != null) {
			basicLayer.setVisibility(View.VISIBLE);
		}
	}

	protected void hideBasicLayer() {
		if (basicLayer != null) {
			basicLayer.setVisibility(View.GONE);
		}
	}

	/**
	 * 初始化广告
	 */
	private void initBanner(List<ActionEntity> actionList) {
		int size = 1;
		bannerIndicatorGroup.removeAllViews();
		mBannerTitle.setText("");
		if (actionList != null && actionList.size() > 0) {
			size = actionList.size();
			ActionEntity entity = actionList.get(0);
			if (entity != null) {
				mBannerTitle.setText(entity.getActionName());
			}
		}
		bannerIndicatorViews = new ImageView[size];
		for (int i = 0; i < size; i++) {
			bannerIndicator = new ImageView(mContext);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(13, 13);
			bannerIndicator.setLayoutParams(params);
			bannerIndicator.setPadding(5, 5, 5, 5);
			bannerIndicatorViews[i] = bannerIndicator;
			if (i == 0) {
				bannerIndicatorViews[i]
						.setBackgroundResource(R.drawable.glo_city_banner_indicator_focused);
			} else {
				bannerIndicatorViews[i]
						.setBackgroundResource(R.drawable.glo_city_banner_indicator_unfocused);
			}
			bannerIndicatorGroup.addView(bannerIndicatorViews[i]);
		}
		mBannerAdapter = new BannerAdapter(mActivity, actionList);
		bannerViewPager.setAdapter(mBannerAdapter);
		bannerViewPager.setOnPageChangeListener(new BannerPageListener());
		if (size > 1) {
			mRunnable = mRun;
			uiHandler.postDelayed(mRunnable, mDelayTime);
		}
	}

	class BannerPageListener implements OnPageChangeListener {

		@Override
		public void onPageSelected(int index) {
			int newPosition = index;
			if (newPosition >= mBannnerActionEyeList.size()) {
				newPosition = index % mBannnerActionEyeList.size();
			}
			ActionEntity entity = mBannnerActionEyeList.get(newPosition);
			if (entity != null) {
				mBannerTitle.setText(entity.getActionName());
			}
			selectIndex = newPosition;
			uiHandler.removeCallbacks(mRunnable);
			uiHandler.postDelayed(mRunnable, mDelayTime);
			bannerIndicatorViews[newPosition]
					.setBackgroundResource(R.drawable.glo_city_banner_indicator_focused);
			for (int i = 0; i < bannerIndicatorViews.length; i++) {
				if (newPosition != i) {
					bannerIndicatorViews[i]
							.setBackgroundResource(R.drawable.glo_city_banner_indicator_unfocused);
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

	private Handler uiHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			bannerViewPager.setCurrentItem(selectIndex);
		}

	};

	private Runnable mRun = new Runnable() {
		@Override
		public void run() {
			uiHandler.sendEmptyMessage(1);
			selectIndex++;
		}
	};

	/**
	 * 加载广告数据
	 */
	public void loadBannerData(String tyjxCode) {
		mLandscapeList.getAllActionList(tyjxCode, Config.TYPE_CITY,
				new GetActionEyeListBannerCallback(), Config.METHOD_BANNER_ACTION_LIST);

	}

	/**
	 * 加载景点视频
	 */
	public void loadActionEyeData(String tyjxCode) {
		mLandscapeList.getAllActionList(tyjxCode, Config.TYPE_CITY, new GetAllEeyeListCallback(),
				Config.METHOD_CITY_EYELIST);
	}

	/**
	 * 加载数据
	 */
	public void loadData(String tyjxCode) {
		if (CommonUtils.isNetConnectionAvailable(mContext)) {
			loadBannerData(tyjxCode);
			loadActionEyeData(tyjxCode);
		} else {
			showError();
			CommonUtils.showToast(getActivity(), R.string.glo_net_work_error, Toast.LENGTH_SHORT);
		}
	}

	class GetActionEyeListBannerCallback implements HttpCallBack<BaseResponse> {

		@Override
		public void callBack(BaseResponse response, String methodName) {// methodName名称
			showBasicLayer();
			if (response != null && "1".equals(response.getReturnCode())) {
				mBannnerActionEyeList = response.getActions();
				// 重新加载广告数据
				initBanner(mBannnerActionEyeList);
			} else {
				showError();
				CommonUtils.showToast(getActivity(), R.string.glo_currentcity_error,
						Toast.LENGTH_SHORT);
			}
		}

	}

	class GetAllEeyeListCallback implements HttpCallBack<BaseResponse> {

		@Override
		public void callBack(BaseResponse response, String arg1) {
			showBasicLayer();
			if (response != null && "1".equals(response.getReturnCode())) {
				mAllActionEyeList = response.getActions();
				refreshAllActionEye(mAllActionEyeList);
			} else {
				showError();
				CommonUtils.showToast(getActivity(), R.string.glo_currentcity_error,
						Toast.LENGTH_SHORT);
			}
		}

	}

	/**
	 * 刷新所有景点视频
	 */
	public void refreshAllActionEye(List<ActionEntity> actionList) {
		if (actionList != null) {
			mCurrentExpandAdapter.setData(actionList);
			mActionListView.setAdapter(mCurrentExpandAdapter);
			mActionListView.expandGroup(0);
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	/**
	 * 注册城市改变观察者
	 */
	protected void registerDataSetObserver() {
		GloCityMgr.getInstance().registerDataSetObserver(new CityStateObserver());
	}

	// 接收数据变化
	class CityStateObserver extends DataSetObserver {
		@Override
		public void onChanged() {
			showLoading();
			String cityCode = GloCityMgr.getInstance().getTyjxCode();
			tyjxCode = cityCode;
			loadData(cityCode);
		}

	}

	/**
	 * 刷新收藏
	 */
	public void refreshFavorite() {
		if (mCurrentExpandAdapter != null) {
			mCurrentExpandAdapter.notifyDataSetChanged();
		}
	}

}
