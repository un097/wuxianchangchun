package cn.ffcs.external.trafficbroadcast.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;
import cn.ffcs.wisdom.tools.CommonUtils;

import com.baidu.mapapi.map.MapView;
import com.example.external_trafficbroadcast.R;

/**
 * <p>Title:     自定义百度地图放大缩小控件                      </p>
 * <p>Description:                     </p>
 * <p>@author: liaodl                  </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-12-6           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class ZoomControlView extends RelativeLayout implements OnClickListener {

	private Button mButtonZoomin;
	private Button mButtonZoomout;
	private MapView mapView;
	private int maxZoomLevel;
	private int minZoomLevel;

	public ZoomControlView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ZoomControlView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		View view = LayoutInflater.from(
				getContext()).inflate(R.layout.widget_zoom_controls_layout,
				null);
		mButtonZoomin = (Button) view.findViewById(R.id.zoomin);
		mButtonZoomout = (Button) view.findViewById(R.id.zoomout);
		mButtonZoomin.setOnClickListener(this);
		mButtonZoomout.setOnClickListener(this);
		addView(view);
	}

	@Override
	public void onClick(View v) {
		if (mapView == null) {
			throw new NullPointerException("you can call setMapView(MapView mapView) at first");
		}
		int id = v.getId();
		if (id == R.id.zoomin) {//放大
			mapView.getController().zoomIn();
			if (mapView.getZoomLevel() == maxZoomLevel) {
//				mButtonZoomin.setEnabled(false);
				CommonUtils.showToast((Activity) getContext(), "不能再放大了", Toast.LENGTH_SHORT);
			}
		} else if (id == R.id.zoomout) {
			mapView.getController().zoomOut();
			if (mapView.getZoomLevel() == minZoomLevel) {
//				mButtonZoomout.setEnabled(false);
				CommonUtils.showToast((Activity) getContext(), "不能再缩小了", Toast.LENGTH_SHORT);
			}
		}
	}

	/**
	 * 与MapView设置关联
	 * @param mapView
	 */
	public void setMapView(MapView mapView) {
		this.mapView = mapView;
		// 获取最大的缩放级别
		maxZoomLevel = mapView.getMaxZoomLevel();
		// 获取最大的缩放级别
		minZoomLevel = mapView.getMinZoomLevel();
	}

	/**
	 * 根据MapView的缩放级别更新缩放按钮的状态，当达到最大缩放级别，设置mButtonZoomin
	 * 为不能点击，反之设置mButtonZoomout
	 * @param level
	 */
	public void refreshZoomButtonStatus(int level) {
		if (mapView == null) {
			throw new NullPointerException("you can call setMapView(MapView mapView) at first");
		}
		if (level > minZoomLevel && level < maxZoomLevel) {
			if (!mButtonZoomout.isEnabled()) {
				mButtonZoomout.setEnabled(true);
			}
			if (!mButtonZoomin.isEnabled()) {
				mButtonZoomin.setEnabled(true);
			}
		} else if (level == minZoomLevel) {
//			mButtonZoomout.setEnabled(false);
		} else if (level == maxZoomLevel) {
//			mButtonZoomin.setEnabled(false);
		}
	}

}
