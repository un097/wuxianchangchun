package cn.ffcs.external.trafficbroadcast.tool;

import android.content.Context;
import com.baidu.location.BDLocationListener;
import com.baidu.location.*;
import com.baidu.location.LocationClientOption;
import cn.ffcs.wisdom.city.utils.Callback;

/**
 * Created by echo on 2016/1/1.
 */
public class BDHelper {


	private static LocationClient mLocationClient;
   
    /**
     * 在callback中返回字符串，返回用逗号分隔的 经度，纬度,和地址
     *
     * @param context
     * @param callback
     */
    public static void locate1(Context context, final Callback<BDLocation> callback) {
        mLocationClient = new LocationClient(context);     //声明LocationClient类
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(com.baidu.location.BDLocation location) {
//                if (mLocationClient.isStarted())
//                    mLocationClient.stop();

                if (hasResult(location)) {
                    callback.onData(location);
                } else{
                    callback.onData(null);
                }
            }

        });    //注册监听函数

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
//        定位SDK可以返回bd09、bd09ll、gcj02三种类型坐标，若需要将定位点的位置通过百度Android地图 SDK进行地图展示，请返回bd09ll，将无偏差的叠加在百度地图上。
        option.setCoorType(BDLocation.BDLOCATION_GCJ02_TO_BD09LL);//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);

        mLocationClient.start();
    }


    public static boolean hasResult(BDLocation location) {

        if (location == null) return false;

        int locType = location.getLocType();

        return locType == BDLocation.TypeGpsLocation
                || locType == BDLocation.TypeOffLineLocation
                || locType == BDLocation.TypeCacheLocation
                || locType == BDLocation.TypeNetWorkLocation;

    }
    
    public static void stopLocation(){
      if (mLocationClient != null && mLocationClient.isStarted())
           mLocationClient.stop();
    }

}
