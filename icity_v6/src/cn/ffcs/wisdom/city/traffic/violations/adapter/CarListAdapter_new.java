package cn.ffcs.wisdom.city.traffic.violations.adapter;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;
import cn.ffcs.ui.tools.AlertBaseHelper;
import cn.ffcs.widget.LoadingDialog;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.entity.WZconfig;
import cn.ffcs.wisdom.city.sqlite.model.TrafficViolations;
import cn.ffcs.wisdom.city.sqlite.model.TrafficViolationsCars;
import cn.ffcs.wisdom.city.sqlite.service.TrafficViolationsService;
import cn.ffcs.wisdom.city.traffic.violations.TrafficViolationsInfoActivity;
import cn.ffcs.wisdom.city.traffic.violations.bo.TrafficViolationsBo;
import cn.ffcs.wisdom.city.traffic.violations.common.CarType;
import cn.ffcs.wisdom.city.traffic.violations.entity.TrafficViolationsEntity;
import cn.ffcs.wisdom.city.traffic.violations.entity.TrafficViolationsEntity.TrafficViolationsInfo;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.SharedPreferencesUtil;
import cn.ffcs.wisdom.tools.StringUtil;

/**
 * <p>
 * Title: 违章车辆列表适配器
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
 *          Create Time: 2013-9-2
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
public class CarListAdapter_new extends BaseAdapter {

	private Context context;
	private Activity activity;
	private List<WZconfig> list;
	private LayoutInflater mLayoutInflater;

	// TextView violationCount;

	public CarListAdapter_new(Activity activity) {
		this.activity = activity;
		this.context = activity.getApplicationContext();
		mLayoutInflater = (LayoutInflater) context
				.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
	}

	public void setData(List<WZconfig> list) {
		if (list == null) {
			this.list = Collections.emptyList();
		} else {
			this.list = list;
		}
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		WZconfig entity = list.get(position);
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.listview_item_car,
					parent, false);
		}
//		LoadingDialog.getDialog(activity).setMessage("查询中").show();
		TextView carNo = (TextView) convertView.findViewById(R.id.car_no);
		carNo.setText(entity.getKeyList().get(0).getKeyValue());
		TextView violationCount = (TextView) convertView
				.findViewById(R.id.violation_count);
		// if (position==0) {
		// violationCount.setText(String.valueOf(entity.getDetaillist().size()));
		// }
		TrafficViolationsBo.getInstance(activity).queryTrafficViolations(
				new QueryTrafficCallBack(entity.getKeyList().get(0)
						.getKeyValue(), entity.getKeyList().get(2)
						.getKeyValue(), violationCount, convertView),
				entity.getKeyList().get(0).getKeyValue(),
				entity.getKeyList().get(2).getKeyValue(), CarType.SMALL_CAR);

		convertView
				.setOnLongClickListener(new OnDeleteCarClickListener(entity));
		
		return convertView;
	}

	int num = 0;
	
	// 查询回调
	class QueryTrafficCallBack implements HttpCallBack<BaseResp> {
		TextView violationCount;
		View convertView;

		QueryTrafficCallBack(String carNo, String carLastCodes,
				TextView violationCount, View convertView) {
			this.violationCount = violationCount;
			this.convertView = convertView;
		}

		@Override
		public void call(BaseResp response) {
			if (response.isSuccess()) {
				TrafficViolationsEntity entity = (TrafficViolationsEntity) response
						.getObj();
				List<TrafficViolationsInfo> lists = entity.getCarWZInfo();
				if (lists != null && lists.size() > 0) {
					violationCount.setText(String.valueOf(lists.size()));
					convertView
							.setOnClickListener(new OnCarNoClickListener_new(
									lists));
					++num;
				}
				
//				if (num == list.size()) {
//					LoadingDialog.getDialog(activity).dismiss();
//					
//				}
				
				// CommonUtils.showToast(mActivity, response.getDesc(),
				// Toast.LENGTH_SHORT);

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
	 * 删除违章车辆
	 */
	class OnDeleteCarClickListener implements OnLongClickListener {

		WZconfig entity;

		OnDeleteCarClickListener(WZconfig entity) {
			this.entity = entity;
		}

		@Override
		public boolean onLongClick(View v) {
			AlertBaseHelper.showConfirm(
					activity,
					null,
					context.getString(R.string.violation_sure_delete, entity
							.getKeyList().get(0).getKeyValue()),
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							LoadingDialog.getDialog(activity)
									.setMessage("删除中，请稍后").show();
							TrafficViolationsBo.getInstance(context)
									.deleteTrafficViolations(
											new OnDeleteCallBack(entity),
											entity.getKeyList().get(0)
													.getKeyValue(),
											entity.getKeyList().get(2)
													.getKeyValue(),
											CarType.SMALL_CAR);
						}
					});
			return false;
		}
	}

	/**
	 * 删除回调
	 */
	class OnDeleteCallBack implements HttpCallBack<BaseResp> {

		WZconfig entity;

		OnDeleteCallBack(WZconfig entity) {
			this.entity = entity;
		}

		@Override
		public void call(BaseResp response) {
			LoadingDialog.getDialog(activity).dismiss();
			if (response.isSuccess()) {
				TrafficViolationsService.getInstance(context)
						.deleteByCarNoAndLastCodes(
								entity.getKeyList().get(0).getKeyValue(),
								entity.getKeyList().get(2).getKeyValue());
				list.remove(entity);
				// TrafficViolationsBo.getInstance(context).cancelQueryTask(
				// entity.getKeyList().get(0).getKeyValue());
				notifyDataSetChanged();
			} else {
				CommonUtils.showToast(activity,
						R.string.violation_delete_failed, Toast.LENGTH_SHORT);
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
	 * 车辆列表点击
	 */
	class OnCarNoClickListener implements OnClickListener {

		private String carNo;
		private String carLastCodes;

		OnCarNoClickListener(String carNo, String carLastCodes) {
			this.carNo = carNo;
			this.carLastCodes = carLastCodes;
		}

		@Override
		public void onClick(View v) {
			Intent i = new Intent(activity, TrafficViolationsInfoActivity.class);
			i.putExtra(Key.K_CAR_NO, carNo);
			i.putExtra(Key.K_CAR_LAST_CODES, carLastCodes);
			i.putExtra(Key.K_RETURN_TITLE,
					activity.getString(R.string.violation_title));
			activity.startActivity(i);
		}
	}

	/**
	 * 车辆列表点击
	 */
	class OnCarNoClickListener_new implements OnClickListener {

		private List<TrafficViolationsInfo> list;

		OnCarNoClickListener_new(List<TrafficViolationsInfo> list) {
			this.list = list;
		}

		@Override
		public void onClick(View v) {
			Intent i = new Intent(activity, TrafficViolationsInfoActivity.class);
			i.putExtra("list", (Serializable) list);
			i.putExtra(Key.K_RETURN_TITLE,
					activity.getString(R.string.violation_title));
			activity.startActivity(i);
		}
	}

}
