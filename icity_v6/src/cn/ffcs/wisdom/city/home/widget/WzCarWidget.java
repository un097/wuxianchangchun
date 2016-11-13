package cn.ffcs.wisdom.city.home.widget;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.entity.WzCarTypeEntry;
import cn.ffcs.wisdom.city.home.widget.interfaces.OnEditClickListener;
import cn.ffcs.wisdom.city.sqlite.model.TrafficViolationsCars;
import cn.ffcs.wisdom.city.sqlite.service.TrafficViolationsService;
import cn.ffcs.wisdom.city.traffic.violations.TrafficViolationsListActivity;
import cn.ffcs.wisdom.city.traffic.violations.bo.TrafficViolationsBo;
import cn.ffcs.wisdom.city.traffic.violations.entity.TrafficViolationsEntity;
import cn.ffcs.wisdom.city.traffic.violations.entity.TrafficViolationsEntity.TrafficViolationsInfo;
import cn.ffcs.wisdom.city.utils.XmlParser;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.SharedPreferencesUtil;
import cn.ffcs.wisdom.tools.StringUtil;

/**
 * <p>Title: 违章widget        </p>
 * <p>Description: 
 * 1.xml读取车辆所在地
 * 2.xml读取车辆类型
 * 3.初始化显示选择城市的车辆归属地，福州，显示闽A
 * 4.初始化车辆类型，小型车
 * 5.查询成功后将数据保存在缓存当中，等下次读取缓存的数据
 * 6.登录完之后跳转到车辆列表当中
 * </p>
 * <p>@author: Eric.wsd                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-4-11             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class WzCarWidget extends BaseHomeWidget implements OnClickListener {

	private LinearLayout cityOption;
//	private LinearLayout carTypeOption;
	private TextView cityCode;
	private TextView carType;
	private EditText carNo;
	private EditText carLast4;
	private Button search;
	private ProgressBar mBtnSearchStatusProgress; // 搜索状态提示
	private OnEditClickListener mEditClickListener;
	private TextView lastRefreshTime;

	private Animation mShake;

	/*
	 * 车辆类型
	 */
	private List<WzCarTypeEntry> mCarTypeList = new ArrayList<WzCarTypeEntry>(1);// 车辆类型数组
	private String[] carTypeNames;// 车辆类型名称
	private String[] carTypeCodes;// 车辆类型代号
	private int position = 0;// 车辆类型

	/*
	 * 车辆所在地
	 */
	private List<String> carCityCodeList = new ArrayList<String>(0);// 车辆所在地数组
	private String[] carCityCodes;// 车辆所在地

