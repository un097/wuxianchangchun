package cn.ffcs.surfingscene.datamgr;

import cn.ffcs.wisdom.base.DataManager;
import cn.ffcs.wisdom.tools.StringUtil;

public class GloCityMgr extends DataManager {
	private static GloCityMgr instance = new GloCityMgr();

	private String cityName;
	private String tyjxCode;
	private boolean isSuccess;

	public synchronized static GloCityMgr getInstance() {
		if (instance == null) {
			instance = new GloCityMgr();
		}
		return instance;
	}

	public void refreshTyjxCode(String tyjxCode) {
		this.tyjxCode = tyjxCode;
		this.cityName = getCityName();
		this.isSuccess = true;
		notifyDataSetChanged();
	}

	public String getTyjxCode() {
		if (StringUtil.isEmpty(tyjxCode)) {
			tyjxCode = "350100";
		}
		return tyjxCode;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

}
