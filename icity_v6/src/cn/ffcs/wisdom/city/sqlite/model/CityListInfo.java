package cn.ffcs.wisdom.city.sqlite.model;

import java.io.Serializable;

import cn.ffcs.wisdom.city.entity.CityEntity;
import cn.ffcs.wisdom.tools.StringUtil;
import cn.ffcs.wisdom.tools.TimeUitls;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * <p>Title: 城市列表数据库表   </p>
 * <p>Description: 
 * </p>
 * <p>@author: liaodl                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-1-28             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
@DatabaseTable(tableName = "t_city_list_info")
public class CityListInfo implements Serializable {

	private static final long serialVersionUID = 3616521733189596160L;

	// for QueryBuilder to be able to find the fields
	public static final String PROVINCE_LIST_ID_FIELD_NAME = "province_id";
	public static final String CITY_CODE_FIELD_NAME = "city_code";
	public static final String CITY_NAME_FIELD_NAME = "city_name";
	public static final String CITY_INFO_FIELD_NAME = "city_info";
	public static final String CITY_ORDER_FIELD_NAME = "city_order";
	public static final String CITY_SERVER_URL_FIELD_NAME = "server_url";
	public static final String CITY_IS_BUILD_FIELD_NAME = "is_build";
	public static final String INSERT_DATE_FIELD_NAME = "insert_date";
	public static final String SORT_KEY_FIELD_NAME = "sort_key";
	public static final String CITY_PIN_YIN_FIELD_NAME = "pin_yin";
	public static final String CITY_ALL_FIRST_PIN_YIN_FIELD_NAME = "all_first_pin_yin";//所有首字母拼音
	public static final String CITY_STYLE_FIELD_NAME = "city_style";// 城市风格

	@DatabaseField(generatedId = true, columnName = "city_id")
	private int id;

	@DatabaseField(columnName = CITY_CODE_FIELD_NAME)
	private String cityCode;

	@DatabaseField(columnName = CITY_NAME_FIELD_NAME)
	private String cityName;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = PROVINCE_LIST_ID_FIELD_NAME)
	private ProvinceListInfo province;

	@DatabaseField(columnName = CITY_SERVER_URL_FIELD_NAME)
	private String cityServerUrl;

	@DatabaseField(columnName = CITY_ORDER_FIELD_NAME)
	private String cityOrder;

	@DatabaseField(columnName = CITY_IS_BUILD_FIELD_NAME)
	private String isBuild;

	@DatabaseField(columnName = CITY_INFO_FIELD_NAME)
	private String cityInfo;

	@DatabaseField(columnName = INSERT_DATE_FIELD_NAME)
	private String insertDate;

	@DatabaseField(columnName = SORT_KEY_FIELD_NAME)
	private String sortKey;

	@DatabaseField(columnName = CITY_PIN_YIN_FIELD_NAME)
	private String pinyin;// 全拼

	@DatabaseField(columnName = CITY_ALL_FIRST_PIN_YIN_FIELD_NAME)
	private String allFirstPinyin;// 首字母拼音
	
	@DatabaseField(columnName = CITY_STYLE_FIELD_NAME)
	private String cityStyle;

	CityListInfo() {
		// all persisted classes must define a no-arg constructor with at least
		// package visibility
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public ProvinceListInfo getProvince() {
		return province;
	}

	public void setProvince(ProvinceListInfo province) {
		this.province = province;
	}

	public String getCityServerUrl() {
		return cityServerUrl;
	}

	public void setCityServerUrl(String cityServerUrl) {
		this.cityServerUrl = cityServerUrl;
	}

	public String getCityOrder() {
		return cityOrder;
	}

	public void setCityOrder(String cityOrder) {
		this.cityOrder = cityOrder;
	}

	public String getIsBuild() {
		return isBuild;
	}

	public void setIsBuild(String isBuild) {
		this.isBuild = isBuild;
	}

	public String getCityInfo() {
		return cityInfo;
	}

	public void setCityInfo(String cityInfo) {
		this.cityInfo = cityInfo;
	}

	public String getInsertDate() {
		return insertDate;
	}

	public void setInsertDate(String insertDate) {
		this.insertDate = insertDate;
	}

	public String getSortKey() {
		return sortKey;
	}

	public void setSortKey(String sortKey) {
		this.sortKey = sortKey;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public String getAllFirstPinyin() {
		return allFirstPinyin;
	}

	public void setAllFirstPinyin(String allFirstPinyin) {
		this.allFirstPinyin = allFirstPinyin;
	}
	
	public String getCityStyle() {
		return cityStyle;
	}

	public void setCityStyle(String cityStyle) {
		this.cityStyle = cityStyle;
	}

	/**
	 * 实体转换 CityEntity --> CityListInfo 
	 * @param entity
	 * @return
	 */
	public static CityListInfo converter(CityEntity entity) {
		CityListInfo info = new CityListInfo();
		info.setCityCode(entity.getCity_code());
		info.setCityInfo(entity.getAlter_msg());

		String name = entity.getCity_name();
		info.setCityName(name);

		info.setInsertDate(TimeUitls.getCurrentTime());
		info.setCityOrder(entity.getCity_order());
		info.setCityServerUrl(entity.getServer_url());
		info.setCityStyle(entity.getCity_style());

//		String sortKey = "";
//		if(StringUtil.isEmpty(sortKey)){
//			sortKey = ChangeCityUtil.getSortKey(name);
//		}
//		info.setSortKey(sortKey);

		String pinyin = entity.getCityQuanPin();
		if (StringUtil.isEmpty(pinyin)) {
			pinyin = "";
		}
		info.setPinyin(pinyin);

		String allFirstPinyin = entity.getCityJianPin();
		if (StringUtil.isEmpty(allFirstPinyin)) {
			allFirstPinyin = "";
		}
		info.setAllFirstPinyin(allFirstPinyin);

		return info;
	}

	/**
	 * 实体转换 CityEntity --> CityListInfo 
	 * <br/><br/>
	 * ormlite 多表连接
	 * @param cityEntity
	 * @return
	 */
	public static CityListInfo converter(CityEntity cityEntity, ProvinceListInfo provinceListInfo) {
		CityListInfo info = new CityListInfo();
		info.setCityCode(cityEntity.getCity_code());
		info.setCityInfo(cityEntity.getAlter_msg());
		info.setCityStyle(cityEntity.getCity_style());

		info.setInsertDate(TimeUitls.getCurrentTime());

		info.setProvince(provinceListInfo);//插入数据库时，已生成主键id--(ormlite 多表连接)

		String name = cityEntity.getCity_name();
		info.setCityName(name);

//		String sortKey = "";
//		if(StringUtil.isEmpty(sortKey)){
//			sortKey = ChangeCityUtil.getSortKey(name);
//		}
//		info.setSortKey(sortKey);

		String pinyin = cityEntity.getCityQuanPin();
		if (StringUtil.isEmpty(pinyin)) {
			pinyin = "";
		}
		info.setPinyin(pinyin);

		String allFirstPinyin = cityEntity.getCityJianPin();
		if (StringUtil.isEmpty(allFirstPinyin)) {
			allFirstPinyin = "";
		}
		info.setAllFirstPinyin(allFirstPinyin);

		return info;
	}

}
