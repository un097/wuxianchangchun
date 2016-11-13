package cn.ffcs.external.trafficbroadcast.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;
import cn.ffcs.external.trafficbroadcast.activity.MyPhotoListActivity;
import cn.ffcs.external.trafficbroadcast.activity.TrafficBroadcastActivity;
import cn.ffcs.external.trafficbroadcast.activity.TrafficPublishActivity;
import cn.ffcs.external.trafficbroadcast.bo.Traffic_Baoliao_Bo;
import cn.ffcs.external.trafficbroadcast.entity.Traffic_Baoliao_Entity;
import cn.ffcs.external.trafficbroadcast.entity.Traffic_ItemDetail_Entity;
import cn.ffcs.wisdom.city.changecity.location.LocationUtil;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.personcenter.entity.Account;
import cn.ffcs.wisdom.city.simico.base.Application;
import cn.ffcs.wisdom.city.simico.image.CommonImageLoader;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.BitmapUtil;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.SharedPreferencesUtil;
import cn.ffcs.wisdom.tools.SystemCallUtil;

import com.example.external_trafficbroadcast.R;
import com.umeng.analytics.MobclickAgent;

/**
 * 弹出框处理封装(右下角点击红色喇叭弹出的pop框等等地图用到的各种对话框)
 * 
 * @author daizhq
 * 
 * @date 2014.11.10
 * */
public class PopWindow {

	static PopupWindow window1 = null;// 爆料框
	static PopupWindow window2 = null;// 播报框
	static PopupWindow window3 = null;// 发布框
	static PopupWindow window4 = null;// 选择照片框
	static PopupWindow window5 = null;// 爆料详情框
	static PopupWindow window6 = null;// 点击显示地点名称框
	static PopupWindow window7 = null;// 文字播报界面弹出的语音播报框

	private static ImageView iv_broadcast;// 语音播报
	private static ImageView iv_road;// 定制道路
	private static ImageView iv_near;// 附近道路

	private static boolean bb = false;// 控制语音播报开关
	private static boolean br = false;// 控制定制道路开关
	private static boolean bn = false;// 控制附近道路开关

	private static ImageView iv_shunchang;// 顺畅按钮
	private static ImageView iv_huanman;// 缓慢按钮
	private static ImageView iv_yongdu;// 拥堵按钮
	private static ImageView iv_shigu;// 事故按钮
	private static ImageView iv_daolufengbi;// 道路封闭按钮
	private static ImageView iv_jingchazhifa;// 警察执法按钮

	// 选择爆料的类型，默认为顺畅
	// 1顺畅 2缓慢 3拥堵 4道路封闭 5事故 6警察执法
	private static int status = 1;

	private static TextView tv_detail;// 爆料详情
	private static EditText et_location;// 当前位置输入框
	private static ImageView iv_icon;// 路况状态图标
	private static ImageView iv_select_pic;// 点击上传图片按钮
	private static Button btn_cancel;// 取消发布按钮
	private static Button btn_send;// 确定发布按钮
	private static LinearLayout ll_east;// 向东
	private static LinearLayout ll_west;// 向西
	private static LinearLayout ll_south;// 向南
	private static LinearLayout ll_north;// 向北
	private static ImageView iv_east;// 选中向东
	private static ImageView iv_west;// 选中向西
	private static ImageView iv_south;// 选中向南
	private static ImageView iv_north;// 选中向北

	private static ImageView iv_broadcast_list;// 语音播报
	private static ImageView iv_road_list;// 定制道路
	private static ImageView iv_near_list;// 附近道路

	private static boolean bb_list = false;// 控制语音播报开关
	private static boolean br_list = false;// 控制定制道路开关
	private static boolean bn_list = false;// 控制附近道路开关

	// 爆料朝向，默认向东
	// 1向东 2向西 3向南 4向北
	private static String face = "向东";

	private static ImageView iv_head;// 详情用户头像
	private static TextView tv_phone;// 详情用户手机号码
	// 详情类型
	// 1顺畅 2缓慢 3拥堵 4道路封闭 5事故 6警察执法
	private static ImageView iv_type;
	private static TextView tv_detail5;// 打点详情
	private static TextView tv_title5;// 打点标题

	// 打点详情对象
	private static Traffic_ItemDetail_Entity itemEntity = null;

	private static Traffic_Baoliao_Bo baoliaoBo = null;
	private static Traffic_Baoliao_Entity baoliaoEntity = null;

	private static TextView tv_photo;// 选择拍照按钮
	private static TextView tv_alum;// 选择选取本地图片按钮
	private static TextView tv_cancel;// 取消上传图片按钮

	private static Context context;
	private static Activity mActivity;
	// 遮罩层图片
	private static ImageView helpImg;
	// 当前定位的地点名称
	private static String currentLocationName;
	private static String inputStr = "";

	// 显示地点的位置名称
	private static TextView tv_location;
	// 点击地点的位置名称
	private static String locationName = "";

