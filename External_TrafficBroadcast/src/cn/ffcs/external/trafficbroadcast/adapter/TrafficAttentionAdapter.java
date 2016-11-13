package cn.ffcs.external.trafficbroadcast.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.sax.StartElementListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.ffcs.external.trafficbroadcast.activity.TrafficBroadcastActivity;
import cn.ffcs.external.trafficbroadcast.activity.TrafficListActivity;
import cn.ffcs.external.trafficbroadcast.activity.TrafficLocationActivity;
import cn.ffcs.external.trafficbroadcast.bo.Traffic_Attention_Bo;
import cn.ffcs.external.trafficbroadcast.entity.Traffic_AttentionOper_Entity;
import cn.ffcs.external.trafficbroadcast.entity.Traffic_IsCollectedItem_Entity;
import cn.ffcs.widget.LoadingDialog;
import cn.ffcs.wisdom.city.changecity.location.LocationUtil;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.personcenter.entity.Account;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.SharedPreferencesUtil;

import com.example.external_trafficbroadcast.R;
import com.umeng.analytics.MobclickAgent;

/**
 * 播报列表适配器
 * 
 * @author daizhq
 * 
 * @date 2014.12.01
 */
public class TrafficAttentionAdapter extends BaseAdapter {

	public List<Traffic_IsCollectedItem_Entity> dates;
	private Context context;
	private LayoutInflater MyInflater;
	private Handler handler;

	private Traffic_Attention_Bo attentionBo = null;

