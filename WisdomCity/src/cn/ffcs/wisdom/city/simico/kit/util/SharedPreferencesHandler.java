package cn.ffcs.wisdom.city.simico.kit.util;

import java.util.HashSet;
import java.util.Set;

import android.content.SharedPreferences;
import android.text.TextUtils;

public class SharedPreferencesHandler {
	
	protected static final String TAG = SharedPreferencesHandler.class.getSimpleName();
	private final static String regularEx = "|";
	private final static String regularEx2 = "\\|";

	public static Set<String> getStringSet(SharedPreferences prefs, String key,
			Set<String> defValues) {
		String str = prefs.getString(key, "");
		if (!TextUtils.isEmpty(str)) {
			//TLog.log(TAG, "key="+key+" value:"+str);
			String[] values = str.split(regularEx2 );
			if (defValues == null) {
				defValues = new HashSet<String>();
			}
			for (String value : values) {
				if (!TextUtils.isEmpty(value)) {
					//TLog.log(TAG, "item:"+value);
					defValues.add(value);
				}
			}
		}
		return defValues;
	}

	public static SharedPreferences.Editor putStringSet(
			SharedPreferences.Editor ed, String key, Set<String> values) {
		StringBuilder str = new StringBuilder("");
		if (values != null | !values.isEmpty()) {
			Object[] objects = values.toArray();
			for (Object obj : objects) {
				str.append(obj.toString())
				.append(regularEx);
				//str += obj.toString();
				//str += regularEx;
			}
			//TLog.log(TAG, "key="+key+" value:"+str);
			ed.putString(key, str.toString());
		}
		return ed;
	}
}
