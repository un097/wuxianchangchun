/**
 * 
 */
package cn.ffcs.wisdom.city.sqlite.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.ffcs.wisdom.notify.WzCarEntity;
import cn.ffcs.wisdom.notify.WzCarEntity.WzCarItem;
import cn.ffcs.wisdom.tools.JsonUtil;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * <p>Title: 违章车辆表      </p>
 * <p>Description: 
 * 违章车辆表   
 * </p>
 * <p>@author: Eric.wsd                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2012-6-19             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */

@DatabaseTable(tableName = "t_wz_car_info")
public class WzCarInfo {
	public static final int OLD = 0; // 假设已缴费状态
	public static final int NEW = 1; // 新的违章信息
	public static final int MID = 2; // 未缴费状态

	@DatabaseField(generatedId = true, columnName = "id")
	private int id;

	@DatabaseField(columnName = "city_code")
	private String cityCode;

	@DatabaseField(columnName = "car_no")
	private String carNo;

	@DatabaseField(columnName = "carType")
	private String carType;

	@DatabaseField(columnName = "carLast4Code")
	private String carLast4Code;

	@DatabaseField(columnName = "is_new")
	private int isNew; // 1：为new;0:为old

	@DatabaseField(columnName = "insert_date")
	private String insertDate;

	@DatabaseField(columnName = "wz_item")
	private String wzCarItem;
	
	@DatabaseField(columnName = "wz_bh")
	private String wzBh;

	@DatabaseField(columnName = "wz_date")
	private String wzDate;

	@DatabaseField(columnName = "wz_tz_sh")
	private String wzTzSh;
	
	@DatabaseField(columnName = "flag")
	private int flag;//用于刷新时的状态标记
	
	@DatabaseField(columnName = "isRelevance")
	private int isRelevance;

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public String getWzTzSh() {
		return wzTzSh;
	}

	public void setWzTzSh(String wzTzSh) {
		this.wzTzSh = wzTzSh;
	}

	public String getWzDate() {
		return wzDate;
	}

	public void setWzDate(String wzDate) {
		this.wzDate = wzDate;
	}

	public String getWzBh() {
		return wzBh;
	}

	public void setWzBh(String wzBh) {
		this.wzBh = wzBh;
	}

	public String getWzCarItem() {
		return wzCarItem;
	}

	public void setWzCarItem(String wzCarItem) {
		this.wzCarItem = wzCarItem;
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

	public String getCarNo() {
		return carNo;
	}

	public void setCarNo(String carNo) {
		this.carNo = carNo;
	}

	public int getIsNew() {
		return isNew;
	}

	public void setIsNew(int isNew) {
		this.isNew = isNew;
	}

	public String getCarType() {
		return carType;
	}

	public void setCarType(String carType) {
		this.carType = carType;
	}

	public String getCarLast4Code() {
		return carLast4Code;
	}

	public void setCarLast4Code(String carLast4Code) {
		this.carLast4Code = carLast4Code;
	}

	public String getInsertDate() {
		return insertDate;
	}

	public void setInsertDate(String insertDate) {
		this.insertDate = insertDate;
	}
	
	public int getIsRelevance() {
		return isRelevance;
	}

	public void setIsRelevance(int isRelevance) {
		this.isRelevance = isRelevance;
	}

	/**
	 * 违章实体类转换，仅用于新的数据，插入数据库之前的转换
	 * @param entity
	 * @param position
	 * @param cityCode
	 * @return
	 * @throws ParseException 
	 */
	public static WzCarInfo converter(WzCarEntity entity, int position, String cityCode) {
		WzCarInfo info = new WzCarInfo();// 定义一个表记录对象
		info.setCityCode(cityCode);
		info.setIsNew(NEW); // 新插入数据为new
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String nowDate = sdf.format(new java.util.Date());
		info.setInsertDate(String.valueOf(nowDate));
		Date date = null;
		WzCarItem item = entity.getCarWZInfo().get(position);
		try {
			date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(item.getWzTime());// 获取违章日期转化为日期格式
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String wzDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);// 违章日期格式化后转化为字符型，为排序使用
		info.setWzDate(wzDate);
		info.setCarType(entity.getCarType());
		info.setCarNo(entity.getCarNo());
		info.setCarLast4Code(entity.getCarWZInfoCount());
		info.setWzBh(item.getWfdm());
		info.setWzTzSh(item.getWztzdh());
		info.setFlag(MID);//初始为新的违章状态
		info.setWzCarItem(JsonUtil.toJson(item));
		info.setIsRelevance(1);
		return info;
	}
}
