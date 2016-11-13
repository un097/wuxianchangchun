//package cn.ffcs.wisdom.city.changecity;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import android.content.Context;
////import cn.ffcs.config.CityContentProviderTool;
//import cn.ffcs.wisdom.base.CommonTask;
//import cn.ffcs.wisdom.city.changecity.location.LocationBo;
//import cn.ffcs.wisdom.city.common.Config;
//import cn.ffcs.wisdom.city.common.Key;
//import cn.ffcs.wisdom.city.entity.CityEntity;
//import cn.ffcs.wisdom.city.entity.ProvinceEntity;
//import cn.ffcs.wisdom.city.entity.ProvinceListEntity;
//import cn.ffcs.wisdom.city.sqlite.model.CityListInfo;
//import cn.ffcs.wisdom.city.sqlite.service.CityInfoService;
//import cn.ffcs.wisdom.city.sqlite.service.ProvinceInfoService;
//import cn.ffcs.wisdom.city.v6.R;
//import cn.ffcs.wisdom.http.HttpCallBack;
//import cn.ffcs.wisdom.tools.AppHelper;
//import cn.ffcs.wisdom.tools.FileUtils;
//import cn.ffcs.wisdom.tools.SharedPreferencesUtil;
//
///**
// * <p>Title: 切换城市逻辑类                                                   </p>
// * <p>Description:                     </p>
// * <p>@author: liaodl                  </p>
// * <p>Copyright: Copyright (c) 2013    </p>
// * <p>Company: ffcs Co., Ltd.          </p>
// * <p>Create Time: 2013-3-7           </p>
// * <p>@author:                         </p>
// * <p>Update Time:                     </p>
// * <p>Updater:                         </p>
// * <p>Update Comments:                 </p>
// */
//public class ChangeCityBo {
//
//	public Context mContext;
//	private ProvinceInfoService provinceServices;
//	private CityInfoService cityServices;
//
//	public ChangeCityBo(Context ctx) {
//		mContext = ctx;
//		provinceServices = ProvinceInfoService.getInstance(ctx);
//		cityServices = CityInfoService.getInstance(ctx);
//	}
//
//	public void startRequestCityTask(String provinceCode, HttpCallBack<?> callBack) {
//		CityContentProviderTool.clearV7CityCache();// 清除v7城市缓存
//		Map<String, String> params = new HashMap<String, String>(1);
//		params.put("province", provinceCode);
//		//平台换了仿真58080端口，记得加一下参数，后续的江西、宁夏版本仿真环境城市列表出不来，看是不是这原因
//		params.put("osType", AppHelper.getOSType());
//		params.put("versType", mContext.getString(R.string.version_name_update));
//
//		String url = Config.UrlConfig.URL_CHANGE_CITY;
//		CommonTask task = CommonTask.newInstance(callBack, mContext, ProvinceListEntity.class);
//		task.setParams(params, url);
//		task.execute();
//	}
//
//	/**
//	 * 保存省份列表
//	 */
//	public void saveProvinceList(List<ProvinceEntity> provinces, CityEntity entity) {
//		if (provinces != null && provinces.size() > 0 && entity != null) {
//			provinceServices.saveProvinceList(provinces, entity);
//		}
//	}
//
//	/**
//	 * 保存省份列表
//	 */
//	public void saveProvinceList(List<ProvinceEntity> provinces) {
//		// CityEntity entity = getLocationInfo();
//		// if (entity != null) {
//		// services.saveProvinceList(provinces, entity);
//		// } else {
//		// services.saveProvinceList(provinces);
//		// }
//		if (provinces != null && provinces.size() > 0) {
//			provinceServices.saveProvinceList(provinces);
//		}
//	}
//
//	/**
//	 * 数据库中匹配条件结果
//	 *
//	 * @param condition
//	 * @return
//	 */
//	public List<CityListInfo> getSearchResult(String condition) {
//		return cityServices.getSearchResult(condition);
//	}
//
//	/**
//	 * 删除省份列表和对应的城市列表
//	 */
//	public void deleteProvinceCity() {
//		provinceServices.deleteProvinceCity();
//	}
//
//	public List<ProvinceEntity> getOrderProvinces() {
//		CityEntity entity = getLocationInfo();
//		if (entity != null) {
//			provinceServices.updateProvinceByCityCode(entity.getCity_code());
//		}
//		return provinceServices.getOrderProvincesByPinyin();
//	}
//
//	/**
//	 * 得到定位信息
//	 *
//	 * @return
//	 */
//	public CityEntity getLocationInfo() {
//		CityEntity entity = new CityEntity();
//		boolean flag = getLocationStatus();
//		if (flag) {
//			entity = (CityEntity) FileUtils.read(mContext, LocationBo.LOCATION_CITY_FILE);
//			return entity;
//		}
//		return null;
//	}
//
//	/**
//	 * 定位是否成功
//	 *
//	 * @return
//	 */
//	public boolean getLocationStatus() {
//		return SharedPreferencesUtil.getBoolean(mContext, Key.K_LOCATION_SUCCESS);
//	}
//
//	/**
//	 * 更新插入时间
//	 *
//	 * @param entity
//	 */
//	public void updateInsertDate(CityEntity entity, List<ProvinceEntity> provinces) {
//		if (entity == null) {
//			return;
//		}
//		ProvinceEntity provinceEntity = ChangeCityUtil.getProvinceFromCity(entity.getCity_code(),
//				provinces);
//		if (provinceEntity != null) {
//			provinceServices.updateProvince(provinceEntity);
//		}
//	}
//
//	/**
//	 * 更新所有插入时间初值
//	 */
//	public void updateInitInsertDate() {
//		provinceServices.updateInitInsertDate();
//	}
//
//	//保存用户选择的城市
//	public void setSelectCityName(String name) {
//		SharedPreferencesUtil.setValue(mContext, Key.K_SELECT_CITY, name);
//	}
//
//	public String getSelectCityName() {
//		return SharedPreferencesUtil.getValue(mContext, Key.K_SELECT_CITY);
//	}
//
//}
