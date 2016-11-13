package cn.ffcs.surfingscene.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.ffcs.surfingscene.R;
import cn.ffcs.surfingscene.datamgr.FavoriteDataMgr;
import cn.ffcs.surfingscene.sqlite.GlobalEye;
import cn.ffcs.surfingscene.sqlite.GlobalEyeUtil;
import cn.ffcs.surfingscene.tools.GloImageLoader;
import cn.ffcs.surfingscene.tools.VideoPlayerTool;

import com.ffcs.surfingscene.entity.GlobalEyeEntity;

/**
 * <p>Title:  适用于精品，收藏列表排列</p>
 * <p>Description: </p>
 * <p>@author: caijj                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-6-17             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class ListViewFavoriteAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<GlobalEye> mData = new ArrayList<GlobalEye>();
	private GloImageLoader loader;
	private Activity mActivity;
	private Context mContext;

	public ListViewFavoriteAdapter(Activity activity) {
		this.mActivity = activity;
		this.mContext = activity.getApplicationContext();
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		loader = new GloImageLoader(mContext);
		loader.setDefaultFailImage(R.drawable.glo_city_icon_default);
	}

	public void setData(List<GlobalEye> data) {
		if (data == null || data.size() == 0) {
			mData.clear();
		}else {
			synchronized (mData) {
				mData.clear();
				int size = data.size();
				for (int i = 0; i < size; i++) {
					mData.add(data.get(i));
				}
			}
		}
		
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public GlobalEye getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		if (view == null) {
			view = mInflater.inflate(R.layout.glo_listview_item_competitive, null);
		}

		if (position % 2 == 0) {
			view.setBackgroundColor(Color.parseColor("#f1f1f1"));
		} else {
			view.setBackgroundColor(Color.parseColor("#fdfdfd"));
		}

		final GlobalEye eye = getItem(position);
		TextView name = (TextView) view.findViewById(R.id.glo_title);
		TextView descript = (TextView) view.findViewById(R.id.descript);
		ImageView favorite = (ImageView) view.findViewById(R.id.favorite);
		ImageView photo = (ImageView) view.findViewById(R.id.photo);

		name.setText(eye.getPuName());
		descript.setText(eye.getIntro());

		favorite.setTag(eye);

		boolean isFavorite = FavoriteDataMgr.getInstance().isFavorite(mContext, eye.getGeyeId());
		if (isFavorite) {
			favorite.getDrawable().setLevel(3);
		} else {
			favorite.getDrawable().setLevel(2);
		}

		favorite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ImageView favorite = ((ImageView) v);
				int level = favorite.getDrawable().getLevel();
				if (level == 2) { // 收藏
					FavoriteDataMgr.getInstance().doFavorite(mContext, (GlobalEye) v.getTag(), 0);
					favorite.getDrawable().setLevel(3);
				} else { // 取消收藏
					FavoriteDataMgr.getInstance().doFavorite(mContext, (GlobalEye) v.getTag(), 1);
					favorite.getDrawable().setLevel(2);
					mData.remove(eye);
					notifyDataSetChanged();
				}

			}
		});
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				GlobalEyeEntity entity = GlobalEyeUtil.toGloBalEyeEntity(eye);
				if (entity != null) {
					VideoPlayerTool.playVideo(mActivity, entity);
				}
			}
		});
		loader.loadUrl(photo, eye.getImageUrl());
		return view;
	}
}
