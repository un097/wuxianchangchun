package cn.ffcs.wisdom.city.sqlite.service;

import java.util.List;

import android.content.Context;
import cn.ffcs.wisdom.city.sqlite.dao.TrackInfoDao;
import cn.ffcs.wisdom.city.sqlite.dao.impl.TrackInfoDaoImpl;
import cn.ffcs.wisdom.city.sqlite.model.TrackInfo;
/**
 * <p>Title: 最近访问栏目的数据库业务服务类      </p>
 * <p>Description: 
 * 最近访问栏目的数据库业务服务类   
 * </p>
 * <p>@author: Eric.wsd                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-8-13             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class TrackInfoService {
	private static TrackInfoService mTrackInfoService;
	private static TrackInfoDao mTrackInfoDao;
	static final Object mInstanceSync = new Object();

	private TrackInfoService(Context ctx) {
		if (mTrackInfoDao == null) {
			mTrackInfoDao = new TrackInfoDaoImpl(ctx);
		}
	}

	public static TrackInfoService getInstance(Context ctx) {
		synchronized (mInstanceSync) {
			if (mTrackInfoService == null) {
				mTrackInfoService = new TrackInfoService(ctx);
			}
		}

		return mTrackInfoService;
	}
	
	/**
	 * 保存/更新数据
	 * @param trackInfo
	 */
	public void saveOrUpdate(TrackInfo trackInfo) {
		mTrackInfoDao.saveOrUpdate(trackInfo);   
	}
	
	/**
	 * 获取前20条数据
	 * @param cityCode
	 * @return
	 */
	public List<TrackInfo> findAllByCityCode(String cityCode) {
		return mTrackInfoDao.findAllByCityCode(cityCode);
	}
	
	/**
	 * 数据是否存在
	 * @param trackInfo
	 * @return
	 */
	public boolean isExist(TrackInfo trackInfo) {
		return mTrackInfoDao.isExist(trackInfo);
	}
	
	/**
	 * 清除所有的最近访问的记录
	 */
	public void clearAll() {
		mTrackInfoDao.clearAll();
	}
}
