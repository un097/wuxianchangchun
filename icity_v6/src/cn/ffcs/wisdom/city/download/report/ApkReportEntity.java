package cn.ffcs.wisdom.city.download.report;

import java.io.Serializable;
import java.util.List;

import cn.ffcs.wisdom.city.sqlite.model.ApkReportItem;

/**
 * <p>Title:      apk上报json串实体                     </p>
 * <p>Description:                     </p>
 * <p>@author: liaodl                  </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-11-7           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class ApkReportEntity implements Serializable {

	private static final long serialVersionUID = -8434927535376223571L;
	public long timestamp;
	public String sign;
	public List<ApkReportItem> data;

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public List<ApkReportItem> getData() {
		return data;
	}

	public void setData(List<ApkReportItem> data) {
		this.data = data;
	}

}
