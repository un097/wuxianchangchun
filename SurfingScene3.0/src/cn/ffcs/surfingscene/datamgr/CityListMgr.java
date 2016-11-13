package cn.ffcs.surfingscene.datamgr;

import android.content.Context;

import com.ffcs.surfingscene.function.CityList;

public class CityListMgr {
	private static CityList cityList;

	public static CityList getInstance(Context context) {
		if (cityList == null) {
			cityList = new CityList(context);
		}
		return cityList;
	}

}
