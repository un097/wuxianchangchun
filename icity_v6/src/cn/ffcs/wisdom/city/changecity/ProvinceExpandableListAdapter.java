package cn.ffcs.wisdom.city.changecity;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import cn.ffcs.wisdom.city.changecity.ProvinceExpandableListView.ProvinceHeaderAdapter;
import cn.ffcs.wisdom.city.entity.CityEntity;
import cn.ffcs.wisdom.city.entity.ProvinceEntity;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.StringUtil;

/**
 * <p>Title:自定义省份列表数据填充适配器                            </p>
 * <p>Description:                     </p>
 * <p>@author: liaodl                  </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-3-1            </p>
 * <p>@author:                         </p> 
 * <p>Update Time: 2013-3-6            </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class ProvinceExpandableListAdapter extends BaseExpandableListAdapter implements
		ProvinceHeaderAdapter, SectionIndexer {

	private ProvinceExpandableListView listView;
	private List<ProvinceEntity> groupArray;
	private List<List<CityEntity>> childArray;
	private Context context;

	private boolean showDeleteIcon;

	public ProvinceExpandableListAdapter(Context context, ProvinceExpandableListView listView,
			List<ProvinceEntity> groups, List<List<CityEntity>> childs) {
		this.context = context;
		this.listView = listView;
		this.groupArray = groups;
		this.childArray = childs;
	}

	@Override
	public int getProvinceHeaderState(int groupPosition, int childPosition) {
		if (groupPosition == -1) {
			groupPosition = 0;
		}
		final int childCount = getChildrenCount(groupPosition);
		if (childPosition == childCount - 1) {
			return PINNED_HEADER_PUSHED_UP;
		} else if (childPosition == -1 && !listView.isGroupExpanded(groupPosition)) {
			return PINNED_HEADER_GONE;
		} else {
			return PINNED_HEADER_VISIBLE;
		}
	}

	@Override
	public void configureProvinceHeader(View header, int groupPosition, int childPosition, int alpha) {
		try {
			ProvinceEntity groupData = (ProvinceEntity) this.getGroup(groupPosition);
			((TextView) header.findViewById(R.id.groupto)).setText(groupData.getProvinceName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private HashMap<Integer, Integer> groupStatusMap = new HashMap<Integer, Integer>();

	@Override
	public void setGroupClickStatus(int groupPosition, int status) {
		groupStatusMap.put(groupPosition, status);
	}

	@Override
	public int getGroupClickStatus(int groupPosition) {
		if (groupStatusMap.containsKey(groupPosition)) {
			return groupStatusMap.get(groupPosition);
		} else {
			return 0;
		}
	}

	@Override
	public int getGroupCount() {
		for (int x = 0; x < groupArray.size(); x++) {
			listView.expandGroup(x);
		}
		return groupArray.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return childArray.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groupArray.get(groupPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return childArray.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
			ViewGroup parent) {
		ParentHolder parentHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.widget_province_group, null);
			parentHolder = new ParentHolder();
			parentHolder.mGroupname = (TextView) convertView.findViewById(R.id.groupto);
			// parentHolder.mIndicator = (ImageView) convertView.findViewById(R.id.indicator);
			convertView.setTag(parentHolder);
		} else {
			parentHolder = (ParentHolder) convertView.getTag();
		}

		ProvinceEntity entity = (ProvinceEntity) getGroup(groupPosition);
		if (entity != null) {
			parentHolder.mGroupname.setText(entity.getProvinceName());
		}

		// if (isShowDeleteIcon()) {
		// parentHolder.mIndicator.setVisibility(View.VISIBLE);
		// } else {
		// parentHolder.mIndicator.setVisibility(View.GONE);
		// }

		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
			View convertView, ViewGroup parent) {
		ChildHolder childHolder;
		try {
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(R.layout.listview_item_province,null);
				childHolder = new ChildHolder();
				childHolder.mChildTitleTv = (TextView) convertView.findViewById(R.id.city_item_name);
				childHolder.mChildBuildCity = (TextView) convertView.findViewById(R.id.city_item_tip);
				convertView.setTag(childHolder);
			} else {
				childHolder = (ChildHolder) convertView.getTag();
			}

			CityEntity entity = (CityEntity) getChild(groupPosition, childPosition);
			String cityName = entity.getCity_name();
			childHolder.mChildTitleTv.setText(cityName);

			String alterMsg = entity.getAlter_msg();
			if (!StringUtil.isEmpty(alterMsg) && alterMsg.indexOf("...") > 0) {// 后台数据不规范
				alterMsg = alterMsg.replace("...", "");
			}
			if ("1".equals(entity.getIs_build())) {
				if (!StringUtil.isEmpty(alterMsg)) {
					childHolder.mChildBuildCity.setText(alterMsg);
				} else {
					childHolder.mChildBuildCity.setText("正在建设中");
				}
			} else {
				childHolder.mChildBuildCity.setText(alterMsg);
			}

		} catch (Exception e) {
			Log.e("getChildView()异常！" + e);
		}

		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	class ParentHolder {
		TextView mGroupname;
		ImageView mIndicator;
	}

	class ChildHolder {
		TextView mChildTitleTv;
		TextView mChildBuildCity;
		// ImageView mChildItemMoreImg;
	}

	@Override
	public Object[] getSections() {
		return null;
	}

	@Override
	public int getPositionForSection(int section) {
		ProvinceEntity entity = new ProvinceEntity();
		String firstStr = "";
		try {
			if (section == '!') {
				return -1;
			} else {
				for (int i = 0; i < getGroupCount(); i++) {
					entity = (ProvinceEntity) groupArray.get(i);
					firstStr = entity.getProvinceName().substring(0, 1);
					// char firstChar = str.toUpperCase().charAt(0);
					char firstChar = StringUtil.toFirstSpell(firstStr).toUpperCase(Locale.getDefault()).charAt(0);
					if (firstStr.equals("重")) {
						firstChar = 'C';
					}
					if (firstChar == section) {
						return i;
					}
				}
			}
		} catch (Exception e) {
			Log.e("getPositionForSection()方法异常！" + e);
		} finally {
			entity = null;
			firstStr = null;
		}
		return -1;
	}

	@Override
	public int getSectionForPosition(int position) {
		return 0;
	}

	public ProvinceExpandableListView getListView() {
		return listView;
	}

	public void setListView(ProvinceExpandableListView listView) {
		this.listView = listView;
	}

	public List<ProvinceEntity> getGroupArray() {
		return groupArray;
	}

	public void setGroupArray(List<ProvinceEntity> groupArray) {
		this.groupArray = groupArray;
	}

	public List<List<CityEntity>> getChildArray() {
		return childArray;
	}

	public void setChildArray(List<List<CityEntity>> childArray) {
		this.childArray = childArray;
	}

	public boolean isShowDeleteIcon() {
		return showDeleteIcon;
	}

	public void setShowDeleteIcon(boolean showDeleteIcon) {
		this.showDeleteIcon = showDeleteIcon;
	}

}