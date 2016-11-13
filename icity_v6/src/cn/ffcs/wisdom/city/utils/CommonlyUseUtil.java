package cn.ffcs.wisdom.city.utils;

import java.io.File;

import android.content.Context;
import cn.ffcs.wisdom.city.entity.Common;
import cn.ffcs.wisdom.tools.FileUtils;

/**
 * <p>Title:  主页常用工具类                      </p>
 * <p>Description:                     </p>
 * <p>@author: zhangws                 </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-3-7           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class CommonlyUseUtil {

	public static String COMMONLY_USE_FILE = "commonUse";

	/**
	 * 保存主页常用
	 * @param context
	 * @param entity
	 */
	public static void saveCommonlyUse(final Context context, final Common entity,
			String cityCode) {
		if (context != null && entity != null) {
			removeCommonlyUse(context, cityCode);
			FileUtils.write(context, COMMONLY_USE_FILE + cityCode, entity);
		}
	}

	/**
	 * 删除主页常用
	 * @param ctx
	 */
	public static void removeCommonlyUse(Context ctx, String cityCode) {
		File commonFile = ctx.getFileStreamPath(COMMONLY_USE_FILE + cityCode);
		if (commonFile != null && commonFile.exists()) {
			commonFile.delete();
		}
	}

	/**
	 * 获取主页常用
	 * @param context
	 * @return
	 */
	public static Common readCommonlyUse(Context context, String cityCode) {
		Common entity = (Common) FileUtils
				.read(context, COMMONLY_USE_FILE + cityCode);

		if (entity == null) {
			return new Common();
		}
		return entity;
	}
}
