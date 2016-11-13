package cn.ffcs.surfingscene.road.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.ffcs.surfingscene.R;
import cn.ffcs.surfingscene.road.MapGroupListActivity;
import cn.ffcs.surfingscene.road.OftenBlockedActivity;
import cn.ffcs.surfingscene.road.RoadMainActivity;
import cn.ffcs.surfingscene.road.bo.RoadBo;
import cn.ffcs.surfingscene.sqlite.RoadCollectService;
import cn.ffcs.surfingscene.tools.LatLngTool;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.StringUtil;

import com.ffcs.surfingscene.entity.ActionEntity;
import com.ffcs.surfingscene.entity.GlobalEyeEntity;
import com.ffcs.surfingscene.http.HttpCallBack;
import com.ffcs.surfingscene.response.BaseResponse;

public class MapGroupListAdapter extends BaseExpandableListAdapter {

	private List<ActionEntity> groupList;
	private List<List<GlobalEyeEntity>> childList;
	private LayoutInflater layoutInflater;
	private Activity activity;
	private Context context;
	private String phone;
	private RoadBo roadBo;
	private String gloType;
	private boolean isNoCollect = false;
	private boolean haveCollectAnim = true;

	public MapGroupListAdapter(Activity activity, String phone, String gloType) {
		layoutInflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		roadBo = RoadBo.getInstance();
		this.context = activity.getApplicationContext();
		this.phone = phone;
		this.gloType = gloType;
		this.activity = activity;
		groupList = new ArrayList<ActionEntity>();
		childList = new ArrayList<List<GlobalEyeEntity>>();
	}

	public void setData(List<ActionEntity> list) {
		groupList.clear();
		childList.clear();
		if (list != null && list.size() > 0) {
			this.groupList = list;
			for (ActionEntity entity : list) {
				List<GlobalEyeEntity> geyes = entity.getGeyes();
				if (geyes != null && geyes.size() > 0) {
					childList.add(geyes);
				}
			}
		} else {
			this.groupList = Collections.emptyList();
		}
	}

	public void setNoCollect(boolean isNoCollect) {
		this.isNoCollect = isNoCollect;
	}

	/**
	 * 设置是否有收藏动画，default is true
	 * @param haveCollectAnim
	 */
	public void setHaveCollectAnim(boolean haveCollectAnim) {
		this.haveCollectAnim = haveCollectAnim;
	}

	@Override
	public int getGroupCount() {
		if (groupList != null && groupList.size() > 0) {
			return groupList.size();
		}
		return 0;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if (childList != null && childList.size() > 0) {
			return childList.get(groupPosition).size();
		}
		return 0;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groupList.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return childList.get(groupPosition).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
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
			convertView = LayoutInflater.from(context).inflate(R.layout.glo_expand_listview_head,
					parent, false);
			parentHolder = new ParentHolder();
			parentHolder.mGroupname = (TextView) convertView.findViewById(R.id.glo_map_group_title);
			parentHolder.mIndicator = (ImageView) convertView
					.findViewById(R.id.glo_map_group_indicator);
			convertView.setTag(parentHolder);
		} else {
			parentHolder = (ParentHolder) convertView.getTag();
		}

		ActionEntity entity = (ActionEntity) getGroup(groupPosition);
		if (entity != null) {
			parentHolder.mGroupname.setText(entity.getActionName());
		}

