package cn.ffcs.wisdom.notify;

import java.io.Serializable;


/**
 * <h3>消息实体类    </h3>
 */
public class NotificationIQ implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 591667279170583432L;

	public NotificationIQ() {
	}

	private int id; // 消息id

	private int icon; // 应用icon

	private String title; // 标题

	private String content; // 消息内容

	// private String uri; // 通用资源标志符

	private boolean sound; // 通知声音

	private boolean vibrate; // 震动提示

//	private Message message; // 通知消息 - 旧版实体类
	
	private MsgEntity msgEntity; // 爱动漫消息实体  modify by liaodl

	private boolean enable; // 是否启动状态栏显示

	private String wapUrl; 
	
	private String extraParam;// 外部参数以","隔开,key value间用"|"隔开,防止url无法使用

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isSound() {
		return sound;
	}

	public void setSound(boolean sound) {
		this.sound = sound;
	}

	public boolean isVibrate() {
		return vibrate;
	}

	public void setVibrate(boolean vibrate) {
		this.vibrate = vibrate;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

/*	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}*/

	public MsgEntity getMsgEntity() {
		return msgEntity;
	}

	public void setMsgEntity(MsgEntity msgEntity) {
		this.msgEntity = msgEntity;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public String getWapUrl() {
		return wapUrl;
	}

	public void setWapUrl(String wapUrl) {
		this.wapUrl = wapUrl;
	}

	public String getExtraParam() {
		return extraParam;
	}

	public void setExtraParam(String extraParam) {
		this.extraParam = extraParam;
	}

}
