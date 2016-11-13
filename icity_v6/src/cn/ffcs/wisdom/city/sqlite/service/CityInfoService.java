package cn.ffcs.wisdom.city.sqlite.service;

import java.util.List;

import android.content.Context;
import cn.ffcs.wisdom.city.entity.CityEntity;
import cn.ffcs.wisdom.city.entity.ProvinceEntity;
import cn.ffcs.wisdom.city.sqlite.dao.CityDao;
import cn.ffcs.wisdom.city.sqlite.dao.impl.CityDaoImpl;
import cn.ffcs.wisdom.city.sqlite.model.CityListInfo;
import cn.ffcs.wisdom.city.sqlite.model.ProvinceListInfo;

public class CityInfoService {
	
	public static CityInfoService instance;
	static final Object sInstanceSync = new Object();
	
	private CityDao cityDao;

	private CityInfoService(Context ctx) {
		if (cityDao == null) {
			cityDao = new CityDaoImpl(ctx);
		}
	}
	
	public static CityInfoService getInstance(Context ctx){
		synchronized (sInstanceSync) {
			if (instance == null) {
				instance = new CityInfoService(ctx);
			}
		}
		return instance;
	}
	
	public void saveCityList(List<CityEntity> cities, ProvinceListInfo provinceListInfo) {
		cityDao.saveCityList(cities, provinceListInfo);
	}

	/**
	 * 查询结果
	 * @param condition
	 * @return
	 */
	public List<CityListInfo> getSearchResult(String condition) {
		return cityDao.getSearchResult(condition);
	}
	
	/**
	 * 通过城市编码反查省份
	 */
	public ProvinceEntity findProvinceByCityCode(String cityCode) {
		return cityDao.findProvinceByCityCode(cityCode);
	}
	
	public void deleteCity(){
		cityDao.deleteCity();
	}
	
}
