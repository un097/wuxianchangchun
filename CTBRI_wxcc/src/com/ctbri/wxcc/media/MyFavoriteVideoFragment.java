package com.ctbri.wxcc.media;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctbri.wxcc.Constants;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseActivity;
import com.ctbri.wxcc.entity.CommonHolder;
import com.ctbri.wxcc.entity.MediaFavoriteVideo;
import com.ctbri.wxcc.entity.MediaFavoriteVideo.VideoCollection;
import com.ctbri.wxcc.entity.MediaVodVideoBean.VodGroup;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MyFavoriteVideoFragment extends PtrCommonGrid<MediaFavoriteVideo, VideoCollection> {

	private int checked_color;
	private View mBtnContainer;
	private Button mBtnDelete, mBtnSelecteAll;
	private static final int SHOW_BUTTON_DEALAY = 400; // ms
	private ArrayList<VideoCollection> mDeleteIds = new ArrayList<VideoCollection>();
	private static final int ITEM_TYPE_GROUP = 1;
	private static final int ITEM_TYPE_VIDEO = 0;

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
	protected String getActionBarTitle() {
		return getString(R.string.title_myvod_favorite);
	}

	@Override
	protected Class<MediaFavoriteVideo> getGsonClass() {
		return MediaFavoriteVideo.class;
	}

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
					common_adapter.getItem(i).checked = true;
				common_adapter.notifyDataSetChanged();
			}
		}
	};
	private OnClickListener mBtnDeleteListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (common_adapter != null) {
				int count = common_adapter.getCount();
				for (int i = 0; i < count; i++) {
					VideoCollection vc = common_adapter.getItem(i);
					if (vc.checked) {
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

	@Override
	protected int getViewItemType(int position, VideoCollection data) {
		if (MediaUtils.FLAG_GROUP.equals(data.getCollection_type()))
			return ITEM_TYPE_GROUP;
		return ITEM_TYPE_VIDEO;
	}

	@Override
	protected int getViewItemTypeCount() {
		return 2;
	}

	private void saveFavorite() {

		if (common_adapter != null) {
			if (mDeleteIds.size() > 0) {
				StringBuilder sb_groups = new StringBuilder();
				StringBuilder sb_videos = new StringBuilder();
				for (VideoCollection v : mDeleteIds) {
					// 如果组别是 视频组
					if (MediaUtils.FLAG_GROUP.equals(v.getCollection_type())) {
						sb_groups.append(v.getCollection_id());
						sb_groups.append(",");
					} else {
						sb_videos.append(v.getCollection_id());
						sb_videos.append(",");
					}
				}
				// 如果要删除的收藏视频组 id ，不为空
				if (sb_groups.length() > 0) {
					MediaUtils.unFavorite(sb_groups.toString(), MediaUtils.FLAG_GROUP, (BaseActivity) activity);
				}
				// 如果要删除的收藏视频 id ，不为空
				if (sb_videos.length() > 0) {
					MediaUtils.unFavorite(sb_videos.toString(), MediaUtils.FLAG_SINGLE_VIDEO, (BaseActivity) activity);
				}
				common_adapter.notifyDataSetChanged();
				mDeleteIds.clear();
			}
		}
	}

	@Override
	protected List<VideoCollection> getEntitys(MediaFavoriteVideo bean) {
		if (bean != null && bean.getData() != null)
			return bean.getData().getCollection_list();

		return null;
	}

	@Override
	protected boolean isEnd(MediaFavoriteVideo bean) {
		return bean.getData().getIs_end() == 1;
	}

	@Override
	protected boolean initHeaderDetail(View parent, GridView lv_list, LayoutInflater inflater) {
		return false;
	}

	@Override
	protected View getListItemView(int position, View convertView, ViewGroup parent, VideoCollection data, ImageLoader imgloader, DisplayImageOptions dio) {
		CommonHolder holder = null;
		if (convertView != null) {
			holder = (CommonHolder) convertView.getTag();
		} else {
			holder = new CommonHolder();
			int type = getViewItemType(position, data);
			if (type == ITEM_TYPE_VIDEO)
				convertView = super.inflater.inflate(R.layout.media_video_favorite_item, parent, false);
			else
				convertView = super.inflater.inflate(R.layout.media_video_favorite_group_item, parent, false);

			convertView.setTag(holder);

			holder.iv = (ImageView) convertView.findViewById(R.id.iv_favorite);

			holder.v = convertView.findViewById(R.id.frame_favorite);
			holder.v.setOnClickListener(mFrameListener);

			holder.tv = (TextView) convertView.findViewById(R.id.tv_favorite_title);

			holder.chk = (CheckBox) convertView.findViewById(R.id.chk_favorite);
			holder.chk.setOnCheckedChangeListener(mFavoriteListener);

		}
		holder.chk.setTag(data);
		holder.v.setTag(holder);
		holder.tv.setText(data.getCollection_name());
		// 当前是否处于编辑状态
		holder.v.setVisibility(editing ? View.VISIBLE : View.GONE);
		String picUrl = data.getPic_url().trim();
		if (!picUrl.equals(holder.iv.getTag())) {
			holder.iv.setTag(picUrl);
			imgloader.displayImage(picUrl, holder.iv, dio);
		}
		holder.chk.setChecked(data.checked);

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
			((VideoCollection) buttonView.getTag()).checked = isChecked;
			onCheckedItemChange();
		}
	};

	/**
	 * 更改 FrameView 的背影图片
	 * 
	 * @param frame
	 * @param isChecked
	 */
	private void changeFrameBg(View frame, boolean isChecked) {
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
				if (common_adapter.getItem(i).checked)
					checkedCount++;
		}
		mBtnDelete.setText(getString(R.string.tip_delete_my_favorite, checkedCount));
	}

	@Override
	protected void onItemClick(AdapterView<?> parent, View view, int position, long id, VideoCollection entity) {

		if (MediaUtils.FLAG_GROUP.equals(entity.getCollection_type())) {
			Intent toListIntent = new Intent(activity, VideoListActivity.class);
			toListIntent.putExtra("group_id", entity.getCollection_id());
			toListIntent.putExtra("type_id", VodVideoListFragmet.TYPE_VOD);
			toListIntent.putExtra("title", entity.getCollection_name());
			startActivity(toListIntent);
		} else {
			Intent toDetail = new Intent(activity, MediaPlayerActivity.class);
			toDetail.putExtra("type_id", VodVideoListFragmet.TYPE_VOD);
			toDetail.putExtra("vod_id", entity.getCollection_id());
			startActivity(toDetail);
		}
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
