package cn.ffcs.wisdom.city.sqlite;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;

/**
 * <p>Title: DBManager     </p>
 * <p>Description: 
 *  当没有采用OrmLiteBaseActivity等继承的方法时，采用DBManager来获取DBHelper类
 * </p>
 * <p>@author: Eric.wsd                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-1-10             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class DBManager {

	private static DBHelper databaseHelper = null;

	static final Object sInstanceSync = new Object();

	/**
	 * 获取DBHelper操作对象
	 * @param ctx
	 * @return
	 */
	public static DBHelper getHelper(Context ctx) {
		synchronized (sInstanceSync) {
			if (databaseHelper == null) {
				databaseHelper =  OpenHelperManager.getHelper(ctx, DBHelper.class);
			}

			return databaseHelper;
		}
	}

	/**
	 * 释放DBHelper操作对象
	 */
	public static void release() {
		if (databaseHelper != null) {
			OpenHelperManager.releaseHelper();
		}
	}
}
