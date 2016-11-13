package cn.ffcs.wisdom.lbs;

import android.content.Context;
import cn.ffcs.wisdom.tools.Log;

import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

/**
 * <p>LBS定位Client</p>
 */
public class LBSLocationClient {
	private static LocationClient mLocationClient = null;
	private BDLocationListener myListener = null;

	private static LBSLocationClient mLbsLocationClient = null;

	public static LBSLocationClient getInstance(Context context) {
		if (mLbsLocationClient == null) {
			mLbsLocationClient = new LBSLocationClient(context);
		}
		return mLbsLocationClient;
	}

	private LBSLocationClient(Context context) {
		if (mLocationClient == null) {
			mLocationClient = new LocationClient(context);
		}
	}

	public void registerLocationListener(ILbsCallBack icall) {
		try {
			if (myListener != null) {
				Log.i("unRegister");
				mLocationClient.unRegisterLocationListener(myListener);
			}
			LocationClientOption lc = LocationConfig.getOption();
			mLocationClient.setLocOption(lc);
			myListener = new MyLocationListener(icall);
			mLocationClient.registerLocationListener(myListener);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 启动定位服务
	 */
	public void startLocationService() {
		if (!mLocationClient.isStarted()) {
			mLocationClient.start();
		}
	}

	/**
	 * 获取定位信息
	 */
	public void getLocaion() {
		if (mLocationClient != null) {
			int i = mLocationClient.requestLocation();
			Log.i("return code: " + String.valueOf(i));
		} else {
			Log.e("locaionClient is null");
		}
	}

	/**
	 * 关闭定位服务
	 */
	public void stopLocationService() {
		if (mLocationClient != null && mLocationClient.isStarted()) {
			mLocationClient.stop();
		}
	}
}
