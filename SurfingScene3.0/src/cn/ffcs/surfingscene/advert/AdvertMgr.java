package cn.ffcs.surfingscene.advert;

import cn.ffcs.surfingscene.common.Key;
import cn.ffcs.wisdom.tools.SharedPreferencesUtil;
import android.content.Context;

public class AdvertMgr {

	private static AdvertMgr mInstance = new AdvertMgr();

	private AdvertMgr() {
	}

	public static AdvertMgr getInstance() {
		if (mInstance != null)
			mInstance = new AdvertMgr();
		return mInstance;
	}

	public void saveAdvertImg(Context context, String tyjcCode, String url) {
		SharedPreferencesUtil.setValue(context, Key.K_GLO_ADVERT_IMG_URL + tyjcCode, url);
	}

	public String getAdvertImg(Context context, String tyjcCode) {
		return SharedPreferencesUtil.getValue(context, Key.K_GLO_ADVERT_IMG_URL + tyjcCode);
	}

	public void saveAdvertDuration(Context context, String tyjcCode, String duration) {
		SharedPreferencesUtil.setValue(context, Key.K_GLO_ADVERT_DURATION + tyjcCode, duration);
	}

	public String getAdvertDuration(Context context, String tyjcCode) {
		return SharedPreferencesUtil.getValue(context, Key.K_GLO_ADVERT_DURATION + tyjcCode);
	}

	public void saveCityCode(Context context, String cityCode) {
		SharedPreferencesUtil.setValue(context, Key.K_CITY_CODE, cityCode);
	}

	public String getCityCode(Context context) {
		return SharedPreferencesUtil.getValue(context, Key.K_CITY_CODE);
	}

	public void saveTyjxCode(Context context, String tyjxCode) {
		SharedPreferencesUtil.setValue(context, Key.K_AREA_CODE, tyjxCode);
	}

	public String getTyjxCode(Context context) {
		return SharedPreferencesUtil.getValue(context, Key.K_AREA_CODE);
	}
}
