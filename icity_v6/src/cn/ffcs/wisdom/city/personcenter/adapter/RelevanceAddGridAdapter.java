package cn.ffcs.wisdom.city.personcenter.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.ffcs.wisdom.city.personcenter.entity.RelevanceAddEntity.RelevanceAdd;
import cn.ffcs.wisdom.city.utils.CityImageLoader;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.tools.Log;

/**
 * <p>Title:     可关联应用gridView适配器            </p>
 * <p>Description:                     </p>
 * <p>@author: zhangws                 </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-3-6            </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class RelevanceAddGridAdapter extends BaseAdapter {

	private final LayoutInflater mInflater;
	private List<RelevanceAdd> listItem = new ArrayList<RelevanceAdd>();
	private CityImageLoader loader;

	public RelevanceAddGridAdapter(Context context, List<RelevanceAdd> list) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		listItem = list;
		loader = new CityImageLoader(context);
	}

	@Override
	public int getCount() {
		return listItem.size();
	}

	@Override
	public Object getItem(int position) {
		return listItem.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		RelevanceAdd add = listItem.get(position);
		Holder holder;
		try {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.gridview_item_home, parent, false);
				holder = new Holder();
				holder.menuIcon = (ImageView) convertView.findViewById(R.id.gird_item_indicator);
				holder.menuName = (TextView) convertView.findViewById(R.id.gird_item_content);
				holder.menuName.setSelected(true);
				holder.isAppImage = (ImageView) convertView.findViewById(R.id.is_app);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}

			String name = add.getAppMenuName();
			String url = add.getAppMenuV6Icon();
			holder.menuName.setText(name);
			loader.loadUrl(holder.menuIcon, url);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return convertView;
	}

	static final class Holder {
		ImageView menuIcon;
		TextView menuName;
		ImageView isAppImage;
	}
}
