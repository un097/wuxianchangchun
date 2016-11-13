package cn.ffcs.wisdom.city.datamgr;

import cn.ffcs.wisdom.base.DataManager;

/**
 * <p>Title:   全部菜单加载状态管理            </p>
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
public class MenuLoadingMgr extends DataManager {
	private static MenuLoadingMgr advertisingMgr;
	private MenuLoadingStatus status;

	private MenuLoadingMgr() {
	}

	public enum MenuLoadingStatus {
		MENU_LOAD_SUCCESS, MENU_NO_CHANGE, MENU_LOAD_FAIL;
	}

	public static MenuLoadingMgr getInstance() {
		if (advertisingMgr == null) {
			advertisingMgr = new MenuLoadingMgr();
		}
		return advertisingMgr;
	}

	public void setLoadingStatus(MenuLoadingStatus status) {
		this.status = status;
	}

	public MenuLoadingStatus getLoadingStatus() {
		return status;
	}
}
