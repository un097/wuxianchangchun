package cn.ffcs.wisdom.city.traffic.violations;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import cn.ffcs.android.usragent.i;
import cn.ffcs.ui.tools.TopUtil;
import cn.ffcs.widget.LoadingDialog;
import cn.ffcs.wisdom.city.WisdomCityActivity;
import cn.ffcs.wisdom.city.bo.AllMenuBo;
import cn.ffcs.wisdom.city.bo.MenuBo;
import cn.ffcs.wisdom.city.bo.Wz_SelectBo;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.datamgr.MenuLoadingMgr;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.datamgr.MenuLoadingMgr.MenuLoadingStatus;
import cn.ffcs.wisdom.city.entity.WZListEntity;
import cn.ffcs.wisdom.city.entity.WZconfig;
import cn.ffcs.wisdom.city.entity.Wzlist;
import cn.ffcs.wisdom.city.sqlite.model.TrafficViolationsCars;
import cn.ffcs.wisdom.city.sqlite.service.MenuService;
import cn.ffcs.wisdom.city.sqlite.service.TrafficViolationsService;
import cn.ffcs.wisdom.city.traffic.violations.adapter.CarListAdapter;
import cn.ffcs.wisdom.city.traffic.violations.adapter.CarListAdapter_new;
import cn.ffcs.wisdom.city.traffic.violations.bo.GetRecommendServiceRequest_new;
import cn.ffcs.wisdom.city.traffic.violations.bo.TrafficViolationsBo;
import cn.ffcs.wisdom.city.traffic.violations.common.CarType;
import cn.ffcs.wisdom.city.traffic.violations.entity.TrafficViolationsEntity;
import cn.ffcs.wisdom.city.traffic.violations.entity.TrafficViolationsEntity.TrafficViolationsInfo;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.SharedPreferencesUtil;

/**
 * <p>
 * Title: 违章列表
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * 
 * @author: zhangwsh
 *          </p>
 *          <p>
 *          Copyright: Copyright (c) 2013
 *          </p>
 *          <p>
 *          Company: ffcs Co., Ltd.
 *          </p>
 *          <p>
 *          Create Time: 2013-8-30
 *          </p>
 *          <p>
 * @author: </p>
 *          <p>
 *          Update Time:
 *          </p>
 *          <p>
 *          Updater:
 *          </p>
 *          <p>
 *          Update Comments:
 *          </p>
 */
public class TrafficViolationsListActivity extends WisdomCityActivity {

	private ListView carListView;
	// private CarListAdapter adapter;
	private CarListAdapter_new adapter;
	private List<TrafficViolationsCars> carList;
	private AddCarClickListener addCarClickListener = new AddCarClickListener();
	private boolean first = true;
	private int refreshCount = 0;
	private boolean isFinished = false;

	// private TextView topRight;

	@Override
	protected void initComponents() {
		carListView = (ListView) findViewById(R.id.car_list);
		carListView.setDividerHeight(0);
		// topRight = (TextView) findViewById(R.id.top_right_title);
		// TopUtil.updateTitle(topRight,
		// getString(R.string.violation_order_mgr));
		// topRight.setVisibility(View.VISIBLE);
		// topRight.setOnClickListener(new TopRightClick());
	}

	ArrayList<WZconfig> wzconfiglist = new ArrayList<WZconfig>();

	/**
	 * 查询违章
	 */
	public void select_wz() {
		LoadingDialog.getDialog(mActivity).setMessage("查询中").show();
		Wz_SelectBo menuBo = new Wz_SelectBo(mActivity);
		// String cityCode = MenuMgr.getInstance().getCityCode(mContext);
		// String menuVer =
		// MenuService.getInstance(mActivity).getMenuVer(cityCode);
		menuBo.setHttpCallBack(new HttpCallBack<BaseResp>() {

			@Override
			public void progress(Object... obj) {
			}

			@Override
			public void onNetWorkError() {
			}

			@Override
			public void call(BaseResp response) {
				if (response.isSuccess()) {
					WZListEntity wzListEntity = (WZListEntity) response
							.getObj();
					if (wzListEntity != null) {
						wzconfiglist = (ArrayList<WZconfig>) wzListEntity
								.getData();
					}

					if (wzconfiglist == null || wzconfiglist.size() == 0) {
						Intent i = new Intent(mActivity, AddCarActivity.class);
						i.putExtra(Key.K_RETURN_TITLE,
								getString(R.string.violation_title));
						startActivity(i);
					} else {
						initAddView();
						// refreshAllData();
						adapter = new CarListAdapter_new(mActivity);
						adapter.setData(wzconfiglist);
						carListView.setAdapter(adapter);
					}
					LoadingDialog.getDialog(mActivity).dismiss();
				}
			}
		});
		menuBo.request(mContext);

	}


