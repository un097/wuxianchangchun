package cn.ffcs.wisdom.city.sqlite.dao;

import java.util.List;

import cn.ffcs.wisdom.city.entity.CityEntity;
import cn.ffcs.wisdom.city.entity.ProvinceEntity;
import cn.ffcs.wisdom.city.sqlite.model.ProvinceListInfo;


public interface ProvinceDao {

	public void saveProvinceList(List<ProvinceEntity> provinces);
	
	public void saveProvinceList(List<ProvinceEntity> provinces, CityEntity cityEntity);
	
	public void deleteProvince();
	
	public List<ProvinceEntity> getOrderProvincesByPinyin();
	
	public void updateProvince(ProvinceEntity entity);
	
	public ProvinceListInfo getProvince(String name);
	
	/**
	 * 更新所有插入时间初值
	 */
	public void updateInitInsertDate();
	
	public void updateProvinceByCityCode(String cityCode);
}