		if (isExpanded) {
			parentHolder.mIndicator.setImageResource(R.drawable.glo_city_arrow_down);
		} else {
			parentHolder.mIndicator.setImageResource(R.drawable.glo_city_arrow_up);
		}

		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
			View convertView, ViewGroup parent) {
		ChildHolder holder;
		GlobalEyeEntity entitiy = (GlobalEyeEntity) getChild(groupPosition, childPosition);
		if (convertView == null) {
			holder = new ChildHolder();
			convertView = layoutInflater.inflate(R.layout.glo_road_video_list_item, parent, false);
			holder.title = (TextView) convertView.findViewById(R.id.video_title);
			holder.dis = (TextView) convertView.findViewById(R.id.dis);
			holder.collect = (ImageView) convertView.findViewById(R.id.collect);
			convertView.setTag(holder);
		} else {
			holder = (ChildHolder) convertView.getTag();
		}
		holder.title.setText(entitiy.getPuName());

		String latString = entitiy.getLatitude();
		String lngString = entitiy.getLongitude();
		String dis = "";
		if (!StringUtil.isEmpty(latString) && !StringUtil.isEmpty(lngString)
				&& MapGroupListActivity.lat != 0 && MapGroupListActivity.lng != 0
				&& !latString.equals("0") && !lngString.equals("0")) {
			double lat = Double.parseDouble(latString);
			double lng = Double.parseDouble(lngString);
			//计算出距离
			double disDouble = LatLngTool.GetDistance(lat, lng, MapGroupListActivity.lat,
					MapGroupListActivity.lng);
			if (disDouble <= 1000) {
				dis = String.format("%.2f", disDouble) + "米";
			} else {
				dis = String.format("%.2f", disDouble / 1000) + "公里";
			}
		}
		holder.dis.setText(dis);
		int eyeId = entitiy.getGeyeId();
		if (isNoCollect) {
			holder.collect.setVisibility(View.GONE);
		} else {
			boolean isCollect = RoadCollectService.getInstance(context).isCollect(phone, eyeId);
			if (isCollect) {
				holder.collect.setImageResource(R.drawable.glo_collect_yes);
			} else {
				holder.collect.setImageResource(R.drawable.glo_collect_normal);
			}
			holder.collect.setOnClickListener(new CollectClick(isCollect, entitiy, holder.collect));
		}
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	class CollectClick implements OnClickListener {
		private boolean isCollect;
		private GlobalEyeEntity entitiy;
		private ImageView image;

		CollectClick(boolean isCollect, GlobalEyeEntity entitiy, ImageView image) {
			this.isCollect = isCollect;
			this.entitiy = entitiy;
			this.image = image;
		}

		@Override
		public void onClick(final View v) {
			v.setEnabled(false);
			if (isCollect) {
				if (!StringUtil.isEmpty(phone)) {
					roadBo.collect(context, phone, gloType, entitiy.getGeyeId(), 1,
							new HttpCallBack<BaseResponse>() {

								@Override
								public void callBack(BaseResponse resp, String arg1) {
									RoadCollectService.getInstance(context).unCollect(phone,
											entitiy.getGeyeId());
									notifyDataSetChanged();
									v.setEnabled(true);
								}
							});
				} else {
					RoadCollectService.getInstance(context).unCollect(phone, entitiy.getGeyeId());
					v.setEnabled(true);
				}
			} else {
				if (!StringUtil.isEmpty(phone)) {
					roadBo.collect(context, phone, gloType, entitiy.getGeyeId(), 0,
							new HttpCallBack<BaseResponse>() {

								@Override
								public void callBack(BaseResponse resp, String arg1) {
									RoadCollectService.getInstance(context).saveCollect(phone,
											entitiy);
									notifyDataSetChanged();
									v.setEnabled(true);
								}
							});
				} else {
					RoadCollectService.getInstance(context).saveCollect(phone, entitiy);
					v.setEnabled(true);
				}
				if (haveCollectAnim) {
					int[] location = new int[2];
					image.getLocationOnScreen(location);
					RoadMainActivity.startCollectAnim(location[0],
							location[1] - AppHelper.getStatusBarHeight(activity));
				}
			}
			notifyDataSetChanged();
		}
	}

	final static class ParentHolder {
		TextView mGroupname;
		ImageView mIndicator;
	}

	final static class ChildHolder {
		TextView title;
		TextView dis;
		ImageView collect;
	}

}