	class TopRightClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent i = new Intent(mActivity,
					VioalationsOrderManagementActivity.class);
			i.putExtra(Key.K_RETURN_TITLE, getString(R.string.violation_title));
			startActivity(i);
		}
	}

	@Override
	protected void initData() {
		TopUtil.updateTitle(mActivity, R.id.top_title, R.string.violation_title);
		select_wz();
		// carList =
		// TrafficViolationsService.getInstance(mContext).getAllViolationsCar();//MyRelevanceActivityNew
		// GetRelevanceCallBack
		// if (carList == null || carList.size() == 0) {
		// Intent i = new Intent(mActivity, AddCarActivity.class);
		// i.putExtra(Key.K_RETURN_TITLE, getString(R.string.violation_title));
		// startActivity(i);
		// } else {
		// initAddView();
		// refreshAllData();
		// adapter = new CarListAdapter(mActivity);
		// adapter.setData(carList);
		// carListView.setAdapter(adapter);
		// initRefreshTime(refreshTime); //刷新查询时间UI
		// }
	}

	@Override
	protected void onResume() {
		if (!first) {
			initAddView();
			refreshUIData();
		} else {
			first = false;
		}
		super.onResume();
	}

	/**
	 * 刷新数据
	 */
	private void refreshUIData() {
		carList = TrafficViolationsService.getInstance(mContext)
				.getAllViolationsCar();
		if (carList == null || carList.size() == 0) {
			finish();
			return;
		}
		if (adapter == null) {
			adapter = new CarListAdapter_new(mActivity);
			adapter.setData(wzconfiglist);
			carListView.setAdapter(adapter);
		} else {
			adapter.setData(wzconfiglist);
			adapter.notifyDataSetChanged();
		}
	}

	TextView refreshTime; // 查询时间

	/**
	 * 初始化添加按钮
	 */
	private void initAddView() {
		if (carListView.getFooterViewsCount() == 0) {
			// 添加查询时间
			LinearLayout timelayout = new LinearLayout(mContext);
			timelayout.setOrientation(LinearLayout.VERTICAL);
			refreshTime = new TextView(mContext);
			refreshTime.setGravity(Gravity.LEFT);
			refreshTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
			refreshTime.setTextColor(Color.BLACK);
			refreshTime.setTextColor(getResources()
					.getColor(R.color.light_gray));

			LayoutParams timeLp = new LayoutParams(LayoutParams.MATCH_PARENT,
					CommonUtils.convertDipToPx(mContext, 40));
			timeLp.setMargins(0, CommonUtils.convertDipToPx(mContext, 3), 0, 0);
			timelayout.addView(refreshTime, timeLp);
			carListView.addFooterView(timelayout);

			// 添加查询按钮
			LinearLayout ll = new LinearLayout(mContext);
			ll.setOrientation(LinearLayout.VERTICAL);
			TextView addCar = new TextView(mContext);
			addCar.setBackgroundResource(R.drawable.pink_button_selector);// btn_blue_selector
			addCar.setGravity(Gravity.CENTER);
			addCar.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
			addCar.setTextColor(Color.WHITE);
			addCar.setText(R.string.violation_query_other_car);
			addCar.setOnClickListener(addCarClickListener);

			LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
					CommonUtils.convertDipToPx(mContext, 40));
			lp.setMargins(0, CommonUtils.convertDipToPx(mContext, 7.5), 0, 0);
			ll.addView(addCar, lp);
			carListView.addFooterView(ll);
		}
	}

	/**
	 * 刷新全部数据
	 */
	private void refreshAllData() {
		int size = carList.size();
		for (TrafficViolationsCars carInfo : carList) {
			TrafficViolationsBo.getInstance(mContext).queryTrafficViolations(
					new RefreshDataCallBack(carInfo.carNo,
							carInfo.carLastCodes, size), carInfo.carNo,
					carInfo.carLastCodes, CarType.SMALL_CAR);
		}
	}

	/**
	 * 刷新查询时间
	 */
	private void initRefreshTime(TextView refreshTime) {
		Long lastTime = SharedPreferencesUtil.getLong(mContext,
				Key.LAST_REFRESH_TIME, 0L);
		if (lastTime != 0L) {
			String time = mContext.getString(
					R.string.violation_query_last_refresh_time,
					formatTime(lastTime));
			refreshTime.setText(time);
			refreshTime.setVisibility(View.VISIBLE);
		} else {
			refreshTime.setVisibility(View.GONE);
		}
	}

	/**
	 * 格式化刷新时间
	 * 
	 * @return
	 */
	private String formatTime(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日  HH:mm");
		return sdf.format(new Date(time));
	}

	/**
	 * 刷新回调
	 */
	class RefreshDataCallBack implements HttpCallBack<BaseResp> {

		private String carNo;
		private String carLastCodes;
		private int size;

		RefreshDataCallBack(String carNo, String carLastCodes, int size) {
			this.carNo = carNo;
			this.carLastCodes = carLastCodes;
			this.size = size;
		}

		@Override
		public void call(BaseResp response) {
			refreshCount++;
			if (response.isSuccess()) {
				TrafficViolationsEntity entity = (TrafficViolationsEntity) response
						.getObj();
				List<TrafficViolationsInfo> list = entity.getCarWZInfo();
				if (list != null && list.size() > 0) {
					TrafficViolationsService.getInstance(mContext)
							.saveViolation(carNo, carLastCodes, list);
				} else if (list != null && list.size() == 0) {
					TrafficViolationsService.getInstance(mContext)
							.saveEmptyList(carNo, carLastCodes);
				}
			}
			if (refreshCount > size) {
				if (!isFinished) {
					refreshUIData();
				}
			}

			// 刷新查询时间
			initRefreshTime(refreshTime);
		}

		@Override
		public void progress(Object... obj) {
		}

		@Override
		public void onNetWorkError() {
		}
	}

	@Override
	public void finish() {
		super.finish();
		isFinished = true;
	}

	/**
	 * 添加违章车辆点击
	 */
	class AddCarClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent i = new Intent(mActivity, AddCarActivity.class);
			i.putExtra(Key.K_RETURN_TITLE, getString(R.string.violation_title));
			startActivity(i);
		}
	}

	@Override
	protected int getMainContentViewId() {
		return R.layout.act_traffic_violations_list;
	}
}
