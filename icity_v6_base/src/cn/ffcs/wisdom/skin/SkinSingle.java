package cn.ffcs.wisdom.skin;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import cn.ffcs.wisdom.base.DataManager;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.SharedPreferencesUtil;
import cn.ffcs.wisdom.tools.StringUtil;

public final class SkinSingle extends DataManager {

	private static SkinSingle mInstance = new SkinSingle();

	private boolean isLoad = Boolean.FALSE;
	private Context skinContext;

	private SkinSingle() {
	}

	public static SkinSingle getInstance() {
		return mInstance;
	}

	public void loadSkin(Context defContext) {
		String packageName = getCurrentSkinPackageName(defContext);
		if (!StringUtil.isEmpty(packageName)) {
			try {
				skinContext = defContext.createPackageContext(packageName,
						Context.CONTEXT_IGNORE_SECURITY);

			} catch (NameNotFoundException e) {
				Log.e("SkinManager error.", e);
				// e.printStackTrace();
				skinContext = null;
			}

			isLoad = Boolean.TRUE;
		}
	}

	/**
	 * if use skin, return true.
	 */
	public boolean isUse(Context defContext) {
		String packageName = getCurrentSkinPackageName(defContext);
		return !StringUtil.isEmpty(packageName);
	}

	public void settingSkin(Context defContext, String packageName) {
		SharedPreferencesUtil.setValue(defContext, SkinKey.K_SKIN_CURRENT, packageName);
		loadSkin(defContext);

		// The notification skin change
		notifyDataSetChanged();
	}

	/**
	 * if skinContext null, return defContext
	 */
	public Context getSkinContext(Context defContext) {
		if (!isLoad) {
			loadSkin(defContext);
		}
		return skinContext == null ? defContext : skinContext;
	}

	public String getCurrentSkinPackageName(Context defContext) {
		return SharedPreferencesUtil.getValue(defContext, SkinKey.K_SKIN_CURRENT);
	}

}
