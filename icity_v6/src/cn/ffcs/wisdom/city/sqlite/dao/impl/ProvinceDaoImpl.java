//package cn.ffcs.wisdom.city.sqlite.dao.impl;
//
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//
//import android.content.Context;
//import cn.ffcs.wisdom.city.changecity.ChangeCityUtil;
//import cn.ffcs.wisdom.city.common.Key;
//import cn.ffcs.wisdom.city.entity.CityEntity;
//import cn.ffcs.wisdom.city.entity.ProvinceEntity;
//import cn.ffcs.wisdom.city.sqlite.DBHelper;
//import cn.ffcs.wisdom.city.sqlite.DBManager;
//import cn.ffcs.wisdom.city.sqlite.dao.ProvinceDao;
//import cn.ffcs.wisdom.city.sqlite.model.ProvinceListInfo;
//import cn.ffcs.wisdom.city.sqlite.service.CityInfoService;
//import cn.ffcs.wisdom.tools.CommonUtils;
//import cn.ffcs.wisdom.tools.Log;
//import cn.ffcs.wisdom.tools.SharedPreferencesUtil;
//import cn.ffcs.wisdom.tools.StringUtil;
//import cn.ffcs.wisdom.tools.TimeUitls;
//
//import com.j256.ormlite.android.AndroidDatabaseConnection;
//import com.j256.ormlite.dao.RuntimeExceptionDao;
//import com.j256.ormlite.stmt.QueryBuilder;
//
//public class ProvinceDaoImpl implements ProvinceDao {
//
//	private RuntimeExceptionDao<ProvinceListInfo, Integer> provinceDao;
//	private DBHelper helper;
//
//	private Context context;
//	static boolean currentCity = false;
//
//	public ProvinceDaoImpl(Context context) {
//		this.context = context;
//		if (provinceDao == null) {
//			helper = (DBHelper) DBManager.getHelper(context);
//			provinceDao = helper.getRuntimeExceptionDao(ProvinceListInfo.class);
//		}
//	}
//
//	@Override
//	public void saveProvinceList(List<ProvinceEntity> provinces) {
//		if (provinces == null || provinces.size() <= 0) {
//			return;
//		}
//		AndroidDatabaseConnection db = new AndroidDatabaseConnection(helper.getWritableDatabase(),
//				true);
//		db.setAutoCommit(false);
//		for (ProvinceEntity entity : provinces) {
//			ProvinceListInfo info = ProvinceListInfo.converter(entity);
//			provinceDao.create(info);
//
//			List<CityEntity> cities = entity.getCities();
//			saveCityList(cities, info);
//		}
//		try {
//			db.commit(null);
//		} catch (SQLException e) {
//			Log.e(e.getMessage(), e);
//		}
//	}
//
//	/**
//	 * 保存城市列表 <br/>
//	 * </br/> 外键需要，需要在表上面加注解属性
//	 *
//	 * @param cities
//	 * @param provinceListInfo
//	 *            <CityEntity>
//	 * @param ProvinceListInfo
//	 * 实体转换要用到,关联外键时，需在ProvinceDao.create(info)生成过主键id后，调用 <br/>
//	 * 用法参见 {@link cn.ffcs.wisdom.city.sqlite.service.ProvinceInfoService#saveProvinceList}
//	 */
//	public void saveCityList(List<CityEntity> cities, ProvinceListInfo provinceListInfo) {
//		CityInfoService.getInstance(context).saveCityList(cities, provinceListInfo);
//	}
//
//	@Override
//	public void saveProvinceList(List<ProvinceEntity> provinces, CityEntity cityEntity) {
//		if (provinces == null || provinces.size() <= 0 || cityEntity == null) {
//			return;
//		}
//		ProvinceEntity currentProvince = new ProvinceEntity();
//		if (cityEntity != null) {
//			currentProvince = ChangeCityUtil.getProvinceFromCity(cityEntity.getCity_code(),
//					provinces);
//		}
//		AndroidDatabaseConnection db = new AndroidDatabaseConnection(helper.getWritableDatabase(),
//				true);
//		db.setAutoCommit(false);
//
//		for (ProvinceEntity provinceEntity : provinces) {
//			if (provinceEntity.getProvinceName().equals(currentProvince.getProvinceName())) {
//				currentCity = true;
//			}
//			ProvinceListInfo info = ProvinceListInfo.converter(provinceEntity, currentCity);
//			provinceDao.create(info);
//
//			List<CityEntity> cities = provinceEntity.getCities();
//			saveCityList(cities, info);
//			currentCity = false;
//		}
//
//		try {
//			db.commit(null);
//		} catch (SQLException e) {
//			Log.e(e.getMessage(), e);
//		}
//
//	}
//
//	@Override
//	public void deleteProvince() {
//		provinceDao.executeRaw("delete from t_province_list_info");
//	}
//
//	@Override
//	public List<ProvinceEntity> getOrderProvincesByPinyin() {
//		List<ProvinceEntity> OrderProvinceList = new ArrayList<ProvinceEntity>();
//		try {
//			QueryBuilder<ProvinceListInfo, Integer> qb = provinceDao.queryBuilder();
//			if (getLocationStatus()) {
//				qb.orderBy("insert_date", false);// 降序
//			}
//			qb.orderBy("pinyin", true);
//			List<ProvinceListInfo> tmp = qb.query();
//			for (ProvinceListInfo info : tmp) {
//				OrderProvinceList.add(ProvinceEntity.convert(info));
//			}
//		} catch (SQLException e) {
//			Log.e(e.getMessage(), e);
//		}
//		return OrderProvinceList;
//	}
//
//	/**
//	 * 定位是否成功
//	 *
//	 * @return
//	 */
//	public boolean getLocationStatus() {
//		if (!CommonUtils.isNetConnectionAvailable(context)) {
//			return false;
//		}
//		return SharedPreferencesUtil.getBoolean(context, Key.K_LOCATION_SUCCESS);
//	}
//
//	@Override
//	public void updateProvince(ProvinceEntity entity) {
//		if (entity == null) {
//			return;
//		}
//		ProvinceListInfo updateInfo = ProvinceListInfo.converter(entity, true);
//		ProvinceListInfo info = getProvince(entity.getProvinceName());
//		updateInfo.setId(info.getId());
//		provinceDao.update(updateInfo);
//	}
//
//	@Override
//	public ProvinceListInfo getProvince(String name) {
//		ProvinceListInfo info = new ProvinceListInfo();
//		if (!StringUtil.isEmpty(name)) {
//			List<ProvinceListInfo> provinceInfos = provinceDao.queryForEq("province_name", name);
//			if (provinceInfos != null && provinceInfos.size() > 0) {
//				info = provinceInfos.get(0);
//			}
//		}
//		return info;
//	}
//
//	@Override
//	public void updateInitInsertDate() {
//		String sql = "update t_province_list_info set insert_date = '0' ";
//		provinceDao.executeRaw(sql);
//	}
//
//	@Override
//	public void updateProvinceByCityCode(String cityCode) {
//		ProvinceEntity entity = CityInfoService.getInstance(context).findProvinceByCityCode(
//				cityCode);
//		String provinceName = "";
//		if (entity != null) {
//			provinceName = entity.getProvinceName();
//		}
//		String time = TimeUitls.getTomorrowTime();
//		String sql = "update t_province_list_info set insert_date = '" + time
//				+ "' where province_name = '" + provinceName + "'";
//		provinceDao.executeRaw(sql);
//	}
//
//}