	private static Handler handler;
	
	static Bitmap head_pic_bitmapBitmap;

	private static CommonImageLoader mImageLoader = new CommonImageLoader(
			Application.context().getApplicationContext());;

	public PopWindow(Context context, ImageView helpImg,
			String currentLocationName) {
		this.context = context;
		this.mActivity = (TrafficBroadcastActivity) context;
		this.helpImg = helpImg;
		this.currentLocationName = currentLocationName;
	}

	public PopWindow(Context context, ImageView helpImg,
			String currentLocationName, Handler handler) {
		this.context = context;
		this.mActivity = (TrafficBroadcastActivity) context;
		this.helpImg = helpImg;
		this.currentLocationName = currentLocationName;
		this.handler = handler;
	}

	public PopWindow(Context context, ImageView helpImg,
			Traffic_ItemDetail_Entity itemEntity) {
		this.context = context;
		this.helpImg = helpImg;
		this.itemEntity = itemEntity;
	}

	public PopWindow(ImageView helpImg, Context context, String locationName) {
		this.context = context;
		this.helpImg = helpImg;
		this.locationName = locationName;
	}

	public PopWindow(ImageView helpImg, Context context, Handler handler) {
		this.context = context;
		this.helpImg = helpImg;
		this.handler = handler;
	}
	
	public void setCurrentLocationName(String locationName){
		this.currentLocationName = locationName;
	}

	/**
	 * 弹出popwindow及其控件
	 * */
	@SuppressLint("NewApi")
	public static void popAwindow(View parent, int flag) {

		if (flag == 1) {
			// 弹出爆料框
			View v = ((Activity) context).getLayoutInflater().inflate(
					R.layout.pop_baoliao, null);
			window1 = new PopupWindow(v, CommonUtils.convertDipToPx(context,
					200), CommonUtils.convertDipToPx(context, 135));
			// 设置整个popupwindow的样式。
			window1.setBackgroundDrawable(context.getResources().getDrawable(
					R.drawable.baoliao_bg));
			window1.setFocusable(true);
			window1.update();
			int[] location = new int[2];
			parent.getLocationOnScreen(location);
			window1.showAtLocation(parent, Gravity.NO_GRAVITY, location[0]
					- window1.getWidth(), location[1] - window1.getHeight());

			// window一旦消失，必须去掉遮罩层
			window1.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss() {
					// TODO Auto-generated method stub
					hideHelpImg();
				}
			});

			iv_shunchang = (ImageView) v.findViewById(R.id.iv_shunchang);
			iv_huanman = (ImageView) v.findViewById(R.id.iv_huanman);
			iv_yongdu = (ImageView) v.findViewById(R.id.iv_yongdu);
			iv_shigu = (ImageView) v.findViewById(R.id.iv_shigu);
			iv_daolufengbi = (ImageView) v.findViewById(R.id.iv_daolufengbi);
			iv_jingchazhifa = (ImageView) v.findViewById(R.id.iv_jingchazhifa);

