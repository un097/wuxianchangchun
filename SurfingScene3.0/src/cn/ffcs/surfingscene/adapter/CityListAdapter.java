package cn.ffcs.surfingscene.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.ffcs.surfingscene.R;

import com.ffcs.surfingscene.entity.AreaEntity;

/**
 * <p>Title:  城市列表适配器        </p>
 * <p>Description: 
 * </p>
 * <p>@author: Leo                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-6-21             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class CityListAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<AreaEntity> mAreaEntitylist = new ArrayList<AreaEntity>();
	private Context mContext;

	public CityListAdapter(Context context) {
		this.mContext = context;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return mAreaEntitylist.size();
	}

	@Override
	public Object getItem(int position) {
		return mAreaEntitylist.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setData(List<AreaEntity> data) {
		if (data == null || data.size() == 0) {
			return;
		}
		synchronized (mAreaEntitylist) {
			mAreaEntitylist.clear();
			mAreaEntitylist.addAll(data);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final AreaEntity entity = mAreaEntitylist.get(position);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.glo_citychange_item, null);
		}
		if (position % 2 == 0) {
			convertView.setBackgroundColor(mContext.getResources().getColor(R.color.Glo_bg_even));
		} else {
			convertView.setBackgroundColor(mContext.getResources().getColor(R.color.Glo_bg_odd));
		}
		TextView cityName = (TextView) convertView.findViewById(R.id.glo_citychange_item_name);
		TextView eyeCount = (TextView) convertView.findViewById(R.id.glo_citychange_eye_count);
		if (entity != null) {
			if (entity.getAreaName() != null) {
				cityName.setText(entity.getAreaName());
			}
			if (entity.getCount() != null && !entity.getCount().equals("")) {
				eyeCount.setText(String.valueOf(entity.getCount()));
			}
		}
		
		return convertView;
	}

}
