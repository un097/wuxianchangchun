package cn.ffcs.wisdom.lbs;

import com.baidu.location.LocationClientOption;

/**
 * <p>Title: 定位配置类      </p>
 * <p>Description: 
 *  1. 开启GPS
 *  2. 开启获取所有定位信息
 * </p>
 * <p>@author: Eric.wsd                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-1-29             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class LocationConfig {
	private static final String BAIDULOCATION = "bd09ll";
	private static LocationConfig mLocationCofing;
	private static LocationClientOption option;

	private LocationConfig() {
		if (option == null)
			option = new LocationClientOption();
	}

	/**
	 * 配置定位属性
	 * @return
	 */
	public static LocationClientOption getOption() {
		if (mLocationCofing == null) {
			mLocationCofing = new LocationConfig();
			
			option.setCoorType(BAIDULOCATION);
			option.setOpenGps(true);
			option.setAddrType("all"); // 返回的定位结果包含地址信息
		}

		return option;
	}
}