			iv_shunchang.setOnClickListener(baoliaoListener);
			iv_huanman.setOnClickListener(baoliaoListener);
			iv_yongdu.setOnClickListener(baoliaoListener);
			iv_shigu.setOnClickListener(baoliaoListener);
			iv_daolufengbi.setOnClickListener(baoliaoListener);
			iv_jingchazhifa.setOnClickListener(baoliaoListener);

		} else if (flag == 2) {
			// 弹出语音播报开关框
			View v = ((Activity) context).getLayoutInflater().inflate(
					R.layout.pop_bobao, null);
			window2 = new PopupWindow(v, CommonUtils.convertDipToPx(context,
					200), CommonUtils.convertDipToPx(context, 145));
			// 设置整个popupwindow的样式。
			window2.setBackgroundDrawable(context.getResources().getDrawable(
					R.drawable.bobao_bg));
			window2.setFocusable(true);
			window2.update();
			int[] location = new int[2];
			parent.getLocationOnScreen(location);
			window2.showAtLocation(parent, Gravity.NO_GRAVITY, location[0]
					+ parent.getWidth(), location[1] - 20);

			// window一旦消失，必须去掉遮罩层
			window2.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss() {
					// TODO Auto-generated method stub
					hideHelpImg();
				}
			});

			iv_broadcast = (ImageView) v.findViewById(R.id.iv_broadcast);
			iv_road = (ImageView) v.findViewById(R.id.iv_road);
			iv_near = (ImageView) v.findViewById(R.id.iv_near);

			bb = SharedPreferencesUtil.getBoolean(context, "k_road_broadcast");
			br = SharedPreferencesUtil.getBoolean(context, "k_road_collected");
			bn = SharedPreferencesUtil.getBoolean(context, "k_road_near");

			if (bb) {
				iv_broadcast.setBackgroundDrawable(context.getResources()
						.getDrawable(R.drawable.open));
			} else {
				iv_broadcast.setBackgroundDrawable(context.getResources()
						.getDrawable(R.drawable.off));
			}

			if (br) {
				iv_road.setBackgroundDrawable(context.getResources()
						.getDrawable(R.drawable.open));
			} else {
				iv_road.setBackgroundDrawable(context.getResources()
						.getDrawable(R.drawable.off));
			}

			if (bn) {
				iv_near.setBackgroundDrawable(context.getResources()
						.getDrawable(R.drawable.open));
			} else {
				iv_near.setBackgroundDrawable(context.getResources()
						.getDrawable(R.drawable.off));
			}
			iv_broadcast.setOnClickListener(bobaoListener);
			iv_road.setOnClickListener(bobaoListener);
			iv_near.setOnClickListener(bobaoListener);
		} else if (flag == 3) {
			// 弹出路况确认框
			View v = ((Activity) context).getLayoutInflater().inflate(
					R.layout.pop_publish, null);
			window3 = new PopupWindow(v, LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			// 设置整个popupwindow的样式。(注释掉避免输入框失效)
			// window3.setBackgroundDrawable(context.getResources().getDrawable(
			// R.drawable.publish_bg));
			window3.setFocusable(true);
			window3.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
			window3.update();
			window3.showAtLocation(v, Gravity.CENTER_VERTICAL, 0, 0);

			// window一旦消失，必须去掉遮罩层
			window3.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss() {
					// TODO Auto-generated method stub
					inputStr = "";
					hideHelpImg();
				}
			});

			tv_detail = (TextView) v.findViewById(R.id.tv_detail);
			et_location = (EditText) v.findViewById(R.id.et_location);
			iv_icon = (ImageView) v.findViewById(R.id.iv_icon);
			iv_select_pic = (ImageView) v.findViewById(R.id.iv_select_pic);
			btn_cancel = (Button) v.findViewById(R.id.btn_cacel);
			btn_send = (Button) v.findViewById(R.id.btn_send);
			ll_east = (LinearLayout) v.findViewById(R.id.ll_east);
			ll_west = (LinearLayout) v.findViewById(R.id.ll_west);
			ll_south = (LinearLayout) v.findViewById(R.id.ll_south);
			ll_north = (LinearLayout) v.findViewById(R.id.ll_north);
			iv_east = (ImageView) v.findViewById(R.id.iv_east);
			iv_west = (ImageView) v.findViewById(R.id.iv_west);
			iv_south = (ImageView) v.findViewById(R.id.iv_south);
			iv_north = (ImageView) v.findViewById(R.id.iv_north);

			// 填充当前的地点名称
			setDetail(currentLocationName, face, status);

			// 路况类型图标设置
			if (status == 1) {// 顺畅
				iv_icon.setBackgroundDrawable(context.getResources()
						.getDrawable(R.drawable.iv_shunchang));
			} else if (status == 2) {// 缓慢
				iv_icon.setBackgroundDrawable(context.getResources()
						.getDrawable(R.drawable.iv_huanman));
			} else if (status == 3) {// 拥堵
				iv_icon.setBackgroundDrawable(context.getResources()
						.getDrawable(R.drawable.iv_yongdu));
			} else if (status == 4) {// 道路封闭
				iv_icon.setBackgroundDrawable(context.getResources()
						.getDrawable(R.drawable.iv_daolufengbi));
			} else if (status == 5) {// 事故
				iv_icon.setBackgroundDrawable(context.getResources()
						.getDrawable(R.drawable.iv_shigu));
			} else if (status == 6) {// 警察执法
				iv_icon.setBackgroundDrawable(context.getResources()
						.getDrawable(R.drawable.iv_jingchazhifa));
			}

			ll_east.setOnClickListener(publishListener);
			ll_west.setOnClickListener(publishListener);
			ll_south.setOnClickListener(publishListener);
			ll_north.setOnClickListener(publishListener);
			iv_select_pic.setOnClickListener(publishListener);
			btn_cancel.setOnClickListener(publishListener);
			btn_send.setOnClickListener(publishListener);

			// 监听
			et_location.addTextChangedListener(new TextWatcher() {
				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					// TODO Auto-generated method stub
					inputStr = et_location.getText().toString().trim();
					if (inputStr.length() > 13) {
						Toast.makeText(context, "最多输入13个字", Toast.LENGTH_SHORT)
								.show();
					}
					if (!inputStr.equals("")) {
						setDetail(inputStr, face, status);
					} else {
						setDetail(currentLocationName, face, status);
					}
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
					// TODO Auto-generated method stub
				}

				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub
				}
			});
		} else if (flag == 4) {
			// 弹出图片上传框
			View v = ((Activity) context).getLayoutInflater().inflate(
					R.layout.pop_select_pic, null);
			window4 = new PopupWindow(v, LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
			window4.setFocusable(true);
			window4.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
			window4.update();
			window4.showAtLocation(v, Gravity.CENTER_VERTICAL, 0, 1000);

			tv_photo = (TextView) v.findViewById(R.id.tv_photo);
			tv_alum = (TextView) v.findViewById(R.id.tv_alum);
			tv_cancel = (TextView) v.findViewById(R.id.tv_cancel);

			tv_photo.setOnClickListener(selecterListner);
			tv_alum.setOnClickListener(selecterListner);
			tv_cancel.setOnClickListener(selecterListner);
		} else if (flag == 5) {
			// 地图页面弹出详情框(点击小车标志在页面顶部弹出的白色框)
			View v = ((Activity) context).getLayoutInflater().inflate(
					R.layout.pop_baoliao_detail, null);
			window5 = new PopupWindow(v, LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT);
			window5.setBackgroundDrawable(context.getResources().getDrawable(
					R.drawable.detail_bg));
			window5.setFocusable(true);
			window5.setAnimationStyle(R.style.popwinAnimation);
			window5.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
			window5.update();
			window5.showAsDropDown(parent);

			// window一旦消失，必须去掉遮罩层
			window5.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss() {
					// TODO Auto-generated method stub
					hideHelpImg();
				}
			});

			iv_head = (ImageView) v.findViewById(R.id.iv_head5);
			tv_phone = (TextView) v.findViewById(R.id.tv_phone);
			iv_type = (ImageView) v.findViewById(R.id.iv_type);
			tv_detail5 = (TextView) v.findViewById(R.id.tv_detail5);
			tv_title5 = (TextView) v.findViewById(R.id.tv_title5);

			tv_phone.setText(itemEntity.getMobile());
			tv_detail5.setText(itemEntity.getDetail());
			tv_title5.setText(itemEntity.getTitle());