//	/**
//	 * 违章widget
//	 */
//	private static final String WZ_WIDGET_CAR_CITYCODE = "wz_widget_car_citycode";// 车辆归属地编号
//	private static final String WZ_WIDGET_CAR_NO = "wz_widget_car_no";// 车牌号
//	private static final String WZ_WIDGET_CAR_TYPE = "wz_widget_car_type";// 车辆类型
//	private static final String WZ_WIDGET_CAR_LAST4 = "wz_widget_car_last4";// 车架后4位

	public WzCarWidget(Context context, AttributeSet attr) {
		super(context, attr);
		mContext = context;
		initComponents();
		indata();
	}

	/**
	 * 初始化控件
	 */
	public void initComponents() {
		carNo = (EditText) findViewById(R.id.car_no);
		carNo.setOnClickListener(new OnEditTextClickListener(LinearLayout.FOCUS_BEFORE_DESCENDANTS));
		carType = (TextView) findViewById(R.id.city_type);
		carLast4 = (EditText) findViewById(R.id.car_last_4);
		carLast4.setOnClickListener(new OnEditTextClickListener(
				LinearLayout.FOCUS_BEFORE_DESCENDANTS));
		search = (Button) findViewById(R.id.search);
		search.setOnClickListener(new SearchOnclick());
		mShake = AnimationUtils.loadAnimation(mContext, R.anim.shake);// Edit抖动效果
		cityOption = (LinearLayout) findViewById(R.id.city_option);
		cityOption.setOnClickListener(this);
//		carTypeOption = (LinearLayout) findViewById(R.id.car_type_option);
//		carTypeOption.setOnClickListener(this);
		cityCode = (TextView) findViewById(R.id.city);
		mBtnSearchStatusProgress = (ProgressBar) findViewById(R.id.car_search_status); // 查询过程进度条
		lastRefreshTime = (TextView) findViewById(R.id.car_search_last_refresh_time);

		initRefreshTime();
	}

	private void initRefreshTime() {
		Long lastTime = SharedPreferencesUtil.getLong(mContext, Key.LAST_REFRESH_TIME, 0L);
		if (lastTime != 0L) {
			String time = mContext.getString(R.string.violation_query_last_refresh_time,
					formatTime(lastTime));
			lastRefreshTime.setText(time);
			lastRefreshTime.setVisibility(View.VISIBLE);
		} else {
			lastRefreshTime.setVisibility(View.GONE);
		}
	}

	/**
	 * 格式化刷新时间
	 * @return
	 */
	private String formatTime(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日  HH:mm");
		return sdf.format(new Date(time));
	}

	/**
	 * @param v
	 */
	private void doClickEvent(View v, int focusability) {
		if (mEditClickListener != null) {
			mEditClickListener.onClick(v, focusability);
		}
		v.requestFocus();
		((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE))
				.showSoftInput(v, 0);
	}

	/**
	 * 初始化数据
	 */
	public void indata() {
		initCarTypeSpinner();
		initCarCityCode();
		List<TrafficViolationsCars> vioList = TrafficViolationsService.getInstance(mContext)
				.getAllViolationsCar();
		if (vioList != null && vioList.size() > 0) {
			String carNoNum = vioList.get(vioList.size() - 1).carNo;
			String carLastCodes = vioList.get(vioList.size() - 1).carLastCodes;
			carNo.setText(carNoNum.substring(1));
			carLast4.setText(carLastCodes);
			cityCode.setText("闽" + carNoNum.substring(0, 1));
			carType.setText(carTypeNames[position]);// 设置默认值
		}
//		String wzCarNo = SharedPreferencesUtil.getValue(mContext, WZ_WIDGET_CAR_NO);
//		if (!StringUtil.isEmpty(wzCarNo)) {
//			position = SharedPreferencesUtil.getInteger(mContext, WZ_WIDGET_CAR_TYPE);
//			carNo.setText(wzCarNo);
//			String carCityCode = SharedPreferencesUtil.getValue(mContext, WZ_WIDGET_CAR_CITYCODE);
//			cityCode.setText(carCityCode);
//			String carlast = SharedPreferencesUtil.getValue(mContext, WZ_WIDGET_CAR_LAST4);
//			carLast4.setText(carlast);
//		}
	}

	class SearchOnclick implements OnClickListener {

		@Override
		public void onClick(View v) {
			try {
				String carCityCode = cityCode.getText().toString();
				String carNos = carNo.getText().toString();
				String carLast4Code = carLast4.getText().toString();
				if (!CommonUtils.isNetConnectionAvailable(mContext)) {
					Toast.makeText(mContext, R.string.net_error, Toast.LENGTH_SHORT).show();
					return;
				}
				if (StringUtil.isEmpty(carNos.trim()) || carNos.trim().length() != 5) { // 校验车牌号
					CommonUtils.showErrorByEditText(carNo,
							mContext.getString(R.string.violation_input_car_number_error), mShake);
					return;
				}
				if (StringUtil.isEmpty(carLast4Code) || carLast4Code.trim().length() != 4) { // 校验车架后4位
					CommonUtils.showErrorByEditText(carLast4,
							mContext.getString(R.string.violation_input_last_codes_error), mShake);
					return;
				}
				doClickEvent(v, ViewGroup.FOCUS_BLOCK_DESCENDANTS);
//				bo.getWzCarInfos(carCityCode.substring(1) + carNos,
//						carTypeCodes[position].toString(), carLast4Code);
				TrafficViolationsBo.getInstance(mContext)
						.queryTrafficViolations(
								new QueryViolationCallBack(carCityCode.substring(1) + carNos,
										carLast4Code), carCityCode.substring(1) + carNos,
								carLast4Code, "02");// FIXME Cartype暂时写死
				showSearchProgress();

				//记录查询时间
				Date date = new Date();
				SharedPreferencesUtil.setLong(mContext, Key.LAST_REFRESH_TIME, date.getTime());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class QueryViolationCallBack implements HttpCallBack<BaseResp> {
		private String carNo;
		private String carLastCodes;

		QueryViolationCallBack(String carNo, String carLastCodes) {
			this.carNo = carNo;
			this.carLastCodes = carLastCodes;
		}

		@Override
		public void call(BaseResp response) {
			initRefreshTime(); //刷新时间UI

			if (response.isSuccess()) {
				TrafficViolationsEntity entity = (TrafficViolationsEntity) response.getObj();
				List<TrafficViolationsInfo> list = entity.getCarWZInfo();
				if (list != null && list.size() > 0) {
					TrafficViolationsService.getInstance(mContext).saveViolation(carNo,
							carLastCodes, list);
				} else if (list != null && list.size() == 0) {
					TrafficViolationsService.getInstance(mContext).saveEmptyList(carNo,
							carLastCodes);
				}
				Intent i = new Intent(mContext, TrafficViolationsListActivity.class);
				i.putExtra(Key.K_RETURN_TITLE, mContext.getString(R.string.home_name));
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.startActivity(i);
			} else {
				if (!StringUtil.isEmpty(response.getDesc())) {
					Toast.makeText(mContext, response.getDesc(), Toast.LENGTH_SHORT).show();
				}
			}
			hideSearchProgress();
		}

		@Override
		public void progress(Object... obj) {
		}

		@Override
		public void onNetWorkError() {
		}
	}

	// 从xml配置文件加载车辆所在地数据
	private void loadCarCityCode() {
		if (carCityCodeList != null && carCityCodeList.size() > 0)
			return;
		List<String> list = XmlParser.carCityCodeParser(mContext);
		if (list != null) {
			carCityCodeList.clear();
			carCityCodeList.addAll(list);
		}
	}

	// 初始化车类型
	private void initCarCityCode() {
		loadCarCityCode();
		int size = carCityCodeList.size();
		carCityCodes = new String[size];
		for (int i = 0; i < size; i++) {
			carCityCodes[i] = carCityCodeList.get(i);
		}

		String citycode = MenuMgr.getInstance().getCityCode(mContext);// 获取城市代号
		// 默认值
		cityCode.setText("闽" + XmlParser.getCarTypeByCitycode(mContext, citycode));// 为Edit添加省份简称
	}

	// 从xml配置文件加载车辆类型数据
	private void loadCarType() {
		if (mCarTypeList != null && mCarTypeList.size() > 0)
			return;
		List<WzCarTypeEntry> list = XmlParser.getWzCarTypeData(mContext);
		if (list != null) {
			mCarTypeList.clear();
			mCarTypeList.addAll(list);
		}
	}

	// 初始化车类型
	private void initCarTypeSpinner() {
		loadCarType();
		int size = mCarTypeList.size();
		carTypeNames = new String[size];
		carTypeCodes = new String[size];
		for (int i = 0; i < size; i++) {
			carTypeNames[i] = mCarTypeList.get(i).getTypeName();
			carTypeCodes[i] = mCarTypeList.get(i).getTypeCode();
		}
		// 默认值
		carType.setText(carTypeNames[0]);// 设置默认值
	}

	// 显示查询状态提示
	private void showSearchProgress() {
		mBtnSearchStatusProgress.setVisibility(View.VISIBLE);
		search.setText("查询中...");
		search.setClickable(false);
	}

	// 隐藏查询状态提示
	private void hideSearchProgress() {
		mBtnSearchStatusProgress.setVisibility(View.GONE);
		search.setText("查询");
		search.setClickable(true);
	}

	// 获取系统当前的时间
	public void getNowTime() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH点mm分");
		String updateTime = sdf.format(new java.util.Date());// 默认更新值
		SharedPreferencesUtil.setValue(mContext, Key.REFRESH_UPDATE_TIME, updateTime);
	}

//	/**
//	 * 违章查询回调
//	 */
//	class WZSearchCall implements HttpCallBack<QueryWzCarResp> {
//
//		@Override
//		public void call(QueryWzCarResp response) {
//			if (response.isSuccess() && response.getEntity() != null) {
//				SharedPreferencesUtil.setInteger(mContext, WZ_WIDGET_CAR_TYPE, position);
//				SharedPreferencesUtil.setValue(mContext, WZ_WIDGET_CAR_NO, carNo.getText()
//						.toString());
//				SharedPreferencesUtil.setValue(mContext, WZ_WIDGET_CAR_CITYCODE, cityCode.getText()
//						.toString());
//				SharedPreferencesUtil.setValue(mContext, WZ_WIDGET_CAR_LAST4, carLast4.getText()
//						.toString());
//				Toast.makeText(mContext, response.getDesc(), Toast.LENGTH_SHORT).show();
//				Intent intent = new Intent(mContext, BreakRulesActivity.class);
//				intent.putExtra("is_widget", true);
//				mContext.startActivity(intent);
//				try {
//					getNowTime();
//				} catch (ParseException e) {
//
//				}
//			} else {
//				Toast.makeText(mContext, response.getDesc(), Toast.LENGTH_SHORT).show();
//			}
//			hideSearchProgress();
//		}
//
//		@Override
//		public void progress(Object... obj) {
//
//		}
//
//		@Override
//		public void onNetWorkError() {
//
//		}
//
//	}

	@Override
	public void refresh() {

	}

	@Override
	public int setContentView() {
		return R.layout.widget_wzcar;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.city_option) {
			new AlertDialog.Builder(mContext).setTitle("选择车辆归属地")
					.setItems(carCityCodes, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							cityCode.setText(carCityCodes[which]);
						}
					}).show();// 构造车辆归属地对话框
		} else if (id == R.id.car_type_option) {
			new AlertDialog.Builder(mContext).setTitle("选择车辆类型")
					.setItems(carTypeNames, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							carType.setText(carTypeNames[which]);
							position = which;
						}
					}).show();// 构造车辆类型对话框
		}

	}

	public void setOnEidtClickListener(OnEditClickListener listener) {
		this.mEditClickListener = listener;
	}

	class OnEditTextClickListener implements OnClickListener {
		int focusability;

		OnEditTextClickListener(int focusability) {
			this.focusability = focusability;
		}

		@Override
		public void onClick(View v) {
			doClickEvent(v, focusability);
		}
	}
}
