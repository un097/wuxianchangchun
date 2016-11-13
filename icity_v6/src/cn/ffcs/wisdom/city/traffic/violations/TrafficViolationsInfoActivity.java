package cn.ffcs.wisdom.city.traffic.violations;

import java.util.List;

import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import cn.ffcs.ui.tools.TopUtil;
import cn.ffcs.widget.LoadingDialog;
import cn.ffcs.wisdom.city.WisdomCityActivity;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.sqlite.model.TrafficViolations;
import cn.ffcs.wisdom.city.sqlite.service.TrafficViolationsService;
import cn.ffcs.wisdom.city.traffic.violations.adapter.ViolationsInfoExpandListAdapter;
import cn.ffcs.wisdom.city.traffic.violations.bo.TrafficViolationsBo;
import cn.ffcs.wisdom.city.traffic.violations.common.CarType;
import cn.ffcs.wisdom.city.traffic.violations.entity.TrafficViolationsEntity;
import cn.ffcs.wisdom.city.traffic.violations.entity.TrafficViolationsEntity.TrafficViolationsInfo;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.StringUtil;

/**
 * <p>Title:   违章详细列表          </p>
 * <p>Description:                     </p>
 * <p>@author: zhangwsh                </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-9-2           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class TrafficViolationsInfoActivity extends WisdomCityActivity {

	private String carNo;
	private String carLastCodes;
	private ExpandableListView exListView;
	private ViolationsInfoExpandListAdapter adapter;
	private List<TrafficViolationsInfo> list;
	private boolean first = true;
	private boolean isGetValue = false;
	private LinearLayout noData;
//	private TextView topRight;

	@Override
	protected void initComponents() {
		exListView = (ExpandableListView) findViewById(R.id.violations_list);
		noData = (LinearLayout) findViewById(R.id.no_data);
//		topRight = (TextView) findViewById(R.id.top_right_title);
//		TopUtil.updateTitle(topRight, getString(R.string.violation_all_handle));
//		topRight.setVisibility(View.VISIBLE);
//		topRight.setOnClickListener(new TopRightClick());
	}

	class TopRightClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent i = new Intent(mActivity, ViolationsHandleActivity.class);
			i.putExtra(Key.K_CAR_NO, carNo);
			i.putExtra(Key.K_CAR_LAST_CODES, carLastCodes);
			startActivity(i);
		}
	}

	@Override
	protected void initData() {
		Intent i = getIntent();
		carNo = i.getStringExtra(Key.K_CAR_NO);
		carLastCodes = i.getStringExtra(Key.K_CAR_LAST_CODES);
		isGetValue = i.getBooleanExtra(Key.K_IS_GET_VALUE, false);
		list = (List<TrafficViolationsInfo>)i.getSerializableExtra("list");
		TopUtil.updateTitle(mActivity, R.id.top_title, carNo);
		exListView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition,
					long id) {
				boolean isExpanded = parent.isGroupExpanded(groupPosition);
				if (isExpanded) {
					((ImageView) v.findViewById(R.id.cursor))
							.setImageResource(R.drawable.down_arrow);
				} else {
					((ImageView) v.findViewById(R.id.cursor)).setImageResource(R.drawable.up_arrow);
				}
				return false;
			}
		});
		MobclickAgent.onEvent(mContext, "E_C_violationQuery_violationQueryClick");
		initView();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
//		if (first) {
//			first = false;
//			if (isGetValue) {
//				LoadingDialog.getDialog(mActivity).setMessage("查询中").show();
//				TrafficViolationsBo.getInstance(mContext).queryTrafficViolations(
//						new QueryTrafficCallBack(carNo, carLastCodes), carNo, carLastCodes,
//						CarType.SMALL_CAR);
//			} else {
//				initView();
//			}
//		}
		super.onWindowFocusChanged(hasFocus);
	}

	/**
	 * 显示UI
	 */
	private void initView() {
		if (list != null && list.size() > 0) {
			noData.setVisibility(View.GONE);
			adapter = new ViolationsInfoExpandListAdapter(mContext, list);
			exListView.setAdapter(adapter);
		} else {
			noData.setVisibility(View.VISIBLE);
		}
	}

	class QueryTrafficCallBack implements HttpCallBack<BaseResp> {
		private String carNo;
		private String carLastCodes;

		QueryTrafficCallBack(String carNo, String carLastCodes) {
			this.carNo = carNo;
			this.carLastCodes = carLastCodes;
		}

		@Override
		public void call(BaseResp response) {
			if (response.isSuccess()) {
				TrafficViolationsEntity entity = (TrafficViolationsEntity) response.getObj();
				List<TrafficViolationsInfo> requestList = entity.getCarWZInfo();
				if (requestList != null && requestList.size() > 0) {
					TrafficViolationsService.getInstance(mContext).saveViolation(carNo,
							carLastCodes, requestList);
				} else if (requestList != null && requestList.size() == 0) {
					TrafficViolationsService.getInstance(mContext).saveEmptyList(carNo,
							carLastCodes);
				}
				initView();
				LoadingDialog.getDialog(mActivity).dismiss();
			} else {
				LoadingDialog.getDialog(mActivity).dismiss();
				if (!StringUtil.isEmpty(response.getDesc())) {
					CommonUtils.showToast(mActivity, response.getDesc(), Toast.LENGTH_SHORT);
				}
			}
		}

		@Override
		public void progress(Object... obj) {
		}

		@Override
		public void onNetWorkError() {
		}
	}

	@Override
	protected int getMainContentViewId() {
		return R.layout.act_traffic_violations_info;
	}
}
