package cn.ffcs.wisdom.city.traffic.violations;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.method.ReplacementTransformationMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
//import cn.ffcs.external.watercoal.common.Constants;
import cn.ffcs.ui.tools.TopUtil;
import cn.ffcs.widget.LoadingDialog;
import cn.ffcs.wisdom.city.WisdomCityActivity;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.sqlite.service.TrafficViolationsService;
import cn.ffcs.wisdom.city.traffic.violations.bo.TrafficViolationsBo;
import cn.ffcs.wisdom.city.traffic.violations.common.CarType;
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
 * <p>Title:   添加查询车辆         </p>
 * <p>Description:                     </p>
 * <p>@author: zhangwsh                </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-8-30           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class AddCarActivity extends WisdomCityActivity {

	private EditText carNoView;
	private EditText carLastCodesView;
	private TextView queryTrafficViolations;

	TextView mXieyi;
	CheckBox mRemainAccount; // 记住帐号

	private final static String xieyi = "已了解服务协议";

	@Override
	protected void initComponents() {
		carNoView = (EditText) findViewById(R.id.car_no);
		carNoView.setTransformationMethod(new InputLowerToUpper());
		carLastCodesView = (EditText) findViewById(R.id.car_last_codes);
		queryTrafficViolations = (TextView) findViewById(R.id.query);
		queryTrafficViolations.setOnClickListener(new OnQueryClickListener());

		mRemainAccount = (CheckBox) findViewById(R.id.remain_account);

		mXieyi = (TextView) findViewById(R.id.user_search_xieyi);
		mXieyi.setText(Html.fromHtml(xieyi));
		mXieyi.setMovementMethod(LinkMovementMethod.getInstance());
		SpannableString spanStr = new SpannableString(Html.fromHtml(xieyi));
		spanStr.setSpan(new SpanClick(), 3, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		mXieyi.setText(spanStr);
	}

	public class InputLowerToUpper extends ReplacementTransformationMethod{ 
	    @Override 
	    protected char[] getOriginal() { 
	        char[] lower = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z' }; 
	        return lower; 
	    } 
	   
	    @Override 
	    protected char[] getReplacement() { 
	        char[] upper = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z' }; 
	        return upper; 
	    } 
	   
	}
	
	class SpanClick extends ClickableSpan {

		@Override
		public void onClick(View widget) {
			// 跳转到协议wap页面
			Intent i = new Intent();
			i.setClassName(mContext, "cn.ffcs.wisdom.city.web.BrowserActivity");
//			i.putExtra(K.U_BROWSER_URL, Constants.URL_USER_NOTE);
//			i.putExtra(K.U_BROWSER_TITLE, "服务协议");
			startActivity(i);
		}

		@Override
		public void updateDrawState(TextPaint ds) {
			ds.setColor(Color.parseColor("#0799ea"));
			ds.setUnderlineText(true);
		}

	}

	// 查询点击
	class OnQueryClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			String carNo = carNoView.getText().toString().toUpperCase();
			String carLastCodes = carLastCodesView.getText().toString().toUpperCase();

			if (StringUtil.isEmpty(carNo)) {
				CommonUtils.showToast(mActivity, "车牌号不能为空", Toast.LENGTH_SHORT);
				return;
			}

			if (StringUtil.isEmpty(carLastCodes)) { // 校验车架后4位
				CommonUtils.showToast(mActivity, "车架后四位不能为空", Toast.LENGTH_SHORT);
				return;
			}

			if (carNo.trim().length() != 6) { // 校验车牌号
				CommonUtils.showToast(mActivity,
						mContext.getString(R.string.violation_input_car_number_error),
						Toast.LENGTH_SHORT);
				return;
			}

			if (carLastCodes.trim().length() != 4) { // 校验车架后4位
				CommonUtils.showToast(mActivity,
						mContext.getString(R.string.violation_input_last_codes_error),
						Toast.LENGTH_SHORT);
				return;
			}

//			if (TrafficViolationsService.getInstance(mContext).isExist(carNo, carLastCodes)) {
//				CommonUtils.showToast(mActivity, mContext.getString(R.string.violation_is_exist),
//						Toast.LENGTH_SHORT);
//				return;
//			}
			
//			if (!mRemainAccount.isChecked()) {
//				AlertBaseHelper.showConfirm(mActivity, R.string.wc_usernote_dialog_title,
//						R.string.wc_usernote_tips, R.string.wc_usernote_dialog_confirmbtn,
//						R.string.wc_usernote_dialog_concernbtn, new View.OnClickListener() {
//							@Override
//							public void onClick(View v) {
//								// 跳转到协议wap页面
//								Intent i = new Intent();
//								i.setClassName(mContext, "cn.ffcs.wisdom.city.web.BrowserActivity");
//								i.putExtra(K.U_BROWSER_URL, Constants.URL_USER_NOTE);
//								i.putExtra(K.U_BROWSER_TITLE, "服务协议");
//								startActivity(i);
//							}
//						}, new OnClickListener() {
//
//							@Override
//							public void onClick(View v) {
//								mRemainAccount.setChecked(true);
//							}
//						});
//				return;
//			}

			LoadingDialog.getDialog(mActivity).setMessage(getString(R.string.violation_querying))
					.show();
			TrafficViolationsBo.getInstance(mContext).queryTrafficViolations(
					new QueryTrafficCallBack(carNo, carLastCodes), carNo, carLastCodes,
					CarType.SMALL_CAR);
		}
	}

	// 查询回调
	class QueryTrafficCallBack implements HttpCallBack<BaseResp> {
		private String carNo;
		private String carLastCodes;

		QueryTrafficCallBack(String carNo, String carLastCodes) {
			this.carNo = carNo;
			this.carLastCodes = carLastCodes;
		}

		@Override
		public void call(BaseResp response) {
			//记录查询时间
			Date date = new Date();
			SharedPreferencesUtil.setLong(mContext, Key.LAST_REFRESH_TIME, date.getTime());
			
			if (response.isSuccess()) {
				TrafficViolationsEntity entity = (TrafficViolationsEntity) response.getObj();
				List<TrafficViolationsInfo> list = entity.getCarWZInfo();
//				if (list != null && list.size() > 0) {
//					TrafficViolationsService.getInstance(mContext).saveViolation(carNo,
//							carLastCodes, list);
//				} else if (list != null && list.size() == 0) {
//					TrafficViolationsService.getInstance(mContext).saveEmptyList(carNo,
//							carLastCodes);
//				}
				LoadingDialog.getDialog(mActivity).dismiss();
				Intent i = new Intent(mActivity, TrafficViolationsInfoActivity.class);
				i.putExtra("list", (Serializable) list);
				i.putExtra(Key.K_RETURN_TITLE, getString(R.string.violation_title));
				startActivity(i);
				finish();
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
	protected void initData() {
		TopUtil.updateTitle(mActivity, R.id.top_title, R.string.violation_add_car);
		initCarCode();// 初始化车牌号城市
	}

	/**
	 * 初始化车牌号城市
	 */
	private void initCarCode() {
		String citycode = MenuMgr.getInstance().getCityCode(mContext);// 获取城市代号
		carNoView.setText(XmlParser.getCarTypeByCitycode(mContext, citycode));// 为Edit添加省份简称
		carNoView.setSelection(carNoView.getText().length());
	}

	@Override
	protected int getMainContentViewId() {
		return R.layout.act_traffic_violations_add_car;
	}
}
