package cn.ffcs.external.trafficbroadcast.tool;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import cn.ffcs.external.trafficbroadcast.entity.Traffic_SimpleItem_Entity;
import cn.ffcs.external.trafficbroadcast.view.PopWindow;

import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.external_trafficbroadcast.R;
import com.ffcs.surfingscene.entity.GlobalEyeEntity;

/**
 * <p>
 * Title: 自定义地图上的标记
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * 
 * @author: liaodl
 *          </p>
 *          <p>
 *          Copyright: Copyright (c) 2013
 *          </p>
 *          <p>
 *          Company: ffcs Co., Ltd.
 *          </p>
 *          <p>
 *          Create Time: 2013-12-7
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
public class MyOverlayItem extends ItemizedOverlay<OverlayItem> {

	private List<Traffic_SimpleItem_Entity> simpleList = new ArrayList<Traffic_SimpleItem_Entity>();
	/**
	 * 用来过滤掉经纬度为0的情况，导致百度地图onTab index错乱问题
	 */
	private List<Traffic_SimpleItem_Entity> tempList = new ArrayList<Traffic_SimpleItem_Entity>();
	private List<OverlayItem> mOverlayItemList = new ArrayList<OverlayItem>();
	private List<OverlayItem> mOverlayItemList1 = new ArrayList<OverlayItem>();

	private Context context;
	private RelativeLayout rl_title;
	private static ImageView helpImg;

	private Drawable marker;
	private GeoPoint geo;
	private String title;
	OverlayItem item;

	private MapView mapView;

	public MyOverlayItem(Drawable marker, MapView mapView, Context context,
			PopWindow window, RelativeLayout rl_title, ImageView helpImg,
			List<Traffic_SimpleItem_Entity> simpleList) {
		super(marker, mapView);
		this.context = context;
		this.mapView = mapView;
		window = window;
		this.rl_title = rl_title;
		this.helpImg = helpImg;
		this.simpleList = simpleList;
	}

	public MyOverlayItem(Drawable marker, MapView mapView, Context context,
			GeoPoint geo, String title) {
		super(marker, mapView);
		this.context = context;
		this.mapView = mapView;
		this.marker = marker;
		this.geo = geo;
		this.title = title;
	}

	public void setCameras() {
		if (simpleList == null || simpleList.size() < 0) {
			return;
		}
		tempList.clear();
		for (Traffic_SimpleItem_Entity entity : simpleList) {
			int latitude = (int) (Double.valueOf(entity.getLat()) * 1E6);
			int longitude = (int) (Double.valueOf(entity.getLng()) * 1E6);
			GeoPoint geyePoint = new GeoPoint(latitude, longitude);
			OverlayItem item = new OverlayItem(geyePoint,
					entity.getStatus_disc(), "");
			if (entity.getStatus() == 1) {// 顺畅
				item.setMarker(context.getResources().getDrawable(
						R.drawable.iv_shunchang_small));
			} else if (entity.getStatus() == 2) {// 缓慢
				item.setMarker(context.getResources().getDrawable(
						R.drawable.iv_huanman_small));
			} else if (entity.getStatus() == 3) {// 道路封闭
				item.setMarker(context.getResources().getDrawable(
						R.drawable.iv_daolufengbi_small));
			} else if (entity.getStatus() == 4) {// 拥堵
				item.setMarker(context.getResources().getDrawable(
						R.drawable.iv_yongdu_small));
			} else if (entity.getStatus() == 5) {// 事故
				item.setMarker(context.getResources().getDrawable(
						R.drawable.iv_shigu_small));
			} else if (entity.getStatus() == 6) {// 警察执法
				item.setMarker(context.getResources().getDrawable(
						R.drawable.iv_jingchazhifa_small));
			}
			mOverlayItemList.add(item);
			tempList.add(entity);
		}
		// this.simpleList = tempList;
		addItem(mOverlayItemList);
		System.out.println("simpleList=====>>" + simpleList.size());
		mapView.refresh();
	}

	/**
	 * 点击地点打图标
	 * */
	public void showLocation() {
		// TODO Auto-generated method stub
		System.out.println("title--->>" + title);
		System.out.println("lat--->>" + geo.getLatitudeE6());
		System.out.println("lng--->>" + geo.getLongitudeE6());
		System.out.println("marker--->>" + marker);
		item = new OverlayItem(geo, title, "");
		item.setMarker(marker);
		mOverlayItemList1.add(item);
		addItem(mOverlayItemList1);
		mapView.refresh();
	}

	private ItemClickListener itemClickListener;

	public void setItemClickListener(ItemClickListener listener) {
		this.itemClickListener = listener;
	}

	public interface ItemClickListener {
		void removeItemOnClickMap();
	}

	@Override
	public boolean onTap(GeoPoint geoPoint, MapView mapView) {
		if (clickPopListener != null) {
			clickPopListener.removePopOnClickMap();
		}
		
		if(itemClickListener != null){
			itemClickListener.removeItemOnClickMap();
		}
		// 添加遮罩层
		// showHelp();
		// 弹出详情框
		// window.popAwindow(rl_title, 5);
		return super.onTap(geoPoint, mapView);
	}

	@Override
	protected boolean onTap(int index) {
		if (clickPopListener != null && simpleList.size() > 0) {
			Traffic_SimpleItem_Entity entity = simpleList.get(index);
			clickPopListener.showDetailPop(entity);
		}
		if(itemClickListener != null){
			itemClickListener.removeItemOnClickMap();
		}
		return super.onTap(index);
	}

	private ClickPopListener clickPopListener;

	public void setClickPopListener(ClickPopListener clickPopListener) {
		this.clickPopListener = clickPopListener;
	}

	public interface ClickPopListener {
		void showDetailPop(Traffic_SimpleItem_Entity entity);

		void removePopOnClickMap();
	}

	/**
	 * 显示引导
	 */
	private static void showHelp() {
		helpImg.setVisibility(View.VISIBLE);
		helpImg.setBackgroundResource(R.drawable.cover_bg);
	}

}
