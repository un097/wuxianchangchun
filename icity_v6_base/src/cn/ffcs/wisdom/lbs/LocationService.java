package cn.ffcs.wisdom.lbs;

import android.content.Context;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.Log;

import com.baidu.location.BDLocation;

/**
 * <h3>定位异步服务</h3>
 * <p>异步进行基站定位，后续可以增加GPS定位到这里。启动异步定位前，需要使用{@link #setParams}方法进行参数设置后再调用。</p>
 */
public class LocationService{

	private String action; // broadcast action
	private LBSLocationClient mLocationClient = null;
	private Context mContext;

	public LocationService(HttpCallBack<BaseResp> iCall, Context context) {
		this.mContext=context;
		mLocationClient = LBSLocationClient.getInstance(context);
		mLocationClient.registerLocationListener(new GetLocationCallback());
		mLocationClient.startLocationService();
	}

	/**
	 * 设置参数
	 * @param action
	 * 		参数
	 */
	public void setParams(String action) {
		this.action = action;
	}
	
	public void start(){
		mLocationClient.getLocaion();
	}

	class GetLocationCallback implements ILbsCallBack {
		@Override
		public void call(BDLocation location) {
			if (location != null) {
				int locType = location.getLocType();
				if (locType != BDLocation.TypeGpsLocation
						&& locType != BDLocation.TypeCacheLocation
						&& locType != BDLocation.TypeNetWorkLocation) {// 不是GPS,不是缓存,不是网络定位，则表示定位失败
					Log.e("location failed");
				} else {
					LocationUtils.sendBroadcast(location, mContext, action);
				}
			} else {
				Log.e("location failed");
			}
			mLocationClient.stopLocationService();
		}
	}
}
