//package cn.ffcs.wisdom.city.sqlite.service;
//
//import java.util.List;
//
//import android.content.Context;
//import cn.ffcs.wisdom.city.entity.CityEntity;
//import cn.ffcs.wisdom.city.entity.ProvinceEntity;
//import cn.ffcs.wisdom.city.sqlite.dao.ProvinceDao;
//import cn.ffcs.wisdom.city.sqlite.dao.impl.ProvinceDaoImpl;
//import cn.ffcs.wisdom.city.sqlite.model.ProvinceListInfo;
//
///**
// * <p>Title: 操作省份列表、城市列表服务类                          </p>
// * <p>Description:                     </p>
// * <p>@author: liaodl                  </p>
// * <p>Copyright: Copyright (c) 2013    </p>
// * <p>Company: ffcs Co., Ltd.          </p>
// * <p>Create Time: 2013-3-9            </p>
// * <p>@author:                         </p>
// * <p>Update Time:                     </p>
// * <p>Updater:                         </p>
// * <p>Update Comments:                 </p>
// */
//public class ProvinceInfoService {
//	private static ProvinceInfoService provinceInfoService;
//
//	private ProvinceDao provinceDao;
//
//	private Context context;
//	static final Object sInstanceSync = new Object();
//
//	private ProvinceInfoService(Context ctx) {
//		context = ctx;
//		if (provinceDao == null) {
//			provinceDao = new ProvinceDaoImpl(ctx);
//		}
//	}
//
//	public static ProvinceInfoService getInstance(Context ctx) {
//		synchronized (sInstanceSync) {
//			if (provinceInfoService == null) {
//				provinceInfoService = new ProvinceInfoService(ctx);
//			}
//		}
//		return provinceInfoService;
//	}
//
//	/**
//	 * 保存省份列表
//	 * @param provinces
//	 * @param cityEntity
//	 */
//	public void saveProvinceList(List<ProvinceEntity> provinces) {
//		provinceDao.saveProvinceList(provinces);
//	}
//
//	/**
//	 * 保存省份列表,把定位到的当前城市所在省份置顶
//	 * @param provinces
//	 * @param cityEntity
//	 */
//	public void saveProvinceList(List<ProvinceEntity> provinces, CityEntity cityEntity) {
//		provinceDao.saveProvinceList(provinces, cityEntity);
//	}
//
//	/**
//	 * 查询结果--旧版，可扩展，保留。
//	 * @param condition
//	 * @return
//	 */
//	// public GenericRawResults<CityListInfo> getSearchResult2(String condition)
//	// {
//	// String selection = "select * from t_city_list_info";
//	// String args = "where " + CityListInfo.CITY_NAME_FIELD_NAME + " like '%"
//	// + condition + "%' or " + CityListInfo.CITY_CODE_FIELD_NAME
//	// + " like '%" + condition + "%' or "
//	// + CityListInfo.CITY_PIN_YIN_FIELD_NAME + " like '"
//	// + ChangeCityUtil.getPYSearchRegExp(condition, "%") + "%'"
//	// + " order by " + CityListInfo.CITY_PIN_YIN_FIELD_NAME;
//	// String sql = selection + " " + args;
//	// GenericRawResults<CityListInfo> rawResults =
//	// cityDao.queryRaw(sql,cityDao.getRawRowMapper());
//	// return rawResults;
//	// }
//
//
//	public void deleteProvinceCity() {
//		provinceDao.deleteProvince();
//		CityInfoService.getInstance(context).deleteCity();
//	}
//
//	/**
//	 * 获得排好序的省份列表
//	 *
//	 * @return
//	 */
//	public List<ProvinceEntity> getOrderProvincesByPinyin() {
//		return provinceDao.getOrderProvincesByPinyin();
//	}
//
//	/**
//	 * 更新定位到的城市，置顶
//	 * @param provinceEntity
//	 */
//	public void updateProvince(ProvinceEntity entity) {
//		provinceDao.updateProvince(entity);
//	}
//
//	public ProvinceListInfo getProvince(String name) {
//		return provinceDao.getProvince(name);
//	}
//
//	/**
//	 * 更新所有插入时间初值
//	 */
//	public void updateInitInsertDate() {
//		provinceDao.updateInitInsertDate();
//	}
//
//	public void updateProvinceByCityCode(String cityCode) {
//		provinceDao.updateProvinceByCityCode(cityCode);
//	}
//
//}
