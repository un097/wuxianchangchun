package cn.ffcs.surfingscene.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.ffcs.surfingscene.R;
import cn.ffcs.surfingscene.datamgr.FavoriteDataMgr;
import cn.ffcs.surfingscene.tools.GloImageLoader;
import cn.ffcs.surfingscene.tools.VideoPlayerTool;

import com.ffcs.surfingscene.entity.ActionEntity;
import com.ffcs.surfingscene.entity.GlobalEyeEntity;

/**
 * <p>Title: 城市景点适配器         </p>
 * <p>Description: 
 * </p>
 * <p>@author: Leo                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-6-18             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class GloCityExpandAdapter extends BaseExpandableListAdapter {
	private LayoutInflater mInflater;
	private Context mContext;
	private Activity mActivity;
	private List<ActionEntity> mParentActionEntity = new ArrayList<ActionEntity>();
	private GloImageLoader loader;

	public GloCityExpandAdapter(Activity activity) {
		this.mActivity = activity;
		this.mContext = activity.getApplication();
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		loader = new GloImageLoader(mContext);
		loader.setDefaultFailImage(R.drawable.glo_city_icon_default);
	}

	public void setData(List<ActionEntity> data) {
		if (data != null && data.size() > 0) {
			synchronized (mParentActionEntity) {
				mParentActionEntity.clear();
				mParentActionEntity.addAll(data);
			}
		} else {
			synchronized (mParentActionEntity) {
				mParentActionEntity.clear();
			}
		}

	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		ActionEntity groupEntity = mParentActionEntity.get(groupPosition);
		if (groupEntity != null) {
			List<GlobalEyeEntity> childEntityList = groupEntity.getGeyes();
			if (childEntityList != null) {
				return childEntityList.get(childPosition);
			}
		}
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
			View convertView, ViewGroup parent) {
		ChildHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.glo_expandlistview_child, null);
			holder = new ChildHolder();
			holder.mChildIcon = (ImageView) convertView.findViewById(R.id.glo_city_child_icon);
			holder.mChildTitle = (TextView) convertView.findViewById(R.id.glo_city_child_title);
			holder.mChildContent = (TextView) convertView.findViewById(R.id.glo_city_child_content);
			holder.mCollect = (ImageView) convertView.findViewById(R.id.glo_city_child_collect);
			convertView.setTag(holder);
		} else {
			holder = (ChildHolder) convertView.getTag();
		}
		final GlobalEyeEntity childEntity = (GlobalEyeEntity) getChild(groupPosition, childPosition);
		String url = childEntity.getImgUrl();
		loader.loadUrl(holder.mChildIcon, url);
		holder.mChildTitle.setText(childEntity.getPuName());
		holder.mChildContent.setText(childEntity.getIntro());
		boolean isFavorite = FavoriteDataMgr.getInstance().isFavorite(mContext,
				childEntity.getGeyeId());
		holder.mChildContent.setText(childEntity.getIntro());
		holder.mCollect.setTag(childEntity);
		if (isFavorite) {
			holder.mCollect.getDrawable().setLevel(3);
		} else {
			holder.mCollect.getDrawable().setLevel(2);
		}
		holder.mCollect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ImageView favorite = ((ImageView) v);
				int level = favorite.getDrawable().getLevel();
				if (level == 2) { // 收藏
					FavoriteDataMgr.getInstance().doFavorite(mContext,
							(GlobalEyeEntity) v.getTag(), 0);
					favorite.getDrawable().setLevel(3);
				} else { // 取消收藏
					FavoriteDataMgr.getInstance().doFavorite(mContext,
							(GlobalEyeEntity) v.getTag(), 1);
					favorite.getDrawable().setLevel(2);
				}
			}
		});
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				VideoPlayerTool.playVideo(mActivity, childEntity);

			}
		});
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if (mParentActionEntity != null && mParentActionEntity.size() > 0) {
			ActionEntity groupEntity = mParentActionEntity.get(groupPosition);
			if (groupEntity != null) {
				List<GlobalEyeEntity> childEntityList = groupEntity.getGeyes();
				if (childEntityList != null) {
					return childEntityList.size();
				}
			}
		}

		return 0;
	}

	@Override
	public Object getGroup(int groupPosition) {
		if (mParentActionEntity != null && mParentActionEntity.size() > 0) {
			return mParentActionEntity.get(groupPosition);
		}
		return null;

	}

	@Override
	public int getGroupCount() {
		if (mParentActionEntity != null) {
			return mParentActionEntity.size();
		}
		return 0;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
			ViewGroup parent) {
		ParentHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.glo_expandlistview_parent, null);
			holder = new ParentHolder();
			holder.mParentIcon = (ImageView) convertView.findViewById(R.id.glo_city_parent_icon);
			holder.mParentTitle = (TextView) convertView.findViewById(R.id.glo_city_parent_title);
			holder.mParentCount = (TextView) convertView
					.findViewById(R.id.glo_city_parent_count_text);
			holder.mParentContent = (TextView) convertView
					.findViewById(R.id.glo_city_parent_content);
			holder.mParentIndicator = (ImageView) convertView.findViewById(R.id.glo_city_indicator);
			convertView.setTag(holder);
		} else {
			holder = (ParentHolder) convertView.getTag();
		}

		if (isExpanded) {
			holder.mParentIndicator.setBackgroundResource(R.drawable.glo_city_arrow_up);
		} else {
			holder.mParentIndicator.setBackgroundResource(R.drawable.glo_city_arrow_down);
		}

		// 数据填充
		ActionEntity groupEntity = (ActionEntity) getGroup(groupPosition);
		if (groupEntity != null) {
			if (groupEntity.getGeyes() != null) {
				int size = groupEntity.getGeyes().size();
				holder.mParentCount.setText(String.valueOf(size));
			}
			String url = groupEntity.getImgUrl();
			loader.loadUrl(holder.mParentIcon, url);
			holder.mParentTitle.setText(groupEntity.getActionName());
			holder.mParentContent.setText(groupEntity.getIntro());
		}
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	// 标题标签项
	final static class ParentHolder {
		ImageView mParentIcon;// 图片
		TextView mParentTitle;// 标题
		TextView mParentCount;// 计数
		TextView mParentContent;// 内容
		ImageView mParentIndicator;// 展开标识符
	}

	// 子菜单项
	final static class ChildHolder {
		ImageView mChildIcon;// 图片
		TextView mChildTitle;// 标题
		TextView mChildContent;// 内容
		ImageView mCollect;// 收藏
	}
}
