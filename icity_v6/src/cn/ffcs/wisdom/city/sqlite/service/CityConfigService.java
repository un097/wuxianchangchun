package cn.ffcs.wisdom.city.sqlite.service;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import cn.ffcs.wisdom.city.sqlite.DBHelper;
import cn.ffcs.wisdom.city.sqlite.DBManager;
import cn.ffcs.wisdom.city.sqlite.model.CityConfig;
import cn.ffcs.wisdom.tools.StringUtil;

import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RuntimeExceptionDao;

public class CityConfigService {

	private static CityConfigService service;
	private static RuntimeExceptionDao<CityConfig, Integer> dao;

	static final Object sInstanceSync = new Object();

	private CityConfigService(Context ctx) {
		if (dao == null) {
			DBHelper helper = DBManager.getHelper(ctx);
			dao = helper.getRuntimeExceptionDao(CityConfig.class);
		}
	}

	public static CityConfigService getInstance(Context ctx) {
		synchronized (sInstanceSync) {
			if (service == null) {
				service = new CityConfigService(ctx);
			}
		}
		return service;
	}

	public void saveConfig(String cityCode, CityConfig config) {
		synchronized (sInstanceSync) {
			if (config != null) {
				CityConfig defConfig = load(cityCode);
				if (defConfig != null) {
					config.setId(defConfig.getId());
					dao.update(config);
				} else {
					dao.create(config);
				}
			}
		}
	}

	public CityConfig load(String cityCode) {
		synchronized (sInstanceSync) {
			if (!StringUtil.isEmpty(cityCode)) {
				try {
					String sql = "select * from t_city_config t where t.city_code = " + cityCode;
					GenericRawResults<CityConfig> results = dao
							.queryRaw(sql, dao.getRawRowMapper());

					if (results != null) {
						List<CityConfig> list = results.getResults();
						if (list != null && list.size() == 1) {
							return list.get(0);
						}
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

}
