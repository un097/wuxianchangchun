package cn.ffcs.surfingscene.road.adapter;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.ffcs.surfingscene.R;
import cn.ffcs.surfingscene.road.OftenBlockedActivity;
import cn.ffcs.surfingscene.road.bo.RoadBo;
import cn.ffcs.surfingscene.sqlite.RoadCollect;
import cn.ffcs.surfingscene.sqlite.RoadCollectService;
import cn.ffcs.surfingscene.tools.LatLngTool;
import cn.ffcs.wisdom.tools.StringUtil;

import com.ffcs.surfingscene.http.HttpCallBack;
import com.ffcs.surfingscene.response.BaseResponse;

public class RoadCollectAdapter extends BaseAdapter {
	private List<RoadCollect> list;
	private LayoutInflater layoutInflater;
	private Context context;
	private String phone;
	private RoadBo roadBo;
	private String collectType = "1024";

	public RoadCollectAdapter(Context context, String phone) {
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
		this.phone = phone;
		roadBo = RoadBo.getInstance();
	}

	public void setData(List<RoadCollect> list) {
		if (list != null) {
			this.list = list;
		} else {
			this.list = Collections.emptyList();
		}
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
		RoadCollect entitiy = list.get(position);
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
		holder.title.setText(entitiy.pubName);

		String latString = entitiy.lat;
		String lngString = entitiy.lng;
		String dis = "";
		if (!StringUtil.isEmpty(latString) && !StringUtil.isEmpty(lngString)
				&& OftenBlockedActivity.lat != 0 && OftenBlockedActivity.lng != 0
				&& !latString.equals("0") && !lngString.equals("0")) {
			double lat = Double.parseDouble(latString);
			double lng = Double.parseDouble(lngString);
			double disDouble = LatLngTool.GetDistance(lat, lng, OftenBlockedActivity.lat,
					OftenBlockedActivity.lng);
			if (disDouble <= 1000) {
				dis = String.format("%.2f", disDouble) + "米";
			} else {
				dis = String.format("%.2f", disDouble / 1000) + "公里";
			}
		}
		holder.dis.setText(dis);
		holder.collect.setImageResource(R.drawable.glo_collect_yes);
		holder.collect.setOnClickListener(new CollectClick(position, entitiy.geyeId));
		return convertView;
	}

	class CollectClick implements OnClickListener {
		private int position;
		private int eyeId;

		CollectClick(int position, int eyeId) {
			this.position = position;
			this.eyeId = eyeId;
		}

		@Override
		public void onClick(final View v) {
			v.setEnabled(false);
			if (StringUtil.isEmpty(phone)) {
				RoadCollectService.getInstance(context).unCollect(phone, eyeId);
				list.remove(position);
				notifyDataSetChanged();
				v.setEnabled(true);
			} else {
				roadBo.collect(context, phone, collectType, eyeId, 1,
						new HttpCallBack<BaseResponse>() {

							@Override
							public void callBack(BaseResponse resp, String arg1) {
								RoadCollectService.getInstance(context).unCollect(phone, eyeId);
								list.remove(position);
								notifyDataSetChanged();
								v.setEnabled(true);
							}
						});
			}
		}
	}

	final static class Holder {
		TextView title;
		TextView dis;
		ImageView collect;
	}
}
