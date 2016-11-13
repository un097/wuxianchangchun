package cn.ffcs.wisdom.city.modular.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ffcs.wisdom.base.DataManager;
import cn.ffcs.wisdom.city.modular.query.entity.QueryInfo;

public class QueryInfoDataMgr extends DataManager {

	private static QueryInfoDataMgr mInstance = new QueryInfoDataMgr();

	private Map<String, List<QueryInfo>> mData = new HashMap<String, List<QueryInfo>>();

	public static QueryInfoDataMgr getInstance() {
		if (mInstance == null)
			mInstance = new QueryInfoDataMgr();
		return mInstance;
	}

	public void refresh(String cityCode, List<QueryInfo> list) {
		if (list == null || list.size() <= 0) {
			return;
		}

		synchronized (mData) {
			List<QueryInfo> info = mData.get(cityCode);
			if (info == null) {
				info = new ArrayList<QueryInfo>();
			}
			info.clear();
			info.addAll(list);
			mData.put(cityCode, info);
		}
	}

	public QueryInfo getQueryInfo(String cityCode, String itemId) {
		List<QueryInfo> infos = mData.get(cityCode);
		if (infos == null) {
			return null;
		}
		for (QueryInfo info : infos) {
			if (info.getItem_id().equals(itemId))
				return info;
		}
		return null;
	}

}
