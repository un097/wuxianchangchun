package com.ctbri.wxcc.db;

import android.content.Context;

import com.ctbri.wwcc.greenrobot.DaoMaster;
import com.ctbri.wwcc.greenrobot.DaoMaster.OpenHelper;
import com.ctbri.wwcc.greenrobot.DaoSession;

public class DBManager {

	private static final String DATABASE_NAME = "ctbri_db";

	public static DaoMaster getDaoMaster(Context context) {
		OpenHelper helper = new DaoMaster.DevOpenHelper(context,
				DBManager.DATABASE_NAME, null);
		DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());
		return daoMaster;
	}

	/**
	 * 取得DaoSession
	 * 
	 * @param context
	 * @return
	 */
	public static DaoSession getDaoSession(DaoMaster daoMaster, Context context) {
		if (daoMaster == null) {
			daoMaster = getDaoMaster(context);
		}
		DaoSession daoSession = daoMaster.newSession();
		return daoSession;
	}
}
