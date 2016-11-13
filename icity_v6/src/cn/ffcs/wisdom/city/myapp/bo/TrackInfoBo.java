package cn.ffcs.wisdom.city.myapp.bo;

import java.util.List;

import android.content.Context;
import cn.ffcs.wisdom.base.BaseBo;
import cn.ffcs.wisdom.city.sqlite.model.MenuItem;
import cn.ffcs.wisdom.city.sqlite.model.TrackInfo;
import cn.ffcs.wisdom.city.sqlite.service.TrackInfoService;

/**
 * <p>Title: 最近访问逻辑类  </p>
 * <p>Description: 
 * 最近访问逻辑类  
 * </p>
 * <p>@author: Eric.wsd                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-8-15             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class TrackInfoBo extends BaseBo {
	private static TrackInfoBo mTrackInfoBo;
	static Object syncObject = new Object();
	private Context mContext;

	private TrackInfoBo(Context context) {
		super(null);
		this.mContext = context;
	}

	public static TrackInfoBo newInstance(Context context) {
		synchronized (syncObject) {
			if (mTrackInfoBo == null) {
				mTrackInfoBo = new TrackInfoBo(context);
			}
		}

		return mTrackInfoBo;
	}

	/**
	 * 根据城市编码获取相应的访问记录
	 * @param cityCode
	 * @return
	 */
	public List<TrackInfo> queryAll(String cityCode) {
		return TrackInfoService.getInstance(mContext).findAllByCityCode(cityCode);
	}

	/**
	 * 清除所有历史记录
	 */
	public void clearAll() {
		TrackInfoService.getInstance(mContext).clearAll();
	}

	/**
	 * 保存记录到DB
	 * @param item
	 */
	public void saveOrUpdate(MenuItem item) {
		TrackInfoService.getInstance(mContext).saveOrUpdate(TrackInfo.converter(item));
	}
}
