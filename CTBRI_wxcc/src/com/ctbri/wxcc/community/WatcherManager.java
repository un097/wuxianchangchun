package com.ctbri.wxcc.community;


public interface WatcherManager {

	<T extends Watcher> void addWatcher(T watcher);
	<T extends Watcher> void removeWatcher(T watcher);
	void notifys();
	void trigger(int type, Object obj);
}
