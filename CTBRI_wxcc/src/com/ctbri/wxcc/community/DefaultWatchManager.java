package com.ctbri.wxcc.community;

import java.util.ArrayList;
import java.util.List;

public class DefaultWatchManager implements WatcherManager {
	public DefaultWatchManager() {
		watchers = new ArrayList<Watcher>();
	}

	private List<Watcher> watchers;

	@Override
	public void addWatcher(Watcher watcher) {
		watchers.add(watcher);
	}

	@Override
	public <T extends Watcher> void removeWatcher(T w) {
			watchers.remove(w);
	}

	@Override
	public void trigger(int type, Object obj) {
			for (Watcher w : watchers) {
				if (w.getType() == type)
					w.trigger(obj);
			}
	}

	@Override
	public void notifys() {

	}

}
