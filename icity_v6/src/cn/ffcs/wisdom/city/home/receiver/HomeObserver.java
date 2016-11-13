package cn.ffcs.wisdom.city.home.receiver;

import cn.ffcs.wisdom.base.DataManager;

public class HomeObserver extends DataManager {

	private static HomeObserver mInstance;
	private String tag;

	public static HomeObserver getInstance() {
		if (mInstance == null)
			mInstance = new HomeObserver();
		return mInstance;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getTag() {
		return tag;
	}
}
