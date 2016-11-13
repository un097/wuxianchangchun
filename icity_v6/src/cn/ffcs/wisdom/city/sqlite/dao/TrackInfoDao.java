package cn.ffcs.wisdom.city.sqlite.dao;

import java.util.List;

import cn.ffcs.wisdom.city.sqlite.model.TrackInfo;
/**
 * <p>Title: 最近访问，Dao接口类    </p>
 * <p>Description: 
 * 最近访问，Dao接口类
 * </p>
 * <p>@author: Eric.wsd                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-8-13             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public interface TrackInfoDao {
	/**
	 * 保存到数据库
	 * @param trackInfo
	 */
	public void save(TrackInfo trackInfo);
	
	/**
	 * 更新数据到数据库
	 * @param trackInfo
	 */
	public void update(TrackInfo trackInfo);
	
	/**
	 * 保存或者更新数据到数据库
	 * @param trackInfo
	 */
	public void saveOrUpdate(TrackInfo trackInfo);
	
	/**
	 * 根据地市编码，来获取前20个历史记录
	 * @param cityCode
	 * @return
	 */
	public List<TrackInfo> findAllByCityCode(String cityCode);
	
	/**
	 * 判断是否存在
	 * @param trackInfo
	 * @return
	 */
	public boolean isExist(TrackInfo trackInfo);
	
	/**
	 * 根据给定的获取数据库中值
	 * @param trackInfo
	 * @return
	 */
	public TrackInfo find(TrackInfo trackInfo);
	
	/**
	 * 清除所有记录
	 */
	public void clearAll();
}