	public TrafficAttentionAdapter(Context context, Handler handler,
			List<Traffic_IsCollectedItem_Entity> dates) {
		this.context = context;
		this.dates = dates;
		this.handler = handler;
		this.MyInflater = LayoutInflater.from(this.context);
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return dates.size();
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return dates.get(position);
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = MyInflater.inflate(R.layout.item_bobao, null, false);
			viewHolder = new ViewHolder();
			viewHolder.location = (TextView) convertView.findViewById(R.id.tv_location);
			viewHolder.distance = (TextView) convertView.findViewById(R.id.tv_distance);
			viewHolder.detail = (TextView) convertView.findViewById(R.id.tv_detail);
			viewHolder.time = (TextView) convertView.findViewById(R.id.tv_time);
			viewHolder.iv_attention = (ImageView) convertView.findViewById(R.id.iv_attention);
			viewHolder.iv_icon_bg = (ImageView) convertView.findViewById(R.id.iv_bg_icon);
			viewHolder.iv_icon_head = (ImageView) convertView.findViewById(R.id.iv_head_icon);
			viewHolder.ll_attention = (RelativeLayout) convertView.findViewById(R.id.ll_attention);
			viewHolder.ll_distance = (LinearLayout) convertView.findViewById(R.id.ll_distance);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.location.setText(dates.get(position).getTitle());
		String dis = "";
		double disDouble = Double.valueOf(dates.get(position).getDistance());
		if (disDouble <= 1000) {
			dis = "距离:" + String.format("%.2f", disDouble) + "米";
		} else {
			dis = "距离:" + String.format("%.2f", disDouble / 1000) + "公里";
		}
		viewHolder.distance.setText(dis);
		viewHolder.detail.setText(dates.get(position).getDetail());

		// 间隔时间为0表示1分钟之内
		if (dates.get(position).getInterval_time() == 0) {
			viewHolder.time.setText("1分钟内");
		} else {
			int mIntervalTime = dates.get(position).getInterval_time();
			if (mIntervalTime < 60) {// 小于1小时
				viewHolder.time.setText(String.valueOf(dates.get(position).getInterval_time())
						+ "分钟前");
			} else if (mIntervalTime < 60 * 24) {// 小于1天
				viewHolder.time.setText(String.valueOf(dates.get(position).getInterval_time() / 60)
						+ "小时前");
			} else if (mIntervalTime >= 60 * 24) {//大于1天
				viewHolder.time.setText(String.valueOf(dates.get(position).getInterval_time() / (60 * 24))
						+ "天前");
			}
		}

		// 是否已添加收藏
		if (dates.get(position).getIs_collected() == 1) {// 1为已收藏
			viewHolder.iv_attention.setBackgroundDrawable(context.getResources().getDrawable(
					R.drawable.iv_attention));
		} else {
			viewHolder.iv_attention.setBackgroundDrawable(context.getResources().getDrawable(
					R.drawable.iv_no_attention));
		}

		// 底片图标
		if (dates.get(position).getStatus() == 1) {// 顺畅
			viewHolder.iv_icon_bg.setBackgroundDrawable(context.getResources().getDrawable(
					R.drawable.iv_shunchang_bg));
		} else if (dates.get(position).getStatus() == 2) {// 缓慢
			viewHolder.iv_icon_bg.setBackgroundDrawable(context.getResources().getDrawable(
					R.drawable.iv_huanman_bg));
		} else if (dates.get(position).getStatus() == 3) {// 道路封闭
			viewHolder.iv_icon_bg.setBackgroundDrawable(context.getResources().getDrawable(
					R.drawable.iv_daolufengbi_bg));
		} else if (dates.get(position).getStatus() == 4) {// 拥堵
			viewHolder.iv_icon_bg.setBackgroundDrawable(context.getResources().getDrawable(
					R.drawable.iv_yongdu_bg));
		} else if (dates.get(position).getStatus() == 5) {// 事故
			viewHolder.iv_icon_bg.setBackgroundDrawable(context.getResources().getDrawable(
					R.drawable.iv_shigu_bg));
		} else if (dates.get(position).getStatus() == 6) {// 警察执法
			viewHolder.iv_icon_bg.setBackgroundDrawable(context.getResources().getDrawable(
					R.drawable.iv_jingchazhifa_bg));
		}

		// 左上角图标
		if (dates.get(position).getSource().equals("0")) {// 官方
			viewHolder.iv_icon_head.setBackgroundDrawable(context.getResources().getDrawable(
					R.drawable.iv_head_guanfang));
		} else if (dates.get(position).getSource().equals("1")) {// 用户
			viewHolder.iv_icon_head.setBackgroundDrawable(context.getResources().getDrawable(
					R.drawable.iv_head_user));
		}

		// 关注图标监听
		viewHolder.ll_attention.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				HashMap<String, String> param = new HashMap<String, String>();
				param.put("A_collectTraffic_trafficList_trafficTitle", dates.get(position).getTitle());
				MobclickAgent.onEvent(context, "E_C_trafficList_collectTrafficClick", param);
				Account account = AccountMgr.getInstance().getAccount(context);
				String user_id = String.valueOf(account.getData().getUserId());
				// 测试使用用户账号
				// String user_id = "7623773";
				boolean isLogin = Boolean.parseBoolean(SharedPreferencesUtil.getValue(
						context, Key.K_IS_LOGIN));
				if (!isLogin) {// 如果是未登录用户就不用请求收藏列表
					handler.obtainMessage(4).sendToTarget();
				} else {

					showProgressBar("正在提交...");
					attentionBo = new Traffic_Attention_Bo(context);

					Map<String, Object> params = new HashMap<String, Object>(1);

					String mobile = account.getData().getMobile();
					String lat = LocationUtil.getLatitude(context);
					String lng = LocationUtil.getLongitude(context);
					String sign = user_id;

					// String cityCode =
					// MenuMgr.getInstance().getCityCode(mContext);

					if (lat == null || lat.equals("")) {
						lat = "unknown";
					}
					if (lng == null || lng.equals("")) {
						lng = "unknown";
					}
					if (mobile == null || mobile.equals("")) {
						mobile = "unknown";
					}

					params.put("city_code", "2201");
					params.put("org_code", "2201");
					params.put("mobile", mobile);
					params.put("longitude", lng);
					params.put("latitude", lat);
					params.put("sign", sign);
					params.put("user_id", user_id);

					String[] road_ids = new String[10];
					road_ids[0] = String.valueOf(String.valueOf(dates.get(position).getId()));

					params.put("road_ids", road_ids);
					// 1为收藏列表，点击则选择取消收藏，否则为添加收藏
					if (dates.get(position).getIs_collected() == 1) {
						params.put("oper_type", "2");
					} else {
						params.put("oper_type", "1");
					}

					attentionBo
							.startRequestTask(new AttentionOperCallBack(), context, params,
									"http://ccgd.153.cn:50081/icity-api-client-other/icity/service/lbs/road/collect");
				}
			}
		});

		// 定位监听
		viewHolder.ll_distance.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 跳转到地图页面定位
				// Intent intent = new Intent();
				// intent.setClass(context, TrafficLocationActivity.class);
				// intent.putExtra("lat", dates.get(position).getLat());
				// intent.putExtra("lng", dates.get(position).getLng());
				// context.startActivity(intent);
			}
		});

		return convertView;
	}

	/**
	 * 获取收藏列表回调
	 * */
	class AttentionOperCallBack implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp response) {
			// TODO Auto-generated method stub
			System.out.println("收藏操作返回====>>" + response.getHttpResult());
			if(response.isSuccess()){
				Traffic_AttentionOper_Entity attentionOperEntity = (Traffic_AttentionOper_Entity) response
						.getObj();
				if (attentionOperEntity.getResult_code().equals("0")) {
					hideProgressBar();
					CommonUtils.showToast((Activity) context, "提交成功！", Toast.LENGTH_SHORT);
					showProgressBar("正在加载路况列表...");
					handler.obtainMessage(5).sendToTarget();
				} else {
					hideProgressBar();
					CommonUtils.showToast((Activity) context, "提交失败！", Toast.LENGTH_SHORT);
				}
			}else{
				hideProgressBar();
				CommonUtils.showToast((Activity) context, "提交失败！", Toast.LENGTH_SHORT);
			}
		}

		@Override
		public void progress(Object... obj) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onNetWorkError() {
			// TODO Auto-generated method stub

		}

	}

	class ViewHolder {
		// 播报地点
		TextView location;
		// 播报距离
		TextView distance;
		// 播报详情
		TextView detail;
		// 播报时间
		TextView time;
		// 关注图标
		ImageView iv_attention;
		// 图标底片
		ImageView iv_icon_bg;
		// 左上角图标
		ImageView iv_icon_head;
		// 添加/取消收藏列表点击区域
		RelativeLayout ll_attention;
		// 地理位置区域
		LinearLayout ll_distance;
	}

	/**
	 * 读取进度条
	 * */
	public void showProgressBar(String message) {
		LoadingDialog.getDialog((Activity) context).setMessage(message).show();
	}

	/**
	 * 隐藏进度条
	 * */
	public void hideProgressBar() {
		LoadingDialog.getDialog((Activity) context).cancel();
	}

}