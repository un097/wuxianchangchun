package cn.ffcs.surfingscene.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.ffcs.surfingscene.R;
import cn.ffcs.surfingscene.datamgr.FavoriteDataMgr;
import cn.ffcs.surfingscene.sqlite.GlobalEyesService;
import cn.ffcs.surfingscene.tools.GloImageLoader;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.CommonUtils;

import com.ffcs.surfingscene.entity.GlobalEyeEntity;

/**
 * <p>Title: 精品九宫格布局</p>
 * <p>Description: </p>
 * <p>@author: caijj                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-6-17             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class CompGridViewAdapter extends BaseAdapter {

	LayoutInflater mInflater;
	List<GlobalEyeEntity> mData = new ArrayList<GlobalEyeEntity>(4);
	static final float SCALE = 1.5f;
	int width;
	int height;

	Context mContext;
	GloImageLoader loader;

	public CompGridViewAdapter(Context context) {
		mContext = context;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		int screenWidth = AppHelper.getScreenWidth(context);

		int spacing = CommonUtils.convertDipToPx(context, 32);

		width = (screenWidth - spacing) / 2;
		height = (int) (width / SCALE);

		loader = new GloImageLoader(context);
		loader.setDefaultFailImage(R.drawable.glo_comp_default);
	}

	public void setData(List<GlobalEyeEntity> data) {
		if (data == null || data.size() == 0) {
			return;
		}

		synchronized (mData) {
			mData.clear();
			int size = data.size();
			size = size > 4 ? 4 : size;
			for (int i = 0; i < size; i++) {
				mData.add(data.get(i));
			}
		}
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public GlobalEyeEntity getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if (view == null) {
			view = mInflater.inflate(R.layout.glo_gridview_item_competitive, null);
		}

		AbsListView.LayoutParams params = new AbsListView.LayoutParams(width, height);
		params.width = width;
		params.height = height;
		view.setLayoutParams(params);

		GlobalEyeEntity eye = getItem(position);

		ImageView photo = (ImageView) view.findViewById(R.id.photo);
		TextView title = (TextView) view.findViewById(R.id.glo_title);
		ImageView favorite = (ImageView) view.findViewById(R.id.favorite);

		title.setSelected(true);

		favorite.setTag(eye);

		boolean isFavorite = GlobalEyesService.getInstance(mContext).isFavorite(eye.getGeyeId());
		if (isFavorite) {
			favorite.getDrawable().setLevel(3);
		} else {
			favorite.getDrawable().setLevel(1);
		}

		favorite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ImageView favorite = ((ImageView) v);
				int level = favorite.getDrawable().getLevel();
				if (level == 1) { // 收藏
					FavoriteDataMgr.getInstance().doFavorite(mContext, (GlobalEyeEntity) v.getTag(), 0);
					favorite.getDrawable().setLevel(3);
				} else { // 取消收藏
					FavoriteDataMgr.getInstance().doFavorite(mContext, (GlobalEyeEntity) v.getTag(), 1);
					favorite.getDrawable().setLevel(1);
				}

			}
		});

		title.setText(eye.getPuName());
		loader.loadUrl(photo, eye.getImgUrl());

		return view;
	}

}
