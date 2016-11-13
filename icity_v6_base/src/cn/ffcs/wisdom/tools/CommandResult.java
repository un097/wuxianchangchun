package cn.ffcs.wisdom.tools;

/**
 * <p>Title:   调用系统命令回调实体     </p>
 * <p>Description:                     </p>
 * <p>@author: zhangwsh                </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-9-23           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class CommandResult {

	public int resultCode;

	public String successMsg;

	public String errorMsg;

	CommandResult(int resultCode, String successMsg, String errorMsg) {
		this.resultCode = resultCode;
		this.successMsg = successMsg;
		this.errorMsg = errorMsg;
	}
}
