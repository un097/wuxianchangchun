package cn.ffcs.wisdom.city.utils;

import java.util.List;

import android.content.Context;
import cn.ffcs.wisdom.city.datamgr.LogReportMgr;
import cn.ffcs.wisdom.city.entity.LogReportEntity;
import cn.ffcs.wisdom.city.sqlite.model.LogItem;

import com.google.gson.Gson;

/**
 * <p>Title:日志上报工具          </p>
 * <p>Description: 
 * </p>
 * <p>@author: Leo                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-9-3             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class LogReportUtil {
	/**
	 * 日志对象转换
	 * @param logs
	 * @return
	 */
	public static String convertLogToJson(List<LogReportEntity> logs) {
		String json = null;
		if (logs != null && logs.size() > 0) {
			Gson gson = new Gson();
			json = gson.toJson(logs);
		}
		return json;
	}

	/**
	 * 获取日志,并转换成日志对象
	 * @return
	 */
	public static String logItemToJson(Context mContext,List<LogItem> logs) {
		String jsonBody = null;
		if (logs != null && logs.size() > 0) {
			List<LogReportEntity> reportList = LogReportMgr.getInstance().convertToLogEntity(logs);
			if (reportList != null && reportList.size() > 0) {
				jsonBody = convertLogToJson(reportList);
			}
		}
		return jsonBody;
	}
}
