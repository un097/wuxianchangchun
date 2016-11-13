package cn.ffcs.wisdom.city.traffic.violations;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import cn.ffcs.ui.tools.TopUtil;
import cn.ffcs.wisdom.city.WisdomCityActivity;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.personcenter.LoginActivity;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.traffic.violations.bo.TrafficViolationsBo;
import cn.ffcs.wisdom.city.traffic.violations.entity.AllPayInfoEntity;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.CommonUtils;

/**
 * <p>Title:   违章全部处理       </p>
 * <p>Description:                     </p>
 * <p>@author: zhangwsh                </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-9-26           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class ViolationsHandleActivity extends WisdomCityActivity {

	private View goTopPay;
	private String carNo;
	private String carLastCodes;
	private String provinceName = "闽";// 车牌号省份名
	private AllPayInfoEntity entity;
	private TextView carNoTextView;
	private TextView violationCount;
	private TextView violationMark;
	private TextView violationAllPay;
	private TextView violationAgencyMoney;
	private TextView violationPay;

	@Override
	protected void initComponents() {
		goTopPay = findViewById(R.id.goto_pay);
		carNoTextView = (TextView) findViewById(R.id.car_no);
		violationCount = (TextView) findViewById(R.id.violation_count);
		violationAgencyMoney = (TextView) findViewById(R.id.violation_all_agency_money);
		violationPay = (TextView) findViewById(R.id.violation_all_money);
		violationAllPay = (TextView) findViewById(R.id.all_money_count);
	}

	class OnPayClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (AccountMgr.getInstance().isLogin(mContext)) {
				Intent intent = new Intent();
				intent.setClassName(mActivity, "cn.aiqc.park.activity.PayActivity");
				intent.putExtra(Key.K_RETURN_TITLE, getString(R.string.violation_money_detial));
				intent.putExtra("cjh", carLastCodes);
				intent.putExtra("cph", provinceName + carNo);
				intent.putExtra("money", entity.total_count);
				intent.putExtra("phoneNo", AccountMgr.getInstance().getMobile(mContext));
				startActivity(intent);
			} else {
				Intent intent = new Intent(mActivity,LoginActivity.class);
				startActivity(intent);
			}
		}
	}

	@Override
	protected void initData() {
		Intent i = getIntent();
		carNo = i.getStringExtra(Key.K_CAR_NO);
		carLastCodes = i.getStringExtra(Key.K_CAR_LAST_CODES);
		TopUtil.updateTitle(mActivity, R.id.top_title, carNo);
		TrafficViolationsBo.getInstance(mContext).getViolationAllPay(new GetAllPayCallBack(),
				carNo, carLastCodes);
	}

	class GetAllPayCallBack implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp response) {
			if (response.isSuccess()) {
				entity = (AllPayInfoEntity) response.getObj();
				carNoTextView.setText(entity.car_last_code);
				violationCount.setText(entity.violate_count);
				violationMark.setText(entity.violate_marking);
				violationPay.setText(String.valueOf(entity.penalty_amount));
				violationAgencyMoney.setText(String.valueOf(entity.agency_fees));
				violationAllPay.setText(String.valueOf(entity.total_count));
				goTopPay.setOnClickListener(new OnPayClickListener());
			} else {
				CommonUtils.showToast(mActivity, response.getDesc(), Toast.LENGTH_SHORT);
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
		return R.layout.act_violation_handle;
	}
}
