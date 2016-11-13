package cn.ffcs.wisdom.city.sqlite.model;

import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.notify.MsgEntity;
import cn.ffcs.wisdom.tools.JsonUtil;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.StringUtil;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * <p>Title: 爱动漫消息中心推送-消息通知表        </p>
 * <p>Description: 
 *   爱动漫消息中心推送消息通知表
 * </p>
 * <p>@author: liaodl                </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-1-11             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
@DatabaseTable(tableName = "t_notification_info")
public class NotificationInfo {

	@DatabaseField(generatedId = true, columnName = "_id")
	private int id;

	@DatabaseField(columnName = "city_code")
	private String cityCode;

	@DatabaseField(columnName = "msg_id")
	//平台给的消息id
	private String msgId;

	@DatabaseField(columnName = "idm_msg_id")
	//爱动漫id，消息回执用的
	private String idmMgId;

	@DatabaseField(columnName = "msg_info")
	private String msgInfo;

	/**
	 * 消息类型  
	 * -1. 表示其他类型
	 * 1.  表示系统类型    	---  通知提醒 
	 * 2.  表示个人类型   	---  个人中心消息
	 * 3.  表示意见反馈    
	 * 4.  表示天气类型 
	 * 5.  表示违章类型
	 * 6.  表示新闻详情类型
	 */
	@DatabaseField(columnName = "msg_type")
	private String msgType;

	@DatabaseField(columnName = "insert_date")
	private String insertDate;

	@DatabaseField(columnName = "is_new")
	private String isNew; // 是否新消息，未被读取

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

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public String getIdmMgId() {
		return idmMgId;
	}

	public void setIdmMgId(String idmMgId) {
		this.idmMgId = idmMgId;
	}

	public String getMsgInfo() {
		return msgInfo;
	}

	public void setMsgInfo(String msgInfo) {
		this.msgInfo = msgInfo;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public String getInsertDate() {
		return insertDate;
	}

	public void setInsertDate(String insertDate) {
		this.insertDate = insertDate;
	}

	public String getIsNew() {
		return isNew;
	}

	public void setIsNew(String isNew) {
		this.isNew = isNew;
	}

	//TODO 以后某个模块具体类名要是改了，这里也要同步修改
	public static final String FEED_BACK_RESULT_ACTIVITY = "cn.ffcs.wisdom.city.setting.feedback.FeedBackReplyActivity";
	public static final String NOTIFICATION_ACTIVITY = "cn.ffcs.wisdom.city.push.NotificationActivity";
	public static final String NOTIFICATION_DETAIL_ACTIVITY = "cn.ffcs.wisdom.city.push.NotificationDetailActivity";
	public static final String MY_NOTICE_ACTIVITY = "cn.ffcs.wisdom.city.personcenter.MyNotifierActivity";
	public static final String BROWSER_ACTIVITY = "cn.ffcs.wisdom.city.web.BrowserActivity";

	/**
	 * 实体与表转换器
	 * 
	 * @param entity
	 * @param cityCode
	 * @return
	 */
	public static NotificationInfo converter(MsgEntity entity, String cityCode) {
		NotificationInfo table = new NotificationInfo();
		try {
			table.setMsgId(entity.getContent().getMsgId());
			table.setCityCode(cityCode);
			table.setIsNew("true");
			table.setMsgInfo(JsonUtil.toJson(entity));
//			table.setInsertDate(TimeUitls.getCurrentTime());
			table.setInsertDate(entity.getContent().getParam().getCreateTime());

			String type = entity.getContent().getParam().getMsgType();
			Log.i("--平台传回的msgTpye值为--：" + type);
//			String pkg = entity.getContent().getParam().getAndroidPak();
			String cls = entity.getContent().getParam().getAndroidClass();
			Log.i("--实体与表转换器中cls--：" + cls);
//			String clzz = pkg + "." + cls;
			/**
			 * 如果平台没向我们传消息类型，只能按已知情况做出判断。
			 */
			if (StringUtil.isEmpty(type)) {
				if (FEED_BACK_RESULT_ACTIVITY.equals(cls)) {//意见反馈
					type = Config.OTHER_MSG_TYPE;
				} else if (MY_NOTICE_ACTIVITY.equals(cls)) {//个人中心通知消息
					type = Config.PERSON_MSG_TYPE;
				} else {//默认是NOTIFICATION_DETAIL_ACTIVITY 系统通知消息
					type = Config.SYSTEM_MSG_TYPE;
				}
			}
			Log.i("--处理后msgTpye值为--：" + type);
			table.setMsgType(type);

			// 新增爱动漫消息类型保存
			table.setIdmMgId(entity.getIdmMsgId());
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}

		return table;
	}

}
