package cn.ffcs.wisdom.city.sqlite.dao;

import cn.ffcs.wisdom.city.sqlite.model.RedPotInfo;

/**
 * <p>Title: 小红点，Dao接口类    </p>
 * <p>Description: 
 * 小红点，Dao接口类
 * </p>
 * <p>@author: Eric.wsd                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2014-3-11             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public interface RedPotInfoDao {
	/**
	 * 保存到数据库
	 * @param RedPotInfo
	 */
	public void save(RedPotInfo redPotInfo);

	/**
	 * 更新数据到数据库
	 * @param RedPotInfo
	 */
	public void update(RedPotInfo redPotInfo);

	/**
	 * 保存或者更新数据到数据库
	 * @param RedPotInfo
	 */
	public void saveOrUpdate(RedPotInfo redPotInfo);

	/**
	 * 判断是否存在
	 * @param RedPotInfo
	 * @return
	 */
	public boolean isExist(RedPotInfo redPotInfo);

	/**
	 * 根据给定的获取数据库中值
	 * @param RedPotInfo
	 * @return
	 */
	public RedPotInfo find(String menuId);

}
