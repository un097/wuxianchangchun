package cn.ffcs.surfingscene.tools;

import java.util.Comparator;

import android.content.Context;
import cn.ffcs.surfingscene.sqlite.RoadCollectService;

import com.ffcs.surfingscene.entity.GlobalEyeEntity;

/**
 * <p>Title:                           </p>
 * <p>Description:                     </p>
 * <p>@author: zhangwsh                </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-11-5           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class ComparatorEye implements Comparator<GlobalEyeEntity> {

	private Context mContext;
	private String phone;

	public ComparatorEye(Context context, String phone) {
		this.mContext = context;
		this.phone = phone;
	}

	@Override
	public int compare(GlobalEyeEntity o1, GlobalEyeEntity o2) {
		boolean isCollect1 = RoadCollectService.getInstance(mContext).isCollect(phone,
				o1.getGeyeId());
		boolean isCollect2 = RoadCollectService.getInstance(mContext).isCollect(phone,
				o2.getGeyeId());
		if (isCollect1 && isCollect2) {
			return 0;
		} else if (isCollect1 && !isCollect2) {
			return -1;
		} else if (!isCollect1 && isCollect2) {
			return 1;
		} else if (!isCollect1 && !isCollect2) {
			return 0;
		} else {
			return 0;
		}
	}
}
