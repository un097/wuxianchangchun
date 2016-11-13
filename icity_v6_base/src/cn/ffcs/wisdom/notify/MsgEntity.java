package cn.ffcs.wisdom.notify;

import java.io.Serializable;

/**
 * <p>Title: MsgEntity					 </p>
 * <p>Description: 引入爱动漫消息接入机制，消息的实体类			     </p>
 * <p>Author: liaodl					 </p>
 * <p>Copyright: Copyright (c) 2013      </p>
 * <p>Company: ffcs Co., Ltd.            </p>
 * <p>Create Time: 2013-1-18             </p>
 * <p>Update Time: 				         </p>
 * <p>Updater: liaodl                    </p>
 * <p>Update Comments: 					 </p>
 */
public class MsgEntity implements Serializable {

	private static final long serialVersionUID = 77994991744391389L;

	private String title;

	private Content content;

	// 新增爱动漫消息类型保存
	private String idmMsgId;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Content getContent() {
		return content;
	}

	public void setContent(Content content) {
		this.content = content;
	}

	public String getIdmMsgId() {
		return idmMsgId;
	}

	public void setIdmMsgId(String idmMsgId) {
		this.idmMsgId = idmMsgId;
	}

	public class Content implements Serializable {

		private static final long serialVersionUID = 2602783109445806865L;

		private String msgId;

		private String isNew;

		private String wap;

		public String getWap() {
			return wap;
		}

		public void setWap(String wap) {
			this.wap = wap;
		}

		public String getActivity() {
			return activity;
		}

		public void setActivity(String activity) {
			this.activity = activity;
		}

		private String activity;

		//是否需要登录
		private String isLogin;

		private String desc;

		private String msgContent;

		private Param param;

		public String getIsLogin() {
			return isLogin;
		}

		public void setIsLogin(String isLogin) {
			this.isLogin = isLogin;
		}



		public String getMsgId() {
			return msgId;
		}

		public void setMsgId(String msgId) {
			this.msgId = msgId;
		}

		public String getIsNew() {
			return isNew;
		}

		public void setIsNew(String isNew) {
			this.isNew = isNew;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		public String getMsgContent() {
			return msgContent;
		}

		public void setMsgContent(String msgContent) {
			this.msgContent = msgContent;
		}

		public Param getParam() {
			return param;
		}

		public void setParam(Param param) {
			this.param = param;
		}

		public class Param implements Serializable {

			private static final long serialVersionUID = 4212789466223651344L;

			private String appSize;
			private String createTime;
			private String picUrl;
			private String updateTime;
			private String mbtId;
			private String isMustlogin;
			private String androidClass;
			private String androidPak;
			private String id;
			private String msgsendBusinessType;
			private String title;
			private String serviceId;
			private String wapUrl;
			private String msgType;
			private String appType;
			private String extraParam;

			public String getAppSize() {
				return appSize;
			}
			public void setAppSize(String appSize) {
				this.appSize = appSize;
			}
			public String getCreateTime() {
				return createTime;
			}
			public void setCreateTime(String createTime) {
				this.createTime = createTime;
			}
			public String getPicUrl() {
				return picUrl;
			}
			public void setPicUrl(String picUrl) {
				this.picUrl = picUrl;
			}
			public String getUpdateTime() {
				return updateTime;
			}
			public void setUpdateTime(String updateTime) {
				this.updateTime = updateTime;
			}
			public String getMbtId() {
				return mbtId;
			}
			public void setMbtId(String mbtId) {
				this.mbtId = mbtId;
			}
			public String getIsMustlogin() {
				return isMustlogin;
			}
			public void setIsMustlogin(String isMustlogin) {
				this.isMustlogin = isMustlogin;
			}
			public String getAndroidClass() {
				return androidClass;
			}
			public void setAndroidClass(String androidClass) {
				this.androidClass = androidClass;
			}
			public String getAndroidPak() {
				return androidPak;
			}
			public void setAndroidPak(String androidPak) {
				this.androidPak = androidPak;
			}
			public String getId() {
				return id;
			}
			public void setId(String id) {
				this.id = id;
			}
			public String getMsgsendBusinessType() {
				return msgsendBusinessType;
			}
			public void setMsgsendBusinessType(String msgsendBusinessType) {
				this.msgsendBusinessType = msgsendBusinessType;
			}
			public String getTitle() {
				return title;
			}
			public void setTitle(String title) {
				this.title = title;
			}
			public String getServiceId() {
				return serviceId;
			}
			public void setServiceId(String serviceId) {
				this.serviceId = serviceId;
			}
			public String getWapUrl() {
				return wapUrl;
			}
			public void setWapUrl(String wapUrl) {
				this.wapUrl = wapUrl;
			}
			public String getMsgType() {
				return msgType;
			}
			public void setMsgType(String msgType) {
				this.msgType = msgType;
			}
			public String getAppType() {
				return appType;
			}
			public void setAppType(String appType) {
				this.appType = appType;
			}
			public String getExtraParam() {
				return extraParam;
			}
			public void setExtraParam(String extraParam) {
				this.extraParam = extraParam;
			}
		}
	}
}
