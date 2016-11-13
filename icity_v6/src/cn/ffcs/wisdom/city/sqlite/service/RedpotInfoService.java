package cn.ffcs.wisdom.city.sqlite.service;

import android.content.Context;
import cn.ffcs.wisdom.city.sqlite.dao.RedPotInfoDao;
import cn.ffcs.wisdom.city.sqlite.dao.impl.RedPotInfoDaoImpl;
import cn.ffcs.wisdom.city.sqlite.model.RedPotInfo;

/**
 * <p>Title: 小红点的数据库业务服务类      </p>
 * <p>Description: 
 * 小红点的数据库业务服务类   
 * </p>
 * <p>@author: Eric.wsd                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2014-3-11             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class RedpotInfoService {
	private static RedpotInfoService mRedPotInfoService;
	private static RedPotInfoDao mRedPotInfoDao;
	static final Object mInstanceSync = new Object();

	private RedpotInfoService(Context ctx) {
		if (mRedPotInfoDao == null) {
			mRedPotInfoDao = new RedPotInfoDaoImpl(ctx);
		}
	}

	public static RedpotInfoService getInstance(Context ctx) {
		synchronized (mInstanceSync) {
			if (mRedPotInfoService == null) {
				mRedPotInfoService = new RedpotInfoService(ctx);
			}
		}

		return mRedPotInfoService;
	}

	/**
	 * 保存/更新数据
	 * @param RedPotInfo
	 */
	public void saveOrUpdate(RedPotInfo redPotInfo) {
		mRedPotInfoDao.saveOrUpdate(redPotInfo);
	}

	/**
	 * 数据是否存在
	 * @param RedPotInfo
	 * @return
	 */
	public boolean isExist(RedPotInfo redPotInfo) {
		return mRedPotInfoDao.isExist(redPotInfo);
	}

	public RedPotInfo find(String menuId) {
		return mRedPotInfoDao.find(menuId);
	}
}