//			Log.e("sb", "itemEntity.getSource()    "   + itemEntity.getSource());
//			Log.e("sb", "itemEntity.getHead_pic()    "   + itemEntity.getHead_pic());
//			itemEntity.setHead_pic("");
			if (itemEntity.getSource().equals("0")) {
				//设置为默认的头像
				iv_head.setBackgroundDrawable(Application.context().getResources().getDrawable(R.drawable.iv_head));
			}else {
				if (itemEntity.getHead_pic() == null || itemEntity.getHead_pic().equals("") ) {
					//设置为应用图标的那个图片
					BitmapDrawable draw = (BitmapDrawable) Application.context().getResources().getDrawable(R.drawable.ic_launcher);
					head_pic_bitmapBitmap = draw.getBitmap();
					iv_head.setImageBitmap(BitmapUtil.changeBitmap(head_pic_bitmapBitmap, 32, 32));
				}else { 
					//下载服务器的图片并显示
					mImageLoader.loadUrl(iv_head, itemEntity.getHead_pic(), 32, 32);// 下载图片
				}
			}
			

			if (itemEntity.getStatus() == 1) {
				iv_type.setBackgroundDrawable(context.getResources()
						.getDrawable(R.drawable.iv_shunchang));
			} else if (itemEntity.getStatus() == 2) {
				iv_type.setBackgroundDrawable(context.getResources()
						.getDrawable(R.drawable.iv_huanman));
			} else if (itemEntity.getStatus() == 3) {
				iv_type.setBackgroundDrawable(context.getResources()
						.getDrawable(R.drawable.iv_daolufengbi));
			} else if (itemEntity.getStatus() == 4) {
				iv_type.setBackgroundDrawable(context.getResources()
						.getDrawable(R.drawable.iv_yongdu));
			} else if (itemEntity.getStatus() == 5) {
				iv_type.setBackgroundDrawable(context.getResources()
						.getDrawable(R.drawable.iv_shigu));
			} else if (itemEntity.getStatus() == 6) {
				iv_type.setBackgroundDrawable(context.getResources()
						.getDrawable(R.drawable.iv_jingchazhifa));
			}

		} else if (flag == 6) {
			// 地图页面弹出地址框
			View v = ((Activity) context).getLayoutInflater().inflate(
					R.layout.pop_location, null);
			window6 = new PopupWindow(v, LayoutParams.WRAP_CONTENT, 50);
			window6.setBackgroundDrawable(context.getResources().getDrawable(
					R.drawable.detail_bg));
			window6.setFocusable(true);
			window6.setAnimationStyle(R.style.popwinAnimation);
			window6.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
			window6.update();
			window6.showAtLocation(v, Gravity.CENTER_VERTICAL, 0, 0);

			// window一旦消失，必须去掉遮罩层
			window6.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss() {
					// TODO Auto-generated method stub
					hideHelpImg();
				}
			});

			tv_location = (TextView) v.findViewById(R.id.tv_location);
			tv_location.setText(locationName);
		} else if (flag == 7) {
			// 弹出语音播报开关框
			View v = ((Activity) context).getLayoutInflater().inflate(
					R.layout.pop_bobao_list, null);
			window7 = new PopupWindow(v, CommonUtils.convertDipToPx(context,
					200), CommonUtils.convertDipToPx(context, 145));
			// 设置整个popupwindow的样式。
			window7.setBackgroundDrawable(context.getResources().getDrawable(
					R.drawable.bobao_right));
			window7.setFocusable(true);
			window7.update();
			int[] location = new int[2];
			parent.getLocationOnScreen(location);
			window7.showAtLocation(parent, Gravity.NO_GRAVITY, location[0]
					- window7.getWidth() - 20, location[1] - 20);

			// window一旦消失，必须去掉遮罩层
			window7.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss() {
					// TODO Auto-generated method stub
					hideHelpImg();
				}
			});

			iv_broadcast_list = (ImageView) v.findViewById(R.id.iv_broadcast);
			iv_road_list = (ImageView) v.findViewById(R.id.iv_road);
			iv_near_list = (ImageView) v.findViewById(R.id.iv_near);

			bb_list = SharedPreferencesUtil.getBoolean(context,
					"k_road_broadcast");
			br_list = SharedPreferencesUtil.getBoolean(context,
					"k_road_collected");
			bn_list = SharedPreferencesUtil.getBoolean(context, "k_road_near");

			if (bb_list) {
				iv_broadcast_list.setBackgroundDrawable(context.getResources()
						.getDrawable(R.drawable.open));
			} else {
				iv_broadcast_list.setBackgroundDrawable(context.getResources()
						.getDrawable(R.drawable.off));
			}

			if (br_list) {
				iv_road_list.setBackgroundDrawable(context.getResources()
						.getDrawable(R.drawable.open));
			} else {
				iv_road_list.setBackgroundDrawable(context.getResources()
						.getDrawable(R.drawable.off));
			}

			if (bn_list) {
				iv_near_list.setBackgroundDrawable(context.getResources()
						.getDrawable(R.drawable.open));
			} else {
				iv_near_list.setBackgroundDrawable(context.getResources()
						.getDrawable(R.drawable.off));
			}
			iv_broadcast_list.setOnClickListener(bobaoListener_list);
			iv_road_list.setOnClickListener(bobaoListener_list);
			iv_near_list.setOnClickListener(bobaoListener_list);

		}

	}

	/**
	 * 爆料里面的小图标点击
	 */
	static OnClickListener baoliaoListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			if (id == R.id.iv_shunchang) {
				if (window1 != null) {
					window1.dismiss();
				}
				status = 1;
				showHelp();
				// popAwindow(v, 3);
			} else if (id == R.id.iv_huanman) {
				if (window1 != null) {
					window1.dismiss();
				}
				status = 2;
				showHelp();
				// popAwindow(v, 3);
			} else if (id == R.id.iv_yongdu) {
				if (window1 != null) {
					window1.dismiss();
				}
				status = 3;
				showHelp();
				// popAwindow(v, 3);
			} else if (id == R.id.iv_shigu) {
				if (window1 != null) {
					window1.dismiss();
				}
				status = 5;
				showHelp();
				// popAwindow(v, 3);
			} else if (id == R.id.iv_daolufengbi) {
				if (window1 != null) {
					window1.dismiss();
				}
				status = 4;
				showHelp();
				// popAwindow(v, 3);
			} else if (id == R.id.iv_jingchazhifa) {
				if (window1 != null) {
					window1.dismiss();
				}
				status = 6;
				showHelp();
				// popAwindow(v, 3);
			}
			ArrayList<String> alumList = new ArrayList<String>();
			String detail = "";
			if (inputStr.equals("")) {
				// detail = currentLocationName + face;
				detail = face;
			} else {
				detail = inputStr + face;
			}
			Intent intent = new Intent();
			intent.setClass(mActivity, TrafficPublishActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("alumList", (Serializable) alumList);
			intent.putExtras(bundle);
			intent.putExtra("status", status);
			intent.putExtra("currentLocationName", currentLocationName);
			intent.putExtra("detail", detail);
			intent.putExtra("face", face);
			mActivity.startActivity(intent);
		}
	};

	/**
	 * 地图界面播报里面的小图标点击
	 */
	static OnClickListener bobaoListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			if (id == R.id.iv_broadcast) {
				if (bb) {
					bb = false;
					br = false;
					bn = false;
					// //停止播报
					// mHandler.removeCallbacks(mSpeechWinRun);
					// TtsSpeechApi.getIntance().stopSpeaking();
					SharedPreferencesUtil.setBoolean(context,
							"k_road_broadcast", bb);
					SharedPreferencesUtil.setBoolean(context,
							"k_road_collected", br);
					SharedPreferencesUtil
							.setBoolean(context, "k_road_near", bn);
					handler.obtainMessage(2).sendToTarget();
				} else {
					bb = true;
					br = true;
					bn = true;
					SharedPreferencesUtil.setBoolean(context,
							"k_road_broadcast", bb);
					SharedPreferencesUtil.setBoolean(context,
							"k_road_collected", br);
					SharedPreferencesUtil
							.setBoolean(context, "k_road_near", bn);
					// startToBroadcast(0);//定制道路 + 附近道路
					// currentPositio = 0;
					// mHandler.postDelayed(mSpeechWinRun, 1000);
					handler.obtainMessage(3).sendToTarget();
				}
				changeState(bb, iv_broadcast);
				changeState(br, iv_road);
				changeState(bn, iv_near);
			} else if (id == R.id.iv_road) {
				// //停止播报
				// mHandler.removeCallbacks(mSpeechWinRun);
				// TtsSpeechApi.getIntance().stopSpeaking();
				handler.obtainMessage(2).sendToTarget();
				if (br) {
					br = false;
					if (false == bn) {
						bb = false;
					}
				} else {
					br = true;
					bb = true;
				}
				SharedPreferencesUtil.setBoolean(context, "k_road_broadcast",
						bb);
				SharedPreferencesUtil.setBoolean(context, "k_road_collected",
						br);
				SharedPreferencesUtil.setBoolean(context, "k_road_near", bn);
				changeState(bb, iv_broadcast);
				changeState(br, iv_road);
				changeState(bn, iv_near);
				// broadcast(bb, br, bn);
				handler.obtainMessage(3).sendToTarget();
			} else if (id == R.id.iv_near) {
				// //停止播报
				// mHandler.removeCallbacks(mSpeechWinRun);
				// TtsSpeechApi.getIntance().stopSpeaking();
				handler.obtainMessage(2).sendToTarget();
				if (bn) {
					bn = false;
					if (false == br) {
						bb = false;
					}
				} else {
					bn = true;
					bb = true;
				}
				SharedPreferencesUtil.setBoolean(context, "k_road_broadcast",
						bb);
				SharedPreferencesUtil.setBoolean(context, "k_road_collected",
						br);
				SharedPreferencesUtil.setBoolean(context, "k_road_near", bn);
				changeState(bb, iv_broadcast);
				changeState(br, iv_road);
				changeState(bn, iv_near);
				// broadcast(bb, br, bn);
				handler.obtainMessage(3).sendToTarget();
			}
		}

	};

	/**
	 * 文字列表界面播报里面的小图标点击
	 */
	static OnClickListener bobaoListener_list = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			if (id == R.id.iv_broadcast) {
				if (bb_list) {
					bb_list = false;
					br_list = false;
					bn_list = false;
					SharedPreferencesUtil.setBoolean(context,
							"k_road_broadcast", bb_list);
					SharedPreferencesUtil.setBoolean(context,
							"k_road_collected", br_list);
					SharedPreferencesUtil.setBoolean(context, "k_road_near",
							bn_list);
					// 停止播报
					handler.obtainMessage(7).sendToTarget();
					handler.obtainMessage(9).sendToTarget();
				} else {
					bb_list = true;
					br_list = true;
					bn_list = true;
					SharedPreferencesUtil.setBoolean(context,
							"k_road_broadcast", bb_list);
					SharedPreferencesUtil.setBoolean(context,
							"k_road_collected", br_list);
					SharedPreferencesUtil.setBoolean(context, "k_road_near",
							bn_list);
					// 进行语音播报
					handler.obtainMessage(8).sendToTarget();
				}
				changeState(bb_list, iv_broadcast_list);
				changeState(br_list, iv_road_list);
				changeState(bn_list, iv_near_list);
			} else if (id == R.id.iv_road) {
				// 停止播报
				handler.obtainMessage(7).sendToTarget();
				if (br_list) {
					br_list = false;
					if (false == bn_list) {
						bb_list = false;
					}
				} else {
					br_list = true;
					bb_list = true;
				}
				SharedPreferencesUtil.setBoolean(context, "k_road_broadcast",
						bb_list);
				SharedPreferencesUtil.setBoolean(context, "k_road_collected",
						br_list);
				SharedPreferencesUtil.setBoolean(context, "k_road_near",
						bn_list);
				changeState(bb_list, iv_broadcast_list);
				changeState(br_list, iv_road_list);
				changeState(bn_list, iv_near_list);
				// 进行语音播报
				handler.obtainMessage(8).sendToTarget();
			} else if (id == R.id.iv_near) {
				// 停止播报
				handler.obtainMessage(7).sendToTarget();
				if (bn_list) {
					bn_list = false;
					if (false == br_list) {
						bb_list = false;
					}
				} else {
					bn_list = true;
					bb_list = true;
				}
				SharedPreferencesUtil.setBoolean(context, "k_road_broadcast",
						bb_list);
				SharedPreferencesUtil.setBoolean(context, "k_road_collected",
						br_list);
				SharedPreferencesUtil.setBoolean(context, "k_road_near",
						bn_list);
				changeState(bb_list, iv_broadcast_list);
				changeState(br_list, iv_road_list);
				changeState(bn_list, iv_near_list);
				// 进行语音播报
				handler.obtainMessage(8).sendToTarget();
			}
		}

	};

	/**
	 * 发布详情里面的小图标点击
	 */
	static OnClickListener publishListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			if (id == R.id.btn_cacel) {
				window3.dismiss();
				inputStr = "";
			} else if (id == R.id.btn_send) {
				btn_send.setEnabled(false);
				MobclickAgent.onEvent(context,
						"E_C_trafficBroadcast_trafficBrokeClick");
				baoliaoRequest();
			} else if (id == R.id.iv_select_pic) {
				popAwindow(v, 4);
			} else if (id == R.id.ll_east) {
				face = "向东";
				setFace(0, 8, 8, 8);
				if (inputStr.equals("")) {
					setDetail(currentLocationName, face, status);
				} else {
					setDetail(inputStr, face, status);
				}
			} else if (id == R.id.ll_west) {
				face = "向西";
				setFace(8, 0, 8, 8);
				if (inputStr.equals("")) {
					setDetail(currentLocationName, face, status);
				} else {
					setDetail(inputStr, face, status);
				}
			} else if (id == R.id.ll_south) {
				face = "向南";
				setFace(8, 8, 0, 8);
				if (inputStr.equals("")) {
					setDetail(currentLocationName, face, status);
				} else {
					setDetail(inputStr, face, status);
				}
			} else if (id == R.id.ll_north) {
				face = "向北";
				setFace(8, 8, 8, 0);
				if (inputStr.equals("")) {
					setDetail(currentLocationName, face, status);
				} else {
					setDetail(inputStr, face, status);
				}
			} else {
			}
		}

	};

	/**
	 * 选择图片列表项点击
	 */
	static OnClickListener selecterListner = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			if (id == R.id.tv_photo) {
				// Intent intent1 = new Intent(context, MyCameraActivity.class);
				// intent1.putExtra("status", status);
				// intent1.putExtra("currentLocationName", currentLocationName);
				// intent1.putExtra("detail", tv_detail.getText().toString());
				// intent1.putExtra("face", face);
				// context.startActivity(intent1);
				SystemCallUtil.camera(mActivity, Config.SDCARD_CITY_TMP);
			} else if (id == R.id.tv_alum) {
				Intent intent2 = new Intent(context, MyPhotoListActivity.class);
				intent2.putExtra("status", status);
				intent2.putExtra("currentLocationName", currentLocationName);
				intent2.putExtra("detail", tv_detail.getText().toString());
				intent2.putExtra("face", face);
				context.startActivity(intent2);
				// SystemCallUtil.photoAlbum(mActivity);
			} else if (id == R.id.tv_cancel) {
				window4.dismiss();
			}
			// window3.dismiss();
			window4.dismiss();
		}

	};

	/**
	 * 改变开关状态
	 * */
	@SuppressLint("NewApi")
	private static void changeState(Boolean bool, View v) {
		if (bool) {
			v.setBackgroundDrawable(context.getResources().getDrawable(
					R.drawable.open));
		} else {
			v.setBackgroundDrawable(context.getResources().getDrawable(
					R.drawable.off));
		}
	}

	/**
	 * 显示引导
	 */
	private static void showHelp() {
		helpImg.setVisibility(View.VISIBLE);
		helpImg.setBackgroundResource(R.drawable.cover_bg);
	}

	/**
	 * 隐藏引导图
	 */
	private static void hideHelpImg() {
		helpImg.setVisibility(View.GONE);
		helpImg.setBackgroundDrawable(null);
	}

	/**
	 * 设置播报详情
	 * */
	private static void setDetail(String currentLocationName, String face,
			int status) {
		et_location.setHint(currentLocationName);
		if (status == 1) {
			tv_detail.setText(currentLocationName + "  " + face + "  " + "顺畅");
		} else if (status == 2) {
			tv_detail.setText(currentLocationName + "  " + face + "  " + "缓慢");
		} else if (status == 3) {
			tv_detail.setText(currentLocationName + "  " + face + "  " + "拥堵");
		} else if (status == 4) {
			tv_detail
					.setText(currentLocationName + "  " + face + "  " + "道路封闭");
		} else if (status == 5) {
			tv_detail.setText(currentLocationName + "  " + face + "  " + "事故");
		} else if (status == 6) {
			tv_detail
					.setText(currentLocationName + "  " + face + "  " + "警察执法");
		}
	}

	/**
	 * 设置朝向
	 */
	private static void setFace(int east, int west, int south, int north) {
		iv_east.setVisibility(east);
		iv_west.setVisibility(west);
		iv_south.setVisibility(south);
		iv_north.setVisibility(north);
	}

	/**
	 * 发起爆料请求
	 * */
	private static void baoliaoRequest() {

		baoliaoBo = new Traffic_Baoliao_Bo(context);
		Map<String, String> params = new HashMap<String, String>(1);

		Account account = AccountMgr.getInstance().getAccount(context);
		String user_id = String.valueOf(account.getData().getUserId());
		boolean isLogin = Boolean.parseBoolean(SharedPreferencesUtil.getValue(
				context, Key.K_IS_LOGIN));
		if (!isLogin) {
			Intent intent = new Intent();
			intent.setClassName(context,
					"cn.ffcs.changchuntv.activity.login.LoginActivity");
			context.startActivity(intent);
			btn_send.setEnabled(true);
		} else {
			String mobile = account.getData().getMobile();
			String lat = LocationUtil.getLatitude(context);
			String lng = LocationUtil.getLongitude(context);
			System.out.println("lat===>>" + lat + ",  lng===>>" + lng);
			String sign = user_id;
			String title = "";
			String detail = "";

			// String cityCode = MenuMgr.getInstance().getCityCode(mContext);

			if (lat == null || lat.equals("")) {
				lat = "unknown";
			}
			if (lng == null || lng.equals("")) {
				lng = "unknown";
			}
			if (mobile == null || mobile.equals("")) {
				mobile = "unknown";
			}
			if (inputStr.equals("")) {
				title = currentLocationName;
				// detail = currentLocationName + face;
				detail = face;
			} else {
				title = inputStr;
				detail = inputStr + face;
			}

			System.out.println("爆料的用户id---->>" + user_id);
			params.put("city_code", "2201");
			params.put("org_code", "2201");
			params.put("mobile", mobile);
			params.put("longitude", lng);
			params.put("latitude", lat);
			params.put("sign", sign);

			params.put("user_id", user_id);
			params.put("title", title);
			params.put("detail", detail);
			params.put("status", String.valueOf(status));
			params.put("pic_uri", "[]");

			baoliaoBo
					.startRequestTask(
							new baoliaoCallBack(),
							context,
							params,
							"http://ccgd.153.cn:50081/icity-api-client-other/icity/service/lbs/road/addRoadExpose");
		}

	}

	/**
	 * 爆料回调
	 * */
	static class baoliaoCallBack implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp response) {
			// TODO Auto-generated method stub
			System.out.println("爆料回调====>>" + response.getHttpResult());
			baoliaoEntity = (Traffic_Baoliao_Entity) response.getObj();

			if (baoliaoEntity != null) {
				if ("0".equals(baoliaoEntity.getResult_code())) {
					window3.dismiss();
					btn_send.setEnabled(true);
					CommonUtils.showToast((Activity) context, "路况爆料成功",
							Toast.LENGTH_SHORT);
				} else {
					btn_send.setEnabled(true);
					CommonUtils.showToast((Activity) context, "路况爆料失败"
							+ baoliaoEntity.getResult_desc(),
							Toast.LENGTH_SHORT);
				}
			} else {
				btn_send.setEnabled(true);
				CommonUtils.showToast((Activity) context, "路况爆料失败",
						Toast.LENGTH_SHORT);
			}

			face = "向东";
			inputStr = "";

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

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SystemCallUtil.REQUESTCODE_CAMERA
				&& resultCode == Activity.RESULT_OK) {
			window3.dismiss();
			ArrayList<String> alumList = new ArrayList<String>();
			alumList.add(SystemCallUtil.FILE_FULL_PATH);
			String detail = "";
			if (inputStr.equals("")) {
				// detail = currentLocationName + face;
				detail = face;
			} else {
				detail = inputStr + face;
			}
			Intent intent = new Intent();
			intent.setClass(mActivity, TrafficPublishActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("alumList", (Serializable) alumList);
			intent.putExtras(bundle);
			intent.putExtra("status", status);
			intent.putExtra("currentLocationName", currentLocationName);
			intent.putExtra("detail", detail);
			intent.putExtra("face", face);
			mActivity.startActivity(intent);
		} else if (requestCode == SystemCallUtil.REQUESTCODE_PHOTOALBUM
				&& resultCode == Activity.RESULT_OK) {
			window3.dismiss();
			Uri uri = data.getData();
			ArrayList<String> alumList = new ArrayList<String>();
			alumList.add(uri.getPath());
			String detail = "";
			if (inputStr.equals("")) {
				// detail = currentLocationName + face;
				detail = face;
			} else {
				detail = inputStr + face;
			}
			Intent intent = new Intent();
			intent.setClass(mActivity, TrafficPublishActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("alumList", (Serializable) alumList);
			intent.putExtras(bundle);
			intent.putExtra("status", status);
			intent.putExtra("currentLocationName", currentLocationName);
			intent.putExtra("detail", detail);
			intent.putExtra("face", face);
			mActivity.startActivity(intent);
		}
	}

}
