package cn.ffcs.wisdom.city.splashs;

import cn.ffcs.wisdom.base.DataManager;

/**
 * <p>Title:  多图闪屏	                   </p>
 * <p>Description:                     </p>
 * <p>@author: liaodl                  </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-5-19           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class SplashMgr extends DataManager {

	private static SplashMgr instance = new SplashMgr();

	private boolean menuComplete;

	private SplashMgr() {
	}

	public synchronized static SplashMgr getInstance() {
		if (instance == null) {
			instance = new SplashMgr();
		}
		return instance;
	}

	public boolean isMenuComplete() {
		return menuComplete;
	}

	public void setMenuComplete(boolean menuComplete) {
		this.menuComplete = menuComplete;
	}

}
