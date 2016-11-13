package com.ctbri.wxcc.community;

public interface Watcher {
	/**
	 * 触发类型为刷新 的监听器
	 */
	int TYPE_REFRESH = 0;
	/**
	 * 触发类别为更新的 监听器
	 */
	int TYPE_UPDATE_CONMENT_DESC = 1;
	/**
	 * 触发类型为 分享的监听器
	 */
	int TYPE_SHARE = 2;
	
	/**
	 * 触发类型为  下拉刷新的监听器
	 */
	int TYPE_PULLTOREFRESH = 3;
	/**
	 * @see <br />  {@link Watcher#TYPE_REFRESH} <br />{@link #TYPE_UPDATE_CONMENT_DESC}
	 * @param type 
	 */
	void trigger(Object obj);
	
	int getType();
}
