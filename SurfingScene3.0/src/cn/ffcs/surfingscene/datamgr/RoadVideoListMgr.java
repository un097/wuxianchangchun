package cn.ffcs.surfingscene.datamgr;

import java.util.List;

import com.ffcs.surfingscene.entity.ActionEntity;
import com.ffcs.surfingscene.entity.GlobalEyeEntity;

public class RoadVideoListMgr {

	private static RoadVideoListMgr mInstance = new RoadVideoListMgr();
	private List<GlobalEyeEntity> gloList = null;
	private List<ActionEntity> actions;

	public static RoadVideoListMgr getInstance() {
		if (mInstance == null) {
			mInstance = new RoadVideoListMgr();
		}
		return mInstance;
	}

	public void setGloList(List<GlobalEyeEntity> gloList) {
		this.gloList = gloList;
	}

	public List<GlobalEyeEntity> getGloList() {
		return gloList;
	}

	public void clearData() {
		gloList = null;
		actions = null;
	}

	public synchronized void setGloGroupList(List<ActionEntity> actions) {
		this.actions = actions;
	}

	public List<ActionEntity> getGloGroupList() {
		return actions;
	}

}
