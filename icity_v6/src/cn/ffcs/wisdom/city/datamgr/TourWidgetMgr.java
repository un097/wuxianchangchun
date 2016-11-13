package cn.ffcs.wisdom.city.datamgr;

import java.util.ArrayList;
import java.util.List;

import cn.ffcs.wisdom.base.DataManager;

import com.ffcs.surfingscene.entity.ActionEntity;

/**
 * <p>Title:  旅游推荐Widget信息管理                      </p>
 * <p>Description:                     </p>
 * <p>@author: liaodl                  </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-4-12           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class TourWidgetMgr extends DataManager {

	private static TourWidgetMgr instance = new TourWidgetMgr();

	private List<ActionEntity> mData;

	private TourWidgetMgr() {
		mData = new ArrayList<ActionEntity>();
	}

	public synchronized static TourWidgetMgr getInstance() {
		if (instance == null) {
			instance = new TourWidgetMgr();
		}
		return instance;
	}

	public List<ActionEntity> getTourInfo() {
		if(mData == null){
			mData = new ArrayList<ActionEntity>();
		}
		return mData;
	}

	public synchronized void refreshData(List<ActionEntity> list) {
		this.mData.clear();
		this.mData.addAll(list);
	}
}
