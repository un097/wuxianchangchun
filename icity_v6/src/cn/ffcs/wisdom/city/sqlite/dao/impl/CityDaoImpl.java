package cn.ffcs.wisdom.city.sqlite.dao.impl;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import cn.ffcs.wisdom.city.entity.CityEntity;
import cn.ffcs.wisdom.city.entity.ProvinceEntity;
import cn.ffcs.wisdom.city.sqlite.DBHelper;
import cn.ffcs.wisdom.city.sqlite.DBManager;
import cn.ffcs.wisdom.city.sqlite.dao.CityDao;
import cn.ffcs.wisdom.city.sqlite.model.CityListInfo;
import cn.ffcs.wisdom.city.sqlite.model.ProvinceListInfo;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.StringUtil;

import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RuntimeExceptionDao;

public class CityDaoImpl implements CityDao {

	private static RuntimeExceptionDao<CityListInfo, Integer> cityDao;

	public CityDaoImpl(Context context) {
		if (cityDao == null) {
			DBHelper helper = (DBHelper) DBManager.getHelper(context);
			cityDao = helper.getRuntimeExceptionDao(CityListInfo.class);
		}
	}

	@Override
	public void saveCityList(List<CityEntity> cities, ProvinceListInfo provinceListInfo) {
		if (cities == null || cities.size() <= 0 || provinceListInfo == null) {
			return;
		}
		for (CityEntity cityEntity : cities) {
			CityListInfo info = CityListInfo.converter(cityEntity, provinceListInfo);
			cityDao.create(info);
		}
	}

	@Override
	public List<CityListInfo> getSearchResult(String condition) {
		if (StringUtil.isEmpty(condition)) {
			return Collections.emptyList();
		}
		String selection = "select * from t_city_list_info";
		String args = "where " + CityListInfo.CITY_NAME_FIELD_NAME + " like '%" + condition + "%'"
				+ " or " + CityListInfo.CITY_CODE_FIELD_NAME + " like '%" + condition + "%'"
				+ " or " + CityListInfo.CITY_ALL_FIRST_PIN_YIN_FIELD_NAME + " like '" + condition
				+ "%'" + " or " + CityListInfo.CITY_PIN_YIN_FIELD_NAME + " like '" + condition
				+ "%'" + " order by " + CityListInfo.CITY_ALL_FIRST_PIN_YIN_FIELD_NAME;
		String sql = selection + " " + args;
		GenericRawResults<CityListInfo> rawResults = cityDao.queryRaw(sql,
				cityDao.getRawRowMapper());
		if (rawResults != null) {
			try {
				return rawResults.getResults();
			} catch (SQLException e) {
				Log.e(e.getMessage(), e);
			}
		}
		return Collections.emptyList();
	}

	@Override
	public void deleteCity() {
		cityDao.executeRaw("delete from t_city_list_info");
	}

	@Override
	public ProvinceEntity findProvinceByCityCode(String cityCode) {
		ProvinceEntity entity = new ProvinceEntity();
		List<CityListInfo> cityListInfo = cityDao.queryForAll();
		if (cityListInfo == null || cityListInfo.size() <= 0) {
			return entity;
		}
		for (CityListInfo info : cityListInfo) {
			if (info.getCityCode().equals(cityCode)) {
				ProvinceListInfo proInfo = info.getProvince();
				entity = ProvinceEntity.convert(proInfo);
			}
		}
		return entity;
	}

}
