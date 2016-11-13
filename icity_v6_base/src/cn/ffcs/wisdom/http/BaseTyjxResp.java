package cn.ffcs.wisdom.http;

import java.io.Serializable;

/**
 * <p>Title: 天翼景象相应的对象基类，需要获取景象返回对象，需要继承此基类  </p>
 * <p>Description: 
 * This is a change object in SysLog
 * </p>
 * <p>@author: Eric.wsd                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-7-18             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */

public class BaseTyjxResp extends BaseResp implements Serializable {
	private static final long serialVersionUID = -7218548620983651731L;
	
	public static final String SUCCESS = "1";	//请求成功
	public static final String ERROR = "0";		//请求失败
	public static final String NETWORK_ERROR = "-1";		//网络异常

	public static final String MSG_PARAMS_UNINTEGRATED = "01"; // 参数不完整或者不能为空
	public static final String MSG_APPKEY_UNVALID = "02"; // 应用的APPKEY不正确
	public static final String MSG_APPSECRET_UNVALID = "03"; // 应用的密钥错误

	private String returnCode; // 1 成功， 0 失败

	private String errorCode; // 01：参数不完整；02：应用appkey不正确；03：应用的密钥错误

	private String msg; // 返回信息

	private String subCode;

	private String subMsg;

	private String body; // 提交的请求信息

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getSubCode() {
		return this.subCode;
	}

	public void setSubCode(String subCode) {
		this.subCode = subCode;
	}

	public String getSubMsg() {
		return this.subMsg;
	}

	public void setSubMsg(String subMsg) {
		this.subMsg = subMsg;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public boolean isSuccess() {
		return this.errorCode == null && this.subCode == null;
	}

	/**
	 * @return the returnCode
	 */
	public String getReturnCode() {
		return returnCode;
	}

	/**
	 * @param returnCode the returnCode to set
	 */
	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}
}
