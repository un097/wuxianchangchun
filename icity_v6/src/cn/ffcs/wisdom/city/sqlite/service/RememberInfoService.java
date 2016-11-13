package cn.ffcs.wisdom.city.sqlite.service;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import cn.ffcs.wisdom.city.sqlite.DBHelper;
import cn.ffcs.wisdom.city.sqlite.DBManager;
import cn.ffcs.wisdom.city.sqlite.model.RememberInfo;
import cn.ffcs.wisdom.tools.Log;

import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RuntimeExceptionDao;

/**
 * <p>Title:     记住密码服务类               </p>
 * <p>Description:                     </p>
 * <p>@author: zhangws                 </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-8-9           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class RememberInfoService {
	public static RememberInfoService mService;

	private RuntimeExceptionDao<RememberInfo, Integer> mDao;

	static final Object sInstanceSync = new Object();

	private RememberInfoService(Context ctx) {
		if (mDao == null) {
			DBHelper helper = DBManager.getHelper(ctx);
			mDao = helper.getRuntimeExceptionDao(RememberInfo.class);
		}
	}

	public static RememberInfoService getInstance(Context ctx) {
		synchronized (sInstanceSync) {
			if (mService == null) {
				mService = new RememberInfoService(ctx);
			}
		}
		return mService;
	}

	/**
	 * 保存帐密信息
	 * @param phone
	 * @param password
	 */
	public void saveUserInfo(String phone, String password) {
		RememberInfo info = new RememberInfo();
		info.userName = phone;
		info.password = password;
		info.updateTime = System.currentTimeMillis();
		int id = isHavePhone(phone);
		if (id == 0) {
			mDao.create(info);
		} else {
			info.id = id;
			mDao.update(info);
		}
	}

	/**
	 * 获取已记录手机id
	 * @param phone
	 * @return
	 */
	private int isHavePhone(String phone) {
		String sql = "select id from t_remember_info where user_name = '" + phone + "'";
		GenericRawResults<RememberInfo> results = mDao.queryRaw(sql, mDao.getRawRowMapper());
		if (results != null) {
			try {
				RememberInfo info = results.getFirstResult();
				if (info != null) {
					return info.id;
				}
			} catch (Exception e) {
				Log.e(e.getMessage(), e);
			}
		}
		return 0;
	}

	/**
	 * 删除对应手机号码记录
	 * @param phone
	 */
	public void deleteByPhone(String phone) {
		String sql = "delete from t_remember_info where user_name = '" + phone + "'";
		mDao.executeRawNoArgs(sql);
	}

	/**
	 * 获取全部的记录信息
	 * @return
	 */
	public List<RememberInfo> getAllRemember() {
		String sql = "select * from t_remember_info order by update_time desc";
		GenericRawResults<RememberInfo> results = mDao.queryRaw(sql, mDao.getRawRowMapper());
		if (results != null) {
			try {
				List<RememberInfo> info = results.getResults();
				if (info != null) {
					return info;
				}
			} catch (Exception e) {
				Log.e(e.getMessage(), e);
			}
		}
		return Collections.emptyList();
	}
}
