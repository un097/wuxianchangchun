package cn.ffcs.surfingscene.datamgr;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import cn.ffcs.surfingscene.sqlite.GlobalEye;
import cn.ffcs.surfingscene.sqlite.GlobalEyeUtil;
import cn.ffcs.surfingscene.sqlite.GlobalEyesService;
import cn.ffcs.wisdom.base.DataManager;

import com.ffcs.surfingscene.entity.GlobalEyeEntity;

/**
 * <p>Title:  收藏数据管理</p>
 * <p>Description: 
 * 仅缓存当前城市数据
 * 1、切换城市，请清除缓存
 * 2、退出景点视频，请清除缓存
 * </p>
 * <p>@author: caijj                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-6-18             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class FavoriteDataMgr extends DataManager {

	private static FavoriteDataMgr mInstance;

	private List<GlobalEye> favorites;

	private boolean dataChange;

	private FavoriteDataMgr() {
		favorites = new ArrayList<GlobalEye>(5);
		dataChange = true;

		GloCityMgr.getInstance().registerDataSetObserver(new DataSetObserver() {
			@Override
			public void onChanged() {
				dataChange();
			}
		});
	}

	public static FavoriteDataMgr getInstance() {
		if (mInstance == null)
			mInstance = new FavoriteDataMgr();
		return mInstance;
	}

	public void dataChange() {
		synchronized (favorites) {
			dataChange = true;
			favorites.clear();
		}
	}

	public boolean isFavorite(Context context, Integer geyeId) {
		return GlobalEyesService.getInstance(context).isFavorite(geyeId);
	}

	public void doFavorite(Context context, GlobalEye eye, Integer favorite) {
		if (eye != null) {
			GlobalEyesService.getInstance(context).doFavorite(eye, favorite);

			dataChange();
		}
	}

	public void doFavorite(Context context, GlobalEyeEntity eye, Integer favorite) {
		if (eye != null) {
			GlobalEye data = GlobalEyeUtil.toGloBalEye(eye);
			String tyjxCode = GloCityMgr.getInstance().getTyjxCode();
			data.setTyjxCode(tyjxCode);
			GlobalEyesService.getInstance(context).doFavorite(data, favorite);

			dataChange();
		}
	}

	public List<GlobalEye> getFavorites(Context context, String tyjxCode) {
		if (dataChange) {
			refresh(context, tyjxCode);
		}
		return favorites;
	}

	public void refresh(Context context, String tyjxCode) {
		synchronized (favorites) {
			List<GlobalEye> data = loadData(context, tyjxCode);
			favorites.addAll(data);
			dataChange = false;
		}
	}

	public void clear() {
		synchronized (favorites) {
			dataChange = true;
			favorites.clear();
		}
	}

	private List<GlobalEye> loadData(Context context, String tyjxCode) {
		return GlobalEyesService.getInstance(context).queryFavoriteTyjxCode(tyjxCode);
	}

}
