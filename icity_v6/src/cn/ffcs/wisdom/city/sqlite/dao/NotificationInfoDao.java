package cn.ffcs.wisdom.city.sqlite.dao;

import java.util.List;

import android.content.Context;
import cn.ffcs.wisdom.city.sqlite.model.NotificationInfo;
import cn.ffcs.wisdom.notify.MsgEntity;

/**
 * <p>Title: 消息通知数据操作接口       </p>
 * <p>Description: 
 * 消息通知数据操作接口
 * </p>
 * <p>@author: liaodl                  </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-1-11           </p>
 * <p>Update Time: 			           </p>
 * <p>Updater:                		   </p>
 * <p>Update Comments: 				   </p>
 */
public interface NotificationInfoDao {

	/**
	 * 判断通知消息是否存在
	 * @param table
	 * @return
	 */
	public boolean isExist(Context context, MsgEntity notice);

	/**
	 * 查找所有新的通知消息
	 * @return
	 */
	public List<NotificationInfo> findAllByNew(Context context);
	
	/**
	 * 查找所有通知消息
	 * @return
	 */
	public List<NotificationInfo> findAll(Context context);

	/**
	 * 删除通知
	 * @param entity
	 */
	public void remove(MsgEntity entity);
	
	/**
	 * 清空通知
	 * @param entity
	 */
	public void clear();

	/**
	 * 更新新通知为已读
	 */
	public void updateNewById(String id);

	/**
	 * 保存消息到到数据库
	 * @param context
	 * @param entity
	 */
	public void save(Context context, MsgEntity entity);

	/**
	 * 查找所有新消息
	 * @param context
	 * @param msgType  消息类型
	 * 如下：
	 * -1. 表示其他类型
	 * 1.  表示系统类型    	---  通知提醒 
	 * 2.  表示个人类型   	---  个人中心消息
	 * 3.  表示意见反馈    
	 * 4.  表示天气类型 
	 * 5.  表示违章类型
	 */
	public List<NotificationInfo> findNewMsgByType(Context context,	String msgType);

	/**
	 * 根据消息类型，把这类消息标记为已读
	 * @param msgType
	 */
	public void updateNewByMsgType(String msgType);

	public List<NotificationInfo> findSystemNotice(Context context);

	/**
	 * 查找所新消息
	 * @param context
	 * @param msgType  消息类型
	 * 如下：
	 * -1. 表示其他类型
	 * 1.  表示系统类型    	---  通知提醒 
	 * 2.  表示个人类型   	---  个人中心消息
	 * 3.  表示意见反馈    
	 * 4.  表示天气类型 
	 * 5.  表示违章类型
	 */
	public List<NotificationInfo> findAllMsgByType(Context context, String msgType);

	/**
	 * 通过平台id查找爱动漫消息分配给我们消息的id
	 * @param msgId
	 * @return
	 */
	public NotificationInfo findInfoById(String msgId);
}
