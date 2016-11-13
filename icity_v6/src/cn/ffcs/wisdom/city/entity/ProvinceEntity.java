package cn.ffcs.wisdom.city.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.ffcs.wisdom.city.sqlite.model.CityListInfo;
import cn.ffcs.wisdom.city.sqlite.model.ProvinceListInfo;

import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.ForeignCollection;

/**
 * <p>Title:  省份实体类		   </p>
 * <p>Description:<br/>          
 * </p>
 * <p>@author: liaodl                  </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-1-28           </p>
 */
public class ProvinceEntity implements Serializable {

	private static final long serialVersionUID = 383671861753816984L;

	private List<CityEntity> buildCities;
	private List<CityEntity> cities;
	private String provinceName;
	private boolean isZXS;
	
	private String provinceQuanPin;

	public List<CityEntity> getBuildCities() {
		return buildCities;
	}

	public void setBuildCities(List<CityEntity> buildCities) {
		this.buildCities = buildCities;
	}

	public List<CityEntity> getCities() {
		return cities;
	}

	public void setCities(List<CityEntity> cities) {
		this.cities = cities;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public boolean isZXS() {
		return isZXS;
	}

	public void setZXS(boolean isZXS) {
		this.isZXS = isZXS;
	}
	
	public String getProvinceQuanPin() {
		return provinceQuanPin;
	}

	public void setProvinceQuanPin(String provinceQuanPin) {
		this.provinceQuanPin = provinceQuanPin;
	}

	/**
	 * 实体转换器 ProvinceListInfo --> ProvinceEntity
	 * @param 
	 * @return
	 */
	public static ProvinceEntity convert(ProvinceListInfo info){
		ProvinceEntity entity = new ProvinceEntity();
		if (info == null) {
			return entity;
		}
		entity.setProvinceName(info.getProvinceName());
		
		List<CityEntity> cityEntityList = new ArrayList<CityEntity>();
		List<CityEntity> cityBuildEntityList = new ArrayList<CityEntity>();
		ForeignCollection<CityListInfo> cities = info.getCities();
		CloseableIterator<CityListInfo> iterator = cities.closeableIterator();
		while (iterator.hasNext()) {
			CityListInfo cityListInfo = (CityListInfo) iterator.next();
//			if(StringUtil.isEmpty(cityListInfo.getIsBuild())){
				cityEntityList.add(CityEntity.converter(cityListInfo));
//			}
//			cityBuildEntityList.add(CityEntity.converter(cityListInfo));
		}
		entity.setCities(cityEntityList);
		entity.setBuildCities(cityBuildEntityList);
		
		return entity;
	}
}
