//package cn.ffcs.surfingscene.activity;
//
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.List;
//import android.content.Intent;
//import android.graphics.drawable.BitmapDrawable;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.ImageView;
//import android.widget.ListView;
//import android.widget.PopupWindow;
//import android.widget.TextView;
//import android.widget.Toast;
//import cn.ffcs.config.ExternalKey;
//import cn.ffcs.surfingscene.R;
//import cn.ffcs.surfingscene.adapter.RoadAdapter;
//import cn.ffcs.surfingscene.common.Key;
//import cn.ffcs.surfingscene.datamgr.AreaMgr;
//import cn.ffcs.surfingscene.sqlite.AreaList;
//import cn.ffcs.ui.tools.TopUtil;
//import cn.ffcs.widget.LoadingBar;
//import cn.ffcs.widget.LoadingDialog;
//import cn.ffcs.wisdom.tools.CommonUtils;
//import cn.ffcs.wisdom.tools.Log;
//
//import com.ffcs.surfingscene.entity.GlobalEyeEntity;
//import com.ffcs.surfingscene.function.CameraList;
//import com.ffcs.surfingscene.function.CityList;
//import com.ffcs.surfingscene.http.HttpCallBack;
//import com.ffcs.surfingscene.response.BaseResponse;
//
///**
// * 
// * 项目名称：SurfingScene 类名称：RoadHomeActivity.java 类描述： 路况视频首页 创建人：yangchaoxun
// * 创建时间：2013-6-14 下午3:54:37 修改人：yangchaoxun 修改时间： 修改备注：
// */
//
//public class RoadHomeActivity extends GlobaleyeBaseActivity implements OnClickListener {
//	private ImageView top_right;// 右键
//	private TextView pop_search, pop_map, choose_province, choose_city, choose_county;
//	private ListView road_home_lv;
//	private RoadAdapter adapter;
//	private PopupWindow popupWindow = null;
//	public static int PROV = 1;
//	public static int CITY = 2;
//	public static int COUN = 3;
//	private String gloType;// 全球眼类型
//	private LoadingBar bar;
//	private String areaCode, areaName;
//	private String cityAreaCode, cityAreaName;
//	private String countyAreaCode, countyAreaName;
//	private String title;
//	private List<GlobalEyeEntity> eyelist = new ArrayList<GlobalEyeEntity>();
//
//	private void initPopWindow() {
//		if (popupWindow != null) {
//			popupWindow.dismiss();
//		}
//		View contentView = LayoutInflater.from(getApplicationContext()).inflate(
//				R.layout.glo_popwin, null); // 加载popupWindow的布局文件   
////		WindowManager wm = this.getWindowManager();
////	     int width = wm.getDefaultDisplay().getWidth();
////	     int height = wm.getDefaultDisplay().getHeight();
////	     System.out.println("Window"+width);
////	     System.out.println("Window"+height);
////	     DisplayMetrics metric = new DisplayMetrics();
////	        getWindowManager().getDefaultDisplay().getMetrics(metric);
////	     float density = metric.density;  // 屏幕密度（0.75 / 1.0 / 1.5）
////	     int densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）
////	     int w=metric.widthPixels;
////	     int h=metric.heightPixels;
////	     System.out.println(density+">"+densityDpi+">"+w+">"+h);
//	     popupWindow = new PopupWindow(contentView, CommonUtils.convertDipToPx(mContext, 150),
//					CommonUtils.convertDipToPx(mContext, 130)); // 声明一个弹出框   
//		// 为弹出框设定自定义的布局  
//		//popupWindow.setContentView(contentView);   
//		popupWindow.setFocusable(true);
//		popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
//		popupWindow.setBackgroundDrawable(new BitmapDrawable());
//		popupWindow.setOutsideTouchable(true);
//		popupWindow.showAsDropDown(top_right);
//		pop_search = (TextView) contentView.findViewById(R.id.pop_search_btn);
//		pop_map = (TextView) contentView.findViewById(R.id.pop_map_btn);
//		pop_map.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent();
//				intent.setClass(RoadHomeActivity.this, RoadMapActivity.class);
//				intent.putExtra("list", (Serializable) eyelist);
//				if (title != null) {
//					intent.putExtra(Key.K_RETURN_TITLE, title);
//				} else {
//					intent.putExtra(Key.K_RETURN_TITLE, getString(R.string.glo_road_title));
//				}
//
//				RoadHomeActivity.this.startActivity(intent);
//				popupWindow.dismiss();
//			}
//		});
//		pop_search.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent();
//				intent.setClass(RoadHomeActivity.this, SearchResultActivity.class);
//				intent.putExtra(Key.K_AREA_CODE, cityAreaCode);
//				intent.putExtra(Key.K_RETURN_TITLE,title);
//				RoadHomeActivity.this.startActivity(intent);
//				popupWindow.dismiss();
//			}
//		});
//	}
//
//	@Override
//	public void onClick(View v) {
//		Intent intent;
//		if (v.getId() == R.id.road_tv_province) {
//			intent = new Intent(mActivity, ChooseCityActivity.class);
//			Bundle bunlde = new Bundle();
//			bunlde.putString(Key.K_CITY_TYPE, getString(R.string.glo_choose_province));
//			bunlde.putString(Key.K_PARENT_CODE, "0");
//			intent.putExtras(bunlde);
//			startActivityForResult(intent, PROV);
//		} else if (v.getId() == R.id.road_tv_city) {
//			intent = new Intent(mActivity, ChooseCityActivity.class);
//			Bundle bunlde = new Bundle();
//			bunlde.putString(Key.K_CITY_TYPE, getString(R.string.glo_choose_city));
//			bunlde.putString(Key.K_PARENT_CODE, areaCode);
//			intent.putExtras(bunlde);
//			startActivityForResult(intent, CITY);
//		} else if (v.getId() == R.id.road_tv_county) {
//			intent = new Intent(mActivity, ChooseCityActivity.class);
//			Bundle bunlde = new Bundle();
//			bunlde.putString(Key.K_CITY_TYPE, getString(R.string.glo_choose_county));
//			bunlde.putString(Key.K_PARENT_CODE, cityAreaCode);
//			intent.putExtras(bunlde);
//			startActivityForResult(intent, COUN);
//		}
//	}
//
//	class GetCityCallBack implements HttpCallBack<BaseResponse> {
//
//		@Override
//		public void callBack(BaseResponse response, String url) {
//			try {
//				if (response.getReturnCode().equals("1")) {
//					AreaMgr.getInstance().saveAreaList(mContext, response.getArea());
//					AreaMgr.getInstance().refresh(mContext, areaCode);
//					AreaList entity = AreaMgr.getInstance().getProByCity(mContext, cityAreaCode);
//					changePro(entity, false);
//					changeCity(AreaMgr.getInstance().getAreaList(mContext, cityAreaCode));
//					geteye(null);
//				} else {
//					CommonUtils.showToast(mActivity, getString(R.string.glo_loading_failed),
//							Toast.LENGTH_SHORT);
//				}
//				LoadingDialog.getDialog(mActivity).dismiss();
//			} catch (Exception e) {
//				Log.e(e.getMessage(), e);
//			}
//		}
//	}
//
//	class GeteyeCallBack implements HttpCallBack<BaseResponse> {
//
//		@Override
//		public void callBack(BaseResponse response, String url) {
//			try {
//				if (response.getReturnCode().equals("1")) {
//					bar.setVisibility(View.GONE);
//					road_home_lv.setVisibility(View.VISIBLE);
//					eyelist = response.getGeyes();
//					RoadHomeActivity.this.adapter = new RoadAdapter(RoadHomeActivity.this, eyelist);
//					road_home_lv.setAdapter(adapter);
//					
//				} else {
//					CommonUtils.showToast(mActivity, getString(R.string.glo_loading_failed),
//							Toast.LENGTH_SHORT);
//				}
//
//			} catch (Exception e) {
//				Log.e(e.getMessage(), e);
//			}
//		}
//	}
//
//	/**
//	 * @param countyEntity
//	 */
//	private void changeCounty(AreaList countyEntity) {
//		if (countyEntity != null) {
//			countyAreaCode = countyEntity.areaCode;
//			countyAreaName = countyEntity.areaName;
//			choose_county.setText(countyAreaName);
//		}
//	}
//
//	/**
//	 * @param cityEntity
//	 */
//	private void changeCity(AreaList cityEntity) {
//		if (cityEntity != null) {
//			cityAreaCode = cityEntity.areaCode;
//			cityAreaName = cityEntity.areaName;
//			choose_city.setText(cityAreaName);
//			choose_county.setText(R.string.glo_road_all);
////			List<AreaList> thridList = AreaMgr.getInstance().getThridAreaList(mContext,
////					cityAreaCode);
//
////			if (thridList.size() > 0) {
////				AreaList countyEntity = thridList.get(0);
////				changeCounty(countyEntity);
////			}
//		}
//	}
//
//	/**
//	 * @param proEntity
//	 */
//	private void changePro(AreaList proEntity, boolean isGetSecond) {
//		if (proEntity != null) {
//			areaCode = proEntity.areaCode;
//			areaName = proEntity.areaName;
//			choose_province.setText(areaName);
//			if (isGetSecond) {
//				List<AreaList> secondList = AreaMgr.getInstance().getSecondAreaList(mContext,
//						areaCode);
//				if (secondList.size() > 0) {
//					AreaList cityEntity = secondList.get(0);
//					changeCity(cityEntity);
//				}
//			}
//		}
//	}
//
//	public void city() {
//		if (gloType == null) {
//			gloType = "1000";
//		}
//		LoadingDialog.getDialog(mActivity).setMessage(getString(R.string.glo_loading)).show();
//		CityList citylist = new CityList(this);
//		citylist.getallcityinfo(gloType, new GetCityCallBack(), "/area/listOfAllarea");
//	}
//
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if (resultCode == RESULT_OK && data != null) {
//			AreaList entitiy = (AreaList) data.getSerializableExtra(Key.K_RETURN_ENTITY);
//			if (requestCode == PROV) {
//				changePro(entitiy, true);
//				geteye(null);
//			} else if (requestCode == CITY) {
//				changeCity(entitiy);
//				geteye(null);
//			} else if (requestCode == COUN) {
//				changeCounty(entitiy);
//				geteye(countyAreaCode);
//			}
//		}
//	}
//
//	@Override
//	protected void initComponents() {
//		choose_province = (TextView) this.findViewById(R.id.road_tv_province);
//		choose_city = (TextView) this.findViewById(R.id.road_tv_city);
//		choose_county = (TextView) this.findViewById(R.id.road_tv_county);
//		bar = (LoadingBar) this.findViewById(R.id.loading_bar);
//		choose_province.setOnClickListener(this);
//		choose_city.setOnClickListener(this);
//		choose_county.setOnClickListener(this);
//		choose_county.setText(R.string.glo_road_all);
//		title = getIntent().getStringExtra(Key.K_TITLE_NAME);
//		if (title != null) {
//			TopUtil.updateTitle(this, R.id.top_title, title); // 设置标题
//		} else {
//			TopUtil.updateTitle(this, R.id.top_title, R.string.glo_road_title); // 设置标题
//		}
//		TopUtil.updateRight(this, R.id.top_right, R.drawable.top_right);
//		top_right = (ImageView) this.findViewById(R.id.top_right);
//		road_home_lv = (ListView) this.findViewById(R.id.road_home_lv);
//		top_right.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				initPopWindow();
//			}
//		});
//	}
//
//	@Override
//	protected void initData() {
//		cityAreaCode = getIntent().getStringExtra(Key.K_AREA_CODE);
//		cityAreaName = getIntent().getStringExtra(Key.K_AREA_NAME);
//		gloType = getIntent().getStringExtra(Key.K_GLO_TYPE);
//		if (cityAreaCode == null) {
//			cityAreaCode = "350100";
//		}
//		if (cityAreaName == null) {
//			cityAreaName = "福州市";
//		}
//
//		city();
//		road_home_lv.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				eyelist.get(position).getGeyeId();
//				Intent intent = new Intent(RoadHomeActivity.this, RoadPlayActivity.class);
//				Bundle bundle = new Bundle();
//				bundle.putInt(ExternalKey.K_EYE_ID, eyelist.get(position).getGeyeId());
//				bundle.putString(Key.K_EYE_NAME, eyelist.get(position).getPuName());
//				bundle.putString(Key.K_EYE_INTRO, eyelist.get(position).getIntro());
//				if (title != null) {
//					bundle.putString(ExternalKey.K_EYE_TITLE, title);
//				} else {
//					bundle.putString(Key.K_RETURN_TITLE, getString(R.string.glo_road_title));
//				}
//				intent.putExtras(bundle);
//				RoadHomeActivity.this.startActivity(intent);
//			}
//		});
//	}
//
//	@Override
//	protected int getMainContentViewId() {
//		return R.layout.glo_act_road_home;
//	}
//
//	@Override
//	protected Class<?> getResouceClass() {
//		return R.class;
//	}
//
//	protected void geteye(String countyAreaCode) {
//		if (gloType == null) {
//			gloType = "1000";
//		}
//		bar.setVisibility(View.VISIBLE);
//		CameraList camera = new CameraList(this);
//		camera.searchCamerofArea(cityAreaCode, gloType, countyAreaCode, new GeteyeCallBack(),
//				"/geye/list");
//
//	}
//
//}
