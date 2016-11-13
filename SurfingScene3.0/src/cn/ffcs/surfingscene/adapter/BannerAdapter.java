package cn.ffcs.surfingscene.adapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import cn.ffcs.surfingscene.R;
import cn.ffcs.surfingscene.activity.GloBannerListActivity;
import cn.ffcs.surfingscene.common.Key;
import cn.ffcs.surfingscene.tools.GloImageLoader;

import com.ffcs.surfingscene.entity.ActionEntity;

/**
 * <p>Title: 当前城市广告适配器         </p>
 * <p>Description: 
 * </p>
 * <p>@author: Leo                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-6-25             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class BannerAdapter extends PagerAdapter {
	private Activity mActivity;
	private List<ActionEntity> mList;
	private final LayoutInflater mInflater;
	private List<View> mViewList = new ArrayList<View>();
	private GloImageLoader loader;
	private int count = 0;
	private Context mContext;

	public BannerAdapter(Activity activity, List<ActionEntity> list) {
		this.mContext = activity.getApplication();
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mActivity = activity;
		this.mList = list;
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				View view = mInflater.inflate(R.layout.glo_city_viewpager_item, null);
				mViewList.add(view);
			}
			count = list.size();
		} else {
			list = new ArrayList<ActionEntity>();
		}

		loader = new GloImageLoader(mContext);
		loader.setDefaultFailImage(R.drawable.glo_city_banner_default);
	}

	@Override
	public int getCount() {
		return count + 1;
	}

	@Override
	public Object instantiateItem(View container, int position) {
		if (position >= mViewList.size()) {
			if (mViewList.size() != 0) {
				int newPosition = position % mViewList.size();
				position = newPosition;
				count++;
			}
		}
		if (position < 0) {
			position = -position;
			count--;
		}
		try {
			View view = mViewList.get(position);
			ActionEntity entity = mList.get(position);
			String url = entity.getImgUrl();
			ImageView imageView = (ImageView) view.findViewById(R.id.glo_city_banner_img);
			imageView.setOnClickListener(new OnBannerClick(entity));
			loader.loadUrl(imageView, url);
			((ViewPager) container).addView(mViewList.get(position), 0);
		} catch (Exception e) {
		}
		if (mViewList.size() > 0) {
			return mViewList.get(position);
		}
		return null;
	}

	class OnBannerClick implements OnClickListener {
		private ActionEntity mActionEntity;

		public OnBannerClick(ActionEntity entity) {
			this.mActionEntity = entity;
		}

		@Override
		public void onClick(View v) {
			if (mActionEntity != null) {
				Intent intent = new Intent(mActivity, GloBannerListActivity.class);
				intent.putExtra(Key.K_BANNER_LIST, (Serializable) mActionEntity);
				intent.putExtra(Key.K_RETURN_TITLE, mActivity.getString(R.string.glo_app_title));
				mActivity.startActivity(intent);
			}
		}

	}

	@Override
	public boolean isViewFromObject(View view, Object obj) {
		return view == obj;
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		if (position >= mViewList.size()) {
			if (mViewList.size() != 0) {
				int newPosition = position % mViewList.size();
				position = newPosition;
			}

		}
		if (position < 0) {
			position = -position;
		}
	}

	@Override
	public int getItemPosition(Object object) {
		return super.getItemPosition(object);
	}

}
