package cn.ffcs.wisdom.city.sqlite.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.sqlite.DBHelper;
import cn.ffcs.wisdom.city.sqlite.DBManager;
import cn.ffcs.wisdom.city.sqlite.dao.NotificationInfoDao;
import cn.ffcs.wisdom.city.sqlite.model.NotificationInfo;
import cn.ffcs.wisdom.notify.MsgEntity;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.StringUtil;

import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;
/**
 * <p>Title: 消息推送数据库操作类                                        </p>
 * <p>Description:                     </p>
 * <p>@author: liaodl                  </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-3-28           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class NotificationInfoDaoImpl implements NotificationInfoDao {

	private static RuntimeExceptionDao<NotificationInfo, Integer> noticeDao;

	public NotificationInfoDaoImpl(Context context) {
		if (noticeDao == null) {
			DBHelper helper = (DBHelper) DBManager.getHelper(context);
			noticeDao = helper.getRuntimeExceptionDao(NotificationInfo.class);
		}
	}

	@Override
	public boolean isExist(Context context, MsgEntity entity) {
		if (entity == null || entity.getContent() == null) {
			Log.e("notification is null");
			return false;
		}
//		String cityCode = MenuMgr.getInstance().getCityCode(context);
		String msgId = entity.getContent().getMsgId();
		Map<String, Object> args = new HashMap<String, Object>();
//		args.put("city_code", cityCode);
		args.put("msg_id", msgId);
		List<NotificationInfo> noticeList = noticeDao.queryForFieldValues(args);
		if (noticeList != null && noticeList.size() > 0) {
			return true;
		}
		return false;
	}

	@Override
	public List<NotificationInfo> findAllByNew(Context context) {
//		String cityCode = MenuMgr.getInstance().getCityCode(context);
		List<NotificationInfo> noticeList = new ArrayList<NotificationInfo>();
		try {
			QueryBuilder<NotificationInfo, Integer> qb = noticeDao.queryBuilder();
			qb.where().eq("is_new", "true");
			noticeList = qb.query();// 得到所有结果
		} catch (SQLException e) {
			Log.i("--findAllByNew()--数据库无相关信息。--");
			return noticeList;
		}
		return noticeList;
	}

	@Override
	public List<NotificationInfo> findAll(Context context) {
		List<NotificationInfo> noticeList = new ArrayList<NotificationInfo>();
//		String cityCode = MenuMgr.getInstance().getCityCode(context);
		String sql = "select * from t_notification_info order by is_new desc, insert_date desc";
		GenericRawResults<NotificationInfo> rawResults = noticeDao.queryRaw(sql, noticeDao.getRawRowMapper());
		if (rawResults != null) {
			for (NotificationInfo info : rawResults) {
				noticeList.add(info);
			}
			return noticeList;
		}
		return noticeList;
	}

	@Override
	public void remove(MsgEntity entity) {
		if (entity == null) {
			Log.e("--Notification is null--");
			return;
		}
		String sql = "delete from t_notification_info where msg_id=?";
		String[] args = new String[] { entity.getContent().getMsgId() };
		noticeDao.executeRaw(sql, args);
	}

	@Override
	public void clear() {
		String sql = "delete from t_notification_info";
//		String[] args = new String[] { };
//		noticeDao.executeRaw(sql, args);
		noticeDao.executeRawNoArgs(sql);
		
	}

	@Override
	public void updateNewById(String id) {
		if (StringUtil.isEmpty(id)) {
			Log.e("--msgId is null or null--");
			return;
		}

		String sql = "update t_notification_info set is_new=? where msg_id=?";
		String[] args = new String[] { "false", id };
		noticeDao.executeRaw(sql, args);
	}

	@Override
	public void save(Context context, MsgEntity entity) {
		try {
			String cityCode = MenuMgr.getInstance().getCityCode(context);
			NotificationInfo info = NotificationInfo.converter(entity, cityCode);
			noticeDao.create(info);
		} catch (Exception e) {
			Log.e("--消息保存失败--");
			e.printStackTrace();
		}
	}

	@Override
	public List<NotificationInfo> findNewMsgByType(Context context, String msgType) {
		List<NotificationInfo> noticeList = new ArrayList<NotificationInfo>();
		try {
//			String cityCode = MenuMgr.getInstance().getCityCode(context);
			QueryBuilder<NotificationInfo, Integer> qb = noticeDao.queryBuilder();
			qb.where().eq("msg_type", msgType).and().eq("is_new", "true");
//			qb.where().eq("msg_type", msgType);
//			qb.where().eq("is_new", "true");
			qb.orderBy("insert_date", false);// 降序排列
			noticeList = qb.query();
			if (noticeList != null && noticeList.size() > 0) {
				return noticeList;
			}
		} catch (Exception e) {
			Log.i("--findMsgByMsgType()--数据库无相关信息。--");
			return noticeList;
		}
		return noticeList;
	}

	@Override
	public void updateNewByMsgType(String msgType) {
		if (StringUtil.isEmpty(msgType)) {
			Log.e("msg_type is null");
			return;
		}

		String sql = "update t_notification_info set is_new=? where msg_type=?";
		String[] args = new String[] { "false", msgType };
		noticeDao.executeRaw(sql, args);
	}

	@Override
	public List<NotificationInfo> findSystemNotice(Context context) {
		List<NotificationInfo> noticeList = new ArrayList<NotificationInfo>();
//		String cityCode = MenuMgr.getInstance().getCityCode(context);
		String sql = "select * from t_notification_info where msg_type="+ Config.SYSTEM_MSG_TYPE +" order by is_new desc, insert_date desc";
		GenericRawResults<NotificationInfo> rawResults = noticeDao.queryRaw(sql, noticeDao.getRawRowMapper());
		if (rawResults != null) {
			for (NotificationInfo info : rawResults) {
				noticeList.add(info);
			}
			return noticeList;
		}
		return noticeList;
	}
	
	@Override
	public List<NotificationInfo> findAllMsgByType(Context context, String msgType) {
		List<NotificationInfo> noticeList = new ArrayList<NotificationInfo>();
		try {
//			String cityCode = MenuMgr.getInstance().getCityCode(context);
			QueryBuilder<NotificationInfo, Integer> qb = noticeDao.queryBuilder();
			qb.where().eq("msg_type", msgType);
			qb.orderBy("insert_date", false);// 降序排列
			noticeList = qb.query();
			if (noticeList != null && noticeList.size() > 0) {
				return noticeList;
			}
		} catch (Exception e) {
			Log.i("--findMsgByMsgType()--数据库无相关信息。--");
			return noticeList;
		}
		return noticeList;
	}

	@Override
	public NotificationInfo findInfoById(String msgId) {
		NotificationInfo info = new NotificationInfo();
		if(StringUtil.isEmpty(msgId)){
			return info;
		}
		try {
			QueryBuilder<NotificationInfo, Integer> qb = noticeDao.queryBuilder();
			qb.where().eq("msg_id", msgId);
			info = qb.queryForFirst();
			if (info != null) {
				return info;
			}
		} catch (Exception e) {
			Log.i("--findInfoById()--数据库无相关信息。--");
			return info;
		}
		return info;
	}

}
