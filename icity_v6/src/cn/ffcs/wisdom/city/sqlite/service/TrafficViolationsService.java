package cn.ffcs.wisdom.city.sqlite.service;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import cn.ffcs.wisdom.city.sqlite.DBHelper;
import cn.ffcs.wisdom.city.sqlite.DBManager;
import cn.ffcs.wisdom.city.sqlite.model.TrafficViolations;
import cn.ffcs.wisdom.city.sqlite.model.TrafficViolationsCars;
import cn.ffcs.wisdom.city.traffic.violations.entity.TrafficViolationsEntity.TrafficViolationsInfo;
import cn.ffcs.wisdom.tools.Log;

import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.dao.RuntimeExceptionDao;

/**
 * <p>Title:   违章数据库服务器         </p>
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
public class TrafficViolationsService {

	private static TrafficViolationsService trafficViolationsService;
	private static RuntimeExceptionDao<TrafficViolations, Integer> trafficViolationsDao;
	private static RuntimeExceptionDao<TrafficViolationsCars, Integer> carDao;

	private static final Object sInstanceSync = new Object();
	private DBHelper helper;

	private TrafficViolationsService(Context ctx) {
		if (trafficViolationsDao == null || carDao == null) {
			helper = DBManager.getHelper(ctx);
			trafficViolationsDao = helper.getRuntimeExceptionDao(TrafficViolations.class);
			carDao = helper.getRuntimeExceptionDao(TrafficViolationsCars.class);
		}
	}

	public static TrafficViolationsService getInstance(Context ctx) {
		synchronized (sInstanceSync) {
			if (trafficViolationsService == null) {
				trafficViolationsService = new TrafficViolationsService(ctx);
			}
		}
		return trafficViolationsService;
	}

	/**
	 * 保存违章
	 * @param carNo
	 * @param carLastCodes
	 * @param list
	 */
	public void saveViolation(String carNo, String carLastCodes, List<TrafficViolationsInfo> list) {
		if (list != null) {
			AndroidDatabaseConnection db = new AndroidDatabaseConnection(
					helper.getWritableDatabase(), true);
			db.setAutoCommit(false);
			deleteByCarNoAndLastCodes(carNo, carLastCodes);
			int size = list.size();
			for (int i = 0; i < size; i++) {
				trafficViolationsDao.create(TrafficViolations.convertEntity(carNo, carLastCodes,
						list.get(i)));
			}
			carDao.create(TrafficViolationsCars.convertEntity(carNo, carLastCodes, size));
			try {
				db.commit(null);
			} catch (SQLException e) {
				Log.e(e + "");
			}
		}
	}

	/**
	 * 保存没有违章信息的车辆
	 * @param carNo
	 * @param carLastCodes
	 */
	public void saveEmptyList(String carNo, String carLastCodes) {
		AndroidDatabaseConnection db = new AndroidDatabaseConnection(helper.getWritableDatabase(),
				true);
		db.setAutoCommit(false);
		deleteByCarNoAndLastCodesFromCarTable(carNo, carLastCodes);
		carDao.create(TrafficViolationsCars.convertEntity(carNo, carLastCodes, 0));
		try {
			db.commit(null);
		} catch (SQLException e) {
			Log.e(e + "");
		}
	}

	/**
	 * 从单独一张车表里删除车辆
	 * @param carNo
	 * @param carLastCodes
	 */
	private void deleteByCarNoAndLastCodesFromCarTable(String carNo, String carLastCodes) {
		String sql = "delete from t_traffic_violations_car where car_no = '" + carNo
				+ "' and car_last_codes = '" + carLastCodes + "'";
		carDao.executeRawNoArgs(sql);
	}

	/**
	 * 根据车牌和车架删除记录
	 */
	public void deleteByCarNoAndLastCodes(String carNo, String carLastCodes) {
		String sql = "delete from t_violations where car_no = '" + carNo
				+ "' and car_last_codes = '" + carLastCodes + "'";
		trafficViolationsDao.executeRawNoArgs(sql);
		String sql2 = "delete from t_traffic_violations_car where car_no = '" + carNo
				+ "' and car_last_codes = '" + carLastCodes + "'";
		carDao.executeRawNoArgs(sql2);
	}

	/**
	 * 根据车牌删除记录
	 */
	public void deleteByCarNo(String carNo) {
		String sql = "delete from t_violations where car_no = '" + carNo + "'";
		trafficViolationsDao.executeRawNoArgs(sql);
		String sql2 = "delete from t_traffic_violations_car where car_no = '" + carNo + "'";
		carDao.executeRawNoArgs(sql2);
	}

//	/**
//	 * 删除全部数据
//	 */
//	private void deleteAll() {
//		String sql = "delete from t_violations";
//		trafficViolationsDao.executeRawNoArgs(sql);
//		String sql2 = "delete from t_traffic_violations_car";
//		carDao.executeRawNoArgs(sql2);
//	}

	/**
	 * 获取违章车辆信息
	 * @return
	 */
	public List<TrafficViolationsCars> getAllViolationsCar() {
		List<TrafficViolationsCars> list = carDao.queryForAll();
		if (list == null) {
			return Collections.emptyList();
		}
		return list;
	}

	/**
	 * 是否存在
	 * @param carNo
	 * @param carLastCodes
	 * @return
	 */
	public boolean isExist(String carNo, String carLastCodes) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("car_no", carNo);
		map.put("car_last_codes", carLastCodes);
		List<TrafficViolationsCars> list = carDao.queryForFieldValues(map);
		if (list != null && list.size() != 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 根据车牌和车架查询结果
	 * @param carNo
	 * @param carLastCodes
	 * @return
	 */
	public List<TrafficViolations> getViolationsByCarNoAndCarLastCodes(String carNo,
			String carLastCodes) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("car_no", carNo);
		map.put("car_last_codes", carLastCodes);
		List<TrafficViolations> list = trafficViolationsDao.queryForFieldValues(map);
		if (list != null && list.size() > 0) {
			return list;
		} else {
			return Collections.emptyList();
		}
	}
}
