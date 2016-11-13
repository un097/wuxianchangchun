package cn.ffcs.surfingscene.road.adapter;

import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

import com.ffcs.surfingscene.entity.GlobalEyeEntity;
import com.ffcs.surfingscene.http.HttpCallBack;
import com.ffcs.surfingscene.response.BaseResponse;

public class OftenBlockedAdapter extends BaseAdapter {
	private List<GlobalEyeEntity> list;
	private LayoutInflater layoutInflater;
	private Context context;
	private String phone;
	private boolean isNoCollect = false;
	private RoadBo roadBo;
	private String gloType;
	private boolean haveCollectAnim = true;
	private Activity activity;

	public OftenBlockedAdapter(Activity activity, String phone, String gloType) {
		layoutInflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		roadBo = RoadBo.getInstance();
		this.context = activity.getApplicationContext();
		this.phone = phone;
		this.gloType = gloType;
		this.activity = activity;
	}

	public void setData(List<GlobalEyeEntity> list) {
		if (list != null) {
			this.list = list;
		} else {
			this.list = Collections.emptyList();
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
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder;
		GlobalEyeEntity entitiy = list.get(position);
		if (convertView == null) {
			holder = new Holder();
			convertView = layoutInflater.inflate(R.layout.glo_road_video_list_item, parent, false);
			holder.title = (TextView) convertView.findViewById(R.id.video_title);
			holder.dis = (TextView) convertView.findViewById(R.id.dis);
			holder.collect = (ImageView) convertView.findViewById(R.id.collect);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
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

	final static class Holder {
		TextView title;
		TextView dis;
		ImageView collect;
	}
}
