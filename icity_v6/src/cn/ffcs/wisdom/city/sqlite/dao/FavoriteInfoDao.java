package cn.ffcs.wisdom.city.sqlite.dao;

import android.content.Context;
import cn.ffcs.wisdom.city.sqlite.DBManager;
import cn.ffcs.wisdom.city.sqlite.model.FavoriteInfoModel;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;

/**
 * <p>Title: 收藏夹Dao类     </p>
 * <p>Description: 
 *  通过DBManager获取Helper，获取相应的Dao，需要手动关闭数据库
 * </p>
 * <p>@author: Eric.wsd                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-1-17             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class FavoriteInfoDao extends RuntimeExceptionDao<FavoriteInfoModel, Integer> {

	private static FavoriteInfoDao dao;
	static Object syncObject = new Object();

	public static FavoriteInfoDao newInstance(Context ctx) {
		synchronized (syncObject) {
			if (dao == null) {
				dao = DBManager.getHelper(ctx).getRuntimeExceptionDao(FavoriteInfoModel.class);
			}

			return dao;
		}
	}

	private FavoriteInfoDao(Dao<FavoriteInfoModel, Integer> dao) {
		super(dao);
	}
}
