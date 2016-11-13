package cn.ffcs.surfingscene.sqlite;

import java.io.Serializable;

import com.ffcs.surfingscene.entity.AreaEntity;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "t_areaList")
public class AreaList implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8576621858914583093L;

	@DatabaseField(generatedId = true) 
	private Integer id;
	
	@DatabaseField(columnName = "area_code")
	public String areaCode;
	
	@DatabaseField(columnName = "area_order")
	public String areaOrder;
	
	@DatabaseField(columnName = "area_name")
	public String areaName;
	
	@DatabaseField(columnName = "area_parent")
	public String areaParent;
	
	@DatabaseField(columnName = "area_type")
	public String areaType;

	public static AreaList converEntity(AreaEntity entity){
		AreaList listEntity=new AreaList();
		listEntity.areaCode=entity.getAreaCode();
		listEntity.areaName=entity.getAreaName();
		listEntity.areaOrder=entity.getAreaOrder();
		listEntity.areaParent=entity.getParentCode();
		listEntity.areaType=entity.getAreaType();
		return listEntity;
	}
}
