package cn.ffcs.wisdom.city.sqlite.model;

import cn.ffcs.wisdom.city.traffic.violations.entity.TrafficViolationsEntity.TrafficViolationsInfo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * <p>Title:    违章表       </p>
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
@DatabaseTable(tableName = "t_violations")
public class TrafficViolations {

	@DatabaseField(generatedId = true)
	public int id;

	@DatabaseField(columnName = "car_no")
	public String carNo;// 车牌号

	@DatabaseField(columnName = "car_last_codes")
	public String carLastCodes;// 车架后几位

	@DatabaseField(columnName = "violations_address")
	public String violationsAddress;// 违章地点

	@DatabaseField(columnName = "violations_info")
	public String violationsInfo;// 违章信息描述

	@DatabaseField(columnName = "violations_time")
	public String violationsTime;// 违章时间

	@DatabaseField(columnName = "violations_money")
	public String violationsMoney;// 违章罚款金额

	@DatabaseField(columnName = "violations_mark")
	public String violationsMark;// 违章扣分

	@DatabaseField(columnName = "violations_id")
	public String violationsId;// 违章信息编号

	/**
	 * 实体转换
	 * @param carNo
	 * @param carLastCodes
	 * @param entity
	 * @return
	 */
	public static TrafficViolations convertEntity(String carNo, String carLastCodes,
			TrafficViolationsInfo entity) {
		TrafficViolations violations = new TrafficViolations();
		violations.carNo = carNo;
		violations.carLastCodes = carLastCodes;
//		violations.violationsAddress = entity.getWzAddress();
//		violations.violationsId = entity.getWztzdh();
//		violations.violationsMark = entity.getKfz();
//		violations.violationsMoney = entity.getFkje();
//		violations.violationsTime = entity.getWzTime();
//		violations.violationsInfo = entity.getWfxx();
		violations.violationsAddress = entity.getIllegalAddr();
		violations.violationsId = entity.getId() + "";
		violations.violationsMark = entity.getScore();
		violations.violationsMoney = entity.getAmount() + "";
		violations.violationsTime = entity.getIllegalTime();
		violations.violationsInfo = entity.getIllegalInfo();
		return violations;
	}
}
