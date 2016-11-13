package cn.ffcs.wisdom.city.sqlite.dao;

import java.util.List;

import cn.ffcs.wisdom.city.entity.CityEntity;
import cn.ffcs.wisdom.city.entity.ProvinceEntity;
import cn.ffcs.wisdom.city.sqlite.model.CityListInfo;
import cn.ffcs.wisdom.city.sqlite.model.ProvinceListInfo;

public interface CityDao {

	public void saveCityList(List<CityEntity> cities, ProvinceListInfo provinceListInfo);
	
	public List<CityListInfo> getSearchResult(String condition);
	
	public void deleteCity();
	
	/**
	 * 通过城市编码反查省份
	 */
	public ProvinceEntity findProvinceByCityCode(String cityCode);
}
