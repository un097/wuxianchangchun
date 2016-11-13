package cn.ffcs.wisdom.skin;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import cn.ffcs.wisdom.tools.Log;

/**
 * <p>Title: WisdomCity</p>
 * <p>Description: 换肤管理类</p>
 * <p>Author: tianya</p>
 * <p>CreateTime: 2012-6-3 上午01:58:03 </p>
 * <p>CopyRight: 4.0.2 </p>
 */
public final class SkinManager {

	public static Drawable getDrawable(Context defContext, int resid) {
		if (defContext == null || resid == 0) {
			return null;
		}

		// no use external skin.
		if (!isUse(defContext)) {
			return defContext.getResources().getDrawable(resid);
		}

		String resTypeName = getResourceTypeName(defContext, resid);
		String resName = getResourceName(defContext, resid);

		Context skinContext = getSkinContext(defContext); // skin context
		String skinResName = getSkinResourceName(skinContext, resName);
		int skinResid = getResourceId(skinResName, resTypeName, skinContext);
		Log.d("skinResid " + skinResid);
		return skinContext.getResources().getDrawable(skinResid);
	}

	public static String getString(Context defContext, int resid) {

		if (defContext == null || resid == 0) {
			return "";
		}

		// no use external skin.
		if (!isUse(defContext)) {
			return defContext.getResources().getString(resid);
		}

		String resTypeName = getResourceTypeName(defContext, resid);
		String resName = getResourceName(defContext, resid);

		Context skinContext = getSkinContext(defContext);
		String skinResName = getSkinResourceName(skinContext, resName);
		int skinResid = getResourceId(skinResName, resTypeName, skinContext);

		return skinContext.getString(skinResid);
	}

	public static boolean isUse(Context defContext) {
		return SkinSingle.getInstance().isUse(defContext);
	}

	// get skinContext
	private static Context getSkinContext(Context defContext) {
		return SkinSingle.getInstance().getSkinContext(defContext);
	}

	/**
	 * Return a resource identifier for the given resource name.  A fully
	 * qualified resource name is of the form "package:type/entry".  The first
	 * two components (package and type) are optional if defType and
	 * defPackage, respectively, are specified here.
	 * 
	 * <p>Note: use of this function is discouraged.  It is much more
	 * efficient to retrieve resources by identifier than by name.
	 * 
	 * @param resName The name of the desired resource.
	 * @param resTypeName Optional default resource type to find, if "type/" is
	 *                not included in the name.  Can be null to require an
	 *                explicit type.
	 * 
	 * @return int The associated resource identifier.  Returns 0 if no such
	 *         resource was found.  (0 is not a valid resource ID.)
	 */
	private static int getResourceId(String resName, String resTypeName, Context skinContext) {
		return skinContext.getResources()
				.getIdentifier(resName, null, skinContext.getPackageName());
	}

	private static String getResourceTypeName(Context defContext, int resid) {
		if (defContext == null || resid == 0) {
			return "";
		}
		return defContext.getResources().getResourceTypeName(resid);
	}

	private static String getResourceName(Context defContext, int resid) {
		if (defContext == null || resid == 0) {
			return "";
		}
		return defContext.getResources().getResourceName(resid);
	}

	private static String getSkinResourceName(Context skinContext, String name) {
		String skinPackage = skinContext.getPackageName();
		String suf = name.substring(name.indexOf(":"), name.length());
		return skinPackage + suf;
	}

	/**
	 * skin setting
	 */
	// setting the skin packageName
	public static void settingSkin(Context context, String packageName) {
		Log.i("setting skin packageName " + packageName);
		SkinSingle.getInstance().settingSkin(context, packageName);
	}

	public static String getCurrentSkinPackageName(Context defContext) {
		return SkinSingle.getInstance().getCurrentSkinPackageName(defContext);
	}

	public static void registerObserver(DataSetObserver observer) {
		SkinSingle.getInstance().registerDataSetObserver(observer);
	}

	public static void unregisterDataSetObserver(DataSetObserver observer) {
		SkinSingle.getInstance().unregisterDataSetObserver(observer);
	}
}
