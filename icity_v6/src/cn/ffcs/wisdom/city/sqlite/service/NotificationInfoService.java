package cn.ffcs.wisdom.city.sqlite.service;

import java.util.List;

import android.content.Context;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.sqlite.dao.NotificationInfoDao;
import cn.ffcs.wisdom.city.sqlite.dao.impl.NotificationInfoDaoImpl;
import cn.ffcs.wisdom.city.sqlite.model.NotificationInfo;
import cn.ffcs.wisdom.notify.MsgEntity;

/**
 * <p>Title: 爱动漫消息中心通知业务层       </p>
 * <p>Description: 
 *   消息通知业务层
 * </p>
 * <p>@author: liaodl                </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-1-13             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class NotificationInfoService {

	private static NotificationInfoService noticeService;
	private static NotificationInfoDao noticeDao;
	
	static final Object sInstanceSync = new Object();

	private NotificationInfoService(Context ctx) {
		if (noticeDao == null) {
			noticeDao = new NotificationInfoDaoImpl(ctx);
		}
	}

	public static NotificationInfoService getInstance(Context ctx) {
		synchronized (sInstanceSync) {
			if (noticeService == null) {
				noticeService = new NotificationInfoService(ctx);
			}
		}

		return noticeService;
	}

	/**
	 * 判断消息是否存在
	 * @param context
	 * @param notice 消息
	 * @return
	 */
	public boolean isExist(Context context, MsgEntity notice) {
		return noticeDao.isExist(context, notice);
	}

	/**
	 * 查找所有新消息
	 * @param context
	 * @return
	 */
	public List<NotificationInfo> findAllByNew(Context context) {
		return noticeDao.findAllByNew(context);
	}

	/**
	 * 查找所有消息，包括新旧
	 * @param context
	 * @return
	 */
	public List<NotificationInfo> findAll(Context context) {
		return noticeDao.findAll(context);
	}
	
	/**
	 * 返回所有新的系统通知消息，用于在设置界面显示新消息条数
	 * @param context
	 * @return
	 */
	public List<NotificationInfo> findNewSystemNotice(Context context) {
		return noticeDao.findNewMsgByType(context, Config.SYSTEM_MSG_TYPE);
	}
	
	/**
	 * 返回所有系统消息
	 * @param context
	 * @return
	 */
	public List<NotificationInfo> findSystemNotice(Context context) {
		return noticeDao.findSystemNotice(context);//测试通过
//		return noticeDao.findAllMsgByType(context, Config.SYSTEM_MSG_TYPE);
	}

	/**
	 * 移除消息通知
	 * @param entity
	 */
	public void remove(MsgEntity entity) {
		noticeDao.remove(entity);
	}
	
	/**
	 * 清空消息通知
	 * @param entity
	 */
	public void clear() {
		noticeDao.clear();
	}

	/**
	 * 依据消息号，更新新消息为已读
	 */
	public void updateNewById(String id) {
		noticeDao.updateNewById(id);
	}

	/**
	 * 保存所有消息到数据库中。
	 * @param cityCode
	 * @param entity	
	 */
	public void save(Context context, MsgEntity entity) {
		noticeDao.save(context, entity);
	}

	/**
	 * 返回所有新的反馈消息
	 * @param mContext
	 * @return
	 */
	public List<NotificationInfo> findNewFeedBack(Context context) {
		return noticeDao.findNewMsgByType(context, Config.FEED_MSG_TYPE);
	}
	
	/**
	 * 查找所有反馈消息
	 * @param mContext
	 * @return
	 */
	public List<NotificationInfo> findFeedBack(Context context) {
		return noticeDao.findAllMsgByType(context, Config.FEED_MSG_TYPE);
	}
	
	/**
	 * 返回所有新的个人中心-我的消息
	 * @param mContext
	 * @return
	 */
	public List<NotificationInfo> findNewMyNotification(Context context) {
		return noticeDao.findNewMsgByType(context, Config.PERSON_MSG_TYPE);
	}
	
	/**
	 * 返回所有个人中心-我的消息
	 * @param mContext
	 * @return
	 */
	public List<NotificationInfo> findMyNotification(Context context) {
		return noticeDao.findAllMsgByType(context, Config.PERSON_MSG_TYPE);
	}

	/**
	 * 返回所有新的违章通知消息
	 * @param context
	 * @return
	 */
	public List<NotificationInfo> findWZMsg(Context context) {
		return noticeDao.findNewMsgByType(context, Config.WZ_MSG_TYPE);
	}

	/**
	 * 根据消息类型，把这类消息标记为已读
	 * @param msgType
	 */
	public void updateNewByMsgType(String msgType) {
		noticeDao.updateNewByMsgType(msgType);
	}

	/**
	 * 通过平台id查找爱动漫消息分配给我们消息的id
	 * @param msgId
	 * @return
	 */
	public NotificationInfo findIdmMsgId(String msgId) {
		return noticeDao.findInfoById(msgId);
	}

}
