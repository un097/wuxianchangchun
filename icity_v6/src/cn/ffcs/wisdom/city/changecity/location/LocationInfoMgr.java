package cn.ffcs.wisdom.city.changecity.location;

import cn.ffcs.wisdom.base.DataManager;
import cn.ffcs.wisdom.city.entity.CityEntity;

/**
 * <p>Title:  定位信息                                                           </p>
 * <p>Description:                     </p>
 * <p>@author: liaodl                  </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-3-9           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class LocationInfoMgr extends DataManager {

	private static LocationInfoMgr instance = new LocationInfoMgr();

	private CityEntity cityEntity;

	private LocationInfoMgr() {
	}

	public synchronized static LocationInfoMgr getInstance() {
		if (instance == null) {
			instance = new LocationInfoMgr();
		}
		return instance;
	}

	public CityEntity getCityEntity() {
		return cityEntity;
	}

	public void setCityEntity(CityEntity cityEntity) {
		this.cityEntity = cityEntity;
	}
}
