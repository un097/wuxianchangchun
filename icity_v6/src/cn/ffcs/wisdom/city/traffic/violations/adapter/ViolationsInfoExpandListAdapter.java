package cn.ffcs.wisdom.city.traffic.violations.adapter;

import java.util.List;

import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.ffcs.wisdom.city.sqlite.model.TrafficViolations;
import cn.ffcs.wisdom.city.traffic.violations.entity.TrafficViolationsEntity.TrafficViolationsInfo;
import cn.ffcs.wisdom.city.v6.R;

/**
 * <p>Title:    车辆违章详细列表          </p>
 * <p>Description:                     </p>
 * <p>@author: zhangwsh                </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-9-2           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class ViolationsInfoExpandListAdapter extends BaseExpandableListAdapter {

	private Context context;
	private LayoutInflater mInflater;
	private List<TrafficViolationsInfo> list;

	public ViolationsInfoExpandListAdapter(Context context, List<TrafficViolationsInfo> list) {
		this.context = context;
		mInflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
		this.list = list;
	}

	@Override
	public int getGroupCount() {
		return list.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return list.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return list.get(groupPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
			ViewGroup parent) {
		ParentHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.listview_item_parent_violation_info, parent,
					false);
			holder = new ParentHolder();
			holder.violationTimeView = (TextView) convertView.findViewById(R.id.violation_time);
			holder.violationMarkView = (TextView) convertView.findViewById(R.id.violations_mark);
			holder.violationMoneyView = (TextView) convertView.findViewById(R.id.violations_money);
			holder.cursor = (ImageView) convertView.findViewById(R.id.cursor);
			convertView.setTag(holder);
		} else {
			holder = (ParentHolder) convertView.getTag();
		}
		TrafficViolationsInfo entity = list.get(groupPosition);
		holder.violationTimeView.setText(entity.getIllegalTime());
//		holder.violationMarkView.setText(context.getString(R.string.violation_mark,
//				entity.violationsMark));
//		holder.violationMoneyView.setText(context.getString(R.string.violation_money,
//				entity.violationsMoney));
		if (isExpanded) {
			holder.cursor.setImageResource(R.drawable.up_arrow);
		} else {
			holder.cursor.setImageResource(R.drawable.down_arrow);
		}
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
			View convertView, ViewGroup parent) {
		ChildHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.listview_item_child_violation_info, parent,
					false);
			holder = new ChildHolder();
			holder.violationAction = (TextView) convertView.findViewById(R.id.violation_action);
			holder.violationAddress = (TextView) convertView.findViewById(R.id.violation_address);
			convertView.setTag(holder);
		} else {
			holder = (ChildHolder) convertView.getTag();
		}
		TrafficViolationsInfo entity = list.get(groupPosition);
		holder.violationAction.setText(entity.getIllegalInfo());
		holder.violationAddress.setText(entity.getIllegalAddr());
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	static final class ParentHolder {
		TextView violationTimeView;
		TextView violationMarkView;
		TextView violationMoneyView;
		ImageView cursor;
	}

	static final class ChildHolder {
		TextView violationAction;
		TextView violationAddress;
	}
}
