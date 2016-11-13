package cn.ffcs.wisdom.city.changecity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import cn.ffcs.wisdom.base.DataManager;
import cn.ffcs.wisdom.city.entity.ProvinceEntity;

public class ProvinceMgr extends DataManager {

	private static ProvinceMgr mInstance = new ProvinceMgr();

	private List<ProvinceEntity> mData = new ArrayList<ProvinceEntity>();
	
	static final Object sInstanceSync = new Object();
	
	private ProvinceMgr() {
	}

	public static ProvinceMgr getInstance() {
		synchronized (sInstanceSync) {
			if (mInstance == null)
				mInstance = new ProvinceMgr();
		}
		
		return mInstance;
	}

	public void refreshData(Context context, List<ProvinceEntity> list) {
		if (list == null || list.size() <= 0) {
			return;
		}

		synchronized (mData) {
			mData.clear();
			mData.addAll(list);
		}
		
	}

	public List<ProvinceEntity> getProvince() {
		return mData;
	}

}
