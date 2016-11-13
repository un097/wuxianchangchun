package cn.ffcs.wisdom.city.datamgr;

import android.content.Context;
import cn.ffcs.wisdom.base.DataManager;
import cn.ffcs.wisdom.city.entity.Common;
import cn.ffcs.wisdom.city.utils.CommonlyUseUtil;

/**
 * <p>Title:  常用管理                   </p>
 * <p>Description:                     </p>
 * <p>@author: zhangws                 </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-3-8           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class CommonlyUseMgr extends DataManager {

	private static CommonlyUseMgr mInstance = new CommonlyUseMgr();
	
	private Common common;
	static final Object sInstanceSync = new Object();
	
	public static CommonlyUseMgr getInstance() {
		synchronized (sInstanceSync) {
			if (mInstance == null)
				mInstance = new CommonlyUseMgr();
		}
		return mInstance;
	}

	/**
	 * 获取常用
	 * @param context
	 * @return
	 */
	public Common getCommUse(Context context,String cityCode){
		if(common!=null){
			return common;
		}
		return CommonlyUseUtil.readCommonlyUse(context,cityCode);
	}
	
	/**
	 * 刷新常用
	 * @param context
	 * @param entity
	 */
	public void refreshCommUse(Context context,Common entity,String cityCode){
		CommonlyUseUtil.saveCommonlyUse(context, entity,cityCode);
	}
}
