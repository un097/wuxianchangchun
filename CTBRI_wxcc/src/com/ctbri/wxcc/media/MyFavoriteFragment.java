package com.ctbri.wxcc.media;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctbri.wxcc.Constants;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.entity.CommonHolder;
import com.ctbri.wxcc.entity.MediaFavoriteVideo.VideoCollection;
import com.ctbri.wxcc.widget.SquareImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public abstract class MyFavoriteFragment<T, E> extends PtrCommonGrid<T, E> {

	protected int checked_color;
	private View mBtnContainer;
	private Button mBtnDelete, mBtnSelecteAll;
	private static final int SHOW_BUTTON_DEALAY = 400; // ms
	private ArrayList<E> mDeleteIds = new ArrayList<E>();
	public static final int ITEM_TYPE_GROUP = 1;
	public static final int ITEM_TYPE_ITEM = 0;

	// 是否 编辑中
	private boolean editing = false;

	@Override
	public void onAttach(Activity activity_) {
		super.onAttach(activity_);
		checked_color = getResources().getColor(R.color.black_60);
	}

	@Override
	protected boolean isInflateActionBar() {
		return true;
	}

	@Override
	protected int getLayoutResId() {
		return R.layout.media_favorite_gridview;
	}

	@Override
	protected String getListUrl() {
		return Constants.METHOD_VIDEO_MY_COLLECTION;
	}

	@Override
	protected abstract String getActionBarTitle();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);
		// 绑定 编辑按钮事件
		v.findViewById(R.id.action_bar_right_tv_btn).setOnClickListener(mEditBtnListener);
		mBtnContainer = v.findViewById(R.id.rl_button_container);

		mBtnDelete = (Button) v.findViewById(R.id.btn_delete);
		mBtnDelete.setOnClickListener(mBtnDeleteListener);

		mBtnSelecteAll = (Button) v.findViewById(R.id.btn_selecte_all);
		mBtnSelecteAll.setOnClickListener(mBtnSelecteListener);

		return v;
	}

	private OnClickListener mBtnSelecteListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (common_adapter != null) {
				int count = common_adapter.getCount();
				for (int i = 0; i < count; i++)
					checkItem(common_adapter.getItem(i));
				common_adapter.notifyDataSetChanged();
			}
		}
	};

	/**
	 * 该项被选中时，进行标示操作
	 * 
	 * @param e
	 */
	protected abstract void checkItem(E e);

	protected abstract boolean itemIsChecked(E e);

	protected abstract boolean itemIsChecked(E e, boolean checked);

	private OnClickListener mBtnDeleteListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (common_adapter != null) {
				int count = common_adapter.getCount();
				for (int i = 0; i < count; i++) {
					E vc = common_adapter.getItem(i);
					if (itemIsChecked(vc)) {
						mDeleteIds.add(vc);
					}
				}

				if (mDeleteIds.size() > 0) {
					common_adapter.removeAll(mDeleteIds);
					common_adapter.notifyDataSetChanged();
				}
				// 提交删除操作
				saveFavorite();
				// 更新删除按钮数量
				onCheckedItemChange();
			}
		}
	};
	/**
	 * 编辑按钮点击事件
	 */
	private OnClickListener mEditBtnListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			editing = !editing;
			((TextView) v).setText(editing ? R.string.txt_edit_success : R.string.txt_edit);

			if (editing) {
				mBtnContainer.setVisibility(View.VISIBLE);
				onCheckedItemChange();
				doEdit();
			} else {
				saveFavorite();
				mBtnContainer.setVisibility(View.GONE);
			}
		}
	};

	private void doEdit() {
		if (common_adapter != null) {
			mBtnContainer.postDelayed(new Runnable() {
				@Override
				public void run() {
					common_adapter.notifyDataSetChanged();
				}
			}, SHOW_BUTTON_DEALAY);

		}
	}

	// @Override
	// protected int getViewItemType(int position, E data) {
	// if (MediaUtils.FLAG_GROUP.equals(data.getCollection_type()))
	// return ITEM_TYPE_GROUP;
	// return ITEM_TYPE_VIDEO;
	// }

	@Override
	protected int getViewItemTypeCount() {
		return 2;
	}

	public abstract String getItemId(E e);

	private void saveFavorite() {

		if (common_adapter != null) {
			if (mDeleteIds.size() > 0) {
				StringBuilder sb_groups = new StringBuilder();
				StringBuilder sb_videos = new StringBuilder();
				for (E v : mDeleteIds) {
					int type = getViewItemType(-1, v);
					String itemId = getItemId(v);
					// 如果组别是 视频组
					if (type == ITEM_TYPE_GROUP) {
						sb_groups.append(itemId);
						sb_groups.append(",");
					} else {
						sb_videos.append(itemId);
						sb_videos.append(",");
					}
				}
				// 如果要删除的收藏视频组 id ，不为空
				if (sb_groups.length() > 0) {
					unFavroiteGroup(sb_groups.toString());
					// MediaUtils.unFavorite(sb_groups.toString(),
					// MediaUtils.FLAG_GROUP, (BaseActivity) activity);
				}
				// 如果要删除的收藏视频 id ，不为空
				if (sb_videos.length() > 0) {
					unFavorite(sb_videos.toString());
					// MediaUtils.unFavorite(sb_videos.toString(),
					// MediaUtils.FLAG_SINGLE_VIDEO, (BaseActivity) activity);
				}
				common_adapter.notifyDataSetChanged();
				mDeleteIds.clear();
			}
		}
	}

	public abstract String getItemName(E e);

	public abstract String getItemPic(E e);

	public abstract int getItemGroupLayout();

	public abstract int getItemLayout();

	public abstract void unFavroiteGroup(String ids);

	public abstract void unFavorite(String ids);

	@Override
	protected boolean initHeaderDetail(View parent, GridView lv_list, LayoutInflater inflater) {
		return false;
	}

	@Override
	protected View getListItemView(int position, View convertView, ViewGroup parent, E data, ImageLoader imgloader, DisplayImageOptions dio) {
		CommonHolder holder = null;
		if (convertView != null) {
			holder = (CommonHolder) convertView.getTag();
		} else {
			holder = new CommonHolder();
			int type = getViewItemType(position, data);
			if (type == ITEM_TYPE_ITEM)
				convertView = super.inflater.inflate(getItemLayout(), parent, false);
			else
				convertView = super.inflater.inflate(getItemGroupLayout(), parent, false);

			convertView.setTag(holder);

			holder.iv = (ImageView) convertView.findViewById(R.id.iv_favorite);
			// 选中时的遮罩
			holder.v = convertView.findViewById(R.id.frame_favorite);
			holder.v.setOnClickListener(mFrameListener);

			holder.tv = (TextView) convertView.findViewById(R.id.tv_favorite_title);

			holder.chk = (CheckBox) convertView.findViewById(R.id.chk_favorite);
			holder.chk.setOnCheckedChangeListener(mFavoriteListener);

		}
		holder.chk.setTag(data);
		holder.v.setTag(holder);
		holder.tv.setText(getItemName(data));
		// 当前是否处于编辑状态
		holder.v.setVisibility(editing ? View.VISIBLE : View.GONE);
		boolean isChecked = itemIsChecked(data);
		String picUrl = getItemPic(data);
		if (!picUrl.equals(holder.iv.getTag())) {
			holder.iv.setTag(picUrl);
			imgloader.displayImage(picUrl, holder.iv, dio);
		}

		if (holder.chk.isChecked() == isChecked)
			changeFrameBg(holder.v, isChecked);
		else
			holder.chk.setChecked(isChecked);

		return convertView;
	}

	/**
	 * 透明图片的点击事件
	 */
	private OnClickListener mFrameListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			((CommonHolder) v.getTag()).chk.toggle();
		}
	};
	/**
	 * Checkbox 选中事件
	 */
	private OnCheckedChangeListener mFavoriteListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			changeFrameBg(((View) buttonView.getParent()), isChecked);
			itemIsChecked((E) buttonView.getTag(), isChecked);
			onCheckedItemChange();
		}
	};

	/**
	 * 更改 FrameView 的背影图片
	 * 
	 * @param frame
	 * @param isChecked
	 */
	protected void changeFrameBg(View frame, boolean isChecked) {
		if (!isChecked) {
			frame.setBackgroundColor(checked_color);
		} else {
			frame.setBackgroundDrawable(null);
		}
	}

	private void onCheckedItemChange() {
		int checkedCount = 0;
		if (common_adapter != null) {
			int count = common_adapter.getCount();
			for (int i = 0; i < count; i++)
				if (itemIsChecked(common_adapter.getItem(i)))
					checkedCount++;
		}
		mBtnDelete.setText(getString(R.string.tip_delete_my_favorite, checkedCount));
	}

	@Override
	protected void onItemClick(AdapterView<?> parent, View view, int position, long id, E entity) {

		// if (MediaUtils.FLAG_GROUP.equals(entity.getCollection_type())) {
		// Intent toListIntent = new Intent(activity, VideoListActivity.class);
		// toListIntent.putExtra("group_id", entity.getCollection_id());
		// toListIntent.putExtra("type_id", VodVideoListFragmet.TYPE_VOD);
		// toListIntent.putExtra("title", entity.getCollection_name());
		// startActivity(toListIntent);
		// } else {
		// Intent toDetail = new Intent(activity, MediaPlayerActivity.class);
		// toDetail.putExtra("type_id", VodVideoListFragmet.TYPE_VOD);
		// toDetail.putExtra("vod_id", entity.getCollection_id());
		// startActivity(toDetail);
		// }
	}

	@Override
	protected String getAnalyticsTitle() {
		return null;
	}

	@Override
	protected boolean isEnabledAnalytics() {
		return false;
	}

}
