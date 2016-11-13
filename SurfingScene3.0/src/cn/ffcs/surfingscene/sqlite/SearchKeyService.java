package cn.ffcs.surfingscene.sqlite;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import cn.ffcs.wisdom.tools.Log;

import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RuntimeExceptionDao;

public class SearchKeyService {
	private static SearchKeyService mInstance;
	private static RuntimeExceptionDao<SearchKey, Integer> mDao;

	static final Object sInstanceSync = new Object();
	private DBHelper helper;

	private SearchKeyService(Context context) {
		if (mDao == null) {
			helper = new DBHelper(context);
			mDao = helper.getRuntimeExceptionDao(SearchKey.class);
		}
	}

	public static SearchKeyService getInstance(Context context) {
		if (mInstance == null)
			mInstance = new SearchKeyService(context);
		return mInstance;
	}

	/**
	 * 获取关键词列表
	 * @param key
	 * @return
	 */
	public List<SearchKey> getSearchKey() {
		List<SearchKey> list = null;
		String sql = "select distinct key_word from t_search_key order by id desc";
		GenericRawResults<SearchKey> results = mDao.queryRaw(sql, mDao.getRawRowMapper());
		if (results != null) {
			try {
				list = results.getResults();
			} catch (Exception e) {
				Log.e(e.getMessage(), e);
			}
		}
		if (list != null) {
			return list;
		} else {
			return Collections.emptyList();
		}
	}

	/**
	 * 写入关键词
	 * @param key
	 */
	public void saveKey(String key) {
		mDao.create(SearchKey.convertEntity(key));
	}
}
