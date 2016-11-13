package cn.ffcs.wisdom.city.changecity.location;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import cn.ffcs.wisdom.base.CommonTask;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.entity.CityEntity;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.lbs.ILbsCallBack;
import cn.ffcs.wisdom.lbs.LBSLocationClient;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.FileUtils;
import cn.ffcs.wisdom.tools.SharedPreferencesUtil;

/**
 * <p>Title: 定位业务逻辑层        </p>
 * <p>Description: 
 * 定位业务逻辑层     
 * </p>
 * <p>@author: Eric.wsd                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2012-6-20             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class LocationBo implements HttpCallBack<BaseResp> {
	public static final String LOCATION_CITY_FILE = "location_city_file";
	private Context mContext;
	private HttpCallBack<BaseResp> mCall;
	private LBSLocationClient mLocationClient = null;

	public LocationBo(Context context) {
		this.mContext = context;
	}

	public LocationBo(Context context, HttpCallBack<BaseResp> call) {
		this(context);
		this.mCall = call;
	}

	/**
	 * 根据Location获取城市信息 <br/><br/> 
	 * 采用新版CommonTask，无需自己再写LocationTask、LocationResp
	 * @param lat
	 * @param lng
	 * @param city
	 * @param district <br/>
	 */
	public void location(String lat, String lng, String city, String district) {
		Map<String, String> params = new HashMap<String, String>(1);
		String versionCode = AppHelper.getVersionCode(mContext) + "";
		String clientType = mContext.getString(R.string.version_name_update);

		params.put("lat", lat);
		params.put("lng", lng);
		params.put("city_name", city);
		params.put("district", district);
		params.put("client_version", versionCode);
		params.put("client_type", clientType);

		String url = Config.UrlConfig.URL_LOCATION;
		if (mCall != null) {
			CommonTask task = CommonTask.newInstance(mCall, mContext, CityEntity.class);
			task.setParams(params, url);
			task.execute();
		}
	}

	public void getLocation(Context context, ILbsCallBack callBack) {
		mLocationClient = LBSLocationClient.getInstance(context);
		mLocationClient.registerLocationListener(callBack);
		mLocationClient.startLocationService();
		mLocationClient.getLocaion();
	}

	public void stopLocation() {
		mLocationClient.stopLocationService();
	}

	@Override
	public void call(BaseResp response) {//定位失败，手动调用
		if (mCall != null) {
			mCall.call(response);
		}
	}

	@Override
	public void progress(Object... obj) {
	}

	@Override
	public void onNetWorkError() {
	}

	/**
	 * 设置定位成功失败状态标志
	 * @param flag
	 */
	public void setLocationStatus(boolean flag) {
		SharedPreferencesUtil.setBoolean(mContext, Key.K_LOCATION_SUCCESS, flag);
	}

	public boolean getLocationStatus() {
		return SharedPreferencesUtil.getBoolean(mContext, Key.K_LOCATION_SUCCESS);
	}

	/**
	 * 设置定位得到的信息
	 * @param flag
	 */
	public void setLocationInfo(CityEntity entity) {
		FileUtils.write(mContext, LOCATION_CITY_FILE, entity);
	}

	/**
	 * 得到定位信息
	 * 
	 * @return
	 */
	public CityEntity getLocationInfo() {
		CityEntity entity = new CityEntity();
		boolean flag = getLocationStatus();
		if (flag) {
			entity = (CityEntity) FileUtils.read(mContext, LocationBo.LOCATION_CITY_FILE);
			return entity;
		}
		return null;
	}
}
