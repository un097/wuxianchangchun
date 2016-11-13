package cn.ffcs.wisdom.city.sqlite.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * <p>Title:    违章车辆表          </p>
 * <p>Description:                     </p>
 * <p>@author: zhangwsh                </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-8-30           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
@DatabaseTable(tableName = "t_traffic_violations_car")
public class TrafficViolationsCars {

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField(columnName = "car_no")
	public String carNo;

	@DatabaseField(columnName = "car_last_codes")
	public String carLastCodes;

	@DatabaseField(columnName = "violations_count")
	public int violationsCount;

	public static TrafficViolationsCars convertEntity(String carNo, String carLastCodes, int count) {
		TrafficViolationsCars trafficViolationsCars = new TrafficViolationsCars();
		trafficViolationsCars.carNo = carNo;
		trafficViolationsCars.carLastCodes = carLastCodes;
		trafficViolationsCars.violationsCount = count;
		return trafficViolationsCars;
	}
}
