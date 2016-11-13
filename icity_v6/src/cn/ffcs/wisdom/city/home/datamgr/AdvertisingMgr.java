package cn.ffcs.wisdom.city.home.datamgr;

import java.util.HashMap;
import java.util.Map;

import cn.ffcs.wisdom.city.home.entity.AdvertisingEntity;

/**
 * <p>Title:  广告缓存      </p>
 * <p>Description:                     </p>
 * <p>@author: zhangwsh                </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-10-31           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class AdvertisingMgr {

	private static AdvertisingMgr advertisingMgr;
	private Map<String, AdvertisingEntity> advertisingEntityMap = new HashMap<String, AdvertisingEntity>();

	private AdvertisingMgr() {
	}

	public static AdvertisingMgr getInstance() {
		if (advertisingMgr == null) {
			advertisingMgr = new AdvertisingMgr();
		}
		return advertisingMgr;
	}

	public AdvertisingEntity getAdvertisingEntity(String cityCode) {
		if (advertisingEntityMap == null) {
			return null;
		}
		return advertisingEntityMap.get(cityCode);
	}

	public void setAdvertisingEntity(String cityCode, AdvertisingEntity advertisingEntity) {
		if (advertisingEntityMap == null) {
			advertisingEntityMap = new HashMap<String, AdvertisingEntity>();
		}
		advertisingEntityMap.put(cityCode, advertisingEntity);
	}
	
	
	public void setAdvertisingEntity_withCode(String code, AdvertisingEntity advertisingEntity) {
		if (advertisingEntityMap == null) {
			advertisingEntityMap = new HashMap<String, AdvertisingEntity>();
		}
		advertisingEntityMap.put(code, advertisingEntity);
	}
	
	public AdvertisingEntity getAdvertisingEntity_withCode(String code) {
		if (advertisingEntityMap == null) {
			return null;
		}
		return advertisingEntityMap.get(code);
	}

	public void clearAdvertisingEntity() {
		if (advertisingEntityMap != null) {
			advertisingEntityMap.clear();
			advertisingEntityMap = null;
		}
	}
}
