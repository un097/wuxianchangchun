package cn.ffcs.wisdom.city.entity;

import java.io.Serializable;
import java.util.List;

import cn.ffcs.wisdom.city.sqlite.model.MenuItem;

public class Common implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3627028422337047317L;
	private List<MenuItem> commonApp;
	private MenuItem characteristicChannel;

	public MenuItem getCharacteristicChannel() {
		return characteristicChannel;
	}

	public void setCharacteristicChannel(MenuItem characteristicChannel) {
		this.characteristicChannel = characteristicChannel;
	}

	public MenuItem getMyApp() {
		return myApp;
	}

	public void setMyApp(MenuItem myApp) {
		this.myApp = myApp;
	}

	private MenuItem myApp;

	public List<MenuItem> getCommonApp() {
		return commonApp;
	}

	public void setCommonApp(List<MenuItem> commonApp) {
		this.commonApp = commonApp;
	}
}
