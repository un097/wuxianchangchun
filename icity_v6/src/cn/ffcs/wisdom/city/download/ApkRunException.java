package cn.ffcs.wisdom.city.download;

/**
 * <p>Title:   自定义运行异常                                              </p>
 * <p>Description:                     </p>
 * <p>@author: liaodl                  </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-5-30           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class ApkRunException extends Exception {

	private static final long serialVersionUID = 7542220337134461604L;

	public ApkRunException() {

	}

	public ApkRunException(String detailMessage) {
		super(detailMessage);
	}

	public ApkRunException(Throwable throwable) {
		super(throwable);
	}

	public ApkRunException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

}
