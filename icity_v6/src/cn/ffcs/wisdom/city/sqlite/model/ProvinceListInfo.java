package cn.ffcs.wisdom.city.sqlite.model;

import java.io.Serializable;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.ffcs.wisdom.city.entity.ProvinceEntity;
import cn.ffcs.wisdom.tools.StringUtil;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * <p>Title: 省份列表数据库表   </p>
 * <p>Description: 
 *  一对多:一个省份对应多个城市
 * </p>
 * <p>@author: liaodl                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-1-28             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
@DatabaseTable(tableName = "t_province_list_info")
public class ProvinceListInfo implements Serializable {

	private static final long serialVersionUID = 7024814366972823281L;

	@DatabaseField(generatedId = true, columnName = "province_id")
	private int id;

	@DatabaseField(columnName = "province_name", canBeNull = false)
	private String provinceName;

	@DatabaseField(columnName = "insert_date")
	private String insertDate;

	@DatabaseField(columnName = "first_char")
	private char firstChar;

	@DatabaseField(columnName = "pinyin")
	private String pinyin;
	
	@ForeignCollectionField
	private ForeignCollection<CityListInfo> cities;
	
	public ProvinceListInfo() {
		// all persisted classes must define a no-arg constructor with at least package visibility
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getInsertDate() {
		return insertDate;
	}

	public void setInsertDate(String insertDate) {
		this.insertDate = insertDate;
	}

	public char getFirstChar() {
		return firstChar;
	}

	public void setFirstChar(char firstChar) {
		this.firstChar = firstChar;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}
	
	public ForeignCollection<CityListInfo> getCities() {
		return cities;
	}

	public void setCities(ForeignCollection<CityListInfo> cities) {
		this.cities = cities;
	}

	/**
	 * 实体转换器 ProvinceEntity --> ProvinceListInfo
	 * @param entity
	 * @return
	 */
	public static ProvinceListInfo converter(ProvinceEntity entity) {
		ProvinceListInfo info = new ProvinceListInfo();
		if (entity == null) {
			return info;
		}
		String name = entity.getProvinceName();
		if (StringUtil.isEmpty(name)) {
			name = "";
		}
		info.setProvinceName(name);
		
		info.setInsertDate("0");// 时间初始值都默认为0

		String pinyin = entity.getProvinceQuanPin();
		if (StringUtil.isEmpty(pinyin)) {
			pinyin = "";
		}
		info.setPinyin(pinyin);
		return info;
	}
	
	public static ProvinceListInfo converterBak(ProvinceEntity entity) {
		ProvinceListInfo info = new ProvinceListInfo();
		if (entity == null) {
			return info;
		}
		String name = entity.getProvinceName();
		if (StringUtil.isEmpty(name)) {
			name = "";
		}
		info.setProvinceName(name);
		
		info.setInsertDate("0");// 时间初始值都默认为0
		
		String pinyin = entity.getProvinceQuanPin();
		if (StringUtil.isEmpty(pinyin)) {
			pinyin = "";
		}
		info.setPinyin(pinyin);
		return info;
	}
	
	/**
	 * 实体转换器 ProvinceEntity --> ProvinceListInfo
	 * 
	 * @param entity
	 * @param currentCity 定位到的城市是否为当前城市
	 * @return
	 */
	public static ProvinceListInfo converter(ProvinceEntity entity,
			boolean currentCity) {
		ProvinceListInfo info = converter(entity);
		if (currentCity) {// 定位到的当前城市要置顶
			Format formatter=new SimpleDateFormat("yyyy-MM-dd hh:MM:ss"); 
			Calendar Cal=Calendar.getInstance(); 
			Cal.setTime(new Date()); 
			//一天后的时间
			Cal.add(java.util.Calendar.HOUR_OF_DAY,24); 
			info.setInsertDate(formatter.format(Cal.getTime()));
		}
		return info;
	}

}
